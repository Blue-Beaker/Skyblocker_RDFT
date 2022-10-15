package me.xmrvizzy.skyblocker;

import java.util.Map;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.ObjectUtils.Null;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.HotbarSlotLock;
import me.xmrvizzy.skyblocker.skyblock.Locator;
import me.xmrvizzy.skyblocker.skyblock.skyblockerCLI;
import me.xmrvizzy.skyblocker.skyblock.dungeon.DungeonBlaze;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointRenderer;
import me.xmrvizzy.skyblocker.skyblock.skyblockerCLI;

public class SkyblockerMod implements ClientModInitializer {
	public static final String NAMESPACE = "skyblocker";
	private static int TICKS = 0;
	@Override
	public void onInitializeClient() {
		HotbarSlotLock.init();
		SkyblockerConfig.init();
		Locator.init();
        new skyblockerCLI(ClientCommandManager.DISPATCHER);
	}


	public static void onTick() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) return;
		if (SkyblockerConfig.get().locations.dwarvenMines.wishingCompassLocator || SkyblockerConfig.get().locations.events.ancestorSpadeLocator){
			if (client.options.keyUse.wasPressed()) {
				Locator.onUseLocator(client);
			}
			if (Locator.keyUseCurrentLine.wasPressed()) {
				Locator.useCurrentLine();
			}
			if (Locator.keyClearLocatorLines.wasPressed()) {
				Locator.clearLocatorLines();
			}
			if (Locator.keyShowLocatedTargets.wasPressed()) {
				Locator.showLocatedTargets();
			}
			Locator.tick(client);
			WaypointRenderer.tick();
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
			if (client.world != null && !client.isInSingleplayer())
				Utils.sbChecker();

			TICKS = 0;
		}
	}
}