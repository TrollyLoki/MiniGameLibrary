package net.trollyloki.minigames.library.managers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Listener class for passing events to parties
 */
public class MiniGameManager implements Listener {

    private final Plugin plugin;
    private final Map<UUID, Party> parties;
    private final Map<UUID, Game> games;

    /**
     * Constructs a new party listener
     *
     * @param plugin Plugin
     */
    public MiniGameManager(Plugin plugin) {
        this.plugin = plugin;
        this.parties = new HashMap<>();
        this.games = new HashMap<>();
    }

    /**
     * Gets the plugin of this party listener
     *
     * @return Plugin
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Gets the Bukkit {@link Player} instance of a player
     *
     * @param uuid UUID
     * @return Bukkit player
     */
    public Player getOnlinePlayer(UUID uuid) {
        return getPlugin().getServer().getPlayer(uuid);
    }

    /**
     * Gets a set of Bukkit {@link Player Players} instances from a set of uuids
     *
     * @param uuids UUIDs
     * @return Set of Bukkit players
     */
    public Set<Player> getOnlinePlayers(Set<UUID> uuids) {
        HashSet<Player> onlinePlayers = new HashSet<>();
        for (UUID uuid : uuids) {
            Player player = getOnlinePlayer(uuid);
            if (player != null)
                onlinePlayers.add(player);
        }
        return Collections.unmodifiableSet(onlinePlayers);
    }

    /**
     * Checks if the given player is in a party
     *
     * @param player Player
     * @return {@code true} if the player is in a party
     */
    public boolean inParty(UUID player) {
        return parties.containsKey(player);
    }

    /**
     * Gets the party that the given player is in
     *
     * @param player Player
     * @return Possibly null party
     */
    public Party getParty(UUID player) {
        return parties.get(player);
    }

    /**
     * Checks if the given player is in a game
     *
     * @param player Player
     * @return {@code true} if the player is in a party
     */
    public boolean inGame(UUID player) {
        return games.containsKey(player);
    }

    /**
     * Gets the game that the given player is in
     *
     * @param player Player
     * @return Possibly null game
     */
    public Game getGame(UUID player) {
        return games.get(player);
    }

    /**
     * Puts the given player in the given party
     *
     * @param player Player
     * @param party Party
     * @return {@code true} if the player was not already in a party
     */
    boolean joinParty(UUID player, Party party) {
        return parties.putIfAbsent(player, party) == null;
    }

    /**
     * Removes the given player from their party
     *
     * @param player Player
     * @return {@code true} if the player was in a party
     */
    boolean leaveParty(UUID player) {
        return parties.remove(player) != null;
    }

    /**
     * Puts the given player in the given game
     *
     * @param player Player
     * @param game Game
     * @return {@code true} if the player was not already in a game
     */
    boolean joinGame(UUID player, Game game) {
        return games.putIfAbsent(player, game) == null;
    }

    /**
     * Removes the given player from their game
     *
     * @param player Player
     * @return {@code true} if the player was in a game
     */
    boolean leaveGame(UUID player) {
        return games.remove(player) != null;
    }

    /**
     * Updates all scoreboards in all active games
     */
    public void updateScoreboards() {
        Set<Game> gameSet = new HashSet<>(games.values());
        for (Game game : gameSet)
            game.getScoreboard().updateScoreboards();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onPlayerJoin(event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onPlayerQuit(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onPlayerMove(event);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onPlayerDropItem(event);
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onPlayerGameModeChange(event);
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onPlayerToggleFlight(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onPlayerInteract(event);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onPlayerInteractEntity(event);
    }

    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onPlayerArmorStandManipulate(event);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Game game = getGame(event.getEntity().getUniqueId());
            if (game != null)
                game.onPlayerShootBow(event);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Game game = getGame(((Player) event.getEntity().getShooter()).getUniqueId());
            if (game != null)
                game.onProjectileHit(event);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Game game = getGame(event.getEntity().getUniqueId());
            if (game != null)
                game.onPlayerDamage(event);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Game game = getGame(event.getEntity().getUniqueId());
            if (game != null)
                game.onPlayerDamageByEntity(event);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onBlockPlace(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Game game = getGame(event.getPlayer().getUniqueId());
        if (game != null)
            game.onBlockBreak(event);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        for (Game game : new HashSet<>(games.values()))
            game.onEntityChangeBlock(event);
    }

}
