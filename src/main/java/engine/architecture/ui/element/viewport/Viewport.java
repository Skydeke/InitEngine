package engine.architecture.ui.element.viewport;

import engine.architecture.system.AppContext;
import engine.architecture.system.Window;
import engine.architecture.ui.element.UIElement;
import engine.architecture.ui.element.button.Button;
import engine.architecture.ui.element.button.ButtonSettings;
import engine.architecture.ui.element.layout.Box;
import engine.architecture.ui.element.layout.Inset;
import engine.architecture.ui.element.layout.TopbarLayout;
import engine.architecture.ui.element.layout.ViewportLayout;
import engine.architecture.ui.element.panel.Panel;
import engine.architecture.ui.event.InputManager;
import engine.architecture.ui.event.ResizeEvent;
import engine.architecture.ui.event.mouse.HoverLostEvent;
import engine.architecture.ui.event.mouse.HoverStartEvent;
import engine.architecture.ui.event.mouse.MouseClickEvent;
import engine.utils.Color;
import engine.utils.libraryBindings.maths.joml.Vector2f;
import engine.utils.libraryBindings.maths.joml.Vector2i;
import engine.utils.libraryBindings.maths.joml.Vector4i;

import static engine.architecture.ui.event.mouse.MouseClickEvent.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Contains functionality akin to a window in an OS
 * Can be dragged, resized
 * TODO: opened, closed, hidden, docked, named, debug stats
 * TODO: constraints within mainPanel
 */
public class Viewport extends UIElement {

    protected float minWidth = 40;
    protected float minHeight = 40;
    // child panels
    protected Panel topBar;
    protected Panel edgePanel;
    protected Panel mainPanel;
    boolean activeDrag = false;
    boolean hover = false;
    // defines areas where window cursor acts to resize
    // left , top , right, bottom
    private Box[] edgeBox;
    // 0: left , 1: top , 2: right, 3: bottom, -1: none
    private int dragEdge = -1;
    // size in pixels the mouse can hover over to start a resize event
    private int edgeSize = 5;
    private ViewportSettings viewportSettings;

    protected Viewport(ViewportSettings vs) {

        super();

        this.viewportSettings = vs;

        this.topBar = new Panel();
        topBar.setColor(vs.getTopBarColor());
        topBar.setRounding(vs.getRounding().x, vs.getRounding().y, 0, 0);

        this.edgePanel = new Panel();
        edgePanel.setColor(vs.getBorderColor());
        edgePanel.setRounding(0, 0, vs.getRounding().z, vs.getRounding().w);

        edgeBox = new Box[4];

        setControlling(true);
        setEdgeBoxs();
        initTopBar();

        // update viewport state
        onEvent(e -> {

            if (e instanceof ResizeEvent)
                setEdgeBoxs();

            else if (e instanceof HoverStartEvent)
                hover = true;

            else if (e instanceof HoverLostEvent)
                hover = false;

            else if (e instanceof MouseClickEvent) {
                MouseClickEvent m = (MouseClickEvent) e;

                // resize window start
                if ((m.getAction() == BUTTON_CLICK)) {

                    if (dragEdge != -1) {
                        AppContext.instance().getElementManager().setFocused(this);
                        activeDrag = true;
                        e.consume();
                    }
                }

                // resize window end
                else if ((m.getAction() == BUTTON_RELEASED) && activeDrag) {
                    AppContext.instance().getElementManager().resetFocused();
                    activeDrag = false;
                    e.consume();
                }

                if (!e.isConsumed() && mainPanel.getAbsoluteBox().isWithin(m.getScreenPos()))
                    mainPanel.handle(e);
                else if (!e.isConsumed() && topBar.getAbsoluteBox().isWithin(m.getScreenPos()))
                    topBar.handle(e);
            }

            if (!e.isConsumed()) {
                mainPanel.handle(e);
            }
        });

        // make viewport draggable by the window
        topBar.onEvent(e -> {
            if (e instanceof MouseClickEvent) {
                // move window
                MouseClickEvent m = (MouseClickEvent) e;
                if (m.getKey() == GLFW_MOUSE_BUTTON_LEFT) {
                    if (m.getAction() == BUTTON_HELD) {
                        getRelativeBox().translate(m.getScreenDelta());
                        getParent().recalculateAbsolutePositions();
                    }
                    e.consume();
                }
            }
        });


        // create window layout
        ViewportLayout vsp = new ViewportLayout(this);
        vsp.setTopBarHeight(vs.getTopBarSize());
        setLayout(vsp);

        // add sub panels as children
        super.addChildren(topBar, edgePanel);
//        addChildren( edgePanel);
//        addChildren( topBar);
    }

