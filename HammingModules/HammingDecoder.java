package HammingModules;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Created by bnjhope on 28/10/16.
 */
public class HammingDecoder {

    /**
     * The dimension of the Hamming codes in the file.
     */
    private int dimension;

    /**
     * The word length of the codes in the file.
     */
    private int wordlength;

    /**
     * The height of the interleaver table to use.
     */
    private int interleaveHeight;

    /**
     * The matrix used for correcting errors when they are read in.
     */
    private char[][] errorCorrectionMatrix;

    /**
     * The matrix used for decoding corrected values.
     */
    private char[][] decoderMatrix;

    /**
     * The input stream from the encoded file.
     */
    private FileInputStream input;

    /**
     * The output stream to the decoded file.
     */
    private FileOutputStream output;

    /**
     * Manages the interleaving process.
     */
    private InterleavingManager interleaveManager;

    /**
     * Decodes a given file. Determines the dimension, word length and interleaving height from the file name.
     * @param filename The file to decode.
     */
    public void decode(String filename) {
        //sets the values of dimension, word length and interleaving table height from the file name
        this.setFilenameValues(filename);

        //set up the input and output file streams
        this.setUpFileStreams(filename);

        //instantiate the interleaving manager from the value given by the file name
        this.interleaveManager = new InterleavingManager(this.interleaveHeight);

        //construct the error correction and decoder matrices using the values from the file name
        this.constructMatrices();

    }

    /**
     * Constructs the error correction and decoder matrices from the values fetched by the file name of the input file.
     */
    private void constructMatrices() {

        //instantiate the error correction matrix with as many rows as there are parity bits
        //and a word length number of columns
        this.errorCorrectionMatrix = new char[this.wordlength - this.dimension][wordlength];

        //instantiate the decoder correction matrx with as many rows as there are data bits (i.e the dimension)
        //and a word length number of columns
        this.decoderMatrix = new char[this.dimension][wordlength];

        //create a character array with 'p' at the locations in the array where a parity bit would be located
        //and a '1' at every data bit location
        char[] word = createHammingString();

        //represents a row in the matrix table - will be converted to a character array before being added.
        String result;

        //determines if the current bit that we are checking influences the parity bit or not
        boolean isParity;

        //counts the number of bits that have been checked for bits that influence parity,
        //holds the power of 2,
        //provides steppers through the rows for the decoder matrix and the error correction matrix
        int parityStepper, powerVal, decoderMatrixStepper = 0, errCorrectionStepper = 0;

        //the character that represents a parity bit in the character array
        char parityBit = 'p';

        //for every character in this character array
        for(int i = 0; i < word.length; i++) {

            //if the character at the current index in the array is a parity bit then add to the error-correction matrix
            //if not then add to the decoder matrix
            if(word[i] == parityBit) {

                //get the power which will be used to determine which bits in the array we need to determine
                //each bit in a generator row
                powerVal = (int) (Math.log(i + 1) / Math.log(2));

                //set isParity to true, reset the parity stepper and set the result string as blank
                isParity = true;

                parityStepper = 0;

                result = "";

                //for all the remaining characters in the word array
                for(int j = i; j < word.length; j++) {

                    //if the character at the position is not a parity bit then check if it influences the value
                    //of the parity bit and add to the matrix row depending on that
                    if(word[j] != parityBit) {

                        //if this bit does influence the value of the parity bit then add 1 to the row
                        if(isParity) {
                            result += '1';

                        //otherwise add 0
                        } else {
                            result += '0';
                        }

                    //if the character at this position is a parity bit then add a 0 to the string, unless it is the
                        //first character of the checking position
                    } else {
                        if(j == i)
                            result += '1';
                        else
                            result += '0';
                    }

                    //if we have reached the end of a section in the word to scan for parity bits
                    //then flip the boolean which determines that we need to use bits that we are
                    //currently checking for the parity bit
                    if(++parityStepper == Math.pow(2, powerVal)) {
                        parityStepper = 0;
                        isParity = !isParity;
                    }

                }

                if(result.length() != this.wordlength)
                    //fill the start of the row with 0s if it is not as long as the word length specifies
                    result = this.fillStartWith0s(result);

                this.errorCorrectionMatrix[errCorrectionStepper++] = result.toCharArray();

            //if the bit being checked is not a parity bit then generat
            } else {
                this.decoderMatrix[decoderMatrixStepper++] = this.generateDecodeRow(i);
            }
        }

    }
    /**
     * Fills the beginning of a given string with 0s until it reaches the length of the dimension.
     * @param str The string to complete with 0s at the beginning
     * @return The string with 0s filled in at the beginning.
     */
    private String fillStartWith0s(String str) {

        while(str.length() < this.wordlength) {
            str = '0' + str;
        }

        return str;
    }


