package HammingCoding.HammingModules;

/**
 * Created by bh59 on 16/11/16.
 */
public class DecodingResult {

    /**
     * Length of the code words.
     */
    private int wordlength;

    /**
     * The height of the interleaving table.
     */
    private int interleaveHeight;

    /**
     * The resulting words from after error correction and decoding occur, combined into one word.
     */
    private String resultString;

    /**
     * The words that result from correcting any errors in the codewords and decoding them
     * back into their original words.
     */
    private String[] words;

    /**
     * The Hamming codewords that came from undoing the interleaving process.
     */
    private String[] postInterleaveCodewords;

    /**
     * The codewords that result from correcting the errors in the codewords given after the interleaving process.
     */
    private String[] errorCorrectedCodeWords;

    /**
     * The stream of bits that has been received from the channel.
     */
    private String receivedData;

    public DecodingResult(int wordlength, int interleaveHeight, String resultString, String[] words, String[] postInterleaveCodewords, String[] errorCorrectedCodeWords, String receivedData) {
        this.wordlength = wordlength;
        this.interleaveHeight = interleaveHeight;
        this.resultString = resultString;
        this.words = words;
        this.postInterleaveCodewords = postInterleaveCodewords;
        this.errorCorrectedCodeWords = errorCorrectedCodeWords;
        this.receivedData = receivedData;
    }

    public String getResultString() {
        return resultString;
    }

    /**
     * Output the conversions from words from the channel and their corresponding Hamming codewords.
     */
    public void outputCodewordConversions() {

        System.out.println("Decoding Conversions : ");
        for(int i = 0; i < this.interleaveHeight; i++)
            System.out.println(this.postInterleaveCodewords[i] + " => " + this.errorCorrectedCodeWords[i] + " => " + this.words[i]);
        System.out.println();

    }

    /**
     * Output the codewords that were intitially received from the channel.
     */
    public void outputReceivedWords() {

        System.out.println("Words received from channel : ");
        for(int i = 0; i < this.interleaveHeight; i++) {
            System.out.println(this.receivedData.substring(i * this.wordlength, i * this.wordlength + this.wordlength));
        }
        System.out.println();

    }

}
