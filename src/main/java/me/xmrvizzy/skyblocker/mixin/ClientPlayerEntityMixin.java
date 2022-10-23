package me.xmrvizzy.skyblocker.mixin;

import com.mojang.authlib.GameProfile;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.HotbarSlotLock;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    /*@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String message, CallbackInfo ci) {
        String[] split = message.toLowerCase().split(" ");
        if (split.length > 0 && split[0].contentEquals("/skb")) {
            ci.cancel();
        }
    }*/

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    public void dropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
        if (Utils.isSkyblock) HotbarSlotLock.handleDropSelectedItem(this.inventory.selectedSlot, cir);
    }

    @Shadow
    private boolean inSneakingPose;
    @Redirect(method = "tickMovement()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;inSneakingPose:Z", opcode = Opcodes.PUTFIELD))
    private void injected(ClientPlayerEntity clientPlayerEntity,boolean sneaking) {
        if(Utils.useOldHitbox() && SkyblockerConfig.get().hitbox.oldPlayerHitbox){
            this.inSneakingPose = !this.abilities.flying && !this.isSwimming() && (this.isSneaking());
        }
        else{
            this.inSneakingPose = sneaking;
        }
    }
}