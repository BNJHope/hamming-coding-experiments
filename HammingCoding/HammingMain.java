package HammingCoding;

import HammingCoding.HammingModules.HammingManager;

/**
 * Created by bnjhope on 20/10/16.
 */
public class HammingMain {

    public static void main(String[] args) {

        final String testFlag = "-t", outputFlag = "-o", graphFlag = "-g";
        int val, interleaveHeight;
        double pOfError, pOfGoodToBad, pOfBadToGood;
        HammingManager hm = new HammingManager();

        String instructFlag = args[0];
        if (instructFlag.equals(testFlag)) {
            runTests();
        } else if (instructFlag.equals(outputFlag)) {

            val = Integer.parseInt(args[1]);
            pOfError = Double.parseDouble(args[2]);
            pOfGoodToBad = Double.parseDouble(args[3]);
            pOfBadToGood = Double.parseDouble(args[4]);
            interleaveHeight = Integer.parseInt(args[5]);

            hm.doHammingProcess(val, pOfError, pOfGoodToBad, pOfBadToGood, interleaveHeight, "-o", 500);
        } else if (instructFlag.equals(graphFlag)) {
            generateGraphData();
        } else {
            System.out.println("Invalid type flag.");
        }

    }

    public static void runTests() {
        HammingManager hm = new HammingManager();

        //the start and end values for the r value of a Hamming process
        final int rStart = 3, rEnd = 8;

        //the start and end values for the interleaving height of a Hamming process
        final int interleaveStart = 1, interleaveEnd = 10;

        //the start and end values for the probability of an error whilst in a bad state in the Hamming process
        final double pOfErrorStart = 0.1, pOfErrorEnd = 0.9;

        //the start and end values for the probability of transitioning to a bad state whilst in a good state
        final double pOfGoodToBadStart = 0.1, pOfGoodToBadEnd = 0.9;

        //the start and end values for the probability of transitioning to a good state whilst in a bad state
        final double pOfBadToGoodStart = 0.1, pOfBadToGoodEnd = 0.9;

        //the increment value for the probabilities
        final double probabilityIncrement = 0.1;

        for (int r = rStart; r <= rEnd; r++) {
            for (int interleaveHeight = interleaveStart; interleaveHeight <= interleaveEnd; interleaveHeight++) {
                for (double pOfError = pOfErrorStart; pOfError <= pOfErrorEnd; pOfError += probabilityIncrement) {
                    for (double pOfGoodToBad = pOfGoodToBadStart; pOfGoodToBad <= pOfGoodToBadEnd; pOfGoodToBad += probabilityIncrement) {
                        for (double pOfBadToGood = pOfBadToGoodStart; pOfBadToGood <= pOfBadToGoodEnd; pOfBadToGood += probabilityIncrement) {
                            hm.doHammingProcess(r, pOfError, pOfGoodToBad, pOfBadToGood, interleaveHeight, "-t", 1000);
                        }
                        System.out.println();
                    }
                    System.out.println();
                }
                System.out.println();
            }
            System.out.println();
        }

    }

    /**
     * Used to output coordinates needed for graph and table data.
     */
    public static void generateGraphData() {
        HammingManager hm = new HammingManager();

        //fixed values used when they are not being varied.
        final int rFixed = 2, interleaveHeightFixed = 4;
        final double pErrorFixed = 0.6, pGoodToBadFixed = 0.8, pBadToGoodFixed = 0.3;

        //get data results when we vary the value of r
        for (int r = 3; r <= 8; r++) {
            hm.doHammingProcess(r, pErrorFixed, pGoodToBadFixed, pBadToGoodFixed, interleaveHeightFixed, "-r", 500);
        }

        System.out.println("\n\n");

        //get data results when we vary the probability of an error occuring in a bad state
        for (double pError = 0.1; pError <= 0.9; pError += 0.1) {
            hm.doHammingProcess(rFixed, pError, pGoodToBadFixed, pBadToGoodFixed, interleaveHeightFixed, "-pe", 500);
        }

        System.out.println("\n\n");

        //get data results when we vary the probability of changing from a good to a bad state
        for (double pGoodToBad = 0.1; pGoodToBad <= 0.9; pGoodToBad += 0.1) {
            hm.doHammingProcess(rFixed, pErrorFixed, pGoodToBad, pBadToGoodFixed, interleaveHeightFixed, "-pgb", 500);
        }

        System.out.println("\n\n");

        //get data results when we vary the probability of changing from a bad to a good state
        for (double pBadToGood = 0.1; pBadToGood <= 0.9; pBadToGood += 0.1) {
            hm.doHammingProcess(rFixed, pErrorFixed, pGoodToBadFixed, pBadToGood, interleaveHeightFixed, "-pbg", 500);
        }

        System.out.println("\n\n");

        //get data results when we vary the interleaving height
        for (int interleaveHeight = 1; interleaveHeight <= 20; interleaveHeight++) {
            hm.doHammingProcess(rFixed, pErrorFixed, pGoodToBadFixed, pBadToGoodFixed, interleaveHeight, "-ih", 500);
        }
    }
}
