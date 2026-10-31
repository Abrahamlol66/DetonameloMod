package mx.abraham.moreusefulenchants.logic;

import mx.abraham.moreusefulenchants.MoreUsefulEnchants;
import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;
import java.util.Optional;

/**
 * Logica del encantamiento Auto-Smelt: reemplaza cada drop de bloque que tenga receta de
 * fundicion por su resultado (misma cantidad) y otorga la XP de esa receta. Se enlaza a
 * LootTableEvents.MODIFY_DROPS en vez de PlayerBlockBreakEvents para que aplique igual a
 * bloques rotos directamente, por Detonate o por Veinminer (todos pasan por la misma
 * resolucion de loot table).
 */
public final class AutoSmeltHandler {
	public static final TagKey<Block> BLACKLIST = TagKey.create(Registries.BLOCK, MoreUsefulEnchants.id("auto_smelt_blacklist"));

	private AutoSmeltHandler() {
	}

	public static void modifyDrops(Holder<net.minecraft.world.level.storage.loot.LootTable> lootTable, LootContext context, List<ItemStack> drops) {
		if (!(context.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}
		Entity breaker = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
		if (!(breaker instanceof ServerPlayer player)) {
			return;
		}
		BlockState blockState = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
		if (blockState == null || blockState.is(BLACKLIST)) {
			return;
		}

		ItemStack tool = player.getMainHandItem();
		int level = ModEnchantments.getLevel(serverLevel.registryAccess(), ModEnchantments.AUTO_SMELT, tool);
		if (level <= 0) {
			return;
		}

		RecipeManager recipeManager = serverLevel.recipeAccess();
		float totalExperience = 0.0f;

		for (int i = 0; i < drops.size(); i++) {
			ItemStack drop = drops.get(i);
			if (drop.isEmpty()) {
				continue;
			}
			Optional<RecipeHolder<SmeltingRecipe>> recipe =
					recipeManager.getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(new ItemStack(drop.getItem())), serverLevel);
			if (recipe.isEmpty()) {
				continue;
			}

			SmeltingRecipe smeltingRecipe = recipe.get().value();
			ItemStack smelted = smeltingRecipe.assemble(new SingleRecipeInput(new ItemStack(drop.getItem())));
			if (smelted.isEmpty()) {
				continue;
			}

			totalExperience += smeltingRecipe.experience() * drop.getCount();

			ItemStack replacement = smelted.copyWithCount(drop.getCount());
			drops.set(i, replacement);
		}

		if (totalExperience > 0.0f) {
			player.giveExperiencePoints(Math.round(totalExperience));
		}
	}
}
