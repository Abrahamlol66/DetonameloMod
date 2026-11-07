package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.item.EnchantedBookUtil;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Migracion de libros vanilla con encantamientos del mod: Container.startOpen(...) lo
 * llaman cofres, barriles, shulker boxes, dispensadores, etc. justo al abrirse (ver
 * ChestMenu/ShulkerBoxMenu/HopperMenu/DispenserMenu/CrafterMenu), asi que es el punto
 * barato para revisar sus slots una sola vez por apertura en vez de cada tick.
 */
@Mixin(Container.class)
public interface ContainerMixin {
	@Inject(method = "startOpen", at = @At("HEAD"))
	private void moreusefulenchants$migrateBooksOnOpen(final ContainerUser containerUser, final CallbackInfo ci) {
		Container self = (Container) this;
		int size = self.getContainerSize();
		for (int slot = 0; slot < size; slot++) {
			ItemStack current = self.getItem(slot);
			ItemStack normalized = EnchantedBookUtil.normalize(current);
			if (normalized != current) {
				self.setItem(slot, normalized);
			}
		}
	}
}
