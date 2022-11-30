package me.xmrvizzy.skyblocker.skyblock.dwarven;


import java.util.ArrayList;
import java.util.List;

import javax.rmi.CORBA.Util;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.RomanNumeralsParser;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class CustomCountLabel {
    public static Text getLabel(ItemStack stack){
        if(!Utils.isSkyblock || stack.getCount()>1) return null;
        String name = ItemUtils.getId(stack);
        if(name.endsWith("glass_pane")) return null;
        if(SkyblockerConfig.get().ui.sackItemCount){
            String count = getSackCount(stack);
            if(count!=null) return new LiteralText(count);
        }
        if(SkyblockerConfig.get().ui.hotmPerkLevels && PriceInfoTooltip.getInternalNameForItem(stack)==null){
            int level = getPerkLevel(stack);
            if(level>0) return new LiteralText(String.valueOf(level));
        }
        if(SkyblockerConfig.get().ui.skillLevels){
            int level = getSkillLevel(stack);
            if(level>0 && level<1000) return new LiteralText(String.valueOf(level));
        }
        return null;
    }
    public static int getPerkLevel(ItemStack stack){
        String name = ItemUtils.getId(stack);
        if("diamond".equals(name)||"emerald".equals(name)||"redstone_block".equals(name)){
            List<Text> tooltip = ItemUtils.getTooltip(stack);
            if(tooltip.size()>=2 && PriceInfoTooltip.getInternalNameForItem(stack)==null){
                String levels = tooltip.get(1).getString();
                if(levels.contains("Level ")){
                    try{
                    int level = Integer.parseInt(levels.split("/",2)[0].replace("Level ", ""));
                    return level;
                    }
                    catch(Exception e){
                        return 0;
                    }
                }
            }
        }
        return 0;
    }
    public static int getSkillLevel(ItemStack stack){
        try{
            String[] namesplit = stack.getName().getString().split(" ");
            if(namesplit.length<2) return 0;
            try{
                int level = Integer.parseInt(namesplit[namesplit.length-1]);
                return level;
            }catch(Exception e){
                int level = RomanNumeralsParser.romanToInt(namesplit[namesplit.length-1]);
                if(level>0) return level;
                else return 0;
            }
        }catch(Exception e){
        }
        return 0;
    }
    public static String getSackCount(ItemStack stack){
        //List<Text> tooltip = ItemUtils.getTooltip(stack);
        try{
            String line = ItemUtils.getLoreLine(stack, 2).getString();
            //String line = tooltip.get(3).getString();
            if(line.startsWith("Stored: ") || line.startsWith(" Amount: ")){
                int count = Integer.parseInt(line.replaceAll(" ","").substring(7).split("/")[0].replaceAll(",", ""));
                if(count<1000){
                    return String.valueOf(count);
                }else if(count<1000000){
                    return String.valueOf(count/1000)+"k";
                }else{
                    return String.valueOf(count/1000000)+"M";
                }
            }
        }
        catch(Exception e){

        }
        return null;
    }
}
