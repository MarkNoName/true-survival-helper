package net.levelz.mixin.item;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class ItemStackClientMixin {
	@Unique
	private int itemId;
	@Unique
	private List<Component> tooltipAddition;

	@Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void getTooltipMixin(@Nullable Player player, TooltipFlag context, CallbackInfoReturnable<List<Component>> info, List<Component> list) {
		if (player != null) {
			ItemStack stack = (ItemStack) (Object) this;
			int itemId = BuiltInRegistries.ITEM.getId(stack.getItem());
			if (this.itemId != itemId) {
				this.itemId = itemId;
				this.tooltipAddition = getAdditionalTooltip(player);
			}
			if (this.tooltipAddition != null && !this.tooltipAddition.isEmpty())
				list.addAll(this.tooltipAddition);
		}
	}

	private List<Component> getAdditionalTooltip(Player player) {
		List<Component> list = Lists.newArrayList();
		ItemStack stack = (ItemStack) (Object) this;
		ArrayList<Object> levelList = new ArrayList<Object>();
		int itemId = BuiltInRegistries.ITEM.getId(stack.getItem());
		if (stack.getItem() instanceof BlockItem) {
			Block block = ((BlockItem) stack.getItem()).getBlock();
			if (block instanceof AnvilBlock) {
				levelList = LevelLists.anvilList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.BEEHIVE || block == Blocks.BEE_NEST) {
				levelList = LevelLists.beehiveList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.BREWING_STAND) {
				levelList = LevelLists.brewingStandList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.ENCHANTING_TABLE) {
				levelList = LevelLists.enchantingTableList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.CAULDRON) {
				levelList = LevelLists.cauldronList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.BARREL) {
				levelList = LevelLists.barrelList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.BLAST_FURNACE) {
				levelList = LevelLists.blastFurnaceList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.FURNACE) {
				levelList = LevelLists.furnaceList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.CARTOGRAPHY_TABLE) {
				levelList = LevelLists.cartographyList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.COMPOSTER) {
				levelList = LevelLists.composterList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.GRINDSTONE) {
				levelList = LevelLists.grindstoneList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.LECTERN) {
				levelList = LevelLists.lecternList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.LOOM) {
				levelList = LevelLists.loomList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.SMITHING_TABLE) {
				levelList = LevelLists.smithingTableList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.SMOKER) {
				levelList = LevelLists.smokerList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.STONECUTTER) {
				levelList = LevelLists.stonecutterList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (block == Blocks.BEACON) {
				levelList = LevelLists.beaconList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (!LevelLists.customBlockList.isEmpty() && LevelLists.customBlockList.contains(BuiltInRegistries.BLOCK.getKey(block).toString())) {
				levelList = LevelLists.customBlockList;
				String string = BuiltInRegistries.BLOCK.getKey(block).toString();
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, string, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(string) + 2).toString())
							.withStyle(ChatFormatting.RED));
				}
			}
			if (PlayerStatsManager.listContainsItemOrBlock(player, itemId, 2))
				list.add(Component.translatable("item.levelz.alchemy_restriction.tooltip", PlayerStatsManager.getUnlockLevel(itemId, 2)).withStyle(ChatFormatting.RED));
			if (PlayerStatsManager.listContainsItemOrBlock(player, BuiltInRegistries.BLOCK.getId(block), 1))
				list.add(Component.translatable("item.levelz.mining_restriction.tooltip", PlayerStatsManager.getUnlockLevel(BuiltInRegistries.BLOCK.getId(block), 1)).withStyle(ChatFormatting.RED));
		} else {
			Item item = stack.getItem();
			if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(BuiltInRegistries.ITEM.getKey(item).toString())) {
				levelList = LevelLists.customItemList;
				String string = BuiltInRegistries.ITEM.getKey(item).toString();
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, string, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(string) + 2).toString())
							.withStyle(ChatFormatting.RED));
				}
			} else if (item instanceof TieredItem) {
				if (item instanceof PickaxeItem || item instanceof ShovelItem) {
					levelList = LevelLists.toolList;
					String material = ((TieredItem) item).getTier().toString().toLowerCase();
					if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
						list.add(Component.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
								.withStyle(ChatFormatting.RED));
					}
				} else if (item instanceof AxeItem) {
					levelList = LevelLists.axeList;
					String material = ((TieredItem) item).getTier().toString().toLowerCase();
					if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
						list.add(Component.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
								.withStyle(ChatFormatting.RED));
					}
					levelList = LevelLists.swordList;
					if (ConfigInit.CONFIG.bindAxeDamageToSwordRestriction && !PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
						list.add(Component.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + "_attack.tooltip",
								levelList.get(levelList.indexOf(material) + 2).toString()).withStyle(ChatFormatting.RED));
					}
				} else if (item instanceof HoeItem) {
					levelList = LevelLists.hoeList;
					String material = ((TieredItem) item).getTier().toString().toLowerCase();
					if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
						list.add(Component.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
								.withStyle(ChatFormatting.RED));
					}
				} else if (item instanceof SwordItem) {
					levelList = LevelLists.swordList;
					String material = ((TieredItem) item).getTier().toString().toLowerCase();
					if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
						list.add(Component.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
								.withStyle(ChatFormatting.RED));
					}
				}
			} else if (item instanceof ShearsItem) {
				levelList = LevelLists.sheepList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz.sheep_restriction.tooltip", StringUtils.capitalize((String) levelList.get(0)), levelList.get(1)).withStyle(ChatFormatting.RED));
				}
				levelList = LevelLists.shearsList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item == Items.FLINT_AND_STEEL) {
				levelList = LevelLists.flintAndSteelList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item == Items.GLASS_BOTTLE) {
				levelList = LevelLists.dragonBreathList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item == Items.COMPASS) {
				levelList = LevelLists.compassList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item == Items.TOTEM_OF_UNDYING) {
				levelList = LevelLists.totemList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item instanceof BowItem) {
				levelList = LevelLists.bowList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item instanceof TridentItem) {
				levelList = LevelLists.tridentList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item instanceof CrossbowItem) {
				levelList = LevelLists.crossbowList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item instanceof ArmorItem) {
				try {
					levelList = LevelLists.armorList;
					String material = ((ArmorItem) item).getMaterial().getName().toLowerCase();
					if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, material, false)) {
						list.add(Component.translatable("item.levelz." + levelList.get(levelList.indexOf(material) + 1).toString() + ".tooltip", levelList.get(levelList.indexOf(material) + 2).toString())
								.withStyle(ChatFormatting.RED));
					}
				} catch (AbstractMethodError ignored) {
				}
			} else if (item instanceof ShieldItem) {
				levelList = LevelLists.shieldList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item instanceof ElytraItem) {
				levelList = LevelLists.elytraList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item instanceof BucketItem) {
				levelList = LevelLists.bucketList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			} else if (item instanceof FishingRodItem) {
				levelList = LevelLists.fishingList;
				if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, null, false)) {
					list.add(Component.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).withStyle(ChatFormatting.RED));
				}
			}
			if (PlayerStatsManager.listContainsItemOrBlock(player, itemId, 2))
				list.add(Component.translatable("item.levelz.alchemy_restriction.tooltip", PlayerStatsManager.getUnlockLevel(itemId, 2)).withStyle(ChatFormatting.RED));
			if (PlayerStatsManager.listContainsItemOrBlock(player, itemId, 3))
				list.add(Component.translatable("item.levelz.smithing_restriction.tooltip", PlayerStatsManager.getUnlockLevel(itemId, 3)).withStyle(ChatFormatting.RED));
		}
		if (PlayerStatsManager.listContainsItemOrBlock(player, itemId, 4))
			for (int i = 0; i < LevelLists.craftingItemList.size(); i++) {
				if (LevelLists.craftingItemList.get(i).contains(itemId)) {
					list.add(Component.translatable("item.levelz.crafting_restriction.tooltip", StringUtils.capitalize(LevelLists.craftingSkillList.get(i)), PlayerStatsManager.getUnlockLevel(itemId, 4))
							.withStyle(ChatFormatting.RED));
					break;
				}
			}

		return list;
	}
}
