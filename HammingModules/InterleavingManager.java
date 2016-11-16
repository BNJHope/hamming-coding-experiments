package HammingModules;

/**
 * Created by bh59 on 26/10/16.
 */
public class InterleavingManager {


    /**
     * The height of the grid that the interleaving table represents
     * and the word length for the width of the grid.
     */
    private int height, wordLength;

    /**
     * The grid of the interleaving table, represented as a 2 dimensional array of characters.
     */
    private char[][] grid;

    public InterleavingManager(int height, int wordLength) {
        this.height = height;
        this.wordLength = wordLength;
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

        for(int row = 0; row < this.height; row++)
            for(int col = 0; col < this.wordLength; col++)
                this.grid[row][col] = strToEncode.charAt(index++);
    }

    /**
     * Adds the string to the table column by column.
     * @param strToDecode The string to add to the table.
     */
    private void decodeIn(String strToDecode) {

        //the index of where in the string the interleaver has reaced
        int index = 0;

        for(int col = 0; col < this.wordLength; col++)
            for(int row = 0; row < this.height; row++)
                this.grid[row][col] = strToDecode.charAt(index++);

    }

    /**
     * Reads the string from the table column by column.
     * @return The result of reading bits column by column.
     */
    private String encodeOut() {

        //the string that is constructed from the interleaving process
        String result = "";

        for(int col = 0; col < this.wordLength; col++)
            for(int row = 0; row < this.height; row++)
                result += this.grid[row][col];

        return result;
    }

    /**
     * Reads the string from the table row by row.
     * @return The result of reading bits row by row.
     */
    private String decodeOut() {

        //the string that is constructed from the interleaving process
        String result = "";

        for(int row = 0; row < this.height; row++)
            for(int col = 0; col < this.wordLength; col++)
                result += this.grid[row][col];

        return result;
    }

}
