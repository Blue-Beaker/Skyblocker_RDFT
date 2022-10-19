package me.xmrvizzy.skyblocker.mixin;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.dungeon.DungeonPuzzles;
import me.xmrvizzy.skyblocker.skyblock.dwarven.Fetchur;
import me.xmrvizzy.skyblocker.skyblock.dwarven.Puzzler;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String msg = message.getString();
            Pattern coordsPattern = Pattern.compile("([0-9]+ [0-9]+ [0-9]+)");
            Matcher coordsMatcher = coordsPattern.matcher(msg);
            if(coordsMatcher.find()){
                String coords = coordsMatcher.group(1);
                Pattern namePattern = Pattern.compile("(temple|odawa|key guardian|divan|corleone|city|lpc|king|queen|bal|Khazad|grotto)",Pattern.CASE_INSENSITIVE);
                Matcher nameMatcher = namePattern.matcher(msg);
                MutableText addWaypointButton;
                if(nameMatcher.find()){
                    String name = nameMatcher.group(1);
                    String command = String.format("/sbwp add '%s' %s", name,coords);
                    addWaypointButton = new LiteralText(" [Add Waypoint "+name+"]").styled((style) -> {
                        return style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,command )).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(command)));
                    });
                }
                else{
                    String command = String.format("/sbwp add '%s' %s", "name",coords);
                    addWaypointButton = new LiteralText(" [Add Waypoint]").styled((style) -> {
                        return style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,command )).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(command)));
                    });
                }
                finalText = message.shallowCopy().append(addWaypointButton);
            }
        }
        return finalText;
    }

}
