import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Find key of Viginere code (key is 8 characters long)
 *
 */
public class VigenereSifra{

    private static String coded = "";
    private static Map<Character, Double> frequencyEnglishLetter = new HashMap<Character, Double>();


    /**
     * Main function
     * @param args
     */
    public static void main(String[] args)
    {
        if(args.length == 0)
        {
            System.out.println("Provide argument with filename error!");
            return;
        }

        // Read encrypted (coded) message with Viginere code
        try {
            coded = Files.readString(Paths.get(args[0]));
        } catch (IOException e) {
            System.out.println("File not found error!: " + args[0]);
            return;
        }
        // Initialize English alphabet
        initEnglishAlphabet();
        // make it upper case
        coded = coded.toUpperCase();

        System.out.println("Coded: \n" + coded);

        int letterCount = coded.length();
        char[] encMsg = new char [letterCount] ;
        char[] viginereKey =  new char [8];

        coded.getChars(0, letterCount, encMsg, 0);
        int values[] = new int[letterCount];
        int count = 0;


        for (int i = 0; i < letterCount; i++)
        {
            values[count++] = encMsg[i] - 'A';
        }

        // find key
        viginereKey = findKey(values, count);

        // Convert to string
        String keyAsString = new String(viginereKey);
        System.out.println();
        System.out.println("Key: " + keyAsString);
        System.out.println();
        System.out.println("Original Message:");
        System.out.println(decode(coded, keyAsString));

    }

    private static void initEnglishAlphabet()
    {
        // Initialize English alphabet of 26 letters
        frequencyEnglishLetter.put('A', 0.08167);
        frequencyEnglishLetter.put('B', 0.01492);
        frequencyEnglishLetter.put('C', 0.02782);
        frequencyEnglishLetter.put('D', 0.04253);
        frequencyEnglishLetter.put('E', 0.12702);
        frequencyEnglishLetter.put('F', 0.02228);
        frequencyEnglishLetter.put('G', 0.02015);
        frequencyEnglishLetter.put('H', 0.06094);
        frequencyEnglishLetter.put('I', 0.06966);
        frequencyEnglishLetter.put('J', 0.00153);
        frequencyEnglishLetter.put('K', 0.00772);
        frequencyEnglishLetter.put('L', 0.04025);
        frequencyEnglishLetter.put('M', 0.02406);
        frequencyEnglishLetter.put('N', 0.06749);
        frequencyEnglishLetter.put('O', 0.07507);
        frequencyEnglishLetter.put('P', 0.01929);
        frequencyEnglishLetter.put('Q', 0.00095);
        frequencyEnglishLetter.put('R', 0.05987);
        frequencyEnglishLetter.put('S', 0.06327);
        frequencyEnglishLetter.put('T', 0.09056);
        frequencyEnglishLetter.put('U', 0.02758);
        frequencyEnglishLetter.put('V', 0.00978);
        frequencyEnglishLetter.put('W', 0.02360);
        frequencyEnglishLetter.put('X', 0.00150);
        frequencyEnglishLetter.put('Y', 0.01974);
        frequencyEnglishLetter.put('Z', 0.00074);
    }

    /**
     * Decode message
     * @param coded
     * @param myKey
     * @return
     */
    private static String decode(String coded, String myKey) {
        StringBuilder decoded = new StringBuilder(coded.length());
        int charposition = 0;
        for (char c : coded.toCharArray())
        {
            char theChar = myKey.charAt(charposition);
            int intValue = c - theChar;

            if(intValue < 0)
            {
                intValue += 26;
            }
            char myChar = (char) (intValue + 'A');
            decoded.append(myChar);

            charposition = (charposition + 1) % 8;
        }
        return decoded.toString();
    }

    /**
     * Shift
     * @param values
     * @param frequencies
     * @return
     */
    static int shift(double []values, Map<Character, Double> frequencies) {
        double s = 0;
        double difference, distance;
        double best = 100000000;
        int curr;
        int shiftToStore = 0;
        for (int i = 0; i < 26; i++)
        {
            s += values[i];
        }

        for (curr = 0; curr < 26; curr++)
        {
            difference = 0;
            for (int i = 0; i < 26; i++)
            {
                char c = (char)('A' + i);
                distance = values[(i + curr) % 26] / s - frequencies.get(c);
                difference += distance * distance / frequencies.get(c);
            }
            if (difference < best)
            {
                best = difference;
                shiftToStore = curr;
            }
        }
        return shiftToStore;
    }

    /**
     * Find key
     * @param values
     * @param letterCount
     * @return
     */
    static char[] findKey(int []values, int letterCount) {
        char[] key = new char[8];
        double s;
        double distance;
        double difference;
        double[] x = new double[26];
        double[] res = new double[26];
        int rotation;

        for (int iter = 0; iter < 8; iter++) {

            for (int i = 0; i < 26; i++) {
                res[i] = 0;
            }
            for (int j = iter; j < letterCount; j += 8) {
                res[values[j]]++;
            }
            // shift
            rotation = shift(res, frequencyEnglishLetter);
            try {
                key[iter] = (char)(rotation + 'A');
            }
            catch (Exception e) {
                System.out.print(e.getMessage());
            }

            for (int i = 0; i < 26; i++)
            {
                x[i] += res[(i + rotation) % 26];
            }
        }
        s = 0;
        for (int i = 0; i < 26; i++) {
            s += x[i];
        }

        difference = 0;
        for (int i = 0; i < 26; i++)
        {
            char c = (char)('A' + i);
            distance = x[i] / s - frequencyEnglishLetter.get(c);
            difference += distance * distance / frequencyEnglishLetter.get(c);
        }

        return key;
    }

}