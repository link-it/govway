/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;

/**
 * ContenutiTransazioneArchivioBean
 * 
 *  
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContenutiTransazioneArchivioBean {
	// contenuto file manifest
	private String contentType;
	private Long contentLength;
	private String messageType;
	private String transactionId;
	private String protocol;
	// contenuto file message
	private byte[] message;
	// elenco multipart headers
	List<DumpMultipartHeader> headersMultiPart;
	//header trasporto
	List<DumpHeaderTrasporto> headers;
	//contenuti
	List<DumpContenuto> contenuti;
	//allegati
	Map<String,DumpAllegato> allegati;
	
	public ContenutiTransazioneArchivioBean() {
		this.headers = new ArrayList<>();
		this.headersMultiPart = new ArrayList<>();
		this.contenuti = new ArrayList<>();
		this.allegati = new HashMap<>();
	}
	
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Long getContentLength() {
		return this.contentLength;
	}
	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}
	public String getMessageType() {
		return this.messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getTransactionId() {
		return this.transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getProtocol() {
		return this.protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public byte[] getMessage() {
		return this.message;
	}
	public void setMessage(byte[] message) {
		this.message = message;
	}
	public List<DumpMultipartHeader> getHeadersMultiPart() {
		return this.headersMultiPart;
	}
	public List<DumpHeaderTrasporto> getHeaders() {
		return this.headers;
	}
	public List<DumpContenuto> getContenuti() {
		return this.contenuti;
	}
	public Map<String,DumpAllegato> getAllegati() {
		return this.allegati;
	}

}
