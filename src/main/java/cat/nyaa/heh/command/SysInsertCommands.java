package cat.nyaa.heh.command;

import cat.nyaa.heh.ConfigItem;
import cat.nyaa.heh.HamsterEcoHelper;
import cat.nyaa.nyaacore.ILocalizer;
import cat.nyaa.nyaacore.cmdreceiver.Arguments;
import cat.nyaa.nyaacore.cmdreceiver.CommandReceiver;
import cat.nyaa.nyaacore.cmdreceiver.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SysInsertCommands extends CommandReceiver implements ShortcutCommand {

    /**
     * @param plugin for logging purpose only
     * @param _i18n
     */
    public SysInsertCommands(Plugin plugin, ILocalizer _i18n) {
        super(plugin, _i18n);
        this.plugin = HamsterEcoHelper.plugin;
    }

    private HamsterEcoHelper plugin;

    @Override
    public String getHelpPrefix() {
        return "sysinsert";
    }

    private static final String PERMISSION_ADMIN = "heh.admin";

    @SubCommand(isDefaultCommand = true, permission = PERMISSION_ADMIN)
    public void onInsert(CommandSender sender, Arguments arguments) {
        List<ConfigItem> auctionItems = (List<ConfigItem>) plugin.itemConfiguration.getList("auction", new ArrayList<>());
        List<ConfigItem> requisitionItems = (List<ConfigItem>) plugin.itemConfiguration.getList("requisition", new ArrayList<>());
        String cmd = arguments.nextString("auc");
        double price = arguments.nextDouble();
        ConfigItem configItem = new ConfigItem(((Player) sender).getItemInHand(), price);
        if (configItem.getItemStack().getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You must have an valid item in your hand.");
            return;
        }

        String string = ConfigItem.serializeItem(((Player) sender).getItemInHand());
        ItemStack restoredItem = ConfigItem.deserializeItem(string);
        if (!((Player) sender).getInventory().getItemInHand().equals(restoredItem)) {
            sender.sendMessage(ChatColor.RED + "该物品序列化后与原始物品不符，可能存在潜在风险！");
        }


        switch (cmd) {
            case "auc":
                auctionItems.add(configItem);
                plugin.itemConfiguration.set("auction", auctionItems);
                sender.sendMessage(ChatColor.GREEN + "Auction item saved");
                break;
            case "req":
                requisitionItems.add(configItem);
                plugin.itemConfiguration.set("requisition", requisitionItems);
                sender.sendMessage(ChatColor.GREEN + "Requisition item saved");
                break;
        }
        try {
            plugin.itemConfiguration.save(plugin.itemFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public List<String> sampleCompleter(CommandSender sender, Arguments arguments) {
        return new ArrayList<>();
    }

    @Override
    public String getShortcutName() {
        return "hinsert";
    }
}
