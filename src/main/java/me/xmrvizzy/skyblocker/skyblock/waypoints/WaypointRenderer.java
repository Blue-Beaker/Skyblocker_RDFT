package me.xmrvizzy.skyblocker.skyblock.waypoints;

import org.lwjgl.opengl.GL11;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.locator.PointedLocator;
import me.xmrvizzy.skyblocker.utils.RenderUtils;
import me.xmrvizzy.skyblocker.utils.RenderUtilsLiving;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

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
            drawWaypoints(matrices, Utils.serverArea);
            if("CrystalHollows".equals(Utils.serverArea)){
                drawWaypoints(matrices, Utils.getLobbyAutoCH());
            }
        }
        catch(Exception e){
            System.out.println("WaypointRenderer: " + e.getMessage());
        }
    }
    public static void drawWaypoints(MatrixStack matrices,String area){
        if(WaypointList.get(area)!=null){
            for(String name : WaypointList.get(area).keySet()){
                Waypoint waypoint = WaypointList.get(area).get(name);
                //Locator.drawLineEnds(target, new Vec3d(client.player.getX(),client.player.getEyeY(),client.player.getZ()), 0.0f,0.0f,1.0f,1.0f);
                Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
                Double distance = Math.sqrt(waypoint.getBlockPos().getSquaredDistance(client.player.getPos(), false));
                Double cameraDistance = Math.sqrt(waypoint.getCenterPos().squaredDistanceTo(cameraPos));
                Vec3d direction = waypoint.getCenterPos().subtract(cameraPos).normalize().multiply(10.0);
                Vec3d renderPos;
                if(cameraDistance>10.0)renderPos = direction.add(cameraPos);
                else renderPos=waypoint.getCenterPos();
                Double renderSize = Math.min(cameraDistance,10.0)*SkyblockerConfig.get().waypoint.labelSize;
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                RenderUtils.drawOutlineBox(waypoint.getBlockPos(), waypoint.color[0], waypoint.color[1], waypoint.color[2],1.0f, SkyblockerConfig.get().waypoint.outlineWidth);
                RenderUtilsLiving.drawTextColored(matrices, String.format("%s[%.0fm]", name,distance), renderPos.x,renderPos.y,renderPos.z, renderSize, waypoint.color[0], waypoint.color[1], waypoint.color[2], 1.0f);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                for(double[] line : waypoint.locatorLines){
                    PointedLocator.drawLine(line, waypoint.color[0], waypoint.color[1], waypoint.color[2], SkyblockerConfig.get().waypoint.lineWidth);
                }
            }
        }
    }
}
