package HammingCoding.HammingModules;

/**
 * Created by bh59 on 16/11/16.
 */
public class EncodingResult {

    /**
     * Length of the code words.
     */
    private int wordlength;

    /**
     * The height of the interleaving table used.
     */
    private int interleaveHeight;

    /**
     * The words input from the channel combined into one string.
     */
    private String inputStream;

    /**
     * The words given by the channel that were encoded.
     */
    private String[] words;

    /**
     * The Hamming codewords.
     */
    private String[] codewords;

    /**
     * The stream of bits that has resulted from interleaving with the code words.
     */
    private String interleavedResult;

    public EncodingResult(int wordlength, int interleaveHeight, String inputStream, String[] words, String[] codewords, String interleavedResult) {
        this.wordlength = wordlength;
        this.interleaveHeight = interleaveHeight;
        this.inputStream = inputStream;
        this.words = words;
        this.codewords = codewords;
        this.interleavedResult = interleavedResult;
    }

    public String getInputStream() {
        return inputStream;
    }

    public String getInterleavedResult() {
        return this.interleavedResult;
    }

    /**
     * Output the conversions from words from the channel and their corresponding Hamming codewords.
     */
    public void outputCodewordConversions() {

        System.out.println("Encoding Conversions : ");
        for(int i = 0; i < this.interleaveHeight; i++)
            System.out.println(this.words[i] + " => " + this.codewords[i]);
        System.out.println();

    }

    /**
     * Output the codewords that will be transmitted after interleaving.
     */
    public void outputInterleaveResults() {

        System.out.println("Interleaved words to be transmitted : ");
        for(int i = 0; i < this.interleaveHeight; i++) {
            System.out.println(this.interleavedResult.substring(i * this.wordlength, i * this.wordlength + this.wordlength));
        }
        System.out.println("");
    }

}
