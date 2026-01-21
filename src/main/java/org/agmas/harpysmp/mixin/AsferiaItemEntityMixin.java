package org.agmas.harpysmp.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.harpysmp.components.HarpyLivesComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class AsferiaItemEntityMixin {

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    public void phantomHud(PlayerEntity player, CallbackInfo ci) {
        if (HarpyLivesComponent.KEY.get(player).invisible) {
            ci.cancel();
        }
    }
}
