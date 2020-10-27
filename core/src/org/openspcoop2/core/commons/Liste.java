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
	public final static int PDD;
	public final static int PDD_SOGGETTI;
	
	public final static int SOGGETTI;
	public final static int SOGGETTI_RUOLI;
	
	public final static int PORTE_APPLICATIVE;
	public final static int PORTE_APPLICATIVE_BY_SOGGETTO;
	public final static int PORTE_APPLICATIVE_PROP;
	public final static int PORTE_APPLICATIVE_AZIONI;
	public final static int PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO;
	public final static int PORTE_APPLICATIVE_SOGGETTO;
	public final static int PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO;
	public final static int PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST;
	public final static int PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE;
	public final static int PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA;
	public final static int PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
	public final static int PORTE_APPLICATIVE_MTOM_REQUEST;
	public final static int PORTE_APPLICATIVE_MTOM_RESPONSE;
	public final static int PORTE_APPLICATIVE_RUOLI;
	public final static int PORTE_APPLICATIVE_SCOPE;
	public final static int PORTE_APPLICATIVE_EXTENDED;
	public final static int PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
	public final static int PORTE_APPLICATIVE_TRASFORMAZIONI;
	public final static int PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
	public final static int PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
	public final static int PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER;
	public final static int PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRI;
	public final static int PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO;
	public final static int PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO;
	public final static int PORTE_APPLICATIVE_PROPRIETA_AUTENTICAZIONE;
	public final static int PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE;
	public final static int PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO;
	public final static int PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
	public final static int PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPRIETA;
	public final static int PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA;
	
	public final static int PORTE_DELEGATE;
	public final static int PORTE_DELEGATE_BY_SOGGETTO;
	public final static int PORTE_DELEGATE_PROP;
	public final static int PORTE_DELEGATE_SERVIZIO_APPLICATIVO;
	public final static int PORTE_DELEGATE_AZIONI;
	public final static int PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST;
	public final static int PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE;
	public final static int PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA;
	public final static int PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
	public final static int PORTE_DELEGATE_MTOM_REQUEST;
	public final static int PORTE_DELEGATE_MTOM_RESPONSE;
	public final static int PORTE_DELEGATE_RUOLI;
	public final static int PORTE_DELEGATE_SCOPE;
	public final static int PORTE_DELEGATE_EXTENDED;
	public final static int PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
	public final static int PORTE_DELEGATE_TRASFORMAZIONI;
	public final static int PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE;
	public final static int PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER;
	public final static int PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER;
	public final static int PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_PARAMETRI;
	public final static int PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO;
	public final static int PORTE_DELEGATE_PROPRIETA_AUTENTICAZIONE;
	public final static int PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE;
	public final static int PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO;
	
	public final static int SERVIZIO_APPLICATIVO;
	public final static int SERVIZI_APPLICATIVI_BY_SOGGETTO;
	public final static int SERVIZIO_APPLICATIVO_RUOLI;
	
	public final static int ACCORDI;
	public final static int ACCORDI_AZIONI;
	public final static int ACCORDI_EROGATORI;
	public final static int ACCORDI_PORTTYPE;
	public final static int ACCORDI_PORTTYPE_AZIONI;
	public final static int ACCORDI_PORTTYPE_AZIONI_MESSAGE_INPUT;
	public final static int ACCORDI_PORTTYPE_AZIONI_MESSAGE_OUTPUT;
	public final static int ACCORDI_ALLEGATI;
	public final static int ACCORDI_API_RESOURCES;
	public final static int ACCORDI_API_RESOURCES_RESPONSE;
	public final static int ACCORDI_API_RESOURCES_REPRESENTATION_REQUEST;
	public final static int ACCORDI_API_RESOURCES_REPRESENTATION_RESPONSE;
	public final static int ACCORDI_API_RESOURCES_PARAMETERS_REQUEST;
	public final static int ACCORDI_API_RESOURCES_PARAMETERS_RESPONSE;
	
	public final static int ACCORDI_COOPERAZIONE;
	public final static int ACCORDI_COOP_PARTECIPANTI;
	public final static int ACCORDI_COOP_ALLEGATI;
	public final static int ACCORDI_COMPONENTI;
	
	public final static int SERVIZI;	
	public final static int SERVIZI_FRUITORI;
	public final static int SERVIZI_FRUITORI_PORTE_DELEGATE;
	public final static int SERVIZI_ALLEGATI;
	public final static int CONFIGURAZIONE_EROGAZIONE;
	public final static int CONFIGURAZIONE_FRUIZIONE;

	public final static int	GRUPPI;
	public final static int	RUOLI;	
	public final static int	SCOPE;	
	public final static int	REGISTRI;
	public final static int	ROUTING;
	public final static int SYSTEM_PROPERTIES;
	
	public final static int STATISTICHE_STATO;
	
	public final static int SU;
	public final static int UTENTI_SERVIZI;
	public final static int UTENTI_SOGGETTI;

	public final static int MONITOR_MSG;
	
	public final static int FILTRI;

	public final static int AUDIT_REPORT;
	
	public final static int OPERATIONS_CODA;
    public final static int OPERATIONS_ESEGUITE;
    public final static int OPERATIONS_FALLITE;
    public final static int OPERATIONS_INVALIDE;
    public final static int OPERATIONS_RICERCA;
    public final static int OPERATIONS_WAITING;
    
    public final static int CONFIGURAZIONE_EXTENDED;	
    
    public final static int CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY;	
    public final static int CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;
    public final static int CONFIGURAZIONE_GESTIONE_POLICY_TOKEN;
    public final static int CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
    public final static int CONFIGURAZIONE_PROXY_PASS_REGOLA;
    
    public final static int CONFIGURAZIONE_CANALI;
    public final static int CONFIGURAZIONE_CANALI_NODI;
    
	

    private static int numeroListe = 0;
    static {
    	PDD = numeroListe ++;
    	PDD_SOGGETTI = numeroListe ++;
    	SOGGETTI = numeroListe ++;
    	SOGGETTI_RUOLI = numeroListe ++;
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
    	SERVIZIO_APPLICATIVO = numeroListe ++;
    	SERVIZI_APPLICATIVI_BY_SOGGETTO = numeroListe ++;
    	SERVIZIO_APPLICATIVO_RUOLI = numeroListe ++;
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
    	CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA = numeroListe ++;
    	CONFIGURAZIONE_PROXY_PASS_REGOLA = numeroListe ++;
    	CONFIGURAZIONE_CANALI = numeroListe ++;
    	CONFIGURAZIONE_CANALI_NODI = numeroListe ++;
    }
    
	
	public static int getTotaleListe()
	{
		return numeroListe;
	}
}
