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
	public final static String ACCORDI_DI_SERVIZIO = "accordi";
	/** String che contiene il nome della directory che contiene l'accordo di cooperazione */
	public final static String ACCORDI_DI_COOPERAZIONE = "accordi_cooperazione";
	/** String che contiene il nome della directory che contiene le porte di dominio */
	public final static String PORTE_DI_DOMINIO = "porte_di_dominio";
	/** String che contiene il nome di un wsdl definitorio */
	public final static String WSDL_DEFINITORIO = "definitorio.xsd";
	/** String che contiene il nome di un wsdl concettuale */
	public final static String WSDL_CONCETTUALE = "concettuale.wsdl";
	/** String che contiene il nome di un wsdl logico erogatore */
	public final static String WSDL_LOGICO_EROGATORE = "logicoErogatore.wsdl";
	/** String che contiene il nome di un wsdl logico fruitore*/
	public final static String WSDL_LOGICO_FRUITORE = "logicoFruitore.wsdl";
	/** String che contiene il nome di un wsdl implementativo erogatore */
	public final static String WSDL_IMPLEMENTATIVO_EROGATORE = "implementazioneErogatore.wsdl";
	/** String che contiene il nome di un wsdl implementativo fruitore*/
	public final static String WSDL_IMPLEMENTATIVO_FRUITORE = "implementazioneFruitore.wsdl";
	/** String che contiene il nome di una conversazione concettuale */
	public final static String SPECIFICA_CONVERSAZIONE_CONCETTUALE = "specificaConversazioneConcettuale.xml";
	/** String che contiene il nome di una conversazione erogatore */
	public final static String SPECIFICA_CONVERSAZIONE_EROGATORE = "specificaConversazioneErogatore.xml";
	/** String che contiene il nome di una conversazione fruitore*/
	public final static String SPECIFICA_CONVERSAZIONE_FRUITORE = "specificaConversazioneFruitore.xml";
	/** String che contiene l'indirizzo di un wsdl non definito */
	public final static String WSDL_UNDEFINED = "http://undefined";
	/** String che contiene l'indirizzo di un wsbl non definito */
	public final static String SPECIFICA_CONVERSAZIONE_UNDEFINED = "http://undefined";
	/** String che contiene il prefisso di una directory che contiene wsdl di un accordo/servizio */
	public final static String WSDL_DIR = "wsdl";
	/** String che contiene il prefisso di una directory che contiene wsbl di un accordo */
	public final static String SPECIFICHE_CONVERSAZIONI_DIR = "specificheConversazioni";
	/** String che contiene il prefisso di una directory che contiene allegati di un accordo/servizio */
	public final static String ALLEGATI_DIR = "allegati";
	/** String che contiene il prefisso di una directory che contiene specificheSemiformali di un accordo/servizio */
	public final static String SPECIFICHE_SEMIFORMALI_DIR = "specificheSemiformali";
	/** String che contiene il prefisso di una directory che contiene specificheCoordinamento di un accordo di servizio composto */
	public final static String SPECIFICHE_COORDINAMENTO_DIR = "specificheCoordinamento";
	/** String che contiene il prefisso di una directory che contiene specificheSicurezza di un servizio */
	public final static String SPECIFICHE_SICUREZZA_DIR = "specificheSicurezza";
	/** String che contiene il prefisso di una directory che contiene specificheLivelliServizio di un servizio */
	public final static String SPECIFICHE_LIVELLI_SERVIZIO_DIR = "specificheLivelliServizio";
	/** String che contiene il nome della directory che contiene i servizi di un soggetto */
	public final static String SERVIZI = "servizi";
	/** String che contiene il nome del file che contiene l'index dei servizi di un soggetto */
	public final static String INDEX_SERVIZI = "servizi.index";
	/** String che contiene il nome del file che contiene l'index dei servizi di un soggetto */
	public final static String INDEX_SERVIZI_MANIFEST = "----------------------------------------------------------------------------------------------------\n"+
		"File di index dei servizi erogati da un soggetto.\n"+
		"----------------------------------------------------------------------------------------------------\n"+
		"Legenda: uriAccordo#tipoSoggetto/nomeSoggetto#tipoServizio/nomeServizio#indicazioneServizioCorrelato\n"+
		"----------------------------------------------------------------------------------------------------\n\n";
	public final static String INDEX_SERVIZI_SEPARATORE="#";
	
	 /** Slash di una url */
    public final static String URL_SEPARATOR = CostantiRegistroServizi.URL_SEPARATOR;
}
