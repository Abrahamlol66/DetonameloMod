package mx.abraham.detonamelo;

import mx.abraham.detonamelo.logic.DetonateHandler;
import mx.abraham.detonamelo.logic.TreecapitatorHandler;
import mx.abraham.detonamelo.logic.VeinMinerHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Detonamelo implements ModInitializer {
	public static final String MOD_ID = "detonamelo";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PlayerBlockBreakEvents.AFTER.register(DetonateHandler::afterBlockBreak);
		PlayerBlockBreakEvents.AFTER.register(TreecapitatorHandler::afterBlockBreak);
		PlayerBlockBreakEvents.AFTER.register(VeinMinerHandler::afterBlockBreak);

		LOGGER.info("Detonamelo cargado.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
