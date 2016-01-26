/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Modificato per supportare le seguenti funzionalita':
 * - Generazione ID all'interno delle interfacce di OpenSPCoop2
 * - Gestione caratteri massimi per numeri e cifre
 * - Possibilita' di utilizzare lowerCase e/o upperCase
 * 
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.utils.id.apache.serial;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.id.apache.AbstractStringIdentifierGenerator;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

import java.io.Serializable;

/**
 * <code>AlphanumericGenerator</code> is an identifier generator
 * that generates an incrementing number in base 36 as a String
 * object.
 *
 * <p>All generated ids have the same length (padding with 0's on the left),
 * which is determined by the <code>size</code> parameter passed to the constructor.<p>
 *
 * <p>The <code>wrap</code> property determines whether or not the sequence wraps
 * when it reaches the largest value that can be represented in <code>size</code>
 * base 36 digits. If <code>wrap</code> is false and the the maximum representable
 * value is exceeded, an IllegalStateException is thrown</p>
 *
 * @author Commons-Id team
 * @version $Id: AlphanumericGenerator.java 480488 2006-11-29 08:57:26Z bayard $
 */
/**
 * OpenSPCoop2
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlphanumericGenerator extends AbstractStringIdentifierGenerator implements Serializable {

	/**
	 * <code>serialVersionUID</code> is the serializable UID for the binary version of the class.
	 */
	private static final long serialVersionUID = 20060120L;

	/**
	 * Should the counter wrap.
	 */
	private boolean wrapping = true;

	/**
	 * The counter.
	 */
	private char[] count = null;


	private char START_CHAR = 'a';
	public void setStartChar(char startChar) throws UtilsException{
		try{
			if(!RegularExpressionEngine.isMatch((startChar+""),"^[a-z]$")){
				throw new UtilsException("Deve essere fornito una lettera [a-z]");
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		if(startChar>this.END_CHAR){
			throw new UtilsException("Deve essere fornito una lettera di posizione minore dell'attuale carattere di fine: ["+this.END_CHAR+"]");
		}
		this.START_CHAR = startChar;
	}
	
	private char END_CHAR = 'z';
	public void setEndChar(char endChar) throws UtilsException{
		try{
			if(!RegularExpressionEngine.isMatch((endChar+""),"^[a-z]$")){
				throw new UtilsException("Deve essere fornito una lettera [a-z]");
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		if(endChar<this.START_CHAR){
			throw new UtilsException("Deve essere fornito una lettera di posizione maggiore dell'attuale carattere di inizio: ["+this.START_CHAR+"]");
		}
		this.END_CHAR = endChar;
	}

	private boolean upperChar = true;
	public void setUpperChar(boolean upperChar) throws UtilsException {
		//System.out.println("SET UPPER ["+upperChar+"]");
		if(!upperChar && !this.lowerChar){
			throw new UtilsException("Almeno una combinazione tra maiuscolo e minuscolo deve essere permessa");
		}
		this.upperChar = upperChar;
	}

	private boolean lowerChar = true;
	public void setLowerChar(boolean lowerChar) throws UtilsException {
		//System.out.println("SET LOWER ["+lowerChar+"]");
		if(!lowerChar && !this.upperChar){
			throw new UtilsException("Almeno una combinazione tra maiuscolo e minuscolo deve essere permessa");
		}
		this.lowerChar = lowerChar;
	}

	
	private char START_DIGIT = '0';
	private boolean nonAncoraUtilizzato = true;
	public void setStartDigit(char startDigit) throws UtilsException{
//		if(this.nonAncoraUtilizzato==false){
//			throw new UtilsException("Deve essere indicato prima di generare un identificativo");
//		}
		if(Character.isDigit(startDigit)==false){
			throw new UtilsException("Deve essere fornito un numero");
		}
		if(startDigit>this.END_DIGIT){
			throw new UtilsException("Deve essere fornito un numero di posizione minore dell'attuale numero di fine: ["+this.END_DIGIT+"]");
		}
		this.START_DIGIT = startDigit;
		if(this.nonAncoraUtilizzato){
			for (int i = 0; i < this.count.length; i++) {
				this.count[i] = this.START_DIGIT;  // zero
			}
		}
	}
	
	private char END_DIGIT = '9';
	public void setEndDigit(char endDigit) throws UtilsException{
		if(Character.isDigit(endDigit)==false){
			throw new UtilsException("Deve essere fornito un numero");
		}
		if(endDigit<this.START_DIGIT){
			throw new UtilsException("Deve essere fornito un numero di posizione maggiore dell'attuale numero di inizio: ["+this.START_DIGIT+"]");
		}
		this.END_DIGIT = endDigit;
	}

	
	
	
	
	public void setStartEndChar(char startChar,char endChar) throws UtilsException{
		if(startChar>endChar){
			throw new UtilsException("Deve essere fornito un valore di start minore del valore di end]");
		}
		char oldStart = this.START_CHAR;
		char oldEnd = this.END_CHAR;
		try{
			this.START_CHAR=startChar;
			this.END_CHAR=endChar;
			this.setStartChar(startChar);
			this.setEndChar(endChar);
		}catch(Exception e){
			this.START_CHAR=oldStart;
			this.END_CHAR=oldEnd;
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public void setStartEndDigit(char startDigit,char endDigit) throws UtilsException{
		if(startDigit>endDigit){
			throw new UtilsException("Deve essere fornito un valore di start minore del valore di end]");
		}
		char oldStart = this.START_DIGIT;
		char oldEnd = this.END_DIGIT;
		try{
			this.START_DIGIT=startDigit;
			this.END_DIGIT=endDigit;
			this.setStartDigit(startDigit);
			this.setEndDigit(endDigit);
		}catch(Exception e){
			this.START_DIGIT=oldStart;
			this.END_DIGIT=oldEnd;
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	

	/**
	 * Constructor with a default size for the alphanumeric identifier.
	 *
	 * @param wrap should the factory wrap when it reaches the maximum
	 *  long value (or throw an exception)
	 */
	public AlphanumericGenerator(boolean wrap) {
		this(wrap, DEFAULT_ALPHANUMERIC_IDENTIFIER_SIZE);
	}

	/**
	 * Constructor.
	 *
	 * @param wrap should the factory wrap when it reaches the maximum
	 *  long value (or throw an exception)
	 * @param size  the size of the identifier
	 */
	public AlphanumericGenerator(boolean wrap, int size) {
		super();
		
		//System.out.println("SIZE ["+size+"]");
		
		this.wrapping = wrap;
		if (size < 1) {
			throw new IllegalArgumentException("The size must be at least one");
		}
		this.count = new char[size];
		for (int i = 0; i < size; i++) {
			this.count[i] = this.START_DIGIT;  // zero
		}
	}

	/**
	 * Construct with a counter, that will start at the specified
	 * alphanumeric value.</p>
	 *
	 * @param wrap should the factory wrap when it reaches the maximum
	 * value (or throw an exception)
	 * @param initialValue the initial value to start at
	 */
	public AlphanumericGenerator(boolean wrap, String initialValue) {
		super();
		this.wrapping = wrap;
		this.count = initialValue.toCharArray();
		this.nonAncoraUtilizzato = false;

		//System.out.println("INITIAL ["+initialValue+"]");
		
		char upperStartChar = (this.START_CHAR+"").toUpperCase().charAt(0);
		char lowerStartChar = (this.START_CHAR+"").toLowerCase().charAt(0);
		
		char upperEndChar = (this.END_CHAR+"").toUpperCase().charAt(0);
		char lowerEndChar = (this.END_CHAR+"").toLowerCase().charAt(0);
		
		for (int i = 0; i < this.count.length; i++) {
			char ch = this.count[i];
			
			//System.out.println("CHECK i["+i+"]=["+ch+"]["+(int)ch+"]");
			
			//System.out.println("CHECK DIGIT ["+this.START_DIGIT+"]-["+this.END_DIGIT+"]");
			
			if (ch >= this.START_DIGIT && ch <= this.END_DIGIT) continue;
			
			if(this.lowerChar){
				//System.out.println("CHECK LOWER ["+lowerStartChar+"]-["+lowerEndChar+"] ["+(int)lowerStartChar+"]-["+(int)lowerEndChar+"]");
				if (ch >= lowerStartChar && ch <= lowerEndChar) continue;
			}
			
			if(this.upperChar){
				//System.out.println("CHECK UPPER ["+upperStartChar+"]-["+upperEndChar+"] ["+(int)upperStartChar+"]-["+(int)upperEndChar+"]");
				if (ch >= upperStartChar && ch <= upperEndChar) continue;
			}

			throw new IllegalArgumentException(
					"character " + this.count[i] + " is not valid");
		}
	}

	@Override
	public long maxLength() {
		return this.count.length;
	}

	@Override
	public long minLength() {
		return this.count.length;
	}

	/**
	 * Getter for property wrap.
	 *
	 * @return <code>true</code> if this generator is set up to wrap.
	 *
	 */
	public boolean isWrap() {
		return this.wrapping;
	}

	/**
	 * Sets the wrap property.
	 *
	 * @param wrap value for the wrap property
	 *
	 */
	public void setWrap(boolean wrap) {
		this.wrapping = wrap;
	}

	/**
	 * Returns the (constant) size of the strings generated by this generator.
	 *
	 * @return the size of generated identifiers
	 */
	public int getSize() {
		return this.count.length;
	}

	@Override
	public synchronized String nextStringIdentifier() throws MaxReachedException {
		
		this.nonAncoraUtilizzato = false;
		
		char upperStartChar = (this.START_CHAR+"").toUpperCase().charAt(0);
		char lowerStartChar = (this.START_CHAR+"").toLowerCase().charAt(0);
		
		char upperEndChar = (this.END_CHAR+"").toUpperCase().charAt(0);
		char lowerEndChar = (this.END_CHAR+"").toLowerCase().charAt(0);
		
//		System.out.println("upperStartChar["+upperStartChar+"]");
//		System.out.println("lowerStartChar["+lowerStartChar+"]");
//		System.out.println("upperEndChar["+upperEndChar+"]");
//		System.out.println("lowerEndChar["+lowerEndChar+"]");
		
		for (int i = this.count.length - 1; i >= 0; i--) {
			
			if(this.count[i] == this.END_DIGIT){
				if(this.lowerChar)
					this.count[i] = lowerStartChar;
				else
					this.count[i] = upperStartChar;
				i = -1;
			}
			else if(this.lowerChar && this.count[i] == lowerEndChar){
				if (i == 0 && !this.wrapping) {
					if(!this.upperChar){
						throw new MaxReachedException
						("The maximum number of identifiers has been reached");
					}
				}
				if(this.upperChar){
					this.count[i] = upperStartChar;
					i = -1;
				}
				else{
					this.count[i] = this.START_DIGIT;
				}
			}
			else if(this.upperChar && this.count[i] == upperEndChar){
				if (i == 0 && !this.wrapping) {
					throw new MaxReachedException
					("The maximum number of identifiers has been reached");
				}
				this.count[i] = this.START_DIGIT;
			}
			else {
				this.count[i]++;
				i = -1;
			}
		}
		return new String(this.count);
	}
}
