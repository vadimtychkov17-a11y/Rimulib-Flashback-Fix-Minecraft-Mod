package com.fix.rimulib.mixin;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Registry.class)
public interface ExampleMixin<T> {

    @Shadow
    T getValue(ResourceKey<T> key);

    @Inject(method = "getValueOrThrow", at = @At("HEAD"), cancellable = true)
    private void onGetValueOrThrow(ResourceKey<T> key, CallbackInfoReturnable<T> cir) {
        if (key != null && key.toString().contains("gravitia_stage")) {
            T value = this.getValue(key);
            if (value == null) {
                cir.setReturnValue(null);
            }
        }
    }
}
