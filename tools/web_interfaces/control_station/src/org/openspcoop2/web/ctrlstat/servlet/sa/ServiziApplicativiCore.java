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
package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.pdd.core.autenticazione.ApiKey;
import org.openspcoop2.pdd.core.autenticazione.ApiKeyUtilities;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;

/**
 * ServiziApplicativiCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiziApplicativiCore extends ControlStationCore {

	public ServiziApplicativiCore() throws DriverControlStationException {
		super();
	}
	public ServiziApplicativiCore(ControlStationCore core) throws DriverControlStationException {
		super(core);
	}
		
	public ServizioApplicativo getServizioApplicativo(long idServizioApplicativo) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getServizioApplicativo(idServizioApplicativo);

		} catch (DriverConfigurazioneNotFound e) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  e),e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativo(IDServizioApplicativo)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getServizioApplicativo(idServizioApplicativo);

		} 
		catch (DriverConfigurazioneNotFound  e) {
			// Lasciare DEBUG, usato anche in servizio API RS
			ControlStationCore.logDebug("[ControlStationCore::" + nomeMetodo + "] ExceptionNotFound :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] NotFound :" + e.getMessage(),e);
		}
		catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	
	public boolean isSupportatoAutenticazioneApplicativiErogazione(String protocollo) throws DriverRegistroServiziException {
		String nomeMetodo = "isSupportatoAutenticazioneApplicativiErogazione";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoAutenticazioneApplicativiErogazioni();
			
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public boolean isSupportatoAutenticazioneApplicativiEsterniErogazione(String protocollo) throws DriverRegistroServiziException {
		String nomeMetodo = "isSupportatoAutenticazioneApplicativiEsterniErogazione";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoAutenticazioneApplicativiEsterniErogazioni();
			
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public long getIdServizioApplicativo(IDSoggetto idSoggetto, String nomeServizioApplicativo) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIdServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getIdServizioApplicativo(idSoggetto, nomeServizioApplicativo);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiBasicList(String utente, String password, boolean checkPassword) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoWithCredenzialiBasicList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoWithCredenzialiBasicList(utente, password, checkPassword);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public String toAppId(String protocollo, IDServizioApplicativo idSA,  boolean multipleApiKeys) throws DriverConfigurazioneException {
		
		Connection con = null;
		String nomeMetodo = "toAppId";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return ApiKeyUtilities.toAppId(protocollo, idSA, multipleApiKeys, driver.getDriverConfigurazioneDB());

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
		
	}
	
	public ApiKey newApiKey(String protocollo, IDServizioApplicativo idSA) throws DriverConfigurazioneException {
		
		Connection con = null;
		String nomeMetodo = "newApiKey";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return ApiKeyUtilities.newApiKey(protocollo, idSA, this.getApplicativiApiKeyLunghezzaPasswordGenerate(), driver.getDriverConfigurazioneDB());

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
		
	}
	
	public ApiKey newMultipleApiKey() throws DriverConfigurazioneException {
		
		String nomeMetodo = "newMultipleApiKey";
		try {
			return ApiKeyUtilities.newMultipleApiKey(this.getApplicativiApiKeyLunghezzaPasswordGenerate());
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		}
		
	}
	
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiApiKeyList(String utente, boolean appId) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoWithCredenzialiApiKeyList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoWithCredenzialiApiKeyList(utente, appId);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiSslList(String subject, String issuer) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoWithCredenzialiSslList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoWithCredenzialiSslList(subject, issuer);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiSslList(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoWithCredenzialiSslList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoWithCredenzialiSslList(certificate, strictVerifier);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiPrincipalList(String principal) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoWithCredenzialiPrincipalList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoWithCredenzialiPrincipalList(principal);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiTokenList(String tokenPolicy, String tokenClientId) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoWithCredenzialiTokenList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			boolean tokenWithHttpsEnabled = false;
			// basta un protocollo che lo supporta per doverli cercare anche con la funzionalita' abilitata
			for(IProtocolFactory<?> protocolFactory: ProtocolFactoryManager.getInstance().getProtocolFactories().values()) {
				if(protocolFactory.createProtocolConfiguration().isSupportatoAutenticazioneApplicativiHttpsConToken()) {
					tokenWithHttpsEnabled = true;
					break;
				}
			}
			
			return driver.getDriverConfigurazioneDB().servizioApplicativoWithCredenzialiTokenList(tokenPolicy, tokenClientId, tokenWithHttpsEnabled);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<ServizioApplicativo> servizioApplicativoList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoList(ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<ServizioApplicativo> servizioApplicativoList(IDSoggetto idSoggetto,ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoList(idSoggetto,ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	

	public boolean isServizioApplicativoInUsoComeErogatore(ServizioApplicativo sa, Map<ErrorsHandlerCostant, String> whereIsInUso) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "isServizioApplicativoInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().isServizioApplicativoInUsoComeErogatore(sa, whereIsInUso);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	
	public boolean existsServizioApplicativo(IDServizioApplicativo idSA) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsServizioApplicativo(idSA);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDServizioApplicativoDB> getIdServiziApplicativiWithIdErogatore(Long idErogatore) throws DriverConfigurazioneException {
		return getIdServiziApplicativiWithIdErogatore(idErogatore, null, false, false);
	}
	public List<IDServizioApplicativoDB> getIdServiziApplicativiWithIdErogatore(Long idErogatore,
			boolean checkIM, boolean checkConnettoreAbilitato) throws DriverConfigurazioneException {
		return getIdServiziApplicativiWithIdErogatore(idErogatore, null, checkIM, checkConnettoreAbilitato);
	}
	public List<IDServizioApplicativoDB> getIdServiziApplicativiWithIdErogatore(Long idErogatore, String tipo, 
			boolean checkIM, boolean checkConnettoreAbilitato) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServiziApplicativiWithIdErogatore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getIdServiziApplicativiWithIdErogatore(idErogatore, tipo, checkIM, checkConnettoreAbilitato);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<IDServizioApplicativoDB> soggettiServizioApplicativoList(IDSoggetto idSoggetto,String superuser,CredenzialeTipo credenziale, Boolean appId, String tipo, 
			boolean bothSslAndToken, String tokenPolicy) throws DriverConfigurazioneException {
		return soggettiServizioApplicativoList(idSoggetto, superuser, credenziale, appId, tipo, 
				bothSslAndToken, tokenPolicy, false);
	}
	public List<IDServizioApplicativoDB> soggettiServizioApplicativoList(IDSoggetto idSoggetto,String superuser,CredenzialeTipo credenziale, Boolean appId, String tipo, 
			boolean bothSslAndToken, String tokenPolicy, boolean tokenPolicyOR) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "soggettiServizioApplicativoList(soggetto,user,credenziale,appId,tipo,ssl,token)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().soggettiServizioApplicativoList(idSoggetto,superuser,credenziale, appId, tipo, 
					bothSslAndToken, tokenPolicy, tokenPolicyOR);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<ServizioApplicativo> soggettiServizioApplicativoList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "soggettiServizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().soggettiServizioApplicativoList(superuser, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<ServizioApplicativo> soggettiServizioApplicativoList(ISearch ricerca,Long idSoggetto) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "soggettiServizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().soggettiServizioApplicativoList(idSoggetto, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isServizioApplicativoInUso(IDServizioApplicativo idServizioApplicativo,
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean isRegistroServiziLocale, boolean normalizeObjectIds, boolean verificaRuoli) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "isServizioApplicativoInUso";
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();

			return DBOggettiInUsoUtils.isServizioApplicativoInUso(con, this.tipoDB, idServizioApplicativo, whereIsInUso, isRegistroServiziLocale, normalizeObjectIds, verificaRuoli);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	
	public List<IDServizioApplicativo> getAllIdServiziApplicativi(FiltroRicercaServiziApplicativi filtroRicerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getAllIdServiziApplicativi";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getAllIdServiziApplicativi(filtroRicerca);

		} catch (DriverConfigurazioneNotFound e) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  e),e);
			return new ArrayList<>();
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<String> servizioApplicativoRuoliList(long idSA, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoRuoliList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoRuoliList(idSA, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public long getIdServizioApplicativoByConnettore(long idConnettore) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIdServizioApplicativoByConnettore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getIdServizioApplicativoByConnettore(idConnettore);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public String getDettagliServizioApplicativoInUso(IDServizioApplicativo idServizioApplicativo, boolean verificaRuoli) throws DriverConfigurazioneException {
		EnumMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new EnumMap<>(ErrorsHandlerCostant.class);
		boolean normalizeObjectIds = true;
		boolean saInUso  = this.isServizioApplicativoInUso(idServizioApplicativo, whereIsInUso, this.isRegistroServiziLocale(), normalizeObjectIds, verificaRuoli );
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(saInUso) {
			String s = DBOggettiInUsoUtils.toString(idServizioApplicativo, whereIsInUso, false, "\n", normalizeObjectIds);
			if(s!=null && s.startsWith("\n") && s.length()>1) {
				s = s.substring(1);
			}
			inUsoMessage.append(s);
			inUsoMessage.append("\n");
		} else {
			inUsoMessage.append(ServiziApplicativiCostanti.LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO);
		}
		
		return inUsoMessage.toString();
	}
	public List<Proprieta> serviziApplicativiProprietaList(int idSA, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "serviziApplicativiProprietaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().serviziApplicativiProprietaList(idSA, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<String> nomiProprietaSA(String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoSoggettiProtocollo) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "nomiProprietaSA";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().nomiProprietaSA(filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo); 

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
}
