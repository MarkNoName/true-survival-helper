package net.libz.api;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class InventoryTab {
	private final Class<?>[] screenClasses;
	private final Component title;
	@Nullable
	private final ResourceLocation texture;
	private final int preferedPos;

	public InventoryTab(Component title, @Nullable ResourceLocation texture, int preferedPos, Class<?>... screenClasses) {
		this.screenClasses = screenClasses;
		this.title = title;
		this.texture = texture;
		this.preferedPos = preferedPos;
	}

	public Component getTitle() {
		return this.title;
	}

	@Nullable
	public ResourceLocation getTexture() {
		return this.texture;
	}

	@Nullable
	public ItemStack getItemStack(Minecraft client) {
		return null;
	}

	public int getPreferedPos() {
		return this.preferedPos;
	}

	public boolean shouldShow(Minecraft client) {
		return true;
	}

	public void onClick(Minecraft client) {
	}

	public boolean canClick(Class<?> screenClass, Minecraft client) {
		return !isSelectedScreen(screenClass);
	}

	public boolean isSelectedScreen(Class<?> screenClass) {
		for (int i = 0; i < screenClasses.length; i++) {
			if (screenClasses[i].equals(screenClass)) {
				return true;
			}
		}
		return false;
	}
}
