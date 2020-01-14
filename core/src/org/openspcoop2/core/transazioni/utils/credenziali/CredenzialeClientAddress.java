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

package org.openspcoop2.core.transazioni.utils.credenziali;

import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.utils.UtilsException;

/**     
 * CredenzialeTrasporto
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialeClientAddress extends AbstractCredenziale {

	private String socketAddress;
	private String transportAddress;
		
	public CredenzialeClientAddress(String socketAddress, String transportAddress) {
		super(TipoCredenzialeMittente.client_address);
		this.socketAddress = socketAddress;
		this.transportAddress = transportAddress;
	}

	@Override
	public String getCredenziale() throws UtilsException {
		boolean socketAddressDefined = this.socketAddress!=null && !"".equals(this.socketAddress);
		boolean transportAddressDefined = this.transportAddress!=null && !"".equals(this.transportAddress);
		if(socketAddressDefined && transportAddressDefined) {
			return getSocketAddressDBValue(this.socketAddress)+" "+getTransportAddressDBValue(this.transportAddress);		
		}
		else if(socketAddressDefined) {
			return getSocketAddressDBValue(this.socketAddress);		
		}
		else {
			return getTransportAddressDBValue(this.transportAddress);
		}
	}
	
	private static String PREFIX_SOCKET = "#S#";
	private static String PREFIX_TRANSPORT = "#T#";
	
	public static String getSocketAddressDBValue(String address) {
		return PREFIX_SOCKET + address + PREFIX_SOCKET;
	}
	public static String getTransportAddressDBValue(String address) {
		return PREFIX_TRANSPORT + address + PREFIX_TRANSPORT;
	}
	
	public static boolean isSocketAddressDBValue(String address) {
		return address.contains(PREFIX_SOCKET);
	}
	public static boolean isTransportAddressDBValue(String address) {
		return address.contains(PREFIX_TRANSPORT);
	}
	
	public static String convertSocketDBValueToOriginal(String address) {
		if(isSocketAddressDBValue(address)) {
			address = address.trim();
			String [] tmp = address.split(PREFIX_SOCKET);
			for (int i = 0; i < tmp.length; i++) {
				String s = tmp[i];
				if(s!=null) {
					s = s.trim();
					if(!"".equals(s)) {
						if(!isTransportAddressDBValue(s)) {
							return s;
						}	
					}
				}
			}
		}
		return null;
	}
	public static String convertTransportDBValueToOriginal(String address) {
		if(isTransportAddressDBValue(address)) {
			address = address.trim();
			String [] tmp = address.split(PREFIX_TRANSPORT);
			for (int i = 0; i < tmp.length; i++) {
				String s = tmp[i];
				if(s!=null) {
					s = s.trim();
					if(!"".equals(s)) {
						if(!isSocketAddressDBValue(s)) {
							return s;
						}	
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
