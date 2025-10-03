package dev.dubhe.gugle.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import dev.dubhe.gugle.carpet.api.Consumer;
import dev.dubhe.gugle.carpet.api.tools.text.ComponentTranslate;
import dev.dubhe.gugle.carpet.commands.*;
import dev.dubhe.gugle.carpet.tools.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class GcaExtension implements CarpetExtension, ModInitializer {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(ResourceKey.class, new DimTypeSerializer())
            .create();
    public static String MOD_ID = "gca";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static @NotNull ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static final HashMap<Player, Map.Entry<FakePlayerInventoryContainer, FakePlayerEnderChestContainer>> fakePlayerInventoryContainerMap = new HashMap<>();
    public static final HashMap<String, java.util.function.Consumer<ServerPlayer>> ON_PLAYER_LOGGED_IN = new HashMap<>();

    public static final List<Map.Entry<Long, Consumer>> planFunction = new ArrayList<>();

    // 自动保存相关变量
    private static long lastAutoSaveTime = 0;
    private static final long TICKS_PER_MINUTE = 1200; // 20 ticks/秒 * 60秒

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(GcaSetting.class);
        // 初始化命令权限
        BlistCommand.PERMISSION.init(CarpetServer.minecraft_server);
        BotCommand.BOT_INFO.init(CarpetServer.minecraft_server);
        LocCommand.LOC_POINT.init(CarpetServer.minecraft_server);
        TodoCommand.TODO.init(CarpetServer.minecraft_server);
        WlistCommand.PERMISSION.init(CarpetServer.minecraft_server);
    }

    @Override
    public void onTick(MinecraftServer server) {
        // 处理自动保存
        if (GcaSetting.autoSaveFakePlayers && GcaSetting.fakePlayerResident) {
            long currentTime = server.getTickCount();
            long intervalTicks = GcaSetting.autoSaveInterval * TICKS_PER_MINUTE;

            if (currentTime - lastAutoSaveTime >= intervalTicks) {
                lastAutoSaveTime = currentTime;
                saveFakePlayers(server);
            }
        }

        // 处理计划函数
        if (!planFunction.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            List<Map.Entry<Long, Consumer>> toRemove = new ArrayList<>();

            for (Map.Entry<Long, Consumer> entry : planFunction) {
                if (currentTime >= entry.getKey()) {
                    entry.getValue().accept();
                    toRemove.add(entry);
                }
            }
            planFunction.removeAll(toRemove);
        }
    }

    public static void saveFakePlayers(MinecraftServer server) {
        if (!GcaSetting.fakePlayerResident) return;

        JsonObject fakePlayerList = new JsonObject();
        fakePlayerInventoryContainerMap.keySet().forEach(player -> {
            if (!(player instanceof EntityPlayerMPFake)) return;
            if (player.saveWithoutId(new CompoundTag()).contains("gca.NoResident")) return;
            String username = player.getGameProfile().getName();
            fakePlayerList.add(username, FakePlayerResident.save(player));
        });

        File file = server.getWorldPath(LevelResource.ROOT).resolve("fake_player.gca.json").toFile();
        try (BufferedWriter bfw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            bfw.write(GSON.toJson(fakePlayerList));
            LOGGER.debug("Auto-saved fake players list");
        } catch (IOException e) {
            LOGGER.error("Failed to auto-save fake players: {}", e.getMessage(), e);
        }
    }

    @Override
    public void onPlayerLoggedIn(ServerPlayer player) {
        GcaExtension.fakePlayerInventoryContainerMap.put(player, Map.entry(
                new FakePlayerInventoryContainer(player), new FakePlayerEnderChestContainer(player)
        ));
        java.util.function.Consumer<ServerPlayer> consumer = ON_PLAYER_LOGGED_IN.remove(player.getGameProfile().getName());
        if (consumer != null) consumer.accept(player);
    }

    @Override
    public void onPlayerLoggedOut(ServerPlayer player) {
        GcaExtension.fakePlayerInventoryContainerMap.remove(player);
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        // 服务器关闭时保存假人列表
        if (GcaSetting.fakePlayerResident) {
            saveFakePlayers(server);
        }
        fakePlayerInventoryContainerMap.clear();
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        if (GcaSetting.fakePlayerResident) {
            File file = server.getWorldPath(LevelResource.ROOT).resolve("fake_player.gca.json").toFile();
            if (!file.isFile()) {
                return;
            }
            try (BufferedReader bfr = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                JsonObject fakePlayerList = GSON.fromJson(bfr, JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : fakePlayerList.entrySet()) {
                    FakePlayerResident.load(entry, server);
                }
            } catch (IOException e) {
                GcaExtension.LOGGER.error(e.getMessage(), e);
            }
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        BotCommand.register(dispatcher);
        LocCommand.register(dispatcher);
        HereCommand.register(dispatcher);
        WhereisCommand.register(dispatcher);
        TodoCommand.register(dispatcher);
        WlistCommand.register(dispatcher);
        BlistCommand.register(dispatcher);
        SopCommand.register(dispatcher);
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return ComponentTranslate.getTranslations(lang);
    }

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(this);
    }
}