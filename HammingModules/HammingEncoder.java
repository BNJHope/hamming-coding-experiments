package HammingModules;

import ErrorModels.BurstErrorModel;

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
    private HashMap<String, String> existingCodes;

    /**
     * Manages the interleaving process.
     */
    private InterleavingManager interleaveManager;

    /**
     * The burst error model to be used for transmission.
     */
    private BurstErrorModel errModel;

    /**
     * Encodes the file using the given parameters for the burst error model and the height of the interleave table.
     * @param filename The file to encode.
     * @param val The value to determine the dimension and word length of the encoding.
     * @param pOfError The probability of an error in the burst error model.
     * @param pOfGoodToBad The probability of transitioning from a good to a bad state in the burst error model.
     * @param pOfBadToGood The probability of transitioning from a good to a bad state in the burst error model.
     * @param interleaveHeight The height and width of the interleaving table to be used by the interleaver.
     */
    public void encode(String filename, int val, double pOfError, double pOfGoodToBad, double pOfBadToGood, int interleaveHeight) {

        //initialises the existing codes map
        this.existingCodes = new HashMap();

        //initialises the burst error model
        this.errModel = new BurstErrorModel(pOfError, pOfGoodToBad, pOfBadToGood);

        //initialises the interleave manager of this Hamming Encoder
        this.interleaveManager = new InterleavingManager(interleaveHeight);

        //the input from the file and the size of the interleave table
        int inputFromFile = 0, interleaveTableSize = (int) Math.pow(interleaveHeight, 2);

        //the number of bits in a byte - when we have a string of bits, we need to figure out when it is this long
        //so that it can be written to the file.
        final int bitsLimit = 8;

        //a buffer that will store the bits read in from the file
        //and another one for storing bits to be written to the file.
        String inBuffer = "", currBuffer = "", outBuff = "" , interleaveOutput = "", charToBeWritten = "";

        //set up the file streams to be used for writing and reading to a file.
        this.setUpFileStreams(filename, val, interleaveHeight);

        //calculate the length and the dimension for Hamming coder using the value provided.
        this.calculateLengthAndDimension(val);

        //setup the gernator matrix
        this.constructGeneratorMatrix();

        //try to read the file to the end of the file.
        try {
            while((inputFromFile = this.input.read()) != EOFCONST) {

                //convert the input from the file into the input buffer
                inBuffer = this.convertIntToBits(inputFromFile);

                //while the size of the input buffer is greater than or equal to the size of the dimension,
                //remove dimension-sized chunks of the input buffer from the front and convert them into Hamming codes
                //that can be added to the file
                while(inBuffer.length() >= this.dimension) {

                    //convert a dimension sized chunk into a Hamming code and add it to the buffer
                    currBuffer = inBuffer.substring(0, this.dimension);
                    outBuff += this.convertToHamming(currBuffer);

                    //while the size of the output buffer is greater than or equal to the size of the interleave
                    //table, carry out the interleaving process on chunks of the size of the table and add them to
                    //the output buffer so that they are ready to be added to the file
                    while(outBuff.length() >= interleaveTableSize) {
                        interleaveOutput += this.interleaveManager.encode(outBuff.substring(0, interleaveTableSize));
                        outBuff = outBuff.substring(interleaveTableSize);
                    }

                    while(interleaveOutput.length() >= bitsLimit) {
                        charToBeWritten = this.generateErrorString(interleaveOutput.substring(0, bitsLimit));
                        outputToFile(charToBeWritten);
                        interleaveOutput = interleaveOutput.substring(bitsLimit);
                    }

                    inBuffer = inBuffer.substring(this.dimension);
                }
            }

        //if there are still bits leftover but not enough to conver into a Hamming codeword on their own
        //then add 0s onto the end of the buffer till they become long enough to be converted into a Hamming
        //codeword and transfer the bits.
        if(inBuffer.length() > 0) {

            outBuff += this.convertToHamming(this.addZeroesToEndOfWord(inBuffer));

            //while the size of the output buffer is greater than or equal to the size of the interleave
            //table, carry out the interleaving process on chunks of the size of the table and add them to
            //the output buffer so that they are ready to be added to the file
            while(outBuff.length() >= interleaveTableSize) {
                interleaveOutput += this.interleaveManager.encode(outBuff.substring(0, interleaveTableSize));
                outBuff = outBuff.substring(interleaveTableSize);
            }

            while(interleaveOutput.length() >= bitsLimit) {
                charToBeWritten = this.generateErrorString(interleaveOutput.substring(0, bitsLimit));
                outputToFile(charToBeWritten);
                interleaveOutput = interleaveOutput.substring(bitsLimit);
            }
        }

        } catch (IOException e) {
            System.err.println("Error reading file stream.\nClosing.");
            System.exit(0);
        }

        //close the input and output streams.
        this.cleanUp();
    }

    /**
     * If a word is at the end of a file then it may not be long enough to be encoded into a Hamming code word. Therefore,
     * we add 0s to the end of it so that it is long enough, but this process will not disrupt the decoding process.
     * @param str The word to add 0s to the end of.
     * @return The word with 0s on the end.
     */
    private String addZeroesToEndOfWord(String word) {

        //while the word's length is less than the dimension, add 0s to the end of it.
        while(word.length() < this.dimension) {
            word += '0';
        }

        return word;
    }

    /**
     * Closes the input and output file streams.
     */
    private void cleanUp() {

        //try to close the input streams - if it does not work then tell the user.
        try {
            this.input.close();
            this.output.close();
        } catch (IOException e) {
            System.err.println("Error closing streams.\nExiting.");
            System.exit(1);
        }

    }

    /**
     * If a string of bits is not long enough to be converted into bytes then add 0s to the end so that
     * the decoding procedure is not interrupted.
     * @param strToAdd The string to add 0s to the end of.
     * @return The string of bits passed to the function with 0s at the end.
     */
    private String addZeroesToEnd(String strToAdd) {

        //number of bits in a byte, which is the limit for adding 0s on to.
        final int bitsInByte = 8;

        //while the string does not have enough characters left yet to form a byte, add 0s to the end
        while(strToAdd.length() < bitsInByte)
            strToAdd += '0';

        return strToAdd;
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

    /**
     * Writes the given string of bits to the file.
     * @param strToWrite The bits to write to the file.
     */
    private void outputToFile(String strToWrite) {

        //turns the string of bits into the integer that it represents so that it can be written to the file.
        int infoToWrite = Integer.parseInt(strToWrite, 2);

        try {
            this.output.write(infoToWrite);
        } catch (IOException e) {
            System.err.println("Problem writing with output stream.\nExiting");
            System.exit(1);
        }

    }

    /**
     * Sets up the input and output file stream for the encoding.
     * @param pathOfFile The name of the file to be read.
     * @param val The value which the encoder is using for length of word and dimension.
     * @param interleaveHeight The height of the interleaving table.
     */
    private void setUpFileStreams(String pathOfFile, int val, int interleaveHeight) {

        //create a new file stream creator
        FileStreamCreator streamCreator = new FileStreamCreator();

        //create the output stream with the constructed file name
        this.output = streamCreator.createEncoderOutputStream(pathOfFile, val, interleaveHeight);

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
     * @return A character array with '1' at data bits and 'p' at parity bits
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

        //the result to be returned.
        String result;

        //if the string that needs to be converted to a Hamming code has already been seen before and so there is a code
        //for it then fetch it from the existing codes map instead of recalculating it.
        //If this is the first time that it has been seen then we have to calculate it first.
        if (this.existingCodes.containsKey(strToConvert)) {

            result = this.existingCodes.get(strToConvert);

        } else {
            //the resulting character array that we get from the matrix multiplication
            char[] resultChars = new char[wordLength], strToConvertCharArray = strToConvert.toCharArray();

            //the bit to add to the result array that is calculated from the generator matrix
            char bitToAdd;

            //calculate every bit of the result to add
            for (int i = 0; i < resultChars.length; i++) {
                bitToAdd = multiplyRows(this.generator[i], strToConvertCharArray);
                resultChars[i] = bitToAdd;
            }

            //create the result string by converting the character array into a string
            result = new String(resultChars);

            //put the new code into the hashmap with the string passed to the function as the key
            this.existingCodes.put(strToConvert,result);

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
}

