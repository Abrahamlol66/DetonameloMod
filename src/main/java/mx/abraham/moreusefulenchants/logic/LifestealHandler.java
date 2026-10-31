package mx.abraham.moreusefulenchants.logic;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;

/**
 * Logica del encantamiento Lifesteal: al golpear con un ataque cargado (cooldown >= 0.9), cura
 * al atacante un porcentaje del daño final infligido segun el nivel, maximo 4 de vida (2
 * corazones) por golpe. No cura si el daño fue 0 o el objetivo no es una entidad viva de verdad
 * (armor stands, etc.).
 */
public final class LifestealHandler {
	private static final float[] HEAL_PERCENT_BY_LEVEL = {0.0f, 0.10f, 0.15f, 0.20f};
	private static final float MAX_HEAL = 4.0f;
	private static final float MIN_ATTACK_STRENGTH = 0.9f;

	private LifestealHandler() {
	}

	public static void afterDamage(LivingEntity entity, DamageSource damageSource, float baseDamageTaken, float damageTaken, boolean blocked) {
		if (damageTaken <= 0.0f || !entity.isAlive() || entity instanceof ArmorStand) {
			return;
		}
		if (!(damageSource.getEntity() instanceof ServerPlayer attacker)) {
			return;
		}
		if (!(attacker.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		ItemStack weapon = attacker.getMainHandItem();
		int level = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.LIFESTEAL, weapon);
		if (level <= 0 || level >= HEAL_PERCENT_BY_LEVEL.length) {
			return;
		}

		if (attacker.getAttackStrengthScale(0.5f) < MIN_ATTACK_STRENGTH) {
			return;
		}

		float heal = Math.min(damageTaken * HEAL_PERCENT_BY_LEVEL[level], MAX_HEAL);
		if (heal > 0.0f) {
			attacker.heal(heal);
		}
	}
}
