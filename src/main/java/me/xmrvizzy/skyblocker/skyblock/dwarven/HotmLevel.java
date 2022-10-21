package me.xmrvizzy.skyblocker.skyblock.dwarven;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.item.ItemStack;

public class HotmLevel {
    public static int getHotmPerkLevel(ItemStack stack){
        if(Utils.isSkyblock && SkyblockerConfig.get().locations.dwarvenMines.hotmPerkLevels){
            String name = ItemUtils.getId(stack);
            if(("diamond".equals(name)||"emerald".equals(name))&& PriceInfoTooltip.getInternalNameForItem(stack)==null){
                String levels = ItemUtils.getTooltip(stack).get(1).getString();
                if(levels.contains("Level ")){
                    int level = Integer.parseInt(levels.split("/",2)[0].replace("Level ", ""));
                    return level;
                }
            }
        }
        return 0;
    }
}
