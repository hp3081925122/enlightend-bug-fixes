# Enlightend Bug Fixes

**Enlightend Bug Fixes** is a lightweight, zero-extra-dependency bugfix, server stability, TPS/network optimization, Xaero map visibility, and modular tool compatibility addon for **Enlightend (`enlightened_end`)** on Minecraft **1.20.1 Forge**.

Unlike overhaul addons that require external libraries, add bloated items, or delete Enlightend's signature End fog and Void Leviathan, **Enlightend Bug Fixes** strictly preserves 100% of Enlightend's original content, visuals, and atmosphere while fixing over 30 underlying bugs, exploits, packet storms, and server crashes.

---

## Key Fixes & Optimizations

### 1. Dedicated Server Crashes, Worldgen Corruption & Startup Errors
- **Dedicated Server `NoClassDefFoundError` Fixes**: Removed direct `net.minecraft.client.Minecraft` references from all **12 common procedures** (`PlayerRadiation`, `Enderstride`, `Rooted`, `VoidsteelPickaxeRightclicked`, gas/grass placement, and log stripping).
- **Sub-Biome `SimplexNoise` Seed Corruption Fix**: Fixed `DefaultBiomeSeedProcedure` overwriting the server's static biome noise generator with `seed = 1L` on client world load, fixed `SetBiomeSeedProcedure` switching to `LegacyRandomSource` and scrambling all End sub-biomes upon portal travel, prevented early chunkgen `NullPointerException`s across all 6 sub-biome checks, and synced the exact 64-bit world seed to multiplayer clients without breaking network compatibility.
- **Parallel Startup Thread Safety & `workQueue` Leak Protection**: Wrapped `ComposterBlock.COMPOSTABLES` registration inside `event.enqueueWork(...)`, blocked client-side `queueServerWork` memory leaks (including all 3 flying dart projectiles queuing 400-tick closures every tick), and isolated exceptions in delayed server tasks.
- **Broken Datapack JSON Fixes**:
  - Disabled `squelcher_biome_modifier.json` referencing deleted entity `enlightened_end:squelcher`.
  - Restricted 7 End-only `forge:any` biome modifiers (`adamantite_node`, `irradium_ore`, `malachite_ore`, `palerock`, `ooze_pocket`, `ooze_pocket_underground`, `void_leviathan`) to End biomes so Overworld/Nether chunk generation no longer wastes cycles evaluating End features.
  - Replaced broken 1.19 `"minecraft:smithing"` recipe under misspelled `famersdelight` namespace with a proper conditional 1.20.1 `smithing_transform` recipe and `farmersdelight:tools/knives` / `forge:tools/knives` tags.
  - Cleaned up 90+ broken Nourished End / WIP block references in `data/fabric/tags/blocks/mineable/needs_tool_level_*` and `cerulean_stalk_blocks`.

### 2. Severe Network Packet Storms & TPS/FPS Optimizations
- **`OozeRainChanceProcedure` Global Broadcast Storm Fix**: Stopped broadcasting `SavedDataSyncMessage` to all players across all dimensions **60+ times per second**; now ticks once per End server tick and only broadcasts when Ooze Rain actually starts or stops.
- **`PlayerTickEvent` `PlayerVariables` Packet Storm & Allocation Elimination**:
  - Eliminated constant per-tick `syncPlayerVariables` packet storms and `.orElse(new PlayerVariables())` garbage allocations across `EndBarrenAmbience`, `OozeRainParticles`, `EndMusic`, `PlayerRadiation` (which previously spammed 20 packets/sec forever for Creative/Spectator players), `RadiationTick`, `RadioactiveItems` (eliminating 36 `ItemStack.copy()` calls and up to 72 packets per tick), and `DashingLeggings`.
  - Fixed `EndMusicProcedure` running `stopsound @a music music.end` and cutting off music for **every player on the entire server**.
  - Disabled intrusive `/tellraw @p` Patreon chat spam on entering the End (configurable).
- **Radiation, Depleted Irradium & Glacium Entity Scan Optimization**: Replaced `Entity.class` + `Stream.sorted(...)` scans with pre-filtered `LivingEntity.class` scans; idle Nuclear Furnaces no longer scan $16\times 16\times 16$ entity boxes 20 times/sec.
- **Client Render & OpenGL State Fixes**:
  - Removed leaked `RenderSystem.enableBlend()` / additive `SRC_ALPHA, ONE` blend state from `EndSkiesProcedure` (`ComputeFogColor`).
  - Fixed `EndRenderingProcedure` cancelling Overworld/Nether terrain fog and bypassing Blindness/Darkness fog.
  - Cached `BoostBoots` armor model (previously calling `bakeLayer` twice per frame) and eliminated per-frame allocations/GL state churn in `RadiationBarOverlay`.

