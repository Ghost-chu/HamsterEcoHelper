package cat.nyaa.heh.ui;

import cat.nyaa.heh.ui.component.BaseComponent;
import cat.nyaa.heh.ui.component.BasePagedComponent;
import cat.nyaa.heh.ui.component.button.ButtonHolder;
import cat.nyaa.heh.ui.component.button.ButtonRegister;
import cat.nyaa.heh.ui.component.button.GUIButton;
import cat.nyaa.heh.ui.component.impl.ButtonComponent;
import cat.nyaa.heh.ui.component.impl.MarketComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class BaseUi implements InventoryHolder {
    private UUID uiuid;
    protected Inventory uiInventory;
    protected BasePagedComponent pagedComponent;
    protected ButtonComponent buttonComponent;

    public BaseUi() {
        uiInventory = Bukkit.createInventory(this, 54, getTitle());
        pagedComponent = getPageComponent();
        buttonComponent = new ButtonComponent(5, 0, pagedComponent);
        initButtons();
        this.uiuid = UUID.randomUUID();
    }

    protected abstract BasePagedComponent getPageComponent();

    private void initButtons() {
        ButtonRegister instance = ButtonRegister.getInstance();
        GUIButton buttonPrevious = instance.PREVIOUS_PAGE.clone();
        GUIButton buttonNextPage = instance.NEXT_PAGE.clone();
        buttonComponent.setButtonAt(buttonComponent.access(0, 0), buttonPrevious);
        buttonComponent.setButtonAt(buttonComponent.access(0, buttonComponent.columns() - 1), buttonNextPage);
    }

    protected abstract String getTitle();

    public void onClickRawSlot(InventoryClickEvent event) {
        int slot = event.getSlot();
        event.setCancelled(true);
        List<? extends BaseComponent> components = Arrays.asList(pagedComponent, buttonComponent);
        BaseComponent comp = components.stream().filter(com -> com.containsRawSlot(slot)).findFirst().orElse(null);
        if (comp instanceof ButtonHolder){
            GUIButton buttonAt = ((ButtonHolder) comp).getButtonAt(slot);
            if(buttonAt != null){
                buttonAt.doAction(event, ((ButtonHolder) comp).getControlled());
            }
        }
        if (comp == null) return;
        event.setCancelled(true);
        switch (event.getClick()) {
            case LEFT:
                comp.onLeftClick(event);
                break;
            case SHIFT_LEFT:
                comp.onShiftLeftClick(event);
                break;
            case RIGHT:
                comp.onRightClick(event);
                break;
            case MIDDLE:
                comp.onMiddleClick(event);
                break;
            default:
                break;
        }
    }

    public UUID getUid(){
        return uiuid;
    }
}
