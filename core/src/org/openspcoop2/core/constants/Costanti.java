/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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

package org.openspcoop2.core.constants;

import java.text.SimpleDateFormat;

/**
 * Costanti 
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	 /** Contesto della Porta di Dominio */ 
	public final static String CLUSTER_ID = "ID";
	public final static String CONNECTION_PDD = "CONNECTION_PDD";
	public final static String PROTOCOLLO = "PROTOCOLLO";
	public final static String ID_PORTA = "ID_PORTA";
	public final static String ID_FUNZIONE = "ID_FUNZIONE";
	public final static String ID_MESSAGGIO = "ID_MESSAGGIO";
	public final static String ID_FRUITORE = "FRUITORE";
	public final static String ID_SERVIZIO = "SERVIZIO";
	public final static String HEADER_TRASPORTO = "HEADER_TRASPORTO";
	public final static String TIPO_OPERAZIONE_IM = "TIPO_OPERAZIONE_IM";
	public final static String PORTA_DELEGATA = "PORTA_DELEGATA";
	public final static String SOAP_VERSION = "SOAP_VERSION";
	public final static String STATELESS = "STATELESS";
	public final static String DATA_PRESA_IN_CARICO = "DATA_PRESA_IN_CARICO";
	public final static String [] CONTEXT_OBJECT = 
		new String [] {Costanti.CLUSTER_ID,Costanti.PROTOCOLLO,
			Costanti.ID_PORTA,Costanti.ID_FUNZIONE,
			Costanti.ID_MESSAGGIO,Costanti.ID_FRUITORE,Costanti.ID_SERVIZIO,
			Costanti.HEADER_TRASPORTO,Costanti.TIPO_OPERAZIONE_IM,
			Costanti.STATELESS,Costanti.DATA_PRESA_IN_CARICO};
    
	public final static String dateFormat = "yyyy-MM-dd_HH:mm:ss.SSS"; 
	public static SimpleDateFormat newSimpleDateFormat(){
		return new SimpleDateFormat(dateFormat); // SimpleDateFormat non e' thread-safe
	}
}