### 3. Major Gameplay, Loot & Exploit Fixes
- **Nuclear Furnace Complete Repair**:
  - Fixed fuel slot deleting the **2nd `Irradium Bar`** when stack count was `2`.
  - Fixed burning fuel with 0 smeltable inputs, 0-tick instant smelting of the first item, ignoring recipe output counts/stack limits, negative `fuelTime` underflow, and infinite 20 packets/sec `sendBlockUpdated` spam when idle.
  - **Remote Bedrock Destruction / Block Duplication Exploit Fix**: Fixed `NuclearFurnaceNeighbourBlockChangesProcedure` mistyping `(z, y ± 1, z)` instead of `(x, y ± 1, z)` when breaking adjacent Hoppers.
  - Restricted slot `0` to `Irradium Bar`, restricted automation extraction to output slots `4–6`, and fixed unbound `NuclearFurnaceScreenMenu#stillValid`.
- **Bedrock Destruction Fixes**:
  - Fixed `VoidsteelPickaxeRightclickedProcedure` (`Adamantite Pickaxe`) checking harvest level at player feet `(x, y, z)` instead of the raytraced block and added indestructible block protection (`getDestroySpeed >= 0`).
  - Fixed bonemealing `Underlily` unconditionally overwriting Bedrock/blocks directly underneath `(x, y - 1, z)`.
- **Unobtainable Drops & Broken Loot Tables Fixed**:
  - Added `entities/fumesplat.json` dropping `splatter_gland` with Looting support (fixing MCreator's leftover `exhauster.json` name that made **Potions of Suffocation unobtainable in survival**).
  - Resurrected `entities/sacrifice_statue_gift.json` (which failed to parse on startup due to deleted `enlightened_end:frost_aspect` and renamed `adamantite_smithing_template`).
  - Added missing `blocks/cerulean_frond.json`, fixed `blocks/cerulean_grass.json` dropping `minecraft:nether_sprouts` instead of `cerulean_grass`, and added `looting_enchant` to `eidolon.json`.
- **Entity, Cauldron & Item Interaction Fixes**:
  - Fixed `RinglingAttackProcedure` inverting `!(entity instanceof BouncelingEntity)` and instantly deleting (`discard()`) any non-Bounceling entity a Ringling attacked.
  - Fixed `FlashBombEntity` bouncing off entities without discarding and causing infinite consecutive flashbangs, registered vanilla Dispenser projectile firing behavior for `Flash Bomb`, and fixed dispenser/ownerless block hits failing to detonate.
  - Fixed impossible advancements `feed_ringling_advancement` and `milk_ensnare` (previously checking `ServerPlayer` on the mob instead of the player) and Creative bottle/jar consumption.
  - Fixed `EmptyCauldronOozeProcedure` returning an empty `Bucket` instead of `Bucket of Ooze` and failing to fill `Glass Bottle`s with `Ooze Bottle`s.
  - Blocked Hopper item extraction from `Offering Statue` (`SacrificalStatueBlockEntity`) to prevent infinite free rare item extraction.
  - Fixed placing stacked `Helium Jar` / `Xenon Jar` wiping out the entire held stack with a single empty `Gas Jar`.
  - Fixed `HeliumGasBlock` & `XenonGasBlock` missing `noOcclusion()` and culling adjacent solid block faces (X-ray bug).
  - Reclassified 7 non-mob entities (`floating_elevibloom`, `activated_nuclear_bomb`, `falling_bounceling_nut`, `disturbed_helium_gas`, `disturbed_xenon_gas`, `falling_heliosand`, `ooze_bubble`) from `MONSTER` to `MISC` so they stop starving the End monster spawn cap.

### 4. Xaero's Map Visibility & Modular Tool Compatibility
- **119 Invisible Blocks Fixed on Maps**: Mapped all **119 visible blocks** that defaulted to `MapColor.NONE` to texture-matched vanilla `MapColor`s so End terrain and builds no longer render as transparent void holes on Xaero's World Map, Xaero's Minimap, and vanilla maps.
- **157 Hardcoded `PickaxeItem` Blocks & 3 Log-Stripping Procedures Standardized**: Replaced hardcoded `instanceof PickaxeItem` / `instanceof AxeItem` checks with standard `ForgeHooks.isCorrectToolForDrops`, `ToolActions.AXE_STRIP`, and vanilla `minecraft:needs_{stone,iron,diamond}_tool` + Forge/vanilla tags so custom modular tools (such as Tinkers' Construct and Tetra) follow standard tag harvesting rules.
