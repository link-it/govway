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



package org.openspcoop2.protocol.sdi.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe dove sono fornite le stringhe costanti, definite dalla specifica del protocollo SdI, 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SDICostanti {
   
	public final static String OPENSPCOOP2_LOCAL_HOME = "GOVWAY_HOME";
	
	public final static String SDI_PROTOCOL_NAME = "sdi";
	
    public final static String SDI_PROPERTIES_LOCAL_PATH = "sdi_local.properties";
    public final static String SDI_PROPERTIES = "SDI_PROPERTIES";
	
    
    public final static String SDI_PROTOCOL_NAMESPACE="www.openspcoop2.org/protocols/sdi";
    
    public final static boolean SDI_FATTURAZIONE_ATTIVA = true;
    public final static boolean SDI_FATTURAZIONE_PASSIVA = false;
    
    public final static String SDI_TIPO_FATTURA_XML = "XML";
    public final static String SDI_TIPO_FATTURA_ZIP = "ZIP";
    public final static String SDI_TIPO_FATTURA_P7M = "P7M";
    
    public final static String SDI_FATTURA_ESTENSIONE_XML = "xml";
    public final static String SDI_FATTURA_ESTENSIONE_P7M = "xml.p7m";
    public final static String SDI_FATTURA_ESTENSIONE_ZIP = "zip";
    
    public final static String SDI_ATTRIBUTE_VERSION_FATTURA_PA_10 = "1.0";
    public final static String SDI_ATTRIBUTE_VERSION_FATTURA_PA_11 = "1.1";
    public final static String SDI_ATTRIBUTE_VERSION_FATTURA_PA_12 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType.FPA12.name();
    public final static String SDI_ATTRIBUTE_VERSION_FATTURA_PR_12 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType.FPR12.name();
    public final static String SDI_ATTRIBUTE_VERSION_FATTURA_SEMPLIFICATA_10 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.FormatoTrasmissioneType.FSM10.name();
    
    public final static String SDI_VERSIONE_FATTURA_PA_10 = it.gov.fatturapa.sdi.fatturapa.v1_0.constants.FormatoTrasmissioneType.SDI10.name();
    public final static String SDI_VERSIONE_FATTURA_PA_11 = it.gov.fatturapa.sdi.fatturapa.v1_1.constants.FormatoTrasmissioneType.SDI11.name();
    public final static String SDI_VERSIONE_FATTURA_PA_12 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType.FPA12.name();
    public final static String SDI_VERSIONE_FATTURA_PR_12 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType.FPR12.name();
    public final static String SDI_VERSIONE_FATTURA_SEMPLIFICATA_10 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.FormatoTrasmissioneType.FSM10.name();
    public final static List<String> SDI_VERSIONI_FATTURA = new ArrayList<String>();
    static{
    	SDI_VERSIONI_FATTURA.add(SDI_VERSIONE_FATTURA_PA_12);
    	SDI_VERSIONI_FATTURA.add(SDI_VERSIONE_FATTURA_PR_12);
    	SDI_VERSIONI_FATTURA.add(SDI_VERSIONE_FATTURA_PA_11);
    	SDI_VERSIONI_FATTURA.add(SDI_VERSIONE_FATTURA_PA_10);
    	SDI_VERSIONI_FATTURA.add(SDI_VERSIONE_FATTURA_SEMPLIFICATA_10);
    }
    
    public final static String SDI_MESSAGE_CONTEXT_FATTURA = "FatturaPA";
    public final static String SDI_MESSAGE_CONTEXT_FATTURA_METADATI = "FatturaPAMetadati";
    public final static String SDI_MESSAGE_CONTEXT_FATTURA_METADATI_BYTES = "FatturaPAMetadatiBytes";
    public final static String SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI = "MessaggioServizioSdI";
    public final static String SDI_MESSAGE_CONTEXT_AT_ARCHIVIO_ZIP = "ATArchivioZIP";
    public final static String SDI_MESSAGE_CONTEXT_AT_ARCHIVIO_XML = "ATArchivioXML";
    
    
    public final static String SDI_BUSTA_EXT_FORMATO_ARCHIVIO_INVIO_FATTURA = "FormatoArchivioInvioFattura";
    public final static String SDI_BUSTA_EXT_FORMATO_ARCHIVIO_BASE64 = "FormatoArchivioBase64";
    public final static String SDI_BUSTA_EXT_IDENTIFICATIVO_SDI = "IdentificativoSdI";
    public final static String SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA = "IdentificativoSdIFattura";
    public final static String SDI_BUSTA_EXT_NOME_FILE = "NomeFile";
    public final static String SDI_BUSTA_EXT_NOME_FILE_FATTURA = "NomeFileFattura";
    public final static String SDI_BUSTA_EXT_NOME_FILE_METADATI = "NomeFileMetadati";
    public final static String SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA = "NomeFileInternoNotifica";
    public final static String SDI_BUSTA_EXT_HASH_IN_NOTIFICA = "HashNotifica";
    public final static String SDI_BUSTA_EXT_MESSAGE_ID = "MessageId";
    public final static String SDI_BUSTA_EXT_MESSAGE_ID_COMMITTENTE = "MessageIdCommittente";
    public final static String SDI_BUSTA_EXT_VERSIONE_FATTURA_PA = "FormatoFatturaPA";
    public final static String SDI_BUSTA_EXT_POSIZIONE_FATTURA_PA = "PosizioneFatturaPA";
    public final static String SDI_BUSTA_EXT_TENTATIVI_INVIO = "TentativiInvio";
    public final static String SDI_BUSTA_EXT_NOTE = "Note";
    public final static String SDI_BUSTA_EXT_DESCRIZIONE = "Descrizione";
    public final static String SDI_BUSTA_EXT_ERRORI = "Errori";
    public final static String SDI_BUSTA_EXT_ESITO = "Esito";
    public final static String SDI_BUSTA_EXT_ESITO_NOTIFICA = "EsitoNotifica";
    public final static String SDI_BUSTA_EXT_SCARTO = "Scarto";
    public final static String SDI_BUSTA_EXT_DATA_ORA_RICEZIONE = "DataOraRicezione";
    public final static String SDI_BUSTA_EXT_DATA_MESSA_A_DISPOSIZIONE = "DataMessaADisposizione";
    public final static String SDI_BUSTA_EXT_DATA_ORA_CONSEGNA = "DataOraConsegna";
    public final static String SDI_BUSTA_EXT_ERRORE = "Errore";
    public final static String SDI_BUSTA_EXT_ESITO_COMMITTENTE = "EsitoCommittente";
    public final static String SDI_BUSTA_EXT_DESTINATARIO_CODICE = "Destinatario-Codice";
    public final static String SDI_BUSTA_EXT_DESTINATARIO_DESCRIZIONE = "Destinatario-Descrizione";
    public final static String SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_IDENTIFICATIVO_SDI = "RiferimentoArchivio-IdentificativoSdI";
    public final static String SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_NOME_FILE = "RiferimentoArchivio-NomeFile";
    public final static String SDI_BUSTA_EXT_HashFileOriginale = "HashFileOriginale";
    
    public final static String SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_PREFIX_ = "RiferimentoFattura-";
    public final static String SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_ANNO = SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_PREFIX_+"Anno";
    public final static String SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_NUMERO = SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_PREFIX_+"Numero";
    public final static String SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_POSIZIONE = SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_PREFIX_+"Posizione";
        
    public final static String SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_ = "DatiTrasmissione-";
    public final static String SDI_BUSTA_EXT_TRASMITTENTE_ID_PAESE = SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_+"IdTrasmittente-IdPaese";
    public final static String SDI_BUSTA_EXT_TRASMITTENTE_ID_CODICE = SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_+"IdTrasmittente-IdCodice";
    public final static String SDI_BUSTA_EXT_TRASMISSIONE_PROGRESSIVO_INVIO = SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_+"ProgressivoInvio";
    public final static String SDI_BUSTA_EXT_TRASMISSIONE_PEC_DESTINATARIO = SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_+"PECDestinatario";
    
    public final static String SDI_BUSTA_EXT_CODICE_DESTINATARIO = "CodiceDestinatario";
     
    public final static String SDI_BUSTA_EXT_SOGGETTO_EMITTENTE = "SoggettoEmittente";
    
    public final static String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_ = "CedentePrestatore-";
    public final static String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_DENOMINAZIONE = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"Denominazione";
    public final static String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_NOME = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"Nome";
    public final static String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_COGNOME = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"Cognome";
    public final static String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_ID_PAESE = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"IdPaese";
    public final static String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_ID_CODICE = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"IdCodice";
    public final static String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_CODICE_FISCALE = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"CodiceFiscale";
    
    public final static String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_ = "CessionarioCommittente-";
    public final static String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_DENOMINAZIONE = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"Denominazione";
    public final static String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_NOME = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"Nome";
    public final static String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_COGNOME = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"Cognome";
    public final static String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_ID_PAESE = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"IdPaese";
    public final static String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_ID_CODICE = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"IdCodice";
    public final static String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_CODICE_FISCALE = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"CodiceFiscale";
    
    public final static String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_ = "TerzoIntermediarioOSoggettoEmittente-";
    public final static String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_DENOMINAZIONE = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"Denominazione";
    public final static String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_NOME = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"Nome";
    public final static String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_COGNOME = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"Cognome";
    public final static String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_ID_PAESE = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"IdPaese";
    public final static String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_ID_CODICE = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"IdCodice";
    public final static String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_CODICE_FISCALE = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"CodiceFiscale";
    
    public final static String SDI_BUSTA_APPLICATIVO_MITTENTE_FATTURA = "ApplicativoMittenteFattura";
    
    public final static String SDI_HEADER_ID_CORRELAZIONE = "GovWay-SDI-IdCorrelazione";
    
    public final static String SDI_HEADER_FILE_METADATI = "GovWay-SDI-FileMetadati";
    
}





