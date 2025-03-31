package com.r1nge.dogs.emperor;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.r1nge.dogs.emperor.DogsEmperorMod.LOGGER;

public class Commands {
    private double maxDistance = 50;

    public void Initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("attack")
                .executes(context -> {
                            var server = context.getSource().getServer();
                            var player = context.getSource().getPlayer();
                            var world = player.getServerWorld();
                            var camera = player.getCameraEntity();
                            //TODO: fix tick progress
                            var tickProgress = 1;
                            HitResult raycast = raycast(camera, maxDistance, maxDistance, tickProgress);
                            switch (raycast.getType()) {
                                case MISS, BLOCK -> {
                                }
                                case ENTITY -> {
                                    var tamedWolves = getTamedWolves(world, player);
                                    var target = ((EntityHitResult) raycast).getEntity();
                                    var targetLiving = ((LivingEntity) target);
                                    for (WolfEntity wolf : tamedWolves) {
                                        if (wolf.getUuid().equals(targetLiving.getUuid())) {
                                            LOGGER.info("Ignoring same wolf");
                                            continue;
                                        }
                                        wolf.setTarget(targetLiving);
                                        wolf.setAttacking(true);
                                    }
                                }
                            }


                            return 1;
                        }
                )));
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

    private List<WolfEntity> getTamedWolves(ServerWorld serverWorld, LivingEntity player) {
        var tamedWolves = new ArrayList<WolfEntity>();
        var entities = serverWorld.getEntitiesByClass(WolfEntity.class, player.getBoundingBox().expand(50), wolf -> // Increase the bounding box to search wider area
                wolf.isTamed()).stream().collect(Collectors.toList());
        for (Entity entity : entities) {
            if (entity instanceof WolfEntity) {
                var wolf = (WolfEntity) entity;
                if (wolf.isTamed()) {
                    if (wolf.isOwner(player)) {
                        if (!wolf.isSitting()) {
                            tamedWolves.add(wolf);
                        }
                    }
                }
            }
        }

        return tamedWolves;
    }

}

