package me.xmrvizzy.skyblocker.skyblock.commands;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import me.xmrvizzy.skyblocker.skyblock.CooldownDisplay;
import me.xmrvizzy.skyblocker.skyblock.CooldownDisplay.Ability;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.skyblock.solver.ContainerScreenSolverManager;
import me.xmrvizzy.skyblocker.skyblock.waypoints.Waypoint;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointList;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointStorage;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.Utils;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class SkyblockerDebugCLI {
	MinecraftClient client = MinecraftClient.getInstance();
    public SkyblockerDebugCLI(CommandDispatcher<FabricClientCommandSource> dispatcher){

        LiteralCommandNode<FabricClientCommandSource> sbrDebug = dispatcher.register(literal("sbrd")
            .then(literal("item")
                .then(literal("getInternalName")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(PriceInfoTooltip.getInternalNameForItem(client.player.getMainHandStack())));
                    return 1;
                }))
                .then(literal("getHeldItemTooltipBlocks")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(ItemUtils.getTooltipStringsBlocks(context.getSource().getPlayer().getMainHandStack()).toString()));
                    return 1;
                }))
                .then(literal("getHeldItemTooltipBlocksWithoutName")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(ItemUtils.getTooltipStringsBlocks(context.getSource().getPlayer().getMainHandStack(),false).toString()));
                    return 1;
                }))
                .then(literal("getHeldItemAbilities")
                .executes(context -> {
                    Ability ability1=CooldownDisplay.getAbility(0, context.getSource().getPlayer().getMainHandStack());
                    Ability ability2=CooldownDisplay.getAbility(1, context.getSource().getPlayer().getMainHandStack());
                    if(ability1!=null)
                    context.getSource().sendFeedback(new LiteralText(ability1.toString()));
                    if(ability2!=null)
                    context.getSource().sendFeedback(new LiteralText(ability2.toString()));
                    return 1;
                }))
                .then(literal("getCooldowns")
                .executes(context -> {
                    for(String ability:CooldownDisplay.cooldowns.keySet())
                    context.getSource().sendFeedback(new LiteralText(ability.toString()+" "+CooldownDisplay.cooldowns.get(ability)));
                    return 1;
                }))
                .then(literal("getId")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(ItemUtils.getId(context.getSource().getPlayer().getMainHandStack())));
                    return 1;
                }))
                )
            .then(literal("server")
                .then(literal("getTabInfo")
                    .executes(context -> {
                        context.getSource().sendFeedback(new LiteralText(Utils.getTabInfo().toString()));
                        return 1;
                    })
                )
                .then(literal("getServerArea")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(Utils.serverArea));
                    return 1;
                }))
                .then(literal("getSidebar")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(Utils.getSidebar().toString()));
                    return 1;
                }))
                .then(literal("getSublocation")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(Utils.subLocation));
                    return 1;
                }))
                .then(literal("getHeader")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(Utils.getTabHeader()));
                    return 1;
                }))
                .then(literal("getFooter")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(Utils.getTabFooter()));
                    return 1;
                }))
                .then(literal("getCrystalHollowsCloseTime")
                .executes(context -> {
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    context.getSource().sendFeedback(new LiteralText(sdf.format(new Date(Utils.getCrystalHollowsCloseTime()*1000))));
                    return 1;
                }))
            )
            .then(literal("waypoint")
                .then(literal("printJson")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(WaypointStorage.toJsonString()));
                    return 1;
                }))
                .then(literal("storeJson")
                .executes(context -> {
                    if(WaypointStorage.storeJsonFile()){
                        context.getSource().sendFeedback(new LiteralText("Stored to skyblocker_waypoints.json"));
                        return 1;
                    }
                    else{
                        context.getSource().sendError(new LiteralText("Failed to store").formatted(Formatting.RED));
                        return 0;
                    }
                }))
                .then(literal("read")
                .then(ClientCommandManager.argument("jsondata",StringArgumentType.greedyString())
                .executes(context -> {
                    if(WaypointStorage.fromJsonString(StringArgumentType.getString(context, "jsondata"))){
                        context.getSource().sendFeedback(new LiteralText("Stored Waypoints"));
                        return 1;
                    }
                    else{
                        context.getSource().sendError(new LiteralText("Failed to store").formatted(Formatting.RED));
                        return 0;
                    }
                })))
                .then(literal("crystalHollowTimes")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(WaypointList.crystalHollowsTime.toString()));
                    return 1;
                }))
            )
            .then(literal("screen")
            .then(literal("containerName")
            .executes(context -> {
                context.getSource().sendFeedback(new LiteralText(ContainerScreenSolverManager.currentContainer));
                return 1;
            }))
            )
        );
        dispatcher.register(ClientCommandManager.literal("skyblockerdebug").redirect(sbrDebug));
    }
}
