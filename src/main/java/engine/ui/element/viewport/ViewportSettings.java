package engine.ui.element.viewport;

import engine.utils.Color;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4i;

public class ViewportSettings {

    public final Color TOPBAR_COLOR_DEFAULT = new Color(0x1c2a5e);
    public final Color BORDER_COLOR_DEFAULT = new Color(0x1b2136);
    public final Color PANEL_COLOR_DEFAULT = new Color(0x151a2b);

    @Getter
    @Setter
    private Vector4i rounding = new Vector4i(5);

    @Setter
    @Getter
    private Color topBarColor = TOPBAR_COLOR_DEFAULT,
            borderColor = BORDER_COLOR_DEFAULT,
            panelColor = PANEL_COLOR_DEFAULT;

    @Setter
    @Getter
    private int topBarSize = 24;
//    private int topBarSize = 0;

    @Setter
    @Getter
    private int borderSize = 5;
//    private int borderSize = 0;


    public ViewportSettings setRounding(Vector4i rounding) {
        this.rounding = rounding;
        return this;
    }

    public ViewportSettings setTopBarColor(Color topBarColor) {
        this.topBarColor = topBarColor;
        return this;
    }

    public ViewportSettings setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public ViewportSettings setPanelColor(Color panelColor) {
        this.panelColor = panelColor;
        return this;
    }

    public ViewportSettings setTopBarSize(int topBarSize) {
        this.topBarSize = topBarSize;
        return this;
    }

    public ViewportSettings setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        return this;
    }
}
