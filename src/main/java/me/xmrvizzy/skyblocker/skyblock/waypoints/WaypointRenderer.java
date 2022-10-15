package me.xmrvizzy.skyblocker.skyblock.waypoints;

import me.xmrvizzy.skyblocker.skyblock.Locator;
import me.xmrvizzy.skyblocker.utils.RenderUtils;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class WaypointRenderer {
    static boolean renderHooked = false;
    public static void tick(){
        if(!renderHooked){
            WorldRenderEvents.END.register(WaypointRenderer::drawWaypoints);
            renderHooked = true;
        }
    }
    public static void drawWaypoints(WorldRenderContext wrc){
        try{
        for(Waypoint target : WaypointList.get(Utils.serverArea).values()){
            //Locator.drawLineEnds(target, new Vec3d(client.player.getX(),client.player.getEyeY(),client.player.getZ()), 0.0f,0.0f,1.0f,1.0f);
            RenderUtils.drawOutlineBox(target.blockPos, target.color[0], target.color[1], target.color[2],1.0f, 5.0f);
            for(double[] line : target.locatorLines){
                Locator.drawLine(line, target.color[0], target.color[1], target.color[2], 5.0f);
            }
        }
        }
        catch(Exception e){
            System.out.println("WaypointRenderer: " + e.getStackTrace());
        }
    }
}
