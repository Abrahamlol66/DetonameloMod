package mx.abraham.moreusefulenchants.item;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Punto central para decidir que identidad de item (libro normal / libro encantado
 * vanilla / libro encantado del mod) le corresponde a un stack con forma de libro,
 * segun los encantamientos que tenga guardados en STORED_ENCHANTMENTS. Cada mixin que
 * crea o modifica un libro encantado (yunque, piedra de afilar, mesa de encantamiento,
 * loot tables, comercio de aldeanos) llama a {@link #normalize(ItemStack)} al final
 * para aplicar la regla general: si tiene al menos un encantamiento del mod, es el
 * libro del mod; si no, sigue el comportamiento vanilla de siempre.
 */
public final class EnchantedBookUtil {
	private EnchantedBookUtil() {
	}

	/** true si el item es uno de los tres "con forma de libro": normal, vanilla o del mod. */
	public static boolean isBookShaped(final Item item) {
		return item == Items.BOOK || item == Items.ENCHANTED_BOOK || item == ModItems.ENCHANTED_BOOK;
	}

	/** true si el stack es el libro encantado del mod o el vanilla (no el libro normal). */
	public static boolean isEnchantedBook(final ItemStack stack) {
		return stack.is(Items.ENCHANTED_BOOK) || stack.is(ModItems.ENCHANTED_BOOK);
	}

	public static boolean hasModEnchantment(final ItemEnchantments enchantments) {
		for (Holder<Enchantment> holder : enchantments.keySet()) {
			if (holder.unwrapKey().filter(ModEnchantments.ALL::contains).isPresent()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Dado un stack con forma de libro (normal, vanilla o del mod), devuelve el stack con
	 * la identidad correcta segun sus STORED_ENCHANTMENTS actuales: libro normal si no
	 * tiene ninguno, libro del mod si tiene al menos uno del mod, libro vanilla en otro
	 * caso. Si el stack no tiene forma de libro, o ya tiene la identidad correcta, se
	 * devuelve el mismo objeto sin copiar.
	 */
	public static ItemStack normalize(final ItemStack stack) {
		if (!isBookShaped(stack.getItem())) {
			return stack;
		}

		ItemEnchantments enchantments = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
		Item target = enchantments.isEmpty() ? Items.BOOK : hasModEnchantment(enchantments) ? ModItems.ENCHANTED_BOOK : Items.ENCHANTED_BOOK;
		if (stack.getItem() == target) {
			return stack;
		}

		ItemStack result = stack.transmuteCopy(target);
		if (target == Items.BOOK) {
			result.remove(DataComponents.STORED_ENCHANTMENTS);
			result.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
		} else {
			result.set(DataComponents.STORED_ENCHANTMENTS, enchantments);
			result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
		}
		return result;
	}
}
