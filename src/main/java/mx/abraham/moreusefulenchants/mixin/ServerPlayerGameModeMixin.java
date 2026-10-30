package mx.abraham.moreusefulenchants.mixin;

import mx.abraham.moreusefulenchants.logic.DetonateHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Captura la cara (Direction) que el jugador esta minando, para que Detonate
 * pueda orientar el area 3x3 segun esa cara.
 */
@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
	@Shadow
	public ServerPlayer player;

	@Inject(method = "handleBlockBreakAction", at = @At("HEAD"))
	private void moreusefulenchants$captureMinedFace(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int maxBuildHeight, int sequence, CallbackInfo ci) {
		if (action == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
			DetonateHandler.setLastMinedFace(this.player, direction);
		}
	}
}
