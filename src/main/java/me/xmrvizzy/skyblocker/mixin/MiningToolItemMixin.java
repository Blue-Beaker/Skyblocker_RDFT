package me.xmrvizzy.skyblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.item.HoeItem;
import net.minecraft.item.MiningToolItem;

@Mixin(MiningToolItem.class)
public abstract class MiningToolItemMixin {
    @Inject(method = "getMiningSpeedMultiplier(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;)F", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<Float> cir) {
        if(Utils.isSkyblock && SkyblockerConfig.get().hitbox.removeHoeEfficency && ((MiningToolItem)(Object)this) instanceof HoeItem)
        cir.setReturnValue(1.0F);
    }
}
