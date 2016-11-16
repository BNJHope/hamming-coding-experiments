package HammingModules;

import java.util.Random;

/**
 * Created by bh59 on 15/11/16.
 */
public class Channel {

    /**
     * This represents that there will be an equal probability of either a 1 or a 0 being transferred
     * through the channel.
     */
    private static final double PROBABILITY_OF_ZERO_BIT = 0.5;

    /**
     * The generator of the random numbers.
     */
    private Random randomGen;

    /**
     * The dimension - which is the number of bits to return from the channel.
     */
    private int dimension;

    public Channel(int dimension) {
        this.dimension = dimension;
        this.randomGen = new Random();
    }

    /**
     * Generates a dimension length of bits to return with equal probabilities of a 0 or 1.
     * @return A string of the dimension length of bits.
     */
    public String getBits() {

        //the string of bits to return
        String result = "";

        //the value from the random generator
        double bitVal;

        //generate the string by getting a random number between 0 and 1 -
        //if the number is less than 0.5 then add a '0' to the result string,
        //otherwise add a '1' to the result string.
        for(int i = 0; i < dimension; i++) {
            bitVal = this.randomGen.nextDouble();
            result += (bitVal < PROBABILITY_OF_ZERO_BIT) ? '0' : '1';
        }

        return result;
    }
}
