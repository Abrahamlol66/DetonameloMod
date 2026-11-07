package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.item.EnchantedBookUtil;
import mx.abraham.moreusefulenchants.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

/**
 * EnchantmentHelper.getComponentType(ItemStack) es el chokepoint real de lectura/
 * escritura de encantamientos (canStoreEnchantments, setEnchantments,
 * getEnchantmentsForCrafting, ItemStack.enchant(...) pasan por aqui): decide si un
 * stack guarda sus encantamientos en STORED_ENCHANTMENTS (forma de libro) o en
 * ENCHANTMENTS (item normal) comparando contra Items.ENCHANTED_BOOK. Sin este mixin,
 * moreusefulenchants:enchanted_book usaria el componente equivocado y todo lo demas
 * (yunque, mesa de encantamiento, piedra de afilar) fallaria para el.
 *
 * Tambien ajustamos enchantItem(...), la logica real de "tirar encantamientos al azar"
 * que usan la mesa de encantamiento (via EnchantmentMenu) y ciertas loot tables: si el
 * resultado termino siendo un libro con al menos un encantamiento del mod, lo sube al
 * libro del mod.
 */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
	@Inject(method = "getComponentType", at = @At("HEAD"), cancellable = true)
	private static void moreusefulenchants$useStoredEnchantmentsForModBook(
			final ItemStack itemStack, final CallbackInfoReturnable<DataComponentType<ItemEnchantments>> cir) {
		if (itemStack.is(ModItems.ENCHANTED_BOOK)) {
			cir.setReturnValue(DataComponents.STORED_ENCHANTMENTS);
		}
	}

	@Inject(method = "enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"), cancellable = true)
	private static void moreusefulenchants$upgradeEnchantedBook(
			final RandomSource random, final ItemStack itemStack, final int enchantmentCost,
			final Stream<Holder<Enchantment>> source, final CallbackInfoReturnable<ItemStack> cir) {
		ItemStack result = cir.getReturnValue();
		ItemStack normalized = EnchantedBookUtil.normalize(result);
		if (normalized != result) {
			cir.setReturnValue(normalized);
		}
	}
}
