package net.trollyloki.minigames.library.utils;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PlayerScoreboard {

    private static final String NAME = "game";

    private final Scoreboard scoreboard;
    private final Objective objective;
    private ArrayList<String> oldLines;
    private final Team team;

    /**
     * Constructs a new player scoreboard
     *
     * @param manager Scoreboard manager
     */
    public PlayerScoreboard(ScoreboardManager manager) {
        this.scoreboard = manager.getNewScoreboard();
        this.oldLines = new ArrayList<>();
        this.objective = scoreboard.registerNewObjective(NAME, "dummy", NAME);
        this.team = scoreboard.registerNewTeam(NAME);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Gets the Bukkit scoreboard associated with this game scoreboard
     *
     * @return Scoreboard
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    /**
     * Gets the objective associated with this scoreboard
     *
     * @return Objective
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * Sets the title of this scoreboard
     *
     * @param title New title
     */
    public void setTitle(String title) {
        getObjective().setDisplayName(title);
    }

    /**
     * Sets the lines on this scoreboard
     *
     * @param lines New lines
     */
    public void setLines(ArrayList<String> lines) {

        HashMap<String, Integer> add = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (add.containsKey(line)) { // if line has a duplicate name add §r
                while (add.containsKey(line))
                    line += ChatColor.RESET;
                lines.set(i, line);
            }
            add.put(line, lines.size() - i);
        }

        LinkedList<String> remove = new LinkedList<>();
        for (int i = 0; i < oldLines.size(); i++) {
            String line = oldLines.get(i);
            Integer newIndex = add.get(line);
            if (newIndex == null) // line no longer exists so remove it
                remove.add(line);
            else if (newIndex == oldLines.size() - i) // line already exists so don't add it
                add.remove(line);
        }

        oldLines = lines; // update list of old lines
        for (String line : remove)
            getScoreboard().resetScores(line);
        for (Map.Entry<String, Integer> line : add.entrySet())
            getObjective().getScore(line.getKey()).setScore(line.getValue());

    }

    /**
     * Gets the team associated with this scoreboard
     *
     * @return Team
     */
    public Team getTeam() {
        return team;
    }

}
