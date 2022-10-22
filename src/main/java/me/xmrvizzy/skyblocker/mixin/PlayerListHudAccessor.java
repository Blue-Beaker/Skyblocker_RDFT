package me.xmrvizzy.skyblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;

@Mixin(PlayerListHud.class)
public interface PlayerListHudAccessor {
    @Accessor("header")
    Text getHeader();
    @Accessor("footer")
    Text getFooter();
}
