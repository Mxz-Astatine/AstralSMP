package org.agmas.harpysmp.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;
import org.agmas.harpysmp.Harpysmp;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.UUID;

public class HarpyLivesComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<HarpyLivesComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Harpysmp.MOD_ID, "lives"), HarpyLivesComponent.class);
    private final PlayerEntity player;
    public long graceTime = Date.from(Instant.now().plus(7, ChronoUnit.DAYS)).getTime();
    public int lives = 4;
    public String nickname = null;
    public boolean invisible = false;

    public void reset() {
        this.lives = 4;
        this.sync();
    }

    public HarpyLivesComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void serverTick() {
        if (DeathbanWorldComponent.KEY.get(player.getServer().getOverworld()).enabled) {
            if (!player.isSpectator() && !player.isCreative() && lives <= 0) {
                ((ServerPlayerEntity) player).changeGameMode(GameMode.SPECTATOR);
            }
        } else {
            if (player.isSpectator() && lives <= 0) {
                ((ServerPlayerEntity) player).changeGameMode(GameMode.SURVIVAL);
                TeleportTarget target = ((ServerPlayerEntity) player).getRespawnTarget(false, null);
                ((ServerPlayerEntity) player).teleport(target.world(), target.pos().x, target.pos().y, target.pos().z, target.yaw(), target.pitch());
            }
        }
        if (!player.getUuid().equals(UUID.fromString(Harpysmp.ASFERIA_UUID))) {
            invisible = false;
        }
        sync();
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("lives", this.lives);
        tag.putLong("graceTime", this.graceTime);
        tag.putBoolean("invisible", this.invisible);
        if (nickname != null) {
            tag.putString("nickName", nickname);
        }
    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.lives = tag.contains("lives") ? tag.getInt("lives") : 0;
        this.graceTime = tag.contains("graceTime") ? tag.getLong("graceTime") : 0;
        this.invisible = tag.contains("invisible") && tag.getBoolean("invisible");
        if (!tag.contains("nickName")) {
            nickname = null;
        } else {
            nickname = tag.getString("nickName");
        }
    }

}
