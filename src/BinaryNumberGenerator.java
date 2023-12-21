import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BinaryNumberGenerator {
    public static String[] generateUniqueBinaryNumbers(int count) {
        if (count > 256) {
            throw new IllegalArgumentException("Count cannot exceed 256 for 8-bit binary numbers");
        }

        Set<String> set = new HashSet<>();
        Random random = new Random();
        String[] result = new String[count];
        int index = 0;

        while (set.size() < count) {
            int randomNumber = random.nextInt(256); // Generate random number between 0 and 255
            String binaryString = Integer.toBinaryString(randomNumber);
            if (binaryString.length() < 8) {
                int padding = 8 - binaryString.length();
                binaryString = "0".repeat(padding) + binaryString;
            }
            if (!set.contains(binaryString)) {
                set.add(binaryString);
                result[index] = binaryString;
                index++;
            }
        }

        return result;
    }
}