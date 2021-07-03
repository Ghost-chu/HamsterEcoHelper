package cat.nyaa.heh;

import cat.nyaa.heh.api.HamsterEcoHelperAPI;
import cat.nyaa.heh.business.auction.Auction;
import cat.nyaa.heh.business.auction.Requisition;
import cat.nyaa.heh.business.item.ShopItem;
import cat.nyaa.heh.business.item.ShopItemManager;
import cat.nyaa.heh.business.item.ShopItemType;
import cat.nyaa.heh.business.signshop.ItemFrameShop;
import cat.nyaa.heh.business.signshop.SignShopManager;
import cat.nyaa.heh.business.signshop.SignShopSell;
import cat.nyaa.heh.business.transaction.Tax;
import cat.nyaa.heh.business.transaction.TransactionController;
import cat.nyaa.heh.command.MainCommand;
import cat.nyaa.heh.db.DatabaseManager;
import cat.nyaa.heh.db.MarketConnection;
import cat.nyaa.heh.db.SignShopConnection;
import cat.nyaa.heh.events.listeners.SignEvents;
import cat.nyaa.heh.events.listeners.UiEvents;
import cat.nyaa.heh.ui.BaseUi;
import cat.nyaa.heh.ui.SignShopGUI;
import cat.nyaa.heh.ui.UiManager;
import cat.nyaa.heh.ui.component.button.ButtonRegister;
import cat.nyaa.heh.utils.EcoUtils;
import cat.nyaa.heh.utils.SystemAccountUtils;
import cat.nyaa.nyaacore.component.ISystemBalance;
import cat.nyaa.nyaacore.component.NyaaComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class HamsterEcoHelper extends JavaPlugin implements HamsterEcoHelperAPI {
    public static HamsterEcoHelper plugin;
    public Configuration config;
    I18n i18n;

    MainCommand mainCommand;
    UiEvents uiEvents;
    SignEvents signEvents;

    Auction auction;
    DatabaseManager databaseManager;
    UiManager uiManager;

    public File itemFile = new File(getDataFolder(),"sysitem.yml");
    public YamlConfiguration itemConfiguration;

    @Override
    public void onEnable() {
        plugin = this;
        onReload();
        registerCommands();
        uiEvents = new UiEvents(this);
        signEvents = new SignEvents();
        Bukkit.getPluginManager().registerEvents(uiEvents, this);
        Bukkit.getPluginManager().registerEvents(signEvents, this);
        new BukkitRunnable(){
            @Override
            public void run() {
                Random random = new Random();
                if(random.nextInt(config.systemAuctionChance) != 0){
                    return;
                }
                List<ConfigItem> auctionItems = new ArrayList<>();
                itemConfiguration.getStringList("auction").forEach(entry-> auctionItems.add(ConfigItem.deserialize(entry)));
                if(auctionItems.isEmpty()){
                    return;
                }
                int sel = random.nextInt(auctionItems.size());
                ConfigItem selected = auctionItems.get(sel);
                ShopItem item = ShopItemManager.newShopItem(SystemAccountUtils.getSystemUuid(), ShopItemType.AUCTION, selected.getItemStack(), selected.getPrice());
                ShopItemManager.insertShopItem(item);
                Auction.startAuction(item, selected.getPrice(), 1.0D, selected.getPrice());
            }
        }.runTaskTimer(this,0,config.systemAuctionInterval);
        new BukkitRunnable(){
            @Override
            public void run() {
                Random random = new Random();
                if(random.nextInt(config.systemRequisitionChance) != 0){
                    return;
                }
                List<ConfigItem> requisitionItems = new ArrayList<>();
                itemConfiguration.getStringList("requisition").forEach(entry-> requisitionItems.add(ConfigItem.deserialize(entry)));
                if(requisitionItems.isEmpty()){
                    return;
                }
                int sel = random.nextInt(requisitionItems.size());
                ConfigItem selected = requisitionItems.get(sel);
                ShopItem item = ShopItemManager.newShopItem(SystemAccountUtils.getSystemUuid(), ShopItemType.REQUISITION, selected.getItemStack(), selected.getPrice());
                ShopItemManager.insertShopItem(item);
                Requisition.startRequisition(item);
            }
        }.runTaskTimer(this,0,config.systemRequisitionInterval);
    }

    private void registerCommands() {
        mainCommand = new MainCommand(this, i18n);
        Bukkit.getPluginCommand("hamsterecohelper").setExecutor(mainCommand);
    }

    @Override
    public void onDisable() {
        if (auction != null){
            auction.abort();
        }
        databaseManager.close();
        uiManager.getMarketUis().forEach(BaseUi::close);
        plugin = null;
    }

    public void onReload() {
        config = new Configuration();
        config.load();
        if(!itemFile.exists()){
            try {
                //noinspection ResultOfMethodCallIgnored
                itemFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        itemConfiguration = YamlConfiguration.loadConfiguration(itemFile);
        i18n = new I18n(plugin, config.language);
        i18n.load();
        databaseManager = DatabaseManager.getInstance();
        uiManager = UiManager.getInstance();
        MarketConnection.getInstance();
        SignShopConnection.getInstance();
        TransactionController.getInstance();
        SystemAccountUtils.init();
        try {
            NyaaComponent.register(ISystemBalance.class, new SystemAccountUtils());
        }catch (Exception i){
            i.printStackTrace();
        }
        EcoUtils.getInstance();
        ButtonRegister.getInstance().load();
        SignShopManager ssm = SignShopManager.getInstance();
        ssm.load();
        ItemFrameShop.reloadFrames();
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    @Override
    public boolean withdrawPlayer(OfflinePlayer player, double amount, ShopItemType type, String taxReason) {
        return TransactionController.getInstance().withdrawWithTax(player, amount, type, taxReason);
    }

    @Override
    public double getSystemBalance() {
        return SystemAccountUtils.getSystemBalance();
    }

    @Override
    public boolean depositToSystem(String reason, double amount) {
        try {
            Tax tax = TransactionController.getInstance().newTax(SystemAccountUtils.getSystemUuid(), 0, amount, System.currentTimeMillis(), reason);
            TransactionController.getInstance().retrieveTax(tax);
            return true;
        }catch (Exception e){
            Bukkit.getLogger().log(Level.WARNING, "error while depositing system", e);
            return false;
        }
    }

    @Override
    public boolean depositToSystem(OfflinePlayer from, String reason, double amount) {
        boolean withdraw = SystemAccountUtils.withdraw(from, amount);
        if (!withdraw){
            return false;
        }
        try {
            Tax tax = TransactionController.getInstance().newTax(from.getUniqueId(), 0, amount, System.currentTimeMillis(), reason);
            TransactionController.getInstance().retrieveTax(tax);
            return withdraw;
        }catch (Exception e){
            Bukkit.getLogger().log(Level.WARNING, "error while depositing system", e);
            return false;
        }
    }



    @Override
    public boolean chargeFee(OfflinePlayer from, String reason, double amount) {
        boolean success = SystemAccountUtils.withdraw(from, amount);
        if (success){
            Tax tax = TransactionController.getInstance().newTax(from.getUniqueId(), 0, amount, System.currentTimeMillis(), reason);
            TransactionController.getInstance().retrieveTax(tax);
        }
        return success;
    }

    @Override
    public boolean withdrawFromSystem(String reason, double amount) {
        try {
            Tax tax = TransactionController.getInstance().newTax(SystemAccountUtils.getSystemUuid(), 0, -amount, System.currentTimeMillis(), reason);
            TransactionController.getInstance().retrieveTax(tax);
            return true;
        }catch (Exception e){
            Bukkit.getLogger().log(Level.WARNING, "error while withdrawing system", e);
            return false;
        }
    }

    @Override
    public boolean withdrawFromSystem(OfflinePlayer from, String reason, double amount) {
        boolean deposit = SystemAccountUtils.deposit(from, amount);
        if (!deposit){
            return false;
        }
        try {
            Tax tax = TransactionController.getInstance().newTax(from.getUniqueId(), 0, -amount, System.currentTimeMillis(), reason);
            TransactionController.getInstance().retrieveTax(tax);
            return deposit;
        }catch (Exception e){
            Bukkit.getLogger().log(Level.WARNING, "error while withdrawing system", e);
            return false;
        }
    }

    @Override
    public Inventory openShopfor(Player opener, OfflinePlayer shopOwner) {
        SignShopSell signShopSell = new SignShopSell(shopOwner.getUniqueId());
        SignShopGUI signShopGUI = UiManager.getInstance().newSignShopGUI(signShopSell);
        Inventory inventory = signShopGUI.getInventory();
        signShopGUI.open(opener);
        return inventory;
    }

    public HamsterEcoHelperAPI getImpl(){
        return this;
    }



}




