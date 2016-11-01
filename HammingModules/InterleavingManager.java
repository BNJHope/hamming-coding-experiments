package HammingModules;

/**
 * Created by bh59 on 26/10/16.
 */
public class InterleavingManager {

    /**
     * The constant we use for the EOFStringSize to determine that we are not using it as it is not the last file
     */
    private static final int NOT_IN_USE = -1;

    /**
     * The height of the grid that the interleaving table represents
     * and the word length for the width of the grid.
     */
    private int height, wordLength, gridSize, EOFStringSize;

    /**
     * The grid of the interleaving table, represented as a 2 dimensional array of characters.
     */
    private char[][] grid;

    public InterleavingManager(int height, int wordLength) {
        this.height = height;
        this.wordLength = wordLength;
        this.gridSize = height * wordLength;
        this.EOFStringSize = NOT_IN_USE;
        this.grid = new char[height][wordLength];
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

        boolean EOFString = false;

        for(int i = 0; i < this.height; i++) {
            for(int j = 0; j < this.wordLength; j++) {
                this.grid[i][j] = strToEncode.charAt(index++);
                if((strToEncode.length() < this.gridSize) && (index == strToEncode.length())) {
                    EOFString = true;
                    this.EOFStringSize = strToEncode.length();
                    break;
                }
            }
            if(EOFString) break;
        }
    }

    /**
     * Adds the string to the table column by column.
     * @param strToDecode The string to add to the table.
     */
    private void decodeIn(String strToDecode) {

        //the index of where in the string the interleaver has reaced
        int index = 0;

        boolean EOFString = false;

        for(int i = 0; i < this.wordLength; i++) {
            for(int j = 0; j < this.height; j++) {
                this.grid[j][i] = strToDecode.charAt(index++);
                if((strToDecode.length() < this.gridSize) && (index == strToDecode.length())) {
                    EOFString = true;
                    this.EOFStringSize = strToDecode.length();
                    break;
                }
            }
            if(EOFString) break;
        }
    }

    /**
     * Reads the string from the table column by column.
     * @return The result of reading bits column by column.
     */
    private String encodeOut() {

        //the string that is constructed from the interleaving process
        String result = "";

        int index = 0;

        boolean EOFStringReached = false;

        for(int i = 0; i < this.wordLength; i++) {
            for(int j = 0; j < this.height; j++) {
                result += this.grid[j][i];
                index++;
                if(this.EOFStringSize != NOT_IN_USE && index == this.EOFStringSize) {
                    EOFStringReached = true;
                    break;
                }
            }
            if(EOFStringReached) break;
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

        int index = 0;

        boolean EOFStringReached = false;

        for(int i = 0; i < this.height; i++) {
            for(int j = 0; j < this.wordLength; j++) {
                result += this.grid[i][j];
                index++;
                if(this.EOFStringSize != NOT_IN_USE && index == this.EOFStringSize) {
                    EOFStringReached = true;
                    break;
                }
            }
            if(EOFStringReached) break;
        }

        return result;

    }

}
