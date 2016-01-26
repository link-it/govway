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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.PortaApplicativa;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaApplicativa 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaModel extends AbstractModel<PortaApplicativa> {

	public PortaApplicativaModel(){
	
		super();
	
		this.SOGGETTO_VIRTUALE = new org.openspcoop2.core.config.model.PortaApplicativaSoggettoVirtualeModel(new Field("soggetto-virtuale",org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale.class,"porta-applicativa",PortaApplicativa.class));
		this.SERVIZIO = new org.openspcoop2.core.config.model.PortaApplicativaServizioModel(new Field("servizio",org.openspcoop2.core.config.PortaApplicativaServizio.class,"porta-applicativa",PortaApplicativa.class));
		this.AZIONE = new org.openspcoop2.core.config.model.PortaApplicativaAzioneModel(new Field("azione",org.openspcoop2.core.config.PortaApplicativaAzione.class,"porta-applicativa",PortaApplicativa.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.ServizioApplicativoModel(new Field("servizio-applicativo",org.openspcoop2.core.config.ServizioApplicativo.class,"porta-applicativa",PortaApplicativa.class));
		this.PROPRIETA_PROTOCOLLO = new org.openspcoop2.core.config.model.ProprietaProtocolloModel(new Field("proprieta-protocollo",org.openspcoop2.core.config.ProprietaProtocollo.class,"porta-applicativa",PortaApplicativa.class));
		this.MTOM_PROCESSOR = new org.openspcoop2.core.config.model.MtomProcessorModel(new Field("mtom-processor",org.openspcoop2.core.config.MtomProcessor.class,"porta-applicativa",PortaApplicativa.class));
		this.MESSAGE_SECURITY = new org.openspcoop2.core.config.model.MessageSecurityModel(new Field("message-security",org.openspcoop2.core.config.MessageSecurity.class,"porta-applicativa",PortaApplicativa.class));
		this.VALIDAZIONE_CONTENUTI_APPLICATIVI = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel(new Field("validazione-contenuti-applicativi",org.openspcoop2.core.config.ValidazioneContenutiApplicativi.class,"porta-applicativa",PortaApplicativa.class));
		this.CORRELAZIONE_APPLICATIVA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaModel(new Field("correlazione-applicativa",org.openspcoop2.core.config.CorrelazioneApplicativa.class,"porta-applicativa",PortaApplicativa.class));
		this.CORRELAZIONE_APPLICATIVA_RISPOSTA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaRispostaModel(new Field("correlazione-applicativa-risposta",org.openspcoop2.core.config.CorrelazioneApplicativaRisposta.class,"porta-applicativa",PortaApplicativa.class));
		this.ID_SOGGETTO = new Field("id-soggetto",java.lang.Long.class,"porta-applicativa",PortaApplicativa.class);
		this.ID_ACCORDO = new Field("id-accordo",java.lang.Long.class,"porta-applicativa",PortaApplicativa.class);
		this.ID_PORT_TYPE = new Field("id-port-type",java.lang.Long.class,"porta-applicativa",PortaApplicativa.class);
		this.TIPO_SOGGETTO_PROPRIETARIO = new Field("tipo-soggetto-proprietario",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.NOME_SOGGETTO_PROPRIETARIO = new Field("nome-soggetto-proprietario",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.STATO_MESSAGE_SECURITY = new Field("stato-message-security",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.NOME = new Field("nome",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.RICEVUTA_ASINCRONA_SIMMETRICA = new Field("ricevuta-asincrona-simmetrica",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.RICEVUTA_ASINCRONA_ASIMMETRICA = new Field("ricevuta-asincrona-asimmetrica",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.INTEGRAZIONE = new Field("integrazione",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.ALLEGA_BODY = new Field("allega-body",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.SCARTA_BODY = new Field("scarta-body",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.GESTIONE_MANIFEST = new Field("gestione-manifest",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.STATELESS = new Field("stateless",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.BEHAVIOUR = new Field("behaviour",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.AUTORIZZAZIONE_CONTENUTO = new Field("autorizzazione-contenuto",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"porta-applicativa",PortaApplicativa.class);
	
	}
	
	public PortaApplicativaModel(IField father){
	
		super(father);
	
		this.SOGGETTO_VIRTUALE = new org.openspcoop2.core.config.model.PortaApplicativaSoggettoVirtualeModel(new ComplexField(father,"soggetto-virtuale",org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale.class,"porta-applicativa",PortaApplicativa.class));
		this.SERVIZIO = new org.openspcoop2.core.config.model.PortaApplicativaServizioModel(new ComplexField(father,"servizio",org.openspcoop2.core.config.PortaApplicativaServizio.class,"porta-applicativa",PortaApplicativa.class));
		this.AZIONE = new org.openspcoop2.core.config.model.PortaApplicativaAzioneModel(new ComplexField(father,"azione",org.openspcoop2.core.config.PortaApplicativaAzione.class,"porta-applicativa",PortaApplicativa.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.ServizioApplicativoModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.core.config.ServizioApplicativo.class,"porta-applicativa",PortaApplicativa.class));
		this.PROPRIETA_PROTOCOLLO = new org.openspcoop2.core.config.model.ProprietaProtocolloModel(new ComplexField(father,"proprieta-protocollo",org.openspcoop2.core.config.ProprietaProtocollo.class,"porta-applicativa",PortaApplicativa.class));
		this.MTOM_PROCESSOR = new org.openspcoop2.core.config.model.MtomProcessorModel(new ComplexField(father,"mtom-processor",org.openspcoop2.core.config.MtomProcessor.class,"porta-applicativa",PortaApplicativa.class));
		this.MESSAGE_SECURITY = new org.openspcoop2.core.config.model.MessageSecurityModel(new ComplexField(father,"message-security",org.openspcoop2.core.config.MessageSecurity.class,"porta-applicativa",PortaApplicativa.class));
		this.VALIDAZIONE_CONTENUTI_APPLICATIVI = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel(new ComplexField(father,"validazione-contenuti-applicativi",org.openspcoop2.core.config.ValidazioneContenutiApplicativi.class,"porta-applicativa",PortaApplicativa.class));
		this.CORRELAZIONE_APPLICATIVA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaModel(new ComplexField(father,"correlazione-applicativa",org.openspcoop2.core.config.CorrelazioneApplicativa.class,"porta-applicativa",PortaApplicativa.class));
		this.CORRELAZIONE_APPLICATIVA_RISPOSTA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaRispostaModel(new ComplexField(father,"correlazione-applicativa-risposta",org.openspcoop2.core.config.CorrelazioneApplicativaRisposta.class,"porta-applicativa",PortaApplicativa.class));
		this.ID_SOGGETTO = new ComplexField(father,"id-soggetto",java.lang.Long.class,"porta-applicativa",PortaApplicativa.class);
		this.ID_ACCORDO = new ComplexField(father,"id-accordo",java.lang.Long.class,"porta-applicativa",PortaApplicativa.class);
		this.ID_PORT_TYPE = new ComplexField(father,"id-port-type",java.lang.Long.class,"porta-applicativa",PortaApplicativa.class);
		this.TIPO_SOGGETTO_PROPRIETARIO = new ComplexField(father,"tipo-soggetto-proprietario",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.NOME_SOGGETTO_PROPRIETARIO = new ComplexField(father,"nome-soggetto-proprietario",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.STATO_MESSAGE_SECURITY = new ComplexField(father,"stato-message-security",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.RICEVUTA_ASINCRONA_SIMMETRICA = new ComplexField(father,"ricevuta-asincrona-simmetrica",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.RICEVUTA_ASINCRONA_ASIMMETRICA = new ComplexField(father,"ricevuta-asincrona-asimmetrica",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.INTEGRAZIONE = new ComplexField(father,"integrazione",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.ALLEGA_BODY = new ComplexField(father,"allega-body",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.SCARTA_BODY = new ComplexField(father,"scarta-body",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.GESTIONE_MANIFEST = new ComplexField(father,"gestione-manifest",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.STATELESS = new ComplexField(father,"stateless",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.BEHAVIOUR = new ComplexField(father,"behaviour",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.AUTORIZZAZIONE_CONTENUTO = new ComplexField(father,"autorizzazione-contenuto",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"porta-applicativa",PortaApplicativa.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.PortaApplicativaSoggettoVirtualeModel SOGGETTO_VIRTUALE = null;
	 
	public org.openspcoop2.core.config.model.PortaApplicativaServizioModel SERVIZIO = null;
	 
	public org.openspcoop2.core.config.model.PortaApplicativaAzioneModel AZIONE = null;
	 
	public org.openspcoop2.core.config.model.ServizioApplicativoModel SERVIZIO_APPLICATIVO = null;
	 
	public org.openspcoop2.core.config.model.ProprietaProtocolloModel PROPRIETA_PROTOCOLLO = null;
	 
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
	 
	public IField RICEVUTA_ASINCRONA_SIMMETRICA = null;
	 
	public IField RICEVUTA_ASINCRONA_ASIMMETRICA = null;
	 
	public IField INTEGRAZIONE = null;
	 
	public IField ALLEGA_BODY = null;
	 
	public IField SCARTA_BODY = null;
	 
	public IField GESTIONE_MANIFEST = null;
	 
	public IField STATELESS = null;
	 
	public IField BEHAVIOUR = null;
	 
	public IField AUTORIZZAZIONE_CONTENUTO = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 

	@Override
	public Class<PortaApplicativa> getModeledClass(){
		return PortaApplicativa.class;
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