package com.pipboy.test;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class scythe extends Item {
	public scythe(Item.Properties properties) {
		super(properties);
	}
	
	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		Vec3 vel = target.position().subtract(attacker.position()).normalize();
		float boost = 3;
		target.setDeltaMovement(new Vec3(0, -2, 0));
		Level level = target.level();
		if (!level.isClientSide()) {
	        BlockPos pos = target.blockPosition();
	        level.setBlockAndUpdate(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()), Blocks.AIR.defaultBlockState());
	        level.setBlockAndUpdate(new BlockPos(pos.getX(), pos.getY() - 2, pos.getZ()), Blocks.AIR.defaultBlockState());
	        target.setPos(new Vec3((int)target.position().x, target.position().y, (int)target.position().z).subtract(new Vec3(0.5f, 0, -0.5f)));
	    }
	}
}