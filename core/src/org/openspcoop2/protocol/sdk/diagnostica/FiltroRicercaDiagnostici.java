/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.sdk.diagnostica;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.id.IDSoggetto;

/**
 * Oggetto contenente informazioni per la ricerca di Diagnostici
 * 
 * 
 * @author Stefano Corallo (corallo@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaDiagnostici implements Serializable{


	private static final long serialVersionUID = 2103096411857601491L;
  
    
    protected Date dataFine;
    protected Date dataInizio;
	
    protected String idTransazione;
   
	protected IDSoggetto dominio;
    protected String idFunzione;
    
    protected String idBustaRichiesta;
    protected String idBustaRisposta;
		
    protected Integer severita;
    
    protected String codice;
    
    protected String messaggioCercatoInternamenteTestoDiagnostico;
    
    protected String protocollo;
    
    protected Map<String, String> properties;

    protected Boolean checkApplicativoIsNull;
	protected String applicativo;

    
 
    public FiltroRicercaDiagnostici() {
    	this.properties = new HashMap<>();
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
  
	
    public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
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
    
    public void setProperties(Map<String, String> params) {
    	this.properties = params;
    }
    
    public Map<String, String> getProperties() {
    	return this.properties;
    }

	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
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
	
    public Boolean getCheckApplicativoIsNull() {
		return this.checkApplicativoIsNull;
	}

	public void setCheckApplicativoIsNull(Boolean checkApplicativoIsNull) {
		this.checkApplicativoIsNull = checkApplicativoIsNull;
	}

	public String getApplicativo() {
		return this.applicativo;
	}

	public void setApplicativo(String applicativo) {
		this.applicativo = applicativo;
	}
	
	@Override
	public String toString() {
		
		String pattern="idBustaRichiesta [{0}]" +
				"idBustaRisposta [{1}]" +
				(this.dataInizio!=null ? " dataInizio [{2,date} {2,time}]" : " dataInizio [{2}]") +
				(this.dataFine!=null   ? " dataFine   [{3,date} {3,time}]" : " dataFine [{3}]") + 
				" dominio  [{4}]" +
				" parametriEstensioneSize [{5}]"+
				" protocollo [{6}]"+
				" messaggioCercatoInternamenteTestoDiagnostico [{7}]"+
				" idFunzione [{8}]"+
				" severita [{9}]"+
				" codice [{10}]"+
				" idTransazione [{11}]"+
				" applicativo [{12}]"+
				" checkApplicativoIsNull [{13}]";
		
		return MessageFormat.format(pattern, 
				this.idBustaRichiesta!=null ? this.idBustaRichiesta : "not set",
				this.idBustaRisposta!=null ? this.idBustaRisposta : "not set",
				this.dataInizio!=null ? this.dataInizio.getTime() : "not set",
				this.dataFine!=null ? this.dataFine.getTime() : "not set",
				this.dominio!=null ? this.dominio.toString() : "not set",
				this.properties!=null ? this.properties.size() : "not set",
				this.protocollo!=null ? this.protocollo : "not set",
				this.messaggioCercatoInternamenteTestoDiagnostico!=null ? this.messaggioCercatoInternamenteTestoDiagnostico : "not set",
				this.idFunzione!=null ? this.idFunzione : "not set",
				this.severita!=null ? this.severita : "not set",
				this.codice!=null ? this.codice : "not set",
				this.idTransazione!=null ? this.idTransazione : "not set",
				this.applicativo!=null ? this.applicativo : "not set",
				this.checkApplicativoIsNull!=null ? this.checkApplicativoIsNull : "not set"
				);
	}


}


