package me.xmrvizzy.skyblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.annotation.Nullable;
import me.xmrvizzy.skyblocker.skyblock.item.WikiLookup;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {
    protected HandledScreenMixin(Text title) {
        super(title);
    }
    @Shadow protected int backgroundWidth;
    @Shadow protected int backgroundHeight;
    @Shadow
    @Nullable protected Slot focusedSlot;
    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.focusedSlot != null){
            if (keyCode != 256 && !this.client.options.keyInventory.matchesKey(keyCode, scanCode)){
                if (WikiLookup.wikiLookup.matchesKey(keyCode, scanCode)) WikiLookup.openWiki(this.focusedSlot);
                if (WikiLookup.wikiLookupOfficial.matchesKey(keyCode, scanCode)) WikiLookup.openWiki(this.focusedSlot,true);
            }
        }
    }
}
