import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class RSA {
    private static BigInteger p, q, n, phi, e, d;
    private static final SecureRandom random = new SecureRandom();
    private static final int BIT_LENGTH = 1024;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to input P & Q? (yes/no)");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("yes")) {
            System.out.print("Enter P: ");
            p = new BigInteger(scanner.nextLine().trim());
            System.out.print("Enter Q: ");
            q = new BigInteger(scanner.nextLine().trim());
        } else {
            p = BigInteger.probablePrime(BIT_LENGTH, random);
            q = BigInteger.probablePrime(BIT_LENGTH, random);
            System.out.println("Generated P: " + p);
            System.out.println("Generated Q: " + q);
        }
        n = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(BIT_LENGTH / 2, random);
        while (!e.gcd(phi).equals(BigInteger.ONE)) {
            e = BigInteger.probablePrime(BIT_LENGTH / 2, random);
        }
        d = e.modInverse(phi);
        System.out.print("Enter plain text: ");
        String plaintext = scanner.nextLine();
        byte[] plainBytes = plaintext.getBytes();
        BigInteger plainBigInt = new BigInteger(plainBytes);
        BigInteger cipherBigInt = encrypt(plainBigInt);
        BigInteger decryptedBigInt = decrypt(cipherBigInt);
        byte[] decryptedBytes = decryptedBigInt.toByteArray();
        String decryptedText = new String(decryptedBytes);
        System.out.println("Cipher Text: " + cipherBigInt);
        System.out.println("Decrypted Text: " + decryptedText);
    }
    public static BigInteger encrypt(BigInteger plaintext) {
        return squareAndMultiply(plaintext, e, n);
    }
    public static BigInteger decrypt(BigInteger ciphertext) {
        // chinese remainder theorem
        BigInteger dp = d.mod(p.subtract(BigInteger.ONE));
        BigInteger dq = d.mod(q.subtract(BigInteger.ONE));
        BigInteger qInv = q.modInverse(p);
        BigInteger m1 = squareAndMultiply(ciphertext, dp, p);
        BigInteger m2 = squareAndMultiply(ciphertext, dq, q);
        BigInteger h = qInv.multiply(m1.subtract(m2)).mod(p);
        if (h.compareTo(BigInteger.ZERO) < 0) {
            h = h.add(p);
        }
        BigInteger m = m2.add(h.multiply(q));
        return m;
    }
    private static BigInteger squareAndMultiply(BigInteger base, BigInteger exponent, BigInteger modulus) {
        BigInteger result = BigInteger.ONE;
        BigInteger b = base.mod(modulus);
        while (exponent.compareTo(BigInteger.ZERO) > 0) {
            if (exponent.and(BigInteger.ONE).equals(BigInteger.ONE)) {
                result = result.multiply(b).mod(modulus);
            }
            exponent = exponent.shiftRight(1);
            b = b.multiply(b).mod(modulus);
        }
        return result;
    }
}
