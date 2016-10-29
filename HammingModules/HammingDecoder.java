package HammingModules;

import java.io.FileInputStream;
import java.io.FileOutputStream;

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
     * Decodes a given file. Determines the dimension, word length and interleaving height from the file name.
     * @param filename The file to decode.
     */
    public void decode(String filename) {
        //sets the values of dimension, word length and interleaving table height from the file name
        this.setFilenameValues(filename);


    }

    private void constructErrorCorrectionMatrix() {

    }

    private void constructDecoderMatrix() {

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

        //sets the values of this decoder depending on what has been read from the file.
        this.dimension = (int) Math.pow(val,2) - val - 1;

        this.wordlength = (int) Math.pow(val,2) - 1;

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
