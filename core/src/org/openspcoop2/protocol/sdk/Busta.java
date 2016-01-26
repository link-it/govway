/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.protocol.sdk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.tracciamento.Data;
import org.openspcoop2.core.tracciamento.Eccezioni;
import org.openspcoop2.core.tracciamento.ProfiloCollaborazione;
import org.openspcoop2.core.tracciamento.ProfiloTrasmissione;
import org.openspcoop2.core.tracciamento.Proprieta;
import org.openspcoop2.core.tracciamento.Protocollo;
import org.openspcoop2.core.tracciamento.Riscontri;
import org.openspcoop2.core.tracciamento.Soggetto;
import org.openspcoop2.core.tracciamento.SoggettoIdentificativo;
import org.openspcoop2.core.tracciamento.TipoData;
import org.openspcoop2.core.tracciamento.Trasmissioni;
import org.openspcoop2.core.tracciamento.constants.TipoInoltro;
import org.openspcoop2.core.tracciamento.constants.TipoProfiloCollaborazione;
import org.openspcoop2.core.tracciamento.constants.TipoTempo;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.utils.date.DateManager;

/**
 * Classe utilizzata per rappresentare una Busta.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Busta implements java.io.Serializable { 

	private static final long serialVersionUID = 1L;

	
	// busta
    private org.openspcoop2.core.tracciamento.Busta busta;
	
 
	// servizio [info-richiedente]
	protected String tipoServizioRichiedenteBustaDiServizio;
	protected String servizioRichiedenteBustaDiServizio;

	// azione [info-richiedente]
	protected String azioneRichiedenteBustaDiServizio;
	
	// identificativi [info-richiedente]
	protected String riferimentoMsgBustaRichiedenteServizio;
	
	// ListaEccezioni
	protected List<Eccezione> listaEccezioni = new ArrayList<Eccezione>();
	
	// ListaRiscontri
	protected List<Riscontro> listaRiscontri = new ArrayList<Riscontro>();
	
	// ListaTrasmissioni
	protected List<Trasmissione> listaTrasmissioni = new ArrayList<Trasmissione>();
	
	public Busta(String protocollo){
		
		this.busta = new org.openspcoop2.core.tracciamento.Busta();
		
		this.setSequenza(-1);
		this.setProtocollo(protocollo);
	}

	public Busta(IProtocolFactory protocolFactory,Servizio infoServizio, IDSoggetto mittente, IDSoggetto destinatario, String id, boolean generateListaTrasmissione) throws ProtocolException {
		
		this.busta = new org.openspcoop2.core.tracciamento.Busta();
		
		this.setSequenza(-1);
		
		if(infoServizio!=null){
			this.setProfiloDiCollaborazione(infoServizio.getProfiloDiCollaborazione());
			if(infoServizio.getProfiloDiCollaborazione()!=null){
				this.setProfiloDiCollaborazioneValue(protocolFactory.createTraduttore().toString(infoServizio.getProfiloDiCollaborazione()));
			}
			this.setConfermaRicezione(infoServizio.getConfermaRicezione());
			this.setScadenza(infoServizio.getScadenza());
			if(infoServizio.getIDServizio()!=null){
				this.setTipoServizio(infoServizio.getIDServizio().getTipoServizio());
				this.setServizio(infoServizio.getIDServizio().getServizio());
				this.setAzione(infoServizio.getIDServizio().getAzione());
			}
			this.setTipoServizioCorrelato(infoServizio.getTipoServizioCorrelato());
			this.setServizioCorrelato(infoServizio.getServizioCorrelato());
			if(infoServizio.getInoltro()!=null){
				this.setInoltro(infoServizio.getInoltro());
				this.setInoltroValue(protocolFactory.createTraduttore().toString(infoServizio.getInoltro()));
			}
		}
		
		if(mittente!=null){
			this.setTipoMittente(mittente.getTipo());
			this.setMittente(mittente.getNome());
			this.setIdentificativoPortaMittente(mittente.getCodicePorta());
		}
		
		if(destinatario!=null){
			this.setTipoDestinatario(destinatario.getTipo());
			this.setDestinatario(destinatario.getNome());
			this.setIdentificativoPortaDestinatario(destinatario.getCodicePorta());
		}
		
		this.setID(id);
		if(protocolFactory!=null){
			this.setProtocollo(protocolFactory.getProtocol());
		}
		
		if(generateListaTrasmissione){
			// aggiunto lista trasmissione
			if(this.sizeListaTrasmissioni()<=0){
				Trasmissione trasmissione = new Trasmissione();
				
				if(this.busta.getMittente()!=null){
					if(this.busta.getMittente().getIdentificativo()!=null){
						trasmissione.setTipoOrigine(this.busta.getMittente().getIdentificativo().getTipo());
						trasmissione.setOrigine(this.busta.getMittente().getIdentificativo().getBase());
					}
					trasmissione.setIdentificativoPortaOrigine(this.busta.getMittente().getIdentificativoPorta());
					trasmissione.setIndirizzoOrigine(this.busta.getMittente().getIndirizzo());
				}
				
				if(this.busta.getDestinatario()!=null){
					if(this.busta.getDestinatario().getIdentificativo()!=null){
						trasmissione.setTipoDestinazione(this.busta.getDestinatario().getIdentificativo().getTipo());
						trasmissione.setDestinazione(this.busta.getDestinatario().getIdentificativo().getBase());
					}
					trasmissione.setIdentificativoPortaDestinazione(this.busta.getDestinatario().getIdentificativoPorta());
					trasmissione.setIndirizzoDestinazione(this.busta.getDestinatario().getIndirizzo());
				}
				
				this.addTrasmissione(trasmissione);
			}
		}
	}
	
	public Busta(org.openspcoop2.core.tracciamento.Busta busta){
		this.busta = busta;
		
    	// eccezioni
    	if(busta.getEccezioni()!=null && busta.getEccezioni().sizeEccezioneList()>0){
    		for (org.openspcoop2.core.tracciamento.Eccezione eccezione : busta.getEccezioni().getEccezioneList()) {
    			this.addEccezione(new Eccezione(eccezione),false);
    		}
    	}
    	
    	// riscontri
    	if(busta.getRiscontri()!=null && busta.getRiscontri().sizeRiscontroList()>0){
    		for (org.openspcoop2.core.tracciamento.Riscontro riscontro : busta.getRiscontri().getRiscontroList()) {
    			this.addRiscontro(new Riscontro(riscontro),false);
    		}
    	}
    	
    	// trasmissioni
    	if(busta.getTrasmissioni()!=null && busta.getTrasmissioni().sizeTrasmissioneList()>0){
    		for (org.openspcoop2.core.tracciamento.Trasmissione trasmissione : busta.getTrasmissioni().getTrasmissioneList()) {
    			this.addTrasmissione(new Trasmissione(trasmissione),false);
    		}
    	}
	}

	

	
	// base
	
	public org.openspcoop2.core.tracciamento.Busta getBusta() {
		return this.busta;
	}
	public void setBusta(org.openspcoop2.core.tracciamento.Busta busta) {
		this.busta = busta;
		
    	// eccezioni
    	if(busta.getEccezioni()!=null && busta.getEccezioni().sizeEccezioneList()>0)
    	for (org.openspcoop2.core.tracciamento.Eccezione eccezione : busta.getEccezioni().getEccezioneList()) {
			this.addEccezione(new Eccezione(eccezione),false);
		}
    	
    	// riscontri
    	if(busta.getRiscontri()!=null && busta.getRiscontri().sizeRiscontroList()>0)
    	for (org.openspcoop2.core.tracciamento.Riscontro riscontro : busta.getRiscontri().getRiscontroList()) {
			this.addRiscontro(new Riscontro(riscontro),false);
		}
    	
    	// trasmissioni
    	if(busta.getTrasmissioni()!=null && busta.getTrasmissioni().sizeTrasmissioneList()>0)
    	for (org.openspcoop2.core.tracciamento.Trasmissione trasmissione : busta.getTrasmissioni().getTrasmissioneList()) {
			this.addTrasmissione(new Trasmissione(trasmissione),false);
		}
	}
	
	
	
	// id  [Wrapper]
	
	public Long getId() {
		return this.busta.getId();
	}
	public void setId(Long id) {
		this.busta.setId(id);
	}
	
	
	
	
	
	// mittente [Wrapper]
		
	public String getMittente() {
		if(this.busta.getMittente()!=null && this.busta.getMittente().getIdentificativo()!=null)
			return this.busta.getMittente().getIdentificativo().getBase();
		else
			return null;
	}
	public void setMittente(String value) {
		if(value!=null){
			if(this.busta.getMittente()==null){
				this.busta.setMittente(new Soggetto());
			}
			if(this.busta.getMittente().getIdentificativo()==null){
				this.busta.getMittente().setIdentificativo(new SoggettoIdentificativo());
			}
			this.busta.getMittente().getIdentificativo().setBase(value);
		}
		else{
			if(this.busta.getMittente()!=null){
				if(this.busta.getMittente().getIdentificativo()!=null){
					if(this.busta.getMittente().getIdentificativo().getTipo()==null){
						this.busta.getMittente().setIdentificativo(null);
					}
					else{
						this.busta.getMittente().getIdentificativo().setBase(null);
					}	
				}
				if(this.busta.getMittente().getIdentificativo()==null && 
						this.busta.getMittente().getIdentificativoPorta()==null &&
						this.busta.getMittente().getIndirizzo()==null){
					this.busta.setMittente(null);
				}
			}
		}
	}
	
	public String getTipoMittente() {
		if(this.busta.getMittente()!=null && this.busta.getMittente().getIdentificativo()!=null)
			return this.busta.getMittente().getIdentificativo().getTipo();
		else
			return null;
	}
	public void setTipoMittente(String value) {
		if(value!=null){
			if(this.busta.getMittente()==null){
				this.busta.setMittente(new Soggetto());
			}
			if(this.busta.getMittente().getIdentificativo()==null){
				this.busta.getMittente().setIdentificativo(new SoggettoIdentificativo());
			}
			this.busta.getMittente().getIdentificativo().setTipo(value);
		}
		else{
			if(this.busta.getMittente()!=null){
				if(this.busta.getMittente().getIdentificativo()!=null){
					if(this.busta.getMittente().getIdentificativo().getBase()==null){
						this.busta.getMittente().setIdentificativo(null);
					}
					else{
						this.busta.getMittente().getIdentificativo().setTipo(null);
					}	
				}
				if(this.busta.getMittente().getIdentificativo()==null && 
						this.busta.getMittente().getIdentificativoPorta()==null &&
						this.busta.getMittente().getIndirizzo()==null){
					this.busta.setMittente(null);
				}
			}
		}
	}

	public String getIdentificativoPortaMittente() {
		if(this.busta.getMittente()!=null)
			return this.busta.getMittente().getIdentificativoPorta();
		else
			return null;
	}

	public void setIdentificativoPortaMittente(String identificativoPortaMittente) {
		if(identificativoPortaMittente!=null){
			if(this.busta.getMittente()==null){
				this.busta.setMittente(new Soggetto());
			}
			this.busta.getMittente().setIdentificativoPorta(identificativoPortaMittente);
		}
		else{
			if(this.busta.getMittente()!=null){
				this.busta.getMittente().setIdentificativoPorta(null);
				if(this.busta.getMittente().getIdentificativo()==null && 
						this.busta.getMittente().getIdentificativoPorta()==null &&
						this.busta.getMittente().getIndirizzo()==null){
					this.busta.setMittente(null);
				}
			}
		}
	}
	
	public String getIndirizzoMittente() {
		if(this.busta.getMittente()!=null)
			return this.busta.getMittente().getIndirizzo();
		else
			return null;
	}
	public void setIndirizzoMittente(String value) {
		if(value!=null){
			if(this.busta.getMittente()==null){
				this.busta.setMittente(new Soggetto());
			}
			this.busta.getMittente().setIndirizzo(value);
		}
		else{
			if(this.busta.getMittente()!=null){
				this.busta.getMittente().setIndirizzo(null);
				if(this.busta.getMittente().getIdentificativo()==null && 
						this.busta.getMittente().getIdentificativoPorta()==null &&
						this.busta.getMittente().getIndirizzo()==null){
					this.busta.setMittente(null);
				}
			}
		}
	}
	
	
	
	
	// destinatario [Wrapper]
	
	public String getDestinatario() {
		if(this.busta.getDestinatario()!=null && this.busta.getDestinatario().getIdentificativo()!=null)
			return this.busta.getDestinatario().getIdentificativo().getBase();
		else
			return null;
	}
	public void setDestinatario(String value) {
		if(value!=null){
			if(this.busta.getDestinatario()==null){
				this.busta.setDestinatario(new Soggetto());
			}
			if(this.busta.getDestinatario().getIdentificativo()==null){
				this.busta.getDestinatario().setIdentificativo(new SoggettoIdentificativo());
			}
			this.busta.getDestinatario().getIdentificativo().setBase(value);
		}
		else{
			if(this.busta.getDestinatario()!=null){
				if(this.busta.getDestinatario().getIdentificativo()!=null){
					if(this.busta.getDestinatario().getIdentificativo().getTipo()==null){
						this.busta.getDestinatario().setIdentificativo(null);
					}
					else{
						this.busta.getDestinatario().getIdentificativo().setBase(null);
					}	
				}
				if(this.busta.getDestinatario().getIdentificativo()==null && 
						this.busta.getDestinatario().getIdentificativoPorta()==null &&
						this.busta.getDestinatario().getIndirizzo()==null){
					this.busta.setDestinatario(null);
				}
			}
		}
	}

	public String getTipoDestinatario() {
		if(this.busta.getDestinatario()!=null && this.busta.getDestinatario().getIdentificativo()!=null)
			return this.busta.getDestinatario().getIdentificativo().getTipo();
		else
			return null;
	}
	public void setTipoDestinatario(String value) {
		if(value!=null){
			if(this.busta.getDestinatario()==null){
				this.busta.setDestinatario(new Soggetto());
			}
			if(this.busta.getDestinatario().getIdentificativo()==null){
				this.busta.getDestinatario().setIdentificativo(new SoggettoIdentificativo());
			}
			this.busta.getDestinatario().getIdentificativo().setTipo(value);
		}else{
			if(this.busta.getDestinatario()!=null){
				if(this.busta.getDestinatario().getIdentificativo()!=null){
					if(this.busta.getDestinatario().getIdentificativo().getBase()==null){
						this.busta.getDestinatario().setIdentificativo(null);
					}
					else{
						this.busta.getDestinatario().getIdentificativo().setTipo(null);
					}	
				}
				if(this.busta.getDestinatario().getIdentificativo()==null && 
						this.busta.getDestinatario().getIdentificativoPorta()==null &&
						this.busta.getDestinatario().getIndirizzo()==null){
					this.busta.setDestinatario(null);
				}
			}
		}
	}
	
	public String getIdentificativoPortaDestinatario() {
		if(this.busta.getDestinatario()!=null)
			return this.busta.getDestinatario().getIdentificativoPorta();
		else
			return null;
	}

	public void setIdentificativoPortaDestinatario(
			String identificativoPortaDestinatario) {
		if(identificativoPortaDestinatario!=null){
			if(this.busta.getDestinatario()==null){
				this.busta.setDestinatario(new Soggetto());
			}
			this.busta.getDestinatario().setIdentificativoPorta(identificativoPortaDestinatario);
		}
		else{
			if(this.busta.getDestinatario()!=null){
				this.busta.getDestinatario().setIdentificativoPorta(null);
				if(this.busta.getDestinatario().getIdentificativo()==null && 
						this.busta.getDestinatario().getIdentificativoPorta()==null &&
						this.busta.getDestinatario().getIndirizzo()==null){
					this.busta.setDestinatario(null);
				}
			}
		}
	}
	
	public String getIndirizzoDestinatario() {
		if(this.busta.getDestinatario()!=null)
			return this.busta.getDestinatario().getIndirizzo();
		else
			return null;
	}
	public void setIndirizzoDestinatario(String value) {
		if(value!=null){
			if(this.busta.getDestinatario()==null){
				this.busta.setDestinatario(new Soggetto());
			}
			this.busta.getDestinatario().setIndirizzo(value);
		}else{
			if(this.busta.getDestinatario()!=null){
				this.busta.getDestinatario().setIndirizzo(null);
				if(this.busta.getDestinatario().getIdentificativo()==null && 
						this.busta.getDestinatario().getIdentificativoPorta()==null &&
						this.busta.getDestinatario().getIndirizzo()==null){
					this.busta.setDestinatario(null);
				}
			}
		}
	}

	
	
	// profilo di collaborazione [Wrapper]
	
	public ProfiloDiCollaborazione getProfiloDiCollaborazione() {
		if(this.busta.getProfiloCollaborazione()!=null && this.busta.getProfiloCollaborazione().getTipo()!=null){
			switch (this.busta.getProfiloCollaborazione().getTipo()) {
			case ONEWAY:
				return ProfiloDiCollaborazione.ONEWAY;
			case SINCRONO:
				return ProfiloDiCollaborazione.SINCRONO;
			case ASINCRONO_ASIMMETRICO:
				return ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO;
			case ASINCRONO_SIMMETRICO:
				return ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO;
			case SCONOSCIUTO:
				return ProfiloDiCollaborazione.UNKNOWN;
			}
		}
		return null;
	}
	public void setProfiloDiCollaborazione(ProfiloDiCollaborazione value) {
		if(value!=null){
			if(this.busta.getProfiloCollaborazione()==null){
				this.busta.setProfiloCollaborazione(new ProfiloCollaborazione());
			}
			switch (value) {
			case ONEWAY:
				this.busta.getProfiloCollaborazione().setTipo(TipoProfiloCollaborazione.ONEWAY);
				break;
			case SINCRONO:
				this.busta.getProfiloCollaborazione().setTipo(TipoProfiloCollaborazione.SINCRONO);
				break;
			case ASINCRONO_ASIMMETRICO:
				this.busta.getProfiloCollaborazione().setTipo(TipoProfiloCollaborazione.ASINCRONO_ASIMMETRICO);
				break;
			case ASINCRONO_SIMMETRICO:
				this.busta.getProfiloCollaborazione().setTipo(TipoProfiloCollaborazione.ASINCRONO_SIMMETRICO);
				break;
			case UNKNOWN:
				this.busta.getProfiloCollaborazione().setTipo(TipoProfiloCollaborazione.SCONOSCIUTO);
				break;
			}
		}
		else{
			if(this.busta.getProfiloCollaborazione()!=null){
				if(this.busta.getProfiloCollaborazione().getBase()==null){
					this.busta.setProfiloCollaborazione(null);
				}
				else{
					this.busta.getProfiloCollaborazione().setTipo(null);
				}
			}
		}
		
	}
	
	public void setProfiloDiCollaborazione(ProfiloDiCollaborazione profiloDiCollaborazione, String value) {
		this.setProfiloDiCollaborazione(profiloDiCollaborazione);
		this.setProfiloDiCollaborazioneValue(value);
	}
	
	public String getProfiloDiCollaborazioneValue() {
		if(this.busta.getProfiloCollaborazione()!=null){
			return this.busta.getProfiloCollaborazione().getBase();
		}
		return null;
	}
	public void setProfiloDiCollaborazioneValue(String profiloDiCollaborazioneValue) {
		if(profiloDiCollaborazioneValue!=null){
			if(this.busta.getProfiloCollaborazione()==null){
				this.busta.setProfiloCollaborazione(new ProfiloCollaborazione());
			}
			this.busta.getProfiloCollaborazione().setBase(profiloDiCollaborazioneValue);
		}else{
			if(this.busta.getProfiloCollaborazione()!=null){
				if(this.busta.getProfiloCollaborazione().getTipo()==null){
					this.busta.setProfiloCollaborazione(null);
				}
				else{
					this.busta.getProfiloCollaborazione().setBase(null);
				}
			}
		}
	}
	
	public String getServizioCorrelato() {
		if(this.busta.getServizioCorrelato()!=null){
			return this.busta.getServizioCorrelato().getBase();
		}
		return null;
	}
	public void setServizioCorrelato(String value) {
		if(value!=null){
			if(this.busta.getServizioCorrelato()==null){
				this.busta.setServizioCorrelato(new org.openspcoop2.core.tracciamento.Servizio());
			}
			this.busta.getServizioCorrelato().setBase(value);
		}else{
			if(this.busta.getServizioCorrelato()!=null){
				this.busta.getServizioCorrelato().setBase(null);
				if(this.busta.getServizioCorrelato().getTipo()==null &&
						(this.busta.getServizioCorrelato().getVersione()==null || this.busta.getServizioCorrelato().getVersione()==1)){
					this.busta.setServizioCorrelato(null);
				}
			}
		}
	}
	
	public String getTipoServizioCorrelato() {
		if(this.busta.getServizioCorrelato()!=null){
			return this.busta.getServizioCorrelato().getTipo();
		}
		return null;
	}
	public void setTipoServizioCorrelato(String value) {
		if(value!=null){
			if(this.busta.getServizioCorrelato()==null){
				this.busta.setServizioCorrelato(new org.openspcoop2.core.tracciamento.Servizio());
			}
			this.busta.getServizioCorrelato().setTipo(value);
		}else{
			if(this.busta.getServizioCorrelato()!=null){
				this.busta.getServizioCorrelato().setTipo(null);
				if(this.busta.getServizioCorrelato().getBase()==null &&
						(this.busta.getServizioCorrelato().getVersione()==null || this.busta.getServizioCorrelato().getVersione()==1)){
					this.busta.setServizioCorrelato(null);
				}
			}
		}
	}
	

	
	
	// collaborazione [Wrapper]
	
	public String getCollaborazione() {
		return this.busta.getCollaborazione();
	}
	public void setCollaborazione(String value) {
		this.busta.setCollaborazione(value);
	}
	
	
		
	// servizio
	
	public String getServizio() {
		if(this.busta.getServizio()!=null){
			return this.busta.getServizio().getBase();
		}
		return null;
	}
	public void setServizio(String value) {
		if(value!=null){
			if(this.busta.getServizio()==null){
				this.busta.setServizio(new org.openspcoop2.core.tracciamento.Servizio());
			}
			this.busta.getServizio().setBase(value);
		}else{
			if(this.busta.getServizio()!=null){
				this.busta.getServizio().setBase(null);
				if(this.busta.getServizioCorrelato().getTipo()==null &&
						(this.busta.getServizioCorrelato().getVersione()==null || this.busta.getServizioCorrelato().getVersione()==1)){
					this.busta.setServizioCorrelato(null);
				}
			}
		}
	}

	public String getTipoServizio() {
		if(this.busta.getServizio()!=null){
			return this.busta.getServizio().getTipo();
		}
		return null;
	}
	public void setTipoServizio(String value) {
		if(value!=null){
			if(this.busta.getServizio()==null){
				this.busta.setServizio(new org.openspcoop2.core.tracciamento.Servizio());
			}
			this.busta.getServizio().setTipo(value);
		}else{
			if(this.busta.getServizio()!=null){
				this.busta.getServizio().setTipo(null);
				if(this.busta.getServizioCorrelato().getBase()==null &&
						(this.busta.getServizioCorrelato().getVersione()==null || this.busta.getServizioCorrelato().getVersione()==1)){
					this.busta.setServizioCorrelato(null);
				}
			}
		}
	}

	public int getVersioneServizio() {
		if(this.busta.getServizio()!=null && this.busta.getServizio().getVersione()!=null){
			return this.busta.getServizio().getVersione();
		}
		return 1;
	}
	public void setVersioneServizio(int versioneServizio) {
		if(this.busta.getServizio()==null){
			this.busta.setServizio(new org.openspcoop2.core.tracciamento.Servizio());
		}
		this.busta.getServizio().setVersione(versioneServizio);
	}
	public void setVersioneServizio(String versioneServizio) {
		if(versioneServizio==null){
			return;
		}
		this.setVersioneServizio(Integer.parseInt(versioneServizio));
	}
	
	
	
	// servizio [info-richiedente]
	
	public String getTipoServizioRichiedenteBustaDiServizio() {
		return this.tipoServizioRichiedenteBustaDiServizio;
	}
	public void setTipoServizioRichiedenteBustaDiServizio(String value) {
		this.tipoServizioRichiedenteBustaDiServizio = value;
	}
	
	public String getServizioRichiedenteBustaDiServizio() {
		return this.servizioRichiedenteBustaDiServizio;
	}
	public void setServizioRichiedenteBustaDiServizio(String value) {
		this.servizioRichiedenteBustaDiServizio = value;
	}
	
	
	
	
	// azione [wrapped] 
		
	public String getAzione() {
		return this.busta.getAzione();
	}
	public void setAzione(String value) {
		this.busta.setAzione(value);
	}
	
	
	
	// azione [info-richiedente]
	
	public String getAzioneRichiedenteBustaDiServizio() {
		return this.azioneRichiedenteBustaDiServizio;
	}
	public void setAzioneRichiedenteBustaDiServizio(String value) {
		this.azioneRichiedenteBustaDiServizio = value;
	}
	
	
	
	
	// identificativi [wrapped]
	
	public String getID() {
		return this.busta.getIdentificativo();
	}
	public void setID(String value) {
		this.busta.setIdentificativo(value);
	}
	
	public String getRiferimentoMessaggio() {
		return this.busta.getRiferimentoMessaggio();
	}
	public void setRiferimentoMessaggio(String value) {
		this.busta.setRiferimentoMessaggio(value);
	}

	
	
	// identificativi [info-richiedente]
	
	public String getRiferimentoMsgBustaRichiedenteServizio() {
		return this.riferimentoMsgBustaRichiedenteServizio;
	}
	public void setRiferimentoMsgBustaRichiedenteServizio(String value) {
		this.riferimentoMsgBustaRichiedenteServizio = value;
	}
	

	
	// date [wrapped]
	
	public TipoOraRegistrazione getTipoOraRegistrazione() {
		if(this.busta.getOraRegistrazione()!=null && 
				this.busta.getOraRegistrazione().getSorgente()!=null &&
				this.busta.getOraRegistrazione().getSorgente().getTipo()!=null){
			switch (this.busta.getOraRegistrazione().getSorgente().getTipo()) {
			case LOCALE:
				return TipoOraRegistrazione.LOCALE;
			case SINCRONIZZATO:
				return TipoOraRegistrazione.SINCRONIZZATO;
			case SCONOSCIUTO:
				return TipoOraRegistrazione.UNKNOWN;
			}
		}
		return null;
	}
	protected void setTipoOraRegistrazione(TipoOraRegistrazione tipoOraRegistrazione) {
		if(tipoOraRegistrazione!=null){
			if(this.busta.getOraRegistrazione()==null){
				this.busta.setOraRegistrazione(new Data());
			}
			if(this.busta.getOraRegistrazione().getSorgente()==null){
				this.busta.getOraRegistrazione().setSorgente(new TipoData());
			}
			switch (tipoOraRegistrazione) {
			case LOCALE:
				this.busta.getOraRegistrazione().getSorgente().setTipo(TipoTempo.LOCALE);
				break;
			case SINCRONIZZATO:
				this.busta.getOraRegistrazione().getSorgente().setTipo(TipoTempo.SINCRONIZZATO);
				break;
			case UNKNOWN:
				this.busta.getOraRegistrazione().getSorgente().setTipo(TipoTempo.SCONOSCIUTO);
				break;
			}
		}
		else {
			if(this.busta.getOraRegistrazione()!=null){
				if(this.busta.getOraRegistrazione().getSorgente()!=null){
					if(this.busta.getOraRegistrazione().getSorgente().getBase()==null){
						this.busta.getOraRegistrazione().setSorgente(null);
					}
					else{
						this.busta.getOraRegistrazione().getSorgente().setTipo(null);
					}	
				}
				if(this.busta.getOraRegistrazione().getSorgente()==null && this.busta.getOraRegistrazione().getDateTime()==null){
					this.busta.setOraRegistrazione(null);
				}
			}
		}
	}
	
	public void setTipoOraRegistrazione(TipoOraRegistrazione tipo, String value) {
		this.setTipoOraRegistrazione(tipo);
		this.setTipoOraRegistrazioneValue(value);
	}

	public String getTipoOraRegistrazioneValue() {
		if(this.busta.getOraRegistrazione()!=null && 
				this.busta.getOraRegistrazione().getSorgente()!=null){
			return this.busta.getOraRegistrazione().getSorgente().getBase();
		}
		return null;
	}
	public void setTipoOraRegistrazioneValue(String tipoOraRegistrazioneValue) {
		if(tipoOraRegistrazioneValue!=null){
			if(this.busta.getOraRegistrazione()==null){
				this.busta.setOraRegistrazione(new Data());
			}
			if(this.busta.getOraRegistrazione().getSorgente()==null){
				this.busta.getOraRegistrazione().setSorgente(new TipoData());
			}
			this.busta.getOraRegistrazione().getSorgente().setBase(tipoOraRegistrazioneValue);
		}
		else {
			if(this.busta.getOraRegistrazione()!=null){
				if(this.busta.getOraRegistrazione().getSorgente()!=null){
					if(this.busta.getOraRegistrazione().getSorgente().getTipo()==null){
						this.busta.getOraRegistrazione().setSorgente(null);
					}
					else{
						this.busta.getOraRegistrazione().getSorgente().setBase(null);
					}	
				}
				if(this.busta.getOraRegistrazione().getSorgente()==null && this.busta.getOraRegistrazione().getDateTime()==null){
					this.busta.setOraRegistrazione(null);
				}
			}
		}
	}
		
	public Date getOraRegistrazione() {
		if(this.busta.getOraRegistrazione()!=null){
			return this.busta.getOraRegistrazione().getDateTime();
		}
		return null;
	}
	public void setOraRegistrazione(Date value) {
		if(value!=null){
			if(this.busta.getOraRegistrazione()==null){
				this.busta.setOraRegistrazione(new Data());
			}
			this.busta.getOraRegistrazione().setDateTime(value);
		}
		else {
			if(this.busta.getOraRegistrazione()!=null){
				if(this.busta.getOraRegistrazione().getSorgente()==null){
					this.busta.setOraRegistrazione(null);
				}
				else{
					this.busta.getOraRegistrazione().setDateTime(null);
				}
			}
		}
	}

	public Date getScadenza() {
		return this.busta.getScadenza();
	}
	public void setScadenza(Date value) {
		this.busta.setScadenza(value);
	}
	
		
	
	
	
	// profilo di trasmissione [wrapped]

	public boolean isConfermaRicezione() {
		if(this.busta.getProfiloTrasmissione()!=null){
			return this.busta.getProfiloTrasmissione().getConfermaRicezione();
		}
		return false;
	}
	public void setConfermaRicezione(boolean value) {
		if(this.busta.getProfiloTrasmissione()==null){
			this.busta.setProfiloTrasmissione(new ProfiloTrasmissione());
		}
		this.busta.getProfiloTrasmissione().setConfermaRicezione(value);
	}

	public Inoltro getInoltro() {
		if(this.busta.getProfiloTrasmissione()!=null && 
				this.busta.getProfiloTrasmissione().getInoltro()!=null &&
				this.busta.getProfiloTrasmissione().getInoltro().getTipo()!=null){
			switch (this.busta.getProfiloTrasmissione().getInoltro().getTipo()) {
			case INOLTRO_CON_DUPLICATI:
				return Inoltro.CON_DUPLICATI;
			case INOLTRO_SENZA_DUPLICATI:
				return Inoltro.SENZA_DUPLICATI;
			case SCONOSCIUTO:
				return Inoltro.UNKNOWN;
			}
		}
		return null;
	}
	protected void setInoltro(Inoltro inoltro) {
		if(inoltro!=null){
			if(this.busta.getProfiloTrasmissione()==null){
				this.busta.setProfiloTrasmissione(new ProfiloTrasmissione());
			}
			if(this.busta.getProfiloTrasmissione().getInoltro()==null){
				this.busta.getProfiloTrasmissione().setInoltro(new org.openspcoop2.core.tracciamento.Inoltro());
			}
			switch (inoltro) {
			case CON_DUPLICATI:
				this.busta.getProfiloTrasmissione().getInoltro().setTipo(TipoInoltro.INOLTRO_CON_DUPLICATI);
				break;
			case SENZA_DUPLICATI:
				this.busta.getProfiloTrasmissione().getInoltro().setTipo(TipoInoltro.INOLTRO_SENZA_DUPLICATI);
				break;
			case UNKNOWN:
				this.busta.getProfiloTrasmissione().getInoltro().setTipo(TipoInoltro.SCONOSCIUTO);
				break;
			}
		}
		else{
			if(this.busta.getProfiloTrasmissione()!=null){
				if(this.busta.getProfiloTrasmissione().getInoltro()!=null){
					if(this.getBusta().getProfiloTrasmissione().getInoltro().getBase()!=null){
						this.busta.getProfiloTrasmissione().getInoltro().setTipo(null);
					}
					else{
						this.busta.getProfiloTrasmissione().setInoltro(null);
					}		
				}
			}
		}
	}
	
	public void setInoltro(Inoltro inoltro, String value) {
		this.setInoltro(inoltro);
		this.setInoltroValue(value);
	}
	
	public String getInoltroValue() {
		if(this.busta.getProfiloTrasmissione()!=null && 
				this.busta.getProfiloTrasmissione().getInoltro()!=null){
			return this.busta.getProfiloTrasmissione().getInoltro().getBase();
		}
		return null;
	}
	public void setInoltroValue(String inoltroValue) {
		if(inoltroValue!=null){
			if(this.busta.getProfiloTrasmissione()==null){
				this.busta.setProfiloTrasmissione(new ProfiloTrasmissione());
			}
			if(this.busta.getProfiloTrasmissione().getInoltro()==null){
				this.busta.getProfiloTrasmissione().setInoltro(new org.openspcoop2.core.tracciamento.Inoltro());
			}
			this.busta.getProfiloTrasmissione().getInoltro().setBase(inoltroValue);
		}
		else{
			if(this.busta.getProfiloTrasmissione()!=null){
				if(this.busta.getProfiloTrasmissione().getInoltro()!=null){
					if(this.getBusta().getProfiloTrasmissione().getInoltro().getTipo()!=null){
						this.busta.getProfiloTrasmissione().getInoltro().setBase(null);
					}
					else{
						this.busta.getProfiloTrasmissione().setInoltro(null);
					}		
				}
			}
		}
	}
	
	
	
	
	// sequenza [wrapped]
	
	public long getSequenza() {
		if(this.busta.getProfiloTrasmissione()!=null &&
				this.busta.getProfiloTrasmissione().getSequenza()!=null){
			return this.busta.getProfiloTrasmissione().getSequenza();
		}
		return -1;
	}
	public void setSequenza(long value) {
		if(value>=0){
			if(this.busta.getProfiloTrasmissione()==null){
				this.busta.setProfiloTrasmissione(new ProfiloTrasmissione());
			}
			this.busta.getProfiloTrasmissione().setSequenza(new Long(value).intValue());
		}
		else{
			if(this.busta.getProfiloTrasmissione()!=null){
				this.busta.getProfiloTrasmissione().setSequenza(null);
			}
		}
	}




	
	// servizi applicativi
	
	public String getServizioApplicativoFruitore() {
		return this.busta.getServizioApplicativoFruitore();
	}
	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.busta.setServizioApplicativoFruitore(servizioApplicativoFruitore);
	}

	public String getServizioApplicativoErogatore() {
		return this.busta.getServizioApplicativoErogatore();
	}
	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		this.busta.setServizioApplicativoErogatore(servizioApplicativoErogatore);
	}
	

	
	
	// protocollo [wrapped]
	
	public String getProtocollo() {
		if(this.busta.getProtocollo()!=null){
			return this.busta.getProtocollo().getIdentificativo();
		}
		return null;
	}
	public void setProtocollo(String protocollo) {
		if(protocollo!=null){
			if(this.busta.getProtocollo()==null){
				this.busta.setProtocollo(new Protocollo());
			}
			this.busta.getProtocollo().setIdentificativo(protocollo);
		}
		else{
			if(this.busta.getProtocollo()!=null){
				if(this.busta.getProtocollo().sizeProprietaList()<=0){
					this.busta.setProtocollo(null);
				}
				else{
					this.busta.getProtocollo().setIdentificativo(null);
				}
			}
		}
	}


	
	
	// digest [wrapped]
	
	public String getDigest() {
		return this.busta.getDigest();
	}
	public void setDigest(String digest) {
		this.busta.setDigest(digest);
	}
	

		
	// properties [wrapped]
	
	public void addProperty(String key,String value){
		// Per evitare nullPointer durante la serializzazione
		// Non deve essere inserito nemmeno il valore ""
		if(value!=null && !"".equals(value)){
			if(this.busta.getProtocollo()==null){
				this.busta.setProtocollo(new Protocollo());
			}
			Proprieta proprieta = new Proprieta();
			proprieta.setNome(key);
			proprieta.setValore(value);
			this.removeProperty(key); // per evitare doppioni
			this.busta.getProtocollo().addProprieta(proprieta);
		}
	}

	public int sizeProperties(){
		if(this.busta.getProtocollo()!=null){
			return this.busta.getProtocollo().sizeProprietaList();
		}
		return 0;
	}

	public String getProperty(String key){
		if(this.busta.getProtocollo()!=null){
			for (int i = 0; i < this.busta.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.busta.getProtocollo().getProprieta(i);
				if(proprieta.getNome().equals(key)){
					return proprieta.getValore();
				}
			}
		}
		return null;
	}

	public String removeProperty(String key){
		if(this.busta.getProtocollo()!=null){
			for (int i = 0; i < this.busta.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.busta.getProtocollo().getProprieta(i);
				if(proprieta.getNome().equals(key)){
					this.busta.getProtocollo().removeProprieta(i);
					return proprieta.getValore();
				}
			}
		}
		return null;
	}

	public String[] getPropertiesValues() {
		List<String> propertiesValues = new ArrayList<String>();
		if(this.busta.getProtocollo()!=null){
			for (int i = 0; i < this.busta.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.busta.getProtocollo().getProprieta(i);
				propertiesValues.add(proprieta.getValore());
			}
		}
		if(propertiesValues.size()>0){
			return propertiesValues.toArray(new String[1]);
		}
		else{
			return null;
		}
	}

	public String[] getPropertiesNames() {
		List<String> propertiesValues = new ArrayList<String>();
		if(this.busta.getProtocollo()!=null){
			for (int i = 0; i < this.busta.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.busta.getProtocollo().getProprieta(i);
				propertiesValues.add(proprieta.getNome());
			}
		}
		if(propertiesValues.size()>0){
			return propertiesValues.toArray(new String[1]);
		}
		else{
			return null;
		}
	}

	// Non devono essere usati.
	// Altrimenti poi se viene effettuato una add o remove sulla lista o hashtable ritornata, la modifica non ha effetto
//	public void setProperties(Hashtable<String, String> params) {
//		Enumeration<String> keys = params.keys();
//		while (keys.hasMoreElements()) {
//			String key = (String) keys.nextElement();
//			this.addProperty(key, params.get(key));
//		}
//	}
//
//	public Hashtable<String, String> getProperties() {
//		Hashtable<String, String> map = new Hashtable<String, String>();
//		if(this.busta.getProtocollo()!=null){
//			for (int i = 0; i < this.busta.getProtocollo().sizeProprietaList(); i++) {
//				Proprieta proprieta = this.busta.getProtocollo().getProprieta(i);
//				map.put(proprieta.getNome(), proprieta.getValore());
//			}
//		}
//		return map;
//	}
//	
//	public List<Map.Entry<String, String>> getPropertiesAsList(){
//		List <Map.Entry<String, String>> toRet = new ArrayList<Map.Entry<String, String>>();
//		toRet.addAll(this.getProperties().entrySet());
//		return toRet;
//	}





	
	// ListaEccezioni
		
	public List<Eccezione> getListaEccezioni() {
		return this.listaEccezioni;
	}

	public int sizeListaEccezioni() {
		return this.listaEccezioni.size();
	}

	public List<Eccezione> cloneListaEccezioni() {
		if(this.listaEccezioni!=null){
			List<Eccezione> eccs = new ArrayList<Eccezione>();
			for(int i=0; i<this.sizeListaEccezioni(); i++){
				eccs.add(this.getEccezione(i).clone());
			}
			return eccs;
		}else{
			return null;
		}
	}

	public Eccezione getEccezione(int index) {
		return this.listaEccezioni.get( index );
	}

	public void addEccezione(Eccezione e) {
		this.addEccezione(e, true);
	}
	private void addEccezione(Eccezione e, boolean addCore) {
		this.listaEccezioni.add(e);
		if(addCore){
			if(this.busta.getEccezioni()==null){
				this.busta.setEccezioni(new Eccezioni());
			}
			this.busta.getEccezioni().addEccezione(e.getEccezione());
		}
	}

	public Eccezione removeEccezione(int index) {
		this.busta.getEccezioni().removeEccezione(index);
		return this.listaEccezioni.remove(index);
	}

	public String toStringListaEccezioni(IProtocolFactory protocolFactory) throws ProtocolException{
		if(this.sizeListaEccezioni()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<this.sizeListaEccezioni(); i++){
				if(i>0)
					bf.append("\n");
				bf.append(this.getEccezione(i).toString(protocolFactory));
			}
			return bf.toString();
		}else{
			return null;
		}
	}

	public String toStringListaEccezioni_erroriNonGravi(IProtocolFactory protocolFactory) throws ProtocolException{
		if(this.sizeListaEccezioni()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<this.sizeListaEccezioni(); i++){
				if(LivelloRilevanza.isEccezioneLivelloGrave(this.getEccezione(i).getRilevanza()) == false){
					if(i>0)
						bf.append("\n");
					bf.append(this.getEccezione(i).toString(protocolFactory));
				}
			}
			return bf.toString();
		}else{
			return null;
		}
	}

	public String toStringListaEccezioni_erroriGravi(IProtocolFactory protocolFactory) throws ProtocolException{
		if(this.sizeListaEccezioni()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<this.sizeListaEccezioni(); i++){
				if(LivelloRilevanza.isEccezioneLivelloGrave(this.getEccezione(i).getRilevanza())){
					if(i>0)
						bf.append("\n");
					bf.append(this.getEccezione(i).toString(protocolFactory));
				}
			}
			return bf.toString();
		}else{
			return null;
		}
	}

	public boolean containsEccezioniGravi(){
		for(int i=0; i<this.sizeListaEccezioni(); i++){
			if(LivelloRilevanza.isEccezioneLivelloGrave(this.getEccezione(i).getRilevanza())){
				return true;
			}
		}
		return false;
	}

	public static String toStringListaEccezioni(java.util.Vector<Eccezione> errors, IProtocolFactory protocolFactory) throws ProtocolException{
		if(errors.size()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<errors.size(); i++){
				if(i>0)
					bf.append("\n");
				bf.append(errors.get(i).toString(protocolFactory));
			}
			return bf.toString();
		}else{
			return null;
		}
	}

	public static String toStringListaEccezioni_erroriNonGravi(java.util.Vector<Eccezione> errors, IProtocolFactory protocolFactory) throws ProtocolException{
		if(errors.size()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<errors.size(); i++){
				if(LivelloRilevanza.isEccezioneLivelloGrave(errors.get(i).getRilevanza()) == false){
					if(i>0)
						bf.append("\n");
					bf.append(errors.get(i).toString(protocolFactory));
				}
			}
			return bf.toString();
		}else{
			return null;
		}
	}

	public static String toStringListaEccezioni_erroriGravi(java.util.Vector<Eccezione> errors, IProtocolFactory protocolFactory) throws ProtocolException{
		if(errors.size()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<errors.size(); i++){
				if(LivelloRilevanza.isEccezioneLivelloGrave(errors.get(i).getRilevanza())){
					if(i>0)
						bf.append("\n");
					bf.append(errors.get(i).toString(protocolFactory));
				}
			}
			return bf.toString();
		}else{
			return null;
		}
	}

	public static boolean containsEccezioniGravi(java.util.Vector<Eccezione> errors){
		for(int i=0; i<errors.size(); i++){
			if(LivelloRilevanza.isEccezioneLivelloGrave(errors.get(i).getRilevanza())){
				return true;
			}
		}
		return false;

	}
	
	
	
	
	
	// ListaRiscontri

	public List<Riscontro> getListaRiscontri() {
		if (this.listaRiscontri == null) {
			this.listaRiscontri = new ArrayList<Riscontro>();
		}
		return this.listaRiscontri;
	}
	
	public int sizeListaRiscontri() {
		return this.listaRiscontri.size();
	}	

	public Riscontro getRiscontro(int index) {
		return this.listaRiscontri.get( index );
	}
	
	public void addRiscontro(Riscontro r) {
		this.addRiscontro(r, true);
	}
	private void addRiscontro(Riscontro r, boolean addCore) {
		this.listaRiscontri.add(r);
		if(addCore){
			if(this.busta.getRiscontri()==null){
				this.busta.setRiscontri(new Riscontri());
			}
			this.busta.getRiscontri().addRiscontro(r.getRiscontro());
		}
	}
	
	public Riscontro removeRiscontro(int index) {
		this.busta.getRiscontri().removeRiscontro(index);
		return this.listaRiscontri.remove(index);
	}
	
	
	
	
	// ListaTrasmissioni
	
	public List<Trasmissione> getListaTrasmissioni() {
		if (this.listaTrasmissioni == null) {
			this.listaTrasmissioni = new ArrayList<Trasmissione>();
		}
		return this.listaTrasmissioni;
	}
	
	public int sizeListaTrasmissioni() {
		return this.listaTrasmissioni.size();
	}
	
	public Trasmissione getTrasmissione(int index) {
		return this.listaTrasmissioni.get( index );
	}

	public void addTrasmissione(Trasmissione t) {
		this.addTrasmissione(t, true);
	}
	private void addTrasmissione(Trasmissione t, boolean addCore) {
		this.listaTrasmissioni.add(t);
		if(addCore){
			if(this.busta.getTrasmissioni()==null){
				this.busta.setTrasmissioni(new Trasmissioni());
			}
			this.busta.getTrasmissioni().addTrasmissione(t.getTrasmissione());
		}
	}

	public Trasmissione removeTrasmissione(int index) {
		this.busta.getTrasmissioni().removeTrasmissione(index);
		return this.listaTrasmissioni.remove(index);
	}














	/**
	 * Data una busta, genera la corrispondente busta di risposta
	 * Inverte mittente con destinatario.
	 * Setta una nuova ora di registrazione
	 *
	 * @return una busta utilizzabile come risposta
	 * 
	 */
	public Busta invertiBusta(TipoOraRegistrazione tipoOraRegistrazione, String tipoTempo){
		Busta bustaHTTPReply = null;
		bustaHTTPReply = new Busta(this.getProtocollo());

		bustaHTTPReply.setTipoDestinatario(this.getTipoMittente());
		bustaHTTPReply.setDestinatario(this.getMittente());
		bustaHTTPReply.setIdentificativoPortaDestinatario(this.getIdentificativoPortaMittente());
		bustaHTTPReply.setIndirizzoDestinatario(this.getIndirizzoMittente());

		bustaHTTPReply.setTipoMittente(this.getTipoDestinatario());
		bustaHTTPReply.setMittente(this.getDestinatario());
		bustaHTTPReply.setIndirizzoMittente(this.getIndirizzoDestinatario());
		bustaHTTPReply.setIdentificativoPortaMittente(this.getIdentificativoPortaDestinatario());

		bustaHTTPReply.setServizioApplicativoFruitore(this.getServizioApplicativoFruitore()); // lo mantengo, non deve essere invertito

		Date oraRegistrazione = DateManager.getDate();
		bustaHTTPReply.setOraRegistrazione(oraRegistrazione);
		bustaHTTPReply.setTipoOraRegistrazione(tipoOraRegistrazione,tipoTempo);
		return bustaHTTPReply;
	}

	/**
	 * Data una busta, ne ritorna una copia
	 *
	 * @return una busta
	 * 
	 */
	@Override 
	public Busta clone(){
		
		// Non uso il base clone per far si che venga usato il costruttore new String()
		
		Busta clone = null;
		if(this.getProtocollo()!=null){
			clone = new Busta(new String(this.getProtocollo()));
		}else{
			String pNull = null;
			clone = new Busta(pNull);
		}

		// id 
		clone.setId(this.getId()!=null ? new Long(this.getId()) : null);
		
		// mittente
		clone.setTipoMittente(this.getTipoMittente()!=null ? new String(this.getTipoMittente()) : null);
		clone.setIndirizzoMittente(this.getIndirizzoMittente()!=null ? new String(this.getIndirizzoMittente()) : null);
		clone.setMittente(this.getMittente()!=null ? new String(this.getMittente()) : null);
		clone.setIdentificativoPortaMittente(this.getIdentificativoPortaMittente()!=null ? new String(this.getIdentificativoPortaMittente()) : null);

		// destinatario
		clone.setTipoDestinatario(this.getTipoDestinatario()!=null ? new String(this.getTipoDestinatario()) : null);
		clone.setIndirizzoDestinatario(this.getIndirizzoDestinatario()!=null ? new String(this.getIndirizzoDestinatario()) : null);
		clone.setDestinatario(this.getDestinatario()!=null ? new String(this.getDestinatario()) : null);
		clone.setIdentificativoPortaDestinatario(this.getIdentificativoPortaDestinatario()!=null ? new String(this.getIdentificativoPortaDestinatario()) : null);
		
		// profilo di collaborazione
		clone.setProfiloDiCollaborazione(this.getProfiloDiCollaborazione());
		clone.setProfiloDiCollaborazioneValue(this.getProfiloDiCollaborazioneValue()!=null ? new String(this.getProfiloDiCollaborazioneValue()) : null);
		clone.setServizioCorrelato(this.getServizioCorrelato()!=null ? new String(this.getServizioCorrelato()) : null);
		clone.setTipoServizioCorrelato(this.getTipoServizioCorrelato()!=null ? new String(this.getTipoServizioCorrelato()) : null);
		
		// collaborazione
		clone.setCollaborazione(this.getCollaborazione()!=null ? new String(this.getCollaborazione()) : null);
		
		// servizio
		clone.setServizio(this.getServizio()!=null ? new String(this.getServizio()) : null);
		clone.setTipoServizio(this.getTipoServizio()!=null ? new String(this.getTipoServizio()) : null);
		clone.setVersioneServizio(new Integer(this.getVersioneServizio()));
		
		// servizio [info-richiedente]
		clone.setServizioRichiedenteBustaDiServizio(this.servizioRichiedenteBustaDiServizio!=null ? new String(this.servizioRichiedenteBustaDiServizio) : null);
		clone.setTipoServizioRichiedenteBustaDiServizio(this.tipoServizioRichiedenteBustaDiServizio!=null ? new String(this.tipoServizioRichiedenteBustaDiServizio) : null);
		
		// azione
		clone.setAzione(this.getAzione()!=null ? new String(this.getAzione()) : null);
		
		// azione [info-richiedente]
		clone.setAzioneRichiedenteBustaDiServizio(this.azioneRichiedenteBustaDiServizio!=null ? new String(this.azioneRichiedenteBustaDiServizio) : null);
		
		// identificativi
		clone.setID(this.getID()!=null ? new String(this.getID()) : null);
		clone.setRiferimentoMessaggio(this.getRiferimentoMessaggio()!=null ? new String(this.getRiferimentoMessaggio()) : null);
		
		// identificativi [info-richiedente]
		clone.setRiferimentoMsgBustaRichiedenteServizio(this.riferimentoMsgBustaRichiedenteServizio!=null ? new String(this.riferimentoMsgBustaRichiedenteServizio) : null);
		
		// date
		clone.setOraRegistrazione(this.getOraRegistrazione()!=null ? new Date(this.getOraRegistrazione().getTime()) : null);
		clone.setTipoOraRegistrazione(this.getTipoOraRegistrazione(),
				this.getTipoOraRegistrazioneValue()!=null ? new String(this.getTipoOraRegistrazioneValue()) : null);
		clone.setTipoOraRegistrazioneValue(this.getTipoOraRegistrazioneValue()!=null ? new String(this.getTipoOraRegistrazioneValue()) : null);
		clone.setScadenza(this.getScadenza()!=null ? new Date(this.getScadenza().getTime()) : null);

		// profilo di trasmissione
		clone.setInoltro(this.getInoltro(),
				this.getInoltroValue()!=null ? new String(this.getInoltroValue()) : null);
		clone.setInoltroValue(this.getInoltroValue()!=null ? new String(this.getInoltroValue()) : null);
		clone.setConfermaRicezione(new Boolean(this.isConfermaRicezione()));
		
		// sequenza
		clone.setSequenza(new Long(this.getSequenza()));
		
		// servizi applicativi
		clone.setServizioApplicativoFruitore(this.getServizioApplicativoFruitore()!=null ? new String(this.getServizioApplicativoFruitore()) : null);
		clone.setServizioApplicativoErogatore(this.getServizioApplicativoErogatore()!=null ? new String(this.getServizioApplicativoErogatore()) : null);

		// protocollo
		clone.setProtocollo(this.getProtocollo()!=null ? new String(this.getProtocollo()) : null);

		// digest
		clone.setDigest(this.getDigest()!=null ? new String(this.getDigest()) : null);

		// properties
		String[]propertiesNames = this.getPropertiesNames();
		if(propertiesNames!=null){
			for (int i = 0; i < propertiesNames.length; i++) {
				String key = propertiesNames[i];
				String value = this.getProperty(key);
				if(key!=null && value!=null){
					clone.addProperty(new String(key), new String(value));
				}
			}
		}

		// ListaEccezioni
		for(int i=0; i<this.sizeListaEccezioni(); i++){
			clone.addEccezione(this.getEccezione(i).clone());
		}
		
		// ListaRiscontri
		for(int i=0; i<this.sizeListaRiscontri(); i++){
			clone.addRiscontro(this.getRiscontro(i).clone());
		}
		
		// ListaTrasmissioni
		for(int i=0; i<this.sizeListaTrasmissioni(); i++){
			clone.addTrasmissione(this.getTrasmissione(i).clone());
		}



		return clone;
	}

}