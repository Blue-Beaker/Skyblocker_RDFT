package me.xmrvizzy.skyblocker;

import java.util.Map;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.ObjectUtils.Null;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.CooldownDisplay;
import me.xmrvizzy.skyblocker.skyblock.HotbarSlotLock;
import me.xmrvizzy.skyblocker.skyblock.skyblockerCLI;
import me.xmrvizzy.skyblocker.skyblock.dungeon.DungeonBlaze;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.skyblock.locator.DistancedLocator;
import me.xmrvizzy.skyblocker.skyblock.locator.PointedLocator;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointRenderer;
import me.xmrvizzy.skyblocker.skyblock.skyblockerCLI;

public class SkyblockerMod implements ClientModInitializer {
	public static final String NAMESPACE = "skyblocker";
	private static int TICKS = 0;
	@Override
	public void onInitializeClient() {
		HotbarSlotLock.init();
		SkyblockerConfig.init();
		PointedLocator.init();
		CooldownDisplay.init();
        new skyblockerCLI(ClientCommandManager.DISPATCHER);
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

			TICKS = 0;
		}
	}
}