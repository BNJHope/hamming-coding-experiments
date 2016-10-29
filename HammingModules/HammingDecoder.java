package HammingModules;

/**
 * Created by bnjhope on 28/10/16.
 */
public class HammingDecoder {

    private int dimension;

    private int wordlength;

    private int interleaveHeight;

    private char[][] errorCorrectionMatrix;

    private char[][] decoderMatrix;

    public void decode() {

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
    private void getFilenameValues(String filename) {

        //in the file name the values that give the dimension/word length come before the value for the interleave height.
        //Therefore, when we split the string into an array, the value for dimension/word length will come first and then
        //the interleave height value will come next.
        final int valLocation = 0, interleaveHeightLocation = 1;

        String valFindString = "." + FileStreamCreator.fileEncodeKeyword;

        String beginningSegment = filename.substring(filename.indexOf(valFindString) + valFindString.length());

        String componentsSegment = beginningSegment.substring(0, beginningSegment.indexOf("."));

        String[] values = componentsSegment.split("-");

        int val = Integer.parseInt(values[valLocation]), interleaveHeight = Integer.parseInt(values[interleaveHeightLocation]);

        this.dimension = (int) Math.pow(val,2) - val - 1;

        this.wordlength = (int) Math.pow(val,2) - 1;

        this.interleaveHeight = interleaveHeight;
    }

}
