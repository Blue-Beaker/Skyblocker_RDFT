package me.xmrvizzy.skyblocker.skyblock.waypoints;

import java.util.HashMap;
import java.util.Set;

import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;



public class WaypointList {
    static HashMap<String, HashMap<String, Waypoint>> list = new HashMap<String, HashMap<String, Waypoint>>();
    public static HashMap<String, Waypoint> get(String area){
        return list.get(area);
    }
    public static HashMap<String, Waypoint> get(){
        return list.get(Utils.serverArea);
    }
    public static Set<String> getAreas(){
        return list.keySet();
    }

    public static Boolean add(String name, Waypoint waypoint){
        return add(Utils.serverArea,name,waypoint);
    }
    public static Boolean add(String area, String name, Waypoint waypoint){
        list.putIfAbsent(area, new HashMap<String, Waypoint>());
        if(list.get(area).putIfAbsent(name, waypoint)==null){
            store();
            return true;
        }
        return false;
    }

    public static Boolean remove(String name){
        return remove(Utils.serverArea,name);
    }
    public static Boolean remove(String area, String name){
        list.putIfAbsent(area, new HashMap<String, Waypoint>());
        if(list.get(area).remove(name)!=null){
            store();
            return true;
        }
        return false;
    }

    public static Boolean rename(String area, String name, String name2){
        list.putIfAbsent(area, new HashMap<String, Waypoint>());
        Waypoint waypoint = list.get(area).remove(name);
        if(waypoint!=null){
            return add(area,name2,waypoint);
        }else{
            return false;
        }
    }
    public static Boolean rename(String name, String name2){
        return rename(Utils.serverArea,name,name2);
    }

    public static String addAutoRenaming(String area, String name, Waypoint waypoint){
        if(!WaypointList.add(area, name, waypoint)){
            int renamingIndex=1;
            while(!WaypointList.add(area, name+"("+renamingIndex+")", waypoint)){
                renamingIndex++;
            }
            return name+"("+renamingIndex+")";
        }else{
            return name;
        }
    }
    public static String addAutoRenaming(String name, Waypoint waypoint){
        return addAutoRenaming(Utils.serverArea,name,waypoint);
    }

    public static void clear(String area){
        list.putIfAbsent(area, new HashMap<String, Waypoint>());
        list.get(area).clear();
        store();
    }
    public static void clear(){
        clear(Utils.serverArea);
    }

    public static Boolean setColor(String area, String name, float[] color){
        list.putIfAbsent(area, new HashMap<String, Waypoint>());
        if(list.get(area).get(name)==null){
            return false;
        }
        else{
            list.get(area).get(name).color=color;
            store();
            return true;
        }
    }
    public static Boolean setColor(String name, float[] color){
        return setColor(Utils.serverArea, name, color);
    }
    public static void store(){
        WaypointStorage.storeJsonFile();
    }
}
