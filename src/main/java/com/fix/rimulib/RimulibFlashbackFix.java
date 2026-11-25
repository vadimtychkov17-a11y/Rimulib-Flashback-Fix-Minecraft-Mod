package com.fix.rimulib.mixin;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Registry.class)
public interface ExampleMixin<T> {

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
        if (key == null) {
            return;
        }

        String keyString = key.toString();
        if (!isProtectedModDimension(keyString)) {
            return;
        }

        @SuppressWarnings("unchecked")
        Registry<T> registry = (Registry<T>) this;

        if (!registry.containsKey(key) && isReplayOrVirtualContext()) {
            // Берем первый реально существующий элемент из реестра (обычный мир Overworld),
            // чтобы Rimulib получил настоящий объект вместо пустоты (null) и не упал на .type()
            T fallback = registry.stream().findFirst().orElse(null);

            LoggerFactory.getLogger("RimulibFlashbackFix").warn(
                    "[RimulibFlashbackFix] Replaced missing dimension {} with safe fallback: {}",
                    keyString,
                    fallback
            );
            cir.setReturnValue(fallback);
        }
    }
}
