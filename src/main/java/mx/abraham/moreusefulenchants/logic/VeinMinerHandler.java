package mx.abraham.moreusefulenchants.logic;

import mx.abraham.moreusefulenchants.MoreUsefulEnchants;
import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Logica del encantamiento Veinminer: al minar una mena de mineral, se minan tambien todas
 * las menas del mismo tipo que esten conectadas (vein mining).
 */
public final class VeinMinerHandler {
	/** Union de todos los tags vanilla de menas minerales (no hay un tag unico en vanilla). */
	public static final TagKey<Block> ORE = TagKey.create(Registries.BLOCK, MoreUsefulEnchants.id("ore"));

	private static final int MAX_ORES = 128;

	private VeinMinerHandler() {
	}

	/** Llamado desde PlayerBlockBreakEvents.AFTER (solo servidor). */
	public static void afterBlockBreak(Level level, Player player, BlockPos centerPos, BlockState centerState, BlockEntity blockEntity) {
		if (BreakGuard.isBreakingExtra()) {
			return;
		}
		if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		if (!centerState.is(ORE)) {
			return;
		}

		ItemStack tool = serverPlayer.getMainHandItem();
		int veinminerLevel = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.VEINMINER, tool);
		if (veinminerLevel <= 0) {
			return;
		}

		Block oreBlock = centerState.getBlock();
		ServerPlayerGameMode gameMode = serverPlayer.gameMode;

		for (BlockPos pos : findConnectedOres(serverLevel, centerPos, oreBlock)) {
			if (pos.equals(centerPos)) {
				continue;
			}
			if (serverPlayer.getMainHandItem().isEmpty()) {
				// El pico se rompio: detenemos la veta.
				break;
			}

			BlockState state = serverLevel.getBlockState(pos);
			if (state.isAir() || state.getBlock() != oreBlock) {
				continue;
			}
			if (state.getDestroySpeed(serverLevel, pos) < 0.0f) {
				continue;
			}
			if (!serverPlayer.mayBuild() || !serverPlayer.mayInteract(serverLevel, pos)) {
				continue;
			}
			if (!serverPlayer.getMainHandItem().isCorrectToolForDrops(state)) {
				continue;
			}

			BreakGuard.runGuarded(() -> gameMode.destroyBlock(pos));
		}
	}

	/** BFS de menas del mismo bloque, conectadas por cara (6 vecinos), con limite de seguridad. */
	private static List<BlockPos> findConnectedOres(ServerLevel level, BlockPos start, Block oreBlock) {
		List<BlockPos> found = new ArrayList<>();
		Set<BlockPos> visited = new HashSet<>();
		Deque<BlockPos> queue = new ArrayDeque<>();
		queue.add(start);
		visited.add(start);

		while (!queue.isEmpty() && found.size() < MAX_ORES) {
			BlockPos pos = queue.poll();
			found.add(pos);
			for (Direction direction : Direction.values()) {
				BlockPos neighbor = pos.relative(direction);
				if (visited.contains(neighbor)) {
					continue;
				}
				visited.add(neighbor);
				if (level.getBlockState(neighbor).getBlock() == oreBlock) {
					queue.add(neighbor);
				}
			}
		}
		return found;
	}
}
