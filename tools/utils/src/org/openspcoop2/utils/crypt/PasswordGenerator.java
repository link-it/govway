/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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


package org.openspcoop2.utils.crypt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;

/**
 * PasswordGenerator
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class PasswordGenerator extends PasswordVerifier {
	
	public static PasswordGenerator DEFAULT;
	static {
		DEFAULT = new PasswordGenerator();
		DEFAULT.setIncludeLowerCaseLetter(true);
		DEFAULT.setIncludeUpperCaseLetter(true);
		DEFAULT.setIncludeNumber(true);
		DEFAULT.setIncludeNotAlphanumericSymbol(true);
	}
	
	
	public PasswordGenerator(){
		super();
	}
	public PasswordGenerator(String resource) throws UtilsException{
		super(resource);
	}
	public PasswordGenerator(InputStream is) throws UtilsException{
		super(is);
	}
	public PasswordGenerator(Properties p) throws UtilsException{
		super(p);
	}
	public PasswordGenerator(PasswordVerifier pv) throws UtilsException{
		super(pv);
		if(!this.includeLowerCaseLetter && !this.includeUpperCaseLetter && !this.includeNumber) {
			// non essendoci vincoli genero password come il generatore di default
			this.includeLowerCaseLetter = DEFAULT.isIncludeLowerCaseLetter();
			this.includeUpperCaseLetter = DEFAULT.isIncludeUpperCaseLetter();
			this.includeNumber = DEFAULT.isIncludeNumber();
		}
	}
	
	private String dictionaryChars = "abcdefghijklmnopqrstuvwxyz";
	private String dictionaryNumbers = "1234567890";
	private String dictionaryAlpha = "!?@#$%^&*()-+<>.:_";

	public String getDictionaryChars() {
		return this.dictionaryChars;
	}
	public void setDictionaryChars(String dictionaryChars) {
		this.dictionaryChars = dictionaryChars;
	}
	public String getDictionaryNumbers() {
		return this.dictionaryNumbers;
	}
	public void setDictionaryNumbers(String dictionaryNumbers) {
		this.dictionaryNumbers = dictionaryNumbers;
	}
	public String getDictionaryAlpha() {
		return this.dictionaryAlpha;
	}
	public void setDictionaryAlpha(String dictionaryAlpha) {
		this.dictionaryAlpha = dictionaryAlpha;
	}
	
	private int defaultLength = 10;
	public int getDefaultLength() {
		return this.defaultLength;
	}
	public void setDefaultLength(int defaultLength) {
		this.defaultLength = defaultLength;
	}
	
	private boolean base64 = false;
	private boolean hex = false;
	public boolean isBase64() {
		return this.base64;
	}
	public void setBase64(boolean base64) {
		this.base64 = base64;
	}
	public boolean isHex() {
		return this.hex;
	}
	public void setHex(boolean hex) {
		this.hex = hex;
	}
	
	private String prefix = null;
	private String suffix = null;
	public String getPrefix() {
		return this.prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return this.suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public String generate() throws UtilsException{
				
		if(this.minLenght>0){
			if(this.defaultLength<this.minLenght) {
				this.defaultLength = this.minLenght;
			}
		}
		
		if(this.maxLenght>0){
			if(this.defaultLength>this.maxLenght){
				this.defaultLength = this.maxLenght;
			}
		}
		
		return this.generate(this.defaultLength);
	}
	public String generate(int length) throws UtilsException{
		return this.generate("login",length);
	}
	public String generate(String username, int length) throws UtilsException{
		
		if(this.minLenght>0){
			if(length<this.minLenght){
				throw new UtilsException("La password deve essere composta almeno da "+this.minLenght+" caratteri");
			}
		}
		if(this.maxLenght>0){
			if(length>this.maxLenght){
				throw new UtilsException("La password non deve essere composta da più di "+this.minLenght+" caratteri");
			}
		}
		
		int tentativi = 100;
		for (int i = 0; i < tentativi; i++) {
			String password = _generate(length);
			if(this.validate(username, password)) {
				
				if(this.base64) {
					password =  Base64Utilities.encodeAsString(password.getBytes());
				}
				else if(this.hex) {
					password = HexBinaryUtilities.encodeAsString(password.getBytes());
				}
				
				// NOTA i prefissi e i suffissi non si codificano in modo che si possa aggiungere caratteri speciali che consentano di identificare le parti una volta effettuata la codifica (es. il '.' in base64)
				if(this.prefix!=null) {
					password = this.prefix + password;
				}
				if(this.suffix!=null) {
					password = password + this.suffix;
				}
				
				return password;
			}
		}
		
		throw new UtilsException("La generazione non è riuscita a produrre una password che soddisfi tutti i vincoli"); 
	}
	
	private String _generate(int length) throws UtilsException{
		
		if(!this.includeLowerCaseLetter && !this.includeUpperCaseLetter && !this.includeNumber) {
			throw new UtilsException("La generazione richiede almeno che l'utilizzo di numeri o caratteri sia abilitato"); 
		}
		
		List<String> password = new ArrayList<>();
		Random random = new Random();
		
		String tmpDictionaryCharsLowerCase = new String(this.dictionaryChars);
		String tmpDictionaryCharsUpperCase = new String(this.dictionaryChars);
		String tmpDictionaryNumbers = new String(this.dictionaryNumbers);
		String tmpDictionaryAlpha = new String(this.dictionaryAlpha);
		
		int i = 0;
		if(this.includeNotAlphanumericSymbol){
			int randomOffset = random.nextInt(tmpDictionaryAlpha.length());
			String s = tmpDictionaryAlpha.charAt(randomOffset)+"";
			password.add(s);
			i++;
			if(this.allDistinctCharacters){
				tmpDictionaryAlpha = tmpDictionaryAlpha.replace(s, "");
			}
		}
		for (; i < length;) {
			boolean addChar = false;
			if(this.includeLowerCaseLetter){
				if(tmpDictionaryCharsLowerCase.length()>0) {
					int randomOffset = random.nextInt(tmpDictionaryCharsLowerCase.length());
					String s = tmpDictionaryCharsLowerCase.charAt(randomOffset)+"";
					password.add(s);
					addChar = true;
					i++;
					if(this.allDistinctCharacters){
						tmpDictionaryCharsLowerCase = tmpDictionaryCharsLowerCase.replace(s, "");
					}
				}
			}
			if(i >= length) {
				break;
			}
			if(this.includeUpperCaseLetter){
				if(tmpDictionaryCharsUpperCase.length()>0) {
					int randomOffset = random.nextInt(tmpDictionaryCharsUpperCase.length());
					String s = tmpDictionaryCharsUpperCase.charAt(randomOffset)+"";
					password.add(s.toUpperCase());
					addChar = true;
					i++;
					if(this.allDistinctCharacters){
						tmpDictionaryCharsUpperCase = tmpDictionaryCharsUpperCase.replace(s, "");
					}
				}
			}
			if(i >= length) {
				break;
			}
			if(this.includeNumber){
				if(tmpDictionaryNumbers.length()>0) {
					int randomOffset = random.nextInt(tmpDictionaryNumbers.length());
					String s = tmpDictionaryNumbers.charAt(randomOffset)+"";
					password.add(s);
					addChar = true;
					i++;
					if(this.allDistinctCharacters){
						tmpDictionaryNumbers = tmpDictionaryNumbers.replace(s, "");
					}
				}
			}
			if(!addChar) {
				throw new UtilsException("Sono terminati i caratteri utilizzabili per la generazione della password di lunghezza '"+length+"'"); 
			}
		}
		
		
		Collections.shuffle(password);
		
		StringBuilder bf = new StringBuilder();
		for (String s : password) {
			bf.append(s);
		}
		return  bf.toString();
		
	}
}
