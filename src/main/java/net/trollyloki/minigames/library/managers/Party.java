package net.trollyloki.minigames.library.managers;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * Represents a group of players
 */
public class Party {

    private final MiniGameManager manager;
    private final Set<UUID> players;
    private final Set<UUID> moderators;
    private final Set<UUID> invitees;
    private Game game;

    /**
     * Constructs a new party
     *
     * @param manager Mini-game manager
     */
    public Party(MiniGameManager manager) {
        this.manager = manager;
        this.players = new HashSet<>();
        this.moderators = new HashSet<>();
        this.invitees = new HashSet<>();
        this.game = null;
    }

    /**
     * Gets the set of players that are in this party
     *
     * @return Set of players
     */
    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    /**
     * Gets a set of online players in this party
     *
     * @return Set of online players
     */
    public Set<Player> getOnlinePlayers() {
        return manager.getOnlinePlayers(players);
    }

    /**
     * Gets the set of players that are moderators of this party
     *
     * @return Set of players
     */
    public Set<UUID> getModerators() {
        return Collections.unmodifiableSet(moderators);
    }

    /**
     * Adds the given player to this party
     *
     * @param player Player
     * @return {@code true} if the player was not already in this party
     * @throws IllegalStateException If the player is in another party
     */
    public boolean add(UUID player) throws IllegalStateException {
        if (!manager.joinParty(player, this))
            throw new IllegalStateException("Given player is in another party");
        invitees.remove(player);
        return players.add(player);
    }

    /**
     * Removes the given player from this party
     *
     * @param player Player
     * @return {@code true} if the player was in this party
     */
    public boolean remove(UUID player) {
        if (players.remove(player)) {
            if (!manager.leaveParty(player)) // this should never be true unless something has gone very wrong
                manager.getPlugin().getLogger()
                        .warning("Party object vs MiniGameManager mismatch for player " + player);
            return true;
        }
        return false;
    }

    /**
     * Checks if the given player is in this party
     *
     * @param player Player
     * @return {@code true} if the player is in this party
     */
    public boolean contains(UUID player) {
        return players.contains(player);
    }

    /**
     * Gets the amount of players that are in this party
     *
     * @return Amount of players
     */
    public int size() {
        return players.size();
    }

    /**
     * Checks if the given player is a moderator of this party
     *
     * @param player Player
     * @return {@code true} if the player is a moderator of this party
     */
    public boolean isModerator(UUID player) {
        return moderators.contains(player);
    }

    /**
     * Promotes the given player to a moderator of this party
     *
     * @param player Player
     * @return {@code true} if the player was not already a moderator of this party
     * @throws IllegalStateException If the player is not in this party
     */
    public boolean promote(UUID player) throws IllegalStateException {
        if (!players.contains(player))
            throw new IllegalStateException("Given player is not in this party");
        return moderators.add(player);
    }

    /**
     * Demotes the given player from a moderator of this party
     *
     * @param player Player
     * @return {@code true} if the player was a moderator of this party
     * @throws IllegalStateException If the player is not in this party
     */
    public boolean demote(UUID player) throws IllegalStateException {
        if (!players.contains(player))
            throw new IllegalStateException("Given player is not in this party");
        return moderators.remove(player);
    }

    /**
     * Invites the given player to this party
     *
     * @param player Player
     * @return {@code true} if the player was not already invited to this party
     * @throws IllegalStateException If the player is already in this party
     */
    public boolean invite(UUID player) throws IllegalStateException {
        if (players.contains(player))
            throw new IllegalStateException("Given player is already in this party");
        return invitees.add(player);
    }

    /**
     * Uninvites the given player from this party
     *
     * @param player Player
     * @return {@code true} if the player was invited to this party
     * @throws IllegalStateException If the player is already in this party
     */
    public boolean uninvite(UUID player) throws IllegalStateException {
        if (players.contains(player))
            throw new IllegalStateException("Given player is already in this party");
        return invitees.remove(player);
    }

    /**
     * Checks if the given player is invited to this party
     *
     * @param player Player
     * @return {@code true} if the player is invited to this party
     */
    public boolean isInvited(UUID player) {
        return invitees.contains(player);
    }

    /**
     * Gets the game that this party is currently in
     *
     * @return Game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Sets the game that this party is currently in
     *
     * @param game Game
     */
    void setGame(Game game) {
        this.game = game;
    }

}
