package mx.abraham.moreusefulenchants.logic;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Logica del encantamiento Lifesteal: al golpear con un ataque cargado (cooldown >= 0.9), cura
 * al atacante una cantidad fija por nivel mas un porcentaje del daño final infligido, con un
 * tope que tambien escala por nivel. La parte fija es la que hace que se note el efecto incluso
 * contra objetivos con mucha armadura (donde el daño real por golpe es pequeño y un porcentaje
 * de eso solo no alcanzaria a notarse). No cura si el daño fue 0 o el objetivo no es una entidad
 * viva de verdad (armor stands, etc.).
 */
public final class LifestealHandler {
	private static final float[] FLAT_HEAL_BY_LEVEL = {0.0f, 1.0f, 1.5f, 2.0f};
	private static final float[] HEAL_PERCENT_BY_LEVEL = {0.0f, 0.20f, 0.30f, 0.40f};
	private static final float[] MAX_HEAL_BY_LEVEL = {0.0f, 3.0f, 5.0f, 7.0f};
	private static final float MIN_ATTACK_STRENGTH = 0.9f;

	/**
	 * Cooldown de ataque capturado por PlayerAttackMixin ANTES de que Player.attack() lo
	 * resetee a 0. ServerLivingEntityEvents.AFTER_DAMAGE se dispara despues de ese reset, asi
	 * que leer getAttackStrengthScale() ahi siempre daria 0 sin importar que tan cargado
	 * estaba el golpe.
	 */
	private static final Map<UUID, Float> LAST_ATTACK_STRENGTH = new HashMap<>();

	private LifestealHandler() {
	}

	public static void recordAttackStrength(Player player, float attackStrengthScale) {
		LAST_ATTACK_STRENGTH.put(player.getUUID(), attackStrengthScale);
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

		Float attackStrength = LAST_ATTACK_STRENGTH.remove(attacker.getUUID());
		if (attackStrength == null || attackStrength < MIN_ATTACK_STRENGTH) {
			return;
		}

		float heal = Math.min(FLAT_HEAL_BY_LEVEL[level] + damageTaken * HEAL_PERCENT_BY_LEVEL[level], MAX_HEAL_BY_LEVEL[level]);
		if (heal > 0.0f) {
			attacker.heal(heal);
		}
	}
}
