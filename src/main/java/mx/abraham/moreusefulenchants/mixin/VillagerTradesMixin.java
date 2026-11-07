package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.item.ModItems;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.VillagerTrades;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * VillagerTrades.enchantedBook(...) (las dos sobrecargas que arman los comercios de
 * bibliotecario de "esmeralda + libro -> libro encantado") construyen un
 * FilteredFunction que descarta el resultado si no es exactamente minecraft:enchanted_book.
 * Una vez que EnchantRandomlyFunctionMixin/SetEnchantmentsFunctionMixin suben el libro a
 * moreusefulenchants:enchanted_book cuando el encantamiento es del mod, ese filtro lo
 * descartaria por completo. Este mixin hace que el filtro acepte ambos items.
 */
@Mixin(VillagerTrades.class)
public abstract class VillagerTradesMixin {
	@Redirect(
			method = "enchantedBook(Lnet/minecraft/core/HolderGetter;Lnet/minecraft/core/HolderSet;)Ljava/util/List;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/predicates/ItemPredicate$Builder;of(Lnet/minecraft/core/HolderGetter;[Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/advancements/predicates/ItemPredicate$Builder;")
	)
	private static ItemPredicate.Builder moreusefulenchants$acceptBothBookTypesRandom(
			final ItemPredicate.Builder builder, final HolderGetter<Item> items, final ItemLike[] original) {
		return builder.of(items, Items.ENCHANTED_BOOK, ModItems.ENCHANTED_BOOK);
	}

	@Redirect(
			method = "enchantedBook(Lnet/minecraft/core/HolderGetter;Lnet/minecraft/core/Holder;I)Ljava/util/List;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/predicates/ItemPredicate$Builder;of(Lnet/minecraft/core/HolderGetter;[Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/advancements/predicates/ItemPredicate$Builder;")
	)
	private static ItemPredicate.Builder moreusefulenchants$acceptBothBookTypesFixed(
			final ItemPredicate.Builder builder, final HolderGetter<Item> items, final ItemLike[] original) {
		return builder.of(items, Items.ENCHANTED_BOOK, ModItems.ENCHANTED_BOOK);
	}
}
