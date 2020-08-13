package engine.architecture.ui.element;

import engine.architecture.system.Window;
import engine.architecture.ui.constraints.UIConstraints;
import engine.architecture.ui.element.layout.*;
import engine.architecture.ui.element.viewport.Viewport;
import engine.architecture.ui.event.Event;
import engine.architecture.ui.event.ResizeEvent;
import engine.utils.libraryBindings.maths.joml.Vector2f;
import engine.utils.libraryBindings.maths.joml.Vector2i;
import engine.utils.libraryBindings.opengl.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Optional;

public abstract class UIElement {

    /**
     * Spatial data of element
     */
    @Getter
    protected Box relativeBox, absoluteBox;
    @Getter
    @Setter
    protected UIConstraints constraints = null;
    @Setter
    protected Layout layout;
    @Getter
    private LayoutType alignType = LayoutType.RELATIVE_TO_PARENT;
    @Getter
    protected int minWidth, minHeight, preferredWidth, preferredHeight;
    @Setter
    @Getter
    protected Inset inset;
    /**
     * Decides whether element will let children
     * recieve events on their own or if this element
     * will have to explicitly pass down events to children
     */
    @Getter
    @Setter
    boolean controlling = false;
    @Setter
    @Getter
    boolean activated = true;
    @Getter
    private ArrayList<UIElement> children;
    @Getter
    @Setter
    private UIElement parent;
    private ArrayList<Event.EventHandler> handlers;
    @Getter
    private int UUID;

    protected UIElement() {
        children = new ArrayList<>(5);
        handlers = new ArrayList<>(2);
        layout = new ConstraintLayout(this);
        relativeBox = new Box(0, 0, 0, 0);
        absoluteBox = new Box(0, 0, 0, 0);
        UUID = Utils.generateNewUUID_GUI();
        inset = new Inset(0);
    }

    /**
     * Renders all children. OpenGL functionality will have to
     * be added by subclasses
     */
    public void render() {
        children.stream()
                .filter(UIElement::isActivated)
                .forEach(UIElement::render);
    }

    /**
     * For a sub-element to take advantage of if it requires constant
     * re-calculation
     */
    public void update() {
        children.stream()
                .filter(UIElement::isActivated)
                .forEach(UIElement::update);
    }

    /**
     * Adds child element to this element
     * A child gains the following by being added as a child
     * (assuming it's attached in some way to the AppContext
     * root)
     * <p>
     * 1. Layout information and resize events
     * 2. Ordered rendering (owner renders before child)
     * 3. Registered for event traversal
     * 4. Registered for render traversal
     *
     * @param e event to add
     */
    public void addChild(UIElement e) {
        e.setParent(this);
        children.add(e);
    }

    /**
     * Read addChild() for detailed documentation
     *
     * @param elements collection of elements
     */
    public void addChildren(UIElement... elements) {
        for (UIElement e : elements) {
            e.setParent(this);
            children.add(e);
        }
    }

