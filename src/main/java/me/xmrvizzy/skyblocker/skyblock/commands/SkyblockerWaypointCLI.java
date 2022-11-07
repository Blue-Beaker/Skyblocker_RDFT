package me.xmrvizzy.skyblocker.skyblock.commands;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import com.ibm.icu.text.SimpleDateFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.waypoints.Waypoint;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointList;
import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointStorage;
import me.xmrvizzy.skyblocker.utils.StringUtils;
import me.xmrvizzy.skyblocker.utils.Utils;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import java.sql.Date;
import java.util.HashMap;


public class SkyblockerWaypointCLI {
	MinecraftClient client = MinecraftClient.getInstance();
    public SkyblockerWaypointCLI(CommandDispatcher<FabricClientCommandSource> dispatcher){
        LiteralCommandNode<FabricClientCommandSource> waypointCommand = dispatcher.register(literal("sbwp")
                .then(literal("add")
                    .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .then(ClientCommandManager.argument("X", IntegerArgumentType.integer())
                        .then(ClientCommandManager.argument("Y", IntegerArgumentType.integer())
                        .then(ClientCommandManager.argument("Z", IntegerArgumentType.integer())
                        .executes(context -> {
                            return addWaypoint(context,StringArgumentType.getString(context, "name"));
                        })
                        )))
                        .executes(context -> {
                            return addWaypoint(context,StringArgumentType.getString(context, "name"));
                        })
                    )
                    .executes(context -> {
                        context.getSource().sendFeedback(new LiteralText("Usage:  /sbwp [area (area)] add (WaypointName) [Coords: X Y Z]").formatted(Formatting.RED));
                        return 0;
                    })
                )
                .then(literal("remove")
                    .then(ClientCommandManager.argument("name",WaypointNameArgumentType.string())
                        .executes(context -> {
                            return removeWaypoint(context, WaypointNameArgumentType.getString(context, "name"));
                        })
                    )
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText("Usage:  /sbwp [area (area)] remove (WaypointName)").formatted(Formatting.RED));
                    return 0;
                })
                )
                .then(literal("rename")
                    .then(ClientCommandManager.argument("name1",WaypointNameArgumentType.string())
                    .then(ClientCommandManager.argument("name2",StringArgumentType.string())
                        .executes(context -> {
                            return renameWaypoint(context, WaypointNameArgumentType.getString(context, "name1"), StringArgumentType.getString(context, "name2"));
                        })
                    ))
                .executes(context -> {
                    context.getSource().sendFeedback(new LiteralText("Usage:  /sbwp [area (area)] rename (Waypoint) (NewName)").formatted(Formatting.RED));
                    return 0;
                })
                )
                .then(literal("color")
                    .then(ClientCommandManager.argument("name", WaypointNameArgumentType.string())
                    .then(ClientCommandManager.argument("R", FloatArgumentType.floatArg(0.0f,1.0f))
                    .then(ClientCommandManager.argument("G", FloatArgumentType.floatArg(0.0f,1.0f))
                    .then(ClientCommandManager.argument("B", FloatArgumentType.floatArg(0.0f,1.0f))
                    .executes(context -> {
                        return setColor(context,WaypointNameArgumentType.getString(context, "name"), new float[]{FloatArgumentType.getFloat(context, "R"),FloatArgumentType.getFloat(context, "G"),FloatArgumentType.getFloat(context, "B")});
                    })
                    )))
                    )
                    .executes(context -> {
                        context.getSource().sendFeedback(new LiteralText("Usage: /sbwp [area (area)] color (WaypointName) [Color: R G B]").formatted(Formatting.RED));
                        return 0;
                    })
                )
                .then(literal("list")
                    .then(ClientCommandManager.argument("area",WaypointAreaArgumentType.string())
                        .executes(context -> {
                            listWaypoint(context, WaypointAreaArgumentType.getString(context, "area"));
                            return 1;
                        })
                    )
                    .executes(context -> {
                        listWaypoint(context);
                        return 1;
                    })
                )
                .then(literal("clear")
                    .then(ClientCommandManager.argument("area",WaypointAreaArgumentType.string())
                        .executes(context -> {
                            clearWaypoint(context, WaypointAreaArgumentType.getString(context, "area"));
                            return 1;
                        })
                    )
                    .executes(context -> {
                        clearWaypoint(context);
                        return 1;
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
                .then(literal("reload")
                    .executes(context -> {
                        return reload(context);
                    })
                )
                .then(literal("removeClosedLobby")
                    .executes(context -> {
                        return removeClosedLobby(context);
                    })
                )
                .then(literal("area")
                    .then(ClientCommandManager.argument("area",WaypointAreaArgumentType.string())
                    .then(literal("add")
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                            .then(ClientCommandManager.argument("X", IntegerArgumentType.integer())
                            .then(ClientCommandManager.argument("Y", IntegerArgumentType.integer())
                            .then(ClientCommandManager.argument("Z", IntegerArgumentType.integer())
                            .executes(context -> {
                                return addWaypoint(context,StringArgumentType.getString(context, "name"));
                            })
                            )))
                            .executes(context -> {
                                return addWaypoint(context,StringArgumentType.getString(context, "name"));
                            })
                        )
                        .executes(context -> {
                            context.getSource().sendFeedback(new LiteralText("Usage:  /sbwp [area (area)] add (WaypointName) [Coords: X Y Z]").formatted(Formatting.RED));
                            return 0;
                        })
                    )
                    .then(literal("remove")
                        .then(ClientCommandManager.argument("name",WaypointNameArgumentType.string())
                            .executes(context -> {
                                return removeWaypoint(context, WaypointNameArgumentType.getString(context, "name"));
                            })
                        )
                    .executes(context -> {
                        context.getSource().sendFeedback(new LiteralText("Usage:  /sbwp [area (area)] remove (WaypointName)").formatted(Formatting.RED));
                        return 0;
                    })
                    )
                    .then(literal("rename")
                        .then(ClientCommandManager.argument("name1",WaypointNameArgumentType.string())
                        .then(ClientCommandManager.argument("name2",StringArgumentType.string())
                            .executes(context -> {
                                return renameWaypoint(context, WaypointNameArgumentType.getString(context, "name1"), StringArgumentType.getString(context, "name2"));
                            })
                        ))
                    .executes(context -> {
                        context.getSource().sendFeedback(new LiteralText("Usage:  /sbwp [area (area)] rename (Waypoint) (NewName)").formatted(Formatting.RED));
                        return 0;
                    })
                    )
                    .then(literal("color")
                        .then(ClientCommandManager.argument("name", WaypointNameArgumentType.string())
                        .then(ClientCommandManager.argument("R", FloatArgumentType.floatArg(0.0f,1.0f))
                        .then(ClientCommandManager.argument("G", FloatArgumentType.floatArg(0.0f,1.0f))
                        .then(ClientCommandManager.argument("B", FloatArgumentType.floatArg(0.0f,1.0f))
                        .executes(context -> {
                            return setColor(context,WaypointNameArgumentType.getString(context, "name"), new float[]{FloatArgumentType.getFloat(context, "R"),FloatArgumentType.getFloat(context, "G"),FloatArgumentType.getFloat(context, "B")});
                        })
                        )))
                        )
                        .executes(context -> {
                            context.getSource().sendFeedback(new LiteralText("Usage: /sbwp [area (area)] color (WaypointName) [Color: R G B]").formatted(Formatting.RED));
                            return 0;
                        })
                    )
                )
            )
        );
        dispatcher.register(ClientCommandManager.literal("waypoint").redirect(waypointCommand));
        dispatcher.register(ClientCommandManager.literal("skyblockerwaypoint").redirect(waypointCommand));
    }
    public int addWaypoint(CommandContext<FabricClientCommandSource> context, String name){
        try{
            String area = getAreaFromContext(context);
            BlockPos pos = getCoordsFromContext(context);
            float[] color = getColorFromContext(context);
            Waypoint waypoint = new Waypoint(pos,color);
            if(WaypointList.add(area, name, waypoint)){
                context.getSource().sendFeedback(new LiteralText(String.format("Added waypoint \'%s\' at %d,%d,%d ",name, pos.getX(),pos.getY(),pos.getZ())).formatted(Formatting.GREEN)
                .append(new LiteralText("[VIEW]").styled((style) -> {
                    return style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sbwp list")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("/sbwp list")));
                })));
                if(area.startsWith("CH_") && SkyblockerConfig.get().waypoint.crystalHollowsWarning){
                    context.getSource().sendFeedback(new LiteralText("Waypoints in CrystalHollows are separated by lobbies. However you can create static waypoints by using ").formatted(Formatting.AQUA)
                    .append(new LiteralText("/sbwp area CrystalHollows add (name) [X Y Z]").formatted(Formatting.GOLD).append("\nThis message only shows once unless you re-enable it in config.")));
                    SkyblockerConfig.get().waypoint.crystalHollowsWarning=false;
                }
                return 1;
            }
            else{
                context.getSource().sendError(new LiteralText(String.format("The waypoint with name \'%s\' already exists in \'%s\', please use another name instead",name,area)).formatted(Formatting.YELLOW));
                return 0;
            }
        }
        catch(Exception e){
            context.getSource().sendError(new LiteralText(String.format("Failed to add waypoint: %s",e.getLocalizedMessage())).formatted(Formatting.RED));
            return 0;
        }
    }

    public int removeWaypoint(CommandContext<FabricClientCommandSource> context, String name){
        String area = getAreaFromContext(context);
        if(WaypointList.remove(area,name)){
            context.getSource().sendFeedback(new LiteralText(String.format("Removed waypoint \'%s\'",name)).formatted(Formatting.GREEN));
            return(1);
        }
        else{
            context.getSource().sendFeedback(new LiteralText(String.format("Waypoint \'%s\' doesn't exist in \'%s\'",name,area)).formatted(Formatting.RED));
            return(0);
        }
    }
    public int renameWaypoint(CommandContext<FabricClientCommandSource> context, String name1, String name2){
        if(WaypointList.rename(getAreaFromContext(context),name1,name2)){
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
            if(area.startsWith("CH_") && WaypointList.crystalHollowsTime.containsKey(area)){
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date(WaypointList.crystalHollowsTime.get(area)*1000));
                context.getSource().sendFeedback(new LiteralText(String.format("%s closing at %s",area,time)).formatted(Formatting.AQUA));
            }
            context.getSource().sendFeedback(new LiteralText(String.format("======[Skyblocker Waypoints in %s]======",area)).formatted(Formatting.GREEN));

            for(String name : list.keySet()){
                BlockPos pos = list.get(name).getBlockPos();
                float[] color = list.get(name).color;
                context.getSource().sendFeedback(new LiteralText(String.format("%s at (%d,%d,%d) ",name,pos.getX(),pos.getY(),pos.getZ())).formatted(Formatting.GREEN)
                .append(new LiteralText("[COLOR]").styled((style) -> {
                    return style.withColor(Formatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/sbwp area %s color %s %.2f %.2f %.2f",StringUtils.addQuotesIfNeeded(area),StringUtils.addQuotesIfNeeded(name),color[0],color[1],color[2]))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("/sbwp color '"+name+"' (R G B)")));
                }))
                .append(new LiteralText("[SHARE]").styled((style) -> {
                    return style.withColor(Formatting.AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("%s %d %d %d", name,pos.getX(),pos.getY(),pos.getZ()))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Share this in the chat!")));
                }))
                .append(new LiteralText("[REMOVE]").styled((style) -> {
                    return style.withColor(Formatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/sbwp area %s remove %s",StringUtils.addQuotesIfNeeded(area),StringUtils.addQuotesIfNeeded(name)))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("/sbwp remove '"+name+"'")));
                })));
            }
            context.getSource().sendFeedback(new LiteralText("=========================").formatted(Formatting.GREEN));
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
        if("CrystalHollows".equals(Utils.serverArea)){
            listWaypoint(context, Utils.getLobbyAutoCH());
        }
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
        if("CrystalHollows".equals(Utils.serverArea))
        clearWaypoint(context,Utils.getLobbyAutoCH());
        else
        clearWaypoint(context,Utils.serverArea);
    }
    public int setColor(CommandContext<FabricClientCommandSource> context,String name, float[] color){
        String area = getAreaFromContext(context);
        if(!WaypointList.setColor(area,name, color)){
            context.getSource().sendFeedback(new LiteralText(String.format("Waypoint \'%s\' doesn't exist in \'%s\'",name,area)).formatted(Formatting.RED));
            return(0);
        }
        else{
            return(1);
        }
    }
    public int reload(CommandContext<FabricClientCommandSource> context){
        try{
            WaypointStorage.reloadJsonFile();
            context.getSource().sendFeedback(new LiteralText("Reloaded waypoints!").formatted(Formatting.GREEN));
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(new LiteralText(e.getStackTrace().toString()).formatted(Formatting.RED));
            e.printStackTrace();
            return 0;
        }
    }
    public String getAreaFromContext(CommandContext<FabricClientCommandSource> context){
        try{
            return WaypointAreaArgumentType.getString(context, "area");
        }
        catch(Exception e){
            if("CrystalHollows".equals(Utils.serverArea)) return Utils.getLobbyAutoCH();
            return Utils.serverArea;
        }
    }
    public BlockPos getCoordsFromContext(CommandContext<FabricClientCommandSource> context){
        try{
            return new BlockPos(IntegerArgumentType.getInteger(context, "X"),IntegerArgumentType.getInteger(context, "Y"),IntegerArgumentType.getInteger(context, "Z"));
        }
        catch(Exception e){
            return client.player.getBlockPos();
        }
    }
    public float[] getColorFromContext(CommandContext<FabricClientCommandSource> context){
        try{
            return new float[]{IntegerArgumentType.getInteger(context, "R"),IntegerArgumentType.getInteger(context, "G"),IntegerArgumentType.getInteger(context, "B")};
        }
        catch(Exception e){
            return new float[]{1f,1f,1f};
        }
    }
    public int removeClosedLobby(CommandContext<FabricClientCommandSource> context){
        context.getSource().sendFeedback(new LiteralText("Removed closed lobbies:"+WaypointList.removeClosedLobby().toString()).formatted(Formatting.GREEN));
        return 1;
    }

}
