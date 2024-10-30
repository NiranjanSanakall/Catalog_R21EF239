import java.io.FileReader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ShamirSecretSharing {
    
    // Function to convert a value from a given base to decimal
    public static BigInteger convertBase(String value, int base) {
        return new BigInteger(value, base);
    }

    // Function to read JSON and decode the input points
    public static Map<BigInteger, BigInteger> decodePoints(String filepath) {
        Map<BigInteger, BigInteger> points = new HashMap<>();
        
        try (FileReader reader = new FileReader(filepath)) {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
            JSONObject keys = (JSONObject) jsonObject.get("keys");
    
            if (keys == null) {
                System.out.println("Error: 'keys' not found in JSON.");
                return points;
            }
    
            Long n = (Long) keys.get("n");
            Long k = (Long) keys.get("k");
    
            if (n == null || k == null) {
                System.out.println("Error: 'n' or 'k' is missing in 'keys'.");
                return points;
            }
    
            // Iterating over the points, assuming the keys are numbered sequentially
            for (int i = 1; i <= n; i++) {
                JSONObject point = (JSONObject) jsonObject.get(String.valueOf(i));
                
                if (point == null) {
                    System.out.println("Error: Point " + i + " is missing.");
                    continue;
                }
    
                String baseStr = (String) point.get("base");
                String valueStr = (String) point.get("value");
    
                if (baseStr == null || valueStr == null) {
                    System.out.println("Error: Base or Value is missing for point " + i);
                    continue;
                }
    
                int base = Integer.parseInt(baseStr);
                BigInteger x = BigInteger.valueOf(i);
                BigInteger y = convertBase(valueStr, base);
    
                points.put(x, y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return points;
    }
    

    // Function to compute Lagrange interpolation at x = 0 (constant term of the polynomial)
    public static BigInteger lagrangeInterpolation(Map<BigInteger, BigInteger> points, int k) {
        BigInteger secret = BigInteger.ZERO;
        
        for (Map.Entry<BigInteger, BigInteger> entry : points.entrySet()) {
            BigInteger xi = entry.getKey();
            BigInteger yi = entry.getValue();

            BigInteger term = yi;
            for (Map.Entry<BigInteger, BigInteger> other : points.entrySet()) {
                BigInteger xj = other.getKey();
                if (!xi.equals(xj)) {
                    term = term.multiply(xj).divide(xj.subtract(xi));
                }
            }
            secret = secret.add(term);
        }
        return secret.mod(BigInteger.valueOf(2).pow(256));  // Constraint: Within a 256-bit number
    }

    public static void main(String[] args) {
        // Load points from the JSON file
        String filepath1 = "C:\\Users\\Niranjan sanakall\\OneDrive\\Desktop\\ShamirSecreatSharing\\file1.json";
        String filepath2 = "C:\\Users\\Niranjan sanakall\\OneDrive\\Desktop\\ShamirSecreatSharing\\file2.json";
        
        Map<BigInteger, BigInteger> points1 = decodePoints(filepath1);
        Map<BigInteger, BigInteger> points2 = decodePoints(filepath2);

        // Retrieve the value of k from one of the test cases for interpolation
        int k1 = 3;  // Example for Test Case 1; adjust based on input
        int k2 = 7;  // Example for Test Case 2; adjust based on input

        // Find the secrets for both test cases
        BigInteger secret1 = lagrangeInterpolation(points1, k1);
        BigInteger secret2 = lagrangeInterpolation(points2, k2);

        System.out.println("Secret for Test Case 1: " + secret1);
        System.out.println("Secret for Test Case 2: " + secret2);
    }
}
