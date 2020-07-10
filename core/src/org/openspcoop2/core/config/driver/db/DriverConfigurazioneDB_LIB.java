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


package org.openspcoop2.core.config.driver.db;


import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;
import org.openspcoop2.core.config.GestioneErroreSoapFault;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ProtocolProperty;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDefault;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.StatoServiziPddIntegrationManager;
import org.openspcoop2.core.config.StatoServiziPddPortaApplicativa;
import org.openspcoop2.core.config.StatoServiziPddPortaDelegata;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.TrasformazioneRest;
import org.openspcoop2.core.config.TrasformazioneSoap;
import org.openspcoop2.core.config.TrasformazioneSoapRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.constants.AlgoritmoCache;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettiErogatori;
import org.openspcoop2.core.config.constants.ProprietaProtocolloValore;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.ServiceBinding;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoConnessioneRisposte;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.constants.TrasformazioneRegolaParametroTipoAzione;
import org.openspcoop2.core.config.constants.ValidazioneBusteTipoControllo;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.config.constants.VersioneSOAP;
import org.openspcoop2.core.config.driver.ConnettorePropertiesUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.constants.CRUDType;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.mapping.DBProtocolPropertiesUtils;
import org.openspcoop2.core.mapping.ProprietariProtocolProperty;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**
 * Libreria contenente i metodi di accesso al db e metodi di utilita'
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverConfigurazioneDB_LIB {

	/** Logger utilizzato per debug. */
	public static org.slf4j.Logger log = LoggerWrapperFactory.getLogger(CostantiConfigurazione.DRIVER_DB_LOGGER);

	// Tipo database e tabella Soggetto PDD ereditato da DriverConfigurazioneDB
	private static String tipoDB = null;
	private static String tabellaSoggetti = CostantiDB.SOGGETTI;

	/** Log ereditato da DriverRegistroServiziDB
	 */
	private static boolean initialize = false;
	public static void initStaticLogger(Logger aLog){
		if(DriverConfigurazioneDB_LIB.initialize==false){
			if(aLog!=null){
				DriverConfigurazioneDB_LIB.log = aLog;
			}
			DriverConfigurazioneDB_LIB.initialize = true;
		}
	}
	public static boolean isStaticLoggerInitialized(){
		return DriverConfigurazioneDB_LIB.initialize;
	}

	// Setto il tipoDB
	public static void setTipoDB(String tipoDatabase) {
		DriverConfigurazioneDB_LIB.tipoDB = tipoDatabase;
	}
	public static void setTabellaSoggetti(String tab) {
		DriverConfigurazioneDB_LIB.tabellaSoggetti = tab;
	}

	public static String getValue(StatoFunzionalita funzionalita){
		if(funzionalita==null){
			return null;
		}
		else{
			return funzionalita.getValue();
		}
	}
	public static String getValue(StatoFunzionalitaConWarning funzionalita){
		if(funzionalita==null){
			return null;
		}
		else{
			return funzionalita.getValue();
		}
	}
	public static String getValue(MTOMProcessorType tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(ValidazioneContenutiApplicativiTipo tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(CorrelazioneApplicativaRichiestaIdentificazione identificazione){
		if(identificazione==null){
			return null;
		}
		else{
			return identificazione.getValue();
		}
	}
	public static String getValue(CorrelazioneApplicativaRispostaIdentificazione identificazione){
		if(identificazione==null){
			return null;
		}
		else{
			return identificazione.getValue();
		}
	}
	public static String getValue(CorrelazioneApplicativaGestioneIdentificazioneFallita identificazione){
		if(identificazione==null){
			return null;
		}
		else{
			return identificazione.getValue();
		}
	}
	public static String getValue(InvocazioneServizioTipoAutenticazione tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(FaultIntegrazioneTipo tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(CredenzialeTipo tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(ProprietaProtocolloValore valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(Severita valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(ValidazioneBusteTipoControllo valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(AlgoritmoCache valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(GestioneErroreComportamento valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(TipoConnessioneRisposte valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(PortaApplicativaSoggettiFruitori valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(PortaDelegataSoggettiErogatori valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(TipoGestioneCORS valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(VersioneSOAP valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(TrasformazioneRegolaParametroTipoAzione valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(StatoFunzionalitaCacheDigestQueryParameter valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(RuoloContesto valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	public static String getValue(ServiceBinding valore){
		if(valore==null){
			return null;
		}
		else{
			return valore.getValue();
		}
	}
	
	
	public static StatoFunzionalita getEnumStatoFunzionalita(String value){
		if(value==null){
			return null;
		}
		else{
			return StatoFunzionalita.toEnumConstant(value);
		}
	}
	public static StatoFunzionalitaConWarning getEnumStatoFunzionalitaConWarning(String value){
		if(value==null){
			return null;
		}
		else{
			return StatoFunzionalitaConWarning.toEnumConstant(value);
		}
	}
	public static MTOMProcessorType getEnumMTOMProcessorType(String value){
		if(value==null){
			return null;
		}
		else{
			return MTOMProcessorType.toEnumConstant(value);
		}
	}
	public static ValidazioneContenutiApplicativiTipo getEnumValidazioneContenutiApplicativiTipo(String value){
		if(value==null){
			return null;
		}
		else{
			return ValidazioneContenutiApplicativiTipo.toEnumConstant(value);
		}
	}
	public static CorrelazioneApplicativaRichiestaIdentificazione getEnumCorrelazioneApplicativaRichiestaIdentificazione(String value){
		if(value==null){
			return null;
		}
		else{
			return CorrelazioneApplicativaRichiestaIdentificazione.toEnumConstant(value);
		}
	}
	public static CorrelazioneApplicativaRispostaIdentificazione getEnumCorrelazioneApplicativaRispostaIdentificazione(String value){
		if(value==null){
			return null;
		}
		else{
			return CorrelazioneApplicativaRispostaIdentificazione.toEnumConstant(value);
		}
	}
	public static CorrelazioneApplicativaGestioneIdentificazioneFallita getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(String value){
		if(value==null){
			return null;
		}
		else{
			return CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstant(value);
		}
	}
	public static InvocazioneServizioTipoAutenticazione getEnumInvocazioneServizioTipoAutenticazione(String value){
		if(value==null){
			return null;
		}
		else{
			return InvocazioneServizioTipoAutenticazione.toEnumConstant(value);
		}
	}
	public static FaultIntegrazioneTipo getEnumFaultIntegrazioneTipo(String value){
		if(value==null){
			return null;
		}
		else{
			return FaultIntegrazioneTipo.toEnumConstant(value);
		}
	}
	public static CredenzialeTipo getEnumCredenzialeTipo(String value){
		if(value==null){
			return null;
		}
		else{
			return CredenzialeTipo.toEnumConstant(value);
		}
	}
	public static ProprietaProtocolloValore getEnumProprietaProtocolloValore(String value){
		if(value==null){
			return null;
		}
		else{
			return ProprietaProtocolloValore.toEnumConstant(value);
		}
	}
	public static Severita getEnumSeverita(String value){
		if(value==null){
			return null;
		}
		else{
			return Severita.toEnumConstant(value);
		}
	}
	public static ValidazioneBusteTipoControllo getEnumValidazioneBusteTipoControllo(String value){
		if(value==null){
			return null;
		}
		else{
			return ValidazioneBusteTipoControllo.toEnumConstant(value);
		}
	}
	public static AlgoritmoCache getEnumAlgoritmoCache(String value){
		if(value==null){
			return null;
		}
		else{
			return AlgoritmoCache.toEnumConstant(value);
		}
	}
	public static GestioneErroreComportamento getEnumGestioneErroreComportamento(String value){
		if(value==null){
			return null;
		}
		else{
			return GestioneErroreComportamento.toEnumConstant(value);
		}
	}
	public static TipoConnessioneRisposte getEnumTipoConnessioneRisposte(String value){
		if(value==null){
			return null;
		}
		else{
			return TipoConnessioneRisposte.toEnumConstant(value);
		}
	}
	public static PortaApplicativaSoggettiFruitori getEnumPortaApplicativaSoggettiFruitori(String value){
		if(value==null){
			return null;
		}
		else{
			return PortaApplicativaSoggettiFruitori.toEnumConstant(value);
		}
	}
	public static PortaDelegataSoggettiErogatori getEnumPortaDelegataSoggettiErogatori(String value){
		if(value==null){
			return null;
		}
		else{
			return PortaDelegataSoggettiErogatori.toEnumConstant(value);
		}
	}
	public static TipoGestioneCORS getEnumTipoGestioneCORS(String value){
		if(value==null){
			return null;
		}
		else{
			return TipoGestioneCORS.toEnumConstant(value);
		}
	}
	public static VersioneSOAP getEnumVersioneSOAP(String value){
		if(value==null){
			return null;
		}
		else{
			return VersioneSOAP.toEnumConstant(value);
		}
	}
	public static TrasformazioneRegolaParametroTipoAzione getEnumTrasformazioneRegolaParametroTipoAzione(String value){
		if(value==null){
			return null;
		}
		else{
			return TrasformazioneRegolaParametroTipoAzione.toEnumConstant(value);
		}
	}
	public static StatoFunzionalitaCacheDigestQueryParameter getEnumStatoFunzionalitaCacheDigestQueryParameter(String value){
		if(value==null){
			return null;
		}
		else{
			return StatoFunzionalitaCacheDigestQueryParameter.toEnumConstant(value);
		}
	}
	public static RuoloContesto getEnumRuoloContesto(String value){
		if(value==null){
			return null;
		}
		else{
			return RuoloContesto.toEnumConstant(value);
		}
	}
	public static ServiceBinding getEnumServiceBinding(String value){
		if(value==null){
			return null;
		}
		else{
			return ServiceBinding.toEnumConstant(value);
		}
	}
	
	
	
	
	public static String formatSQLString(String sql, Object... params) {
		String res = sql;

		for (int i = 0; i < params.length; i++) {
			res = res.replaceFirst("\\?", "{" + i + "}");
		}

		return MessageFormat.format(res, params);

	}
	
	
	
	/** Metodi CRUD di accesso al DB */

	/**
	 * CRUD oggetto Soggetto Non si occupa di chiudere la connessione con
	 * il db in caso di errore in quanto verra' gestita dal metodo chiamante
	 * 
	 * @param type
	 *            Tipo operazione {1 (CREATE),2 (UPDATE),3 (DELETE)}
	 * @param soggetto
	 * @return L'ID dell'oggetto creato in caso di CREATE, altrimenti il numero
	 *         di righe che sono state modificate/cancellate
	 * @throws DriverConfigurazioneException
	 */
	public static long CRUDSoggetto(int type, org.openspcoop2.core.config.Soggetto soggetto, Connection con) throws DriverConfigurazioneException {
		if (soggetto == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSoggetto] Parametro non valido.");

		String nome = soggetto.getNome();
		String tipo = soggetto.getTipo();

		if (nome == null || nome.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSoggetto] Parametro Nome non valido.");
		if (tipo == null || tipo.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSoggetto] Parametro Tipo non valido.");

		String descrizione = soggetto.getDescrizione();
		String identificativoPorta = soggetto.getIdentificativoPorta();
		String superuser = soggetto.getSuperUser();

		boolean router = soggetto.getRouter();
		boolean isDefault = soggetto.isDominioDefault();
		
		String pdUrlPrefixRewriter = soggetto.getPdUrlPrefixRewriter();
		String paUrlPrefixRewriter = soggetto.getPaUrlPrefixRewriter();
		
		PreparedStatement updateStmt = null;
		PreparedStatement selectStmt = null;
		String updateQuery = "";
		String selectQuery = "";
		ResultSet selectRS = null;
		int n = 0;
		try {

			// preparo lo statement in base al tipo di operazione
			switch (type) {
			case CREATE:
				// CREATE
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(DriverConfigurazioneDB_LIB.tabellaSoggetti);
				sqlQueryObject.addInsertField("nome_soggetto", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("identificativo_porta", "?");
				sqlQueryObject.addInsertField("tipo_soggetto", "?");
				sqlQueryObject.addInsertField("is_router", "?");
				sqlQueryObject.addInsertField("is_default", "?");
				sqlQueryObject.addInsertField("superuser", "?");
				sqlQueryObject.addInsertField("pd_url_prefix_rewriter", "?");
				sqlQueryObject.addInsertField("pa_url_prefix_rewriter", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				int index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, identificativoPorta);
				updateStmt.setString(index++, tipo);
				updateStmt.setInt(index++, (router ? CostantiDB.TRUE : CostantiDB.FALSE));
				updateStmt.setInt(index++, (isDefault ? CostantiDB.TRUE : CostantiDB.FALSE));
				updateStmt.setString(index++, superuser);
				updateStmt.setString(index++, pdUrlPrefixRewriter);
				updateStmt.setString(index++, paUrlPrefixRewriter);
				// eseguo lo statement
				n = updateStmt.executeUpdate();

				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto CREATE : \n" + DBUtils.formatSQLString(updateQuery, nome, descrizione, identificativoPorta, tipo, router, superuser));
				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(DriverConfigurazioneDB_LIB.tabellaSoggetti);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
				sqlQueryObject.addWhereCondition("nome_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, tipo);
				selectStmt.setString(2, nome);

				break;

			case UPDATE:
				// UPDATE
				String oldNomeSoggetto = null;
				String oldTipoSoggetto = null;
				if(soggetto.getOldIDSoggettoForUpdate()!=null){
					oldNomeSoggetto = soggetto.getOldIDSoggettoForUpdate().getNome();
					oldTipoSoggetto = soggetto.getOldIDSoggettoForUpdate().getTipo();
				}
				// controllo se sono presenti i campi necessari per
				// l'aggiornamento
				if (oldNomeSoggetto == null || oldNomeSoggetto.equals(""))
					oldNomeSoggetto = nome;
				if (oldTipoSoggetto == null || oldTipoSoggetto.equals(""))
					oldTipoSoggetto = tipo;

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(DriverConfigurazioneDB_LIB.tabellaSoggetti);
				sqlQueryObject.addUpdateField("nome_soggetto", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("identificativo_porta", "?");
				sqlQueryObject.addUpdateField("tipo_soggetto", "?");
				sqlQueryObject.addUpdateField("is_router", "?");
				sqlQueryObject.addUpdateField("is_default", "?");
				sqlQueryObject.addUpdateField("superuser", "?");
				sqlQueryObject.addUpdateField("pd_url_prefix_rewriter", "?");
				sqlQueryObject.addUpdateField("pa_url_prefix_rewriter", "?");
				sqlQueryObject.addWhereCondition("nome_soggetto=?");
				sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, identificativoPorta);
				updateStmt.setString(index++, tipo);
				updateStmt.setInt(index++, (router ? CostantiDB.TRUE : CostantiDB.FALSE));
				updateStmt.setInt(index++, (isDefault ? CostantiDB.TRUE : CostantiDB.FALSE));
				updateStmt.setString(index++, superuser);
				updateStmt.setString(index++, pdUrlPrefixRewriter);
				updateStmt.setString(index++, paUrlPrefixRewriter);
				updateStmt.setString(index++, oldNomeSoggetto);
				updateStmt.setString(index++, oldTipoSoggetto);
				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();

				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto UPDATE : \n" + DBUtils.formatSQLString(updateQuery, nome, descrizione, identificativoPorta, tipo, router, soggetto.getId()));
				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n);
				break;

			case DELETE:
				// DELETE
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(DriverConfigurazioneDB_LIB.tabellaSoggetti);
				sqlQueryObject.addWhereCondition("nome_soggetto=?");
				sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setString(1, soggetto.getNome());
				updateStmt.setString(2, soggetto.getTipo());
				n = updateStmt.executeUpdate();
				updateStmt.close();

				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n);
				DriverConfigurazioneDB_LIB.log.debug("CRUDSoggetto DELETE : \n" + DBUtils.formatSQLString(updateQuery, soggetto.getNome(), soggetto.getTipo()));

				break;
			}

			// in caso di create eseguo la select e ritorno l'id dell'oggetto
			// creato
			if (type == CostantiDB.CREATE) {
				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {
					soggetto.setId(selectRS.getLong("id"));
					return soggetto.getId();
				}
			}

			return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSoggetto] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSoggetto] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 * CRUD oggetto Connettore. In caso di CREATE inserisce nel db il dati del
	 * connettore passato e ritorna l'id dell'oggetto creato Non si occupa di
	 * chiudere la connessione con il db in caso di errore in quanto verra'
	 * gestita dal metodo chiamante
	 * 
	 * @param type
	 *            Tipo operazione {1 (CREATE),2 (UPDATE),3 (DELETE)}
	 * @param connettore
	 * @return id del connettore in caso di type 1 (CREATE)
	 */
	public static long CRUDConnettore(int type, Connettore connettore, Connection connection) throws DriverConfigurazioneException {
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(connettore == null) throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] L'oggetto Connettore non puo essere null");
		if (type!=CostantiDB.DELETE){
			if(connettore.getNome() == null || connettore.getNome().trim().equals(""))throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore]Il nome Connettore non puo essere null");
		}
		// il tipo di connettore (http, jms, disabilitato o altro)
		String nomeConnettore = connettore.getNome();
		String endpointtype = connettore.getTipo();

		if (endpointtype == null || endpointtype.trim().equals(""))
			endpointtype = TipiConnettore.DISABILITATO.getNome();

		String url = null;// in caso di tipo http
		boolean debug = false;
		String nome = null; // jms
		String tipo = null; // jms
		String utente = null;// jms
		String password = null;// jms
		String initcont = null;// jms
		String urlpkg = null;// jms
		String provurl = null;// jms
		String connectionfactory = null;// jms
		String sendas = null;// jms

		String transfer_mode = null; // in caso di tipo http e https
		Integer transfer_mode_chunk_size = null; // in caso di tipo http e https

		boolean proxy = false;
		String proxy_type = null;
		String proxy_hostname = null;
		String proxy_port = null;
		String proxy_username = null;
		String proxy_password = null;
		
		Integer tempiRisposta_connectionTimeout = null;
		Integer tempiRisposta_readTimeout = null;
		Integer tempiRisposta_avgResponseTime = null;

		String redirect_mode = null; // in caso di tipo http e https
		Integer redirect_max_hop = null; // in caso di tipo http e https
		
		String token_policy = null;
		
		boolean isAbilitato = false;

		Hashtable<String, String> extendedProperties = new Hashtable<String, String>();
		
		List<String> propertiesGestiteAttraversoColonneAdHoc = new ArrayList<String>();
		
		// setto i dati, se le property non sono presenti il loro valore rimarra
		// a null e verra settato come tale nel DB
		String nomeProperty = null;
		String valoreProperty = null;
		for (int i = 0; i < connettore.sizePropertyList(); i++) {
			nomeProperty = connettore.getProperty(i).getNome();

			valoreProperty = connettore.getProperty(i).getValore();
			if (valoreProperty != null && valoreProperty.equals(""))
				valoreProperty = null;

			// Debug
			if (nomeProperty.equals(CostantiDB.CONNETTORE_DEBUG)){
				if("true".equals(valoreProperty)){
					debug=true;
				}
			}
			
			// Proxy
			if (nomeProperty.equals(CostantiDB.CONNETTORE_PROXY_TYPE)){
				proxy = true;
				proxy_type = valoreProperty;
				
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				
				// cerco altri valori del proxy
				for (Property propertyCheck: connettore.getPropertyList()) {
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_HOSTNAME)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_hostname = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_PORT)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_port = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_USERNAME)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_username = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_PASSWORD)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_password = propertyCheck.getValore();
					}
				}
			}
			
			// Tempi Risposta
			if (nomeProperty.equals(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				tempiRisposta_connectionTimeout = Integer.parseInt(valoreProperty);
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				tempiRisposta_readTimeout = Integer.parseInt(valoreProperty);
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				tempiRisposta_avgResponseTime = Integer.parseInt(valoreProperty);
			}
			
			// TransferMode
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				transfer_mode = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				transfer_mode_chunk_size = Integer.parseInt(valoreProperty);
			}
			
			// RedirectMode
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				redirect_mode = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				redirect_max_hop = Integer.parseInt(valoreProperty);
			}
			
			// TokenPolicy
			if (nomeProperty.equals(CostantiDB.CONNETTORE_TOKEN_POLICY)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				token_policy = valoreProperty;
			}
			

			if(TipiConnettore.HTTP.getNome().equals(endpointtype)){
				if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_LOCATION))
					url = valoreProperty;
			}
			else if(TipiConnettore.JMS.getNome().equals(endpointtype)){
				if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_NOME))
					nome = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_TIPO))
					tipo = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_USER))
					utente = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_PWD))
					password = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL))
					initcont = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG))
					urlpkg = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL))
					provurl = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY))
					connectionfactory = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_SEND_AS))
					sendas = valoreProperty;
			}

			// se endpointype != disabilitato allora lo setto abilitato
			if (!endpointtype.equalsIgnoreCase(TipiConnettore.DISABILITATO.getNome()))
				isAbilitato = true;
			
			// extendedProperties
			if(nomeProperty.startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)){
				extendedProperties.put(nomeProperty, valoreProperty);
			}

		}

		try {

			long idConnettore = 0;
			int n = 0;
			switch (type) {
			case CREATE:

				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addInsertField("endpointtype", "?");
				sqlQueryObject.addInsertField("url", "?");
				sqlQueryObject.addInsertField("transfer_mode", "?");
				sqlQueryObject.addInsertField("transfer_mode_chunk_size", "?");
				sqlQueryObject.addInsertField("redirect_mode", "?");
				sqlQueryObject.addInsertField("redirect_max_hop", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				sqlQueryObject.addInsertField("initcont", "?");
				sqlQueryObject.addInsertField("urlpkg", "?");
				sqlQueryObject.addInsertField("provurl", "?");
				sqlQueryObject.addInsertField("connection_factory", "?");
				sqlQueryObject.addInsertField("send_as", "?");
				sqlQueryObject.addInsertField("nome_connettore", "?");
				sqlQueryObject.addInsertField("debug", "?");				
				sqlQueryObject.addInsertField("proxy", "?");		
				sqlQueryObject.addInsertField("proxy_type", "?");		
				sqlQueryObject.addInsertField("proxy_hostname", "?");		
				sqlQueryObject.addInsertField("proxy_port", "?");		
				sqlQueryObject.addInsertField("proxy_username", "?");		
				sqlQueryObject.addInsertField("proxy_password", "?");	
				sqlQueryObject.addInsertField("connection_timeout", "?");		
				sqlQueryObject.addInsertField("read_timeout", "?");		
				sqlQueryObject.addInsertField("avg_response_time", "?");		
				sqlQueryObject.addInsertField("custom", "?");
				sqlQueryObject.addInsertField("token_policy", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = connection.prepareStatement(sqlQuery);

				int index = 1;
				stm.setString(index++, endpointtype);
				stm.setString(index++, (isAbilitato ? url : null));
				stm.setString(index++, (isAbilitato ? transfer_mode : null));
				if(isAbilitato && transfer_mode_chunk_size!=null){
					stm.setInt(index++, transfer_mode_chunk_size);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, (isAbilitato ? redirect_mode : null));
				if(isAbilitato && redirect_max_hop!=null){
					stm.setInt(index++, redirect_max_hop);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, isAbilitato ? nome : null);
				stm.setString(index++, isAbilitato ? tipo : null);
				stm.setString(index++, (isAbilitato ? utente : null));
				stm.setString(index++, (isAbilitato ? password : null));
				stm.setString(index++, (isAbilitato ? initcont : null));
				stm.setString(index++, (isAbilitato ? urlpkg : null));
				stm.setString(index++, (isAbilitato ? provurl : null));
				stm.setString(index++, (isAbilitato ? connectionfactory : null));
				stm.setString(index++, (isAbilitato ? sendas : null));
				stm.setString(index++, nomeConnettore);
				if(debug){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				if(proxy){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, isAbilitato && proxy ? proxy_type : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_hostname : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_port : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_username : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_password : null);
				if(tempiRisposta_connectionTimeout!=null) {
					stm.setInt(index++, tempiRisposta_connectionTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRisposta_readTimeout!=null) {
					stm.setInt(index++, tempiRisposta_readTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRisposta_avgResponseTime!=null) {
					stm.setInt(index++, tempiRisposta_avgResponseTime);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, token_policy);

				DriverConfigurazioneDB_LIB.log.debug("CRUDConnettore CREATE : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, url, 
						transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						nome, tipo, utente, password, initcont, urlpkg, provurl, connectionfactory, sendas, nomeConnettore,debug,
						proxy, proxy_type, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_avgResponseTime,
						(connettore.getCustom()!=null && connettore.getCustom())),
						token_policy);

				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + n + " row(s)");


				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("endpointtype = ?");
				sqlQueryObject.addWhereCondition("nome_connettore = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = connection.prepareStatement(sqlQuery);

				stm.setString(1, endpointtype);
				stm.setString(2, nomeConnettore);

				DriverConfigurazioneDB_LIB.log.debug("Recupero idConnettore inserito : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, nomeConnettore));

				rs = stm.executeQuery();

				if (rs.next()) {
					idConnettore = rs.getLong("id");
					connettore.setId(idConnettore);
				} else {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] Errore tentanto di effettuare la select dopo una create, non riesco a recuperare l'id!");
				}

				rs.close();
				stm.close();				
				
				// Custom properties
				if(connettore.getCustom()!=null && connettore.getCustom()){					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (int i = 0; i < connettore.sizePropertyList(); i++) {
						nomeProperty = connettore.getProperty(i).getNome();
						if(propertiesGestiteAttraversoColonneAdHoc.contains(nomeProperty)){
							continue;
						}
						valoreProperty = connettore.getProperty(i).getValore();
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeProperty+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, valoreProperty);
						stm.setLong(3, connettore.getId());
						stm.executeUpdate();
						stm.close();
					}				
				}
				else if(extendedProperties.size()>0){
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					Enumeration<String> keys = extendedProperties.keys();
					while (keys.hasMoreElements()) {
						nomeProperty = (String) keys.nextElement();
						valoreProperty = extendedProperties.get(nomeProperty);
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeProperty+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, valoreProperty);
						stm.setLong(3, connettore.getId());
						stm.executeUpdate();
						stm.close();
					}				
				}

				break;

			case UPDATE:
				// update
				idConnettore = connettore.getId();

				if (idConnettore < 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] L'id del connettore non puo essere 0 tentando di fare una operazione di update.");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addUpdateField("endpointtype", "?");
				sqlQueryObject.addUpdateField("url", "?");
				sqlQueryObject.addUpdateField("transfer_mode", "?");
				sqlQueryObject.addUpdateField("transfer_mode_chunk_size", "?");
				sqlQueryObject.addUpdateField("redirect_mode", "?");
				sqlQueryObject.addUpdateField("redirect_max_hop", "?");
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("tipo", "?");
				sqlQueryObject.addUpdateField("utente", "?");
				sqlQueryObject.addUpdateField("password", "?");
				sqlQueryObject.addUpdateField("initcont", "?");
				sqlQueryObject.addUpdateField("urlpkg", "?");
				sqlQueryObject.addUpdateField("provurl", "?");
				sqlQueryObject.addUpdateField("connection_factory", "?");
				sqlQueryObject.addUpdateField("send_as", "?");
				sqlQueryObject.addUpdateField("nome_connettore", "?");
				sqlQueryObject.addUpdateField("debug", "?");
				sqlQueryObject.addUpdateField("proxy", "?");		
				sqlQueryObject.addUpdateField("proxy_type", "?");		
				sqlQueryObject.addUpdateField("proxy_hostname", "?");		
				sqlQueryObject.addUpdateField("proxy_port", "?");		
				sqlQueryObject.addUpdateField("proxy_username", "?");		
				sqlQueryObject.addUpdateField("proxy_password", "?");
				sqlQueryObject.addUpdateField("connection_timeout", "?");		
				sqlQueryObject.addUpdateField("read_timeout", "?");		
				sqlQueryObject.addUpdateField("avg_response_time", "?");
				sqlQueryObject.addUpdateField("custom", "?");
				sqlQueryObject.addUpdateField("token_policy", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = connection.prepareStatement(sqlQuery);

				index = 1;
				stm.setString(index++, endpointtype);
				stm.setString(index++, url);
				stm.setString(index++, (isAbilitato ? transfer_mode : null));
				if(isAbilitato && transfer_mode_chunk_size!=null){
					stm.setInt(index++, transfer_mode_chunk_size);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, (isAbilitato ? redirect_mode : null));
				if(isAbilitato && redirect_max_hop!=null){
					stm.setInt(index++, redirect_max_hop);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, nome);
				stm.setString(index++, tipo);
				stm.setString(index++, utente);
				stm.setString(index++, password);
				stm.setString(index++, initcont);
				stm.setString(index++, urlpkg);
				stm.setString(index++, provurl);
				stm.setString(index++, connectionfactory);
				stm.setString(index++, sendas);
				stm.setString(index++, nomeConnettore);
				if(debug){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				if(proxy){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, isAbilitato && proxy ? proxy_type : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_hostname : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_port : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_username : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_password : null);
				if(tempiRisposta_connectionTimeout!=null) {
					stm.setInt(index++, tempiRisposta_connectionTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRisposta_readTimeout!=null) {
					stm.setInt(index++, tempiRisposta_readTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRisposta_avgResponseTime!=null) {
					stm.setInt(index++, tempiRisposta_avgResponseTime);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, token_policy);
				stm.setLong(index++, idConnettore);

				stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("CRUDConnettore UPDATE : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, url, 
						transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						nome, tipo, utente, password, initcont, urlpkg, provurl, connectionfactory, sendas,nomeConnettore, debug,
						proxy, proxy_type, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_avgResponseTime,
						(connettore.getCustom()!=null && connettore.getCustom()),
						token_policy,
						idConnettore));

				// Custom properties
				// Delete eventuali vecchie properties
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				// Aggiungo attuali
				if(connettore.getCustom()!=null && connettore.getCustom()){					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (int i = 0; i < connettore.sizePropertyList(); i++) {
						nomeProperty = connettore.getProperty(i).getNome();
						if(propertiesGestiteAttraversoColonneAdHoc.contains(nomeProperty)){
							continue;
						}
						valoreProperty = connettore.getProperty(i).getValore();
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeProperty+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, valoreProperty);
						stm.setLong(3, idConnettore);
						stm.executeUpdate();
						stm.close();
					}				
				}
				else if(extendedProperties.size()>0){
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					Enumeration<String> keys = extendedProperties.keys();
					while (keys.hasMoreElements()) {
						nomeProperty = (String) keys.nextElement();
						valoreProperty = extendedProperties.get(nomeProperty);
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeProperty+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, valoreProperty);
						stm.setLong(3, idConnettore);
						stm.executeUpdate();
						stm.close();
					}			
				}
				
				break;

			case DELETE:
				// delete
				idConnettore = connettore.getId();

				if (idConnettore < 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] L'id del connettore non puo essere 0 tentando di fare una operazione di delete.");

				// Delete eventuali vecchie properties
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				
				// Delete connettori
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("CRUDConnettore DELETE : \n" + DBUtils.formatSQLString(sqlQuery, idConnettore));

				break;
			}

			// ritorno l id del connettore questo e' utile in caso di create
			return idConnettore;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] SQLException : " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * @param type
	 * @param aPD
	 * @param con
	 * @throws DriverConfigurazioneException
	 */
	public static long CRUDPortaDelegata(int type, PortaDelegata aPD, Connection con) throws DriverConfigurazioneException {

		if (aPD == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata] Porta Delegata non valida.");

		// parametri necessari
		String nomePorta = aPD.getNome();
		String nomeProprietario = aPD.getNomeSoggettoProprietario();
		String tipoProprietario = aPD.getTipoSoggettoProprietario();
		if (nomePorta == null || nomePorta.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata] Nome della Porta Delegata non valido.");
		if (nomeProprietario == null || nomeProprietario.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata] Nome proprietario Porta Delegata non valido.");
		if (tipoProprietario == null || tipoProprietario.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata] Tipo proprietario della Porta Delegata non valido.");

		PreparedStatement stm = null;
		//PreparedStatement stm1 = null;
		String sqlQuery;
		ResultSet rs = null;

		String autenticazione = aPD.getAutenticazione();
		String autorizzazione = aPD.getAutorizzazione();
		String autorizzazioneXacmlPolicy = aPD.getXacmlPolicy();
		String autorizzazioneContenuto = aPD.getAutorizzazioneContenuto();
		String descrizione = aPD.getDescrizione();
		GestioneToken gestioneToken = aPD.getGestioneToken();

		PortaDelegataAzione azione = aPD.getAzione();
		long idAzione = ((azione != null && azione.getId() != null) ? azione.getId() : -1);

		long idSoggettoProprietario;
		try {
			idSoggettoProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
		} catch (Exception e1) {
			throw new DriverConfigurazioneException(e1);
		}
		long idPortaDelegata = -1;

		PortaDelegataServizio servizio = aPD.getServizio();
		long idServizioPD = ((servizio != null && servizio.getId() != null) ? servizio.getId() : -1);

		PortaDelegataSoggettoErogatore soggErogatore = aPD.getSoggettoErogatore();
		long idSoggettoErogatore = ((soggErogatore != null && soggErogatore.getId() != null) ? soggErogatore.getId() : -1);

		MtomProcessor mtomProcessor = aPD.getMtomProcessor();
		MTOMProcessorType mtomMode_request = null;
		MTOMProcessorType mtomMode_response = null;
		if(mtomProcessor!=null){
			if(mtomProcessor.getRequestFlow()!=null){
				mtomMode_request = mtomProcessor.getRequestFlow().getMode();
			}
			if(mtomProcessor.getResponseFlow()!=null){
				mtomMode_response = mtomProcessor.getResponseFlow().getMode();
			}
		}
		
		MessageSecurity messageSecurity = aPD.getMessageSecurity();
		String messageSecurityStatus = aPD.getStatoMessageSecurity();
		StatoFunzionalita messageSecurityApplyMtom_request = null;
		StatoFunzionalita messageSecurityApplyMtom_response = null;
		String securityRequestMode = null;
		String securityResponseMode = null;
		if(messageSecurity!=null){
			if(messageSecurity.getRequestFlow()!=null){
				messageSecurityApplyMtom_request = messageSecurity.getRequestFlow().getApplyToMtom();
				securityRequestMode = messageSecurity.getRequestFlow().getMode();
			}
			if(messageSecurity.getResponseFlow()!=null){
				messageSecurityApplyMtom_response = messageSecurity.getResponseFlow().getApplyToMtom();
				securityResponseMode = messageSecurity.getResponseFlow().getMode();
			}
		}
		
		CorrelazioneApplicativa corrApp = aPD.getCorrelazioneApplicativa();
		CorrelazioneApplicativaRisposta corrAppRisposta = aPD.getCorrelazioneApplicativaRisposta();
		
		String msg_diag_severita = null;
		String tracciamento_esiti = null;
		if(aPD.getTracciamento()!=null){
			msg_diag_severita = DriverConfigurazioneDB_LIB.getValue(aPD.getTracciamento().getSeverita());
			tracciamento_esiti = aPD.getTracciamento().getEsiti();
		}
		
		CorsConfigurazione corsConfigurazione = aPD.getGestioneCors();
		String cors_stato = null;
		String cors_tipo = null; 
		String cors_all_allow_origins = null; 
		String cors_allow_credentials = null; 
		int cors_allow_max_age = CostantiDB.FALSE;
		Integer cors_allow_max_age_seconds = null;
		String cors_allow_origins = null; 
		String cors_allow_headers = null; 
		String cors_allow_methods = null; 
		String cors_allow_expose_headers = null; 
		if(corsConfigurazione!=null) {
			cors_stato = getValue(corsConfigurazione.getStato());
			cors_tipo = getValue(corsConfigurazione.getTipo());
			cors_all_allow_origins = getValue(corsConfigurazione.getAccessControlAllAllowOrigins());
			cors_allow_credentials = getValue(corsConfigurazione.getAccessControlAllowCredentials());
			if(corsConfigurazione.getAccessControlMaxAge()!=null) {
				cors_allow_max_age = CostantiDB.TRUE;
				cors_allow_max_age_seconds = corsConfigurazione.getAccessControlMaxAge();	
			}
			if(corsConfigurazione.getAccessControlAllowOrigins()!=null && corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowOrigins().getOrigin(i));
				}
				cors_allow_origins = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowHeaders()!=null && corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowHeaders().getHeader(i));
				}
				cors_allow_headers = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowMethods()!=null && corsConfigurazione.getAccessControlAllowMethods().sizeMethodList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowMethods().sizeMethodList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowMethods().getMethod(i));
				}
				cors_allow_methods = bf.toString();
			}
			if(corsConfigurazione.getAccessControlExposeHeaders()!=null && corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlExposeHeaders().getHeader(i));
				}
				cors_allow_expose_headers = bf.toString();
			}
		}
		
		ResponseCachingConfigurazione responseCachingConfigurazone = aPD.getResponseCaching();
		String response_cache_stato = null;
		Integer response_cache_seconds = null;
		Long response_cache_max_msg_size = null;
		String response_cache_hash_url = null;
		String response_cache_hash_query = null;
		String response_cache_hash_query_list = null;
		String response_cache_hash_headers = null;
		String response_cache_hash_headers_list = null;
		String response_cache_hash_payload = null;
		boolean response_cache_noCache = true;
		boolean response_cache_maxAge = true;
		boolean response_cache_noStore = true;
		List<ResponseCachingConfigurazioneRegola> response_cache_regole = null;
		if(responseCachingConfigurazone!=null) {
			response_cache_stato = getValue(responseCachingConfigurazone.getStato());
			response_cache_seconds = responseCachingConfigurazone.getCacheTimeoutSeconds();
			response_cache_max_msg_size = responseCachingConfigurazone.getMaxMessageSize();
			if(responseCachingConfigurazone.getControl()!=null) {
				response_cache_noCache = responseCachingConfigurazone.getControl().getNoCache();
				response_cache_maxAge = responseCachingConfigurazone.getControl().getMaxAge();
				response_cache_noStore = responseCachingConfigurazone.getControl().getNoStore();
			}
			if(responseCachingConfigurazone.getHashGenerator()!=null) {
				response_cache_hash_url = getValue(responseCachingConfigurazone.getHashGenerator().getRequestUri());
				
				response_cache_hash_query = getValue(responseCachingConfigurazone.getHashGenerator().getQueryParameters());
				if(StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(responseCachingConfigurazone.getHashGenerator().getQueryParameters())) {
					if(responseCachingConfigurazone.getHashGenerator().getQueryParameterList()!=null && responseCachingConfigurazone.getHashGenerator().sizeQueryParameterList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < responseCachingConfigurazone.getHashGenerator().sizeQueryParameterList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(responseCachingConfigurazone.getHashGenerator().getQueryParameter(i));
						}
						response_cache_hash_query_list = bf.toString();
					}
				}
				
				response_cache_hash_headers = getValue(responseCachingConfigurazone.getHashGenerator().getHeaders());
				if(StatoFunzionalita.ABILITATO.equals(responseCachingConfigurazone.getHashGenerator().getHeaders())) {
					if(responseCachingConfigurazone.getHashGenerator().getHeaderList()!=null && responseCachingConfigurazone.getHashGenerator().sizeHeaderList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < responseCachingConfigurazone.getHashGenerator().sizeHeaderList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(responseCachingConfigurazone.getHashGenerator().getHeader(i));
						}
						response_cache_hash_headers_list = bf.toString();
					}
				}
				
				response_cache_hash_payload = getValue(responseCachingConfigurazone.getHashGenerator().getPayload());
			}
			response_cache_regole = responseCachingConfigurazone.getRegolaList();
		}
		
		ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
		IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoPortaDelegata();
		
		try {
			int n = 0;
			int i = 0;
			switch (type) {
			case CREATE:

				//soggetto erogatore
				String tipoSoggErogatore = (soggErogatore != null ? soggErogatore.getTipo() : null);
				String nomeSoggErogatore = (soggErogatore != null ? soggErogatore.getNome() : null);
				if(tipoSoggErogatore==null || tipoSoggErogatore.equals("")) throw new DriverConfigurazioneException("Tipo Soggetto Erogatore non impostato.");
				if(nomeSoggErogatore==null || nomeSoggErogatore.equals("")) throw new DriverConfigurazioneException("Nome Soggetto Erogatore non impostato.");

				//servizio
				String tipoServizio = (servizio != null ? servizio.getTipo() : null);
				String nomeServizio = (servizio != null ? servizio.getNome() : null);
				Integer versioneServizio = (servizio != null ? servizio.getVersione() : null);
				if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
				if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
				if(versioneServizio==null) throw new DriverConfigurazioneException("Versione Servizio non impostato.");

				//Azione
				String nomeAzione = (azione != null ? azione.getNome() : null);
				String patternAzione = (azione != null ? azione.getPattern() : null);
				String nomePortaDeleganteAzione = (azione != null ? azione.getNomePortaDelegante() : null);
				StatoFunzionalita forceInterfaceBased = (azione != null ? azione.getForceInterfaceBased() : null);
				PortaDelegataAzioneIdentificazione modeAzione = (azione != null ? azione.getIdentificazione() : null);
				//Se il bean Azione nn e' presente allora non controllo nulla
				if(azione!=null){
					if(modeAzione==null || modeAzione.equals("")) 
						modeAzione = PortaDelegataAzioneIdentificazione.STATIC;
					switch (modeAzione) {
					case CONTENT_BASED:
					case URL_BASED:
					case HEADER_BASED:
						if(patternAzione==null || patternAzione.equals("")) throw new DriverConfigurazioneException("Pattern Azione non impostato.");
						nomeAzione=null;
						break;
					case DELEGATED_BY:
						if(nomePortaDeleganteAzione==null || nomePortaDeleganteAzione.equals("")) throw new DriverConfigurazioneException("Nome Porta Delegante Azione non impostata.");
						nomeAzione=null;
						break;
					case INPUT_BASED:
					case SOAP_ACTION_BASED:
					case INTERFACE_BASED:
						//nessun campo obbligatorio
						break;
					case STATIC:
						//se non c'e' l'id dell'azione ci deve essere il nome
						if(idAzione<=0){
							if(nomeAzione==null || nomeAzione.equals("")) throw new DriverConfigurazioneException("Nome Azione non impostato.");
						}
						patternAzione=null;
						break;
					default:
						break;
					}
				}

				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addInsertField("nome_porta", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("id_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("tipo_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("nome_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("id_servizio", "?");
				sqlQueryObject.addInsertField("tipo_servizio", "?");
				sqlQueryObject.addInsertField("nome_servizio", "?");
				sqlQueryObject.addInsertField("versione_servizio", "?");
				sqlQueryObject.addInsertField("id_azione", "?");
				sqlQueryObject.addInsertField("nome_azione", "?");
				sqlQueryObject.addInsertField("mode_azione", "?");
				sqlQueryObject.addInsertField("pattern_azione", "?");
				sqlQueryObject.addInsertField("nome_porta_delegante_azione", "?");
				sqlQueryObject.addInsertField("force_interface_based_azione", "?");
				sqlQueryObject.addInsertField("autenticazione", "?");
				sqlQueryObject.addInsertField("autenticazione_opzionale", "?");
				sqlQueryObject.addInsertField("token_policy", "?");
				sqlQueryObject.addInsertField("token_opzionale", "?");
				sqlQueryObject.addInsertField("token_validazione", "?");
				sqlQueryObject.addInsertField("token_introspection", "?");
				sqlQueryObject.addInsertField("token_user_info", "?");
				sqlQueryObject.addInsertField("token_forward", "?");
				sqlQueryObject.addInsertField("token_options", "?");
				sqlQueryObject.addInsertField("token_authn_issuer", "?");
				sqlQueryObject.addInsertField("token_authn_client_id", "?");
				sqlQueryObject.addInsertField("token_authn_subject", "?");
				sqlQueryObject.addInsertField("token_authn_username", "?");
				sqlQueryObject.addInsertField("token_authn_email", "?");
				sqlQueryObject.addInsertField("autorizzazione", "?");
				sqlQueryObject.addInsertField("autorizzazione_xacml", "?");
				sqlQueryObject.addInsertField("autorizzazione_contenuto", "?");
				sqlQueryObject.addInsertField("mtom_request_mode", "?");
				sqlQueryObject.addInsertField("mtom_response_mode", "?");
				sqlQueryObject.addInsertField("security", "?");
				sqlQueryObject.addInsertField("security_mtom_req", "?");
				sqlQueryObject.addInsertField("security_mtom_res", "?");
				sqlQueryObject.addInsertField("security_request_mode", "?");
				sqlQueryObject.addInsertField("security_response_mode", "?");
				sqlQueryObject.addInsertField("id_soggetto", "?");
				sqlQueryObject.addInsertField("ricevuta_asincrona_sim", "?");
				sqlQueryObject.addInsertField("ricevuta_asincrona_asim", "?");
				sqlQueryObject.addInsertField("integrazione", "?");
				sqlQueryObject.addInsertField("scadenza_correlazione_appl", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_stato", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_mtom", "?");
				sqlQueryObject.addInsertField("allega_body", "?");
				sqlQueryObject.addInsertField("scarta_body", "?");
				sqlQueryObject.addInsertField("gestione_manifest", "?");
				sqlQueryObject.addInsertField("stateless", "?");
				sqlQueryObject.addInsertField("local_forward", "?");
				sqlQueryObject.addInsertField("local_forward_pa", "?");
				sqlQueryObject.addInsertField("ruoli_match", "?");
				sqlQueryObject.addInsertField("scope_stato", "?");
				sqlQueryObject.addInsertField("scope_match", "?");
				sqlQueryObject.addInsertField("ricerca_porta_azione_delegata", "?");
				sqlQueryObject.addInsertField("msg_diag_severita", "?");
				sqlQueryObject.addInsertField("tracciamento_esiti", "?");
				sqlQueryObject.addInsertField("stato", "?");
				// cors
				sqlQueryObject.addInsertField("cors_stato", "?");
				sqlQueryObject.addInsertField("cors_tipo", "?");
				sqlQueryObject.addInsertField("cors_all_allow_origins", "?");
				sqlQueryObject.addInsertField("cors_allow_credentials", "?");
				sqlQueryObject.addInsertField("cors_allow_max_age", "?");
				sqlQueryObject.addInsertField("cors_allow_max_age_seconds", "?");
				sqlQueryObject.addInsertField("cors_allow_origins", "?");
				sqlQueryObject.addInsertField("cors_allow_headers", "?");
				sqlQueryObject.addInsertField("cors_allow_methods", "?");
				sqlQueryObject.addInsertField("cors_allow_expose_headers", "?");
				// responseCaching
				sqlQueryObject.addInsertField("response_cache_stato", "?");
				sqlQueryObject.addInsertField("response_cache_seconds", "?");
				sqlQueryObject.addInsertField("response_cache_max_msg_size", "?");
				sqlQueryObject.addInsertField("response_cache_control_nocache", "?");
				sqlQueryObject.addInsertField("response_cache_control_maxage", "?");
				sqlQueryObject.addInsertField("response_cache_control_nostore", "?");
				sqlQueryObject.addInsertField("response_cache_hash_url", "?");
				sqlQueryObject.addInsertField("response_cache_hash_query", "?");
				sqlQueryObject.addInsertField("response_cache_hash_query_list", "?");
				sqlQueryObject.addInsertField("response_cache_hash_headers", "?");
				sqlQueryObject.addInsertField("response_cache_hash_hdr_list", "?");
				sqlQueryObject.addInsertField("response_cache_hash_payload", "?");
				// id
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("id_port_type", "?");
				// options
				sqlQueryObject.addInsertField("options", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				int index = 1;
				stm.setString(index++, nomePorta);
				stm.setString(index++, descrizione);
				stm.setLong(index++, idSoggettoErogatore);				
				stm.setString(index++, tipoSoggErogatore);
				stm.setString(index++, nomeSoggErogatore);
				stm.setLong(index++, idServizioPD);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setInt(index++, versioneServizio);
				stm.setLong(index++, idAzione);
				stm.setString(index++, nomeAzione);
				if(modeAzione!=null){
					stm.setString(index++, modeAzione.toString());
				}
				else{
					stm.setString(index++, null);
				}
				stm.setString(index++, patternAzione);
				stm.setString(index++, nomePortaDeleganteAzione);
				stm.setString(index++, getValue(forceInterfaceBased));
				// autenticazione
				stm.setString(index++, autenticazione);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getAutenticazioneOpzionale()) : null);
				// token
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getPolicy() : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getTokenOpzionale()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getValidazione()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getIntrospection()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getUserInfo()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getForward()) : null);
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getOptions() : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getIssuer()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getClientId()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getSubject()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getUsername()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getEmail()) : null);
				// autorizzazione
				stm.setString(index++, autorizzazione);
				stm.setString(index++, autorizzazioneXacmlPolicy);
				stm.setString(index++, autorizzazioneContenuto);
				// mtom
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_response));
				stm.setString(index++, securityRequestMode);
				stm.setString(index++, securityResponseMode);
				// proprietario
				stm.setLong(index++, idSoggettoProprietario);
				//ricevuta asincrona_asimmetrica/simmetrica
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaSimmetrica()));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaAsimmetrica()));				
				//integrazione
				stm.setString(index++, aPD.getIntegrazione());
				//correlazione applicativa scadenza
				stm.setString(index++, aPD.getCorrelazioneApplicativa()!=null ? aPD.getCorrelazioneApplicativa().getScadenza() : null);
				//validazione xsd
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getStato()) : null);
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getTipo()) : null);
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) : null);

				// InvocazionePorta: funzionalita' attachment
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getAllegaBody()) : null);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getScartaBody()) : null);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getGestioneManifest()) : null);
				
				// Stateless
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getStateless()) : null);
				
				// LocalForward
				stm.setString(index++, aPD!=null && aPD.getLocalForward()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getLocalForward().getStato()) : null);
				stm.setString(index++, aPD!=null && aPD.getLocalForward()!=null ? aPD.getLocalForward().getPortaApplicativa() : null);
				
				// Ruoli
				stm.setString(index++, aPD!=null && aPD.getRuoli()!=null && aPD.getRuoli().getMatch()!=null ? 
						aPD.getRuoli().getMatch().getValue() : null);
				
				// Scope
				stm.setString(index++, aPD!=null && aPD.getScope()!=null && aPD.getScope().getStato()!=null ? 
						DriverConfigurazioneDB_LIB.getValue(aPD.getScope().getStato()) : null);
				stm.setString(index++, aPD!=null && aPD.getScope()!=null && aPD.getScope().getMatch()!=null ? 
						aPD.getScope().getMatch().getValue() : null);
				
				// Ricerca Porta Azione Delegata
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getRicercaPortaAzioneDelegata()) : null);
				
				// Tracciamento
				stm.setString(index++, msg_diag_severita);
				stm.setString(index++, tracciamento_esiti);
				
				// Stato
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getStato()) : null);
				
				// cors
				stm.setString(index++, cors_stato);
				stm.setString(index++, cors_tipo);
				stm.setString(index++, cors_all_allow_origins);
				stm.setString(index++, cors_allow_credentials);
				stm.setInt(index++, cors_allow_max_age);
				if(cors_allow_max_age_seconds!=null) {
					stm.setInt(index++, cors_allow_max_age_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				stm.setString(index++, cors_allow_origins);
				stm.setString(index++, cors_allow_headers);
				stm.setString(index++, cors_allow_methods);
				stm.setString(index++, cors_allow_expose_headers);
				
				// responseCaching
				stm.setString(index++, response_cache_stato);
				if(response_cache_seconds!=null) {
					stm.setInt(index++, response_cache_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				if(response_cache_max_msg_size!=null) {
					stm.setLong(index++, response_cache_max_msg_size);
				}
				else {
					stm.setNull(index++, java.sql.Types.BIGINT);
				}
				stm.setInt(index++, response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setString(index++, response_cache_hash_url);
				stm.setString(index++, response_cache_hash_query);
				stm.setString(index++, response_cache_hash_query_list);
				stm.setString(index++, response_cache_hash_headers);
				stm.setString(index++, response_cache_hash_headers_list);
				stm.setString(index++, response_cache_hash_payload);
				
				//idaccordo
				stm.setLong(index++, aPD.getIdAccordo()!=null ? aPD.getIdAccordo() : -1L);
				stm.setLong(index++, aPD.getIdPortType() !=null ? aPD.getIdPortType() : -1L);
				
				// options
				stm.setString(index++, aPD.getOptions());
				
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + 
						DBUtils.formatSQLString(sqlQuery, nomePorta, descrizione, 
								idSoggettoErogatore, tipoSoggErogatore, nomeSoggErogatore, 
								idServizioPD, tipoServizio, nomeServizio, 
								idAzione, nomeAzione, modeAzione, patternAzione, nomePortaDeleganteAzione, getValue(forceInterfaceBased),
								autenticazione, aPD.getAutenticazioneOpzionale(),
								(gestioneToken!=null ? gestioneToken.getPolicy() : null),
								(gestioneToken!=null ? gestioneToken.getTokenOpzionale() : null),
								(gestioneToken!=null ? gestioneToken.getValidazione() : null),
								(gestioneToken!=null ? gestioneToken.getIntrospection() : null),
								(gestioneToken!=null ? gestioneToken.getUserInfo() : null),
								(gestioneToken!=null ? gestioneToken.getForward() : null),
								(gestioneToken!=null ? gestioneToken.getOptions() : null),
								((gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? gestioneToken.getAutenticazione().getIssuer() : null),
								((gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? gestioneToken.getAutenticazione().getClientId() : null),
								((gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? gestioneToken.getAutenticazione().getSubject() : null),
								((gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? gestioneToken.getAutenticazione().getUsername() : null),
								((gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? gestioneToken.getAutenticazione().getEmail() : null),
								autorizzazione, autorizzazioneXacmlPolicy, autorizzazioneContenuto,
								mtomMode_request, mtomMode_response,
								messageSecurityStatus, messageSecurityApplyMtom_request, messageSecurityApplyMtom_response, securityRequestMode, securityResponseMode,
								idSoggettoProprietario,
								aPD.getRicevutaAsincronaSimmetrica(),aPD.getRicevutaAsincronaAsimmetrica(),aPD.getIntegrazione(),
								(aPD.getCorrelazioneApplicativa()!=null ? aPD.getCorrelazioneApplicativa().getScadenza() : null),
								(aPD.getValidazioneContenutiApplicativi()!=null ? aPD.getValidazioneContenutiApplicativi().getStato() : null),
								(aPD.getValidazioneContenutiApplicativi()!=null ? aPD.getValidazioneContenutiApplicativi().getTipo() : null),
								(aPD.getValidazioneContenutiApplicativi()!=null ? aPD.getValidazioneContenutiApplicativi().getAcceptMtomMessage() : null),
								aPD.getAllegaBody(),aPD.getScartaBody(),aPD.getGestioneManifest(),aPD.getStateless(),aPD.getLocalForward(),
								(aPD!=null && aPD.getRuoli()!=null && aPD.getRuoli().getMatch()!=null ? 
										aPD.getRuoli().getMatch().getValue() : null),
								(aPD!=null && aPD.getScope()!=null && aPD.getScope().getStato()!=null ? 
										aPD.getScope().getStato() : null),
								(aPD!=null && aPD.getScope()!=null && aPD.getScope().getMatch()!=null ? 
										aPD.getScope().getMatch().getValue() : null),
								aPD.getRicercaPortaAzioneDelegata(),
								msg_diag_severita,tracciamento_esiti,
								aPD.getStato(),
								cors_stato, cors_tipo, cors_all_allow_origins, cors_allow_credentials, cors_allow_max_age, cors_allow_max_age_seconds,
								cors_allow_origins, cors_allow_headers, cors_allow_methods, cors_allow_expose_headers,
								response_cache_stato, response_cache_seconds, response_cache_max_msg_size, 
								(response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE),
								(response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE),
								(response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE),
								response_cache_hash_url, response_cache_hash_query, response_cache_hash_query_list, response_cache_hash_headers, response_cache_hash_headers_list, response_cache_hash_payload,
								aPD.getIdAccordo(),aPD.getIdPortType(),
								aPD.getOptions()));
				n = stm.executeUpdate();
				stm.close();

				DriverConfigurazioneDB_LIB.log.debug("Inserted " + n + " row(s).");

				// recupero l'id della porta delegata appena inserita
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereCondition("nome_porta = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idSoggettoProprietario);
				stm.setString(2, nomePorta);

				rs = stm.executeQuery();

				if (rs.next()) {
					idPortaDelegata = rs.getLong("id");
					aPD.setId(idPortaDelegata);
					rs.close();
					stm.close();
				} else {
					rs.close();
					stm.close();
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(CREATE)] Impossibile recuperare l'ID della PortaDelegata appena create.");
				}

				if(mtomProcessor!=null){
					
					MtomProcessorFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					i = 0;
					if(mtomProcessor.getRequestFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getPattern());
							stm.setString(4, reqParam.getContentType());
							stm.setInt(5, reqParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);

							stm.executeUpdate();
						}	
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom request flow con id=" + idPortaDelegata);

					MtomProcessorFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(mtomProcessor.getResponseFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getPattern());
							stm.setString(4, resParam.getContentType());
							stm.setInt(5, resParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom response flow con id=" + idPortaDelegata);
					
				}
				
				// se security abilitato setto la lista
				//if ((messageSecurity != null) && CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				// Devo settarli sempre se ci sono, in modo che lo switch abilitato-disabilitato funzioni
				if ((messageSecurity != null) )  {
					MessageSecurityFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					i = 0;
					if(messageSecurity.getRequestFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getValore());

							stm.executeUpdate();
						}	
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " request flow con id=" + idPortaDelegata);

					MessageSecurityFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(messageSecurity.getResponseFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getValore());
	
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " response flow con id=" + idPortaDelegata);
				}

				// la lista di correlazioni applicative contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				if (corrApp != null) {

					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQueryObject.addInsertField("riuso_id", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrApp.sizeElementoList(); i++) {
						cae = corrApp.getElemento(i);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.setString(6, DriverConfigurazioneDB_LIB.getValue(cae.getRiusoIdentificativo()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa con id=" + idPortaDelegata);
				}
				
				// la lista di correlazioni applicative contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				if (corrAppRisposta != null) {

					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaRispostaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrAppRisposta.sizeElementoList(); i++) {
						cae = corrAppRisposta.getElemento(i);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa risposta con id=" + idPortaDelegata);
				}
				
				// serviziapplicativi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);

				for (i = 0; i < aPD.sizeServizioApplicativoList(); i++) {
					PortaDelegataServizioApplicativo servizioApplicativo = aPD.getServizioApplicativo(i);
					String nomeSA = servizioApplicativo.getNome();
					//il tipo e il nome proprietario servizio applicativo sono gli stessi della porta delegata
					String nomeProprietarioSA = aPD.getNomeSoggettoProprietario(); //servizioApplicativo.getNomeSoggettoProprietario();
					String tipoProprietarioSA = aPD.getTipoSoggettoProprietario(); //servizioApplicativo.getTipoSoggettoProprietario();
					if (nomeSA == null || nomeSA.equals(""))
						throw new DriverConfigurazioneException("Nome del ServizioApplicativo associato non valido.");
					if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Nome Proprietario del ServizioApplicativo associato non valido.");
					if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Tipo Proprietario del ServizioApplicativo associato non valido.");

					long idSA = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);

					if (idSA <= 0)
						throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");

					stm.setLong(1, idPortaDelegata);
					stm.setLong(2, idSA);
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " associazioni ServizioApplicativo<->PortaDelegata associati alla PortaDelegata[" + idPortaDelegata + "]");

				// set prop autenticazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPD.sizeProprietaAutenticazioneList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutenticazione(i);
					stm.setLong(1, aPD.getId());
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SetProtocolPropAutenticazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// set prop autorizzazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPD.sizeProprietaAutorizzazioneList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutorizzazione(i);
					stm.setLong(1, aPD.getId());
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SetProtocolPropAutorizzazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// set prop autorizzazione contenuto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPD.sizeProprietaAutorizzazioneContenutoList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutorizzazioneContenuto(i);
					stm.setLong(1, aPD.getId());
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SetProtocolPropAutorizzazioneContenuto associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				// set prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPD.sizeProprietaList(); i++) {
					Proprieta propProtocollo = aPD.getProprieta(i);
					stm.setLong(1, aPD.getId());
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SetProtocolProp associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				// Ruoli
				n=0;
				if(aPD.getRuoli()!=null && aPD.getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPD.getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPD.getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPD.getId());
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " ruoli alla PortaDelegata[" + idPortaDelegata + "]");
				
				// Scope
				n=0;
				if(aPD.getScope()!=null && aPD.getScope().sizeScopeList()>0){
					for (int j = 0; j < aPD.getScope().sizeScopeList(); j++) {
						Scope scope = aPD.getScope().getScope(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_SCOPE);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("scope", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPD.getId());
						stm.setString(2, scope.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto scope[" + scope.getNome() + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " scope alla PortaDelegata[" + idPortaDelegata + "]");
				
				// Azioni
				n=0;
				if(aPD.getAzione()!=null && aPD.getAzione().sizeAzioneDelegataList()>0){
					for (int j = 0; j < aPD.getAzione().sizeAzioneDelegataList(); j++) {
						String azioneDelegata = aPD.getAzione().getAzioneDelegata(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AZIONI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("azione", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPD.getId());
						stm.setString(2, azioneDelegata);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto azione delegata [" + azioneDelegata + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " azioni delegate alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// Cache Regole
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CACHE_REGOLE);
						sqlQueryObject.addInsertField("id_porta", "?");
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							sqlQueryObject.addInsertField("status_min", "?");
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							sqlQueryObject.addInsertField("status_max", "?");
						}
						sqlQueryObject.addInsertField("fault", "?");
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							sqlQueryObject.addInsertField("cache_seconds", "?");
						}
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						int indexCR = 1;
						stm.setLong(indexCR++, aPD.getId());
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMin());
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMax());
						}
						stm.setInt(indexCR++, regola.getFault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							stm.setInt(indexCR++, regola.getCacheTimeoutSeconds());
						}
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta regola di cache alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " regole di cache alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// dumpConfigurazione
				CRUDDumpConfigurazione(type, con, aPD.getDump(), aPD.getId(), CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PD);
				
				// trasformazioni
				CRUDTrasformazioni(type, con, aPD.getTrasformazioni(), aPD.getId(), true);
				
				
				// extendedInfo
				i=0;
				if(aPD.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPD.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD, aPD.getExtendedInfo(i), CRUDType.CREATE);
						}
					}
				}
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " associazioni ExtendedInfo<->PortaDelegata associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				break;

			case UPDATE:
				//soggetto erogatore
				tipoSoggErogatore = (soggErogatore != null ? soggErogatore.getTipo() : null);
				nomeSoggErogatore = (soggErogatore != null ? soggErogatore.getNome() : null);
				if(tipoSoggErogatore==null || tipoSoggErogatore.equals("")) throw new DriverConfigurazioneException("Tipo Soggetto Erogatore non impostato.");
				if(nomeSoggErogatore==null || nomeSoggErogatore.equals("")) throw new DriverConfigurazioneException("Nome Soggetto Erogatore non impostato.");

				//servizio
				tipoServizio = (servizio != null ? servizio.getTipo() : null);
				nomeServizio = (servizio != null ? servizio.getNome() : null);
				versioneServizio = (servizio != null ? servizio.getVersione() : null);
				if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
				if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
				if(versioneServizio==null) throw new DriverConfigurazioneException("Versione Servizio non impostato.");

				//Azione
				nomeAzione = (azione != null ? azione.getNome() : null);
				patternAzione = (azione != null ? azione.getPattern() : null);
				modeAzione = (azione != null ? azione.getIdentificazione() : null);
				nomePortaDeleganteAzione = (azione != null ? azione.getNomePortaDelegante() : null);
				forceInterfaceBased = (azione != null ? azione.getForceInterfaceBased() : null);
				//Se il bean Azione nn e' presente allora non controllo nulla
				if(azione!=null){
					if(modeAzione==null || modeAzione.equals("")) 
						modeAzione = PortaDelegataAzioneIdentificazione.STATIC;
					switch (modeAzione) {
					case CONTENT_BASED:
					case URL_BASED:
					case HEADER_BASED:
						if(patternAzione==null || patternAzione.equals("")) throw new DriverConfigurazioneException("Pattern Azione non impostato.");
						nomeAzione=null;
						break;
					case DELEGATED_BY:
						if(nomePortaDeleganteAzione==null || nomePortaDeleganteAzione.equals("")) throw new DriverConfigurazioneException("Nome Porta Delegante Azione non impostata.");
						nomeAzione=null;
						break;
					case INPUT_BASED:
					case SOAP_ACTION_BASED:
					case INTERFACE_BASED:
						//nessun campo obbligatorio
						break;
					case STATIC:
						//se non c'e' l'id dell'azione ci deve essere il nome
						if(idAzione<=0){
							if(nomeAzione==null || nomeAzione.equals("")) throw new DriverConfigurazioneException("Nome Azione non impostato.");
						}
						patternAzione=null;
						break;
					default:
						break;
					}
				}

				// update
				String oldNomePD = null;
				if(aPD.getOldIDPortaDelegataForUpdate()!=null){
					oldNomePD = aPD.getOldIDPortaDelegataForUpdate().getNome();
				}
				DriverConfigurazioneDB_LIB.log.debug("OLD-PD["+oldNomePD+"] PD["+nomePorta+"]");
				
				if (oldNomePD == null || oldNomePD.equals("")){
					DriverConfigurazioneDB_LIB.log.debug("old nomePD is null, assegno: "+nomePorta);
					oldNomePD = nomePorta;
				}

				// if(aPD.getId() == null || aPD.getId()<=0) throw new
				// DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(UPDATE)]
				// L'id della porta e' necessario per effettuare l'UPDATE.");
				idPortaDelegata = DBUtils.getIdPortaDelegata(oldNomePD, con, DriverConfigurazioneDB_LIB.tipoDB);
				// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(idPortaDelegata<=0) {
					idPortaDelegata = DBUtils.getIdPortaDelegata(oldNomePD, con, DriverConfigurazioneDB_LIB.tipoDB);
				}
				if(idPortaDelegata<=0) 
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(UPDATE)] id porta delegata non trovato nome["+oldNomePD+"]");
				aPD.setId(idPortaDelegata);
				
				
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addUpdateField("nome_porta", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("id_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("tipo_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("nome_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("id_servizio", "?");
				sqlQueryObject.addUpdateField("tipo_servizio", "?");
				sqlQueryObject.addUpdateField("nome_servizio", "?");
				sqlQueryObject.addUpdateField("versione_servizio", "?");
				sqlQueryObject.addUpdateField("id_azione", "?");
				sqlQueryObject.addUpdateField("nome_azione", "?");
				sqlQueryObject.addUpdateField("mode_azione", "?");
				sqlQueryObject.addUpdateField("pattern_azione", "?");
				sqlQueryObject.addUpdateField("nome_porta_delegante_azione", "?");
				sqlQueryObject.addUpdateField("force_interface_based_azione", "?");
				sqlQueryObject.addUpdateField("autenticazione", "?");
				sqlQueryObject.addUpdateField("autenticazione_opzionale", "?");
				sqlQueryObject.addUpdateField("token_policy", "?");
				sqlQueryObject.addUpdateField("token_opzionale", "?");
				sqlQueryObject.addUpdateField("token_validazione", "?");
				sqlQueryObject.addUpdateField("token_introspection", "?");
				sqlQueryObject.addUpdateField("token_user_info", "?");
				sqlQueryObject.addUpdateField("token_forward", "?");
				sqlQueryObject.addUpdateField("token_options", "?");
				sqlQueryObject.addUpdateField("token_authn_issuer", "?");
				sqlQueryObject.addUpdateField("token_authn_client_id", "?");
				sqlQueryObject.addUpdateField("token_authn_subject", "?");
				sqlQueryObject.addUpdateField("token_authn_username", "?");
				sqlQueryObject.addUpdateField("token_authn_email", "?");
				sqlQueryObject.addUpdateField("autorizzazione", "?");
				sqlQueryObject.addUpdateField("autorizzazione_xacml", "?");
				sqlQueryObject.addUpdateField("autorizzazione_contenuto", "?");
				sqlQueryObject.addUpdateField("mtom_request_mode", "?");
				sqlQueryObject.addUpdateField("mtom_response_mode", "?");
				sqlQueryObject.addUpdateField("security", "?");
				sqlQueryObject.addUpdateField("security_mtom_req", "?");
				sqlQueryObject.addUpdateField("security_mtom_res", "?");
				sqlQueryObject.addUpdateField("security_request_mode", "?");
				sqlQueryObject.addUpdateField("security_response_mode", "?");
				sqlQueryObject.addUpdateField("id_soggetto", "?");
				sqlQueryObject.addUpdateField("ricevuta_asincrona_sim", "?");
				sqlQueryObject.addUpdateField("ricevuta_asincrona_asim", "?");
				sqlQueryObject.addUpdateField("integrazione", "?");
				sqlQueryObject.addUpdateField("scadenza_correlazione_appl", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_stato", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_mtom", "?");				
				sqlQueryObject.addUpdateField("allega_body", "?");
				sqlQueryObject.addUpdateField("scarta_body", "?");
				sqlQueryObject.addUpdateField("gestione_manifest", "?");
				sqlQueryObject.addUpdateField("stateless", "?");
				sqlQueryObject.addUpdateField("local_forward", "?");
				sqlQueryObject.addUpdateField("local_forward_pa", "?");
				sqlQueryObject.addUpdateField("ruoli_match", "?");
				sqlQueryObject.addUpdateField("scope_stato", "?");
				sqlQueryObject.addUpdateField("scope_match", "?");
				sqlQueryObject.addUpdateField("ricerca_porta_azione_delegata", "?");
				sqlQueryObject.addUpdateField("msg_diag_severita", "?");
				sqlQueryObject.addUpdateField("tracciamento_esiti", "?");
				sqlQueryObject.addUpdateField("stato", "?");
				// cors
				sqlQueryObject.addUpdateField("cors_stato", "?");
				sqlQueryObject.addUpdateField("cors_tipo", "?");
				sqlQueryObject.addUpdateField("cors_all_allow_origins", "?");
				sqlQueryObject.addUpdateField("cors_allow_credentials", "?");
				sqlQueryObject.addUpdateField("cors_allow_max_age", "?");
				sqlQueryObject.addUpdateField("cors_allow_max_age_seconds", "?");
				sqlQueryObject.addUpdateField("cors_allow_origins", "?");
				sqlQueryObject.addUpdateField("cors_allow_headers", "?");
				sqlQueryObject.addUpdateField("cors_allow_methods", "?");
				sqlQueryObject.addUpdateField("cors_allow_expose_headers", "?");
				// responseCaching
				sqlQueryObject.addUpdateField("response_cache_stato", "?");
				sqlQueryObject.addUpdateField("response_cache_seconds", "?");
				sqlQueryObject.addUpdateField("response_cache_max_msg_size", "?");
				sqlQueryObject.addUpdateField("response_cache_control_nocache", "?");
				sqlQueryObject.addUpdateField("response_cache_control_maxage", "?");
				sqlQueryObject.addUpdateField("response_cache_control_nostore", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_url", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_query", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_query_list", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_headers", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_hdr_list", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_payload", "?");
				// id
				sqlQueryObject.addUpdateField("id_accordo", "?");
				sqlQueryObject.addUpdateField("id_port_type", "?");
				// options
				sqlQueryObject.addUpdateField("options", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = con.prepareStatement(sqlQuery);

				index = 1;
				stm.setString(index++, nomePorta);
				stm.setString(index++, descrizione);
				stm.setLong(index++, idSoggettoErogatore);
				stm.setString(index++, tipoSoggErogatore);
				stm.setString(index++, nomeSoggErogatore);
				stm.setLong(index++, idServizioPD);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setInt(index++, versioneServizio);
				stm.setLong(index++, idAzione);
				stm.setString(index++, nomeAzione);
				if(modeAzione!=null)
					stm.setString(index++, modeAzione.toString());
				else
					stm.setString(index++, null);
				stm.setString(index++, patternAzione);
				stm.setString(index++, nomePortaDeleganteAzione);
				stm.setString(index++, getValue(forceInterfaceBased));
				// autenticazione
				stm.setString(index++, autenticazione);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getAutenticazioneOpzionale()) : null);
				// token
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getPolicy() : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getTokenOpzionale()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getValidazione()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getIntrospection()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getUserInfo()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getForward()) : null);
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getOptions() : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getIssuer()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getClientId()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getSubject()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getUsername()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getEmail()) : null);
				// autorizzazione
				stm.setString(index++, autorizzazione);
				stm.setString(index++, autorizzazioneXacmlPolicy);
				stm.setString(index++, autorizzazioneContenuto);
				// mtom
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_response));
				stm.setString(index++, securityRequestMode);
				stm.setString(index++, securityResponseMode);
				// soggettoProprietario
				stm.setLong(index++, idSoggettoProprietario);
				//ricevuta asincrona_asimmetrica/simmetrica
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaSimmetrica()));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaAsimmetrica()));
				//integrazione
				stm.setString(index++, aPD.getIntegrazione());
				//scadenza correlazione applicativa
				stm.setString(index++, aPD.getCorrelazioneApplicativa()!=null ? aPD.getCorrelazioneApplicativa().getScadenza() : null);
				//validazione xsd
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getStato()) : null);
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getTipo()) : null);
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) : null);
				// InvocazionePorta: funzionalita' attachment
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getAllegaBody()) : null);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getScartaBody()) : null);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getGestioneManifest()) : null);
				// Stateless
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getStateless()) : null);
				// LocalForward
				stm.setString(index++, aPD!=null && aPD.getLocalForward()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getLocalForward().getStato()) : null);
				stm.setString(index++, aPD!=null && aPD.getLocalForward()!=null ? aPD.getLocalForward().getPortaApplicativa() : null);
				// Ruoli
				stm.setString(index++, aPD!=null && aPD.getRuoli()!=null && aPD.getRuoli().getMatch()!=null ? 
						aPD.getRuoli().getMatch().getValue() : null);
				// Scope
				stm.setString(index++, aPD!=null && aPD.getScope()!=null && aPD.getScope().getStato()!=null ? 
						DriverConfigurazioneDB_LIB.getValue(aPD.getScope().getStato()) : null);
				stm.setString(index++, aPD!=null && aPD.getScope()!=null && aPD.getScope().getMatch()!=null ? 
						aPD.getScope().getMatch().getValue() : null);
				// RicercaPortaAzioneDelegata
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getRicercaPortaAzioneDelegata()) : null);
				// Tracciamento
				stm.setString(index++, msg_diag_severita);
				stm.setString(index++, tracciamento_esiti);
				// Stato
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getStato()) : null);
				// cors
				stm.setString(index++, cors_stato);
				stm.setString(index++, cors_tipo);
				stm.setString(index++, cors_all_allow_origins);
				stm.setString(index++, cors_allow_credentials);
				stm.setInt(index++, cors_allow_max_age);
				if(cors_allow_max_age_seconds!=null) {
					stm.setInt(index++, cors_allow_max_age_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				stm.setString(index++, cors_allow_origins);
				stm.setString(index++, cors_allow_headers);
				stm.setString(index++, cors_allow_methods);
				stm.setString(index++, cors_allow_expose_headers);
				
				// responseCaching
				stm.setString(index++, response_cache_stato);
				if(response_cache_seconds!=null) {
					stm.setInt(index++, response_cache_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				if(response_cache_max_msg_size!=null) {
					stm.setLong(index++, response_cache_max_msg_size);
				}
				else {
					stm.setNull(index++, java.sql.Types.BIGINT);
				}
				stm.setInt(index++, response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setString(index++, response_cache_hash_url);
				stm.setString(index++, response_cache_hash_query);
				stm.setString(index++, response_cache_hash_query_list);
				stm.setString(index++, response_cache_hash_headers);
				stm.setString(index++, response_cache_hash_headers_list);
				stm.setString(index++, response_cache_hash_payload);
				//idAccordo
				stm.setLong(index++,aPD.getIdAccordo() != null ? aPD.getIdAccordo() : -1L);
				stm.setLong(index++, aPD.getIdPortType() != null ? aPD.getIdPortType() : -1L);
				// options
				stm.setString(index++, aPD.getOptions());
				
				// where
				stm.setLong(index++, idPortaDelegata);

				//DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, nomePorta, descrizione, 
				// soggErogatore.getId(), soggErogatore.getTipo(), soggErogatore.getNome(), soggErogatore.getIdentificazione(), servizio.getId(), 
				// servizio.getTipo(), servizio.getNome(), servizio.getIdentificazione(), azione.getId(), azione.getNome(), azione.getIdentificazione(), 
				// autenticazione, autorizzazione, messageSecurityStatus, idSoggettoProprietario, idPortaDelegata));

				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Updated " + n + " row(s).");

				
				
				// mtom
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				stm.executeUpdate();
				stm.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				stm.executeUpdate();
				stm.close();
				
				if(mtomProcessor!=null){
				
					MtomProcessorFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
	
					i = 0;
					if(mtomProcessor.getRequestFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getPattern());
							stm.setString(4, reqParam.getContentType());
							stm.setInt(5, reqParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}	
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom request flow con id=" + idPortaDelegata);
	
					MtomProcessorFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(mtomProcessor.getResponseFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getPattern());
							stm.setString(4, resParam.getContentType());
							stm.setInt(5, resParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom response flow con id=" + idPortaDelegata);
					
				}
				
				
				
				// se security abilitato setto la lista
				// la lista contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				//if ((messageSecurity != null) && CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				// Devo settarli sempre se ci sono, in modo che lo switch abilitato-disabilitato funzioni
				if ((messageSecurity != null) )  {

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaDelegata);
					stm.executeUpdate();
					stm.close();

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaDelegata);
					stm.executeUpdate();
					stm.close();

					//inserisco i valori presenti nella lista 
					MessageSecurityFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					i = 0;
					if(messageSecurity.getRequestFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " request flow con id=" + idPortaDelegata);

					MessageSecurityFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(messageSecurity.getResponseFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " response flow con id=" + idPortaDelegata);
				}


				// la lista di correlazioni applicative contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				stm.executeUpdate();
				stm.close();
				
				if (corrApp != null) {
					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQueryObject.addInsertField("riuso_id", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrApp.sizeElementoList(); i++) {
						cae = corrApp.getElemento(i);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.setString(6, DriverConfigurazioneDB_LIB.getValue(cae.getRiusoIdentificativo()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa con id=" + idPortaDelegata);
				}

				// la lista di correlazioni applicative risposta contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				stm.executeUpdate();
				stm.close();
				
				if (corrAppRisposta != null) {
					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaRispostaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrAppRisposta.sizeElementoList(); i++) {
						cae = corrAppRisposta.getElemento(i);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa risposta con id=" + idPortaDelegata);
				}
				
				/*Sincronizzazione servizi applicativi*/
				//la lista dei servizi applicativi passata contiene tutti e soli i servizi applicativi necessari
				//quindi nel db devono essere presenti tutti e solo quelli presenti nella lista
				//se la lista e' vuota allora i servizi applicativi vanno cancellati

				//TODO possibile ottimizzazione in termini di tempo
				//cancello i servizi applicativi associati alla porta e inserisco tutti e soli quelli presenti in lista
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" servizi applicativi associati alla Porta Delegata "+idPortaDelegata);
				//scrivo la lista nel db
				n=0;
				for (i = 0; i < aPD.sizeServizioApplicativoList(); i++) {
					PortaDelegataServizioApplicativo servizioApplicativo = aPD.getServizioApplicativo(i);

					String nomeSA = servizioApplicativo.getNome();
					//il tipo e il nome proprietario servizio applicativo sono gli stessi della porta delegata
					//controllo se sono settati gli old, perche potrei essere in un caso di update
					String nomeProprietarioSA = aPD.getNomeSoggettoProprietario(); 
					String tipoProprietarioSA = aPD.getTipoSoggettoProprietario(); 

					if (nomeSA == null || nomeSA.equals(""))
						throw new DriverConfigurazioneException("Nome del ServizioApplicativo associato non valido.");
					if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Nome Proprietario del ServizioApplicativo associato non valido.");
					if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Tipo Proprietario del ServizioApplicativo associato non valido.");

					long idSA = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);

					if (idSA <= 0)
						throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_SA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaDelegata);
					stm.setLong(2, idSA);

					stm.executeUpdate();
					stm.close();
					n++;
					DriverConfigurazioneDB_LIB.log.debug("Aggiunta associazione PortaDelegata<->ServizioApplicativo [" + idPortaDelegata + "]<->[" + idSA + "]");
				}

				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " associazioni PortaDelegata<->ServizioApplicativo associati alla PortaDelegata[" + idPortaDelegata + "]");

				
				
				/*Proprieta Autenticazione associate alla Porta Delegata*/

				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta di autenticazione associate alla Porta Delegata "+idPortaDelegata);
				// set prop
				int newProps = 0;
				for (i = 0; i < aPD.sizeProprietaAutenticazioneList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutenticazione(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaDelegata);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolProp Autenticazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				/*Proprieta Autorizzazione associate alla Porta Delegata*/

				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta di autorizzazione associate alla Porta Delegata "+idPortaDelegata);
				// set prop
				newProps = 0;
				for (i = 0; i < aPD.sizeProprietaAutorizzazioneList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutorizzazione(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaDelegata);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolProp Autorizzazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				/*Proprieta Autorizzazione Contenuti associate alla Porta Delegata*/

				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta di autorizzazione contenuti associate alla Porta Delegata "+idPortaDelegata);
				// set prop
				newProps = 0;
				for (i = 0; i < aPD.sizeProprietaAutorizzazioneContenutoList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutorizzazioneContenuto(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaDelegata);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolProp AutorizzazioneContenuto associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				
				/*Proprieta associate alla Porta Delegata*/

				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta associate alla Porta Delegata "+idPortaDelegata);
				// set prop
				newProps = 0;
				for (i = 0; i < aPD.sizeProprietaList(); i++) {
					Proprieta propProtocollo = aPD.getProprieta(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaDelegata);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolProp associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// Ruoli
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" ruoli associati alla Porta Delegata "+idPortaDelegata);
				
				n=0;
				if(aPD.getRuoli()!=null && aPD.getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPD.getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPD.getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " ruoli alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				// Scope
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" scope associati alla Porta Delegata "+idPortaDelegata);
				
				n=0;
				if(aPD.getScope()!=null && aPD.getScope().sizeScopeList()>0){
					for (int j = 0; j < aPD.getScope().sizeScopeList(); j++) {
						Scope scope = aPD.getScope().getScope(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_SCOPE);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("scope", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, scope.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto scope[" + scope.getNome() + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " scope alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				// Azioni
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AZIONI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" azioni delegate associate alla Porta Delegata "+idPortaDelegata);
				
				n=0;
				if(aPD.getAzione()!=null && aPD.getAzione().sizeAzioneDelegataList()>0){
					for (int j = 0; j < aPD.getAzione().sizeAzioneDelegataList(); j++) {
						String azioneDelegata = aPD.getAzione().getAzioneDelegata(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AZIONI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("azione", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, azioneDelegata);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta azione delegata [" + azioneDelegata + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " azioni delegate alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				// Cache Regole
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CACHE_REGOLE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" regole di cache associate alla Porta Delegata "+idPortaDelegata);
				
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CACHE_REGOLE);
						sqlQueryObject.addInsertField("id_porta", "?");
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							sqlQueryObject.addInsertField("status_min", "?");
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							sqlQueryObject.addInsertField("status_max", "?");
						}
						sqlQueryObject.addInsertField("fault", "?");
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							sqlQueryObject.addInsertField("cache_seconds", "?");
						}
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						int indexCR = 1;
						stm.setLong(indexCR++, idPortaDelegata);
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMin());
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMax());
						}
						stm.setInt(indexCR++, regola.getFault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							stm.setInt(indexCR++, regola.getCacheTimeoutSeconds());
						}
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta regola di cache alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " regole di cache alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				// dumpConfigurazione
				CRUDDumpConfigurazione(type, con, aPD.getDump(), idPortaDelegata, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PD);
				
				
				// trasformazioni
				CRUDTrasformazioni(type, con, aPD.getTrasformazioni(), idPortaDelegata, true);
				
				
				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD, CRUDType.UPDATE);
				}
				
				i=0;
				if(aPD.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPD.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD, aPD.getExtendedInfo(i), CRUDType.UPDATE);
						}
					}
				}
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + i + " associazioni ExtendedInfo<->PortaDelegata associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				break;

			case DELETE:
				// delete
				idPortaDelegata = DBUtils.getIdPortaDelegata(nomePorta, con, DriverConfigurazioneDB_LIB.tipoDB);
				if (idPortaDelegata <= 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(DELETE)] Non e' stato possibile recuperare l'id della Porta Delegata, necessario per effettuare la DELETE.");

				// dumpConfigurazione
				CRUDDumpConfigurazione(type, con, aPD.getDump(), idPortaDelegata, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PD);
				
				// trasformazioni
				CRUDTrasformazioni(type, con, aPD.getTrasformazioni(), idPortaDelegata, true);
				
				// Cache Regole
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CACHE_REGOLE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" regole di cache associate alla Porta Delegata "+idPortaDelegata);
				
				// azioni delegate
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AZIONI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" azioni delegate associate alla Porta Delegata "+idPortaDelegata);
				
				// ruoli
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" ruoli associati alla Porta Delegata "+idPortaDelegata);
				
				// scope
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" scope associati alla Porta Delegata "+idPortaDelegata);
				
				// mtom
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " request flow con id=" + idPortaDelegata);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " response flow con id=" + idPortaDelegata);

				
				// message security
				//if ( CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " request flow con id=" + idPortaDelegata);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " response flow con id=" + idPortaDelegata);
				//}

				// servizi applicativi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " associazioni PortaDelegata<->ServizioApplicativo associati alla PortaDelegata[" + idPortaDelegata + "]");

				// cancello anche le flow di request/response associate a questa
				// porta applicativa
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " security_request flow associate alla PortaDelegata[" + idPortaDelegata + "]");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " security_response flow associate alla PortaDelegata[" + idPortaDelegata + "]");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);

				n = stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " correlazione associate alla PortaDelegata[" + idPortaDelegata + "]");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);

				n = stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " correlazione per la rispsota associate alla PortaDelegata[" + idPortaDelegata + "]");

				// cancello le prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolProp associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				// cancello le prop di autorizzazione contenuti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolPropAutorizzazioneContenuto associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				// cancello le prop di autorizzazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolPropAutorizzazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// cancello le prop di autenticazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolPropAutenticazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD, CRUDType.DELETE);
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " row(s).");


				break;
			}
			return idPortaDelegata;
		} catch (DriverConfigurazioneException e) {
			throw e;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata] SQLException : " + se.getMessage(),se);
		} catch (Exception e) {
			throw new DriverConfigurazioneException("Errore durante operazione("+type+") CRUDPortaDelegata.",e);
		}finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}

	/**
	 * 
	 * @param type
	 * @param aSA
	 * @param con
	 * @return id
	 * @throws DriverConfigurazioneException
	 */
	public static long CRUDServizioApplicativo(int type, ServizioApplicativo aSA, Connection con) throws DriverConfigurazioneException {
		if (aSA == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo] Servizio Applicativo non valido.");

		String nomeSA = aSA.getNome();
		String tipoProprietario = aSA.getTipoSoggettoProprietario();
		String nomeProprietario = aSA.getNomeSoggettoProprietario();

		if (nomeSA == null || nomeSA.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo] Nome Servizio Applicativo non valido.");
		if (tipoProprietario == null || tipoProprietario.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo] Tipo Proprietario Servizio Applicativo non valido.");
		if (nomeProprietario == null || nomeProprietario.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo] Nome Proprietario Servizio Applicativo non valido.");

		PreparedStatement stm = null;
		String sqlQuery;
		ResultSet rs = null;
		int n = 0;

		try {
			String tipoSA = aSA.getTipo();
			int useAsClient = aSA.getUseAsClient() ? CostantiDB.TRUE : CostantiDB.FALSE;
			String descrizione = aSA.getDescrizione();
			DriverConfigurazioneDB_LIB.log.debug("get ID Soggetto con tipo["+tipoProprietario+"] e nome["+nomeProprietario+"]");
			long idProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
			DriverConfigurazioneDB_LIB.log.debug("get ID Soggetto con tipo["+tipoProprietario+"] e nome["+nomeProprietario+"] : "+idProprietario);
			InvocazionePorta invPorta = aSA.getInvocazionePorta();
			InvocazioneServizio invServizio = aSA.getInvocazioneServizio();
			RispostaAsincrona ricezione = aSA.getRispostaAsincrona();

			Connettore connettoreRisp = null;
			Connettore connettoreInv = null;
			long idConnettoreRisp = 0;
			long idConnettoreInv = 0;
			long idServizioApplicativo = 0;
			Credenziali credenzialiInvocazionePorta = null;
			InvocazioneCredenziali credenzialiInvocazione = null;
			InvocazionePortaGestioneErrore gestErr = null;
			String fault = null;

			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("as_client", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("sbustamentorisp", "?");
				sqlQueryObject.addInsertField("sbustamento_protocol_info_risp", "?");
				sqlQueryObject.addInsertField("getmsgrisp", "?");
				sqlQueryObject.addInsertField("tipoauthrisp", "?");
				sqlQueryObject.addInsertField("utenterisp", "?");
				sqlQueryObject.addInsertField("passwordrisp", "?");
				sqlQueryObject.addInsertField("id_connettore_risp", "?");
				sqlQueryObject.addInsertField("sbustamentoinv", "?");
				sqlQueryObject.addInsertField("sbustamento_protocol_info_inv", "?");
				sqlQueryObject.addInsertField("getmsginv", "?");
				sqlQueryObject.addInsertField("tipoauthinv", "?");
				sqlQueryObject.addInsertField("utenteinv", "?");
				sqlQueryObject.addInsertField("passwordinv", "?");
				sqlQueryObject.addInsertField("id_connettore_inv", "?");
				sqlQueryObject.addInsertField("id_soggetto", "?");
				sqlQueryObject.addInsertField("fault", "?");
				sqlQueryObject.addInsertField("tipoauth", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				sqlQueryObject.addInsertField("subject", "?");
				sqlQueryObject.addInsertField("cn_subject", "?");
				sqlQueryObject.addInsertField("issuer", "?");
				sqlQueryObject.addInsertField("cn_issuer", "?");
				sqlQueryObject.addInsertField("certificate", "?");
				sqlQueryObject.addInsertField("cert_strict_verification", "?");
				sqlQueryObject.addInsertField("invio_x_rif_inv", "?");
				sqlQueryObject.addInsertField("risposta_x_rif_inv", "?");
				sqlQueryObject.addInsertField("invio_x_rif", "?");
				sqlQueryObject.addInsertField("invio_x_rif_risp", "?");
				sqlQueryObject.addInsertField("risposta_x_rif_risp", "?");
				sqlQueryObject.addInsertField("sbustamento_protocol_info", "?");
				sqlQueryObject.addInsertField("fault_actor", "?");
				sqlQueryObject.addInsertField("generic_fault_code", "?");
				sqlQueryObject.addInsertField("prefix_fault_code", "?");
				sqlQueryObject.addInsertField("tipologia_fruizione", "?");
				sqlQueryObject.addInsertField("tipologia_erogazione", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);

				// creo i connettori, ma disabilitati

				// connettore risp
				//il nome del connettore deve essere univoco Connettore[RISP | INV]_nomeSA+tipoSoggetto+nomeSoggetto
				connettoreRisp = new Connettore();
				connettoreRisp.setNome("ConnettoreRISP_" + aSA.getNome()+"_"+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario());
				connettoreRisp.setTipo(TipiConnettore.DISABILITATO.getNome());
				//Creo il connettore disabilitato
				idConnettoreRisp = DriverConfigurazioneDB_LIB.CRUDConnettore(1, connettoreRisp, con);
				//Se il connettore mi era stato passato allora devo aggiornare il connettore con i dati giusti
				if(ricezione!=null && ricezione.getConnettore()!=null){
					Connettore connettore= ricezione.getConnettore();
					//setto l'id del connettore e il nome che aveva prima
					connettore.setId(idConnettoreRisp);
					connettore.setNome(connettoreRisp.getNome());//il nome DEVE essere quello creato in precedenza per assicurarsi che sia univoco
					DriverConfigurazioneDB_LIB.CRUDConnettore(CostantiDB.UPDATE, connettore, con);
				}

				// connettore inv
				connettoreInv = new Connettore();
				// connettoreInv.addProperty(prop);
				connettoreInv.setNome("ConnettoreINV_" + aSA.getNome()+"_"+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario());
				connettoreInv.setTipo(TipiConnettore.DISABILITATO.getNome());
				idConnettoreInv = DriverConfigurazioneDB_LIB.CRUDConnettore(1, connettoreInv, con);

				//setto i valori corretti del connettore se mi era stato passato
				if(invServizio!=null && invServizio.getConnettore()!=null){
					Connettore connettore = invServizio.getConnettore();
					connettore.setId(idConnettoreInv);
					connettore.setNome(connettoreInv.getNome());//il nome DEVE essere quello creato in precedenza per assicurarsi che sia univoco
					DriverConfigurazioneDB_LIB.CRUDConnettore(CostantiDB.UPDATE, connettore, con);
				}

				int index = 1;
				
				stm.setString(index++, nomeSA);
				stm.setString(index++, tipoSA);
				stm.setInt(index++, useAsClient);
				stm.setString(index++, descrizione);

				// RicezioneRisposta
				stm.setInt(index++, (ricezione != null && (CostantiConfigurazione.ABILITATO.equals(ricezione.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (ricezione != null && (!CostantiConfigurazione.DISABILITATO.equals(ricezione.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getGetMessage()) : null);
				// setto credenziali risp
				credenzialiInvocazione = ricezione != null ? ricezione.getCredenziali() : null;
				stm.setString(index++, (ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getAutenticazione()) : null)); //l'autenticazione e' quella della risposta asincrona
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getUser() : null));
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getPassword() : null));
				// setto idconnettore risp
				stm.setLong(index++, idConnettoreRisp);

				// InvocazioneServizio
				stm.setInt(index++, (invServizio != null && (CostantiConfigurazione.ABILITATO.equals(invServizio.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (invServizio != null && (!CostantiConfigurazione.DISABILITATO.equals(invServizio.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getGetMessage()) : null);
				// setto credenziali inv
				credenzialiInvocazione = invServizio != null ? invServizio.getCredenziali() : null;
				stm.setString(index++, (invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getAutenticazione()) : null));//l'autenticazione e' quella dell invocazione servizio
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getUser() : null));
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getPassword() : null));
				// setto idconnettore inv
				stm.setLong(index++, idConnettoreInv);

				// idsoggetto proprietario
				stm.setLong(index++, idProprietario);

				// InvocazionePorta
				gestErr = invPorta != null ? invPorta.getGestioneErrore() : null;
				fault = (gestErr != null ? DriverConfigurazioneDB_LIB.getValue(gestErr.getFault()) : null);
				stm.setString(index++, fault);
				// setto credenziali invocaizone porta
				// per il momento c'e' soltato una credenziale,quindi un solo
				// oggetto nella lista
				credenzialiInvocazionePorta = (invPorta != null && invPorta.sizeCredenzialiList() > 0 ? invPorta.getCredenziali(0) : null);
				stm.setString(index++, (credenzialiInvocazionePorta != null ? DriverConfigurazioneDB_LIB.getValue(credenzialiInvocazionePorta.getTipo()) : null));
				stm.setString(index++, (credenzialiInvocazionePorta != null ? credenzialiInvocazionePorta.getUser() : null));
				stm.setString(index++, (credenzialiInvocazionePorta != null ? credenzialiInvocazionePorta.getPassword() : null));
				
				String subject = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getSubject()!=null && !"".equals(credenzialiInvocazionePorta.getSubject()))
					subject = credenzialiInvocazionePorta.getSubject();
				stm.setString(index++, (subject != null ? CertificateUtils.formatPrincipal(subject, PrincipalType.subject) : null));
				String subjectCN = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCnSubject()!=null && !"".equals(credenzialiInvocazionePorta.getCnSubject()))
					subjectCN = credenzialiInvocazionePorta.getCnSubject();
				stm.setString(index++, subjectCN);
				
				String issuer = null;
				if(credenzialiInvocazionePorta != null && org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(credenzialiInvocazionePorta.getTipo())) {
					stm.setString(index++, CostantiDB.getISSUER_APIKEY(credenzialiInvocazionePorta.isAppId()));
				}
				else {
					if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getIssuer()!=null && !"".equals(credenzialiInvocazionePorta.getIssuer()))
						issuer = credenzialiInvocazionePorta.getIssuer();
					stm.setString(index++, (issuer != null ? CertificateUtils.formatPrincipal(issuer, PrincipalType.issuer) : null));
				}
				String issuerCN = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCnIssuer()!=null && !"".equals(credenzialiInvocazionePorta.getCnIssuer()))
					issuerCN = credenzialiInvocazionePorta.getCnIssuer();
				stm.setString(index++, issuerCN);
				
				byte [] certificate = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCertificate()!=null) {
					certificate = credenzialiInvocazionePorta.getCertificate();
				}
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDB);
				jdbcAdapter.setBinaryData(stm, index++, certificate);
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.isCertificateStrictVerification()) {
					stm.setInt(index++, CostantiDB.TRUE);
				}				
				else {
					stm.setInt(index++, CostantiDB.FALSE);
				}

				// aggiungo gestione invio/risposta per riferimento
				// invocazione servizio
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getInvioPerRiferimento()) : null);
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getRispostaPerRiferimento()) : null);
				// invocazione porta
				stm.setString(index++, invPorta != null ? DriverConfigurazioneDB_LIB.getValue(invPorta.getInvioPerRiferimento()) : null);
				// ricezione risposta
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getInvioPerRiferimento()) : null);
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getRispostaPerRiferimento()) : null);
				// sbustamento info protocolo
				stm.setInt(index++, (invPorta != null && (!CostantiConfigurazione.DISABILITATO.equals(invPorta.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				//Invocazione Porta : fault_actor, generic_fault_code, prefix_fault_code
				stm.setString(index++, gestErr!=null ? gestErr.getFaultActor() : null);
				stm.setString(index++, gestErr!=null ? DriverConfigurazioneDB_LIB.getValue(gestErr.getGenericFaultCode()) : null);
				stm.setString(index++, gestErr!=null ? gestErr.getPrefixFaultCode() : null);
								
				//tipologia erogazione/fruizione
				stm.setString(index++, aSA.getTipologiaFruizione()!=null ? TipologiaFruizione.valueOf(aSA.getTipologiaFruizione().toUpperCase()).toString() : TipologiaFruizione.DISABILITATO.toString());
				stm.setString(index++, aSA.getTipologiaErogazione()!=null ? TipologiaErogazione.valueOf(aSA.getTipologiaErogazione().toUpperCase()).toString() : TipologiaErogazione.DISABILITATO.toString());
				
				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + n + " row(s)");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idProprietario);
				stm.setString(2, nomeSA);

				rs = stm.executeQuery();

				if (rs.next()) {
					idServizioApplicativo = rs.getLong("id");
					aSA.setId(idServizioApplicativo);
					rs.close();
					stm.close();
				} else {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo(CREATE)] Impossibile trovare il servizio appena creato.");
				}
				
				// GestioneErrore
				if(aSA.getRispostaAsincrona()!=null && aSA.getRispostaAsincrona().getGestioneErrore()!=null){
					
					DriverConfigurazioneDB_LIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.CREATE, 
							aSA.getRispostaAsincrona().getGestioneErrore(), idProprietario, idServizioApplicativo, false, con);
					
					ISQLQueryObject sqlQueryObjectUpdate = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObjectUpdate.addUpdateTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObjectUpdate.addUpdateField("id_gestione_errore_risp", "?");
					sqlQueryObjectUpdate.addWhereCondition("id = ?");
					stm = con.prepareStatement(sqlQueryObjectUpdate.createSQLUpdate());
					stm.setLong(1, aSA.getRispostaAsincrona().getGestioneErrore().getId());
					stm.setLong(2, idServizioApplicativo);
					stm.executeUpdate();
					
				}
				
				// GestioneErrore
				if(aSA.getInvocazioneServizio()!=null && aSA.getInvocazioneServizio().getGestioneErrore()!=null){
					
					DriverConfigurazioneDB_LIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.CREATE, 
							aSA.getInvocazioneServizio().getGestioneErrore(), idProprietario, idServizioApplicativo, true, con);
					
					ISQLQueryObject sqlQueryObjectUpdate = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObjectUpdate.addUpdateTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObjectUpdate.addUpdateField("id_gestione_errore_inv", "?");
					sqlQueryObjectUpdate.addWhereCondition("id = ?");
					stm = con.prepareStatement(sqlQueryObjectUpdate.createSQLUpdate());
					stm.setLong(1, aSA.getInvocazioneServizio().getGestioneErrore().getId());
					stm.setLong(2, idServizioApplicativo);
					stm.executeUpdate();
					
				}
				
				// Ruoli
				n=0;
				if(invPorta!=null && invPorta.getRuoli()!=null && invPorta.getRuoli().sizeRuoloList()>0){
					for (int i = 0; i < invPorta.getRuoli().sizeRuoloList(); i++) {
						Ruolo ruolo = invPorta.getRuoli().getRuolo(i);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
						sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aSA.getId());
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] al servizioApplicativo "+idServizioApplicativo);
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " ruoli al servizioApplicativo "+idServizioApplicativo);
				
				
				// ProtocolProperties
				DriverConfigurazioneDB_LIB.CRUDProtocolProperty(CostantiDB.CREATE, aSA.getProtocolPropertyList(), 
						idServizioApplicativo, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO, con, DriverConfigurazioneDB_LIB.tipoDB);
				
				
				break;

			case UPDATE:
				String oldNomeSA = null;
				String oldNomeProprietario = null;
				String oldTipoProprietario = null;
				if(aSA.getOldIDServizioApplicativoForUpdate()!=null){
					oldNomeSA = aSA.getOldIDServizioApplicativoForUpdate().getNome();
					if(aSA.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario()!=null){
						oldNomeProprietario = aSA.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getNome();
						oldTipoProprietario = aSA.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getTipo();
					}
				}

				if (oldNomeSA == null || oldNomeSA.equals(""))
					oldNomeSA = nomeSA;
				if (oldNomeProprietario == null || oldNomeProprietario.equals(""))
					oldNomeProprietario = nomeProprietario;
				if (oldTipoProprietario == null || oldTipoProprietario.equals(""))
					oldTipoProprietario = tipoProprietario;

				long oldIdProprietario = DBUtils.getIdSoggetto(oldNomeProprietario, oldTipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(oldIdProprietario <=0) {
					oldIdProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				}
				if(oldIdProprietario <=0) 
					throw new DriverConfigurazioneException("Impossibile recuperare l'id del Soggetto Proprietario del Servizio Applicativo");
				
				
				// update
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addUpdateField("tipo", "?");
				sqlQueryObject.addUpdateField("as_client", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("sbustamentorisp", "?");
				sqlQueryObject.addUpdateField("sbustamento_protocol_info_risp", "?");
				sqlQueryObject.addUpdateField("getmsgrisp", "?");
				sqlQueryObject.addUpdateField("tipoauthrisp", "?");
				sqlQueryObject.addUpdateField("utenterisp", "?");
				sqlQueryObject.addUpdateField("passwordrisp", "?");
				sqlQueryObject.addUpdateField("id_connettore_risp", "?");
				sqlQueryObject.addUpdateField("sbustamentoinv", "?");
				sqlQueryObject.addUpdateField("sbustamento_protocol_info_inv", "?");
				sqlQueryObject.addUpdateField("getmsginv", "?");
				sqlQueryObject.addUpdateField("tipoauthinv", "?");
				sqlQueryObject.addUpdateField("utenteinv", "?");
				sqlQueryObject.addUpdateField("passwordinv", "?");
				sqlQueryObject.addUpdateField("id_connettore_inv", "?");
				sqlQueryObject.addUpdateField("fault", "?");
				sqlQueryObject.addUpdateField("tipoauth", "?");
				sqlQueryObject.addUpdateField("utente", "?");
				sqlQueryObject.addUpdateField("password", "?");
				sqlQueryObject.addUpdateField("subject", "?");
				sqlQueryObject.addUpdateField("cn_subject", "?");
				sqlQueryObject.addUpdateField("issuer", "?");
				sqlQueryObject.addUpdateField("cn_issuer", "?");
				sqlQueryObject.addUpdateField("certificate", "?");
				sqlQueryObject.addUpdateField("cert_strict_verification", "?");
				sqlQueryObject.addUpdateField("invio_x_rif_inv", "?");
				sqlQueryObject.addUpdateField("risposta_x_rif_inv", "?");
				sqlQueryObject.addUpdateField("invio_x_rif", "?");
				sqlQueryObject.addUpdateField("invio_x_rif_risp", "?");
				sqlQueryObject.addUpdateField("risposta_x_rif_risp", "?");
				sqlQueryObject.addUpdateField("sbustamento_protocol_info", "?");
				sqlQueryObject.addUpdateField("fault_actor", "?");
				sqlQueryObject.addUpdateField("generic_fault_code", "?");
				sqlQueryObject.addUpdateField("prefix_fault_code", "?");
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("id_soggetto", "?");
				if(aSA.getRispostaAsincrona()!=null && aSA.getRispostaAsincrona().getGestioneErrore()!=null){
					sqlQueryObject.addUpdateField("id_gestione_errore_risp", "?");
				}
				if(aSA.getInvocazioneServizio()!=null && aSA.getInvocazioneServizio().getGestioneErrore()!=null){
					sqlQueryObject.addUpdateField("id_gestione_errore_inv", "?");
				}
				sqlQueryObject.addUpdateField("tipologia_fruizione", "?");
				sqlQueryObject.addUpdateField("tipologia_erogazione", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = con.prepareStatement(sqlQuery);

				idServizioApplicativo = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(oldNomeSA, oldTipoProprietario, oldNomeProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(idServizioApplicativo<=0) {
					idServizioApplicativo = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(oldNomeSA, tipoProprietario, nomeProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				}
				if (idServizioApplicativo <= 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo(UPDATE)] ID del ServizioApplicativo necessario per l'aggiornamento.");
				// recupero i connettori

				// connettore risp asinc				
				RispostaAsincrona rispAsin = aSA.getRispostaAsincrona();
				connettoreRisp = rispAsin != null ? rispAsin.getConnettore() : new Connettore();
				String newNomeConnettoreRisp = "ConnettoreRISP_"+nomeSA+"_"+tipoProprietario+nomeProprietario;
				//idConnettoreRisp = DBUtils.getIdConnettore(nomeConnettoreRisp, con, tipoDB);
				idConnettoreRisp = DriverConfigurazioneDB_LIB.getIdConnettore_SA_RISP(idServizioApplicativo, con);
				
				// connettore inv servizio
				InvocazioneServizio invServ = aSA.getInvocazioneServizio();
				connettoreInv = invServ != null ? invServizio.getConnettore() : new Connettore();
				String newNomeConnettoreInv = "ConnettoreINV_"+nomeSA+"_"+tipoProprietario+nomeProprietario;
				//idConnettoreInv = DBUtils.getIdConnettore(nomeConnettoreInv, con, tipoDB);
				idConnettoreInv = DriverConfigurazioneDB_LIB.getIdConnettore_SA_INV(idServizioApplicativo, con);
				
				//Controllo consistenza degli id dei connettori in quanto devono essere specificati
				//e quindi maggiori di 0
				if(idConnettoreInv <=0 || idConnettoreRisp<=0) throw new DriverConfigurazioneException("I connettori del servizio applicativo sono invalidi");
				
				/*
				 * Problema:
				 * 	Se il nuovo connettore e' disabilitato (e il nome del connettore non cambia)
				 * 	il valore presente sul db non cambia anche se questo valore e' != da DISABILITATO
				 * Fix:
				 * 	I valori del nuovo connettore devono essere sempre aggiornati
				 *   
				 */

				String nomeConnettoreRisp = DriverConfigurazioneDB_LIB.getConnettore(idConnettoreRisp, con).getNome();
				String nomeConnettoreInv = DriverConfigurazioneDB_LIB.getConnettore(idConnettoreInv, con).getNome();

				String pattern = "Aggiorno Connettore [{0}] : id [{1}] oldNome [{2}] newNome [{2}]";

				DriverConfigurazioneDB_LIB.log.debug(MessageFormat.format(pattern, "Risposta Asincrona",idConnettoreRisp, nomeConnettoreRisp, newNomeConnettoreRisp));
				//aggiorno connettore risp
				connettoreRisp.setNome(newNomeConnettoreRisp);
				connettoreRisp.setId(idConnettoreRisp);
				DriverConfigurazioneDB_LIB.CRUDConnettore(CostantiDB.UPDATE, connettoreRisp, con);

				//aggiorno connettore inv
				DriverConfigurazioneDB_LIB.log.debug(MessageFormat.format(pattern, "Invocazione Servizio",idConnettoreInv, nomeConnettoreInv, newNomeConnettoreInv));
				connettoreInv.setNome(newNomeConnettoreInv);
				connettoreInv.setId(idConnettoreInv);
				DriverConfigurazioneDB_LIB.CRUDConnettore(CostantiDB.UPDATE, connettoreInv, con);


				// Setto i dati del ServizioApplicativo

				index = 1;
				
				stm.setString(index++, tipoSA);
				stm.setInt(index++, useAsClient);
				stm.setString(index++, descrizione);

				// RicezioneRisposta
				stm.setInt(index++, (ricezione != null && (CostantiConfigurazione.ABILITATO.equals(ricezione.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (ricezione != null && (!CostantiConfigurazione.DISABILITATO.equals(ricezione.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getGetMessage()) : null);
				// setto credenziali risp
				credenzialiInvocazione = ricezione != null ? ricezione.getCredenziali() : null;
				stm.setString(index++, (ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getAutenticazione()) : null));
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getUser() : null));
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getPassword() : null));
				// setto idconnettore risp
				stm.setLong(index++, idConnettoreRisp);

				// InvocazioneServizio
				stm.setInt(index++, (invServizio != null && (CostantiConfigurazione.ABILITATO.equals(invServizio.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (invServizio != null && (!CostantiConfigurazione.DISABILITATO.equals(invServizio.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getGetMessage()) : null);
				// setto credenziali inv
				credenzialiInvocazione = invServizio != null ? invServizio.getCredenziali() : null;
				stm.setString(index++, (invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getAutenticazione()) : null));
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getUser() : null));
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getPassword() : null));
				// setto idconnettore inv
				stm.setLong(index++, idConnettoreInv);

				// InvocazionePorta
				gestErr = invPorta != null ? invPorta.getGestioneErrore() : null;
				fault = (gestErr != null ? DriverConfigurazioneDB_LIB.getValue(gestErr.getFault()) : null);
				stm.setString(index++, fault);
				// setto credenziali invocaizone porta
				// per il momento c'e' soltato una credenziale,quindi un solo
				// oggetto nella lista
				credenzialiInvocazionePorta = (invPorta != null && invPorta.sizeCredenzialiList() > 0 ? invPorta.getCredenziali(0) : null);
				stm.setString(index++, (credenzialiInvocazionePorta != null ? DriverConfigurazioneDB_LIB.getValue(credenzialiInvocazionePorta.getTipo()) : null));
				stm.setString(index++, (credenzialiInvocazionePorta != null ? credenzialiInvocazionePorta.getUser() : null));
				stm.setString(index++, (credenzialiInvocazionePorta != null ? credenzialiInvocazionePorta.getPassword() : null));
				
				subject = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getSubject()!=null && !"".equals(credenzialiInvocazionePorta.getSubject()))
					subject = credenzialiInvocazionePorta.getSubject();
				stm.setString(index++, (subject != null ? CertificateUtils.formatPrincipal(subject, PrincipalType.subject) : null));
				subjectCN = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCnSubject()!=null && !"".equals(credenzialiInvocazionePorta.getCnSubject()))
					subjectCN = credenzialiInvocazionePorta.getCnSubject();
				stm.setString(index++, subjectCN);
				
				issuer = null;
				if(credenzialiInvocazionePorta != null && org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(credenzialiInvocazionePorta.getTipo())) {
					stm.setString(index++, CostantiDB.getISSUER_APIKEY(credenzialiInvocazionePorta.isAppId()));
				}
				else {
					if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getIssuer()!=null && !"".equals(credenzialiInvocazionePorta.getIssuer()))
						issuer = credenzialiInvocazionePorta.getIssuer();
					stm.setString(index++, (issuer != null ? CertificateUtils.formatPrincipal(issuer, PrincipalType.issuer) : null));
				}
				issuerCN = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCnIssuer()!=null && !"".equals(credenzialiInvocazionePorta.getCnIssuer()))
					issuerCN = credenzialiInvocazionePorta.getCnIssuer();
				stm.setString(index++, issuerCN);
				
				certificate = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCertificate()!=null) {
					certificate = credenzialiInvocazionePorta.getCertificate();
				}
				jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDB);
				jdbcAdapter.setBinaryData(stm, index++, certificate);
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.isCertificateStrictVerification()) {
					stm.setInt(index++, CostantiDB.TRUE);
				}				
				else {
					stm.setInt(index++, CostantiDB.FALSE);
				}

				// aggiungo gestione invio/risposta per riferimento
				// invocazione servizio
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getInvioPerRiferimento()) : null);
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getRispostaPerRiferimento()) : null);
				// invocazione porta
				stm.setString(index++, invPorta != null ? DriverConfigurazioneDB_LIB.getValue(invPorta.getInvioPerRiferimento()) : null);
				// ricezione risposta
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getInvioPerRiferimento()) : null);
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getRispostaPerRiferimento()) : null);
				// protocol info
				stm.setInt(index++, (invPorta != null && (!CostantiConfigurazione.DISABILITATO.equals(invPorta.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				//Invocazione Porta : fault_actor, generic_fault_code, prefix_fault_code
				stm.setString(index++, gestErr!=null ? gestErr.getFaultActor() : null);
				stm.setString(index++, gestErr!=null ? DriverConfigurazioneDB_LIB.getValue(gestErr.getGenericFaultCode()) : null);
				stm.setString(index++, gestErr!=null ? gestErr.getPrefixFaultCode() : null);
				//Aggiorno nome servizio applicativo
				stm.setString(index++, nomeSA);
				//Aggiorno il proprietario
				stm.setLong(index++, idProprietario<0 ? oldIdProprietario : idProprietario);

				// GestioneErrore risposta asincrona
				if(aSA.getRispostaAsincrona() !=null && aSA.getRispostaAsincrona().getGestioneErrore()!=null){
					DriverConfigurazioneDB_LIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.UPDATE, 
							aSA.getRispostaAsincrona().getGestioneErrore(), idProprietario, idServizioApplicativo, false, con);
					stm.setLong(index++, aSA.getRispostaAsincrona().getGestioneErrore().getId());
				}
				//	GestioneErrore invocazione servizio
				if(aSA.getInvocazioneServizio() !=null && aSA.getInvocazioneServizio().getGestioneErrore()!=null){
					DriverConfigurazioneDB_LIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.UPDATE, 
							aSA.getInvocazioneServizio().getGestioneErrore(), idProprietario, idServizioApplicativo, true, con);
					stm.setLong(index++, aSA.getInvocazioneServizio().getGestioneErrore().getId());
				}
				
				//tipologia erogazione/fruizione
				stm.setString(index++, aSA.getTipologiaFruizione()!=null ? TipologiaFruizione.valueOf(aSA.getTipologiaFruizione().toUpperCase()).toString() : TipologiaFruizione.DISABILITATO.toString());
				stm.setString(index++, aSA.getTipologiaErogazione()!=null ? TipologiaErogazione.valueOf(aSA.getTipologiaErogazione().toUpperCase()).toString() : TipologiaErogazione.DISABILITATO.toString());
				
				// where
				stm.setLong(index++, idServizioApplicativo); 
				stm.setString(index++, oldNomeSA); 
				stm.setLong(index++, oldIdProprietario); 

				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Updated " + n + " row(s)");

				
				
				// Ruoli
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, aSA.getId());
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" ruoli associati al servizioApplicativo "+idServizioApplicativo);
				
				n=0;
				if(invPorta!=null && invPorta.getRuoli()!=null && invPorta.getRuoli().sizeRuoloList()>0){
					for (int i = 0; i < invPorta.getRuoli().sizeRuoloList(); i++) {
						Ruolo ruolo = invPorta.getRuoli().getRuolo(i);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
						sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aSA.getId());
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] al servizioApplicativo "+idServizioApplicativo);
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " ruoli al servizioApplicativo "+idServizioApplicativo);
				
				
				// ProtocolProperties
				DriverConfigurazioneDB_LIB.CRUDProtocolProperty(CostantiDB.UPDATE, aSA.getProtocolPropertyList(), 
						idServizioApplicativo, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO, con, DriverConfigurazioneDB_LIB.tipoDB);
				
				break;

			case DELETE:
				// delete
				// if(aSA.getId()==null || aSA.getId()<=0) throw new
				// DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo(DELETE)]
				// id del ServizioApplicativo non valida.");

				DriverConfigurazioneDB_LIB.log.debug("get ID Servizio Applicativo con nome["+nomeSA+"] tipoProprietario["+tipoProprietario+"] nomeProprietario["+nomeProprietario+"]");
				idServizioApplicativo = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeSA, tipoProprietario, nomeProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				DriverConfigurazioneDB_LIB.log.debug("get ID Servizio Applicativo: "+idServizioApplicativo); 

				// ProtocolProperties
				DriverConfigurazioneDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						idServizioApplicativo, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO, con, DriverConfigurazioneDB_LIB.tipoDB);
				
				// ruoli
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " ruoli associati al ServizioApplicativo[" + idServizioApplicativo + "]");
				
				// cancello anche le associazioni delle porteapplicative
				// associate a questo servizio
				// serviziapplicativi
				
				List<Long> idsPA_SA = new ArrayList<>(); 
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				rs = stm.executeQuery();
				while(rs.next()) {
					idsPA_SA.add(rs.getLong("id"));
				}
				rs.close();
				stm.close();
				
				if(!idsPA_SA.isEmpty()) {
					for (Long idsapa : idsPA_SA) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
						sqlQueryObject.addWhereCondition("id_porta=?");
						sqlQuery = sqlQueryObject.createSQLDelete();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idsapa);
						n=stm.executeUpdate();
						stm.close();
						DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta relative all'associazione '"+idsapa+"' (SA "+idServizioApplicativo+")");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Deleted PA associazioni...");
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " associazioni di PortaApplicativa<->ServizioApplicativo associate al ServizioApplicativo[" + idServizioApplicativo + "]");

				// faccio lo stesso per le portedelegate
				DriverConfigurazioneDB_LIB.log.debug("Deleted PD associazioni...");
				DriverConfigurazioneDB_LIB.log.debug("Deleted PA associazioni...");
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " associazioni di PortaDelegata<->ServizioApplicativo associate al ServizioApplicativo[" + idServizioApplicativo + "]");



				DriverConfigurazioneDB_LIB.log.debug("Deleted ...");
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				stm.setString(2, nomeSA);
				stm.setLong(3, idProprietario);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizioApplicativo,nomeSA,idProprietario));
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " row(s)");


				//cancello i connettori

				// Connettore asincrono
				DriverConfigurazioneDB_LIB.log.debug("Recupero connettore asincrono ...");
				if(aSA.getRispostaAsincrona()!=null && aSA.getRispostaAsincrona().getConnettore()!=null){
					connettoreRisp=aSA.getRispostaAsincrona().getConnettore();
				}else{
					connettoreRisp = new Connettore();
					connettoreRisp.setTipo(TipiConnettore.DISABILITATO.getNome());
				}
				nomeConnettoreRisp = "ConnettoreRISP_" + aSA.getNome()+"_"+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario();
				connettoreRisp.setNome(nomeConnettoreRisp);
				idConnettoreRisp = DBUtils.getIdConnettore(nomeConnettoreRisp, con, DriverConfigurazioneDB_LIB.tipoDB);
				DriverConfigurazioneDB_LIB.log.debug("Recupero connettore asincrono id["+idConnettoreRisp+"]");
				connettoreRisp.setId(idConnettoreRisp);


				// Connettore inv servizio
				DriverConfigurazioneDB_LIB.log.debug("Recupero connettore invocazione servizio ...");
				if(aSA.getInvocazioneServizio()!=null && aSA.getInvocazioneServizio().getConnettore()!=null){
					connettoreInv=aSA.getInvocazioneServizio().getConnettore();
				}else{
					connettoreInv = new Connettore();
					connettoreInv.setTipo(TipiConnettore.DISABILITATO.getNome());
				}
				nomeConnettoreInv = "ConnettoreINV_" + aSA.getNome()+"_"+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario();
				connettoreInv.setNome(nomeConnettoreInv);
				idConnettoreInv = DBUtils.getIdConnettore(nomeConnettoreInv, con, DriverConfigurazioneDB_LIB.tipoDB);
				DriverConfigurazioneDB_LIB.log.debug("Recupero connettore invocazione servizio id["+idConnettoreInv+"]");
				connettoreInv.setId(idConnettoreInv);


				//Controllo consistenza degli id dei connettori in quanto devono essere specificati
				//e quindi maggiori di 0
				if(idConnettoreInv <=0 || idConnettoreRisp<=0) throw new DriverConfigurazioneException("I connettori del servizio applicativo sono invalidi");

				// se il connettore e' abilitato allora propago le modifiche al
				// connettore
				DriverConfigurazioneDB_LIB.log.debug("Delete connettore asincrono ...");
				DriverConfigurazioneDB_LIB.CRUDConnettore(CostantiDB.DELETE, connettoreRisp, con);
				DriverConfigurazioneDB_LIB.log.debug("Delete connettore invocazione servizio ...");
				DriverConfigurazioneDB_LIB.CRUDConnettore(CostantiDB.DELETE, connettoreInv, con);

				
				// Delete gestione errore risposta asincrona
				if(aSA.getRispostaAsincrona() !=null && aSA.getRispostaAsincrona().getGestioneErrore()!=null){
					DriverConfigurazioneDB_LIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.DELETE, 
							aSA.getRispostaAsincrona().getGestioneErrore(), idProprietario, idServizioApplicativo, false, con);
				}
				
				
				// Delete gestione errore invocazione servizio
				if(aSA.getInvocazioneServizio() !=null && aSA.getInvocazioneServizio().getGestioneErrore()!=null){
					DriverConfigurazioneDB_LIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.DELETE, 
							aSA.getInvocazioneServizio().getGestioneErrore(), idProprietario, idServizioApplicativo, true, con);
				}
				

				break;

			}

			return idServizioApplicativo;
		} catch (DriverConfigurazioneException e) {
			throw e;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverConfigurazioneException("Exception : " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}

	public static long CRUDPortaApplicativa(int type, PortaApplicativa aPA, Connection con) throws DriverConfigurazioneException {
		if (aPA == null)
			throw new DriverConfigurazioneException("Porta Applicativa non valida.");
		// parametri necessari
		String nomePorta = aPA.getNome();
		String nomeProprietario = aPA.getNomeSoggettoProprietario();
		String tipoProprietario = aPA.getTipoSoggettoProprietario();
		if (nomePorta == null || nomePorta.equals(""))
			throw new DriverConfigurazioneException("Nome della Porta Applicativa non valido.");
		if (nomeProprietario == null || nomeProprietario.equals(""))
			throw new DriverConfigurazioneException("Nome proprietario Porta Applicativa non valido.");
		if (tipoProprietario == null || tipoProprietario.equals(""))
			throw new DriverConfigurazioneException("Tipo proprietario della Porta Applicativa non valido.");

		PreparedStatement stm = null;
		String sqlQuery = "";
		ResultSet rs = null;

		String descrizione = aPA.getDescrizione();

		String autenticazione = aPA.getAutenticazione();
		String autorizzazione = aPA.getAutorizzazione();
		String autorizzazioneXacmlPolicy = aPA.getXacmlPolicy();
		GestioneToken gestioneToken = aPA.getGestioneToken(); 
		
		PortaApplicativaAzione azione = aPA.getAzione();
		PortaApplicativaServizio servizio = aPA.getServizio();
		long idServizio = ((servizio != null && servizio.getId() != null) ? servizio.getId() : -1);
		// long idServizio = (servizio !=null ?
		// getIdServizio(servizio.getNome(),
		// servizio.getTipo(),aPA.getNomeSoggettoProprietario(),aPA.getTipoSoggettoProprietario()
		// , con) : -1);

		PortaApplicativaSoggettoVirtuale soggVirt = aPA.getSoggettoVirtuale();
		String tipoSoggVirt = (soggVirt != null ? soggVirt.getTipo() : null);
		String nomeSoggVirt = (soggVirt != null ? soggVirt.getNome() : null);
		//long idSoggVirt = ((soggVirt != null && soggVirt.getId() != null) ? soggVirt.getId() : -1);
		long idSoggVirt=-1;
		try {
			idSoggVirt = DBUtils.getIdSoggetto(nomeSoggVirt, tipoSoggVirt, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
		} catch (CoreException e1) {
			DriverConfigurazioneDB_LIB.log.error(e1.getMessage(),e1);
		}

		Proprieta propProtocollo = null;

		MtomProcessor mtomProcessor = aPA.getMtomProcessor();
		MTOMProcessorType mtomMode_request = null;
		MTOMProcessorType mtomMode_response = null;
		if(mtomProcessor!=null){
			if(mtomProcessor.getRequestFlow()!=null){
				mtomMode_request = mtomProcessor.getRequestFlow().getMode();
			}
			if(mtomProcessor.getResponseFlow()!=null){
				mtomMode_response = mtomProcessor.getResponseFlow().getMode();
			}
		}
		
		MessageSecurity messageSecurity = aPA.getMessageSecurity();
		String messageSecurityStatus = aPA.getStatoMessageSecurity();
		StatoFunzionalita messageSecurityApplyMtom_request = null;
		StatoFunzionalita messageSecurityApplyMtom_response = null;
		String securityRequestMode = null;
		String securityResponseMode = null;
		if(messageSecurity!=null){
			if(messageSecurity.getRequestFlow()!=null){
				messageSecurityApplyMtom_request = messageSecurity.getRequestFlow().getApplyToMtom();
				securityRequestMode = messageSecurity.getRequestFlow().getMode();
			}
			if(messageSecurity.getResponseFlow()!=null){
				messageSecurityApplyMtom_response = messageSecurity.getResponseFlow().getApplyToMtom();
				securityResponseMode = messageSecurity.getResponseFlow().getMode();
			}
		}
		
		CorrelazioneApplicativa corrApp = aPA.getCorrelazioneApplicativa();
		CorrelazioneApplicativaRisposta corrAppRisposta = aPA.getCorrelazioneApplicativaRisposta();

		String msg_diag_severita = null;
		String tracciamento_esiti = null;
		if(aPA.getTracciamento()!=null){
			msg_diag_severita = DriverConfigurazioneDB_LIB.getValue(aPA.getTracciamento().getSeverita());
			tracciamento_esiti = aPA.getTracciamento().getEsiti();
		}
		
		CorsConfigurazione corsConfigurazione = aPA.getGestioneCors();
		String cors_stato = null;
		String cors_tipo = null; 
		String cors_all_allow_origins = null; 
		String cors_allow_credentials = null; 
		int cors_allow_max_age = CostantiDB.FALSE;
		Integer cors_allow_max_age_seconds = null;
		String cors_allow_origins = null; 
		String cors_allow_headers = null; 
		String cors_allow_methods = null; 
		String cors_allow_expose_headers = null; 
		if(corsConfigurazione!=null) {
			cors_stato = getValue(corsConfigurazione.getStato());
			cors_tipo = getValue(corsConfigurazione.getTipo());
			cors_all_allow_origins = getValue(corsConfigurazione.getAccessControlAllAllowOrigins());
			cors_allow_credentials = getValue(corsConfigurazione.getAccessControlAllowCredentials());
			if(corsConfigurazione.getAccessControlMaxAge()!=null) {
				cors_allow_max_age = CostantiDB.TRUE;
				cors_allow_max_age_seconds = corsConfigurazione.getAccessControlMaxAge();	
			}
			if(corsConfigurazione.getAccessControlAllowOrigins()!=null && corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowOrigins().getOrigin(i));
				}
				cors_allow_origins = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowHeaders()!=null && corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowHeaders().getHeader(i));
				}
				cors_allow_headers = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowMethods()!=null && corsConfigurazione.getAccessControlAllowMethods().sizeMethodList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowMethods().sizeMethodList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowMethods().getMethod(i));
				}
				cors_allow_methods = bf.toString();
			}
			if(corsConfigurazione.getAccessControlExposeHeaders()!=null && corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlExposeHeaders().getHeader(i));
				}
				cors_allow_expose_headers = bf.toString();
			}
		}
		
		ResponseCachingConfigurazione responseCachingConfigurazone = aPA.getResponseCaching();
		String response_cache_stato = null;
		Integer response_cache_seconds = null;
		Long response_cache_max_msg_size = null;
		String response_cache_hash_url = null;
		String response_cache_hash_query = null;
		String response_cache_hash_query_list = null;
		String response_cache_hash_headers = null;
		String response_cache_hash_headers_list = null;
		String response_cache_hash_payload = null;
		boolean response_cache_noCache = true;
		boolean response_cache_maxAge = true;
		boolean response_cache_noStore = true;
		List<ResponseCachingConfigurazioneRegola> response_cache_regole = null;
		if(responseCachingConfigurazone!=null) {
			response_cache_stato = getValue(responseCachingConfigurazone.getStato());
			response_cache_seconds = responseCachingConfigurazone.getCacheTimeoutSeconds();
			response_cache_max_msg_size = responseCachingConfigurazone.getMaxMessageSize();
			if(responseCachingConfigurazone.getControl()!=null) {
				response_cache_noCache = responseCachingConfigurazone.getControl().getNoCache();
				response_cache_maxAge = responseCachingConfigurazone.getControl().getMaxAge();
				response_cache_noStore = responseCachingConfigurazone.getControl().getNoStore();
			}
			if(responseCachingConfigurazone.getHashGenerator()!=null) {
				response_cache_hash_url = getValue(responseCachingConfigurazone.getHashGenerator().getRequestUri());
				
				response_cache_hash_query = getValue(responseCachingConfigurazone.getHashGenerator().getQueryParameters());
				if(StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(responseCachingConfigurazone.getHashGenerator().getQueryParameters())) {
					if(responseCachingConfigurazone.getHashGenerator().getQueryParameterList()!=null && responseCachingConfigurazone.getHashGenerator().sizeQueryParameterList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < responseCachingConfigurazone.getHashGenerator().sizeQueryParameterList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(responseCachingConfigurazone.getHashGenerator().getQueryParameter(i));
						}
						response_cache_hash_query_list = bf.toString();
					}
				}
				
				response_cache_hash_headers = getValue(responseCachingConfigurazone.getHashGenerator().getHeaders());
				if(StatoFunzionalita.ABILITATO.equals(responseCachingConfigurazone.getHashGenerator().getHeaders())) {
					if(responseCachingConfigurazone.getHashGenerator().getHeaderList()!=null && responseCachingConfigurazone.getHashGenerator().sizeHeaderList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < responseCachingConfigurazone.getHashGenerator().sizeHeaderList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(responseCachingConfigurazone.getHashGenerator().getHeader(i));
						}
						response_cache_hash_headers_list = bf.toString();
					}
				}
				
				response_cache_hash_payload = getValue(responseCachingConfigurazone.getHashGenerator().getPayload());
			}
			response_cache_regole = responseCachingConfigurazone.getRegolaList();
		}
		
		String behaviour = null;
		if(aPA.getBehaviour()!=null) {
			behaviour = aPA.getBehaviour().getNome();
		}
		
		ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
		IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoPortaApplicativa();
		
		try {
			int n = 0;
			int i = 0;
			long idPortaApplicativa = 0;
			long idProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
			// preparo lo statement in base al tipo di operazione
			switch (type) {
			case CREATE:
				// CREATE

				//campi obbligatori
				//servizio ci deve essere l'id oppure tipo e nome
				String tipoServizio = (servizio != null ? servizio.getTipo() : null);
				String nomeServizio = (servizio != null ? servizio.getNome() : null);
				Integer versioneServizio = (servizio != null ? servizio.getVersione() : null);
				//se l'id non e' valido allora devono esserci necessariamente il tipo e il nome
				if(idServizio<=0){
					if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
					if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
					if(versioneServizio==null) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
				}

				//Azione
				String nomeAzione = (azione != null ? azione.getNome() : null);
				String patternAzione = (azione != null ? azione.getPattern() : null);
				String nomePortaDeleganteAzione = (azione != null ? azione.getNomePortaDelegante() : null);
				StatoFunzionalita forceInterfaceBased = (azione != null ? azione.getForceInterfaceBased() : null);
				PortaApplicativaAzioneIdentificazione modeAzione = (azione != null ? azione.getIdentificazione() : null);
				//Se il bean Azione nn e' presente allora non controllo nulla
				if(azione!=null){
					if(modeAzione==null || modeAzione.equals("")) 
						modeAzione = PortaApplicativaAzioneIdentificazione.STATIC;
					switch (modeAzione) {
					case CONTENT_BASED:
					case URL_BASED:
					case HEADER_BASED:
						if(patternAzione==null || patternAzione.equals("")) throw new DriverConfigurazioneException("Pattern Azione non impostato.");
						nomeAzione=null;
						break;
					case DELEGATED_BY:
						if(nomePortaDeleganteAzione==null || nomePortaDeleganteAzione.equals("")) throw new DriverConfigurazioneException("Nome Porta Delegante Azione non impostata.");
						nomeAzione=null;
						break;
					case INPUT_BASED:
					case SOAP_ACTION_BASED:
					case INTERFACE_BASED:
					case PROTOCOL_BASED:
						//nessun campo obbligatorio
						break;
					case STATIC:
						//ci deve essere il nome
						if(nomeAzione==null || nomeAzione.equals("")) throw new DriverConfigurazioneException("Nome Azione non impostato.");
						patternAzione=null;
						break;
					default:
						break;
					}
				}
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addInsertField("nome_porta", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("id_soggetto_virtuale", "?");
				sqlQueryObject.addInsertField("tipo_soggetto_virtuale", "?");
				sqlQueryObject.addInsertField("nome_soggetto_virtuale", "?");
				sqlQueryObject.addInsertField("id_servizio", "?");
				sqlQueryObject.addInsertField("tipo_servizio", "?");
				sqlQueryObject.addInsertField("servizio", "?");
				sqlQueryObject.addInsertField("versione_servizio", "?");
				sqlQueryObject.addInsertField("azione", "?");
				sqlQueryObject.addInsertField("mode_azione", "?");
				sqlQueryObject.addInsertField("pattern_azione", "?");
				sqlQueryObject.addInsertField("nome_porta_delegante_azione", "?");
				sqlQueryObject.addInsertField("force_interface_based_azione", "?");
				sqlQueryObject.addInsertField("mtom_request_mode", "?");
				sqlQueryObject.addInsertField("mtom_response_mode", "?");
				sqlQueryObject.addInsertField("security", "?");
				sqlQueryObject.addInsertField("security_mtom_req", "?");
				sqlQueryObject.addInsertField("security_mtom_res", "?");
				sqlQueryObject.addInsertField("security_request_mode", "?");
				sqlQueryObject.addInsertField("security_response_mode", "?");
				sqlQueryObject.addInsertField("id_soggetto", "?");
				sqlQueryObject.addInsertField("ricevuta_asincrona_sim", "?");
				sqlQueryObject.addInsertField("ricevuta_asincrona_asim", "?");
				sqlQueryObject.addInsertField("integrazione", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_stato", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_mtom", "?");
				sqlQueryObject.addInsertField("allega_body", "?");
				sqlQueryObject.addInsertField("scarta_body", "?");
				sqlQueryObject.addInsertField("gestione_manifest", "?");
				sqlQueryObject.addInsertField("stateless", "?");
				sqlQueryObject.addInsertField("behaviour", "?");
				sqlQueryObject.addInsertField("autenticazione", "?");
				sqlQueryObject.addInsertField("autenticazione_opzionale", "?");
				sqlQueryObject.addInsertField("token_policy", "?");
				sqlQueryObject.addInsertField("token_opzionale", "?");
				sqlQueryObject.addInsertField("token_validazione", "?");
				sqlQueryObject.addInsertField("token_introspection", "?");
				sqlQueryObject.addInsertField("token_user_info", "?");
				sqlQueryObject.addInsertField("token_forward", "?");
				sqlQueryObject.addInsertField("token_options", "?");
				sqlQueryObject.addInsertField("token_authn_issuer", "?");
				sqlQueryObject.addInsertField("token_authn_client_id", "?");
				sqlQueryObject.addInsertField("token_authn_subject", "?");
				sqlQueryObject.addInsertField("token_authn_username", "?");
				sqlQueryObject.addInsertField("token_authn_email", "?");
				sqlQueryObject.addInsertField("autorizzazione", "?");
				sqlQueryObject.addInsertField("autorizzazione_xacml", "?");
				sqlQueryObject.addInsertField("autorizzazione_contenuto", "?");
				sqlQueryObject.addInsertField("ruoli_match", "?");
				sqlQueryObject.addInsertField("scope_stato", "?");
				sqlQueryObject.addInsertField("scope_match", "?");
				sqlQueryObject.addInsertField("ricerca_porta_azione_delegata", "?");
				sqlQueryObject.addInsertField("msg_diag_severita", "?");
				sqlQueryObject.addInsertField("tracciamento_esiti", "?");
				sqlQueryObject.addInsertField("stato", "?");
				// cors
				sqlQueryObject.addInsertField("cors_stato", "?");
				sqlQueryObject.addInsertField("cors_tipo", "?");
				sqlQueryObject.addInsertField("cors_all_allow_origins", "?");
				sqlQueryObject.addInsertField("cors_allow_credentials", "?");
				sqlQueryObject.addInsertField("cors_allow_max_age", "?");
				sqlQueryObject.addInsertField("cors_allow_max_age_seconds", "?");
				sqlQueryObject.addInsertField("cors_allow_origins", "?");
				sqlQueryObject.addInsertField("cors_allow_headers", "?");
				sqlQueryObject.addInsertField("cors_allow_methods", "?");
				sqlQueryObject.addInsertField("cors_allow_expose_headers", "?");
				// responseCaching
				sqlQueryObject.addInsertField("response_cache_stato", "?");
				sqlQueryObject.addInsertField("response_cache_seconds", "?");
				sqlQueryObject.addInsertField("response_cache_max_msg_size", "?");
				sqlQueryObject.addInsertField("response_cache_control_nocache", "?");
				sqlQueryObject.addInsertField("response_cache_control_maxage", "?");
				sqlQueryObject.addInsertField("response_cache_control_nostore", "?");
				sqlQueryObject.addInsertField("response_cache_hash_url", "?");
				sqlQueryObject.addInsertField("response_cache_hash_query", "?");
				sqlQueryObject.addInsertField("response_cache_hash_query_list", "?");
				sqlQueryObject.addInsertField("response_cache_hash_headers", "?");
				sqlQueryObject.addInsertField("response_cache_hash_hdr_list", "?");
				sqlQueryObject.addInsertField("response_cache_hash_payload", "?");
				// servizio applicativo default
				sqlQueryObject.addInsertField("id_sa_default", "?");
				// id
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("id_port_type", "?");
				sqlQueryObject.addInsertField("scadenza_correlazione_appl", "?");
				// options
				sqlQueryObject.addInsertField("options", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				
				int index = 1;
				
				stm.setString(index++, nomePorta);
				stm.setString(index++, descrizione);
				stm.setLong(index++, idSoggVirt);
				stm.setString(index++, tipoSoggVirt);//tipo sogg virt
				stm.setString(index++, nomeSoggVirt); //nome sogg virt
				stm.setLong(index++, idServizio);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setInt(index++, versioneServizio);
				stm.setString(index++, (azione != null ? azione.getNome() : null));
				if(modeAzione!=null){
					stm.setString(index++, modeAzione.toString());
				}
				else{
					stm.setString(index++, null);
				}
				stm.setString(index++, patternAzione);
				stm.setString(index++, nomePortaDeleganteAzione);
				stm.setString(index++, getValue(forceInterfaceBased));
				// mtom
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_response));
				stm.setString(index++, securityRequestMode);
				stm.setString(index++, securityResponseMode);
				// proprietario
				stm.setLong(index++, idProprietario);
				//ricevuta asincrona_asimmetrica/simmetrica
				stm.setString(index++, aPA.getRicevutaAsincronaSimmetrica()!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getRicevutaAsincronaSimmetrica()) : null);
				stm.setString(index++, aPA.getRicevutaAsincronaAsimmetrica()!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getRicevutaAsincronaAsimmetrica()) : null);
				//integrazione
				stm.setString(index++, aPA.getIntegrazione()!=null ? aPA.getIntegrazione() : null);
				//validazione xsd
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getValidazioneContenutiApplicativi().getStato()) : null);
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getValidazioneContenutiApplicativi().getTipo()) : null);
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) : null);
				
				// InvocazionePorta: funzionalita' attachment
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getAllegaBody()) : null);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getScartaBody()) : null);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getGestioneManifest()) : null);
				
				// Stateless
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getStateless()) : null);
				
				// Behaviour
				stm.setString(index++, behaviour);
				
				// Autenticazione
				stm.setString(index++, autenticazione);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getAutenticazioneOpzionale()) : null);
				// token
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getPolicy() : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getTokenOpzionale()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getValidazione()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getIntrospection()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getUserInfo()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getForward()) : null);
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getOptions() : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getIssuer()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getClientId()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getSubject()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getUsername()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getEmail()) : null);
				// Autorizzazione
				stm.setString(index++, autorizzazione);
				stm.setString(index++, autorizzazioneXacmlPolicy);
				stm.setString(index++, aPA!=null ? aPA.getAutorizzazioneContenuto() : null);
				
				// Ruoli
				stm.setString(index++, aPA!=null && aPA.getRuoli()!=null && aPA.getRuoli().getMatch()!=null ? 
						aPA.getRuoli().getMatch().getValue() : null);
				
				// Scope
				stm.setString(index++, aPA!=null && aPA.getScope()!=null && aPA.getScope().getStato()!=null ? 
						DriverConfigurazioneDB_LIB.getValue(aPA.getScope().getStato()) : null);
				stm.setString(index++, aPA!=null && aPA.getScope()!=null && aPA.getScope().getMatch()!=null ? 
						aPA.getScope().getMatch().getValue() : null);
				
				// RicercaPortaAzioneDelegata
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getRicercaPortaAzioneDelegata()) : null);
				
				// Tracciamento
				stm.setString(index++, msg_diag_severita);
				stm.setString(index++, tracciamento_esiti);
				
				// Stato
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getStato()) : null);
				
				// cors
				stm.setString(index++, cors_stato);
				stm.setString(index++, cors_tipo);
				stm.setString(index++, cors_all_allow_origins);
				stm.setString(index++, cors_allow_credentials);
				stm.setInt(index++, cors_allow_max_age);
				if(cors_allow_max_age_seconds!=null) {
					stm.setInt(index++, cors_allow_max_age_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				stm.setString(index++, cors_allow_origins);
				stm.setString(index++, cors_allow_headers);
				stm.setString(index++, cors_allow_methods);
				stm.setString(index++, cors_allow_expose_headers);
				
				// responseCaching
				stm.setString(index++, response_cache_stato);
				if(response_cache_seconds!=null) {
					stm.setInt(index++, response_cache_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				if(response_cache_max_msg_size!=null) {
					stm.setLong(index++, response_cache_max_msg_size);
				}
				else {
					stm.setNull(index++, java.sql.Types.BIGINT);
				}
				stm.setInt(index++, response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setString(index++, response_cache_hash_url);
				stm.setString(index++, response_cache_hash_query);
				stm.setString(index++, response_cache_hash_query_list);
				stm.setString(index++, response_cache_hash_headers);
				stm.setString(index++, response_cache_hash_headers_list);
				stm.setString(index++, response_cache_hash_payload);
				
				// servizio applicativo default
				long idServizioApplicativoDefault = -1;
				if(aPA.getServizioApplicativoDefault()!=null) {
					idServizioApplicativoDefault = DBUtils.getIdServizioApplicativo(aPA.getServizioApplicativoDefault(), tipoProprietario, nomeProprietario, 
							con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
					if(idServizioApplicativoDefault<=0) {
						throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaApplicativa(CREATE)] Impossibile recuperare l'ID del servizio applicativo di default '"+aPA.getServizioApplicativoDefault()+"'.");
					}
				}
				stm.setLong(index++, idServizioApplicativoDefault);
				
				//idaccordo
				stm.setLong(index++, aPA.getIdAccordo()!=null ? aPA.getIdAccordo() : -1L);
				stm.setLong(index++, aPA.getIdPortType() !=null ? aPA.getIdPortType() : -1L);
				
				// ScadenzaCorrelazioneApplicativa
				stm.setString(index++, aPA.getCorrelazioneApplicativa()!=null ? aPA.getCorrelazioneApplicativa().getScadenza() : null);
				
				// options
				stm.setString(index++, aPA.getOptions());
				
				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Created " + n + " row(s)");

				// recupero l'id della porta applicativa appena inserita
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereCondition("nome_porta = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idProprietario);
				stm.setString(2, nomePorta);

				rs = stm.executeQuery();

				if (rs.next()) {
					idPortaApplicativa = rs.getLong("id");
					aPA.setId(idPortaApplicativa);
				} else {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaApplicativa(CREATE)] Impossibile recuperare l'ID della PortaApplicativa appena create.");
				}
				rs.close();
				stm.close();
				
				
				if(mtomProcessor!=null){
					
					MtomProcessorFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					i = 0;
					if(mtomProcessor.getRequestFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getPattern());
							stm.setString(4, reqParam.getContentType());
							stm.setInt(5, reqParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);

							stm.executeUpdate();
						}	
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom request flow con id=" + idPortaApplicativa);

					MtomProcessorFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(mtomProcessor.getResponseFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getPattern());
							stm.setString(4, resParam.getContentType());
							stm.setInt(5, resParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom response flow con id=" + idPortaApplicativa);
					
				}
				
				// se security abilitato setto la lista
				//if ((messageSecurity != null) && CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				// Devo settarli sempre se ci sono, in modo che lo switch abilitato-disabilitato funzioni
				if ((messageSecurity != null) )  {
					MessageSecurityFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					i = 0;
					if(messageSecurity.getRequestFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " request flow con id=" + idPortaApplicativa);

					MessageSecurityFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(messageSecurity.getResponseFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " response flow con id=" + idPortaApplicativa);
				}

				// la lista di correlazioni applicative contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				if (corrApp != null) {

					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrApp.sizeElementoList(); i++) {
						cae = corrApp.getElemento(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa con id=" + idPortaApplicativa);
				}
				
				// la lista di correlazioni applicative risposta contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				if (corrAppRisposta != null) {

					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaRispostaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrAppRisposta.sizeElementoList(); i++) {
						cae = corrAppRisposta.getElemento(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa risposta con id=" + idPortaApplicativa);
				}
				
				// serviziapplicativi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
				sqlQueryObject.addInsertField("connettore_nome", "?");
				sqlQueryObject.addInsertField("connettore_notifica", "?");
				sqlQueryObject.addInsertField("connettore_descrizione", "?");
				sqlQueryObject.addInsertField("connettore_stato", "?");
				sqlQueryObject.addInsertField("connettore_filtri", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);

				List<Long> idsPA_SA = new ArrayList<>();
				for (i = 0; i < aPA.sizeServizioApplicativoList(); i++) {
					PortaApplicativaServizioApplicativo servizioApplicativo = aPA.getServizioApplicativo(i);
					String nomeSA = servizioApplicativo.getNome();
					//nome/tipo soggetto proprietario servizio applicativo sono gli stessi della porta applicativa
					String nomeProprietarioSA = aPA.getNomeSoggettoProprietario();//servizioApplicativo.getNomeSoggettoProprietario();
					String tipoProprietarioSA = aPA.getTipoSoggettoProprietario();//servizioApplicativo.getTipoSoggettoProprietario();
					if (nomeSA == null || nomeSA.equals(""))
						throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)::Nome del ServizioApplicativo associato non valido.");
					if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)::Nome Proprietario del ServizioApplicativo associato non valido.");
					if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)::Tipo Proprietario del ServizioApplicativo associato non valido.");

					long idSA = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);

					if (idSA <= 0)
						throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");

					idsPA_SA.add(idSA);
					
					int indexSA = 1;
					stm.setLong(indexSA++, idPortaApplicativa);
					stm.setLong(indexSA++, idSA);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? servizioApplicativo.getDatiConnettore().getNome() : null);
					stm.setInt(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? (servizioApplicativo.getDatiConnettore().isNotifica() ? CostantiDB.TRUE : CostantiDB.FALSE) : CostantiDB.FALSE);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? servizioApplicativo.getDatiConnettore().getDescrizione() : null);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? getValue(servizioApplicativo.getDatiConnettore().getStato()) : null);
					
					String filtri = null; 
					if(servizioApplicativo.getDatiConnettore()!=null) {
						if(servizioApplicativo.getDatiConnettore().getFiltroList()!=null && servizioApplicativo.getDatiConnettore().sizeFiltroList()>0) {
							StringBuilder bf = new StringBuilder();
							for (int k = 0; k < servizioApplicativo.getDatiConnettore().sizeFiltroList(); k++) {
								if(k>0) {
									bf.append(",");
								}
								bf.append(servizioApplicativo.getDatiConnettore().getFiltro(k));
							}
							filtri = bf.toString();
						}
					}
					stm.setString(indexSA++, filtri);
					
					stm.executeUpdate();
					
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " servizi applicativi associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				
				
				// serviziapplicativi props			
				for (i = 0; i < aPA.sizeServizioApplicativoList(); i++) {
					PortaApplicativaServizioApplicativo servizioApplicativo = aPA.getServizioApplicativo(i);
					String nomeSA = servizioApplicativo.getNome();
					
					if(servizioApplicativo.getDatiConnettore()!=null && servizioApplicativo.getDatiConnettore().sizeProprietaList()>0) {
					
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
						sqlQueryObject.addSelectField("id");
						sqlQueryObject.addWhereCondition("id_porta=?");
						sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
						sqlQueryObject.setANDLogicOperator(true);
						
						long idSA = idsPA_SA.get(i);
						
						long idPA_SA = -1;
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setLong(2, idSA);
						rs = stm.executeQuery();
						if(rs.next()) {
							idPA_SA = rs.getLong("id");
						}
						rs.close();
						stm.close();
						
						if(idPA_SA<=0) {
							throw new DriverConfigurazioneException("Impossibile recuperare l'id della registrazione del Servizio Applicativo [" + nomeSA + "](id:"+idSA+") alla porta applicativa con id:"+idPortaApplicativa );
						}
						
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("valore", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						
						int j = 0;
						for ( ; j < servizioApplicativo.getDatiConnettore().sizeProprietaList(); j++) {
							
							Proprieta p = servizioApplicativo.getDatiConnettore().getProprieta(j);
							
							stm.setLong(1, idPA_SA);
							stm.setString(2, p.getNome());
							stm.setString(3, p.getValore());
							stm.executeUpdate();
							
						}
	
						stm.close();
						DriverConfigurazioneDB_LIB.log.debug("Insererted " + j + " SetSAProp associati al Servizio Applicativo [" + nomeSA + "](id:"+idSA+") della PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				
				// set prop behaviour
				if(aPA.getBehaviour()!=null) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					for (i = 0; i < aPA.getBehaviour().sizeProprietaList(); i++) {
						propProtocollo = aPA.getBehaviour().getProprieta(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, propProtocollo.getNome());
						stm.setString(3, propProtocollo.getValore());
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SeBehaviourProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				}
				
				
				
				// set prop autenticazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPA.sizeProprietaAutenticazioneList(); i++) {
					propProtocollo = aPA.getProprietaAutenticazione(i);
					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SetProtocolPropAutenticazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				// set prop autorizzazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPA.sizeProprietaAutorizzazioneList(); i++) {
					propProtocollo = aPA.getProprietaAutorizzazione(i);
					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SetProtocolPropAutorizzazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// set prop autorizzazione contenuti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPA.sizeProprietaAutorizzazioneContenutoList(); i++) {
					propProtocollo = aPA.getProprietaAutorizzazioneContenuto(i);
					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SetProtocolPropAutorizzazioneContenuti associati alla PortaApplicativa[" + idPortaApplicativa + "]");
								
				
				
				// set prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPA.sizeProprietaList(); i++) {
					propProtocollo = aPA.getProprieta(i);
					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SetProtocolProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				
				// Ruoli
				n=0;
				if(aPA.getRuoli()!=null && aPA.getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPA.getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPA.getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPA.getId());
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " ruoli alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				// Scope
				n=0;
				if(aPA.getScope()!=null && aPA.getScope().sizeScopeList()>0){
					for (int j = 0; j < aPA.getScope().sizeScopeList(); j++) {
						Scope scope = aPA.getScope().getScope(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("scope", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPA.getId());
						stm.setString(2, scope.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto scope[" + scope.getNome() + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " scope alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				// Soggetti
				n=0;
				if(aPA.getSoggetti()!=null && aPA.getSoggetti().sizeSoggettoList()>0){
					for (int j = 0; j < aPA.getSoggetti().sizeSoggettoList(); j++) {
						PortaApplicativaAutorizzazioneSoggetto soggetto = aPA.getSoggetti().getSoggetto(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("tipo_soggetto", "?");
						sqlQueryObject.addInsertField("nome_soggetto", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPA.getId());
						stm.setString(2, soggetto.getTipo());
						stm.setString(3, soggetto.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto soggetto [" + soggetto.getTipo() + "/"+soggetto.getNome()+"] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " soggetti alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				// serviziapplicativi autorizzati
				if(aPA.getServiziApplicativiAutorizzati()!=null && aPA.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
	
					for (i = 0; i < aPA.getServiziApplicativiAutorizzati().sizeServizioApplicativoList(); i++) {
						PortaApplicativaAutorizzazioneServizioApplicativo servizioApplicativo = aPA.getServiziApplicativiAutorizzati().getServizioApplicativo(i);
						String nomeSA = servizioApplicativo.getNome();
						String nomeProprietarioSA = servizioApplicativo.getNomeSoggettoProprietario();
						String tipoProprietarioSA = servizioApplicativo.getTipoSoggettoProprietario();
						if (nomeSA == null || nomeSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Nome del ServizioApplicativo associato non valido.");
						if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Nome Proprietario del ServizioApplicativo associato non valido.");
						if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Tipo Proprietario del ServizioApplicativo associato non valido.");
	
						long idSA = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
	
						if (idSA <= 0)
							throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
	
						stm.setLong(1, idPortaApplicativa);
						stm.setLong(2, idSA);
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " servizi applicativi autorizzati associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				}
				
				
				// Azioni
				n=0;
				if(aPA.getAzione()!=null && aPA.getAzione().sizeAzioneDelegataList()>0){
					for (int j = 0; j < aPA.getAzione().sizeAzioneDelegataList(); j++) {
						String azioneDelegata = aPA.getAzione().getAzioneDelegata(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("azione", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPA.getId());
						stm.setString(2, azioneDelegata);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto azione delegata [" + azioneDelegata + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " azioni delegate alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// Cache Regole
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE);
						sqlQueryObject.addInsertField("id_porta", "?");
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							sqlQueryObject.addInsertField("status_min", "?");
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							sqlQueryObject.addInsertField("status_max", "?");
						}
						sqlQueryObject.addInsertField("fault", "?");
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							sqlQueryObject.addInsertField("cache_seconds", "?");
						}
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						int indexCR = 1;
						stm.setLong(indexCR++, aPA.getId());
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMin());
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMax());
						}
						stm.setInt(indexCR++, regola.getFault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							stm.setInt(indexCR++, regola.getCacheTimeoutSeconds());
						}
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta regola di cache alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " regole di cache alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// dumpConfigurazione
				CRUDDumpConfigurazione(type, con, aPA.getDump(), aPA.getId(), CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PA);
				
				
				// trasformazioni
				CRUDTrasformazioni(type, con, aPA.getTrasformazioni(), aPA.getId(), false);
				
				
				// extendedInfo
				i=0;
				if(aPA.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPA.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPA, aPA.getExtendedInfo(i), CRUDType.CREATE);
						}
					}
				}
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + i + " associazioni ExtendedInfo<->PortaApplicativa associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				break;

			case UPDATE:
				// UPDATE
				String oldNomePA = null;
				if(aPA.getOldIDPortaApplicativaForUpdate()!=null){
					oldNomePA = aPA.getOldIDPortaApplicativaForUpdate().getNome();
				}
				if (oldNomePA == null || oldNomePA.equals(""))
					oldNomePA = nomePorta;

				//campi obbligatori
				//servizio ci deve essere l'id oppure tipo e nome
				tipoServizio = (servizio != null ? servizio.getTipo() : null);
				nomeServizio = (servizio != null ? servizio.getNome() : null);
				versioneServizio = (servizio != null ? servizio.getVersione() : null);
				//se l'id non e' valido allora devono esserci necessariamente il tipo e il nome
				if(idServizio<=0){
					if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
					if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
					if(versioneServizio==null) throw new DriverConfigurazioneException("Versione Servizio non impostato.");
				}
				
				//Azione
				nomeAzione = (azione != null ? azione.getNome() : null);
				patternAzione = (azione != null ? azione.getPattern() : null);
				nomePortaDeleganteAzione = (azione != null ? azione.getNomePortaDelegante() : null);
				forceInterfaceBased = (azione != null ? azione.getForceInterfaceBased() : null);
				modeAzione = (azione != null ? azione.getIdentificazione() : null);
				//Se il bean Azione nn e' presente allora non controllo nulla
				if(azione!=null){
					if(modeAzione==null || modeAzione.equals("")) 
						modeAzione = PortaApplicativaAzioneIdentificazione.STATIC;
					switch (modeAzione) {
					case CONTENT_BASED:
					case URL_BASED:
					case HEADER_BASED:
						if(patternAzione==null || patternAzione.equals("")) throw new DriverConfigurazioneException("Pattern Azione non impostato.");
						nomeAzione=null;
						break;
					case DELEGATED_BY:
						if(nomePortaDeleganteAzione==null || nomePortaDeleganteAzione.equals("")) throw new DriverConfigurazioneException("Nome Porta Delegante Azione non impostata.");
						nomeAzione=null;
						break;
					case INPUT_BASED:
					case SOAP_ACTION_BASED:
					case INTERFACE_BASED:
					case PROTOCOL_BASED:
						//nessun campo obbligatorio
						break;
					case STATIC:
						//ci deve essere il nome
						if(nomeAzione==null || nomeAzione.equals("")) throw new DriverConfigurazioneException("Nome Azione non impostato.");
						patternAzione=null;
						break;
					default:
						break;
					}
				}

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("id_soggetto_virtuale", "?");
				sqlQueryObject.addUpdateField("tipo_soggetto_virtuale", "?");
				sqlQueryObject.addUpdateField("nome_soggetto_virtuale", "?");
				sqlQueryObject.addUpdateField("id_servizio", "?");
				sqlQueryObject.addUpdateField("tipo_servizio", "?");
				sqlQueryObject.addUpdateField("servizio", "?");
				sqlQueryObject.addUpdateField("versione_servizio", "?");
				sqlQueryObject.addUpdateField("azione", "?");
				sqlQueryObject.addUpdateField("mode_azione", "?");
				sqlQueryObject.addUpdateField("pattern_azione", "?");
				sqlQueryObject.addUpdateField("nome_porta_delegante_azione", "?");
				sqlQueryObject.addUpdateField("force_interface_based_azione", "?");
				sqlQueryObject.addUpdateField("mtom_request_mode", "?");
				sqlQueryObject.addUpdateField("mtom_response_mode", "?");
				sqlQueryObject.addUpdateField("security", "?");
				sqlQueryObject.addUpdateField("security_mtom_req", "?");
				sqlQueryObject.addUpdateField("security_mtom_res", "?");
				sqlQueryObject.addUpdateField("security_request_mode", "?");
				sqlQueryObject.addUpdateField("security_response_mode", "?");
				sqlQueryObject.addUpdateField("nome_porta", "?");
				sqlQueryObject.addUpdateField("ricevuta_asincrona_sim", "?");
				sqlQueryObject.addUpdateField("ricevuta_asincrona_asim", "?");
				sqlQueryObject.addUpdateField("integrazione", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_stato", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_mtom", "?");	
				sqlQueryObject.addUpdateField("id_soggetto", "?");
				sqlQueryObject.addUpdateField("allega_body", "?");
				sqlQueryObject.addUpdateField("scarta_body", "?");
				sqlQueryObject.addUpdateField("gestione_manifest", "?");
				sqlQueryObject.addUpdateField("stateless", "?");
				sqlQueryObject.addUpdateField("behaviour", "?");
				sqlQueryObject.addUpdateField("autenticazione", "?");
				sqlQueryObject.addUpdateField("autenticazione_opzionale", "?");
				sqlQueryObject.addUpdateField("token_policy", "?");
				sqlQueryObject.addUpdateField("token_opzionale", "?");
				sqlQueryObject.addUpdateField("token_validazione", "?");
				sqlQueryObject.addUpdateField("token_introspection", "?");
				sqlQueryObject.addUpdateField("token_user_info", "?");
				sqlQueryObject.addUpdateField("token_forward", "?");
				sqlQueryObject.addUpdateField("token_options", "?");
				sqlQueryObject.addUpdateField("token_authn_issuer", "?");
				sqlQueryObject.addUpdateField("token_authn_client_id", "?");
				sqlQueryObject.addUpdateField("token_authn_subject", "?");
				sqlQueryObject.addUpdateField("token_authn_username", "?");
				sqlQueryObject.addUpdateField("token_authn_email", "?");
				sqlQueryObject.addUpdateField("autorizzazione", "?");
				sqlQueryObject.addUpdateField("autorizzazione_xacml", "?");
				sqlQueryObject.addUpdateField("autorizzazione_contenuto", "?");
				sqlQueryObject.addUpdateField("ruoli_match", "?");
				sqlQueryObject.addUpdateField("scope_stato", "?");
				sqlQueryObject.addUpdateField("scope_match", "?");
				sqlQueryObject.addUpdateField("ricerca_porta_azione_delegata", "?");
				sqlQueryObject.addUpdateField("msg_diag_severita", "?");
				sqlQueryObject.addUpdateField("tracciamento_esiti", "?");
				sqlQueryObject.addUpdateField("stato", "?");
				// cors
				sqlQueryObject.addUpdateField("cors_stato", "?");
				sqlQueryObject.addUpdateField("cors_tipo", "?");
				sqlQueryObject.addUpdateField("cors_all_allow_origins", "?");
				sqlQueryObject.addUpdateField("cors_allow_credentials", "?");
				sqlQueryObject.addUpdateField("cors_allow_max_age", "?");
				sqlQueryObject.addUpdateField("cors_allow_max_age_seconds", "?");
				sqlQueryObject.addUpdateField("cors_allow_origins", "?");
				sqlQueryObject.addUpdateField("cors_allow_headers", "?");
				sqlQueryObject.addUpdateField("cors_allow_methods", "?");
				sqlQueryObject.addUpdateField("cors_allow_expose_headers", "?");
				// responseCaching
				sqlQueryObject.addUpdateField("response_cache_stato", "?");
				sqlQueryObject.addUpdateField("response_cache_seconds", "?");
				sqlQueryObject.addUpdateField("response_cache_max_msg_size", "?");
				sqlQueryObject.addUpdateField("response_cache_control_nocache", "?");
				sqlQueryObject.addUpdateField("response_cache_control_maxage", "?");
				sqlQueryObject.addUpdateField("response_cache_control_nostore", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_url", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_query", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_query_list", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_headers", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_hdr_list", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_payload", "?");
				// servizio applicativo default
				sqlQueryObject.addUpdateField("id_sa_default", "?");
				// id
				sqlQueryObject.addUpdateField("id_accordo", "?");
				sqlQueryObject.addUpdateField("id_port_type", "?");
				sqlQueryObject.addUpdateField("scadenza_correlazione_appl", "?");
				// options
				sqlQueryObject.addUpdateField("options", "?");
				
				sqlQueryObject.addWhereCondition("nome_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = con.prepareStatement(sqlQuery);

				idPortaApplicativa = DBUtils.getIdPortaApplicativa(oldNomePA, con, DriverConfigurazioneDB_LIB.tipoDB);
				//  Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(idPortaApplicativa<=0) {
					idPortaApplicativa = DBUtils.getIdPortaApplicativa(oldNomePA, con, DriverConfigurazioneDB_LIB.tipoDB);
				}
				if(idPortaApplicativa<=0) 
					throw new DriverConfigurazioneException("Impossibile recuperare l'id della Porta Applicativa nomePA["+oldNomePA+"] (old["+
							(aPA.getOldIDPortaApplicativaForUpdate()!=null?aPA.getOldIDPortaApplicativaForUpdate().getNome():null)+"]");
										
				index = 1;
				
				stm.setString(index++, descrizione);
				stm.setLong(index++, idSoggVirt);
				stm.setString(index++, tipoSoggVirt);//(soggVirt != null ? soggVirt.getTipo() : null));
				stm.setString(index++, nomeSoggVirt);//(soggVirt != null ? soggVirt.getNome() : null));
				stm.setLong(index++, idServizio);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setInt(index++, versioneServizio);
				stm.setString(index++, (azione != null ? azione.getNome() : null));
				if(modeAzione!=null){
					stm.setString(index++, modeAzione.toString());
				}
				else{
					stm.setString(index++, null);
				}
				stm.setString(index++, patternAzione);
				stm.setString(index++, nomePortaDeleganteAzione);
				stm.setString(index++, getValue(forceInterfaceBased));
				// mtom
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_response));
				stm.setString(index++, securityRequestMode);
				stm.setString(index++, securityResponseMode);
				// nomePorta
				stm.setString(index++, nomePorta);
				//ricevuta asincrona_asimmetrica/simmetrica
				stm.setString(index++, aPA.getRicevutaAsincronaSimmetrica()!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getRicevutaAsincronaSimmetrica()) : null);
				stm.setString(index++, aPA.getRicevutaAsincronaAsimmetrica()!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getRicevutaAsincronaAsimmetrica()) : null);
				//integrazione
				stm.setString(index++, aPA.getIntegrazione()!=null ? aPA.getIntegrazione() : null);
				//validazione xsd
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getValidazioneContenutiApplicativi().getStato()) : null);
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getValidazioneContenutiApplicativi().getTipo()) : null);
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) : null);
				stm.setLong(index++, idProprietario);//il nuovo proprietario se cambiato
				// InvocazionePorta: funzionalita' attachment
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getAllegaBody()) : null);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getScartaBody()) : null);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getGestioneManifest()) : null);
				// Stateless
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getStateless()) : null);
				// Behaviour
				stm.setString(index++, behaviour);
				// Autenticazione
				stm.setString(index++, autenticazione);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getAutenticazioneOpzionale()) : null);
				// token
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getPolicy() : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getTokenOpzionale()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getValidazione()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getIntrospection()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getUserInfo()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getForward()) : null);
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getOptions() : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getIssuer()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getClientId()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getSubject()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getUsername()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getEmail()) : null);
				// Autorizzazione
				stm.setString(index++, autorizzazione);
				stm.setString(index++, autorizzazioneXacmlPolicy);
				stm.setString(index++, aPA!=null ? aPA.getAutorizzazioneContenuto() : null);
				// Ruoli
				stm.setString(index++, aPA!=null && aPA.getRuoli()!=null && aPA.getRuoli().getMatch()!=null ? 
						aPA.getRuoli().getMatch().getValue() : null);
				// Scope
				stm.setString(index++, aPA!=null && aPA.getScope()!=null && aPA.getScope().getStato()!=null ? 
						DriverConfigurazioneDB_LIB.getValue(aPA.getScope().getStato()) : null);
				stm.setString(index++, aPA!=null && aPA.getScope()!=null && aPA.getScope().getMatch()!=null ? 
						aPA.getScope().getMatch().getValue() : null);
				// RicercaPortaAzioneDelegata
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getRicercaPortaAzioneDelegata()) : null);
				// Tracciamento
				stm.setString(index++, msg_diag_severita);
				stm.setString(index++, tracciamento_esiti);
				// Stato
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDB_LIB.getValue(aPA.getStato()) : null);
				// cors
				stm.setString(index++, cors_stato);
				stm.setString(index++, cors_tipo);
				stm.setString(index++, cors_all_allow_origins);
				stm.setString(index++, cors_allow_credentials);
				stm.setInt(index++, cors_allow_max_age);
				if(cors_allow_max_age_seconds!=null) {
					stm.setInt(index++, cors_allow_max_age_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				stm.setString(index++, cors_allow_origins);
				stm.setString(index++, cors_allow_headers);
				stm.setString(index++, cors_allow_methods);
				stm.setString(index++, cors_allow_expose_headers);				
				// responseCaching
				stm.setString(index++, response_cache_stato);
				if(response_cache_seconds!=null) {
					stm.setInt(index++, response_cache_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				if(response_cache_max_msg_size!=null) {
					stm.setLong(index++, response_cache_max_msg_size);
				}
				else {
					stm.setNull(index++, java.sql.Types.BIGINT);
				}
				stm.setInt(index++, response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setString(index++, response_cache_hash_url);
				stm.setString(index++, response_cache_hash_query);
				stm.setString(index++, response_cache_hash_query_list);
				stm.setString(index++, response_cache_hash_headers);
				stm.setString(index++, response_cache_hash_headers_list);
				stm.setString(index++, response_cache_hash_payload);
				
				// servizio applicativo default
				idServizioApplicativoDefault = -1;
				if(aPA.getServizioApplicativoDefault()!=null) {
					idServizioApplicativoDefault = DBUtils.getIdServizioApplicativo(aPA.getServizioApplicativoDefault(), tipoProprietario, nomeProprietario, 
							con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
					if(idServizioApplicativoDefault<=0) {
						throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaApplicativa(CREATE)] Impossibile recuperare l'ID del servizio applicativo di default '"+aPA.getServizioApplicativoDefault()+"'.");
					}
				}
				stm.setLong(index++, idServizioApplicativoDefault);
				
				// id
				stm.setLong(index++, aPA.getIdAccordo() !=null ? aPA.getIdAccordo() : -1L);
				stm.setLong(index++, aPA.getIdPortType() !=null ? aPA.getIdPortType() : -1L);
				// ScadenzaCorrelazioneApplicativa
				stm.setString(index++, aPA.getCorrelazioneApplicativa()!=null ? aPA.getCorrelazioneApplicativa().getScadenza() : null);
				// options
				stm.setString(index++, aPA.getOptions());
				
				// where
				stm.setString(index++, oldNomePA);

				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Updated " + n + " row(s).");

				
				
				// mtom
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				stm.executeUpdate();
				stm.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				stm.executeUpdate();
				stm.close();
				
				if(mtomProcessor!=null){
				
					MtomProcessorFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
	
					i = 0;
					if(mtomProcessor.getRequestFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getPattern());
							stm.setString(4, reqParam.getContentType());
							stm.setInt(5, reqParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}	
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom request flow con id=" + idPortaApplicativa);
	
					MtomProcessorFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(mtomProcessor.getResponseFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getPattern());
							stm.setString(4, resParam.getContentType());
							stm.setInt(5, resParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom response flow con id=" + idPortaApplicativa);
					
				}
				
				
				// se security abilitato setto la lista
				// la lista contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				//if ((messageSecurity != null) && CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				// Devo settarli sempre se ci sono, in modo che lo switch abilitato-disabilitato funzioni
				if ((messageSecurity != null) )  {

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaApplicativa);
					stm.executeUpdate();
					stm.close();

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaApplicativa);
					stm.executeUpdate();
					stm.close();

					//inserisco i valori presenti nella lista 
					MessageSecurityFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					i = 0;
					if(messageSecurity.getRequestFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " request flow con id=" + idPortaApplicativa);

					MessageSecurityFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(messageSecurity.getResponseFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " response flow con id=" + idPortaApplicativa);
				}

				
				// la lista di correlazioni applicative contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				stm.executeUpdate();
				stm.close();
				
				if (corrApp != null) {
					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrApp.sizeElementoList(); i++) {
						cae = corrApp.getElemento(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa con id=" + idPortaApplicativa);
				}
				
				// la lista di correlazioni applicative risposta contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				stm.executeUpdate();
				stm.close();
				
				if (corrAppRisposta != null) {
					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaRispostaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrAppRisposta.sizeElementoList(); i++) {
						cae = corrAppRisposta.getElemento(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa risposta con id=" + idPortaApplicativa);
				}
		
				
				/*Sincronizzazione servizi applicativi*/
				//la lista dei servizi applicativi passata contiene tutti e soli i servizi applicativi necessari
				//quindi nel db devono essere presenti tutti e solo quelli presenti nella lista

				
				
				// serviziapplicativi props			
				
				idsPA_SA = new ArrayList<>(); 
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();
				while(rs.next()) {
					idsPA_SA.add(rs.getLong("id"));
				}
				rs.close();
				stm.close();
				
				if(!idsPA_SA.isEmpty()) {
					for (Long idsapa : idsPA_SA) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
						sqlQueryObject.addWhereCondition("id_porta=?");
						sqlQuery = sqlQueryObject.createSQLDelete();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idsapa);
						n=stm.executeUpdate();
						stm.close();
						DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta relative all'associazione '"+idsapa+"' (Porta Applicativa "+idPortaApplicativa+")");
					}
				}
				
				
				//TODO possibile ottimizzazione in termini di tempo
				//cancello i servizi applicativi associati alla porta e inserisco tutti e soli quelli presenti in lista
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" servizi applicativi associati alla Porta Applicativa "+idPortaApplicativa);
				
				
				
				//scrivo la lista nel db
				n=0;
				idsPA_SA = new ArrayList<>();
				for (i = 0; i < aPA.sizeServizioApplicativoList(); i++) {
					PortaApplicativaServizioApplicativo servizioApplicativo = aPA.getServizioApplicativo(i);

					String nomeSA = servizioApplicativo.getNome();
					//il tipo e il nome proprietario servizio applicativo sono gli stessi della porta delegata
					String nomeProprietarioSA = aPA.getNomeSoggettoProprietario(); 
					String tipoProprietarioSA = aPA.getTipoSoggettoProprietario(); 
					if (nomeSA == null || nomeSA.equals(""))
						throw new DriverConfigurazioneException("Nome del ServizioApplicativo associato non valido.");
					if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Nome Proprietario del ServizioApplicativo associato non valido.");
					if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Tipo Proprietario del ServizioApplicativo associato non valido.");

					long idSA = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);

					if (idSA <= 0)
						throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");

					idsPA_SA.add(idSA);
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQueryObject.addInsertField("connettore_nome", "?");
					sqlQueryObject.addInsertField("connettore_notifica", "?");
					sqlQueryObject.addInsertField("connettore_descrizione", "?");
					sqlQueryObject.addInsertField("connettore_stato", "?");
					sqlQueryObject.addInsertField("connettore_filtri", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
				
					int indexSA = 1;
					stm.setLong(indexSA++, idPortaApplicativa);
					stm.setLong(indexSA++, idSA);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? servizioApplicativo.getDatiConnettore().getNome() : null);
					stm.setInt(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? (servizioApplicativo.getDatiConnettore().isNotifica() ? CostantiDB.TRUE : CostantiDB.FALSE) : CostantiDB.FALSE);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? servizioApplicativo.getDatiConnettore().getDescrizione() : null);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? getValue(servizioApplicativo.getDatiConnettore().getStato()) : null);
					
					String filtri = null; 
					if(servizioApplicativo.getDatiConnettore()!=null) {
						if(servizioApplicativo.getDatiConnettore().getFiltroList()!=null && servizioApplicativo.getDatiConnettore().sizeFiltroList()>0) {
							StringBuilder bf = new StringBuilder();
							for (int k = 0; k < servizioApplicativo.getDatiConnettore().sizeFiltroList(); k++) {
								if(k>0) {
									bf.append(",");
								}
								bf.append(servizioApplicativo.getDatiConnettore().getFiltro(k));
							}
							filtri = bf.toString();
						}
					}
					stm.setString(indexSA++, filtri);

					stm.executeUpdate();
					stm.close();
					n++;
					DriverConfigurazioneDB_LIB.log.debug("Aggiunta associazione PortaApplicativa<->ServizioApplicativo [" + idPortaApplicativa + "]<->[" + idSA + "]");
				}

				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " associazioni PortaApplicativa<->ServizioApplicativo associati alla PortaDelegata[" + idPortaApplicativa + "]");


				// serviziapplicativi props			
				
				for (i = 0; i < aPA.sizeServizioApplicativoList(); i++) {
					PortaApplicativaServizioApplicativo servizioApplicativo = aPA.getServizioApplicativo(i);
					String nomeSA = servizioApplicativo.getNome();
					
					if(servizioApplicativo.getDatiConnettore()!=null && servizioApplicativo.getDatiConnettore().sizeProprietaList()>0) {
					
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
						sqlQueryObject.addSelectField("id");
						sqlQueryObject.addWhereCondition("id_porta=?");
						sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						
						long idSA = idsPA_SA.get(i);
						
						long idPA_SA = -1;
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setLong(2, idSA);
						rs = stm.executeQuery();
						if(rs.next()) {
							idPA_SA = rs.getLong("id");
						}
						rs.close();
						stm.close();
						
						if(idPA_SA<=0) {
							throw new DriverConfigurazioneException("Impossibile recuperare l'id della registrazione del Servizio Applicativo [" + nomeSA + "](id:"+idSA+") alla porta applicativa con id:"+idPortaApplicativa );
						}
						
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("valore", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						
						int j = 0;
						for ( ; j < servizioApplicativo.getDatiConnettore().sizeProprietaList(); j++) {
							
							Proprieta p = servizioApplicativo.getDatiConnettore().getProprieta(j);
							
							stm.setLong(1, idPA_SA);
							stm.setString(2, p.getNome());
							stm.setString(3, p.getValore());
							stm.executeUpdate();
							
						}
	
						stm.close();
						DriverConfigurazioneDB_LIB.log.debug("Insererted " + j + " SetSAProp associati al Servizio Applicativo [" + nomeSA + "](id:"+idSA+") della PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				
				// set prop behaviour
				
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				
				if(aPA.getBehaviour()!=null) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					for (i = 0; i < aPA.getBehaviour().sizeProprietaList(); i++) {
						propProtocollo = aPA.getBehaviour().getProprieta(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, propProtocollo.getNome());
						stm.setString(3, propProtocollo.getValore());
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SeBehaviourProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				}
				
				
				
				
				
				
				
				
				
				/*Proprieta autenticazione associate alla Porta Applicativa*/
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta di autenticazione associate alla Porta Applicativa "+idPortaApplicativa);
				// set prop
				int newProps = 0;
				for (i = 0; i < aPA.sizeProprietaAutenticazioneList(); i++) {
					propProtocollo = aPA.getProprietaAutenticazione(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolPropAutenticazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				/*Proprieta autorizzazione associate alla Porta Applicativa*/
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta di autorizzazione associate alla Porta Applicativa "+idPortaApplicativa);
				// set prop
				newProps = 0;
				for (i = 0; i < aPA.sizeProprietaAutorizzazioneList(); i++) {
					propProtocollo = aPA.getProprietaAutorizzazione(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolPropAutorizzazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				
				/*Proprieta autorizzazione contenuti associate alla Porta Applicativa*/
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta di autorizzazione contenuti associate alla Porta Applicativa "+idPortaApplicativa);
				// set prop
				newProps = 0;
				for (i = 0; i < aPA.sizeProprietaAutorizzazioneContenutoList(); i++) {
					propProtocollo = aPA.getProprietaAutorizzazioneContenuto(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolPropAutorizzazioneContenuto associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				
				
				
				/*Proprieta associate alla Porta Applicativa*/
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta associate alla Porta Applicativa "+idPortaApplicativa);
				// set prop
				newProps = 0;
				for (i = 0; i < aPA.sizeProprietaList(); i++) {
					propProtocollo = aPA.getProprieta(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				
				
				// Ruoli
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" ruoli associati alla Porta Applicativa "+idPortaApplicativa);
				
				n=0;
				if(aPA.getRuoli()!=null && aPA.getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPA.getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPA.getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " ruoli alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// Scope
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" scope associati alla Porta Applicativa "+idPortaApplicativa);
				
				n=0;
				if(aPA.getScope()!=null && aPA.getScope().sizeScopeList()>0){
					for (int j = 0; j < aPA.getScope().sizeScopeList(); j++) {
						Scope scope = aPA.getScope().getScope(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("scope", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, scope.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto scope[" + scope.getNome() + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " scope alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// Soggetti
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" soggetti associati alla Porta Applicativa "+idPortaApplicativa);
				
				n=0;
				if(aPA.getSoggetti()!=null && aPA.getSoggetti().sizeSoggettoList()>0){
					for (int j = 0; j < aPA.getSoggetti().sizeSoggettoList(); j++) {
						PortaApplicativaAutorizzazioneSoggetto soggetto = aPA.getSoggetti().getSoggetto(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("tipo_soggetto", "?");
						sqlQueryObject.addInsertField("nome_soggetto", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, soggetto.getTipo());
						stm.setString(3, soggetto.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto soggetto [" + soggetto.getTipo() + "/"+soggetto.getNome()+"] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " soggetti alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				
				//la lista dei servizi applicativi passata contiene tutti e soli i servizi applicativi necessari
				//quindi nel db devono essere presenti tutti e solo quelli presenti nella lista

				//TODO possibile ottimizzazione in termini di tempo
				//cancello i servizi applicativi associati alla porta e inserisco tutti e soli quelli presenti in lista
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" servizi applicativi autorizzati associati alla Porta Applicativa "+idPortaApplicativa);
				
				//scrivo la lista nel db
				if(aPA.getServiziApplicativiAutorizzati()!=null && aPA.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
	
					for (i = 0; i < aPA.getServiziApplicativiAutorizzati().sizeServizioApplicativoList(); i++) {
						PortaApplicativaAutorizzazioneServizioApplicativo servizioApplicativoAutorizzato = aPA.getServiziApplicativiAutorizzati().getServizioApplicativo(i);
						String nomeSA = servizioApplicativoAutorizzato.getNome();
						String nomeProprietarioSA = servizioApplicativoAutorizzato.getNomeSoggettoProprietario();
						String tipoProprietarioSA = servizioApplicativoAutorizzato.getTipoSoggettoProprietario();
						if (nomeSA == null || nomeSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Nome del ServizioApplicativo associato non valido.");
						if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Nome Proprietario del ServizioApplicativo associato non valido.");
						if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Tipo Proprietario del ServizioApplicativo associato non valido.");
	
						long idSA = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
	
						if (idSA <= 0)
							throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
	
						stm.setLong(1, idPortaApplicativa);
						stm.setLong(2, idSA);
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " servizi applicativi autorizzati associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				}
				
				
				
				// Azioni
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" azioni associati alla Porta Applicativa "+idPortaApplicativa);
				
				n=0;
				if(aPA.getAzione()!=null && aPA.getAzione().sizeAzioneDelegataList()>0){
					for (int j = 0; j < aPA.getAzione().sizeAzioneDelegataList(); j++) {
						String azioneDelegata = aPA.getAzione().getAzioneDelegata(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("azione", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, azioneDelegata);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto azione delegata [" + azioneDelegata + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " azione delegate alla PortaApplicativa[" + idPortaApplicativa + "]");
				
			
				
				
				// Cache Regole
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" regole di cache associate alla PortaApplicativa "+idPortaApplicativa);
				
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE);
						sqlQueryObject.addInsertField("id_porta", "?");
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							sqlQueryObject.addInsertField("status_min", "?");
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							sqlQueryObject.addInsertField("status_max", "?");
						}
						sqlQueryObject.addInsertField("fault", "?");
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							sqlQueryObject.addInsertField("cache_seconds", "?");
						}
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						int indexCR = 1;
						stm.setLong(indexCR++, idPortaApplicativa);
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMin());
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMax());
						}
						stm.setInt(indexCR++, regola.getFault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							stm.setInt(indexCR++, regola.getCacheTimeoutSeconds());
						}
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta regola di cache alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " regole di cache alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// dumpConfigurazione
				CRUDDumpConfigurazione(type, con, aPA.getDump(), idPortaApplicativa, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PA);
				
				
				// trasformazioni
				CRUDTrasformazioni(type, con, aPA.getTrasformazioni(), idPortaApplicativa, false);
				
				
				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  aPA, CRUDType.UPDATE);
				}
				
				i=0;
				if(aPA.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPA.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  aPA, aPA.getExtendedInfo(i), CRUDType.UPDATE);
						}
					}
				}
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + i + " associazioni ExtendedInfo<->PortaApplicativa associati alla PortaApplicativa[" + idPortaApplicativa + "]");
								
				break;

			case DELETE:
				// DELETE
				// if(aPA.getId()==null || aPA.getId()<=0) throw new
				// DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaApplicativa(DELETE)]
				// id della PortaApplicativa non valida.");


				idPortaApplicativa = DBUtils.getIdPortaApplicativa(nomePorta, con, DriverConfigurazioneDB_LIB.tipoDB);
				if (idPortaApplicativa <= 0)
					throw new DriverConfigurazioneException("Non e' stato possibile recuperare l'id della Porta Applicativa.");

				// dumpConfigurazione
				CRUDDumpConfigurazione(type, con, aPA.getDump(), idPortaApplicativa, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PA);
				
				// trasformazioni
				CRUDTrasformazioni(type, con, aPA.getTrasformazioni(), idPortaApplicativa, false);
				
				// Cache Regole
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" regole di cache associate alla Porta Applicativa "+idPortaApplicativa);
				
				// azioni delegate
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellate "+n+" azioni delegate associate alla Porta Applicativa "+idPortaApplicativa);
				
				// sa autorizzati
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" servizi applicativi autorizzati associati alla Porta Applicativa "+idPortaApplicativa);
				
				// soggetti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" soggetti associati alla Porta Applicativa "+idPortaApplicativa);
				
				// ruoli
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" ruoli associati alla Porta Applicativa "+idPortaApplicativa);
				
				// scope
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" scope associati alla Porta Applicativa "+idPortaApplicativa);
				
				// mtom
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " request flow con id=" + idPortaApplicativa);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " response flow con id=" + idPortaApplicativa);
				
				
				// message security
				//if ( CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " request flow con id=" + idPortaApplicativa);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " response flow con id=" + idPortaApplicativa);
				//}

				// serviziapplicativi props
				idsPA_SA = new ArrayList<>(); 
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();
				while(rs.next()) {
					idsPA_SA.add(rs.getLong("id"));
				}
				rs.close();
				stm.close();
				
				if(!idsPA_SA.isEmpty()) {
					for (Long idsapa : idsPA_SA) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
						sqlQueryObject.addWhereCondition("id_porta=?");
						sqlQuery = sqlQueryObject.createSQLDelete();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idsapa);
						n=stm.executeUpdate();
						stm.close();
						DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta relative all'associazione '"+idsapa+"' (Porta Applicativa "+idPortaApplicativa+")");
					}
				}
				
				// serviziapplicativi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " servizi applicativi associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				// cancello le prop relative al behaviour
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " BehaviourProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				// cancello anche le flow di request/response associate a questa
				// porta applicativa
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " security_request flow associate alla PortaApplicativa[" + idPortaApplicativa + "]");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " security_response flow associate alla PortaApplicativa[" + idPortaApplicativa + "]");

				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);

				n = stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " correlazione associate alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);

				n = stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " correlazione della risposta associate alla PortaApplicativa[" + idPortaApplicativa + "]");
								
				// cancello le prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				// cancello le prop di autorizzazione contenuti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolPropAutorizzazioneContenuto associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				// cancello le prop di autorizzazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolPropAutorizzazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				// cancello le prop di autenticazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolPropAutenticazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  aPA, CRUDType.DELETE);
				}
				
				// porta applicativa
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.addWhereCondition("nome_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				stm.setLong(2, idProprietario);
				stm.setString(3, nomePorta);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " row(s).");

				break;
			}

			return idPortaApplicativa;
		} catch (DriverConfigurazioneException e) {
			throw e;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaApplicativa] SQLException [" + se.getMessage() + "].",se);
		} catch (Exception e) {
			throw new DriverConfigurazioneException("Errore durante operazione("+type+") CRUDPortaApplicativa.",e);
		}finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}

	public static void CRUDRoutingTable(int type, RoutingTable aRT, Connection con) throws DriverConfigurazioneException {
		PreparedStatement updateStmt = null;
		PreparedStatement insertStmt = null;
		PreparedStatement updateStmtSelectRegistri = null;
		ResultSet rsSelectRegistri = null;
		String updateQuery = "";

		int i = 0;

		RoutingTableDestinazione rtd = null;
		Route route = null;
		RouteGateway gw = null;
		RouteRegistro rg = null;
		String tipo = null;
		String nome = null;
		long idRoute = 0;

		try {

			switch (type) {
			case CREATE:

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("routing_enabled", "?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				if(aRT.getAbilitata()!=null && aRT.getAbilitata())
					updateStmt.setString(1, CostantiConfigurazione.ABILITATO.toString());
				else
					updateStmt.setString(1, CostantiConfigurazione.DISABILITATO.toString());
				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + DBUtils.formatSQLString(updateQuery, (aRT.getAbilitata()!=null && aRT.getAbilitata())));
				updateStmt.executeUpdate();
				updateStmt.close();

				// CREATE
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.ROUTING);
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("tiporotta", "?");
				sqlQueryObject.addInsertField("tiposoggrotta", "?");
				sqlQueryObject.addInsertField("nomesoggrotta", "?");
				sqlQueryObject.addInsertField("registrorotta", "?");
				sqlQueryObject.addInsertField("is_default", "?");
				updateQuery = sqlQueryObject.createSQLInsert();

				i = 0;
				if(aRT.getDefault()!=null){
					RoutingTableDefault rtDefault = aRT.getDefault();
					for (i = 0; i < rtDefault.sizeRouteList(); i++) {
						route = rtDefault.getRoute(i);
	
						gw = route.getGateway();// rotta gateway
						rg = route.getRegistro();// rotta registro
	
						updateStmt = con.prepareStatement(updateQuery);
	
						updateStmt.setString(1, null);// nn ho tipo
						updateStmt.setString(2, null);// nn ho nome
						updateStmt.setString(3, (gw != null ? "gateway" : "registro"));
						updateStmt.setString(4, (gw != null ? gw.getTipo() : null));// se
						// rotta
						// gateway
						// setto
						// tiposoggrotta
						updateStmt.setString(5, (gw != null ? gw.getNome() : null));// se
						// rotta
						// gateway
						// setto
						// nomesoggrotta
						long registroRotta = 0;
						if(rg!=null){
							if(rg.getId()<=0){
								if(rg.getNome()!=null && ("".equals(rg.getNome())==false)){
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
									sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
									sqlQueryObject.addSelectField("id");
									sqlQueryObject.addWhereCondition("nome = ?");
									String selectQuery = sqlQueryObject.createSQLQuery();
									updateStmtSelectRegistri = con.prepareStatement(selectQuery);
									updateStmtSelectRegistri.setString(1, rg.getNome());
									rsSelectRegistri = updateStmtSelectRegistri.executeQuery();
									if(rsSelectRegistri!=null && rsSelectRegistri.next()){
										registroRotta = rsSelectRegistri.getLong("id");
									}
									rsSelectRegistri.close();
									updateStmtSelectRegistri.close();
								}
							}
						}
						updateStmt.setLong(6, registroRotta);
	
						updateStmt.setInt(7, CostantiDB.TRUE);
	
						updateStmt.executeUpdate();
						updateStmt.close();
					}
				}

				DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " Default route.");

				for (i = 0; i < aRT.sizeDestinazioneList(); i++) {
					rtd = aRT.getDestinazione(i);
					nome = rtd.getNome();
					tipo = rtd.getTipo();

					for (int j = 0; j < rtd.sizeRouteList(); j++) {
						route = rtd.getRoute(j);
						gw = route.getGateway();// rotta gateway
						rg = route.getRegistro();// rotta registro

						updateStmt = con.prepareStatement(updateQuery);

						updateStmt.setString(1, tipo);
						updateStmt.setString(2, nome);
						updateStmt.setString(3, (gw != null ? "gateway" : "registro"));
						updateStmt.setString(4, (gw != null ? gw.getTipo() : null));// se
						// rotta
						// gateway
						// setto
						// tiposoggrotta
						updateStmt.setString(5, (gw != null ? gw.getNome() : null));// se
						// rotta
						// gateway
						// setto
						// nomesoggrotta
						long registroRotta = 0;
						if(rg!=null){
							if(rg.getId()<=0){
								if(rg.getNome()!=null && ("".equals(rg.getNome())==false)){
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
									sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
									sqlQueryObject.addSelectField("id");
									sqlQueryObject.addWhereCondition("nome = ?");
									String selectQuery = sqlQueryObject.createSQLQuery();
									updateStmtSelectRegistri = con.prepareStatement(selectQuery);
									updateStmtSelectRegistri.setString(1, rg.getNome());
									rsSelectRegistri = updateStmtSelectRegistri.executeQuery();
									if(rsSelectRegistri!=null && rsSelectRegistri.next()){
										registroRotta = rsSelectRegistri.getLong("id");
									}
									rsSelectRegistri.close();
									updateStmtSelectRegistri.close();
								}
							}
						}
						updateStmt.setLong(6, registroRotta);

						updateStmt.setInt(7, CostantiDB.FALSE);

						updateStmt.executeUpdate();
						updateStmt.close();
					}
				}

				DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " Destination route.");

				break;

			case 2:
				// UPDATE

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("routing_enabled", "?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				if(aRT.getAbilitata()!=null && aRT.getAbilitata())
					updateStmt.setString(1, CostantiConfigurazione.ABILITATO.toString());
				else
					updateStmt.setString(1, CostantiConfigurazione.DISABILITATO.toString());
				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + DBUtils.formatSQLString(updateQuery, aRT.getAbilitata()!=null && aRT.getAbilitata()));
				updateStmt.executeUpdate();
				updateStmt.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.ROUTING);
				sqlQueryObject.addUpdateField("tipo", "?");
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("tiporotta", "?");
				sqlQueryObject.addUpdateField("tiposoggrotta", "?");
				sqlQueryObject.addUpdateField("nomesoggrotta", "?");
				sqlQueryObject.addUpdateField("registrorotta", "?");
				sqlQueryObject.addUpdateField("is_default", "?");
				sqlQueryObject.addWhereCondition("id = ?");
				updateQuery = sqlQueryObject.createSQLUpdate();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.ROUTING);
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("tiporotta", "?");
				sqlQueryObject.addInsertField("tiposoggrotta", "?");
				sqlQueryObject.addInsertField("nomesoggrotta", "?");
				sqlQueryObject.addInsertField("registrorotta", "?");
				sqlQueryObject.addInsertField("is_default", "?");
				String insertQuery = sqlQueryObject.createSQLInsert();

				/**
				 * La lista contiene tutte e sole le entry necessarie
				 */
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addDeleteTable(CostantiDB.ROUTING);
				//sqlQueryObject.addWhereCondition("is_default<>?");//cancello le rotte non di default
				String queryDelete = sqlQueryObject.createSQLDelete();
				DriverConfigurazioneDB_LIB.log.debug("DELETING Destination Route : "+queryDelete);
				updateStmt = con.prepareStatement(queryDelete);
				//updateStmt.setInt(1, CostantiDB.TRUE);
				int n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " Destination route.");

				i = 0;
				if(aRT.getDefault()!=null){
					RoutingTableDefault rtDefault = aRT.getDefault();
					for (i = 0; i < rtDefault.sizeRouteList(); i++) {
						route = rtDefault.getRoute(i);

						idRoute = route.getId();
	
						gw = route.getGateway();// rotta gateway
						rg = route.getRegistro();// rotta registro
	
						long registroRotta = 0;
						if(rg!=null){
							if(rg.getId()<=0){
								if(rg.getNome()!=null && ("".equals(rg.getNome())==false)){
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
									sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
									sqlQueryObject.addSelectField("id");
									sqlQueryObject.addWhereCondition("nome = ?");
									String selectQuery = sqlQueryObject.createSQLQuery();
									updateStmtSelectRegistri = con.prepareStatement(selectQuery);
									updateStmtSelectRegistri.setString(1, rg.getNome());
									rsSelectRegistri = updateStmtSelectRegistri.executeQuery();
									if(rsSelectRegistri!=null && rsSelectRegistri.next()){
										registroRotta = rsSelectRegistri.getLong("id");
									}
									rsSelectRegistri.close();
									updateStmtSelectRegistri.close();
								}
							}
						}
	
						insertStmt = con.prepareStatement(insertQuery);
	
						insertStmt.setString(1, null);// nn ho tipo
						insertStmt.setString(2, null);// nn ho nome
						insertStmt.setString(3, (gw != null ? "gateway" : "registro"));
						insertStmt.setString(4, (gw != null ? gw.getTipo() : null));// se
						// rotta
						// gateway
						// setto
						// tiposoggrotta
						insertStmt.setString(5, (gw != null ? gw.getNome() : null));// se
						// rotta
						// gateway
						// setto
						// nomesoggrotta
	
						insertStmt.setLong(6, registroRotta);
	
						insertStmt.setInt(7, CostantiDB.TRUE);
						insertStmt.executeUpdate();
						insertStmt.close();
	
					}
				}

				DriverConfigurazioneDB_LIB.log.debug("Updated " + i + " Default route.");

				for (i = 0; i < aRT.sizeDestinazioneList(); i++) {
					rtd = aRT.getDestinazione(i);
					nome = rtd.getNome();
					tipo = rtd.getTipo();

					for (int j = 0; j < rtd.sizeRouteList(); j++) {
						route = rtd.getRoute(j);

						idRoute = route.getId();

						gw = route.getGateway();// rotta gateway
						rg = route.getRegistro();// rotta registro

						long registroRotta = 0;
						if(rg!=null){
							if(rg.getId()<=0){
								if(rg.getNome()!=null && ("".equals(rg.getNome())==false)){
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
									sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
									sqlQueryObject.addSelectField("id");
									sqlQueryObject.addWhereCondition("nome = ?");
									String selectQuery = sqlQueryObject.createSQLQuery();
									updateStmtSelectRegistri = con.prepareStatement(selectQuery);
									updateStmtSelectRegistri.setString(1, rg.getNome());
									rsSelectRegistri = updateStmtSelectRegistri.executeQuery();
									if(rsSelectRegistri!=null && rsSelectRegistri.next()){
										registroRotta = rsSelectRegistri.getLong("id");
									}
									rsSelectRegistri.close();
									updateStmtSelectRegistri.close();
								}
							}
						}


						insertStmt = con.prepareStatement(insertQuery);

						insertStmt.setString(1, tipo);
						insertStmt.setString(2, nome);
						insertStmt.setString(3, (gw != null ? "gateway" : "registro"));
						insertStmt.setString(4, (gw != null ? gw.getTipo() : null));
						insertStmt.setString(5, (gw != null ? gw.getNome() : null));

						insertStmt.setLong(6, registroRotta);

						insertStmt.setInt(7, CostantiDB.FALSE);
						insertStmt.executeUpdate();
						insertStmt.close();

					}
				}

				DriverConfigurazioneDB_LIB.log.debug("Updated " + i + " Destination route.");



				break;

			case 3:
				// DELETE
				i = 0;
				if(aRT.getDefault()!=null){
					RoutingTableDefault rtDefault = aRT.getDefault();
					for (i = 0; i < rtDefault.sizeRouteList(); i++) {
						route = rtDefault.getRoute(i);

						if (route.getId() == null || route.getId() <= 0)
							throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRoutingTable(DELETE)] id route non valida.");
						idRoute = route.getId();
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.ROUTING);
						sqlQueryObject.addWhereCondition("id=?");
						String sqlQuery = sqlQueryObject.createSQLDelete();
						updateStmt = con.prepareStatement(sqlQuery);
						updateStmt.setLong(1, idRoute);
						updateStmt.executeUpdate();
						updateStmt.close();
	
						DriverConfigurazioneDB_LIB.log.debug("Deleted " + i + " Destination route.");
					}
				}
				break;
			}

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRoutingTable] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRoutingTable] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {				
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {				
				if(insertStmt!=null)insertStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {				
				if(rsSelectRegistri!=null)
					rsSelectRegistri.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {				
				if(updateStmtSelectRegistri!=null)
					updateStmtSelectRegistri.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public static long CRUDAccessoRegistro(int type, AccessoRegistroRegistro registro, Connection con) throws DriverConfigurazioneException {
		if (registro == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Il servizio non puo essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;

		long idRegistro = 0;
		int n = 0;
		String nome = registro.getNome();
		String location = registro.getLocation();
		String tipo = registro.getTipo().toString();
		String user = registro.getUser();
		String password = registro.getPassword();

		try {
			switch (type) {
			case CREATE:
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.REGISTRI);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("location", "?");
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nome);
				updateStmt.setString(2, location);
				updateStmt.setString(3, tipo);
				updateStmt.setString(4, user);
				updateStmt.setString(5, password);

				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery, nome, location, tipo, user, password));

				n = updateStmt.executeUpdate();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.addWhereCondition("location = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, nome);
				selectStmt.setString(2, location);
				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {
					idRegistro = selectRS.getLong("id");
					registro.setId(idRegistro);
				}

				break;
			case UPDATE:

				if (registro.getId() == null || registro.getId() <= 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro(UPDATE)] L'id del Servizio e' necessario.");
				idRegistro = registro.getId();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.ROUTING);
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("location", "?");
				sqlQueryObject.addUpdateField("tipo", "?");
				sqlQueryObject.addUpdateField("utente", "?");
				sqlQueryObject.addUpdateField("password", "?");
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, registro.getNome());
				updateStmt.setString(2, registro.getLocation());
				updateStmt.setString(3, registro.getTipo().toString());
				updateStmt.setString(4, registro.getUser());
				updateStmt.setString(5, registro.getPassword());
				updateStmt.setLong(6, idRegistro);

				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery, nome, location, tipo, user, password, idRegistro));
				n = updateStmt.executeUpdate();

				break;
			case DELETE:
				if (registro.getId() == null || registro.getId() <= 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro(DELETE)] L'id del Servizio e' necessario.");
				idRegistro = registro.getId();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.REGISTRI);
				sqlQueryObject.addWhereCondition("id=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idRegistro);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery, idRegistro));
				n=updateStmt.executeUpdate();
				updateStmt.close();

				break;
			}

			if (type == CostantiDB.CREATE)
				return idRegistro;
			else
				return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public static long CRUDAccessoRegistro(int type, AccessoRegistro registro, Connection con) throws DriverConfigurazioneException {
		if (registro == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Il registro non può essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
		ResultSet selectRS = null;

		long idRegistro = 0;
		int n = 0;
		Cache arc = registro.getCache();
		String statoCache = "disabilitato";
		String dimensionecache = null;
		String algoritmocache = null;
		String idlecache = null;
		String lifecache = null;
		if (arc != null) {
			statoCache = "abilitato";
			dimensionecache = arc.getDimensione();
			if(arc.getAlgoritmo()!=null){
				algoritmocache = arc.getAlgoritmo().toString();
			}
			idlecache = arc.getItemIdleTime();
			lifecache = arc.getItemLifeSecond();
		}

		try {
			switch (type) {
			case CREATE:
			case UPDATE:

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("statocache", "?");
				sqlQueryObject.addUpdateField("dimensionecache", "?");
				sqlQueryObject.addUpdateField("algoritmocache", "?");
				sqlQueryObject.addUpdateField("idlecache", "?");
				sqlQueryObject.addUpdateField("lifecache", "?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, statoCache);
				updateStmt.setString(2, dimensionecache);
				updateStmt.setString(3, algoritmocache);
				updateStmt.setString(4, idlecache);
				updateStmt.setString(5, lifecache);

				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + DBUtils.formatSQLString(updateQuery, statoCache, dimensionecache, algoritmocache, idlecache, lifecache));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				// Elimino i registri e li ricreo
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.REGISTRI);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
				int risultato = updateStmt.executeUpdate();
				DriverConfigurazioneDB_LIB.log.debug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
				updateStmt.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.REGISTRI);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("location", "?");
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				for (int i = 0; i < registro.sizeRegistroList(); i++) {
					updateStmt = con.prepareStatement(updateQuery);
					AccessoRegistroRegistro arr = registro.getRegistro(i);
					String nome = arr.getNome();
					String location = arr.getLocation();
					String tipo = arr.getTipo().toString();
					String utente = arr.getUser();
					String password = arr.getPassword();

					updateStmt.setString(1, nome);
					updateStmt.setString(2, location);
					updateStmt.setString(3, tipo);
					updateStmt.setString(4, utente);
					updateStmt.setString(5, password);
					DriverConfigurazioneDB_LIB.log.debug("eseguo query INSERT INTO " + CostantiDB.REGISTRI + "(nome, location, tipo, utente, password) VALUES ("+
							nome+", "+location+", "+tipo+", "+utente+", "+password+")");
					updateStmt.executeUpdate();
					updateStmt.close();
				}

				break;
			case DELETE:
				// non rimuovo nulla in quanto la tabella configurazione
				// contiene solo una riga con i valori
				// che vanno modificati con la update
				break;
			}

			if (type == CostantiDB.CREATE)
				return idRegistro;
			else
				return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(selectRS!=null)selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	
	
	public static long CRUDAccessoConfigurazione(int type, AccessoConfigurazione accessoConfigurazione, Connection con) throws DriverConfigurazioneException {
		if (accessoConfigurazione == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoConfigurazione] Parametro accessoConfigurazione non può essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";

		int n = 0;
		Cache cache = accessoConfigurazione.getCache();
		String statoCache = "disabilitato";
		String dimensionecache = null;
		String algoritmocache = null;
		String idlecache = null;
		String lifecache = null;
		if (cache != null) {
			statoCache = "abilitato";
			dimensionecache = cache.getDimensione();
			if(cache.getAlgoritmo()!=null){
				algoritmocache = cache.getAlgoritmo().toString();
			}
			idlecache = cache.getItemIdleTime();
			lifecache = cache.getItemLifeSecond();
		}

		try {
			switch (type) {
			case CREATE:
			case UPDATE:

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("config_statocache", "?");
				sqlQueryObject.addUpdateField("config_dimensionecache", "?");
				sqlQueryObject.addUpdateField("config_algoritmocache", "?");
				sqlQueryObject.addUpdateField("config_idlecache", "?");
				sqlQueryObject.addUpdateField("config_lifecache", "?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, statoCache);
				updateStmt.setString(2, dimensionecache);
				updateStmt.setString(3, algoritmocache);
				updateStmt.setString(4, idlecache);
				updateStmt.setString(5, lifecache);

				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + DBUtils.formatSQLString(updateQuery, statoCache, dimensionecache, algoritmocache, idlecache, lifecache));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				break;
			case DELETE:
				// non rimuovo nulla in quanto la tabella configurazione
				// contiene solo una riga con i valori
				// che vanno modificati con la update
				break;
			}

			return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoConfigurazione] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoConfigurazione] Exception [" + se.getMessage() + "].",se);
		} finally {
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	
	
	public static long CRUDAccessoDatiAutorizzazione(int type, AccessoDatiAutorizzazione accessoDatiAutorizzazione, Connection con) throws DriverConfigurazioneException {
		if (accessoDatiAutorizzazione == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoDatiAutorizzazione] Parametro accessoDatiAutorizzazione non può essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";

		int n = 0;
		Cache cache = accessoDatiAutorizzazione.getCache();
		String statoCache = "disabilitato";
		String dimensionecache = null;
		String algoritmocache = null;
		String idlecache = null;
		String lifecache = null;
		if (cache != null) {
			statoCache = "abilitato";
			dimensionecache = cache.getDimensione();
			if(cache.getAlgoritmo()!=null){
				algoritmocache = cache.getAlgoritmo().toString();
			}
			idlecache = cache.getItemIdleTime();
			lifecache = cache.getItemLifeSecond();
		}

		try {
			switch (type) {
			case CREATE:
			case UPDATE:

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("auth_statocache", "?");
				sqlQueryObject.addUpdateField("auth_dimensionecache", "?");
				sqlQueryObject.addUpdateField("auth_algoritmocache", "?");
				sqlQueryObject.addUpdateField("auth_idlecache", "?");
				sqlQueryObject.addUpdateField("auth_lifecache", "?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, statoCache);
				updateStmt.setString(2, dimensionecache);
				updateStmt.setString(3, algoritmocache);
				updateStmt.setString(4, idlecache);
				updateStmt.setString(5, lifecache);

				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + DBUtils.formatSQLString(updateQuery, statoCache, dimensionecache, algoritmocache, idlecache, lifecache));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				break;
			case DELETE:
				// non rimuovo nulla in quanto la tabella configurazione
				// contiene solo una riga con i valori
				// che vanno modificati con la update
				break;
			}

			return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoDatiAutorizzazione] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoDatiAutorizzazione] Exception [" + se.getMessage() + "].",se);
		} finally {
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	
	public static void CRUDServiziPdD(int type, StatoServiziPdd statoServiziPdD, Connection con) throws DriverConfigurazioneException {
		if (statoServiziPdD == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServiziPdD] Le configurazioni del servizio non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
		ResultSet selectRS = null;
		

		try {
			switch (type) {
			case CREATE:
			case UPDATE:

				// Elimino le configurazioni e le ricreo
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_PDD_FILTRI);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
				int risultato = updateStmt.executeUpdate();
				DriverConfigurazioneDB_LIB.log.debug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
				updateStmt.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_PDD);
				sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
				risultato = updateStmt.executeUpdate();
				DriverConfigurazioneDB_LIB.log.debug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
				updateStmt.close();
				
				// Ricreo
				if(statoServiziPdD.getPortaDelegata()!=null){
				
					StatoServiziPddPortaDelegata sPD = statoServiziPdD.getPortaDelegata();
					
					int stato = CostantiDB.TRUE;
					if(sPD.getStato()!=null){
						stato = CostantiConfigurazione.DISABILITATO.equals(sPD.getStato()) ? CostantiDB.FALSE : CostantiDB.TRUE;
					}
					
					DriverConfigurazioneDB_LIB.registraComponentePdD(CostantiDB.COMPONENTE_SERVIZIO_PD, stato, con, sPD.getFiltroAbilitazioneList(), sPD.getFiltroDisabilitazioneList());
					
				}
				if(statoServiziPdD.getPortaApplicativa()!=null){
					
					StatoServiziPddPortaApplicativa sPA = statoServiziPdD.getPortaApplicativa();
					
					int stato = CostantiDB.TRUE;
					if(sPA.getStato()!=null){
						stato = CostantiConfigurazione.DISABILITATO.equals(sPA.getStato()) ? CostantiDB.FALSE : CostantiDB.TRUE;
					}
					
					DriverConfigurazioneDB_LIB.registraComponentePdD(CostantiDB.COMPONENTE_SERVIZIO_PA, stato, con, sPA.getFiltroAbilitazioneList(), sPA.getFiltroDisabilitazioneList());
					
				}
				if(statoServiziPdD.getIntegrationManager()!=null){
					
					StatoServiziPddIntegrationManager sIM = statoServiziPdD.getIntegrationManager();
					
					int stato = CostantiDB.TRUE;
					if(sIM.getStato()!=null){
						stato = CostantiConfigurazione.DISABILITATO.equals(sIM.getStato()) ? CostantiDB.FALSE : CostantiDB.TRUE;
					}
					
					DriverConfigurazioneDB_LIB.registraComponentePdD(CostantiDB.COMPONENTE_SERVIZIO_IM, stato, con, null, null);
					
				}
				

				break;
			case DELETE:
				// non rimuovo nulla in quanto la tabella configurazione
				// contiene solo una riga con i valori
				// che vanno modificati con la update
				break;
			}


		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(selectRS!=null)selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	private static void registraComponentePdD(String componente,int stato,Connection con,
			List<TipoFiltroAbilitazioneServizi> abilitazioni,
			List<TipoFiltroAbilitazioneServizi> disabilitazioni) throws Exception{
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
		ResultSet selectRS = null;
		

		try {
			
			// registro componente
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_PDD);
			sqlQueryObject.addInsertField("componente", "?");
			sqlQueryObject.addInsertField("stato", "?");
			updateQuery = sqlQueryObject.createSQLInsert();
			updateStmt = con.prepareStatement(updateQuery);
			DriverConfigurazioneDB_LIB.log.debug("eseguo query INSERT INTO " + CostantiDB.SERVIZI_PDD + "(componente, stato) VALUES ('"+
					componente+"', "+stato+")");
			updateStmt.setString(1, componente);
			updateStmt.setInt(2, stato);
			updateStmt.executeUpdate();
			updateStmt.close();
			
			// recuper id del componente
			long idComponente = -1;
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_PDD);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("componente=?");
			updateQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(updateQuery);
			DriverConfigurazioneDB_LIB.log.debug("eseguo query ["+updateQuery+"] per il componente ["+componente+"]");
			selectStmt.setString(1, componente);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()){
				idComponente = selectRS.getLong("id");
			}else{
				throw new Exception("Query ["+updateQuery+"] per il componente ["+componente+"] non ha ritornato risultati");
			}
			selectRS.close();
			selectStmt.close();
			
			// registro i filtri
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_PDD_FILTRI);
			sqlQueryObject.addInsertField("id_servizio_pdd", "?");
			sqlQueryObject.addInsertField("tipo_filtro", "?");
			sqlQueryObject.addInsertField("tipo_soggetto_fruitore", "?");
			sqlQueryObject.addInsertField("soggetto_fruitore", "?");
			sqlQueryObject.addInsertField("identificativo_porta_fruitore", "?");
			sqlQueryObject.addInsertField("tipo_soggetto_erogatore", "?");
			sqlQueryObject.addInsertField("soggetto_erogatore", "?");
			sqlQueryObject.addInsertField("identificativo_porta_erogatore", "?");
			sqlQueryObject.addInsertField("tipo_servizio", "?");
			sqlQueryObject.addInsertField("servizio", "?");
			sqlQueryObject.addInsertField("versione_servizio", "?");
			sqlQueryObject.addInsertField("azione", "?");
			updateQuery = sqlQueryObject.createSQLInsert();
			
			if(abilitazioni!=null){
				for (TipoFiltroAbilitazioneServizi filtro : abilitazioni) {
			
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					
					updateStmt.setLong(index++, idComponente);
					updateStmt.setString(index++,CostantiDB.TIPO_FILTRO_ABILITAZIONE_SERVIZIO_PDD);
					
					updateStmt.setString(index++,filtro.getTipoSoggettoFruitore());
					updateStmt.setString(index++,filtro.getSoggettoFruitore());
					updateStmt.setString(index++,filtro.getIdentificativoPortaFruitore());
					
					updateStmt.setString(index++,filtro.getTipoSoggettoErogatore());
					updateStmt.setString(index++,filtro.getSoggettoErogatore());
					updateStmt.setString(index++,filtro.getIdentificativoPortaErogatore());
					
					updateStmt.setString(index++,filtro.getTipoServizio());
					updateStmt.setString(index++,filtro.getServizio());
					if(filtro.getVersioneServizio()!=null){
						updateStmt.setInt(index++,filtro.getVersioneServizio());
					}
					else{
						updateStmt.setNull(index++, java.sql.Types.INTEGER);
					}
					
					updateStmt.setString(index++,filtro.getAzione());
					
					updateStmt.executeUpdate();
					updateStmt.close();
					
				}
			}
			
			if(disabilitazioni!=null){
				for (TipoFiltroAbilitazioneServizi filtro : disabilitazioni) {
			
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					
					updateStmt.setLong(index++, idComponente);
					updateStmt.setString(index++,CostantiDB.TIPO_FILTRO_DISABILITAZIONE_SERVIZIO_PDD);
					
					updateStmt.setString(index++,filtro.getTipoSoggettoFruitore());
					updateStmt.setString(index++,filtro.getSoggettoFruitore());
					updateStmt.setString(index++,filtro.getIdentificativoPortaFruitore());
					
					updateStmt.setString(index++,filtro.getTipoSoggettoErogatore());
					updateStmt.setString(index++,filtro.getSoggettoErogatore());
					updateStmt.setString(index++,filtro.getIdentificativoPortaErogatore());
					
					updateStmt.setString(index++,filtro.getTipoServizio());
					updateStmt.setString(index++,filtro.getServizio());
					if(filtro.getVersioneServizio()!=null){
						updateStmt.setInt(index++,filtro.getVersioneServizio());
					}
					else{
						updateStmt.setNull(index++, java.sql.Types.INTEGER);
					}
					
					updateStmt.setString(index++,filtro.getAzione());
					
					updateStmt.executeUpdate();
					updateStmt.close();
					
				}
			}
			 
		} finally {

			try {
				if(selectRS!=null)selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	
	public static void CRUDSystemPropertiesPdD(int type, SystemProperties systemProperties, Connection con) throws DriverConfigurazioneException {
		if (systemProperties == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDSystemPropertiesPdD] Le configurazioni per le system properties non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
		ResultSet selectRS = null;
		

		try {
			
			// Elimino le configurazioni e le ricreo per insert e update
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
			String sqlQuery = sqlQueryObject.createSQLDelete();
			updateStmt = con.prepareStatement(sqlQuery);
			DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(updateQuery));
			int risultato = updateStmt.executeUpdate();
			DriverConfigurazioneDB_LIB.log.debug("eseguo query risultato["+risultato+"]: " + DBUtils.formatSQLString(updateQuery));
			updateStmt.close();
			
			switch (type) {
			case CREATE:
			case UPDATE:
		
				for (int i = 0; i < systemProperties.sizeSystemPropertyList(); i++) {
				
					Property sp = systemProperties.getSystemProperty(i);
					String nome = sp.getNome();
					String valore = sp.getValore();
					
					// Riga
					// registro componente
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					DriverConfigurazioneDB_LIB.log.debug("eseguo query INSERT INTO " + CostantiDB.SYSTEM_PROPERTIES_PDD + "(nome, valore) VALUES ('"+
							nome+"', "+valore+")");
					updateStmt.setString(1, nome);
					updateStmt.setString(2, valore);
					updateStmt.executeUpdate();
					updateStmt.close();
					
					// recuper id del componente
					long idComponente = -1;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addWhereCondition("nome=?");
					sqlQueryObject.addWhereCondition("valore=?");
					updateQuery = sqlQueryObject.createSQLQuery();
					selectStmt = con.prepareStatement(updateQuery);
					DriverConfigurazioneDB_LIB.log.debug("eseguo query ["+updateQuery+"] per la prop nome["+nome+"] valore["+valore+"]");
					selectStmt.setString(1, nome);
					selectStmt.setString(2, valore);
					selectRS = selectStmt.executeQuery();
					if(selectRS.next()){
						idComponente = selectRS.getLong("id");
					}else{
						throw new Exception("Query ["+updateQuery+"] per la prop nome["+nome+"] valore["+valore+"] non ha ritornato risultati");
					}
					selectRS.close();
					selectStmt.close();
					
					sp.setId(idComponente);
				}

				break;
			case DELETE:
				// non rimuovo in quanto gia fatto sopra.
				break;
			}


		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDAccessoRegistro] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(selectRS!=null)selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	
	
	
	public static void CRUDGenericProperties(int type, GenericProperties genericProperties, Connection con) throws DriverConfigurazioneException {
		if (genericProperties == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGenericProperties] Le configurazioni per le generic properties non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
		ResultSet selectRS = null;
		

		try {
			
			if(genericProperties.getNome()==null) {
				throw new DriverConfigurazioneException("Nome non fornito");
			}
			if(genericProperties.getTipologia()==null) {
				throw new DriverConfigurazioneException("Tipologia non fornita");
			}
			
			// Recupero id generic properties
			long idParent = -1;
			if(type == CostantiDB.UPDATE || type == CostantiDB.DELETE) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.addWhereCondition("tipologia=?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(sqlQuery);
				selectStmt.setString(1, genericProperties.getNome());
				selectStmt.setString(2, genericProperties.getTipologia());
				selectRS = selectStmt.executeQuery();
				
				if(selectRS.next()) {
					idParent = selectRS.getLong("id");
				}
				selectRS.close();
				selectStmt.close();
				
				if(idParent<=0) {
					throw new DriverConfigurazioneException("Configuration Property non trovato con nome ["+genericProperties.getNome()+"] e tipologia ["+genericProperties.getTipologia()+"]");
				}
				
				// Elimino anche le configurazioni (nell'update le ricreo)
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_GENERIC_PROPERTY);
				sqlQueryObject.addWhereCondition("id_props=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idParent);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idParent);
				updateStmt.executeUpdate();
				updateStmt.close();
			}
			

			switch (type) {
			case CREATE:
			case UPDATE:
		
				// insert

				List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", genericProperties.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("descrizione", genericProperties.getDescrizione() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipologia", genericProperties.getTipologia() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", genericProperties.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
				
				long idProperties = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
						new CustomKeyGeneratorObject(CostantiDB.CONFIG_GENERIC_PROPERTIES, CostantiDB.CONFIG_GENERIC_PROPERTIES_COLUMN_ID, 
								CostantiDB.CONFIG_GENERIC_PROPERTIES_SEQUENCE, CostantiDB.CONFIG_GENERIC_PROPERTIES_TABLE_FOR_ID),
						listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
				if(idProperties<=0){
					throw new Exception("ID (Generic Properties) autoincrementale non ottenuto");
				}

				for(int l=0; l<genericProperties.sizePropertyList();l++){
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONFIG_GENERIC_PROPERTY);
					sqlQueryObject.addInsertField("id_props", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idProperties);
					updateStmt.setString(2, genericProperties.getProperty(l).getNome());
					updateStmt.setString(3, genericProperties.getProperty(l).getValore());
					updateStmt.executeUpdate();
					updateStmt.close();
				}

				break;
			case DELETE:
				// non rimuovo in quanto gia fatto sopra.
				break;
			}


		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGenericProperties] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGenericProperties] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(selectRS!=null)selectRS.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(selectStmt!=null)selectStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	
	
	
	
	public static long CRUDConfigurazioneGenerale(int type, Configurazione config, Connection con) throws DriverConfigurazioneException {
		
		if(config.sizeExtendedInfoList()>0 && 
				config.getRoutingTable()==null && 
				config.getAccessoRegistro()==null &&
				config.getAccessoConfigurazione()==null && 
				config.getAccessoDatiAutorizzazione()==null &&
				config.getAccessoDatiAutenticazione()==null && 
				config.getMultitenant()==null &&
				config.getUrlInvocazione()==null &&
				config.getValidazioneBuste()==null && 
				config.getValidazioneContenutiApplicativi()==null &&
				config.getIndirizzoRisposta()==null &&	
				config.getAttachments()==null &&
				config.getRisposte()==null &&
				config.getInoltroBusteNonRiscontrate()==null && 
				config.getMessaggiDiagnostici()==null && 
				config.getTracciamento()==null &&
				config.getTransazioni()==null &&
				config.getDump()==null &&		
				config.getGestioneErrore()==null && 
				config.getIntegrationManager()==null &&
				config.getStatoServiziPdd()==null &&
				config.getSystemProperties()==null && 
				(config.getGenericPropertiesList()==null || config.getGenericPropertiesList().size()<=0) &&
				config.getGestioneCors()==null && 
				config.getResponseCaching()==null ) {
						
			// caso speciale extended info
			ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
			IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoConfigurazione();
			if(extInfoConfigurazioneDriver!=null){
			
				try{
					CRUDType crudType = null;
					switch (type) {
					case CREATE:
						crudType = CRUDType.CREATE;
						break;
					case UPDATE:
						crudType = CRUDType.UPDATE;
						break;
					case DELETE:
						crudType = CRUDType.DELETE;
						break;
					}
					
					switch (type) {
					case CREATE:
					case UPDATE:
						//extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, crudType);												
						if(config.sizeExtendedInfoList()>0){
							for(int l=0; l<config.sizeExtendedInfoList();l++){
								extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), crudType);
								extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), crudType);
							}
						}
						break;
					case DELETE:
						//extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, crudType);
						if(config.sizeExtendedInfoList()>0){
							for(int l=0; l<config.sizeExtendedInfoList();l++){
								extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), crudType);
							}
						}
						break;
					}
				}catch (Exception se) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale-Extended] Exception [" + se.getMessage() + "].",se);
				} 
				
			}
			
			return -1;
			
		}
		else{
			
			return _CRUDConfigurazioneGenerale(type, config, con);
			
		}
		
	}

	private static long _CRUDConfigurazioneGenerale(int type, Configurazione config, Connection con) throws DriverConfigurazioneException {
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		int n = 0;
		long idConfigurazione = 0;

		IndirizzoRisposta indirizzoPerRisposta = config.getIndirizzoRisposta();
		InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate = config.getInoltroBusteNonRiscontrate();
		IntegrationManager integrationManager = config.getIntegrationManager();
		MessaggiDiagnostici messaggiDiagnostici = config.getMessaggiDiagnostici();
		Risposte risposte = config.getRisposte();
		ValidazioneBuste validazioneBuste = config.getValidazioneBuste();
		AccessoRegistro car = config.getAccessoRegistro();
		AccessoConfigurazione aConfig = config.getAccessoConfigurazione();
		AccessoDatiAutorizzazione aDatiAuthz = config.getAccessoDatiAutorizzazione();
		AccessoDatiAutenticazione aDatiAuthn = config.getAccessoDatiAutenticazione();
		AccessoDatiGestioneToken aDatiGestioneToken = config.getAccessoDatiGestioneToken();
		AccessoDatiKeystore aDatiKeystore = config.getAccessoDatiKeystore();
		Attachments att = config.getAttachments();

		ConfigurazioneMultitenant multitenant = config.getMultitenant();
		
		CorsConfigurazione corsConfigurazione = config.getGestioneCors();
		String cors_stato = null;
		String cors_tipo = null; 
		String cors_all_allow_origins = null; 
		String cors_allow_credentials = null; 
		int cors_allow_max_age = CostantiDB.FALSE;
		Integer cors_allow_max_age_seconds = null;
		String cors_allow_origins = null; 
		String cors_allow_headers = null; 
		String cors_allow_methods = null; 
		String cors_allow_expose_headers = null; 
		if(corsConfigurazione!=null) {
			cors_stato = getValue(corsConfigurazione.getStato());
			cors_tipo = getValue(corsConfigurazione.getTipo());
			cors_all_allow_origins = getValue(corsConfigurazione.getAccessControlAllAllowOrigins());
			cors_allow_credentials = getValue(corsConfigurazione.getAccessControlAllowCredentials());
			if(corsConfigurazione.getAccessControlMaxAge()!=null) {
				cors_allow_max_age = CostantiDB.TRUE;
				cors_allow_max_age_seconds = corsConfigurazione.getAccessControlMaxAge();	
			}
			if(corsConfigurazione.getAccessControlAllowOrigins()!=null && corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowOrigins().getOrigin(i));
				}
				cors_allow_origins = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowHeaders()!=null && corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowHeaders().getHeader(i));
				}
				cors_allow_headers = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowMethods()!=null && corsConfigurazione.getAccessControlAllowMethods().sizeMethodList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowMethods().sizeMethodList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowMethods().getMethod(i));
				}
				cors_allow_methods = bf.toString();
			}
			if(corsConfigurazione.getAccessControlExposeHeaders()!=null && corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlExposeHeaders().getHeader(i));
				}
				cors_allow_expose_headers = bf.toString();
			}
		}
		
		ResponseCachingConfigurazioneGenerale responseCachingConfigurazione = config.getResponseCaching();
		
		String response_cache_stato = null;
		Integer response_cache_seconds = null;
		Long response_cache_max_msg_size = null;
		String response_cache_hash_url = null;
		String response_cache_hash_query = null;
		String response_cache_hash_query_list = null;
		String response_cache_hash_headers = null;
		String response_cache_hash_headers_list = null;
		String response_cache_hash_payload = null;
		boolean response_cache_noCache = true;
		boolean response_cache_maxAge = true;
		boolean response_cache_noStore = true;
		List<ResponseCachingConfigurazioneRegola> response_cache_regole = null;
		if(responseCachingConfigurazione!=null && responseCachingConfigurazione.getConfigurazione()!=null) {
			response_cache_stato = getValue(responseCachingConfigurazione.getConfigurazione().getStato());
			response_cache_seconds = responseCachingConfigurazione.getConfigurazione().getCacheTimeoutSeconds();
			response_cache_max_msg_size = responseCachingConfigurazione.getConfigurazione().getMaxMessageSize();
			if(responseCachingConfigurazione.getConfigurazione().getControl()!=null) {
				response_cache_noCache = responseCachingConfigurazione.getConfigurazione().getControl().getNoCache();
				response_cache_maxAge = responseCachingConfigurazione.getConfigurazione().getControl().getMaxAge();
				response_cache_noStore = responseCachingConfigurazione.getConfigurazione().getControl().getNoStore();
			}
			if(responseCachingConfigurazione.getConfigurazione().getHashGenerator()!=null) {
				response_cache_hash_url = getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getRequestUri());
				
				response_cache_hash_query = getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameters());
				if(StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameters())) {
					if(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameterList()!=null && responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeQueryParameterList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeQueryParameterList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getQueryParameter(i));
						}
						response_cache_hash_query_list = bf.toString();
					}
				}
				
				response_cache_hash_headers = getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeaders());
				if(StatoFunzionalita.ABILITATO.equals(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeaders())) {
					if(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeaderList()!=null && responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeHeaderList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < responseCachingConfigurazione.getConfigurazione().getHashGenerator().sizeHeaderList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getHeader(i));
						}
						response_cache_hash_headers_list = bf.toString();
					}
				}
				
				response_cache_hash_payload = getValue(responseCachingConfigurazione.getConfigurazione().getHashGenerator().getPayload());
			}
			response_cache_regole = responseCachingConfigurazione.getConfigurazione().getRegolaList();
		}
		
		Cache responseCaching_cache = null;
		String responseCaching_dimensioneCache = null;
		String responseCaching_algoritmoCache = null;
		String responseCaching_idleCache = null;
		String responseCaching_lifeCache = null;
		String responseCaching_statoCache = null;
		if(responseCachingConfigurazione !=null){
			responseCaching_cache = responseCachingConfigurazione.getCache();

		}
		responseCaching_statoCache = (responseCaching_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (responseCaching_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			responseCaching_dimensioneCache = responseCaching_cache.getDimensione();
			responseCaching_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(responseCaching_cache.getAlgoritmo());
			responseCaching_idleCache = responseCaching_cache.getItemIdleTime();
			responseCaching_lifeCache = responseCaching_cache.getItemLifeSecond();
		}
		
		
		Cache consegna_cache = null;
		String consegna_dimensioneCache = null;
		String consegna_algoritmoCache = null;
		String consegna_idleCache = null;
		String consegna_lifeCache = null;
		String consegna_statoCache = null;
		if(config.getAccessoDatiConsegnaApplicativi() !=null){
			consegna_cache = config.getAccessoDatiConsegnaApplicativi().getCache();

		}
		consegna_statoCache = (consegna_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (consegna_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			consegna_dimensioneCache = consegna_cache.getDimensione();
			consegna_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(consegna_cache.getAlgoritmo());
			consegna_idleCache = consegna_cache.getItemIdleTime();
			consegna_lifeCache = consegna_cache.getItemLifeSecond();
		}
		
		String utilizzoIndTelematico = null;
		if(indirizzoPerRisposta!=null){
			utilizzoIndTelematico =	DriverConfigurazioneDB_LIB.getValue(indirizzoPerRisposta.getUtilizzo());
		}
		String cadenza_inoltro = null;
		if(inoltroBusteNonRiscontrate!=null){
			cadenza_inoltro = inoltroBusteNonRiscontrate.getCadenza();
		}
		String autenticazione = null;
		if(integrationManager!=null){
			autenticazione = integrationManager.getAutenticazione();
		}
		String msg_diag_severita = null;
		String msg_diag_severita_log4j = null;
		if(messaggiDiagnostici!=null){
			msg_diag_severita = DriverConfigurazioneDB_LIB.getValue(messaggiDiagnostici.getSeverita());
			msg_diag_severita_log4j = DriverConfigurazioneDB_LIB.getValue(messaggiDiagnostici.getSeveritaLog4j());
		}
		String val_controllo = null;
		String val_stato = null;
		String val_manifest = null;
		String val_profiloCollaborazione = null;
		if(validazioneBuste!=null){
			val_controllo = DriverConfigurazioneDB_LIB.getValue(validazioneBuste.getControllo());
			val_stato = DriverConfigurazioneDB_LIB.getValue(validazioneBuste.getStato());
			val_manifest = DriverConfigurazioneDB_LIB.getValue(validazioneBuste.getManifestAttachments());
			val_profiloCollaborazione = DriverConfigurazioneDB_LIB.getValue(validazioneBuste.getProfiloCollaborazione());
		}

		String gestioneManifest = null;
		if(att!=null){
			gestioneManifest = DriverConfigurazioneDB_LIB.getValue(att.getGestioneManifest());
		}

		Cache registro_cache = null;
		String registro_dimensioneCache = null;
		String registro_algoritmoCache = null;
		String registro_idleCache = null;
		String registro_lifeCache = null;
		String registro_statoCache = null;
		if(car !=null){
			registro_cache = car.getCache();

		}
		registro_statoCache = (registro_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (registro_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			registro_dimensioneCache = registro_cache.getDimensione();
			registro_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(registro_cache.getAlgoritmo());
			registro_idleCache = registro_cache.getItemIdleTime();
			registro_lifeCache = registro_cache.getItemLifeSecond();
		}
		
		Cache config_cache = null;
		String config_dimensioneCache = null;
		String config_algoritmoCache = null;
		String config_idleCache = null;
		String config_lifeCache = null;
		String config_statoCache = null;
		if(aConfig !=null){
			config_cache = aConfig.getCache();

		}
		config_statoCache = (config_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (config_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			config_dimensioneCache = config_cache.getDimensione();
			config_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(config_cache.getAlgoritmo());
			config_idleCache = config_cache.getItemIdleTime();
			config_lifeCache = config_cache.getItemLifeSecond();
		}
		
		Cache authz_cache = null;
		String authz_dimensioneCache = null;
		String authz_algoritmoCache = null;
		String authz_idleCache = null;
		String authz_lifeCache = null;
		String authz_statoCache = null;
		if(aDatiAuthz !=null){
			authz_cache = aDatiAuthz.getCache();

		}
		authz_statoCache = (authz_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (authz_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			authz_dimensioneCache = authz_cache.getDimensione();
			authz_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(authz_cache.getAlgoritmo());
			authz_idleCache = authz_cache.getItemIdleTime();
			authz_lifeCache = authz_cache.getItemLifeSecond();
		}
		
		Cache authn_cache = null;
		String authn_dimensioneCache = null;
		String authn_algoritmoCache = null;
		String authn_idleCache = null;
		String authn_lifeCache = null;
		String authn_statoCache = null;
		if(aDatiAuthn !=null){
			authn_cache = aDatiAuthn.getCache();

		}
		authn_statoCache = (authn_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (authn_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			authn_dimensioneCache = authn_cache.getDimensione();
			authn_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(authn_cache.getAlgoritmo());
			authn_idleCache = authn_cache.getItemIdleTime();
			authn_lifeCache = authn_cache.getItemLifeSecond();
		}
		
		Cache token_cache = null;
		String token_dimensioneCache = null;
		String token_algoritmoCache = null;
		String token_idleCache = null;
		String token_lifeCache = null;
		String token_statoCache = null;
		if(aDatiGestioneToken !=null){
			token_cache = aDatiGestioneToken.getCache();

		}
		token_statoCache = (token_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (token_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			token_dimensioneCache = token_cache.getDimensione();
			token_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(token_cache.getAlgoritmo());
			token_idleCache = token_cache.getItemIdleTime();
			token_lifeCache = token_cache.getItemLifeSecond();
		}
		
		Cache keystore_cache = null;
		String keystore_dimensioneCache = null;
		String keystore_algoritmoCache = null;
		String keystore_idleCache = null;
		String keystore_lifeCache = null;
		String keystore_statoCache = null;
		String keystore_crlLifeCache = null;
		if(aDatiKeystore !=null){
			keystore_cache = aDatiKeystore.getCache();
			keystore_crlLifeCache = aDatiKeystore.getCrlItemLifeSecond();

		}
		keystore_statoCache = (keystore_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (keystore_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			keystore_dimensioneCache = keystore_cache.getDimensione();
			keystore_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(keystore_cache.getAlgoritmo());
			keystore_idleCache = keystore_cache.getItemIdleTime();
			keystore_lifeCache = keystore_cache.getItemLifeSecond();
		}

		Tracciamento t = config.getTracciamento();
		String tracciamentoBuste = null;
		String tracciamentoEsiti = null;
		if (t != null) {
			tracciamentoBuste = DriverConfigurazioneDB_LIB.getValue(t.getStato());
			tracciamentoEsiti = t.getEsiti();
		}
		
		Dump d = config.getDump();
		String dumpApplicativo = null;
		String dumpPD = null;
		String dumpPA = null;
		DumpConfigurazione dumpConfig = null;
		if (d != null) {
			dumpApplicativo = DriverConfigurazioneDB_LIB.getValue(d.getStato());
			dumpPD = DriverConfigurazioneDB_LIB.getValue(d.getDumpBinarioPortaDelegata());
			dumpPA = DriverConfigurazioneDB_LIB.getValue(d.getDumpBinarioPortaApplicativa());
			dumpConfig = d.getConfigurazione();
		}

		Transazioni transazioni = config.getTransazioni();
		String transazioniTempiElaborazione = null;
		String transazioniToken = null;
		if(transazioni!=null) {
			transazioniTempiElaborazione = DriverConfigurazioneDB_LIB.getValue(transazioni.getTempiElaborazione());
			transazioniToken = DriverConfigurazioneDB_LIB.getValue(transazioni.getToken());
		}
		
		String modRisposta = CostantiConfigurazione.CONNECTION_REPLY.toString();
		if(risposte!=null){
			modRisposta = (risposte.getConnessione().equals(CostantiConfigurazione.CONNECTION_REPLY) ? 
					CostantiConfigurazione.CONNECTION_REPLY.toString() : CostantiConfigurazione.NEW_CONNECTION.toString());
		}
		String routingEnabled =  CostantiConfigurazione.DISABILITATO.toString();
		if(config.getRoutingTable()!=null){
			if(config.getRoutingTable().getAbilitata()!=null && config.getRoutingTable().getAbilitata())
				routingEnabled =  CostantiConfigurazione.ABILITATO.toString();
		}
		
		String validazione_contenuti_stato = null;
		String validazione_contenuti_tipo = null;
		String validazione_contenuti_acceptMtomMessage = null;
		if(config.getValidazioneContenutiApplicativi()!=null){
			validazione_contenuti_stato = DriverConfigurazioneDB_LIB.getValue(config.getValidazioneContenutiApplicativi().getStato());
			validazione_contenuti_tipo = DriverConfigurazioneDB_LIB.getValue(config.getValidazioneContenutiApplicativi().getTipo());
			validazione_contenuti_acceptMtomMessage = DriverConfigurazioneDB_LIB.getValue(config.getValidazioneContenutiApplicativi().getAcceptMtomMessage());
		}

		ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
		IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoConfigurazione();
									
		try {
			switch (type) {
			case CREATE:
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addInsertField("cadenza_inoltro", "?");
				
				sqlQueryObject.addInsertField("validazione_stato", "?");
				sqlQueryObject.addInsertField("validazione_controllo", "?");
				
				sqlQueryObject.addInsertField("msg_diag_severita", "?");
				sqlQueryObject.addInsertField("msg_diag_severita_log4j", "?");
				sqlQueryObject.addInsertField("auth_integration_manager", "?");
				sqlQueryObject.addInsertField("validazione_profilo", "?");
				sqlQueryObject.addInsertField("mod_risposta", "?");
				sqlQueryObject.addInsertField("indirizzo_telematico", "?");
				sqlQueryObject.addInsertField("routing_enabled", "?");
				sqlQueryObject.addInsertField("gestione_manifest", "?");
				sqlQueryObject.addInsertField("validazione_manifest", "?");
				sqlQueryObject.addInsertField("tracciamento_buste", "?");
				sqlQueryObject.addInsertField("tracciamento_esiti", "?");
				sqlQueryObject.addInsertField("transazioni_tempi", "?");
				sqlQueryObject.addInsertField("transazioni_token", "?");
				sqlQueryObject.addInsertField("dump", "?");
				sqlQueryObject.addInsertField("dump_bin_pd", "?");
				sqlQueryObject.addInsertField("dump_bin_pa", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_stato", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_mtom", "?");
				// registro cache
				sqlQueryObject.addInsertField("statocache", "?");
				sqlQueryObject.addInsertField("dimensionecache", "?");
				sqlQueryObject.addInsertField("algoritmocache", "?");
				sqlQueryObject.addInsertField("idlecache", "?");
				sqlQueryObject.addInsertField("lifecache", "?");
				// config cache
				sqlQueryObject.addInsertField("config_statocache", "?");
				sqlQueryObject.addInsertField("config_dimensionecache", "?");
				sqlQueryObject.addInsertField("config_algoritmocache", "?");
				sqlQueryObject.addInsertField("config_idlecache", "?");
				sqlQueryObject.addInsertField("config_lifecache", "?");
				// authz cache
				sqlQueryObject.addInsertField("auth_statocache", "?");
				sqlQueryObject.addInsertField("auth_dimensionecache", "?");
				sqlQueryObject.addInsertField("auth_algoritmocache", "?");
				sqlQueryObject.addInsertField("auth_idlecache", "?");
				sqlQueryObject.addInsertField("auth_lifecache", "?");
				// authn cache
				sqlQueryObject.addInsertField("authn_statocache", "?");
				sqlQueryObject.addInsertField("authn_dimensionecache", "?");
				sqlQueryObject.addInsertField("authn_algoritmocache", "?");
				sqlQueryObject.addInsertField("authn_idlecache", "?");
				sqlQueryObject.addInsertField("authn_lifecache", "?");
				// gestione token cache
				sqlQueryObject.addInsertField("token_statocache", "?");
				sqlQueryObject.addInsertField("token_dimensionecache", "?");
				sqlQueryObject.addInsertField("token_algoritmocache", "?");
				sqlQueryObject.addInsertField("token_idlecache", "?");
				sqlQueryObject.addInsertField("token_lifecache", "?");
				// keystore cache
				sqlQueryObject.addInsertField("keystore_statocache", "?");
				sqlQueryObject.addInsertField("keystore_dimensionecache", "?");
				sqlQueryObject.addInsertField("keystore_algoritmocache", "?");
				sqlQueryObject.addInsertField("keystore_idlecache", "?");
				sqlQueryObject.addInsertField("keystore_lifecache", "?");
				sqlQueryObject.addInsertField("keystore_crl_lifecache", "?");
				// multitenant
				sqlQueryObject.addInsertField("multitenant_stato", "?");
				sqlQueryObject.addInsertField("multitenant_fruizioni", "?");
				sqlQueryObject.addInsertField("multitenant_erogazioni", "?");
				// cors
				sqlQueryObject.addInsertField("cors_stato", "?");
				sqlQueryObject.addInsertField("cors_tipo", "?");
				sqlQueryObject.addInsertField("cors_all_allow_origins", "?");
				sqlQueryObject.addInsertField("cors_allow_credentials", "?");
				sqlQueryObject.addInsertField("cors_allow_max_age", "?");
				sqlQueryObject.addInsertField("cors_allow_max_age_seconds", "?");
				sqlQueryObject.addInsertField("cors_allow_origins", "?");
				sqlQueryObject.addInsertField("cors_allow_headers", "?");
				sqlQueryObject.addInsertField("cors_allow_methods", "?");
				sqlQueryObject.addInsertField("cors_allow_expose_headers", "?");
				// responseCaching
				sqlQueryObject.addInsertField("response_cache_stato", "?");
				sqlQueryObject.addInsertField("response_cache_seconds", "?");
				sqlQueryObject.addInsertField("response_cache_max_msg_size", "?");
				sqlQueryObject.addInsertField("response_cache_control_nocache", "?");
				sqlQueryObject.addInsertField("response_cache_control_maxage", "?");
				sqlQueryObject.addInsertField("response_cache_control_nostore", "?");
				sqlQueryObject.addInsertField("response_cache_hash_url", "?");
				sqlQueryObject.addInsertField("response_cache_hash_query", "?");
				sqlQueryObject.addInsertField("response_cache_hash_query_list", "?");
				sqlQueryObject.addInsertField("response_cache_hash_headers", "?");
				sqlQueryObject.addInsertField("response_cache_hash_hdr_list", "?");
				sqlQueryObject.addInsertField("response_cache_hash_payload", "?");
				// responseCaching cache
				sqlQueryObject.addInsertField("response_cache_statocache", "?");
				sqlQueryObject.addInsertField("response_cache_dimensionecache", "?");
				sqlQueryObject.addInsertField("response_cache_algoritmocache", "?");
				sqlQueryObject.addInsertField("response_cache_idlecache", "?");
				sqlQueryObject.addInsertField("response_cache_lifecache", "?");
				// consegna applicativi cache
				sqlQueryObject.addInsertField("consegna_statocache", "?");
				sqlQueryObject.addInsertField("consegna_dimensionecache", "?");
				sqlQueryObject.addInsertField("consegna_algoritmocache", "?");
				sqlQueryObject.addInsertField("consegna_idlecache", "?");
				sqlQueryObject.addInsertField("consegna_lifecache", "?");

				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				int index = 1;
				
				updateStmt.setString(index++, cadenza_inoltro);
				updateStmt.setString(index++, val_stato);
				updateStmt.setString(index++, val_controllo);
				updateStmt.setString(index++, msg_diag_severita);
				updateStmt.setString(index++, msg_diag_severita_log4j);
				updateStmt.setString(index++, autenticazione);
				updateStmt.setString(index++, val_profiloCollaborazione);
				updateStmt.setString(index++, modRisposta);
				updateStmt.setString(index++, utilizzoIndTelematico);
				updateStmt.setString(index++, routingEnabled);
				updateStmt.setString(index++, gestioneManifest);
				updateStmt.setString(index++, val_manifest);
				updateStmt.setString(index++, tracciamentoBuste);
				updateStmt.setString(index++, tracciamentoEsiti);
				updateStmt.setString(index++, transazioniTempiElaborazione);
				updateStmt.setString(index++, transazioniToken);
				updateStmt.setString(index++, dumpApplicativo);
				updateStmt.setString(index++, dumpPD);
				updateStmt.setString(index++, dumpPA);
				updateStmt.setString(index++, validazione_contenuti_stato);
				updateStmt.setString(index++, validazione_contenuti_tipo);
				updateStmt.setString(index++, validazione_contenuti_acceptMtomMessage);
				// registro cache
				updateStmt.setString(index++, registro_statoCache);
				updateStmt.setString(index++, registro_dimensioneCache);
				updateStmt.setString(index++, registro_algoritmoCache);
				updateStmt.setString(index++, registro_idleCache);
				updateStmt.setString(index++, registro_lifeCache);
				// config cache
				updateStmt.setString(index++, config_statoCache);
				updateStmt.setString(index++, config_dimensioneCache);
				updateStmt.setString(index++, config_algoritmoCache);
				updateStmt.setString(index++, config_idleCache);
				updateStmt.setString(index++, config_lifeCache);
				// authz cache
				updateStmt.setString(index++, authz_statoCache);
				updateStmt.setString(index++, authz_dimensioneCache);
				updateStmt.setString(index++, authz_algoritmoCache);
				updateStmt.setString(index++, authz_idleCache);
				updateStmt.setString(index++, authz_lifeCache);
				// authn cache
				updateStmt.setString(index++, authn_statoCache);
				updateStmt.setString(index++, authn_dimensioneCache);
				updateStmt.setString(index++, authn_algoritmoCache);
				updateStmt.setString(index++, authn_idleCache);
				updateStmt.setString(index++, authn_lifeCache);
				// token cache
				updateStmt.setString(index++, token_statoCache);
				updateStmt.setString(index++, token_dimensioneCache);
				updateStmt.setString(index++, token_algoritmoCache);
				updateStmt.setString(index++, token_idleCache);
				updateStmt.setString(index++, token_lifeCache);
				// keystore cache
				updateStmt.setString(index++, keystore_statoCache);
				updateStmt.setString(index++, keystore_dimensioneCache);
				updateStmt.setString(index++, keystore_algoritmoCache);
				updateStmt.setString(index++, keystore_idleCache);
				updateStmt.setString(index++, keystore_lifeCache);
				updateStmt.setString(index++, keystore_crlLifeCache);
				// multitenant
				updateStmt.setString(index++, multitenant!=null ? getValue(multitenant.getStato()) : null);
				updateStmt.setString(index++, multitenant!=null ? getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null);
				updateStmt.setString(index++, multitenant!=null ? getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null);
				// cors
				updateStmt.setString(index++, cors_stato);
				updateStmt.setString(index++, cors_tipo);
				updateStmt.setString(index++, cors_all_allow_origins);
				updateStmt.setString(index++, cors_allow_credentials);
				updateStmt.setInt(index++, cors_allow_max_age);
				if(cors_allow_max_age_seconds!=null) {
					updateStmt.setInt(index++, cors_allow_max_age_seconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				updateStmt.setString(index++, cors_allow_origins);
				updateStmt.setString(index++, cors_allow_headers);
				updateStmt.setString(index++, cors_allow_methods);
				updateStmt.setString(index++, cors_allow_expose_headers);				
				// responseCaching
				updateStmt.setString(index++, response_cache_stato);
				if(response_cache_seconds!=null) {
					updateStmt.setInt(index++, response_cache_seconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				if(response_cache_max_msg_size!=null) {
					updateStmt.setLong(index++, response_cache_max_msg_size);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.BIGINT);
				}
				updateStmt.setInt(index++, response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setString(index++, response_cache_hash_url);
				updateStmt.setString(index++, response_cache_hash_query);
				updateStmt.setString(index++, response_cache_hash_query_list);
				updateStmt.setString(index++, response_cache_hash_headers);
				updateStmt.setString(index++, response_cache_hash_headers_list);
				updateStmt.setString(index++, response_cache_hash_payload);
				// responseCaching cache
				updateStmt.setString(index++, responseCaching_statoCache);
				updateStmt.setString(index++, responseCaching_dimensioneCache);
				updateStmt.setString(index++, responseCaching_algoritmoCache);
				updateStmt.setString(index++, responseCaching_idleCache);
				updateStmt.setString(index++, responseCaching_lifeCache);
				// consegna applicativi cache
				updateStmt.setString(index++, consegna_statoCache);
				updateStmt.setString(index++, consegna_dimensioneCache);
				updateStmt.setString(index++, consegna_algoritmoCache);
				updateStmt.setString(index++, consegna_idleCache);
				updateStmt.setString(index++, consegna_lifeCache);

				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + 
						DBUtils.formatSQLString(updateQuery, 
								cadenza_inoltro, 
								val_stato, val_controllo, 
								msg_diag_severita, msg_diag_severita_log4j, 
								autenticazione, 
								val_profiloCollaborazione, 
								modRisposta, utilizzoIndTelematico, 
								routingEnabled, gestioneManifest, 
								val_manifest, tracciamentoBuste, transazioniTempiElaborazione, transazioniToken, dumpApplicativo, dumpPD, dumpPA,
								validazione_contenuti_stato,validazione_contenuti_tipo,validazione_contenuti_acceptMtomMessage,
								registro_statoCache, registro_dimensioneCache, registro_algoritmoCache, registro_idleCache, registro_lifeCache,
								config_statoCache, config_dimensioneCache, config_algoritmoCache, config_idleCache, config_lifeCache,
								authz_statoCache, authz_dimensioneCache, authz_algoritmoCache, authz_idleCache, authz_lifeCache,
								authn_statoCache, authn_dimensioneCache, authn_algoritmoCache, authn_idleCache, authn_lifeCache,
								token_statoCache, token_dimensioneCache, token_algoritmoCache, token_idleCache, token_lifeCache,
								keystore_statoCache, keystore_dimensioneCache, keystore_algoritmoCache, keystore_idleCache, keystore_lifeCache, keystore_crlLifeCache,
								(multitenant!=null ? getValue(multitenant.getStato()) : null),
								(multitenant!=null ? getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null),
								(multitenant!=null ? getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null),
								cors_stato, cors_tipo, cors_all_allow_origins, cors_allow_credentials, cors_allow_max_age, cors_allow_max_age_seconds,
								cors_allow_origins, cors_allow_headers, cors_allow_methods, cors_allow_expose_headers,
								response_cache_stato, response_cache_seconds, response_cache_max_msg_size, 
								(response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE),
								(response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE),
								(response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE),
								response_cache_hash_url, response_cache_hash_query, response_cache_hash_query_list, response_cache_hash_headers, response_cache_hash_headers_list, response_cache_hash_payload,
								responseCaching_statoCache, responseCaching_dimensioneCache, responseCaching_algoritmoCache, responseCaching_idleCache, responseCaching_lifeCache,
								consegna_statoCache, consegna_dimensioneCache, consegna_algoritmoCache, consegna_idleCache, consegna_lifeCache
								));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				// delete from config_url_invocazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_REGOLE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// insert into config_protocolli
				if(config.getUrlInvocazione()!=null){
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
					sqlQueryObject.addInsertField("base_url", "?");
					sqlQueryObject.addInsertField("base_url_fruizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int indexP = 1;
					updateStmt.setString(indexP++, config.getUrlInvocazione().getBaseUrl());
					updateStmt.setString(indexP++, config.getUrlInvocazione().getBaseUrlFruizione());
					updateStmt.executeUpdate();
					updateStmt.close();
					
					if(config.getUrlInvocazione().sizeRegolaList()>0){
						for(int k=0; k<config.getUrlInvocazione().sizeRegolaList();k++){
							ConfigurazioneUrlInvocazioneRegola configUrlInvocazioneRegola = config.getUrlInvocazione().getRegola(k);
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.CONFIG_URL_REGOLE);
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("posizione", "?");
							sqlQueryObject.addInsertField("stato", "?");
							sqlQueryObject.addInsertField("descrizione", "?");
							sqlQueryObject.addInsertField("regexpr", "?");
							sqlQueryObject.addInsertField("regola", "?");
							sqlQueryObject.addInsertField("contesto_esterno", "?");
							sqlQueryObject.addInsertField("base_url", "?");
							sqlQueryObject.addInsertField("protocollo", "?");
							sqlQueryObject.addInsertField("ruolo", "?");
							sqlQueryObject.addInsertField("service_binding", "?");
							sqlQueryObject.addInsertField("tipo_soggetto", "?");
							sqlQueryObject.addInsertField("nome_soggetto", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							indexP = 1;
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getNome());
							updateStmt.setInt(indexP++, configUrlInvocazioneRegola.getPosizione());
							updateStmt.setString(indexP++, DriverConfigurazioneDB_LIB.getValue(configUrlInvocazioneRegola.getStato()));
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getDescrizione());
							updateStmt.setInt(indexP++, configUrlInvocazioneRegola.isRegexpr() ? CostantiDB.TRUE : CostantiDB.FALSE);
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getRegola());
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getContestoEsterno());
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getBaseUrl());
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getProtocollo());
							updateStmt.setString(indexP++, DriverConfigurazioneDB_LIB.getValue(configUrlInvocazioneRegola.getRuolo()));
							updateStmt.setString(indexP++, DriverConfigurazioneDB_LIB.getValue(configUrlInvocazioneRegola.getServiceBinding()));
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getSoggetto()!=null ? configUrlInvocazioneRegola.getSoggetto().getTipo() : null);
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getSoggetto()!=null ? configUrlInvocazioneRegola.getSoggetto().getNome() : null);
							updateStmt.executeUpdate();
							updateStmt.close();
	
						}
					}
				}
				
				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msg diag appender
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getMessaggiDiagnostici().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDiagAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_APPENDER, CostantiDB.MSG_DIAGN_APPENDER_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_APPENDER_SEQUENCE, CostantiDB.MSG_DIAGN_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDiagAppender<=0){
							throw new Exception("ID (msg diag appender) autoincrementale non ottenuto");
						}
						
						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idMsgDiagAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}

				// delete from tracciamento appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracciamento appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracciamento appender
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getTracciamento().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_APPENDER, CostantiDB.TRACCIAMENTO_APPENDER_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_APPENDER_SEQUENCE, CostantiDB.TRACCIAMENTO_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceAppender<=0){
							throw new Exception("ID (tracce appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idTracceAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}
				
				
				// delete from dump appender_prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from dump appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into dump appender
				if(config.getDump()!=null){
					for(int k=0; k<config.getDump().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getDump().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idDumpAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.DUMP_APPENDER, CostantiDB.DUMP_APPENDER_COLUMN_ID, 
										CostantiDB.DUMP_APPENDER_SEQUENCE, CostantiDB.DUMP_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idDumpAppender<=0){
							throw new Exception("ID (dump appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.DUMP_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idDumpAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}
				
				// Cache Regole
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							sqlQueryObject.addInsertField("status_min", "?");
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							sqlQueryObject.addInsertField("status_max", "?");
						}
						sqlQueryObject.addInsertField("fault", "?");
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							sqlQueryObject.addInsertField("cache_seconds", "?");
						}
						String sqlQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(sqlQuery);
						int indexCR = 1;
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							updateStmt.setInt(indexCR++, regola.getReturnCodeMin());
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							updateStmt.setInt(indexCR++, regola.getReturnCodeMax());
						}
						updateStmt.setInt(indexCR++, regola.getFault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							updateStmt.setInt(indexCR++, regola.getCacheTimeoutSeconds());
						}
						updateStmt.executeUpdate();
						updateStmt.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta regola di cache");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " regole di cache");
				
				// dumpConfigurazione
				CRUDDumpConfigurazione(type, con, dumpConfig, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				

				// delete from msgdiag ds prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msgdiag ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msgdiag ds
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getMessaggiDiagnostici().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_DS, CostantiDB.MSG_DIAGN_DS_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_DS_SEQUENCE, CostantiDB.MSG_DIAGN_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDsAppender<=0){
							throw new Exception("ID (msgdiag ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.MSG_DIAGN_DS_PROP);
							sqlQueryObject.addInsertField("id_prop", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idMsgDsAppender);
							updateStmt.setString(2, ds.getProperty(l).getNome());
							updateStmt.setString(3, ds.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}

				// delete from tracce ds prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracce ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracce ds
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getTracciamento().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_DS, CostantiDB.TRACCIAMENTO_DS_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_DS_SEQUENCE, CostantiDB.TRACCIAMENTO_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceDsAppender<=0){
							throw new Exception("ID (tracciamento ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.TRACCIAMENTO_DS_PROP);
							sqlQueryObject.addInsertField("id_prop", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idTracceDsAppender);
							updateStmt.setString(2, ds.getProperty(l).getNome());
							updateStmt.setString(3, ds.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}
				
				// ExtendedInfo
				if(extInfoConfigurazioneDriver!=null){
					
					//extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, CRUDType.CREATE);
										
					if(config.sizeExtendedInfoList()>0){
						for(int l=0; l<config.sizeExtendedInfoList();l++){
							extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.CREATE);
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.CREATE);
						}
					}
				}


				break;
			case UPDATE:
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("cadenza_inoltro", "?");
				sqlQueryObject.addUpdateField("validazione_stato", "?");
				sqlQueryObject.addUpdateField("validazione_controllo", "?");
				sqlQueryObject.addUpdateField("msg_diag_severita", "?");
				sqlQueryObject.addUpdateField("msg_diag_severita_log4j", "?");
				sqlQueryObject.addUpdateField("auth_integration_manager", "?");
				sqlQueryObject.addUpdateField("validazione_profilo", "?");
				sqlQueryObject.addUpdateField("mod_risposta", "?");
				sqlQueryObject.addUpdateField("indirizzo_telematico", "?");
				sqlQueryObject.addUpdateField("routing_enabled", "?");
				sqlQueryObject.addUpdateField("gestione_manifest", "?");
				sqlQueryObject.addUpdateField("validazione_manifest", "?");
				sqlQueryObject.addUpdateField("tracciamento_buste", "?");
				sqlQueryObject.addUpdateField("tracciamento_esiti", "?");
				sqlQueryObject.addUpdateField("transazioni_tempi", "?");
				sqlQueryObject.addUpdateField("transazioni_token", "?");
				sqlQueryObject.addUpdateField("dump", "?");
				sqlQueryObject.addUpdateField("dump_bin_pd", "?");
				sqlQueryObject.addUpdateField("dump_bin_pa", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_stato", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_mtom", "?");
				// registro cache
				sqlQueryObject.addUpdateField("statocache", "?");
				sqlQueryObject.addUpdateField("dimensionecache", "?");
				sqlQueryObject.addUpdateField("algoritmocache", "?");
				sqlQueryObject.addUpdateField("idlecache", "?");
				sqlQueryObject.addUpdateField("lifecache", "?");
				// config cache
				sqlQueryObject.addUpdateField("config_statocache", "?");
				sqlQueryObject.addUpdateField("config_dimensionecache", "?");
				sqlQueryObject.addUpdateField("config_algoritmocache", "?");
				sqlQueryObject.addUpdateField("config_idlecache", "?");
				sqlQueryObject.addUpdateField("config_lifecache", "?");
				// authz cache
				sqlQueryObject.addUpdateField("auth_statocache", "?");
				sqlQueryObject.addUpdateField("auth_dimensionecache", "?");
				sqlQueryObject.addUpdateField("auth_algoritmocache", "?");
				sqlQueryObject.addUpdateField("auth_idlecache", "?");
				sqlQueryObject.addUpdateField("auth_lifecache", "?");
				// authn cache
				sqlQueryObject.addUpdateField("authn_statocache", "?");
				sqlQueryObject.addUpdateField("authn_dimensionecache", "?");
				sqlQueryObject.addUpdateField("authn_algoritmocache", "?");
				sqlQueryObject.addUpdateField("authn_idlecache", "?");
				sqlQueryObject.addUpdateField("authn_lifecache", "?");
				// token cache
				sqlQueryObject.addUpdateField("token_statocache", "?");
				sqlQueryObject.addUpdateField("token_dimensionecache", "?");
				sqlQueryObject.addUpdateField("token_algoritmocache", "?");
				sqlQueryObject.addUpdateField("token_idlecache", "?");
				sqlQueryObject.addUpdateField("token_lifecache", "?");
				// keystore cache
				sqlQueryObject.addUpdateField("keystore_statocache", "?");
				sqlQueryObject.addUpdateField("keystore_dimensionecache", "?");
				sqlQueryObject.addUpdateField("keystore_algoritmocache", "?");
				sqlQueryObject.addUpdateField("keystore_idlecache", "?");
				sqlQueryObject.addUpdateField("keystore_lifecache", "?");
				sqlQueryObject.addUpdateField("keystore_crl_lifecache", "?");
				// multitenant
				sqlQueryObject.addUpdateField("multitenant_stato", "?");
				sqlQueryObject.addUpdateField("multitenant_fruizioni", "?");
				sqlQueryObject.addUpdateField("multitenant_erogazioni", "?");
				// cors
				sqlQueryObject.addUpdateField("cors_stato", "?");
				sqlQueryObject.addUpdateField("cors_tipo", "?");
				sqlQueryObject.addUpdateField("cors_all_allow_origins", "?");
				sqlQueryObject.addUpdateField("cors_allow_credentials", "?");
				sqlQueryObject.addUpdateField("cors_allow_max_age", "?");
				sqlQueryObject.addUpdateField("cors_allow_max_age_seconds", "?");
				sqlQueryObject.addUpdateField("cors_allow_origins", "?");
				sqlQueryObject.addUpdateField("cors_allow_headers", "?");
				sqlQueryObject.addUpdateField("cors_allow_methods", "?");
				sqlQueryObject.addUpdateField("cors_allow_expose_headers", "?");
				// responseCaching
				sqlQueryObject.addUpdateField("response_cache_stato", "?");
				sqlQueryObject.addUpdateField("response_cache_seconds", "?");
				sqlQueryObject.addUpdateField("response_cache_max_msg_size", "?");
				sqlQueryObject.addUpdateField("response_cache_control_nocache", "?");
				sqlQueryObject.addUpdateField("response_cache_control_maxage", "?");
				sqlQueryObject.addUpdateField("response_cache_control_nostore", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_url", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_query", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_query_list", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_headers", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_hdr_list", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_payload", "?");
				// responseCaching cache
				sqlQueryObject.addUpdateField("response_cache_statocache", "?");
				sqlQueryObject.addUpdateField("response_cache_dimensionecache", "?");
				sqlQueryObject.addUpdateField("response_cache_algoritmocache", "?");
				sqlQueryObject.addUpdateField("response_cache_idlecache", "?");
				sqlQueryObject.addUpdateField("response_cache_lifecache", "?");
				// consegna applicativi cache
				sqlQueryObject.addUpdateField("consegna_statocache", "?");
				sqlQueryObject.addUpdateField("consegna_dimensionecache", "?");
				sqlQueryObject.addUpdateField("consegna_algoritmocache", "?");
				sqlQueryObject.addUpdateField("consegna_idlecache", "?");
				sqlQueryObject.addUpdateField("consegna_lifecache", "?");

				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				
				index = 1;
				
				updateStmt.setString(index++, cadenza_inoltro);
				updateStmt.setString(index++, val_stato);
				updateStmt.setString(index++, val_controllo);
				updateStmt.setString(index++, msg_diag_severita);
				updateStmt.setString(index++, msg_diag_severita_log4j);
				updateStmt.setString(index++, autenticazione);
				updateStmt.setString(index++, val_profiloCollaborazione);
				updateStmt.setString(index++, modRisposta);
				updateStmt.setString(index++, utilizzoIndTelematico);
				updateStmt.setString(index++, routingEnabled);
				updateStmt.setString(index++, gestioneManifest);
				updateStmt.setString(index++, val_manifest);
				updateStmt.setString(index++, tracciamentoBuste);
				updateStmt.setString(index++, tracciamentoEsiti);
				updateStmt.setString(index++, transazioniTempiElaborazione);
				updateStmt.setString(index++, transazioniToken);
				updateStmt.setString(index++, dumpApplicativo);
				updateStmt.setString(index++, dumpPD);
				updateStmt.setString(index++, dumpPA);
				updateStmt.setString(index++, validazione_contenuti_stato);
				updateStmt.setString(index++, validazione_contenuti_tipo);
				updateStmt.setString(index++, validazione_contenuti_acceptMtomMessage);
				// registro cache
				updateStmt.setString(index++, registro_statoCache);
				updateStmt.setString(index++, registro_dimensioneCache);
				updateStmt.setString(index++, registro_algoritmoCache);
				updateStmt.setString(index++, registro_idleCache);
				updateStmt.setString(index++, registro_lifeCache);
				// config cache
				updateStmt.setString(index++, config_statoCache);
				updateStmt.setString(index++, config_dimensioneCache);
				updateStmt.setString(index++, config_algoritmoCache);
				updateStmt.setString(index++, config_idleCache);
				updateStmt.setString(index++, config_lifeCache);
				// authz cache
				updateStmt.setString(index++, authz_statoCache);
				updateStmt.setString(index++, authz_dimensioneCache);
				updateStmt.setString(index++, authz_algoritmoCache);
				updateStmt.setString(index++, authz_idleCache);
				updateStmt.setString(index++, authz_lifeCache);
				// authn cache
				updateStmt.setString(index++, authn_statoCache);
				updateStmt.setString(index++, authn_dimensioneCache);
				updateStmt.setString(index++, authn_algoritmoCache);
				updateStmt.setString(index++, authn_idleCache);
				updateStmt.setString(index++, authn_lifeCache);
				// token cache
				updateStmt.setString(index++, token_statoCache);
				updateStmt.setString(index++, token_dimensioneCache);
				updateStmt.setString(index++, token_algoritmoCache);
				updateStmt.setString(index++, token_idleCache);
				updateStmt.setString(index++, token_lifeCache);
				// keystore cache
				updateStmt.setString(index++, keystore_statoCache);
				updateStmt.setString(index++, keystore_dimensioneCache);
				updateStmt.setString(index++, keystore_algoritmoCache);
				updateStmt.setString(index++, keystore_idleCache);
				updateStmt.setString(index++, keystore_lifeCache);
				updateStmt.setString(index++, keystore_crlLifeCache);
				// multitenant
				updateStmt.setString(index++, multitenant!=null ? getValue(multitenant.getStato()) : null);
				updateStmt.setString(index++, multitenant!=null ? getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null);
				updateStmt.setString(index++, multitenant!=null ? getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null);
				// cors
				updateStmt.setString(index++, cors_stato);
				updateStmt.setString(index++, cors_tipo);
				updateStmt.setString(index++, cors_all_allow_origins);
				updateStmt.setString(index++, cors_allow_credentials);
				updateStmt.setInt(index++, cors_allow_max_age);
				if(cors_allow_max_age_seconds!=null) {
					updateStmt.setInt(index++, cors_allow_max_age_seconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				updateStmt.setString(index++, cors_allow_origins);
				updateStmt.setString(index++, cors_allow_headers);
				updateStmt.setString(index++, cors_allow_methods);
				updateStmt.setString(index++, cors_allow_expose_headers);				
				// responseCaching
				updateStmt.setString(index++, response_cache_stato);
				if(response_cache_seconds!=null) {
					updateStmt.setInt(index++, response_cache_seconds);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.INTEGER);
				}
				if(response_cache_max_msg_size!=null) {
					updateStmt.setLong(index++, response_cache_max_msg_size);
				}
				else {
					updateStmt.setNull(index++, java.sql.Types.BIGINT);
				}
				updateStmt.setInt(index++, response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setInt(index++, response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				updateStmt.setString(index++, response_cache_hash_url);
				updateStmt.setString(index++, response_cache_hash_query);
				updateStmt.setString(index++, response_cache_hash_query_list);
				updateStmt.setString(index++, response_cache_hash_headers);
				updateStmt.setString(index++, response_cache_hash_headers_list);
				updateStmt.setString(index++, response_cache_hash_payload);
				// responseCaching cache
				updateStmt.setString(index++, responseCaching_statoCache);
				updateStmt.setString(index++, responseCaching_dimensioneCache);
				updateStmt.setString(index++, responseCaching_algoritmoCache);
				updateStmt.setString(index++, responseCaching_idleCache);
				updateStmt.setString(index++, responseCaching_lifeCache);
				// consegna applicativi cache
				updateStmt.setString(index++, consegna_statoCache);
				updateStmt.setString(index++, consegna_dimensioneCache);
				updateStmt.setString(index++, consegna_algoritmoCache);
				updateStmt.setString(index++, consegna_idleCache);
				updateStmt.setString(index++, consegna_lifeCache);
				
				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + 
						DBUtils.formatSQLString(updateQuery, 
								cadenza_inoltro, 
								val_stato, val_controllo, 
								msg_diag_severita, msg_diag_severita_log4j, 
								autenticazione, 
								val_profiloCollaborazione, 
								modRisposta, utilizzoIndTelematico, 
								routingEnabled, gestioneManifest, 
								val_manifest, 
								tracciamentoBuste, transazioniTempiElaborazione, transazioniToken, dumpApplicativo, dumpPD, dumpPA,
								validazione_contenuti_stato,validazione_contenuti_tipo,
								registro_statoCache, registro_dimensioneCache, registro_algoritmoCache, registro_idleCache, registro_lifeCache,
								config_statoCache, config_dimensioneCache, config_algoritmoCache, config_idleCache, config_lifeCache,
								authz_statoCache, authz_dimensioneCache, authz_algoritmoCache, authz_idleCache, authz_lifeCache,
								authn_statoCache, authn_dimensioneCache, authn_algoritmoCache, authn_idleCache, authn_lifeCache,
								token_statoCache, token_dimensioneCache, token_algoritmoCache, token_idleCache, token_lifeCache,
								keystore_statoCache, keystore_dimensioneCache, keystore_algoritmoCache, keystore_idleCache, keystore_lifeCache, keystore_crlLifeCache,
								(multitenant!=null ? getValue(multitenant.getStato()) : null),
								(multitenant!=null ? getValue(multitenant.getFruizioneSceltaSoggettiErogatori()) : null),
								(multitenant!=null ? getValue(multitenant.getErogazioneSceltaSoggettiFruitori()) : null),
								cors_stato, cors_tipo, cors_all_allow_origins, cors_allow_credentials, cors_allow_max_age, cors_allow_max_age_seconds,
								cors_allow_origins, cors_allow_headers, cors_allow_methods, cors_allow_expose_headers,
								response_cache_stato, response_cache_seconds, response_cache_max_msg_size, 
								response_cache_hash_url, response_cache_hash_query, response_cache_hash_query_list, response_cache_hash_headers, response_cache_hash_headers_list, response_cache_hash_payload,
								responseCaching_statoCache, responseCaching_dimensioneCache, responseCaching_algoritmoCache, responseCaching_idleCache, responseCaching_lifeCache,
								consegna_statoCache, consegna_dimensioneCache, consegna_algoritmoCache, consegna_idleCache, consegna_lifeCache
								));

				n = updateStmt.executeUpdate();
				updateStmt.close();

				// delete from config_url_invocazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_REGOLE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// insert into config_protocolli
				if(config.getUrlInvocazione()!=null){
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
					sqlQueryObject.addInsertField("base_url", "?");
					sqlQueryObject.addInsertField("base_url_fruizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int indexP = 1;
					updateStmt.setString(indexP++, config.getUrlInvocazione().getBaseUrl());
					updateStmt.setString(indexP++, config.getUrlInvocazione().getBaseUrlFruizione());
					updateStmt.executeUpdate();
					updateStmt.close();
					
					if(config.getUrlInvocazione().sizeRegolaList()>0){
						for(int k=0; k<config.getUrlInvocazione().sizeRegolaList();k++){
							ConfigurazioneUrlInvocazioneRegola configUrlInvocazioneRegola = config.getUrlInvocazione().getRegola(k);
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.CONFIG_URL_REGOLE);
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("posizione", "?");
							sqlQueryObject.addInsertField("stato", "?");
							sqlQueryObject.addInsertField("descrizione", "?");
							sqlQueryObject.addInsertField("regexpr", "?");
							sqlQueryObject.addInsertField("regola", "?");
							sqlQueryObject.addInsertField("contesto_esterno", "?");
							sqlQueryObject.addInsertField("base_url", "?");
							sqlQueryObject.addInsertField("protocollo", "?");
							sqlQueryObject.addInsertField("ruolo", "?");
							sqlQueryObject.addInsertField("service_binding", "?");
							sqlQueryObject.addInsertField("tipo_soggetto", "?");
							sqlQueryObject.addInsertField("nome_soggetto", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							indexP = 1;
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getNome());
							updateStmt.setInt(indexP++, configUrlInvocazioneRegola.getPosizione());
							updateStmt.setString(indexP++, DriverConfigurazioneDB_LIB.getValue(configUrlInvocazioneRegola.getStato()));
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getDescrizione());
							updateStmt.setInt(indexP++, configUrlInvocazioneRegola.isRegexpr() ? CostantiDB.TRUE : CostantiDB.FALSE);
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getRegola());
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getContestoEsterno());
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getBaseUrl());
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getProtocollo());
							updateStmt.setString(indexP++, DriverConfigurazioneDB_LIB.getValue(configUrlInvocazioneRegola.getRuolo()));
							updateStmt.setString(indexP++, DriverConfigurazioneDB_LIB.getValue(configUrlInvocazioneRegola.getServiceBinding()));
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getSoggetto()!=null ? configUrlInvocazioneRegola.getSoggetto().getTipo() : null);
							updateStmt.setString(indexP++, configUrlInvocazioneRegola.getSoggetto()!=null ? configUrlInvocazioneRegola.getSoggetto().getNome() : null);
							updateStmt.executeUpdate();
							updateStmt.close();
	
						}
					}
				}
				
				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msg diag appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msg diag appender
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getMessaggiDiagnostici().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDiagAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_APPENDER, CostantiDB.MSG_DIAGN_APPENDER_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_APPENDER_SEQUENCE, CostantiDB.MSG_DIAGN_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDiagAppender<=0){
							throw new Exception("ID (msg diag appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							DriverConfigurazioneDB_LIB.log.debug("INSERT INTO "+CostantiDB.MSG_DIAGN_APPENDER_PROP+" (id_appender,nome,valore) VALUES ("+idMsgDiagAppender+","+appender.getProperty(l).getNome()+","+appender.getProperty(l).getValore()+")");
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idMsgDiagAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}

				// delete from tracciamento appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracciamento appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracciamento appender
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getTracciamento().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_APPENDER, CostantiDB.TRACCIAMENTO_APPENDER_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_APPENDER_SEQUENCE, CostantiDB.TRACCIAMENTO_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceAppender<=0){
							throw new Exception("ID (tracce appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							DriverConfigurazioneDB_LIB.log.debug("INSERT INTO "+CostantiDB.TRACCIAMENTO_APPENDER_PROP+" (id_appender,nome,valore) VALUES ("+idTracceAppender+","+appender.getProperty(l).getNome()+","+appender.getProperty(l).getValore()+")");
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idTracceAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}
				
				
				// delete from dump appender_prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from dump appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_APPENDER);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into dump appender
				if(config.getDump()!=null){
					for(int k=0; k<config.getDump().sizeOpenspcoopAppenderList();k++){
						OpenspcoopAppender appender = config.getDump().getOpenspcoopAppender(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo", appender.getTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idDumpAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.DUMP_APPENDER, CostantiDB.DUMP_APPENDER_COLUMN_ID, 
										CostantiDB.DUMP_APPENDER_SEQUENCE, CostantiDB.DUMP_APPENDER_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idDumpAppender<=0){
							throw new Exception("ID (dump appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<appender.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.DUMP_APPENDER_PROP);
							sqlQueryObject.addInsertField("id_appender", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idDumpAppender);
							updateStmt.setString(2, appender.getProperty(l).getNome());
							updateStmt.setString(3, appender.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}
				
				// Cache Regole
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" regole di cache");
				
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							sqlQueryObject.addInsertField("status_min", "?");
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							sqlQueryObject.addInsertField("status_max", "?");
						}
						sqlQueryObject.addInsertField("fault", "?");
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							sqlQueryObject.addInsertField("cache_seconds", "?");
						}
						sqlQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(sqlQuery);
						int indexCR = 1;
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							updateStmt.setInt(indexCR++, regola.getReturnCodeMin());
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							updateStmt.setInt(indexCR++, regola.getReturnCodeMax());
						}
						updateStmt.setInt(indexCR++, regola.getFault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							updateStmt.setInt(indexCR++, regola.getCacheTimeoutSeconds());
						}
						updateStmt.executeUpdate();
						updateStmt.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta regola di cache");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " regole di cache");
				
				// dumpConfigurazione
				CRUDDumpConfigurazione(type, con, dumpConfig, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				

				// delete from msgdiag ds prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from msgdiag ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.MSG_DIAGN_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into msgdiag ds
				if(config.getMessaggiDiagnostici()!=null){
					for(int k=0; k<config.getMessaggiDiagnostici().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getMessaggiDiagnostici().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idMsgDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGN_DS, CostantiDB.MSG_DIAGN_DS_COLUMN_ID, 
										CostantiDB.MSG_DIAGN_DS_SEQUENCE, CostantiDB.MSG_DIAGN_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idMsgDsAppender<=0){
							throw new Exception("ID (msgdiag ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.MSG_DIAGN_DS_PROP);
							sqlQueryObject.addInsertField("id_prop", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idMsgDsAppender);
							updateStmt.setString(2, ds.getProperty(l).getNome());
							updateStmt.setString(3, ds.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}

				// delete from tracce ds prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS_PROP);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete from tracce ds
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_DS);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				// insert into tracce ds
				if(config.getTracciamento()!=null){
					for(int k=0; k<config.getTracciamento().sizeOpenspcoopSorgenteDatiList();k++){
						OpenspcoopSorgenteDati ds = config.getTracciamento().getOpenspcoopSorgenteDati(k);
						
						List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", ds.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome_jndi", ds.getNomeJndi() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_database", ds.getTipoDatabase() , InsertAndGeneratedKeyJDBCType.STRING) );
						
						long idTracceDsAppender = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
								new CustomKeyGeneratorObject(CostantiDB.TRACCIAMENTO_DS, CostantiDB.TRACCIAMENTO_DS_COLUMN_ID, 
										CostantiDB.TRACCIAMENTO_DS_SEQUENCE, CostantiDB.TRACCIAMENTO_DS_TABLE_FOR_ID),
								listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
						if(idTracceDsAppender<=0){
							throw new Exception("ID (tracciamento ds appender) autoincrementale non ottenuto");
						}

						for(int l=0; l<ds.sizePropertyList();l++){
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addInsertTable(CostantiDB.TRACCIAMENTO_DS_PROP);
							sqlQueryObject.addInsertField("id_prop", "?");
							sqlQueryObject.addInsertField("nome", "?");
							sqlQueryObject.addInsertField("valore", "?");
							updateQuery = sqlQueryObject.createSQLInsert();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idTracceDsAppender);
							updateStmt.setString(2, ds.getProperty(l).getNome());
							updateStmt.setString(3, ds.getProperty(l).getValore());
							updateStmt.executeUpdate();
							updateStmt.close();
						}

					}
				}

				// ExtendedInfo
				if(extInfoConfigurazioneDriver!=null){
					
					//extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, CRUDType.UPDATE);
										
					if(config.sizeExtendedInfoList()>0){
						for(int l=0; l<config.sizeExtendedInfoList();l++){
							extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
						}
					}
				}
				

				break;

			case DELETE:
				
				//extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, CRUDType.DELETE);
				if(config.sizeExtendedInfoList()>0){
					for(int l=0; l<config.sizeExtendedInfoList();l++){
						extInfoConfigurazioneDriver.deleteExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
						extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l), CRUDType.UPDATE);
					}
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIGURAZIONE);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				break;

			}

			if (type == CostantiDB.CREATE)
				return idConfigurazione;
			else
				return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	private static void CRUDDumpConfigurazione(int type, Connection con, DumpConfigurazione dumpConfig, 
			Long idProprietario, String tipoProprietario) throws DriverConfigurazioneException {
		
		PreparedStatement updateStmt = null;
		try {
			switch (type) {
			case CREATE:
		
				if(dumpConfig==null) {
					break;
				}
				
				long idRequestIn = -1;
				if(dumpConfig.getRichiestaIngresso()!=null) {
					idRequestIn = createDumpConfigurazioneRegola(dumpConfig.getRichiestaIngresso(), con);
				}
				
				long idRequestOut = -1;
				if(dumpConfig.getRichiestaUscita()!=null) {
					idRequestOut = createDumpConfigurazioneRegola(dumpConfig.getRichiestaUscita(), con);
				}
				
				long idResponseIn = -1;
				if(dumpConfig.getRispostaIngresso()!=null) {
					idResponseIn = createDumpConfigurazioneRegola(dumpConfig.getRispostaIngresso(), con);
				}
				
				long idResponseOut = -1;
				if(dumpConfig.getRispostaUscita()!=null) {
					idResponseOut = createDumpConfigurazioneRegola(dumpConfig.getRispostaUscita(), con);
				}
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.DUMP_CONFIGURAZIONE);
				sqlQueryObject.addInsertField("proprietario", "?");
				sqlQueryObject.addInsertField("id_proprietario", "?");
				sqlQueryObject.addInsertField("dump_realtime", "?");
				sqlQueryObject.addInsertField("id_richiesta_ingresso", "?");
				sqlQueryObject.addInsertField("id_richiesta_uscita", "?");
				sqlQueryObject.addInsertField("id_risposta_ingresso", "?");
				sqlQueryObject.addInsertField("id_risposta_uscita", "?");
				String updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setString(index++, tipoProprietario);
				updateStmt.setLong(index++, idProprietario!=null ? idProprietario : -1);
				updateStmt.setString(index++, getValue(dumpConfig.getRealtime()));
				updateStmt.setLong(index++, idRequestIn);
				updateStmt.setLong(index++, idRequestOut);
				updateStmt.setLong(index++, idResponseIn);
				updateStmt.setLong(index++, idResponseOut);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				break;
				
			case UPDATE:
				
				// Per la delete recupero l'immagine attuale del confi
				DumpConfigurazione dumpConfigOld = DriverConfigurazioneDB_LIB.readDumpConfigurazione(con, idProprietario, tipoProprietario);
				if(dumpConfigOld!=null) {
					CRUDDumpConfigurazione(DELETE, con, dumpConfigOld, idProprietario, tipoProprietario);
				}
				
				// Creo la nuova immagine
				if(dumpConfig!=null) {
					CRUDDumpConfigurazione(CREATE, con, dumpConfig, idProprietario, tipoProprietario);
				}
				break;
				
			case DELETE:
				
				if(dumpConfig==null) {
					break;
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_CONFIGURAZIONE_REGOLA);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				
				if(dumpConfig.getRichiestaIngresso()!=null) {
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, dumpConfig.getRichiestaIngresso().getId());
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				if(dumpConfig.getRichiestaUscita()!=null) {
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, dumpConfig.getRichiestaUscita().getId());
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				if(dumpConfig.getRispostaIngresso()!=null) {
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, dumpConfig.getRispostaIngresso().getId());
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				if(dumpConfig.getRispostaUscita()!=null) {
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, dumpConfig.getRispostaUscita().getId());
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_CONFIGURAZIONE);
				if(!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG.equals(tipoProprietario)) {
					sqlQueryObject.addWhereCondition("id_proprietario=?");
				}
				sqlQueryObject.addWhereCondition("proprietario=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				index = 1;
				if(!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG.equals(tipoProprietario)) {
					updateStmt.setLong(index++, idProprietario);
				}
				updateStmt.setString(index++, tipoProprietario);
				updateStmt.executeUpdate();
				
				break;
			}
		
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] Exception [" + se.getMessage() + "].",se);
		} finally {
	
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			
		}
	}
	
	private static long createDumpConfigurazioneRegola(DumpConfigurazioneRegola dumpRegola, Connection con) throws Exception {
		List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
		listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("body", getValue(dumpRegola.getBody()) , InsertAndGeneratedKeyJDBCType.STRING) );
		listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("attachments", getValue(dumpRegola.getAttachments()) , InsertAndGeneratedKeyJDBCType.STRING) );
		listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("headers", getValue(dumpRegola.getHeaders()) , InsertAndGeneratedKeyJDBCType.STRING) );
		
		long idDumpconfigurazioneRegola = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
				new CustomKeyGeneratorObject(CostantiDB.DUMP_CONFIGURAZIONE_REGOLA, CostantiDB.DUMP_CONFIGURAZIONE_REGOLA_COLUMN_ID, 
						CostantiDB.DUMP_CONFIGURAZIONE_REGOLA_SEQUENCE, CostantiDB.DUMP_CONFIGURAZIONE_REGOLA_TABLE_FOR_ID),
				listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
		if(idDumpconfigurazioneRegola<=0){
			throw new Exception("ID (dump configurazione regola) autoincrementale non ottenuto");
		}
		return idDumpconfigurazioneRegola;
	}
	
	protected static DumpConfigurazione readDumpConfigurazione(Connection con, Long idProprietario, String tipoProprietario) throws Exception {
		PreparedStatement stm1=null;
		ResultSet rs1= null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DUMP_CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			if(!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG.equals(tipoProprietario)) {
				sqlQueryObject.addWhereCondition("id_proprietario=?");
			}
			sqlQueryObject.addWhereCondition("proprietario=?");
			sqlQueryObject.setANDLogicOperator(true);
			
			String sqlQuery = sqlQueryObject.createSQLQuery();
			
			stm1 = con.prepareStatement(sqlQuery);
			int index = 1;
			if(!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG.equals(tipoProprietario)) {
				stm1.setLong(index++, idProprietario);
			}
			stm1.setString(index++, tipoProprietario);
			rs1 = stm1.executeQuery();
			
			//recuper tutti gli appender e le prop di ogni appender
			DumpConfigurazione dumpConfig = null;
			if(rs1.next()){
				
				dumpConfig = new DumpConfigurazione();
				
				dumpConfig.setId(rs1.getLong("id"));
				
				dumpConfig.setRealtime(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("dump_realtime")));
				
				long idRequestIn = rs1.getLong("id_richiesta_ingresso");
				if(idRequestIn>0) {
					dumpConfig.setRichiestaIngresso(readDumpConfigurazioneRegola(con, idRequestIn));
				}
				
				long idRequestOut = rs1.getLong("id_richiesta_uscita");
				if(idRequestOut>0) {
					dumpConfig.setRichiestaUscita(readDumpConfigurazioneRegola(con, idRequestOut));
				}
				
				long idResponseIn = rs1.getLong("id_risposta_ingresso");
				if(idResponseIn>0) {
					dumpConfig.setRispostaIngresso(readDumpConfigurazioneRegola(con, idResponseIn));
				}
				
				long idResponseOut = rs1.getLong("id_risposta_uscita");
				if(idResponseOut>0) {
					dumpConfig.setRispostaUscita(readDumpConfigurazioneRegola(con, idResponseOut));
				}
				
			}
			return dumpConfig;

		}finally {
			try {
				if(rs1!=null) {
					rs1.close();
				}
			}catch(Exception e) {}
			try {
				if(stm1!=null) {
					stm1.close();
				}
			}catch(Exception e) {}
		}
	}
	
	private static DumpConfigurazioneRegola readDumpConfigurazioneRegola(Connection con, long idRegola) throws Exception {
		PreparedStatement stm1=null;
		ResultSet rs1= null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DUMP_CONFIGURAZIONE_REGOLA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			sqlQueryObject.setANDLogicOperator(true);
			
			String sqlQuery = sqlQueryObject.createSQLQuery();
			
			stm1 = con.prepareStatement(sqlQuery);
			stm1.setLong(1, idRegola);
			rs1 = stm1.executeQuery();
			
			//recuper tutti gli appender e le prop di ogni appender
			DumpConfigurazioneRegola dumpConfig = new DumpConfigurazioneRegola(); // ci sono i default
			if(rs1.next()){
				
				dumpConfig.setId(rs1.getLong("id"));
				dumpConfig.setBody(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("body")));
				dumpConfig.setAttachments(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("attachments")));
				dumpConfig.setHeaders(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("headers")));
				
			}

			return dumpConfig;

		}finally {
			try {
				if(rs1!=null) {
					rs1.close();
				}
			}catch(Exception e) {}
			try {
				if(stm1!=null) {
					stm1.close();
				}
			}catch(Exception e) {}
		}
	}
	
	private static void _normalizePositions(Trasformazioni trasformazioni) {
		if(trasformazioni==null || trasformazioni.sizeRegolaList()<=0) {
			return;
		}
		
		List<Integer> posizioni = new ArrayList<Integer>();
		HashMap<Integer, TrasformazioneRegola> regole = new HashMap<>();
		for (TrasformazioneRegola regola : trasformazioni.getRegolaList()) {
			posizioni.add(regola.getPosizione());
			regole.put(regola.getPosizione(), regola);
		}
		while(trasformazioni.sizeRegolaList()>0) {
			trasformazioni.removeRegola(0);
		}
		Collections.sort( posizioni );
		int posNew = 1;
		for (Integer pos : posizioni) {
			TrasformazioneRegola regola = regole.get(pos);
			regola.setPosizione(posNew);
			trasformazioni.addRegola(regola);
			posNew++;
		}
		
		// normalizzazione delle risposte
		for (TrasformazioneRegola regola : trasformazioni.getRegolaList()) {
			
			if(regola.getRispostaList()==null || regola.sizeRispostaList()<=0){
				continue;
			}
			
			List<Integer> posizioniRisposta = new ArrayList<Integer>();
			HashMap<Integer, TrasformazioneRegolaRisposta> regoleRisposta = new HashMap<>();
			for (TrasformazioneRegolaRisposta regolaRisposta : regola.getRispostaList()) {
				posizioniRisposta.add(regolaRisposta.getPosizione());
				regoleRisposta.put(regolaRisposta.getPosizione(), regolaRisposta);
			}
			while(regola.sizeRispostaList()>0) {
				regola.removeRisposta(0);
			}
			Collections.sort( posizioniRisposta );
			int posRispostaNew = 1;
			for (Integer posRisposta : posizioniRisposta) {
				TrasformazioneRegolaRisposta regolaRisposta = regoleRisposta.get(posRisposta);
				regolaRisposta.setPosizione(posRispostaNew);
				regola.addRisposta(regolaRisposta);
				posRispostaNew++;
			}
		}
	}
	
	private static void CRUDTrasformazioni(int type, Connection con, Trasformazioni trasformazioni, 
			Long idProprietario, boolean portaDelegata) throws Exception {
		
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDB);
		try {
			switch (type) {
			case CREATE:
		
				if(trasformazioni==null || trasformazioni.sizeRegolaList()<=0) {
					break;
				}
				
				_normalizePositions(trasformazioni);
				
				for (TrasformazioneRegola regola : trasformazioni.getRegolaList()) {
					
					String applicabilita_azioni = null;
					if(regola.getApplicabilita()!=null && regola.getApplicabilita().sizeAzioneList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < regola.getApplicabilita().sizeAzioneList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(regola.getApplicabilita().getAzione(i));
						}
						if(bf.length()>0) {
							applicabilita_azioni = bf.toString();
						}
					}
					
					String applicabilita_ct = null;
					if(regola.getApplicabilita()!=null && regola.getApplicabilita().sizeContentTypeList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < regola.getApplicabilita().sizeContentTypeList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(regola.getApplicabilita().getContentType(i));
						}
						if(bf.length()>0) {
							applicabilita_ct = bf.toString();
						}
					}
					
					List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("id_porta", idProprietario , InsertAndGeneratedKeyJDBCType.LONG) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", regola.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("posizione", regola.getPosizione() , InsertAndGeneratedKeyJDBCType.INT) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("stato", DriverConfigurazioneDB_LIB.getValue(regola.getStato()) , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_azioni", applicabilita_azioni , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_ct", applicabilita_ct , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_pattern", (regola.getApplicabilita()!=null ? regola.getApplicabilita().getPattern() : null) , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("req_conversione_enabled", ( regola.getRichiesta()!=null && regola.getRichiesta().getConversione()) ? CostantiDB.TRUE : CostantiDB.FALSE , InsertAndGeneratedKeyJDBCType.INT) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("req_conversione_tipo", (regola.getRichiesta()!=null ? regola.getRichiesta().getConversioneTipo() : null) , InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("req_content_type", (regola.getRichiesta()!=null ? regola.getRichiesta().getContentType() : null) , InsertAndGeneratedKeyJDBCType.STRING) );
					if(regola.getRichiesta()!=null && regola.getRichiesta().getTrasformazioneRest()!=null) {
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("rest_transformation", CostantiDB.TRUE , InsertAndGeneratedKeyJDBCType.INT) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("rest_method", regola.getRichiesta().getTrasformazioneRest().getMetodo() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("rest_path", regola.getRichiesta().getTrasformazioneRest().getPath() , InsertAndGeneratedKeyJDBCType.STRING) );
					}
					if(regola.getRichiesta()!=null && regola.getRichiesta().getTrasformazioneSoap()!=null) {
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_transformation", CostantiDB.TRUE , InsertAndGeneratedKeyJDBCType.INT) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_version", DriverConfigurazioneDB_LIB.getValue(regola.getRichiesta().getTrasformazioneSoap().getVersione()) , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_action", regola.getRichiesta().getTrasformazioneSoap().getSoapAction() , InsertAndGeneratedKeyJDBCType.STRING) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_envelope", (regola.getRichiesta().getTrasformazioneSoap().getEnvelope() ? CostantiDB.TRUE : CostantiDB.FALSE) , InsertAndGeneratedKeyJDBCType.INT) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_envelope_as_attach", (regola.getRichiesta().getTrasformazioneSoap().getEnvelopeAsAttachment() ? CostantiDB.TRUE : CostantiDB.FALSE) , InsertAndGeneratedKeyJDBCType.INT) );
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_envelope_tipo", regola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
					}
					// Insert and return generated key
					String tableName = null;
					String columnIdName = null;
					String sequence = null;
					String tableForId = null;
					if(portaDelegata) {
						tableName = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI;
						columnIdName = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_COLUMN_ID;
						sequence = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SEQUENCE;
						tableForId = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_TABLE_FOR_ID;
					}
					else {
						tableName = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
						columnIdName = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_COLUMN_ID;
						sequence = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SEQUENCE;
						tableForId = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_TABLE_FOR_ID;
					}
					long idtrasformazione = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(tipoDB), 
							new CustomKeyGeneratorObject(tableName, columnIdName, sequence, tableForId),
							listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
					if(idtrasformazione<=0){
						throw new Exception("ID autoincrementale non ottenuto");
					}
					
					if( regola.getRichiesta()!=null && 
							(
									regola.getRichiesta().getConversioneTemplate()!=null 
									||
									(regola.getRichiesta().getTrasformazioneSoap()!=null && regola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null)
							) ) {
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addUpdateTable(tableName);
						if(regola.getRichiesta().getConversioneTemplate()!=null ) {
							sqlQueryObject.addUpdateField("req_conversione_template", "?");
						}
						if(regola.getRichiesta().getTrasformazioneSoap()!=null && regola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null) {
							sqlQueryObject.addUpdateField("soap_envelope_template", "?");
						}
						sqlQueryObject.addWhereCondition(columnIdName+"=?");
						String updateQuery = sqlQueryObject.createSQLUpdate();
						updateStmt = con.prepareStatement(updateQuery);
						int index = 1;
						if(regola.getRichiesta().getConversioneTemplate()!=null ) {
							jdbcAdapter.setBinaryData(updateStmt, index++, regola.getRichiesta()!=null ? regola.getRichiesta().getConversioneTemplate() : null);
						}
						if(regola.getRichiesta().getTrasformazioneSoap()!=null && regola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null) {
							jdbcAdapter.setBinaryData(updateStmt, index++, regola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate());
						}
						updateStmt.setLong(index++, idtrasformazione);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
				
					
					if(regola.getRichiesta()!=null) {
						
						if(regola.getApplicabilita()!=null) {
							
							if(!portaDelegata && regola.getApplicabilita().sizeSoggettoList()>0) {
								// Soggetti
								int n=0;
								for (int j = 0; j < regola.getApplicabilita().sizeSoggettoList(); j++) {
									TrasformazioneRegolaApplicabilitaSoggetto soggetto = regola.getApplicabilita().getSoggetto(j);
									ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI);
									sqlQueryObject.addInsertField("id_trasformazione", "?");
									sqlQueryObject.addInsertField("tipo_soggetto", "?");
									sqlQueryObject.addInsertField("nome_soggetto", "?");
									String sqlQuery = sqlQueryObject.createSQLInsert();
									updateStmt = con.prepareStatement(sqlQuery);
									updateStmt.setLong(1, idtrasformazione);
									updateStmt.setString(2, soggetto.getTipo());
									updateStmt.setString(3, soggetto.getNome());
									updateStmt.executeUpdate();
									updateStmt.close();
									n++;
									DriverConfigurazioneDB_LIB.log.debug("Aggiunto soggetto [" + soggetto.getTipo() + "/"+soggetto.getNome()+"] alla Trasformazione[" + idtrasformazione + "]");
								}
								
								DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " soggetti alla Trasformazione[" + idtrasformazione + "]");
							}
							
							
							// serviziapplicativi
							if(regola.getApplicabilita().sizeServizioApplicativoList()>0) {
								ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
								if(portaDelegata) {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA);
								}
								else {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA);
								}
								sqlQueryObject.addInsertField("id_trasformazione", "?");
								sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
								String sqlQuery = sqlQueryObject.createSQLInsert();
								updateStmt = con.prepareStatement(sqlQuery);
				
								int i = 0;
								for (i = 0; i < regola.getApplicabilita().sizeServizioApplicativoList(); i++) {
									TrasformazioneRegolaApplicabilitaServizioApplicativo servizioApplicativo = regola.getApplicabilita().getServizioApplicativo(i);
									String nomeSA = servizioApplicativo.getNome();
									String nomeProprietarioSA = servizioApplicativo.getNomeSoggettoProprietario();
									String tipoProprietarioSA = servizioApplicativo.getTipoSoggettoProprietario();
									if (nomeSA == null || nomeSA.equals(""))
										throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Transf]::Nome del ServizioApplicativo associato non valido.");
									if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
										throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Transf]::Nome Proprietario del ServizioApplicativo associato non valido.");
									if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
										throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Transf]::Tipo Proprietario del ServizioApplicativo associato non valido.");
				
									long idSA = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				
									if (idSA <= 0)
										throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
				
									updateStmt.setLong(1, idtrasformazione);
									updateStmt.setLong(2, idSA);
									updateStmt.executeUpdate();
								}
								updateStmt.close();
								DriverConfigurazioneDB_LIB.log.debug("Inseriti " + i + " servizi applicativi autorizzati associati alla Trasformazione[" + idtrasformazione + "]");
							}
							
						}
						
						if(regola.getRichiesta().sizeHeaderList()>0) {
							for (TrasformazioneRegolaParametro parametro : regola.getRichiesta().getHeaderList()) {
								
								ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
								if(portaDelegata) {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER);
								}
								else {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER);
								}
								sqlQueryObject.addInsertField("id_trasformazione", "?");
								sqlQueryObject.addInsertField("tipo", "?");
								sqlQueryObject.addInsertField("nome", "?");
								sqlQueryObject.addInsertField("valore", "?");
								
								String updateQuery = sqlQueryObject.createSQLInsert();
								updateStmt = con.prepareStatement(updateQuery);
								int index = 1;
								updateStmt.setLong(index++, idtrasformazione);
								updateStmt.setString(index++, getValue(parametro.getConversioneTipo()));
								updateStmt.setString(index++, parametro.getNome());
								updateStmt.setString(index++, parametro.getValore());
								updateStmt.executeUpdate();
								updateStmt.close();
							}
						}
						
						if(regola.getRichiesta().sizeParametroUrlList()>0) {
							for (TrasformazioneRegolaParametro parametro : regola.getRichiesta().getParametroUrlList()) {
								
								ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
								if(portaDelegata) {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL);
								}
								else {
									sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL);
								}
								sqlQueryObject.addInsertField("id_trasformazione", "?");
								sqlQueryObject.addInsertField("tipo", "?");
								sqlQueryObject.addInsertField("nome", "?");
								sqlQueryObject.addInsertField("valore", "?");
								
								String updateQuery = sqlQueryObject.createSQLInsert();
								updateStmt = con.prepareStatement(updateQuery);
								int index = 1;
								updateStmt.setLong(index++, idtrasformazione);
								updateStmt.setString(index++, getValue(parametro.getConversioneTipo()));
								updateStmt.setString(index++, parametro.getNome());
								updateStmt.setString(index++, parametro.getValore());
								updateStmt.executeUpdate();
								updateStmt.close();
							}
						}
					}
					
					if(regola.sizeRispostaList()>0) {
						
						for (TrasformazioneRegolaRisposta regolaRisposta : regola.getRispostaList()) {
							
							String applicabilita_ct_response = null;
							if(regolaRisposta.getApplicabilita()!=null && regolaRisposta.getApplicabilita().sizeContentTypeList()>0) {
								StringBuilder bf = new StringBuilder();
								for (int i = 0; i < regolaRisposta.getApplicabilita().sizeContentTypeList(); i++) {
									if(i>0) {
										bf.append(",");
									}
									bf.append(regolaRisposta.getApplicabilita().getContentType(i));
								}
								if(bf.length()>0) {
									applicabilita_ct_response = bf.toString();
								}
							}
							
							listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("id_trasformazione", idtrasformazione , InsertAndGeneratedKeyJDBCType.LONG) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", regolaRisposta.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("posizione", regolaRisposta.getPosizione() , InsertAndGeneratedKeyJDBCType.INT) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_status_min", 
									(regolaRisposta.getApplicabilita()!=null && regolaRisposta.getApplicabilita().getReturnCodeMin()!=null && 
									regolaRisposta.getApplicabilita().getReturnCodeMin().intValue()>0) ? regolaRisposta.getApplicabilita().getReturnCodeMin().intValue() : null , InsertAndGeneratedKeyJDBCType.INT) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_status_max", 
									(regolaRisposta.getApplicabilita()!=null && regolaRisposta.getApplicabilita().getReturnCodeMax()!=null && 
									regolaRisposta.getApplicabilita().getReturnCodeMax().intValue()>0) ? regolaRisposta.getApplicabilita().getReturnCodeMax().intValue() : null , InsertAndGeneratedKeyJDBCType.INT) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_ct", applicabilita_ct_response , InsertAndGeneratedKeyJDBCType.STRING) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("applicabilita_pattern", (regolaRisposta.getApplicabilita()!=null ? regolaRisposta.getApplicabilita().getPattern() : null) , InsertAndGeneratedKeyJDBCType.STRING) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("conversione_enabled", regolaRisposta.getConversione() ? CostantiDB.TRUE : CostantiDB.FALSE , InsertAndGeneratedKeyJDBCType.INT) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("conversione_tipo", regolaRisposta.getConversioneTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("content_type", regolaRisposta.getContentType() , InsertAndGeneratedKeyJDBCType.STRING) );
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("return_code", 
									(regolaRisposta.getReturnCode()!=null && 
									regolaRisposta.getReturnCode().intValue()>0) ? regolaRisposta.getReturnCode().intValue() : null , InsertAndGeneratedKeyJDBCType.INT) );
							if(regolaRisposta.getTrasformazioneSoap()!=null) {
								listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_envelope", (regolaRisposta.getTrasformazioneSoap().getEnvelope() ? CostantiDB.TRUE : CostantiDB.FALSE) , InsertAndGeneratedKeyJDBCType.INT) );
								listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_envelope_as_attach", (regolaRisposta.getTrasformazioneSoap().getEnvelopeAsAttachment() ? CostantiDB.TRUE : CostantiDB.FALSE) , InsertAndGeneratedKeyJDBCType.INT) );
								listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("soap_envelope_tipo", regolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo() , InsertAndGeneratedKeyJDBCType.STRING) );
							}
							// Insert and return generated key
							tableName = null;
							columnIdName = null;
							sequence = null;
							tableForId = null;
							if(portaDelegata) {
								tableName = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE;
								columnIdName = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_COLUMN_ID;
								sequence = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_SEQUENCE;
								tableForId = CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_TABLE_FOR_ID;
							}
							else {
								tableName = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
								columnIdName = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_COLUMN_ID;
								sequence = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_SEQUENCE;
								tableForId = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_TABLE_FOR_ID;
							}
							long idtrasformazioneRisposta = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(tipoDB), 
									new CustomKeyGeneratorObject(tableName, columnIdName, sequence, tableForId),
									listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
							if(idtrasformazioneRisposta<=0){
								throw new Exception("ID autoincrementale non ottenuto");
							}
							
							if( 	regolaRisposta.getConversioneTemplate()!=null 
											||
											(regolaRisposta.getTrasformazioneSoap()!=null && regolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null)
								 ) {
								ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
								sqlQueryObject.addUpdateTable(tableName);
								if(regolaRisposta.getConversioneTemplate()!=null ) {
									sqlQueryObject.addUpdateField("conversione_template", "?");
								}
								if(regolaRisposta.getTrasformazioneSoap()!=null && regolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null) {
									sqlQueryObject.addUpdateField("soap_envelope_template", "?");
								}
								sqlQueryObject.addWhereCondition(columnIdName+"=?");
								String updateQuery = sqlQueryObject.createSQLUpdate();
								updateStmt = con.prepareStatement(updateQuery);
								int index = 1;
								if(regolaRisposta.getConversioneTemplate()!=null ) {
									jdbcAdapter.setBinaryData(updateStmt, index++, regolaRisposta.getConversioneTemplate());
								}
								if(regolaRisposta.getTrasformazioneSoap()!=null && regolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()!=null) {
									jdbcAdapter.setBinaryData(updateStmt, index++, regolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate());
								}
								updateStmt.setLong(index++, idtrasformazioneRisposta);
								updateStmt.executeUpdate();
								updateStmt.close();
							}
							
							if(regolaRisposta.sizeHeaderList()>0) {
								for (TrasformazioneRegolaParametro parametro : regolaRisposta.getHeaderList()) {
									
									ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
									if(portaDelegata) {
										sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER);
									}
									else {
										sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER);
									}
									sqlQueryObject.addInsertField("id_transform_risp", "?");
									sqlQueryObject.addInsertField("tipo", "?");
									sqlQueryObject.addInsertField("nome", "?");
									sqlQueryObject.addInsertField("valore", "?");
									
									String updateQuery = sqlQueryObject.createSQLInsert();
									updateStmt = con.prepareStatement(updateQuery);
									int index = 1;
									updateStmt.setLong(index++, idtrasformazioneRisposta);
									updateStmt.setString(index++, getValue(parametro.getConversioneTipo()));
									updateStmt.setString(index++, parametro.getNome());
									updateStmt.setString(index++, parametro.getValore());
									updateStmt.executeUpdate();
									updateStmt.close();
								}
							}
						}
						
					}
					
				}
				
				break;
				
			case UPDATE:
				
				// Per la delete recupero l'immagine attuale del configurazione
				Trasformazioni trasformazioniOld = DriverConfigurazioneDB_LIB.readTrasformazioni(idProprietario, portaDelegata, con);
				if(trasformazioniOld!=null) {
					CRUDTrasformazioni(DELETE, con, trasformazioniOld, idProprietario, portaDelegata);
				}
				
				// Creo la nuova immagine
				if(trasformazioni!=null) {
					
					_normalizePositions(trasformazioni);
					
					CRUDTrasformazioni(CREATE, con, trasformazioni, idProprietario, portaDelegata);
				}
				break;
				
			case DELETE:
				
				if(trasformazioni==null) {
					break;
				}
				
				String tableName = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
				String tableNameSoggetti = portaDelegata ? null : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI;
				String tableNameSA = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA;
				String tableNameHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER;
				String tableNameUrl = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL;
				String tableNameRisposte = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
				String tableNameRisposteHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
				List<Long> idTrasformazioneList = new ArrayList<>();
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(tableName);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				String updateQuery = sqlQueryObject.createSQLQuery();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idProprietario);
				rs = updateStmt.executeQuery();
				while(rs.next()) {
					idTrasformazioneList.add(rs.getLong("id"));
				}
				rs.close();
				updateStmt.close();
				
				if(idTrasformazioneList.size()<=0) {
					break;
				}
				
				for (Long idTrasformazione : idTrasformazioneList) {
					
					if(tableNameSoggetti!=null) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(tableNameSoggetti);
						sqlQueryObject.addWhereCondition("id_trasformazione=?");
						sqlQueryObject.setANDLogicOperator(true);
						updateQuery = sqlQueryObject.createSQLDelete();
						updateStmt = con.prepareStatement(updateQuery);
						updateStmt.setLong(1, idTrasformazione);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(tableNameSA);
					sqlQueryObject.addWhereCondition("id_trasformazione=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idTrasformazione);
					updateStmt.executeUpdate();
					updateStmt.close();
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(tableNameHeader);
					sqlQueryObject.addWhereCondition("id_trasformazione=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idTrasformazione);
					updateStmt.executeUpdate();
					updateStmt.close();
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(tableNameUrl);
					sqlQueryObject.addWhereCondition("id_trasformazione=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idTrasformazione);
					updateStmt.executeUpdate();
					updateStmt.close();
					
					List<Long> idTrasformazioneRisposteList = new ArrayList<>();
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addFromTable(tableNameRisposte);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addWhereCondition("id_trasformazione=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLQuery();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idTrasformazione);
					rs = updateStmt.executeQuery();
					while(rs.next()) {
						idTrasformazioneRisposteList.add(rs.getLong("id"));
					}
					rs.close();
					updateStmt.close();
					
					if(idTrasformazioneRisposteList.size()>0) {
						
						for (Long idTrasformazioneRisposta : idTrasformazioneRisposteList) {
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
							sqlQueryObject.addDeleteTable(tableNameRisposteHeader);
							sqlQueryObject.addWhereCondition("id_transform_risp=?");
							sqlQueryObject.setANDLogicOperator(true);
							updateQuery = sqlQueryObject.createSQLDelete();
							updateStmt = con.prepareStatement(updateQuery);
							updateStmt.setLong(1, idTrasformazioneRisposta);
							updateStmt.executeUpdate();
							updateStmt.close();
							
						}
						
					}
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(tableNameRisposte);
					sqlQueryObject.addWhereCondition("id_trasformazione=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idTrasformazione);
					updateStmt.executeUpdate();
					updateStmt.close();
					
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(tableName);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idProprietario);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				break;
			}
		
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] Exception [" + se.getMessage() + "].",se);
		} finally {
	
			try {
				if(rs!=null)rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			
		}
	}
	
	public static Trasformazioni readTrasformazioni(long idPorta, boolean portaDelegata, Connection con) throws Exception {
		
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		Trasformazioni trasformazioni = null;
		try {
			
			String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_porta=?");
			sqlQueryObject.addOrderBy("posizione");
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idPorta);
	
			log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPorta));
			rs = stm.executeQuery();
	
			while (rs.next()) {
				
				if(trasformazioni==null) {
					trasformazioni = new Trasformazioni();
				}
				
				TrasformazioneRegola regola = new TrasformazioneRegola();
				
				String nome = rs.getString("nome");
				regola.setNome(nome);
				
				int posizione = rs.getInt("posizione");
				regola.setPosizione(posizione);
				
				StatoFunzionalita stato = DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stato"));
				regola.setStato(stato);
				
				String applicabilita_azioni = rs.getString("applicabilita_azioni");
				String applicabilita_ct = rs.getString("applicabilita_ct");
				String applicabilita_pattern = rs.getString("applicabilita_pattern");
				if( (applicabilita_azioni!=null && !"".equals(applicabilita_azioni)) ||
						(applicabilita_ct!=null && !"".equals(applicabilita_ct)) ||
						(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)) 
						) {
					TrasformazioneRegolaApplicabilitaRichiesta applicabilita = new TrasformazioneRegolaApplicabilitaRichiesta();
					
					if( (applicabilita_azioni!=null && !"".equals(applicabilita_azioni)) ) {
						if(applicabilita_azioni.contains(",")) {
							String [] tmp = applicabilita_azioni.split(",");
							for (int i = 0; i < tmp.length; i++) {
								applicabilita.addAzione(tmp[i].trim());
							}
						}
						else {
							applicabilita.addAzione(applicabilita_azioni);
						}
					}
					
					if( (applicabilita_ct!=null && !"".equals(applicabilita_ct)) ) {
						if(applicabilita_ct.contains(",")) {
							String [] tmp = applicabilita_ct.split(",");
							for (int i = 0; i < tmp.length; i++) {
								applicabilita.addContentType(tmp[i].trim());
							}
						}
						else {
							applicabilita.addContentType(applicabilita_ct);
						}
					}
					
					if(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)){
						applicabilita.setPattern(applicabilita_pattern);
					}
					
					regola.setApplicabilita(applicabilita);
				}
				
				TrasformazioneRegolaRichiesta richiesta = new TrasformazioneRegolaRichiesta();
				
				int req_conversione_enabled = rs.getInt("req_conversione_enabled");
				if(CostantiDB.TRUE == req_conversione_enabled) {
					richiesta.setConversione(true);
				}
				else {
					richiesta.setConversione(false);
				}
				richiesta.setConversioneTipo(rs.getString("req_conversione_tipo"));
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDB);
				richiesta.setConversioneTemplate(jdbcAdapter.getBinaryData(rs, "req_conversione_template"));
				richiesta.setContentType(rs.getString("req_content_type"));
				
				int trasformazione_rest = rs.getInt("rest_transformation");
				if(CostantiDB.TRUE == trasformazione_rest) {
					TrasformazioneRest trasformazioneRest = new TrasformazioneRest();
					trasformazioneRest.setMetodo(rs.getString("rest_method"));
					trasformazioneRest.setPath(rs.getString("rest_path"));
					richiesta.setTrasformazioneRest(trasformazioneRest);
				}
					
				int trasformazione_soap = rs.getInt("soap_transformation");
				if(CostantiDB.TRUE == trasformazione_soap) {
					TrasformazioneSoap trasformazioneSoap = new TrasformazioneSoap();
					
					trasformazioneSoap.setVersione(DriverConfigurazioneDB_LIB.getEnumVersioneSOAP(rs.getString("soap_version")));
					trasformazioneSoap.setSoapAction(rs.getString("soap_action"));
					
					int envelope = rs.getInt("soap_envelope");
					if(CostantiDB.TRUE == envelope) {
						trasformazioneSoap.setEnvelope(true);
					}
					else {
						trasformazioneSoap.setEnvelope(false);
					}
					
					int envelope_as_attach = rs.getInt("soap_envelope_as_attach");
					if(CostantiDB.TRUE == envelope_as_attach) {
						trasformazioneSoap.setEnvelopeAsAttachment(true);
						
						trasformazioneSoap.setEnvelopeBodyConversioneTipo(rs.getString("soap_envelope_tipo"));
						trasformazioneSoap.setEnvelopeBodyConversioneTemplate(jdbcAdapter.getBinaryData(rs, "soap_envelope_template"));
					}
					else {
						trasformazioneSoap.setEnvelopeAsAttachment(false);
					}
					
					richiesta.setTrasformazioneSoap(trasformazioneSoap);
				}
				
				regola.setId(rs.getLong("id"));
				
				regola.setRichiesta(richiesta);
				
				trasformazioni.addRegola(regola);
				
			}
			rs.close();
			stm.close();
			
			if(trasformazioni==null) {
				return trasformazioni;
			}
			
			for (TrasformazioneRegola regola : trasformazioni.getRegolaList()) {
				

				// ** SOGGETTI **
				
				if(!portaDelegata) {
					
					nomeTabella = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI;
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_trasformazione=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, regola.getId());
					rs = stm.executeQuery();
	
					while (rs.next()) {
						
						if(regola.getApplicabilita()==null) {
							regola.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
						}
						
						TrasformazioneRegolaApplicabilitaSoggetto soggetto = new TrasformazioneRegolaApplicabilitaSoggetto();
						soggetto.setTipo(rs.getString("tipo_soggetto"));
						soggetto.setNome(rs.getString("nome_soggetto"));
						
						regola.getApplicabilita().addSoggetto(soggetto);
					
					}
					rs.close();
					stm.close();
				}
				
				
				// ** APPLICATIVI **
				
				nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_trasformazione=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, regola.getId());
				rs = stm.executeQuery();

				// per ogni entry 
				// prendo l'id del servizio associato, recupero il nome e
				// aggiungo
				// il servizio applicativo alla PortaDelegata da ritornare
				while (rs.next()) {
					long idSA = rs.getLong("id_servizio_applicativo");

					if (idSA != 0) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addSelectField("tipo_soggetto");
						sqlQueryObject.addSelectField("nome_soggetto");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm1 = con.prepareStatement(sqlQuery);
						stm1.setLong(1, idSA);

						log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA));

						rs1 = stm1.executeQuery();

						TrasformazioneRegolaApplicabilitaServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new TrasformazioneRegolaApplicabilitaServizioApplicativo();
							servizioApplicativo.setId(idSA);
							servizioApplicativo.setNome(rs1.getString("nome"));
							servizioApplicativo.setTipoSoggettoProprietario(rs1.getString("tipo_soggetto"));
							servizioApplicativo.setNomeSoggettoProprietario(rs1.getString("nome_soggetto"));
							if(regola.getApplicabilita()==null) {
								regola.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
							}
							regola.getApplicabilita().addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
					}
				}
				rs.close();
				stm.close();
				
				
				// ** HEADER **
				
				nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_trasformazione=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, regola.getId());
		
				log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPorta));
				rs = stm.executeQuery();
		
				while (rs.next()) {
					TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
					parametro.setConversioneTipo(getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
					parametro.setNome(rs.getString("nome"));
					parametro.setValore(rs.getString("valore"));
					parametro.setId(rs.getLong("id"));
					regola.getRichiesta().addHeader(parametro);
				}
				
				rs.close();
				stm.close();
				
				
				// ** URL **
				
				nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_trasformazione=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, regola.getId());
		
				log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPorta));
				rs = stm.executeQuery();
		
				while (rs.next()) {
					TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
					parametro.setConversioneTipo(getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
					parametro.setNome(rs.getString("nome"));
					parametro.setValore(rs.getString("valore"));
					parametro.setId(rs.getLong("id"));
					regola.getRichiesta().addParametroUrl(parametro);
				}
				
				rs.close();
				stm.close();
				
				
				
				// ** RISPOSTE **
				
				nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_trasformazione=?");
				sqlQueryObject.addOrderBy("posizione");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, regola.getId());
		
				log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPorta));
				rs = stm.executeQuery();
		
				while (rs.next()) {
				
					TrasformazioneRegolaRisposta risposta = new TrasformazioneRegolaRisposta();
					
					String nome = rs.getString("nome");
					risposta.setNome(nome);
					
					int posizione = rs.getInt("posizione");
					risposta.setPosizione(posizione);
					
					int applicabilita_status_min = rs.getInt("applicabilita_status_min");
					int applicabilita_status_max = rs.getInt("applicabilita_status_max");
					String applicabilita_ct = rs.getString("applicabilita_ct");
					String applicabilita_pattern = rs.getString("applicabilita_pattern");
					if( (applicabilita_status_min >0 || applicabilita_status_max>0) ||
							(applicabilita_ct!=null && !"".equals(applicabilita_ct)) ||
							(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)) 
							) {
						TrasformazioneRegolaApplicabilitaRisposta applicabilita = new TrasformazioneRegolaApplicabilitaRisposta();
						
						if(applicabilita_status_min>0) {
							applicabilita.setReturnCodeMin(applicabilita_status_min);
						}
						if(applicabilita_status_max>0) {
							applicabilita.setReturnCodeMax(applicabilita_status_max);
						}

						if( (applicabilita_ct!=null && !"".equals(applicabilita_ct)) ) {
							if(applicabilita_ct.contains(",")) {
								String [] tmp = applicabilita_ct.split(",");
								for (int i = 0; i < tmp.length; i++) {
									applicabilita.addContentType(tmp[i].trim());
								}
							}
							else {
								applicabilita.addContentType(applicabilita_ct);
							}
						}
						
						if(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)){
							applicabilita.setPattern(applicabilita_pattern);
						}
						
						risposta.setApplicabilita(applicabilita);
					}
					
					int conversione_enabled = rs.getInt("conversione_enabled");
					if(CostantiDB.TRUE == conversione_enabled) {
						risposta.setConversione(true);
					}
					else {
						risposta.setConversione(false);
					}
					risposta.setConversioneTipo(rs.getString("conversione_tipo"));
					IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDB);
					risposta.setConversioneTemplate(jdbcAdapter.getBinaryData(rs, "conversione_template"));
					risposta.setContentType(rs.getString("content_type"));
					int return_code = rs.getInt("return_code");
					if(return_code>0) {
						risposta.setReturnCode(return_code);
					}
					
					if(regola.getRichiesta().getTrasformazioneRest()!=null) { // La risposta deve essere ritrasformata in soap
					
						TrasformazioneSoapRisposta trasformazioneSoap = new TrasformazioneSoapRisposta();
						
						int envelope = rs.getInt("soap_envelope");
						if(CostantiDB.TRUE == envelope) {
							trasformazioneSoap.setEnvelope(true);
						}
						else {
							trasformazioneSoap.setEnvelope(false);
						}
						
						int envelope_as_attach = rs.getInt("soap_envelope_as_attach");
						if(CostantiDB.TRUE == envelope_as_attach) {
							trasformazioneSoap.setEnvelopeAsAttachment(true);
							
							trasformazioneSoap.setEnvelopeBodyConversioneTipo(rs.getString("soap_envelope_tipo"));
							trasformazioneSoap.setEnvelopeBodyConversioneTemplate(jdbcAdapter.getBinaryData(rs, "soap_envelope_template"));
						}
						else {
							trasformazioneSoap.setEnvelopeAsAttachment(false);
						}
						
						risposta.setTrasformazioneSoap(trasformazioneSoap);
						
					}

					risposta.setId(rs.getLong("id"));
					
					regola.addRisposta(risposta);
					
				}
				
				rs.close();
				stm.close();
				
				
				// ** HEADER RISPOSTA **
				
				for (TrasformazioneRegolaRisposta risposta : regola.getRispostaList()) {
					
					nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_transform_risp=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, risposta.getId());
			
					log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPorta));
					rs = stm.executeQuery();
			
					while (rs.next()) {
						TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
						parametro.setConversioneTipo(getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
						parametro.setNome(rs.getString("nome"));
						parametro.setValore(rs.getString("valore"));
						parametro.setId(rs.getLong("id"));
						risposta.addHeader(parametro);
					}
					
					rs.close();
					stm.close();
					
				}
			}
			
		}finally {
			try {
				if(rs1!=null)
					rs1.close();
			}catch(Exception eClose) {}
			try {
				if(rs!=null)
					rs.close();
			}catch(Exception eClose) {}
			try {
				if(stm1!=null)
					stm1.close();
			}catch(Exception eClose) {}
			try {
				if(stm!=null)
					stm.close();
			}catch(Exception eClose) {}
		}
		
		return trasformazioni;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/** Metodi di Utilita' */

	/**
	 * Ritorna il connettore con idConnettore, null se il connettore non esiste
	 */
	protected static Connettore getConnettore(long idConnettore, Connection connection) throws DriverConfigurazioneException {

		Connettore connettore = null;

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverConfigurazioneDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			if (rs.next()) {
				String endpoint = rs.getString("endpointtype");
				if (endpoint == null || endpoint.equals("") || endpoint.equals(TipiConnettore.DISABILITATO.getNome())) {
					connettore = new Connettore();
					connettore.setNome(rs.getString("nome_connettore"));
					connettore.setTipo(TipiConnettore.DISABILITATO.getNome());
					connettore.setId(idConnettore);

				} else {
					Property prop = null;
					connettore = new Connettore();
					connettore.setNome(rs.getString("nome_connettore"));
					connettore.setTipo(endpoint);
					// l'id del connettore e' quello passato come parametro
					connettore.setId(idConnettore);

					// Debug
					if(rs.getInt("debug")==1){
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_DEBUG);
						prop.setValore("true");
						connettore.addProperty(prop);
					}
					
					// Proxy
					if(rs.getInt("proxy")==1){
						
						String tmp = rs.getString("proxy_type");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_TYPE);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_hostname");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_port");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_PORT);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_username");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_USERNAME);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_password");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_PASSWORD);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
					}
					
					// Tempi Risposta
					int connectionTimeout = rs.getInt("connection_timeout");
					if(connectionTimeout>0){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
						prop.setValore(connectionTimeout+"");
						connettore.addProperty(prop);
						
					}
					int readTimeout = rs.getInt("read_timeout");
					if(readTimeout>0){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
						prop.setValore(readTimeout+"");
						connettore.addProperty(prop);
						
					}
					int avgResponseTime = rs.getInt("avg_response_time");
					if(avgResponseTime>0){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
						prop.setValore(avgResponseTime+"");
						connettore.addProperty(prop);
						
					}
					
					// transfer_mode
					String transferMode = rs.getString("transfer_mode");
					if(transferMode!=null && !"".equals(transferMode)){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
						prop.setValore(transferMode.trim());
						connettore.addProperty(prop);
						
						transferMode = rs.getString("transfer_mode_chunk_size");
						if(transferMode!=null && !"".equals(transferMode)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
							prop.setValore(transferMode.trim());
							connettore.addProperty(prop);
						}
					}
					
					// redirect_mode
					String redirectMode = rs.getString("redirect_mode");
					if(redirectMode!=null && !"".equals(redirectMode)){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
						prop.setValore(redirectMode.trim());
						connettore.addProperty(prop);
						
						redirectMode = rs.getString("redirect_max_hop");
						if(redirectMode!=null && !"".equals(redirectMode)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
							prop.setValore(redirectMode.trim());
							connettore.addProperty(prop);
						}
					}
					
					// token policy
					String tokenPolicy = rs.getString("token_policy");
					if(tokenPolicy!=null && !"".equals(tokenPolicy)){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_TOKEN_POLICY);
						prop.setValore(tokenPolicy.trim());
						connettore.addProperty(prop);
						
					}


					if (endpoint.equals(CostantiDB.CONNETTORE_TIPO_HTTP)) {
						//	url
						String value = rs.getString("url");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverConfigurazioneException("Connettore di tipo http possiede una url non definita");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
						prop.setValore(value);
						connettore.addProperty(prop);
					} else if (endpoint.equals(TipiConnettore.JMS.getNome())){//jms

						// nome coda/topic
						String value = rs.getString("nome");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverConfigurazioneException("Connettore di tipo jms possiede il nome della coda/topic non definito");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_JMS_NOME);
						prop.setValore(value);
						connettore.addProperty(prop);

						// tipo
						value = rs.getString("tipo");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverConfigurazioneException("Connettore di tipo jms possiede il tipo della coda non definito");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_JMS_TIPO);
						prop.setValore(value);
						connettore.addProperty(prop);

						// connection-factory
						value = rs.getString("connection_factory");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverConfigurazioneException("Connettore di tipo jms non possiede la definizione di una Connection Factory");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
						prop.setValore(value);
						connettore.addProperty(prop);

						// send_as
						value = rs.getString("send_as");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverConfigurazioneException("Connettore di tipo jms possiede il tipo dell'oggetto JMS non definito");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_JMS_SEND_AS);
						prop.setValore(value);
						connettore.addProperty(prop);

						// user
						String usr = rs.getString("utente");
						if (usr != null && !usr.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_USER);
							prop.setValore(usr);
							connettore.addProperty(prop);
						}
						// password
						String pwd = rs.getString("password");
						if (pwd != null && !pwd.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PWD);
							prop.setValore(pwd);
							connettore.addProperty(prop);
						}
						// context-java.naming.factory.initial
						String initcont = rs.getString("initcont");
						if (initcont != null && !initcont.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
							prop.setValore(initcont);
							connettore.addProperty(prop);
						}
						// context-java.naming.factory.url.pkgs
						String urlpkg = rs.getString("urlpkg");
						if (urlpkg != null && !urlpkg.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
							prop.setValore(urlpkg);
							connettore.addProperty(prop);
						}
						// context-java.naming.provider.url
						String provurl = rs.getString("provurl");
						if (provurl != null && !provurl.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
							prop.setValore(provurl);
							connettore.addProperty(prop);
						}

					}else if(endpoint.equals(TipiConnettore.NULL.getNome())){
						//nessuna proprieta per connettore null
					}else if(endpoint.equals(TipiConnettore.NULLECHO.getNome())){
						//nessuna proprieta per connettore nullEcho
					}else if (!endpoint.equals(TipiConnettore.DISABILITATO.getNome())) {
						if(rs.getLong("custom")==1){
							// connettore custom
							DriverConfigurazioneDB_LIB.readPropertiesConnettoreCustom(idConnettore,connettore,connection);
							connettore.setCustom(true);
						}
						else{
							// legge da file properties
							connettore.setPropertyList(ConnettorePropertiesUtilities.getPropertiesConnettore(endpoint,connection,DriverConfigurazioneDB_LIB.tipoDB));
						}
					}

				}
			}
			
			// Extended Info
			DriverConfigurazioneDB_LIB.readPropertiesConnettoreExtendedInfo(idConnettore,connettore,connection);
			
			return connettore;
		} catch (SQLException sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::getConnettore] SQLException : " + sqle.getMessage(),sqle);
		}catch (DriverConfigurazioneException e) {
			throw e;
		}catch (Exception sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::getConnettore] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	protected static void readPropertiesConnettoreCustom(long idConnettore, Connettore connettore, Connection connection) throws DriverConfigurazioneException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverConfigurazioneDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();
			
			while (rs.next()) {
				String nome = rs.getString("name");
				String valore = rs.getString("value");
				
				if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){ // lo posso aver aggiunto prima
					boolean found = false;
					for (int i = 0; i < connettore.sizePropertyList(); i++) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())){
							// already exists
							found = true;
							break;
						}
					}
					if(found){
						continue; // è gia stato aggiunto.
					}
				}
				
				Property prop = new Property();
				prop.setNome(nome);
				prop.setValore(valore);
				connettore.addProperty(prop);
			}
			
			rs.close();
			stm.close();

		} catch (SQLException sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::readPropertiesConnettoreCustom] SQLException : " + sqle.getMessage(),sqle);
		}catch (Exception sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::readPropertiesConnettoreCustom] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	protected static void readPropertiesConnettoreExtendedInfo(long idConnettore, Connettore connettore, Connection connection) throws DriverConfigurazioneException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			sqlQueryObject.addWhereLikeCondition("name", CostantiConnettori.CONNETTORE_EXTENDED_PREFIX+"%");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverConfigurazioneDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			while (rs.next()) {
				String nome = rs.getString("name");
				String valore = rs.getString("value");
				
				// Le proprietà sono già state inserite in caso di connettore custom
				boolean found = false;
				for (int i = 0; i < connettore.sizePropertyList(); i++) {
					if(nome.equals(connettore.getProperty(i).getNome())){
						// already exists
						found = true;
						break;
					}
				}
				if(found){
					continue; // è gia stato aggiunto.
				}
				
				Property prop = new Property();
				prop.setNome(nome);
				prop.setValore(valore);
				connettore.addProperty(prop);
			}
			
			rs.close();
			stm.close();

		} catch (SQLException sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::readPropertiesConnettoreExtendedInfo] SQLException : " + sqle.getMessage(),sqle);
		}catch (Exception sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::readPropertiesConnettoreExtendedInfo] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}

	/**
	 * Se il connettore e' null lo considero disabilitato
	 * @param connettore
	 * @return true se il connettore e' abilitato
	 */
	protected static boolean isConnettoreAbilitato(Connettore connettore) {

		//Se connettore null oppure il tipo e' null o "" o DISABILITATO allora connettore disabilitato
		//altrimenti e' abilitato.
		if (connettore == null)
			return false;

		String tipo = connettore.getTipo();
		if(!TipiConnettore.DISABILITATO.getNome().equals(tipo)) return true;
		else return false;
	}

	protected static long getIdConnettore_SA_RISP(long idServizioApplicativo,Connection con) throws DriverConfigurazioneException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setLong(1, idServizioApplicativo);
			rs=stm.executeQuery();

			if(rs.next()){
				idConnettore = rs.getLong("id_connettore_risp");
			}

			return idConnettore;

		}catch (SQLException e) {
			throw new DriverConfigurazioneException(e);
		}catch (Exception e) {
			throw new DriverConfigurazioneException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}

	protected static long getIdConnettore_SA_INV(long idServizioApplicativo,Connection con) throws DriverConfigurazioneException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setLong(1, idServizioApplicativo);
			rs=stm.executeQuery();

			if(rs.next()){
				idConnettore = rs.getLong("id_connettore_inv");
			}

			return idConnettore;

		}catch (SQLException e) {
			throw new DriverConfigurazioneException(e);
		}catch (Exception e) {
			throw new DriverConfigurazioneException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}

	
	
	
	
	
	public static GestioneErrore getGestioneErrore(long idGestioneErrore,Connection con) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		GestioneErrore gestioneErrore = null;
		
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			
			// Get gestione errore generale
			sqlQueryObject.addFromTable(CostantiDB.GESTIONE_ERRORE);
			sqlQueryObject.addSelectField("comportamento_default");
			sqlQueryObject.addSelectField("cadenza_rispedizione");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addWhereCondition("id=?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idGestioneErrore);
			rs = stm.executeQuery();

			if (rs.next()) {
				gestioneErrore = new GestioneErrore();
				gestioneErrore.setId(idGestioneErrore);
				gestioneErrore.setComportamento(GestioneErroreComportamento.toEnumConstant(rs.getString("comportamento_default")));
				gestioneErrore.setCadenzaRispedizione(rs.getString("cadenza_rispedizione"));
				gestioneErrore.setNome(rs.getString("nome"));

				//trasporto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_gestione_errore = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, gestioneErrore.getId());
				rs1 = stm1.executeQuery();
				while(rs1.next())
				{
					GestioneErroreCodiceTrasporto trasporto = new GestioneErroreCodiceTrasporto();
					trasporto.setComportamento(GestioneErroreComportamento.toEnumConstant(rs1.getString("comportamento")));
					trasporto.setCadenzaRispedizione(rs1.getString("cadenza_rispedizione"));
					if(rs1.getLong("valore_massimo")>0){
						String maxVal = ""+rs1.getLong("valore_massimo");
						trasporto.setValoreMassimo(!maxVal.equals("") ? Integer.valueOf(maxVal) : null);
					}
					if(rs1.getLong("valore_minimo")>0){
						String minVal = ""+rs1.getLong("valore_minimo");
						trasporto.setValoreMinimo(!minVal.equals("") ? Integer.valueOf(minVal) : null);
					}
					gestioneErrore.addCodiceTrasporto(trasporto);
				}
				rs1.close();
				stm1.close();

				//soap
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GESTIONE_ERRORE_SOAP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_gestione_errore = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, gestioneErrore.getId());
				rs1 = stm1.executeQuery();
				while(rs1.next())
				{
					GestioneErroreSoapFault soap = new GestioneErroreSoapFault();
					soap.setComportamento(GestioneErroreComportamento.toEnumConstant(rs1.getString("comportamento")));
					soap.setCadenzaRispedizione(rs1.getString("cadenza_rispedizione"));
					soap.setFaultActor(rs1.getString("fault_actor"));
					soap.setFaultCode(rs1.getString("fault_code"));
					soap.setFaultString(rs1.getString("fault_string"));
					gestioneErrore.addSoapFault(soap);
				}
				rs1.close();
				stm1.close();

			} else {
				throw new DriverConfigurazioneNotFound("Gestione errore con id["+idGestioneErrore+"] non presente");
			}
			rs.close();
			stm.close();

			return gestioneErrore;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException(" SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException(" Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs1!=null) rs1.close();
				if(stm1!=null) stm1.close();
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	public static long getIdGestioneErrore(String nomeGestioneErrore,Connection con) throws DriverConfigurazioneException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idGestioneErrore=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.GESTIONE_ERRORE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, nomeGestioneErrore);
			rs=stm.executeQuery();

			if(rs.next()){
				idGestioneErrore = rs.getLong("id");
			}

			return idGestioneErrore;

		}catch (SQLException e) {
			throw new DriverConfigurazioneException(e);
		}catch (Exception e) {
			throw new DriverConfigurazioneException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	public static long CRUDGestioneErroreServizioApplicativo(int type, org.openspcoop2.core.config.GestioneErrore gestioneErrore, 
			long idSoggettoProprietario,long idServizioApplicativo,boolean invocazioneServizio,
			Connection con)throws DriverConfigurazioneException{
		if(invocazioneServizio)
			return DriverConfigurazioneDB_LIB.CRUDGestioneErrore(type, gestioneErrore, idSoggettoProprietario, idServizioApplicativo,1,con);
		else
			return DriverConfigurazioneDB_LIB.CRUDGestioneErrore(type, gestioneErrore, idSoggettoProprietario, idServizioApplicativo,2,con);
	}
	public static long CRUDGestioneErroreComponenteCooperazione(int type, org.openspcoop2.core.config.GestioneErrore gestioneErrore, 
			Connection con) throws DriverConfigurazioneException{
		return DriverConfigurazioneDB_LIB.CRUDGestioneErrore(type, gestioneErrore, -1,-1,3,con) ;
	}
	public static long CRUDGestioneErroreComponenteIntegrazione(int type, org.openspcoop2.core.config.GestioneErrore gestioneErrore, 
			Connection con) throws DriverConfigurazioneException{
		return DriverConfigurazioneDB_LIB.CRUDGestioneErrore(type, gestioneErrore,-1,-1,4,con) ;
	}
	
	private static long CRUDGestioneErrore(int type, org.openspcoop2.core.config.GestioneErrore gestioneErrore, 
			long idSoggettoProprietario,long idServizioApplicativo,
			int tipoCRUD,Connection con) throws DriverConfigurazioneException {

		if (gestioneErrore == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGestioneErrore] Parametro non valido.");
		
		String nomeGestioneErrore = null; // Costruito obbligatoriamente dal driver
		if(tipoCRUD==1 || tipoCRUD==2){
			// crud servizioApplicativo invocazioneServizio(1) o rispostaAsincrona(2)
			if(idSoggettoProprietario<=0){
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGestioneErrore] Soggetto proprietario non fornito.");
			}
			if(idServizioApplicativo<=0){
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGestioneErrore] Servizio applicativo non fornito.");
			}
			nomeGestioneErrore = "Soggetto("+idSoggettoProprietario+")_SA("+idServizioApplicativo+")_";
			if(tipoCRUD==1)
				nomeGestioneErrore = nomeGestioneErrore + "INV";
			else
				nomeGestioneErrore = nomeGestioneErrore + "RISP";
		}else if(tipoCRUD==3){
			// componente cooperazione
			nomeGestioneErrore = "componenteCooperazioneGestioneErroreDefaultPdD";
		}else if(tipoCRUD==4){
			// componente integrazione
			nomeGestioneErrore = "componenteIntegrazioneGestioneErroreDefaultPdD";
		}

		// updateNome
		gestioneErrore.setNome(nomeGestioneErrore);

		// Type
		int tipoOperazione = type;
		// Recupero id gestione errore se presente
		long idGestioneErroreChange = -1;
		if (type == CostantiDB.UPDATE){
			try{
				idGestioneErroreChange = DriverConfigurazioneDB_LIB.getIdGestioneErrore(nomeGestioneErrore, con);
			}catch (Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
			if(idGestioneErroreChange<=0){
				tipoOperazione = CostantiDB.CREATE;
			}
		}
		
		
		PreparedStatement updateStmt = null;
		PreparedStatement selectStmt = null;
		String updateQuery = "";
		String selectQuery = "";
		ResultSet selectRS = null;
		int n = 0;
		try {

			// preparo lo statement in base al tipo di operazione
			switch (tipoOperazione) {
			case CREATE:
				// CREATE
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.GESTIONE_ERRORE);
				sqlQueryObject.addInsertField("comportamento_default", "?");
				sqlQueryObject.addInsertField("cadenza_rispedizione", "?");
				sqlQueryObject.addInsertField("nome", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, DriverConfigurazioneDB_LIB.getValue(gestioneErrore.getComportamento()));
				updateStmt.setString(2, gestioneErrore.getCadenzaRispedizione());
				updateStmt.setString(3, gestioneErrore.getNome());
				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();

				DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
						gestioneErrore.getComportamento(),gestioneErrore.getCadenzaRispedizione(),gestioneErrore.getNome()));
				DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore type = " + type + " row affected =" + n);

				// Recupero id
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GESTIONE_ERRORE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("nome = ?");
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, gestioneErrore.getNome());
				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {
					gestioneErrore.setId(selectRS.getLong("id"));
				}else{
					throw new Exception("id gestione errore non trovato dopo inserimento con nome ["+gestioneErrore.getNome()+"]");
				}
				selectRS.close();
				selectStmt.close();
				
				// Insert gestione errore trasporto
				for(int i=0; i<gestioneErrore.sizeCodiceTrasportoList(); i++){
					GestioneErroreCodiceTrasporto tr = gestioneErrore.getCodiceTrasporto(i);
					
					int valoreMassimo = -1;
					int valoreMinimo = -1;
					if(tr.getValoreMassimo()!=null){
						valoreMassimo = tr.getValoreMassimo().intValue();
					}
					if(tr.getValoreMinimo()!=null){
						valoreMinimo = tr.getValoreMinimo().intValue();
					}
					
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
					sqlQueryObject.addInsertField("id_gestione_errore", "?");
					if(valoreMassimo>=0)
						sqlQueryObject.addInsertField("valore_massimo", "?");
					if(valoreMinimo>=0)
						sqlQueryObject.addInsertField("valore_minimo", "?");
					sqlQueryObject.addInsertField("comportamento", "?");
					sqlQueryObject.addInsertField("cadenza_rispedizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);

					int index = 1;
					updateStmt.setLong(index, gestioneErrore.getId()); index++;
					if(valoreMassimo>=0){
						updateStmt.setInt(index, valoreMassimo); index++;
					}
					if(valoreMinimo>=0){
						updateStmt.setInt(index, valoreMinimo); index++;
					}
					updateStmt.setString(index, DriverConfigurazioneDB_LIB.getValue(tr.getComportamento())); index++;
					updateStmt.setString(index, tr.getCadenzaRispedizione()); index++;

					// eseguo lo statement
					n = updateStmt.executeUpdate();
					updateStmt.close();
					
					if(valoreMassimo>=0 && valoreMinimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMassimo,valoreMinimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else if(valoreMassimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMassimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else if(valoreMinimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMinimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else{
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),tr.getComportamento(),tr.getCadenzaRispedizione()));
					}
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") type = " + type + " row affected =" + n);
				}
				
				// Insert gestione errore SOAP FAULT
				for(int i=0; i<gestioneErrore.sizeSoapFaultList(); i++){
					GestioneErroreSoapFault sf = gestioneErrore.getSoapFault(i);
										
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.GESTIONE_ERRORE_SOAP);
					sqlQueryObject.addInsertField("id_gestione_errore", "?");
					sqlQueryObject.addInsertField("fault_actor", "?");
					sqlQueryObject.addInsertField("fault_code", "?");
					sqlQueryObject.addInsertField("fault_string", "?");
					sqlQueryObject.addInsertField("comportamento", "?");
					sqlQueryObject.addInsertField("cadenza_rispedizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);

					updateStmt.setLong(1, gestioneErrore.getId()); 
					updateStmt.setString(2, sf.getFaultActor());
					updateStmt.setString(3,sf.getFaultCode());
					updateStmt.setString(4,sf.getFaultString());
					updateStmt.setString(5,DriverConfigurazioneDB_LIB.getValue(sf.getComportamento()));
					updateStmt.setString(6, sf.getCadenzaRispedizione());

					// eseguo lo statement
					n = updateStmt.executeUpdate();
					updateStmt.close();
					
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_SoapFault("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),sf.getFaultActor(),sf.getFaultCode(),sf.getFaultString(),sf.getComportamento(),sf.getCadenzaRispedizione()));
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_SoapFault("+i+") type = " + type + " row affected =" + n);
				}
				
				break;

			case UPDATE:
			
				// UPDATE (ci entro solo se prima ho trovato un gestore errore inserito (idGestioneErroreChange) )
					
				// Set idGestionErrore
				gestioneErrore.setId(idGestioneErroreChange);
				
				// Update gestion errore
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.GESTIONE_ERRORE);
				sqlQueryObject.addUpdateField("comportamento_default", "?");
				sqlQueryObject.addUpdateField("cadenza_rispedizione", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				
				updateStmt.setString(1, DriverConfigurazioneDB_LIB.getValue(gestioneErrore.getComportamento()));
				updateStmt.setString(2, gestioneErrore.getCadenzaRispedizione());
				updateStmt.setLong(3, idGestioneErroreChange);
				n = updateStmt.executeUpdate();
				updateStmt.close();
			
				DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore UPDATE : \n" + DBUtils.formatSQLString(updateQuery, 
						gestioneErrore.getComportamento(),gestioneErrore.getCadenzaRispedizione(),idGestioneErroreChange));
				DriverConfigurazioneDB_LIB.log.debug("CRUGestioneErrore type = " + type + " row affected =" + n);
				
				// Delete vecchie gestione errore trasporto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
				sqlQueryObject.addWhereCondition("id_gestione_errore=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idGestioneErroreChange);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore (Delete old trasporto) UPDATE : \n" + DBUtils.formatSQLString(updateQuery, 
						idGestioneErroreChange));
				DriverConfigurazioneDB_LIB.log.debug("CRUGestioneErrore (Delete old trasporto) type = " + type + " row affected =" + n);
				
				// Delete vecchie gestione errore soap fault
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE_SOAP);
				sqlQueryObject.addWhereCondition("id_gestione_errore=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idGestioneErroreChange);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore (Delete old soap fault) UPDATE : \n" + DBUtils.formatSQLString(updateQuery, 
						idGestioneErroreChange));
				DriverConfigurazioneDB_LIB.log.debug("CRUGestioneErrore (Delete old soap fault) type = " + type + " row affected =" + n);
				
				//  Insert gestione errore trasporto
				for(int i=0; i<gestioneErrore.sizeCodiceTrasportoList(); i++){
					GestioneErroreCodiceTrasporto tr = gestioneErrore.getCodiceTrasporto(i);
					
					int valoreMassimo = -1;
					int valoreMinimo = -1;
					if(tr.getValoreMassimo()!=null){
						valoreMassimo = tr.getValoreMassimo().intValue();
					}
					if(tr.getValoreMinimo()!=null){
						valoreMinimo = tr.getValoreMinimo().intValue();
					}
					
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
					sqlQueryObject.addInsertField("id_gestione_errore", "?");
					if(valoreMassimo>=0)
						sqlQueryObject.addInsertField("valore_massimo", "?");
					if(valoreMinimo>=0)
						sqlQueryObject.addInsertField("valore_minimo", "?");
					sqlQueryObject.addInsertField("comportamento", "?");
					sqlQueryObject.addInsertField("cadenza_rispedizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);

					int index = 1;
					updateStmt.setLong(index, gestioneErrore.getId()); index++;
					if(valoreMassimo>=0){
						updateStmt.setInt(index, valoreMassimo); index++;
					}
					if(valoreMinimo>=0){
						updateStmt.setInt(index, valoreMinimo); index++;
					}
					updateStmt.setString(index, DriverConfigurazioneDB_LIB.getValue(tr.getComportamento())); index++;
					updateStmt.setString(index, tr.getCadenzaRispedizione()); index++;

					// eseguo lo statement
					n = updateStmt.executeUpdate();
					updateStmt.close();
					
					if(valoreMassimo>=0 && valoreMinimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMassimo,valoreMinimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else if(valoreMassimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMassimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else if(valoreMinimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMinimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else{
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),tr.getComportamento(),tr.getCadenzaRispedizione()));
					}
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") type = " + type + " row affected =" + n);
				}
				
				// Insert gestione errore SOAP FAULT
				for(int i=0; i<gestioneErrore.sizeSoapFaultList(); i++){
					GestioneErroreSoapFault sf = gestioneErrore.getSoapFault(i);
										
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.GESTIONE_ERRORE_SOAP);
					sqlQueryObject.addInsertField("id_gestione_errore", "?");
					sqlQueryObject.addInsertField("fault_actor", "?");
					sqlQueryObject.addInsertField("fault_code", "?");
					sqlQueryObject.addInsertField("fault_string", "?");
					sqlQueryObject.addInsertField("comportamento", "?");
					sqlQueryObject.addInsertField("cadenza_rispedizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);

					updateStmt.setLong(1, gestioneErrore.getId()); 
					updateStmt.setString(2, sf.getFaultActor());
					updateStmt.setString(3,sf.getFaultCode());
					updateStmt.setString(4,sf.getFaultString());
					updateStmt.setString(5,DriverConfigurazioneDB_LIB.getValue(sf.getComportamento()));
					updateStmt.setString(6, sf.getCadenzaRispedizione());

					// eseguo lo statement
					n = updateStmt.executeUpdate();
					updateStmt.close();
					
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_SoapFault("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),sf.getFaultActor(),sf.getFaultCode(),sf.getFaultString(),sf.getComportamento(),sf.getCadenzaRispedizione()));
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_SoapFault("+i+") type = " + type + " row affected =" + n);
				}
				
				break;

			case DELETE:
				// DELETE
				
				long idGestioneErrore = DriverConfigurazioneDB_LIB.getIdGestioneErrore(nomeGestioneErrore, con);
				
				gestioneErrore.setId(idGestioneErrore);
				
				if(idGestioneErrore>0){
					
					// Delete gestione errore trasporto
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
					sqlQueryObject.addWhereCondition("id_gestione_errore=?");
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idGestioneErrore);
					n = updateStmt.executeUpdate();
					updateStmt.close();
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore (Delete trasporto) UPDATE : \n" + DBUtils.formatSQLString(updateQuery, 
							idGestioneErrore));
					DriverConfigurazioneDB_LIB.log.debug("CRUGestioneErrore (Delete trasporto) type = " + type + " row affected =" + n);
					
					// Delete gestione errore soap fault
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE_SOAP);
					sqlQueryObject.addWhereCondition("id_gestione_errore=?");
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idGestioneErrore);
					n = updateStmt.executeUpdate();
					updateStmt.close();
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore (Delete soap fault) UPDATE : \n" + DBUtils.formatSQLString(updateQuery, 
							idGestioneErrore));
					DriverConfigurazioneDB_LIB.log.debug("CRUGestioneErrore (Delete soap fault) type = " + type + " row affected =" + n);

					// Delete gestione errore
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE);
					sqlQueryObject.addWhereCondition("id=?");
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idGestioneErrore);
					n = updateStmt.executeUpdate();
					updateStmt.close();
	
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore type = " + type + " row affected =" + n);
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore DELETE : \n" + DBUtils.formatSQLString(updateQuery, 
							idGestioneErrore));
					
				}
				break;
			}


			return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGestioneErrore] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGestioneErrore] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public static long getIdServizioApplicativo(String nomeServizioApplicativo, String tipoProprietario,String nomeProprietario,
			Connection con, String tipoDB) throws CoreException
	{
		return DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeServizioApplicativo, tipoProprietario, nomeProprietario, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdServizioApplicativo(String nomeServizioApplicativo, String tipoProprietario,String nomeProprietario,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto;
		long idServizioApplicativo=-1;

		try
		{
			idSoggetto = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, tipoDB,tabellaSoggetti);

			//recupero l'id della porta applicativa appena inserita
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			stm.setString(2, nomeServizioApplicativo);

			rs=stm.executeQuery();

			if(rs.next())
			{
				idServizioApplicativo=rs.getLong("id");
			}
			return idServizioApplicativo;
		}catch (SQLException e) {

			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	
	
	
	public static void CRUDProtocolProperty(int type, List<ProtocolProperty> listPP, long idProprietario,
			org.openspcoop2.core.mapping.ProprietariProtocolProperty tipologiaProprietarioProtocolProperty, Connection connection,
			String tipoDatabase) throws DriverConfigurazioneException {
		try {
			DBProtocolPropertiesUtils.CRUDConfigProtocolProperty(log, type, listPP, idProprietario, tipologiaProprietarioProtocolProperty, connection, tipoDatabase);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	
	public static List<ProtocolProperty> getListaProtocolProperty(long idProprietario, org.openspcoop2.core.mapping.ProprietariProtocolProperty tipologiaProprietario, 
			Connection connection,
			String tipoDatabase) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		try {
			return DBProtocolPropertiesUtils.getListaProtocolPropertyConfig(idProprietario,tipologiaProprietario,connection,tipoDatabase);
		}
		catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}
		catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public static ProtocolProperty getProtocolProperty(long id, Connection connection, String tipoDatabase) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		try {
			return DBProtocolPropertiesUtils.getProtocolPropertyConfig(id, connection, tipoDatabase);
		}
		catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}
		catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
}
