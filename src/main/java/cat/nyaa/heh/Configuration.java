package cat.nyaa.heh;

import cat.nyaa.nyaacore.configuration.PluginConfigure;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration extends PluginConfigure {
    @Override
    protected JavaPlugin getPlugin() {
        return HamsterEcoHelper.plugin;
    }

    @Serializable
    public String language = "en_US";

    @Serializable(name = "tax")
    public Map<String, Number> taxRateMap = new LinkedHashMap<>();
    {
        taxRateMap.put("market", 10d);
        taxRateMap.put("sign_shop_sell", 5d);
        taxRateMap.put("sign_shop_buy", 5d);
        taxRateMap.put("direct", 5d);
        taxRateMap.put("auction", 20d);
        taxRateMap.put("requisition", 5d);
        taxRateMap.put("storage", 0d);
    }

    @Serializable(name = "fee.market.base")
    public double marketFeeBase = 100;
    @Serializable(name = "fee.market.storage")
    public double marketFeeStorage = 10;
    @Serializable(name = "fee.storage.unit")
    public double storageFeeUnit = 10;
    @Serializable(name = "fee.signshop.base")
    public double signShopFeeBase = 0;
    @Serializable(name = "fee.direct.base")
    public double directFeeBase = 50;
    @Serializable(name = "fee.auction.base")
    public double auctionFeeBase = 100;
    @Serializable(name = "fee.requisition.base")
    public double requisitionFeeBase = 50;

    @Serializable(name = "limit.slots.market")
    public int limitSlotMarket = 5;
    @Serializable(name = "limit.slots.signshop")
    public int limitSlotSignshop = 100;
    @Serializable(name = "limit.slots.storage")
    public int limitSlotStorage = 54;
    @Serializable(name = "limit.signs")
    public int limitSigns = 3;
    @Serializable(name = "limit.frames")
    public int limitFrames = 12;

    @Serializable(name = "auction.interval")
    public int auctionStepInterval = 200;
    @Serializable(name = "requisition.duration")
    public int requisitionDuration = 3600;

    @Serializable(name = "command.shortcut.enabled")
    public boolean commandShortcutEnabled = true;

    @Serializable(name = "search.cooldown_tick")
    public int searchCooldownTick = 200;

    @Serializable(name = "timer.auction.interval")
    public int systemAuctionInterval = 2000;

    @Serializable(name = "timer.auction.minplayers")
    public int systemAuctionMinPlayers = 6;

    @Serializable(name = "timer.auction.chance")
    public int systemAuctionChance = 10;

    @Serializable(name = "timer.requisition.interval")
    public int systemRequisitionInterval = 2000;

    @Serializable(name = "timer.requisition.minplayers")
    public int getSystemRequisitionMinPlayers = 6;

    @Serializable(name = "timer.requisition.chance")
    public int systemRequisitionChance = 10;

}
