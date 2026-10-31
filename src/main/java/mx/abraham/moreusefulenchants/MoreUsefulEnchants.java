package mx.abraham.moreusefulenchants;

import mx.abraham.moreusefulenchants.item.ModCreativeTabs;
import mx.abraham.moreusefulenchants.logic.AutoSmeltHandler;
import mx.abraham.moreusefulenchants.logic.DetonateHandler;
import mx.abraham.moreusefulenchants.logic.HarvestHandler;
import mx.abraham.moreusefulenchants.logic.HeadThiefHandler;
import mx.abraham.moreusefulenchants.logic.LifestealHandler;
import mx.abraham.moreusefulenchants.logic.ReplenishHandler;
import mx.abraham.moreusefulenchants.logic.TelekinesisHandler;
import mx.abraham.moreusefulenchants.logic.TreecapitatorHandler;
import mx.abraham.moreusefulenchants.logic.VeinMinerHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreUsefulEnchants implements ModInitializer {
	public static final String MOD_ID = "moreusefulenchants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModCreativeTabs.register();

		PlayerBlockBreakEvents.BEFORE.register(ReplenishHandler::beforeBlockBreak);
		PlayerBlockBreakEvents.AFTER.register(DetonateHandler::afterBlockBreak);
		PlayerBlockBreakEvents.AFTER.register(TreecapitatorHandler::afterBlockBreak);
		PlayerBlockBreakEvents.AFTER.register(VeinMinerHandler::afterBlockBreak);
		PlayerBlockBreakEvents.AFTER.register(HarvestHandler::afterBlockBreak);
		PlayerBlockBreakEvents.AFTER.register(ReplenishHandler::afterBlockBreak);

		// Orden dentro de la resolucion de la loot table: vanilla -> auto_smelt -> replenish -> telekinesis.
		LootTableEvents.MODIFY_DROPS.register(AutoSmeltHandler::modifyDrops);
		LootTableEvents.MODIFY_DROPS.register(ReplenishHandler::modifyDrops);
		LootTableEvents.MODIFY_DROPS.register(TelekinesisHandler::modifyDrops);

		ServerLivingEntityEvents.AFTER_DAMAGE.register(LifestealHandler::afterDamage);
		ServerLivingEntityEvents.AFTER_DEATH.register(HeadThiefHandler::afterDeath);
		ServerLivingEntityEvents.AFTER_DEATH.register(TelekinesisHandler::afterDeath);

		LOGGER.info("MoreUsefulEnchants cargado.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
