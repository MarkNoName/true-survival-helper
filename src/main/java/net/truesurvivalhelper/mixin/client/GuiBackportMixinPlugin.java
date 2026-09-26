package net.truesurvivalhelper.mixin.client;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class GuiBackportMixinPlugin implements IMixinConfigPlugin {
	private static final String SODIUM_MOD_ID = "sodium";
	private static final String SODIUM_SUPPORTED_VERSION = "0.5.13+mc1.20.1";
	private static final String SODIUM_MIXIN_PACKAGE = "net.truesurvivalhelper.mixin.sodium.";

	private boolean sodiumLoaded;
	private boolean sodiumVersionSupported;

	@Override
	public void onLoad(String mixinPackage) {
		FabricLoader loader = FabricLoader.getInstance();
		this.sodiumLoaded = loader.isModLoaded(SODIUM_MOD_ID);
		this.sodiumVersionSupported = loader.getModContainer(SODIUM_MOD_ID)
			.map(mod -> SODIUM_SUPPORTED_VERSION.equals(mod.getMetadata().getVersion().getFriendlyString()))
			.orElse(false);
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.startsWith(SODIUM_MIXIN_PACKAGE)) {
			return this.sodiumVersionSupported;
		} else if (mixinClassName.endsWith(".LevelRendererMixin") || mixinClassName.endsWith(".RenderChunkMixin")) {
			return !this.sodiumLoaded;
		} else {
			return true;
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
