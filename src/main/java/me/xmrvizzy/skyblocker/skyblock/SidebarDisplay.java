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
        if(SkyblockerConfig.get().locations.dungeons.sidebarRanking && Utils.isDungeons){
            try{
                Text scoreLine = findAndReplace("Cleared:",null);
                    if(scoreLine!=null){
                        String lineString = scoreLine.getString();
                        if(lineString.endsWith(")")){
                        String scoreString = lineString.split("\\(")[1].replace(")", "");
                        float score = Float.parseFloat(scoreString);
                        Text ranking;
                        if(score>=300){
                            ranking = new LiteralText(" S+").formatted(Formatting.GOLD);
                        }else if(score>=269.5){
                            ranking = new LiteralText(" S").formatted(Formatting.YELLOW);
                        }else if(score>=230){
                            ranking = new LiteralText(" A").formatted(Formatting.DARK_PURPLE);
                        }else if(score>=160){
                            ranking = new LiteralText(" B").formatted(Formatting.GREEN);
                        }else if(score>=100){
                            ranking = new LiteralText(" C").formatted(Formatting.BLUE);
                        }else{
                            ranking = new LiteralText(" D").formatted(Formatting.RED);
                        }
                        if(ranking!=null){
                            findAndReplace("Cleared:", scoreLine.shallowCopy().append(ranking));
                        }
                    }
                }
            }catch(Exception e){
                text=new LiteralText(e.getMessage());
            }
        }
        if(footer.contains("Your Candy")){
            Pattern candyPattern = Pattern.compile("Candy: ([0-9,]+ )Green, ([0-9,]+ )Purple \\(([0-9,]+) pts");
            Matcher candyMatcher = candyPattern.matcher(footer);
            if(SkyblockerConfig.get().general.sidebar.spookyCandy && candyMatcher.find()){
                String green = candyMatcher.group(1);
                String purple = candyMatcher.group(2);
                String points = candyMatcher.group(3);
                text=new LiteralText("Candy: ").append(new LiteralText(green).formatted(Formatting.GREEN)).append(new LiteralText(purple).formatted(Formatting.DARK_PURPLE)).append(new LiteralText("pts: ")).append(new LiteralText(points).formatted(Formatting.GOLD));
                setLine(2, text);
            }
        }else if(SkyblockerConfig.get().general.sidebar.lastCommission && ("CrystalHollows".equals(Utils.serverArea)||"DwarvenMines".equals(Utils.serverArea))){
            lastComm();
            if(lastComm!=null){
                text=lastComm;
                setLine(2, text);
            }
        }else{
            text=new LiteralText("");
            setLine(2, text);
        }
    }
    public static void setLine(int scoreIndex, Text text){
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
    public static Text findAndReplace(String startsWith,Text replace){
        Scoreboard scoreboard = client.world.getScoreboard();
        List<ScoreboardPlayerScore> list=getSidebarScores();
        for (ScoreboardPlayerScore score : list) {
            Team team = scoreboard.getPlayerTeam(score.getPlayerName());
            if (team == null) return null;
            Text text = team.getPrefix().shallowCopy().append(team.getSuffix());
            if(text.getString().startsWith(startsWith)){
                if(replace!=null){
                    team.setPrefix(replace);
                    team.setSuffix(new LiteralText(""));
                }
                return text;
            }
        }
        return null;
    }
    public static Text getLine(int scoreIndex){
        Scoreboard scoreboard = client.world.getScoreboard();
        List<ScoreboardPlayerScore> list=getSidebarScores();
        for (ScoreboardPlayerScore score : list) {
            if(score.getScore()==scoreIndex){
                Team team = scoreboard.getPlayerTeam(score.getPlayerName());
                if (team == null) return null;
                else return team.getPrefix().shallowCopy().append(team.getSuffix());
            }
        }
        return null;
    }
    public static List<ScoreboardPlayerScore> getSidebarScores(){
        Scoreboard scoreboard = client.world.getScoreboard();
        if (scoreboard == null) return null;
        ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(1);
        if (sidebar == null) return null;
        Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(sidebar);
        List<ScoreboardPlayerScore> list = scores.stream()
                .filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());
        return list;
    }
    public static List<Text> getComms(){
        List<Text> comms = new ArrayList<Text>();
        List<Text> list = Utils.getTabInfoText();
        if(list==null) return comms;
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
