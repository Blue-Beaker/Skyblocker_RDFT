package me.xmrvizzy.skyblocker.utils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.mixin.PlayerListHudAccessor;
import me.xmrvizzy.skyblocker.skyblock.Attribute;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.skyblock.waypoints.AutoWaypoint;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointList;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.PlantBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.lang.model.util.ElementScanner6;

public class Utils {
    public static boolean isSkyblock = false;
    public static boolean isHypixel = false;
    public static boolean isDungeons = false;
    public static boolean isInjected = false;
    public static String serverArea = "None";
    public static String serverId = "None";
    public static String subLocation = "None";
    static MinecraftClient client = MinecraftClient.getInstance();
    public static String parseActionBar(String msg) {
        String[] sections = msg.split(" {3,}");
        List<String> unused = new ArrayList<String>();

        if (msg.contains("❤") && !msg.contains("❈") && sections.length == 2) {
            Attribute.DEFENCE.set(0);
        }

        for (String section : sections) {
            String clear = Pattern.compile("[^0-9 /]").matcher(section).replaceAll("").trim();
            String[] split = clear.split("/");

            if (section.contains("❤")) {
                if (section.startsWith("§6")) split[0] = split[0].substring(1);
                Attribute.HEALTH.set(Integer.parseInt(split[0]));
                Attribute.MAX_HEALTH.set(Integer.parseInt(split[1]));
            } else if (section.contains("❈")) {
                Attribute.DEFENCE.set(Integer.parseInt(clear));
            } else if (section.contains("✎")) {
                Attribute.MANA.set(Integer.parseInt(split[0]));
                Attribute.MAX_MANA.set(Integer.parseInt(split[1]));
            } else {
                if (section.contains("Drill Fuel") && SkyblockerConfig.get().locations.dwarvenMines.enableDrillFuel) continue;
                unused.add(section);
            }
        }

        return String.join("   ", unused);
    }

    public static void sbChecker() {
        List<String> sidebar = getSidebar();
        String string = sidebar.toString();
        if (SkyblockerConfig.get().debug.forceSkyblock){
            isSkyblock = true;
            isInjected = true;
            ItemTooltipCallback.EVENT.register(PriceInfoTooltip::onInjectTooltip);
            if(SkyblockerConfig.get().debug.forceDungeons){
                isDungeons = true;
            }
            else{
                isDungeons = false;
            }
            serverArea=SkyblockerConfig.get().debug.forceArea;
            serverId=SkyblockerConfig.get().debug.forceServerId;
            return;
        }
        if (sidebar.isEmpty()) return;
        if (getTabHeader().contains("You are playing on MC.HYPIXEL.NET")) {
            isHypixel = true;
            if (sidebar.get(0).contains("SKYBLOCK")){
                if(isInjected == false){
                    isInjected = true;
                    ItemTooltipCallback.EVENT.register(PriceInfoTooltip::onInjectTooltip);
                }
                isSkyblock = true;
                subLocation = getSubLocation(sidebar);
                if(SkyblockerConfig.get().waypoint.autoWaypoints)
                AutoWaypoint.autoWaypoint(subLocation);
                serverArea=getArea(getTabInfo());
                serverId=getServer(getTabInfo());
            }
            else isSkyblock = false;
            if (isSkyblock && string.contains("The Catacombs")) isDungeons = true;
            else isDungeons = false;
        } else {
            isHypixel = false;
            isSkyblock = false;
            isDungeons = false;
        }
    }

    public static List<String> getSidebar() {
        List<String> lines = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return lines;

        Scoreboard scoreboard = client.world.getScoreboard();
        if (scoreboard == null) return lines;
        ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(1);
        if (sidebar == null) return lines;

        Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(sidebar);
        List<ScoreboardPlayerScore> list = scores.stream()
                .filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());

        if (list.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        } else {
            scores = list;
        }

        for (ScoreboardPlayerScore score : scores) {
            Team team = scoreboard.getPlayerTeam(score.getPlayerName());
            if (team == null) return lines;
            String text = team.getPrefix().getString() + team.getSuffix().getString();
            if (text.trim().length() > 0)
                lines.add(text);
        }

        lines.add(sidebar.getDisplayName().getString());
        Collections.reverse(lines);

        return lines;
    }
    public static List<String> getTabInfo(){
        List<String> lines = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return lines;
        Collection<PlayerListEntry> players = client.player.networkHandler.getPlayerList();
        for (PlayerListEntry player:players){
            if(player.getDisplayName()!=null){
                String line = player.getDisplayName().getString();
                if(line.length()>0){
                    lines.add(line);
                }
            }
        }
        return lines;
    }
    public static String getTabHeader(){
        return ((PlayerListHudAccessor)client.inGameHud.getPlayerListHud()).getHeader().getString();
    }
    public static String getTabFooter(){
        return ((PlayerListHudAccessor)client.inGameHud.getPlayerListHud()).getFooter().getString();
    }
    public static String getArea(List<String> tabLines){
        for(String line:tabLines){
            if(line.startsWith("Area:")){
                return(line.replace("Area:", "").replaceAll(" ", "").replaceAll("'", ""));
            }
        }
        return("None");
    }
    public static String getServer(List<String> tabLines){
        for(String line:tabLines){
            if(line.startsWith("Server:")){
                return(line.replace("Server:", "").replaceAll(" ", "").replaceAll("'", ""));
            }
        }
        return("None");
    }
    public static String getSubLocation(List<String> lines){
        try{
        String line = lines.get(4);
            if(line.contains(" ⏣ ")){
                return(line.replace(" ⏣ ", ""));
            }
        }
        catch(IndexOutOfBoundsException e){
        }
        return("None");

    }
    public static String getHeldItemId(){
        return PriceInfoTooltip.getInternalNameForItem(client.player.getMainHandStack());
    }
    public static boolean useOldHitbox(){
        return isSkyblock ||(SkyblockerConfig.get().hitbox.hitboxForAllHypixel && isHypixel);
    }
    public static String getCrystalHollowsLobby(){
        return "CH_"+serverId;
    }
    public static long getCrystalHollowsCloseTime(){
        return System.currentTimeMillis()/1000+18000-client.world.getTime()/20;
    }
}