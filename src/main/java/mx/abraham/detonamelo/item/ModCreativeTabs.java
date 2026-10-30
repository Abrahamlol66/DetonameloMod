package mx.abraham.detonamelo.item;

import mx.abraham.detonamelo.Detonamelo;
import mx.abraham.detonamelo.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Pestana propia en el menu creativo con libros encantados de regalo (uno por cada
 * encantamiento del mod), para que Detonamelo tenga su propia seccion como cualquier otro mod.
 */
public final class ModCreativeTabs {
	public static final ResourceKey<CreativeModeTab> DETONAMELO =
			ResourceKey.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, Detonamelo.id("detonamelo"));

	private ModCreativeTabs() {
	}

	public static void register() {
		CreativeModeTab tab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
				.title(Component.translatable("itemGroup.detonamelo"))
				.icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
				.displayItems((parameters, output) -> {
					output.accept(enchantedBook(parameters.holders(), ModEnchantments.DETONATE));
					output.accept(enchantedBook(parameters.holders(), ModEnchantments.TREECAPITATOR));
					output.accept(enchantedBook(parameters.holders(), ModEnchantments.VEINMINER));
				})
				.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, DETONAMELO, tab);
	}

	private static ItemStack enchantedBook(net.minecraft.core.HolderLookup.Provider holders, ResourceKey<Enchantment> key) {
		Holder<Enchantment> holder = holders.lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(key);
		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		enchantments.set(holder, 1);
		book.set(net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
		return book;
	}
}
