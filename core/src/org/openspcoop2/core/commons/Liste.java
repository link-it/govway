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
	
	public final static int PORTE_APPLICATIVE= 3;
	public final static int PORTE_APPLICATIVE_BY_SOGGETTO= 4;
	public final static int PORTE_APPLICATIVE_PROP = 5;
	public final static int PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO = 6;
	public final static int PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST = 7;
	public final static int PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE = 8;
	public final static int PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA = 9;
	public final static int PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA = 10;
	public final static int PORTE_APPLICATIVE_MTOM_REQUEST = 11;
	public final static int PORTE_APPLICATIVE_MTOM_RESPONSE = 12;
	public final static int PORTE_APPLICATIVE_EXTENDED = 13;
	
	public final static int PORTE_DELEGATE = 14;
	public final static int PORTE_DELEGATE_BY_SOGGETTO= 15;
	public final static int PORTE_DELEGATE_SERVIZIO_APPLICATIVO = 16;
	public final static int PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST = 17;
	public final static int PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE = 18;
	public final static int PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA = 19;
	public final static int PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA = 20;
	public final static int PORTE_DELEGATE_MTOM_REQUEST = 21;
	public final static int PORTE_DELEGATE_MTOM_RESPONSE = 22;
	public final static int PORTE_DELEGATE_EXTENDED = 23;
	
	public final static int SERVIZIO_APPLICATIVO = 24;
	public final static int SERVIZI_APPLICATIVI_BY_SOGGETTO= 25;
	
	public final static int ACCORDI = 26;
	public final static int ACCORDI_AZIONI = 27;
	public final static int ACCORDI_EROGATORI = 28;
	public final static int ACCORDI_EROGATORI_FRUITORI = 29;
	public final static int ACCORDI_SERVIZIO_APPLICATIVO = 30;
	public final static int ACCORDI_PORTTYPE = 31;
	public final static int ACCORDI_PORTTYPE_AZIONI = 32;
	public final static int ACCORDI_PORTTYPE_AZIONI_MESSAGE_INPUT = 33;
	public final static int ACCORDI_PORTTYPE_AZIONI_MESSAGE_OUTPUT = 34;
	public final static int ACCORDI_ALLEGATI = 35;
	
	public final static int ACCORDI_COOPERAZIONE = 36;
	public final static int ACCORDI_COOP_PARTECIPANTI= 37;
	public final static int ACCORDI_COOP_ALLEGATI= 38;
	public final static int ACCORDI_COMPONENTI = 39;
	
	public final static int SERVIZI = 40;	
	public final static int SERVIZI_FRUITORI= 41;
	public final static int SERVIZI_SERVIZIO_APPLICATIVO = 42;
	public final static int SERVIZI_ALLEGATI= 43;
	public final static int SERVIZI_PORTE_APPLICATIVE = 44;
	
	public final static int	RUOLI	= 45;
	
	public final static int	REGISTRI = 46;
	public final static int	ROUTING = 47;
	public final static int SYSTEM_PROPERTIES = 48;
	
	public final static int	TRACCE = 49;
	
	public final static int MESSAGGI_DIAGNOSTICI = 50;
	
	public final static int STATISTICHE_STATO = 51;
	
	public final static int SU = 52;

	public final static int MONITOR_MSG = 53;
	
	public final static int FILTRI = 54;

	public final static int AUDIT_REPORT = 55;
	
	public final static int OPERATIONS_CODA = 56;
    public final static int OPERATIONS_ESEGUITE = 57;
    public final static int OPERATIONS_FALLITE = 58;
    public final static int OPERATIONS_INVALIDE = 59;
    public final static int OPERATIONS_RICERCA = 60;
    public final static int OPERATIONS_WAITING = 61;
    
    public final static int CONFIGURAZIONE_EXTENDED = 62;	
	

    
    
	
	public static int getTotaleListe()
	{
		return 63;
	}
	
	
}
