package me.xmrvizzy.skyblocker.mixin;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @ModifyVariable(method = "getDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;", at = @At("HEAD"))
    private EntityPose oldHitbox(EntityPose pose) {
        if(Utils.useOldHitbox() && SkyblockerConfig.get().hitbox.oldPlayerHitbox){
            if(pose==EntityPose.CROUCHING||pose==EntityPose.SWIMMING){
                return EntityPose.STANDING;
            }
        }
        return pose;
    }

    @Inject(method = "getActiveEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntityDimensions;)F", at = @At(value = "HEAD"), cancellable = true)
    private void getActiveEyeHeight(EntityPose pose,EntityDimensions entityDimensions,CallbackInfoReturnable<Float> cir){
        if(Utils.useOldHitbox()&&SkyblockerConfig.get().hitbox.oldSneakingEyeHeight&&pose==EntityPose.CROUCHING)
        cir.setReturnValue(1.495f);
        else if(Utils.useOldHitbox()&&SkyblockerConfig.get().hitbox.oldSneakingEyeHeight&&pose==EntityPose.SWIMMING)
        cir.setReturnValue(1.62f);
    }
}
