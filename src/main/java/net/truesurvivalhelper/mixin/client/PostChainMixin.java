package net.truesurvivalhelper.mixin.client;

import java.util.List;

import com.mojang.blaze3d.shaders.Uniform;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.truesurvivalhelper.GuiBackportUniformAccess;

@Mixin(PostChain.class)
public abstract class PostChainMixin implements GuiBackportUniformAccess {
	@Shadow
	@Final
	private List<PostPass> passes;

	@Override
	public void guibackport$setUniform(String name, float value) {
		for (PostPass pass : this.passes) {
			Uniform uniform = pass.getEffect().getUniform(name);
			if (uniform != null) {
				uniform.set(value);
			}
		}
	}
}
