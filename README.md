# True Survival Helper

Native Fabric mod (Minecraft 1.20.1) that acts as a compatibility layer for the
**True Survival** modpack, fixing interactions between mods that don't work correctly
together out of the box - starting with wiring [LevelZ](https://www.curseforge.com/minecraft/mc-mods/levelz)
skill-level restrictions into items from ToughAsNails, Biome Makeover and
VanillaBackport that LevelZ has no restriction path for on its own.

LevelZ and LibZ are part of this mod, in modified form (see below). They are not
separate mods in the pack anymore.

The mod is built specifically for this pack's exact mod set and versions, not as a
generic redistributable addon.


## License

True Survival Helper is licensed under the GNU General Public License, version 3 only
(GPL-3.0-only), see [LICENSE](LICENSE). This covers all source code in this repository.

Textures, sounds and other media assets that are Mark's own original work are not covered
by the GPL and remain All Rights Reserved. Third-party assets, and assets based on them,
stay under their original licenses, see below.

## Included third-party code

### LevelZ

* LevelZ by Globox_Z, [github.com/Globox1997/LevelZ](https://github.com/Globox1997/LevelZ), licensed under the GNU General Public License v3.0.
* This mod contains a modified version of LevelZ 1.4.13 (upstream commit `0dd913f`) in the `net.levelz` package, together with its assets and data under the `levelz` namespace. Changes, September 2026:
  * Merged into True Survival Helper (previously published separately as "True Survival LevelZ"). The source uses Mojang mappings.
  * The settings are fixed values instead of a config file. There is no `levelz.json5`, no config screen and no config sync anymore.
  * Integrations for mods that are not part of the True Survival pack were removed (REI, EMI, Jade, WTHIT, Mod Menu, Trinkets, Placeholder API, TreeChop and others).
  * The skill, skill info and skill list screens are replaced with a backport of the LevelZ 2.0 screens, using recolored LevelZ 2.0.10 GUI textures and `#AAAAAA` GUI text.
  * Custom item entries without a registered item are shown with the name from `text.levelz.object_info.<path>` and the icon of the item named in `text.levelz.object_icon.<path>`.
  * Swords and axes above the player's level no longer hit at all instead of dealing bare-hand damage. Axes used as weapons lose durability as long as the sword requirement is met.
  * The level-up sound plays at most once every 5 seconds.
  * The level requirements of the True Survival pack are the default data. They can still be changed with datapacks.

### LibZ

* LibZ by Globox_Z, [github.com/Globox1997/LibZ](https://github.com/Globox1997/LibZ), licensed under the MIT License. The license notice is in [`src/main/resources/META-INF/LICENSE_libz.txt`](src/main/resources/META-INF/LICENSE_libz.txt).
* This mod contains a modified version of LibZ 1.0.3 in the `net.libz` package. Changes, September 2026: the config file, config sync and the integrations for mods that are not part of the pack were removed, and the inventory tab textures are recolored.

### Cloud Tweaks

* Cloud Tweaks by not_thefirst, [github.com/projectAccounth/better_clouds](https://github.com/projectAccounth/better_clouds), licensed under the MIT License. The cloud rendering of this mod is based on it. The license notice is in [`src/main/resources/META-INF/LICENSE_cloud_tweaks.txt`](src/main/resources/META-INF/LICENSE_cloud_tweaks.txt).

## Third-party assets

* Music: Lunar Event Ambient Music from [Enhanced Celestials](https://github.com/CorgiTaco-MC/Enhanced-Celestials) by CorgiTaco, composed by LudoCrypt.
* License: Licensed under [GNU Lesser General Public License v3.0 (LGPL-3.0)](https://www.gnu.org/licenses/lgpl-3.0.html).
