package ErrorModels;

/**
 * Created by bnjhope on 20/10/16.
 */
public class BurstErrorModel implements ITransmissionError {


    /**
     * The current state of the error model. If it is true, then it is in a good state.
     * If it is false, then it is in a bad state, where a bit will be flipped.
     */
    private int goodState = 1;

    /**
     * The probability that the burst model will go from a good state and stay in good state
     */
    private double pOfGoodToGood;

    /**
     * The probability that the burst model will go from a bad state and stay in bad state
     */
    private double pOfBadToBad;

    /**
     * The probability that the burst model will go from a good state to a bad state
     */
    private double pOfGoodToBad;

    /**
     * The probability that the burst model will go from a bad state to a good state
     */
    private double pOfBadToGood;

    /**
     * The probability for when the burst model is in its bad state that a bit will be flipped.
     */
    private double pOfError;

    /**
     * The probability for when the burst model is in its bad sate that a bit will not be flipped
     */
    private double pOfNoError;
}
