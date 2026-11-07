package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Stream;

/**
 * CreativeModeTabs.generateEnchantmentBookTypesOnlyMaxLevel/AllLevels generan
 * automaticamente un minecraft:enchanted_book por cada encantamiento registrado
 * (incluidos los del mod) en la pestana de Ingredientes. Como el mod tiene su propia
 * pestana con su propio libro (moreusefulenchants:enchanted_book, ver ModCreativeTabs),
 * se filtran aqui los encantamientos del mod para que no salgan duplicados con el
 * namespace vanilla incorrecto.
 */
@Mixin(CreativeModeTabs.class)
public abstract class CreativeModeTabsMixin {
	@Redirect(
			method = {
					"generateEnchantmentBookTypesOnlyMaxLevel",
					"generateEnchantmentBookTypesAllLevels"
			},
			at = @At(value = "INVOKE", target = "Lnet/minecraft/core/HolderLookup;listElements()Ljava/util/stream/Stream;")
	)
	private static Stream<Holder.Reference<Enchantment>> moreusefulenchants$excludeModEnchantments(final HolderLookup<Enchantment> enchantments) {
		return enchantments.listElements().filter(holder -> holder.unwrapKey().filter(ModEnchantments.ALL::contains).isEmpty());
	}
}
