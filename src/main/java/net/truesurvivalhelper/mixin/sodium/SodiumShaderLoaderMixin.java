package net.truesurvivalhelper.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.jellysquid.mods.sodium.client.gl.shader.ShaderLoader;

import net.minecraft.resources.ResourceLocation;

@Mixin(value = ShaderLoader.class, remap = false)
public abstract class SodiumShaderLoaderMixin {
	@Inject(method = "getShaderSource", at = @At("RETURN"), cancellable = true)
	private static void guibackport$patchTerrainShader(ResourceLocation name, CallbackInfoReturnable<String> cir) {
		String path = name.getPath();

		if (path.endsWith("block_layer_opaque.vsh")) {
			cir.setReturnValue(guibackport$patchVertexShader(cir.getReturnValue()));
		} else if (path.endsWith("block_layer_opaque.fsh")) {
			cir.setReturnValue(guibackport$patchFragmentShader(cir.getReturnValue()));
		}
	}

	@Unique
	private static String guibackport$patchVertexShader(String source) {
		String uboAnchor = "void main() {";
		String withUbo = source.replace(
			uboAnchor,
			"layout(std140) uniform ubo_GuiBackportChunkFade {\n"
				+ "    vec4 u_GuiBackportChunkFade[256];\n"
				+ "};\n"
				+ "out float v_GuiBackportChunkVisibility;\n\n"
				+ uboAnchor
		);

		String initAnchor = "_vert_init();";
		String withLookup = withUbo.replace(
			initAnchor,
			initAnchor + "\n    v_GuiBackportChunkVisibility = u_GuiBackportChunkFade[_draw_id].x;"
		);

		return guibackport$requireChanged(source, withLookup, "block_layer_opaque.vsh");
	}

	@Unique
	private static String guibackport$patchFragmentShader(String source) {
		String varyingAnchor = "uniform sampler2D u_BlockTex;";
		String withVarying = source.replace(
			varyingAnchor,
			"in float v_GuiBackportChunkVisibility;\n\n" + varyingAnchor
		);

		String fogAnchor = "fragColor = _linearFog(diffuseColor, v_FragDistance, u_FogColor, u_FogStart, u_FogEnd);";
		String withFade = withVarying.replace(
			fogAnchor,
			"diffuseColor = mix(vec4(u_FogColor.rgb, diffuseColor.a), diffuseColor, v_GuiBackportChunkVisibility);\n\n    " + fogAnchor
		);

		return guibackport$requireChanged(source, withFade, "block_layer_opaque.fsh");
	}

	@Unique
	private static String guibackport$requireChanged(String original, String patched, String fileName) {
		if (patched.equals(original)) {
			throw new IllegalStateException(
				"guibackport: expected anchor text not found while patching Sodium's " + fileName
					+ " - its shader source no longer matches what this was written against."
			);
		}

		return patched;
	}
}
