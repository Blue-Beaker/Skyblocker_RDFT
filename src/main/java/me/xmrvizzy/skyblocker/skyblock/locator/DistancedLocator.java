package me.xmrvizzy.skyblocker.skyblock.locator;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.utils.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class DistancedLocator {
    static MinecraftClient client = MinecraftClient.getInstance();
    static DistancedLocatorSoundListener soundListener = new DistancedLocatorSoundListener();
    static float lastDistance = 0.0f;
    static float nowDistance = 0.0f;
    static boolean renderHooked = false;
    static boolean active = false;
    static int still = 0;
    static ArrayList<BlockPos> blockList = new ArrayList<BlockPos>();
    public static void tick(){
        String skyblockId = PriceInfoTooltip.getInternalNameForItem(client.player.getMainHandStack());
        if("DWARVEN_METAL_DETECTOR".equals(skyblockId)){
            soundListener.enable();
            if(!renderHooked){
                WorldRenderEvents.END.register(DistancedLocator::render);
                renderHooked = true;
            }
            active=true;
        }
        else{
            soundListener.disable();
            active=false;
        }
    }
    public static String tick(String msg){
        if(msg.contains("TREASURE:")){
            String dist = msg.split(":")[1].replace(" ", "").replace("m", "").replace("§b","");
            nowDistance = Float.parseFloat(dist);
            if(nowDistance==lastDistance){
                if(still<100)
                still++;
            }
            else still=0;
            lastDistance=nowDistance;
            if(SkyblockerConfig.get().locations.dwarvenMines.metalDetectorLocatorFast){
            calculate(soundListener.pos,nowDistance);
            msg=msg+"§c DISABLE MUSIC";}
            else if(still>1){
            calculate(client.player.getPos(),nowDistance);
            msg=msg+"§c Move a bit";
            }
            else msg=msg+"§c Stand Still";
            if(SkyblockerConfig.get().debug.debugDistancedLocator){
                client.player.sendMessage(new LiteralText(String.valueOf(blockList.size())),false);
            }
        }
        return msg;
    }
    public static void calculate(Vec3d pos, float distance){
        if(blockList.isEmpty()){
            startTracking(pos, distance);
        }else{
            ArrayList<BlockPos> removeList = new ArrayList<BlockPos>();
            for(BlockPos block:blockList){
                if(!atDistance(block, pos, distance)){
                    removeList.add(block);
                }
            }
            blockList.removeAll(removeList);
        }
    }
    public static void startTracking(Vec3d pos, float distance){
        int x1=(int)Math.floor(pos.x-distance);
        int x2=(int)Math.ceil(pos.x+distance);
        int y1=64;
        int y2=80;
        int z1=(int)Math.floor(pos.z-distance);
        int z2=(int)Math.ceil(pos.z+distance);
        for(int x=x1;x<x2;x++){
            for(int y=y1;y<y2;y++){
                for(int z=z1;z<z2;z++){
                    BlockPos block = new BlockPos(x,y,z);
                    if(atDistance(block, pos, distance)){
                        blockList.add(block);
                    }
                }
            }
        }
    }
    public static boolean atDistance(BlockPos block,Vec3d pos,float distance){
        return Math.abs(Math.sqrt(block.getSquaredDistance(pos.x, pos.y, pos.z, false))-distance)<0.6;
    }
    public static void render(WorldRenderContext wrc){
        if(active){
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        ArrayList<BlockPos> list2 = new ArrayList<BlockPos>();
        list2.addAll(blockList);
        if(list2.size()<300)
        for(BlockPos block : list2){
            //RenderUtils.drawOutlineBox(block, 0f, 0.5f, 1f, 1f);
            RenderUtils.drawFilledBox(block, 0f, 0.5f, 1f, 0.5f);
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }
}
