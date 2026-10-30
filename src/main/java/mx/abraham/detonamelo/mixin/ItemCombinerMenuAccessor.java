package mx.abraham.detonamelo.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * player y resultSlots estan declarados en ItemCombinerMenu (la superclase de AnvilMenu), no
 * en AnvilMenu mismo, asi que @Shadow en un mixin de AnvilMenu no los encuentra. Este accessor
 * mixin los expone para usarlos desde AnvilMenuMixin.
 */
@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {
	@Accessor("player")
	Player detonamelo$getPlayer();

	@Accessor("resultSlots")
	ResultContainer detonamelo$getResultSlots();
}
