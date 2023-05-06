import java.math.BigInteger;
import java.util.Random;

public class EllipticCurve {
    private static final BigInteger P = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
    private static final BigInteger A = BigInteger.ZERO;
    private static final BigInteger B = BigInteger.valueOf(7L);
    private static final BigInteger GX = new BigInteger("55066263022277343669578718895168534326250603453777594175500187360389116729240");
    private static final BigInteger GY = new BigInteger("32670510020758816978083085130507043184471273380659243275938904335757337482424");

    private static final Random rand = new Random();

    public static class Point {
        public BigInteger x, y;

        public Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }

        public boolean isInfinity() {
            return x == null && y == null;
        }

        public Point negate() {
            return new Point(x, y.negate().mod(P));
        }

        public Point add(Point other) {
            if (this.isInfinity()) {
                return other;
            }
            if (other.isInfinity()) {
                return this;
            }
            if (this.x.equals(other.x) && this.y.equals(other.y.negate().mod(P))) {
                return new Point(null, null); // Infinity
            }
            if (!this.x.equals(other.x)) {
                BigInteger slope = this.y.subtract(other.y).multiply(this.x.subtract(other.x).modInverse(P)).mod(P);
                BigInteger x3 = slope.multiply(slope).subtract(this.x).subtract(other.x).mod(P);
                BigInteger y3 = slope.multiply(this.x.subtract(x3)).subtract(this.y).mod(P);
                return new Point(x3, y3);
            } else {
                BigInteger slope = this.x.pow(2).multiply(BigInteger.valueOf(3)).add(A).multiply(this.y.multiply(BigInteger.valueOf(2)).modInverse(P)).mod(P);
                BigInteger x3 = slope.multiply(slope).subtract(this.x.multiply(BigInteger.valueOf(2))).mod(P);
                BigInteger y3 = slope.multiply(this.x.subtract(x3)).subtract(this.y).mod(P);
                return new Point(x3, y3);
            }
        }

        public Point multiply(BigInteger n) {
            if (n.equals(BigInteger.ZERO)) {
                return new Point(null, null); // Infinity
            }
            Point p = this;
            Point r = new Point(null, null); // Infinity
            while (n.compareTo(BigInteger.ZERO) > 0) {
                if (n.mod(BigInteger.valueOf(2)).equals(BigInteger.ONE)) {
                    r = r.add(p);
                }
                p = p.add(p);
                n = n.shiftRight(1);
            }
            return r;
        }

        @Override
        public String toString() {
            return "(" + x.toString(16) + ", " + y.toString(16) + ")";
        }
    }
    public static void main(String[] args) {
     Point G = new Point(GX, GY);
     BigInteger d = new BigInteger(256, rand);
     Point Q = G.multiply(d);
     System.out.println("Private key: " + d.toString(16));
     System.out.println("Public key: " + Q.toString());
        
    String message = "I sense amogus";
    byte[] bytes = message.getBytes();
    BigInteger k = new BigInteger(256, rand);
    Point P = G.multiply(k);
    BigInteger x = P.x.mod(P);
    }
}










