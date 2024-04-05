/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.core.transazioni.utils.credenziali;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.utils.UtilsException;

/**     
 * AbstractCredenziale
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCredenzialeList extends AbstractCredenziale {

	private List<String> values;
	
	protected AbstractCredenzialeList(TipoCredenzialeMittente tipo, List<String> values) {
		super(tipo);
		this.values = values;
	}
	
	@Override
	public String getCredenziale() throws UtilsException{
		StringBuilder bf = new StringBuilder();
		bf.append(PREFIX);
		for (String value : this.values) {
			bf.append(value);
			bf.append(PREFIX);
		}
		return bf.toString();
	}
	
	public static final String PREFIX = "##";
	
	public static String getDBValue(String address) {
		return getDBValue(address, true);
	}
	public static String getDBValue(String address, boolean ricercaEsatta) {
		if(ricercaEsatta) {
			return PREFIX + address + PREFIX;
		}
		else {
			return PREFIX + "%" + address + "%" + PREFIX;
		}
	}
	
	public static List<String> normalizeToList(String dbValue){
		List<String> lReturn = null;
		if(dbValue.contains(PREFIX)) {
			List<String> l = new ArrayList<>();
			String [] tmp = dbValue.split(PREFIX);
			normalizeToList(tmp, l);
			if(!l.isEmpty()) {
				return l;
			}
		}
		return lReturn;
	}
	private static void normalizeToList(String [] tmp, List<String> l){
		if(tmp!=null && tmp.length>0) {
			for (String t : tmp) {
				if(t!=null) {
					t = t.trim();
					if(!StringUtils.isEmpty(t)) {
						l.add(t);
					}
				}
			}
		}
	}
	public static String normalize(String dbValue) {
		List<String> l = normalizeToList(dbValue);
		if(l!=null && !l.isEmpty()) {
			StringBuilder bf = new StringBuilder();
			for (String v : l) {
				if(bf.length()>0) {
					bf.append(",");
				}
				bf.append(v);
			}
			return bf.toString();
		}
		return null;
	}
	
	@Override
	public void updateCredenziale(String newCredential) throws UtilsException{
		throw new UtilsException("Aggiornamento non supportato dal tipo di credenziale");
	}
}
