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
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
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

	private List<Attachment> attachments = new ArrayList<>();
	
	private Map<String, String> headers = new Hashtable<String, String>();
	
	private Map<String, String> contenuti = new Hashtable<String, String>();
	
	private String idTransazione;

	private Date gdo;

	private String protocollo;
	
	
	// le restanti informazioni non vengono serializzate su database.
	
	private IDSoggetto dominio;
	private TipoPdD tipoPdD;
	private String idFunzione;
	
	private String idBusta;
	private IDSoggetto fruitore;
	private IDServizio servizio;

	
	public Messaggio() {}
	public Messaggio(DumpMessaggio dumpMessaggio) {
		
		this.tipoMessaggio = dumpMessaggio.getTipoMessaggio();
		
		this.contentType = dumpMessaggio.getContentType();
		
		this.body = dumpMessaggio.getBody();
		if(dumpMessaggio.getMultipartContentId()!=null || 
				dumpMessaggio.getMultipartContentLocation()!=null ||
				dumpMessaggio.getMultipartContentType()!=null ||
				dumpMessaggio.getMultipartHeaderList()!=null ||
				dumpMessaggio.getMultipartHeaderList().size()>0) {
			this.bodyMultipartInfo = new BodyMultipartInfo();
			this.bodyMultipartInfo.setContentId(dumpMessaggio.getMultipartContentId());
			this.bodyMultipartInfo.setContentLocation(dumpMessaggio.getMultipartContentLocation());
			this.bodyMultipartInfo.setContentType(dumpMessaggio.getMultipartContentType());
			if(dumpMessaggio.getMultipartHeaderList()!=null ||
				dumpMessaggio.getMultipartHeaderList().size()>0) {
				for (DumpMultipartHeader hdr : dumpMessaggio.getMultipartHeaderList()) {
					String valore = hdr.getValore();
					if(valore==null) {
						valore = "";
					}
					this.bodyMultipartInfo.getHeaders().put(hdr.getNome(), valore);
				}
			}
		}
		
		if(dumpMessaggio.sizeAllegatoList()>0) {
			for (DumpAllegato allegato : dumpMessaggio.getAllegatoList()) {
				Attachment attachment = new Attachment();
				attachment.setContentId(allegato.getContentId());
				attachment.setContentLocation(allegato.getContentLocation());
				attachment.setContentType(allegato.getContentType());
				attachment.setContent(allegato.getAllegato());
				if(allegato.sizeHeaderList()>0) {
					for (DumpHeaderAllegato hdr : allegato.getHeaderList()) {
						String valore = hdr.getValore();
						if(valore==null) {
							valore = "";
						}
						attachment.getHeaders().put(hdr.getNome(), valore);
					}
				}
				this.attachments.add(attachment);
			}
		}
		
		if(dumpMessaggio.sizeHeaderTrasportoList()>0) {
			for (DumpHeaderTrasporto hdr : dumpMessaggio.getHeaderTrasportoList()) {
				String valore = hdr.getValore();
				if(valore==null) {
					valore = "";
				}
				this.headers.put(hdr.getNome(), valore);
			}
		}
		
		if(dumpMessaggio.sizeContenutoList()>0) {
			for (DumpContenuto contenuto : dumpMessaggio.getContenutoList()) {
				String valore = contenuto.getValore();
				if(valore==null) {
					valore = "";
				}
				this.contenuti.put(contenuto.getNome(), valore);
			}
		}
		
		this.idTransazione = dumpMessaggio.getIdTransazione();
		
		this.gdo = dumpMessaggio.getDumpTimestamp();
		
		this.protocollo = dumpMessaggio.getProtocollo();
	}
	
	
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
