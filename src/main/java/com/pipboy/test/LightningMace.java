package com.pipboy.test;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LightningMace extends Item {
	private static final ResourceLocation BASE_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("pipboy", "base_attack_damage");
	private static final ResourceLocation BASE_ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath("pipboy", "base_attack_speed");
	public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
	private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
	public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
	private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;
	
	public LightningMace(Item.Properties p_333796_) {
		super(p_333796_);
	}
	

	public InteractionResult use(Level level, Player player, InteractionHand hand) {

		ItemStack itemstack = player.getItemInHand(hand);
		Vec3 boost = new Vec3(0, 3, 0);
		player.setDeltaMovement(boost);

		player.getCooldowns().addCooldown(itemstack, 160);
		if (level instanceof ServerLevel serverLevel) {
	        serverLevel.sendParticles(
	            net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
	            player.getX(), player.getY(), player.getZ(),
	            20,
	            0.5, 0.2, 0.5,
	            0.1
	        );

	        if (player instanceof ServerPlayer serverPlayer) {
	            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
	        }
	    }
		return InteractionResult.SUCCESS;
	}

	public static ItemAttributeModifiers createAttributes() {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE,
						new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5.0, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED,
						new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.4F, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND)
				.build();
	}

	public static Tool createToolProperties() {
		return new Tool(List.of(), 1.0F, 2, false);
	}

	@Override
	public void hurtEnemy(ItemStack p_334046_, LivingEntity p_333712_, LivingEntity p_333812_) {
		if (canSmashAttack(p_333812_)) {
			ServerLevel serverlevel = (ServerLevel) p_333812_.level();
			p_333812_.setDeltaMovement(p_333812_.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
			if (p_333812_ instanceof ServerPlayer serverplayer) {
				serverplayer.currentImpulseImpactPos = this.calculateImpactPosition(serverplayer);
				serverplayer.setIgnoreFallDamageFromCurrentImpulse(true);
				serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
			}

			if (p_333712_.onGround()) {
				if (p_333812_ instanceof ServerPlayer serverplayer1) {
					serverplayer1.setSpawnExtraParticlesOnFall(true);
				}

				SoundEvent soundevent = p_333812_.fallDistance > 5.0 ? SoundEvents.MACE_SMASH_GROUND_HEAVY
						: SoundEvents.MACE_SMASH_GROUND;
				serverlevel.playSound(null, p_333812_.getX(), p_333812_.getY(), p_333812_.getZ(), soundevent,
						p_333812_.getSoundSource(), 1.0F, 1.0F);
			} else {
				serverlevel.playSound(null, p_333812_.getX(), p_333812_.getY(), p_333812_.getZ(),
						SoundEvents.MACE_SMASH_AIR, p_333812_.getSoundSource(), 1.0F, 1.0F);
			}

			knockback(serverlevel, p_333812_, p_333712_);
		}
	}
	int rmod = 0;
	@Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		
		rmod += 5;
		if (level instanceof ServerLevel serverLevel) {
			for (int angle = 0; angle < 4; angle++) {
				
				float a = (float) Math.toRadians(angle * 80 + rmod);
				serverLevel.sendParticles(
			            net.minecraft.core.particles.ParticleTypes.TRIAL_OMEN,
			            entity.getX() + Math.cos(a) * 3f, entity.getY() - 0.7f, entity.getZ() + Math.sin(a) * 3f,
			            20,
			            0.5, 0.2, 0.5,
			            0.1
			        );
				
			}
	        
		}
		
	}
	
	private Vec3 calculateImpactPosition(ServerPlayer player) {
		return player.isIgnoringFallDamageFromCurrentImpulse() && player.currentImpulseImpactPos != null
				&& player.currentImpulseImpactPos.y <= player.position().y ? player.currentImpulseImpactPos
						: player.position();
	}

	@Override
	public void postHurtEnemy(ItemStack p_345716_, LivingEntity p_345817_, LivingEntity p_346003_) {
		if (canSmashAttack(p_346003_)) {
			p_346003_.resetFallDistance();
		}
	}

	@Override
	public float getAttackDamageBonus(Entity p_344900_, float p_335575_, DamageSource p_344972_) {
		if (p_344972_.getDirectEntity() instanceof LivingEntity livingentity) {
			if (!canSmashAttack(livingentity)) {
				return 0.0F;
			} else {
				double d3 = 3.0;
				double d0 = 8.0;
				double d1 = livingentity.fallDistance * 2f;
				double d2;
				if (d1 <= 3.0) {
					d2 = 4.0 * d1;
				} else if (d1 <= 8.0) {
					d2 = 12.0 + 2.0 * (d1 - 3.0);
				} else {
					d2 = 22.0 + d1 - 8.0;
				}

				return livingentity.level() instanceof ServerLevel serverlevel
						? (float) (d2 + EnchantmentHelper.modifyFallBasedDamage(serverlevel,
								livingentity.getWeaponItem(), p_344900_, p_344972_, 0.0F) * d1)
						: (float) d2;
			}
		} else {
			return 0.0F;
		}
	}

	private static void knockback(Level level, Entity attacker, Entity target) {
		level.levelEvent(2013, target.getOnPos(), 750);
		level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3.5),
				knockbackPredicate(attacker, target)).forEach(p_347296_ -> {
					Vec3 vec3 = p_347296_.position().subtract(target.position());
					double d0 = getKnockbackPower(attacker, p_347296_, vec3);
					Vec3 vec31 = vec3.normalize().scale(d0);
					if (d0 > 0.0) {
						p_347296_.push(vec31.x, 0.7F, vec31.z);
						if (p_347296_ instanceof ServerPlayer serverplayer) {
							serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
						}
					}
				});
	}

	private static Predicate<LivingEntity> knockbackPredicate(Entity attacker, Entity target) {
		return p_393278_ -> {
			boolean flag = !p_393278_.isSpectator();
			boolean flag1 = p_393278_ != attacker && p_393278_ != target;
			boolean flag2 = !attacker.isAlliedTo(p_393278_);
			boolean flag3 = !(p_393278_ instanceof TamableAnimal tamableanimal
					&& target instanceof LivingEntity livingentity && tamableanimal.isTame()
					&& tamableanimal.isOwnedBy(livingentity));
			boolean flag4 = !(p_393278_ instanceof ArmorStand armorstand && armorstand.isMarker());
			boolean flag5 = target.distanceToSqr(p_393278_) <= Math.pow(3.5, 2.0);
			return flag && flag1 && flag2 && flag3 && flag4 && flag5;
		};
	}

	private static double getKnockbackPower(Entity attacker, LivingEntity entity, Vec3 offset) {
		return (3.5 - offset.length()) * 0.7F * (attacker.fallDistance > 5.0 ? 2 : 1)
				* (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
	}

	public static boolean canSmashAttack(LivingEntity entity) {
		return entity.fallDistance > 1.5 && !entity.isFallFlying();
	}

	@Nullable
	@Override
	public DamageSource getDamageSource(LivingEntity p_372868_) {
		return canSmashAttack(p_372868_) ? p_372868_.damageSources().mace(p_372868_) : super.getDamageSource(p_372868_);
	}
}
