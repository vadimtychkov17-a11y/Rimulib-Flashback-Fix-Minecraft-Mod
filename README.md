# Rimulib Flashback Fix

A lightweight **Minecraft Fabric** compatibility mod resolving crashes during Flashback replay playback caused by missing dimensions from **Rimulib** and **The Shooting Star**.

## 🐛 The Problem
Flashback initializes a virtual `ReplayServer` during replays where custom dimensions like `shooting_star_demo:gravitia_stage` remain unregistered. This triggers an `IllegalStateException` in Rimulib during `ServerLevelEvents.LOAD` when it calls `Registry.getValueOrThrow(...)` on the missing dimension.

## 🛠️ How It Works
A Fabric Mixin intercepts `net.minecraft.core.Registry#getValueOrThrow` at `HEAD`. If the lookup for `shooting_star_demo:gravitia_stage` fails in an environment lacking custom dimensions, it safely returns `null` rather than crashing.

- **Multi-Version Wrapper Compatible:** Supports runtime class-unpacked `rimulib_multi` containers.
- **Zero Performance Impact:** Minimal overhead restricted to specific dimension lookups.

## 📥 Installation
1. Install **Fabric Loader** (tested on Minecraft 26.3 / Java 25).
2. Place the `.jar` file (avoiding `-sources.jar`) into your `mods` folder with Rimulib, The Shooting Star, and Flashback.

## 📋 Tested Environments
- **Minecraft:** 26.3 (Fabric) | **Java:** 25
- **Rimulib:** 1.3.0 | **The Shooting Star (Demo):** 1.6.0-demo | **Flashback:** 0.43.6

## 📄 License
Licensed under **CC0-1.0** (Open Source / Modpack friendly).
