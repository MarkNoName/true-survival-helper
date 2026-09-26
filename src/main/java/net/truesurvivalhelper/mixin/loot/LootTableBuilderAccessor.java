package net.truesurvivalhelper.mixin.loot;

import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTable.Builder.class)
public interface LootTableBuilderAccessor {
	@Accessor("paramSet")
	LootContextParamSet tsh$paramSet();
}
