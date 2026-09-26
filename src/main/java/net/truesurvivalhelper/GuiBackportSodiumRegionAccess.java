package net.truesurvivalhelper;

import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.gl.shader.uniform.GlUniformBlock;

public interface GuiBackportSodiumRegionAccess {
	void guibackport$writeFadeVisibility(int sectionIndex, float visibility);

	void guibackport$uploadAndBindFadeVisibility(CommandList commandList, GlUniformBlock uniformBlock);
}
