package HammingModules;

/**
 * Created by bnjhope on 20/10/16.
 */
public class HammingMain {

    public static void main(String[] args){

        String fileName = args[0];
        int val = Integer.parseInt(args[1]);
        HammingEncoder encoder = new HammingEncoder();
        encoder.encode(fileName, val);
    }
}
