package source.ErrorModels;

/**
 * Created by bnjhope on 20/10/16.
 */
public interface ITransmissionError {

    /**
     * Simulates whether a bit in a transmission should be flipped or not.
     * @return True if a bit should be flipped according to the model, false if not.
     */
    public boolean flip();
}
