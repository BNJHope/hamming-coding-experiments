package HammingModules;

/**
 * Created by bh59 on 26/10/16.
 */
public class InterleavingManager {

    /**
     * The length and width of the grid that the interleaving table represents.
     */
    private int dimension;

    private char[][] grid;

    public InterleavingManager(int dimension) {
        this.dimension = dimension;
        this.grid = new char[dimension][dimension];
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
    public void encodeIn(String strToEncode) {

        int index = 0;

        for(int i = 0; i < this.dimension; i++) {
            for(int j = 0; j < this.dimension; j++) {
                this.grid[i][j] = strToEncode.charAt(index++);
            }
        }
    }

    /**
     * Adds the string to the table column by column.
     * @param strToDecode The string to add to the table.
     */
    public void decodeIn(String strToDecode) {

        int index = 0;

        for(int i = 0; i < this.dimension; i++) {
            for(int j = 0; j < this.dimension; j++) {
                this.grid[j][i] = strToDecode.charAt(index++);
            }
        }
    }

    /**
     * Reads the string from the table column by column.
     * @return The result of reading bits column by column.
     */
    public String encodeOut() {

        String result = "";

        for(int i = 0; i < this.dimension; i++) {
            for(int j = 0; j < this.dimension; j++) {
                result += this.grid[j][i];
            }
        }

        return result;
    }

    /**
     * Reads the string from the table row by row.
     * @return The result of reading bits row by row.
     */
    public String decodeOut() {

        String result = "";

        for(int i = 0; i < this.dimension; i++) {
            for(int j = 0; j < this.dimension; j++) {
                result += this.grid[i][j];
            }
        }

        return result;

    }

}
