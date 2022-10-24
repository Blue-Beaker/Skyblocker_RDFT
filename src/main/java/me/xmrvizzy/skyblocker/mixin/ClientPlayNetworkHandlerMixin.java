package me.xmrvizzy.skyblocker.mixin;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow private CommandDispatcher<CommandSource> commandDispatcher;

    /*@Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftClient client, Screen screen, ClientConnection connection, GameProfile profile, CallbackInfo ci) {
        commandDispatcher.register(literal("skb"));
    }

    @Inject(method = "onCommandTree", at = @At("RETURN"))
    private void onCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
        commandDispatcher.register(literal("skb"));
    }*/

}