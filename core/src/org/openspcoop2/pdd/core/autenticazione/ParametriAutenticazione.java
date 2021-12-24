/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.autenticazione;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openspcoop2.core.config.Proprieta;


/**
 * ParametriAutenticazione
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParametriAutenticazione implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String separator = "@@@";
	private static final String separator_line = "@#@";
	
	public ParametriAutenticazione() {}
	public ParametriAutenticazione(List<Proprieta> list) {
		if(list!=null && !list.isEmpty()) {
			for (Proprieta proprieta : list) {
				this.map.put(proprieta.getNome(), proprieta.getValore());
			}
		}
	}
	public ParametriAutenticazione(ParametriAutenticazione p) {
		if(p!=null && p.map!=null) {
			this.map = p.map;
		}
	}
	public ParametriAutenticazione(String dbValue) throws AutenticazioneException {
		if(dbValue!=null) {
			if(dbValue.contains(separator)==false) {
				throw new AutenticazioneException("Formato errato");
			}
			if(dbValue.contains(separator_line)) {
				String [] linee = dbValue.split(separator_line);
				if(linee==null || linee.length<=0) {
					throw new AutenticazioneException("Formato errato (linea senza valori?)");
				}
				for (int i = 0; i < linee.length; i++) {
					String linea = linee[i];
					String [] tmp = linea.split(separator);
					if(tmp==null || tmp.length!=2) {
						throw new AutenticazioneException("Formato errato (coppia non presente?)");
					}
					this.map.put(tmp[0], tmp[1]);
				}
			}
			else {
				String [] tmp = dbValue.split(separator);
				if(tmp==null || tmp.length!=2) {
					throw new AutenticazioneException("Formato errato (coppia non presente?)");
				}
				this.map.put(tmp[0], tmp[1]);
			}
		}
	}
	
	protected HashMap<String, String> map = new HashMap<>();
	
	public void add(String nome, String valore) {
		this.map.put(nome, valore);
	}
	
	public Set<String> keys() {
		return this.map.keySet();
	}
	
	public String get(String key) {
		return this.map.get(key);
	}
	
	public String convertToDBValue() {
		if(this.map.isEmpty()) {
			return null;
		}
		StringBuilder bf = new StringBuilder();
		Iterator<String> it = this.map.keySet().iterator();
		while (it.hasNext()) {
			if(bf.length()>0) {
				bf.append(separator_line);
			}
			String key = (String) it.next();
			String value = this.map.get(key);
			bf.append(key).append(separator).append(value);
		}
		return bf.toString();
	}
	
	@Override
	public String toString(){
		if(this.map.isEmpty()) {
			return "";
		}
		StringBuilder bf = new StringBuilder();
		Iterator<String> it = this.map.keySet().iterator();
		while (it.hasNext()) {
			if(bf.length()>0) {
				bf.append("\n");
			}
			String key = (String) it.next();
			String value = this.map.get(key);
			bf.append(key).append("=").append(value);
		}
		return bf.toString();
	}
}
