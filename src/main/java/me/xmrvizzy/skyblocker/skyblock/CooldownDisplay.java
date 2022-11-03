package me.xmrvizzy.skyblocker.skyblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class CooldownDisplay {
    public static final int RIGHT_CLICK = 0;
    public static final int LEFT_CLICK = 1;
    public static final int ON_BREAK = 2;
    public static final int DUMMY = 3;
    public static final String[] buttonNames = new String[]{"RIGHT CLICK","LEFT CLICK"};
    public static HashMap<String,Integer> cooldowns = new HashMap<String,Integer>();
    public static HashMap<String,Ability> abilities = new HashMap<String,Ability>();
    public static HashMap<String,Ability> extraAbilities = new HashMap<String,Ability>();
    public static ArrayList<HashMap<String,Ability>> abilitiyOwners = new ArrayList<HashMap<String,Ability>>(2);
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static class Ability{
        public String ability;
        public int button;
        public int cooldown;
        public Ability(String ability,int button,int cooldown){
            this.ability=ability;
            this.button=button;
            this.cooldown=cooldown;
        }
        public String toString(){
            return ability+","+buttonNames[button]+","+cooldown+"s";
        }
    }
    public static void init(){
        abilitiyOwners.add(new HashMap<String,Ability>());
        abilitiyOwners.add(new HashMap<String,Ability>());
    }
    public static void tick(){
        ArrayList<String> cooldownsToRemove = new ArrayList<String>();
        for(String ability:cooldowns.keySet()){
            cooldowns.put(ability, cooldowns.get(ability)-1);
            if(cooldowns.get(ability)<=0){
                cooldownsToRemove.add(ability);
            }
        }
        for(String ability:cooldownsToRemove){
            cooldowns.remove(ability);
        }
    }
    public static Ability getAbility(int button,ItemStack item){
        Ability ability;
        if(PriceInfoTooltip.getInternalNameForItem(item)==null) return null;
        List<List<String>> lines = ItemUtils.getTooltipStringsBlocks(item,false);
        for(List<String> block:lines){
            String firstLine = block.get(0);
            if(firstLine.startsWith("Ability:")){
                if(firstLine.contains(buttonNames[button])){
                    String abilityName = firstLine.replace("Ability: ", "").replace(" "+buttonNames[button], "");
                    for(String line:block){
                        if(line.contains("Cooldown:")){
                            int cooldown;
                            try{
                                if(line.contains("s"))
                                cooldown = Integer.parseInt(line.replace("Cooldown: ", "").replace("s", ""));
                                else if(line.contains("m"))
                                cooldown = Integer.parseInt(line.replace("Cooldown: ", "").replace("m", ""))*60;
                                else return null;
                                ability = new Ability(abilityName, button, cooldown);
                                String skyblockId = PriceInfoTooltip.getInternalNameForItem(item);
                                abilitiyOwners.get(button).put(skyblockId, ability);
                                abilities.put(abilityName, ability);
                                return ability;
                            }
                            catch(NumberFormatException e){
                                return null;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    public static Ability getAbilityCached(int button,String id){
        return abilitiyOwners.get(button).get(id);
    }
    public static void setCooldown(String ability,int seconds){
        cooldowns.put(ability, seconds*20);
    }
    public static void setDefaultCooldown(int button){
        Ability ability = getAbility(button, client.player.getMainHandStack());
        if(ability==null || cooldowns.get(ability.ability)!=null) return;
        cooldowns.put(ability.ability, ability.cooldown*20);
    }
    public static float getCooldownProgress(Ability ability){
        float k = 0.0f;
        if(CooldownDisplay.cooldowns.containsKey(ability.ability)){
            int cooldown = CooldownDisplay.cooldowns.get(ability.ability);
            k = cooldown/(ability.cooldown*20f);
        }
        return k;
    }
}
