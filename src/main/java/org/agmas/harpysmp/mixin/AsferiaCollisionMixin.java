package org.agmas.harpysmp.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.harpysmp.components.HarpyLivesComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class AsferiaCollisionMixin {

    @Inject(method = "collidesWith", at = @At("HEAD"), cancellable = true)
    public void phantomHud(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (other instanceof PlayerEntity) {
            if (HarpyLivesComponent.KEY.get(other).invisible) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
