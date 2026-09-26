package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.stats.PlayerStatsManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(CraftingMenu.class)
public class CraftingScreenHandlerMixin {
	@Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private static void updateResultMixin(AbstractContainerMenu handler, Level world, Player player, CraftingContainer craftingInventory, ResultContainer resultInventory, CallbackInfo info,
			ServerPlayer serverPlayerEntity, ItemStack itemStack) {
		if (!player.isCreative() && PlayerStatsManager.listContainsItemOrBlock(player, BuiltInRegistries.ITEM.getId(itemStack.getItem()), 4)) {
			resultInventory.setItem(0, ItemStack.EMPTY);
			handler.setRemoteSlot(0, ItemStack.EMPTY);
			serverPlayerEntity.connection.send(new ClientboundContainerSetSlotPacket(handler.containerId, handler.incrementStateId(), 0, ItemStack.EMPTY));
			info.cancel();
		}
	}
}
