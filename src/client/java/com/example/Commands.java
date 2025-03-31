package com.example;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Commands {
    private double maxDistance = 50;

    public void Initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("attack")
                .executes(context -> {
                            var localClient = MinecraftClient.getInstance();
                            var world = localClient.world;
                            var camera = localClient.cameraEntity;
                            var tickProgress = localClient.getRenderTickCounter().getTickProgress(true);
                            HitResult raycast = raycast(camera, maxDistance, maxDistance, tickProgress);
                            switch (raycast.getType()) {
                                case MISS -> {
                                    sendMessage(context, "Hit nothing");
                                }
                                case BLOCK -> {
                                    sendMessage(context, "Hit block");
                                }
                                case ENTITY -> {
                                    sendMessage(context, "Hit Entity");

                                    var tamedWolves = getTamedWolves(localClient.player);
                                    var target = ((EntityHitResult) raycast).getEntity();
                                    var targetLiving = ((LivingEntity) target);
                                    var server = MinecraftClient.getInstance().getServer();
                                    for (WolfEntity wolf : tamedWolves) {
                                        wolf.setTarget(targetLiving);
                                        wolf.setAttacking(true);

                                        server.execute(() -> {
                                            RegistryKey<World> worldKey = wolf.getWorld().getRegistryKey();
                                            ServerWorld serverWorld = server.getWorld(worldKey);
                                            wolf.tryAttack(serverWorld, targetLiving);
                                        });
                                    }
                                }
                            }


                            return 1;
                        }
                )));
    }

    private void sendMessage(CommandContext<FabricClientCommandSource> context, String message) {
        context.getSource().sendFeedback(Text.literal(message));
    }

    private HitResult raycast(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickProgress) {
        double distance = Math.max(blockInteractionRange, entityInteractionRange);
        double distanceSquared = MathHelper.square(distance);
        Vec3d vec3d = camera.getCameraPosVec(tickProgress);
        HitResult hitResult = camera.raycast(distance, tickProgress, false);
        double f = hitResult.getPos().squaredDistanceTo(vec3d);
        if (hitResult.getType() != HitResult.Type.MISS) {
            distanceSquared = f;
            distance = Math.sqrt(f);
        }

        Vec3d vec3d2 = camera.getRotationVec(tickProgress);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
        Box box = camera.getBoundingBox().stretch(vec3d2.multiply(distance)).expand(1.0F, 1.0F, 1.0F);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box, EntityPredicates.CAN_HIT, distanceSquared);
        return entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(vec3d) < f ? ensureTargetInRange(entityHitResult, vec3d, entityInteractionRange) : ensureTargetInRange(hitResult, vec3d, blockInteractionRange);
    }

    private HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d vec3d = hitResult.getPos();
        if (!vec3d.isInRange(cameraPos, interactionRange)) {
            Vec3d vec3d2 = hitResult.getPos();
            Direction direction = Direction.getFacing(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
            return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
        } else {
            return hitResult;
        }
    }

    private List<WolfEntity> getTamedWolves(LivingEntity player) {
        var tamedWolves = new ArrayList<WolfEntity>();
        var entities = MinecraftClient.getInstance().world.getEntities();
        for (Entity entity : entities) {
            if (entity instanceof WolfEntity) {
                var wolf = (WolfEntity) entity;
                if (wolf.isTamed()) {
                    if (wolf.isOwner(player)) {
                        tamedWolves.add(wolf);
                    }
                }
            }
        }

        return tamedWolves;
    }
}

