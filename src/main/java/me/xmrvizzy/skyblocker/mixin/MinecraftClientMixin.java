package me.xmrvizzy.skyblocker.mixin;

import me.xmrvizzy.skyblocker.SkyblockerMod;
import me.xmrvizzy.skyblocker.skyblock.HotbarSlotLock;
import me.xmrvizzy.skyblocker.skyblock.solver.ContainerScreenSolverManager;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        SkyblockerMod.onTick();
    }

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    public void handleInputEvents(CallbackInfo ci) {
        if (Utils.isSkyblock) HotbarSlotLock.handleInputEvents(player);
    }
}