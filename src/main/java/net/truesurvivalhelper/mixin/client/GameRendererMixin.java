package net.truesurvivalhelper.mixin.client;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.truesurvivalhelper.GuiBackportBlurAccess;
import net.truesurvivalhelper.GuiBackportUniformAccess;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements GuiBackportBlurAccess {
	@Unique
	private static final ResourceLocation GUIBACKPORT_BLUR_LOCATION = new ResourceLocation("shaders/post/guibackport_menu_blur.json");
	@Unique
	private static final float GUIBACKPORT_BLUR_RADIUS = 5.0F;

	@Shadow
	@Final
	private Minecraft minecraft;

	@Unique
	private PostChain guibackport$blurEffect;
	@Unique
	private boolean guibackport$blurredThisFrame;

	@Inject(method = "render(FJZ)V", at = @At("HEAD"))
	private void guibackport$resetBlurGuard(float partialTick, long finishTimeNano, boolean tick, CallbackInfo ci) {
		this.guibackport$blurredThisFrame = false;
	}

	@Inject(method = "reloadShaders", at = @At("RETURN"))
	private void guibackport$loadBlurEffect(ResourceProvider resourceProvider, CallbackInfo ci) {
		if (this.guibackport$blurEffect != null) {
			this.guibackport$blurEffect.close();
			this.guibackport$blurEffect = null;
		}

		try {
			PostChain chain = new PostChain(
				this.minecraft.getTextureManager(),
				this.minecraft.getResourceManager(),
				this.minecraft.getMainRenderTarget(),
				GUIBACKPORT_BLUR_LOCATION
			);
			chain.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
			this.guibackport$blurEffect = chain;
		} catch (IOException | JsonSyntaxException e) {
		}
	}

	@Inject(method = "resize", at = @At("RETURN"))
	private void guibackport$resizeBlurEffect(int width, int height, CallbackInfo ci) {
		if (this.guibackport$blurEffect != null) {
			this.guibackport$blurEffect.resize(width, height);
		}
	}

	@Override
	public void guibackport$processMenuBlur() {
		if (this.guibackport$blurEffect == null || this.guibackport$blurredThisFrame) {
			return;
		}
		this.guibackport$blurredThisFrame = true;

		RenderSystem.enableBlend();
		((GuiBackportUniformAccess) this.guibackport$blurEffect).guibackport$setUniform("Radius", GUIBACKPORT_BLUR_RADIUS);
		this.guibackport$blurEffect.process(0.0F);
		RenderSystem.disableBlend();
		this.minecraft.getMainRenderTarget().bindWrite(false);
	}
}
