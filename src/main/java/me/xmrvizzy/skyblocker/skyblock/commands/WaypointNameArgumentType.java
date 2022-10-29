package me.xmrvizzy.skyblocker.skyblock.commands;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointList;
import me.xmrvizzy.skyblocker.utils.StringUtils;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;

public class WaypointNameArgumentType implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readString();
        return string;
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String area;
        try {
            area=context.getArgument("area", String.class);
        } catch (Exception e) {
            area=Utils.serverArea;
        }
        try{
            return CommandSource.suggestMatching(StringUtils.addQuotesIfNeeded(WaypointList.get(area).keySet()), builder);
        }
        catch(Exception e){
            return CommandSource.suggestMatching(new String[]{"INVALID AREA"}, builder);
        }
    }
    public static WaypointNameArgumentType string(){
        return new WaypointNameArgumentType();
    }
    public static String getString(CommandContext<FabricClientCommandSource> context, String name){
        return (String)context.getArgument(name, String.class);
    }
}
