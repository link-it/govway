/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteDelegateCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateCore extends ControlStationCore {

	public PorteDelegateCore() throws Exception {
		super();
	}
	public PorteDelegateCore(ControlStationCore core) throws Exception {
		super(core);
	}
	

	public void configureControlloAccessiPortaDelegata(PortaDelegata portaDelegata, 
			String fruizioneAutenticazione, String fruizioneAutenticazioneOpzionale,
			String fruizioneAutorizzazione, String fruizioneAutorizzazioneAutenticati, String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			String fruizioneServizioApplicativo, String fruizioneRuolo, 
			String fruizioneAutorizzazione_tokenOptions,
			String fruizioneAutorizzazioneScope, String fruizioneScope, String fruizioneAutorizzazioneScopeMatch) {
		
		portaDelegata.setAutenticazione(fruizioneAutenticazione);
		if(fruizioneAutenticazioneOpzionale != null){
			if(ServletUtils.isCheckBoxEnabled(fruizioneAutenticazioneOpzionale))
				portaDelegata.setAutenticazioneOpzionale(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO);
			else 
				portaDelegata.setAutenticazioneOpzionale(org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO);
		} else 
			portaDelegata.setAutenticazioneOpzionale(null);
		portaDelegata.setAutorizzazione(AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(fruizioneAutorizzazione, 
				ServletUtils.isCheckBoxEnabled(fruizioneAutorizzazioneAutenticati), 
				ServletUtils.isCheckBoxEnabled(fruizioneAutorizzazioneRuoli),
				ServletUtils.isCheckBoxEnabled(fruizioneAutorizzazioneScope),
				fruizioneAutorizzazione_tokenOptions,
				RuoloTipologia.toEnumConstant(fruizioneAutorizzazioneRuoliTipologia)));
		
		if(fruizioneAutorizzazioneRuoliMatch!=null && !"".equals(fruizioneAutorizzazioneRuoliMatch)){
			RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant(fruizioneAutorizzazioneRuoliMatch);
			if(tipoRuoloMatch!=null){
				if(portaDelegata.getRuoli()==null){
					portaDelegata.setRuoli(new AutorizzazioneRuoli());
				}
				portaDelegata.getRuoli().setMatch(tipoRuoloMatch);
			}
		}

		// servizioApplicativo
		if(fruizioneServizioApplicativo!=null && !"".equals(fruizioneServizioApplicativo) && !"-".equals(fruizioneServizioApplicativo)){
			PortaDelegataServizioApplicativo sa = new PortaDelegataServizioApplicativo();
			sa.setNome(fruizioneServizioApplicativo);
			portaDelegata.addServizioApplicativo(sa);
		}

		// ruolo
		if(fruizioneRuolo!=null && !"".equals(fruizioneRuolo) && !"-".equals(fruizioneRuolo)){
			if(portaDelegata.getRuoli()==null){
				portaDelegata.setRuoli(new AutorizzazioneRuoli());
			}
			Ruolo ruolo = new Ruolo();
			ruolo.setNome(fruizioneRuolo);
			portaDelegata.getRuoli().addRuolo(ruolo);
		}
		
		if(ServletUtils.isCheckBoxEnabled(fruizioneAutorizzazioneScope)) {
			if(portaDelegata.getScope() == null)
				portaDelegata.setScope(new AutorizzazioneScope());
			
			portaDelegata.getScope().setStato(StatoFunzionalita.ABILITATO); 
		}
		else {
			portaDelegata.setScope(null);
		}
		// scope
		if(fruizioneScope!=null && !"".equals(fruizioneScope) && !"-".equals(fruizioneScope)){
			if(portaDelegata.getScope() == null) {
				portaDelegata.setScope(new AutorizzazioneScope());
			}
			Scope scope = new Scope();
			scope.setNome(fruizioneScope);
			portaDelegata.getScope().addScope(scope);
		}
		if(fruizioneAutorizzazioneScopeMatch!=null && !"".equals(fruizioneAutorizzazioneScopeMatch)){
			ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant(fruizioneAutorizzazioneScopeMatch);
			if(scopeTipoMatch!=null){
				if(portaDelegata.getScope()==null){
					portaDelegata.setScope(new AutorizzazioneScope());
				}
				portaDelegata.getScope().setMatch(scopeTipoMatch);
			}
		}
	}
	
	public void configureControlloAccessiGestioneToken (PortaDelegata portaDelegata, String gestioneToken, 
			String gestioneTokenPolicy, String gestioneTokenOpzionale,   
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			String autorizzazione_tokenOptions) {
		if(portaDelegata.getGestioneToken() == null)
			portaDelegata.setGestioneToken(new GestioneToken());
		
		if(gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) {
			portaDelegata.getGestioneToken().setPolicy(gestioneTokenPolicy);
			portaDelegata.getGestioneToken().setTokenOpzionale(StatoFunzionalita.toEnumConstant(gestioneTokenOpzionale)); 
			portaDelegata.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenValidazioneInput));
			portaDelegata.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenIntrospection));
			portaDelegata.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenUserInfo));
			portaDelegata.getGestioneToken().setForward(StatoFunzionalita.toEnumConstant(gestioneTokenForward)); 
			portaDelegata.getGestioneToken().setOptions(autorizzazione_tokenOptions);
			if(portaDelegata.getGestioneToken().getAutenticazione()==null) {
				portaDelegata.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
			}
			portaDelegata.getGestioneToken().getAutenticazione().setIssuer(StatoFunzionalita.toEnumConstant(autenticazioneTokenIssuer)); 
			portaDelegata.getGestioneToken().getAutenticazione().setClientId(StatoFunzionalita.toEnumConstant(autenticazioneTokenClientId)); 
			portaDelegata.getGestioneToken().getAutenticazione().setSubject(StatoFunzionalita.toEnumConstant(autenticazioneTokenSubject)); 
			portaDelegata.getGestioneToken().getAutenticazione().setUsername(StatoFunzionalita.toEnumConstant(autenticazioneTokenUsername)); 
			portaDelegata.getGestioneToken().getAutenticazione().setEmail(StatoFunzionalita.toEnumConstant(autenticazioneTokenEMail)); 
		} else {
			portaDelegata.getGestioneToken().setPolicy(null);
			portaDelegata.getGestioneToken().setTokenOpzionale(StatoFunzionalita.DISABILITATO); 
			portaDelegata.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.DISABILITATO);
			portaDelegata.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.DISABILITATO);
			portaDelegata.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.DISABILITATO);
			portaDelegata.getGestioneToken().setForward(StatoFunzionalita.DISABILITATO); 
			portaDelegata.getGestioneToken().setOptions(null);
			if(portaDelegata.getGestioneToken().getAutenticazione()!=null) {
				portaDelegata.getGestioneToken().setAutenticazione(null);
			}
		}
	}
	
	
	public List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getPorteDelegateWithServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPorteDelegateWithServizio(idServizio);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<PortaDelegata> porteDelegateWithSoggettoErogatoreList(long idSoggettoErogatore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "porteDelegateWithSoggettoErogatoreList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateWithSoggettoErogatoreList(idSoggettoErogatore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<PortaDelegata> porteDelegateWithTipoNomeErogatoreList(String tipoSoggettoErogatore, String nomeSoggettoErogatore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "porteDelegateWithTipoNomeErogatoreList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateWithTipoNomeErogatoreList(tipoSoggettoErogatore, nomeSoggettoErogatore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio, String tiposervizio, String nomeservizio, Integer versioneServizio,
			Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPorteDelegateWithServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPorteDelegateWithServizio(idServizio, tiposervizio, nomeservizio, versioneServizio,
					idSoggetto, tiposoggetto, nomesoggetto);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsPortaDelegataAzione(String nome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsPortaDelegataAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsPortaDelegataAzione(nome);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsPortaDelegata";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsPortaDelegata(idPD);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityRequestList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateMessageSecurityRequestList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateMessageSecurityRequestList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityResponseList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateMessageSecurityResponseList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateMessageSecurityResponseList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getPortaDelegata";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPortaDelegata(idPD);

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public PortaDelegata getPortaDelegata(long idPortaDelegata) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPortaDelegata";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPortaDelegata(idPortaDelegata);

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDPortaDelegata> getPortaDelegataAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getPortaDelegataAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPortaDelegataAzione(nome);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	
	public long getIdPortaDelegata(String nomePorta) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPortaDelegata";
	
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			return DBUtils.getIdPortaDelegata(nomePorta,  con, this.getTipoDatabase());

		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDPortaDelegata> getAllIdPorteDelegate(FiltroRicercaPorteDelegate filtroRicerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getAllIdPorteDelegate";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getAllIdPorteDelegate(filtroRicerca);

		} catch (DriverConfigurazioneNotFound notFound) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + notFound.getMessage(), notFound);
			return new ArrayList<IDPortaDelegata>();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<PortaDelegata> porteDelegateList(int idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateList(idSoggetto, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<PortaDelegata> porteDelegateList(String superUser, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateList(superUser, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<ServizioApplicativo> porteDelegateServizioApplicativoList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateServizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateServizioApplicativoList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<CorrelazioneApplicativaElemento> porteDelegateCorrelazioneApplicativaList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateCorrelazioneApplicativaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateCorrelazioneApplicativaList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<CorrelazioneApplicativaRispostaElemento> porteDelegateCorrelazioneApplicativaRispostaList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateCorrelazioneApplicativaRispostaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateCorrelazioneApplicativaRispostaList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage());
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<MtomProcessorFlowParameter> porteDelegateMTOMRequestList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateMTOMRequestList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateMTOMRequestList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<MtomProcessorFlowParameter> porteDelegateMTOMResponseList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateMTOMResponseList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateMTOMResponseList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	
	
	public IDPortaDelegata getIDPortaDelegataAssociataDefault(IDServizio idServizio, IDSoggetto fruitore) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIDPortaDelegataAssociataDefault";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getIDPortaDelegataAssociataDefault(idServizio,fruitore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public IDPortaDelegata getIDPortaDelegataAssociataAzione(IDServizio idServizio, IDSoggetto fruitore) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIDPortaDelegataAssociataAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getIDPortaDelegataAssociataAzione(idServizio,fruitore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDPortaDelegata> getIDPorteDelegateAssociate(IDServizio idServizio, IDSoggetto fruitore) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIDPorteDelegateAssociate";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getIDPorteDelegateAssociate(idServizio,fruitore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsMappingFruizionePortaDelegata(MappingFruizionePortaDelegata mapping) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsMappingFruizionePortaDelegata";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.existsMappingFruizionePortaDelegata(mapping);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<String> portaDelegataRuoliList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "portaDelegataRuoliList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().portaDelegataRuoliList(idPD, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<String> portaDelegataScopeList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "portaDelegataScopeList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().portaDelegataScopeList(idPD, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public MappingFruizionePortaDelegata getMappingFruizionePortaDelegata(PortaDelegata pd) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getMappingFruizionePortaDelegata";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setNome(pd.getNome());
			
			IDSoggetto idFruitore = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
			
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pd.getServizio().getTipo(), pd.getServizio().getNome(), 
					pd.getSoggettoErogatore().getTipo(),pd.getSoggettoErogatore().getNome(), 
					pd.getServizio().getVersione());
			
			return DBMappingUtils.getMappingFruizione(idServizio, idFruitore, idPD, con, this.tipoDB);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public String getLabelRegolaMappingFruizionePortaDelegata(PortaDelegata pd) throws DriverConfigurazioneException {
		return getLabelRegolaMappingFruizionePortaDelegata(pd, 50);
	}
	public String getLabelRegolaMappingFruizionePortaDelegata(PortaDelegata pd, int sizeSubstring) throws DriverConfigurazioneException {
		MappingFruizionePortaDelegata mapping = this.getMappingFruizionePortaDelegata(pd);
		if(mapping.isDefault()) {
			return PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_NOME_DEFAULT;
			//return "(*)";
		}
		else {
			//return mapping.getNome();
			List<String> listaAzioni = pd.getAzione()!= null ?  pd.getAzione().getAzioneDelegataList() : new ArrayList<String>();
			if(listaAzioni.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (String string : listaAzioni) {
					if(sb.length() >0)
						sb.append(", ");
					
					sb.append(string);
				}
				if(sb.length()>sizeSubstring)
					return sb.toString().substring(0, (sizeSubstring-3))+"...";
				else 
					return sb.toString();
			}
			else {
				return "???";
			}
		}
	}
	public List<Proprieta> porteDelPropList(int idPortaDelegata, Search ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelPropList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegatePropList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Map<String, Properties> readMessageSecurityRequestPropertiesConfiguration(long idPortaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "readMessageSecurityRequestPropertiesConfiguration";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			Map<String, String> readProperties = DBPropertiesUtils.readProperties(con, this.tipoDB, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME, 
					CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_ID_PORTA, idPortaDelegata);
			return DBPropertiesUtils.toMultiMap(readProperties);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}	
	}
	
	public Map<String, Properties> readMessageSecurityResponsePropertiesConfiguration(long idPortaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "readMessageSecurityResponsePropertiesConfiguration";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			Map<String, String> readProperties = DBPropertiesUtils.readProperties(con, this.tipoDB, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME, 
					CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_ID_PORTA, idPortaDelegata);
			return DBPropertiesUtils.toMultiMap(readProperties);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}	
	}
}
