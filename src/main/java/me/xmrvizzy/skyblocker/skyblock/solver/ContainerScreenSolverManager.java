package me.xmrvizzy.skyblocker.skyblock.solver;

import java.util.ArrayList;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.solver.ExpTable.ChronomatronSolver;
import me.xmrvizzy.skyblocker.skyblock.solver.ExpTable.ExpTableScreenListener;
import me.xmrvizzy.skyblocker.skyblock.solver.NetworkRelay.NetworkRelaySolver;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.client.sound.SoundInstanceListener;

public class ContainerScreenSolverManager {
    static MinecraftClient client = MinecraftClient.getInstance();
    public static String currentContainer = null; 
    static ArrayList<ScreenHandlerListener> screenHandlerListeners = new ArrayList<ScreenHandlerListener>();
    static ArrayList<SoundInstanceListener> soundInstanceListeners = new ArrayList<SoundInstanceListener>();
    
    public static void screenChecker(Screen screen){
        if(Utils.isSkyblock && screen instanceof GenericContainerScreen){
            GenericContainerScreen containerScreen = (GenericContainerScreen)screen;
            String containerName = screen.getTitle().getString();
            if(containerName==null) return;
            if(SkyblockerConfig.get().solvers.networkRelaySolver && "9f™ Network Relay".equals(containerName)){
                NetworkRelaySolver.enable(containerScreen);
            }
            else NetworkRelaySolver.disable();
            if(SkyblockerConfig.get().solvers.expTableSolver &&(containerName.startsWith("Ultrasequencer (")||containerName.startsWith("Chronomatron (")||containerName.startsWith("Superpairs ("))){
                ExpTableScreenListener.instance.enable(containerScreen);
            }
            else ExpTableScreenListener.instance.disable();
            currentContainer = containerName;
        }
        else{
            NetworkRelaySolver.disable();
            ExpTableScreenListener.instance.disable();
        }
    } 
    public static void tick(Screen screen){
        if(screen instanceof GenericContainerScreen){
            GenericContainerScreen containerScreen = (GenericContainerScreen)screen;
            if(ExpTableScreenListener.instance.enabled){
                ExpTableScreenListener.instance.tick(containerScreen);
            }
        }
    }
}
