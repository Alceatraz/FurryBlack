package common;

import org.junit.jupiter.api.Test;

public class CommonTest {

	@Test
	void test() {

		// JDK 7 HashMap Anti Collisions

		int i = 0x8FFFFFFF;

		int a = i >>> 20;
		int b = i >>> 12;

		int c = a ^ b;

		int d = i ^ c;

		int e = d >>> 7;
		int f = d >>> 4;

		int g = e ^ f;

		int h = d ^ g;

		System.out.println(Integer.toBinaryString(i));

		System.out.println(Integer.toBinaryString(a));
		System.out.println(Integer.toBinaryString(b));
		System.out.println(Integer.toBinaryString(c));
		System.out.println(Integer.toBinaryString(d));
		System.out.println(Integer.toBinaryString(e));
		System.out.println(Integer.toBinaryString(f));
		System.out.println(Integer.toBinaryString(g));
		System.out.println(Integer.toBinaryString(h));

		/**
		 * i
		 * 
		 * 10001111111111111111111111111111
		 * 
		 * a = i >>> 20 b = i >>> 12
		 * 
		 * c = a XOR b
		 * 
		 * 00000000000000000000100011111111
		 * 
		 * 00000000000010001111111111111111
		 * 
		 * 00000000000010001111011100000000
		 * 
		 * d = i XOR c
		 * 
		 * 10001111111101110000100011111111
		 * 
		 * e = d >>> 7 f = d >>> 4
		 * 
		 * g = e XOR f
		 * 
		 * 00000001000111111110111000010001
		 * 
		 * 00001000111111110111000010001111
		 * 
		 * 00001001111000001001111010011110
		 * 
		 * h = d XOR g
		 * 
		 * 10000110000101111001011001100001
		 * 
		 */

	}

}
