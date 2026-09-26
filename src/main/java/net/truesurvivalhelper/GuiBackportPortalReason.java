package net.truesurvivalhelper;

import net.minecraft.client.gui.GuiGraphics;

public interface GuiBackportPortalReason {
	int OTHER = 0;
	int NETHER_PORTAL = 1;
	int END_PORTAL = 2;

	int guibackport$getPortalReason();

	void guibackport$setPortalReason(int reason);

	void guibackport$renderPortalBackground(GuiGraphics guiGraphics);
}
