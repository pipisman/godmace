package com.pipboy.test;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class staff extends Item {
	public staff(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		
		ItemStack itemstack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(itemstack)) {
            return InteractionResult.FAIL;
        }
		Vec3 lookAngle = player.getLookAngle();
		// Vec3 tnt = player.getLookAngle.yRot((float)Math.PI / 2)
		double speed = itemstack.getCount();
		
		player.setPos(lookAngle.x * speed + player.position().x, lookAngle.y * speed + player.position().y, lookAngle.z * speed + player.position().z);

		player.hurtMarked = true;
		itemstack.consume(1, player);
		
		player.getCooldowns().addCooldown(itemstack, 300);
		return InteractionResult.SUCCESS;
	}
}