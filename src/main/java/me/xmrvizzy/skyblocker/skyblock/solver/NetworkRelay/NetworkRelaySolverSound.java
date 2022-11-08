package me.xmrvizzy.skyblocker.skyblock.solver.NetworkRelay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.math.Vec3d;

public class NetworkRelaySolverSound implements SoundInstanceListener {
    
    static MinecraftClient client = MinecraftClient.getInstance();
    public boolean enabled = false;
    public Vec3d pos;
    public void enable(){
        if(!enabled){
            client.getSoundManager().registerListener(this);
            enabled=true;
        }
    }
    public void disable(){
        if(enabled){
            client.getSoundManager().unregisterListener(this);
            enabled=false;
        }
    }
    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet) {
        NetworkRelaySolver.onSoundPlayed(sound, soundSet);
    }
  
}
