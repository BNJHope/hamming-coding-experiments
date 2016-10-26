package HammingModules;

import java.io.*;
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

        //the resulting string to return at the end of the function.
        String result = "";

        int bp = 0;

        //if the Hamming code for the given string has previously been constructed then fetch it from the Hashmap
        //that maps words to its corresponding Hamming code.
        if(this.existingCodes.containsKey(strToConvert))
            result = this.existingCodes.get(strToConvert);
        else {
            //the result of applying Hamming encoding on the string to convert.
            char[] characterStore = this.constructHammingTemplateString(strToConvert);

            bp = 0;

            //calculate the parity bits from the template string
            characterStore = calculateParityBits(characterStore);

            //convert this result into a string
            result = new String(characterStore);

            bp = 0;

            //put the new code in the hashmap so that if we see this word to encode again then
            //it can just be retrieved from the hashmap.
            this.existingCodes.put(strToConvert, result);
        }

        return result;
    }

    /**
     * Makes a new char array that inserts that 'p' character where a partiy bit will lie in a Hamming code.
     * @param strToConvert The string to produce the template for.
     * @return A char array that has 'p' where a parity bit should lie in the Hamming code.
     */
    public char[] constructHammingTemplateString(String strToConvert) {

        //the array that will contain the parity bit locations and raw characters.
        char[] result = new char[this.wordLength];

        //current position of where in strToConvert we have got to in filling the array with actual data bits.
        //we start from the front of the string.
        int strPos = strToConvert.length() - 1;

        //for every space in the array check to see if its a power of two or not. If it is, then mark it as a parity bit.
        for(int i = result.length - 1; i >= 0; i--) {

            //if the bit at the position i + 1 is a power of 2 then it must be a parity bit
            //so we set its value to 'p' in the array.
            //otherwise, fill out the value of the array with the value of the string
            // at its current position.
            if(((result.length - i) & (result.length - i - 1)) == 0) {
                result[i] = 'p';
            } else {
                result[i] =  strToConvert.charAt(strPos--);
            }

        }

        return result;
    }

    public char[] calculateParityBits(char[] arrayToCheck) {

        //represents the difference between a single digit integer and its ASCII value
        final int ASCIIStep = 48;

        //a holder value for calculating the parity bit,
        //a stepper that determines how far into the array the parity bit is in checking if it should be a 1 or 0,
        //and the power of two which the parity bit holds
        int parityBit = 0, parityStepper = 0, powerValue = 0;

        //determines if we are counting the current characters in the array as
        boolean isParitySegment = true;

        //the character to check to see for parity
        char currentChar = '\0';

        for(int i = arrayToCheck.length - 1; i >= 0; i--) {
            if(arrayToCheck[i] == 'p') {
                //get the power of 2 value by calculating log base 2 of the position of the bit in the Hamming code.
                powerValue = (int) (Math.log(arrayToCheck.length - i) / Math.log(2));

                //this loop determines the value of the parity bit by starting from the position of the parity bit
                //and going to the end of the array and checking the parity of values where necessary
                for(int j = i; j >= 0; j--) {

                    //if the checker is currently in a section for checking the parity of a bit then
                    //perform the XOR on a bit to either flip the current parity bit that we are tracking
                    // if the character at the current index of the array is a 1 or keep it in its
                    //current state if the character we are checking is a 0.
                    if(isParitySegment == true) {

                        //get the current character from the array using the inner index
                        currentChar = arrayToCheck[j];
                        //if we come across a parity bit then we ignore it. This only happens for the very first character
                        //of the search, as later parity bits will not be found in stretches of bits that influence the
                        //parity bit.
                        if(currentChar != 'p') {
                            //XOR the parity bit with the numeric value of the curent bit in the array.
                            parityBit ^= Character.getNumericValue(currentChar);
                        }
                    }

                    //whether we are in a checking phase or not, increase the phase stepper
                    parityStepper++;
                    //if the phase stepper is equal to the power value i.e we have reached the limit of a phase
                    //then that means we flip out of/into a checking phase and we reset the phase stepper to 0.
                    if(parityStepper == Math.pow(2, powerValue)) {
                        parityStepper = 0;
                        isParitySegment = !isParitySegment;
                    }

                }

                //resets is parity segmenet to true for the next bit
                isParitySegment = true;
                parityStepper = 0;

                //once we have calculated the parity bit, place it in the position of the 'p' that we just determined.
                arrayToCheck[i] = (char) (ASCIIStep + parityBit);
            }
        }

        return arrayToCheck;
    }

}

