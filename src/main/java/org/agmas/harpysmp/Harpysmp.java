package org.agmas.harpysmp;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.agmas.harpysmp.components.DeathbanWorldComponent;
import org.agmas.harpysmp.components.HarpyLivesComponent;
import org.agmas.holo.state.HoloPlayerComponent;
import org.agmas.holo.util.FakestPlayer;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class Harpysmp implements ModInitializer {

    public static String MOD_ID = "harpysmp";
    public static String ASFERIA_UUID = "f66d366b-a3c0-491f-aa26-5cdb0466e060";
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("setLives").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(CommandManager.argument("player", EntityArgumentType.player()).then(CommandManager.argument("lives", IntegerArgumentType.integer(0)).executes((commandContext)->{
                ServerPlayerEntity entity = EntityArgumentType.getPlayer(commandContext, "player");
                int lives = IntegerArgumentType.getInteger(commandContext, "lives");
                HarpyLivesComponent.KEY.get(entity).lives = lives;
                for (ServerPlayerEntity player : entity.getServer().getPlayerManager().getPlayerList()) {
                    player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, entity));
                }
                return 1;
            }))));
            dispatcher.register(CommandManager.literal("setGracePeriod").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(CommandManager.argument("player", EntityArgumentType.player()).then(CommandManager.argument("lives", IntegerArgumentType.integer(0)).executes((commandContext)->{
                ServerPlayerEntity entity = EntityArgumentType.getPlayer(commandContext, "player");
                int lives = IntegerArgumentType.getInteger(commandContext, "lives");
                HarpyLivesComponent.KEY.get(entity).graceTime = Date.from(Instant.now().plus(lives, ChronoUnit.SECONDS)).getTime();
                return 1;
            }))));
            dispatcher.register(CommandManager.literal("deathban").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes((commandContext)->{
                DeathbanWorldComponent.KEY.get(commandContext.getSource().getServer().getOverworld()).enabled = !DeathbanWorldComponent.KEY.get(commandContext.getSource().getServer().getOverworld()).enabled;
                return 1;
            }));
//            dispatcher.register(CommandManager.literal("toggleFlight").requires(serverCommandSource -> serverCommandSource.getPlayer().getUuid().equals(UUID.fromString(ASFERIA_UUID))).executes((commandContext)->{
//                commandContext.getSource().getPlayer().getAbilities().allowFlying = !commandContext.getSource().getPlayer().getAbilities().allowFlying;
//                commandContext.getSource().getPlayer().sendAbilitiesUpdate();
//                return 1;
//            }));
            dispatcher.register(CommandManager.literal("toggleInvis").requires(serverCommandSource -> serverCommandSource.getPlayer().getUuid().equals(UUID.fromString(ASFERIA_UUID))).executes((commandContext)->{
                HarpyLivesComponent.KEY.get(commandContext.getSource().getPlayer()).invisible = !HarpyLivesComponent.KEY.get(commandContext.getSource().getPlayer()).invisible;
                commandContext.getSource().getPlayer().sendMessage(Text.of(HarpyLivesComponent.KEY.get(commandContext.getSource().getPlayer()).invisible+""),true);
                return 1;
            }));
//            dispatcher.register(CommandManager.literal("setNickname").then(CommandManager.argument("name", StringArgumentType.string()).executes((commandContext)->{
//                String name = StringArgumentType.getString(commandContext, "name");
//                if (name.length() > 16) name = name.substring(0,16);
//                HarpyLivesComponent.KEY.get(commandContext.getSource().getPlayer()).nickname = name;
//                HarpyLivesComponent.KEY.get(commandContext.getSource().getPlayer()).sync();
//                for (ServerPlayerEntity player : commandContext.getSource().getPlayer().getServer().getPlayerManager().getPlayerList()) {
//                    player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, (ServerPlayerEntity) commandContext.getSource().getPlayer()));
//                }
//                return 1;
//            })));
        });


        ServerLivingEntityEvents.AFTER_DEATH.register(((serverPlayerEntity, damageSource) -> {
            if (!(serverPlayerEntity instanceof ServerPlayerEntity)) return;
            if (serverPlayerEntity.getUuid().equals(ASFERIA_UUID)) return;
            if (serverPlayerEntity.getAttacker() instanceof PlayerEntity killer && HarpyLivesComponent.KEY.get(serverPlayerEntity).lives > 0) {
                if (HoloPlayerComponent.KEY.get(serverPlayerEntity).inHoloMode) return;
                if (serverPlayerEntity instanceof FakestPlayer) return;
                if (serverPlayerEntity == killer) return;
                if (killer.getUuid().equals(UUID.fromString(ASFERIA_UUID))) return;
                if (HarpyLivesComponent.KEY.get(serverPlayerEntity).graceTime > Date.from(Instant.now()).getTime()) {

                    long time = HarpyLivesComponent.KEY.get(serverPlayerEntity).graceTime - Date.from(Instant.now()).getTime();
                    time = time / 1000;
                    time = time / 60;
                    serverPlayerEntity.getServer().getPlayerManager().broadcast(serverPlayerEntity.getDisplayName().copy().append(Text.literal( "'s death did not count due to their grace period of ").append(Text.literal(time+""))).append(" minutes."), false);
                    return;
                }
                if (HarpyLivesComponent.KEY.get(killer).graceTime > Date.from(Instant.now()).getTime()) {

                    long time = HarpyLivesComponent.KEY.get(killer).graceTime - Date.from(Instant.now()).getTime();
                    time = time / 1000;
                    time = time / 60;
                    serverPlayerEntity.getServer().getPlayerManager().broadcast(serverPlayerEntity.getDisplayName().copy().append(Text.literal( "'s death did not count due to their killer's grace period of ").append(Text.literal(time+""))).append(" minutes."), false);
                    return;
                }
                HarpyLivesComponent.KEY.get(serverPlayerEntity).lives--;
                HarpyLivesComponent.KEY.get(serverPlayerEntity).graceTime = Date.from(Instant.now().plus(1, ChronoUnit.HOURS)).getTime();
                HarpyLivesComponent.KEY.get(serverPlayerEntity).sync();
                if (HarpyLivesComponent.KEY.get(serverPlayerEntity).lives == 0) {
                    for (ServerPlayerEntity player : serverPlayerEntity.getServer().getPlayerManager().getPlayerList()) {
                        player.playSoundToPlayer(SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.MASTER, 1, 1);
                    }
                } else {
                    for (ServerPlayerEntity player : serverPlayerEntity.getServer().getPlayerManager().getPlayerList()) {
                        player.playSoundToPlayer(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 1, 1);
                    }
                }
                for (ServerPlayerEntity player : serverPlayerEntity.getServer().getPlayerManager().getPlayerList()) {
                    player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, (ServerPlayerEntity) serverPlayerEntity));
                }
                return;
            }
            return;
        }));
    }

    public static Color colorFromLives(int lives) {
        return switch (lives) {
            case 1 -> Color.RED;
            case 2 -> Color.YELLOW;
            case 3 -> Color.GREEN;
            case 4 -> Color.GREEN.darker();
            default -> Color.GRAY;
        };
    }
}
