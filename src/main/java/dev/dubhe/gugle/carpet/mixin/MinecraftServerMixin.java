package dev.dubhe.gugle.carpet.mixin;

import dev.dubhe.gugle.carpet.GcaExtension;
import dev.dubhe.gugle.carpet.GcaSetting;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "saveEverything", at = @At("HEAD"))
    private void onAutoSave(CallbackInfoReturnable<Boolean> cir) {
        // 当游戏自动保存时，保存假人列表
        if (GcaSetting.autoSaveFakePlayers && GcaSetting.fakePlayerResident) {
            MinecraftServer server = (MinecraftServer) (Object) this;
            GcaExtension.saveFakePlayers(server);
        }
    }
}