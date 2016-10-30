package HammingModules;

/**
 * Created by bh59 on 26/10/16.
 */
public class InterleavingManager {

    /**
     * The length and width of the grid that the interleaving table represents.
     */
    private int dimension;

    /**
     * The grid of the interleaving table, represented as a 2 dimensional array of characters.
     */
    private char[][] grid;

    /**
     * When it has been detected that the size of the interleaving table is bigger than the input string, which will
     * occur at the end of the file, then this value stores how long the last string is so that the output manager
     * knows how far into the table it needs to read.
     */
    private int EOFIndex;

    public InterleavingManager(int dimension) {
        this.dimension = dimension;
        this.grid = new char[dimension][dimension];
        this.EOFIndex = -1;
    }

    /**
     * Encodes a string with the interleaving table.
     * @param strToEncode The string to encode.
     * @return The string encoded with interleaving.
     */
    public String encode(String strToEncode) {

        //read the string into the table row by row.
        this.encodeIn(strToEncode);

        //output the result of reading the grid column by column
        return this.encodeOut();
    }

    /**
     * Decodes a string with the interleaving table.
     * @param strToDecode The string to decode.
     * @return The string decoded with interleaving.
     */
    public String decode(String strToDecode) {

        //read the string into the table column by column.
        this.decodeIn(strToDecode);

        //output the result of reading the grid row by row
        return this.decodeOut();
    }

    /**
     * Adds the string to the table row by row.
     * @param strToEncode The string to add to the table.
     */
    private void encodeIn(String strToEncode) {

        int index = 0;

        boolean EOFReached = false;

        if(strToEncode.length() < Math.pow(this.dimension, 2)) {
            this.EOFIndex = strToEncode.length();
        }

        for(int i = 0; i < this.dimension; i++) {
            for(int j = 0; j < this.dimension; j++) {
                this.grid[i][j] = strToEncode.charAt(index++);
                if(index == this.EOFIndex) {
                    EOFReached = true;
                    break;
                }
            }
            if(EOFReached)
                break;
        }
    }

    /**
     * Adds the string to the table column by column.
     * @param strToDecode The string to add to the table.
     */
    private void decodeIn(String strToDecode) {

        //the index of where in the string the interleaver has reaced
        int index = 0;

        //for when we are interleaving with the last bits in the file - determines if the end of the string
        //has been reached so that the interleaver can break the loops
        boolean EOFReached = false;

        //if the size of the input string is less than the size of the grid then set the limit to the interleaving
        //process as the
        if(strToDecode.length() < Math.pow(this.dimension, 2)) {
            this.EOFIndex = strToDecode.length();
        }

        for(int i = 0; i < this.dimension; i++) {
            for(int j = 0; j < this.dimension; j++) {
                this.grid[j][i] = strToDecode.charAt(index++);
                if(index == this.EOFIndex) {
                    EOFReached = true;
                    break;
                }
            }
            if(EOFReached)
                break;
        }
    }

    /**
     * Reads the string from the table column by column.
     * @return The result of reading bits column by column.
     */
    private String encodeOut() {

        //the string that is constructed from the interleaving process
        String result = "";

        //if this string of bits is the last to be read from the file, then we need to track that we do not
        //use too many characters from the grid as they will not all have been formed from the input. Therefore,
        //this stepper keeps track of which part of the string we are at so we can check if we have reached the input
        //limit or not
        int eofStepper = 0;

        //determines if we have reached limit of the end of file string or not
        boolean EOFReached = false;

        for(int i = 0; i < this.dimension; i++) {
            for(int j = 0; j < this.dimension; j++) {
                result += this.grid[j][i];

                //if this is the last string of bits from the file then make sure we track the stepper
                if(EOFIndex != - 1) {
                    eofStepper++;

                    //if the stepper has reached the limit then break the inner loop and set the flag
                    //for breaking the outer loop
                    if(eofStepper == EOFIndex){
                        EOFReached = true;
                        break;
                    }
                }
            }
            //if the end of file string limit flag has been set by the inner loop then break the outer loop
            if(EOFReached) break;
        }

        return result;
    }

    /**
     * Reads the string from the table row by row.
     * @return The result of reading bits row by row.
     */
    private String decodeOut() {

        //the string that is constructed from the interleaving process
        String result = "";

        //if this string of bits is the last to be read from the file, then we need to track that we do not
        //use too many characters from the grid as they will not all have been formed from the input. Therefore,
        //this stepper keeps track of which part of the string we are at so we can check if we have reached the input
        //limit or not
        int eofStepper = 0;

        //determines if we have reached limit of the end of file string or not
        boolean EOFReached = false;

        for(int i = 0; i < this.dimension; i++) {
            for(int j = 0; j < this.dimension; j++) {
                result += this.grid[i][j];

                //if this is the last string of bits from the file then make sure we track the stepper
                if(EOFIndex != - 1) {
                    eofStepper++;

                    //if the stepper has reached the limit then break the inner loop and set the flag
                    //for breaking the outer loop
                    if(eofStepper == EOFIndex){
                        EOFReached = true;
                        break;
                    }
                }
            }

            //if the end of file string limit flag has been set by the inner loop then break the outer loop
            if(EOFReached) break;
        }

        return result;

    }

}
