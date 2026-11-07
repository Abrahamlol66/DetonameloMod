package mx.abraham.moreusefulenchants.logic;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Logica del encantamiento Lightning: al golpear (sin importar que tan cargado este el
 * ataque, funciona hasta haciendo spam), probabilidad de que caiga un rayo sobre el
 * objetivo con dano propio, ambos escalando por nivel. Funciona con cualquier clima, a
 * diferencia de Canalizacion. El rayo es puramente visual (LightningBolt.setVisualOnly(true),
 * asi vanilla se salta su propio bucle de "danar a todo lo que este cerca" y la colocacion
 * de fuego en bloques).
 *
 * El dano base (fijo, 5.0) y los efectos secundarios vanilla (cargar creepers, convertir
 * aldeanos en brujas) se reusan llamando a Entity.thunderHit(...); como ese metodo trae el
 * 5.0 clavado y no escala por nivel, se completa con un hurtServer(...) extra por la
 * diferencia hasta llegar al dano real del nivel (BASE_THUNDER_DAMAGE). thunderHit tambien
 * trae logica de "prender fuego", pero solo prende si remainingFireTicks ya estaba en -1 en
 * ese instante (ver Entity.tick/clearFire: para una entidad normal eso nunca pasa, se queda
 * en 0), por eso en la practica nunca prende fuego al objetivo.
 *
 * El objetivo ya esta en su ventana de invulnerabilidad por el golpe de espada que disparo
 * este evento (LivingEntity.hurtServer ignora cualquier dano nuevo que no supere al del
 * golpe anterior mientras invulnerableTime > 10), asi que sin resetearla el dano del rayo
 * quedaria absorbido en silencio. Se resetea antes de cada hurtServer para que ambos (el de
 * thunderHit y el extra) se traten como golpes independientes.
 */
public final class LightningHandler {
	private static final float[] CHANCE_BY_LEVEL = {0.0f, 0.15f, 0.30f, 0.45f};
	private static final float[] DAMAGE_BY_LEVEL = {0.0f, 6.0f, 10.0f, 15.0f};
	private static final float BASE_THUNDER_DAMAGE = 5.0f;
	private static final long COOLDOWN_MILLIS = 1000L;

	private static final Map<UUID, Long> LAST_STRIKE_MILLIS = new HashMap<>();

	private LightningHandler() {
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
		int level = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.LIGHTNING, weapon);
		if (level <= 0 || level >= CHANCE_BY_LEVEL.length) {
			return;
		}

		if (isOnCooldown(entity)) {
			return;
		}

		if (serverLevel.getRandom().nextFloat() >= CHANCE_BY_LEVEL[level]) {
			return;
		}

		strike(serverLevel, entity, attacker, DAMAGE_BY_LEVEL[level]);
		LAST_STRIKE_MILLIS.put(entity.getUUID(), System.currentTimeMillis());
	}

	private static boolean isOnCooldown(LivingEntity entity) {
		Long last = LAST_STRIKE_MILLIS.get(entity.getUUID());
		return last != null && System.currentTimeMillis() - last < COOLDOWN_MILLIS;
	}

	private static void strike(ServerLevel serverLevel, LivingEntity target, ServerPlayer attacker, float damage) {
		LightningBolt bolt = new LightningBolt(EntityTypes.LIGHTNING_BOLT, serverLevel);
		bolt.setPos(target.getX(), target.getY(), target.getZ());
		bolt.setVisualOnly(true);
		bolt.setCause(attacker);
		serverLevel.addFreshEntity(bolt);

		target.invulnerableTime = 0;
		target.thunderHit(serverLevel, bolt);

		float extraDamage = damage - BASE_THUNDER_DAMAGE;
		if (extraDamage > 0.0f) {
			target.invulnerableTime = 0;
			target.hurtServer(serverLevel, serverLevel.damageSources().lightningBolt(), extraDamage);
		}
	}
}
