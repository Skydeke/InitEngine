package engine.architecture.ui.element.layout;

public class Inset {

    public int top, bottom, left, right;

    public Inset(int scalar) {
        this.top = scalar;
        this.bottom = scalar;
        this.left = scalar;
        this.right = scalar;
    }

    public Inset(int top, int bottom, int left, int right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public static Inset empty() {
        return new Inset(0);
    }

}
