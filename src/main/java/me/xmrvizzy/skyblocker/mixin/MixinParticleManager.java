package me.xmrvizzy.skyblocker.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry.RegistryManagerEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.locator.PointedLocator;
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
        if (SkyblockerConfig.get().items.hideParticleFrozenScythe && Utils.hasItemInHotbar.get("FROZEN_SCYTHE") && parameters.getType().equals(ParticleTypes.SPIT)){
            cir.cancel();
        }
        if (SkyblockerConfig.get().items.hideParticleDreadlord && Utils.hasItemInHotbar.get("CRYPT_DREADLORD_SWORD") && parameters.getType().equals(ParticleTypes.SMOKE)){
            cir.cancel();
        }
    }
}