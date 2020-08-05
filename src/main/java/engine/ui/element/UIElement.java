package engine.ui.element;

import engine.architecture.system.Window;
import engine.ui.element.viewport.Viewport;
import engine.ui.event.Event;
import engine.ui.event.ResizeEvent;
import engine.ui.layout.AbsoluteLayout;
import engine.ui.layout.Box;
import engine.ui.layout.Inset;
import engine.ui.layout.Layout;
import engine.utils.libraryWrappers.maths.joml.Vector2f;
import engine.utils.libraryWrappers.maths.joml.Vector2i;
import engine.utils.libraryWrappers.opengl.utils.Utils;
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
    @Setter
    protected Layout layout;
    @Getter
    protected int minWidth, minHeight, preferredWidth, preferredHeight;
    @Setter
    @Getter
    protected Inset inset;
    /**
     * Decides whether or not this element's layout
     * should be decided by it's parents. If enabled,
     * an element can choose its absolute layout
     * by calling setAbsoluteBox()
     */
    @Getter
    @Setter
    boolean attached = false;
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
    @Setter
    boolean changed = true;
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
        layout = new AbsoluteLayout(this);
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
                .filter(e -> e.isActivated())
                .forEach(e -> e.render());
    }

    /**
     * For a sub-element to take advantage of if it requires constant
     * re-calculation
     */
    public void update() {
        children.stream()
                .filter(e -> e.isActivated())
                .forEach(e -> e.update());
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
        children.add(e);
        e.setParent(this);
    }

    /**
     * Read addChild() for detailed documentation
     *
     * @param elements collection of elements
     */
    public void addChildren(UIElement... elements) {
        for (UIElement e : elements) {
            children.add(e);
            e.setParent(this);
        }
    }

    /**
     * This element and downwards on the graph, the element tree is spatially
     * laid out based on rules set by each element's Layout object
     */
    public void layoutChildren() {
        layout.update();
        int i = 0;
        for (UIElement child : children) {
            if (child.isAttached()) continue;

            Optional<Box> relative = layout.findRelativeTransform(child, i++);
            if (relative.isPresent() && child.setBox(relative.get())) {

                child.handle(new ResizeEvent());
                child.layoutChildren();
            }
        }
    }

    public void forceTreeLayout() {
        int i = 0;
        for (UIElement child : children) {
            //if(child.isAttached()) continue;
            Optional<Box> relative = layout.findRelativeTransform(child, i++);
            if (relative.isPresent())
                if (child.setBox(relative.get()))
                    child.handle(new ResizeEvent());
            child.forceTreeLayout();
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

    public Vector2i getPixelSizeForRelative(Box within) {
        Vector2i ret = new Vector2i();
        Box box = within.relativeTo(getAbsoluteBox());
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
     * @param newRelative relative box this element will be set to
     * @return true if box changed, false if it didn't
     */
    public boolean setBox(Box newRelative) {

        boolean ret = true;

        if (getRelativeBox().equals(newRelative))
            ret = false;

        if (parent == null)
            absoluteBox.set(relativeBox);
        else {
            Box newAbsolute = newRelative.relativeTo(parent.absoluteBox);
            if (newAbsolute.width * Window.instance().getWidth() >= minWidth &&
                    newAbsolute.height * Window.instance().getHeight() >= minHeight)
                relativeBox.set(newRelative);
            if (getAbsoluteBox().equals(newAbsolute)) ret = true;
            absoluteBox.set(newAbsolute);
        }

        return ret;
    }

    public boolean setAbsoluteBox(Box newAbsolute) {
        if (!attached)
            return false;
        if (absoluteBox.equals(newAbsolute))
            return false;
        absoluteBox.set(newAbsolute);
        return true;
    }

    /**
     * Appends event handler to front of list
     * This means on any subclass of class that derives from Element,
     * if that subclass calls super() before calling onEvent(), the super
     * class's handler functionality can be overwritten by consuming the event
     *
     * @param handler
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

    public UIElement findAtPosForced(Vector2f pos) {
        UIElement ret = this;
        boolean leaf = false;
        while (!leaf) {

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
        children.stream().forEach(e -> e.cleanup());
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

//    @Override
//    public String toString(){
//        StringBuilder builder = new StringBuilder();
//        builder.append(getClass()+ "\n");
//        for(Element child: children)
//            builder.append("    - " + child.getClass() + "\n");
//        return builder.toString();
//    }

}
