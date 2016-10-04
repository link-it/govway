/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.utils;

import java.util.Random;

/**
 *  RandomString
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
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