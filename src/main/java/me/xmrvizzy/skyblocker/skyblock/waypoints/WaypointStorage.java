package me.xmrvizzy.skyblocker.skyblock.waypoints;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import me.xmrvizzy.skyblocker.SkyblockerMod;
import net.fabricmc.loader.api.FabricLoader;


public class WaypointStorage {
    public static String toJsonString(){
        Gson gson = new Gson();
        String json = gson.toJson(WaypointList.list);
        return json;
    }
    public static String toPrettyJsonString(){
        Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
        String json = gson.toJson(WaypointList.list);
        return json;
    }
    public static String toPrettyJsonString2(){
        Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
        String json = gson.toJson(WaypointList.crystalHollowsTime);
        return json;
    }
    public static Boolean fromJsonString(String json){
        Gson gson = new Gson();
        java.lang.reflect.Type setType = new TypeToken<HashMap<String, HashMap<String, Waypoint>>>(){}.getType();
        try{
            WaypointList.list=gson.fromJson(json,setType);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public static Boolean storeJsonFile(){
        try {
            File file = SkyblockerMod.configDir.resolve("waypoints.json").toFile();
            FileWriter writer = new FileWriter(file);
            writer.write(toPrettyJsonString());
            writer.flush();
            writer.close();
            File file2 = SkyblockerMod.configDir.resolve("crystal_hollows_close_times.json").toFile();
            FileWriter writer2 = new FileWriter(file2);
            writer2.write(toPrettyJsonString2());
            writer2.flush();
            writer2.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void reloadJsonFile() throws IOException{
        Path file = SkyblockerMod.configDir.resolve("waypoints.json");
        java.lang.reflect.Type setType = new TypeToken<HashMap<String, HashMap<String, Waypoint>>>(){}.getType();
        BufferedReader reader = Files.newBufferedReader(file);
        Gson gson = new Gson();
        HashMap<String, HashMap<String, Waypoint>> list = gson.fromJson(reader, setType);
        for(HashMap<String, Waypoint> waypoints:list.values()){
            for(Waypoint waypoint:waypoints.values()){
                if(waypoint.pos==null){
                    waypoint.pos=new int[]{0,0,0};
                }
                if(waypoint.color==null){
                    waypoint.color=new float[]{1f,1f,1f};
                }
                if(waypoint.locatorLines==null){
                    waypoint.locatorLines=new ArrayList<double[]>();
                }
            }
        }
        WaypointList.list=list;
        Path file2 = SkyblockerMod.configDir.resolve("crystal_hollows_close_times.json");
        java.lang.reflect.Type setType2 = new TypeToken<HashMap<String, Long>>(){}.getType();
        BufferedReader reader2 = Files.newBufferedReader(file2);
        WaypointList.crystalHollowsTime = gson.fromJson(reader2, setType2);
    }
    public static void readJsonFile(){
        try {
            reloadJsonFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
