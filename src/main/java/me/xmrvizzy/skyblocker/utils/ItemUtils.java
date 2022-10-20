package me.xmrvizzy.skyblocker.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemUtils {

    public static List<Text> getTooltip(ItemStack item) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && item != null)
            return item.getTooltip(client.player, TooltipContext.Default.NORMAL);
        return Collections.emptyList();
    }

    public static List<String> getTooltipStrings(ItemStack item) {
        List<Text> lines = getTooltip(item);
        List<String> list = new ArrayList<>();

        for (Text line : lines) {
            String string = line.getString();
            if (!string.replaceAll("\\s+","").isEmpty())
                list.add(string);
        }

        return list;
    }

    public static List<List<String>> getTooltipStringsBlocks(ItemStack item) {
        List<Text> lines = getTooltip(item);
        List<List<String>> list = new ArrayList<List<String>>();
        list.add(new ArrayList<String>());
        for (Text line : lines) {
            String string = line.getString();
            if (!string.replaceAll("\\s+","").isEmpty()){
                list.get(list.size()-1).add(string);
            }
            else{
                if(list.get(list.size()-1).size()>0){
                    list.add(new ArrayList<String>());
                }
            }
        }

        return list;
    }
    public static String getId(Item item){
        return Registry.ITEM.getId(item).getPath();
    }
    public static String getId(ItemStack itemStack){
        return getId(itemStack.getItem());
    }
}