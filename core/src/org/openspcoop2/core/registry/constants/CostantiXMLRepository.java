/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.registry.constants;

/**
 * Costanti per gli oggetti dao del package org.openspcoop.dao.registry
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiXMLRepository {

	    
    // Per gestione XML/UDDI:
	/** String che contiene il nome della directory che contiene l'accordo di servizio */
	public static final String ACCORDI_DI_SERVIZIO = "accordi";
	/** String che contiene il nome della directory che contiene l'accordo di cooperazione */
	public static final String ACCORDI_DI_COOPERAZIONE = "accordi_cooperazione";
	/** String che contiene il nome della directory che contiene le porte di dominio */
	public static final String PORTE_DI_DOMINIO = "porte_di_dominio";
	/** String che contiene il nome della directory che contiene i gruppi */
	public static final String GRUPPI = "gruppi";
	/** String che contiene il nome della directory che contiene i ruoli */
	public static final String RUOLI = "ruoli";
	/** String che contiene il nome della directory che contiene i scope */
	public static final String SCOPE = "scope";
	/** String che contiene il nome di un wsdl definitorio */
	public static final String WSDL_DEFINITORIO = "definitorio.xsd";
	/** String che contiene il nome di un wsdl concettuale */
	public static final String WSDL_CONCETTUALE = "concettuale.wsdl";
	/** String che contiene il nome di un wsdl logico erogatore */
	public static final String WSDL_LOGICO_EROGATORE = "logicoErogatore.wsdl";
	/** String che contiene il nome di un wsdl logico fruitore*/
	public static final String WSDL_LOGICO_FRUITORE = "logicoFruitore.wsdl";
	/** String che contiene il nome di un wsdl implementativo erogatore */
	public static final String WSDL_IMPLEMENTATIVO_EROGATORE = "implementazioneErogatore.wsdl";
	/** String che contiene il nome di un wsdl implementativo fruitore*/
	public static final String WSDL_IMPLEMENTATIVO_FRUITORE = "implementazioneFruitore.wsdl";
	/** String che contiene il nome di una conversazione concettuale */
	public static final String SPECIFICA_CONVERSAZIONE_CONCETTUALE = "specificaConversazioneConcettuale.xml";
	/** String che contiene il nome di una conversazione erogatore */
	public static final String SPECIFICA_CONVERSAZIONE_EROGATORE = "specificaConversazioneErogatore.xml";
	/** String che contiene il nome di una conversazione fruitore*/
	public static final String SPECIFICA_CONVERSAZIONE_FRUITORE = "specificaConversazioneFruitore.xml";
	/** String che contiene l'indirizzo di un wsdl non definito */
	public static final String WSDL_UNDEFINED = "http://undefined";
	/** String che contiene l'indirizzo di un wsbl non definito */
	public static final String SPECIFICA_CONVERSAZIONE_UNDEFINED = "http://undefined";
	/** String che contiene il prefisso di una directory che contiene wsdl di un accordo/servizio */
	public static final String WSDL_DIR = "wsdl";
	/** String che contiene il prefisso di una directory che contiene wsbl di un accordo */
	public static final String SPECIFICHE_CONVERSAZIONI_DIR = "specificheConversazioni";
	/** String che contiene il prefisso di una directory che contiene allegati di un accordo/servizio */
	public static final String ALLEGATI_DIR = "allegati";
	/** String che contiene il prefisso di una directory che contiene specificheSemiformali di un accordo/servizio */
	public static final String SPECIFICHE_SEMIFORMALI_DIR = "specificheSemiformali";
	/** String che contiene il prefisso di una directory che contiene specificheCoordinamento di un accordo di servizio composto */
	public static final String SPECIFICHE_COORDINAMENTO_DIR = "specificheCoordinamento";
	/** String che contiene il prefisso di una directory che contiene specificheSicurezza di un servizio */
	public static final String SPECIFICHE_SICUREZZA_DIR = "specificheSicurezza";
	/** String che contiene il prefisso di una directory che contiene specificheLivelliServizio di un servizio */
	public static final String SPECIFICHE_LIVELLI_SERVIZIO_DIR = "specificheLivelliServizio";
	/** String che contiene il nome della directory che contiene i servizi di un soggetto */
	public static final String SERVIZI = "servizi";
	/** String che contiene il nome del file che contiene l'index dei servizi di un soggetto */
	public static final String INDEX_SERVIZI = "servizi.index";
	/** String che contiene il nome del file che contiene l'index dei servizi di un soggetto */
	public static final String INDEX_SERVIZI_MANIFEST = "----------------------------------------------------------------------------------------------------\n"+
		"File di index dei servizi erogati da un soggetto.\n"+
		"----------------------------------------------------------------------------------------------------\n"+
		"Legenda: uriAccordo#tipoSoggetto/nomeSoggetto#tipoServizio/nomeServizio#indicazioneServizioCorrelato\n"+
		"----------------------------------------------------------------------------------------------------\n\n";
	public static final String INDEX_SERVIZI_SEPARATORE="#";
	
	 /** Slash di una url */
    public static final String URL_SEPARATOR = CostantiRegistroServizi.URL_SEPARATOR;
}
