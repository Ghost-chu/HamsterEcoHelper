package cat.nyaa.heh.ui.component.impl;

import cat.nyaa.heh.HamsterEcoHelper;
import cat.nyaa.heh.business.item.ShopItem;
import cat.nyaa.heh.business.signshop.BaseSignShop;
import cat.nyaa.heh.business.transaction.TaxReason;
import cat.nyaa.heh.ui.SignShopGUI;
import cat.nyaa.heh.ui.UiManager;
import cat.nyaa.heh.ui.component.ShopComponent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class SignShopComponent extends ShopComponent {
    BaseSignShop signShop;

    public SignShopComponent(Inventory inventory, BaseSignShop signShop) {
        super(inventory);
        this.signShop = signShop;
    }

    @Override
    protected void onPostTransaction() {
        UiManager.getInstance().getSignShopUis(signShop.getOwner()).forEach(SignShopGUI::refreshGUI);
    }

    @Override
    protected String getReason() {
        return TaxReason.REASON_SIGN_SHOP;
    }

    @Override
    protected double getFee() {
        return HamsterEcoHelper.plugin.config.signShopFeeBase;
    }

    @Override
    public void loadData() {
        signShop.loadItems();
        this.items = signShop.getItems();
    }

    @Override
    public void loadData(List<ShopItem> data) {
        this.items = data;
    }
}
