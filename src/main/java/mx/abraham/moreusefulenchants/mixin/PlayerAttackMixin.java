package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.logic.LifestealHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Player.attack() resetea el cooldown de ataque (onAttack -> resetOnlyAttackStrengthTicker)
 * ANTES de aplicar el daño, asi que para cuando ServerLivingEntityEvents.AFTER_DAMAGE se
 * dispara, getAttackStrengthScale() ya esta en 0 sin importar que tan cargado estaba el golpe.
 * Este mixin captura el valor real justo al entrar al metodo, antes de ese reset, para que
 * Lifesteal pueda leerlo despues.
 */
@Mixin(Player.class)
public abstract class PlayerAttackMixin {
	@Inject(method = "attack", at = @At("HEAD"))
	private void moreusefulenchants$captureAttackStrength(Entity entity, CallbackInfo ci) {
		Player player = (Player) (Object) this;
		LifestealHandler.recordAttackStrength(player, player.getAttackStrengthScale(0.5f));
	}
}
