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


package org.openspcoop2.pdd.mdb.threads;

import java.sql.Timestamp;

/** 
 * 
 * @author Fabio Tronci ( tronci@link.it )
 * @author $Author$
 * @version $Rev$, $Date$
 *
 * Rappresenta un messaggio che viene trattato dagli Workers nell'implementazione Threads-Based dei moduli 
 * OpenSPCoop
 */


public class MessageIde {

	String tipo = "";
	String idMessaggio = "";
	//Contiene un messaggio jms serializzato
	byte [] msg_bytes = null;
	
	Timestamp redelivery_delay = null;
	
	int redelivery_count = 0;
	
	
	public String getIdMessaggio() {
		return this.idMessaggio;
	}
	public void setIdMessaggio(String id) {
		this.idMessaggio = id;
	}
	public byte[] getMsg_bytes() {
		return this.msg_bytes;
	}
	public void setMsg_bytes(byte[] msg_bytes) {
		this.msg_bytes = msg_bytes;
	}
	public String getTipo() {
		return this.tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public MessageIde(String idMessaggio, String tipo){
		this.idMessaggio = idMessaggio;
		this.tipo = tipo;
	}
	public MessageIde() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @return un timestamp che indica a che ora va ritrattato il messaggio
	 */
	public Timestamp getRedelivery_delay() {
		return this.redelivery_delay;
	}
	
	/**
	 * 
	 * @param redelivery_delay l'ora a partire da cui puo essere ritrattato il messaggio
	 */
	public void setRedelivery_delay(Timestamp redelivery_delay) {
		this.redelivery_delay = redelivery_delay;
	}
	
	/**
	 * 
	 * @return il numero di volte che il messaggio e' stato trattato da un singolo nodo openSPCoop
	 */
	public int getRedelivery_count() {
		return this.redelivery_count;
	}
	
	/**
	 * 
	 * @param redelivery_count il numero di volte che il messaggio e' stato trattato da un singolo nodo openSPCoop
	 */
	public void setRedelivery_count(int redelivery_count) {
		this.redelivery_count = redelivery_count;
	}
	
}
