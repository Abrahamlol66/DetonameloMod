package mx.abraham.moreusefulenchants.logic;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Logica del encantamiento Telekinesis: los drops de bloques (incluidos los extra de Detonate,
 * Veinminer, Treecapitator y Harvest) y los drops + XP de mobs van directo al inventario del
 * jugador; lo que no cabe cae en su posicion en vez de en la del bloque/mob.
 *
 * Se registra DESPUES de AutoSmeltHandler en LootTableEvents.MODIFY_DROPS para que el orden
 * sea loot table -> auto_smelt -> telekinesis, como pide el diseno.
 */
public final class TelekinesisHandler {
	private TelekinesisHandler() {
	}

	public static void modifyDrops(Holder<net.minecraft.world.level.storage.loot.LootTable> lootTable, LootContext context, List<ItemStack> drops) {
		if (!(context.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}

		Entity thisEntity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
		ServerPlayer player = null;
		if (thisEntity instanceof ServerPlayer breaker) {
			// Rompimiento de bloque: el jugador es quien lo mina.
			player = breaker;
		} else {
			Entity attacker = context.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
			if (attacker instanceof ServerPlayer killer) {
				// Muerte de mob: el jugador es quien lo mato.
				player = killer;
			}
		}

		if (player == null) {
			return;
		}

		ItemStack tool = player.getMainHandItem();
		int level = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.TELEKINESIS, tool);
		if (level <= 0) {
			return;
		}

		for (int i = 0; i < drops.size(); i++) {
			ItemStack drop = drops.get(i);
			if (drop.isEmpty()) {
				continue;
			}
			ItemStack remainder = drop.copy();
			boolean added = player.getInventory().add(remainder);
			if (added && remainder.isEmpty()) {
				drops.set(i, ItemStack.EMPTY);
			} else {
				spawnAtPlayer(serverLevel, player, remainder);
				drops.set(i, ItemStack.EMPTY);
			}
		}
	}

	/** Despues de matar un mob con Telekinesis, la XP se absorbe directo en vez de volar como orbes. */
	public static void afterDeath(LivingEntity entity, DamageSource damageSource) {
		if (!(entity.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		if (!(damageSource.getEntity() instanceof ServerPlayer player)) {
			return;
		}

		ItemStack tool = player.getMainHandItem();
		int level = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.TELEKINESIS, tool);
		if (level <= 0) {
			return;
		}

		AABB area = new AABB(entity.position(), entity.position()).inflate(1.5);
		for (ExperienceOrb orb : serverLevel.getEntitiesOfClass(ExperienceOrb.class, area)) {
			player.giveExperiencePoints(orb.getValue());
			orb.discard();
		}
	}

	private static void spawnAtPlayer(ServerLevel level, ServerPlayer player, ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}
		ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), stack);
		level.addFreshEntity(itemEntity);
	}
}
