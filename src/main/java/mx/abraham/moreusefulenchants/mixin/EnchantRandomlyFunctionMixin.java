package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.item.EnchantedBookUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EnchantRandomlyFunction es la loot function de "encantar al azar" (pesca, cofres):
 * convierte un libro normal en minecraft:enchanted_book y le mete el encantamiento
 * sorteado. Si ese encantamiento es del mod (ej. max_health via on_random_loot), el
 * resultado debe quedar como el libro del mod.
 */
@Mixin(EnchantRandomlyFunction.class)
public abstract class EnchantRandomlyFunctionMixin {
	@Inject(method = "enchantItem", at = @At("RETURN"), cancellable = true)
	private void moreusefulenchants$upgradeEnchantedBook(
			final ItemStack itemStack, final Holder<Enchantment> enchantment, final LootContext context,
			final CallbackInfoReturnable<ItemStack> cir) {
		ItemStack result = cir.getReturnValue();
		ItemStack normalized = EnchantedBookUtil.normalize(result);
		if (normalized != result) {
			cir.setReturnValue(normalized);
		}
	}
}
