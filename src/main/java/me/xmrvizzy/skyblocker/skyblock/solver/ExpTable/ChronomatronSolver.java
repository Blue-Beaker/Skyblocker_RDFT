package me.xmrvizzy.skyblocker.skyblock.solver.ExpTable;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ChronomatronSolver implements SoundInstanceListener{
    public static ChronomatronSolver instance = new ChronomatronSolver();
    MinecraftClient client = MinecraftClient.getInstance();
    public Boolean enabled = false;
    public void enable(){
        if(!enabled){
            client.getSoundManager().registerListener(this);
            if(SkyblockerConfig.get().debug.debugExpTableSolver)
            client.player.sendMessage(new LiteralText("Chronomatron Solver started"), false);
            enabled=true;
        }
    }
    public void disable(){
        if(enabled){
            client.getSoundManager().unregisterListener(this);
            if(SkyblockerConfig.get().debug.debugExpTableSolver)
            client.player.sendMessage(new LiteralText("Chronomatron Solver stopped"), false);
            enabled=false;
        }
    }
    @Override
    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet) {
        if(client.world!=null){
            String name = sound.getId().toString();
            if("minecraft:block.note_block.harp".equals(name)){
                Float pitch = sound.getPitch();
                client.player.sendMessage(new LiteralText(String.format("%.3f %s", pitch,name)).formatted(Formatting.AQUA), false);
            }
        }
    }
    
}
