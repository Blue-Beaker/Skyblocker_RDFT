package me.xmrvizzy.skyblocker.mixin;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.CooldownDisplay;
import me.xmrvizzy.skyblocker.skyblock.CooldownDisplay.Ability;
import me.xmrvizzy.skyblocker.skyblock.dungeon.DungeonPuzzles;
import me.xmrvizzy.skyblocker.skyblock.dwarven.Fetchur;
import me.xmrvizzy.skyblocker.skyblock.dwarven.Puzzler;
import me.xmrvizzy.skyblocker.skyblock.waypoints.AutoWaypoint;
import me.xmrvizzy.skyblocker.skyblock.waypoints.Waypoint;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.List;
import java.util.UUID;

@Mixin(ChatHudListener.class)
public class ChatHudListenerMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onMessage(MessageType messageType, Text message, UUID senderUuid, CallbackInfo ci) {
        String msg = message.getString();

        if (Utils.isDungeons) {
            if (SkyblockerConfig.get().locations.dungeons.solveThreeWeirdos && msg.contains("[NPC]"))
                DungeonPuzzles.threeWeirdos(msg);
            
            DungeonPuzzles.trivia(msg, ci);
        }

        if (Utils.isSkyblock) {
            if (SkyblockerConfig.get().messages.autoOpenMenu && msg.contains("[OPEN MENU]")) {
                List<Text> siblings = message.getSiblings();
                for (Text sibling : siblings) {
                    if (sibling.getString().contains("[OPEN MENU]")) {
                        this.client.player.sendChatMessage(sibling.getStyle().getClickEvent().getValue());
                    }
                }
            }

            if (SkyblockerConfig.get().messages.autoOpenMenu && msg.contains("[PICK UP]")) {
                List<Text> siblings = message.getSiblings();
                for (Text sibling : siblings) {
                    if (sibling.getString().contains("[PICK UP]")) {
                        this.client.player.sendChatMessage(sibling.getStyle().getClickEvent().getValue());
                    }
                }
            }
            if (msg.contains("[NPC]")) {
                if (SkyblockerConfig.get().locations.dwarvenMines.solveFetchur &&
                        msg.contains("Fetchur")) {
                    Fetchur.solve(msg, ci);
                }

                if (SkyblockerConfig.get().locations.dwarvenMines.solvePuzzler &&
                        msg.contains("Puzzler"))
                    Puzzler.solve(msg);

                if (SkyblockerConfig.get().waypoint.autoWaypoints &&
                (msg.contains("King Yolkar")))
                AutoWaypoint.autoAddWaypointWithFeedback("King", new Waypoint(client.player.getBlockPos(),new float[]{1f,0.5f,0f}));

            }
            if(SkyblockerConfig.get().general.cooldownDisplay){
                if(msg.endsWith(" is now available!")){
                    String abilityName = msg.replace(" is now available!", "");
                    CooldownDisplay.cooldowns.remove(abilityName);
                }
                else if(msg.startsWith("You used your ") && msg.endsWith(" Pickaxe Ability!")){
                    String abilityName = msg.replace("You used your ", "").replace(" Pickaxe Ability!", "");
                    Ability ability = CooldownDisplay.abilities.get(abilityName);
                    CooldownDisplay.setCooldown(abilityName, ability.cooldown);
                }
                else if(msg.startsWith("Used ") && msg.endsWith("!")){
                    String abilityName = msg.replace("Used ", "").replace("!", "");
                    Ability ability = CooldownDisplay.abilities.get(abilityName);
                    if(ability!=null)
                    CooldownDisplay.setCooldown(abilityName, ability.cooldown);
                }
            }
            if (SkyblockerConfig.get().messages.hideAbility &&
                    (msg.contains("This ability is currently on cooldown for ") ||
                    msg.contains("No more charges, next one in ") ||
                    msg.contains("This ability is on cooldown for ")))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideHeal &&
                    ((msg.contains("You healed ") &&
                    msg.contains(" health!")) || msg.contains(" healed you for ")))
                {    
                    ci.cancel();
                }


            if (SkyblockerConfig.get().messages.hideAOTE &&
                    msg.contains("There are blocks in the way!"))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideImplosion &&
                    msg.contains("Your Implosion hit "))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideMoltenWave &&
                    msg.contains("Your Molten Wave hit "))
                ci.cancel();
            
            if (SkyblockerConfig.get().messages.hideUnbreakable &&
                (msg.contains("A magical force surrounding this area prevents you from breaking blocks!") || msg.contains("You cannot mine this close to an entrance!")))
                ci.cancel();
        }
    }

    @ModifyVariable(method = "onChatMessage", at = @At("HEAD"))
    public Text onMessage(Text message) {
        Text finalText = message;
        if(SkyblockerConfig.get().messages.chatCoords){
            finalText=AutoWaypoint.chatCoords((MutableText)message);
        }
        return finalText;
    }

}
