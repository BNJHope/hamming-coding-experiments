package HammingModules;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by bnjhope on 28/10/16.
 */
public class HammingDecoder {

    /**
     * The result that occurs from reading the file
     * when the reader has reached the end of the file.
     */
    private static final int EOFCONST = -1;

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
     * The number of errors detected in the decoding procedure.
     */
    private int errorCount;

    /**
     * Maps a codeword to its corresponding word if it has previously been constructed.
     */
    private HashMap<String, String> existingCodes;

    /**
     * Decodes a given file. Determines the dimension, word length and interleaving height from the file name.
     * @param filename The file to decode.
     */
    public void decode(String filename) {

        //initialises the existing codes map
        this.existingCodes = new HashMap();

        //initialise the error count at 0
        this.errorCount = 0;

        //sets the values of dimension, word length and interleaving table height from the file name
        this.setFilenameValues(filename);

        //set up the input and output file streams
        this.setUpFileStreams(filename);

        //instantiate the interleaving manager from the value given by the file name
        this.interleaveManager = new InterleavingManager(this.interleaveHeight, );

        //construct the error correction and decoder matrices using the values from the file name
        this.constructMatrices();

        //number of bits in byte
        final int bitsInByte = 8;

        //the value read in from the input file,
        //and the size of the interleaving table
        int intToReadIn = 0, interleaveSize = (int) Math.pow(this.interleaveHeight, 2);

        //string buffers of bits for reading in from the file and writing to the output file.
        String inBuff = "", interleaveOutput = "", outputBuff = "", charToWrite = "";

        //try to read in from the file input stream
        try {

            //while the file reader has not reached the end of the file, carry out the error correction and decode process
            while((intToReadIn = this.input.read()) != EOFCONST) {

                //add the bit representation of the character we just read in onto the end of the buffer
                inBuff += this.convertToBitString(intToReadIn);

                //while the buff is longer than or equal to the size of the interleave table,
                //process the characters through the table so that they can be checked for errors and decoded
                while(inBuff.length() >= interleaveSize) {
                    interleaveOutput += this.interleaveManager.decode(inBuff.substring(0, interleaveSize));
                    inBuff = inBuff.substring(interleaveSize);
                }

                while(interleaveOutput.length() >= wordlength) {
                    outputBuff += this.decodeCorrectedCode(this.checkForErrors(interleaveOutput.substring(0, wordlength)));
                    interleaveOutput = interleaveOutput.substring(wordlength);
                }

                while(outputBuff.length() >= bitsInByte) {
                    charToWrite = outputBuff.substring(0, bitsInByte);
                    this.outputToFile(charToWrite);
                    outputBuff = outputBuff.substring(bitsInByte);
                }
            }

            if(inBuff.length() > 0) {

                //decode the remaining input buffer with the interleave table
                interleaveOutput += this.interleaveManager.decode(inBuff);

                //while the size of the output from the interleave table is greater than the length of a word, error
                //correct and decode it
                while(interleaveOutput.length() >= wordlength) {
                    outputBuff += this.decodeCorrectedCode(this.checkForErrors(interleaveOutput.substring(0, wordlength)));
                    interleaveOutput = interleaveOutput.substring(wordlength);
                }

                //if there are still bits left to be interleaved then add zeroes to the end to make sure that the
                //output buffer can be converted into equal bytes
                if(interleaveOutput.length() > 0) {
                    while(interleaveOutput.length() % wordlength != 0) {
                        interleaveOutput += '0';
                    }
                    outputBuff += this.decodeCorrectedCode(this.checkForErrors(interleaveOutput));

                    while((outputBuff.length() % bitsInByte) != 0)
                       outputBuff += '0';

                }

                while(outputBuff.length() >= bitsInByte) {
                    charToWrite = outputBuff.substring(0, bitsInByte);
                    this.outputToFile(charToWrite);
                    outputBuff = outputBuff.substring(bitsInByte);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.cleanUp();
        System.out.println("Errors found : " + this.errorCount);
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
     * Converts a given integer into a string of 8 bits.
     * @param intToConvert The integer to convert into bits.
     * @return The integer in the form of 8 bits.
     */
    private String convertToBitString(int intToConvert) {

        //convert the integer given to the function into a binary string
        String result = Integer.toBinaryString(intToConvert);

        //add 0s to the front of the string of bits until it has 8 bits in the string, as the toBinaryString method
        //does not add any 0 bits to the front if they are redundant
        while(result.length() < 8) {
            result = '0' + result;
        }

        return result;
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
