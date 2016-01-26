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


package org.openspcoop2.protocol.sdk.tracciamento;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.tracciamento.Allegati;
import org.openspcoop2.core.tracciamento.Dominio;
import org.openspcoop2.core.tracciamento.DominioSoggetto;
import org.openspcoop2.core.tracciamento.Proprieta;
import org.openspcoop2.core.tracciamento.Protocollo;
import org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione;
import org.openspcoop2.core.tracciamento.constants.TipoEsitoElaborazione;
import org.openspcoop2.protocol.sdk.Allegato;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.EsitoElaborazioneMessaggioTracciatura;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;

/**
 * Bean Contenente le informazioni relative alle tracce
 * 
 * @author Stefano Corallo <corallo@link.it>
 * @author Nardi Lorenzo
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Traccia  implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	// Busta
	protected Busta busta;
	private byte[] bustaInByte;
    private SOAPElement bustaInDom;

    // properties
    protected Hashtable<String, String> properties = new Hashtable<String, String>();
    
    // protocollo
    private String protocollo;
    
    // traccia
    private org.openspcoop2.core.tracciamento.Traccia traccia;
    
    // lista allegati
    private List<Allegato> allegati = new ArrayList<Allegato>();
    
    
	public Traccia() {
    	this.traccia =  new org.openspcoop2.core.tracciamento.Traccia();
	}
    public Traccia(org.openspcoop2.core.tracciamento.Traccia traccia) {
    	
    	this.traccia = traccia;
    	
    	// protocollo
    	if(traccia.getBusta()!=null && traccia.getBusta().getProtocollo()!=null){
    		this.protocollo = traccia.getBusta().getProtocollo().getIdentificativo();
    	}
    	
    	// busta
    	if(traccia.getBusta()!=null){
    		this.busta = new Busta(traccia.getBusta());
    	}
    	
    	// allegati
    	if(traccia.getAllegati()!=null && traccia.getAllegati().sizeAllegatoList()>0)
    	for (org.openspcoop2.core.tracciamento.Allegato allegato : traccia.getAllegati().getAllegatoList()) {
			this.addAllegato(new Allegato(allegato),false);
		}
	}
    
    
    
    // base
    
    public org.openspcoop2.core.tracciamento.Traccia getTraccia() {
		return this.traccia;
	}
	public void setTraccia(org.openspcoop2.core.tracciamento.Traccia traccia) {
		this.traccia = traccia;
		
    	// protocollo
    	if(traccia.getBusta()!=null && traccia.getBusta().getProtocollo()!=null){
    		this.protocollo = traccia.getBusta().getProtocollo().getIdentificativo();
    	}
    	
    	// allegati
    	if(traccia.getAllegati()!=null && traccia.getAllegati().sizeAllegatoList()>0)
    	for (org.openspcoop2.core.tracciamento.Allegato allegato : traccia.getAllegati().getAllegatoList()) {
			this.addAllegato(new Allegato(allegato),false);
		}
	}
    
    
	
	// id  [Wrapper]
	
	public Long getId() {
		return this.traccia.getId();
	}
	public void setId(Long id) {
		this.traccia.setId(id);
	}
	
	
    // data [Wrapper]
    
	public Date getGdo() {
        return this.traccia.getOraRegistrazione();
    }
    public void setGdo(Date value) {
        this.traccia.setOraRegistrazione(value);
    }
    
    
    // dominio [wrapper]
    
    public IDSoggetto getIdSoggetto() {
    	IDSoggetto idSoggetto = null;
    	if(this.traccia.getDominio()!=null){
    		if(this.traccia.getDominio().getSoggetto()!=null){
    			if(idSoggetto==null){
    				idSoggetto = new IDSoggetto();
    			}
    			idSoggetto.setTipo(this.traccia.getDominio().getSoggetto().getTipo());
    			idSoggetto.setNome(this.traccia.getDominio().getSoggetto().getBase());
    		}
    		if(this.traccia.getDominio().getIdentificativoPorta()!=null){
    			if(idSoggetto==null){
    				idSoggetto = new IDSoggetto();
    			}
    			idSoggetto.setCodicePorta(this.traccia.getDominio().getIdentificativoPorta());
    		}
    	}
    	return idSoggetto;
    }
    public void setIdSoggetto(IDSoggetto value) {
        if(value!=null && (value.getTipo()!=null || value.getNome()!=null || value.getCodicePorta()!=null)){
        	if(value.getTipo()!=null || value.getNome()!=null){
		    	if(this.traccia.getDominio()==null){
		        	this.traccia.setDominio(new Dominio());
		        }
		    	if(this.traccia.getDominio().getSoggetto()==null){
		    		this.traccia.getDominio().setSoggetto(new DominioSoggetto());
		    	}
		    	this.traccia.getDominio().getSoggetto().setTipo(value.getTipo());
		    	this.traccia.getDominio().getSoggetto().setBase(value.getNome());
        	}
        	else{
        		if(this.traccia.getDominio()!=null){
		        	this.traccia.getDominio().setSoggetto(null);
		        }
        	}
        	
        	if(value.getCodicePorta()!=null){
        		if(this.traccia.getDominio()==null){
		        	this.traccia.setDominio(new Dominio());
		        }
        		this.traccia.getDominio().setIdentificativoPorta(value.getCodicePorta());
        	}
        	else{
        		if(this.traccia.getDominio()!=null){
		        	this.traccia.getDominio().setIdentificativoPorta(null);
		        }
        	}
        }
        else{
        	if(this.traccia.getDominio()!=null){
        		if(this.traccia.getDominio().getFunzione()==null){
        			this.traccia.setDominio(null);
        		}
        		else{
        			this.traccia.getDominio().setIdentificativoPorta(null);
        			this.traccia.getDominio().setSoggetto(null);
        		}
        	}
        }
    }
	public TipoPdD getTipoPdD() {
		if(this.traccia.getDominio()!=null && this.traccia.getDominio().getFunzione()!=null){
			switch (this.traccia.getDominio().getFunzione()) {
			case PORTA_DELEGATA:
				return TipoPdD.DELEGATA;
			case PORTA_APPLICATIVA:
				return TipoPdD.APPLICATIVA;
			case INTEGRATION_MANAGER:
				return TipoPdD.INTEGRATION_MANAGER;
			case ROUTER:
				return TipoPdD.ROUTER;
			}
		}
		return null;
	}
	public void setTipoPdD(TipoPdD tipoPdD) {
		if(tipoPdD!=null){
			if(this.traccia.getDominio()==null){
				this.traccia.setDominio(new Dominio());
			}
			switch (tipoPdD) {
			case DELEGATA:
				this.traccia.getDominio().setFunzione(org.openspcoop2.core.tracciamento.constants.TipoPdD.PORTA_DELEGATA);
				break;
			case APPLICATIVA:
				this.traccia.getDominio().setFunzione(org.openspcoop2.core.tracciamento.constants.TipoPdD.PORTA_APPLICATIVA);
				break;
			case INTEGRATION_MANAGER:
				this.traccia.getDominio().setFunzione(org.openspcoop2.core.tracciamento.constants.TipoPdD.INTEGRATION_MANAGER);
				break;
			case ROUTER:
				this.traccia.getDominio().setFunzione(org.openspcoop2.core.tracciamento.constants.TipoPdD.ROUTER);
				break;
			}
		}
		else{
        	if(this.traccia.getDominio()!=null){
        		if(this.traccia.getDominio().getIdentificativoPorta()==null && this.traccia.getDominio().getSoggetto()==null){
        			this.traccia.setDominio(null);
        		}
        		else{
        			this.traccia.getDominio().setFunzione(null);
        		}
        	}
        }
	}
    
    
    // tipoTraccia [wrapper]
    
	public TipoTraccia getTipoMessaggio() {
		if(this.traccia.getTipo()!=null){
			switch (this.traccia.getTipo()) {
			case RICHIESTA:
				return TipoTraccia.RICHIESTA;
			case RISPOSTA:
				return TipoTraccia.RISPOSTA;
			}
		}
		return null;
    }
    public void setTipoMessaggio(TipoTraccia value) {
    	if(value!=null){
			switch (value) {
			case RICHIESTA:
				this.traccia.setTipo(org.openspcoop2.core.tracciamento.constants.TipoTraccia.RICHIESTA);
				break;
			case RISPOSTA:
				this.traccia.setTipo(org.openspcoop2.core.tracciamento.constants.TipoTraccia.RISPOSTA);
				break;
			}
		}
    }

    
    
	// Busta
    
    public Busta getBusta() {
        return this.busta;
    }
    public void setBusta(Busta value) {
        this.busta = value;
        if(value!=null)
        	this.traccia.setBusta(value.getBusta());
        else
        	this.traccia.setBusta(null);
    }
    public byte[] getBustaAsByteArray() {
		return this.bustaInByte;
	}
	public void setBustaAsByteArray(byte[] bustaInByte) {
		this.bustaInByte = bustaInByte;
	}
	public SOAPElement getBustaAsElement() {
		return this.bustaInDom;
	}
	public void setBustaAsElement(SOAPElement bustaInDom) {
		this.bustaInDom = bustaInDom;
	}
	// [wrapper]
	public String getBustaAsString() {
		return this.traccia.getBustaXml();
	}
	public void setBustaAsString(String bustaAsString) {
		this.traccia.setBustaXml(bustaAsString);
	}
	
	
	// correlazione [wrapper]
    
	public String getCorrelazioneApplicativa() {
        return this.traccia.getIdentificativoCorrelazioneRichiesta();
    }
    public void setCorrelazioneApplicativa(String value) {
    	this.traccia.setIdentificativoCorrelazioneRichiesta(value);
    }
    
    public String getCorrelazioneApplicativaRisposta() {
        return this.traccia.getIdentificativoCorrelazioneRisposta();
    }
    public void setCorrelazioneApplicativaRisposta(String value) {
    	this.traccia.setIdentificativoCorrelazioneRisposta(value);
    }
    
    
    
    // location [wrapper]
    
    public String getLocation() {
        return this.traccia.getLocation();
    }
    public void setLocation(String value) {
    	this.traccia.setLocation(value);
    }
    
    


    // properties
    public void addPropertyInBusta(String key,String value){
    	if(this.traccia.getBusta()==null){
			this.traccia.setBusta(new org.openspcoop2.core.tracciamento.Busta());
		}
		if(this.traccia.getBusta().getProtocollo()==null){
			this.traccia.getBusta().setProtocollo(new Protocollo());
		}
		if(this.traccia.getBusta().getProtocollo().getProprietaList()==null){
			this.traccia.getBusta().getProtocollo().setProprietaList(new ArrayList<Proprieta>());
		}
		boolean exists = false;
		for (int i = 0; i < this.traccia.getBusta().getProtocollo().sizeProprietaList(); i++) {
			Proprieta p = this.traccia.getBusta().getProtocollo().getProprieta(i);
			if(key.equals(p.getNome())){
				exists = true;
				break;
			}
		}
		if(!exists){
    		Proprieta proprieta = new Proprieta();
    		proprieta.setNome(key);
    		proprieta.setValore(value);
    		this.traccia.getBusta().getProtocollo().addProprieta(proprieta);
		}
    }
    public void addProperty(String key,String value){
    	this.properties.put(key,value);
    }  
    public int sizeProperties(){
    	return this.properties.size();
    }
    public String getProperty(String key){
    	String value = this.properties.get(key);
    	if(value==null || "".equals(value)){
    		if(Costanti.CLUSTER_ID.equals(key)){
    			if(this.traccia.getBusta()!=null && this.traccia.getBusta().getProtocollo()!=null && 
    					this.traccia.getBusta().getProtocollo().getProprietaList()!=null){
    				for (int i = 0; i < this.traccia.getBusta().getProtocollo().sizeProprietaList(); i++) {
						Proprieta p = this.traccia.getBusta().getProtocollo().getProprieta(i);
						if(Costanti.CLUSTER_ID.equals(p.getNome())){
							return p.getValore();
						}
					}
    			}
    		}
    	}
    	return value;
    }
    public String removeProperty(String key){
    	return this.properties.remove(key);
    }
    public String[] getPropertiesValues() {
    	return this.properties.values().toArray(new String[this.properties.size()]);
    }
    public String[] getPropertiesNames() {
    	return this.properties.keySet().toArray(new String[this.properties.size()]);
    }
    public void setProperties(Hashtable<String, String> params) {
    	this.properties = params;
    }
    public Hashtable<String, String> getProperties() {
    	return this.properties;
    }
    
    
    // protocollo
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	
	
    // esito [Wrapper]
	public EsitoElaborazioneMessaggioTracciato getEsitoElaborazioneMessaggioTracciato() {
		if(this.traccia.getEsitoElaborazione()!=null){
			EsitoElaborazioneMessaggioTracciato esito = new EsitoElaborazioneMessaggioTracciato();
			esito.setDettaglio(this.traccia.getEsitoElaborazione().getDettaglio());
			if(this.traccia.getEsitoElaborazione().getTipo()!=null){
				switch (this.traccia.getEsitoElaborazione().getTipo()) {
				case INVIATO:
					esito.setEsito(EsitoElaborazioneMessaggioTracciatura.INVIATO);
					break;
				case RICEVUTO:
					esito.setEsito(EsitoElaborazioneMessaggioTracciatura.RICEVUTO);
					break;
				case ERRORE:
					esito.setEsito(EsitoElaborazioneMessaggioTracciatura.ERRORE);
					break;
				}
			}
			return esito;
		}
		return null;
	}
	public void setEsitoElaborazioneMessaggioTracciato(
			EsitoElaborazioneMessaggioTracciato esitoElaborazioneMessaggioTracciato) {
		if(esitoElaborazioneMessaggioTracciato==null){
			return;
		}
		if(this.traccia.getEsitoElaborazione()==null){
			this.traccia.setEsitoElaborazione(new TracciaEsitoElaborazione());
		}
		this.traccia.getEsitoElaborazione().setDettaglio(esitoElaborazioneMessaggioTracciato.getDettaglio());
		if(esitoElaborazioneMessaggioTracciato.getEsito()!=null){
			switch (esitoElaborazioneMessaggioTracciato.getEsito()) {
			case INVIATO:
				this.traccia.getEsitoElaborazione().setTipo(TipoEsitoElaborazione.INVIATO);
				break;
			case RICEVUTO:
				this.traccia.getEsitoElaborazione().setTipo(TipoEsitoElaborazione.RICEVUTO);
				break;
			case ERRORE:
				this.traccia.getEsitoElaborazione().setTipo(TipoEsitoElaborazione.ERRORE);
				break;
			}
		}
	}
	
	
    // allegati [wrapped]
	public List<Allegato> getListaAllegati() {
		return this.allegati;
    }
	public int sizeListaAllegati() {
		return this.allegati.size();
	}
	public void addAllegato(Allegato a) {
		this.addAllegato(a, true);
	}
	private void addAllegato(Allegato a, boolean addCore) {
		this.allegati.add(a);
		if(addCore){
			if(this.traccia.getAllegati()==null){
				this.traccia.setAllegati(new Allegati());
			}
			this.traccia.getAllegati().addAllegato(a.getAllegato());
		}
	}
	public Allegato getAllegato(int index) {
		return this.allegati.get(index);
	}
	public Allegato removeAllegato(int index) {
		this.traccia.getAllegati().removeAllegato(index);
		return this.allegati.remove(index);
	}
	protected void setListaAllegati(List<Allegato> listaAllegati) {
		this.allegati = listaAllegati;
	}
	
	
	
	
	@Override
	public Traccia clone(){
		
		// Non uso il base clone per far si che venga usato il costruttore new String()
		
		Traccia clone = new Traccia();
		
		// id
		clone.setId(this.getId()!=null ? new Long(this.getId()) : null);
		
    	// data
		clone.setGdo(this.getGdo()!=null ? new Date(this.getGdo().getTime()) : null);
		
        // dominio
		clone.setIdSoggetto(this.getIdSoggetto()!=null ? this.getIdSoggetto().clone() : null);
		clone.setTipoPdD(this.getTipoPdD());
    	
        // tipoTraccia
		clone.setTipoMessaggio(this.getTipoMessaggio());
		
		// busta
		clone.setBusta(this.getBusta()!=null ? this.getBusta().clone() : null);
		ByteArrayOutputStream bout = null;
		if(this.getBustaAsByteArray()!=null){
			bout = new ByteArrayOutputStream();
			try{
				bout.write(this.getBustaAsByteArray());
				bout.flush();
				bout.close();
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
			clone.setBustaAsByteArray(bout.toByteArray());
		}
		clone.setBustaAsElement(this.getBustaAsElement()); // non clonato, vedere se si trova un modo efficente se serve
		clone.setBustaAsString(this.getBustaAsString()!=null ? new String(this.getBustaAsString()) : null);
		
    	// correlazione
		clone.setCorrelazioneApplicativa(this.getCorrelazioneApplicativa()!=null ? new String(this.getCorrelazioneApplicativa()) : null);
		clone.setCorrelazioneApplicativaRisposta(this.getCorrelazioneApplicativaRisposta()!=null ? new String(this.getCorrelazioneApplicativaRisposta()) : null);
    	
    	// location
		clone.setLocation(this.getLocation()!=null ? new String(this.getLocation()) : null);
		
    	// esito
		clone.setEsitoElaborazioneMessaggioTracciato(this.getEsitoElaborazioneMessaggioTracciato()!=null ? this.getEsitoElaborazioneMessaggioTracciato().clone() : null);
		
		// properties
		if(this.properties!=null && this.properties.size()>0){
			Enumeration<String> keys = this.properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String value = this.properties.get(key);
				if(key!=null && value!=null){
					clone.addProperty(new String(key), new String(value));
				}
			}
		}
		
		// protocollo
		clone.setProtocollo(this.protocollo!=null ? new String(this.protocollo) : null);
		
    	// allegati
		for(int i=0; i<this.sizeListaAllegati(); i++){
			clone.addAllegato(this.getAllegato(i).clone());
		}
		
		return clone;
	}
}
