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
            client.player.sendMessage(new LiteralText("Chronomatron Solver started").formatted(Formatting.BOLD), false);
            enabled=true;
        }
    }
    public void disable(){
        if(enabled){
            client.getSoundManager().unregisterListener(this);
            client.player.sendMessage(new LiteralText("Chronomatron Solver stopped").formatted(Formatting.BOLD), false);
            enabled=false;
        }
    }
    @Override
    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet) {
        if(client.world!=null){
            String name = sound.getId().toString();
            if("minecraft:block.note_block.pling".equals(name)){
                Float pitch = sound.getPitch();
                Formatting formatting;
                if(pitch<0.557f){
                    formatting=Formatting.RED;
                }else if(pitch<0.620f){
                    formatting=Formatting.BLUE;
                }else if(pitch<0.747f){
                    formatting=Formatting.GREEN;
                }else if(pitch<0.826f){
                    formatting=Formatting.YELLOW;
                }else if(pitch<1.001f){
                    formatting=Formatting.AQUA;
                }else if(pitch<1.112f){
                    formatting=Formatting.LIGHT_PURPLE;
                }else if(pitch<1.255f){
                    formatting=Formatting.DARK_GREEN;
                }else if(pitch<1.493f){
                    formatting=Formatting.DARK_AQUA;
                }else if(pitch<1.668f){
                    formatting=Formatting.GOLD;
                }else{
                    formatting=Formatting.DARK_PURPLE;
                }
                client.player.sendMessage(new LiteralText(String.format("%.3f", pitch)).formatted(formatting), false);
            }
        }
    }
    
}
