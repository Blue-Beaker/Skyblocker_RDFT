package me.xmrvizzy.skyblocker.skyblock.waypoints;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Waypoint {
    public int[] pos = new int[]{0,0,0};
    public float[] color = new float[]{1f,1f,1f};
    public ArrayList<double[]> locatorLines = new ArrayList<double[]>();
    public Waypoint(Vec3d pos, float[] displayColor, ArrayList<double[]> lines){
        BlockPos blockPos = new BlockPos(pos);
        this.pos = new int[]{blockPos.getX(),blockPos.getY(),blockPos.getZ()};
        color = displayColor;
        locatorLines = lines;
    }
    public Waypoint(BlockPos pos, float[] displayColor, ArrayList<double[]> lines){
        this.pos = new int[]{pos.getX(),pos.getY(),pos.getZ()};
        color = displayColor;
        locatorLines = lines;
    }
    public Waypoint(Vec3d pos, float[] displayColor){
        BlockPos blockPos = new BlockPos(pos);
        this.pos = new int[]{blockPos.getX(),blockPos.getY(),blockPos.getZ()};
        color = displayColor;
    }
    public Waypoint(BlockPos pos, float[] displayColor){
        this.pos = new int[]{pos.getX(),pos.getY(),pos.getZ()};
        color = displayColor;
    }
    public Waypoint(Vec3d pos){
        BlockPos blockPos = new BlockPos(pos);
        this.pos = new int[]{blockPos.getX(),blockPos.getY(),blockPos.getZ()};
    }
    public Waypoint(BlockPos pos){
        this.pos = new int[]{pos.getX(),pos.getY(),pos.getZ()};
    }
    public int getX(){
        return pos[0];
    }
    public int getY(){
        return pos[1];
    }
    public int getZ(){
        return pos[2];
    }
    public BlockPos getBlockPos(){
        return new BlockPos(pos[0],pos[1],pos[2]);
    }
    public Vec3d getCenterPos(){
        return new Vec3d(pos[0]+0.5,pos[1]+0.5,pos[2]+0.5);
    }
}
