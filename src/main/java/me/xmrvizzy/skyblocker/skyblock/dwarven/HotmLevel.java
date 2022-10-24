package me.xmrvizzy.skyblocker.skyblock.dwarven;


import java.util.List;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class HotmLevel {
    public static int getHotmPerkLevel(ItemStack stack){
        if(Utils.isSkyblock && SkyblockerConfig.get().locations.dwarvenMines.hotmPerkLevels){
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
        }
        return 0;
    }
}
