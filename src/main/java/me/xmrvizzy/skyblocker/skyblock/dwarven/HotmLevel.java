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
import net.minecraft.text.Text;

public class HotmLevel {
    public static int getLevel(ItemStack stack){
        if(!Utils.isSkyblock || stack.getCount()>1 || PriceInfoTooltip.getInternalNameForItem(stack)!=null) return 0;
        String name = ItemUtils.getId(stack);
        if(name.endsWith("glass_pane")) return 0;
        if(SkyblockerConfig.get().ui.hotmPerkLevels){
            int level = getPerkLevel(stack);
            if(level>0) return level;
        }
        if(SkyblockerConfig.get().ui.skillLevels){
            int level = getSkillLevel(stack);
            if(level>0) return level;
        }
        return 0;
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
}
