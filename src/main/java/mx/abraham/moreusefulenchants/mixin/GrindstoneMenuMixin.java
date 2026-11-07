package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.item.ModItems;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * GrindstoneMenu.removeNonCursesFrom(...) solo revisa item.is(Items.ENCHANTED_BOOK)
 * para decidir si, tras quitarle los encantamientos (menos maldiciones), el libro debe
 * volver a ser un libro normal. Sin este redirect, un libro encantado del mod vaciado
 * en la piedra de afilar se quedaria como libro del mod vacio en vez de libro normal.
 *
 * ItemStack.is(Item) en realidad es el default generico TypedInstance.is(T), que erasa
 * a is(Object) en bytecode (confirmado con javap sobre el jar real) - por eso el target
 * del redirect es Object y no Item.
 */
@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin {
	@Redirect(method = "removeNonCursesFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
	private boolean moreusefulenchants$isAnyEnchantedBook(final ItemStack stack, final Object item) {
		return stack.is(Items.ENCHANTED_BOOK) || stack.is(ModItems.ENCHANTED_BOOK);
	}
}