    /**
     * Gives a string of 0s but with a 1 at the given index for when we need to add a row to the decoder matrix.
     * @param index The index where the 1 is located in the row.
     * @return An array of 0s with 1 at the given index.
     */
    private char[] generateDecodeRow(int index) {

        char[] wordToReturn = new char[wordlength];

        Arrays.fill(wordToReturn, '0');

        wordToReturn[index] = '1';

        return wordToReturn;
    }

    /**
     * Creates a character array with '1' placed on data parts and 'p' placed on parity bit positions
     * @return A character array with '1' at data bits and 'p' at parity bits
     */
    private char[] createHammingString() {

        //how data and parity bits are represented in the array
        char parityBit = 'p', dataBit = '1';

        //stepper for the power of 2 so that each parity bit is filled in
        int parityBitFiller = 0;

        //the character array to be returned
        char[] word = new char[this.wordlength];

        //fill the entire array with a '1' to denote a data bit, and then we fill
        //all cells with index of (power of 2) - 1 with a p to differentiate.
        Arrays.fill(word, dataBit);
        while(Math.pow(2, parityBitFiller) < this.wordlength) {
            word[((int) Math.pow(2, parityBitFiller++)) - 1] = parityBit;
        }

        return word;
    }

    /**
     * Get the value used for dimension and word length from the encoded file name and also the height of the
     * interleaving table.
     * @param filename The file name to get the values from.
     */
    private void setFilenameValues(String filename) {

        //in the file name the values that give the dimension/word length come before the value for the interleave height.
        //Therefore, when we split the string into an array, the value for dimension/word length will come first and then
        //the interleave height value will come next.
        final int valLocation = 0, interleaveHeightLocation = 1;

        //the power of two needed to calculate the dimension and word length
        int twoPower = 0;

        //the part of the file that determines that it is an encoded file. We need this so that we can jump
        //straight to the part in the file name that contains both the dimension/word length value and the interleaving
        //value
        String valFindString = "." + FileStreamCreator.fileEncodeKeyword;

        //removes file name and path to file and also the encode file key word so that we can get the values
        //that we need
        String beginningSegment = filename.substring(filename.indexOf(valFindString) + valFindString.length());

        //removes anything after the values that we need
        String componentsSegment = beginningSegment.substring(0, beginningSegment.indexOf("."));

        //splits the values string into an array of two values - the first determines dimension/word length and the second
        //determines the height of the interleaving table
        String[] values = componentsSegment.split("-");

        //the two values in the array converted into integers
        int val = Integer.parseInt(values[valLocation]), interleaveHeight = Integer.parseInt(values[interleaveHeightLocation]);

        twoPower = (int) Math.pow(2,val);

        //sets the values of this decoder depending on what has been read from the file.
        this.dimension = twoPower - val - 1;

        this.wordlength = twoPower - 1;

        this.interleaveHeight = interleaveHeight;
    }

    /**
     * Sets up the input and output file stream for the decoding.
     * @param pathOfFile The name of the file to be read.
     */
    private void setUpFileStreams(String pathOfFile) {

        //create a new file stream creator
        FileStreamCreator streamCreator = new FileStreamCreator();

        //create the output stream with the constructed file name
        this.output = streamCreator.createDecoderOutputStream(pathOfFile);

        //creates the input stream using the name of the file given to the
        //program.
        this.input = streamCreator.createInputStream(pathOfFile);
    }

}
