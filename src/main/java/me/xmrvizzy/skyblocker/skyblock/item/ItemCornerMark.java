package me.xmrvizzy.skyblocker.skyblock.item;

import java.util.List;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.dwarven.CustomCountLabel;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.StatIcons;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
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
                if(sbid.startsWith("ULTIMATE_"))
                return new LiteralText(sbid.split(";")[1]).formatted(Formatting.LIGHT_PURPLE);
                else
                return new LiteralText(sbid.split(";")[1]).formatted(Formatting.WHITE);
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
                else if(config.prehistoricEggSteps && "PREHISTORIC_EGG".equals(sbid) && stack.getTag()!=null && stack.getTag().contains("ExtraAttributes") && stack.getTag().getCompound("ExtraAttributes").contains("blocks_walked")){
                    int steps = stack.getTag().getCompound("ExtraAttributes").getInt("blocks_walked");
                    Formatting formatting;
                    if(steps<4000){
                        formatting=Formatting.WHITE;
                    }else if(steps<10000){
                        formatting=Formatting.GREEN;
                    }else if(steps<20000){
                        formatting=Formatting.BLUE;
                    }else if(steps<40000){
                        formatting=Formatting.DARK_PURPLE;
                    }else{
                        formatting=Formatting.GOLD;
                    }
                    return new LiteralText(String.valueOf(steps)).formatted(formatting);
                    
                }
                else if(config.defuserTraps && "DEFUSE_KIT".equals(sbid) && stack.getTag()!=null && stack.getTag().contains("ExtraAttributes") && stack.getTag().getCompound("ExtraAttributes").contains("trapsDefused")){
                    return new LiteralText(String.valueOf(stack.getTag().getCompound("ExtraAttributes").getInt("trapsDefused"))).formatted(Formatting.GREEN);
                    
                }
                else if(config.trainingWeightsStrength && "TRAINING_WEIGHTS".equals(sbid)){
                    List<String> tooltip = ItemUtils.getTooltipStrings(stack);
                    for(String line:tooltip){
                        if(line.contains("Strength Gain: "))
                        return new LiteralText(line.replace("Strength Gain: ", "")).formatted(Formatting.RED);
                    }
                }
                else if(config.runeLevels && "RUNE".equals(sbid)){
                    int level = CustomCountLabel.getSkillLevel(stack);
                    Text rarity = ItemUtils.getLoreLine(stack, -1);
                    return new LiteralText(String.valueOf(level)).setStyle(rarity.getSiblings().get(0).getStyle().withBold(false));
                }
                else{
                    if(SkyblockerConfig.get().items.cornerMarks.petLevels){
                        Text nameText = stack.getName();
                        String name = stack.getName().getString();
                        if(name.startsWith("[Lvl ")){
                            String lvl = name.substring(5, 8).replace("]", "").replace(" ", "");
                            try{
                                List<Text> namelist = nameText.getSiblings();
                                Style style = namelist.get(1).getStyle();
                                return new LiteralText(""+lvl).setStyle(style.withBold(false));
                            }catch(Exception e){
                                return new LiteralText(""+lvl).formatted(Formatting.GRAY);
                            }
                        }
                    }
                    if(SkyblockerConfig.get().ui.collectionLevels){
                    try {
                        String line2 = ItemUtils.getLoreLine(stack, 0).getString();
                        if(line2.startsWith("View all your")){
                            String line5 = ItemUtils.getLoreLine(stack, 3).getString();
                            return new LiteralText(String.valueOf(CustomCountLabel.getSkillLevel(stack))).formatted((line5.startsWith("Progress to")) ? Formatting.WHITE : Formatting.GOLD);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        return null;
    }
}
