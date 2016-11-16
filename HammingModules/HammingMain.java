package HammingModules;

/**
 * Created by bnjhope on 20/10/16.
 */
public class HammingMain {

    public static void main(String[] args){

        int val = Integer.parseInt(args[0]), interleaveHeight = Integer.parseInt(args[4]);
        double pOfError = Double.parseDouble(args[1]), pOfGoodToBad = Double.parseDouble(args[2]), pOfBadToGood = Double.parseDouble(args[3]);
        HammingManager hm = new HammingManager();
        hm.doHammingProcess(val, pOfError, pOfGoodToBad, pOfBadToGood, interleaveHeight);
  }
}
