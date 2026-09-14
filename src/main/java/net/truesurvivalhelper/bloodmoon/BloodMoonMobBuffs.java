package net.truesurvivalhelper.bloodmoon;

import java.util.UUID;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class BloodMoonMobBuffs {
	private static final UUID HEALTH_MODIFIER_ID = UUID.fromString("b100d000-0000-4000-8000-000000000001");
	private static final UUID DAMAGE_MODIFIER_ID = UUID.fromString("b100d000-0000-4000-8000-000000000002");

	private static final double HEALTH_MULTIPLIER = 0.5;
	private static final double DAMAGE_MULTIPLIER = 0.25;
	private static final float ARMOR_CHANCE = 0.5F;

	private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

	private record ArmorTier(int weight, Item boots, Item leggings, Item chestplate, Item helmet) {
		Item[] pieces() {
			return new Item[]{boots, leggings, chestplate, helmet};
		}
	}

	private static final ArmorTier[] ARMOR_TIERS = {
		new ArmorTier(45, Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET),
		new ArmorTier(30, Items.CHAINMAIL_BOOTS, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_HELMET),
		new ArmorTier(20, Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET),
		new ArmorTier(5, Items.DIAMOND_BOOTS, Items.DIAMOND_LEGGINGS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET),
	};
	private static final int ARMOR_TIER_TOTAL_WEIGHT = 100;

	private BloodMoonMobBuffs() {
	}

	public static void apply(Mob mob, RandomSource random) {
		buffAttribute(mob, Attributes.MAX_HEALTH, HEALTH_MODIFIER_ID, "tsh:blood_moon_health", HEALTH_MULTIPLIER);
		buffAttribute(mob, Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER_ID, "tsh:blood_moon_damage", DAMAGE_MULTIPLIER);
		mob.setHealth(mob.getMaxHealth());

		if (random.nextFloat() < ARMOR_CHANCE) {
			equipRandomArmor(mob, random);
		}
	}

	private static void buffAttribute(Mob mob, Attribute attribute, UUID id, String name, double multiplier) {
		AttributeInstance instance = mob.getAttribute(attribute);
		if (instance == null || instance.getModifier(id) != null) {
			return;
		}
		instance.addPermanentModifier(new AttributeModifier(id, name, multiplier, AttributeModifier.Operation.MULTIPLY_TOTAL));
	}

	private static void equipRandomArmor(Mob mob, RandomSource random) {
		Item[] tier = pickArmorTier(random).pieces();
		int pieces = 1 + random.nextInt(ARMOR_SLOTS.length);
		for (int i = 0; i < pieces; i++) {
			mob.setItemSlot(ARMOR_SLOTS[i], new ItemStack(tier[i]));
		}
	}

	private static ArmorTier pickArmorTier(RandomSource random) {
		int roll = random.nextInt(ARMOR_TIER_TOTAL_WEIGHT);
		int cumulative = 0;
		for (ArmorTier tier : ARMOR_TIERS) {
			cumulative += tier.weight();
			if (roll < cumulative) {
				return tier;
			}
		}
		return ARMOR_TIERS[0];
	}
}
