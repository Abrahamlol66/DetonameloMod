package mx.abraham.moreusefulenchants.logic;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Logica del encantamiento Detonate: mina un area 3x3 del mismo bloque alrededor del bloque
 * roto, y continua 2 capas mas hacia adentro (opuesto a la cara minada) para que el area
 * total sea un cubo de 3x3x3 (3 de ancho, 3 de alto, 3 de profundidad).
 */
public final class DetonateHandler {
	/** Capas de profundidad: la del bloque minado (0) + 2 extra. */
	private static final int DEPTH_LAYERS = 3;

	private static final Map<UUID, Direction> LAST_MINED_FACE = new HashMap<>();

	private DetonateHandler() {
	}

	/** Llamado desde el mixin de ServerPlayerGameMode cuando el jugador empieza a minar un bloque. */
	public static void setLastMinedFace(ServerPlayer player, Direction direction) {
		LAST_MINED_FACE.put(player.getUUID(), direction);
	}

	/** Llamado desde PlayerBlockBreakEvents.AFTER (solo servidor). */
	public static void afterBlockBreak(Level level, Player player, BlockPos centerPos, BlockState centerState, BlockEntity blockEntity) {
		if (BreakGuard.isBreakingExtra()) {
			return;
		}
		if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		// Detonate solo cuenta si la herramienta en la mano principal lo tiene.
		ItemStack tool = serverPlayer.getMainHandItem();
		int detonateLevel = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.DETONATE, tool);
		if (detonateLevel <= 0) {
			return;
		}

		Direction minedFace = LAST_MINED_FACE.remove(serverPlayer.getUUID());
		if (minedFace == null) {
			return;
		}

		Block centerBlock = centerState.getBlock();
		ServerPlayerGameMode gameMode = serverPlayer.gameMode;

		for (BlockPos extraPos : cubeOffsets(centerPos, minedFace)) {
			if (serverPlayer.getMainHandItem().isEmpty()) {
				// La herramienta se rompio a la mitad del area: detenemos el resto.
				break;
			}

			BlockState extraState = serverLevel.getBlockState(extraPos);
			if (extraState.isAir() || extraState.getBlock() != centerBlock) {
				continue;
			}
			if (extraState.getDestroySpeed(serverLevel, extraPos) < 0.0f) {
				// Bloque irrompible (p. ej. bedrock).
				continue;
			}
			if (!serverPlayer.mayBuild() || !serverPlayer.mayInteract(serverLevel, extraPos)) {
				continue;
			}
			if (!serverPlayer.getMainHandItem().isCorrectToolForDrops(extraState)) {
				continue;
			}

			BreakGuard.runGuarded(() -> gameMode.destroyBlock(extraPos));
		}
	}

	/**
	 * Todas las posiciones del cubo 3x3x3 alrededor del centro (menos el propio centro, que ya
	 * se rompio por vanilla): la capa 0 es el plano perpendicular a la cara minada (3x3, sin el
	 * centro), y las capas 1 y 2 repiten ese mismo plano 1 y 2 bloques hacia adentro (direccion
	 * opuesta a la cara minada).
	 */
	private static List<BlockPos> cubeOffsets(BlockPos center, Direction minedFace) {
		List<BlockPos> positions = new ArrayList<>(26);
		Direction.Axis axis = minedFace.getAxis();
		Direction depthDirection = minedFace.getOpposite();

		for (int depth = 0; depth < DEPTH_LAYERS; depth++) {
			BlockPos layerCenter = center.relative(depthDirection, depth);
			for (int a = -1; a <= 1; a++) {
				for (int b = -1; b <= 1; b++) {
					if (depth == 0 && a == 0 && b == 0) {
						// El bloque central ya se rompio por la accion normal de minar.
						continue;
					}
					BlockPos pos = switch (axis) {
						case Y -> layerCenter.offset(a, 0, b);
						case Z -> layerCenter.offset(a, b, 0);
						case X -> layerCenter.offset(0, b, a);
					};
					positions.add(pos);
				}
			}
		}
		return positions;
	}
}
