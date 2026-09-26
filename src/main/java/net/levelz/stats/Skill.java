package net.levelz.stats;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import net.minecraft.util.RandomSource;

public enum Skill {
	HEALTH(1, "HealthLevel"),
	STRENGTH(2, "StrengthLevel"),
	AGILITY(3, "AgilityLevel"),
	DEFENSE(4, "DefenseLevel"),
	STAMINA(5, "StaminaLevel"),
	LUCK(6, "LuckLevel"),
	ARCHERY(7, "ArcheryLevel"),
	TRADE(8, "TradeLevel"),
	SMITHING(9, "SmithingLevel"),
	MINING(10, "MiningLevel"),
	FARMING(11, "FarmingLevel"),
	ALCHEMY(12, "AlchemyLevel");

	final int id;
	final String nbt;

	Skill(int id, String nbt) {
		this.id = id;
		this.nbt = nbt;
	}

	public static Skill fromNbt(String nbt) {
		return Arrays.stream(Skill.values()).filter(it -> it.nbt.equals(nbt)).findFirst().orElse(null);
	}

	public static Iterable<Skill> listInRandomOrder(RandomSource random) {
		if (random == null) {
			random = RandomSource.create();
		}
		RandomSource finalRandom = random;
		return Arrays.stream(Skill.values()).sorted(Comparator.comparing(it -> finalRandom.nextInt())).collect(Collectors.toList());
	}

	public int getId() {
		return id;
	}

	public String getNbt() {
		return nbt;
	}
}
