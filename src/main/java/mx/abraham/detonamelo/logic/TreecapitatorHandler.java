package mx.abraham.detonamelo.logic;

import mx.abraham.detonamelo.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Logica del encantamiento Treecapitator: tala el arbol completo (troncos conectados +
 * hojas naturales cercanas) y replanta un brote del mismo tipo en la base.
 */
public final class TreecapitatorHandler {
	private static final int MAX_LOGS = 256;
	private static final int MAX_LEAVES = 1024;
	private static final int MAX_LEAF_DISTANCE = 6;

	/** El brote plantado no se descuenta de los drops del arbol (se mantiene igual que vanilla). */
	private static final boolean CONSUME_SAPLING_FROM_DROPS = false;

	private static final Map<String, Block> SPECIES_TO_SAPLING = new HashMap<>();

	static {
		SPECIES_TO_SAPLING.put("oak", Blocks.OAK_SAPLING);
		SPECIES_TO_SAPLING.put("spruce", Blocks.SPRUCE_SAPLING);
		SPECIES_TO_SAPLING.put("birch", Blocks.BIRCH_SAPLING);
		SPECIES_TO_SAPLING.put("jungle", Blocks.JUNGLE_SAPLING);
		SPECIES_TO_SAPLING.put("acacia", Blocks.ACACIA_SAPLING);
		SPECIES_TO_SAPLING.put("dark_oak", Blocks.DARK_OAK_SAPLING);
		SPECIES_TO_SAPLING.put("cherry", Blocks.CHERRY_SAPLING);
		SPECIES_TO_SAPLING.put("pale_oak", Blocks.PALE_OAK_SAPLING);
		SPECIES_TO_SAPLING.put("mangrove", Blocks.MANGROVE_PROPAGULE);
		SPECIES_TO_SAPLING.put("azalea", Blocks.AZALEA);
		SPECIES_TO_SAPLING.put("flowering_azalea", Blocks.FLOWERING_AZALEA);
	}

	private TreecapitatorHandler() {
	}

	/** Llamado desde PlayerBlockBreakEvents.AFTER (solo servidor). */
	public static void afterBlockBreak(Level level, Player player, BlockPos centerPos, BlockState centerState, BlockEntity blockEntity) {
		if (BreakGuard.isBreakingExtra()) {
			return;
		}
		if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		if (!centerState.is(BlockTags.OVERWORLD_NATURAL_LOGS)) {
			return;
		}

		ItemStack tool = serverPlayer.getMainHandItem();
		int treecapitatorLevel = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.TREECAPITATOR, tool);
		if (treecapitatorLevel <= 0) {
			return;
		}

		Block trunkBlock = centerState.getBlock();

		List<BlockPos> logs = findConnectedLogs(serverLevel, centerPos, trunkBlock);
		List<BlockPos> leaves = findConnectedLeaves(serverLevel, logs);
		if (leaves.isEmpty()) {
			// Sin hojas naturales pegadas: no es un arbol de verdad (p. ej. una casa de troncos).
			return;
		}

		String species = detectSpecies(serverLevel, leaves, trunkBlock);
		Block saplingBlock = SPECIES_TO_SAPLING.get(species);

		List<BlockPos> saplingPositions = saplingBlock == null
				? List.of()
				: findSaplingPositions(serverLevel, logs, saplingBlock);

		// Rompe los troncos (ya incluye el bloque central, pero destroyBlock sobre ese
		// bloque ya roto simplemente no hace nada porque esta en el aire).
		ServerPlayerGameMode gameMode = serverPlayer.gameMode;
		for (BlockPos logPos : logs) {
			if (logPos.equals(centerPos)) {
				continue;
			}
			if (serverPlayer.getMainHandItem().isEmpty()) {
				// El hacha se rompio: detenemos la tala.
				return;
			}
			BreakGuard.runGuarded(() -> gameMode.destroyBlock(logPos));
		}

		// Rompe las hojas generando sus drops vanilla, sin gastar durabilidad del hacha.
		ItemStack finalTool = serverPlayer.getMainHandItem();
		for (BlockPos leafPos : leaves) {
			BlockState leafState = serverLevel.getBlockState(leafPos);
			if (!(leafState.getBlock() instanceof LeavesBlock)) {
				continue;
			}
			BreakGuard.runGuarded(() -> {
				Block.dropResources(leafState, serverLevel, leafPos, serverLevel.getBlockEntity(leafPos), serverPlayer, finalTool);
				serverLevel.removeBlock(leafPos, false);
			});
		}

