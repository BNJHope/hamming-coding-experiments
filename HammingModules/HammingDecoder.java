package HammingModules;

import java.util.Arrays;
import java.util.HashMap;

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
     * Manages the interleaving process.
     */
    private InterleavingManager interleaveManager;

    /**
     * The number of errors detected in the decoding procedure.
     */
    private int errorCount;

    /**
     * Maps a codeword to its corresponding word if it has previously been constructed.
     */
    private HashMap<String, String> existingCodes;

    public HammingDecoder(int val, int interleaveHeight){
        this.existingCodes = new HashMap();


        //sets the values of dimension and word length
        this.calculateLengthAndDimension(val);

        //sets the interleave height from the given arguments
        this.interleaveHeight = interleaveHeight;

        //instantiate the interleaving manager from the value given by the file name
        this.interleaveManager = new InterleavingManager(this.interleaveHeight, this.wordlength);

        //construct the error correction and decoder matrices using the values for word length
        //and dimension
        this.constructMatrices();
    }

    /**
     * Decodes the given string and tries to correct any errors that it has.
     * @param encodedValue The string of bits to decode.
     */
    public DecodingResult decode(String encodedValue) {

        DecodingResult result;

        //reset the error count
        this.errorCount = 0;

        //the interleaving manager needs to deal with the input string
        String interleaveOutput = this.interleaveManager.decode(encodedValue), outputResult = "";

        //an array to keep the code words that we're reading in and the decoded keyword we have as a result
        String codewords[] = new String[this.interleaveHeight], correctedWords[] = new String[this.interleaveHeight], decodedWords[] = new String[this.interleaveHeight];

        for(int i = 0; i < this.interleaveHeight; i++) {
            codewords[i] = interleaveOutput.substring(0, wordlength);
            interleaveOutput = interleaveOutput.substring(wordlength);
            correctedWords[i] = this.checkForErrors(codewords[i]);
            decodedWords[i] = this.decodeCorrectedCode(correctedWords[i]);
            outputResult += decodedWords[i];
        }

        result = new DecodingResult(this.wordlength, this.interleaveHeight, outputResult, decodedWords, codewords, correctedWords, encodedValue, this.errorCount);

        return result;
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
        this.wordlength = twoPower - 1;
        this.dimension = twoPower - val - 1;

    }

    /**
     * Checks a word from the file for errors and corrects them if it detects that there are any.
     * @param wordToCheck The word to check for errors.
     * @return The word given with any error corrections if any are detected.
     */
    private String checkForErrors(String wordToCheck) {

        //the word converted into a character array.
        char[] wordArray = wordToCheck.toCharArray();

        //holds the errors when detected and their positions
        char[] syndrome = new char[this.wordlength - this.dimension];

        //the sum of the values after the syndrome matrix has been determined
        int syndromeSum = 0;

        //fill in the syndrome matrix by multiplying the error correction matrix and the given word
        for(int i = 0; i < syndrome.length; i++) {
            syndrome[i] = this.multiplyRows(this.errorCorrectionMatrix[i], wordArray);
            if(syndrome[i] != '0') {
                syndromeSum += Math.pow(2, i);
            }

        }

        //if errors were found then increase the error count and correct the error
        if(syndromeSum != 0) {
            this.errorCount++;
            wordArray[syndromeSum - 1] = (wordArray[syndromeSum - 1] == '0') ? '1' : '0';
        }

        return new String(wordArray);
    }

    /**
     * Decodes an error corrected word so it can be written to the file.
     * @param bitsToDecode The string of bits to decode.
     * @return The decode value.
     */
    private String decodeCorrectedCode(String bitsToDecode) {

        //the result that is either found in the existing codes map or
        //is calculated
        String result;

        if(this.existingCodes.containsKey(bitsToDecode)) {
            result = this.existingCodes.get(bitsToDecode);
        } else {
            //an array of bits that are calculated through matrix multiplacation
            //and the bits to decode in array form
            char[] decodeArray = new char[dimension], wordArray = bitsToDecode.toCharArray();

            //multiply every row in the decoder matrix with the word to get the decoded
            //value as a matrix of bits
            for(int i = 0; i < this.dimension; i++) {
                decodeArray[i] = this.multiplyRows(this.decoderMatrix[i], wordArray);
            }

            result = new String(decodeArray);

            this.existingCodes.put(bitsToDecode, result);
        }

        return result;
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

}
