package me.xmrvizzy.skyblocker;

import java.nio.file.Path;
import java.util.Map;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.ObjectUtils.Null;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.CooldownDisplay;
import me.xmrvizzy.skyblocker.skyblock.HotbarSlotLock;
import me.xmrvizzy.skyblocker.skyblock.SkyblockerCLI;
import me.xmrvizzy.skyblocker.skyblock.SkyblockerDebugCLI;
import me.xmrvizzy.skyblocker.skyblock.dungeon.DungeonBlaze;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.skyblock.locator.DistancedLocator;
import me.xmrvizzy.skyblocker.skyblock.locator.PointedLocator;
import me.xmrvizzy.skyblocker.skyblock.solver.ContainerScreenSolverManager;
import me.xmrvizzy.skyblocker.skyblock.solver.NetworkRelaySolverSound;
import me.xmrvizzy.skyblocker.skyblock.waypoints.AutoWaypoint;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointRenderer;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointStorage;
import me.xmrvizzy.skyblocker.skyblock.SkyblockerCLI;

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
		configDir.toFile().mkdirs();
		WaypointStorage.readJsonFile();
        new SkyblockerCLI(ClientCommandManager.DISPATCHER);
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
		TICKS++;
		if (TICKS % 4 == 0) 
			try {
				if(Utils.isDungeons){
					DungeonBlaze.DungeonBlaze();
				}

			}catch(Exception e) {
				// do nothing :))
			}
		if (TICKS % 20 == 0) {
			if (client.world != null && (!client.isInSingleplayer()||SkyblockerConfig.get().debug.forceSkyblock))
				Utils.sbChecker();
			if(Utils.isSkyblock){
				ContainerScreenSolverManager.screenChecker(client.currentScreen);
			}
			TICKS = 0;
		}
	}
}