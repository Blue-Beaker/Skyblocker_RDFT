package me.xmrvizzy.skyblocker.skyblock.waypoints;

import org.lwjgl.opengl.GL11;

import me.xmrvizzy.skyblocker.skyblock.Locator;
import me.xmrvizzy.skyblocker.utils.RenderUtils;
import me.xmrvizzy.skyblocker.utils.RenderUtilsLiving;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class WaypointRenderer {
    static boolean renderHooked = false;
    static MinecraftClient client = MinecraftClient.getInstance();
    public static void tick(){
        if(!renderHooked){
            WorldRenderEvents.END.register(WaypointRenderer::drawWaypoints);
            renderHooked = true;
        }
    }
    public static void drawWaypoints(WorldRenderContext wrc){
        try{
            MatrixStack matrices = new MatrixStack();
            if(WaypointList.get(Utils.serverArea)!=null){
                for(String name : WaypointList.get(Utils.serverArea).keySet()){
                    Waypoint waypoint = WaypointList.get(Utils.serverArea).get(name);
                    //Locator.drawLineEnds(target, new Vec3d(client.player.getX(),client.player.getEyeY(),client.player.getZ()), 0.0f,0.0f,1.0f,1.0f);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    Double distance = Math.sqrt(waypoint.blockPos.getSquaredDistance(client.player.getPos(), false));
                    Double sqrtdistance = Math.sqrt(distance);
                    RenderUtils.drawOutlineBox(waypoint.blockPos, waypoint.color[0], waypoint.color[1], waypoint.color[2],1.0f, new Float(5.0f/sqrtdistance));
                    RenderUtilsLiving.drawTextColored(matrices, String.format("%s[%.0fm]", name,distance), waypoint.blockPos.getX()+0.5, waypoint.blockPos.getY()+0.5, waypoint.blockPos.getZ()+0.5, sqrtdistance*0.5, waypoint.color[0], waypoint.color[1], waypoint.color[2], 1.0f);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    for(double[] line : waypoint.locatorLines){
                        Locator.drawLine(line, waypoint.color[0], waypoint.color[1], waypoint.color[2], 5.0f);
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println("WaypointRenderer: " + e.getMessage());
        }
    }
}
