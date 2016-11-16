package source.HammingModules;

/**
 * Created by bnjhope on 20/10/16.
 */
public class HammingMain {

    public static void main(String[] args){

        final String testFlag = "-t", outputFlag = "-o";
        int val = Integer.parseInt(args[1]), interleaveHeight = Integer.parseInt(args[5]);
        double pOfError = Double.parseDouble(args[2]), pOfGoodToBad = Double.parseDouble(args[3]), pOfBadToGood = Double.parseDouble(args[4]);
        HammingManager hm = new HammingManager();

        String instructFlag = args[0];
        if(instructFlag.equals(testFlag)) {
            runTests();
        } else if(instructFlag.equals(outputFlag)){
            hm.doHammingProcess(val, pOfError, pOfGoodToBad, pOfBadToGood, interleaveHeight, true);
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

      for(int r = rStart; r <= rEnd; r++) {
          for (int interleaveHeight = interleaveStart; interleaveHeight <= interleaveEnd; interleaveHeight++) {
              for (double pOfError = pOfErrorStart; pOfError <= pOfErrorEnd; pOfError += probabilityIncrement) {
                  for (double pOfGoodToBad = pOfGoodToBadStart; pOfGoodToBad <= pOfGoodToBadEnd; pOfGoodToBad += probabilityIncrement) {
                      for (double pOfBadToGood = pOfBadToGoodStart; pOfBadToGood <= pOfBadToGoodEnd; pOfBadToGood += probabilityIncrement) {
                          hm.doHammingProcess(r, pOfError, pOfGoodToBad, pOfBadToGood, interleaveHeight, false);
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

}
