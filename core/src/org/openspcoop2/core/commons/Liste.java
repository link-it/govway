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




package org.openspcoop2.core.commons;

/**
 * Liste
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public final class Liste
{
	public static final int PDD;
	public static final int PDD_SOGGETTI;
	
	public static final int SOGGETTI;
	public static final int SOGGETTI_RUOLI;
	public static final int SOGGETTI_PROP;
	
	public static final int PORTE_APPLICATIVE;
	public static final int PORTE_APPLICATIVE_BY_SOGGETTO;
	public static final int PORTE_APPLICATIVE_PROP;
	public static final int PORTE_APPLICATIVE_AZIONI;
	public static final int PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO;
	public static final int PORTE_APPLICATIVE_SOGGETTO;
	public static final int PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO;
	public static final int PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST;
	public static final int PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE;
	public static final int PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA;
	public static final int PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
	public static final int PORTE_APPLICATIVE_MTOM_REQUEST;
	public static final int PORTE_APPLICATIVE_MTOM_RESPONSE;
	public static final int PORTE_APPLICATIVE_RUOLI;
	public static final int PORTE_APPLICATIVE_SCOPE;
	public static final int PORTE_APPLICATIVE_EXTENDED;
	public static final int PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
	public static final int PORTE_APPLICATIVE_TRASFORMAZIONI;
	public static final int PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
	public static final int PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
	public static final int PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER;
	public static final int PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRI;
	public static final int PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO;
	public static final int PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO;
	public static final int PORTE_APPLICATIVE_PROPRIETA_AUTENTICAZIONE;
	public static final int PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE;
	public static final int PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO;
	public static final int PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
	public static final int PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPRIETA;
	public static final int PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA;
	public static final int PORTE_APPLICATIVE_TOKEN_SERVIZIO_APPLICATIVO;
	public static final int PORTE_APPLICATIVE_TOKEN_RUOLI;
	
	public static final int PORTE_DELEGATE;
	public static final int PORTE_DELEGATE_BY_SOGGETTO;
	public static final int PORTE_DELEGATE_PROP;
	public static final int PORTE_DELEGATE_SERVIZIO_APPLICATIVO;
	public static final int PORTE_DELEGATE_AZIONI;
	public static final int PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST;
	public static final int PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE;
	public static final int PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA;
	public static final int PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
	public static final int PORTE_DELEGATE_MTOM_REQUEST;
	public static final int PORTE_DELEGATE_MTOM_RESPONSE;
	public static final int PORTE_DELEGATE_RUOLI;
	public static final int PORTE_DELEGATE_SCOPE;
	public static final int PORTE_DELEGATE_EXTENDED;
	public static final int PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
	public static final int PORTE_DELEGATE_TRASFORMAZIONI;
	public static final int PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE;
	public static final int PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER;
	public static final int PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER;
	public static final int PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_PARAMETRI;
	public static final int PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO;
	public static final int PORTE_DELEGATE_PROPRIETA_AUTENTICAZIONE;
	public static final int PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE;
	public static final int PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO;
	public static final int PORTE_DELEGATE_TOKEN_SERVIZIO_APPLICATIVO;
	public static final int PORTE_DELEGATE_TOKEN_RUOLI;
	
	public static final int SERVIZIO_APPLICATIVO;
	public static final int SERVIZI_APPLICATIVI_BY_SOGGETTO;
	public static final int SERVIZIO_APPLICATIVO_RUOLI;
	public static final int SERVIZI_APPLICATIVI_PROP;
	
	public static final int ACCORDI;
	public static final int ACCORDI_AZIONI;
	public static final int ACCORDI_EROGATORI;
	public static final int ACCORDI_PORTTYPE;
	public static final int ACCORDI_PORTTYPE_AZIONI;
	public static final int ACCORDI_PORTTYPE_AZIONI_MESSAGE_INPUT;
	public static final int ACCORDI_PORTTYPE_AZIONI_MESSAGE_OUTPUT;
	public static final int ACCORDI_ALLEGATI;
	public static final int ACCORDI_API_RESOURCES;
	public static final int ACCORDI_API_RESOURCES_RESPONSE;
	public static final int ACCORDI_API_RESOURCES_REPRESENTATION_REQUEST;
	public static final int ACCORDI_API_RESOURCES_REPRESENTATION_RESPONSE;
	public static final int ACCORDI_API_RESOURCES_PARAMETERS_REQUEST;
	public static final int ACCORDI_API_RESOURCES_PARAMETERS_RESPONSE;
	
	public static final int ACCORDI_COOPERAZIONE;
	public static final int ACCORDI_COOP_PARTECIPANTI;
	public static final int ACCORDI_COOP_ALLEGATI;
	public static final int ACCORDI_COMPONENTI;
	
	public static final int SERVIZI;	
	public static final int SERVIZI_FRUITORI;
	public static final int SERVIZI_FRUITORI_PORTE_DELEGATE;
	public static final int SERVIZI_ALLEGATI;
	public static final int CONFIGURAZIONE_EROGAZIONE;
	public static final int CONFIGURAZIONE_FRUIZIONE;

	public static final int	GRUPPI;
	public static final int	RUOLI;	
	public static final int	SCOPE;	
	public static final int	REGISTRI;
	public static final int	ROUTING;
	public static final int SYSTEM_PROPERTIES;
	
	public static final int STATISTICHE_STATO;
	
	public static final int SU;
	public static final int UTENTI_SERVIZI;
	public static final int UTENTI_SOGGETTI;

	public static final int MONITOR_MSG;
	
	public static final int FILTRI;

	public static final int AUDIT_REPORT;
	
	public static final int OPERATIONS_CODA;
    public static final int OPERATIONS_ESEGUITE;
    public static final int OPERATIONS_FALLITE;
    public static final int OPERATIONS_INVALIDE;
    public static final int OPERATIONS_RICERCA;
    public static final int OPERATIONS_WAITING;
    
    public static final int CONFIGURAZIONE_EXTENDED;	
    
    public static final int CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY;	
    public static final int CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;
    public static final int CONFIGURAZIONE_GESTIONE_POLICY_TOKEN;
    public static final int CONFIGURAZIONE_GESTIONE_ATTRIBUTE_AUTHORITY;
    public static final int CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
    public static final int CONFIGURAZIONE_PROXY_PASS_REGOLA;
    
    public static final int CONFIGURAZIONE_CANALI;
    public static final int CONFIGURAZIONE_CANALI_NODI;
    
    public static final int CONFIGURAZIONE_PLUGINS_ARCHIVI;
    public static final int CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR;
    public static final int CONFIGURAZIONE_PLUGINS_CLASSI;
    public static final int CONFIGURAZIONE_ALLARMI;
    public static final int CONFIGURAZIONE_ALLARMI_HISTORY;
    
    public static final int CONFIGURAZIONE_HANDLERS_RICHIESTA;
    public static final int CONFIGURAZIONE_HANDLERS_RISPOSTA;
    public static final int CONFIGURAZIONE_HANDLERS_SERVIZIO;
    
    public static final int REMOTE_STORE_KEY;


    private static int numeroListe = 0;
    static {
    	PDD = numeroListe ++;
    	PDD_SOGGETTI = numeroListe ++;
    	SOGGETTI = numeroListe ++;
    	SOGGETTI_RUOLI = numeroListe ++;
    	SOGGETTI_PROP = numeroListe ++;
    	PORTE_APPLICATIVE= numeroListe ++;
    	PORTE_APPLICATIVE_BY_SOGGETTO= numeroListe ++;
    	PORTE_APPLICATIVE_PROP = numeroListe ++;
    	PORTE_APPLICATIVE_AZIONI = numeroListe ++;
    	PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO = numeroListe ++;
    	PORTE_APPLICATIVE_SOGGETTO = numeroListe ++;
    	PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO = numeroListe ++;
    	PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST = numeroListe ++;
    	PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE = numeroListe ++;
    	PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA = numeroListe ++;
    	PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA = numeroListe ++;
    	PORTE_APPLICATIVE_MTOM_REQUEST = numeroListe ++;
    	PORTE_APPLICATIVE_MTOM_RESPONSE = numeroListe ++;
    	PORTE_APPLICATIVE_RUOLI = numeroListe ++;
    	PORTE_APPLICATIVE_SCOPE = numeroListe ++;
    	PORTE_APPLICATIVE_EXTENDED = numeroListe ++;
    	PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA = numeroListe ++;
    	PORTE_APPLICATIVE_TRASFORMAZIONI = numeroListe ++;
    	PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE = numeroListe ++;
    	PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER = numeroListe ++;
    	PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER = numeroListe ++;
    	PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRI = numeroListe ++;
    	PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO = numeroListe ++;
    	PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO= numeroListe ++;
    	PORTE_APPLICATIVE_PROPRIETA_AUTENTICAZIONE = numeroListe ++;
    	PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE = numeroListe ++;
    	PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO = numeroListe ++;
    	PORTE_APPLICATIVE_CONNETTORI_MULTIPLI = numeroListe ++;
    	PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPRIETA = numeroListe ++;
    	PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA = numeroListe ++;
    	PORTE_APPLICATIVE_TOKEN_SERVIZIO_APPLICATIVO = numeroListe ++;
    	PORTE_APPLICATIVE_TOKEN_RUOLI = numeroListe ++;
    	PORTE_DELEGATE = numeroListe ++;
    	PORTE_DELEGATE_BY_SOGGETTO= numeroListe ++;
    	PORTE_DELEGATE_PROP= numeroListe ++;
    	PORTE_DELEGATE_SERVIZIO_APPLICATIVO = numeroListe ++;
    	PORTE_DELEGATE_AZIONI = numeroListe ++;
    	PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST = numeroListe ++;
    	PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE = numeroListe ++;
    	PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA = numeroListe ++;
    	PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA = numeroListe ++;
    	PORTE_DELEGATE_MTOM_REQUEST = numeroListe ++;
    	PORTE_DELEGATE_MTOM_RESPONSE = numeroListe ++;
    	PORTE_DELEGATE_RUOLI = numeroListe ++;
    	PORTE_DELEGATE_SCOPE = numeroListe ++;
    	PORTE_DELEGATE_EXTENDED = numeroListe ++;
    	PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA = numeroListe ++;
    	PORTE_DELEGATE_TRASFORMAZIONI = numeroListe ++;
    	PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE = numeroListe ++;
    	PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER = numeroListe ++;
    	PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER = numeroListe ++;
    	PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_PARAMETRI = numeroListe ++;
    	PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO = numeroListe ++;
    	PORTE_DELEGATE_PROPRIETA_AUTENTICAZIONE = numeroListe ++;
    	PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE = numeroListe ++;
    	PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO = numeroListe ++;
    	PORTE_DELEGATE_TOKEN_SERVIZIO_APPLICATIVO = numeroListe ++;
    	PORTE_DELEGATE_TOKEN_RUOLI = numeroListe ++;
    	SERVIZIO_APPLICATIVO = numeroListe ++;
    	SERVIZI_APPLICATIVI_BY_SOGGETTO = numeroListe ++;
    	SERVIZIO_APPLICATIVO_RUOLI = numeroListe ++;
    	SERVIZI_APPLICATIVI_PROP = numeroListe ++;
    	ACCORDI = numeroListe ++;
    	ACCORDI_AZIONI = numeroListe ++;
    	ACCORDI_EROGATORI = numeroListe ++;
    	ACCORDI_PORTTYPE = numeroListe ++;
    	ACCORDI_PORTTYPE_AZIONI = numeroListe ++;
    	ACCORDI_PORTTYPE_AZIONI_MESSAGE_INPUT = numeroListe ++;
    	ACCORDI_PORTTYPE_AZIONI_MESSAGE_OUTPUT = numeroListe ++;
    	ACCORDI_ALLEGATI = numeroListe ++;
    	ACCORDI_API_RESOURCES = numeroListe ++;
    	ACCORDI_API_RESOURCES_RESPONSE = numeroListe ++;
    	ACCORDI_API_RESOURCES_REPRESENTATION_REQUEST = numeroListe ++;
    	ACCORDI_API_RESOURCES_REPRESENTATION_RESPONSE = numeroListe ++;
    	ACCORDI_API_RESOURCES_PARAMETERS_REQUEST = numeroListe ++;
    	ACCORDI_API_RESOURCES_PARAMETERS_RESPONSE = numeroListe ++;
    	ACCORDI_COOPERAZIONE = numeroListe ++;
    	ACCORDI_COOP_PARTECIPANTI = numeroListe ++;
    	ACCORDI_COOP_ALLEGATI = numeroListe ++;
    	ACCORDI_COMPONENTI = numeroListe ++;
    	SERVIZI = numeroListe ++;	
    	SERVIZI_FRUITORI= numeroListe ++;
    	SERVIZI_FRUITORI_PORTE_DELEGATE = numeroListe ++;
    	SERVIZI_ALLEGATI= numeroListe ++;
    	CONFIGURAZIONE_EROGAZIONE = numeroListe ++;
    	CONFIGURAZIONE_FRUIZIONE = numeroListe ++;
    	GRUPPI = numeroListe ++;	
    	RUOLI = numeroListe ++;	
    	SCOPE = numeroListe ++;	
    	REGISTRI = numeroListe ++;
    	ROUTING = numeroListe ++;
    	SYSTEM_PROPERTIES = numeroListe ++;
    	STATISTICHE_STATO = numeroListe ++;
    	SU = numeroListe ++;
    	UTENTI_SERVIZI = numeroListe ++;
    	UTENTI_SOGGETTI = numeroListe ++;
    	MONITOR_MSG = numeroListe ++;
    	FILTRI = numeroListe ++;
    	AUDIT_REPORT = numeroListe ++;
    	OPERATIONS_CODA = numeroListe ++;
        OPERATIONS_ESEGUITE = numeroListe ++;
        OPERATIONS_FALLITE = numeroListe ++;
        OPERATIONS_INVALIDE = numeroListe ++;
        OPERATIONS_RICERCA = numeroListe ++;
        OPERATIONS_WAITING = numeroListe ++;
        CONFIGURAZIONE_EXTENDED = numeroListe ++;	
    	CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY = numeroListe ++;
    	CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY = numeroListe ++;
    	CONFIGURAZIONE_GESTIONE_POLICY_TOKEN = numeroListe ++;
    	CONFIGURAZIONE_GESTIONE_ATTRIBUTE_AUTHORITY = numeroListe ++;
    	CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA = numeroListe ++;
    	CONFIGURAZIONE_PROXY_PASS_REGOLA = numeroListe ++;
    	CONFIGURAZIONE_CANALI = numeroListe ++;
    	CONFIGURAZIONE_CANALI_NODI = numeroListe ++;
    	CONFIGURAZIONE_PLUGINS_ARCHIVI = numeroListe ++;
    	CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR = numeroListe ++;
    	CONFIGURAZIONE_PLUGINS_CLASSI = numeroListe ++;
    	CONFIGURAZIONE_ALLARMI = numeroListe ++;
    	CONFIGURAZIONE_ALLARMI_HISTORY = numeroListe ++;
    	CONFIGURAZIONE_HANDLERS_RICHIESTA = numeroListe ++;
    	CONFIGURAZIONE_HANDLERS_RISPOSTA = numeroListe ++;
    	CONFIGURAZIONE_HANDLERS_SERVIZIO = numeroListe ++;
    	REMOTE_STORE_KEY = numeroListe ++;
    }
    
	
	public static int getTotaleListe()
	{
		return numeroListe;
	}
}
