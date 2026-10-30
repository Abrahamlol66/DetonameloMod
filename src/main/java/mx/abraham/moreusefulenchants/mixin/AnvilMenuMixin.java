package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla deja aplicar cualquier encantamiento a cualquier item via yunque cuando el jugador
 * esta en modo creativo o cuando el item base es un libro (AnvilMenu.createResult fuerza
 * compatible=true en esos casos, ignorando supported_items). Para los encantamientos del mod
 * queremos que la restriccion de herramienta se respete siempre, asi que invalidamos el
 * resultado si de todos modos termino con alguno de ellos en un item que no soporta.
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
	@Shadow
	private DataSlot cost;

	@Inject(method = "createResult", at = @At("RETURN"))
	private void moreusefulenchants$enforceToolRestrictions(CallbackInfo ci) {
		ItemCombinerMenuAccessor self = (ItemCombinerMenuAccessor) this;
		ResultContainer resultSlots = self.moreusefulenchants$getResultSlots();
		Player player = self.moreusefulenchants$getPlayer();

		ItemStack result = resultSlots.getItem(0);
		if (result.isEmpty()) {
			return;
		}

		RegistryAccess registryAccess = player.level().registryAccess();
		for (net.minecraft.resources.ResourceKey<Enchantment> key : ModEnchantments.ALL) {
			if (hasUnsupportedEnchant(registryAccess, key, result)) {
				resultSlots.setItem(0, ItemStack.EMPTY);
				this.cost.set(0);
				return;
			}
		}
	}

	private static boolean hasUnsupportedEnchant(RegistryAccess registryAccess, net.minecraft.resources.ResourceKey<Enchantment> key, ItemStack result) {
		Holder<Enchantment> holder = ModEnchantments.getHolder(registryAccess, key);
		return ModEnchantments.getLevel(registryAccess, key, result) > 0 && !holder.value().isSupportedItem(result);
	}
}
