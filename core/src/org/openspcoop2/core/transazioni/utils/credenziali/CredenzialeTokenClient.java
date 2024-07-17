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

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.utils.UtilsException;

/**     
 * CredenzialeTokenApplicativoClient
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialeTokenClient extends AbstractCredenziale {

	private String clientId;
	private IDServizioApplicativo applicativo;
		
	public CredenzialeTokenClient(String clientId, IDServizioApplicativo applicativo) {
		super(TipoCredenzialeMittente.TOKEN_CLIENT_ID);
		this.clientId = clientId;
		this.applicativo = applicativo;
	}

	@Override
	public String getCredenziale() throws UtilsException {
		boolean clientIdDefined = this.clientId!=null && !"".equals(this.clientId);
		boolean applicativoDefined = this.applicativo!=null;
		if(clientIdDefined && applicativoDefined) {
			return getClientIdDBValue(this.clientId)+" "+getApplicationDBValue(getApplicationAsString(this.applicativo));		
		}
		else if(clientIdDefined) {
			return getClientIdDBValue(this.clientId);		
		}
		else {
			return getApplicationDBValue(getApplicationAsString(this.applicativo));
		}
	}
	
	public static String getApplicationAsString(IDServizioApplicativo applicativo) {
		return applicativo.toFormatString();
	}
	public static IDServizioApplicativo getApplicationFromStringValue(String v) throws CoreException {
		try {
			return IDServizioApplicativo.toIDServizioApplicativo(v);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	private static final String PREFIX_CLIENT_ID = "#C#";
	private static final String PREFIX_APPLICATION = "#A#";
	
	public static String getClientIdDBValue(String clientId) {
		return getClientIdDBValue(clientId, true);
	}
	public static String getClientIdDBValue(String clientId, boolean ricercaEsatta) {
		if(ricercaEsatta) {
			return PREFIX_CLIENT_ID + clientId + PREFIX_CLIENT_ID;
		}
		else {
			return PREFIX_CLIENT_ID + "%" + clientId + "%" + PREFIX_CLIENT_ID;
		}
	}
	public static String getApplicationDBValue(String applicative) {
		return getApplicationDBValue(applicative, true);
	}
	public static String getApplicationDBValue(String applicative, boolean ricercaEsatta) {
		if(ricercaEsatta) {
			return PREFIX_APPLICATION + applicative + PREFIX_APPLICATION;
		}
		else {
			return PREFIX_APPLICATION + "%" + applicative + "%" + PREFIX_APPLICATION;
		}
	}
	
	public static boolean isClientIdDBValue(String clientId) {
		return clientId.contains(PREFIX_CLIENT_ID);
	}
	public static boolean isApplicationDBValue(String applicative) {
		return applicative.contains(PREFIX_APPLICATION);
	}
	
	public static String convertClientIdDBValueToOriginal(String id) {
		if(isClientIdDBValue(id)) {
			id = id.trim();
			String [] tmp = id.split(PREFIX_CLIENT_ID);
			for (int i = 0; i < tmp.length; i++) {
				String s = tmp[i];
				if(s!=null) {
					s = s.trim();
					if( (!"".equals(s)) &&
						(!isApplicationDBValue(s)) ){
						return s;
					}
				}
			}
		}
		return id; // ritorno lo stesso valore poichè l'informazione è stata salvata non strutturata (solo con il client id)
	}
	public static IDServizioApplicativo convertApplicationDBValueToOriginal(String id) throws CoreException {
		String s = convertApplicationDBValueToOriginalAsString(id);
		if(s!=null) {
			return getApplicationFromStringValue(s);
		}
		return null;
	}
	public static String convertApplicationDBValueToOriginalAsString(String id) {
		if(isApplicationDBValue(id)) {
			id = id.trim();
			String [] tmp = id.split(PREFIX_APPLICATION);
			for (int i = 0; i < tmp.length; i++) {
				String s = tmp[i];
				if(s!=null) {
					s = s.trim();
					if( (!"".equals(s)) &&
						(!isClientIdDBValue(s)) 
						){
						return s;
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public void updateCredenziale(String newCredential) throws UtilsException{
		throw new UtilsException("Aggiornamento non supportato dal tipo di credenziale");
	}
}
