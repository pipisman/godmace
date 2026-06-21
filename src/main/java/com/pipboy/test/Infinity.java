package com.pipboy.test;

import java.util.List;

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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Infinity extends Item {
	public Infinity(Item.Properties properties) {
		super(properties);
	}
	
	@Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		double radius = 6f;

	    // 2. Create a bounding box around the entity (the player/holder)
	    // .inflate(radius) expands the box in all directions (x, y, z)
	    AABB area = entity.getBoundingBox().inflate(radius);
	    // 3. Get the list of entities
	    // Use LivingEntity.class to get mobs/players, or Entity.class for everything (items, TNT, etc.)
	    List<Entity> allEntities = level.getEntities(entity, area);

	    for (Entity target : allEntities) {

	    	if (target instanceof Projectile projectile) {
	    		Vec3 diff = new Vec3(-entity.position().x + target.position().x, 
	        			-(entity.position().y - 1) + target.position().y,
	        			-entity.position().z + target.position().z);
	    	    
	    	    projectile.shoot(diff.x, diff.y, diff.z, 1.0F, 0.0F);
	    	    
	    	    	    	    projectile.hasImpulse = true;
	    	}
	    	else if (entity.distanceToSqr(target) <= radius * radius) {
	        	Vec3 diff = new Vec3(-entity.position().x + target.position().x, 
	        			-(entity.position().y - 1) + target.position().y,
	        			-entity.position().z + target.position().z);
	        	Vec3 close = new Vec3(1 - (diff.length() / radius),
	        			1 - (diff.length() / radius),
	        			1 - (diff.length() / radius));
	        	diff = diff.normalize().multiply(close);
	            target.setDeltaMovement(diff); 
	        }
	    }
    }
}