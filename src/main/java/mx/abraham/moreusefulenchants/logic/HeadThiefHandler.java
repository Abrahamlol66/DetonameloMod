package mx.abraham.moreusefulenchants.logic;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Logica del encantamiento Head Thief: al matar con el arma, probabilidad de soltar la cabeza
 * del mob (zombi, esqueleto, creeper, piglin, jugador), +2% por nivel de Saqueo. Excluidos
 * Ender Dragon (su cabeza ya se consigue en los barcos del End) y Wither Skeleton (ya tiene
 * su propia probabilidad vanilla) - con ellos no hacemos nada. No duplica: si el mob ya iba a
 * soltar su cabeza por una via vanilla (creeper cargado), tampoco soltamos otra.
 */
public final class HeadThiefHandler {
	private static final float[] BASE_CHANCE_BY_LEVEL = {0.0f, 0.05f, 0.10f, 0.15f};
	private static final float LOOTING_BONUS_PER_LEVEL = 0.02f;

	private HeadThiefHandler() {
	}

	public static void afterDeath(LivingEntity entity, DamageSource damageSource) {
		if (!(entity.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		if (!(damageSource.getEntity() instanceof ServerPlayer attacker)) {
			return;
		}

		ItemStack weapon = attacker.getMainHandItem();
		int level = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.HEAD_THIEF, weapon);
		if (level <= 0 || level >= BASE_CHANCE_BY_LEVEL.length) {
			return;
		}

		ItemStack head = resolveHead(entity);
		if (head.isEmpty()) {
			return;
		}

		int lootingLevel = ModEnchantments.getLevel(serverLevel.registryAccess(), Enchantments.LOOTING, weapon);
		float chance = BASE_CHANCE_BY_LEVEL[level] + LOOTING_BONUS_PER_LEVEL * lootingLevel;

		if (serverLevel.getRandom().nextFloat() >= chance) {
			return;
		}

		if (ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.TELEKINESIS, weapon) > 0) {
			ItemStack remainder = head.copy();
			if (!attacker.getInventory().add(remainder) || !remainder.isEmpty()) {
				entity.spawnAtLocation(serverLevel, remainder);
			}
		} else {
			entity.spawnAtLocation(serverLevel, head);
		}
	}

	/** La cabeza del mob, o ItemStack.EMPTY si es un tipo sin cabeza soportada o ya la droppea vanilla. */
	private static ItemStack resolveHead(LivingEntity entity) {
		if (entity.getClass() == Zombie.class) {
			return new ItemStack(Items.ZOMBIE_HEAD);
		}
		if (entity.getClass() == Skeleton.class) {
			return new ItemStack(Items.SKELETON_SKULL);
		}
		if (entity instanceof Creeper creeper) {
			// Un creeper cargado ya suelta su cabeza por el loot table vanilla.
			return creeper.isPowered() ? ItemStack.EMPTY : new ItemStack(Items.CREEPER_HEAD);
		}
		if (entity instanceof Piglin) {
			return new ItemStack(Items.PIGLIN_HEAD);
		}
		if (entity instanceof ServerPlayer player) {
			ItemStack head = new ItemStack(Items.PLAYER_HEAD);
			head.set(DataComponents.PROFILE, ResolvableProfile.createResolved(player.getGameProfile()));
			return head;
		}
		// Ender Dragon y Wither Skeleton quedan fuera a proposito (vanilla ya los maneja).
		return ItemStack.EMPTY;
	}
}
