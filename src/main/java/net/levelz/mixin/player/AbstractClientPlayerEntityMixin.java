package net.levelz.mixin.player;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerListAccess;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin implements PlayerListAccess {
	@Shadow
	@Nullable
	protected PlayerInfo getPlayerInfo() {
		return null;
	}

	@Override
	public int getLevel() {
		if (getPlayerInfo() != null) {
			return ((PlayerListAccess) getPlayerInfo()).getLevel();
		}
		return 0;
	}
}
