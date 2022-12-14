package me.xmrvizzy.skyblocker;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.ClipboardChecker;
import me.xmrvizzy.skyblocker.skyblock.CooldownDisplay;
import me.xmrvizzy.skyblocker.skyblock.HotbarSlotLock;
import me.xmrvizzy.skyblocker.skyblock.SidebarDisplay;
import me.xmrvizzy.skyblocker.skyblock.SoundPrinter;
import me.xmrvizzy.skyblocker.skyblock.commands.SkyblockerDebugCLI;
import me.xmrvizzy.skyblocker.skyblock.commands.SkyblockerWaypointCLI;
import me.xmrvizzy.skyblocker.skyblock.dungeon.DungeonBlaze;
import me.xmrvizzy.skyblocker.skyblock.item.WikiLookup;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import me.xmrvizzy.skyblocker.skyblock.locator.DistancedLocator;
import me.xmrvizzy.skyblocker.skyblock.locator.PointedLocator;
import me.xmrvizzy.skyblocker.skyblock.solver.ContainerScreenSolverManager;
import me.xmrvizzy.skyblocker.skyblock.waypoints.AutoWaypoint;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointList;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointRenderer;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointStorage;

public class SkyblockerMod implements ClientModInitializer {
	public static final String NAMESPACE = "skyblocker";
	private static int TICKS = 0;
	public static Path configDir=FabricLoader.getInstance().getConfigDir().resolve("skyblocker/");
	@Override
	public void onInitializeClient() {
		HotbarSlotLock.init();
		SkyblockerConfig.init();
		PointedLocator.init();
		CooldownDisplay.init();
		AutoWaypoint.init();
		WikiLookup.init();
		configDir.toFile().mkdirs();
		WaypointStorage.readJsonFile();
        new SkyblockerWaypointCLI(ClientCommandManager.DISPATCHER);
        new SkyblockerDebugCLI(ClientCommandManager.DISPATCHER);
	}


	public static void onTick() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) return;
		if (Utils.isSkyblock && (SkyblockerConfig.get().locations.dwarvenMines.wishingCompassLocator || SkyblockerConfig.get().locations.events.ancestorSpadeLocator)){
			if (PointedLocator.keyUseCurrentLine.wasPressed()) {
				PointedLocator.useCurrentLine();
			}
			if (PointedLocator.keyClearLocatorLines.wasPressed()) {
				PointedLocator.clearLocatorLines();
			}
			if (PointedLocator.keyShowLocatedTargets.wasPressed()) {
				PointedLocator.showLocatedTargets();
			}
			PointedLocator.tick(client);
		}
		if (client.options.keyUse.isPressed()) {
			if(Utils.isSkyblock && SkyblockerConfig.get().locations.dwarvenMines.wishingCompassLocator || SkyblockerConfig.get().locations.events.ancestorSpadeLocator)
				PointedLocator.onUseLocator(client);
            CooldownDisplay.setDefaultCooldown(CooldownDisplay.RIGHT_CLICK);
		}
        if (client.options.keyAttack.isPressed()) {
            CooldownDisplay.setDefaultCooldown(CooldownDisplay.LEFT_CLICK);
        }
		WaypointRenderer.tick();
		if(Utils.isSkyblock && SkyblockerConfig.get().locations.dwarvenMines.metalDetectorLocator){
			DistancedLocator.tick();
		}
		if(Utils.isSkyblock && SkyblockerConfig.get().general.cooldownDisplay){
			CooldownDisplay.tick();
		}
		if(SkyblockerConfig.get().network.realPing)
		Pinger.instance.tick();
		TICKS++;
		if (TICKS % 4 == 0) 
			try {
				if(Utils.isDungeons){
					DungeonBlaze.DungeonBlaze();
				}

			}catch(Exception e) {
				// do nothing :))
			}
			if(Utils.isSkyblock){
				SidebarDisplay.tick();
				ContainerScreenSolverManager.tick(client.currentScreen);
			}
		if (TICKS % 20 == 0) {
			if (client.world != null && (!client.isInSingleplayer()||SkyblockerConfig.get().debug.forceSkyblock)){
				Utils.sbChecker();
				WaypointList.checkCrystalHollowsLobby();
				SoundPrinter.instance.check();
			}
			else if(client.isInSingleplayer()){
				Utils.isHypixel=false;
				Utils.isSkyblock=false;
				Utils.isDungeons=false;
				Utils.isInjected=false;
			}
			if(Utils.isSkyblock){
				ContainerScreenSolverManager.screenChecker(client.currentScreen);
				if(SkyblockerConfig.get().waypoint.autoClean){
					List<String> removed = WaypointList.removeClosedLobby();
					if(SkyblockerConfig.get().waypoint.autoCleanFeedback && !removed.isEmpty())
					client.player.sendMessage(new LiteralText("[Skyblocker]Removed closed lobbies:"+removed.toString()).formatted(Formatting.GREEN),false);
				}
				ClipboardChecker.tick();
			}
		}
		if(TICKS%200==0){
			if(SkyblockerConfig.get().network.realPing)
			Pinger.instance.ping();
			TICKS = 0;
		}
	}
}