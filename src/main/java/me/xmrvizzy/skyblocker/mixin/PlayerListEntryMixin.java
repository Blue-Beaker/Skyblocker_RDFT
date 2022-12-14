package me.xmrvizzy.skyblocker.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.xmrvizzy.skyblocker.Pinger;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
    @Shadow int latency;
    @Redirect(method = "getLatency", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/PlayerListEntry;latency:I", opcode = Opcodes.GETFIELD))
    protected int getLatency(PlayerListEntry entry){
        MinecraftClient client = MinecraftClient.getInstance();
        if(SkyblockerConfig.get().network.realPing && entry.getProfile().getId().equals(client.player.getGameProfile().getId())){
            return Pinger.instance.ping;
        }else 
        return this.latency;
    }
}
