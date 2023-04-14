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

package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.basic.archive.EsitoUtils;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * ArchiviCostanti
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiviCostanti {
	
	private ArchiviCostanti() {}

	/* OBJECT NAME */
	
	public static final String OBJECT_NAME_ARCHIVI_IMPORT = "import";
	public static final ForwardParams TIPO_OPERAZIONE_IMPORT = ForwardParams.OTHER("");
	
	public static final String OBJECT_NAME_ARCHIVI_EXPORT = "export";
	public static final ForwardParams TIPO_OPERAZIONE_EXPORT = ForwardParams.OTHER("");
	
	public static final String OBJECT_NAME_ARCHIVI_DIAGNOSTICA = "diagnostica";
	public static final ForwardParams TIPO_OPERAZIONE_DIAGNOSTICA = ForwardParams.OTHER("");
	
	public static final String OBJECT_NAME_ARCHIVI_DIAGNOSTICA_TESTO = "diagnosticaTesto";
	public static final ForwardParams TIPO_OPERAZIONE_DIAGNOSTICA_TESTO = ForwardParams.OTHER("");
	
	public static final String OBJECT_NAME_ARCHIVI_TRACCIAMENTO = "tracciamento";
	public static final ForwardParams TIPO_OPERAZIONE_TRACCIAMENTO = ForwardParams.OTHER("");
	
	public static final String OBJECT_NAME_ARCHIVI_TRACCIAMENTO_TESTO = "tracciamentoTesto";
	public static final ForwardParams TIPO_OPERAZIONE_TRACCIAMENTO_TESTO = ForwardParams.OTHER("");
	
	
	/* SERVLET NAME EXPORT */
	
	public static final String SERVLET_NAME_DOCUMENTI_EXPORT = "downloadDocumento";
	public static final String SERVLET_NAME_TRACCE_EXPORT = "exporterTracce";
	public static final String SERVLET_NAME_MESSAGGI_DIAGNOSTICI_EXPORT = "exporterDiagnostica";
	public static final String SERVLET_NAME_PACKAGE_EXPORT = "exporterArchivi";
	public static final String SERVLET_NAME_RESOCONTO_EXPORT = "downloadResoconto";
	
	
	
	/* SERVLET NAME */
	
	public static final String SERVLET_NAME_ARCHIVI_IMPORT = OBJECT_NAME_ARCHIVI_IMPORT+".do";
	private static final List<String> SERVLET_ARCHIVI_IMPORT = new ArrayList<>();
	public static List<String> getServletArchiviImport() {
		return SERVLET_ARCHIVI_IMPORT;
	}
	static{
		SERVLET_ARCHIVI_IMPORT.add(SERVLET_NAME_ARCHIVI_IMPORT);
	}
	
	public static final String SERVLET_NAME_ARCHIVI_EXPORT = OBJECT_NAME_ARCHIVI_EXPORT+".do";
	private static final List<String> SERVLET_ARCHIVI_EXPORT = new ArrayList<>();
	public static List<String> getServletArchiviExport() {
		return SERVLET_ARCHIVI_EXPORT;
	}
	static{
		SERVLET_ARCHIVI_EXPORT.add(SERVLET_NAME_ARCHIVI_EXPORT);
		SERVLET_ARCHIVI_EXPORT.add(SERVLET_NAME_PACKAGE_EXPORT);
	}
	
	public static final String SERVLET_NAME_ARCHIVI_DIAGNOSTICA = OBJECT_NAME_ARCHIVI_DIAGNOSTICA+".do";
	public static final String SERVLET_NAME_ARCHIVI_DIAGNOSTICA_LIST = OBJECT_NAME_ARCHIVI_DIAGNOSTICA+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final String SERVLET_NAME_ARCHIVI_DIAGNOSTICA_TESTO = OBJECT_NAME_ARCHIVI_DIAGNOSTICA_TESTO+".do";
	private static final List<String> SERVLET_ARCHIVI_DIAGNOSTICA = new ArrayList<>();
	public static List<String> getServletArchiviDiagnostica() {
		return SERVLET_ARCHIVI_DIAGNOSTICA;
	}
	static{
		SERVLET_ARCHIVI_DIAGNOSTICA.add(SERVLET_NAME_ARCHIVI_DIAGNOSTICA);
		SERVLET_ARCHIVI_DIAGNOSTICA.add(SERVLET_NAME_ARCHIVI_DIAGNOSTICA_LIST);
		SERVLET_ARCHIVI_DIAGNOSTICA.add(SERVLET_NAME_ARCHIVI_DIAGNOSTICA_TESTO);
	}
	
	public static final String SERVLET_NAME_ARCHIVI_TRACCIAMENTO = OBJECT_NAME_ARCHIVI_TRACCIAMENTO+".do";
	public static final String SERVLET_NAME_ARCHIVI_TRACCIAMENTO_LIST = OBJECT_NAME_ARCHIVI_TRACCIAMENTO+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final String SERVLET_NAME_ARCHIVI_TRACCIAMENTO_TESTO = OBJECT_NAME_ARCHIVI_TRACCIAMENTO_TESTO+".do";
	private static final List<String> SERVLET_ARCHIVI_TRACCIAMENTO = new ArrayList<>();
	public static List<String> getServletArchiviTracciamento() {
		return SERVLET_ARCHIVI_TRACCIAMENTO;
	}
	static{
		SERVLET_ARCHIVI_TRACCIAMENTO.add(SERVLET_NAME_ARCHIVI_TRACCIAMENTO);
		SERVLET_ARCHIVI_TRACCIAMENTO.add(SERVLET_NAME_ARCHIVI_TRACCIAMENTO_LIST);
		SERVLET_ARCHIVI_TRACCIAMENTO.add(SERVLET_NAME_ARCHIVI_TRACCIAMENTO_TESTO);
	}
	
		
		
	/* LABEL GENERALI */
	
	public static final String LABEL_ARCHIVI_IMPORT = "Importa";
	public static final String LABEL_ARCHIVI_EXPORT = "Esporta";
	public static final String LABEL_ARCHIVI_ELIMINA = "Elimina";
	public static final String LABEL_ARCHIVI_AVANTI = "Avanti";
	public static final String LABEL_DOWNLOAD = "Download";
	
	public static final String LABEL_NOME_SOGGETTO = "Nome";
	
	public static final String LABEL_ARCHIVIO = "Archivio";
	public static final String LABEL_IMPORT_ERRORE = "<BR><BR><B>Errore</B>: ";
	public static final String LABEL_IMPORT_ERROR_HEADER = "Rilevato un errore durante l'import dell'archivio: <BR>";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_MANCANTI = "Attenzione: L'archivio non possiede tutte le informazioni necessarie per completare l'operazione di import";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_IDENTIFICAZIONE_MANCANTI = "Informazioni d'identificazione mancanti";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI = "Informazioni di protocollo non presenti (es. profilo di collaborazione, correlazione per gli asincroni ...)";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_WSDL_MODE="Automatica dall'accordo (WSDL,SpecificaConversazione,...)";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_USER_INPUT = "Configurazione manuale dei servizi e delle operazioni presenti nel WSDL";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO = "Servizio (PortType) implementato non esistente";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO_KEY = "@ID_PORT_TYPE@";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO = "Il servizio implementato ("+
			LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO_KEY+") non risulta registrato nell'accordo di servizio parte comune (o composto) riferito. Selezionare uno dei servizi esistenti";
	
	
	/* PARAMETRI */
	
	public static final String PARAMETRI_ARCHIVI = "params";
	
	public static final String PARAMETRO_ARCHIVI_ID = "id";
	public static final String PARAMETRO_ARCHIVI_TIPO_ACCORDO = "what";
	
	public static final String PARAMETRO_ARCHIVI_PROTOCOLLO = "protocollo";

	public static final String PARAMETRO_ARCHIVI_ALLEGATO_ID_ALLEGATO = "idAllegato";
	public static final String PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO = "idAccordo";
	public static final String PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO = "tipoDocumento";
	public static final String PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO = "tipoDocumentoDaScaricare";
	
	public static final String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_TIPO_CONNETTORE_REGISTRO = "tipoConn";
	public static final String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_ALIAS_CONNETTORE = "aliasConn";
	public static final String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_ID_CONNETTORE = "idConn";
	public static final String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_NOME_CONNETTORE = "nomeConn";
	
	public static final String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_TOKEN_NOME = "tokNome";
	public static final String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_TOKEN_TIPOLOGIA = "tokTlg";
	public static final String PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_TOKEN_TIPO = "tokTipo";
			
	public static final String PARAMETRO_ARCHIVI_JAR_NOME_ARCHVIO = "nomeJar";
	public static final String PARAMETRO_ARCHIVI_JAR_NOME_PLUGIN = "nomePlugin";
	
	public static final String PARAMETRO_ARCHIVI_IMPORTER_MODALITA = "modalita";
	public static final String PARAMETRO_ARCHIVI_PACKAGE_FILE = "theFile";
	public static final String PARAMETRO_ARCHIVI_PACKAGE_FILE_PATH = "filePath";
	public static final String PARAMETRO_ARCHIVI_PACKAGE_FILE_SIMPLE_NAME = "fileName";
	public static final String PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI = "validazioneDocumenti";
	public static final String PARAMETRO_ARCHIVI_UPDATE_ENABLED = "updateEnabled";
	public static final String PARAMETRO_ARCHIVI_IMPORT_DELETE_POLICY_CONFIG_ENABLED = "policyConfigEnabled";
	public static final String PARAMETRO_ARCHIVI_IMPORT_DELETE_PLUGIN_CONFIG_ENABLED = "pluginConfigEnabled";
	public static final String PARAMETRO_ARCHIVI_IMPORT_CONFIG_ENABLED = "configEnabled";
	public static final String PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO = "tipologiaArchivio";
	
	public static final String PARAMETRO_ARCHIVI_TIPO = "tipoArchivio";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID ="importInformationMissingObjectId";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID_DESCRIPTION ="importInformationMissingObjectIdDescription";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_COLLECTION_FILE_PATH ="importInformationMissingCollectionFilePath";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_FILE_PATH ="importInformationMissingObjectFilePath";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_CLASS ="importInformationMissingClassObject";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT = "soggetto";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT = "versione"; // lasciare versione per avere il widget grafico spinbanner
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT = "modalitaAcquisizione";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_IS_DEFINED ="servizioInputIsDefined";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE ="servizioInput";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_IS_DEFINED = "_azioneInputIsDefined";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION = "_azioneInput";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO = "_profiloCollaborazioneInput";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO = "_servizioAzioneCorrelataInput";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_CORRELATA = "_azioneCorrelataInput";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_PORT_TYPE_IMPLEMENTED_INPUT = "portTypeImplemented";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT = "accordoServizioParteComune";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT = "accordoCooperazione";
	public static final String PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_HIDDEN = "RequisitoProprietaInputHidden_";
	public static final String PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_NAME_HIDDEN = "RequisitoProprietaInputNameHidden_";
	public static final String PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_STEP_INCREMENT_HIDDEN = "RequisitoProprietaInputStepIncrementHidden_";
	public static final String PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_VALUE = "RequisitoProprietaInputValue_";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN = "ProprietaInputHidden_";
	public static final String PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_VALUE = "ProprietaInputValue_";
	public static final String PARAMETRO_ARCHIVI_EXPORT_TIPO = "tipoExport";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG = "cascadePolicyConfig";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_PLUGIN_CONFIG = "cascadePluginConfig";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE = "cascade";
	public static final String PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP = "tipoDump";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD = "cascadePdd";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_GRUPPI = "cascadeGruppi";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_RUOLI = "cascadeRuoli";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_SCOPE = "cascadeScope";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI = "cascadeSoggetti";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI = "cascadeServiziApplicativi";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE = "cascadePorteDelegate";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE = "cascadePorteApplicative";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE = "cascadeAc";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE = "cascadeAspc";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO = "cascadeAsc";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA = "cascadeAsps";
	public static final String PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI = "cascadeFruizioni";
	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	
	public static final String LABEL_PARAMETRO_ARCHIVI_PACKAGE_FILE = "File";
	public static final String LABEL_PARAMETRO_ARCHIVI_PACKAGE_FILE_SIMPLE_NAME = "File Caricato";
	public static final String LABEL_PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI_LEFT = "Validazione Documenti";
	public static final String LABEL_PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI_RIGHT = "Le interfacce delle API (Wsdl, OpenAPI 3) vengono validate";
	public static final String LABEL_PARAMETRO_ARCHIVI_UPDATE_ENABLED_LEFT = "Aggiornamento";
	public static final String LABEL_PARAMETRO_ARCHIVI_UPDATE_ENABLED_RIGHT = "Gli elementi già esistenti verranno aggiornati";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_POLICY_CONFIG_LEFT = EsitoUtils.LABEL_IMPORT_POLICY;
	public static final String LABEL_PARAMETRO_ARCHIVI_DELETE_POLICY_CONFIG_LEFT = EsitoUtils.LABEL_DELETE_POLICY;
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_POLICY_CONFIG_RIGHT = "Eventuali policy globali (Token, Rate Limiting) presenti nell'archivio verranno importate";
	public static final String LABEL_PARAMETRO_ARCHIVI_DELETE_POLICY_CONFIG_RIGHT = "Eventuali policy globali (Token, Rate Limiting) presenti nell'archivio verranno eliminate";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_POLICY_CON_ALLARMI_CONFIG_RIGHT = "Eventuali policy globali (Token, Rate Limiting, Allarmi) presenti nell'archivio verranno importate";
	public static final String LABEL_PARAMETRO_ARCHIVI_DELETE_POLICY_CON_ALLARMI_CONFIG_RIGHT = "Eventuali policy globali (Token, Rate Limiting, Allarmi) presenti nell'archivio verranno eliminate";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_PLUGIN_CONFIG_LEFT = EsitoUtils.LABEL_IMPORT_PLUGIN;
	public static final String LABEL_PARAMETRO_ARCHIVI_DELETE_PLUGIN_CONFIG_LEFT = EsitoUtils.LABEL_DELETE_PLUGIN;
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_PLUGIN_CONFIG_RIGHT = "Eventuali plugins (Classi, Archivi) presenti nell'archivio verranno importati";
	public static final String LABEL_PARAMETRO_ARCHIVI_DELETE_PLUGIN_CONFIG_RIGHT = "Eventuali plugins (Classi, Archivi) presenti nell'archivio verranno eliminati";
	
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_CONFIG_LEFT = EsitoUtils.LABEL_IMPORT_CONFIGURAZIONE;
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_CONFIG_RIGHT = "Una eventuale configurazione presente nell'archivio verrà importata";
	public static final String LABEL_PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO = "Tipologia archivio";
	public static final String LABEL_PARAMETRO_ARCHIVI_TIPO = "Tipo";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID_DESCRIPTION ="Descrizione";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT = "Soggetto";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT = "Versione";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT = "Modalità Acquisizione";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE_KEY = "@PT_NAME@";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE = 
			"Informazioni per il port type '"+LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE_KEY+"'";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE ="Nome servizio";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE_KEY = "@OP_NAME@";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE = 
			"Operation '"+LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE_KEY+"'";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION ="Nome azione";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO = "Profilo di collaborazione";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO = 
			"Nome del servizio di richiesta correlato";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_CORRELATA = 
			"Nome dell'azione di richiesta correlata";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_PORT_TYPE_IMPLEMENTED_INPUT = "Servizio";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT = "AccordoServizioParteComune/ServizioComposto";
	public static final String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT = "AccordoCooperazione";
	
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG_LEFT = EsitoUtils.LABEL_IMPORT_POLICY;
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG_CON_ALLARMI_RIGHT = "Vengono incluse le policy globali (Token, Rate Limiting, Allarmi)";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG_RIGHT = "Vengono incluse le policy globali (Token, Rate Limiting)";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PLUGIN_CONFIG_LEFT = EsitoUtils.LABEL_IMPORT_PLUGIN;
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PLUGIN_CONFIG_RIGHT = "Vengono inclusi i plugins (Classi, Archivi)";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_LEFT = "Elementi di Registro";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_RIGHT = "Vengono inclusi gli elementi riferiti";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SEZIONE = "Configurazione 'Cascade'";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP = "Modalità";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD = "Porte di Dominio";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_RUOLI = "Ruoli";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SCOPE = "Scope";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI = "Soggetti";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI = "Servizi Applicativi";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE = "Porte Delegate";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE = "Porte Applicative";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE = "Accordi Cooperazione";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE = "APIs";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO = "Accordi Servizio Composto";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA = "Servizi";
	public static final String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI = "Fruizioni";
		
	
	
	/* DEFAULT VALUE PARAMETRI */
	
	public static final String PARAMETRO_ARCHIVI_IMPORTER_MODALITA_IMPORT = "import";
	public static final String PARAMETRO_ARCHIVI_IMPORTER_MODALITA_ELIMINA = "elimina";
	
	public static final String PARAMETRO_DOWNLOAD_RESOCONTO_VALORE = "DownloadResocontoValore";
	
	public static final String PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED = "-";
	
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED = "-";
	
	public static final String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED = "-";
	
	public static final String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_PARTE_COMUNE = "accordiAPC";
	public static final String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_PARTE_SPECIFICA = "accordiAPS";
	public static final String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_SERVIZIO_COMPOSTO = "accordiASC";
	public static final String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_COOPERAZIONE = "accordiCooperazione";
	
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE = "aspc";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA = "asps";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_COOPERAZIONE = "ac";

	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_DOCUMENTO = "documento";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_DEFINITORIO = "wsdlDefinitorio";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_CONCETTUALE = "wsdlConcettuale";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_EROGATORE = "wsdlLogicoErogatore";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_FRUITORE = "wsdlLogicoFruitore";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_EROGATORE = "wsdlImplementativoErogatore";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_FRUITORE = "wsdlImplementativoFruitore";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_CONCETTUALE = "specificaConversazioneConcettuale";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_EROGATORE = "specificaConversazioneLogicoErogatore";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_FRUITORE = "specificaConversazioneLogicoFruitore";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_XSD_SCHEMA_COLLECTION = "xsdSchemaCollection";
		
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_TIPO_SOGGETTO_FRUITORE = "wsdlImplementativoTipoSoggettoFruitore";
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_NOME_SOGGETTO_FRUITORE = "wsdlImplementativoNomeSoggettoFruitore";
	
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
		
	public static final String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ARCHIVIO_JAR = "jar";
	
	private static final String[] PARAMETRO_VALORI_ARCHIVI_EXPORT_TIPO_DUMP = { ArchiveType.ALL.toString(), ArchiveType.ALL_WITHOUT_CONFIGURAZIONE.toString(), ArchiveType.CONFIGURAZIONE.toString() };
	public static String[] getParametroValoriArchiviExportTipoDump() {
		return PARAMETRO_VALORI_ARCHIVI_EXPORT_TIPO_DUMP;
	}
	private static final String[] PARAMETRO_LABEL_ARCHIVI_EXPORT_TIPO_DUMP = { "Esportazione completa", ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO , ConfigurazioneCostanti.LABEL_CONFIGURAZIONE };
	public static String[] getParametroLabelArchiviExportTipoDump() {
		return PARAMETRO_LABEL_ARCHIVI_EXPORT_TIPO_DUMP;
	}
	public static final String DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA = "interfaccia";
	public static final String DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA_STANDARD = "standard";
	public static final String DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA_AVANZATA = "avanzata";
	public static final String DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE = "connettore";
	public static final String DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE_HTTP = "http";
	public static final String DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE_QUALSIASI = "qualsiasi";
	

}
