package engine.architecture.ui.element;

import engine.architecture.system.AppContext;
import engine.architecture.system.Window;
import engine.architecture.ui.event.Event;
import engine.architecture.ui.event.InputManager;
import engine.architecture.ui.event.mouse.HoverLostEvent;
import engine.architecture.ui.event.mouse.HoverStartEvent;
import engine.architecture.ui.event.mouse.MouseClickEvent;

import java.util.ArrayList;
import java.util.Optional;

import static engine.architecture.ui.event.mouse.MouseClickEvent.BUTTON_CLICK;

public class ElementManager {

    private static ElementManager instance;
    public Optional<UIElement> eventHog;
    private ArrayList<Event> events;
    private UIElement hovered, root, focused, top, lastAccepted;
    private boolean focusLock;

    private ElementManager() {
        events = new ArrayList<>();
    }

    public static ElementManager instance() {
        if (instance == null)
            instance = new ElementManager();
        return instance;
    }

    /**
     * Initialize parameters that need access to applications data
     *
     * @param context currently running context
     */
    public void init(AppContext context) {
        this.root = context.getRoot();
        this.focused = root;
        this.hovered = root;
        this.lastAccepted = root;
        this.eventHog = Optional.empty();
    }

    /**
     * Sends events and updates the focused elements
     */
    public void update() {

        if (top == null) top = root.getChildren().get(0);

        for (Event e : events) {

            eventHog.ifPresent(element -> element.handle(e));

            // if event is mouse event, the event could be pointing at something
            // further down the element tree. We must first pass the event to the
            // first element that is either
            //      1. A leaf, so its enclosing area has nothing over it
            //      2. Controlling, as in isControlling() returns true. This
            //         flag forces any refocusing to go through it first
            if (e instanceof MouseClickEvent) {
                MouseClickEvent m = (MouseClickEvent) e;
                if (m.getAction() == BUTTON_CLICK) {

                    // if were focus locked, we only want to look at elements
                    // deeper in the element tree than the focus element.

                    UIElement prev, post;
                    if (focusLock) {
                        focused.handle(m);
                        prev = focused;
                    } else if (top.getAbsoluteBox().isWithin(m.getScreenPos())) {
                        top.handle(e);
                        prev = top;
                        if (e.isConsumed())
                            lastAccepted = top;
                    } else {
                        prev = root.findAtPos(m.getScreenPos());
                        if (prev == root) continue;
                        setTop(prev);
                        top.handle(e);
                        if (e.isConsumed())
                            lastAccepted = top;
                    }

                    while (!e.isConsumed()) {
                        post = prev.findAtPos(m.getScreenPos());

                        if (post == prev)
                            break;

                        if (post == root) {
                            lastAccepted = root;
                            break;
                        } else {
                            lastAccepted = post.getViewport();
                        }

                        if (lastAccepted != null)
                            lastAccepted.handle(e);
                        prev = post;
                    }
                } else {
                    if (focusLock) focused.handle(e);
                    else{
                        if (lastAccepted != null){
                            lastAccepted.handle(e);
                        }
                    }
                }
            } else {
                if (focusLock) focused.handle(e);
                else lastAccepted.handle(e);
            }
        }
        // we processed all the events, so we can clear til next frame
        events.clear();

        UIElement elem;
        if (focusLock || Window.instance().isHidden())
            return;

        elem = root.findAtPos(InputManager.instance().getCursorPos());

        // update if the element being hovered over changed
        if (elem != hovered) {
            hovered.handle(new HoverLostEvent());
            hovered = elem;
            elem.handle(new HoverStartEvent());
        }
    }

    /**
     * Force this element to be the first to recieve any events
     *
     * @param focused element to handle all incoming events
     */
    public void setFocused(UIElement focused) {
        if (focusLock) return;
        this.focused = focused;
        this.focusLock = true;
        focused.recalculateAbsolutePositions();
        System.out.println("Focused: " + focused);
    }

    public boolean isFocused(UIElement maybeFocused) {
        return this.focused == maybeFocused;
    }

    /**
     * Resets the focused element to the root of the element tree
     */
    public void resetFocused() {
        this.focusLock = false;
        this.focused = root;
        root.recalculateAbsolutePositions();
        System.out.println("Focused: " + focused);
    }

    public void setEventHog(UIElement e) {
        if (e != null) eventHog = Optional.of(e);
    }

    public void resetEventHog() {
        eventHog = Optional.empty();
    }

    /**
     * Add event to the queue of events
     *
     * @param e event
     */
    public void fire(Event e) {
        events.add(e);
    }

    /**
     * pushes element to front of root list
     */
    public void setTop(UIElement element) {
        root.getChildren().remove(element);
        root.getChildren().add(0, element);
        this.top = element;
    }

    public boolean isMouseOver(UIElement _element) {
        for (UIElement elem : root.getChildren()) {
            if (elem.getAbsoluteBox().isWithin(Window.instance().getCursorPosf())) {
                return _element == elem;
            }
        }
        return false;
    }

    public boolean isTop(UIElement _element) {
        return _element == this.top;
    }
}
