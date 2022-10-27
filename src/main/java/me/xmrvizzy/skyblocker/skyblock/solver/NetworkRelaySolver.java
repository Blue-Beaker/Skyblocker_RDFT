package me.xmrvizzy.skyblocker.skyblock.solver;

import me.xmrvizzy.skyblocker.utils.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class NetworkRelaySolver{
    static MinecraftClient client = MinecraftClient.getInstance();
    public static final NetworkRelaySolverScreen NETWORK_RELAY_SOLVER_SCREEN = new NetworkRelaySolverScreen();
    public static final NetworkRelaySolverSound NETWORK_RELAY_SOLVER_SOUND = new NetworkRelaySolverSound();
    public static Screen screen;
    static int lastSlot = 0;
    public static boolean enabled = false;
    public static void enable(Screen screen){
        if(!enabled){
            NetworkRelaySolver.screen=screen;
            //NETWORK_RELAY_SOLVER_SCREEN.enable(screen);
            NETWORK_RELAY_SOLVER_SOUND.enable();
            client.player.sendMessage(new LiteralText("Started 9f™ relay solver").formatted(Formatting.AQUA), false);
            enabled=true;
        }
    }
    public static void disable(){
        if(enabled){
            //NETWORK_RELAY_SOLVER_SCREEN.disable();
            NETWORK_RELAY_SOLVER_SOUND.disable();
            client.player.sendMessage(new LiteralText("Stopped 9f™ relay solver").formatted(Formatting.AQUA), false);
            enabled=false;
        }
    }
    public static void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet) {
        if(client.world!=null){
            String name = sound.getId().toString();
            Float pitch = sound.getPitch();
            client.player.sendMessage(new LiteralText(String.format("%.3f %s", pitch,name)).formatted(Formatting.AQUA), false);
        }
    }
    public static void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack){
        if("yellow_stained_glass_pane".equals(ItemUtils.getId(stack))){
            lastSlot=slotId;
        }
    }
}