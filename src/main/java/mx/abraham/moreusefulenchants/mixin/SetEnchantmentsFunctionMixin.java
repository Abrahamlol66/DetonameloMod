package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.item.EnchantedBookUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * SetEnchantmentsFunction fija encantamientos especificos (usada por los comercios de
 * aldeanos bibliotecarios con encantamiento fijo y por loot tables con un encantamiento
 * puntual). Si alguno de los fijados es del mod, el libro resultante debe ser el libro
 * del mod.
 */
@Mixin(SetEnchantmentsFunction.class)
public abstract class SetEnchantmentsFunctionMixin {
	@Inject(method = "run", at = @At("RETURN"), cancellable = true)
	private void moreusefulenchants$upgradeEnchantedBook(
			final ItemStack itemStack, final LootContext context, final CallbackInfoReturnable<ItemStack> cir) {
		ItemStack result = cir.getReturnValue();
		ItemStack normalized = EnchantedBookUtil.normalize(result);
		if (normalized != result) {
			cir.setReturnValue(normalized);
		}
	}
}
