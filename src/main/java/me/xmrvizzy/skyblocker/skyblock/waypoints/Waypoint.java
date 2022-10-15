package me.xmrvizzy.skyblocker.skyblock.waypoints;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Waypoint {
    public BlockPos blockPos;
    public float[] color = new float[]{1f,1f,1f};
    public ArrayList<double[]> locatorLines = new ArrayList<double[]>();
    public Waypoint(Vec3d pos, float[] displayColor, ArrayList<double[]> lines){
        blockPos = new BlockPos(pos);
        color = displayColor;
        locatorLines = lines;
    }
    public Waypoint(BlockPos pos, float[] displayColor, ArrayList<double[]> lines){
        blockPos = pos;
        color = displayColor;
        locatorLines = lines;
    }
    public Waypoint(Vec3d pos, float[] displayColor){
        blockPos = new BlockPos(pos);
        color = displayColor;
    }
    public Waypoint(BlockPos pos, float[] displayColor){
        blockPos = pos;
        color = displayColor;
    }
    public Waypoint(Vec3d pos){
        blockPos = new BlockPos(pos);
    }
    public Waypoint(BlockPos pos){
        blockPos = pos;
    }
}
