package hasjamon.chickenslayspawneggs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ChickensLaySpawnEggs extends JavaPlugin implements Listener {
    private final Map<Material, Integer> spawnEggs = new HashMap<>();

    @Override
    public void onEnable() {
        spawnEggs.put(Material.TROPICAL_FISH_SPAWN_EGG, 250);
        spawnEggs.put(Material.COW_SPAWN_EGG, 150);
        spawnEggs.put(Material.SHEEP_SPAWN_EGG, 150);
        spawnEggs.put(Material.PIG_SPAWN_EGG, 150);
        spawnEggs.put(Material.HORSE_SPAWN_EGG, 100);
        spawnEggs.put(Material.COD_SPAWN_EGG, 100);
        spawnEggs.put(Material.SALMON_SPAWN_EGG, 100);
        spawnEggs.put(Material.DONKEY_SPAWN_EGG, 100);
        spawnEggs.put(Material.RABBIT_SPAWN_EGG, 100);
        spawnEggs.put(Material.GOAT_SPAWN_EGG, 100);
        spawnEggs.put(Material.WOLF_SPAWN_EGG, 50);
        spawnEggs.put(Material.BAT_SPAWN_EGG, 50);
        spawnEggs.put(Material.SQUID_SPAWN_EGG, 50);
        spawnEggs.put(Material.BEE_SPAWN_EGG, 50);
        spawnEggs.put(Material.PARROT_SPAWN_EGG, 50);
        spawnEggs.put(Material.LLAMA_SPAWN_EGG, 50);
        spawnEggs.put(Material.CAT_SPAWN_EGG, 50);
        spawnEggs.put(Material.FOX_SPAWN_EGG, 50);
        spawnEggs.put(Material.MULE_SPAWN_EGG, 50);
        spawnEggs.put(Material.TURTLE_SPAWN_EGG, 50);
        spawnEggs.put(Material.PANDA_SPAWN_EGG, 50);
        spawnEggs.put(Material.POLAR_BEAR_SPAWN_EGG, 50);
        spawnEggs.put(Material.DOLPHIN_SPAWN_EGG, 50);
        spawnEggs.put(Material.OCELOT_SPAWN_EGG, 35);
        spawnEggs.put(Material.PUFFERFISH_SPAWN_EGG, 35);
        spawnEggs.put(Material.TRADER_LLAMA_SPAWN_EGG, 35);
        spawnEggs.put(Material.GLOW_SQUID_SPAWN_EGG, 35);
        spawnEggs.put(Material.ZOMBIE_SPAWN_EGG, 20);
        spawnEggs.put(Material.SKELETON_SPAWN_EGG, 20);
        spawnEggs.put(Material.SPIDER_SPAWN_EGG, 20);
        spawnEggs.put(Material.CAVE_SPIDER_SPAWN_EGG, 20);
        spawnEggs.put(Material.CREEPER_SPAWN_EGG, 20);
        spawnEggs.put(Material.DROWNED_SPAWN_EGG, 20);
        spawnEggs.put(Material.HUSK_SPAWN_EGG, 20);
        spawnEggs.put(Material.PHANTOM_SPAWN_EGG, 20);
        spawnEggs.put(Material.SILVERFISH_SPAWN_EGG, 20);
        spawnEggs.put(Material.ENDERMITE_SPAWN_EGG, 20);
        spawnEggs.put(Material.PILLAGER_SPAWN_EGG, 20);
        spawnEggs.put(Material.STRIDER_SPAWN_EGG, 15);
        spawnEggs.put(Material.SLIME_SPAWN_EGG, 15);
        spawnEggs.put(Material.WANDERING_TRADER_SPAWN_EGG, 15);
        spawnEggs.put(Material.AXOLOTL_SPAWN_EGG, 15);
        spawnEggs.put(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG, 10);
        spawnEggs.put(Material.VEX_SPAWN_EGG, 10);
        spawnEggs.put(Material.WITCH_SPAWN_EGG, 10);
        spawnEggs.put(Material.VINDICATOR_SPAWN_EGG, 10);
        spawnEggs.put(Material.STRAY_SPAWN_EGG, 10);
        spawnEggs.put(Material.GUARDIAN_SPAWN_EGG, 10);
        spawnEggs.put(Material.ENDERMAN_SPAWN_EGG, 10);
        spawnEggs.put(Material.PIGLIN_SPAWN_EGG, 10);
        spawnEggs.put(Material.PIGLIN_BRUTE_SPAWN_EGG, 10);
        spawnEggs.put(Material.ZOGLIN_SPAWN_EGG, 10);
        spawnEggs.put(Material.HOGLIN_SPAWN_EGG, 10);
        spawnEggs.put(Material.MAGMA_CUBE_SPAWN_EGG, 10);
        spawnEggs.put(Material.BLAZE_SPAWN_EGG, 7);
        spawnEggs.put(Material.RAVAGER_SPAWN_EGG, 7);
        spawnEggs.put(Material.WITHER_SKELETON_SPAWN_EGG, 5);
        spawnEggs.put(Material.SHULKER_SPAWN_EGG, 5);
        spawnEggs.put(Material.EVOKER_SPAWN_EGG, 5);
        spawnEggs.put(Material.SKELETON_HORSE_SPAWN_EGG, 5);
        spawnEggs.put(Material.VILLAGER_SPAWN_EGG, 5);
        spawnEggs.put(Material.ZOMBIE_VILLAGER_SPAWN_EGG, 5);
        spawnEggs.put(Material.MOOSHROOM_SPAWN_EGG, 3);
        spawnEggs.put(Material.ELDER_GUARDIAN_SPAWN_EGG, 3);
        spawnEggs.put(Material.GHAST_SPAWN_EGG, 3);
        spawnEggs.put(Material.CHICKEN_SPAWN_EGG, 1);
        spawnEggs.put(Material.ZOMBIE_HORSE_SPAWN_EGG, 1);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        Item item = e.getEntity();
        ItemStack itemStack = item.getItemStack();

        // If it's an egg and wasn't dropped by a player: 1% chance to lay a random spawn egg instead
        if (itemStack.getType() == Material.EGG && item.getThrower() == null && item.getPickupDelay() == 10) {
            List<Entity> nearbyEntities = item.getNearbyEntities(5, 5, 5);
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

            if (Math.random() <= 0.01 * calcSpawnChanceBonus(namedChickensPos.size())) {
                itemStack.setType(getRandomSpawnEgg(letterBonuses));
            }
        }
    }

    // Returns log2(n + 2)
    private double calcSpawnChanceBonus(double numNamedChickens){
        // log2(x) = log(x) / log(2)
        return Math.log(numNamedChickens + 2) / Math.log(2);
    }

    private Material getRandomSpawnEgg(Map<Character, Integer> letterBonuses){
        Random rand = new Random();
        int totalWeight = calcTotalWeight(letterBonuses);
        int i = rand.nextInt(totalWeight);

        for(Map.Entry<Material, Integer> egg : spawnEggs.entrySet()){
            Character firstLetter = egg.getKey().name().toLowerCase().charAt(0);
            Integer bonus = letterBonuses.get(firstLetter);
            int weight = egg.getValue();

            if(bonus != null)
                weight *= (1 + bonus);
            i -= weight;

            if(i <= 0)
                return egg.getKey();
        }

        // We should never get this far
        return Material.TROPICAL_FISH_SPAWN_EGG;
    }

    private int calcTotalWeight(Map<Character, Integer> letterBonuses){
        int totalWeight = 0;

        for(Map.Entry<Material, Integer> egg : spawnEggs.entrySet()){
            Character firstLetter = egg.getKey().name().toLowerCase().charAt(0);
            Integer bonus = letterBonuses.get(firstLetter);
            Integer weight = egg.getValue();

            if(bonus != null)
                weight *= (1 + bonus);
            totalWeight += weight;
        }

        return totalWeight;
    }
}