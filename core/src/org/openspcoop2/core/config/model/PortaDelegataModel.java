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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.PortaDelegata;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaDelegata 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataModel extends AbstractModel<PortaDelegata> {

	public PortaDelegataModel(){
	
		super();
	
		this.SOGGETTO_EROGATORE = new org.openspcoop2.core.config.model.PortaDelegataSoggettoErogatoreModel(new Field("soggetto-erogatore",org.openspcoop2.core.config.PortaDelegataSoggettoErogatore.class,"porta-delegata",PortaDelegata.class));
		this.SERVIZIO = new org.openspcoop2.core.config.model.PortaDelegataServizioModel(new Field("servizio",org.openspcoop2.core.config.PortaDelegataServizio.class,"porta-delegata",PortaDelegata.class));
		this.AZIONE = new org.openspcoop2.core.config.model.PortaDelegataAzioneModel(new Field("azione",org.openspcoop2.core.config.PortaDelegataAzione.class,"porta-delegata",PortaDelegata.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.ServizioApplicativoModel(new Field("servizio-applicativo",org.openspcoop2.core.config.ServizioApplicativo.class,"porta-delegata",PortaDelegata.class));
		this.MTOM_PROCESSOR = new org.openspcoop2.core.config.model.MtomProcessorModel(new Field("mtom-processor",org.openspcoop2.core.config.MtomProcessor.class,"porta-delegata",PortaDelegata.class));
		this.MESSAGE_SECURITY = new org.openspcoop2.core.config.model.MessageSecurityModel(new Field("message-security",org.openspcoop2.core.config.MessageSecurity.class,"porta-delegata",PortaDelegata.class));
		this.VALIDAZIONE_CONTENUTI_APPLICATIVI = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel(new Field("validazione-contenuti-applicativi",org.openspcoop2.core.config.ValidazioneContenutiApplicativi.class,"porta-delegata",PortaDelegata.class));
		this.CORRELAZIONE_APPLICATIVA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaModel(new Field("correlazione-applicativa",org.openspcoop2.core.config.CorrelazioneApplicativa.class,"porta-delegata",PortaDelegata.class));
		this.CORRELAZIONE_APPLICATIVA_RISPOSTA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaRispostaModel(new Field("correlazione-applicativa-risposta",org.openspcoop2.core.config.CorrelazioneApplicativaRisposta.class,"porta-delegata",PortaDelegata.class));
		this.ID_SOGGETTO = new Field("id-soggetto",java.lang.Long.class,"porta-delegata",PortaDelegata.class);
		this.ID_ACCORDO = new Field("id-accordo",java.lang.Long.class,"porta-delegata",PortaDelegata.class);
		this.ID_PORT_TYPE = new Field("id-port-type",java.lang.Long.class,"porta-delegata",PortaDelegata.class);
		this.TIPO_SOGGETTO_PROPRIETARIO = new Field("tipo-soggetto-proprietario",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME_SOGGETTO_PROPRIETARIO = new Field("nome-soggetto-proprietario",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.STATO_MESSAGE_SECURITY = new Field("stato-message-security",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME = new Field("nome",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.LOCATION = new Field("location",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.AUTENTICAZIONE = new Field("autenticazione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.AUTORIZZAZIONE = new Field("autorizzazione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.AUTORIZZAZIONE_CONTENUTO = new Field("autorizzazione-contenuto",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.RICEVUTA_ASINCRONA_SIMMETRICA = new Field("ricevuta-asincrona-simmetrica",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.RICEVUTA_ASINCRONA_ASIMMETRICA = new Field("ricevuta-asincrona-asimmetrica",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.INTEGRAZIONE = new Field("integrazione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.ALLEGA_BODY = new Field("allega-body",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.SCARTA_BODY = new Field("scarta-body",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.GESTIONE_MANIFEST = new Field("gestione-manifest",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.STATELESS = new Field("stateless",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.LOCAL_FORWARD = new Field("local-forward",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"porta-delegata",PortaDelegata.class);
	
	}
	
	public PortaDelegataModel(IField father){
	
		super(father);
	
		this.SOGGETTO_EROGATORE = new org.openspcoop2.core.config.model.PortaDelegataSoggettoErogatoreModel(new ComplexField(father,"soggetto-erogatore",org.openspcoop2.core.config.PortaDelegataSoggettoErogatore.class,"porta-delegata",PortaDelegata.class));
		this.SERVIZIO = new org.openspcoop2.core.config.model.PortaDelegataServizioModel(new ComplexField(father,"servizio",org.openspcoop2.core.config.PortaDelegataServizio.class,"porta-delegata",PortaDelegata.class));
		this.AZIONE = new org.openspcoop2.core.config.model.PortaDelegataAzioneModel(new ComplexField(father,"azione",org.openspcoop2.core.config.PortaDelegataAzione.class,"porta-delegata",PortaDelegata.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.ServizioApplicativoModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.core.config.ServizioApplicativo.class,"porta-delegata",PortaDelegata.class));
		this.MTOM_PROCESSOR = new org.openspcoop2.core.config.model.MtomProcessorModel(new ComplexField(father,"mtom-processor",org.openspcoop2.core.config.MtomProcessor.class,"porta-delegata",PortaDelegata.class));
		this.MESSAGE_SECURITY = new org.openspcoop2.core.config.model.MessageSecurityModel(new ComplexField(father,"message-security",org.openspcoop2.core.config.MessageSecurity.class,"porta-delegata",PortaDelegata.class));
		this.VALIDAZIONE_CONTENUTI_APPLICATIVI = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel(new ComplexField(father,"validazione-contenuti-applicativi",org.openspcoop2.core.config.ValidazioneContenutiApplicativi.class,"porta-delegata",PortaDelegata.class));
		this.CORRELAZIONE_APPLICATIVA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaModel(new ComplexField(father,"correlazione-applicativa",org.openspcoop2.core.config.CorrelazioneApplicativa.class,"porta-delegata",PortaDelegata.class));
		this.CORRELAZIONE_APPLICATIVA_RISPOSTA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaRispostaModel(new ComplexField(father,"correlazione-applicativa-risposta",org.openspcoop2.core.config.CorrelazioneApplicativaRisposta.class,"porta-delegata",PortaDelegata.class));
		this.ID_SOGGETTO = new ComplexField(father,"id-soggetto",java.lang.Long.class,"porta-delegata",PortaDelegata.class);
		this.ID_ACCORDO = new ComplexField(father,"id-accordo",java.lang.Long.class,"porta-delegata",PortaDelegata.class);
		this.ID_PORT_TYPE = new ComplexField(father,"id-port-type",java.lang.Long.class,"porta-delegata",PortaDelegata.class);
		this.TIPO_SOGGETTO_PROPRIETARIO = new ComplexField(father,"tipo-soggetto-proprietario",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME_SOGGETTO_PROPRIETARIO = new ComplexField(father,"nome-soggetto-proprietario",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.STATO_MESSAGE_SECURITY = new ComplexField(father,"stato-message-security",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.LOCATION = new ComplexField(father,"location",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.AUTENTICAZIONE = new ComplexField(father,"autenticazione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.AUTORIZZAZIONE = new ComplexField(father,"autorizzazione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.AUTORIZZAZIONE_CONTENUTO = new ComplexField(father,"autorizzazione-contenuto",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.RICEVUTA_ASINCRONA_SIMMETRICA = new ComplexField(father,"ricevuta-asincrona-simmetrica",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.RICEVUTA_ASINCRONA_ASIMMETRICA = new ComplexField(father,"ricevuta-asincrona-asimmetrica",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.INTEGRAZIONE = new ComplexField(father,"integrazione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.ALLEGA_BODY = new ComplexField(father,"allega-body",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.SCARTA_BODY = new ComplexField(father,"scarta-body",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.GESTIONE_MANIFEST = new ComplexField(father,"gestione-manifest",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.STATELESS = new ComplexField(father,"stateless",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.LOCAL_FORWARD = new ComplexField(father,"local-forward",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"porta-delegata",PortaDelegata.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.PortaDelegataSoggettoErogatoreModel SOGGETTO_EROGATORE = null;
	 
	public org.openspcoop2.core.config.model.PortaDelegataServizioModel SERVIZIO = null;
	 
	public org.openspcoop2.core.config.model.PortaDelegataAzioneModel AZIONE = null;
	 
	public org.openspcoop2.core.config.model.ServizioApplicativoModel SERVIZIO_APPLICATIVO = null;
	 
	public org.openspcoop2.core.config.model.MtomProcessorModel MTOM_PROCESSOR = null;
	 
	public org.openspcoop2.core.config.model.MessageSecurityModel MESSAGE_SECURITY = null;
	 
	public org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel VALIDAZIONE_CONTENUTI_APPLICATIVI = null;
	 
	public org.openspcoop2.core.config.model.CorrelazioneApplicativaModel CORRELAZIONE_APPLICATIVA = null;
	 
	public org.openspcoop2.core.config.model.CorrelazioneApplicativaRispostaModel CORRELAZIONE_APPLICATIVA_RISPOSTA = null;
	 
	public IField ID_SOGGETTO = null;
	 
	public IField ID_ACCORDO = null;
	 
	public IField ID_PORT_TYPE = null;
	 
	public IField TIPO_SOGGETTO_PROPRIETARIO = null;
	 
	public IField NOME_SOGGETTO_PROPRIETARIO = null;
	 
	public IField STATO_MESSAGE_SECURITY = null;
	 
	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField LOCATION = null;
	 
	public IField AUTENTICAZIONE = null;
	 
	public IField AUTORIZZAZIONE = null;
	 
	public IField AUTORIZZAZIONE_CONTENUTO = null;
	 
	public IField RICEVUTA_ASINCRONA_SIMMETRICA = null;
	 
	public IField RICEVUTA_ASINCRONA_ASIMMETRICA = null;
	 
	public IField INTEGRAZIONE = null;
	 
	public IField ALLEGA_BODY = null;
	 
	public IField SCARTA_BODY = null;
	 
	public IField GESTIONE_MANIFEST = null;
	 
	public IField STATELESS = null;
	 
	public IField LOCAL_FORWARD = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 

	@Override
	public Class<PortaDelegata> getModeledClass(){
		return PortaDelegata.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}