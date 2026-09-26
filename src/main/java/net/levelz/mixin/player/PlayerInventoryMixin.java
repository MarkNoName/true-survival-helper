package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerBreakBlockAccess;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin implements PlayerBreakBlockAccess {
	@Shadow
	@Mutable
	@Final
	public Player player;

	@Shadow
	@Mutable
	@Final
	public NonNullList<ItemStack> items;

	@Shadow
	public int selected;

	public boolean canBreakBlock = true;

	public float blockBreakExtraDelta = 1.0F;

	@Inject(method = "getDestroySpeed", at = @At(value = "HEAD"), cancellable = true)
	private void getBlockBreakingSpeedMixin(BlockState block, CallbackInfoReturnable<Float> info) {
		if (!this.canBreakBlock)
			info.setReturnValue(1.0F);
	}

	@Override
	public void setInventoryBlockBreakable(boolean breakable) {
		this.canBreakBlock = breakable;
	}

	@Override
	public void setAbstractBlockBreakDelta(float breakingDelta) {
		this.blockBreakExtraDelta = breakingDelta;
	}

	@Override
	public float getBreakingAbstractBlockDelta() {
		return this.blockBreakExtraDelta;
	}
}
