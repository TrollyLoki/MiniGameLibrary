package net.trollyloki.minigames.library.managers;

import net.trollyloki.minigames.library.utils.GameScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Represents a game
 */
public abstract class Game extends BukkitRunnable implements AutoCloseable {

    private final MiniGameManager manager;
    private final HashSet<UUID> players;
    private final GameScoreboard scoreboard;
    private boolean invisibility = false;

    /**
     * Constructs a new game
     */
    public Game(MiniGameManager manager) {
        this.manager = manager;
        this.players = new HashSet<>();
        this.scoreboard = new GameScoreboard(manager);

        runTaskTimer(manager.getPlugin(), 0, 0);
    }

    /**
     * Gets a set of players in this game
     *
     * @return Set of players
     */
    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    /**
     * Gets a set of online players in this game
     *
     * @return Set of online players
     */
    public Set<Player> getOnlinePlayers() {
        return manager.getOnlinePlayers(players);
    }

    /**
     * Adds the given player to this game
     *
     * @param player Player
     * @return {@code true} if the player was not already in this game
     * @throws IllegalStateException If the player is in another game
     */
    public boolean add(UUID player) throws IllegalStateException {
        if (!manager.joinGame(player, this))
            throw new IllegalStateException("Given player is in another game");
        return players.add(player);
    }

    /**
     * Adds all the players in the given party to this game
     *
     * @param party Party
     * @return Number of players added
     */
    public int addAll(Party party) {
        int count = 0;
        for (UUID player : party.getPlayers()) {
            if (add(player))
                count++;
        }
        return count;
    }

    /**
     * Removes the given player from this game
     *
     * @param player Player
     * @return {@code true} if the player was in this game
     */
    public boolean remove(UUID player) {
        if (players.remove(player)) {
            Player p = manager.getOnlinePlayer(player);
            if (p != null)
                p.setScoreboard(manager.getPlugin().getServer().getScoreboardManager().getMainScoreboard());

            if (!manager.leaveGame(player)) // this should never be true unless something has gone very wrong
                manager.getPlugin().getLogger()
                        .warning("Game object vs MiniGameManager mismatch for player " + player);
            return true;
        }
        return false;
    }

    /**
     * Checks if the given player is in this game
     *
     * @param player Player
     * @return {@code true} if the player is in this game
     */
    public boolean contains(UUID player) {
        return players.contains(player);
    }

    /**
     * Gets the amount of players that are in this game
     *
     * @return Amount of players
     */
    public int size() {
        return players.size();
    }

    /**
     * Gets the game scoreboard for this game
     *
     * @return Game scoreboard
     */
    public GameScoreboard getScoreboard() {
        return scoreboard;
    }

    /**
     * Sets invisibility
     *
     * @param value {@code true} if players should be able to see other players
     */
    public void setInvisibility(boolean value) {
        this.invisibility = value;
        Set<Player> players = getOnlinePlayers();
        for (Player player : players) {
            for (Player p : players) {
                if (p != player) {
                    if (value)
                        player.hidePlayer(manager.getPlugin(), p);
                    else
                        player.showPlayer(manager.getPlugin(), p);
                }
            }
        }
    }

    /**
     * Removes all players from this game and unregisters it
     */
    public void close() {
        cancel();
        setInvisibility(false);
        for (UUID player : new HashSet<>(players)) {
            getScoreboard().remove(player);
            remove(player);
        }
    }

    /**
     * This will be called every tick
     */
    @Override
    public void run() {

    }

    /**
     * This will be called when a player in this game joins the server
     *
     * @param event PlayerJoinEvent
     */
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    /**
     * This will be called when a player in this game quits the server
     *
     * @param event PlayerQuitEvent
     */
    public void onPlayerQuit(PlayerQuitEvent event) {

    }

    /**
     * This will be called when a player in this game moves
     *
     * @param event PlayerMoveEvent
     */
    public void onPlayerMove(PlayerMoveEvent event) {

    }

    /**
     * This will be called when a player in this game drops an item
     *
     * @param event PlayerDropItemEvent
     */
    public void onPlayerDropItem(PlayerDropItemEvent event) {

    }

    /**
     * This will be called when a player in this game changes game mode
     *
     * @param event PlayerGameModeChangeEvent
     */
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {

    }

    /**
     * This will be called when a player in this game toggles flight
     *
     * @param event PlayerToggleFlightEvent
     */
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {

    }

    /**
     * This will be called when a player in this game interacts with something
     *
     * @param event PlayerInteractEvent
     */
    public void onPlayerInteract(PlayerInteractEvent event) {

    }

    /**
     * This will be called when a player in this game interacts with an entity
     *
     * @param event PlayerInteractEntityEvent
     */
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

    }

    /**
     * This will be called when a player in this game manipulates an armor stand
     *
     * @param event PlayerArmorStandManipulateEvent
     */
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {

    }

    /**
     * This will be called when a player in this game shoots a bow
     *
     * @param event EntityShootBowEvent
     */
    public void onPlayerShootBow(EntityShootBowEvent event) {

    }

    /**
     * This will be called when a projectile shot by a player in this game hits something
     *
     * @param event ProjectileHitEvent
     */
    public void onProjectileHit(ProjectileHitEvent event) {

    }

    /**
     * This will be called when a player in this game takes damage
     *
     * @param event EntityDamageEvent
     */
    public void onPlayerDamage(EntityDamageEvent event) {

    }

    /**
     * This will be called when a player in this game is damaged by an entity
     *
     * @param event EntityDamageByEntityEvent
     */
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {

    }

    /**
     * This will be called when a player places a block
     *
     * @param event BlockPlaceEvent
     */
    public void onBlockPlace(BlockPlaceEvent event) {

    }

    /**
     * This will be called when a player breaks a block
     *
     * @param event BlockBreakEvent
     */
    public void onBlockBreak(BlockBreakEvent event) {

    }

    /**
     * This will be called when an entity changes a block
     *
     * @param event EntityChangeBlockEvent
     */
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

    }

}
