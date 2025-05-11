package com.r1nge.wolves.emperor.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WolvesEmperorMixin {

	@Shadow @Mutable private MutableWorldProperties properties;

	@Inject(at = @At("HEAD"), method = "getTimeOfDay")
	private void init(CallbackInfoReturnable<Long> cir) {
		// This code is injected into the start of MinecraftServer.loadWorld()V
		properties.getTimeOfDay();
	}
}