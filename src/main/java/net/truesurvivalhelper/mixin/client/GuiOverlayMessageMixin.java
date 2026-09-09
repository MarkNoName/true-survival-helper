package net.truesurvivalhelper.mixin.client;

import java.util.Locale;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.stats.Skill;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.truesurvivalhelper.client.DenialFeedback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Every LevelZ skill-restriction denial - LevelZ's own dozens of Mixins for vanilla
 * items/blocks, this mod's copper/leaf/wool/cladded JSON-driven ones, and this mod's
 * own custom-Mixin ones (canteens, water purifier, thermoregulator, totem, spears) -
 * all funnel through exactly one client-side call: Player#displayClientMessage(...,
 * true), which resolves to this one vanilla method. So this single Mixin replaces the
 * plain action-bar text with the icon-box overlay everywhere at once, without touching
 * any of LevelZ's own Mixins individually.
 *
 * Detected purely by translation key shape (item.levelz.*.tooltip - the exact,
 * closed set of keys LevelZ uses for every one of its denial messages, confirmed
 * against its own lang file), so any ordinary action-bar message (sleep, unrelated
 * mods, etc.) passes through untouched.
 *
 * Lives in its own net.truesurvivalhelper.mixin.client package, separate from the
 * plain helper classes it calls into (net.truesurvivalhelper.client) - a mixins.json
 * "package" claims the whole namespace exclusively for Mixin's classloader, so normal
 * classes can't share it with the mixin itself.
 */
@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public class GuiOverlayMessageMixin {
	private static final String PREFIX = "item.levelz.";
	private static final String SUFFIX = ".tooltip";

	@Inject(method = "setOverlayMessage", at = @At("HEAD"), cancellable = true)
	private void tsh$interceptLevelZDenial(Component message, boolean animateColor, CallbackInfo ci) {
		if (!(message.getContents() instanceof TranslatableContents contents)) {
			return;
		}
		String key = contents.getKey();
		if (!key.startsWith(PREFIX) || !key.endsWith(SUFFIX)) {
			return;
		}
		String token = key.substring(PREFIX.length(), key.length() - SUFFIX.length());
		Skill skill = tsh$resolveSkill(token);
		if (skill == null) {
			return;
		}
		Object[] args = contents.getArgs();
		if (args.length == 0 || !(args[0] instanceof Integer level)) {
			return;
		}
		DenialFeedback.trigger(skill, level);
		ci.cancel();
	}

	private static Skill tsh$resolveSkill(String token) {
		try {
			if (token.endsWith("_attack")) {
				return Skill.valueOf(token.substring(0, token.length() - "_attack".length()).toUpperCase(Locale.ROOT));
			}
			return switch (token) {
				case "mining_restriction" -> Skill.MINING;
				case "alchemy_restriction" -> Skill.ALCHEMY;
				case "smithing_restriction" -> Skill.SMITHING;
				// These two carry an extra non-level argument (a category/name string) the
				// box isn't built to show - leave them as plain vanilla text.
				case "crafting_restriction", "sheep_restriction" -> null;
				default -> Skill.valueOf(token.toUpperCase(Locale.ROOT));
			};
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
