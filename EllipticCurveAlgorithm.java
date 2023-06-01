import java.math.BigInteger;

public class EllipticCurve {
    private BigInteger a, b, p, Gx, Gy;

    public EllipticCurve(BigInteger a, BigInteger b, BigInteger p, BigInteger Gx, BigInteger Gy) {
        this.a = a;
        this.b = b;
        this.p = p;
        this.Gx = Gx;
        this.Gy = Gy;
    }

    public BigInteger[] pointAddition(BigInteger[] p1, BigInteger[] p2) {
        BigInteger x1 = p1[0];
        BigInteger y1 = p1[1];
        BigInteger x2 = p2[0];
        BigInteger y2 = p2[1];

        BigInteger lambda, x3, y3;

        if (x1.equals(x2) && y1.equals(y2)) { // Point doubling
            lambda = x1.multiply(x1).multiply(BigInteger.valueOf(3)).add(a)
                    .multiply(y1.multiply(BigInteger.valueOf(2)).modInverse(p));
            x3 = lambda.multiply(lambda).subtract(x1.multiply(BigInteger.valueOf(2))).mod(p);
            y3 = lambda.multiply(x1.subtract(x3)).subtract(y1).mod(p);
        } else { // Point addition
            lambda = y2.subtract(y1).multiply(x2.subtract(x1).modInverse(p));
            x3 = lambda.multiply(lambda).subtract(x1).subtract(x2).mod(p);
            y3 = lambda.multiply(x1.subtract(x3)).subtract(y1).mod(p);
        }

        return new BigInteger[] { x3, y3 };
    }

    public BigInteger[] scalarMultiplication(BigInteger[] p, BigInteger k) {
        BigInteger[] result = new BigInteger[] { BigInteger.ZERO, BigInteger.ZERO };

        while (k.compareTo(BigInteger.ZERO) > 0) {
            if (k.and(BigInteger.ONE).equals(BigInteger.ONE)) {
                result = pointAddition(result, p);
            }
            p = pointAddition(p, p);
            k = k.shiftRight(1);
        }

        return result;
    }

    public BigInteger[] encrypt(BigInteger[] publicKey, String plaintext) {
        BigInteger[] encrypted = new BigInteger[plaintext.length()];

        for (int i = 0; i < plaintext.length(); i++) {
            BigInteger charValue = BigInteger.valueOf(plaintext.charAt(i));
            BigInteger[] sharedPoint = scalarMultiplication(publicKey, charValue);
            encrypted[i] = sharedPoint[0];
        }

        return encrypted;
    }

    public String decrypt(BigInteger privateKey, BigInteger[] ciphertext) {
        StringBuilder decrypted = new StringBuilder();

        BigInteger[] sharedPoint = scalarMultiplication(new BigInteger[] { Gx, Gy }, privateKey.modInverse(p));

        for (BigInteger c : ciphertext) {
            BigInteger charValue = sharedPoint[0].multiply(c).mod(p);
            decrypted.append((char) charValue.intValue());
        }

        return decrypted.toString();
    }

    public static void main(String[] args) {
        BigInteger a = new BigInteger("2");
        BigInteger b = new BigInteger("2");
        BigInteger p = new BigInteger("17");
        BigInteger Gx = new BigInteger("5");
        BigInteger Gy = new BigInteger("1");
        EllipticCurve curve = new EllipticCurve(a, b, p, Gx, Gy);

        BigInteger privateKey = new BigInteger("5");
        BigInteger[] publicKey = curve.scalarMultiplication(new BigInteger[] { Gx, Gy }, privateKey);

        String plaintext = "Hello, world!";

        // Encryption
        BigInteger[] encrypted = curve.encrypt(publicKey, plaintext);
        for (BigInteger c : encrypted) {
            System.out.print(c + " ");
        }
        System.out.println();

        // Decryption
        String decrypted = curve.decrypt(privateKey, encrypted);
        System.out.println(decrypted);
    }
}
