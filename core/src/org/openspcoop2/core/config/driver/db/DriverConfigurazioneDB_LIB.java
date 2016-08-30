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


package org.openspcoop2.core.config.driver.db;


import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;
import org.openspcoop2.core.config.GestioneErroreSoapFault;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ProprietaProtocollo;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDefault;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.StatoServiziPddIntegrationManager;
import org.openspcoop2.core.config.StatoServiziPddPortaApplicativa;
import org.openspcoop2.core.config.StatoServiziPddPortaDelegata;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
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
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataServizioIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;
import org.openspcoop2.core.config.constants.ProprietaProtocolloValore;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoConnessioneRisposte;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.constants.ValidazioneBusteTipoControllo;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.config.driver.ConnettorePropertiesUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

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
				sqlQueryObject.addInsertField("superuser", "?");
				sqlQueryObject.addInsertField("pd_url_prefix_rewriter", "?");
				sqlQueryObject.addInsertField("pa_url_prefix_rewriter", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nome);
				updateStmt.setString(2, descrizione);
				updateStmt.setString(3, identificativoPorta);
				updateStmt.setString(4, tipo);
				updateStmt.setInt(5, (router ? CostantiDB.TRUE : CostantiDB.FALSE));
				updateStmt.setString(6, superuser);
				updateStmt.setString(7, pdUrlPrefixRewriter);
				updateStmt.setString(8, paUrlPrefixRewriter);
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
				String oldNomeSoggetto = soggetto.getOldNomeForUpdate();
				String oldTipoSoggetto = soggetto.getOldTipoForUpdate();
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
				sqlQueryObject.addUpdateField("superuser", "?");
				sqlQueryObject.addUpdateField("pd_url_prefix_rewriter", "?");
				sqlQueryObject.addUpdateField("pa_url_prefix_rewriter", "?");
				sqlQueryObject.addWhereCondition("nome_soggetto=?");
				sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nome);
				updateStmt.setString(2, descrizione);
				updateStmt.setString(3, identificativoPorta);
				updateStmt.setString(4, tipo);
				updateStmt.setInt(5, (router ? CostantiDB.TRUE : CostantiDB.FALSE));
				updateStmt.setString(6, superuser);
				updateStmt.setString(7, pdUrlPrefixRewriter);
				updateStmt.setString(8, paUrlPrefixRewriter);
				updateStmt.setString(9, oldNomeSoggetto);
				updateStmt.setString(10, oldTipoSoggetto);
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

		boolean isAbilitato = false;

		Hashtable<String, String> extendedProperties = new Hashtable<String, String>();
		
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
				sqlQueryObject.addInsertField("custom", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = connection.prepareStatement(sqlQuery);

				stm.setString(1, endpointtype);
				stm.setString(2, (isAbilitato ? url : null));
				stm.setString(3, isAbilitato ? nome : null);
				stm.setString(4, isAbilitato ? tipo : null);
				stm.setString(5, (isAbilitato ? utente : null));
				stm.setString(6, (isAbilitato ? password : null));
				stm.setString(7, (isAbilitato ? initcont : null));
				stm.setString(8, (isAbilitato ? urlpkg : null));
				stm.setString(9, (isAbilitato ? provurl : null));
				stm.setString(10, (isAbilitato ? connectionfactory : null));
				stm.setString(11, (isAbilitato ? sendas : null));
				stm.setString(12, nomeConnettore);
				if(debug){
					stm.setInt(13, 1);
				}else{
					stm.setInt(13, 0);
				}
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(14, 1);
				}else{
					stm.setInt(14, 0);
				}

				DriverConfigurazioneDB_LIB.log.debug("CRUDConnettore CREATE : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, url, nome, tipo, utente, password, initcont, urlpkg, provurl, connectionfactory, sendas, nomeConnettore,debug,(connettore.getCustom()!=null && connettore.getCustom())));

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
				sqlQueryObject.addUpdateField("custom", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = connection.prepareStatement(sqlQuery);

				stm.setString(1, endpointtype);
				stm.setString(2, url);
				stm.setString(3, nome);
				stm.setString(4, tipo);
				stm.setString(5, utente);
				stm.setString(6, password);
				stm.setString(7, initcont);
				stm.setString(8, urlpkg);
				stm.setString(9, provurl);
				stm.setString(10, connectionfactory);
				stm.setString(11, sendas);
				stm.setString(12, nomeConnettore);
				if(debug){
					stm.setInt(13, 1);
				}else{
					stm.setInt(13, 0);
				}
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(14, 1);
				}else{
					stm.setInt(14, 0);
				}
				stm.setLong(15, idConnettore);

				stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("CRUDConnettore UPDATE : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, url, nome, tipo, utente, password, initcont, urlpkg, provurl, connectionfactory, sendas,nomeConnettore, debug,(connettore.getCustom()!=null && connettore.getCustom()),idConnettore));

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
		String autorizzazioneContenuto = aPD.getAutorizzazioneContenuto();
		String descrizione = aPD.getDescrizione();

		PortaDelegataAzione azione = aPD.getAzione();
		long idAzione = ((azione != null && azione.getId() != null) ? azione.getId() : -1);

		long idSoggettoProprietario;
		try {
			idSoggettoProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
		} catch (Exception e1) {
			throw new DriverConfigurazioneException(e1);
		}
		long idPortaDelegata = -1;

		String location = aPD.getLocation();
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
		if(messageSecurity!=null){
			if(messageSecurity.getRequestFlow()!=null){
				messageSecurityApplyMtom_request = messageSecurity.getRequestFlow().getApplyToMtom();
			}
			if(messageSecurity.getResponseFlow()!=null){
				messageSecurityApplyMtom_response = messageSecurity.getResponseFlow().getApplyToMtom();
			}
		}
		
		CorrelazioneApplicativa corrApp = aPD.getCorrelazioneApplicativa();
		CorrelazioneApplicativaRisposta corrAppRisposta = aPD.getCorrelazioneApplicativaRisposta();
		
		ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
		IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoPortaDelegata();
		
		ServizioApplicativo servizioApplicativo = null;
		try {
			int n = 0;
			int i = 0;
			switch (type) {
			case CREATE:

				//soggetto erogatore
				String tipoSoggErogatore = (soggErogatore != null ? soggErogatore.getTipo() : null);
				String nomeSoggErogatore = (soggErogatore != null ? soggErogatore.getNome() : null);
				String patternErogatore  = (soggErogatore != null ? soggErogatore.getPattern() : null);
				PortaDelegataSoggettoErogatoreIdentificazione modeSoggettoErogatore = (soggErogatore != null ? soggErogatore.getIdentificazione() : null);
				if(modeSoggettoErogatore==null || modeSoggettoErogatore.equals("")) 
					modeSoggettoErogatore=PortaDelegataSoggettoErogatoreIdentificazione.STATIC;
				//controllo parametri necessari in base alla modalita

				switch (modeSoggettoErogatore) {
				case CONTENT_BASED:
				case URL_BASED:
					if(tipoSoggErogatore==null || tipoSoggErogatore.equals("")) throw new DriverConfigurazioneException("Tipo Soggetto Erogatore non impostato.");
					if(patternErogatore==null || patternErogatore.equals("")) throw new DriverConfigurazioneException("Pattern Soggetto Erogatore non impostato.");
					nomeSoggErogatore=null;
					break;
				case INPUT_BASED:
					if(tipoSoggErogatore==null || tipoSoggErogatore.equals("")) throw new DriverConfigurazioneException("Tipo Soggetto Erogatore non impostato.");
					break;
				case STATIC:
					//se non c'e' l'id del soggetto allora ci devono essere il tipo e il nome
					if(idSoggettoErogatore<=0){
						if(tipoSoggErogatore==null || tipoSoggErogatore.equals("")) throw new DriverConfigurazioneException("Tipo Soggetto Erogatore non impostato.");
						if(nomeSoggErogatore==null || nomeSoggErogatore.equals("")) throw new DriverConfigurazioneException("Nome Soggetto Erogatore non impostato.");
					}
					patternErogatore=null;
					break;
				default:
					break;
				}

				//servizio
				String tipoServizio = (servizio != null ? servizio.getTipo() : null);
				String nomeServizio = (servizio != null ? servizio.getNome() : null);
				String patternServizio = (servizio != null ? servizio.getPattern() : null);
				PortaDelegataServizioIdentificazione modeServizio = (servizio != null ? servizio.getIdentificazione() : null);
				if(modeServizio==null || modeServizio.equals("")) 
					modeServizio=PortaDelegataServizioIdentificazione.STATIC;
				//campi obbligatori
				switch (modeServizio) {
				case CONTENT_BASED:
				case URL_BASED:
					if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
					if(patternServizio==null || patternServizio.equals("")) throw new DriverConfigurazioneException("Pattern Servizio non impostato.");
					nomeServizio=null;
					break;
				case INPUT_BASED:
					if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
					break;
				case STATIC:
					//se non c'e' l'id del servizio allora ci devono essere il tipo e il nome
					if(idServizioPD<=0){
						if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
						if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
					}
					patternServizio=null;

					break;
				default:
					break;
				}

				//Azione
				String nomeAzione = (azione != null ? azione.getNome() : null);
				String patternAzione = (azione != null ? azione.getPattern() : null);
				StatoFunzionalita forceWsdlBased = (azione != null ? azione.getForceWsdlBased() : null);
				PortaDelegataAzioneIdentificazione modeAzione = (azione != null ? azione.getIdentificazione() : null);
				//Se il bean Azione nn e' presente allora non controllo nulla
				if(azione!=null){
					if(modeAzione==null || modeAzione.equals("")) 
						modeAzione = PortaDelegataAzioneIdentificazione.STATIC;
					switch (modeAzione) {
					case CONTENT_BASED:
					case URL_BASED:
						if(patternAzione==null || patternAzione.equals("")) throw new DriverConfigurazioneException("Pattern Azione non impostato.");
						nomeAzione=null;
						break;
					case INPUT_BASED:
					case SOAP_ACTION_BASED:
					case WSDL_BASED:
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
				sqlQueryObject.addInsertField("location", "?");
				sqlQueryObject.addInsertField("id_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("tipo_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("nome_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("mode_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("id_servizio", "?");
				sqlQueryObject.addInsertField("tipo_servizio", "?");
				sqlQueryObject.addInsertField("nome_servizio", "?");
				sqlQueryObject.addInsertField("mode_servizio", "?");
				sqlQueryObject.addInsertField("id_azione", "?");
				sqlQueryObject.addInsertField("nome_azione", "?");
				sqlQueryObject.addInsertField("mode_azione", "?");
				sqlQueryObject.addInsertField("force_wsdl_based_azione", "?");
				sqlQueryObject.addInsertField("autenticazione", "?");
				sqlQueryObject.addInsertField("autorizzazione", "?");
				sqlQueryObject.addInsertField("autorizzazione_contenuto", "?");
				sqlQueryObject.addInsertField("mtom_request_mode", "?");
				sqlQueryObject.addInsertField("mtom_response_mode", "?");
				sqlQueryObject.addInsertField("ws_security", "?");
				sqlQueryObject.addInsertField("ws_security_mtom_req", "?");
				sqlQueryObject.addInsertField("ws_security_mtom_res", "?");
				sqlQueryObject.addInsertField("id_soggetto", "?");
				sqlQueryObject.addInsertField("ricevuta_asincrona_sim", "?");
				sqlQueryObject.addInsertField("ricevuta_asincrona_asim", "?");
				sqlQueryObject.addInsertField("integrazione", "?");
				sqlQueryObject.addInsertField("pattern_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("pattern_servizio", "?");
				sqlQueryObject.addInsertField("pattern_azione", "?");
				sqlQueryObject.addInsertField("scadenza_correlazione_appl", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_stato", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_mtom", "?");
				sqlQueryObject.addInsertField("allega_body", "?");
				sqlQueryObject.addInsertField("scarta_body", "?");
				sqlQueryObject.addInsertField("gestione_manifest", "?");
				sqlQueryObject.addInsertField("stateless", "?");
				sqlQueryObject.addInsertField("local_forward", "?");
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("id_port_type", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				int index = 1;
				stm.setString(index++, nomePorta);
				stm.setString(index++, descrizione);
				stm.setString(index++, location);
				stm.setLong(index++, idSoggettoErogatore);				
				stm.setString(index++, tipoSoggErogatore);
				stm.setString(index++, nomeSoggErogatore);
				stm.setString(index++, modeSoggettoErogatore.toString());
				stm.setLong(index++, idServizioPD);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setString(index++, modeServizio.toString());
				stm.setLong(index++, idAzione);
				stm.setString(index++, nomeAzione);
				if(modeAzione!=null){
					stm.setString(index++, modeAzione.toString());
				}
				else{
					stm.setString(index++, null);
				}
				stm.setString(index++, getValue(forceWsdlBased));
				stm.setString(index++, autenticazione);
				stm.setString(index++, autorizzazione);
				stm.setString(index++, autorizzazioneContenuto);
				// mtom
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_response));
				// proprietario
				stm.setLong(index++, idSoggettoProprietario);
				//ricevuta asincrona_asimmetrica/simmetrica
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaSimmetrica()));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaAsimmetrica()));				
				//integrazione
				stm.setString(index++, aPD.getIntegrazione());
				//pattern
				stm.setString(index++, patternErogatore);
				stm.setString(index++, patternServizio);
				stm.setString(index++, patternAzione);
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
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getLocalForward()) : null);
				
				//idaccordo
				stm.setLong(index++, aPD.getIdAccordo()!=null ? aPD.getIdAccordo() : -1L);
				stm.setLong(index++, aPD.getIdPortType() !=null ? aPD.getIdPortType() : -1L);
				
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + 
						DBUtils.formatSQLString(sqlQuery, nomePorta, descrizione, location, 
								idSoggettoErogatore, tipoSoggErogatore, nomeSoggErogatore, modeSoggettoErogatore, 
								idServizioPD, tipoServizio, nomeServizio, modeServizio, 
								idAzione, nomeAzione, modeAzione, autenticazione, autorizzazione, autorizzazioneContenuto,
								mtomMode_request, mtomMode_response,
								messageSecurityStatus, messageSecurityApplyMtom_request, messageSecurityApplyMtom_response,
								idSoggettoProprietario,
								aPD.getRicevutaAsincronaSimmetrica(),aPD.getRicevutaAsincronaAsimmetrica(),aPD.getIntegrazione(),
								patternErogatore,patternServizio,patternAzione,(aPD.getCorrelazioneApplicativa()!=null ? aPD.getCorrelazioneApplicativa().getScadenza() : null),
								(aPD.getValidazioneContenutiApplicativi()!=null ? aPD.getValidazioneContenutiApplicativi().getStato() : null),
								(aPD.getValidazioneContenutiApplicativi()!=null ? aPD.getValidazioneContenutiApplicativi().getTipo() : null),
								(aPD.getValidazioneContenutiApplicativi()!=null ? aPD.getValidazioneContenutiApplicativi().getAcceptMtomMessage() : null),
								aPD.getAllegaBody(),aPD.getScartaBody(),aPD.getGestioneManifest(),aPD.getStateless(),aPD.getLocalForward(),aPD.getIdAccordo(),aPD.getIdPortType()));
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
				
				// se ws_security abilitato setto la lista
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
				servizioApplicativo = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);

				for (i = 0; i < aPD.sizeServizioApplicativoList(); i++) {
					servizioApplicativo = aPD.getServizioApplicativo(i);
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

				// extendedInfo
				i=0;
				if(aPD.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPD.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD, aPD.getExtendedInfo(i));
						}
					}
				}
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " associazioni ExtendedInfo<->PortaDelegata associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				break;

			case UPDATE:
				//soggetto erogatore
				tipoSoggErogatore = (soggErogatore != null ? soggErogatore.getTipo() : null);
				nomeSoggErogatore = (soggErogatore != null ? soggErogatore.getNome() : null);
				patternErogatore  = (soggErogatore != null ? soggErogatore.getPattern() : null);
				modeSoggettoErogatore = (soggErogatore != null ? soggErogatore.getIdentificazione() : null);
				if(modeSoggettoErogatore==null || modeSoggettoErogatore.equals("")) 
					modeSoggettoErogatore=PortaDelegataSoggettoErogatoreIdentificazione.STATIC;
				//controllo parametri necessari in base alla modalita

				switch (modeSoggettoErogatore) {
				case CONTENT_BASED:
				case URL_BASED:
					if(tipoSoggErogatore==null || tipoSoggErogatore.equals("")) throw new DriverConfigurazioneException("Tipo Soggetto Erogatore non impostato.");
					if(patternErogatore==null || patternErogatore.equals("")) throw new DriverConfigurazioneException("Pattern Soggetto Erogatore non impostato.");
					nomeSoggErogatore=null;
					break;
				case INPUT_BASED:
					if(tipoSoggErogatore==null || tipoSoggErogatore.equals("")) throw new DriverConfigurazioneException("Tipo Soggetto Erogatore non impostato.");
					break;
				case STATIC:
					//se non c'e' l'id del soggetto allora ci devono essere il tipo e il nome
					if(idSoggettoErogatore<=0){
						if(tipoSoggErogatore==null || tipoSoggErogatore.equals("")) throw new DriverConfigurazioneException("Tipo Soggetto Erogatore non impostato.");
						if(nomeSoggErogatore==null || nomeSoggErogatore.equals("")) throw new DriverConfigurazioneException("Nome Soggetto Erogatore non impostato.");
					}
					patternErogatore=null;
					break;
				default:
					break;
				}

				//servizio
				tipoServizio = (servizio != null ? servizio.getTipo() : null);
				nomeServizio = (servizio != null ? servizio.getNome() : null);
				patternServizio = (servizio != null ? servizio.getPattern() : null);
				modeServizio = (servizio != null ? servizio.getIdentificazione() : null);
				if(modeServizio==null || modeServizio.equals("")) 
					modeServizio=PortaDelegataServizioIdentificazione.STATIC;
				//campi obbligatori
				switch (modeServizio) {
				case CONTENT_BASED:
				case URL_BASED:
					if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
					if(patternServizio==null || patternServizio.equals("")) throw new DriverConfigurazioneException("Pattern Servizio non impostato.");
					nomeServizio=null;
					break;
				case INPUT_BASED:
					if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
					break;
				case STATIC:
					//se non c'e' l'id del servizio allora ci devono essere il tipo e il nome
					if(idServizioPD<=0){
						if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
						if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
					}
					patternServizio=null;
					break;
				default:
					break;
				}

				//Azione
				nomeAzione = (azione != null ? azione.getNome() : null);
				patternAzione = (azione != null ? azione.getPattern() : null);
				modeAzione = (azione != null ? azione.getIdentificazione() : null);
				forceWsdlBased = (azione != null ? azione.getForceWsdlBased() : null);
				//Se il bean Azione nn e' presente allora non controllo nulla
				if(azione!=null){
					if(modeAzione==null || modeAzione.equals("")) 
						modeAzione = PortaDelegataAzioneIdentificazione.STATIC;
					switch (modeAzione) {
					case CONTENT_BASED:
					case URL_BASED:
						if(patternAzione==null || patternAzione.equals("")) throw new DriverConfigurazioneException("Pattern Azione non impostato.");
						nomeAzione=null;
						break;
					case INPUT_BASED:
					case SOAP_ACTION_BASED:
					case WSDL_BASED:
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
				String oldNomePD = aPD.getOldNomeForUpdate();
				String oldNomeProprietario = aPD.getOldNomeSoggettoProprietarioForUpdate();
				String oldTipoProprietario = aPD.getOldTipoSoggettoProprietarioForUpdate();
				DriverConfigurazioneDB_LIB.log.debug("OLD-PD["+oldNomePD+"] PD["+nomePorta+"] OLD-TIPOS["+oldTipoProprietario+"] TIPOS["+tipoProprietario+
						"] OLD-NOMES["+oldNomeProprietario+"] NOMES["+nomeProprietario+"]");
				
				if (oldNomePD == null || oldNomePD.equals("")){
					DriverConfigurazioneDB_LIB.log.debug("old nomePD is null, assegno: "+nomePorta);
					oldNomePD = nomePorta;
				}
				if (oldNomeProprietario == null || oldNomeProprietario.equals("")){
					DriverConfigurazioneDB_LIB.log.debug("oldNomeProprietario is null, assegno: "+nomeProprietario);
					oldNomeProprietario = nomeProprietario;
				}
				if (oldTipoProprietario == null || oldTipoProprietario.equals("")){
					DriverConfigurazioneDB_LIB.log.debug("oldTipoProprietario is null, assegno: "+tipoProprietario);
					oldTipoProprietario = tipoProprietario;
				}

				// if(aPD.getId() == null || aPD.getId()<=0) throw new
				// DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(UPDATE)]
				// L'id della porta e' necessario per effettuare l'UPDATE.");
				idPortaDelegata = DriverConfigurazioneDB_LIB.getIdPortaDelegata(oldNomePD, oldTipoProprietario, oldNomeProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(idPortaDelegata<=0) {
					idPortaDelegata = DriverConfigurazioneDB_LIB.getIdPortaDelegata(oldNomePD, tipoProprietario, nomeProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				}
				if(idPortaDelegata<=0) 
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(UPDATE)] id porta delegata non trovato nome["+oldNomePD+"] tipoProprietario["
							+oldTipoProprietario+"] nomeProprietario["+oldNomeProprietario+"]");
				aPD.setId(idPortaDelegata);
				
				//id soggetto puo essere cambiato
				long oldIdSoggettoProprietario=DBUtils.getIdSoggetto(oldNomeProprietario, oldTipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(oldIdSoggettoProprietario<=0) {
					oldIdSoggettoProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				}
				if(oldIdSoggettoProprietario <=0) throw new DriverConfigurazioneException("Impossibile recuperare l'id del Soggetto Proprietario della Porta Delegata");
				
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addUpdateField("nome_porta", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("location", "?");
				sqlQueryObject.addUpdateField("id_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("tipo_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("nome_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("mode_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("id_servizio", "?");
				sqlQueryObject.addUpdateField("tipo_servizio", "?");
				sqlQueryObject.addUpdateField("nome_servizio", "?");
				sqlQueryObject.addUpdateField("mode_servizio", "?");
				sqlQueryObject.addUpdateField("id_azione", "?");
				sqlQueryObject.addUpdateField("nome_azione", "?");
				sqlQueryObject.addUpdateField("mode_azione", "?");
				sqlQueryObject.addUpdateField("force_wsdl_based_azione", "?");
				sqlQueryObject.addUpdateField("autenticazione", "?");
				sqlQueryObject.addUpdateField("autorizzazione", "?");
				sqlQueryObject.addUpdateField("autorizzazione_contenuto", "?");
				sqlQueryObject.addUpdateField("mtom_request_mode", "?");
				sqlQueryObject.addUpdateField("mtom_response_mode", "?");
				sqlQueryObject.addUpdateField("ws_security", "?");
				sqlQueryObject.addUpdateField("ws_security_mtom_req", "?");
				sqlQueryObject.addUpdateField("ws_security_mtom_res", "?");
				sqlQueryObject.addUpdateField("id_soggetto", "?");
				sqlQueryObject.addUpdateField("ricevuta_asincrona_sim", "?");
				sqlQueryObject.addUpdateField("ricevuta_asincrona_asim", "?");
				sqlQueryObject.addUpdateField("integrazione", "?");
				sqlQueryObject.addUpdateField("pattern_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("pattern_servizio", "?");
				sqlQueryObject.addUpdateField("pattern_azione", "?");
				sqlQueryObject.addUpdateField("scadenza_correlazione_appl", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_stato", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_mtom", "?");				
				sqlQueryObject.addUpdateField("allega_body", "?");
				sqlQueryObject.addUpdateField("scarta_body", "?");
				sqlQueryObject.addUpdateField("gestione_manifest", "?");
				sqlQueryObject.addUpdateField("stateless", "?");
				sqlQueryObject.addUpdateField("local_forward", "?");
				sqlQueryObject.addUpdateField("id_accordo", "?");
				sqlQueryObject.addUpdateField("id_port_type", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = con.prepareStatement(sqlQuery);

				index = 1;
				stm.setString(index++, nomePorta);
				stm.setString(index++, descrizione);
				stm.setString(index++, location);
				stm.setLong(index++, idSoggettoErogatore);
				stm.setString(index++, tipoSoggErogatore);
				stm.setString(index++, nomeSoggErogatore);
				stm.setString(index++, modeSoggettoErogatore.toString());
				stm.setLong(index++, idServizioPD);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setString(index++, modeServizio.toString());
				stm.setLong(index++, idAzione);
				stm.setString(index++, nomeAzione);
				if(modeAzione!=null)
					stm.setString(index++, modeAzione.toString());
				else
					stm.setString(index++, null);
				stm.setString(index++, getValue(forceWsdlBased));
				stm.setString(index++, autenticazione);
				stm.setString(index++, autorizzazione);
				stm.setString(index++, autorizzazioneContenuto);
				// mtom
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_response));
				// soggettoProprietario
				stm.setLong(index++, idSoggettoProprietario);
				//ricevuta asincrona_asimmetrica/simmetrica
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaSimmetrica()));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaAsimmetrica()));
				//integrazione
				stm.setString(index++, aPD.getIntegrazione());
				//pattern
				stm.setString(index++, patternErogatore);
				stm.setString(index++, patternServizio);
				stm.setString(index++, patternAzione);
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
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getLocalForward()) : null);
				//idAccordo
				stm.setLong(index++,aPD.getIdAccordo() != null ? aPD.getIdAccordo() : -1L);
				stm.setLong(index++, aPD.getIdPortType() != null ? aPD.getIdPortType() : -1L);
				
				// where
				stm.setLong(index++, idPortaDelegata);
				stm.setLong(index++, oldIdSoggettoProprietario);

				//DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, nomePorta, descrizione, location, soggErogatore.getId(), soggErogatore.getTipo(), soggErogatore.getNome(), soggErogatore.getIdentificazione(), servizio.getId(), servizio.getTipo(), servizio.getNome(), servizio.getIdentificazione(), azione.getId(), azione.getNome(), azione.getIdentificazione(), autenticazione, autorizzazione, messageSecurityStatus, idSoggettoProprietario, idPortaDelegata));

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
				
				
				
				// se ws_security abilitato setto la lista
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
					servizioApplicativo = aPD.getServizioApplicativo(i);

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

				
				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD);
				}
				
				i=0;
				if(aPD.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPD.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD, aPD.getExtendedInfo(i));
						}
					}
				}
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + i + " associazioni ExtendedInfo<->PortaDelegata associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				break;

			case DELETE:
				// delete
				idPortaDelegata = DriverConfigurazioneDB_LIB.getIdPortaDelegata(nomePorta, tipoProprietario, nomeProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				if (idPortaDelegata <= 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(DELETE)] Non e' stato possibile recuperare l'id della Porta Delegata, necessario per effettuare la DELETE.");

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
				servizioApplicativo = null;
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
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " ws_request flow associate alla PortaDelegata[" + idPortaDelegata + "]");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " ws_response flow associate alla PortaDelegata[" + idPortaDelegata + "]");

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
				
				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD);
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
			// String nome = aSA.getNome();
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
			Credenziali credenziali = null;
			InvocazionePortaGestioneErrore gestErr = null;
			String fault = null;

			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("sbustamentorisp", "?");
				sqlQueryObject.addInsertField("sbustamento_protocol_info_risp", "?");
				sqlQueryObject.addInsertField("getmsgrisp", "?");
				sqlQueryObject.addInsertField("tipoauthrisp", "?");
				sqlQueryObject.addInsertField("utenterisp", "?");
				sqlQueryObject.addInsertField("passwordrisp", "?");
				sqlQueryObject.addInsertField("subjectrisp", "?");
				sqlQueryObject.addInsertField("id_connettore_risp", "?");
				sqlQueryObject.addInsertField("sbustamentoinv", "?");
				sqlQueryObject.addInsertField("sbustamento_protocol_info_inv", "?");
				sqlQueryObject.addInsertField("getmsginv", "?");
				sqlQueryObject.addInsertField("tipoauthinv", "?");
				sqlQueryObject.addInsertField("utenteinv", "?");
				sqlQueryObject.addInsertField("passwordinv", "?");
				sqlQueryObject.addInsertField("subjectinv", "?");
				sqlQueryObject.addInsertField("id_connettore_inv", "?");
				sqlQueryObject.addInsertField("id_soggetto", "?");
				sqlQueryObject.addInsertField("fault", "?");
				sqlQueryObject.addInsertField("tipoauth", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				sqlQueryObject.addInsertField("subject", "?");
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
				connettoreRisp.setNome("ConnettoreRISP_" + aSA.getNome()+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario());
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
				connettoreInv.setNome("ConnettoreINV_" + aSA.getNome()+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario());
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
				stm.setString(index++, descrizione);

				// RicezioneRisposta
				stm.setInt(index++, (ricezione != null && (CostantiConfigurazione.ABILITATO.equals(ricezione.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (ricezione != null && (!CostantiConfigurazione.DISABILITATO.equals(ricezione.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getGetMessage()) : null);
				// setto credenziali risp
				credenziali = ricezione != null ? ricezione.getCredenziali() : null;
				stm.setString(index++, (ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getAutenticazione()) : null)); //l'autenticazione e' quella della risposta asincrona
				stm.setString(index++, (credenziali != null ? credenziali.getUser() : null));
				stm.setString(index++, (credenziali != null ? credenziali.getPassword() : null));
				stm.setString(index++, (credenziali != null ? credenziali.getSubject() : null));
				// setto idconnettore risp
				stm.setLong(index++, idConnettoreRisp);

				// InvocazioneServizio
				stm.setInt(index++, (invServizio != null && (CostantiConfigurazione.ABILITATO.equals(invServizio.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (invServizio != null && (!CostantiConfigurazione.DISABILITATO.equals(invServizio.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getGetMessage()) : null);
				// setto credenziali inv
				credenziali = invServizio != null ? invServizio.getCredenziali() : null;
				stm.setString(index++, (invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getAutenticazione()) : null));//l'autenticazione e' quella dell invocazione servizio
				stm.setString(index++, (credenziali != null ? credenziali.getUser() : null));
				stm.setString(index++, (credenziali != null ? credenziali.getPassword() : null));
				stm.setString(index++, (credenziali != null ? credenziali.getSubject() : null));
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
				credenziali = (invPorta != null && invPorta.sizeCredenzialiList() > 0 ? invPorta.getCredenziali(0) : null);
				stm.setString(index++, (credenziali != null ? DriverConfigurazioneDB_LIB.getValue(credenziali.getTipo()) : null));
				stm.setString(index++, (credenziali != null ? credenziali.getUser() : null));
				stm.setString(index++, (credenziali != null ? credenziali.getPassword() : null));
				String subject = null;
				if(credenziali!=null && credenziali.getSubject()!=null && !"".equals(credenziali.getSubject()))
					subject = credenziali.getSubject();
				stm.setString(index++, (subject != null ? Utilities.formatSubject(subject) : null));

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
				
				
				break;

			case UPDATE:
				String oldNomeSA = aSA.getOldNomeForUpdate();
				String oldNomeProprietario = aSA.getOldNomeSoggettoProprietarioForUpdate();
				String oldTipoProprietario = aSA.getOldTipoSoggettoProprietarioForUpdate();

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
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("sbustamentorisp", "?");
				sqlQueryObject.addUpdateField("sbustamento_protocol_info_risp", "?");
				sqlQueryObject.addUpdateField("getmsgrisp", "?");
				sqlQueryObject.addUpdateField("tipoauthrisp", "?");
				sqlQueryObject.addUpdateField("utenterisp", "?");
				sqlQueryObject.addUpdateField("passwordrisp", "?");
				sqlQueryObject.addUpdateField("subjectrisp", "?");
				sqlQueryObject.addUpdateField("id_connettore_risp", "?");
				sqlQueryObject.addUpdateField("sbustamentoinv", "?");
				sqlQueryObject.addUpdateField("sbustamento_protocol_info_inv", "?");
				sqlQueryObject.addUpdateField("getmsginv", "?");
				sqlQueryObject.addUpdateField("tipoauthinv", "?");
				sqlQueryObject.addUpdateField("utenteinv", "?");
				sqlQueryObject.addUpdateField("passwordinv", "?");
				sqlQueryObject.addUpdateField("subjectinv", "?");
				sqlQueryObject.addUpdateField("id_connettore_inv", "?");
				sqlQueryObject.addUpdateField("fault", "?");
				sqlQueryObject.addUpdateField("tipoauth", "?");
				sqlQueryObject.addUpdateField("utente", "?");
				sqlQueryObject.addUpdateField("password", "?");
				sqlQueryObject.addUpdateField("subject", "?");
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
				//String nomeConnettoreRisp = "ConnettoreRISP_"+oldNomeSA+oldTipoProprietario+oldNomeProprietario;
				String newNomeConnettoreRisp = "ConnettoreRISP_"+nomeSA+tipoProprietario+nomeProprietario;
				//idConnettoreRisp = DBUtils.getIdConnettore(nomeConnettoreRisp, con, tipoDB);
				idConnettoreRisp = DriverConfigurazioneDB_LIB.getIdConnettore_SA_RISP(idServizioApplicativo, con);
				
				// connettore inv servizio
				InvocazioneServizio invServ = aSA.getInvocazioneServizio();
				connettoreInv = invServ != null ? invServizio.getConnettore() : new Connettore();
				//String nomeConnettoreInv = "ConnettoreINV_"+oldNomeSA+oldTipoProprietario+oldNomeProprietario;
				String newNomeConnettoreInv = "ConnettoreINV_"+nomeSA+tipoProprietario+nomeProprietario;
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
				
				stm.setString(index++, descrizione);

				// RicezioneRisposta
				stm.setInt(index++, (ricezione != null && (CostantiConfigurazione.ABILITATO.equals(ricezione.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (ricezione != null && (!CostantiConfigurazione.DISABILITATO.equals(ricezione.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDB_LIB.getValue(ricezione.getGetMessage()) : null);
				// setto credenziali risp
				credenziali = ricezione != null ? ricezione.getCredenziali() : null;
				stm.setString(index++, (credenziali != null ? DriverConfigurazioneDB_LIB.getValue(credenziali.getTipo()) : null));
				stm.setString(index++, (credenziali != null ? credenziali.getUser() : null));
				stm.setString(index++, (credenziali != null ? credenziali.getPassword() : null));
				stm.setString(index++, (credenziali != null ? credenziali.getSubject() : null));
				// setto idconnettore risp
				stm.setLong(index++, idConnettoreRisp);

				// InvocazioneServizio
				stm.setInt(index++, (invServizio != null && (CostantiConfigurazione.ABILITATO.equals(invServizio.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (invServizio != null && (!CostantiConfigurazione.DISABILITATO.equals(invServizio.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDB_LIB.getValue(invServizio.getGetMessage()) : null);
				// setto credenziali inv
				credenziali = invServizio != null ? invServizio.getCredenziali() : null;
				stm.setString(index++, (credenziali != null ? DriverConfigurazioneDB_LIB.getValue(credenziali.getTipo()) : null));
				stm.setString(index++, (credenziali != null ? credenziali.getUser() : null));
				stm.setString(index++, (credenziali != null ? credenziali.getPassword() : null));
				stm.setString(index++, (credenziali != null ? credenziali.getSubject() : null));
				// setto idconnettore inv
				stm.setLong(index++, idConnettoreInv);

				// InvocazionePorta
				gestErr = invPorta != null ? invPorta.getGestioneErrore() : null;
				fault = (gestErr != null ? DriverConfigurazioneDB_LIB.getValue(gestErr.getFault()) : null);
				stm.setString(index++, fault);
				// setto credenziali invocaizone porta
				// per il momento c'e' soltato una credenziale,quindi un solo
				// oggetto nella lista
				credenziali = (invPorta != null && invPorta.sizeCredenzialiList() > 0 ? invPorta.getCredenziali(0) : null);
				stm.setString(index++, (credenziali != null ? DriverConfigurazioneDB_LIB.getValue(credenziali.getTipo()) : null));
				stm.setString(index++, (credenziali != null ? credenziali.getUser() : null));
				stm.setString(index++, (credenziali != null ? credenziali.getPassword() : null));
				subject = null;
				if(credenziali!=null && credenziali.getSubject()!=null && !"".equals(credenziali.getSubject()))
					subject = credenziali.getSubject();
				stm.setString(index++, (subject != null ? Utilities.formatSubject(subject) : null));

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

				break;

			case DELETE:
				// delete
				// if(aSA.getId()==null || aSA.getId()<=0) throw new
				// DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo(DELETE)]
				// id del ServizioApplicativo non valida.");

				DriverConfigurazioneDB_LIB.log.debug("get ID Servizio Applicativo con nome["+nomeSA+"] tipoProprietario["+tipoProprietario+"] nomeProprietario["+nomeProprietario+"]");
				idServizioApplicativo = DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeSA, tipoProprietario, nomeProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				DriverConfigurazioneDB_LIB.log.debug("get ID Servizio Applicativo: "+idServizioApplicativo); 


				// cancello anche le associazioni delle porteapplicative
				// associate a questo servizio
				// serviziapplicativi
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
				nomeConnettoreRisp = "ConnettoreRISP_" + aSA.getNome()+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario();
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
				nomeConnettoreInv = "ConnettoreINV_" + aSA.getNome()+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario();
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

		ProprietaProtocollo propProtocollo = null;

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
		if(messageSecurity!=null){
			if(messageSecurity.getRequestFlow()!=null){
				messageSecurityApplyMtom_request = messageSecurity.getRequestFlow().getApplyToMtom();
			}
			if(messageSecurity.getResponseFlow()!=null){
				messageSecurityApplyMtom_response = messageSecurity.getResponseFlow().getApplyToMtom();
			}
		}
		
		CorrelazioneApplicativa corrApp = aPA.getCorrelazioneApplicativa();
		CorrelazioneApplicativaRisposta corrAppRisposta = aPA.getCorrelazioneApplicativaRisposta();

		ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
		IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoPortaApplicativa();
		
		try {
			int n = 0;
			int i = 0;
			long idPortaApplicativa = 0;
			ServizioApplicativo servizioApplicativo = null;
			long idProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
			// preparo lo statement in base al tipo di operazione
			switch (type) {
			case CREATE:
				// CREATE

				//campi obbligatori
				//servizio ci deve essere l'id oppure tipo e nome
				String tipoServizio = (servizio != null ? servizio.getTipo() : null);
				String nomeServizio = (servizio != null ? servizio.getNome() : null);
				//se l'id non e' valido allora devono esserci necessariamente il tipo e il nome
				if(idServizio<=0){
					if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
					if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
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
				sqlQueryObject.addInsertField("azione", "?");
				sqlQueryObject.addInsertField("mtom_request_mode", "?");
				sqlQueryObject.addInsertField("mtom_response_mode", "?");
				sqlQueryObject.addInsertField("ws_security", "?");
				sqlQueryObject.addInsertField("ws_security_mtom_req", "?");
				sqlQueryObject.addInsertField("ws_security_mtom_res", "?");
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
				sqlQueryObject.addInsertField("autorizzazione_contenuto", "?");
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("id_port_type", "?");
				sqlQueryObject.addInsertField("scadenza_correlazione_appl", "?");
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
				stm.setString(index++, (azione != null ? azione.getNome() : null));
				// mtom
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_response));
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
				stm.setString(index++, aPA!=null ? aPA.getBehaviour() : null);
				
				// Autorizzazione per contenuto
				stm.setString(index++, aPA!=null ? aPA.getAutorizzazioneContenuto() : null);
				
				//idaccordo
				stm.setLong(index++, aPA.getIdAccordo()!=null ? aPA.getIdAccordo() : -1L);
				stm.setLong(index++, aPA.getIdPortType() !=null ? aPA.getIdPortType() : -1L);
				
				// ScadenzaCorrelazioneApplicativa
				stm.setString(index++, aPA.getCorrelazioneApplicativa()!=null ? aPA.getCorrelazioneApplicativa().getScadenza() : null);
				
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
				
				// se ws_security abilitato setto la lista
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
				servizioApplicativo = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);

				for (i = 0; i < aPA.sizeServizioApplicativoList(); i++) {
					servizioApplicativo = aPA.getServizioApplicativo(i);
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

					stm.setLong(1, idPortaApplicativa);
					stm.setLong(2, idSA);
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " servizi applicativi associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				// set prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPA.sizeProprietaProtocolloList(); i++) {
					propProtocollo = aPA.getProprietaProtocollo(i);
					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Insererted " + i + " SetProtocolProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				// extendedInfo
				i=0;
				if(aPA.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPA.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPA, aPA.getExtendedInfo(i));
						}
					}
				}
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + i + " associazioni ExtendedInfo<->PortaApplicativa associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				break;

			case UPDATE:
				// UPDATE
				String oldNomePA = aPA.getOldNomeForUpdate();
				String oldNomeProprietario = aPA.getOldNomeSoggettoProprietarioForUpdate();
				String oldTipoProprietario = aPA.getOldTipoSoggettoProprietarioForUpdate();
				if (oldNomePA == null || oldNomePA.equals(""))
					oldNomePA = nomePorta;
				if (oldNomeProprietario == null || oldNomeProprietario.equals(""))
					oldNomeProprietario = nomeProprietario;
				if (oldTipoProprietario == null || oldTipoProprietario.equals(""))
					oldTipoProprietario = tipoProprietario;

				//campi obbligatori
				//servizio ci deve essere l'id oppure tipo e nome
				tipoServizio = (servizio != null ? servizio.getTipo() : null);
				nomeServizio = (servizio != null ? servizio.getNome() : null);
				//se l'id non e' valido allora devono esserci necessariamente il tipo e il nome
				if(idServizio<=0){
					if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
					if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
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
				sqlQueryObject.addUpdateField("azione", "?");
				sqlQueryObject.addUpdateField("mtom_request_mode", "?");
				sqlQueryObject.addUpdateField("mtom_response_mode", "?");
				sqlQueryObject.addUpdateField("ws_security", "?");
				sqlQueryObject.addUpdateField("ws_security_mtom_req", "?");
				sqlQueryObject.addUpdateField("ws_security_mtom_res", "?");
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
				sqlQueryObject.addUpdateField("autorizzazione_contenuto", "?");
				sqlQueryObject.addUpdateField("id_accordo", "?");
				sqlQueryObject.addUpdateField("id_port_type", "?");
				sqlQueryObject.addUpdateField("scadenza_correlazione_appl", "?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.addWhereCondition("nome_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = con.prepareStatement(sqlQuery);

				idPortaApplicativa = DBUtils.getIdPortaApplicativa(oldNomePA, oldTipoProprietario, oldNomeProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				//  Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(idPortaApplicativa<=0) {
					idPortaApplicativa = DBUtils.getIdPortaApplicativa(oldNomePA, tipoProprietario, nomeProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				}
				if(idPortaApplicativa<=0) 
					throw new DriverConfigurazioneException("Impossibile recuperare l'id della Porta Applicativa nomePA["+oldNomePA+"] (old["+aPA.getOldNomeForUpdate()+"]) tipoProprietario["
						+oldTipoProprietario+"] (old["+aPA.getOldTipoSoggettoProprietarioForUpdate()+"]) nomeProprietario["+oldNomeProprietario+"] (old["+aPA.getOldNomeSoggettoProprietarioForUpdate()+"])");
								
				long oldIdProprietario = DBUtils.getIdSoggetto(oldNomeProprietario, oldTipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(oldIdProprietario<=0) {
					oldIdProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				}
				if(oldIdProprietario <=0) throw new DriverConfigurazioneException("Impossibile recuperare l'id del Soggetto Proprietario della Porta Applicativa");
				
				index = 1;
				
				stm.setString(index++, descrizione);
				stm.setLong(index++, idSoggVirt);
				stm.setString(index++, tipoSoggVirt);//(soggVirt != null ? soggVirt.getTipo() : null));
				stm.setString(index++, nomeSoggVirt);//(soggVirt != null ? soggVirt.getNome() : null));
				stm.setLong(index++, idServizio);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setString(index++, (azione != null ? azione.getNome() : null));
				// mtom
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_response));
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
				stm.setString(index++, aPA!=null ? aPA.getBehaviour() : null);
				// Autorizzazione per contenuto
				stm.setString(index++, aPA!=null ? aPA.getAutorizzazioneContenuto() : null);
				// id
				stm.setLong(index++, aPA.getIdAccordo() !=null ? aPA.getIdAccordo() : -1L);
				stm.setLong(index++, aPA.getIdPortType() !=null ? aPA.getIdPortType() : -1L);
				// ScadenzaCorrelazioneApplicativa
				stm.setString(index++, aPA.getCorrelazioneApplicativa()!=null ? aPA.getCorrelazioneApplicativa().getScadenza() : null);
				
				// where
				stm.setLong(index++, oldIdProprietario);
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
				
				
				// se ws_security abilitato setto la lista
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
				for (i = 0; i < aPA.sizeServizioApplicativoList(); i++) {
					servizioApplicativo = aPA.getServizioApplicativo(i);

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

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaApplicativa);
					stm.setLong(2, idSA);

					stm.executeUpdate();
					stm.close();
					n++;
					DriverConfigurazioneDB_LIB.log.debug("Aggiunta associazione PortaApplicativa<->ServizioApplicativo [" + idPortaApplicativa + "]<->[" + idSA + "]");
				}

				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " associazioni PortaApplicativa<->ServizioApplicativo associati alla PortaDelegata[" + idPortaApplicativa + "]");

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
				int newProps = 0;
				for (i = 0; i < aPA.sizeProprietaProtocolloList(); i++) {
					propProtocollo = aPA.getProprietaProtocollo(i);

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

				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  aPA);
				}
				
				i=0;
				if(aPA.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPA.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  aPA, aPA.getExtendedInfo(i));
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


				idPortaApplicativa = DBUtils.getIdPortaApplicativa(nomePorta, tipoProprietario, nomeProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				if (idPortaApplicativa <= 0)
					throw new DriverConfigurazioneException("Non e' stato possibile recuperare l'id della Porta Applicativa.");

				
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

				// serviziapplicativi
				servizioApplicativo = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " servizi applicativi associati alla PortaApplicativa[" + idPortaApplicativa + "]");

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
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " ws_request flow associate alla PortaApplicativa[" + idPortaApplicativa + "]");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " ws_response flow associate alla PortaApplicativa[" + idPortaApplicativa + "]");

				
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

				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  aPA);
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
	
	
	
	
	
	public static long CRUDConfigurazioneGenerale(int type, Configurazione config, Connection con) throws DriverConfigurazioneException {
		
		if(config.sizeExtendedInfoList()>0 && config.getAccessoConfigurazione()==null && config.getAccessoRegistro()==null &&
				config.getAccessoDatiAutorizzazione()==null && config.getAttachments()==null &&
				config.getGestioneErrore()==null && config.getIndirizzoRisposta()==null &&
				config.getInoltroBusteNonRiscontrate()==null && config.getIntegrationManager()==null &&
				config.getMessaggiDiagnostici()==null && config.getRisposte()==null &&
				config.getRoutingTable()==null && config.getStatoServiziPdd()==null &&
				config.getSystemProperties()==null && config.getTracciamento()==null &&
				config.getValidazioneBuste()==null && config.getValidazioneContenutiApplicativi()==null){
			
			// caso speciale extended info
			ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
			IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoConfigurazione();
			if(extInfoConfigurazioneDriver!=null){
			
				try{
					switch (type) {
					case CREATE:
					case UPDATE:
						extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config);
						
						
						if(config.sizeExtendedInfoList()>0){
							for(int l=0; l<config.sizeExtendedInfoList();l++){
								extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l));
							}
						}
						break;
					case DELETE:
						extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config);
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
		AccessoDatiAutorizzazione aDatiAuth = config.getAccessoDatiAutorizzazione();
		Attachments att = config.getAttachments();

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
		
		Cache auth_cache = null;
		String auth_dimensioneCache = null;
		String auth_algoritmoCache = null;
		String auth_idleCache = null;
		String auth_lifeCache = null;
		String auth_statoCache = null;
		if(aDatiAuth !=null){
			auth_cache = aDatiAuth.getCache();

		}
		auth_statoCache = (auth_cache != null ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString());
		if (auth_statoCache.equals(CostantiConfigurazione.ABILITATO.toString())) {
			auth_dimensioneCache = auth_cache.getDimensione();
			auth_algoritmoCache = DriverConfigurazioneDB_LIB.getValue(auth_cache.getAlgoritmo());
			auth_idleCache = auth_cache.getItemIdleTime();
			auth_lifeCache = auth_cache.getItemLifeSecond();
		}

		Tracciamento t = config.getTracciamento();
		String tracciamentoBuste = null;
		String dumpApplicativo = null;
		String dumpPD = null;
		String dumpPA = null;
		if (t != null) {
			tracciamentoBuste = DriverConfigurazioneDB_LIB.getValue(t.getBuste());
			dumpApplicativo = DriverConfigurazioneDB_LIB.getValue(t.getDump());
			dumpPD = DriverConfigurazioneDB_LIB.getValue(t.getDumpBinarioPortaDelegata());
			dumpPA = DriverConfigurazioneDB_LIB.getValue(t.getDumpBinarioPortaApplicativa());
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
				sqlQueryObject.addInsertField("tracciamento_dump", "?");
				sqlQueryObject.addInsertField("tracciamento_dump_bin_pd", "?");
				sqlQueryObject.addInsertField("tracciamento_dump_bin_pa", "?");
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
				// auth cache
				sqlQueryObject.addInsertField("auth_statocache", "?");
				sqlQueryObject.addInsertField("auth_dimensionecache", "?");
				sqlQueryObject.addInsertField("auth_algoritmocache", "?");
				sqlQueryObject.addInsertField("auth_idlecache", "?");
				sqlQueryObject.addInsertField("auth_lifecache", "?");
				
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
				// auth cache
				updateStmt.setString(index++, auth_statoCache);
				updateStmt.setString(index++, auth_dimensioneCache);
				updateStmt.setString(index++, auth_algoritmoCache);
				updateStmt.setString(index++, auth_idleCache);
				updateStmt.setString(index++, auth_lifeCache);

				DriverConfigurazioneDB_LIB.log.debug("eseguo query :" + 
						DBUtils.formatSQLString(updateQuery, 
								cadenza_inoltro, 
								val_stato, val_controllo, 
								msg_diag_severita, msg_diag_severita_log4j, 
								autenticazione, 
								val_profiloCollaborazione, 
								modRisposta, utilizzoIndTelematico, 
								routingEnabled, gestioneManifest, 
								val_manifest, tracciamentoBuste, dumpApplicativo, dumpPD, dumpPA,
								validazione_contenuti_stato,validazione_contenuti_tipo,validazione_contenuti_acceptMtomMessage,
								registro_statoCache, registro_dimensioneCache, registro_algoritmoCache, registro_idleCache, registro_lifeCache,
								config_statoCache, config_dimensioneCache, config_algoritmoCache, config_idleCache, config_lifeCache,
								auth_statoCache, auth_dimensioneCache, auth_algoritmoCache, auth_idleCache, auth_lifeCache));

				n = updateStmt.executeUpdate();
				updateStmt.close();

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
					
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config);
					
					
					if(config.sizeExtendedInfoList()>0){
						for(int l=0; l<config.sizeExtendedInfoList();l++){
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l));
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
				sqlQueryObject.addUpdateField("tracciamento_dump", "?");
				sqlQueryObject.addUpdateField("tracciamento_dump_bin_pd", "?");
				sqlQueryObject.addUpdateField("tracciamento_dump_bin_pa", "?");
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
				// auth cache
				sqlQueryObject.addUpdateField("auth_statocache", "?");
				sqlQueryObject.addUpdateField("auth_dimensionecache", "?");
				sqlQueryObject.addUpdateField("auth_algoritmocache", "?");
				sqlQueryObject.addUpdateField("auth_idlecache", "?");
				sqlQueryObject.addUpdateField("auth_lifecache", "?");

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
				// auth cache
				updateStmt.setString(index++, auth_statoCache);
				updateStmt.setString(index++, auth_dimensioneCache);
				updateStmt.setString(index++, auth_algoritmoCache);
				updateStmt.setString(index++, auth_idleCache);
				updateStmt.setString(index++, auth_lifeCache);

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
								tracciamentoBuste, dumpApplicativo, dumpPD, dumpPA,
								validazione_contenuti_stato,validazione_contenuti_tipo,
								registro_statoCache, registro_dimensioneCache, registro_algoritmoCache, registro_idleCache, registro_lifeCache,
								config_statoCache, config_dimensioneCache, config_algoritmoCache, config_idleCache, config_lifeCache,
								auth_statoCache, auth_dimensioneCache, auth_algoritmoCache, auth_idleCache, auth_lifeCache));

				n = updateStmt.executeUpdate();
				updateStmt.close();

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
					
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config);
					
					
					if(config.sizeExtendedInfoList()>0){
						for(int l=0; l<config.sizeExtendedInfoList();l++){
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config, config.getExtendedInfo(l));
						}
					}
				}
				

				break;

			case DELETE:
				
				extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log,  config);
				
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
						trasporto.setValoreMassimo(!maxVal.equals("") ? new Integer(maxVal) : null);
					}
					if(rs1.getLong("valore_minimo")>0){
						String minVal = ""+rs1.getLong("valore_minimo");
						trasporto.setValoreMinimo(!minVal.equals("") ? new Integer(minVal) : null);
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
	
	public static long getIdPortaApplicativa(String nomePorta, String tipoProprietario, String nomeProprietario,
			Connection con, String tipoDB) throws CoreException
	{
		return DBUtils.getIdPortaApplicativa(nomePorta, tipoProprietario, nomeProprietario, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdPortaApplicativa(String nomePorta, String tipoProprietario, String nomeProprietario,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto;
		long idPortaApplicativa=-1;

		try
		{
			idSoggetto = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, tipoDB,tabellaSoggetti);

			//recupero l'id della porta applicativa appena inserita
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			stm.setString(2, nomePorta);

			rs=stm.executeQuery();

			if(rs.next())
			{
				idPortaApplicativa=rs.getLong("id");
			}
			return idPortaApplicativa;
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
	
	public static long getIdPortaDelegata(String nomePorta, String tipoProprietario, String nomeProprietario,
			Connection con, String tipoDB) throws CoreException
	{
		return DriverConfigurazioneDB_LIB.getIdPortaDelegata(nomePorta, tipoProprietario, nomeProprietario, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdPortaDelegata(String nomePorta, String tipoProprietario, String nomeProprietario,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto;
		long idPortaDelegata=-1;

		try
		{
			idSoggetto = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, tipoDB,tabellaSoggetti);

			//recupero l'id della porta applicativa appena inserita
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			stm.setString(2, nomePorta);

			rs=stm.executeQuery();

			if(rs.next())
			{
				idPortaDelegata=rs.getLong("id");
			}
			return idPortaDelegata;
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
}
