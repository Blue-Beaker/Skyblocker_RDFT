package me.xmrvizzy.skyblocker.skyblock.waypoints;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.xmrvizzy.skyblocker.skyblock.commands.SkyblockerWaypointCLI;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class AutoWaypoint {
    static HashMap<String,float[]> LOCATIONS = new HashMap<String,float[]>();
    static HashMap<String,String> LOCATION_MAPPINGS = new HashMap<String,String>();
    public static void init(){
        LOCATIONS.put("Khazad-dum", new float[]{1f,1f,0f});
        LOCATIONS.put("Jungle Temple", new float[]{0.5f,0f,1f});
        LOCATIONS.put("Goblin Queen's Den", new float[]{1f,0.5f,0f});
        LOCATIONS.put("Mines of Divan", new float[]{0f,1f,0f});
        LOCATIONS.put("Lost Precursor City", new float[]{0f,0f,1f});
        LOCATIONS.put("Fairy Grotto", new float[]{1f,0f,1f});
        LOCATIONS.put("Dragon's Lair", new float[]{1f,0.8f,0f});
        LOCATION_MAPPINGS.put("bal", "Khazad-dum");
        LOCATION_MAPPINGS.put("khazad", "Khazad-dum");
        LOCATION_MAPPINGS.put("mines", "Mines of Divan");
        LOCATION_MAPPINGS.put("divan", "Mines of Divan");
        LOCATION_MAPPINGS.put("temple", "Jungle Temple");
        LOCATION_MAPPINGS.put("queen", "Goblin Queen's Den");
        LOCATION_MAPPINGS.put("king", "King");
        LOCATION_MAPPINGS.put("city", "Lost Precursor City");
        LOCATION_MAPPINGS.put("lpc", "Lost Precursor City");
        LOCATION_MAPPINGS.put("fairy", "Fairy Grotto");
        LOCATION_MAPPINGS.put("grotto", "Fairy Grotto");
        LOCATION_MAPPINGS.put("dragon", "Dragon's Lair");
        LOCATION_MAPPINGS.put("corleone", "Boss Corleone");
        LOCATION_MAPPINGS.put("corl", "Boss Corleone");
    }
    public static void autoWaypoint(String subLocation){
        subLocation = subLocation.replace("Khazad-dûm", "Khazad-dum");
        if(LOCATIONS.containsKey(subLocation)){
            if(WaypointList.get(Utils.getLobbyAutoCH())==null || WaypointList.get(Utils.getLobbyAutoCH()).get(subLocation)==null){
                MinecraftClient client = MinecraftClient.getInstance();
                Waypoint waypoint = new Waypoint(client.player.getBlockPos(),LOCATIONS.get(subLocation));
                WaypointList.add(subLocation, waypoint);
                client.player.sendMessage(
                    SkyblockerWaypointCLI.addButtonsOnCreation(new LiteralText(String.format("[Skyblocker] Added %s at (%d,%d,%d) ",subLocation,waypoint.getX(),waypoint.getY(),waypoint.getZ())).formatted(Formatting.AQUA),
                    subLocation,Utils.getLobbyAutoCH(),waypoint), 
                false);
            }
        }
    }
    public static MutableText chatCoords(MutableText message){
        MutableText finalText = message;
        String msg = message.getString();
        if(msg.contains("sthw set")) return parseSTHW(message);
        if(msg.contains("$SBECHWP:")) return parseSbeCoords(message);
        Pattern coordsPattern = Pattern.compile("([0-9]+ [0-9]+ [0-9]+)");
        Matcher coordsMatcher = coordsPattern.matcher(msg);
        if(coordsMatcher.find()){
            String coords = coordsMatcher.group(1);
            Pattern namePattern = Pattern.compile("(temple|odawa|key guardian|divan|corleone|corl|city|lpc|king|queen|bal|khazad|grotto)",Pattern.CASE_INSENSITIVE);
            Matcher nameMatcher = namePattern.matcher(msg);
            MutableText addWaypointButton;
            String name;
            if(nameMatcher.find()){
                name = nameMatcher.group(1).toLowerCase();
                if(LOCATION_MAPPINGS.keySet().contains(name)){
                    name=LOCATION_MAPPINGS.get(name);
                }
            }
            else{
                name = "name";
            }
            addWaypointButton = addWaypointButton(name, coords);
            finalText = message.shallowCopy().append(addWaypointButton);
        }
        return finalText;
    } 
    public static MutableText parseSbeCoords(MutableText message){
        MutableText finalText = message;
        String msg = message.getString();
        try{
            String[] split1 = msg.split("SBECHWP:");
            String[] sbeWaypoints = split1[1].split("\\\\n");
            for(String wp : sbeWaypoints){
                try{
                    String[] wpsplit = wp.split("@");
                    finalText.append(addWaypointButton(wpsplit[0], wpsplit[1].replace(",", " ")));
                }catch(IndexOutOfBoundsException e){
                }
            }
        }catch(IndexOutOfBoundsException e){
        }

        return finalText;
    }
    public static MutableText parseSTHW(MutableText message){
        MutableText finalText = message;
        String msg = message.getString();
        try{
            String[] split1 = msg.split("sthw set ");
            String[] split2 = split1[1].split(" ");
            finalText.append(addWaypointButton(split2[3], split2[0]+" "+split2[1]+" "+split2[2]));
        }catch(IndexOutOfBoundsException e){
        }
        return finalText;
    }
    public static MutableText addWaypointButton(String name, String coords){
        String command = String.format("/sbwp add '%s' %s", name,coords);
        return new LiteralText(" [Add Waypoint "+name+"]").styled((style) -> {
            return style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,command )).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(command)));
        });
    }
}
