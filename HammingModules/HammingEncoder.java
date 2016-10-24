package HammingModules;

import java.io.*;

/**
 * Created by bnjhope on 20/10/16.
 */
public class HammingEncoder {

    /**
     * The result fthat occurs from reading the file
     * when the reader has reached the end of the file.
     */
    private static final int EOFCONST = -1;

    /**
     * The length of each word of Huffman coding.
     */
    private int wordLength;

    /**
     * The dimension (i.e number of bits that actually contain data) in the Hammaing code word.
     */
    private int dimension;

    /**
     * The input stream for the original file.
     */
    private FileInputStream input;

    /**
     * The output stream for the encoded file.
     */
    private FileOutputStream output;

    /**
     * Encode the given file with Hamming coding and interleaving.
     * @param val The value which the encoder is using for length of word and dimension.
     * @param filename The name of the file to be encoded.
     */
    public void encode(String filename, int val) {

        //the input from the file
        int inputFromFile = 0;

        //a buffer that will store the bits read in from the file
        //and another one for storing bits to be written to the file.
        String inBuffer = "", currBuffer = "", outBuff = "" ,charToBeWritten = "";

        //set up the file streams to be used for writing and reading to a file.
        this.setUpFileStreams(filename, val);

        //calculate the length and the dimension for Hamming coder using the value provided.
        this.calculateLengthAndDimension(val);

        //try to read the file to the end of the file.
        try {
            while((inputFromFile = this.input.read()) != -1) {

                //convert the input from the file into the input buffer
                inBuffer = this.convertIntToBits(inputFromFile);
                while(inBuffer.length() >= this.dimension) {
                    currBuffer = inBuffer.substring(0, this.dimension);
                    outBuff +=
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file stream.\nClosing.");
            System.exit(0);
        }
    }

    /**
     * Sets up the input and output file stream for the encoding.
     * @param filename The name of the file to be read.
     * @param val The value which the encoder is using for length of word and dimension.
     */
    private void setUpFileStreams(String pathOfFile, int val) {

        //create a new file stream creator
        FileStreamCreator streamCreator = new FileStreamCreator();

        //create the output stream with the constructed file name
        this.output = streamCreator.createOutputStream(pathOfFile, val);

        //creates the input stream using the name of the file given to the
        //program.
        this.input = streamCreator.createInputStream(pathOfFile);
    }

    /**
     * Gets the bit representation of the given integer in string format.
     * @param intToConvert The integer to convert to a string of bits.
     * @return A string representing the integer passed to the function in bits.
     */
    private String convertIntToBits(int intToConvert) {
        return String.format("%8s", Integer.toBinaryString(intToConvert & 0xFF)).replace(' ', '0');
    }

    /**
     * Calculates and stores the value of the word length and dimension using the value given.
     * @param val The value with which the word length and dimension should be calculated.
     */
    private void calculateLengthAndDimension(int val) {

        //stores the value of 2^val, since both the length and the dimension use it
        //to calculate their values.
        int twoPower = 0;

                //if the provided value is not greater than or equal to 2 then we must quit the program.
                //otherwise, set the value of twoPower to 2^val cast as an integer.
                if(val < 2) {
                    System.err.println("Value for length and dimesion must be greater than or equal to 2.\nExiting.");
                    System.exit(0);
                } else
                    twoPower = (int) Math.pow(2, (double) val);

        //set the values of the word length and the dimension.
        this.wordLength = twoPower - 1;
        this.dimension = twoPower - val - 1;

    }

    /**
     * Encodes the given bit string into a Hamming code word with parity bits.
     * @param strToConvert The string to convert into a Hamming code word.
     * @return The code word that results from using Hamming encoding on the given string.
     */
    public String convertToHamming(String strToConvert) {
        //the result of applying Hamming encoding on the string to convert.
        char[] result = new char[this.wordLength - 1];



        return new String(result);
    }
}

