package me.xmrvizzy.skyblocker.skyblock;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
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

import me.xmrvizzy.skyblocker.skyblock.CooldownDisplay.Ability;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.skyblock.waypoints.Waypoint;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointList;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.Utils;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import java.util.HashMap;


public class skyblockerCLI {
	MinecraftClient client = MinecraftClient.getInstance();
    public skyblockerCLI(CommandDispatcher<FabricClientCommandSource> dispatcher){
        LiteralCommandNode<FabricClientCommandSource> waypointCommand = dispatcher.register(literal("sbwp")
                .then(literal("add")
                    .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .then(ClientCommandManager.argument("X", IntegerArgumentType.integer())
                        .then(ClientCommandManager.argument("Y", IntegerArgumentType.integer())
                        .then(ClientCommandManager.argument("Z", IntegerArgumentType.integer())
                        .executes(context -> {
                            return addWaypoint(context,StringArgumentType.getString(context, "name"), new BlockPos(IntegerArgumentType.getInteger(context, "X"),IntegerArgumentType.getInteger(context, "Y"),IntegerArgumentType.getInteger(context, "Z")));
                        })
                        )))
                        .executes(context -> {
                            return addWaypoint(context,StringArgumentType.getString(context, "name"));
                        })
                    )
                    .executes(context -> {
                        context.getSource().sendFeedback(new LiteralText("Usage:  /sbwp add (WaypointName) [Coords: X Y Z]").formatted(Formatting.RED));
                        return 0;
                    })
                )
                .then(literal("remove")
                    .then(ClientCommandManager.argument("name",StringArgumentType.string())
                        .executes(context -> {
                            return removeWaypoint(context, StringArgumentType.getString(context, "name"));
                        })
                    )
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText("Usage:  /sbwp remove (WaypointName)").formatted(Formatting.RED));
                    return 0;
                })
                )
                .then(literal("rename")
                    .then(ClientCommandManager.argument("name1",StringArgumentType.string())
                    .then(ClientCommandManager.argument("name2",StringArgumentType.string())
                        .executes(context -> {
                            return renameWaypoint(context, StringArgumentType.getString(context, "name1"), StringArgumentType.getString(context, "name2"));
                        })
                    ))
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText("Usage:  /sbwp rename (Waypoint) (NewName)").formatted(Formatting.RED));
                    return 0;
                })
                )
                .then(literal("list")
                    .then(ClientCommandManager.argument("area",StringArgumentType.string())
                        .executes(context -> {
                            listWaypoint(context, StringArgumentType.getString(context, "area"));
                            return 1;
                        })
                    )
                    .executes(context -> {
                        listWaypoint(context);
                        return 1;
                    })
                )
                .then(literal("clear")
                    .then(ClientCommandManager.argument("area",StringArgumentType.string())
                        .executes(context -> {
                            clearWaypoint(context, StringArgumentType.getString(context, "area"));
                            return 1;
                        })
                    )
                    .executes(context -> {
                        clearWaypoint(context);
                        return 1;
                    })
                )
                .then(literal("color")
                    .then(ClientCommandManager.argument("name", StringArgumentType.string())
                    .then(ClientCommandManager.argument("R", FloatArgumentType.floatArg(0.0f,1.0f))
                    .then(ClientCommandManager.argument("G", FloatArgumentType.floatArg(0.0f,1.0f))
                    .then(ClientCommandManager.argument("B", FloatArgumentType.floatArg(0.0f,1.0f))
                    .executes(context -> {
                        return setColor(context,StringArgumentType.getString(context, "name"), new float[]{FloatArgumentType.getFloat(context, "R"),FloatArgumentType.getFloat(context, "G"),FloatArgumentType.getFloat(context, "B")});
                    })
                    )))
                    )
                    .executes(context -> {
                        context.getSource().sendFeedback(new LiteralText("Usage: /sbwp color (WaypointName) [Color: R G B]").formatted(Formatting.RED));
                        return 0;
                    })
                )
                .then(literal("listall")
                    .executes(context -> {
                        listAll(context);
                        return 1;
                    })
                )
                .then(literal("listareas")
                    .executes(context -> {
                        listAreas(context);
                        return 1;
                    })
                )
        );
        dispatcher.register(ClientCommandManager.literal("waypoint").redirect(waypointCommand));
        dispatcher.register(ClientCommandManager.literal("skyblockerwaypoint").redirect(waypointCommand));
        
        LiteralCommandNode<FabricClientCommandSource> sbrDebug = dispatcher.register(literal("sbrd")
            .then(literal("getTabInfo")
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText(Utils.getTabInfo().toString()));
                    return 1;
                })
            )
            .then(literal("getInternalName")
            .executes(context -> {
                context.getSource().sendFeedback(new LiteralText(PriceInfoTooltip.getInternalNameForItem(client.player.getMainHandStack())));
                return 1;
            }))
            .then(literal("getServerArea")
            .executes(context -> {
                context.getSource().sendFeedback(new LiteralText(Utils.serverArea));
                return 1;
            }))
            .then(literal("getHeldItemTooltipBlocks")
            .executes(context -> {
                context.getSource().sendFeedback(new LiteralText(ItemUtils.getTooltipStringsBlocks(context.getSource().getPlayer().getMainHandStack()).toString()));
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
        );
        dispatcher.register(ClientCommandManager.literal("skyblockerdebug").redirect(sbrDebug));
    }
    public int addWaypoint(CommandContext<FabricClientCommandSource> context, String name, BlockPos pos, float[] color){
        try{
            Waypoint waypoint = new Waypoint(pos,color);
            if(WaypointList.add(Utils.serverArea, name, waypoint)){
                context.getSource().sendFeedback(new LiteralText(String.format("Added waypoint \'%s\' at %d,%d,%d ",name, pos.getX(),pos.getY(),pos.getZ())).formatted(Formatting.GREEN)
                .append(new LiteralText("[VIEW]").styled((style) -> {
                    return style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sbwp list")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("/sbwp list")));
                })));
                return 1;
            }
            else{
                context.getSource().sendError(new LiteralText(String.format("The waypoint with name \'%s\' already exists, please use another name instead",name)).formatted(Formatting.YELLOW));
                return 0;
            }
        }
        catch(Exception e){
            context.getSource().sendError(new LiteralText(String.format("Failed to add waypoint: %s",e.getLocalizedMessage())).formatted(Formatting.RED));
            return 0;
        }
    }

    public int addWaypoint(CommandContext<FabricClientCommandSource> context, String name, BlockPos pos){
        return addWaypoint(context, name, pos, new float[]{1.0f,1.0f,1.0f});
    }

    public int addWaypoint(CommandContext<FabricClientCommandSource> context, String name, float[] color){
        return addWaypoint(context, name, new BlockPos(context.getSource().getPosition()), color);
    }
    public int addWaypoint(CommandContext<FabricClientCommandSource> context, String name){
        return addWaypoint(context, name, new BlockPos(context.getSource().getPosition()), new float[]{1.0f,1.0f,1.0f});
    }
    public int removeWaypoint(CommandContext<FabricClientCommandSource> context, String name){
        if(WaypointList.remove(name)){
            context.getSource().sendFeedback(new LiteralText(String.format("Removed waypoint \'%s\'",name)).formatted(Formatting.GREEN));
            return(1);
        }
        else{
            context.getSource().sendFeedback(new LiteralText(String.format("Waypoint \'%s\' doesn't exist here",name)).formatted(Formatting.RED));
            return(0);
        }
    }
    public int renameWaypoint(CommandContext<FabricClientCommandSource> context, String name1, String name2){
        if(WaypointList.rename(name1,name2)){
            context.getSource().sendFeedback(new LiteralText(String.format("Renamed waypoint \'%s\' to \'%s\'",name1,name2)).formatted(Formatting.GREEN));
            return(1);
        }
        else{
            context.getSource().sendFeedback(new LiteralText("Failed to rename").formatted(Formatting.RED));
            return(0);
        }
    }
    public void listWaypoint(CommandContext<FabricClientCommandSource> context, String area){
        try{
        HashMap<String,Waypoint> list = WaypointList.get(area);
        if(list!=null){
            context.getSource().sendFeedback(new LiteralText(String.format("======[Skyblocker Waypoints in %s]======",area)).formatted(Formatting.GREEN));
            for(String name : list.keySet()){
                BlockPos pos = list.get(name).blockPos;
                float[] color = list.get(name).color;
                context.getSource().sendFeedback(new LiteralText(String.format("%s at (%d,%d,%d) ",name,pos.getX(),pos.getY(),pos.getZ())).formatted(Formatting.GREEN)
                .append(new LiteralText("[COLOR]").styled((style) -> {
                    return style.withColor(Formatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/sbwp color '%s' %.2f %.2f %.2f", name,color[0],color[1],color[2]))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("/sbwp color '"+name+"' (R G B)")));
                }))
                .append(new LiteralText("[SHARE]").styled((style) -> {
                    return style.withColor(Formatting.AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("%s %d %d %d", name,pos.getX(),pos.getY(),pos.getZ()))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Share this in the chat!")));
                }))
                .append(new LiteralText("[REMOVE]").styled((style) -> {
                    return style.withColor(Formatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sbwp remove '"+name+"'")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("/sbwp remove '"+name+"'")));
                })));
            }
            context.getSource().sendFeedback(new LiteralText("======[Skyblocker Stored Locations END]======").formatted(Formatting.GREEN));
        }
        else{
            context.getSource().sendError(new LiteralText(String.format("There are no waypoints in %s",area)).formatted(Formatting.GREEN));
        }
        }
        catch(Exception e){
            context.getSource().sendError(new LiteralText(String.format("Failed: %s",e.getLocalizedMessage())).formatted(Formatting.RED));
        }
    }
    public void listWaypoint(CommandContext<FabricClientCommandSource> context){
        listWaypoint(context, Utils.serverArea);
    }
    public void listAll(CommandContext<FabricClientCommandSource> context){
        for(String area: WaypointList.getAreas()){
            listWaypoint(context,area);
        }
    }
    public void listAreas(CommandContext<FabricClientCommandSource> context){
        context.getSource().sendFeedback(new LiteralText(WaypointList.getAreas().toString()));
    }
    public void clearWaypoint(CommandContext<FabricClientCommandSource> context, String area){
        WaypointList.clear(area);
        context.getSource().sendFeedback(new LiteralText(String.format("Removed all waypoints in %s", area)));
    }
    public void clearWaypoint(CommandContext<FabricClientCommandSource> context){
        clearWaypoint(context,Utils.serverArea);
    }
    public int setColor(CommandContext<FabricClientCommandSource> context,String name, float[] color){
        if(!WaypointList.setColor(name, color)){
            context.getSource().sendFeedback(new LiteralText(String.format("Waypoint \'%s\' doesn't exist here",name)).formatted(Formatting.RED));
            return(0);
        }
        else{
            return(1);
        }
    }

}
