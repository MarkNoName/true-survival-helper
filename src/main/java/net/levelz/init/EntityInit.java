package net.levelz.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityInit {
	public static final boolean isRedstoneBitsLoaded = FabricLoader.getInstance().isModLoaded("redstonebits");

	public static final EntityType<LevelExperienceOrbEntity> LEVEL_EXPERIENCE_ORB = FabricEntityTypeBuilder.<LevelExperienceOrbEntity>create(MobCategory.MISC, LevelExperienceOrbEntity::new)
			.dimensions(EntityDimensions.fixed(0.5F, 0.5F)).build();

	public static void init() {
		Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation("levelz", "level_experience_orb"), LEVEL_EXPERIENCE_ORB);
	}
}
