package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
	@Accessor("children")
	List<GuiEventListener> tsh$children();

	@Accessor("renderables")
	List<Renderable> tsh$renderables();

	@Accessor("narratables")
	List<NarratableEntry> tsh$narratables();

	@Accessor("width")
	int tsh$width();

	@Accessor("minecraft")
	Minecraft tsh$minecraft();

	@Accessor("minecraft")
	Minecraft guibackport$minecraft();

	@Invoker("addRenderableWidget")
	<T extends GuiEventListener & Renderable & NarratableEntry> T guibackport$addRenderableWidget(T widget);
}
