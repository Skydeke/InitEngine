package engine.architecture.system;


import engine.architecture.event.InputManager;

public class GameEngine implements Runnable {

    public static int FRAMES_PER_SECOND = 0;

    private final Window window;
    private final Thread gameLoopThread;
    private final Thread gameUpdateThread;
    private final SimpleApplication game;
    private final InputManager inputManager;
    private final AppContext app;


    public GameEngine(SimpleApplication game) {
        this.window = Window.instance();
        this.gameLoopThread = new Thread(this, "MAIN GAME LOOP");
        this.gameUpdateThread = new Thread(this::update, "GAME UPDATE LOOP");
        this.game = game;
        this.inputManager = InputManager.instance();
        this.app = AppContext.instance();
    }

    public void init() throws Exception {
        window.init();
        app.init(game);
        inputManager.init(app);
    }

    public void start() {
//        this.gameUpdateThread.start();
        this.gameLoopThread.run();
    }

    @Override
    public void run() {
        float fps = 0;
        int times = 0;
        while (!window.shouldClose() & !game.isClosed()) {
            Time.update();
            times++;
            fps += Time.getDelta();
            if (times >= 50) {
                FRAMES_PER_SECOND = (int) (times / fps);
                times = 0;
                fps = 0;
            }
            game.update(Time.getDelta());
            app.update();
            app.draw(window.isVisible());
            inputManager.update();
            window.update(app.getSceneContext().getPipeline().isAnyChange());
            AppContext.instance().getSceneContext().getPipeline().setAnyChange(false);
        }
        game.cleanUp();
    }

    private void update() {
        while (!window.shouldClose()) {
            Time.update();
            game.update(Time.getDelta());
            inputManager.update();
        }
    }
}
