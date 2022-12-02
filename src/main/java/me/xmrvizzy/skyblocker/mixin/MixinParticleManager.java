package me.xmrvizzy.skyblocker.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.item.PriceInfoTooltip;
import me.xmrvizzy.skyblocker.skyblock.locator.PointedLocator;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.Utils;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "HEAD"), cancellable = true)
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        if (SkyblockerConfig.get().locations.dwarvenMines.wishingCompassLocator && PointedLocator.compassLocator>0 && parameters.getType().equals(ParticleTypes.HAPPY_VILLAGER)){
            Vec3d particlePos = new Vec3d(x,y,z);
            PointedLocator.compassParticleList.add(particlePos);
        }
        if (SkyblockerConfig.get().locations.events.ancestorSpadeLocator && PointedLocator.spadeLocator>0 && parameters.getType().equals(ParticleTypes.DRIPPING_LAVA)){
            Vec3d particlePos = new Vec3d(x,y,z);
		    //MinecraftClient client = MinecraftClient.getInstance();
            PointedLocator.spadeParticleList.add(particlePos);
            //client.player.sendMessage(Text.of(""+x+","+y+","+z), false);
        }
        if (SkyblockerConfig.get().locations.events.ancestorSpadeLocator && PointedLocator.spadeLocator>0 && parameters.getType().equals(ParticleTypes.FIREWORK)){
            Vec3d particlePos = new Vec3d(x,y,z);
		    //MinecraftClient client = MinecraftClient.getInstance();
            PointedLocator.spadeParticleList2.add(particlePos);
            //client.player.sendMessage(Text.of(""+x+","+y+","+z), false);
        }
        if (SkyblockerConfig.get().debug.printParticles){
            MinecraftClient client = MinecraftClient.getInstance();
            client.player.sendMessage(Text.of(""+x+","+y+","+z+parameters.getType().toString()), false);
        }
        if (SkyblockerConfig.get().items.hideParticleFrozenScythe && (parameters.getType().equals(ParticleTypes.SPIT) || parameters.getType().equals(ParticleTypes.CLOUD))){
            if(SkyblockerConfig.get().items.particleHiderHotbar && Utils.hasItemInHotbar.get("FROZEN_SCYTHE") || "FROZEN_SCYTHE".equals(Utils.getHeldItemSBID()))
            cir.cancel();
        }
        if (SkyblockerConfig.get().items.hideParticleDreadlord && parameters.getType().equals(ParticleTypes.SMOKE)){
            if(SkyblockerConfig.get().items.particleHiderHotbar && Utils.hasItemInHotbar.get("CRYPT_DREADLORD_SWORD") || "CRYPT_DREADLORD_SWORD".equals(Utils.getHeldItemSBID()))
            cir.cancel();
        }
    }
}