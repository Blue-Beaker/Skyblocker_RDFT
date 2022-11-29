package me.xmrvizzy.skyblocker.skyblock.locator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.skyblock.waypoints.Waypoint;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointList;
import me.xmrvizzy.skyblocker.utils.RenderUtils;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class PointedLocator {
    static final int LOCATION_NOTHING = 0;
    static final int LOCATION_MAGMA = 1;
    static final int LOCATION_JUNGLE = 2;
    static final int LOCATION_GOBLINS = 3;
    static final int LOCATION_MITHRIL = 4;
    static final int LOCATION_PRECURSOR = 5;
    static final int LOCATION_BURROW = 6;
    static final String[] LOCATION_NAMES = {"Nucleus","Khazad-dum","Temple/Odawa","King/Queen","Mines of Divan","Lost Precursor City","Griffin Burrow"};
    static final float[][] LOCATION_COLORS = {{1f,1f,1f},{1f,1f,0f},{0.5f,0f,1f},{1f,0.5f,0f},{0f,1f,0f},{0f,0f,1f},{1f,0.5f,0f}};

    static MinecraftClient client = MinecraftClient.getInstance();
    public static int compassLocator = 0;
    public static int spadeLocator = 0;
    public static ArrayList<Vec3d> compassParticleList = new ArrayList<Vec3d>();
    public static ArrayList<Vec3d> spadeParticleList = new ArrayList<Vec3d>();
    public static ArrayList<Vec3d> spadeParticleList2 = new ArrayList<Vec3d>();
    static int locationOnLastCompassUse = 0;
    static double[] currentLine = {0.0,0.0,0.0,0.0};
    static boolean hasCurrentLine = false;

    static HashMap<Integer,ArrayList<double[]>> rayLists = new HashMap<Integer,ArrayList<double[]>>();
    static boolean renderHooked = false;
    public static KeyBinding keyClearLocatorLines;
    public static KeyBinding keyShowLocatedTargets;
    public static KeyBinding keyUseCurrentLine;

    public static void init() {
        keyClearLocatorLines = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.clearLocatorLines",
                GLFW.GLFW_KEY_LEFT_BRACKET,
                "key.categories.skyblocker"
        ));
        keyUseCurrentLine = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.useCurrentLine",
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                "key.categories.skyblocker"
        ));
        keyShowLocatedTargets = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.keyShowLocatedTargets",
                GLFW.GLFW_KEY_F8,
                "key.categories.skyblocker"
        ));
    }
    public static void onUseLocator(MinecraftClient client){
        String skyblockId = PriceInfoTooltip.getInternalNameForItem(client.player.getMainHandStack());
        if(SkyblockerConfig.get().locations.dwarvenMines.wishingCompassLocator && "WISHING_COMPASS".equals(skyblockId)){
            compassParticleList.clear();
            locationOnLastCompassUse=getPlayerLocation();
            compassLocator = 20;
            String targetName = LOCATION_NAMES[locationOnLastCompassUse];
            client.player.sendMessage(new LiteralText("[Skyblocker] Started Locating "+targetName).formatted(Formatting.AQUA), false);
            
        }
        else if(SkyblockerConfig.get().locations.events.ancestorSpadeLocator && "ANCESTRAL_SPADE".equals(skyblockId)){
            spadeParticleList.clear();
            locationOnLastCompassUse=LOCATION_BURROW;
            spadeLocator = 60;
            String targetName = LOCATION_NAMES[locationOnLastCompassUse];
            client.player.sendMessage(new LiteralText("[Skyblocker] Started Locating "+targetName).formatted(Formatting.AQUA), false);
        }
        if(SkyblockerConfig.get().debug.showInternalNameOnRightClick){
            client.player.sendMessage(Text.of("" + skyblockId), false);
        }
    }
    public static void tick(MinecraftClient client){
        if(compassLocator >0){compassLocator--;}
        if(compassLocator == 1){calculateLine(removeDuplicatedPoints(compassParticleList));}
        if(spadeLocator >0){spadeLocator--;}
        if(spadeLocator == 1){
            ArrayList<Vec3d> list1 = removeDuplicatedPoints(spadeParticleList);
            ArrayList<Vec3d> list2 = removeDuplicatedPoints(spadeParticleList2);
            ArrayList<Vec3d> list3 = new ArrayList<Vec3d>();
            list3.addAll(list1);
            for(Vec3d pos:list1){
                if(!list2.contains(pos)){
                    list3.remove(pos);
                }
            }
            while(list3.size()>3)
            list3.remove(0);
            calculateLine(list3);
        }
        try{
            if(!renderHooked){
                WorldRenderEvents.END.register(PointedLocator::lineRenderer);
                renderHooked = true;
            }
        }
        catch(Exception e){}
    }
    public static ArrayList<Vec3d> removeDuplicatedPoints(ArrayList<Vec3d> posList){
        List<Vec3d> newList = new ArrayList<>();
        for (int i = 0; i < posList.size(); i++) {
            if (!newList.contains(posList.get(i))) {
                newList.add(posList.get(i));
            }
        }
        posList.clear();
        posList.addAll(newList);
        return(posList);
    }
    public static double[] calculateLine(ArrayList<Vec3d> particleList){
        if(particleList.size()<=2){
            return null;
        }
        String particleListString="Position:";
        Vec3d avgPoint = new Vec3d(0,0,0);
        for(Vec3d pos : particleList){
            if(SkyblockerConfig.get().debug.debugPointingLocator){
            particleListString=particleListString+String.format("(%.2f,%.2f,%.2f),",pos.x,pos.y,pos.z);
            }
            avgPoint=avgPoint.add(pos);
        }
        avgPoint=avgPoint.multiply(1.0/particleList.size());
        String offsetListString="Offset:";
        String listKString="k(y/x,z/x):";
        int length = particleList.size();
        ArrayList<Double> listK1 = new ArrayList<Double>();
        ArrayList<Double> listK2 = new ArrayList<Double>();
        for(int index=0;index<length-1;index++){
            Vec3d pos0 = particleList.get(index);
            Vec3d pos1 = particleList.get(index+1);
            Vec3d direction = pos1.subtract(pos0);
            if(SkyblockerConfig.get().debug.debugPointingLocator){
            offsetListString=offsetListString+String.format("(%.2f,%.2f,%.2f),",direction.x,direction.y,direction.z);
            listKString=listKString+String.format("(%.2f,%.2f),",direction.y/direction.x,direction.z/direction.x);
            }
            listK1.add(direction.y/direction.x);
            listK2.add(direction.z/direction.x);

        }
        double sum1 = 0.0;
        double sum2 = 0.0;
        double k1 = 0.0;
        double k2 = 0.0;
        double b1 = 0.0;
        double b2 = 0.0;
        // processing values
        int size=listK1.size();
        sum1=0.0;
        sum2=0.0;
        for(int index=0;index<size;index++){
            sum1=sum1+listK1.get(index);
            sum2=sum2+listK2.get(index);
        }
        k1=sum1/size;
        k2=sum2/size;
        b1=avgPoint.y-avgPoint.x*k1;
        b2=avgPoint.z-avgPoint.x*k2;
        // calculating distant particle
        double maxDistance=0;
        int mostDistantPoint=0;
        for (int index=0;index<length;index++){
            Vec3d pos = particleList.get(index);
            double distance = (pos.y-(k1*pos.x)+b1)+(pos.z-(k2*pos.x)+b2);
            if(distance>maxDistance){
                maxDistance=distance;
                mostDistantPoint=index;
            }
        }
        if(SkyblockerConfig.get().debug.debugPointingLocator){
        client.player.sendMessage(new LiteralText("======[Skyblocker DEBUG Start]======").formatted(Formatting.DARK_GREEN), false);
        client.player.sendMessage(new LiteralText(particleListString).formatted(Formatting.DARK_GREEN), false);
        client.player.sendMessage(new LiteralText(offsetListString).formatted(Formatting.DARK_GREEN), false);
        client.player.sendMessage(new LiteralText(listKString).formatted(Formatting.DARK_GREEN), false);
        client.player.sendMessage(new LiteralText(String.format("avgPoint:(%.2f,%.2f,%.2f),mostDistantPoint:(%.2f,%.2f,%.2f),maxDistance=%2f",avgPoint.x,avgPoint.y,avgPoint.z,particleList.get(mostDistantPoint).x,particleList.get(mostDistantPoint).y,particleList.get(mostDistantPoint).z,maxDistance)).formatted(Formatting.DARK_GREEN), false);
        client.player.sendMessage(new LiteralText("======[Skyblocker DEBUG End]======").formatted(Formatting.DARK_GREEN), false);
        }
        client.player.sendMessage(new LiteralText(String.format("[Skyblocker] Captured ray: k1=%.2f,k2=%.2f,b1=%.2f,b2=%.2f, ",k1,k2,b1,b2)+"press ").append(keyUseCurrentLine.getBoundKeyLocalizedText()).append(" to store for calculation").formatted(Formatting.AQUA), false);
        /*if(maxDistance>=0.1){
            client.player.sendMessage(new LiteralText("[Skyblocker]This ray may be inaccurate because there is another thing emitting the same particles, such as some runes"), false);
        }*/
        double[] result = {k1,k2,b1,b2,maxDistance,mostDistantPoint};
        currentLine=new double[] {k1,k2,b1,b2};
        hasCurrentLine=true;
        return(result);
    }
    public static void useCurrentLine(){
        if(!hasCurrentLine){
            client.player.sendMessage(new LiteralText("[Skyblocker] No ray to store!").formatted(Formatting.RED),false);
            return;
        }
        hasCurrentLine=false;
        rayLists.putIfAbsent(locationOnLastCompassUse,new ArrayList<double[]>());
        ArrayList<double[]> rayList = rayLists.get(locationOnLastCompassUse);
        String targetName = LOCATION_NAMES[locationOnLastCompassUse];
        rayList.add(currentLine);
        if(rayList.size()>=2){
            Vec3d position = calculatePosition(rayList.get(0), rayList.get(1));
            Waypoint waypoint = new Waypoint(position,LOCATION_COLORS[locationOnLastCompassUse]);
            waypoint.locatorLines.addAll(rayList);
            String name = WaypointList.addAutoRenaming(targetName,waypoint);
            client.player.sendMessage(new LiteralText(String.format("[Skyblocker] Calculated %s at (%.0f,%.0f,%.0f) ",name,position.x,position.y,position.z)).formatted(Formatting.GREEN)
            .append(new LiteralText("[VIEW]").styled((style) -> {
                return style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbwp list")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("/sbwp list")));
            })), false);
            if(position.x>=463&&position.x<=563&&position.z>=460&&position.z<=564&&position.y>=63){ // Check if the target is in nucleus
                client.player.sendMessage(new LiteralText("[Skyblocker]The target is in Crystal Nucleus, maybe "+targetName+" doesn't exist in this lobby?").formatted(Formatting.YELLOW), false);
            }
            clearLocatorLines();
        }else{
            client.player.sendMessage(new LiteralText("[Skyblocker] Locator stored this ray for "+targetName+" , store one more ray to calculate or press ").append(keyClearLocatorLines.getBoundKeyLocalizedText()).append(" to reset").formatted(Formatting.AQUA), false);
        }
    }
    public static void clearLocatorLines(){
        for(ArrayList<double[]> list :rayLists.values())
        list.clear();
    }
    public static Vec3d calculatePosition(double[] line1,double[] line2){
        double ky1=line1[0];
        double kz1=line1[1];
        double by1=line1[2];
        double bz1=line1[3];
        double ky2=line2[0];
        double kz2=line2[1];
        double by2=line2[2];
        double bz2=line2[3];
        double x=(bz2-bz1)/(kz1-kz2);
        double z=kz1*x+bz1;
        double y=((ky1*x+by1)+(ky2*x+by2))/2;
        return new Vec3d(x,y,z);
    }
    public static void showLocatedTargets(){
        client.player.sendChatMessage("/sbwp list");
    }
    public static void drawLine(double[] line, float r, float g, float b, float t){
        double k1=line[0];
        double k2=line[1];
        double b1=line[2];
        double b2=line[3];
        double x=client.player.getPos().x;
        double x1=x-100;
        double x2=x+100;
        RenderUtils.drawLine(x1, x1*k1+b1, x1*k2+b2, x2, x2*k1+b1, x2*k2+b2, r,g,b,t);
    }
    public static void drawLineEnds(Vec3d start, Vec3d end, float r,float g,float b,float t){
        RenderUtils.drawLine(start.x,start.y,start.z,end.x,end.y,end.z,r,g,b,t);
    }
    public static void lineRenderer(WorldRenderContext wrc){
        try{
            for(int index:rayLists.keySet()){
                ArrayList<double[]> lineList = rayLists.get(index);
                for(double[] line:lineList){
                    drawLine(line, 1f, 1f, 1f, 2.0f);
                }
            }
            if(hasCurrentLine){
                drawLine(currentLine, 0f, 1f, 0f, 2.0f);
            }
        }
        catch(Exception e){
            System.out.println("LocatorLineRenderer: " + e.getStackTrace());
        }
    }
    public static int getPlayerLocation(){
        //JUNGLE 2   z<513    MITHRIL 4
        //x<512    NUCLEUS    x>513
        //GOBLINS 3  z>513    PRECURSOR 5
        ClientPlayerEntity player = client.player;
        int location;
        if(player.getY()<=63){
            location = LOCATION_MAGMA;
        }
        else{
                if(player.getZ()<513){
                    location = LOCATION_JUNGLE;
                }
                else{
                    location = LOCATION_GOBLINS;
                }
                if(player.getX()>513){
                    location=location+2;
                }
                else if(player.getX()>512){
                    location=LOCATION_NOTHING;
                }
        }
        return location;
    }
}
