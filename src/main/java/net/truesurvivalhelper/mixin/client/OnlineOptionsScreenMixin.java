package net.truesurvivalhelper.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(OnlineOptionsScreen.class)
public abstract class OnlineOptionsScreenMixin {
	@Redirect(method = "createOnlineOptionsScreen", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private static boolean tsh$skipRealmsNotificationsOption(List<Object> list, Object value, Minecraft minecraft, Screen screen, Options options) {
		if (value == options.realmsNotifications()) {
			return true;
		}
		return list.add(value);
	}
}
