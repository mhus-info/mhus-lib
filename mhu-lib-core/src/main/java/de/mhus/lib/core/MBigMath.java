package de.mhus.lib.core;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class MBigMath {

    public final static BigInteger TWO = BigInteger.valueOf(2);
	private static final BigDecimal SQRT_DIG = new BigDecimal(150);
	private static final BigDecimal SQRT_PRE = new BigDecimal(10).pow(SQRT_DIG.intValue());

    /**
     * Calculate a pow with modulo using the binary algorithm.
     * @param base
     * @param pow
     * @param mod
     * @return
     * @throws IOException
     */
	public static BigInteger binaryPow(BigInteger base, BigInteger pow, BigInteger mod) throws IOException {
		
		if ( mod.subtract(BigInteger.ONE).pow(2).compareTo(base) > 1 ) {
			throw new IOException("modulo is to big");
		}
		BigInteger res = BigInteger.ONE;
		base = base.mod(mod);
		
		while (pow.compareTo(BigInteger.ZERO) == 1 ) {
			if (pow.mod(TWO).equals(BigInteger.ONE))
				res = res.multiply(base).mod(mod);
			pow = pow.shiftRight(1);
			base = base.multiply(base).mod(mod);
		}
		return res;
	}

	/**
	 * Calculate the pow with modulo using divide algorithm.
	 * Using the binaryPow is the fastest!
	 * 
	 * @param base
	 * @param pow
	 * @param mod
	 * @return
	 */
    public static BigInteger dividePow( BigInteger base, BigInteger pow, BigInteger mod) {
    	return dividePow(base, pow, 1).mod(mod);
    }
    
    private static BigInteger dividePow( BigInteger base, BigInteger pow, long level ) {
    	boolean odd = false;
    	if (pow.mod(TWO).equals(BigInteger.ONE)) {
    		// odd
    		pow = pow.subtract(BigInteger.ONE);
    		odd = true;
    	}
    	
    	BigInteger half = pow.divide(TWO);
    	
    	BigInteger res = null;
    	if (half.equals(BigInteger.ONE)) {
    		res = base.multiply(base);
    	} else {
    		res = dividePow(base, half, level+1);
    		res = res.multiply(res);
    	}
    	if (odd)
    		res = res.multiply(base);
    	
    	return res;
    }
    
    /**
     * Calculate the pow with modulo using the simple mathematics straight forward method. Do not
     * use this method! Use the binaryPow for good performance.
     * 
     * @param base
     * @param pow
     * @param mod
     * @return
     */
    public static BigInteger straightPow(BigInteger base, BigInteger pow, BigInteger mod) {
    	BigInteger res = base;
	    for (BigInteger bi = BigInteger.ONE; bi.compareTo( pow ) == -1; bi = bi.add(BigInteger.ONE)) {
	    	res = res.multiply(base);
        }
	    return res.mod(mod);
    }

    public static BigInteger splitPow(BigInteger base, BigInteger pow, BigInteger mod) {
    	BigInteger res = base;
	    for (BigInteger bi = BigInteger.ONE; bi.compareTo( pow ) == -1; bi = bi.add(BigInteger.ONE)) {
	    	res = res.multiply(base).mod(mod);
        }
	    return res.mod(mod);
    }

	/**
	 * Calculate a log10
	 * @param b
	 * @param dp
	 * @return
	 */
	//http://everything2.com/index.pl?node_id=946812       
	public static BigDecimal log10(BigDecimal b, int dp)
	{
		final int NUM_OF_DIGITS = dp+2; // need to add one to get the right number of dp
		                                //  and then add one again to get the next number
		                                //  so I can round it correctly.

		MathContext mc = new MathContext(NUM_OF_DIGITS, RoundingMode.HALF_EVEN);

		//special conditions:
		// log(-x) -> exception
		// log(1) == 0 exactly;
		// log of a number lessthan one = -log(1/x)
		if(b.signum() <= 0)
			throw new ArithmeticException("log of a negative number! (or zero)");
		else if(b.compareTo(BigDecimal.ONE) == 0)
			return BigDecimal.ZERO;
		else if(b.compareTo(BigDecimal.ONE) < 0)
			return (log10((BigDecimal.ONE).divide(b,mc),dp)).negate();

		StringBuffer sb = new StringBuffer();
		//number of digits on the left of the decimal point
		int leftDigits = b.precision() - b.scale();

		//so, the first digits of the log10 are:
		sb.append(leftDigits - 1).append(".");

		//this is the algorithm outlined in the webpage
		int n = 0;
		while(n < NUM_OF_DIGITS)
		{
			b = (b.movePointLeft(leftDigits - 1)).pow(10, mc);
			leftDigits = b.precision() - b.scale();
			sb.append(leftDigits - 1);
			n++;
		}

		BigDecimal ans = new BigDecimal(sb.toString());

		//Round the number to the correct number of decimal places.
		ans = ans.round(new MathContext(ans.precision() - ans.scale() + dp, RoundingMode.HALF_EVEN));
		return ans;
	}
		
	/**
	 * Compute D from E using baghdad algorithm.
	 * 
	 * @param e
	 * @param z
	 * @return
	 */
	public static BigInteger computeDfromE(BigInteger e, BigInteger z) {
		BigDecimal E = new BigDecimal(e);
		BigDecimal Z = new BigDecimal(z);
		BigDecimal D = new BigDecimal(1);
		BigDecimal T = null;
		do {
			D = D.add(Z);
			T = D.divide(E, 100, BigDecimal.ROUND_UP).stripTrailingZeros();
		} while ( T.scale() > 0);
		return T.toBigInteger();
	}

	public static BigDecimal sqrt(BigDecimal x, int scale)
    {
        // Check that x >= 0.
        if (x.signum() < 0) {
            throw new IllegalArgumentException("x < 0");
        }
 
        // n = x*(10^(2*scale))
        BigInteger n = x.movePointRight(scale << 1).toBigInteger();
 
        // The first approximation is the upper half of n.
        int bits = (n.bitLength() + 1) >> 1;
        BigInteger ix = n.shiftRight(bits);
        BigInteger ixPrev;
 
        // Loop until the approximations converge
        // (two successive approximations are equal after rounding).
        do {
            ixPrev = ix;
 
            // x = (x + n/x)/2
            ix = ix.add(n.divide(ix)).shiftRight(1);
 
            Thread.yield();
        } while (ix.compareTo(ixPrev) != 0);
 
        return new BigDecimal(ix, scale);
    }	

	/**
	 * Private utility method used to compute the square root of a BigDecimal.
	 * 
	 * @author Luciano Culacciatti 
	 * @url http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
	 */
	private static BigDecimal sqrtNewtonRaphson  (BigDecimal c, BigDecimal xn, BigDecimal precision){
	    BigDecimal fx = xn.pow(2).add(c.negate());
	    BigDecimal fpx = xn.multiply(new BigDecimal(2));
	    BigDecimal xn1 = fx.divide(fpx,2*SQRT_DIG.intValue(),BigDecimal.ROUND_DOWN);
	    xn1 = xn.add(xn1.negate());
	    BigDecimal currentSquare = xn1.pow(2);
	    BigDecimal currentPrecision = currentSquare.subtract(c);
	    currentPrecision = currentPrecision.abs();
	    if (currentPrecision.compareTo(precision) <= -1){
	        return xn1;
	    }
	    return sqrtNewtonRaphson(c, xn1, precision);
	}

	/**
	 * Uses Newton Raphson to compute the square root of a BigDecimal.
	 * 
	 * @author Luciano Culacciatti 
	 * @url http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
	 */
	public static BigDecimal bigSqrt(BigDecimal c){
	    return sqrtNewtonRaphson(c,new BigDecimal(1),new BigDecimal(1).divide(SQRT_PRE));
	}	

}