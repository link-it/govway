/*
 * OpenSPCoop - Customizable API Gateway
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
	public final static int PDD = 0;
	public final static int PDD_SOGGETTI = 1;
	
	public final static int SOGGETTI = 2;
	public final static int SOGGETTI_RUOLI = 3;
	
	public final static int PORTE_APPLICATIVE= 4;
	public final static int PORTE_APPLICATIVE_BY_SOGGETTO= 5;
	public final static int PORTE_APPLICATIVE_PROP = 6;
	public final static int PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO = 7;
	public final static int PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST = 8;
	public final static int PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE = 9;
	public final static int PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA = 10;
	public final static int PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA = 11;
	public final static int PORTE_APPLICATIVE_MTOM_REQUEST = 12;
	public final static int PORTE_APPLICATIVE_MTOM_RESPONSE = 13;
	public final static int PORTE_APPLICATIVE_RUOLI = 14;
	public final static int PORTE_APPLICATIVE_EXTENDED = 15;
	
	public final static int PORTE_DELEGATE = 16;
	public final static int PORTE_DELEGATE_BY_SOGGETTO= 17;
	public final static int PORTE_DELEGATE_SERVIZIO_APPLICATIVO = 18;
	public final static int PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST = 19;
	public final static int PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE = 20;
	public final static int PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA = 21;
	public final static int PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA = 22;
	public final static int PORTE_DELEGATE_MTOM_REQUEST = 23;
	public final static int PORTE_DELEGATE_MTOM_RESPONSE = 24;
	public final static int PORTE_DELEGATE_RUOLI = 25;
	public final static int PORTE_DELEGATE_EXTENDED = 26;
	
	public final static int SERVIZIO_APPLICATIVO = 27;
	public final static int SERVIZI_APPLICATIVI_BY_SOGGETTO= 28;
	public final static int SERVIZIO_APPLICATIVO_RUOLI = 29;
	
	public final static int ACCORDI = 30;
	public final static int ACCORDI_AZIONI = 31;
	public final static int ACCORDI_EROGATORI = 32;
	public final static int ACCORDI_PORTTYPE = 33;
	public final static int ACCORDI_PORTTYPE_AZIONI = 34;
	public final static int ACCORDI_PORTTYPE_AZIONI_MESSAGE_INPUT = 35;
	public final static int ACCORDI_PORTTYPE_AZIONI_MESSAGE_OUTPUT = 36;
	public final static int ACCORDI_ALLEGATI = 37;
	public final static int ACCORDI_API_RESOURCES = 38;
	
	public final static int ACCORDI_COOPERAZIONE = 39;
	public final static int ACCORDI_COOP_PARTECIPANTI= 40;
	public final static int ACCORDI_COOP_ALLEGATI= 41;
	public final static int ACCORDI_COMPONENTI = 42;
	
	public final static int SERVIZI = 43;	
	public final static int SERVIZI_FRUITORI= 44;
	public final static int SERVIZI_FRUITORI_PORTE_DELEGATE = 45;
	public final static int SERVIZI_ALLEGATI= 46;
	public final static int SERVIZI_PORTE_APPLICATIVE = 47;
	
	public final static int	RUOLI	= 48;	
	public final static int	REGISTRI = 49;
	public final static int	ROUTING = 50;
	public final static int SYSTEM_PROPERTIES = 51;
	
	public final static int	TRACCE = 52;
	
	public final static int MESSAGGI_DIAGNOSTICI = 53;
	
	public final static int STATISTICHE_STATO = 54;
	
	public final static int SU = 55;

	public final static int MONITOR_MSG = 56;
	
	public final static int FILTRI = 57;

	public final static int AUDIT_REPORT = 58;
	
	public final static int OPERATIONS_CODA = 59;
    public final static int OPERATIONS_ESEGUITE = 60;
    public final static int OPERATIONS_FALLITE = 61;
    public final static int OPERATIONS_INVALIDE = 62;
    public final static int OPERATIONS_RICERCA = 63;
    public final static int OPERATIONS_WAITING = 64;
    
    public final static int CONFIGURAZIONE_EXTENDED = 65;	
	

    
    
	
	public static int getTotaleListe()
	{
		return 66;
	}
	
	
}
