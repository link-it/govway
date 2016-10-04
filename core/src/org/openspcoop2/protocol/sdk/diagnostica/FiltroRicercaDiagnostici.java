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



package org.openspcoop2.protocol.sdk.diagnostica;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.id.IDSoggetto;

/**
 * Oggetto contenente informazioni per la ricerca di Diagnostici
 * 
 * 
 * @author Stefano Corallo <corallo@link.it>
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaDiagnostici implements Serializable{


	private static final long serialVersionUID = 2103096411857601491L;

    protected Date dataFine;
    protected Date dataInizio;
	
    protected Boolean delegata;
    protected String nomePorta; // nomePortaDelegata o Applicativa
    protected String idFunzione;
    protected IDSoggetto dominio;
    
    protected Boolean ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
	
    protected String idBustaRichiesta;
    protected String idBustaRisposta;
    protected InformazioniProtocollo busta;
	
    protected String servizioApplicativo;
    
    protected String correlazioneApplicativa;
    protected String correlazioneApplicativaRisposta;
    protected boolean correlazioneApplicativaOrMatch = false;
		
    protected Integer severita;
    
    protected String codice;
    
	private String messaggioCercatoInternamenteTestoDiagnostico;
    
    private String protocollo;
    
    protected Hashtable<String, String> properties;
	
    protected List<IDSoggetto> filtroSoggetti;
   
    
    


    
 
    public FiltroRicercaDiagnostici() {
    	this.properties = new Hashtable<String, String>();
    	this.filtroSoggetti = new ArrayList<IDSoggetto>();
	}
    
    public void addFiltroSoggetto(IDSoggetto soggetto){
    	this.filtroSoggetti.add(soggetto);
    }
    
    public int sizeFiltroSoggetti(){
    	return this.filtroSoggetti.size();
    }
    
    public IDSoggetto getFiltroSoggetto(int i){
    	return this.filtroSoggetti.get(i);
    }
    
    public IDSoggetto removeFiltroSoggetto(int i){
    	return this.filtroSoggetti.remove(i);
    }
    
    public List<IDSoggetto> getFiltroSoggetti() {
    	return this.filtroSoggetti;
    }
    
    public void setFiltroSoggetti(List<IDSoggetto> list) {
		this.filtroSoggetti = list;
    }
        
    public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
    
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	
	public String getCorrelazioneApplicativa() {
		return this.correlazioneApplicativa;
	}
	
	public void setCorrelazioneApplicativa(String correlazioneApplicativa) {
		this.correlazioneApplicativa = correlazioneApplicativa;
	}
	
	public String getCorrelazioneApplicativaRisposta() {
		return this.correlazioneApplicativaRisposta;
	}
	public void setCorrelazioneApplicativaRisposta(String correlazioneApplicativaRisposta) {
		this.correlazioneApplicativaRisposta = correlazioneApplicativaRisposta;
	}
	
	public Integer getSeverita() {
		return this.severita;
	}
	
	public void setSeverita(Integer severita) {
		this.severita = severita;
	}
	
    public String getCodice() {
		return this.codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public String getIdFunzione() {
		return this.idFunzione;
	}
	
	public void setIdFunzione(String idFunzione) {
		this.idFunzione = idFunzione;
	}
  
	
	public Date getDataInizio() {
		return this.dataInizio;
	}
	
	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}
	
	public Date getDataFine() {
		return this.dataFine;
	}
	
	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}
	
	public InformazioniProtocollo getInformazioniProtocollo() {
		return this.busta;
	}
	
	public void setInformazioniProtocollo(InformazioniProtocollo busta) {
		this.busta = busta;
	}
	
	public String getNomePorta() {
		return this.nomePorta;
	}
	
	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}
	
	public Boolean isDelegata() {
		return this.delegata;
	}
	
	public void setDelegata(Boolean delegata) {
		this.delegata = delegata;
	}

	public void addProperty(String key,String value){
    	this.properties.put(key,value);
    }
    
    public int sizeProperties(){
    	return this.properties.size();
    }

    public String getProperty(String key){
    	return this.properties.get(key);
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

	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public boolean isCorrelazioneApplicativaOrMatch() {
		return this.correlazioneApplicativaOrMatch;
	}

	public void setCorrelazioneApplicativaOrMatch(
			boolean correlazioneApplicativaOrMatch) {
		this.correlazioneApplicativaOrMatch = correlazioneApplicativaOrMatch;
	}

	public Boolean getRicercaSoloMessaggiCorrelatiInformazioniProtocollo() {
		return this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
	}

	public void setRicercaSoloMessaggiCorrelatiInformazioniProtocollo(Boolean value) {
		this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo = value;
	}

	public String getIdBustaRichiesta() {
		return this.idBustaRichiesta;
	}

	public void setIdBustaRichiesta(String idBustaRichiesta) {
		this.idBustaRichiesta = idBustaRichiesta;
	}

	public String getIdBustaRisposta() {
		return this.idBustaRisposta;
	}

	public void setIdBustaRisposta(String idBustaRisposta) {
		this.idBustaRisposta = idBustaRisposta;
	}

	public IDSoggetto getDominio() {
		return this.dominio;
	}

	public void setDominio(IDSoggetto dominio) {
		this.dominio = dominio;
	}
	
    public String getMessaggioCercatoInternamenteTestoDiagnostico() {
		return this.messaggioCercatoInternamenteTestoDiagnostico;
	}

	public void setMessaggioCercatoInternamenteTestoDiagnostico(
			String messaggioCercatoInternamenteTestoDiagnostico) {
		this.messaggioCercatoInternamenteTestoDiagnostico = messaggioCercatoInternamenteTestoDiagnostico;
	}
	
	@Override
	public String toString() {
		
		String pattern="idBustaRichiesta [{0}]" +
				"idBustaRisposta [{1}]" +
				" nomePorta [{2}]" +
				" isDelegata [{3}]" +
				(this.dataInizio!=null ? " dataInizio [{4,date} {4,time}]" : " dataInizio [{4}]") +
				(this.dataFine!=null   ? " dataFine   [{5,date} {5,time}]" : " dataFine [{5}]") + 
				" Informazioni Busta [{6}]" +
				" dominio  [{7}]" +
				" onlyMsgDiagWithProtocolInfo [{8}]"+
				" parametriEstensioneSize [{9}]"+
				" protocollo [{10}]"+
				" correlazioneApplicativaRichiesta [{11}]"+
				" correlazioneApplicativaRisposta [{12}]"+
				" correlazioneApplicativaOrMatch [{13}]"+
				" messaggioCercatoInternamenteTestoDiagnostico [{14}]"+
				" idFunzione [{15}]"+
				" servizioApplicativo [{16}]"+
				" severita [{17}]"+
				" codice [{18}]"+
				" filtroSoggettiSize [{19}]";
		
		return MessageFormat.format(pattern, 
				this.idBustaRichiesta!=null ? this.idBustaRichiesta : "not set",
				this.idBustaRisposta!=null ? this.idBustaRisposta : "not set",
				this.nomePorta!=null ? this.nomePorta : "not set",
				this.delegata!=null ? this.delegata : "not set",
				this.dataInizio!=null ? this.dataInizio.getTime() : "not set",
				this.dataFine!=null ? this.dataFine.getTime() : "not set",
				this.busta!=null ? this.busta.toString() : "not set",
				this.dominio!=null ? this.dominio.toString() : "not set",
				this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo!=null ? this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo : "not set",
				this.properties!=null ? this.properties.size() : "not set",
				this.protocollo!=null ? this.protocollo : "not set",
				this.correlazioneApplicativa!=null ? this.correlazioneApplicativa : "not set",
				this.correlazioneApplicativaRisposta!=null ? this.correlazioneApplicativaRisposta : "not set",
				this.correlazioneApplicativaOrMatch,
				this.messaggioCercatoInternamenteTestoDiagnostico!=null ? this.messaggioCercatoInternamenteTestoDiagnostico : "not set",
				this.idFunzione!=null ? this.idFunzione : "not set",
				this.servizioApplicativo!=null ? this.servizioApplicativo : "not set",
				this.severita!=null ? this.severita : "not set",
				this.codice!=null ? this.codice : "not set",
				this.filtroSoggetti!=null ? this.filtroSoggetti.size() : "not set"
				);
	}


}


