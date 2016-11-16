package source.HammingModules;

import source.ErrorModels.BurstErrorModel;

/**
 * Created by bnjhope on 16/11/16.
 */
public class HammingManager {

    /**
     * The Hamming encoder used by this Hamming manager.
     */
    private HammingEncoder encoder;

    /**
     * The error modeul used for the encoding and decoding.
     */
    private BurstErrorModel errModel;

    /**
     * The Hamming decoder used by this Hamming manager.
     */
    private HammingDecoder decoder;

    /**
     * Carry out the Hamming encoding and decoding process with the given probabilities.
     * @param val The 'r' value needed to calculate the word length and the dimension.
     * @param pOfError The probability that a bit is flipped while the error model is in a bad state.
     * @param pOfGoodToBad The probability that while being in a good state that the model flips into a bad state.
     * @param pOfBadToGood The probability that while being in a bad state that the model flips into a good state.
     * @param interleaveHeight The height of the interleaving table to be used.
     */
    public void doHammingProcess(int val, double pOfError, double pOfGoodToBad, double pOfBadToGood, int interleaveHeight, boolean singleOutput) {

        //the number of iterations we want to during encoding.
        int numberOfIterations;

        if(singleOutput)
            numberOfIterations = 3;
        else
            numberOfIterations = 1000;

        //the result from the encoder
        EncodingResult encoderResult;

        //result from errors
        String errorResult;

        //result from decoding
        DecodingResult decodingResult;

        //the total number of errors that occurred over the iterations
        int totalErrorCount = 0, totalBitsTransferred = 0;

        //The success rate of the error corrections and the average across all iterations
        double successRateAverage;

        //construct the Hamming decoder based on the value given for the interleave
        //height and the value for calculating the dimension and word length
        this.encoder = new HammingEncoder(val, interleaveHeight);

        //make a new error model based on the probabilities passed to the function
        this.errModel = new BurstErrorModel(pOfError, pOfGoodToBad, pOfBadToGood);

        //construct the Hamming decoder based on the value given for the interleave
        //height and the value for calculating the dimension and word length
        this.decoder = new HammingDecoder(val, interleaveHeight);

        //for the number of iterations we want, perform the
        //process.
        for(int i = 0; i < numberOfIterations; i++) {
            //produce an encoding result
            encoderResult = this.encoder.encode();

            if(singleOutput) {
                encoderResult.outputCodewordConversions();
                encoderResult.outputInterleaveResults();
            }

            //put errors in the result from the encoder
            errorResult = this.generateErrorString(encoderResult.getInterleavedResult());

            //decode the string of bits that we have
            decodingResult = this.decoder.decode(errorResult);

            if(singleOutput) {
                decodingResult.outputReceivedWords();
                decodingResult.outputCodewordConversions();
            }

            totalBitsTransferred += encoderResult.getInputStream().length();
            totalErrorCount += this.getErrors(encoderResult.getInputStream(), decodingResult.getResultString());
        }

        //total bits transferred in all iterations
        successRateAverage = this.getAverage(totalErrorCount, totalBitsTransferred);

        System.out.println(String.format("r : %d \t pOfError : %.1f \t pGB : %.1f \t pBG : %.1f \t Interleave Height : %d \t Average Success Rate : %f", val, pOfError, pOfGoodToBad, pOfBadToGood, interleaveHeight, successRateAverage));
    }

    /**
     * Creates errors in a string of bits using the burst error model.
     * @param strForErrors The string to produce errors for.
     * @return The string of bits with errors in depending on the burst error model.
     */
    private String generateErrorString(String strForErrors) {

        //the resulting string to return
        String result = "";

        for(int i = 0; i < strForErrors.length(); i++) {
            if(this.errModel.flip()) {
                result += (strForErrors.charAt(i) == '0') ? '1' : '0';
            } else {
                result += strForErrors.charAt(i);
            }
        }

        return result;
    }

    private int getErrors(String input, String output) {

        int totalBits = input.length(), errorCount = 0;

        for(int i = 0; i < totalBits; i++) {
            if(input.charAt(i) != output.charAt(i))
                errorCount++;
        }

        return errorCount;

    }

    private double getAverage(int errorCount, int totalBits) {

        return ((double) (totalBits - errorCount) / totalBits) * 100;

    }

}
