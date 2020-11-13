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

package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.util.Vector;

import org.openspcoop2.protocol.basic.archive.EsitoUtils;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * ArchiviCostanti
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiviCostanti {

	/* OBJECT NAME */
	
	public final static String OBJECT_NAME_ARCHIVI_IMPORT = "import";
	public final static ForwardParams TIPO_OPERAZIONE_IMPORT = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_ARCHIVI_EXPORT = "export";
	public final static ForwardParams TIPO_OPERAZIONE_EXPORT = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_ARCHIVI_DIAGNOSTICA = "diagnostica";
	public final static ForwardParams TIPO_OPERAZIONE_DIAGNOSTICA = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_ARCHIVI_DIAGNOSTICA_TESTO = "diagnosticaTesto";
	public final static ForwardParams TIPO_OPERAZIONE_DIAGNOSTICA_TESTO = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_ARCHIVI_TRACCIAMENTO = "tracciamento";
	public final static ForwardParams TIPO_OPERAZIONE_TRACCIAMENTO = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_ARCHIVI_TRACCIAMENTO_TESTO = "tracciamentoTesto";
	public final static ForwardParams TIPO_OPERAZIONE_TRACCIAMENTO_TESTO = ForwardParams.OTHER("");
	
	
	/* SERVLET NAME EXPORT */
	
	public final static String SERVLET_NAME_DOCUMENTI_EXPORT = "downloadDocumento";
	public final static String SERVLET_NAME_TRACCE_EXPORT = "exporterTracce";
	public final static String SERVLET_NAME_MESSAGGI_DIAGNOSTICI_EXPORT = "exporterDiagnostica";
	public final static String SERVLET_NAME_PACKAGE_EXPORT = "exporterArchivi";
	public final static String SERVLET_NAME_RESOCONTO_EXPORT = "downloadResoconto";
	
	
	
	/* SERVLET NAME */
	
	public final static String SERVLET_NAME_ARCHIVI_IMPORT = OBJECT_NAME_ARCHIVI_IMPORT+".do";
	public final static Vector<String> SERVLET_ARCHIVI_IMPORT = new Vector<String>();
	static{
		SERVLET_ARCHIVI_IMPORT.add(SERVLET_NAME_ARCHIVI_IMPORT);
	}
	
	public final static String SERVLET_NAME_ARCHIVI_EXPORT = OBJECT_NAME_ARCHIVI_EXPORT+".do";
	public final static Vector<String> SERVLET_ARCHIVI_EXPORT = new Vector<String>();
	static{
		SERVLET_ARCHIVI_EXPORT.add(SERVLET_NAME_ARCHIVI_EXPORT);
		SERVLET_ARCHIVI_EXPORT.add(SERVLET_NAME_PACKAGE_EXPORT);
	}
	
	public final static String SERVLET_NAME_ARCHIVI_DIAGNOSTICA = OBJECT_NAME_ARCHIVI_DIAGNOSTICA+".do";
	public final static String SERVLET_NAME_ARCHIVI_DIAGNOSTICA_LIST = OBJECT_NAME_ARCHIVI_DIAGNOSTICA+"List.do";
	public final static String SERVLET_NAME_ARCHIVI_DIAGNOSTICA_TESTO = OBJECT_NAME_ARCHIVI_DIAGNOSTICA_TESTO+".do";
	public final static Vector<String> SERVLET_ARCHIVI_DIAGNOSTICA = new Vector<String>();
	static{
		SERVLET_ARCHIVI_DIAGNOSTICA.add(SERVLET_NAME_ARCHIVI_DIAGNOSTICA);
		SERVLET_ARCHIVI_DIAGNOSTICA.add(SERVLET_NAME_ARCHIVI_DIAGNOSTICA_LIST);
		SERVLET_ARCHIVI_DIAGNOSTICA.add(SERVLET_NAME_ARCHIVI_DIAGNOSTICA_TESTO);
	}
	
	public final static String SERVLET_NAME_ARCHIVI_TRACCIAMENTO = OBJECT_NAME_ARCHIVI_TRACCIAMENTO+".do";
	public final static String SERVLET_NAME_ARCHIVI_TRACCIAMENTO_LIST = OBJECT_NAME_ARCHIVI_TRACCIAMENTO+"List.do";
	public final static String SERVLET_NAME_ARCHIVI_TRACCIAMENTO_TESTO = OBJECT_NAME_ARCHIVI_TRACCIAMENTO_TESTO+".do";
	public final static Vector<String> SERVLET_ARCHIVI_TRACCIAMENTO = new Vector<String>();
	static{
		SERVLET_ARCHIVI_TRACCIAMENTO.add(SERVLET_NAME_ARCHIVI_TRACCIAMENTO);
		SERVLET_ARCHIVI_TRACCIAMENTO.add(SERVLET_NAME_ARCHIVI_TRACCIAMENTO_LIST);
		SERVLET_ARCHIVI_TRACCIAMENTO.add(SERVLET_NAME_ARCHIVI_TRACCIAMENTO_TESTO);
	}
	
		
		
	/* LABEL GENERALI */
	
	public final static String LABEL_ARCHIVI_IMPORT = "Importa";
	public final static String LABEL_ARCHIVI_EXPORT = "Esporta";
	public final static String LABEL_ARCHIVI_ELIMINA = "Elimina";
	public final static String LABEL_ARCHIVI_AVANTI = "Avanti";
	public final static String LABEL_DOWNLOAD = "Download";
	
	public final static String LABEL_NOME_SOGGETTO = "Nome";
	
	public final static String LABEL_ARCHIVIO = "Archivio";
	public final static String LABEL_IMPORT_ERRORE = "<BR><BR><B>Errore</B>: ";
	public final static String LABEL_IMPORT_ERROR_HEADER = "Rilevato un errore durante l'import dell'archivio: <BR>";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_MANCANTI = "Attenzione: L'archivio non possiede tutte le informazioni necessarie per completare l'operazione di import";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_IDENTIFICAZIONE_MANCANTI = "Informazioni d'identificazione mancanti";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI = "Informazioni di protocollo non presenti (es. profilo di collaborazione, correlazione per gli asincroni ...)";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_WSDL_MODE="Automatica dall'accordo (WSDL,SpecificaConversazione,...)";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_USER_INPUT = "Configurazione manuale dei servizi e delle operazioni presenti nel WSDL";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO = "Servizio (PortType) implementato non esistente";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO_KEY = "@ID_PORT_TYPE@";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO = "Il servizio implementato ("+
			LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO_KEY+") non risulta registrato nell'accordo di servizio parte comune (o composto) riferito. Selezionare uno dei servizi esistenti";
	
	
	/* PARAMETRI */
	
	public final static String PARAMETRI_ARCHIVI = "params";
	
	public final static String PARAMETRO_ARCHIVI_ID = "id";
	public final static String PARAMETRO_ARCHIVI_TIPO_ACCORDO = "what";
	
	public final static String PARAMETRO_ARCHIVI_PROTOCOLLO = "protocollo";

	public final static String PARAMETRO_ARCHIVI_ALLEGATO_ID_ALLEGATO = "idAllegato";
	public final static String PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO = "idAccordo";
	public final static String PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO = "tipoDocumento";
	public final static String PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO = "tipoDocumentoDaScaricare";
	
	public final static String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_TIPO_CONNETTORE_REGISTRO = "tipoConn";
	public final static String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_ALIAS_CONNETTORE = "aliasConn";
	public final static String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_ID_CONNETTORE = "idConn";
	public final static String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_NOME_CONNETTORE = "nomeConn";
			
	public final static String PARAMETRO_ARCHIVI_IMPORTER_MODALITA = "modalita";
	public final static String PARAMETRO_ARCHIVI_PACKAGE_FILE = "theFile";
	public final static String PARAMETRO_ARCHIVI_PACKAGE_FILE_PATH = "filePath";
	public final static String PARAMETRO_ARCHIVI_PACKAGE_FILE_SIMPLE_NAME = "fileName";
	public final static String PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI = "validazioneDocumenti";
	public final static String PARAMETRO_ARCHIVI_UPDATE_ENABLED = "updateEnabled";
	public final static String PARAMETRO_ARCHIVI_IMPORT_DELETE_POLICY_CONFIG_ENABLED = "policyConfigEnabled";
	public final static String PARAMETRO_ARCHIVI_IMPORT_CONFIG_ENABLED = "configEnabled";
	public final static String PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO = "tipologiaArchivio";
	
	public final static String PARAMETRO_ARCHIVI_TIPO = "tipoArchivio";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID ="importInformationMissingObjectId";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID_DESCRIPTION ="importInformationMissingObjectIdDescription";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_COLLECTION_FILE_PATH ="importInformationMissingCollectionFilePath";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_FILE_PATH ="importInformationMissingObjectFilePath";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_CLASS ="importInformationMissingClassObject";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT = "soggetto";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT = "versione"; // lasciare versione per avere il widget grafico spinbanner
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT = "modalitaAcquisizione";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_IS_DEFINED ="servizioInputIsDefined";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE ="servizioInput";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_IS_DEFINED = "_azioneInputIsDefined";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION = "_azioneInput";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO = "_profiloCollaborazioneInput";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO = "_servizioAzioneCorrelataInput";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_CORRELATA = "_azioneCorrelataInput";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_PORT_TYPE_IMPLEMENTED_INPUT = "portTypeImplemented";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT = "accordoServizioParteComune";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT = "accordoCooperazione";
	public final static String PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_HIDDEN = "RequisitoProprietaInputHidden_";
	public final static String PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_NAME_HIDDEN = "RequisitoProprietaInputNameHidden_";
	public final static String PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_STEP_INCREMENT_HIDDEN = "RequisitoProprietaInputStepIncrementHidden_";
	public final static String PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_VALUE = "RequisitoProprietaInputValue_";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN = "ProprietaInputHidden_";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_VALUE = "ProprietaInputValue_";
	public final static String PARAMETRO_ARCHIVI_EXPORT_TIPO = "tipoExport";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG = "cascadeConfig";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE = "cascade";
	public final static String PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP = "tipoDump";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD = "cascadePdd";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_GRUPPI = "cascadeGruppi";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_RUOLI = "cascadeRuoli";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_SCOPE = "cascadeScope";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI = "cascadeSoggetti";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI = "cascadeServiziApplicativi";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE = "cascadePorteDelegate";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE = "cascadePorteApplicative";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE = "cascadeAc";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE = "cascadeAspc";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO = "cascadeAsc";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA = "cascadeAsps";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI = "cascadeFruizioni";
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	
	public final static String LABEL_PARAMETRO_ARCHIVI_PACKAGE_FILE = "File";
	public final static String LABEL_PARAMETRO_ARCHIVI_PACKAGE_FILE_SIMPLE_NAME = "File Caricato";
	public final static String LABEL_PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI_LEFT = "Validazione Documenti";
	public final static String LABEL_PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI_RIGHT = "Le interfacce delle API (Wsdl, OpenAPI 3) vengono validate";
	public final static String LABEL_PARAMETRO_ARCHIVI_UPDATE_ENABLED_LEFT = "Aggiornamento";
	public final static String LABEL_PARAMETRO_ARCHIVI_UPDATE_ENABLED_RIGHT = "Gli elementi già esistenti verranno aggiornati";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_POLICY_CONFIG_LEFT = EsitoUtils.LABEL_IMPORT_POLICY;
	public final static String LABEL_PARAMETRO_ARCHIVI_DELETE_POLICY_CONFIG_LEFT = EsitoUtils.LABEL_DELETE_POLICY;
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_POLICY_CONFIG_RIGHT = "Eventuali policy globali (Token, Rate Limiting) presenti nell'archivio verranno importate";
	public final static String LABEL_PARAMETRO_ARCHIVI_DELETE_POLICY_CONFIG_RIGHT = "Eventuali policy globali (Token, Rate Limiting) presenti nell'archivio verranno eliminate";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_CONFIG_LEFT = EsitoUtils.LABEL_IMPORT_CONFIGURAZIONE;
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_CONFIG_RIGHT = "Una eventuale configurazione presente nell'archivio verrà importata";
	public final static String LABEL_PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO = "Tipologia archivio";
	public final static String LABEL_PARAMETRO_ARCHIVI_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID_DESCRIPTION ="Descrizione";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT = "Soggetto";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT = "Versione";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT = "Modalità Acquisizione";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE_KEY = "@PT_NAME@";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE = 
			"Informazioni per il port type '"+LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE_KEY+"'";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE ="Nome servizio";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE_KEY = "@OP_NAME@";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE = 
			"Operation '"+LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE_KEY+"'";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION ="Nome azione";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO = "Profilo di collaborazione";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO = 
			"Nome del servizio di richiesta correlato";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_CORRELATA = 
			"Nome dell'azione di richiesta correlata";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_PORT_TYPE_IMPLEMENTED_INPUT = "Servizio";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT = "AccordoServizioParteComune/ServizioComposto";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT = "AccordoCooperazione";
	
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG_LEFT = EsitoUtils.LABEL_IMPORT_POLICY;
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG_RIGHT = "Vengono incluse le policy globali (Token, Rate Limiting)";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_LEFT = "Elementi di Registro";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_RIGHT = "Vengono inclusi gli elementi riferiti";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SEZIONE = "Configurazione 'Cascade'";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP = "Modalità";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD = "Porte di Dominio";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_RUOLI = "Ruoli";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SCOPE = "Scope";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI = "Soggetti";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI = "Servizi Applicativi";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE = "Porte Delegate";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE = "Porte Applicative";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE = "Accordi Cooperazione";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE = "APIs";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO = "Accordi Servizio Composto";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA = "Servizi";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI = "Fruizioni";
		
	
	
	/* DEFAULT VALUE PARAMETRI */
	
	public final static String PARAMETRO_ARCHIVI_IMPORTER_MODALITA_IMPORT = "import";
	public final static String PARAMETRO_ARCHIVI_IMPORTER_MODALITA_ELIMINA = "elimina";
	
	public final static String PARAMETRO_DOWNLOAD_RESOCONTO_VALORE = "DownloadResocontoValore";
	
	public final static String PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED = "-";
	
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED = "-";
	
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED = "-";
	
	public final static String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_PARTE_COMUNE = "accordiAPC";
	public final static String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_PARTE_SPECIFICA = "accordiAPS";
	public final static String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_SERVIZIO_COMPOSTO = "accordiASC";
	public final static String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_COOPERAZIONE = "accordiCooperazione";
	
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE = "aspc";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA = "asps";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_COOPERAZIONE = "ac";

	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_DOCUMENTO = "documento";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_DEFINITORIO = "wsdlDefinitorio";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_CONCETTUALE = "wsdlConcettuale";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_EROGATORE = "wsdlLogicoErogatore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_FRUITORE = "wsdlLogicoFruitore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_EROGATORE = "wsdlImplementativoErogatore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_FRUITORE = "wsdlImplementativoFruitore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_CONCETTUALE = "specificaConversazioneConcettuale";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_EROGATORE = "specificaConversazioneLogicoErogatore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_FRUITORE = "specificaConversazioneLogicoFruitore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_XSD_SCHEMA_COLLECTION = "xsdSchemaCollection";
		
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_TIPO_SOGGETTO_FRUITORE = "wsdlImplementativoTipoSoggettoFruitore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_NOME_SOGGETTO_FRUITORE = "wsdlImplementativoNomeSoggettoFruitore";
	
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_PROTOCOL_PROPERTY = "protocolProperty";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PROTOCOL_PROPERTY_BINARY = "propertyBinaria";
	
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_PORTA_APPLICATIVA = "pa";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_XACML_POLICY = "xacmlPolicy";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_XACML_POLICY_FILENAME = "xacmlPolicy.xml";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_ID =  CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE;
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RES_ID =  CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RISPOSTA;
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE = "trasfReqConvTemplate"; 
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE_FILENAME = "template";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE = "trasfReqSoapEnvTemp"; 
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE_FILENAME = "template";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE = "trasfResConvTemplate"; 
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE_FILENAME = "template";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE = "trasfResSoapEnvTemp"; 
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE_FILENAME = "template";
	
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_PORTA_DELEGATA = "pd";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_XACML_POLICY = "xacmlPolicy";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_XACML_POLICY_FILENAME = "xacmlPolicy.xml";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_ID =  CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE;
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RES_ID =  CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RISPOSTA;
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE = "trasfReqConvTemplate"; 
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE_FILENAME = "template";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE = "trasfReqSoapEnvTemp"; 
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE_FILENAME = "template";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE = "trasfResConvTemplate"; 
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE_FILENAME = "template";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE = "trasfResSoapEnvTemp"; 
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE_FILENAME = "template";
		
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_SOGGETTO = "sogg";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_SERVIZIO_APPLICATIVO = "sa";
	
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_CONNETTORE_CERTIFICATO_SERVER = "certServer";
	
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_CERTIFICATO_SSL = "sslCert";
		
	public final static String[] PARAMETRO_VALORI_ARCHIVI_EXPORT_TIPO_DUMP = { ArchiveType.ALL.toString(), ArchiveType.ALL_WITHOUT_CONFIGURAZIONE.toString(), ArchiveType.CONFIGURAZIONE.toString() };
	public final static String[] PARAMETRO_LABEL_ARCHIVI_EXPORT_TIPO_DUMP = { "Esportazione completa", ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO , ConfigurazioneCostanti.LABEL_CONFIGURAZIONE };
			//"Dati applicativi", "Parametri di configurazione" };
	
	public final static String DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA = "interfaccia";
	public final static String DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA_STANDARD = "standard";
	public final static String DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA_AVANZATA = "avanzata";
	public final static String DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE = "connettore";
	public final static String DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE_HTTP = "http";
	public final static String DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE_QUALSIASI = "qualsiasi";
	

}
