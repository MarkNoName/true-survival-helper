package net.truesurvivalhelper.bloodmoon.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * Sound events registered client-side only - ambient music has no gameplay effect other players
 * need to agree on, so there's no need for this to exist on the logical server at all.
 */
@Environment(EnvType.CLIENT)
public final class BloodMoonSounds {
	public static final SoundEvent BLOOD_MOON_MUSIC = register("blood_moon");

	private BloodMoonSounds() {
	}

	/** No-op body - referencing this class is enough to run the static initializer above. */
	public static void register() {
	}

	private static SoundEvent register(String name) {
		ResourceLocation id = new ResourceLocation("tsh", name);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}
}
