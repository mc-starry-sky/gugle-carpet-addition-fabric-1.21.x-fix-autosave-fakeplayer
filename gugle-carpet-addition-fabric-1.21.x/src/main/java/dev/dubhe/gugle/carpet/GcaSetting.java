package dev.dubhe.gugle.carpet;

import carpet.api.settings.Rule;
import carpet.api.settings.Validators;

public class GcaSetting {

    public static final String GCA = "GCA";
    public static final String EXPERIMENTAL = "experimental";
    public static final String BOT = "BOT";
    public static final String COMMAND = "command";

    // 允许玩家打开假人背包
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT}
    )
    public static boolean openFakePlayerInventory = false;

    // 在 GCA, EXPERIMENTAL, BOT 分类中添加以下规则：

    // 自动保存假人列表
    @Rule(
            categories = {GCA, EXPERIMENTAL, BOT}
    )
    public static boolean autoSaveFakePlayers = true;

    // 自动保存间隔（分钟）
    @Rule(
            categories = {GCA, EXPERIMENTAL, BOT},
            validators = Validators.NonNegativeNumber.class
    )
    public static int autoSaveInterval = 5;

    // 允许玩家打开假人末影箱
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT},
        options = {"ender_chest", "true", "false"},
        validators = GcaValidators.EnderChest.class
    )
    public static String openFakePlayerEnderChest = "false";

    // 退出存档时保留假人
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT}
    )
    public static boolean fakePlayerResident = false;

    // 退出存档时保留假人动作
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT}
    )
    public static boolean fakePlayerReloadAction = false;

    // 让假人自动补货
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT}
    )
    public static boolean fakePlayerAutoReplenishment = false;

    // 让假人自动钓鱼
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT}
    )
    public static boolean fakePlayerAutoFish = false;

    // 让假人自动切换快损坏的工具
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT}
    )
    public static boolean fakePlayerAutoReplaceTool = false;

    public static final String fakePlayerNoneName = "#none";

    // 假人名称前缀
    @Rule(
        options = {fakePlayerNoneName, "bot_"},
        categories = {GCA, EXPERIMENTAL, BOT}
    )
    public static String fakePlayerPrefixName = fakePlayerNoneName;

    // 假人名称后缀
    @Rule(
        options = {fakePlayerNoneName, "_fake"},
        categories = {GCA, EXPERIMENTAL, BOT}
    )
    public static String fakePlayerSuffixName = fakePlayerNoneName;

    // 方便快捷的假人管理菜单
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT, COMMAND},
        options = {"ops", "0", "1", "2", "3", "4", "true", "false"},
        validators = Validators.CommandLevel.class
    )
    public static String commandBot = "ops";

    // 待办事项清单
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT, COMMAND},
        options = {"ops", "0", "1", "2", "3", "4", "true", "false"},
        validators = Validators.CommandLevel.class
    )
    public static String commandTodo = "ops";

    // 快速发送坐标
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT, COMMAND},
        options = {"ops", "0", "1", "2", "3", "4", "true", "false"},
        validators = Validators.CommandLevel.class
    )
    public static String commandHere = "ops";

    // 快速定位玩家
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT, COMMAND},
        options = {"ops", "0", "1", "2", "3", "4", "true", "false"},
        validators = Validators.CommandLevel.class
    )
    public static String commandWhereis = "ops";

    // 地标管理菜单
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT, COMMAND},
        options = {"ops", "0", "1", "2", "3", "4", "true", "false"},
        validators = Validators.CommandLevel.class
    )
    public static String commandLoc = "ops";

    // 白名单管理
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT, COMMAND}
    )
    public static boolean commandWlist = false;

    // 封禁名单管理
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT, COMMAND}
    )
    public static boolean commandBlist = false;

    // 简单获取op
    @Rule(
        categories = {GCA, EXPERIMENTAL, BOT, COMMAND}
    )
    public static boolean commandSop = false;

    // 让放置的栅栏门与你点击的栅栏门拥有相同的方块状态
    @Rule(
        categories = {GCA, EXPERIMENTAL}
    )
    public static boolean betterFenceGatePlacement = false;

    // 仅允许名称中包含“去皮”的斧头对原木去皮
    @Rule(
        categories = {GCA, EXPERIMENTAL}
    )
    public static boolean betterWoodStrip = false;

    // 右键告示牌时与之附着的方块产生交互
    @Rule(
        categories = {GCA, EXPERIMENTAL}
    )
    public static boolean betterSignInteraction = false;

    // 右键包含物品的展示框时与之附着的方块产生交互
    @Rule(
        categories = {GCA, EXPERIMENTAL}
    )
    public static boolean betterItemFrameInteraction = false;

    // 快速合成时在物品栏保留一份物品
    @Rule(
        categories = {GCA, EXPERIMENTAL}
    )
    public static boolean betterQuickCrafting = false;

    // 简单的游戏内计算器
    @Rule(
        categories = {GCA, EXPERIMENTAL}
    )
    public static boolean simpleInGameCalculator = false;

    // 快速ping好友
    @Rule(
        categories = {GCA, EXPERIMENTAL}
    )
    public static boolean fastPingFriend = false;

    // 设置LC值为多少高度时的值
    @Rule(
        categories = {GCA, EXPERIMENTAL}
    )
    public static int qnmdLC = -1;
}
