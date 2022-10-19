package me.xmrvizzy.skyblocker.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.locator.PointedLocator;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "RETURN"), cancellable = true)
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        if (SkyblockerConfig.get().locations.dwarvenMines.wishingCompassLocator && PointedLocator.compassLocator>0 && parameters.getType().equals(ParticleTypes.HAPPY_VILLAGER)){
            Vec3d particlePos = new Vec3d(x,y,z);
		    //MinecraftClient client = MinecraftClient.getInstance();
            PointedLocator.compassParticleList.add(particlePos);
            //client.player.sendMessage(Text.of(""+x+","+y+","+z), false);
        }
        if (SkyblockerConfig.get().locations.events.ancestorSpadeLocator && PointedLocator.spadeLocator>0 && parameters.getType().equals(ParticleTypes.DRIPPING_LAVA)){
            Vec3d particlePos = new Vec3d(x,y,z);
		    //MinecraftClient client = MinecraftClient.getInstance();
            PointedLocator.spadeParticleList.add(particlePos);
            //client.player.sendMessage(Text.of(""+x+","+y+","+z), false);
        }
    }
}