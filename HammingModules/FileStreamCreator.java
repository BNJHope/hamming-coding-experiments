package HammingModules;

import java.io.*;

/**
 * Created by bnjhope on 20/10/16.
 */
public class FileStreamCreator {

    /**
     * The keyword to be added to the file name to show that it is a file that
     * has been huffman encoded.
     */
    private static final String fileEncodeKeyword = "huff-encode";

    /**
     * Creates the output stream and assigns the object's file output stream
     * with the one that was created from this method.
     * @param filename The name of the file to be used in the output stream.
     * @param val The value which the encoder is using for length of word and dimension.
     * @return The output stream for the file passed to the method.
     */
    public FileOutputStream createOutputStream(String fileName, int val) {

        //create the file name for the output encoded file.
        String encodedFileName = this.constructOutputFilename(fileName);

        //create new file
        File outputFile = new File(encodedFileName);

        //The output stream of the given file name
        FileOutputStream fout = null;

        //create new file from file name
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            System.err.println("Error creating new file " + encodedFileName + " : exiting");
            System.exit(0);
        }

        //create file output stream from the new file
        try {
            fout = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            System.err.println("Error creating new file output stream for " + encodedFileName + ": exiting");
            System.exit(0);
        }

        //assign the object's file output stream to the one we just created using the new file names
        return fout;
    }

    /**
     * Creates the input stream and assigns the object's file input stream
     * with the one that was created from this method.
     * @param filename The name of the file to be used in the input stream.
     * @return The input stream for the file given to the function.
     */
    public FileInputStream createInputStream(String filename) {

        //set up an input stream using the given file name
        FileInputStream fin = null;

        //exit the system if the file does not exist
        try {
            fin = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            System.err.println(filename + "does not exist.\nExiting.");
            System.exit(0);
        }

        return fin;
    }


    /**
     * Creates the file name for the encoded file using the file name given to the program.
     * @param pathOfFile The name of the original file.
     * @param val The value which the encoder is using for length of word and dimension.
     * @return The name of the file which this program outputs.
     */
    public String constructOutputFilename(String pathOfFile, int val) {
        //extract the path to the file
        String path = pathOfFile.substring(0, pathOfFile.lastIndexOf("/"));

        //retrieve the raw file name by removing everything before the raw file name
        String filename = pathOfFile.substring(pathOfFile.lastIndexOf("/") + 1);

        //The original file name split into the parts before and after the first "." so that the "huff-encode" file name part
        //can be inserted
        String[] encodedFileNameComps = filename.split("\\.", 2);

        //Return the encoded file name, made up of the file name components from the original file name and the encoded keyword added
        return (path + "/" + encodedFileNameComps[0] + "." + fileEncodeKeyword + val + "." + encodedFileNameComps[1]);

    }

}
