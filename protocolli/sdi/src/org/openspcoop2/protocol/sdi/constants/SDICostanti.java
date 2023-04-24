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



package org.openspcoop2.protocol.sdi.constants;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;

/**
 * Classe dove sono fornite le stringhe costanti, definite dalla specifica del protocollo SdI, 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SDICostanti {
   
	public static final String OPENSPCOOP2_LOCAL_HOME = "GOVWAY_HOME";
	
	public static final String SDI_PROTOCOL_NAME = "sdi";
	
    public static final String SDI_PROPERTIES_LOCAL_PATH = "sdi_local.properties";
    public static final String SDI_PROPERTIES = "SDI_PROPERTIES";
	
    
    public static final String SDI_PROTOCOL_NAMESPACE="www.openspcoop2.org/protocols/sdi";
    
    public static final boolean SDI_FATTURAZIONE_ATTIVA = true;
    public static final boolean SDI_FATTURAZIONE_PASSIVA = false;
    
    public static final String SDI_TIPO_FATTURA_XML = "XML";
    public static final String SDI_TIPO_FATTURA_ZIP = "ZIP";
    public static final String SDI_TIPO_FATTURA_P7M = "P7M";
    
    public static final String SDI_FATTURA_ESTENSIONE_XML = "xml";
    public static final String SDI_FATTURA_ESTENSIONE_P7M = "xml.p7m";
    public static final String SDI_FATTURA_ESTENSIONE_ZIP = "zip";
    
    public static final String SDI_ATTRIBUTE_VERSION_FATTURA_PA_10 = "1.0";
    public static final String SDI_ATTRIBUTE_VERSION_FATTURA_PA_11 = "1.1";
    public static final String SDI_ATTRIBUTE_VERSION_FATTURA_PA_12 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType.FPA12.name();
    public static final String SDI_ATTRIBUTE_VERSION_FATTURA_PR_12 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType.FPR12.name();
    public static final String SDI_ATTRIBUTE_VERSION_FATTURA_SEMPLIFICATA_10 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.FormatoTrasmissioneType.FSM10.name();
    
    public static final String SDI_VERSIONE_FATTURA_PA_10 = it.gov.fatturapa.sdi.fatturapa.v1_0.constants.FormatoTrasmissioneType.SDI10.name();
    public static final String SDI_VERSIONE_FATTURA_PA_11 = it.gov.fatturapa.sdi.fatturapa.v1_1.constants.FormatoTrasmissioneType.SDI11.name();
    public static final String SDI_VERSIONE_FATTURA_PA_12 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType.FPA12.name();
    public static final String SDI_VERSIONE_FATTURA_PR_12 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType.FPR12.name();
    public static final String SDI_VERSIONE_FATTURA_SEMPLIFICATA_10 = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.FormatoTrasmissioneType.FSM10.name();
    public static final List<String> SDI_VERSIONI_FATTURA = new ArrayList<>();
    static{
    	SDI_VERSIONI_FATTURA.add(SDI_VERSIONE_FATTURA_PA_12);
    	SDI_VERSIONI_FATTURA.add(SDI_VERSIONE_FATTURA_PR_12);
    	SDI_VERSIONI_FATTURA.add(SDI_VERSIONE_FATTURA_PA_11);
    	SDI_VERSIONI_FATTURA.add(SDI_VERSIONE_FATTURA_PA_10);
    	SDI_VERSIONI_FATTURA.add(SDI_VERSIONE_FATTURA_SEMPLIFICATA_10);
    }
    
    public static final MapKey<String> SDI_MESSAGE_CONTEXT_FATTURA = Map.newMapKey("FatturaPA");
    public static final MapKey<String> SDI_MESSAGE_CONTEXT_FATTURA_METADATI = Map.newMapKey("FatturaPAMetadati");
    public static final MapKey<String> SDI_MESSAGE_CONTEXT_FATTURA_METADATI_BYTES = Map.newMapKey("FatturaPAMetadatiBytes");
    public static final MapKey<String> SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI = Map.newMapKey("MessaggioServizioSdI");
    public static final MapKey<String> SDI_MESSAGE_CONTEXT_AT_ARCHIVIO_ZIP = Map.newMapKey("ATArchivioZIP");
    public static final MapKey<String> SDI_MESSAGE_CONTEXT_AT_ARCHIVIO_XML = Map.newMapKey("ATArchivioXML");
    
    
    public static final String SDI_BUSTA_EXT_FORMATO_ARCHIVIO_INVIO_FATTURA = "FormatoArchivioInvioFattura";
    public static final String SDI_BUSTA_EXT_FORMATO_ARCHIVIO_BASE64 = "FormatoArchivioBase64";
    public static final String SDI_BUSTA_EXT_IDENTIFICATIVO_SDI = "IdentificativoSdI";
    public static final String SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA = "IdentificativoSdIFattura";
    public static final String SDI_BUSTA_EXT_NOME_FILE = "NomeFile";
    public static final String SDI_BUSTA_EXT_NOME_FILE_FATTURA = "NomeFileFattura";
    public static final String SDI_BUSTA_EXT_NOME_FILE_METADATI = "NomeFileMetadati";
    public static final String SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA = "NomeFileInternoNotifica";
    public static final String SDI_BUSTA_EXT_HASH_IN_NOTIFICA = "HashNotifica";
    public static final String SDI_BUSTA_EXT_MESSAGE_ID = "MessageId";
    public static final String SDI_BUSTA_EXT_MESSAGE_ID_COMMITTENTE = "MessageIdCommittente";
    public static final String SDI_BUSTA_EXT_VERSIONE_FATTURA_PA = "FormatoFatturaPA";
    public static final String SDI_BUSTA_EXT_POSIZIONE_FATTURA_PA = "PosizioneFatturaPA";
    public static final String SDI_BUSTA_EXT_TENTATIVI_INVIO = "TentativiInvio";
    public static final String SDI_BUSTA_EXT_NOTE = "Note";
    public static final String SDI_BUSTA_EXT_DESCRIZIONE = "Descrizione";
    public static final String SDI_BUSTA_EXT_ERRORI = "Errori";
    public static final String SDI_BUSTA_EXT_ESITO = "Esito";
    public static final String SDI_BUSTA_EXT_ESITO_NOTIFICA = "EsitoNotifica";
    public static final String SDI_BUSTA_EXT_SCARTO = "Scarto";
    public static final String SDI_BUSTA_EXT_DATA_ORA_RICEZIONE = "DataOraRicezione";
    public static final String SDI_BUSTA_EXT_DATA_MESSA_A_DISPOSIZIONE = "DataMessaADisposizione";
    public static final String SDI_BUSTA_EXT_DATA_ORA_CONSEGNA = "DataOraConsegna";
    public static final String SDI_BUSTA_EXT_ERRORE = "Errore";
    public static final String SDI_BUSTA_EXT_ESITO_COMMITTENTE = "EsitoCommittente";
    public static final String SDI_BUSTA_EXT_DESTINATARIO_CODICE = "Destinatario-Codice";
    public static final String SDI_BUSTA_EXT_DESTINATARIO_DESCRIZIONE = "Destinatario-Descrizione";
    public static final String SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_IDENTIFICATIVO_SDI = "RiferimentoArchivio-IdentificativoSdI";
    public static final String SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_NOME_FILE = "RiferimentoArchivio-NomeFile";
    public static final String SDI_BUSTA_EXT_HashFileOriginale = "HashFileOriginale";
    
    public static final String SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_PREFIX_ = "RiferimentoFattura-";
    public static final String SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_ANNO = SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_PREFIX_+"Anno";
    public static final String SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_NUMERO = SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_PREFIX_+"Numero";
    public static final String SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_POSIZIONE = SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_PREFIX_+"Posizione";
        
    public static final String SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_ = "DatiTrasmissione-";
    public static final String SDI_BUSTA_EXT_TRASMITTENTE_ID_PAESE = SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_+"IdTrasmittente-IdPaese";
    public static final String SDI_BUSTA_EXT_TRASMITTENTE_ID_CODICE = SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_+"IdTrasmittente-IdCodice";
    public static final String SDI_BUSTA_EXT_TRASMISSIONE_PROGRESSIVO_INVIO = SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_+"ProgressivoInvio";
    public static final String SDI_BUSTA_EXT_TRASMISSIONE_PEC_DESTINATARIO = SDI_BUSTA_EXT_TRASMITTENTE_PREFIX_+"PECDestinatario";
    
    public static final String SDI_BUSTA_EXT_CODICE_DESTINATARIO = "CodiceDestinatario";
     
    public static final String SDI_BUSTA_EXT_SOGGETTO_EMITTENTE = "SoggettoEmittente";
    
    public static final String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_ = "CedentePrestatore-";
    public static final String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_DENOMINAZIONE = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"Denominazione";
    public static final String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_NOME = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"Nome";
    public static final String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_COGNOME = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"Cognome";
    public static final String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_ID_PAESE = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"IdPaese";
    public static final String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_ID_CODICE = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"IdCodice";
    public static final String SDI_BUSTA_EXT_CEDENTE_PRESTATORE_CODICE_FISCALE = SDI_BUSTA_EXT_CEDENTE_PRESTATORE_PREFIX_+"CodiceFiscale";
    
    public static final String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_ = "CessionarioCommittente-";
    public static final String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_DENOMINAZIONE = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"Denominazione";
    public static final String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_NOME = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"Nome";
    public static final String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_COGNOME = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"Cognome";
    public static final String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_ID_PAESE = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"IdPaese";
    public static final String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_ID_CODICE = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"IdCodice";
    public static final String SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_CODICE_FISCALE = SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_PREFIX_+"CodiceFiscale";
    
    public static final String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_ = "TerzoIntermediarioOSoggettoEmittente-";
    public static final String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_DENOMINAZIONE = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"Denominazione";
    public static final String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_NOME = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"Nome";
    public static final String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_COGNOME = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"Cognome";
    public static final String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_ID_PAESE = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"IdPaese";
    public static final String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_ID_CODICE = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"IdCodice";
    public static final String SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_CODICE_FISCALE = SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_PREFIX_+"CodiceFiscale";
    
    public static final String SDI_BUSTA_APPLICATIVO_MITTENTE_FATTURA = "ApplicativoMittenteFattura";
    
    public static final String SDI_HEADER_ID_CORRELAZIONE = "GovWay-SDI-IdCorrelazione";
    
    public static final String SDI_HEADER_FILE_METADATI = "GovWay-SDI-FileMetadati";
    
}





