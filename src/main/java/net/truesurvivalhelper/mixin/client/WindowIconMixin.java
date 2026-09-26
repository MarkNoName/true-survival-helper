package net.truesurvivalhelper.mixin.client;

import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.truesurvivalhelper.TshPackIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Mixin(Window.class)
public abstract class WindowIconMixin {
	@Redirect(
			method = "setIcon",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/IconSet;getStandardIcons(Lnet/minecraft/server/packs/PackResources;)Ljava/util/List;"
			)
	)
	private List<IoSupplier<InputStream>> tsh$standardIcons(IconSet instance, PackResources packResources) throws IOException {
		List<IoSupplier<InputStream>> custom = TshPackIcon.icons();
		return custom != null ? custom : instance.getStandardIcons(packResources);
	}
}
