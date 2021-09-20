package net.trollyloki.minigames.library.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.trollyloki.minigames.library.managers.MiniGameManager;
import net.trollyloki.minigames.library.managers.Party;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class PartyCommand implements CommandExecutor, TabCompleter {

    public static final String HIJACK_PERMISSION = "party.hijack";

    private final MiniGameManager manager;

    public PartyCommand(MiniGameManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return false;
        }

        if (args.length > 0) {

            Player player = (Player) sender;
            Party party = manager.getParty(player.getUniqueId());

            if (args[0].equalsIgnoreCase("list")) {

                if (party == null) {
                    sender.sendMessage(ChatColor.RED + "You are not in a party");
                    return false;
                }

                List<UUID> players = new LinkedList<>(party.getPlayers());
                List<String> playerNames = new LinkedList<>();
                List<String> moderatorNames = new LinkedList<>();
                for (UUID uuid : players) {
                    OfflinePlayer offlinePlayer = sender.getServer().getOfflinePlayer(uuid);
                    String name = (offlinePlayer.isOnline() ? ChatColor.GREEN : ChatColor.RED)
                            + offlinePlayer.getName() + ChatColor.RESET; // color name based on online status
                    if (party.isModerator(uuid)) // sort into moderators and players
                        moderatorNames.add(name);
                    else
                        playerNames.add(name);
                }

                display(sender, ChatColor.YELLOW + "Moderators: " + String.join(", ", moderatorNames),
                        ChatColor.YELLOW + "Players: " + String.join(", ", playerNames));
                return true;

            }

            else if (args[0].equalsIgnoreCase("remove")) {

                if (args.length > 1) {

                    if (party == null) {
                        sender.sendMessage(ChatColor.RED + "You are not in a party");
                        return false;
                    }

                    if (!party.isModerator(player.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "Only party moderators can remove players");
                        return false;
                    }

                    OfflinePlayer removee = getOfflinePlayer(args[1]);
                    if (removee == null || !party.remove(removee.getUniqueId())) { // player is not in the party
                        sender.sendMessage(ChatColor.RED + (removee != null ? removee.getName() : args[1]) + " is not in the party");
                        return false;
                    } else { // player was removed from the party
                        display(party, ChatColor.YELLOW + player.getName() + " removed " + removee.getName() + " from the party");
                        if (removee.isOnline())
                            display(removee.getPlayer(), ChatColor.YELLOW + "You were removed from the party");
                        return true;
                    }

                }

                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " remove <player>");
                return false;

            }

            else if (args[0].equalsIgnoreCase("disband")) {

                if (party == null) {
                    sender.sendMessage(ChatColor.RED + "You are not in a party");
                    return false;
                }

                if (!party.isModerator(player.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "Only party moderators can disband the party");
                    return false;
                }

                for (UUID uuid : party.getPlayers()) {
                    party.remove(uuid);
                    Player removee = manager.getPlugin().getServer().getPlayer(uuid);
                    if (removee != null)
                        display(removee, ChatColor.YELLOW + player.getName() + " disbanded the party");
                }
                return true;

            }

            else if (args[0].equalsIgnoreCase("join")) {

                if (args.length > 1) {

                    if (party != null) {
                        sender.sendMessage(ChatColor.RED + "You are already in a party");
                        return false;
                    }

                    OfflinePlayer join = getOfflinePlayer(args[1]);
                    Party joinParty = join != null ? manager.getParty(join.getUniqueId()) : null;
                    if (joinParty == null) { // player is not found or not in a party
                        sender.sendMessage(ChatColor.RED + (join != null ? join.getName() : args[1]) + " is not in a party");
                        return false;
                    } else {

                        if (joinParty.isInvited(player.getUniqueId())) { // player has been invited
                            joinParty.add(player.getUniqueId());
                            display(joinParty, ChatColor.YELLOW + player.getName() + " joined the party");
                            return true;
                        } else { // player has not been invited
                            sender.sendMessage(ChatColor.RED + "You have not been invited to " + join.getName() + "'s party");
                            return false;
                        }

                    }

                }

                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " join <player>");
                return false;

            }

            else if (args[0].equalsIgnoreCase("hijack") && sender.hasPermission("party.hijack")) {

                if (args.length > 1) {

                    if (party != null) {
                        sender.sendMessage(ChatColor.RED + "You are already in a party");
                        return false;
                    }

                    OfflinePlayer join = getOfflinePlayer(args[1]);
                    Party joinParty = join != null ? manager.getParty(join.getUniqueId()) : null;
                    if (joinParty == null) { // player is not found or not in a party
                        sender.sendMessage(ChatColor.RED + (join != null ? join.getName() : args[1]) + " is not in a party");
                        return false;
                    } else {

                        joinParty.add(player.getUniqueId());
                        joinParty.promote(player.getUniqueId());
                        display(joinParty, ChatColor.YELLOW + player.getName() + " hijacked the party");
                        return true;

                    }

                }

                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " hijack <player>");
                return false;

            }

            else if (args[0].equalsIgnoreCase("leave")) {

                if (party == null) {
                    sender.sendMessage(ChatColor.RED + "You are not in a party");
                    return false;
                }

                party.remove(player.getUniqueId());
                display(party, ChatColor.YELLOW + player.getName() + " left the party");
                display(sender, ChatColor.YELLOW + "You left the party");
                return true;

            }

            else if (args[0].equalsIgnoreCase("promote")) {

                if (args.length > 1) {

                    if (party == null) {
                        sender.sendMessage(ChatColor.RED + "You are not in a party");
                        return false;
                    }

                    if (!party.isModerator(player.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "Only party moderators can promote players");
                        return false;
                    }

                    OfflinePlayer promotee = getOfflinePlayer(args[1]);
                    if (promotee == null || !party.contains(promotee.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + (promotee != null ? promotee.getName() : args[1]) + " is not in the party");
                        return false;
                    }

                    if (party.promote(promotee.getUniqueId())) {
                        display(party, ChatColor.YELLOW + player.getName() + " promoted " + promotee.getName());
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + promotee.getName() + " is already a party moderator");
                        return false;
                    }

                }

                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " promote <player>");
                return false;

            }

            else if (args[0].equalsIgnoreCase("demote")) {

                if (args.length > 1) {

                    if (party == null) {
                        sender.sendMessage(ChatColor.RED + "You are not in a party");
                        return false;
                    }

                    if (!party.isModerator(player.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "Only party moderators can demote players");
                        return false;
                    }

                    OfflinePlayer demotee = getOfflinePlayer(args[1]);
                    if (demotee == null || !party.contains(demotee.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + (demotee != null ? demotee.getName() : args[1]) + " is not in the party");
                        return false;
                    }

                    if (party.demote(demotee.getUniqueId())) {
                        display(party, ChatColor.YELLOW + player.getName() + " demoted " + demotee.getName());
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + demotee.getName() + " is not a party moderator");
                        return false;
                    }

                }

                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " promote <player>");
                return false;

            }

            else {

                int i = args[0].equalsIgnoreCase("add") ? 1 : 0;
                if (args.length <= i) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " add <player>");
                    return false;
                }

                if (party == null) {
                    party = new Party(manager); // create a new party with the current player as a moderator
                    party.add(player.getUniqueId());
                    party.promote(player.getUniqueId());
                }

                if (!party.isModerator(player.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "Only party moderators can invite new players");
                    return false;
                }

                Collection<? extends Player> players = args[i].equalsIgnoreCase("all")
                        ? manager.getPlugin().getServer().getOnlinePlayers()
                        : Collections.singleton(sender.getServer().getPlayerExact(args[i])); // find online player with given name
                for (Player invitee : players) {
                    if (invitee == null) {
                        sender.sendMessage(ChatColor.RED + args[i] + " is not online");
                        continue;
                    }

                    try {
                        if (party.invite(invitee.getUniqueId())) { // player was invited
                            display(party, ChatColor.YELLOW + player.getName() + " invited " + invitee.getName() + " to the party");

                            TextComponent click = new TextComponent(ChatColor.GOLD + "Click here to accept");
                            click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + player.getName()));
                            click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to join")));
                            display(invitee, new TextComponent(ChatColor.YELLOW + player.getName() + " invited you to their party. "), click);

                        } else { // player was already invited
                            sender.sendMessage(ChatColor.RED + invitee.getName() + " has already been invited to the party");
                        }
                    } catch (IllegalStateException e) { // player is already in the party
                        sender.sendMessage(ChatColor.RED + invitee.getName() + " is already in the party");
                    }
                }
                return true;

            }

        }

        String usage = ChatColor.RED + "Usage: /" + label + " <list|add|remove|disband|join";
        if (sender.hasPermission(HIJACK_PERMISSION))
            usage += "|hijack";
        usage += "|leave|promote|demote>";
        sender.sendMessage(usage);
        return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length > 1) {

            if (args[0].equalsIgnoreCase("list")
                    || args[0].equalsIgnoreCase("disband")
                    || args[0].equalsIgnoreCase("leave")) {
                return new ArrayList<>();
            }

            else if (args[0].equalsIgnoreCase("add")
                    || args[0].equalsIgnoreCase("remove")
                    || args[0].equalsIgnoreCase("join")
                    || (args[0].equalsIgnoreCase("hijack") && sender.hasPermission(HIJACK_PERMISSION))
                    || args[0].equalsIgnoreCase("promote")
                    || args[0].equalsIgnoreCase("demote")) {

                if (args.length > 2) {
                    return new ArrayList<>();
                }

                return getFilteredPlayers(args[1]);

            }

        }

        ArrayList<String> list = new ArrayList<>();
        list.add("list");
        list.add("add");
        list.add("remove");
        list.add("disband");
        list.add("join");
        if (sender.hasPermission(HIJACK_PERMISSION))
            list.add("hijack");
        list.add("leave");
        list.add("promote");
        list.add("demote");

        if (args.length > 0)
            filter(list, args[0]);
        return list;

    }

    private OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer player = manager.getPlugin().getServer().getPlayerExact(name); // find online player with given name
        if (player == null) { // if offline, search usercache for player with given name
            for (OfflinePlayer p : manager.getPlugin().getServer().getOfflinePlayers()) {
                if (name.equalsIgnoreCase(p.getName())) {
                    player = p;
                    break;
                }
            }
        }
        return player;
    }

    private void filter(List<String> list, String start) {
        String finalStart = start.toLowerCase();
        list.removeIf(string -> !string.toLowerCase().startsWith(finalStart));
    }

    private List<String> getFilteredPlayers(String start) {
        start = start.toLowerCase();

        ArrayList<String> list = new ArrayList<>();
        for (Player player : manager.getPlugin().getServer().getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(start))
                list.add(player.getName());
        }

        return list;
    }

    private static void separator(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "----------------------------------------");
    }

    private void display(CommandSender sender, BaseComponent... messages) {
        separator(sender);
        sender.spigot().sendMessage(messages);
        separator(sender);
    }

    private void display(CommandSender sender, String... messages) {
        separator(sender);
        for (String message : messages)
            sender.sendMessage(message);
        separator(sender);
    }

    private void display(Party party, String... messages) {
        for (UUID uuid : party.getPlayers()) {
            Player player = manager.getPlugin().getServer().getPlayer(uuid);
            if (player != null)
                display(player, messages);
        }
    }

}
