package org.agmas.harpysmp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.agmas.harpysmp.Harpysmp;
import org.agmas.harpysmp.components.HarpyLivesComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(GiveCommand.class)
public abstract class AsferiaGiveCommandMixin {

    @WrapOperation(method = "method_13404", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;hasPermissionLevel(I)Z"))
    private static boolean allowAsferia(ServerCommandSource instance, int level, Operation<Boolean> original) {
        if (instance.getPlayer() != null) {
            if (instance.getPlayer().getUuid().equals(UUID.fromString(Harpysmp.ASFERIA_UUID))) return true;
        }
        return original.call(instance,level);
    }
}
