package cat.nyaa.heh;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ConfigItem implements org.bukkit.configuration.serialization.ConfigurationSerializable {
    public ConfigItem(ItemStack itemStack, double price) {
        this.itemStack = itemStack;
        this.price = price;
    }
    public ConfigItem(Map< String, Object> arg ){
        this.itemStack =deserializeItem((String)arg.get("item"));
        this.price = (double)arg.get("price");
    }

    private ItemStack itemStack;
    private double price;

    public double getPrice() {
        return price;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("item", serializeItem(this.itemStack));
        map.put("price", price);
        return map;
    }

    public static ConfigItem deserialize(Map< String, Object> arg){
        return new ConfigItem(deserializeItem((String)arg.get("item")),(double)arg.get("price"));
    }
    public static ConfigItem valueOf(Map< String, Object> arg){
        return new ConfigItem(deserializeItem((String)arg.get("item")),(double)arg.get("price"));
    }

    public static String serializeItem(ItemStack iStack) {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("item", iStack);
        return cfg.saveToString();
    }

    public static ItemStack deserializeItem(String config){
       YamlConfiguration  yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.loadFromString(config);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return yamlConfiguration.getItemStack("item");
    }
}
