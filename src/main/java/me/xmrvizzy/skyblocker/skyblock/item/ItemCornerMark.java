package me.xmrvizzy.skyblocker.skyblock.item;

import java.util.List;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.StatIcons;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ItemCornerMark {
    static final SkyblockerConfig.CornerMarks config = SkyblockerConfig.get().items.cornerMarks;
    public static Text getCornerMark(ItemStack stack){
        String id = ItemUtils.getId(stack);
        if(id==null)return null;
        String sbid = PriceInfoTooltip.getInternalNameForItem(stack);
        if(config.potionLevels && id.endsWith("potion")){
            if(config.jerryPotionIntelligence && "BOTTLE_OF_JYRRE".equals(sbid)){
                List<String> tooltip = ItemUtils.getTooltipStrings(stack);
                for(String line:tooltip){
                    if(line.contains("Intelligence Bonus: "))
                    return new LiteralText("+"+line.replace("Intelligence Bonus: ", "")).formatted(Formatting.AQUA).append(StatIcons.INTELLIGENCE);
                }
            }else{
                String level = PotionOverlay.getLevel(stack.getName().getString());
                if(level!=null)
                return new LiteralText(level).setStyle(stack.getName().getStyle());
            }
        }else if(config.enchBookLevels && "enchanted_book".equals(id)){
            try{
                return new LiteralText(sbid.split(";")[1]).setStyle(stack.getName().getStyle());
            }catch(Exception e){
                return null;
            }
        }else{
                if(config.wishingCompassUses && "WISHING_COMPASS".equals(sbid)){
                    if(stack.getTag()!=null || stack.getTag().contains("ExtraAttributes") && stack.getTag().getCompound("ExtraAttributes").contains("wishing_compass_uses"))
                    return new LiteralText(String.valueOf(3-stack.getTag().getCompound("ExtraAttributes").getInt("wishing_compass_uses"))).formatted(Formatting.YELLOW);
                    else
                    return new LiteralText(String.valueOf(3)).formatted(Formatting.YELLOW);
                }
                else if(config.defuserTraps && "DEFUSE_KIT".equals(sbid) && stack.getTag()!=null && stack.getTag().contains("ExtraAttributes") && stack.getTag().getCompound("ExtraAttributes").contains("trapsDefused")){
                    return new LiteralText("+"+String.valueOf(stack.getTag().getCompound("ExtraAttributes").getInt("trapsDefused"))).formatted(Formatting.GREEN);
                    
                }
                else if(config.trainingWeightsStrength && "TRAINING_WEIGHTS".equals(sbid)){
                    List<String> tooltip = ItemUtils.getTooltipStrings(stack);
                    for(String line:tooltip){
                        if(line.contains("Strength Gain: "))
                        return new LiteralText(line.replace("Strength Gain: ", "")).formatted(Formatting.RED);
                    }
                }
        }
        return null;
    }
}
