package engine.ui.element.viewport;

import engine.ui.element.UIElement;
import engine.ui.element.panel.Panel;
import engine.ui.event.mouse.MouseClickEvent;
import engine.ui.layout.Inset;
import engine.ui.layout.VerticalLayout;

public class VerticalViewport extends Viewport {

    public VerticalViewport(int elementHeight, int minimumX, int minimumY, UIElement... children) {
        this(elementHeight, minimumX, minimumY, new ViewportSettings(), children);
    }

    public VerticalViewport(int elementHeight, int minimumX, int minimumY, ViewportSettings vs, UIElement... children) {
        super(vs);

        Panel listPanel = new Panel();
        setMainPanel(listPanel);

        listPanel.setLayout(new VerticalLayout(listPanel, elementHeight));
        listPanel.addChildren(children);
        listPanel.setChildrenInset(new Inset(5));

        listPanel.onEvent(e -> {
            if (e instanceof MouseClickEvent) {
                MouseClickEvent m = (MouseClickEvent) e;
                for (UIElement child : listPanel.getChildren()) {
                    if (child.getAbsoluteBox().isWithin(m.getScreenPos())) {
                        child.handle(m);
                        break;
                    }

                }
            }

        });

        this.minWidth = minimumX;
        this.minHeight = minimumY;
    }

//    @Override
//    public void render() {
//        super.render();
//
//        listPanel.render();
//    }


}
