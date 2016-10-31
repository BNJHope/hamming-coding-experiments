package HammingModules;

/**
 * Created by bnjhope on 20/10/16.
 */
public class HammingMain {

    public static void main(String[] args){

        String fileName = args[0];
        int val = Integer.parseInt(args[1]), interleaveHeight = Integer.parseInt(args[5]);
        double pOfError = Double.parseDouble(args[2]), pOfGoodToBad = Double.parseDouble(args[3]), pOfBadToGood = Double.parseDouble(args[4]);
        HammingEncoder encoder = new HammingEncoder();
        encoder.encode(fileName, val, pOfError, pOfGoodToBad, pOfBadToGood, interleaveHeight);
        HammingDecoder decoder = new HammingDecoder();
        decoder.decode("/cs/home/bh59/Documents/CS3000/DE/DEP2-error-correction/test_items/loremipsum/loremipsum.huff-encode3-5.txt");
    }
}
