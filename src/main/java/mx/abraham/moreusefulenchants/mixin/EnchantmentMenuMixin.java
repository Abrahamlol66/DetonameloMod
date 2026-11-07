package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.item.EnchantedBookUtil;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Despues de que la mesa de encantamiento convierte un libro normal en
 * minecraft:enchanted_book y le aplica los encantamientos sorteados, si alguno de
 * ellos es del mod el resultado debe quedar como el libro del mod.
 */
@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
	@Shadow
	private Container enchantSlots;

	@Inject(method = "clickMenuButton", at = @At("TAIL"))
	private void moreusefulenchants$upgradeEnchantedBook(final Player player, final int buttonId, final CallbackInfoReturnable<Boolean> cir) {
		ItemStack current = this.enchantSlots.getItem(0);
		ItemStack normalized = EnchantedBookUtil.normalize(current);
		if (normalized != current) {
			this.enchantSlots.setItem(0, normalized);
		}
	}
}