		if (saplingBlock != null) {
			for (BlockPos saplingPos : saplingPositions) {
				serverLevel.setBlockAndUpdate(saplingPos, saplingBlock.defaultBlockState());
			}
		}
	}

	private static List<BlockPos> findConnectedLogs(ServerLevel level, BlockPos start, Block trunkBlock) {
		List<BlockPos> found = new ArrayList<>();
		Set<BlockPos> visited = new HashSet<>();
		Deque<BlockPos> queue = new ArrayDeque<>();
		queue.add(start);
		visited.add(start);

		while (!queue.isEmpty() && found.size() < MAX_LOGS) {
			BlockPos pos = queue.poll();
			found.add(pos);
			for (BlockPos neighbor : neighbors26(pos)) {
				if (visited.contains(neighbor)) {
					continue;
				}
				visited.add(neighbor);
				if (level.getBlockState(neighbor).getBlock() == trunkBlock) {
					queue.add(neighbor);
				}
			}
		}
		return found;
	}

	private static List<BlockPos> findConnectedLeaves(ServerLevel level, List<BlockPos> logs) {
		List<BlockPos> found = new ArrayList<>();
		Map<BlockPos, Integer> distance = new HashMap<>();
		Deque<BlockPos> queue = new ArrayDeque<>();

		for (BlockPos logPos : logs) {
			for (BlockPos neighbor : neighbors26(logPos)) {
				if (distance.containsKey(neighbor)) {
					continue;
				}
				if (isNaturalLeaf(level, neighbor)) {
					distance.put(neighbor, 1);
					queue.add(neighbor);
				}
			}
		}

		while (!queue.isEmpty() && found.size() < MAX_LEAVES) {
			BlockPos pos = queue.poll();
			found.add(pos);
			int dist = distance.get(pos);
			if (dist >= MAX_LEAF_DISTANCE) {
				continue;
			}
			for (BlockPos neighbor : neighbors26(pos)) {
				if (distance.containsKey(neighbor)) {
					continue;
				}
				if (isNaturalLeaf(level, neighbor)) {
					distance.put(neighbor, dist + 1);
					queue.add(neighbor);
				}
			}
		}
		return found;
	}

	private static boolean isNaturalLeaf(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof LeavesBlock)) {
			return false;
		}
		return !state.getValue(LeavesBlock.PERSISTENT);
	}

	/** Especie segun las hojas encontradas; si no se puede determinar, segun el tronco. */
	private static String detectSpecies(ServerLevel level, List<BlockPos> leaves, Block trunkBlock) {
		for (BlockPos leafPos : leaves) {
			Block leafBlock = level.getBlockState(leafPos).getBlock();
			String species = speciesFromId(BuiltInRegistries.BLOCK.getKey(leafBlock), "_leaves");
			if (SPECIES_TO_SAPLING.containsKey(species)) {
				return species;
			}
		}
		return speciesFromId(BuiltInRegistries.BLOCK.getKey(trunkBlock), "_log");
	}

	private static String speciesFromId(Identifier id, String suffix) {
		String path = id.getPath();
		return path.endsWith(suffix) ? path.substring(0, path.length() - suffix.length()) : path;
	}

	/**
	 * Posiciones de la capa mas baja de troncos donde el brote puede sobrevivir.
	 * Si forman un cuadrado 2x2 exacto, se devuelven las 4; si no, solo la mejor candidata.
	 */
	private static List<BlockPos> findSaplingPositions(ServerLevel level, List<BlockPos> logs, Block saplingBlock) {
		int minY = logs.stream().mapToInt(BlockPos::getY).min().orElseThrow();
		List<BlockPos> baseCandidates = new ArrayList<>();
		for (BlockPos pos : logs) {
			if (pos.getY() == minY && saplingBlock.defaultBlockState().canSurvive(level, pos)) {
				baseCandidates.add(pos);
			}
		}

		if (baseCandidates.size() >= 4) {
			for (BlockPos p : baseCandidates) {
				BlockPos east = p.offset(1, 0, 0);
				BlockPos south = p.offset(0, 0, 1);
				BlockPos southEast = p.offset(1, 0, 1);
				if (baseCandidates.contains(east) && baseCandidates.contains(south) && baseCandidates.contains(southEast)) {
					return List.of(p, east, south, southEast);
				}
			}
		}

		return baseCandidates.isEmpty() ? List.of() : List.of(baseCandidates.get(0));
	}

	private static List<BlockPos> neighbors26(BlockPos center) {
		List<BlockPos> result = new ArrayList<>(26);
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (dx == 0 && dy == 0 && dz == 0) {
						continue;
					}
					result.add(center.offset(dx, dy, dz));
				}
			}
		}
		return result;
	}
}
