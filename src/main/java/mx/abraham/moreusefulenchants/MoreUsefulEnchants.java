package mx.abraham.moreusefulenchants;

import mx.abraham.moreusefulenchants.item.ModCreativeTabs;
import mx.abraham.moreusefulenchants.logic.DetonateHandler;
import mx.abraham.moreusefulenchants.logic.TreecapitatorHandler;
import mx.abraham.moreusefulenchants.logic.VeinMinerHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreUsefulEnchants implements ModInitializer {
	public static final String MOD_ID = "moreusefulenchants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModCreativeTabs.register();

		PlayerBlockBreakEvents.AFTER.register(DetonateHandler::afterBlockBreak);
		PlayerBlockBreakEvents.AFTER.register(TreecapitatorHandler::afterBlockBreak);
		PlayerBlockBreakEvents.AFTER.register(VeinMinerHandler::afterBlockBreak);

		LOGGER.info("MoreUsefulEnchants cargado.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
