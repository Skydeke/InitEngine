package engine.architecture.ui.element.button;

import engine.utils.Color;
import engine.utils.libraryBindings.maths.joml.Vector4i;
import engine.utils.libraryBindings.opengl.textures.Texture;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class ButtonSettings {

    public final Color BUTTON_COLOR_DEFAULT = new Color(0x333333);
    public final Color HOVER_COLOR_DEFAULT = new Color(0x222222);

    public final Color CLICK_COLOR_DEFAULT = new Color(0x111111);
    @Setter
    @Getter
    private Color
            buttonColor = BUTTON_COLOR_DEFAULT,
            hoverColor = HOVER_COLOR_DEFAULT,
            clickColor = CLICK_COLOR_DEFAULT;
    @Getter
    @Setter
    private Vector4i rounding = new Vector4i(5);
    @Getter
    private Optional<Texture> buttonTexture = Optional.empty();

    public ButtonSettings(ButtonSettings copy) {
        setClickColor(copy.getClickColor());
        setHoverColor(copy.getHoverColor());
        setButtonColor(copy.getButtonColor());
        if (copy.getButtonTexture().isPresent())
            setButtonTexture(copy.getButtonTexture().get());
        setRounding(copy.getRounding());

    }

    public ButtonSettings() {
    }

    public void setButtonTexture(Texture tex) {
        this.buttonTexture = Optional.of(tex);
    }


}