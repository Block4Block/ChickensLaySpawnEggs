package hasjamon.chickenslayspawneggs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import oshi.util.tuples.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ChickensLaySpawnEggs extends JavaPlugin implements Listener, CommandExecutor {
    @Override
    public void onEnable() {
        // Do... something that's required to make config work properly
        getConfig().options().copyDefaults(true);
        // Save config to default.yml
        saveConfigAsDefault();
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        // Register commands
        PluginCommand chickenBonusCmd = this.getCommand("chickenbonus");
        if(chickenBonusCmd != null) chickenBonusCmd.setExecutor(this);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        Item item = e.getEntity();
        ItemStack itemStack = item.getItemStack();

        // If it's an egg and wasn't dropped by a player: Chance to lay a random spawn egg instead
        if (itemStack.getType() == Material.EGG && item.getThrower() == null && item.getPickupDelay() == 10) {
            Pair<Map<Character, Integer>, Integer> bonuses = calcChickenBonuses(item);
            Map<Character, Integer> letterBonuses = bonuses.getA();
            Integer numNamedChickens = bonuses.getB();

            double spawnChance = this.getConfig().getDouble("spawn-egg-chance");
            if (Math.random() <= spawnChance * calcGeneralChickenBonus(numNamedChickens)) {
                itemStack.setType(getRandomSpawnEgg(letterBonuses));
            }
        }
    }

    // Returns log2(n + 2)
    public double calcGeneralChickenBonus(double numNamedChickens){
        // log2(x) = log(x) / log(2)
        return Math.log(numNamedChickens + 2) / Math.log(2);
    }

    public Pair<Map<Character, Integer>, Integer> calcChickenBonuses(Entity center) {
        int radius = this.getConfig().getInt("named-chicken-radius");
        List<Entity> nearbyEntities = center.getNearbyEntities(radius, radius, radius);
        Set<String> namedChickensPos = new HashSet<>();
        Map<Character, Integer> letterBonuses = new HashMap<>();

        for(Entity ne : nearbyEntities){
            if(ne.getType() == EntityType.CHICKEN){
                String chickenName = ne.getCustomName();

                if(chickenName != null) {
                    Location loc = ne.getLocation();
                    String pos = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();

                    // If no other named chicken has been found at that location
                    if (!namedChickensPos.contains(pos)) {
                        namedChickensPos.add(pos);
                        letterBonuses.merge(chickenName.toLowerCase().charAt(0), 1, Integer::sum);
                    }
                }
            }
        }

        return new Pair<>(letterBonuses, namedChickensPos.size());
    }

    public Material getRandomSpawnEgg(Map<Character, Integer> letterBonuses){
        ConfigurationSection weightConfig = this.getConfig().getConfigurationSection("spawn-egg-weights");
        Random rand = new Random();
        int totalWeight = calcTotalWeight(letterBonuses);
        int i = rand.nextInt(totalWeight);

        if(weightConfig != null) {
            for (String eggName : weightConfig.getKeys(false)) {
                Character firstLetter = eggName.toLowerCase().charAt(0);
                Integer bonus = letterBonuses.get(firstLetter);
                int weight = weightConfig.getInt(eggName);

                if (bonus != null)
                    weight *= (1 + bonus);
                i -= weight;

                if (i <= 0)
                    return Material.valueOf(eggName);
            }
        }

        // We should never get this far
        return Material.TROPICAL_FISH_SPAWN_EGG;
    }

    private int calcTotalWeight(Map<Character, Integer> letterBonuses){
        ConfigurationSection weightConfig = this.getConfig().getConfigurationSection("spawn-egg-weights");
        int totalWeight = 0;

        if(weightConfig != null) {
            for (String eggName : weightConfig.getKeys(false)) {
                Character firstLetter = eggName.toLowerCase().charAt(0);
                Integer bonus = letterBonuses.get(firstLetter);
                int weight = weightConfig.getInt(eggName);

                if (bonus != null)
                    weight *= (1 + bonus);
                totalWeight += weight;
            }
        }

        return totalWeight;
    }

    // Saves the default config; always overwrites. This file is purely for ease of reference; it is never loaded.
    private void saveConfigAsDefault() {
        if (!this.getDataFolder().exists())
            if(!this.getDataFolder().mkdir())
                Bukkit.getConsoleSender().sendMessage("Failed to create data folder.");

        File defaultFile = new File(this.getDataFolder(), "default.yml");
        InputStream cfgStream = this.getResource("config.yml");

        if(cfgStream != null) {
            try {
                Files.copy(cfgStream, defaultFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to save default.yml");
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player player){
            Pair<Map<Character, Integer>, Integer> bonuses = calcChickenBonuses(player);
            Map<Character, Integer> letterBonuses = bonuses.getA();
            Integer numNamedChickens = bonuses.getB();

            double spawnChance = this.getConfig().getDouble("spawn-egg-chance");
            double withBonus = spawnChance * calcGeneralChickenBonus(numNamedChickens);
            player.sendMessage("§7Unique coords with named chickens nearby: " + numNamedChickens);
            player.sendMessage("§7Chance to lay a spawn egg: " + Math.floor(withBonus * 10000) / 100 + "%");
            player.sendMessage("§7Letter bonuses: " + letterBonuses.toString());
            return true;
        }
        return false;
    }
}