package org.agmas.harpysmp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import org.agmas.harpysmp.Harpysmp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(TeleportCommand.class)
public abstract class AsferiaTPCommandMixin {

    @WrapOperation(method = "method_13763", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;hasPermissionLevel(I)Z"))
    private static boolean allowAsferia2(ServerCommandSource instance, int level, Operation<Boolean> original) {
        if (instance.getPlayer() != null) {
            if (instance.getPlayer().getUuid().equals(UUID.fromString(Harpysmp.ASFERIA_UUID))) return true;
        }
        return original.call(instance,level);
    }
    @WrapOperation(method = "method_13764", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;hasPermissionLevel(I)Z"))
    private static boolean allowAsferia(ServerCommandSource instance, int level, Operation<Boolean> original) {
        if (instance.getPlayer() != null) {
            if (instance.getPlayer().getUuid().equals(UUID.fromString(Harpysmp.ASFERIA_UUID))) return true;
        }
        return original.call(instance,level);
    }
}
