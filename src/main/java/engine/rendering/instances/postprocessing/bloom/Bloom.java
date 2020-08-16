package engine.rendering.instances.postprocessing.bloom;

import engine.rendering.RenderOutputData;
import engine.rendering.abstracted.postprocessing.ComposedPostProcessor;
import engine.rendering.abstracted.postprocessing.PostProcessor;
import engine.rendering.instances.postprocessing.combine.Combine;
import engine.rendering.instances.postprocessing.gaussianblur.GaussianBlur;
import engine.rendering.instances.postprocessing.mincolour.MinColour;

public class Bloom extends ComposedPostProcessor implements PostProcessor {

    private final Combine combine;

    private Bloom(MinColour minColour, GaussianBlur gaussianBlur, Combine combine) {
        super(minColour, gaussianBlur, combine);
        this.combine = combine;
    }

    public Bloom(float r, float g, float a) {
        this(new MinColour(r, g, a), new GaussianBlur(16, 4), new Combine(1, 1.2f));
    }

    public static Bloom create(float r, float g, float a) throws Exception {
        final MinColour minColour = new MinColour(r, g, a);
        final GaussianBlur gaussianBlur = new GaussianBlur(16, 4);
        final Combine combine = new Combine(1, 1.2f);
        return new Bloom(minColour, gaussianBlur, combine);
    }

    @Override
    public void beforeProcess(RenderOutputData postProcessingData) {
        combine.setCombination(postProcessingData.getColour());
    }
}
