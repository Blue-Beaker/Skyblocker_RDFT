package me.xmrvizzy.skyblocker.skyblock.item;

import java.text.Normalizer.Form;
import java.util.HashMap;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.StatIcons;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos.Mutable;

public class PotionOverlay {
    static final HashMap<String,String> ROMAN_NUMERALS = new HashMap<>();
    static final HashMap<String,Text> POTION_TYPES = new HashMap<>();
    static final HashMap<String,Text> XPBOOST_TYPES = new HashMap<>();
    static {
        ROMAN_NUMERALS.put(" I", "1");
        ROMAN_NUMERALS.put(" II", "2");
        ROMAN_NUMERALS.put(" III", "3");
        ROMAN_NUMERALS.put(" IV", "4");
        ROMAN_NUMERALS.put(" V", "5");
        ROMAN_NUMERALS.put(" VI", "6");
        ROMAN_NUMERALS.put(" VII", "7");
        ROMAN_NUMERALS.put(" VIII", "8");
        ROMAN_NUMERALS.put(" IX", "9");
        ROMAN_NUMERALS.put(" X", "10");

        POTION_TYPES.put("Healing", StatIcons.HEALTH);
        POTION_TYPES.put("Damage", new LiteralText("❤-").formatted(Formatting.DARK_RED));
        POTION_TYPES.put("Poison", new LiteralText("❤-").formatted(Formatting.DARK_GREEN));
        POTION_TYPES.put("Regeneration", StatIcons.HEALTH_REGEN);
        POTION_TYPES.put("Wounded", new LiteralText("❣-").formatted(Formatting.DARK_RED));
        POTION_TYPES.put("Absorption", StatIcons.ABSORPTION);
        POTION_TYPES.put("Resistance", StatIcons.DEFENSE);
        POTION_TYPES.put("True Resistance", StatIcons.TRUE_DEFENSE);
        POTION_TYPES.put("Strength", StatIcons.STRENGTH);
        POTION_TYPES.put("Weakness", new LiteralText("❁-").formatted(Formatting.DARK_RED));
        POTION_TYPES.put("Speed", StatIcons.SPEED);
        POTION_TYPES.put("Slowness", new LiteralText("✦-").formatted(Formatting.GRAY));
        POTION_TYPES.put("Critical", StatIcons.CRIT_DAMAGE);
        POTION_TYPES.put("Mana", StatIcons.INTELLIGENCE);
        POTION_TYPES.put("Haste", StatIcons.MINING_SPEED);
        POTION_TYPES.put("Spelunker", StatIcons.FORTUNE);
        POTION_TYPES.put("Magic Find", StatIcons.MAGIC_FIND);
        POTION_TYPES.put("Knockback", new LiteralText("KB").formatted(Formatting.DARK_RED));
        POTION_TYPES.put("Archery", new LiteralText("🏹").formatted(Formatting.AQUA));
        POTION_TYPES.put("Jump Boost", new LiteralText("↑").formatted(Formatting.GREEN));
        POTION_TYPES.put("Rabbit", new LiteralText("↑").formatted(Formatting.GREEN).append(StatIcons.SPEED));
        POTION_TYPES.put("Adrenaline", StatIcons.ABSORPTION.shallowCopy().append(StatIcons.SPEED));
        POTION_TYPES.put("Stamina", StatIcons.HEALTH.shallowCopy().append(StatIcons.INTELLIGENCE));
        POTION_TYPES.put("Pet Luck", StatIcons.HEALTH.shallowCopy().append(StatIcons.PET_LUCK));
        POTION_TYPES.put("Spirit", StatIcons.SPEED.shallowCopy().append(StatIcons.CRIT_DAMAGE));

        POTION_TYPES.put("Awkward", StatIcons.ABILITY_DAMAGE.formatted(Formatting.AQUA));

        XPBOOST_TYPES.put("Farming", StatIcons.FORTUNE.formatted(Formatting.YELLOW));
        XPBOOST_TYPES.put("Mining", StatIcons.MINING_SPEED);
        XPBOOST_TYPES.put("Combat", new LiteralText("🗡").formatted(Formatting.RED));
        XPBOOST_TYPES.put("Foraging", new LiteralText("🪓").formatted(Formatting.GREEN));
        XPBOOST_TYPES.put("Fishing", StatIcons.FISHING_SPEED);
        XPBOOST_TYPES.put("Enchanting", StatIcons.INTELLIGENCE);
        XPBOOST_TYPES.put("Alchemy", StatIcons.ABILITY_DAMAGE.formatted(Formatting.DARK_PURPLE));
    }
    public static Text getPotionOverlay(ItemStack stack){
        String id = ItemUtils.getId(stack);
        if(id!=null && id.contains("potion")){
            Text name = stack.getName();
            String nameString = name.getString();
            String level = null;
            if(SkyblockerConfig.get().items.potionOverlayLevels) level = getLevel(nameString);
            MutableText type = getPotionType(nameString);
            if(type!=null){
                if(level!=null)
                return type.append(level);
                else
                return type;
            }
            else if(level!=null)
            return new LiteralText(level);
            }
        return null;
    }
    public static MutableText getPotionType(String name){
        if(name.contains("XP Boost")){
            if(SkyblockerConfig.get().items.potionOverlayXPBoostTypes)
            for(String type:XPBOOST_TYPES.keySet()){
                if(name.startsWith(type))
                return XPBOOST_TYPES.get(type).shallowCopy().append(StatIcons.WISDOM.shallowCopy());
            }
            return StatIcons.WISDOM.shallowCopy();
        }else if(name.startsWith("Water Bottle")){
            return new LiteralText("W").formatted(Formatting.BLUE);
        }
        else{
            for(String type:POTION_TYPES.keySet()){
                if(name.startsWith(type)){
                    return POTION_TYPES.get(type).shallowCopy();
                }
            }
        }
        return null;
    }
    public static String getLevel(String name){
        for(String str:ROMAN_NUMERALS.keySet()){
            if(name.replace(" Potion", "").endsWith(str))
            return ROMAN_NUMERALS.get(str);
        }
        return null;
    }
}
