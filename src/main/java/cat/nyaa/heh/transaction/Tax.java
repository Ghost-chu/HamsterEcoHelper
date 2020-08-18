package cat.nyaa.heh.transaction;

import cat.nyaa.heh.HamsterEcoHelper;
import cat.nyaa.heh.item.ShopItem;
import cat.nyaa.nyaacore.orm.annotations.Column;
import cat.nyaa.nyaacore.orm.annotations.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Table("tax")
public class Tax {
    @Column(name = "uid", primary = true)
    long uid;
    @Column(name = "payer")
    UUID payer;
    @Column(name = "tax")
    double tax;
    @Column(name = "fee")
    double fee;
    @Column(name = "time")
    long time;

    public Tax() {
    }

    public Tax(long uid, UUID from, double tax, long time) {
        this.uid = uid;
        this.payer = from;
        this.tax = tax;
        this.time = time;
    }

    public static BigDecimal calcTax(ShopItem shopItem, BigDecimal price){
        double taxRate = HamsterEcoHelper.plugin.config.taxRateMap.getOrDefault(shopItem.getShopItemType().name().toLowerCase(), 0d).doubleValue();
        return price.multiply(BigDecimal.valueOf(taxRate)).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }
}
