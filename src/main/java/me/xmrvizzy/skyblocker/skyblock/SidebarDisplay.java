package me.xmrvizzy.skyblocker.skyblock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.xmrvizzy.skyblocker.utils.Utils;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.mixin.PlayerListHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SidebarDisplay {
    public static Text text = null;
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static List<Text> comms = new ArrayList<Text>();
    public static Text lastComm = null;
    public static void tick(){
        text = null;
        String footer = Utils.getTabFooter();
        if(footer.contains("Your Candy")){
            Pattern candyPattern = Pattern.compile("Candy: ([0-9,]+ )Green, ([0-9,]+ )Purple \\(([0-9,]+) pts");
            Matcher candyMatcher = candyPattern.matcher(footer);
            if(SkyblockerConfig.get().general.sidebar.spookyCandy && candyMatcher.find()){
                String green = candyMatcher.group(1);
                String purple = candyMatcher.group(2);
                String points = candyMatcher.group(3);
                text=new LiteralText("Candy: ").append(new LiteralText(green).formatted(Formatting.GREEN)).append(new LiteralText(purple).formatted(Formatting.DARK_PURPLE)).append(new LiteralText("pts: ")).append(new LiteralText(points).formatted(Formatting.GOLD));
            }
        }else if(SkyblockerConfig.get().general.sidebar.lastCommission){
            lastComm();
            if(lastComm!=null) text=lastComm;
        }
        if(text!=null)
        replaceLine(2, text);
    }
    public static void replaceLine(int scoreIndex, Text text){
        Scoreboard scoreboard = client.world.getScoreboard();
        if (scoreboard == null) return;
        ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(1);
        if (sidebar == null) return;
        Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(sidebar);
        List<ScoreboardPlayerScore> list = scores.stream()
                .filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());
        for (ScoreboardPlayerScore score : list) {
            if(score.getScore()==scoreIndex){
                Team team = scoreboard.getPlayerTeam(score.getPlayerName());
                if (team == null) return;
                team.setPrefix(text);
                team.setSuffix(new LiteralText(""));
            }
        }
    }
    public static String getLine(List<String> list,String startsWith){
        for(String line : list){
            if(line.startsWith(startsWith)) return line;
        }
        return null;
    }
    public static List<Text> getComms(){
        List<Text> comms = new ArrayList<Text>();
        List<Text> list = Utils.getTabInfoText();
        Iterator<Text> it = list.iterator();
        while(it.hasNext()){
            Text line = it.next();
            if(line.getString().equals("Commissions")){
                try{
                    while(it.hasNext()){
                        Text next = it.next();
                        String nextString = next.getString();
                        if(nextString.endsWith("%") || nextString.endsWith("DONE")) comms.add(next);
                        else return comms;
                    }
                }catch(Exception e){

                }
            }
        }
        return comms;
    }
    public static Text lastComm(){
        List<Text> newComms = getComms();
        if(newComms.size()==0){
            lastComm=null;
        }else{
            for(int i=0;i<newComms.size();i++){
                try{
                    if(!newComms.get(i).equals(comms.get(i))){
                        lastComm=newComms.get(i);
                    }
                }catch(Exception e){
        
                }
            }
        }
        comms.clear();
        comms.addAll(newComms);
        return lastComm;
    }
}
