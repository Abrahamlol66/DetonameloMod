package mx.abraham.detonamelo.enchantment;

import mx.abraham.detonamelo.Detonamelo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.List;

/** Claves y helpers para los encantamientos data-driven del mod. */
public final class ModEnchantments {
	public static final ResourceKey<Enchantment> DETONATE =
			ResourceKey.create(Registries.ENCHANTMENT, Detonamelo.id("detonate"));
	public static final ResourceKey<Enchantment> TREECAPITATOR =
			ResourceKey.create(Registries.ENCHANTMENT, Detonamelo.id("treecapitator"));
	public static final ResourceKey<Enchantment> VEINMINER =
			ResourceKey.create(Registries.ENCHANTMENT, Detonamelo.id("veinminer"));

	/** Todos los encantamientos del mod, usado por el mixin de yunque para validarlos a todos. */
	public static final List<ResourceKey<Enchantment>> ALL = List.of(DETONATE, TREECAPITATOR, VEINMINER);

	private ModEnchantments() {
	}

	/** Nivel del encantamiento en el stack, usando el registry access del nivel actual. */
	public static int getLevel(RegistryAccess registryAccess, ResourceKey<Enchantment> key, ItemStack stack) {
		return EnchantmentHelper.getItemEnchantmentLevel(getHolder(registryAccess, key), stack);
	}

	/** Holder del encantamiento, usando el registry access del nivel actual. */
	public static Holder<Enchantment> getHolder(RegistryAccess registryAccess, ResourceKey<Enchantment> key) {
		HolderLookup.RegistryLookup<Enchantment> lookup = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);
		return lookup.getOrThrow(key);
	}
}
