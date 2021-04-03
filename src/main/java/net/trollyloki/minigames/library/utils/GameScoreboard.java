package net.trollyloki.minigames.library.utils;

import net.trollyloki.minigames.library.managers.MiniGameManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class GameScoreboard {

    private final MiniGameManager manager;
    private final HashMap<UUID, PlayerScoreboard> scoreboards;
    private final HashMap<UUID, String> names;
    private boolean nameTagVisibility = true, collisionRule = true;

    /**
     * Constructs a new game scoreboard
     *
     * @param manager Mini game manager
     */
    public GameScoreboard(MiniGameManager manager) {
        this.manager = manager;
        this.scoreboards = new HashMap<>();
        this.names = new HashMap<>();
    }

    /**
     * Adds the given player to this game scoreboard
     *
     * @param player Player
     * @param name Player's name
     * @return {@code true} if the player was added
     */
    public boolean add(UUID player, String name) {
        if (scoreboards.containsKey(player))
            return false;
        scoreboards.put(player, new PlayerScoreboard(manager.getPlugin().getServer().getScoreboardManager()));
        names.put(player, name);
        setTeamOption(Team.Option.NAME_TAG_VISIBILITY, nameTagVisibility);
        setTeamOption(Team.Option.COLLISION_RULE, collisionRule);
        updateScoreboard(player);
        return true;
    }

    /**
     * Removes the given player from this game scoreboard
     *
     * @param player Player
     * @return {@code true} if the player was removed
     */
    public boolean remove(UUID player) {
        if (!scoreboards.containsKey(player))
            return false;
        scoreboards.remove(player);
        String name = names.remove(player);
        for (PlayerScoreboard scoreboard : scoreboards.values())
            scoreboard.getTeam().removeEntry(name);
        updateScoreboard(player);
        return true;
    }

    /**
     * Gets the name of the given player
     *
     * @param player Player
     * @return Name
     */
    public String getName(UUID player) {
        return names.get(player);
    }

    /**
     * Sets a team option for all scoreboards
     *
     * @param option Option
     * @param value Value
     */
    private void setTeamOption(Team.Option option, boolean value) {
        Team.OptionStatus status = value ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER;
        for (PlayerScoreboard scoreboard : scoreboards.values()) {
            Team team = scoreboard.getTeam();
            team.setOption(option, status);
            for (String name : names.values())
                team.addEntry(name);
        }
    }

    /**
     * Sets name tag visibility
     *
     * @param value {@code true} if name tags should be visible
     */
    public void setNameTagVisibility(boolean value) {
        this.nameTagVisibility = value;
        setTeamOption(Team.Option.NAME_TAG_VISIBILITY, value);
    }

    /**
     * Sets collision rule
     *
     * @param value {@code true} if players should be able to push other players
     */
    public void setCollisionRule(boolean value) {
        this.collisionRule = value;
        setTeamOption(Team.Option.COLLISION_RULE, value);
    }

    /**
     * Updates player's scoreboards for this game scoreboard
     */
    public void updateScoreboards() {
        for (UUID player : scoreboards.keySet())
            updateScoreboard(player);
    }

    /**
     * Updates a player's scoreboard for this game scoreboard
     *
     * @param player Player
     * @return If the player's scoreboard was updated
     */
    private boolean updateScoreboard(UUID player) {
        Player p = manager.getOnlinePlayer(player);
        if (p != null) {
            PlayerScoreboard scoreboard = scoreboards.get(player);
            if (scoreboard != null) {
                p.setScoreboard(scoreboard.getScoreboard());
                return true;
            } else {
                p.setScoreboard(manager.getPlugin().getServer().getScoreboardManager().getMainScoreboard());
                return false;
            }
        }
        return false;
    }

    /**
     * Gets the player scoreboard for the given player, creating one if needed
     *
     * @param player Player
     * @return Player scoreboard
     */
    public PlayerScoreboard getPlayerScoreboard(UUID player) {
        return scoreboards.get(player);
    }

    /**
     * Gets all the player scoreboards for this game scoreboard
     *
     * @return Collection of player scoreboards
     */
    public Collection<PlayerScoreboard> getPlayerScoreboards() {
        return Collections.unmodifiableCollection(scoreboards.values());
    }

}
