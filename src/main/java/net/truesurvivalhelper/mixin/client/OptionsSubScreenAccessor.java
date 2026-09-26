package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionsSubScreen.class)
public interface OptionsSubScreenAccessor {
	@Accessor("options")
	Options guibackport$options();

	@Accessor("lastScreen")
	Screen guibackport$lastScreen();
}
