package me.xmrvizzy.skyblocker.skyblock.solver;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

public class ContainerScreenSolverManager {
    static MinecraftClient client = MinecraftClient.getInstance();
    public static String currentContainer = null; 

    public static void screenChecker(Screen screen){
        if(Utils.isSkyblock && screen instanceof GenericContainerScreen){
            String container = screen.getTitle().getString();
            if(SkyblockerConfig.get().solvers.networkRelaySolver && "9f™ Network Relay".equals(container)){
                NetworkRelaySolver.enable(screen);
            }
            else NetworkRelaySolver.disable();
            currentContainer = container;
        }
        else{
            NetworkRelaySolver.disable();
        }
    } 
}
