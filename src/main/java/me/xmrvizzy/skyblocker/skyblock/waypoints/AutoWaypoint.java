package me.xmrvizzy.skyblocker.skyblock.waypoints;

import java.util.ArrayList;
import java.util.HashMap;

import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class AutoWaypoint {
    static HashMap<String,float[]> LOCATIONS = new HashMap<String,float[]>();
    public static void init(){
        LOCATIONS.put("Khazad-dum", new float[]{1f,1f,0f});
        LOCATIONS.put("Jungle Temple", new float[]{0.5f,0f,1f});
        LOCATIONS.put("Goblin Queen's Den", new float[]{1f,0.5f,0f});
        LOCATIONS.put("Mines of Divan", new float[]{0f,1f,0f});
        LOCATIONS.put("Lost Precursor City", new float[]{0f,0f,1f});
        LOCATIONS.put("Fairy Grotto", new float[]{1f,0f,1f});
        LOCATIONS.put("Dragon's Lair", new float[]{1f,0.8f,0f});
    }
    public static void autoWaypoint(String subLocation){
        subLocation = subLocation.replace("Khazad-dûm", "Khazad-dum");
        if(LOCATIONS.containsKey(subLocation)){
            if(WaypointList.get(Utils.serverArea)==null || WaypointList.get(Utils.serverArea).get(subLocation)==null){
                MinecraftClient client = MinecraftClient.getInstance();
                Waypoint waypint = new Waypoint(client.player.getBlockPos(),LOCATIONS.get(subLocation));
                WaypointList.add(Utils.serverArea, subLocation, waypint);
                client.player.sendMessage(new LiteralText(String.format("[Skyblocker] Added %s at (%d,%d,%d) ",subLocation,waypint.getX(),waypint.getY(),waypint.getZ())).formatted(Formatting.AQUA)
                .append(new LiteralText("[VIEW]").styled((style) -> {
                    return style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbwp list")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("/sbwp list")));
                })), false);
            }
        }
    }
}
