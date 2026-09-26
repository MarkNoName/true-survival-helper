package net.libz.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class LibzServerPacket {
	public static final ResourceLocation SET_MOUSE_POSITION = new ResourceLocation("libz", "set_mouse_position");

	public static void init() {
	}

	public static void writeS2CMousePositionPacket(ServerPlayer serverPlayerEntity, int mouseX, int mouseY) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(mouseX);
		buf.writeInt(mouseY);
		ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(SET_MOUSE_POSITION, buf);
		serverPlayerEntity.connection.send(packet);
	}
}
