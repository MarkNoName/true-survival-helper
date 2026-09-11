package net.truesurvivalhelper.bloodmoon;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/** Shared network channel used to sync Blood Moon's active flag from server to client. */
public final class BloodMoonNetworking {
	public static final ResourceLocation CHANNEL = new ResourceLocation("tsh", "blood_moon_sync");

	private BloodMoonNetworking() {
	}

	public static FriendlyByteBuf write(boolean active) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBoolean(active);
		return buf;
	}
}
