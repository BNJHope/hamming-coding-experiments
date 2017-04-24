# Hamming Coding Experiments
Implementation of Hamming encdoing and decoding, with a model of burst errors in transmission and block interleaving either side of transmission. This was the second practical of the data encoding module in 3rd year. The aim was to test the effectiveness of the error detection and correction capabilities of the Hamming coding system, with variances on the burst error model and the properties of the encoding.

Build
----------
The project comes with a build.xml file to be used with the Apache Ant tool. To build the Java archive (.jar) of this project to be built, run
```sh
$ ant jar
```
the .jar file will be built into the dist directory.

Run
----------
To run the project, values need to be decided for :
* r- the number of data bits in the coding.
* Probability of an error occuring while the burst model is in the bad state of the burst error model.
* The probability of being able to move to a bad state while the burst error model is in a good state.
* The probability of being able to move to a good state while the burst error model is in a bad state.
* The height of the interleaving table.

When the program is run with these values, it outputs the results from simulating a transmission under these conditions. For example, if we set :
* r to 3
* Probability of error to 0.3
* Probability of good to bad to 0.3
* Probability of bad to good to 0.3
* Height of interleaving table to 3
We can then run the program using the command
```sh
$ java -jar dist/HammingCoding.jar -o 3 0.3 0.3 0.3 3
```
with the "-o" flag signifying to output the results. This will also output the transmission success rate at bit level.
