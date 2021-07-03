package cat.nyaa.heh;

import com.google.gson.Gson;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ConfigItem {
    private final static Gson gson = new Gson();
    public ConfigItem(ItemStack itemStack, double price) {
        this.itemStack = itemStack;
        this.price = price;
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


    public String serialize(){
        return gson.toJson(this);
    }

    public static ConfigItem deserialize(String json){
        return gson.fromJson(json,ConfigItem.class);
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
