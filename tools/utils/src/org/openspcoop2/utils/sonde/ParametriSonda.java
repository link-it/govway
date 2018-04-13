/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.utils.sonde;

import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.soap.encoding.soapenc.Base64;
/**
 * Classe contenente i parametri per le Sonde
 * 
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ParametriSonda {

	private String nome;
	private long sogliaWarn, sogliaError;
	private Date dataOk, dataWarn, dataError, dataUltimoCheck;
	private Properties datiCheck;
	private Set<String> reserved = new HashSet<String>();
	private int statoUltimoCheck;
	
	private static final String SEPARATOR = ":";
	private static final String CUSTOM = "CUSTOM##";

	/**
	 * Deserializza una stringa in una lista di properties rappresentanti i dati di check
	 * @param datiCheck stringa rappresentante una lista di parametri nome:valore 
	 * @throws Exception
	 */
	public void unmarshallDatiCheck(String datiCheck) {
		this.datiCheck = new Properties();
		if(datiCheck != null) {
			String[] datiList = datiCheck.split("\\\n"); 
			for(String dato: datiList) {
				if(dato.startsWith(CUSTOM)) {
					String datoReale = dato.substring(CUSTOM.length());
					String[] nameValue = datoReale.split(SEPARATOR); 
					if(nameValue.length != 2){
						System.err.println("Dato Check ["+datoReale+"] non nel formato valido nome"+SEPARATOR+"valore");
					} else {
						try {
							String key = nameValue[0].trim();
							String value = nameValue[1].trim();
							byte[] decodedKey = Base64.decode(key);
							byte[] decodedValue = Base64.decode(value);
							this.datiCheck.put(new String(decodedKey), new String(decodedValue));
						} catch(Throwable t) {
							System.err.println("Errore durante il decoding del parametro ["+datoReale+"]: " + t.getMessage());	
						}
					}
					
				} else {
					String[] nameValue = dato.split(SEPARATOR); 
					if(nameValue.length != 2){
						System.err.println("Dato Check ["+dato+"] non nel formato valido nome"+SEPARATOR+"valore");
					} else {
						this.datiCheck.put(nameValue[0].trim(), nameValue[1].trim());
					}
				}
			}
		}
	}

	/**
	 * Serializza una lista di properties rappresentanti i dati di check in una stringa
	 * @return stringa rappresentante una lista di parametri nome=valore
	 */
	public String marshallDatiCheck() {
		
		if(this.datiCheck != null) {
			StringBuilder sb = new StringBuilder();
			for(Object key: this.datiCheck.keySet()) {
				boolean contains = this.reserved.contains(key);
				
				if(contains) {
					sb.append(key).append(SEPARATOR).append(this.datiCheck.get(key)).append("\n");
				} else {
					String keyEncoded = Base64.encode(key.toString().getBytes());
					String valueEncoded = Base64.encode(this.datiCheck.get(key).toString().getBytes());
					sb.append(CUSTOM).append(keyEncoded).append(SEPARATOR).append(valueEncoded).append("\n");
				}
			}
			return sb.toString();
		} else {
			return null;
		}
	}

	public void putAllCheck(Properties checks) {
		if(checks != null) {
			for(Object key: checks.keySet()) {
				if(!this.reserved.contains(key)) {
					this.datiCheck.put(key, checks.get(key));
				}
			}
		}
	}
	/**
	 *	GETTERS E SETTERS 
	 */
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public long getSogliaWarn() {
		return this.sogliaWarn;
	}
	public void setSogliaWarn(long sogliaWarn) {
		this.sogliaWarn = sogliaWarn;
	}
	public long getSogliaError() {
		return this.sogliaError;
	}
	public void setSogliaError(long sogliaError) {
		this.sogliaError = sogliaError;
	}
	public Date getDataOk() {
		return this.dataOk;
	}
	public void setDataOk(Date dataOk) {
		this.dataOk = dataOk;
	}
	public Date getDataWarn() {
		return this.dataWarn;
	}
	public void setDataWarn(Date dataWarn) {
		this.dataWarn = dataWarn;
	}
	public Date getDataError() {
		return this.dataError;
	}
	public void setDataError(Date dataError) {
		this.dataError = dataError;
	}
	public Date getDataUltimoCheck() {
		return this.dataUltimoCheck;
	}
	public void setDataUltimoCheck(Date dataUltimoCheck) {
		this.dataUltimoCheck = dataUltimoCheck;
	}
	public Properties getDatiCheck() {
		return this.datiCheck;
	}
	public void setDatiCheck(Properties datiCheck) {
		this.datiCheck = datiCheck;
	}
	public int getStatoUltimoCheck() {
		return this.statoUltimoCheck;
	}
	public void setStatoUltimoCheck(int statoUltimoCheck) {
		this.statoUltimoCheck = statoUltimoCheck;
	}

	public Set<String> getReserved() {
		return this.reserved;
	}

	public void setReserved(Set<String> reserved) {
		this.reserved = reserved;
	}
}
