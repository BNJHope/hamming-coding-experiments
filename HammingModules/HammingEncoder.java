package HammingModules;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by bnjhope on 20/10/16.
 */
public class HammingEncoder {

    /**
     * The result that occurs from reading the file
     * when the reader has reached the end of the file.
     */
    private static final int EOFCONST = -1;

    /**
     * The length of each word of Huffman coding.
     */
    private int wordLength;

    /**
     * The dimension (i.e number of bits that actually contain data) in the Hamming code word.
     */
    private int dimension;

    /**
     * The generator matrix to be used by the encoder.
     */
    private char[][] generator;

    /**
     * The input stream for the original file.
     */
    private FileInputStream input;

    /**
     * The output stream for the encoded file.
     */
    private FileOutputStream output;

    /**
     * Maps a word to its corresponding codeword if it has previously been constructed.
     */
    private HashMap<String, String> existingCodes = new HashMap();

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

        //setup the gernator matrix
        this.constructGeneratorMatrix();

        //try to read the file to the end of the file.
        try {
            while((inputFromFile = this.input.read()) != -1) {

                //convert the input from the file into the input buffer
                inBuffer = this.convertIntToBits(inputFromFile);
                while(inBuffer.length() >= this.dimension) {
                    currBuffer = inBuffer.substring(0, this.dimension);
                    outBuff += this.convertToHamming(currBuffer);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file stream.\nClosing.");
            System.exit(0);
        }
    }

    /**
     * Sets up the input and output file stream for the encoding.
     * @param pathOfFile The name of the file to be read.
     * @param val The value which the encoder is using for length of word and dimension.
     */
    private void setUpFileStreams(String pathOfFile, int val) {

        //create a new file stream creator
        FileStreamCreator streamCreator = new FileStreamCreator();

        //create the output stream with the constructed file name
        this.output = streamCreator.createOutputStream(pathOfFile, val, true);

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
                    System.err.println("Value for length and dimension must be greater than or equal to 2.\nExiting.");
                    System.exit(0);
                } else
                    twoPower = (int) Math.pow(2, (double) val);

        //set the values of the word length and the dimension.
        this.wordLength = twoPower - 1;
        this.dimension = twoPower - val - 1;

    }

    /**
     * Constructs the generator matrix once the dimension and word length have been calculated.
     */
    private void constructGeneratorMatrix() {

        //initialise the generator matrix with the word length and dimension
        this.generator = new char[wordLength][dimension];

        //the word for which we base all of our Hamming calculations on
        char[] word = createHammingString();

        //a string of bits to add to the generator matrix
        String generatorString;

        //determines whether the bit in the array that we are checking influences the parity bit or not.
        boolean isParity = false;

        //the index of where a data bit is
        int dataBitIndex = 0, parityStepper = 0, powerVal = 0;

        //create a row in the generator matrix for character in the word.
        for(int i = 0; i < word.length; i++) {

            //if the current character that we are checking in the word is a parity bit then
            //create the character array to add to the generator matrix in relation to this parity bit.
            if(word[i] == 'p') {

                //get the power which will be used to determine which bits in the array we need to determine
                //each bit in a generator row
                powerVal = (int) (Math.log(i + 1) / Math.log(2));

                //set the parity stepper to true before going through the array
                //as we take in the first characters
                isParity = true;

                //set the parity stepper to 0
                parityStepper = 0;

                //initialise a new generator string
                generatorString = new String();

                //for every character that comes after the current parity bit, determine if any of these data bits
                //influence the value of the parity bit
                for(int j = i; j < word.length; j++) {

                    //if the current bit in the word is a data bit
                    if(word[j] == '1') {

                        //if this bit influences the parity check of the parity bit then add a one to the
                        //string which we will add to the generator
                        if(isParity) {
                            generatorString += '1';

                        //if this bit does not influence the parity bit then add a 0 to the generator row
                        } else {
                            generatorString += '0';
                        }
                    }

                    //if we have reached the end of a section in the word to scan for parity bits
                    //then flip the boolean which determines that we need to use bits that we are
                    //currently checking for the parity bit
                    if(++parityStepper == Math.pow(2, powerVal)) {
                        parityStepper = 0;
                        isParity = !isParity;
                    }
                }

                //since we do not check the start of the word for later characters, but we know these
                //will begin with 0, we can fill the beginning of a parity row in the generator matrix with
                //0s if we know that after this process of calculating it that it is not as long as the dimension
                if(generatorString.length() < dimension)
                    generatorString = fillStartWith0s(generatorString);

                //add this row to the generator matrix
                this.generator[i] = generatorString.toCharArray();

            //if it is a data bit then add a data bit row to the generator matrix
            } else {
                this.generator[i] = this.generateDataRow(dataBitIndex++);
            }
        }

    }

    /**
     * Fills the beginning of a given string with 0s until it reaches the length of the dimension.
     * @param str The string to complete with 0s at the beginning
     * @return The string with 0s filled in at the beginning.
     */
    private String fillStartWith0s(String str) {

        while(str.length() < dimension) {
            str = '0' + str;
        }

        return str;
    }

    /**
     * Gives a string of 0s but with a 1 at the given index for when we need to add a data bit row to the generator matrix.
     * @param index The index where the 1 is located in the row.
     * @return An array of 0s with 1 at the given index.
     */
    private char[] generateDataRow(int index) {

        char[] wordToReturn = new char[dimension];

        Arrays.fill(wordToReturn, '0');

        wordToReturn[index] = '1';

        return wordToReturn;
    }

    /**
     * Creates a character array with '1' placed on data parts and 'p' placed on parity bit positions
     * @return
     */
    private char[] createHammingString() {

        //stepper for the power of 2 so that each parity bit is filled in
        int parityBitFiller = 0;

        //the character array to be returned
        char[] word = new char[this.wordLength];

        //fill the entire array with a '1' to denote a data bit, and then we fill
        //all cells with index of (power of 2) - 1 with a p to differentiate.
        Arrays.fill(word, '1');
        while(Math.pow(2, parityBitFiller) < this.wordLength) {
            word[((int) Math.pow(2, parityBitFiller++)) - 1] = 'p';
        }

        return word;
    }

    /**
     * Encodes the given bit string into a Hamming code word with parity bits.
     * @param strToConvert The string to convert into a Hamming code word.
     * @return The code word that results from using Hamming encoding on the given string.
     */
    private String convertToHamming(String strToConvert) {

        //the resulting character array that we get from the matrix multiplication
        char[] result = new char[wordLength], strToConvertCharArray = strToConvert.toCharArray();

        //the bit to add to the result array that is calculated from the generator matrix
        char bitToAdd;

        //calculate every bit of the result to add
        for(int i = 0; i < result.length; i++) {
            bitToAdd = multiplyRows(this.generator[i], strToConvertCharArray);
            result[i] = bitToAdd;
        }

        return new String(result);
    }

    /**
     * Perform matrix multiplication modulo 2 on two given rows.
     * @param row1 The first to perform the multiplication on.
     * @param row2 The second row to perform the multiplication on.
     * @return The result of the multiplication modulo 2, given as a char.
     */
    private char multiplyRows(char[] row1, char[] row2) {

        //the step between a digit and its ascii representation
        final int asciiStep = 48;

        //the sum of the bits resulting from the matrix multiplication
        int sum = 0;

        for(int i = 0; i < row1.length; i++) {
            sum += Character.getNumericValue(row1[i]) * Character.getNumericValue(row2[i]);
        }

        return(char) ((sum % 2) + asciiStep);
    }
}