    /**
     * This element and downwards on the graph, the element tree is spatially
     * laid out based on rules set by each element's Layout object
     */
    public void recalculateAbsolutePositions() {
        layout.update();
        int i = 0;
        for (UIElement child : children) {
            switch (child.alignType) {
                case RELATIVE_TO_PARENT:
                    if (child.getConstraints() != null)
                        child.getConstraints().updateConstraints();
                    Optional<Box> relative = layout.findRelativeTransform(child, i++);
                    if (relative.isPresent() && child.setBox(relative.get())) {
                        child.handle(new ResizeEvent());
                        child.recalculateAbsolutePositions();
                    }
                    break;
                case ABSOLUTE:
                    if (child.getConstraints() != null) {
                        child.getConstraints().updateConstraints();
                        Optional<Box> absolute = Optional.ofNullable(child.getConstraints().getRelativeBox());
                        if (absolute.isPresent()) {
                            child.absoluteBox.set(absolute.get());
                            child.handle(new ResizeEvent());
                            child.recalculateAbsolutePositions();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Get this elements size in pixels
     *
     * @return resolution of element
     */
    public Vector2i getPixelSize() {
        Vector2i ret = new Vector2i();
        Box box = getAbsoluteBox();
        ret.x = (int) (Window.instance().getWidth() * box.getWidth());
        ret.y = (int) (Window.instance().getHeight() * box.getHeight());
        return ret;
    }

    public Vector2i getPixelSizeForRelative(Box parent) {
        Vector2i ret = new Vector2i();
        Box box = parent.relativeTo(getAbsoluteBox());
        ret.x = (int) (Window.instance().getWidth() * box.getWidth());
        ret.y = (int) (Window.instance().getHeight() * box.getHeight());
        return ret;
    }

    /**
     * Sets new relative based on input relative box
     * Also sets absolute box by using data from owner element
     * if either of these were changed, method returns true
     * such that the caller can know if they need to update or render
     *
     * @param position relative box this element will be set to
     * @return true if box changed, false if it didn't
     */
    public boolean setBox(Box position) {

        boolean ret = true;

        if (getRelativeBox().equals(position))
            ret = false;

        if (parent == null) {
            absoluteBox.set(relativeBox);
            alignType = LayoutType.ABSOLUTE;
        } else {
            alignType = LayoutType.RELATIVE_TO_PARENT;
            Box newAbsolute = position.relativeTo(parent.absoluteBox);
            if (newAbsolute.width * Window.instance().getWidth() >= minWidth &&
                    newAbsolute.height * Window.instance().getHeight() >= minHeight)
                relativeBox.set(position);
            if (getAbsoluteBox().equals(newAbsolute)) ret = true;
            absoluteBox.set(newAbsolute);
        }
        recalculateAbsolutePositions();
        return ret;
    }

    /**
     * Appends event handler to front of list
     * This means on any subclass of class that derives from Element,
     * if that subclass calls super() before calling onEvent(), the super
     * class's handler functionality can be overwritten by consuming the event
     *
     * @param handler the Event Handler
     */
    public void onEvent(Event.EventHandler handler) {
        handlers.add(0, handler);
    }

    /**
     * Generic event handler that simply goes through each handler in the handler
     * list until the event is consumed
     *
     * @param event event that this element needs to handle
     */
    public void handle(Event event) {
        for (Event.EventHandler handler : handlers) {
            handler.handle(event);
            if (event.isConsumed()) return;
        }
    }

    public UIElement findAtPos(Vector2f pos) {
        UIElement ret = this;
        boolean leaf = false;
        while (!leaf) {

            if (ret.isControlling() && ret != this) break;
            leaf = true;
            for (UIElement element : ret.getChildren()) {
                if (element.getAbsoluteBox().isWithin(pos)) {
                    ret = element;
                    leaf = false;
                    break;
                }
            }
        }
        return ret;
    }


    public void cleanup() {
        children.forEach(UIElement::cleanup);
    }

    public void setChildrenInset(Inset inset) {
        for (UIElement child : this.getChildren())
            child.setInset(inset);
    }

    public Viewport getViewport() {
        if (this instanceof Viewport) {
            return (Viewport) this;
        } else if (this.getParent() != null) {
            return getParent().getViewport();
        }
        return null;
    }

    public boolean isFocused() {
        return ElementManager.instance().isFocused(this);
    }

    public boolean isMouseOver() {
        return ElementManager.instance().isMouseOver(getViewport());
    }

    public boolean isMouseOverAndTop() {
        Viewport _vp = getViewport();
        return ElementManager.instance().isMouseOver(_vp)
                && ElementManager.instance().isTop(_vp);
    }

    public void setAlignType(LayoutType alignType) {
        this.alignType = alignType;
//        if (getParent() != null)
//            getParent().recalculateAbsolutePositions();
    }
}
