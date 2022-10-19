package me.xmrvizzy.skyblocker.skyblock.locator;

import java.util.ArrayList;

import javax.rmi.CORBA.Util;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;


public class DistancedLocator {
    static MinecraftClient client = MinecraftClient.getInstance();
    static DistancedLocatorSoundListener soundListener = new DistancedLocatorSoundListener();
    static float distance = 0.0f;
    public static void tick(){
        String skyblockId = PriceInfoTooltip.getInternalNameForItem(client.player.getMainHandStack());
        if("DWARVEN_METAL_DETECTOR".equals(skyblockId)){
            soundListener.enable();
        }
        else{
            soundListener.disable();
        }
    }
    public static void getDistance(String msg){
        if(msg.contains("TREASURE:")){
            String dist = msg.split(":")[1].replace(" ", "").replace("m", "").replace("§b","");
            distance = Float.parseFloat(dist);
            if(SkyblockerConfig.get().debug.debugDistancedLocator){
                Vec3d pos = soundListener.pos;
                client.player.sendMessage(new LiteralText(String.format("%.1f,%.1f,%.1f,%.1fm",pos.getX(),pos.getY(),pos.getZ(),DistancedLocator.distance)), false);
            }
        }
    }
}
