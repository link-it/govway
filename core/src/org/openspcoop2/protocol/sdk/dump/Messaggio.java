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


package org.openspcoop2.protocol.sdk.dump;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;

/**
 * Messaggio
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Messaggio implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4718160136521047108L;
	
	private TipoMessaggio tipoMessaggio;
	
	private String contentType;
	
	private byte[] body;
	private BodyMultipartInfo bodyMultipartInfo;

	private List<Attachment> attachments;
	
	private Map<String, String> headers = new Hashtable<String, String>();
	
	private Map<String, String> contenuti = new Hashtable<String, String>();
	
	private Date gdo;
	
	private IDSoggetto dominio;
	private TipoPdD tipoPdD;
	private String idFunzione;
	
	private String idTransazione;
	private String idBusta;
	private IDSoggetto fruitore;
	private IDServizio servizio;

	private String protocollo;
	
	
	public TipoMessaggio getTipoMessaggio() {
		return this.tipoMessaggio;
	}

	public void setTipoMessaggio(TipoMessaggio tipoMessaggio) {
		this.tipoMessaggio = tipoMessaggio;
	}

	public byte[] getBody() {
		return this.body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public BodyMultipartInfo getBodyMultipartInfo() {
		return this.bodyMultipartInfo;
	}

	public void setBodyMultipartInfo(BodyMultipartInfo bodyMultipartInfo) {
		this.bodyMultipartInfo = bodyMultipartInfo;
	}
	
	public List<Attachment> getAttachments() {
		return this.attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Map<String, String> getContenuti() {
		return this.contenuti;
	}

	public void setContenuti(Map<String, String> contenuti) {
		this.contenuti = contenuti;
	}
	
	public Map<String, String> getHeaders() {
		return this.headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Date getGdo() {
		return this.gdo;
	}

	public void setGdo(Date gdo) {
		this.gdo = gdo;
	}

	public IDSoggetto getDominio() {
		return this.dominio;
	}

	public void setDominio(IDSoggetto dominio) {
		this.dominio = dominio;
	}

	public TipoPdD getTipoPdD() {
		return this.tipoPdD;
	}

	public void setTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}

	public String getIdFunzione() {
		return this.idFunzione;
	}

	public void setIdFunzione(String idFunzione) {
		this.idFunzione = idFunzione;
	}

	public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}

	public String getIdBusta() {
		return this.idBusta;
	}

	public void setIdBusta(String idBusta) {
		this.idBusta = idBusta;
	}

	public IDSoggetto getFruitore() {
		return this.fruitore;
	}

	public void setFruitore(IDSoggetto fruitore) {
		this.fruitore = fruitore;
	}

	public IDServizio getServizio() {
		return this.servizio;
	}

	public void setServizio(IDServizio servizio) {
		this.servizio = servizio;
	}

	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	
	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
