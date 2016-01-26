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

import org.openspcoop2.core.commons.AccordiUtils;

/**
 * Costanti per gli oggetti dao del package org.openspcoop.dao.registry
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiRegistroServizi {

	/** Costante che indica un profilo one-way */
    public final static ProfiloCollaborazione ONEWAY = ProfiloCollaborazione.ONEWAY;
    /** Costante che indica un profilo sincrono */
    public final static ProfiloCollaborazione SINCRONO = ProfiloCollaborazione.SINCRONO;
    /** Costante che indica un profilo asincrono simmetrico*/
    public final static ProfiloCollaborazione ASINCRONO_SIMMETRICO = ProfiloCollaborazione.ASINCRONO_SIMMETRICO;
    /** Costante che indica un profilo asincrono asimmetrico*/
    public final static ProfiloCollaborazione ASINCRONO_ASIMMETRICO = ProfiloCollaborazione.ASINCRONO_ASIMMETRICO;

    /** Costante che indica una funzionalita' abilitata */
    public final static StatoFunzionalita ABILITATO = StatoFunzionalita.ABILITATO;
    /** Costante che indica una funzionalita' disabilitata */
    public final static StatoFunzionalita DISABILITATO = StatoFunzionalita.DISABILITATO;
	
    /** Costante che indica implementazioni delle porte di dominio */
    public final static String IMPLEMENTAZIONE_STANDARD = "standard";
    
    /** Slash di una url */
    public final static String URL_SEPARATOR = "/";
    
    /** Nome di una eventuale cache per il registro servizi */
    public final static String CACHE_REGISTRO_SERVIZI = "registroServizi";
    
    /** Logger */
    public static final String REGISTRO_DRIVER_DB_LOGGER = "DRIVER_DB_REGISTRO";
    
    /** Profili Azione */
    public static final String PROFILO_AZIONE_DEFAULT = "default";
    public static final String PROFILO_AZIONE_RIDEFINITO = "ridefinito";
    
    /** Stile WSDL */
    public static final BindingStyle WSDL_STYLE_DOCUMENT = BindingStyle.DOCUMENT;
    public static final BindingStyle WSDL_STYLE_RPC = BindingStyle.RPC;
    public static final BindingUse WSDL_USE_ENCODED = BindingUse.ENCODED;
    public static final BindingUse WSDL_USE_LITERAL = BindingUse.LITERAL;
    
    /** Valori di default per IDAccordo */
    public static final int SOGGETTO_REFERENTE_DEFAULT=AccordiUtils.SOGGETTO_REFERENTE_DEFAULT;
    public static final String VERSIONE_DEFAULT=AccordiUtils.VERSIONE_DEFAULT;
    
    public final static String MANIFESTO_XML = "manifesto.xml";

	public final static String SPECIFICA_CONVERSAZIONE_DIR = "specificaConversazione";
	public final static String SPECIFICA_CONVERSAZIONE_CONCETTUALE = 
		"SpecificaConversazioneConcettuale.xml";
	public final static String SPECIFICA_CONVERSAZIONE_LATO_EROGATORE = 
		"SpecificaConversazioneErogatore.xml";
	public final static String SPECIFICA_CONVERSAZIONE_LATO_FRUITORE = 
		"SpecificaConversazioneFruitore.xml";

	public final static String SPECIFICA_INTERFACCIA_DIR = "specificaInterfaccia";
	public final static String SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL = 
		"InterfacciaConcettuale.wsdl";
	public final static String SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL = 
		"InterfacciaLogicaErogatore.wsdl";
	public final static String SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL = 
		"InterfacciaLogicaFruitore.wsdl";
	
	public final static String ALLEGATI_DIR = "allegati";
	public final static String ALLEGATO_DEFINITORIO_XSD = "InterfacciaDefinitoria.xsd";
	
	public final static String SPECIFICA_SEMIFORMALE_DIR = "specificaSemiformale";		
	
	public final static String SPECIFICA_LIVELLI_SERVIZIO_DIR = "specificaLivelliServizio";	
	
	public final static String SPECIFICA_PORTI_ACCESSO_DIR = "specificaPortiAccesso";	
	public final static String SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL = "PortiAccessoErogatore.wsdl";
	public final static String SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL = "PortiAccessoFruitore.wsdl";
	
	public final static String SPECIFICA_SICUREZZA_DIR = "specificaSicurezza";	
	
	public final static String SPECIFICA_COORDINAMENTO_DIR = "specificaCoordinamento";	
	
	public final static String ROOT_LOCAL_NAME_REGISTRO = "registro-servizi";
	public final static String LOCAL_NAME_SOGGETTO = "soggetto";
	public final static String LOCAL_NAME_PORTA_DOMINIO = "porta-dominio";
	public final static String LOCAL_NAME_PORTA_ACCORDO_SERVIZIO_PARTE_COMUNE = "accordo-servizio-parte-comune";
	public final static String LOCAL_NAME_PORTA_ACCORDO_SERVIZIO_PARTE_SPECIFICA = "accordo-servizio-parte-specifica";
	public final static String LOCAL_NAME_PORTA_ACCORDO_COOPERAZIONE = "accordo-cooperazione";
	public final static String LOCAL_NAME_FRUITORE = "fruitore";
	public final static String TARGET_NAMESPACE = "http://www.openspcoop2.org/core/registry";
}
