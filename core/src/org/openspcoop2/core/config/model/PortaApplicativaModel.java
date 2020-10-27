/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.PortaApplicativaServizioApplicativoModel(new Field("servizio-applicativo",org.openspcoop2.core.config.PortaApplicativaServizioApplicativo.class,"porta-applicativa",PortaApplicativa.class));
		this.PROPRIETA_AUTENTICAZIONE = new org.openspcoop2.core.config.model.ProprietaModel(new Field("proprieta-autenticazione",org.openspcoop2.core.config.Proprieta.class,"porta-applicativa",PortaApplicativa.class));
		this.PROPRIETA_AUTORIZZAZIONE = new org.openspcoop2.core.config.model.ProprietaModel(new Field("proprieta-autorizzazione",org.openspcoop2.core.config.Proprieta.class,"porta-applicativa",PortaApplicativa.class));
		this.PROPRIETA_AUTORIZZAZIONE_CONTENUTO = new org.openspcoop2.core.config.model.ProprietaModel(new Field("proprieta-autorizzazione-contenuto",org.openspcoop2.core.config.Proprieta.class,"porta-applicativa",PortaApplicativa.class));
		this.XACML_POLICY = new Field("xacml-policy",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.SOGGETTI = new org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneSoggettiModel(new Field("soggetti",org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti.class,"porta-applicativa",PortaApplicativa.class));
		this.SERVIZI_APPLICATIVI_AUTORIZZATI = new org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneServiziApplicativiModel(new Field("servizi-applicativi-autorizzati",org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi.class,"porta-applicativa",PortaApplicativa.class));
		this.RUOLI = new org.openspcoop2.core.config.model.AutorizzazioneRuoliModel(new Field("ruoli",org.openspcoop2.core.config.AutorizzazioneRuoli.class,"porta-applicativa",PortaApplicativa.class));
		this.SCOPE = new org.openspcoop2.core.config.model.AutorizzazioneScopeModel(new Field("scope",org.openspcoop2.core.config.AutorizzazioneScope.class,"porta-applicativa",PortaApplicativa.class));
		this.GESTIONE_TOKEN = new org.openspcoop2.core.config.model.GestioneTokenModel(new Field("gestione-token",org.openspcoop2.core.config.GestioneToken.class,"porta-applicativa",PortaApplicativa.class));
		this.PROPRIETA = new org.openspcoop2.core.config.model.ProprietaModel(new Field("proprieta",org.openspcoop2.core.config.Proprieta.class,"porta-applicativa",PortaApplicativa.class));
		this.MTOM_PROCESSOR = new org.openspcoop2.core.config.model.MtomProcessorModel(new Field("mtom-processor",org.openspcoop2.core.config.MtomProcessor.class,"porta-applicativa",PortaApplicativa.class));
		this.MESSAGE_SECURITY = new org.openspcoop2.core.config.model.MessageSecurityModel(new Field("message-security",org.openspcoop2.core.config.MessageSecurity.class,"porta-applicativa",PortaApplicativa.class));
		this.VALIDAZIONE_CONTENUTI_APPLICATIVI = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel(new Field("validazione-contenuti-applicativi",org.openspcoop2.core.config.ValidazioneContenutiApplicativi.class,"porta-applicativa",PortaApplicativa.class));
		this.CORRELAZIONE_APPLICATIVA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaModel(new Field("correlazione-applicativa",org.openspcoop2.core.config.CorrelazioneApplicativa.class,"porta-applicativa",PortaApplicativa.class));
		this.CORRELAZIONE_APPLICATIVA_RISPOSTA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaRispostaModel(new Field("correlazione-applicativa-risposta",org.openspcoop2.core.config.CorrelazioneApplicativaRisposta.class,"porta-applicativa",PortaApplicativa.class));
		this.DUMP = new org.openspcoop2.core.config.model.DumpConfigurazioneModel(new Field("dump",org.openspcoop2.core.config.DumpConfigurazione.class,"porta-applicativa",PortaApplicativa.class));
		this.TRACCIAMENTO = new org.openspcoop2.core.config.model.PortaTracciamentoModel(new Field("tracciamento",org.openspcoop2.core.config.PortaTracciamento.class,"porta-applicativa",PortaApplicativa.class));
		this.GESTIONE_CORS = new org.openspcoop2.core.config.model.CorsConfigurazioneModel(new Field("gestione-cors",org.openspcoop2.core.config.CorsConfigurazione.class,"porta-applicativa",PortaApplicativa.class));
		this.RESPONSE_CACHING = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneModel(new Field("response-caching",org.openspcoop2.core.config.ResponseCachingConfigurazione.class,"porta-applicativa",PortaApplicativa.class));
		this.TRASFORMAZIONI = new org.openspcoop2.core.config.model.TrasformazioniModel(new Field("trasformazioni",org.openspcoop2.core.config.Trasformazioni.class,"porta-applicativa",PortaApplicativa.class));
		this.BEHAVIOUR = new org.openspcoop2.core.config.model.PortaApplicativaBehaviourModel(new Field("behaviour",org.openspcoop2.core.config.PortaApplicativaBehaviour.class,"porta-applicativa",PortaApplicativa.class));
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
		this.AUTENTICAZIONE = new Field("autenticazione",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.AUTENTICAZIONE_OPZIONALE = new Field("autenticazione-opzionale",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.AUTORIZZAZIONE = new Field("autorizzazione",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.AUTORIZZAZIONE_CONTENUTO = new Field("autorizzazione-contenuto",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.RICERCA_PORTA_AZIONE_DELEGATA = new Field("ricerca-porta-azione-delegata",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.SERVIZIO_APPLICATIVO_DEFAULT = new Field("servizio-applicativo-default",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.STATO = new Field("stato",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"porta-applicativa",PortaApplicativa.class);
		this.OPTIONS = new Field("options",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.CANALE = new Field("canale",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
	
	}
	
	public PortaApplicativaModel(IField father){
	
		super(father);
	
		this.SOGGETTO_VIRTUALE = new org.openspcoop2.core.config.model.PortaApplicativaSoggettoVirtualeModel(new ComplexField(father,"soggetto-virtuale",org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale.class,"porta-applicativa",PortaApplicativa.class));
		this.SERVIZIO = new org.openspcoop2.core.config.model.PortaApplicativaServizioModel(new ComplexField(father,"servizio",org.openspcoop2.core.config.PortaApplicativaServizio.class,"porta-applicativa",PortaApplicativa.class));
		this.AZIONE = new org.openspcoop2.core.config.model.PortaApplicativaAzioneModel(new ComplexField(father,"azione",org.openspcoop2.core.config.PortaApplicativaAzione.class,"porta-applicativa",PortaApplicativa.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.PortaApplicativaServizioApplicativoModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.core.config.PortaApplicativaServizioApplicativo.class,"porta-applicativa",PortaApplicativa.class));
		this.PROPRIETA_AUTENTICAZIONE = new org.openspcoop2.core.config.model.ProprietaModel(new ComplexField(father,"proprieta-autenticazione",org.openspcoop2.core.config.Proprieta.class,"porta-applicativa",PortaApplicativa.class));
		this.PROPRIETA_AUTORIZZAZIONE = new org.openspcoop2.core.config.model.ProprietaModel(new ComplexField(father,"proprieta-autorizzazione",org.openspcoop2.core.config.Proprieta.class,"porta-applicativa",PortaApplicativa.class));
		this.PROPRIETA_AUTORIZZAZIONE_CONTENUTO = new org.openspcoop2.core.config.model.ProprietaModel(new ComplexField(father,"proprieta-autorizzazione-contenuto",org.openspcoop2.core.config.Proprieta.class,"porta-applicativa",PortaApplicativa.class));
		this.XACML_POLICY = new ComplexField(father,"xacml-policy",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.SOGGETTI = new org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneSoggettiModel(new ComplexField(father,"soggetti",org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti.class,"porta-applicativa",PortaApplicativa.class));
		this.SERVIZI_APPLICATIVI_AUTORIZZATI = new org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneServiziApplicativiModel(new ComplexField(father,"servizi-applicativi-autorizzati",org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi.class,"porta-applicativa",PortaApplicativa.class));
		this.RUOLI = new org.openspcoop2.core.config.model.AutorizzazioneRuoliModel(new ComplexField(father,"ruoli",org.openspcoop2.core.config.AutorizzazioneRuoli.class,"porta-applicativa",PortaApplicativa.class));
		this.SCOPE = new org.openspcoop2.core.config.model.AutorizzazioneScopeModel(new ComplexField(father,"scope",org.openspcoop2.core.config.AutorizzazioneScope.class,"porta-applicativa",PortaApplicativa.class));
		this.GESTIONE_TOKEN = new org.openspcoop2.core.config.model.GestioneTokenModel(new ComplexField(father,"gestione-token",org.openspcoop2.core.config.GestioneToken.class,"porta-applicativa",PortaApplicativa.class));
		this.PROPRIETA = new org.openspcoop2.core.config.model.ProprietaModel(new ComplexField(father,"proprieta",org.openspcoop2.core.config.Proprieta.class,"porta-applicativa",PortaApplicativa.class));
		this.MTOM_PROCESSOR = new org.openspcoop2.core.config.model.MtomProcessorModel(new ComplexField(father,"mtom-processor",org.openspcoop2.core.config.MtomProcessor.class,"porta-applicativa",PortaApplicativa.class));
		this.MESSAGE_SECURITY = new org.openspcoop2.core.config.model.MessageSecurityModel(new ComplexField(father,"message-security",org.openspcoop2.core.config.MessageSecurity.class,"porta-applicativa",PortaApplicativa.class));
		this.VALIDAZIONE_CONTENUTI_APPLICATIVI = new org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel(new ComplexField(father,"validazione-contenuti-applicativi",org.openspcoop2.core.config.ValidazioneContenutiApplicativi.class,"porta-applicativa",PortaApplicativa.class));
		this.CORRELAZIONE_APPLICATIVA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaModel(new ComplexField(father,"correlazione-applicativa",org.openspcoop2.core.config.CorrelazioneApplicativa.class,"porta-applicativa",PortaApplicativa.class));
		this.CORRELAZIONE_APPLICATIVA_RISPOSTA = new org.openspcoop2.core.config.model.CorrelazioneApplicativaRispostaModel(new ComplexField(father,"correlazione-applicativa-risposta",org.openspcoop2.core.config.CorrelazioneApplicativaRisposta.class,"porta-applicativa",PortaApplicativa.class));
		this.DUMP = new org.openspcoop2.core.config.model.DumpConfigurazioneModel(new ComplexField(father,"dump",org.openspcoop2.core.config.DumpConfigurazione.class,"porta-applicativa",PortaApplicativa.class));
		this.TRACCIAMENTO = new org.openspcoop2.core.config.model.PortaTracciamentoModel(new ComplexField(father,"tracciamento",org.openspcoop2.core.config.PortaTracciamento.class,"porta-applicativa",PortaApplicativa.class));
		this.GESTIONE_CORS = new org.openspcoop2.core.config.model.CorsConfigurazioneModel(new ComplexField(father,"gestione-cors",org.openspcoop2.core.config.CorsConfigurazione.class,"porta-applicativa",PortaApplicativa.class));
		this.RESPONSE_CACHING = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneModel(new ComplexField(father,"response-caching",org.openspcoop2.core.config.ResponseCachingConfigurazione.class,"porta-applicativa",PortaApplicativa.class));
		this.TRASFORMAZIONI = new org.openspcoop2.core.config.model.TrasformazioniModel(new ComplexField(father,"trasformazioni",org.openspcoop2.core.config.Trasformazioni.class,"porta-applicativa",PortaApplicativa.class));
		this.BEHAVIOUR = new org.openspcoop2.core.config.model.PortaApplicativaBehaviourModel(new ComplexField(father,"behaviour",org.openspcoop2.core.config.PortaApplicativaBehaviour.class,"porta-applicativa",PortaApplicativa.class));
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
		this.AUTENTICAZIONE = new ComplexField(father,"autenticazione",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.AUTENTICAZIONE_OPZIONALE = new ComplexField(father,"autenticazione-opzionale",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.AUTORIZZAZIONE = new ComplexField(father,"autorizzazione",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.AUTORIZZAZIONE_CONTENUTO = new ComplexField(father,"autorizzazione-contenuto",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.RICERCA_PORTA_AZIONE_DELEGATA = new ComplexField(father,"ricerca-porta-azione-delegata",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.SERVIZIO_APPLICATIVO_DEFAULT = new ComplexField(father,"servizio-applicativo-default",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"porta-applicativa",PortaApplicativa.class);
		this.OPTIONS = new ComplexField(father,"options",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
		this.CANALE = new ComplexField(father,"canale",java.lang.String.class,"porta-applicativa",PortaApplicativa.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.PortaApplicativaSoggettoVirtualeModel SOGGETTO_VIRTUALE = null;
	 
	public org.openspcoop2.core.config.model.PortaApplicativaServizioModel SERVIZIO = null;
	 
	public org.openspcoop2.core.config.model.PortaApplicativaAzioneModel AZIONE = null;
	 
	public org.openspcoop2.core.config.model.PortaApplicativaServizioApplicativoModel SERVIZIO_APPLICATIVO = null;
	 
	public org.openspcoop2.core.config.model.ProprietaModel PROPRIETA_AUTENTICAZIONE = null;
	 
	public org.openspcoop2.core.config.model.ProprietaModel PROPRIETA_AUTORIZZAZIONE = null;
	 
	public org.openspcoop2.core.config.model.ProprietaModel PROPRIETA_AUTORIZZAZIONE_CONTENUTO = null;
	 
	public IField XACML_POLICY = null;
	 
	public org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneSoggettiModel SOGGETTI = null;
	 
	public org.openspcoop2.core.config.model.PortaApplicativaAutorizzazioneServiziApplicativiModel SERVIZI_APPLICATIVI_AUTORIZZATI = null;
	 
	public org.openspcoop2.core.config.model.AutorizzazioneRuoliModel RUOLI = null;
	 
	public org.openspcoop2.core.config.model.AutorizzazioneScopeModel SCOPE = null;
	 
	public org.openspcoop2.core.config.model.GestioneTokenModel GESTIONE_TOKEN = null;
	 
	public org.openspcoop2.core.config.model.ProprietaModel PROPRIETA = null;
	 
	public org.openspcoop2.core.config.model.MtomProcessorModel MTOM_PROCESSOR = null;
	 
	public org.openspcoop2.core.config.model.MessageSecurityModel MESSAGE_SECURITY = null;
	 
	public org.openspcoop2.core.config.model.ValidazioneContenutiApplicativiModel VALIDAZIONE_CONTENUTI_APPLICATIVI = null;
	 
	public org.openspcoop2.core.config.model.CorrelazioneApplicativaModel CORRELAZIONE_APPLICATIVA = null;
	 
	public org.openspcoop2.core.config.model.CorrelazioneApplicativaRispostaModel CORRELAZIONE_APPLICATIVA_RISPOSTA = null;
	 
	public org.openspcoop2.core.config.model.DumpConfigurazioneModel DUMP = null;
	 
	public org.openspcoop2.core.config.model.PortaTracciamentoModel TRACCIAMENTO = null;
	 
	public org.openspcoop2.core.config.model.CorsConfigurazioneModel GESTIONE_CORS = null;
	 
	public org.openspcoop2.core.config.model.ResponseCachingConfigurazioneModel RESPONSE_CACHING = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioniModel TRASFORMAZIONI = null;
	 
	public org.openspcoop2.core.config.model.PortaApplicativaBehaviourModel BEHAVIOUR = null;
	 
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
	 
	public IField AUTENTICAZIONE = null;
	 
	public IField AUTENTICAZIONE_OPZIONALE = null;
	 
	public IField AUTORIZZAZIONE = null;
	 
	public IField AUTORIZZAZIONE_CONTENUTO = null;
	 
	public IField RICERCA_PORTA_AZIONE_DELEGATA = null;
	 
	public IField SERVIZIO_APPLICATIVO_DEFAULT = null;
	 
	public IField STATO = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField OPTIONS = null;
	 
	public IField CANALE = null;
	 

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