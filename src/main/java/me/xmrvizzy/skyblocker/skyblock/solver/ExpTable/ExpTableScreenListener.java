package me.xmrvizzy.skyblocker.skyblock.solver.ExpTable;

import java.util.ArrayList;
import java.util.List;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.util.collection.DefaultedList;

public class ExpTableScreenListener implements ScreenHandlerListener{
    public static ExpTableScreenListener instance = new ExpTableScreenListener();
    public Boolean enabled = false;
    public Boolean update = true;
    GenericContainerScreen screen;
    MinecraftClient client = MinecraftClient.getInstance();
    ArrayList<Slot> slots = new ArrayList<Slot>();
    
    public void enable(GenericContainerScreen screen){
        this.screen=screen;
        if(!enabled){
            screen.getScreenHandler().addListener(this);
            if(SkyblockerConfig.get().debug.debugExpTableSolver)
            client.player.sendMessage(new LiteralText("ExpTableListener added for ").append(screen.getTitle()), false);
            if(screen.getTitle().getString().startsWith("Chronomatron (")){
                ChronomatronSolver.instance.enable();
            }
            enabled=true;
        }
    }
    public void disable(){
        if(enabled){
            this.screen.getScreenHandler().removeListener(this);
            ChronomatronSolver.instance.disable();
            if(SkyblockerConfig.get().debug.debugExpTableSolver)
            client.player.sendMessage(new LiteralText("ExpTableListener stopped for ").append(screen.getTitle()), false);
            enabled=false;
        }
    }
    @Override
    public void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> stacks) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        String id = ItemUtils.getId(stack);
        client.player.sendMessage(new LiteralText(id+slotId), false);
        if(id==null) return;
        Slot slot = handler.slots.get(slotId);
        if(slot.hasStack()){
            ItemStack stack2 = slot.getStack();
            if(ItemUtils.getId(stack2).endsWith("glass_pane")){

            }else{

            }
        }
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
        // TODO Auto-generated method stub
        
    }

    public void tick(GenericContainerScreen screen){
        ScreenHandler handler = screen.getScreenHandler();
        ItemStack indicatorItem = handler.slots.get(handler.slots.size()-41).getStack();
        if(indicatorItem.isEmpty()) return;
        String id = ItemUtils.getId(indicatorItem);
        if(SkyblockerConfig.get().debug.debugExpTableSolver)
        client.player.sendMessage(new LiteralText(id), false);
        if(id.equals("glowstone")){
            update=true;
        }else{
            update=false;
        }
    }
}
