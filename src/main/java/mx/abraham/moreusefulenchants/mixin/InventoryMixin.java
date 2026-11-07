package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.item.EnchantedBookUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Migracion de libros vanilla con encantamientos del mod al entrar al inventario del
 * jugador (recoger del suelo, resultado de crafteo/yunque/piedra de afilar tomado con
 * el mouse, trueque con aldeanos, etc.): Inventory.add(int, ItemStack) es el punto de
 * entrada real (add(ItemStack) solo delega a el).
 */
@Mixin(Inventory.class)
public abstract class InventoryMixin {
	@ModifyVariable(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), argsOnly = true)
	private ItemStack moreusefulenchants$normalizeBeforeAdd(final ItemStack itemStack) {
		return EnchantedBookUtil.normalize(itemStack);
	}
}
