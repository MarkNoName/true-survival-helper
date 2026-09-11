# True Survival Helper

Native Fabric mod (Minecraft 1.20.1) that acts as a compatibility layer for the
**True Survival** modpack, fixing interactions between mods that don't work correctly
together out of the box - starting with wiring [LevelZ](https://www.curseforge.com/minecraft/mc-mods/levelz)
skill-level restrictions into items from ToughAsNails, Biome Makeover and
VanillaBackport that LevelZ has no restriction path for on its own.

The mod is built specifically for this pack's exact mod set and versions, not as a
generic redistributable addon.

## Building

Requires JDK 21 to run the Gradle/Loom build itself (the compiled mod targets Java 17).

The compile-time dependencies (`libs/*.jar`) are the exact mod jars from the pack's
`mods` folder - copy them in yourself before building:

- `levelz-true-survival-1.4.13.jar`
- `ToughAsNails-fabric-1.20.1-9.2.0.171.jar`
- `biomemakeover-FABRIC-1.20.1-1.11.4.jar`
- `VanillaBackport-fabric-1.20.1-1.2.0.2636.1.jar`

```bash
./gradlew build
```

The output jar lands in `build/libs/`.

## License

MIT, see [LICENSE](LICENSE).

## Credits & Licensing

* Music: Lunar Event Ambient Music from [Enhanced Celestials](https://github.com/CorgiTaco-MC/Enhanced-Celestials) by CorgiTaco, composed by LudoCrypt.
* License: Licensed under [GNU Lesser General Public License v3.0 (LGPL-3.0)](https://www.gnu.org/licenses/lgpl-3.0.html).
