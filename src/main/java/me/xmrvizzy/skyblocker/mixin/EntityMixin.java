package me.xmrvizzy.skyblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.utils.Utils;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "wouldPoseNotCollide(Lnet/minecraft/entity/EntityPose;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void wouldPoseNotCollide(EntityPose pose,CallbackInfoReturnable<Boolean> cir){
        if(Utils.useOldHitbox()&&SkyblockerConfig.get().hitbox.oldPlayerHitbox)
        cir.setReturnValue(true);
    }
}
