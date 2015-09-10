package org.openspcoop2.utils;

import java.util.Random;

public class RandomString {

	private static final char[] symbols;

	static {
		StringBuilder tmp = new StringBuilder();
		for (char ch = '0'; ch <= '9'; ++ch)
			tmp.append(ch);
		for (char ch = 'a'; ch <= 'z'; ++ch)
			tmp.append(ch);
		for (char ch = 'A'; ch <= 'Z'; ++ch)
			tmp.append(ch);
		tmp.append("@");
		tmp.append(".");
		symbols = tmp.toString().toCharArray();
	}   

	private final Random random = new Random();

	private final char[] buf;

	public RandomString(int length) {
		if (length < 1)
			throw new IllegalArgumentException("length < 1: " + length);
		this.buf = new char[length];
	}

	public String nextString() {
		for (int idx = 0; idx < this.buf.length; ++idx) 
			this.buf[idx] = symbols[this.random.nextInt(symbols.length)];
		return new String(this.buf);
	}
}