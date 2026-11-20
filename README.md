# Rimulib Flashback Fix

A lightweight **Minecraft Fabric** compatibility mod that fixes the immediate crash when opening or playing back recordings in **Flashback** while **Rimulib** and **The Shooting Star** are installed.

## 🐛 The Problem
When opening a replay in Flashback, the mod creates a virtual `ReplayServer` where custom modded dimensions (such as `shooting_star_demo:gravitia_stage`) are not registered in the server dimension registry. 

During `ServerLevelEvents.LOAD`, `Rimulib` executes `Regen.adopt` -> `WorldShapes.band` and calls `Registry.getValueOrThrow(...)` without checking if the dimension key exists, causing an immediate crash:
```text
java.lang.IllegalStateException: Missing key in ResourceKey[minecraft:root / minecraft:dimension]: ResourceKey[minecraft:dimension / shooting_star_demo:gravitia_stage]
```

## 🛠️ How It Works
This mod uses a safe `@Pseudo` Fabric Mixin to intercept `cyou.rimuru.rimulib.world.Regen.adopt` at the `HEAD` of the method. If the active server instance belongs to Flashback's `ReplayServer`, the method execution is safely canceled before it attempts to look up unregistered custom dimensions.

- **Client/Server Safe:** Uses `"required": false` and `defaultRequire = 0`, meaning it will never crash your game even if `Rimulib` updates or changes its internal methods.
- **Zero Performance Impact:** Only checks the server class once upon world/replay load.

## 📥 Installation
1. Ensure you have **Fabric Loader** installed for your Minecraft version (tested on **Minecraft 26.3**).
2. Download the latest `.jar` file from the **Releases** or **Actions** tab (make sure not to use the `-sources.jar` file).
3. Drop the `.jar` file into your `.minecraft/mods` folder alongside `Rimulib`, `The Shooting Star`, and `Flashback`.

## 📋 Tested Versions
- **Minecraft:** 26.3 (Fabric)
- **Rimulib:** 1.3.0
- **The Shooting Star (Demo):** 1.6.0-demo
- **Flashback:** 0.43.6

## 📄 License
This project is open-source and free to include in any modpack.
