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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * PasswordVerifier
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class PasswordVerifier {
	
	private final static String PROPERTY_REGULAR_EXPRESSIONS_PREFIX = "passwordVerifier.regularExpression.";
	private final static String PROPERTY_LOGIN_CONTAINS = "passwordVerifier.notContainsLogin";
	private final static String PROPERTY_RESTRICTED_WORDS = "passwordVerifier.restrictedWords";
	private final static String PROPERTY_MIN_LENGTH = "passwordVerifier.minLength";
	private final static String PROPERTY_MAX_LENGTH = "passwordVerifier.maxLength";
	private final static String PROPERTY_INCLUDE_LOWER_CASE_LETTER = "passwordVerifier.lowerCaseLetter";
	private final static String PROPERTY_INCLUDE_UPPER_CASE_LETTER = "passwordVerifier.upperCaseLetter";
	private final static String PROPERTY_INCLUDE_NUMBER = "passwordVerifier.includeNumber";
	private final static String PROPERTY_INCLUDE_NOT_ALPHANUMERIC_SYMBOL = "passwordVerifier.includeNotAlphanumericSymbol";
	private final static String PROPERTY_ALL_DISTINCT_CHARACTERS = "passwordVerifier.allDistinctCharacters";
	
	protected List<String> regulaExpressions = new ArrayList<>();
	protected boolean notContainsLogin = false;
	protected List<String> restrictedWords = new ArrayList<>();
	protected int minLenght = -1;
	protected int maxLenght = -1;
	protected boolean includeLowerCaseLetter = false;
	protected boolean includeUpperCaseLetter = false;
	protected boolean includeNumber = false;
	protected boolean includeNotAlphanumericSymbol = false;
	protected boolean allDistinctCharacters = false;

	public PasswordVerifier(){}
	public PasswordVerifier(PasswordVerifier pv){
		this.regulaExpressions = pv.regulaExpressions;
		this.notContainsLogin = pv.notContainsLogin;
		this.restrictedWords = pv.restrictedWords;
		this.minLenght = pv.minLenght;
		this.maxLenght = pv.maxLenght;
		this.includeLowerCaseLetter = pv.includeLowerCaseLetter;
		this.includeUpperCaseLetter = pv.includeUpperCaseLetter;
		this.includeNumber = pv.includeNumber;
		this.includeNotAlphanumericSymbol = pv.includeNotAlphanumericSymbol;
		this.allDistinctCharacters = pv.allDistinctCharacters;
	}
	public PasswordVerifier(String resource) throws UtilsException{
		InputStream is = null;
		try{
			File f = new File(resource);
			if(f.exists()){
				is = new FileInputStream(f);
			}
			else{
				is = PasswordVerifier.class.getResourceAsStream(resource);
			}
			if(is!=null){
				Properties p = new Properties();
				p.load(is);
				this._init(p);
			}
			else{
				throw new Exception("Resource ["+resource+"] not found");
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
	}
	public PasswordVerifier(InputStream is) throws UtilsException{
		try{
			Properties p = new Properties();
			p.load(is);
			this._init(p);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public PasswordVerifier(Properties p) throws UtilsException{
		this._init(p);
	}
	private void _init(Properties p) throws UtilsException{
		try{
			
			Properties tmpP = Utilities.readProperties(PROPERTY_REGULAR_EXPRESSIONS_PREFIX, p);
			if(tmpP!=null && tmpP.size()>0){
				Enumeration<?> en =tmpP.keys();
				while (en.hasMoreElements()) {
					String key = (String) en.nextElement();
					String value = tmpP.getProperty(key);
					this.regulaExpressions.add(value);
				}
			}
			
			String tmp = p.getProperty(PROPERTY_LOGIN_CONTAINS);
			if(tmp!=null){
				tmp=tmp.trim();
				try{
					this.notContainsLogin = Boolean.parseBoolean(tmp);
				}catch(Exception e){
					throw new Exception("Property '"+PROPERTY_LOGIN_CONTAINS+"' with wrong value '"+tmp+"': "+e.getMessage(),e);
				}
			}
			
			tmp = p.getProperty(PROPERTY_RESTRICTED_WORDS);
			if(tmp!=null){
				tmp=tmp.trim();
				if(tmp.contains(",")){
					String [] split = tmp.split(",");
					for (int i = 0; i < split.length; i++) {
						this.restrictedWords.add(split[i].trim());
					}
				}
				else{
					this.restrictedWords.add(tmp);
				}
			}

			tmp = p.getProperty(PROPERTY_MIN_LENGTH);
			if(tmp!=null){
				tmp=tmp.trim();
				try{
					this.minLenght = Integer.parseInt(tmp);
				}catch(Exception e){
					throw new Exception("Property '"+PROPERTY_MIN_LENGTH+"' with wrong value '"+tmp+"': "+e.getMessage(),e);
				}
			}
			
			tmp = p.getProperty(PROPERTY_MAX_LENGTH);
			if(tmp!=null){
				tmp=tmp.trim();
				try{
					this.maxLenght = Integer.parseInt(tmp);
				}catch(Exception e){
					throw new Exception("Property '"+PROPERTY_MAX_LENGTH+"' with wrong value '"+tmp+"': "+e.getMessage(),e);
				}
			}
			
			tmp = p.getProperty(PROPERTY_INCLUDE_LOWER_CASE_LETTER);
			if(tmp!=null){
				tmp=tmp.trim();
				try{
					this.includeLowerCaseLetter = Boolean.parseBoolean(tmp);
				}catch(Exception e){
					throw new Exception("Property '"+PROPERTY_INCLUDE_LOWER_CASE_LETTER+"' with wrong value '"+tmp+"': "+e.getMessage(),e);
				}
			}
			
			tmp = p.getProperty(PROPERTY_INCLUDE_UPPER_CASE_LETTER);
			if(tmp!=null){
				tmp=tmp.trim();
				try{
					this.includeUpperCaseLetter = Boolean.parseBoolean(tmp);
				}catch(Exception e){
					throw new Exception("Property '"+PROPERTY_INCLUDE_UPPER_CASE_LETTER+"' with wrong value '"+tmp+"': "+e.getMessage(),e);
				}
			}
			
			tmp = p.getProperty(PROPERTY_INCLUDE_NUMBER);
			if(tmp!=null){
				tmp=tmp.trim();
				try{
					this.includeNumber = Boolean.parseBoolean(tmp);
				}catch(Exception e){
					throw new Exception("Property '"+PROPERTY_INCLUDE_NUMBER+"' with wrong value '"+tmp+"': "+e.getMessage(),e);
				}
			}
			
			tmp = p.getProperty(PROPERTY_INCLUDE_NOT_ALPHANUMERIC_SYMBOL);
			if(tmp!=null){
				tmp=tmp.trim();
				try{
					this.includeNotAlphanumericSymbol = Boolean.parseBoolean(tmp);
				}catch(Exception e){
					throw new Exception("Property '"+PROPERTY_INCLUDE_NOT_ALPHANUMERIC_SYMBOL+"' with wrong value '"+tmp+"': "+e.getMessage(),e);
				}
			}
			
			tmp = p.getProperty(PROPERTY_ALL_DISTINCT_CHARACTERS);
			if(tmp!=null){
				tmp=tmp.trim();
				try{
					this.allDistinctCharacters = Boolean.parseBoolean(tmp);
				}catch(Exception e){
					throw new Exception("Property '"+PROPERTY_ALL_DISTINCT_CHARACTERS+"' with wrong value '"+tmp+"': "+e.getMessage(),e);
				}
			}

		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public List<String> getRegulaExpressions() {
		return this.regulaExpressions;
	}
	public void setRegulaExpressions(List<String> regulaExpressions) {
		this.regulaExpressions = regulaExpressions;
	}
	public boolean isNotContainsLogin() {
		return this.notContainsLogin;
	}
	public void setNotContainsLogin(boolean notContainsLogin) {
		this.notContainsLogin = notContainsLogin;
	}
	public List<String> getRestrictedWords() {
		return this.restrictedWords;
	}
	public void setRestrictedWords(List<String> restrictedWords) {
		this.restrictedWords = restrictedWords;
	}
	public int getMinLenght() {
		return this.minLenght;
	}
	public void setMinLenght(int minLenght) {
		this.minLenght = minLenght;
	}
	public int getMaxLenght() {
		return this.maxLenght;
	}
	public void setMaxLenght(int maxLenght) {
		this.maxLenght = maxLenght;
	}
	public boolean isIncludeLowerCaseLetter() {
		return this.includeLowerCaseLetter;
	}
	public void setIncludeLowerCaseLetter(boolean includeLowerCaseLetter) {
		this.includeLowerCaseLetter = includeLowerCaseLetter;
	}
	public boolean isIncludeUpperCaseLetter() {
		return this.includeUpperCaseLetter;
	}
	public void setIncludeUpperCaseLetter(boolean includeUpperCaseLetter) {
		this.includeUpperCaseLetter = includeUpperCaseLetter;
	}
	public boolean isIncludeNumber() {
		return this.includeNumber;
	}
	public void setIncludeNumber(boolean includeNumber) {
		this.includeNumber = includeNumber;
	}
	public boolean isIncludeNotAlphanumericSymbol() {
		return this.includeNotAlphanumericSymbol;
	}
	public void setIncludeNotAlphanumericSymbol(boolean includeNotAlphanumericSymbol) {
		this.includeNotAlphanumericSymbol = includeNotAlphanumericSymbol;
	}
	public boolean isAllDistinctCharacters() {
		return this.allDistinctCharacters;
	}
	public void setAllDistinctCharacters(boolean allDistinctCharacters) {
		this.allDistinctCharacters = allDistinctCharacters;
	}
	
	public boolean validate(String login, String password){
		StringBuilder bf = new StringBuilder();
		return this.validate(login,password,bf);
	}
	public boolean validate(String login, String password,StringBuilder bfMotivazioneErrore){
		if(password==null) {
			bfMotivazioneErrore.append("Password non fornita");
			return false;
		}
		password = password.trim();
		if(this.regulaExpressions.size()>0){
			for (String regExp : this.regulaExpressions) {
				try{
					if (RegularExpressionEngine.isMatch(password, regExp) == false){
						bfMotivazioneErrore.append("La password non rispetta l'espressione regolare: "+regExp);
						return false;
					}
				}catch(Exception e){
					bfMotivazioneErrore.append("Rilevato un errore durante la verifica della password con l'espressione regolare '"+regExp+"': "+e.getMessage());
					return false;
				}
			}
		}
		if(this.notContainsLogin){
			if(password.contains(login)){
				bfMotivazioneErrore.append("La password contiene il nome di login");
				return false;
			}
		}
		if(this.restrictedWords.size()>0){
			for (String word : this.restrictedWords) {
				if(password.toLowerCase().equals(word)){
					bfMotivazioneErrore.append("La password corrisponde ad una delle parole riservate: "+word);
					return false;
				}
			}
		}
		if(this.minLenght>0){
			if(password.length()<this.minLenght){
				bfMotivazioneErrore.append("La password deve essere composta almeno da ").append(this.minLenght).
					append(" caratteri mentre quella fornita ha una lunghezza di ").append(password.length()).append(" caratteri");
				return false;
			}
		}
		if(this.maxLenght>0){
			if(password.length()>this.maxLenght){
				bfMotivazioneErrore.append("La password non deve essere composta da più di ").append(this.minLenght).
					append(" caratteri mentre quella fornita ha una lunghezza di ").append(password.length()).append(" caratteri");
				return false;
			}
		}
		if(this.includeLowerCaseLetter){
			boolean found = false;
			for (int i = 0; i < password.length(); i++) {
				String c = password.charAt(i)+"";
				if(StringUtils.isAllLowerCase(c)){
					found = true;
					break;
				}
			}
			if(!found){
				bfMotivazioneErrore.append("La password deve contenere almeno una lettera minuscola (a - z)");
				return false;
			}
		}
		if(this.includeUpperCaseLetter){
			boolean found = false;
			for (int i = 0; i < password.length(); i++) {
				String c = password.charAt(i)+"";
				if(StringUtils.isAllUpperCase(c)){
					found = true;
					break;
				}
			}
			if(!found){
				bfMotivazioneErrore.append("La password deve contenere almeno una lettera maiuscola (A - Z)");
				return false;
			}
		}
		if(this.includeNumber){
			boolean found = false;
			for (int i = 0; i < password.length(); i++) {
				String c = password.charAt(i)+"";
				if(StringUtils.isNumeric(c)){
					found = true;
					break;
				}
			}
			if(!found){
				bfMotivazioneErrore.append("La password deve contenere almeno un numero (0 - 9)");
				return false;
			}
		}
		if(this.includeNotAlphanumericSymbol){
			boolean found = false;
			for (int i = 0; i < password.length(); i++) {
				String c = password.charAt(i)+"";
				if(!StringUtils.isNumeric(c) && !StringUtils.isAlpha(c)){
					found = true;
					break;
				}
			}
			if(!found){
				bfMotivazioneErrore.append("La password deve contenere almeno un carattere non alfanumerico (ad esempio, !, $, #, %, @)");
				return false;
			}
		}
		if(this.allDistinctCharacters){
			for (int i = 0; i < password.length(); i++) {
				String c = password.charAt(i)+"";
				int count = 0;
				for (int j = 0; j < password.length(); j++) {
					String check = password.charAt(j)+"";
					if(check.equals(c)){
						count++;
					}
				}
				if(count>1){
					bfMotivazioneErrore.append("Tutti i caratteri utilizzati devono essere differenti mentre nella password fornita il carattere '"+c+"' appare "+count+" volte");
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean existsRestriction(){
		String s = this.help("", "", false);
		return s != null && !"".equals(s);
	}
	
	public String help(){
		return this.help("\n", "- ", true);
	}
	public String help(String separator){
		return this.help(separator, "- ", true);
	}
	public String help(String separator, String elenco, boolean premessa){
		StringBuilder bf = new StringBuilder();
		if(premessa){
			bf.append("La password deve rispettare i seguenti vincoli: ");
		}
		if(this.regulaExpressions.size()>0){
			for (String regExp : this.regulaExpressions) {
				bf.append(separator);
				bf.append(elenco).append("deve soddisfare l'espressione regolare: "+regExp);
			}
		}
		if(this.notContainsLogin){
			bf.append(separator);
			bf.append(elenco).append("non deve contenere il nome di login dell'utente");
		}
		if(this.restrictedWords.size()>0){
			bf.append(separator);
			bf.append(elenco).append("non deve corrispondere ad una delle seguenti parole riservate: "+this.restrictedWords);
		}
		if(this.minLenght>0){
			bf.append(separator);
			bf.append(elenco).append("deve essere composta almeno da ").append(this.minLenght).append(" caratteri");
		}
		if(this.maxLenght>0){
			bf.append(separator);
			bf.append(elenco).append("non deve essere composta da più di ").append(this.maxLenght).append(" caratteri");
		}
		if(this.includeLowerCaseLetter){
			bf.append(separator);
			bf.append(elenco).append("deve contenere almeno una lettera minuscola (a - z)");
		}
		if(this.includeUpperCaseLetter){
			bf.append(separator);
			bf.append(elenco).append("deve contenere almeno una lettera maiuscola (A - Z)");
		}
		if(this.includeNumber){
			bf.append(separator);
			bf.append(elenco).append("deve contenere almeno un numero (0 - 9)");
		}
		if(this.includeNotAlphanumericSymbol){
			bf.append(separator);
			bf.append(elenco).append("deve contenere almeno un carattere non alfanumerico (ad esempio, !, $, #, %, @)");
		}
		if(this.allDistinctCharacters){
			bf.append(separator);
			bf.append(elenco).append("tutti i caratteri utilizzati devono essere differenti");
		}
		return bf.toString();
	}
}
