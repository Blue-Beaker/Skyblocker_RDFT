package me.xmrvizzy.skyblocker.skyblock;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.text.LiteralText;

public class SoundPrinter implements SoundInstanceListener{

    public static SoundPrinter instance = new SoundPrinter();
    MinecraftClient client = MinecraftClient.getInstance();
    Boolean enabled = false;
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
    @Override
    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet) {
        // TODO Auto-generated method stub
        client.player.sendMessage(new LiteralText(String.format("%s,%.1f,%.1f", sound.getId().toString(),sound.getPitch(),sound.getVolume())), false);
    }
    public void check(){
        if(SkyblockerConfig.get().debug.printSounds) enable();
        else disable();
    }
}
