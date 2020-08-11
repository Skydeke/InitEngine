package engine.rendering.abstracted.renderers;

import engine.architecture.models.Model;
import engine.architecture.system.AppContext;
import engine.rendering.abstracted.Processable;
import engine.utils.libraryBindings.opengl.shaders.RenderState;
import engine.utils.libraryBindings.opengl.shaders.ShadersProgram;

public abstract class AbstractRenderer3D<T extends Processable> extends Renderer3D<T> {

    private final ShadersProgram<T> shadersProgram;

    public AbstractRenderer3D(ShadersProgram<T> shadersProgram) {
        this.shadersProgram = shadersProgram;
    }

    public AbstractRenderer3D(String vertFile, String fragFile) throws Exception {
        this.shadersProgram = ShadersProgram.create(vertFile, fragFile);
    }

    public void render(AppContext context) {
        if (renderList.isEmpty()) {
            return;
        }

        getShadersProgram().bind();

        final RenderState<T> renderState = new RenderState<>(this, context.getSceneContext().getCamera());
        getShadersProgram().updatePerRenderUniforms(renderState);
        onRenderStage(renderState);


        for (Model model : renderList.keySet()) {
            for (int i = 0; i < model.getMeshes().length; i++) {
                model.bindAndConfigure(i);
                for (T instance : renderList.get(model)){
                    final RenderState<T> instanceState = new RenderState<T>(this, instance, context.getSceneContext().getCamera(), i);

                    if (!canCullInstance(instanceState)) {
                        getShadersProgram().updatePerInstanceUniforms(instanceState);

                        onInstanceRender(instanceState);

                        render(instance);
                    }
                }
                model.unbind(i);
            }
        }

        getShadersProgram().unbind();
    }

    @Override
    public void cleanUp() {
        getShadersProgram().delete();
        onDelete();
    }

    /**
     * Returns the shaders program of the engine.rendering.renderer
     *
     * @return the shaders program of the engine.rendering.renderer
     */
    protected ShadersProgram<T> getShadersProgram() {
        return shadersProgram;
    }

    /**
     * Invoked when the engine.rendering.renderer starts rendering all the processed instances
     *
     * @param renderState the render state of the engine.rendering.renderer
     */
    protected void onRenderStage(RenderState<T> renderState) {

    }

    /**
     * Invoked before rendering an instance
     *
     * @param instanceState the instance rendering state
     */
    protected void onInstanceRender(RenderState<T> instanceState) {

    }

    /**
     * Decide if the instance can be culled from rendering
     *
     * @param instanceState the instance rendering state
     * @return true if the instance can be culled, false otherwise
     */
    protected boolean canCullInstance(RenderState<T> instanceState) {
        return false;
    }

    /**
     * Invoked when the engine.rendering.renderer is deleted
     */
    protected void onDelete() {

    }

    /**
     * Render the given instance
     *
     * @param instance the instance to render
     */
    protected abstract void render(T instance);

}
