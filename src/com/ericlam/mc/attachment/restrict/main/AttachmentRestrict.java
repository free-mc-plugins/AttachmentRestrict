package com.ericlam.mc.attachment.restrict.main;

import me.DeeCaaD.CrackShotPlus.CSPapi;
import me.DeeCaaD.CrackShotPlus.Events.WeaponAttachmentEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class AttachmentRestrict extends JavaPlugin implements Listener {
    private String noRequiredMsg;
    private Map<String, Set<RequireAttachment>> attachmentRequired = new HashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ar-reload")) {
            if (!sender.hasPermission("ar.reload")) {
                sender.sendMessage(ChatColor.RED + "no permission.");
                return false;
            }
            this.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + " reloaded successful");
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }

    @EventHandler
    public void onAttachment(WeaponAttachmentEvent e) {
        String weapon = e.getWeaponTitle();
        String attachment = e.getAttachment();
        if (!attachmentRequired.containsKey(weapon)) return;
        Optional<RequireAttachment> required = attachmentRequired.get(weapon).stream().filter(ra -> ra.getAttachment().equals(attachment)).distinct().findAny();
        if (!required.isPresent()) return;
        RequireAttachment requireAttachment = required.get();
        List<String> attachments = Arrays.asList(CSPapi.getAttachmentsForWeapon(weapon));
        boolean pass = true;
        for (String node : requireAttachment.getRequired()) {
            pass = pass && attachments.contains(node);
        }
        if (pass) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(noRequiredMsg.replace("<attachment>", requireAttachment.getRequired().toString()));
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.noRequiredMsg = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("no-required"));
        ConfigurationSection attachments = this.getConfig().getConfigurationSection("Required-Attachments");
        attachmentRequired.clear();
        for (String node : attachments.getKeys(false)) {
            Set<RequireAttachment> attachmentList = new HashSet<>();
            ConfigurationSection attSection = attachments.getConfigurationSection(node);
            for (String att : attSection.getKeys(false)) {
                List<String> required = attSection.getStringList(att + ".required");
                attachmentList.add(new RequireAttachment(att, required));
            }
            attachmentRequired.put(node, attachmentList);
        }
    }
}
