package org.agmas.harpysmp.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.agmas.harpysmp.Harpysmp;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DeathbanWorldComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<DeathbanWorldComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Harpysmp.MOD_ID, "deathban"), DeathbanWorldComponent.class);
    private final World world;
    public boolean enabled = false;

    public void reset() {
        this.enabled = false;
        this.sync();
    }

    public DeathbanWorldComponent(World world) {
        this.world = world;
    }

    public void sync() {
        KEY.sync(this.world);
    }

    public void serverTick() {
        sync();
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putBoolean("enabled", this.enabled);
    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.enabled = tag.contains("enabled") && tag.getBoolean("enabled");
    }

}
