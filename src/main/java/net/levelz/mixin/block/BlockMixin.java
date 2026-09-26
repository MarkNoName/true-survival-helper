package net.levelz.mixin.block;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.EntityInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;

@Mixin(Block.class)
public class BlockMixin {
	@Nullable
	private ServerPlayer serverPlayerEntity = null;

	@Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"), cancellable = true)
	private static void dropStacksMixin(BlockState state, Level world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo info) {
		if (entity instanceof Player) {
			if (EntityInit.isRedstoneBitsLoaded && entity.getClass().getName().contains("RedstoneBitsFakePlayer")) {
			} else {
				if (PlayerStatsManager.listContainsItemOrBlock((Player) entity, BuiltInRegistries.BLOCK.getId(state.getBlock()), 1)) {
					info.cancel();
				} else if (stack.getItem() instanceof DiggerItem) {
					Item item = stack.getItem();
					ArrayList<Object> levelList = LevelLists.customItemList;
					try {
						if (!levelList.isEmpty() && levelList.contains(BuiltInRegistries.ITEM.getKey(item).toString())) {
							if (!PlayerStatsManager.playerLevelisHighEnough((Player) entity, levelList, BuiltInRegistries.ITEM.getKey(item).toString(), true)) {
								info.cancel();
							}
						}
					} catch (AbstractMethodError ignore) {
					}
					levelList = null;
					if (item instanceof AxeItem) {
						levelList = LevelLists.axeList;
					} else if (item instanceof HoeItem) {
						levelList = LevelLists.hoeList;
					} else if (item instanceof PickaxeItem || item instanceof ShovelItem) {
						levelList = LevelLists.toolList;
					}
					if (levelList != null
							&& !PlayerStatsManager.playerLevelisHighEnough((Player) entity, levelList, ((DiggerItem) stack.getItem()).getTier().toString().toLowerCase(), true)) {
						info.cancel();
					}
				} else if (stack.getItem() instanceof ShearsItem && !PlayerStatsManager.playerLevelisHighEnough((Player) entity, LevelLists.shearsList, null, true)) {
					info.cancel();
				}
			}
		}
	}

	@Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDrops(Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void getDroppedStacksMixin(BlockState state, ServerLevel world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack,
			CallbackInfoReturnable<List<ItemStack>> info, LootParams.Builder builder) {
		if (entity != null && state.getBlock() instanceof DropExperienceBlock && entity instanceof Player playerEntity) {
			if ((float) ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager().getSkillLevel(Skill.MINING) * ConfigInit.CONFIG.miningOreChance > world.random.nextFloat()
					&& EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0 && state.getDrops(builder).size() > 0) {
				Block.popResource(world, pos, state.getDrops(builder).get(0).split(1));
			}
		}
	}

	@Inject(method = "popExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
	protected void dropExperienceMixin(ServerLevel world, BlockPos pos, int size, CallbackInfo info) {
		if (ConfigInit.CONFIG.oreXPMultiplier > 0.0F)
			LevelExperienceOrbEntity.spawn(world, Vec3.atCenterOf(pos),
					(int) (size * ConfigInit.CONFIG.oreXPMultiplier
							* (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
									? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager().getOverallLevel()
									: 1.0F)));
	}

	@Inject(method = "playerWillDestroy", at = @At(value = "HEAD"))
	private void onBreakMixin(Level world, BlockPos pos, BlockState state, Player player, CallbackInfo info) {
		if (!world.isClientSide)
			serverPlayerEntity = (ServerPlayer) player;
	}
}
