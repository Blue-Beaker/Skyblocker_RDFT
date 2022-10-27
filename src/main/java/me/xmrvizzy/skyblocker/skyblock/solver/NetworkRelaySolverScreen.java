package me.xmrvizzy.skyblocker.skyblock.solver;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.util.collection.DefaultedList;

public class NetworkRelaySolverScreen implements ScreenHandlerListener {
    static MinecraftClient client = MinecraftClient.getInstance();
    public boolean enabled = false;
    GenericContainerScreen screen;
    public void enable(GenericContainerScreen screen){
        if(!enabled){
            this.screen=screen;
            screen.getScreenHandler().addListener(this);
            enabled=true;
        }
    }
    public void disable(){
        if(enabled){
            this.screen.getScreenHandler().removeListener(this);
            enabled=false;
        }
    }
    @Override
    public void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> stacks) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        NetworkRelaySolver.onSlotUpdate(handler, slotId, stack);
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
        // TODO Auto-generated method stub
        
    }
    
}
