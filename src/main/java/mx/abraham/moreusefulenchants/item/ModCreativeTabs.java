package mx.abraham.moreusefulenchants.item;

import mx.abraham.moreusefulenchants.MoreUsefulEnchants;
import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Pestana propia en el menu creativo con libros encantados de regalo (uno por cada
 * encantamiento del mod, a su nivel maximo), para que MoreUsefulEnchants tenga su propia
 * seccion como cualquier otro mod.
 */
public final class ModCreativeTabs {
	public static final ResourceKey<CreativeModeTab> MOREUSEFULENCHANTS =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, MoreUsefulEnchants.id("moreusefulenchants"));

	private ModCreativeTabs() {
	}

	public static void register() {
		CreativeModeTab tab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
				.title(Component.translatable("itemGroup.moreusefulenchants"))
				.icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
				.displayItems((parameters, output) -> {
					for (ResourceKey<Enchantment> key : ModEnchantments.ALL) {
						output.accept(enchantedBook(parameters.holders(), key));
					}
				})
				.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MOREUSEFULENCHANTS, tab);
	}

	private static ItemStack enchantedBook(HolderLookup.Provider holders, ResourceKey<Enchantment> key) {
		Holder<Enchantment> holder = holders.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		enchantments.set(holder, holder.value().getMaxLevel());
		book.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
		return book;
	}
}
