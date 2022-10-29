package me.xmrvizzy.skyblocker.skyblock.commands;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import me.xmrvizzy.skyblocker.skyblock.waypoints.WaypointList;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;

public class WaypointAreaArgumentType implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        return string;
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(WaypointList.getAreas(), builder);
    }
    public static WaypointAreaArgumentType string(){
        return new WaypointAreaArgumentType();
    }
    public static String getString(CommandContext<FabricClientCommandSource> context, String name){
        return (String)context.getArgument(name, String.class);
    }
}
