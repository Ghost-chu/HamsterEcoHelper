package cat.nyaa.heh.ui.component.button;

import cat.nyaa.heh.ui.component.BasePagedComponent;

public interface ButtonHolder {
    GUIButton getButtonAt(int index);
    void setButtonAt(int index, GUIButton button);
    BasePagedComponent getControlled();
}
