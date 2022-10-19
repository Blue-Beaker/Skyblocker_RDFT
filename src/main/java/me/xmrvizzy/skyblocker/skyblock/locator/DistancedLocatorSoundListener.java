package me.xmrvizzy.skyblocker.skyblock.locator;


import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class DistancedLocatorSoundListener implements SoundInstanceListener {
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
        if(client.world!=null && SkyblockerConfig.get().locations.dwarvenMines.metalDetectorLocator){
            String name = sound.getId().toString();
            if("minecraft:block.note_block.harp".equals(name)){
                pos = new Vec3d(sound.getX(),sound.getY(),sound.getZ());
            }
        }
    }
  
}
