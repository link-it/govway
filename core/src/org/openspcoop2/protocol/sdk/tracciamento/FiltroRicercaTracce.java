/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.sdk.tracciamento;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;

/**
 * FiltroRicercaTracce
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo
* @author $Author$
* @version $Rev$, $Date$
 */
public class FiltroRicercaTracce implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	protected Date maxDate;
	protected Date minDate;

	protected String idTransazione;
	
	protected RuoloMessaggio tipoTraccia;
	protected TipoPdD tipoPdD;
	protected IDSoggetto dominio;

	protected String idBusta; // utilizzabile con tipo traccia
	protected String idBustaRichiesta;
	protected String idBustaRisposta;
	protected String riferimentoMessaggio;
	protected boolean ricercaSoloBusteErrore;
	protected InformazioniProtocollo informazioniProtocollo;

	protected String servizioApplicativoFruitore;
	protected String servizioApplicativoErogatore;

	protected String idCorrelazioneApplicativa;
	protected String idCorrelazioneApplicativaRisposta;
	protected boolean idCorrelazioneApplicativaOrMatch = false;

	protected String protocollo;

	protected Map<String, String> properties;


	public FiltroRicercaTracce() {
		this.properties = new HashMap<>();
	}


	/**
	 * Gets the value of the idCorrelazioneApplicativa property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getIdCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
	}

	/**
	 * Sets the value of the idCorrelazioneApplicativa property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setIdCorrelazioneApplicativa(String value) {
		this.idCorrelazioneApplicativa = value;
	}

	/**
	 * Gets the value of the idBusta property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getIdBusta() {
		return this.idBusta;
	}

	/**
	 * Sets the value of the idBusta property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setIdBusta(String value) {
		this.idBusta = value;
	}
	
	/**
	 * Gets the value of the idBustaRichiesta property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getIdBustaRichiesta() {
		return this.idBustaRichiesta;
	}

	/**
	 * Sets the value of the idBustaRichiesta property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setIdBustaRichiesta(String value) {
		this.idBustaRichiesta = value;
	}
	
	/**
	 * Gets the value of the idBustaRichiesta property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getIdBustaRisposta() {
		return this.idBustaRisposta;
	}

	/**
	 * Sets the value of the idBustaRichiesta property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setIdBustaRisposta(String value) {
		this.idBustaRisposta = value;
	}


	/**
	 * Gets the value of the maxDate property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link XMLGregorianCalendar }
	 *     
	 */
	public Date getMaxDate() {
		return this.maxDate;
	}

	/**
	 * Sets the value of the maxDate property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link XMLGregorianCalendar }
	 *     
	 */
	public void setMaxDate(Date value) {
		this.maxDate = value;
	}
	/**
	 * Gets the value of the minDate property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link XMLGregorianCalendar }
	 *     
	 */
	public Date getMinDate() {
		return this.minDate;
	}
	/**
	 * Sets the value of the minDate property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link XMLGregorianCalendar }
	 *     
	 */
	public void setMinDate(Date value) {
		this.minDate = value;
	}


	/**
	 * Gets the value of the property property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the property property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getProperty().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link String }
	 * 
	 * 
	 */
	public Map<String, String> getProperty() {
		return this.properties;
	}

	/**
	 * Gets the value of the tipoTraccia property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public RuoloMessaggio getTipoTraccia() {
		return this.tipoTraccia;
	}

	/**
	 * Sets the value of the tipoTraccia property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setTipoTraccia(RuoloMessaggio value) {
		this.tipoTraccia = value;
	}

	public String getIdCorrelazioneApplicativaRisposta() {
		return this.idCorrelazioneApplicativaRisposta;
	}

	public void setIdCorrelazioneApplicativaRisposta(
			String idCorrelazioneApplicativaRisposta) {
		this.idCorrelazioneApplicativaRisposta = idCorrelazioneApplicativaRisposta;
	}

	public boolean isIdCorrelazioneApplicativaOrMatch() {
		return this.idCorrelazioneApplicativaOrMatch;
	}

	public void setIdCorrelazioneApplicativaOrMatch(
			boolean correlazioneApplicativaOrMatch) {
		this.idCorrelazioneApplicativaOrMatch = correlazioneApplicativaOrMatch;
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


	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}

	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}

	public String getServizioApplicativoErogatore() {
		return this.servizioApplicativoErogatore;
	}

	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		this.servizioApplicativoErogatore = servizioApplicativoErogatore;
	}

	public TipoPdD getTipoPdD() {
		return this.tipoPdD;
	}

	public void setTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}

	public IDSoggetto getDominio() {
		return this.dominio;
	}

	public void setDominio(IDSoggetto dominio) {
		this.dominio = dominio;
	}

	public String getRiferimentoMessaggio() {
		return this.riferimentoMessaggio;
	}

	public void setRiferimentoMessaggio(String riferimentoMessaggio) {
		this.riferimentoMessaggio = riferimentoMessaggio;
	}

	public boolean isRicercaSoloBusteErrore() {
		return this.ricercaSoloBusteErrore;
	}

	public void setRicercaSoloBusteErrore(boolean ricercaSoloBusteErrore) {
		this.ricercaSoloBusteErrore = ricercaSoloBusteErrore;
	}

	public InformazioniProtocollo getInformazioniProtocollo() {
		return this.informazioniProtocollo;
	}

	public void setInformazioniProtocollo(
			InformazioniProtocollo informazioniProtocollo) {
		this.informazioniProtocollo = informazioniProtocollo;
	}
	
    public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append("Filtro Ricerca traccia:");
		if(this.idTransazione!=null)
			bf.append(" [id-transazione:"+this.idTransazione+"]");
		if(this.minDate!=null)
			bf.append(" [intervallo-inferiore-data:"+this.minDate+"]");
		if(this.maxDate!=null)
			bf.append(" [intervallo-superiore-data:"+this.maxDate+"]");
		if(this.tipoTraccia!=null)
			bf.append(" [tipo-traccia:"+this.tipoTraccia+"]");
		if(this.tipoPdD!=null)
			bf.append(" [tipo-porta-dominio:"+this.tipoPdD.getTipo()+"]");
		if(this.dominio!=null){
			if(this.dominio.getCodicePorta()!=null){
				bf.append(" [identificativo-porta.codice-porta:"+this.dominio.getCodicePorta()+"]");
			}
			if(this.dominio.getTipo()!=null){
				bf.append(" [identificativo-porta.tipo:"+this.dominio.getTipo()+"]");
			}
			if(this.dominio.getNome()!=null){
				bf.append(" [identificativo-porta.nome:"+this.dominio.getNome()+"]");
			}
		}
		if(this.idBusta!=null)
			bf.append(" [id-busta:"+this.idBusta+"]");
		if(this.idBustaRichiesta!=null)
			bf.append(" [id-busta-richiesta:"+this.idBustaRichiesta+"]");
		if(this.idBustaRisposta!=null)
			bf.append(" [id-busta-risposta:"+this.idBustaRisposta+"]");
		if(this.riferimentoMessaggio!=null)
			bf.append(" [riferimento-messaggio:"+this.riferimentoMessaggio+"]");
		if(this.ricercaSoloBusteErrore)
			bf.append(" [ricerca-solo-buste-errore]");
		if(this.informazioniProtocollo!=null){
			bf.append(this.informazioniProtocollo.toString());
		}
		if(this.servizioApplicativoFruitore!=null)
			bf.append(" [saFruitore:"+this.servizioApplicativoFruitore+"]");
		if(this.servizioApplicativoErogatore!=null)
			bf.append(" [saErogatore:"+this.servizioApplicativoErogatore+"]");
		if(this.idCorrelazioneApplicativa!=null)
			bf.append(" [id-correlazione-applicativa:"+this.idCorrelazioneApplicativa+"]");
		if(this.idCorrelazioneApplicativaRisposta!=null)
			bf.append(" [id-correlazione-applicativa-risposta:"+this.idCorrelazioneApplicativaRisposta+"]");
		if(this.idCorrelazioneApplicativa!=null && this.idCorrelazioneApplicativaRisposta!=null)
			bf.append(" [id-correlazione-applicativa-or-match:"+this.idCorrelazioneApplicativaOrMatch+"]");	
		if(this.protocollo!=null){
			bf.append(" [protocollo:"+this.protocollo+"]");
		}
		if(this.properties!=null)
			bf.append(" [properties:"+this.properties.size()+"]");

		if(bf.length()=="Filtro Ricerca traccia:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}

}
