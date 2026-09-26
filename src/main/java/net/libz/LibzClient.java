package net.libz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.libz.api.*;
import net.libz.network.LibzClientPacket;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class LibzClient implements ClientModInitializer {
	public static final List<InventoryTab> inventoryTabs = new ArrayList<InventoryTab>();
	public static final HashMap<Class<?>, List<InventoryTab>> otherTabs = new HashMap<Class<?>, List<InventoryTab>>();

	public static final ResourceLocation tabTexture = new ResourceLocation("libz:textures/gui/icons.png");

	@Override
	public void onInitializeClient() {
		LibzClientPacket.init();
	}
}
