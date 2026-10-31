package mx.abraham.moreusefulenchants.logic;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Logica del encantamiento Harvest: al romper un cultivo maduro con azada, cosecha tambien
 * los cultivos maduros (del mismo tipo de bloque) en un area horizontal: 3x3 (nivel 1), 5x5
 * (nivel 2) o 7x7 (nivel 3), en el mismo Y. Si la azada tambien tiene Replenish, cada cultivo
 * extra se replanta (eso lo maneja ReplenishHandler, que no respeta el BreakGuard a proposito).
 */
public final class HarvestHandler {
	private HarvestHandler() {
	}

	public static void afterBlockBreak(Level level, Player player, BlockPos centerPos, BlockState centerState, BlockEntity blockEntity) {
		if (BreakGuard.isBreakingExtra()) {
			return;
		}
		if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		if (serverPlayer.isShiftKeyDown()) {
			return;
		}

		int age = ReplenishHandler.getAge(centerState);
		if (age < 0 || age < ReplenishHandler.getMaxAge(centerState)) {
			return;
		}

		ItemStack tool = serverPlayer.getMainHandItem();
		int harvestLevel = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.HARVEST, tool);
		if (harvestLevel <= 0) {
			return;
		}

		int radius = harvestLevel; // nivel 1 = 3x3 (radio 1), 2 = 5x5, 3 = 7x7
		Block cropBlock = centerState.getBlock();
		ServerPlayerGameMode gameMode = serverPlayer.gameMode;

		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if (dx == 0 && dz == 0) {
					continue;
				}
				if (serverPlayer.getMainHandItem().isEmpty()) {
					// La azada se rompio: detenemos la cosecha extra.
					return;
				}

				BlockPos pos = centerPos.offset(dx, 0, dz);
				BlockState state = serverLevel.getBlockState(pos);
				if (state.getBlock() != cropBlock) {
					continue;
				}
				int extraAge = ReplenishHandler.getAge(state);
				if (extraAge < 0 || extraAge < ReplenishHandler.getMaxAge(state)) {
					// No maduro: Harvest solo cosecha cultivos maduros.
					continue;
				}
				if (!serverPlayer.mayBuild() || !serverPlayer.mayInteract(serverLevel, pos)) {
					continue;
				}

				BreakGuard.runGuarded(() -> gameMode.destroyBlock(pos));
			}
		}
	}
}
