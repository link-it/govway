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
package org.openspcoop2.web.monitor.statistiche.export;

import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.ConfigurazioneHandler;
import org.openspcoop2.core.config.ConfigurazioneMessageHandlers;
import org.openspcoop2.core.config.ConfigurazionePortaHandler;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneToken;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAutorizzazioneToken;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULL;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULLEcho;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.web.monitor.core.report.Templates;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPA;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPA.DettaglioSA;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPD;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioRateLimiting;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;
import org.openspcoop2.web.monitor.statistiche.utils.ConfigurazioniUtils;
import org.openspcoop2.web.monitor.statistiche.utils.IgnoreCaseComp;
import org.slf4j.Logger;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperCsvExporterBuilder;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * ConfigurazioniCsvExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ConfigurazioniCsvExporter {

	private Logger log = null;
	private List<String> chiaviColonne = null;
	private List<String> labelColonne = null;
	private PddRuolo ruolo = null;

	public ConfigurazioniCsvExporter(Logger log, PddRuolo ruolo) {
		this.log = log;
		this.ruolo = ruolo;
		this.chiaviColonne = new ArrayList<>();
		this.labelColonne = new ArrayList<>();
		this.init();
	}

	public void init(){
		if(this.ruolo.equals(PddRuolo.DELEGATA)) {
			// init colonne delegata

			/*
		Label Generali

    	azioni (dove viene indicata la singola azione, o le azioni o la scritta unica azione...)
    	url di invocazione
		Identificazione Azione: ....
  		Pattern
  		force interface based

		CORS

		Token (stato)
		Token (opzionale)
		Token (policy)
		Token (validazione input)
		Token (introspection)
		Token (user info)
		Token (forward) 
	    Autenticazione (stato)
	    Autenticazione (opzionale)
	    Autenticazione (proprieta)
		Autenticazione Token (issuer)
		Autenticazione Token (client_id)
		Autenticazione Token (subject)
		Autenticazione Token (username)
		Autenticazione Token (email)
	    
	    AttributeAuthority (attributi)
	    
	    Autorizzazione (stato)
	    Autorizzazione (proprieta)
	    
	    Autorizzazione Trasporto (Soggetti/Applicativi Autorizzati)
		Applicativi Autorizzati (Authz Trasporto) (la colonna contiene l'elenco dei soggetti  separati da '\n')
		Autorizzazione Trasporto (Ruoli Autorizzati) (la colonna contiene l'elenco dei ruoli separati da '\n')
		Ruoli Richiesti (Authz Trasporto) (all/any)
		Ruoli (Authz Trasporto) (separati da '\n')
		
		Autorizzazione Token (Applicativi Autorizzati)
		Applicativi Autorizzati (Authz Token) (la colonna contiene l'elenco dei soggetti  separati da '\n')
		Autorizzazione Token (Ruoli Autorizzati) (la colonna contiene l'elenco dei ruoli separati da '\n')
		Ruoli Richiesti (Authz Token) (all/any)
		Ruoli (Authz Token) (separati da '\n')
		
		Autorizzazione (Token claims)
		
		Autorizzazione per Token Scope
		Scope Richiesti (all/any)
		Scope (la colonna contiene l'elenco degli scope separati da '\n')
	    
	    AutorizzazioneContenuti (stato)
	    AutorizzazioneContenuti (proprieta)
	    
	    RateLimiting
	    
	    Validazione (Stato)
	    Validazione (Tipo)
	    Validazione (Accetta MTOM)
	    
	    Caching Risposta
	    
		Sicurezza Messaggio (Stato)
		Schema Sicurezza Richiesta
		Schema Sicurezza Risposta
		
	 	MTOM Richiesta
		MTOM Risposta
		
		Trasformazioni
		
		Correlazione Applicativa Richiesta
		Correlazione Applicativa Risposta
		
		Registrazione Messaggi
	  
	  	Proprieta
	  	
	  	Metadati
	  	Handlers
	  	
	  	Profilo Interoperabilità
	  
	    Servizio Applicativo
	    MessageBox
	    Sbustamento SOAP
	    Sbustamento Protocollo
	    Connettore (Tipo)
	    Connettore (EndPoint)
	    Connettore (Debug)
	    Connettore (Username)
	    Una singola colonna per ogni altri valore possibile per i connettori http e https denominandola come Connettore (Proxy Endpoint) , Connettore (SSLType) ...
	    Una restante colonna con Connettore (Altre configurazioni) dove si elencano le proprietà rimanenti e le proprietà custom separate da '\n'

			 * */

			this.labelColonne.add(CostantiConfigurazioni.LABEL_MODALITA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TIPO_ASPC);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_ASPC);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORT_TYPE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_FRUITORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_EROGATORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SERVIZIO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AZIONE_RISORSA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_GRUPPO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_STATO);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_URL_DI_INVOCAZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_IDENTIFICAZIONE_AZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PATTERN);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_FORCE_INTERFACE_BASED);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CORS);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_OPZIONALE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_POLICY);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_VALIDAZIONE_INPUT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_INTROSPECTION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_USER_INFO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_FORWARD);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_ISSUER);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_SUBJECT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_EMAIL); 

			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_OPZIONALE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_PROPRIETA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_ATTRIBUTE_AUTHORITY_ATTRIBUTI); 
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_PROPRIETA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RICHIEDENTI_AUTORIZZATI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RICHIEDENTI_APPLICATIVI_AUTORIZZATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RUOLI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RUOLI_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RUOLI_AUTORIZZATI);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_RICHIEDENTI_AUTORIZZATI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_RICHIEDENTI_APPLICATIVI_AUTORIZZATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_RUOLI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_RUOLI_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_RUOLI_AUTORIZZATI);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_CLAIMS);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_SCOPE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SCOPE_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SCOPE_FORNITI);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_CONTENUTI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_CONTENUTI_PROPRIETA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_RATE_LIMITING);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_VALIDAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_VALIDAZIONE_TIPO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_VALIDAZIONE_MTOM);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CACHING_RISPOSTA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_RICHIESTA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_RISPOSTA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_MTOM_RICHIESTA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_MTOM_RISPOSTA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TRASFORMAZIONI);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CORRELAZIONE_APPLICATIVA_RICHIESTA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CORRELAZIONE_APPLICATIVA_RISPOSTA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_REGISTRAZIONE_MESSAGGI);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PROPRIETA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_METADATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_HANDLERS);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONFIGURAZIONE_PROFILO);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_TIPO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_ENDPOINT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_DEBUG);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_TOKEN);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_PROXY_ENDPOINT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_PROXY_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_SSL_TYPE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HOSTNAME_VERIFIER);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_KEY_STORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_TRUST_STORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_CLIENT_CERTIFICATE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_ALTRE_CONFIGURAZIONI);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORTA_DELEGATA);

		} else {
			// init colonne applicativa   
			/*
		Label Generali 	
		
		url di invocazione
		Identificazione Azione: ....
  		Pattern
  		force interface based
  		
  		CORS
  		
		Token (stato)
		Token (opzionale)
		Token (policy)
		Token (validazione input)
		Token (introspection)
		Token (user info)
		Token (forward) 
	    Autenticazione (stato)
	    Autenticazione (opzionale)
		Autenticazione (proprieta)
		Autenticazione Token (issuer)
		Autenticazione Token (client_id)
		Autenticazione Token (subject)
		Autenticazione Token (username)
		Autenticazione Token (email)
	    
	    AttributeAuthority (attributi)
	    
	    Autorizzazione (stato)
	    Autorizzazione (proprieta)
	    
	    Autorizzazione Trasporto (Soggetti/Applicativi Autorizzati)
		Soggetti/Applicativi Autorizzati (Authz Trasporto) (la colonna contiene l'elenco dei soggetti  separati da '\n')
		Autorizzazione Trasporto (Ruoli Autorizzati) (la colonna contiene l'elenco dei ruoli separati da '\n')
		Ruoli Richiesti (Authz Trasporto) (all/any)
		Ruoli (Authz Trasporto) (separati da '\n')
		
		Autorizzazione Token (Applicativi Autorizzati)
		Applicativi Autorizzati (Authz Token) (la colonna contiene l'elenco dei soggetti  separati da '\n')
		Autorizzazione Token (Ruoli Autorizzati) (la colonna contiene l'elenco dei ruoli separati da '\n')
		Ruoli Richiesti (Authz Token) (all/any)
		Ruoli (Authz Token) (separati da '\n')
		
		Autorizzazione (Token claims)
		
		Autorizzazione per Token Scope
		Scope Richiesti (all/any)
		Scope (la colonna contiene l'elenco degli scope separati da '\n')
	    	    
	    AutorizzazioneContenuti (stato)
	    AutorizzazioneContenuti (proprieta)
	    
	    RateLimiting
	    
	    Validazione (Stato)
	    Validazione (Tipo)
	    Validazione (Accetta MTOM)
	    
	    Caching Risposta
	    
		Sicurezza Messaggio (Stato)
		Schema Sicurezza Richiesta
		Schema Sicurezza Risposta
		
	 	MTOM Richiesta
		MTOM Risposta
		
		Trasformazioni
		
		Correlazione Applicativa Richiesta
		Correlazione Applicativa Risposta
	  
		Registrazione Messaggi
		
	  	Proprieta
	  	
	  	Metadati
	  	Handlers
	  	
	  	Profilo Interoperabilità
	    
	    MessageBox
	    Sbustamento SOAP
	    Sbustamento Protocollo
	    Connettore (Tipo)
	    Connettore (EndPoint)
	    Connettore (Debug)
	    Connettore (Username)
	    Una singola colonna per ogni altri valore possibile per i connettori http e https denominandola come Connettore (Proxy Endpoint) , Connettore (SSLType) ...
	    Una restante colonna con Connettore (Altre configurazioni) dove si elencano le proprietà rimanenti e le proprietà custom separate da '\n'

		NomeConnettore
		NomeApplicativoServer

			 * */


			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_MODALITA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TIPO_ASPC);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_ASPC);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORT_TYPE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_EROGATORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SERVIZIO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AZIONE_RISORSA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_GRUPPO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_STATO);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_URL_DI_INVOCAZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_IDENTIFICAZIONE_AZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PATTERN);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_FORCE_INTERFACE_BASED);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CORS);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_OPZIONALE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_POLICY);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_VALIDAZIONE_INPUT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_INTROSPECTION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_USER_INFO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_FORWARD);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_ISSUER);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_SUBJECT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_EMAIL);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_OPZIONALE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_PROPRIETA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_ATTRIBUTE_AUTHORITY_ATTRIBUTI); 
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_PROPRIETA);
						
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RICHIEDENTI_AUTORIZZATI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RICHIEDENTI_SOGGETTI_AUTORIZZATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RICHIEDENTI_APPLICATIVI_AUTORIZZATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RUOLI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RUOLI_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TRASPORTO_RUOLI_AUTORIZZATI);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_RICHIEDENTI_AUTORIZZATI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_RICHIEDENTI_APPLICATIVI_AUTORIZZATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_RUOLI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_RUOLI_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_RUOLI_AUTORIZZATI);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_CLAIMS);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_SCOPE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SCOPE_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SCOPE_FORNITI);			
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_CONTENUTI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_CONTENUTI_PROPRIETA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_RATE_LIMITING);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_VALIDAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_VALIDAZIONE_TIPO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_VALIDAZIONE_MTOM);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CACHING_RISPOSTA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_RICHIESTA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_RISPOSTA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_MTOM_RICHIESTA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_MTOM_RISPOSTA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TRASFORMAZIONI);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CORRELAZIONE_APPLICATIVA_RICHIESTA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CORRELAZIONE_APPLICATIVA_RISPOSTA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_REGISTRAZIONE_MESSAGGI);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PROPRIETA);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_METADATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_HANDLERS);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONFIGURAZIONE_PROFILO);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_MESSAGE_BOX);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SBUSTAMENTO_SOAP);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SBUSTAMENTO_PROTOCOLLO);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_TIPO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_ENDPOINT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_DEBUG);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_TOKEN);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_PROXY_ENDPOINT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_PROXY_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_SSL_TYPE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HOSTNAME_VERIFIER);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_KEY_STORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_TRUST_STORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_CLIENT_CERTIFICATE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_ALTRE_CONFIGURAZIONI);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORTA_APPLICATIVA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORTA_APPLICATIVA_NOME_CONNETTORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORTA_APPLICATIVA_APPLICATIVO_SERVER);

		}

		for (int i = 0; i < this.labelColonne.size(); i++) {
			this.chiaviColonne.add(CostantiConfigurazioni.PREFIX_COLONNA + i);
		}
	}

	public String exportConfigurazioni(List<ConfigurazioneGenerale> listaConfigurazioni, java.io.OutputStream out) throws Exception {
		String errMsg = null;

		DRDataSource dataSource = creaDatasourceConfigurazioni(this.chiaviColonne, this.log);

		popolaDataSourceExport(dataSource,listaConfigurazioni);

		JasperReportBuilder reportBuilder = creaReportBuilder(dataSource, this.log);

		this.esportaCsv(out, reportBuilder, this.chiaviColonne, this.labelColonne);

		return errMsg;
	}


	public void esportaCsv(OutputStream outputStream, JasperReportBuilder report,List<String> chiaviColonne, List<String> labelColonne) throws Exception{
		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();

		// generazione delle label delle colonne
		for (int i = 0; i < labelColonne.size(); i++) {
			String label = labelColonne.get(i);
			String keyColonna = chiaviColonne.get(i);
			TextColumnBuilder<String> nomeColumn = col.column(label, keyColonna, type.stringType());
			colonne.add(nomeColumn);
		}

		report
		.setColumnTitleStyle(Templates.columnTitleStyle)
		.addProperty("net.sf.jasperreports.export.csv.exclude.origin.keep.first.band.1", "columnHeader")
		.ignorePageWidth()
		.ignorePagination()
		.columns(colonne.toArray(new ColumnBuilder[colonne.size()]));

		JasperCsvExporterBuilder builder = export.csvExporter(outputStream);
		report.toCsv(builder); 
	}

	public JasperReportBuilder creaReportBuilder(JRDataSource dataSource,Logger log) throws Exception{
		JasperReportBuilder builder = report();
		builder.setDataSource(dataSource);
		return builder;
	}

	private DRDataSource creaDatasourceConfigurazioni(List<String> colonneSelezionate,Logger log) throws Exception {
		// Scittura Intestazione sono le chiavi delle colonne scelte
		List<String> header = new ArrayList<String>();
		header.addAll(colonneSelezionate);

		DRDataSource dataSource = new DRDataSource(header.toArray(new String[header.size()])); 
		return dataSource;
	}

	private void popolaDataSourceExport(DRDataSource dataSource, List<ConfigurazioneGenerale> lstConfigurazioni) throws Exception  {

		for(ConfigurazioneGenerale configurazione: lstConfigurazioni){
			if(this.ruolo.equals(PddRuolo.DELEGATA)) {
				this.addLinePD(dataSource,configurazione);
			} else {
				List<DettaglioSA> listaSA = configurazione.getPa().getListaSA();
				// se ho piu' di un servizio applicativo aggiungo un linea per servizio
				if(listaSA == null || listaSA.size() <= 1){
					DettaglioSA dettaglioSA = (listaSA != null && listaSA.size() > 0) ? listaSA.get(0): null;
					this.addLinePA(dataSource,configurazione, dettaglioSA);
				} else {
					// Ordino per nome connettore
					
					DettaglioPA dettaglioPA = configurazione.getPa();
					PortaApplicativa paOp2 = dettaglioPA.getPortaApplicativaOp2(); 
					
					List<String> orderFix = new ArrayList<String>();
					Map<String, DettaglioSA> mapIds = new HashMap<String, DettaglioSA>();
					for (DettaglioSA dettaglioSA : listaSA) {
						
						String nomePAConnettore = null;
						if(dettaglioSA != null) {
							ServizioApplicativo saOp2 = dettaglioSA.getSaOp2();
							if(StringUtils.isNotEmpty(saOp2.getNome()) && paOp2.sizeServizioApplicativoList()>0) {
								for (PortaApplicativaServizioApplicativo pasa : paOp2.getServizioApplicativoList()) {
									if(saOp2.getNome().equals(pasa.getNome())) {
										if(pasa.getDatiConnettore()!=null && StringUtils.isNotEmpty(pasa.getDatiConnettore().getNome())) {
											nomePAConnettore = pasa.getDatiConnettore().getNome();
										}
									}
								}
							}
						}
						nomePAConnettore = (nomePAConnettore==null) ? CostantiConfigurazione.NOME_CONNETTORE_DEFAULT : nomePAConnettore;
						
						String checkNome = nomePAConnettore;
						int index = 1;
						while(orderFix.contains(checkNome)) {
							checkNome = nomePAConnettore + "###" + index;
							index++;
						}
						nomePAConnettore = checkNome;
						
						orderFix.add(nomePAConnettore);
						mapIds.put(nomePAConnettore, dettaglioSA);
						
					}
					
					Collections.sort(orderFix, new IgnoreCaseComp()); // per adeguarsi ai db
					for (String nomeConnettore : orderFix) {
						this.addLinePA(dataSource,configurazione, mapIds.get(nomeConnettore));	
					}
					
				}
			}
		}//chiudo for configurazioni
	}

	private void addLinePA(DRDataSource dataSource, ConfigurazioneGenerale configurazione, DettaglioSA dettaglioSA) throws Exception  {
		List<Object> oneLine = new ArrayList<Object>();
		DettaglioPA dettaglioPA = configurazione.getPa();
		PortaApplicativa paOp2 = dettaglioPA.getPortaApplicativaOp2(); 
		org.openspcoop2.core.commons.search.PortaApplicativa portaApplicativa = dettaglioPA.getPortaApplicativa();
		PortaApplicativaAzione paAzione = paOp2.getAzione();
		MappingErogazionePortaApplicativa mappingPA = dettaglioPA.getMappingErogazionePortaApplicativaOp2();
		PortaApplicativa paOp2Default = dettaglioPA.getPortaApplicativaDefaultOp2(); 
		DettaglioRateLimiting rateLimiting = dettaglioPA.getRateLimiting();
		
		// Modalita
		String protocollo = configurazione.getProtocollo();
		if(StringUtils.isNotEmpty(protocollo))
			oneLine.add(NamingUtils.getLabelProtocollo(protocollo));
		else 
			oneLine.add("");
		
		// TIPO ASPC
		if(dettaglioPA.getIdAccordoServizioParteComune() != null) {
			IdAccordoServizioParteComune aspc = dettaglioPA.getIdAccordoServizioParteComune();
			if(aspc.getServiceBinding()!=null) {
				oneLine.add(aspc.getServiceBinding());
			}else { 
				oneLine.add("");
			}
		} else { 
			oneLine.add("");
		}
		
		// ASPC
		if(dettaglioPA.getIdAccordoServizioParteComune() != null) {
			IdAccordoServizioParteComune aspc = dettaglioPA.getIdAccordoServizioParteComune();
			String nomeAspc = aspc.getNome();

			Integer versioneAspc = aspc.getVersione();

			String nomeReferenteAspc = (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getNome() : null;

			String tipoReferenteAspc= (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getTipo() : null;
			
			oneLine.add(NamingUtils.getLabelAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc)));
		} else 
			oneLine.add("");
		
		// PORTTYPE
		if(StringUtils.isNotEmpty(dettaglioPA.getPortType()))
			oneLine.add(dettaglioPA.getPortType());
		else 
			oneLine.add("");

		// EROGATORE
		if(StringUtils.isNotEmpty(configurazione.getErogatore()))
			oneLine.add(configurazione.getErogatore());
		else 
			oneLine.add("");

		// SERVIZIO
		if(StringUtils.isNotEmpty(configurazione.getServizio()))
			oneLine.add(configurazione.getServizio());
		else 
			oneLine.add("");

		// AZIONE
		if(StringUtils.isNotEmpty(portaApplicativa.getNomeAzione())){  // TODO controllare lista azioni
			// Azione: _XXX
			oneLine.add(portaApplicativa.getNomeAzione());
		}
		else{
			List<String> azioni = dettaglioPA.getAzioni(); 
			if(paAzione==null && (azioni == null || azioni.size() == 0)){
				oneLine.add(CostantiConfigurazioni.LABEL_UTILIZZO_DEL_SERVIZIO_SENZA_AZIONE);
			}
			else{
				StringBuilder sb = new StringBuilder();
				// Azioni: XXXs
				if(azioni != null && azioni.size() > 0){
					for (String azione : azioni) {
						if(sb.length()>0) sb.append("\n");
						sb.append(azione);
					}
				}
				oneLine.add(sb.toString());
			}	
		}
		
		// GRUPPO
		if(mappingPA!=null && mappingPA.getDescrizione()!=null) {
			oneLine.add(mappingPA.getDescrizione());
		}
		else {
			oneLine.add("");
		}
		
		// STATO
		if(StringUtils.isNotEmpty(configurazione.getStato()))
			oneLine.add(configurazione.getStato());
		else 
			oneLine.add("");
		
		// URL INVOCAZIONE
		String urlInvocazione = dettaglioPA.getUrlInvocazione();
		
		if(StringUtils.isNotEmpty(urlInvocazione))
			oneLine.add(urlInvocazione);
		else 
			oneLine.add("");
		
		
		if(paAzione != null) {
			// Modalita identificazione azione
			if(paAzione.getIdentificazione() != null){
				oneLine.add(paAzione.getIdentificazione().toString());
			} else {
				oneLine.add("");
			}
			
			//Pattern
			if(StringUtils.isNotEmpty(paAzione.getPattern()))
				oneLine.add(paAzione.getPattern());
			else 
				oneLine.add("");
			
			// force interface based
			if(paAzione.getForceInterfaceBased() != null){
				oneLine.add(paAzione.getForceInterfaceBased().getValue());
			} else {
				oneLine.add("");
			}
			
		} else {
			// Modalita identificazione azione
			oneLine.add("");
			
			//Pattern
			oneLine.add("");
			
			// force interface based
			oneLine.add("");
		}
		
		// CORS
		if(paOp2Default!=null) {
			if(paOp2Default.getGestioneCors()!=null) {
				oneLine.add(paOp2Default.getGestioneCors().getStato().getValue());
			}
			else {
				oneLine.add(CostantiConfigurazioni.VALUE_DEFAULT);
			}
		}
		else {
			if(paOp2.getGestioneCors()!=null) {
				oneLine.add(paOp2.getGestioneCors().getStato().getValue());
			}
			else {
				oneLine.add(CostantiConfigurazioni.VALUE_DEFAULT);
			}
		}
		
		// Token (stato)
		// Token (opzionale)
		// Token (policy)
		// Token (validazione input)
		// Token (introspection)
		// Token (user info)
		// Token (forward)
		GestioneToken gestioneToken = paOp2.getGestioneToken();
		GestioneTokenAutenticazione gestioneTokenAutenticazione = null;
		if(gestioneToken != null) {
			gestioneTokenAutenticazione = gestioneToken.getAutenticazione(); 
			String policy = gestioneToken.getPolicy();
			StatoFunzionalita tokenOpzionale = gestioneToken.getTokenOpzionale() != null ? gestioneToken.getTokenOpzionale()  : StatoFunzionalita.DISABILITATO;
			if(policy != null) {
				oneLine.add(StatoFunzionalita.ABILITATO.getValue());
				oneLine.add(tokenOpzionale.getValue());
				oneLine.add(policy);
				oneLine.add(gestioneToken.getValidazione() != null ? gestioneToken.getValidazione().getValue() : ""); 
				oneLine.add(gestioneToken.getIntrospection() != null ? gestioneToken.getIntrospection().getValue() : "");
				oneLine.add(gestioneToken.getUserInfo() != null ? gestioneToken.getUserInfo().getValue() : "");
				oneLine.add(gestioneToken.getForward() != null ? gestioneToken.getForward().getValue() : "");
				
			} else {
				// sette colonne vuote
				oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
				oneLine.add(tokenOpzionale.getValue());
				oneLine.add("");
				oneLine.add("");
				oneLine.add("");
				oneLine.add("");
				oneLine.add("");
			}
		} else {
			// sette colonne vuote
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
		}
		
		// Autenticazione Token (issuer)
		// Autenticazione Token (client_id)
		// Autenticazione Token (subject)
		// Autenticazione Token (username)
		// Autenticazione Token (email)
		if(gestioneTokenAutenticazione != null) {
			oneLine.add(gestioneTokenAutenticazione.getIssuer()!=null ? gestioneTokenAutenticazione.getIssuer().getValue() : StatoFunzionalita.DISABILITATO.getValue() );
			oneLine.add(gestioneTokenAutenticazione.getClientId()!=null ? gestioneTokenAutenticazione.getClientId().getValue() : StatoFunzionalita.DISABILITATO.getValue() );
			oneLine.add(gestioneTokenAutenticazione.getSubject()!=null ? gestioneTokenAutenticazione.getSubject().getValue() : StatoFunzionalita.DISABILITATO.getValue() );
			oneLine.add(gestioneTokenAutenticazione.getUsername()!=null ? gestioneTokenAutenticazione.getUsername().getValue() : StatoFunzionalita.DISABILITATO.getValue() );
			oneLine.add(gestioneTokenAutenticazione.getEmail()!=null ? gestioneTokenAutenticazione.getEmail().getValue() : StatoFunzionalita.DISABILITATO.getValue() );
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
		}
		
		// Autenticazione (stato)
		// Autenticazione (opzionale)
		// Autenticazione (proprieta)
		if(dettaglioPA.isSupportatoAutenticazione()){
			
			if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(paOp2.getAutenticazione())){
				oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
			}
			else{
				oneLine.add(paOp2.getAutenticazione());
			}

			if(CostantiConfigurazione.ABILITATO.equals(paOp2.getAutenticazioneOpzionale())){
				oneLine.add(CostantiConfigurazione.ABILITATO.getValue());
			}
			else{
				oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
			}
			
			oneLine.add(this.toStringProprieta(paOp2.getProprietaAutenticazioneList()));
			
		} else {
			// tre colonne vuote
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
		}
		
		// AttributeAuthority (attributi)
		oneLine.add(this.toStringAttributeAuthority(paOp2.getAttributeAuthorityList()));
		
		// Autorizzazione (Stato)
		// Autorizzazione (proprieta)
		
		// Autorizzazione Trasporto (Soggetti/Applicativi Autorizzati)
		// Soggetti/Applicativi Autorizzati (Authz Trasporto)
		// Autorizzazione Trasporto (Ruoli Autorizzati)
		// Ruoli Richiesti (Authz Trasporto)
		// Ruoli Forniti (Authz Trasporto)
		
		// Autorizzazione Token (Applicativi Autorizzati)
		// Applicativi Autorizzati (Authz Token)
		// Autorizzazione Token (Ruoli Autorizzati)
		// Ruoli Richiesti (Authz Token)
		// Ruoli Forniti (Authz Token)
		
		// Autorizzazione per Token Scope
		// Scope Richiesti
		// Scope Forniti
		
		// Autorizzazione per Token Claims
		
		// Soggetti Autorizzati la colonna contiene l'elenco dei soggetti  separati da '\n').
		// Ruoli (separati da '\n')
		// Ruoli Richiesti (all/any)

		// Autorizzazione (stato): disabilitato/abilitato/xacmlPolicy/NomeCustom
		String autorizzazione = paOp2.getAutorizzazione();
		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(autorizzazione)){
			oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.XACML_POLICY.getValue().toLowerCase())){
			oneLine.add(TipoAutorizzazione.XACML_POLICY.getValue());
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())
				||
				autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			oneLine.add(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			oneLine.add(autorizzazione);
		}

		// Autorizzazione (proprieta)
		oneLine.add(this.toStringProprieta(paOp2.getProprietaAutorizzazioneList()));
		
		// Autorizzazione Trasporto (Soggetti Autorizzati)
		// Se abilitato:
		// Servizi Applicativi Autorizzati: sa1 (user:xxx)
		//                                  sa2 (user:xxx)
		//                                  sa3 (user:xxx)
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
			/*
			List<String> fruitori = dettaglioPA.getFruitori();

			if(fruitori != null && fruitori.size() > 0){
				StringBuilder sb = new StringBuilder();
				for (String fruitore : fruitori) {
					if(sb.length()>0) sb.append("\n");
					sb.append(fruitore);
				}
				oneLine.add(sb.toString());
			} else 
				oneLine.add("");
			 */
			
			// devo elencare i soggetti autorizzati
			if(paOp2.getSoggetti()!=null && paOp2.getSoggetti().sizeSoggettoList()>0) {
				StringBuilder sb = new StringBuilder();
				for (PortaApplicativaAutorizzazioneSoggetto soggetto : paOp2.getSoggetti().getSoggettoList()) {
					if(sb.length()>0) sb.append("\n");
					sb.append(soggetto.getTipo()+"/"+soggetto.getNome());
				}
				oneLine.add(sb.toString());
			}
			else {
				oneLine.add("");
			}

			// devo elencare gli applicativi autorizzati
			if(paOp2.getServiziApplicativiAutorizzati()!=null && paOp2.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
				StringBuilder sb = new StringBuilder();
				for (PortaApplicativaAutorizzazioneServizioApplicativo sa : paOp2.getServiziApplicativiAutorizzati().getServizioApplicativoList()) {
					if(sb.length()>0) sb.append("\n");
						sb.append(sa.getNome() + " soggetto:"+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario()+"");
				}
				oneLine.add(sb.toString());
			}
			else {
				oneLine.add("");
			}
			
		}else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}

		// Autorizzazione Trasporto (Ruoli Autorizzati)
		// Ruoli Richiesti: tutti/almenoUno
		// Ruoli Autorizzati: ruolo1 (fonte esterna)
		//                    ruolo2 (fonte interna)
		//                    ruolo3 (fonte qualsiasi)
		//
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())){
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
			
			List<String> ruoli = dettaglioPA.getRuoli();
			String match = dettaglioPA.getMatchRuoli();
			oneLine.add(match);
			if(paOp2.getRuoli()!=null){
				StringBuilder sb = new StringBuilder();

				for (String ruolo : ruoli) {
					if(sb.length()>0) sb.append("\n");
					sb.append(ruolo);
				}

				oneLine.add(sb.toString());
			} else {
				oneLine.add("");
			}
			
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
		
		// Autorizzazione Token (Applicativi Autorizzati)
		// Applicativi Autorizzati (Authz Token)
		PortaApplicativaAutorizzazioneToken autorizzazioneToken = paOp2.getAutorizzazioneToken();
		if(autorizzazioneToken!=null && StatoFunzionalita.ABILITATO.equals(autorizzazioneToken.getAutorizzazioneApplicativi())){
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
			StringBuilder sb = new StringBuilder();
			if(autorizzazioneToken.getServiziApplicativi()!=null && autorizzazioneToken.getServiziApplicativi().sizeServizioApplicativoList()>0) {
				for (PortaApplicativaAutorizzazioneServizioApplicativo sa : autorizzazioneToken.getServiziApplicativi().getServizioApplicativoList()) {
					if(sb.length()>0) sb.append("\n");
						sb.append(sa.getNome() + " soggetto:"+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario()+"");
				}
			}
			oneLine.add(sb.toString());
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
		}
		
		// Autorizzazione Token (Ruoli Autorizzati)
		// Ruoli Richiesti (Authz Token)
		// Ruoli Forniti (Authz Token)
		if(autorizzazioneToken!=null && StatoFunzionalita.ABILITATO.equals(autorizzazioneToken.getAutorizzazioneRuoli())){
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
			String match = autorizzazioneToken.getRuoli()!=null && autorizzazioneToken.getRuoli().getMatch()!=null ? 
					(org.openspcoop2.core.config.constants.RuoloTipoMatch.ANY.equals(autorizzazioneToken.getRuoli().getMatch()) ? CostantiConfigurazioni.LABEL_RUOLI_ANY : CostantiConfigurazioni.LABEL_RUOLI_ALL) 
					: 
					"";
			oneLine.add(match);
			if(autorizzazioneToken.getRuoli()!=null && autorizzazioneToken.getRuoli().sizeRuoloList()>0){
				StringBuilder sb = new StringBuilder();
				for (Ruolo ruolo : autorizzazioneToken.getRuoli().getRuoloList()) {
					if(sb.length()>0) sb.append("\n");
						sb.append(ruolo.getNome());
				}
				oneLine.add(sb.toString());
			} else {
				oneLine.add("");
			}
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
		
		// Autorizzazione (Token Claims)
		
		if(gestioneToken != null) {
			if(StringUtils.isNotEmpty(gestioneToken.getOptions()))
				oneLine.add(gestioneToken.getOptions());
			else 
				oneLine.add("");
		}else {
			oneLine.add("");
		}
		
		// Autorizzazione (Scope)
		// Scope Richiesti
		// Scope Forniti
		AutorizzazioneScope scope = paOp2.getScope();
		if(scope != null) {
			oneLine.add(scope.getStato()!=null ? scope.getStato().getValue() : "");
			oneLine.add(scope.getMatch()!=null ? scope.getMatch().getValue() : "");
			
			if(scope.getScopeList()!=null){
				StringBuilder sb = new StringBuilder();

				for (Scope scopeS : scope.getScopeList()) {
					if(sb.length()>0) sb.append("\n");
					sb.append(scopeS.getNome());
				}

				oneLine.add(sb.toString());
			} else {
				oneLine.add("");
			}
			
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
		
		// Autorizzazione Contenuti (Stato)
		String autorizzazioneContenuti = paOp2.getAutorizzazioneContenuto();
		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(autorizzazioneContenuti)){
			oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
		}
		else if(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(autorizzazioneContenuti)){
			oneLine.add(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			oneLine.add(autorizzazioneContenuti);
		}

		// Autorizzazione Contenuti (proprieta)
		oneLine.add(this.toStringProprieta(paOp2.getProprietaAutorizzazioneContenutoList()));
		
		// Rate Limiting
		if(rateLimiting!=null) {
			oneLine.add(rateLimiting.getAsCSVRecord());
		}
		else {
			oneLine.add("");
		}
		
	    //Validazione (Stato)
	    //Validazione (Tipo)
	    //Validazione (Accetta MTOM)
		
		ValidazioneContenutiApplicativi validazioneContenutiApplicativi = paOp2.getValidazioneContenutiApplicativi();
		if(validazioneContenutiApplicativi != null) {
			oneLine.add(validazioneContenutiApplicativi.getStato().getValue());
			oneLine.add(validazioneContenutiApplicativi.getTipo().getValue());
			oneLine.add(validazioneContenutiApplicativi.getAcceptMtomMessage().getValue());
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
		
		// Caching Risposta
		if(paOp2.getResponseCaching()!=null) {
			oneLine.add(paOp2.getResponseCaching().getStato().getValue());
		}
		else {
			oneLine.add(CostantiConfigurazioni.VALUE_DEFAULT);
		}
		
		// Sicurezza Messaggio (Stato)
		// Schema Sicurezza Richiesta
		// Schema Sicurezza Risposta
		MessageSecurity messageSecurity = paOp2.getMessageSecurity();
		String requestMode = null;
		String responseMode = null; 
		
		if(messageSecurity != null) {
			if(messageSecurity.getRequestFlow() != null){
				requestMode = messageSecurity.getRequestFlow().getMode();
			}
			if(messageSecurity.getResponseFlow() != null){
				responseMode = messageSecurity.getResponseFlow().getMode();
			}
		}
		String statoMessageSecurity= paOp2.getStatoMessageSecurity();
		
		if(StringUtils.isNotEmpty(statoMessageSecurity)) {
			oneLine.add(statoMessageSecurity);
			
			if(StatoFunzionalita.ABILITATO.getValue().equals(statoMessageSecurity)) {
				if(StringUtils.isNotEmpty(requestMode)) {
					if(requestMode.equals(CostantiConfigurazioni.VALUE_SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT)) {
						oneLine.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_CONFIGURAZIONE_MANUALE);
					} else {
						oneLine.add(requestMode);
					}
				} else {
					oneLine.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_NESSUNO);
				}
				
				if(StringUtils.isNotEmpty(responseMode)) {
					if(responseMode.equals(CostantiConfigurazioni.VALUE_SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT)) {
						oneLine.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_CONFIGURAZIONE_MANUALE);
					} else {
						oneLine.add(responseMode);
					}
				} else {
					oneLine.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_NESSUNO);
				}
			} else {
				oneLine.add("");
				oneLine.add("");
			}
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
		
		
		// MTOM Richiesta
		// MTOM Risposta
		MtomProcessor mtomProcessor = paOp2.getMtomProcessor();
		
		if(mtomProcessor != null) {
			MtomProcessorFlow requestFlow = mtomProcessor.getRequestFlow();
			if(requestFlow != null) {
				oneLine.add(requestFlow.getMode().getValue());
			} else {
				oneLine.add("");
			}
			
			MtomProcessorFlow responseFlow = mtomProcessor.getResponseFlow();
			if(responseFlow != null) {
				oneLine.add(responseFlow.getMode().getValue());
			} else {
				oneLine.add("");
			}
		} else {
			oneLine.add("");
			oneLine.add("");
		}
		
		// Trasformazioni
		oneLine.add(toStringTrasformazioni(paOp2.getTrasformazioni()));
		
		// Correlazione Applicativa Richiesta
		// Correlazione Applicativa Risposta
		int numCorrelazioneReq = 0;
		int numCorrelazioneRes = 0;

		CorrelazioneApplicativa ca = paOp2.getCorrelazioneApplicativa();
		if (ca != null)
			numCorrelazioneReq = ca.sizeElementoList();

		if (paOp2.getCorrelazioneApplicativaRisposta() != null)
			numCorrelazioneRes = paOp2.getCorrelazioneApplicativaRisposta().sizeElementoList();
		
		if(numCorrelazioneReq > 0) {
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
		}
		
		if(numCorrelazioneRes > 0) {
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
		}
		
		// Registrazione Messaggi
		oneLine.add(toStringDump(paOp2.getDump()));
		
		// Proprieta
		oneLine.add(this.toStringProprieta(paOp2.getProprietaList()));

		// Metadati
		if(StringUtils.isNotEmpty(paOp2.getIntegrazione())){
			oneLine.add(paOp2.getIntegrazione());
		}
		else {
			oneLine.add("");
		}
		
		// Handlers
		oneLine.add(toStringHandlers(paOp2.getConfigurazioneHandler()));
		
		// Profilo Interoperabilità
		if(StringUtils.isNotEmpty(dettaglioPA.getConfigurazioneProfilo())) {
			oneLine.add(dettaglioPA.getConfigurazioneProfilo());
		}
		else {
			oneLine.add("");
		}
		
		// colonne servizio applicativo e relativo connettore
		if(dettaglioSA != null) {
			ServizioApplicativo saOp2 = dettaglioSA.getSaOp2();
			// colonne del servizio applicativo NOME - MESSAGE BOX - SBUSTAMENTO SOAP - SBUSTAMENTO PROTOCOLLO
//			if(StringUtils.isNotEmpty(saOp2.getNome()))
//				oneLine.add(saOp2.getNome());
//			else 
//				oneLine.add("");

			if(saOp2.getInvocazioneServizio()!=null){
				//  MESSAGE BOX 
				if(StringUtils.isNotEmpty(saOp2.getInvocazioneServizio().getGetMessage().getValue()))
					oneLine.add(saOp2.getInvocazioneServizio().getGetMessage().getValue());
				else 
					oneLine.add("");
				// SBUSTAMENTO SOAP
				if(StringUtils.isNotEmpty(saOp2.getInvocazioneServizio().getSbustamentoSoap().getValue()))
					oneLine.add(saOp2.getInvocazioneServizio().getSbustamentoSoap().getValue());
				else 
					oneLine.add("");
				// SBUSTAMENTO PROTOCOLLO
				if(StringUtils.isNotEmpty(saOp2.getInvocazioneServizio().getSbustamentoInformazioniProtocollo().getValue()))
					oneLine.add(saOp2.getInvocazioneServizio().getSbustamentoInformazioniProtocollo().getValue());
				else 
					oneLine.add("");

				if(saOp2.getInvocazioneServizio().getConnettore()!=null){
					Connettore connettore = saOp2.getInvocazioneServizio().getConnettore();
					oneLine.addAll(this.printConnettore(connettore, CostantiConfigurazioni.LABEL_TIPO, 
							saOp2.getInvocazioneServizio().getCredenziali(), 
							saOp2.getInvocazioneServizio().getGetMessage(), saOp2.getInvocazionePorta()));
				} else {
					// 15 colonne vuote
					for (int i = 0; i < 15; i++) {
						oneLine.add("");
					}
				}
			} else {
				// 18 colonne vuote
				for (int i = 0; i < 18; i++) {
					oneLine.add("");
				}
			}

		} else {
			// 19 colonne vuote
			for (int i = 0; i < 19; i++) {
				oneLine.add("");
			}
		}
		
		// NOME PA
		if(StringUtils.isNotEmpty(portaApplicativa.getNome()))
			oneLine.add(portaApplicativa.getNome());
		else 
			oneLine.add("");
		
		// NOME PA CONNETTORE
		String nomePAConnettore = "";
		if(dettaglioSA != null) {
			ServizioApplicativo saOp2 = dettaglioSA.getSaOp2();
			if(StringUtils.isNotEmpty(saOp2.getNome()) && paOp2.sizeServizioApplicativoList()>0) {
				for (PortaApplicativaServizioApplicativo pasa : paOp2.getServizioApplicativoList()) {
					if(saOp2.getNome().equals(pasa.getNome())) {
						if(pasa.getDatiConnettore()!=null && StringUtils.isNotEmpty(pasa.getDatiConnettore().getNome())) {
							nomePAConnettore = pasa.getDatiConnettore().getNome();
						}
					}
				}
			}
		}
		oneLine.add(nomePAConnettore);

		// APPLICATIVO SERVER
		String nomeApplicativoServer = "";
		if(dettaglioSA != null) {
			ServizioApplicativo saOp2 = dettaglioSA.getSaOp2();
			if(StringUtils.isNotEmpty(saOp2.getNome()) &&
					CostantiConfigurazione.SERVER.equals(saOp2.getTipo()) ||
					CostantiConfigurazione.CLIENT_OR_SERVER.equals(saOp2.getTipo())) {
				nomeApplicativoServer = saOp2.getNome();
			}
		}
		oneLine.add(nomeApplicativoServer);
		
		dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 
	}

	private void addLinePD(DRDataSource dataSource, ConfigurazioneGenerale configurazione) throws Exception  {
		List<Object> oneLine = new ArrayList<Object>();
		DettaglioPD dettaglioPD = configurazione.getPd();
		PortaDelegata pdOp2 = dettaglioPD.getPortaDelegataOp2();
		PortaDelegata pdOp2Default = dettaglioPD.getPortaDelegataDefaultOp2();
		org.openspcoop2.core.commons.search.PortaDelegata portaDelegata = dettaglioPD.getPortaDelegata();
		PortaDelegataAzione pdAzione = pdOp2.getAzione();
		MappingFruizionePortaDelegata mappingPD = dettaglioPD.getMappingFruizionePortaDelegataOp2();
		DettaglioRateLimiting rateLimiting = dettaglioPD.getRateLimiting();
		
		// Modalita
		String protocollo = configurazione.getProtocollo();
		if(StringUtils.isNotEmpty(protocollo))
			oneLine.add(NamingUtils.getLabelProtocollo(protocollo));
		else 
			oneLine.add("");

		// TIPO ASPC
		if(dettaglioPD.getIdAccordoServizioParteComune() != null) {
			IdAccordoServizioParteComune aspc = dettaglioPD.getIdAccordoServizioParteComune();
			if(aspc.getServiceBinding()!=null) {
				oneLine.add(aspc.getServiceBinding());
			}else { 
				oneLine.add("");
			}
		} else { 
			oneLine.add("");
		}
		
		// ASPC
		if(dettaglioPD.getIdAccordoServizioParteComune() != null) {
			IdAccordoServizioParteComune aspc = dettaglioPD.getIdAccordoServizioParteComune();
			String nomeAspc = aspc.getNome();

			Integer versioneAspc = aspc.getVersione();

			String nomeReferenteAspc = (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getNome() : null;

			String tipoReferenteAspc= (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getTipo() : null;

			oneLine.add(NamingUtils.getLabelAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc)));
		} else 
			oneLine.add("");
		

		if(StringUtils.isNotEmpty(dettaglioPD.getPortType()))
			oneLine.add(dettaglioPD.getPortType());
		else 
			oneLine.add("");

		if(StringUtils.isNotEmpty(configurazione.getFruitore()))
			oneLine.add(configurazione.getFruitore());
		else 
			oneLine.add("");

		if(StringUtils.isNotEmpty(configurazione.getErogatore()))
			oneLine.add(configurazione.getErogatore());
		else 
			oneLine.add("");

		if(StringUtils.isNotEmpty(configurazione.getServizio()))
			oneLine.add(configurazione.getServizio());
		else 
			oneLine.add("");

		// azioni (dove viene indicata la singola azione, o le azioni o la scritta unica azione...)
		// url di invocazione
		// Identificazione Azione: ....
		// Espressione: xpath o regolare

		// TODO controllare lista azioni
		if(StringUtils.isNotEmpty(portaDelegata.getNomeAzione()) &&
				pdAzione!=null &&
				(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_STATIC.equals(pdAzione.getIdentificazione()))){
			// Azione: _XXX
			oneLine.add(portaDelegata.getNomeAzione());
		}
		else{
			List<String> azioni = dettaglioPD.getAzioni();
			
			if(pdAzione==null && (azioni == null || azioni.size() == 0)){
				oneLine.add(CostantiConfigurazioni.LABEL_UTILIZZO_DEL_SERVIZIO_SENZA_AZIONE);
			}
			else{
				// Azioni: XXXs
				StringBuilder sb = new StringBuilder();
				// Azioni: XXXs
				if(azioni != null && azioni.size() > 0){
					for (String azione : azioni) {
						if(sb.length()>0) sb.append("\n");
						sb.append(azione);
					}
				}
				oneLine.add(sb.toString());
			}
		}
		
		// GRUPPO
		if(mappingPD!=null && mappingPD.getDescrizione()!=null) {
			oneLine.add(mappingPD.getDescrizione());
		}
		else {
			oneLine.add("");
		}
		
		// STATO
		if(StringUtils.isNotEmpty(configurazione.getStato()))
			oneLine.add(configurazione.getStato());
		else 
			oneLine.add("");
		
		
		// URL INVOCAZIONE
		String urlInvocazione = dettaglioPD.getUrlInvocazione();
		
		if(StringUtils.isNotEmpty(urlInvocazione))
			oneLine.add(urlInvocazione);
		else 
			oneLine.add("");
		
		
		if(pdAzione != null) {
			// Modalita identificazione azione
			if(pdAzione.getIdentificazione() != null){
				oneLine.add(pdAzione.getIdentificazione().toString());
			} else {
				oneLine.add("");
			}
			
			//Pattern
			if(StringUtils.isNotEmpty(pdAzione.getPattern()))
				oneLine.add(pdAzione.getPattern());
			else 
				oneLine.add("");
			
			// force interface based
			if(pdAzione.getForceInterfaceBased() != null){
				oneLine.add(pdAzione.getForceInterfaceBased().getValue());
			} else {
				oneLine.add("");
			}
			
		} else {
			// Modalita identificazione azione
			oneLine.add("");
			
			//Pattern
			oneLine.add("");
			
			// force interface based
			oneLine.add("");
		}
		
		// CORS
		if(pdOp2Default!=null) {
			if(pdOp2Default.getGestioneCors()!=null) {
				oneLine.add(pdOp2Default.getGestioneCors().getStato().getValue());
			}
			else {
				oneLine.add(CostantiConfigurazioni.VALUE_DEFAULT);
			}
		}
		else {
			if(pdOp2.getGestioneCors()!=null) {
				oneLine.add(pdOp2.getGestioneCors().getStato().getValue());
			}
			else {
				oneLine.add(CostantiConfigurazioni.VALUE_DEFAULT);
			}
		}
				
		// Token (stato)
		// Token (opzionale)
		// Token (policy)
		// Token (validazione input)
		// Token (introspection)
		// Token (user info)
		// Token (forward)
		GestioneToken gestioneToken = pdOp2.getGestioneToken();
		GestioneTokenAutenticazione gestioneTokenAutenticazione = null;
		if(gestioneToken != null) {
			gestioneTokenAutenticazione = gestioneToken.getAutenticazione(); 
			String policy = gestioneToken.getPolicy();
			StatoFunzionalita tokenOpzionale = gestioneToken.getTokenOpzionale() != null ? gestioneToken.getTokenOpzionale()  : StatoFunzionalita.DISABILITATO;
			if(policy != null) {
				oneLine.add(StatoFunzionalita.ABILITATO.getValue());
				oneLine.add(tokenOpzionale.getValue());
				oneLine.add(policy);
				oneLine.add(gestioneToken.getValidazione() != null ? gestioneToken.getValidazione().getValue() : ""); 
				oneLine.add(gestioneToken.getIntrospection() != null ? gestioneToken.getIntrospection().getValue() : "");
				oneLine.add(gestioneToken.getUserInfo() != null ? gestioneToken.getUserInfo().getValue() : "");
				oneLine.add(gestioneToken.getForward() != null ? gestioneToken.getForward().getValue() : "");
				
			} else {
				// sei colonne vuote
				oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
				oneLine.add(tokenOpzionale.getValue());
				oneLine.add("");
				oneLine.add("");
				oneLine.add("");
				oneLine.add("");
				oneLine.add("");
			}
		} else {
			// sei colonne vuote
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
		}
				
		// Autenticazione Token (issuer)
		// Autenticazione Token (client_id)
		// Autenticazione Token (subject)
		// Autenticazione Token (username)
		// Autenticazione Token (email)
		if(gestioneTokenAutenticazione != null) {
			oneLine.add(gestioneTokenAutenticazione.getIssuer()!=null ? gestioneTokenAutenticazione.getIssuer().getValue() : StatoFunzionalita.DISABILITATO.getValue() );
			oneLine.add(gestioneTokenAutenticazione.getClientId()!=null ? gestioneTokenAutenticazione.getClientId().getValue() : StatoFunzionalita.DISABILITATO.getValue() );
			oneLine.add(gestioneTokenAutenticazione.getSubject()!=null ? gestioneTokenAutenticazione.getSubject().getValue() : StatoFunzionalita.DISABILITATO.getValue() );
			oneLine.add(gestioneTokenAutenticazione.getUsername()!=null ? gestioneTokenAutenticazione.getUsername().getValue() : StatoFunzionalita.DISABILITATO.getValue() );
			oneLine.add(gestioneTokenAutenticazione.getEmail()!=null ? gestioneTokenAutenticazione.getEmail().getValue() : StatoFunzionalita.DISABILITATO.getValue() );
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
		}

		// Autenticazione (stato)
		// Autenticazione (opzionale
		// Autenticazione (proprieta)
		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(pdOp2.getAutenticazione())){
			oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
		}
		else{
			oneLine.add(pdOp2.getAutenticazione());
		}

		if(CostantiConfigurazione.ABILITATO.equals(pdOp2.getAutenticazioneOpzionale())){
			oneLine.add(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
		}
		
		oneLine.add(this.toStringProprieta(pdOp2.getProprietaAutenticazioneList()));
		
		// AttributeAuthority (attributi)
		oneLine.add(this.toStringAttributeAuthority(pdOp2.getAttributeAuthorityList()));
		
		// Autorizzazione (Stato)
		// Autorizzazione (proprieta)
		
		// Autorizzazione Trasporto (Soggetti/Applicativi Autorizzati)
		// Applicativi Autorizzati (Authz Trasporto)
		// Autorizzazione Trasporto (Ruoli Autorizzati)
		// Ruoli Richiesti (Authz Trasporto)
		// Ruoli Forniti (Authz Trasporto)
		
		// Autorizzazione Token (Applicativi Autorizzati)
		// Applicativi Autorizzati (Authz Token)
		// Autorizzazione Token (Ruoli Autorizzati)
		// Ruoli Richiesti (Authz Token)
		// Ruoli Forniti (Authz Token)
		
		// Autorizzazione per Token Scope
		// Scope Richiesti
		// Scope Forniti
		
		// Autorizzazione per Token Claims
		
		/* Dettagli dei valori:
		 * 
		 * Autorizzazione (stato): disabilitato/abilitato/xacmlPolicy/NomeCustom
		 * 
		   Applicativi Autorizzati la colonna contiene l'elenco dei soggetti  separati da '\n').
		   Ruoli (separati da '\n')
		   Ruoli Richiesti (all/any)
		   */

		String autorizzazione = pdOp2.getAutorizzazione();
		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(autorizzazione)){
			oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.XACML_POLICY.getValue().toLowerCase())){
			oneLine.add(TipoAutorizzazione.XACML_POLICY.getValue());
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())
				||
				autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			oneLine.add(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			oneLine.add(autorizzazione);
		}

		// Autorizzazione (proprieta)
		oneLine.add(this.toStringProprieta(pdOp2.getProprietaAutorizzazioneList()));

		// Autorizzazione Trasporto (Soggetti/Applicativi Autorizzati)
		// Applicativi Autorizzati (Authz Trasporto)
		// Se abilitato:
		// Servizi Applicativi Autorizzati: sa1
		//                                  sa2
		//                                  sa3
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
			List<String> sa = dettaglioPD.getSa();
			StringBuilder sb = new StringBuilder();
			for (String servApp : sa) {
				if(sb.length()>0) sb.append("\n");
				sb.append(servApp);
			}
			oneLine.add(sb.toString());
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
		}

		// Autorizzazione Trasporto (Ruoli Autorizzati)
		// Ruoli Richiesti (Authz Trasporto)
		// Ruoli Forniti (Authz Trasporto)
		// Ruoli: tutti/almenoUno
		// Ruoli Autorizzati: ruolo1 (fonte esterna)
		//                    ruolo2 (fonte interna)
		//                    ruolo3 (fonte qualsiasi)
		//
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())){
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
			List<String> ruoli = dettaglioPD.getRuoli();
			String match = dettaglioPD.getMatchRuoli();
			oneLine.add(match);
			if(pdOp2.getRuoli()!=null){
				StringBuilder sb = new StringBuilder();

				for (String ruolo : ruoli) {
					if(sb.length()>0) sb.append("\n");
					sb.append(ruolo);
				}

				oneLine.add(sb.toString());
			} else {
				oneLine.add("");
			}
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
		
		// Autorizzazione Token (Applicativi Autorizzati)
		// Applicativi Autorizzati (Authz Token)
		PortaDelegataAutorizzazioneToken autorizzazioneToken = pdOp2.getAutorizzazioneToken();
		if(autorizzazioneToken!=null && StatoFunzionalita.ABILITATO.equals(autorizzazioneToken.getAutorizzazioneApplicativi())){
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
			StringBuilder sb = new StringBuilder();
			if(autorizzazioneToken.getServiziApplicativi()!=null && autorizzazioneToken.getServiziApplicativi().sizeServizioApplicativoList()>0) {
				for (PortaDelegataServizioApplicativo servApp : autorizzazioneToken.getServiziApplicativi().getServizioApplicativoList()) {
					if(sb.length()>0) sb.append("\n");
						sb.append(servApp.getNome());
				}
			}
			oneLine.add(sb.toString());
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
		}
		
		// Autorizzazione Token (Ruoli Autorizzati)
		// Ruoli Richiesti (Authz Token)
		// Ruoli Forniti (Authz Token)
		if(autorizzazioneToken!=null && StatoFunzionalita.ABILITATO.equals(autorizzazioneToken.getAutorizzazioneRuoli())){
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
			String match = autorizzazioneToken.getRuoli()!=null && autorizzazioneToken.getRuoli().getMatch()!=null ? 
					(org.openspcoop2.core.config.constants.RuoloTipoMatch.ANY.equals(autorizzazioneToken.getRuoli().getMatch()) ? CostantiConfigurazioni.LABEL_RUOLI_ANY : CostantiConfigurazioni.LABEL_RUOLI_ALL) 
					: 
					"";
			oneLine.add(match);
			if(autorizzazioneToken.getRuoli()!=null && autorizzazioneToken.getRuoli().sizeRuoloList()>0){
				StringBuilder sb = new StringBuilder();
				for (Ruolo ruolo : autorizzazioneToken.getRuoli().getRuoloList()) {
					if(sb.length()>0) sb.append("\n");
						sb.append(ruolo.getNome());
				}
				oneLine.add(sb.toString());
			} else {
				oneLine.add("");
			}
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
		
		// Autorizzazione (Token Claims)
		
		if(gestioneToken != null) {
			if(StringUtils.isNotEmpty(gestioneToken.getOptions()))
				oneLine.add(gestioneToken.getOptions());
			else 
				oneLine.add("");
		}else {
			oneLine.add("");
		}
		
		// Autorizzazione (Scope)
		// Scope Richiesti
		// Scope Forniti
		AutorizzazioneScope scope = pdOp2.getScope();
		if(scope != null) {
			oneLine.add(scope.getStato().getValue());
			oneLine.add(scope.getMatch().getValue());
			
			if(scope.getScopeList()!=null){
				StringBuilder sb = new StringBuilder();

				for (Scope scopeS : scope.getScopeList()) {
					if(sb.length()>0) sb.append("\n");
					sb.append(scopeS.getNome());
				}

				oneLine.add(sb.toString());
			} else {
				oneLine.add("");
			}
			
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
				
		// Autorizzazione Contenuti (Stato)
		String autorizzazioneContenuti = pdOp2.getAutorizzazioneContenuto();
		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(autorizzazioneContenuti)){
			oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
		}
		else if(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(autorizzazioneContenuti)){
			oneLine.add(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			oneLine.add(autorizzazioneContenuti);
		}

		// Autorizzazione Contenuti (proprieta)
		oneLine.add(this.toStringProprieta(pdOp2.getProprietaAutorizzazioneContenutoList()));
		
		// Rate Limiting
		if(rateLimiting!=null) {
			oneLine.add(rateLimiting.getAsCSVRecord());
		}
		else {
			oneLine.add("");
		}
		
	    //Validazione (Stato)
	    //Validazione (Tipo)
	    //Validazione (Accetta MTOM)
		
		ValidazioneContenutiApplicativi validazioneContenutiApplicativi = pdOp2.getValidazioneContenutiApplicativi();
		if(validazioneContenutiApplicativi != null) {
			oneLine.add(validazioneContenutiApplicativi.getStato().getValue());
			oneLine.add(validazioneContenutiApplicativi.getTipo().getValue());
			oneLine.add(validazioneContenutiApplicativi.getAcceptMtomMessage().getValue());
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
		
		// Caching Risposta
		if(pdOp2.getResponseCaching()!=null) {
			oneLine.add(pdOp2.getResponseCaching().getStato().getValue());
		}
		else {
			oneLine.add(CostantiConfigurazioni.VALUE_DEFAULT);
		}
		
		// Sicurezza Messaggio (Stato)
		// Schema Sicurezza Richiesta
		// Schema Sicurezza Risposta
		MessageSecurity messageSecurity = pdOp2.getMessageSecurity();
		String requestMode = null;
		String responseMode = null; 
		
		if(messageSecurity != null) {
			if(messageSecurity.getRequestFlow() != null){
				requestMode = messageSecurity.getRequestFlow().getMode();
			}
			if(messageSecurity.getResponseFlow() != null){
				responseMode = messageSecurity.getResponseFlow().getMode();
			}
		}
		String statoMessageSecurity= pdOp2.getStatoMessageSecurity();
		
		if(StringUtils.isNotEmpty(statoMessageSecurity)) {
			oneLine.add(statoMessageSecurity);
			
			if(StatoFunzionalita.ABILITATO.getValue().equals(statoMessageSecurity)) {
				if(StringUtils.isNotEmpty(requestMode)) {
					if(requestMode.equals(CostantiConfigurazioni.VALUE_SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT)) {
						oneLine.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_CONFIGURAZIONE_MANUALE);
					} else {
						oneLine.add(requestMode);
					}
				} else {
					oneLine.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_NESSUNO);
				}
				
				if(StringUtils.isNotEmpty(responseMode)) {
					if(responseMode.equals(CostantiConfigurazioni.VALUE_SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT)) {
						oneLine.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_CONFIGURAZIONE_MANUALE);
					} else {
						oneLine.add(responseMode);
					}
				} else {
					oneLine.add(CostantiConfigurazioni.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_NESSUNO);
				}
			} else {
				oneLine.add("");
				oneLine.add("");
			}
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
		
		
		// MTOM Richiesta
		// MTOM Risposta
		MtomProcessor mtomProcessor = pdOp2.getMtomProcessor();
		
		if(mtomProcessor != null) {
			MtomProcessorFlow requestFlow = mtomProcessor.getRequestFlow();
			if(requestFlow != null) {
				oneLine.add(requestFlow.getMode().getValue());
			} else {
				oneLine.add("");
			}
			
			MtomProcessorFlow responseFlow = mtomProcessor.getResponseFlow();
			if(responseFlow != null) {
				oneLine.add(responseFlow.getMode().getValue());
			} else {
				oneLine.add("");
			}
		} else {
			oneLine.add("");
			oneLine.add("");
		}
		
		// Trasformazioni
		oneLine.add(toStringTrasformazioni(pdOp2.getTrasformazioni()));
		
		// Correlazione Applicativa Richiesta
		// Correlazione Applicativa Risposta
		int numCorrelazioneReq = 0;
		int numCorrelazioneRes = 0;

		CorrelazioneApplicativa ca = pdOp2.getCorrelazioneApplicativa();
		if (ca != null)
			numCorrelazioneReq = ca.sizeElementoList();

		if (pdOp2.getCorrelazioneApplicativaRisposta() != null)
			numCorrelazioneRes = pdOp2.getCorrelazioneApplicativaRisposta().sizeElementoList();
		
		if(numCorrelazioneReq > 0) {
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
		}
		
		if(numCorrelazioneRes > 0) {
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
		}
		
		// Registrazione Messaggi
		oneLine.add(toStringDump(pdOp2.getDump()));
		
		// Proprieta
		oneLine.add(this.toStringProprieta(pdOp2.getProprietaList()));
		
		// Metadati
		if(StringUtils.isNotEmpty(pdOp2.getIntegrazione())){
			oneLine.add(pdOp2.getIntegrazione());
		}
		else {
			oneLine.add("");
		}
		
		// Handlers
		oneLine.add(toStringHandlers(pdOp2.getConfigurazioneHandler()));
		
		// Profilo Interoperabilità
		if(StringUtils.isNotEmpty(dettaglioPD.getConfigurazioneProfilo())) {
			oneLine.add(dettaglioPD.getConfigurazioneProfilo());
		}
		else {
			oneLine.add("");
		}
		
		// connettore
		if(dettaglioPD.getConnettore() !=null){
			Connettore connettore = dettaglioPD.getConnettore();
			oneLine.addAll(this.printConnettore(connettore, CostantiConfigurazioni.LABEL_MODALITA_INOLTRO, 
					null, 
					null, null));
		} else {
			// 15 colonne vuote
			for (int i = 0; i < 15; i++) {
				oneLine.add("");
			}
		}
		
		// Nome PD
		if(StringUtils.isNotEmpty(portaDelegata.getNome()))
			oneLine.add(portaDelegata.getNome());
		else 
			oneLine.add("");
		

		dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 
	}

	/*
	 	1 Connettore (Tipo)
	    2 Connettore (EndPoint)
	    3 Connettore (Debug)
	    4 Connettore (Username)
	    5 Connettore (Token)
    	6 CONNETTORE_PROXY_ENDPOINT
		7 CONNETTORE_PROXY_USERNAME
		8 CONNETTORE_SSL_TYPE
		9 CONNETTORE_HOSTNAME_VERIFIER
		10 CONNETTORE_KEY_STORE
		11 CONNETTORE_TRUST_STORE
		12 CONNETTORE_HTTPS_KEY_STORE_LOCATION
		13 CONNETTORE_HTTPS_TRUST_STORE_LOCATION
		14 CONNETTORE_CLIENT_CERTIFICATE
		15 CONNETTORE_ALTRE_CONFIGURAZIONI
	 * */
	public  List<Object> printConnettore(Connettore connettore,String labelTipoConnettore,
			InvocazioneCredenziali invCredenziali, 
			StatoFunzionalita integrationManager, InvocazionePorta invocazionePorta){
		List<Object> oneLine = new ArrayList<Object>();
		Map<Integer, String> mapProperties = new HashMap<Integer, String>();

		mapProperties.put(1, connettore.getTipo());

		if(TipiConnettore.HTTP.getNome().equals(connettore.getTipo()) || TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
			mapProperties.put(2, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList()));

			boolean find = false;
			if(integrationManager!=null && StatoFunzionalita.ABILITATO.equals(integrationManager)) {
				if(invocazionePorta!=null && invocazionePorta.sizeCredenzialiList()>0) {
					for (Credenziali c : invocazionePorta.getCredenzialiList()) {
						if(CredenzialeTipo.BASIC.equals(c.getTipo()) && c.getUser()!=null && StringUtils.isNotEmpty(c.getUser())){
							find = true;
							mapProperties.put(4, c.getUser());
						}
					}
				}
			}
			if(!find) {
				if(invCredenziali!=null){
					mapProperties.put(4, invCredenziali.getUser());
				}
				else{
					String username = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_USERNAME, connettore.getPropertyList());
					if(username!=null){
						mapProperties.put(4, username);
					}
				}
			}

			String token = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_TOKEN_POLICY, connettore.getPropertyList());
			if(token!=null){
				mapProperties.put(5, token);
			}
			
			//	8 CONNETTORE_SSL_TYPE
			//	9 CONNETTORE_HOSTNAME_VERIFIER
			//	10 CONNETTORE_KEY_STORE
			//	11 CONNETTORE_TRUST_STORE
			//	12 CONNETTORE_HTTPS_KEY_STORE_LOCATION
			//	13 CONNETTORE_HTTPS_TRUST_STORE_LOCATION
			//	14 CONNETTORE_CLIENT_CERTIFICATE
			if(TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
				mapProperties.put(8, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE, connettore.getPropertyList()));
				mapProperties.put(9, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER, connettore.getPropertyList()));
				mapProperties.put(11, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_TYPE, connettore.getPropertyList()));
				
				boolean trustAllCerts = false;
				if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS)) {
					String v = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS, connettore.getPropertyList());
					if("true".equalsIgnoreCase(v)) {
						trustAllCerts = true;
					}
				}
				if(trustAllCerts) {
					mapProperties.put(13, "Trust all certificates");
				}
				else {
					mapProperties.put(13, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION, connettore.getPropertyList()));
				}


				boolean invioCertificatoClient = false;
				String cert = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION, connettore.getPropertyList());
				if(cert!=null){
					mapProperties.put(12, cert); // 12 CONNETTORE_HTTPS_KEY_STORE_LOCATION
					invioCertificatoClient = true;
				}
				if(invioCertificatoClient){ //	10 CONNETTORE_KEY_STORE
					mapProperties.put(10, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_TYPE, connettore.getPropertyList()));
				}
			
				String invioCertificatoClientLabel = invioCertificatoClient +"";
				if(invioCertificatoClient) {
					String certAlias = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_ALIAS, connettore.getPropertyList());
					if(certAlias!=null && StringUtils.isNotEmpty(certAlias)){
						invioCertificatoClientLabel = certAlias;
					}
				}
				mapProperties.put(14, invioCertificatoClientLabel); // 14 CONNETTORE_CLIENT_CERTIFICATE
			}

			String proxy = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE, connettore.getPropertyList());
			// 6 CONNETTORE_PROXY_ENDPOINT
			// 7 CONNETTORE_PROXY_USERNAME
			if(proxy!=null){
				mapProperties.put(6, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME, connettore.getPropertyList())+CostantiConfigurazioni.LABEL_DOTS+
						ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT, connettore.getPropertyList()));

				String username = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME, connettore.getPropertyList());
				if(username!=null){
					mapProperties.put(7, username);
				}
			}
		}
		else if(TipiConnettore.JMS.getNome().equals(connettore.getTipo())){
			mapProperties.put(2, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList()));

			StringBuilder sb = new StringBuilder();
			sb.append(CostantiConfigurazioni.LABEL_TIPO_CODA_JMS).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_JMS_TIPO, connettore.getPropertyList())).append("\n");
			sb.append(CostantiConfigurazioni.LABEL_CONNECTION_FACTORY).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_JMS_CONNECTION_FACTORY, connettore.getPropertyList())).append("\n");
			sb.append(CostantiConfigurazioni.LABEL_SEND_AS).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_JMS_SEND_AS, connettore.getPropertyList()));
			mapProperties.put(15, sb.toString()); //15 CONNETTORE_ALTRE_CONFIGURAZIONI

			if(invCredenziali!=null){
				mapProperties.put(4, invCredenziali.getUser());
			}
			else{
				String username = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_USERNAME, connettore.getPropertyList());
				if(username!=null){
					mapProperties.put(4, username);
				}
			}
		}
		else if(TipiConnettore.FILE.getNome().equals(connettore.getTipo())){
			StringBuilder sb = new StringBuilder();
			sb.append(CostantiConfigurazioni.LABEL_OUTPUT_FILE).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE, connettore.getPropertyList())).append("\n");
			sb.append(CostantiConfigurazioni.LABEL_OUTPUT_FILE_HEADER).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS, connettore.getPropertyList())).append("\n");
			String risposta = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_MODE, connettore.getPropertyList());
			if(risposta!=null){
				sb.append(CostantiConfigurazioni.LABEL_INPUT_FILE).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE, connettore.getPropertyList())).append("\n");
				sb.append(CostantiConfigurazioni.LABEL_INPUT_FILE_HEADER).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS, connettore.getPropertyList()));
			}

			mapProperties.put(15, sb.toString()); //15 CONNETTORE_ALTRE_CONFIGURAZIONI
		}
		else if(TipiConnettore.NULL.getNome().equals(connettore.getTipo())){
			mapProperties.put(2, ConnettoreNULL.LOCATION);
		}
		else if(TipiConnettore.NULLECHO.getNome().equals(connettore.getTipo())){
			mapProperties.put(2, ConnettoreNULLEcho.LOCATION);
		}
		else if(integrationManager!=null && StatoFunzionalita.ABILITATO.equals(integrationManager)) {
			
			// confonde e basta. Nel caso di HTTP ci sara' sempre integration manager però con la location http
			//mapProperties.put(2, "/govway/IntegrationManager/MessageBox");
			
			boolean find = false;
			if(invocazionePorta!=null && invocazionePorta.sizeCredenzialiList()>0) {
				for (Credenziali c : invocazionePorta.getCredenzialiList()) {
					if(CredenzialeTipo.BASIC.equals(c.getTipo()) && c.getUser()!=null && StringUtils.isNotEmpty(c.getUser())){
						find = true;
						mapProperties.put(4, c.getUser());
					}
				}
			}
			
			if(!find) {
				if(invCredenziali!=null){
					mapProperties.put(4, invCredenziali.getUser());
				}
				else{
					String username = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_USERNAME, connettore.getPropertyList());
					if(username!=null){
						mapProperties.put(4, username);
					}
				}
			}
		}
		else{

			List<Property> list = connettore.getPropertyList();
			if(list!=null && list.size()>0){
				StringBuilder sb = new StringBuilder();
				for (Property property : list) {
					if(sb.length() > 0)
						sb.append("\n");

					sb.append(property.getNome()).append(": ").append(property.getValore());
				}

				mapProperties.put(15, sb.toString()); //15 CONNETTORE_ALTRE_CONFIGURAZIONI
			}
		}
		String debug = "false";
		if(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_DEBUG, connettore.getPropertyList())!=null){
			debug = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_DEBUG, connettore.getPropertyList());
		}
		mapProperties.put(3, debug);

		// aggiungo le 15 proprieta previste
		for (int i = 1; i < 16; i++) {
			String valoreProprieta = mapProperties.get(i);

			if(StringUtils.isNotEmpty(valoreProprieta))
				oneLine.add(valoreProprieta);
			else 
				oneLine.add("");
		}

		return oneLine;
	}
	
	private String toStringProprieta(List<Proprieta> list) {
		StringBuilder sb = new StringBuilder();
		if(list!=null && list.size()>0) {
			for (Proprieta p : list) {
				if(sb.length()>0) {
					sb.append("\n");
				}
				sb.append(p.getNome());
				sb.append("=");
				sb.append(p.getValore());
			}
		}
		if(sb.length()>0) {
			return sb.toString();	
		}
		else {
			return "";
		}
	}
	
	private String toStringAttributeAuthority(List<AttributeAuthority> list) {
		StringBuilder sb = new StringBuilder();
		if(list!=null && list.size()>0) {
			for (AttributeAuthority aa : list) {
				if(sb.length()>0) {
					sb.append("\n");
				}
				sb.append(aa.getNome());
				sb.append("=");
				if(aa.sizeAttributoList()>0) {
					boolean first = true;
					for (Object attr : aa.getAttributoList()) {
						if(!first) {
							sb.append(",");
						}
						sb.append(attr);
						first = false;
					}
				}
			}
		}
		if(sb.length()>0) {
			return sb.toString();	
		}
		else {
			return "";
		}
	}
	
	private String toStringTrasformazioni(Trasformazioni trasformazioni) {
		StringBuilder sb = new StringBuilder();
		if(trasformazioni!=null && trasformazioni.sizeRegolaList()>0) {
			for (TrasformazioneRegola regola : trasformazioni.getRegolaList()) {
				if(sb.length()>0) {
					sb.append("\n");
				}
				sb.append(regola.getNome());
				sb.append(" ");
				sb.append(regola.getStato().getValue());
			}
		}
		if(sb.length()>0) {
			return sb.toString();	
		}
		else {
			return "";
		}
	}
	
	private String toStringDump(DumpConfigurazione dump) {
		StringBuilder sb = new StringBuilder();
		if(dump!=null) {
			//sb.append(dump.getRealtime().getValue());
			if(StatoFunzionalita.ABILITATO.equals(dump.getRealtime())) {
				sb.append("\n");
				sb.append("richiesta-ingresso ").append(toStringDumpRegola(dump.getRichiestaIngresso()));
				sb.append("\n");
				sb.append("richiesta-uscita ").append(toStringDumpRegola(dump.getRichiestaUscita()));
				sb.append("\n");
				sb.append("risposta-ingresso ").append(toStringDumpRegola(dump.getRispostaIngresso()));
				sb.append("\n");
				sb.append("risposta-uscita ").append(toStringDumpRegola(dump.getRispostaUscita()));
			}
			else {
				sb.append(CostantiConfigurazioni.VALUE_DEFAULT);
			}
		}
		else {
			sb.append(CostantiConfigurazioni.VALUE_DEFAULT);
		}
		if(sb.length()>0) {
			return sb.toString();	
		}
		else {
			return "";
		}
	}
	private String toStringDumpRegola(DumpConfigurazioneRegola regola) {
		StringBuilder sb = new StringBuilder();
		if(regola!=null) {
			sb.append("header:").append(regola.getHeaders());
			sb.append(" ");
			sb.append("payload:").append(regola.getPayload());
		}
		else {
			sb.append(CostantiConfigurazione.DISABILITATO.getValue());
		}
		if(sb.length()>0) {
			return sb.toString();	
		}
		else {
			return "";
		}
	}
	private String toStringHandlers(ConfigurazionePortaHandler config) {
		StringBuilder sb = new StringBuilder();
		if(config!=null) {
			if(config.getRequest()!=null) {
				sb.append(toStringMessageHandlers(config.getRequest(), "request"));
			}
			if(config.getResponse()!=null) {
				if(sb.length()>0) {
					sb.append("\n");
				}
				sb.append(toStringMessageHandlers(config.getResponse(), "response"));
			}
		}
		if(sb.length()>0) {
			sb.append(CostantiConfigurazioni.VALUE_DEFAULT);
			return sb.toString();	
		}
		else {
			return "";
		}
	}
	private String toStringMessageHandlers(ConfigurazioneMessageHandlers hdr, String role) {
		StringBuilder sb = new StringBuilder("");
		if(hdr.sizePreInList()>0) {
			if(sb.length()>0) {
				sb.append("\n");
			}
			sb.append(role).append("-pre-in:");
			int i = 0;
			for (ConfigurazioneHandler h : hdr.getPreInList()) {
				if(i>0) {
					sb.append(",");
				}
				sb.append(h.getTipo());
				i++;
			}
		}
		if(hdr.sizeInList()>0) {
			if(sb.length()>0) {
				sb.append("\n");
			}
			sb.append(role).append("-in:");
			int i = 0;
			for (ConfigurazioneHandler h : hdr.getInList()) {
				if(i>0) {
					sb.append(",");
				}
				sb.append(h.getTipo());
				i++;
			}
		}
		if(hdr.sizeInProtocolInfoList()>0) {
			if(sb.length()>0) {
				sb.append("\n");
			}
			sb.append(role).append("-in-profile-info:");
			int i = 0;
			for (ConfigurazioneHandler h : hdr.getInProtocolInfoList()) {
				if(i>0) {
					sb.append(",");
				}
				sb.append(h.getTipo());
				i++;
			}
		}
		if(hdr.sizeOutList()>0) {
			if(sb.length()>0) {
				sb.append("\n");
			}
			sb.append(role).append("-out:");
			int i = 0;
			for (ConfigurazioneHandler h : hdr.getOutList()) {
				if(i>0) {
					sb.append(",");
				}
				sb.append(h.getTipo());
				i++;
			}
		}
		if(hdr.sizePostOutList()>0) {
			if(sb.length()>0) {
				sb.append("\n");
			}
			sb.append(role).append("-post-out:");
			int i = 0;
			for (ConfigurazioneHandler h : hdr.getPostOutList()) {
				if(i>0) {
					sb.append(",");
				}
				sb.append(h.getTipo());
				i++;
			}
		}
		return sb.toString();
	}
}
