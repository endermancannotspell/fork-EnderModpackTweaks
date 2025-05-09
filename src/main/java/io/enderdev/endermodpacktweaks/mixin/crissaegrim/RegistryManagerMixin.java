package io.enderdev.endermodpacktweaks.mixin.crissaegrim;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import epicsquid.crissaegrim.RegistryManager;
import epicsquid.mysticallib.event.RegisterFXEvent;
import io.enderdev.endermodpacktweaks.patches.mysticallib.EffectCut;
import io.enderdev.endermodpacktweaks.patches.mysticallib.EffectManager;
import io.enderdev.endermodpacktweaks.patches.mysticallib.FXRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(value = RegistryManager.class, remap = false)
public class RegistryManagerMixin {
    @Shadow
    public static int FX_CUT;

    @WrapMethod(method = "registerFX")
    public void registerFX(RegisterFXEvent event, Operation<Void> original) {
        /*
         * NEVER TOUCH THIS METHOD. YES, INTELLIJ SAYS IT CAN BE TRANSFORMED
         * INTO A LAMBDA, BUT THIS CAUSES ISSUES ON SERVER SIDE.
         */
        FX_CUT = FXRegistry.registerEffect(new Function<NBTTagCompound, Void>() {
            public Void apply(NBTTagCompound t) {
                EffectCut slash = new EffectCut(Minecraft.getMinecraft().world.provider.getDimension());
                slash.read(t);
                EffectManager.addEffect(slash);
                return null;
            }
        });
    }
}
