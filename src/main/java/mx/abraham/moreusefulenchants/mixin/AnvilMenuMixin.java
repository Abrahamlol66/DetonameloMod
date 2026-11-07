package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.enchantment.ModEnchantments;
import mx.abraham.moreusefulenchants.item.EnchantedBookUtil;
import mx.abraham.moreusefulenchants.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla deja aplicar cualquier encantamiento a cualquier item via yunque cuando el jugador
 * esta en modo creativo o cuando el item base es un libro (AnvilMenu.createResult fuerza
 * compatible=true en esos casos, ignorando supported_items). Para los encantamientos del mod
 * queremos que la restriccion de herramienta se respete siempre que el resultado sea una
 * herramienta/arma/armadura real, pero no cuando el resultado es en si un libro (combinar
 * dos libros encantados siempre debe funcionar, igual que en vanilla).
 *
 * Tambien se encarga de que el libro encantado del mod participe en el yunque exactamente
 * igual que minecraft:enchanted_book (bypass de compatibilidad al combinar libros, y subir/
 * bajar la identidad del resultado segun la regla general del mod).
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
	@Shadow
	private DataSlot cost;

	/**
	 * createResult solo tiene una llamada real a ItemStack.is(Object) en el bytecode
	 * compilado: "input.is(Items.ENCHANTED_BOOK)", la que fuerza compatible=true al
	 * combinar libros (confirmado con javap sobre el jar real, no sobre el fuente
	 * decompilado).
	 */
	@Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
	private boolean moreusefulenchants$inputIsAnyEnchantedBook(final ItemStack stack, final Object item) {
		return stack.is(Items.ENCHANTED_BOOK) || stack.is(ModItems.ENCHANTED_BOOK);
	}

	@Inject(method = "createResult", at = @At("RETURN"))
	private void moreusefulenchants$postProcessResult(CallbackInfo ci) {
		ItemCombinerMenuAccessor self = (ItemCombinerMenuAccessor) this;
		ResultContainer resultSlots = self.moreusefulenchants$getResultSlots();
		Player player = self.moreusefulenchants$getPlayer();

		ItemStack result = resultSlots.getItem(0);
		if (result.isEmpty()) {
			return;
		}

		if (EnchantedBookUtil.isBookShaped(result.getItem())) {
			ItemStack normalized = EnchantedBookUtil.normalize(result);
			if (normalized != result) {
				resultSlots.setItem(0, normalized);
			}
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
