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

package org.openspcoop2.protocol.basic;

import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.ImportMode;

/**	
 * Costanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

	
	/** ERRORE APPLICATIVO */
	public static final String ERRORE_INTEGRAZIONE_NAMESPACE = "http://www.openspcoop2.org/core/errore_integrazione";
	public static final String ERRORE_INTEGRAZIONE_PREFIX = "eccIntegrazione";
	public final static String ERRORE_INTEGRAZIONE_PREFIX_CODE = "OPENSPCOOP2_ORG_";
	
	public static final String ERRORE_PROTOCOLLO_PREFIX = "eccProtocollo";
	public static final String ERRORE_PROTOCOLLO_NAMESPACE = "http://www.openspcoop2.org/core/errore_protocollo";
		
    /** Ora di creazione di un tracciamento. Il token 'locale' indica il tempo locale
    non sincronizzato da sistema che lo imposta*/
    public static final String TIPO_TEMPO_LOCALE = "Locale";
    /** Ora di creazione di un tracciamento. Il token 'sincronizzato' indica il tempo sincronizzato di rete*/
    public static final String TIPO_TEMPO_SINCRONIZZATO = "Sincronizzato";

    /** Profilo di Trasmissione, attributo inoltro : Al piu' una volta. */
    public static final String PROFILO_TRASMISSIONE_SENZA_DUPLICATI = "ALPIUUNAVOLTA";
    /** Profilo di Trasmissione, attributo inoltro : Piu' una volta. */
    public static final String PROFILO_TRASMISSIONE_CON_DUPLICATI = "PIUDIUNAVOLTA";

	/** Logger */
    public static final String LOGANALIZER_DRIVER_DB_LOGGER = "DRIVER_DB_LOGANALIZER";
    
    /** Definisce un tipo di EccezioneApplicativa */
    public static final String ECCEZIONE_PROTOCOLLO = "EccezioneProtocollo";
    /** Definisce un tipo di EccezioneApplicativa */
    public static final String ECCEZIONE_INTEGRAZIONE = "EccezioneIntegrazione";
    
	/** Archive mode */
	public static final ArchiveMode OPENSPCOOP_ARCHIVE_MODE = new ArchiveMode("openspcoop");
	public static final ImportMode OPENSPCOOP_IMPORT_ARCHIVE_MODE = new ImportMode(OPENSPCOOP_ARCHIVE_MODE);
	public static final ExportMode OPENSPCOOP_EXPORT_ARCHIVE_MODE = new ExportMode(OPENSPCOOP_ARCHIVE_MODE,true);
	/** Archive mode type */
	public static final ArchiveModeType OPENSPCOOP_ARCHIVE_MODE_TYPE = new ArchiveModeType("openspcoop");
	/** Archive extension */
	public static final String OPENSPCOOP_ARCHIVE_EXT = "zip";
	
	/**
	 *  Archive extension 
	 *  NOTA: i nomi dei file non sono significativi. L'export li 'normalizzera' (es. viene eliminato lo '/')
	 *  	  il vero nome è dentro la definizione xml.
	 *
	 * openspcoop
	 *      | - configurazione
	 *		|		  |
	 *		|		  | - config.xml
	 *      |
	 *      | - porteDominio
	 *		|		  |
	 *		|		  | - pdd1.xml
	 *		|		  |    ...
	 *      | 
	 *      | - soggetti
	 *		|		|
	 *		|		| - tipo_nome
	 *		|		|		|
	 *		|		|		| - registroServizi.xml
	 *		|		|		| - config.xml
	 *		|		|		|
	 *		|		|		| - serviziApplicativi
	 *		|		|		|		|
	 *		|		|		|		| - sa1.xml
	 *		|		|		|		|	...
	 *		|		|		|		
	 *		|		|		| - porteDelegate
	 *		|		|		|		|
	 *		|		|		|		| - pd1.xml
	 *		|		|		|		|   ...
	 *		|		|		|		
	 *		|		|		| - porteApplicative
	 *		|		|		|		|
	 *		|		|		|		| - pa1.xml
	 *		|		|		|		|   ...	
	 *		|		|		|		 
	 *		|		|		| - accordiServizioParteComune
	 *		|		|		|		|
	 *		|		|		|		| - nome[_versione]
	 *		|		|		|		|		|
	 *		|		|		|		|		| - aspc1.xml
	 *		|		|		|		|		| - allegati
	 *		|		|		|		|		|       | 
	 *		|		|		|		|		|       | - doc.txt
	 *		|		|		|		|		|   ...
	 *		|		|		|		|
	 *		|		|		|		| - ...		
	 *		|		|		|		  
	 *		|		|		| - accordiServizioParteSpecifica
	 *		|		|		|		|
	 *		|		|		|		| - nome[_versione]
	 *		|		|		|		|		|
	 *		|		|		|		|		| - asps1.xml
	 *		|		|		|		|		| - allegati
	 *		|		|		|		|		|       | 
	 *		|		|		|		|		|       | - doc.txt
	 *		|		|		|		|		| - fruitori
	 *		|		|		|		|		|       | 
	 *		|		|		|		|		|       | - tipo_nome.xml
	 *		|		|		|		|		|   ...
	 *		|		|		|		|
	 *		|		|		|		| - ...			
	 *		|		|		|		  
	 *		|		|		| - accordiCooperazione
	 *		|		|		|		|
	 *		|		|		|		| - nome[_versione]
	 *		|		|		|		|		|
	 *		|		|		|		|		| - ac1.xml
	 *		|		|		|		|		| - allegati
	 *		|		|		|		|		|       | 
	 *		|		|		|		|		|       | - doc.txt
	 *		|		|		|		|		|   ...
	 *		|		|		|		|
	 *		|		|		|		| - ...		
	 *		|		|		|		  
	 *		|		|		|
	 *		|		|		| - accordiServizioComposto
	 *		|		|		|		|
	 *		|		|		|		| - nome[_versione]
	 *		|		|		|		|		|
	 *		|		|		|		|		| - asc1.xml
	 *		|		|		|		|		| - allegati
	 *		|		|		|		|		|       | 
	 *		|		|		|		|		|       | - doc.txt
	 *		|		|		|		|		|   ...
	 *		|		|		|		|
	 *		|		|		|		| - ...		
	 *		|		|		|		  
	 *		|		|		|		  		  
	 **/
	public static final String OPENSPCOOP2_ARCHIVE_ROOT_DIR = "openspcoop";
	
	public static final String OPENSPCOOP2_ARCHIVE_INFORMATION_MISSING = "informationMissing.xml";
	
	public static final String OPENSPCOOP2_ARCHIVE_EXTENDED_DIR = "extended";
	public static final String OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_NAME = "extended";
	public static final String OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_EXT = ".bin";
	
	public static final String OPENSPCOOP2_ARCHIVE_CONFIGURAZIONE_DIR = "configurazione";
	public static final String OPENSPCOOP2_ARCHIVE_CONFIGURAZIONE_FILE_NAME = "configurazione.xml";
	
	public static final String OPENSPCOOP2_ARCHIVE_PORTE_DOMINIO_DIR = "porteDominio";
	
	public static final String OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR = "soggetti";
	public static final String OPENSPCOOP2_ARCHIVE_SOGGETTI_FILE_NAME_REGISTRO = "soggettoRegistroServizi.xml";
	public static final String OPENSPCOOP2_ARCHIVE_SOGGETTI_FILE_NAME_CONFIG = "soggettoConfigurazione.xml";
	
	public static final String OPENSPCOOP2_ARCHIVE_SERVIZI_APPLICATIVI_DIR = "serviziApplicativi";
	public static final String OPENSPCOOP2_ARCHIVE_PORTE_DELEGATE_DIR = "porteDelegate";
	public static final String OPENSPCOOP2_ARCHIVE_PORTE_APPLICATIVE_DIR = "porteApplicative";
	
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR = "accordiServizioParteComune";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_FILE_NAME = "accordo.xml";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR = "accordiServizioParteSpecifica";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_FILE_NAME = "accordo.xml";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR = "accordiServizioComposto";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_FILE_NAME = "accordo.xml";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR = "accordiCooperazione";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_FILE_NAME = "accordo.xml";
	public static final String OPENSPCOOP2_ARCHIVE_FRUITORE_DIR = "fruitori";
	public static final String OPENSPCOOP2_ARCHIVE_FRUITORE_SERVIZI_APPLICATIVI_AUTORIZZATI = "serviziApplicativiAutorizzati.csv";
	
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL = "wsdl";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_INTERFACCIA_DEFINITORIA = "InterfacciaDefinitoria.xsd";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_CONCETTUALE_WSDL = "InterfacciaConcettuale.wsdl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_EROGATORE_WSDL = "InterfacciaLogicaErogatore.wsdl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_FRUITORE_WSDL = "InterfacciaLogicaFruitore.wsdl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_EROGATORE_WSDL = "InterfacciaImplementativaErogatore.wsdl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_FRUITORE_WSDL = "InterfacciaImplementativaFruitore.wsdl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_XSD_SCHEMA_COLLECTION = "XSDSchemaCollection.zip";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_XSD_SCHEMA_COLLECTION_ERROR = "XSDSchemaCollection.buildError.txt";
	
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_CONVERSAZIONI = "specificheConversazioni";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_CONCETTUALE = "ConversazioneConcettuale.xml";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_LOGICA_EROGATORE = "ConversazioneLogicaErogatore.xml";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_LOGICA_FRUITORE = "ConversazioneLogicaFruitore.xml";
	
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI = "allegati";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI = "specificheSemiformali";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_COORDINAMENTO = "specificheCoordinamento";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_LIVELLI_SERVIZIO = "specificheLivelliServizio";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SICUREZZA = "specificheSicurezza";
	
	
}
