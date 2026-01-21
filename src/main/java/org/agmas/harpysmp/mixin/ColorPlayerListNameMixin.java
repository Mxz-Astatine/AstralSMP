package org.agmas.harpysmp.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import org.agmas.harpysmp.Harpysmp;
import org.agmas.harpysmp.components.HarpyLivesComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public class ColorPlayerListNameMixin {
    @Inject(method = "getPlayerListName", at = @At("RETURN"), cancellable = true)
    public void harpysmp$changeName(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(((PlayerEntity)(Object)this).getDisplayName());
    }
}
