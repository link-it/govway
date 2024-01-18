/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


import java.sql.Connection;
import java.text.MessageFormat;
import java.util.List;

import org.openspcoop2.core.config.ProtocolProperty;
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
import org.openspcoop2.core.config.constants.PluginSorgenteArchivio;
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
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
import org.openspcoop2.core.config.constants.TrasformazioneIdentificazioneRisorsaFallita;
import org.openspcoop2.core.config.constants.TrasformazioneRegolaParametroTipoAzione;
import org.openspcoop2.core.config.constants.ValidazioneBusteTipoControllo;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.config.constants.VersioneSOAP;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.mapping.DBProtocolPropertiesUtils;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * Libreria contenente i metodi di accesso al db e metodi di utilita'
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverConfigurazioneDBLib {
	
	private DriverConfigurazioneDBLib() {}

	/** Logger utilizzato per debug. */
	protected static org.slf4j.Logger log = LoggerWrapperFactory.getLogger(CostantiConfigurazione.DRIVER_DB_LOGGER);
	public static void logDebug(String msg) {
		if(log!=null) {
			log.debug(msg);
		}
	}
	public static void logDebug(String msg, Throwable e) {
		if(log!=null) {
			log.debug(msg, e);
		}
	}
	public static void logError(String msg, Throwable e) {
		if(log!=null) {
			log.error(msg, e);
		}
	}

	// Tipo database e tabella Soggetto PDD ereditato da DriverConfigurazioneDB
	static String tipoDB = null;
	static String tabellaSoggetti = CostantiDB.SOGGETTI;

	/** Log ereditato da DriverRegistroServiziDB
	 */
	private static boolean initialize = false;
	public static void initStaticLogger(Logger aLog){
		if(!DriverConfigurazioneDBLib.initialize){
			if(aLog!=null){
				DriverConfigurazioneDBLib.log = aLog;
			}
			DriverConfigurazioneDBLib.initialize = true;
		}
	}
	public static boolean isStaticLoggerInitialized(){
		return DriverConfigurazioneDBLib.initialize;
	}

	// Setto il tipoDB
	public static void setTipoDB(String tipoDatabase) {
		DriverConfigurazioneDBLib.tipoDB = tipoDatabase;
	}
	public static void setTabellaSoggetti(String tab) {
		DriverConfigurazioneDBLib.tabellaSoggetti = tab;
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
	public static String getValue(TrasformazioneIdentificazioneRisorsaFallita identificazione){
		if(identificazione==null){
			return null;
		}
		else{
			return identificazione.getValue();
		}
	}
	public static String getValue(PluginSorgenteArchivio sorgente){
		if(sorgente==null){
			return null;
		}
		else{
			return sorgente.getValue();
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
	public static TrasformazioneIdentificazioneRisorsaFallita getEnumTrasformazioneIdentificazioneRisorsaFallita(String value){
		if(value==null){
			return null;
		}
		else{
			return TrasformazioneIdentificazioneRisorsaFallita.toEnumConstant(value);
		}
	}
	public static PluginSorgenteArchivio getEnumPluginSorgenteArchivio(String value){
		if(value==null){
			return null;
		}
		else{
			return PluginSorgenteArchivio.toEnumConstant(value);
		}
	}
	
	
	
	
	public static String formatSQLString(String sql, Object... params) {
		String res = sql;

		for (int i = 0; i < params.length; i++) {
			res = res.replaceFirst("\\?", "{" + i + "}");
		}

		return MessageFormat.format(res, params);

	}
	
	

	
	public static void crudProtocolProperty(int type, List<ProtocolProperty> listPP, long idProprietario,
			org.openspcoop2.core.constants.ProprietariProtocolProperty tipologiaProprietarioProtocolProperty, Connection connection,
			String tipoDatabase) throws DriverConfigurazioneException {
		try {
			DBProtocolPropertiesUtils.CRUDConfigProtocolProperty(log, type, listPP, idProprietario, tipologiaProprietarioProtocolProperty, connection, tipoDatabase);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	
	public static List<ProtocolProperty> getListaProtocolProperty(long idProprietario, org.openspcoop2.core.constants.ProprietariProtocolProperty tipologiaProprietario, 
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
