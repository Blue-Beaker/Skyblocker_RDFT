package me.xmrvizzy.skyblocker.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.CooldownDisplay;
import me.xmrvizzy.skyblocker.skyblock.CooldownDisplay.Ability;
import me.xmrvizzy.skyblocker.skyblock.dwarven.HotmLevel;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.regex.Pattern;


@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);
    @Shadow protected float zOffset;
    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    public void renderItemBar(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo ci) {

        if (Utils.isSkyblock && SkyblockerConfig.get().locations.dwarvenMines.enableDrillFuel) {
            if (!stack.isEmpty()) {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("ExtraAttributes")) {
                    if (tag.getCompound("ExtraAttributes").contains("drill_fuel") || tag.getCompound("ExtraAttributes").getString("id")=="PICKONIMBUS" || tag.getCompound("ExtraAttributes").contains("pickonimbus_durability")) {
                        float current = 1500.0F;
                        float max = 3000.0F;
                        
                        for (String line : ItemUtils.getTooltipStrings(stack)) {
                            if (line.contains("Fuel: ")) {
                                String clear = Pattern.compile("[^0-9 /]").matcher(line).replaceAll("").trim();
                                String[] split = clear.split("/");
                                current = Integer.parseInt(split[0]);
                                max = Integer.parseInt(split[1]) * 1000;
                                break;
                            }
                            else if (line.contains("after") && line.contains("uses")) {
                                String clear = Pattern.compile("[^0-9]").matcher(line).replaceAll("").trim();
                                current = Integer.parseInt(clear);
                                max = 5000;
                                break;
                            }
                        }

                        RenderSystem.disableDepthTest();
                        RenderSystem.disableTexture();
                        RenderSystem.disableAlphaTest();
                        RenderSystem.disableBlend();
                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder buffer = tessellator.getBuffer();
                        float hue = Math.max(0.0F, 1.0F - (max - current) / max);
                        int width = Math.round(current / max * 13.0F);
                        int rgb = MathHelper.hsvToRgb(hue / 3.0F, 1.0F, 1.0F);
                        this.renderGuiQuad(buffer, x + 2, y + 13, 13, 2, 0,0,0,255);
                        this.renderGuiQuad(buffer, x + 2, y + 13, width, 1, rgb >> 16 & 255, rgb >> 8 & 255, rgb & 255, 255);
                        RenderSystem.enableBlend();
                        RenderSystem.enableAlphaTest();
                        RenderSystem.enableTexture();
                        RenderSystem.enableDepthTest();
                    }
                }
            }
        }
    }

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    public void renderItemCooldown(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo ci) {
        if(Utils.isSkyblock && SkyblockerConfig.get().general.cooldownDisplay && PriceInfoTooltip.getInternalNameForItem(stack)!=null){
            float k=0.0f;
            Ability ability = CooldownDisplay.getAbilityCached(CooldownDisplay.RIGHT_CLICK, stack);
            if(ability!=null){
                k = CooldownDisplay.getCooldownProgress(ability);
            }
            else{
                ability = CooldownDisplay.getAbilityCached(CooldownDisplay.LEFT_CLICK, stack);
                if(ability!=null) k = CooldownDisplay.getCooldownProgress(ability);
            }
            if (k > 0.0F) {
               RenderSystem.disableDepthTest();
               RenderSystem.disableTexture();
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               Tessellator tessellator2 = Tessellator.getInstance();
               BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
               this.renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0F * (1.0F - k)), 16, MathHelper.ceil(16.0F * k), 255, 255, 255, 127);
               RenderSystem.enableTexture();
               RenderSystem.enableDepthTest();
            }
        }
    }

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    public void renderHotmPerkLevels(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo ci) {
        int hotmLevel = HotmLevel.getHotmPerkLevel(stack);
        if(hotmLevel>=1){
            MatrixStack matrixStack = new MatrixStack();
            String string = String.valueOf(hotmLevel);
            matrixStack.translate(0.0D, 0.0D, (double)(this.zOffset + 200.0F));
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            renderer.draw((String)string, (float)(x + 19 - 2 - renderer.getWidth(string)), (float)(y + 6 + 3), 16777215, true, matrixStack.peek().getModel(), immediate, false, 0, 15728880);
            immediate.draw();
        }
    }
}