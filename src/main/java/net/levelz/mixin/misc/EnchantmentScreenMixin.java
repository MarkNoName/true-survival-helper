package net.levelz.mixin.misc;

import java.util.List;

import com.google.common.collect.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.init.RenderInit;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends AbstractContainerScreen<EnchantmentMenu> {
	public EnchantmentScreenMixin(EnchantmentMenu handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
	}

	@Inject(method = "renderBg", at = @At(value = "TAIL"))
	protected void drawBackgroundMixin(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo info) {
		if (LevelLists.enchantingTableList != null && !LevelLists.enchantingTableList.isEmpty()) {
			int i = (this.width - this.imageWidth) / 2;
			int j = (this.height - this.imageHeight) / 2;
			if (this.isHovering(176, 0, 11, 13, (double) mouseX, (double) mouseY)) {
				context.blit(RenderInit.GUI_ICONS, i + 176, j, 33, 64, 11, 13);
				List<Component> list = Lists.newArrayList();
				String skill = Language.getInstance().getOrDefault("spritetip.levelz.%s_skill".formatted(LevelLists.enchantingTableList.get(0)));
				list.add((Component.translatable("container.levelz.enchanting_tier", 1, skill, LevelLists.enchantingTableList.get(4))).withStyle(ChatFormatting.WHITE));
				list.add((Component.translatable("container.levelz.enchanting_tier", 2, skill, LevelLists.enchantingTableList.get(5))).withStyle(ChatFormatting.WHITE));
				list.add((Component.translatable("container.levelz.enchanting_tier", 3, skill, LevelLists.enchantingTableList.get(6))).withStyle(ChatFormatting.WHITE));
				context.renderComponentTooltip(this.font, list, mouseX, mouseY);
			} else {
				context.blit(RenderInit.GUI_ICONS, i + 176, j, 22, 64, 11, 13);
			}
		}
	}
}
