package mx.abraham.moreusefulenchants.logic;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Logica del encantamiento Replenish: al romper un cultivo maduro con azada, descuenta 1
 * semilla de los drops (o del inventario si no venia en los drops) y vuelve a plantar el
 * cultivo en edad 0. Romper un cultivo NO maduro se cancela, salvo agachado.
 */
public final class ReplenishHandler {
	private static final Map<Block, Item> CROP_TO_SEED = new HashMap<>();
	/** Si se consiguio la semilla (de los drops o del inventario) para replantar. */
	private static final Map<UUID, Boolean> SEED_AVAILABLE = new HashMap<>();

	static {
		CROP_TO_SEED.put(Blocks.WHEAT, Items.WHEAT_SEEDS);
		CROP_TO_SEED.put(Blocks.CARROTS, Items.CARROT);
		CROP_TO_SEED.put(Blocks.POTATOES, Items.POTATO);
		CROP_TO_SEED.put(Blocks.BEETROOTS, Items.BEETROOT_SEEDS);
		CROP_TO_SEED.put(Blocks.NETHER_WART, Items.NETHER_WART);
		CROP_TO_SEED.put(Blocks.COCOA, Items.COCOA_BEANS);
	}

	private ReplenishHandler() {
	}

	/** Edad del cultivo en este estado, o -1 si no es uno de los cultivos soportados. */
	static int getAge(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof CropBlock crop) {
			return crop.getAge(state);
		}
		if (block == Blocks.NETHER_WART) {
			return state.getValue(NetherWartBlock.AGE);
		}
		if (block == Blocks.COCOA) {
			return state.getValue(CocoaBlock.AGE);
		}
		return -1;
	}

	static int getMaxAge(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof CropBlock crop) {
			return crop.getMaxAge();
		}
		if (block == Blocks.NETHER_WART) {
			return NetherWartBlock.MAX_AGE;
		}
		if (block == Blocks.COCOA) {
			return CocoaBlock.MAX_AGE;
		}
		return -1;
	}

	static BlockState replantedState(BlockState matureState) {
		Block block = matureState.getBlock();
		if (block instanceof CropBlock crop) {
			return crop.getStateForAge(0);
		}
		if (block == Blocks.NETHER_WART) {
			return matureState.setValue(NetherWartBlock.AGE, 0);
		}
		if (block == Blocks.COCOA) {
			return matureState.setValue(CocoaBlock.AGE, 0);
		}
		return matureState;
	}

	/** Protege los cultivos no maduros de romperse con Replenish, salvo agachado. */
	public static boolean beforeBlockBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (player.isShiftKeyDown() || !(level instanceof ServerLevel serverLevel)) {
			return true;
		}
		int age = getAge(state);
		if (age < 0) {
			return true;
		}

		ItemStack tool = player.getMainHandItem();
		int level0 = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.REPLENISH, tool);
		if (level0 <= 0) {
			return true;
		}

		int maxAge = getMaxAge(state);
		return age >= maxAge;
	}

	/** Descuenta 1 semilla de los drops (antes de que se spawneen) para Replenish. */
	public static void modifyDrops(Holder<net.minecraft.world.level.storage.loot.LootTable> lootTable, LootContext context, List<ItemStack> drops) {
		if (!(context.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}
		if (!(context.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player)) {
			return;
		}
		BlockState state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
		if (state == null) {
			return;
		}
		int age = getAge(state);
		if (age < 0 || age < getMaxAge(state)) {
			return;
		}

		ItemStack tool = player.getMainHandItem();
		if (ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.REPLENISH, tool) <= 0) {
			return;
		}

		Item seed = CROP_TO_SEED.get(state.getBlock());
		if (seed == null) {
			return;
		}

		for (int i = 0; i < drops.size(); i++) {
			ItemStack drop = drops.get(i);
			if (drop.is(seed) && drop.getCount() > 0) {
				drop.shrink(1);
				SEED_AVAILABLE.put(player.getUUID(), Boolean.TRUE);
				return;
			}
		}

		// No venia en los drops: toma 1 semilla del inventario si hay.
		int removed = player.getInventory().clearOrCountMatchingItems(stack -> stack.is(seed), 1, player.getInventory());
		SEED_AVAILABLE.put(player.getUUID(), removed > 0);
	}

	/** Replanta el cultivo en edad 0, despues de que ya se rompio y se procesaron los drops. */
	public static void afterBlockBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		int age = getAge(state);
		if (age < getMaxAge(state) || age < 0) {
			return;
		}

		ItemStack tool = serverPlayer.getMainHandItem();
		if (ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.REPLENISH, tool) <= 0) {
			return;
		}

		Item seed = CROP_TO_SEED.get(state.getBlock());
		if (seed == null) {
			return;
		}

		// La semilla ya se desconto en modifyDrops (de los drops o del inventario). Si no habia
		// ninguna disponible, no replanta.
		Boolean hadSeed = SEED_AVAILABLE.remove(serverPlayer.getUUID());
		if (hadSeed == null || !hadSeed) {
			return;
		}

		serverLevel.setBlockAndUpdate(pos, replantedState(state));
	}
}
