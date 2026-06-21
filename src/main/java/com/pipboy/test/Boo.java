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

public class Boo extends Item {
	public Boo(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		Vec3 lookAngle = player.getLookAngle();	
		// Vec3 tnt = player.getLookAngle.yRot((float)Math.PI / 2)
		double speed = 1.5;

		player.setDeltaMovement(lookAngle.x * speed, lookAngle.y * speed, lookAngle.z * speed);

		player.hurtMarked = true;
		itemstack.consume(1, player);
		for (int i = 0; i < 12; i++) {
			PrimedTnt primedtnt = new PrimedTnt(level, player.getX(), player.getY(), player.getZ(), player);
			Vec3 tntAngle = new Vec3(1, 0, 0).yRot((float) (Math.PI * 2 * i) / 12);
			primedtnt.setDeltaMovement(tntAngle.x * speed * 0.25f, 1f, tntAngle.z * speed * 0.25f);
			level.addFreshEntity(primedtnt);
		}
		
		return InteractionResult.SUCCESS;
	}
}