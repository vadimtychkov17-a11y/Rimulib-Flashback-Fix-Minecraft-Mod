package com.fix.rimulib.mixin;

import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(MappedRegistry.class)
public abstract class ExampleMixin<T> {

    @Unique
    private static final Logger RIMULIB_FIX_LOGGER = LoggerFactory.getLogger("RimulibFlashbackFix");

    @Unique
    private static final Set<String> LOGGED_MISSING_KEYS = ConcurrentHashMap.newKeySet();

    @Unique
    private static boolean isProtectedModDimension(String keyStr) {
        return keyStr.contains("gravitia_stage")
                || keyStr.contains("shooting_star")
                || keyStr.contains("ton618")
                || keyStr.contains("rimulib");
    }

    @Unique
    private static boolean isReplayOrVirtualContext() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            if (className.contains("flashback")
                    || className.contains("ReplayServer")
                    || className.contains("cyou.rimuru")) {
                return true;
            }
        }
        return false;
    }

    @Inject(method = "getValueOrThrow", at = @At("HEAD"), cancellable = true)
    private void onGetValueOrThrow(ResourceKey<T> key, CallbackInfoReturnable<T> cir) {
        if (key == null || key.location() == null) {
            return;
        }

        String keyString = key.toString();
        if (!isProtectedModDimension(keyString)) {
            return;
        }

        @SuppressWarnings("unchecked")
        MappedRegistry<T> registry = (MappedRegistry<T>) (Object) this;

        if (!registry.containsKey(key) && isReplayOrVirtualContext()) {
            if (LOGGED_MISSING_KEYS.add(keyString)) {
                RIMULIB_FIX_LOGGER.warn(
                        "[RimulibFlashbackFix] Safely bypassed missing dimension key in virtual/replay environment: {}",
                        keyString
                );
            }
            cir.setReturnValue(null);
        }
    }
}
