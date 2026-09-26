package net.levelz.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerListAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public class PlayerEntityRendererMixin {
	@Unique
	private AbstractClientPlayer abstractClientPlayerEntity;

	@Inject(method = "renderNameTag", at = @At(value = "HEAD"))
	protected void renderLabelIfPresentMixin(AbstractClientPlayer abstractClientPlayerEntity, Component text, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i,
			CallbackInfo info) {
		this.abstractClientPlayerEntity = abstractClientPlayerEntity;
	}

	@ModifyArg(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 1))
	protected Component renderLabelIfPresentMixin(Component original) {
		if (ConfigInit.CONFIG.showLevel) {
			return PlayerTeam.formatNameForTeam(abstractClientPlayerEntity.getTeam(),
					Component.translatable("text.levelz.scoreboard", ((PlayerListAccess) abstractClientPlayerEntity).getLevel(), abstractClientPlayerEntity.getName()));
		} else {
			return original;
		}
	}
}
