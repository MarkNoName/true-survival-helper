package net.levelz.mixin.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {
	@Shadow
	private ClientLevel level;

	@Shadow
	private static void renderVoxelShape(PoseStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue,
			float alpha, boolean bl) {
		throw new AbstractMethodError("shadow");
	}

	@Inject(method = "renderHitOutline", at = @At(value = "HEAD"), cancellable = true)
	private void drawBlockOutlineMixin(PoseStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos blockPos, BlockState blockState,
			CallbackInfo info) {
		if (ConfigInit.CONFIG.highlightLocked && PlayerStatsManager.listContainsItemOrBlock(Minecraft.getInstance().player, BuiltInRegistries.BLOCK.getId(blockState.getBlock()), 1)) {
			renderVoxelShape(matrices, vertexConsumer, blockState.getShape(this.level, blockPos, CollisionContext.of(entity)), (double) blockPos.getX() - cameraX,
					(double) blockPos.getY() - cameraY, (double) blockPos.getZ() - cameraZ, 1.0F, 0.0F, 0.0F, 0.4F, false);
			info.cancel();
		}
	}
}