    public void setMainPanel(Panel mainPanel) {
        this.mainPanel = mainPanel;
        mainPanel.setColor(viewportSettings.getPanelColor());
        mainPanel.setRounding(0, 0, viewportSettings.getRounding().z, viewportSettings.getRounding().w);
        int bs = viewportSettings.getBorderSize();
        mainPanel.setInset(new Inset(bs / 2, bs, bs, bs));
        addChild(mainPanel);
    }

    @Override
    public void update() {

        // WINDOW RESIZE //
        // get cursor delta and update window size and position
        if (activeDrag) {
            Vector2f delta = InputManager.instance().getCursorDelta();
            Box box = new Box(getRelativeBox());
            switch (dragEdge) {
                case 0: {   // left
                    box.width -= delta.x;
                    box.x += delta.x;
                    break;
                }
                case 1: { // top
                    box.height -= delta.y;
                    break;
                }
                case 2: { // right
                    box.width += delta.x;
                    break;
                }
                case 3: { // bottom
                    box.height += delta.y;
                    box.y -= delta.y;
                    break;
                }

            }

            Box abs = box.relativeTo(getParent().getAbsoluteBox());
            Vector2i res = Window.instance().getResolution();

            // check if new size meets invariants
            if (abs.width * res.x >= minWidth && abs.height * res.y >= minHeight) {
                setBox(box);
                recalculateAbsolutePositions();
            }

        } else {
            // if no active resize is going on, update the drag edge
            if (hover) {
                Vector2f coord = InputManager.instance().getCursorPos();
                if (edgeBox[0].relativeTo(this.absoluteBox).isWithin(coord)) {
                    Window.instance().setCursor(GLFW_HRESIZE_CURSOR);
                    dragEdge = 0;
                } else if (edgeBox[1].relativeTo(this.absoluteBox).isWithin(coord)) {
                    Window.instance().setCursor(GLFW_VRESIZE_CURSOR);
                    dragEdge = 1;
                } else if (edgeBox[2].relativeTo(this.absoluteBox).isWithin(coord)) {
                    Window.instance().setCursor(GLFW_HRESIZE_CURSOR);
                    dragEdge = 2;
                } else if (edgeBox[3].relativeTo(this.absoluteBox).isWithin(coord)) {
                    Window.instance().setCursor(GLFW_VRESIZE_CURSOR);
                    dragEdge = 3;
                } else {
                    if (dragEdge != -1) {
                        Window.instance().setCursor(GLFW_ARROW_CURSOR);
                        dragEdge = -1;
                    }
                }
            } else {
                // cursor is off the viewport
                if (dragEdge != -1) {
                    Window.instance().setCursor(GLFW_ARROW_CURSOR);
                    dragEdge = -1;
                }
            }
        }

        super.update();
    }

    private void setEdgeBoxs() {
        Vector2i res = getPixelSize();
        float xFactor = (float) viewportSettings.getBorderSize() / res.x;
        float yFactor = (float) viewportSettings.getBorderSize() / res.y;
        // left right top bottom
        edgeBox[0] = new Box(-xFactor, 0, 2 * xFactor, 1);
        edgeBox[1] = new Box(0, 1 - yFactor, 1, 2 * yFactor);
        edgeBox[2] = new Box(1 - xFactor, 0, 2 * xFactor, 1);
        edgeBox[3] = new Box(0, -yFactor, 1, 2 * yFactor);
    }

    private void initTopBar() {
        ButtonSettings bs0 = new ButtonSettings();
        bs0.setRounding(new Vector4i(7));

        Color closeColor = new Color(0xeb463d).darken();
        bs0.setButtonColor(closeColor);
        bs0.setHoverColor(closeColor.brighten());
        bs0.setClickColor(closeColor.darken());
        Button close = new Button(bs0);

        ButtonSettings bs1 = new ButtonSettings();
        Color minColor = new Color(0xebc83d).darken();
        bs1.setButtonColor(minColor);
        bs1.setHoverColor(minColor.brighten());
        bs1.setClickColor(minColor.darken());
        Button minimize = new Button(bs1);

        ButtonSettings bs2 = new ButtonSettings();
        Color expandColor = new Color(0x7ee336).darken();
        bs2.setButtonColor(expandColor);
        bs2.setHoverColor(expandColor.brighten());
        bs2.setClickColor(expandColor.darken());
        Button expand = new Button(bs2);

        topBar.setLayout(new TopbarLayout(topBar, 0));
        topBar.addChildren(close, minimize, expand);
        topBar.setChildrenInset(new Inset(5));
    }
}
