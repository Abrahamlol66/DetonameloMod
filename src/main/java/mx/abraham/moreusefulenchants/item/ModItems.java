package mx.abraham.moreusefulenchants.item;

import mx.abraham.moreusefulenchants.MoreUsefulEnchants;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Items propios del mod. ENCHANTED_BOOK copia exactamente las Properties del libro
 * encantado vanilla (ver Items.ENCHANTED_BOOK en el jar de 26.2: stacksTo(1), rarity
 * RARE, STORED_ENCHANTMENTS vacio, ENCHANTMENT_GLINT_OVERRIDE true) pero con su propio
 * namespace/registro, de modo que REI/EMI/JEI y los mixins de este mod puedan
 * distinguirlo de minecraft:enchanted_book.
 */
public final class ModItems {
	public static final ResourceKey<Item> ENCHANTED_BOOK_KEY =
			ResourceKey.create(Registries.ITEM, MoreUsefulEnchants.id("enchanted_book"));

	public static final Item ENCHANTED_BOOK = Registry.register(
			BuiltInRegistries.ITEM,
			ENCHANTED_BOOK_KEY,
			new Item(
					new Item.Properties()
							.setId(ENCHANTED_BOOK_KEY)
							.stacksTo(1)
							.rarity(Rarity.RARE)
							.component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
							.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
			)
	);

	private ModItems() {
	}

	/** Fuerza la carga de la clase (y por tanto el registro del item) desde el entrypoint. */
	public static void register() {
	}
}
