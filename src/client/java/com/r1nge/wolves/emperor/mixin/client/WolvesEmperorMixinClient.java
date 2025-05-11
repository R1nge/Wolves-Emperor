package com.r1nge.wolves.emperor.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class WolvesEmperorMixinClient {
	@Mutable @Shadow @Final private ClientWorld.Properties clientWorldProperties;
	@Shadow private boolean shouldTickTimeOfDay;

	@Inject(at = @At("HEAD"), method = "tickTime")
	private void init(CallbackInfo info) {
        //clientWorldProperties.setTime(clientWorldProperties.getTime() + 10L);
        //if (shouldTickTimeOfDay) {
        //    clientWorldProperties.setTimeOfDay(clientWorldProperties.getTimeOfDay() + 10L);
        //}
	}
}