package me.xmrvizzy.skyblocker.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.systems.RenderSystem;

import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.annotation.Nullable;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.utils.Utils;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    private float size;
    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0)
    private int getWidth(int i) {
        if(getSize()!=1.0f) return Math.round(i / this.size);
        else return i;
    }
    @Inject(at = @At("HEAD"), method = "render")
    public void scale(MatrixStack matrices, int i, Scoreboard scoreboard, @Nullable ScoreboardObjective objective, CallbackInfo ci) {
        RenderSystem.pushMatrix();
        if(getSize()!=1.0f) RenderSystem.scalef(this.size,this.size,this.size);
    }
    @Inject(at = @At("TAIL"), method = "render")
    public void unScale(MatrixStack matrices, int i, Scoreboard scoreboard, @Nullable ScoreboardObjective objective, CallbackInfo ci) {
        RenderSystem.popMatrix();
    }
    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void renderLatencyIcon(CallbackInfo ci) {
        if(Utils.isSkyblock && SkyblockerConfig.get().general.tabList.hideStatus)
        ci.cancel();
    }
    private float getSize(){
        if(Utils.isSkyblock) this.size = SkyblockerConfig.get().general.tabList.tabSize;
        else this.size = 1.0F;
        return this.size;
    }

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 0)
    private List<PlayerListEntry> injected(List<PlayerListEntry> list) {
        Utils.tabList = list;
        return list;
    }
}
