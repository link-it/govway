/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
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
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteApplicativeCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeCore extends ControlStationCore {

	public PorteApplicativeCore() throws Exception {
		super();
	}
	public PorteApplicativeCore(ControlStationCore core) throws Exception {
		super(core);
	}
	
	public void configureControlloAccessiPortaApplicativa(PortaApplicativa pa,
			String erogazioneAutenticazione, String erogazioneAutenticazioneOpzionale,
			String erogazioneAutorizzazione, String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,
			String nomeSA, String erogazioneRuolo, IDSoggetto idErogazioneSoggettoAutenticato, 
			String erogazioneAutorizzazione_tokenOptions,
			String erogazioneAutorizzazioneScope, String erogazioneScope, String erogazioneAutorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy) {
		pa.setAutenticazione(erogazioneAutenticazione);
		if(erogazioneAutenticazioneOpzionale != null){
			if(ServletUtils.isCheckBoxEnabled(erogazioneAutenticazioneOpzionale))
				pa.setAutenticazioneOpzionale(StatoFunzionalita.ABILITATO);
			else 
				pa.setAutenticazioneOpzionale(StatoFunzionalita.DISABILITATO);
		} else 
			pa.setAutenticazioneOpzionale(null);
		pa.setAutorizzazione(AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(erogazioneAutorizzazione, 
				ServletUtils.isCheckBoxEnabled(erogazioneAutorizzazioneAutenticati), 
				ServletUtils.isCheckBoxEnabled(erogazioneAutorizzazioneRuoli),
				ServletUtils.isCheckBoxEnabled(erogazioneAutorizzazioneScope),
				erogazioneAutorizzazione_tokenOptions,
				RuoloTipologia.toEnumConstant(erogazioneAutorizzazioneRuoliTipologia)));
		
		if(erogazioneAutorizzazione != null && erogazioneAutorizzazione.equals(AutorizzazioneUtilities.STATO_XACML_POLICY) && allegatoXacmlPolicy.getValue() != null) {
			pa.setXacmlPolicy(new String(allegatoXacmlPolicy.getValue()));
		} else {
			pa.setXacmlPolicy(null);
		}
		
		if(erogazioneAutorizzazioneRuoliMatch!=null && !"".equals(erogazioneAutorizzazioneRuoliMatch)){
			RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant(erogazioneAutorizzazioneRuoliMatch);
			if(tipoRuoloMatch!=null){
				if(pa.getRuoli()==null){
					pa.setRuoli(new AutorizzazioneRuoli());
				}
				pa.getRuoli().setMatch(tipoRuoloMatch);
			}
		}
		
		if(nomeSA!=null && !"".equals(nomeSA) && !"-".equals(nomeSA)){
			PortaApplicativaServizioApplicativo sa = new PortaApplicativaServizioApplicativo();
			sa.setNome(nomeSA);
			pa.addServizioApplicativo(sa);
		}

		// ruolo
		if(erogazioneRuolo!=null && !"".equals(erogazioneRuolo) && !"-".equals(erogazioneRuolo)){
			if(pa.getRuoli()==null){
				pa.setRuoli(new AutorizzazioneRuoli());
			}
			Ruolo ruolo = new Ruolo();
			ruolo.setNome(erogazioneRuolo);
			pa.getRuoli().addRuolo(ruolo);
		}
		
		if(idErogazioneSoggettoAutenticato!=null){
			if(pa.getSoggetti() == null) {
				pa.setSoggetti(new PortaApplicativaAutorizzazioneSoggetti());
			}
			
			PortaApplicativaAutorizzazioneSoggetto soggetto = new PortaApplicativaAutorizzazioneSoggetto();
			soggetto.setTipo(idErogazioneSoggettoAutenticato.getTipo());
			soggetto.setNome(idErogazioneSoggettoAutenticato.getNome()); 
			pa.getSoggetti().addSoggetto(soggetto);
		}
		
		if(ServletUtils.isCheckBoxEnabled(erogazioneAutorizzazioneScope)) {
			if(pa.getScope() == null)
				pa.setScope(new AutorizzazioneScope());
			
			pa.getScope().setStato(StatoFunzionalita.ABILITATO); 
		}
		else {
			pa.setScope(null);
		}
		// scope
		if(erogazioneScope!=null && !"".equals(erogazioneScope) && !"-".equals(erogazioneScope)){
			if(pa.getScope() == null) {
				pa.setScope(new AutorizzazioneScope());
			}
			Scope scope = new Scope();
			scope.setNome(erogazioneScope);
			pa.getScope().addScope(scope);
		}
		if(erogazioneAutorizzazioneScopeMatch!=null && !"".equals(erogazioneAutorizzazioneScopeMatch)){
			ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant(erogazioneAutorizzazioneScopeMatch);
			if(scopeTipoMatch!=null){
				if(pa.getScope()==null){
					pa.setScope(new AutorizzazioneScope());
				}
				pa.getScope().setMatch(scopeTipoMatch);
			}
		}
	}
	
	public void configureControlloAccessiGestioneToken (PortaApplicativa portaApplicativa, String gestioneToken, 
			String gestioneTokenPolicy,  String gestioneTokenOpzionale,  
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			String autorizzazione_tokenOptions) {
		if(portaApplicativa.getGestioneToken() == null)
			portaApplicativa.setGestioneToken(new GestioneToken());
		
		if(gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) {
			portaApplicativa.getGestioneToken().setPolicy(gestioneTokenPolicy);
			portaApplicativa.getGestioneToken().setTokenOpzionale(StatoFunzionalita.toEnumConstant(gestioneTokenOpzionale)); 
			portaApplicativa.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenValidazioneInput));
			portaApplicativa.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenIntrospection));
			portaApplicativa.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenUserInfo));
			portaApplicativa.getGestioneToken().setForward(StatoFunzionalita.toEnumConstant(gestioneTokenForward)); 	
			portaApplicativa.getGestioneToken().setOptions(autorizzazione_tokenOptions);
			if(portaApplicativa.getGestioneToken().getAutenticazione()==null) {
				portaApplicativa.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
			}
			portaApplicativa.getGestioneToken().getAutenticazione().setIssuer(StatoFunzionalita.toEnumConstant(autenticazioneTokenIssuer)); 
			portaApplicativa.getGestioneToken().getAutenticazione().setClientId(StatoFunzionalita.toEnumConstant(autenticazioneTokenClientId)); 
			portaApplicativa.getGestioneToken().getAutenticazione().setSubject(StatoFunzionalita.toEnumConstant(autenticazioneTokenSubject)); 
			portaApplicativa.getGestioneToken().getAutenticazione().setUsername(StatoFunzionalita.toEnumConstant(autenticazioneTokenUsername)); 
			portaApplicativa.getGestioneToken().getAutenticazione().setEmail(StatoFunzionalita.toEnumConstant(autenticazioneTokenEMail)); 
		} else {
			portaApplicativa.getGestioneToken().setPolicy(null);
			portaApplicativa.getGestioneToken().setTokenOpzionale(StatoFunzionalita.DISABILITATO); 
			portaApplicativa.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.DISABILITATO);
			portaApplicativa.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.DISABILITATO);
			portaApplicativa.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.DISABILITATO);
			portaApplicativa.getGestioneToken().setForward(StatoFunzionalita.DISABILITATO); 
			portaApplicativa.getGestioneToken().setOptions(null);
			if(portaApplicativa.getGestioneToken().getAutenticazione()!=null) {
				portaApplicativa.getGestioneToken().setAutenticazione(null);
			}
		}
	}
	
	
	public List<PortaApplicativa> porteAppWithServizio(long idSoggettoErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "porteAppWithTipoNomeServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppWithServizio(idSoggettoErogatore, tipoServizio, nomeServizio, versioneServizio);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPortaApplicativa";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPortaApplicativa(idPA);

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
	

	public List<PortaApplicativa> porteAppList(int idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppList(idSoggetto, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<PortaApplicativa> porteAppList(String superUser, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppList(superUser, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<PortaApplicativa> getPorteApplicativeBySoggettoVirtuale(IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPorteApplicativeBySoggettoVirtuale";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPorteApplicativeBySoggettoVirtuale(soggettoVirtuale);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage());
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	

	public List<Proprieta> porteAppPropList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppPropList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppPropList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<ServizioApplicativo> porteAppServizioApplicativoList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppServizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppServizioApplicativoList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<PortaApplicativaAutorizzazioneSoggetto> porteAppSoggettoList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppSoggettoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppSoggettoList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	

	public List<MessageSecurityFlowParameter> porteAppMessageSecurityRequestList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppMessageSecurityRequestList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppMessageSecurityRequestList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<PortaApplicativa> porteAppWithIdServizio(long idServizio) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppWithIdServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppWithIdServizio(idServizio);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<MessageSecurityFlowParameter> porteAppMessageSecurityResponseList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppMessageSecurityResponseList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppMessageSecurityResponseList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<CorrelazioneApplicativaElemento> porteApplicativeCorrelazioneApplicativaList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteApplicativeCorrelazioneApplicativaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeCorrelazioneApplicativaList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<CorrelazioneApplicativaRispostaElemento> porteApplicativeCorrelazioneApplicativaRispostaList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteApplicativeCorrelazioneApplicativaRispostaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeCorrelazioneApplicativaRispostaList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage());
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<PortaApplicativaAzione> porteAppAzioneList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppAzioneList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppAzioneList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	

	public PortaApplicativa getPortaApplicativa(long idPortaApplicativa) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPortaApplicativa";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPortaApplicativa(idPortaApplicativa);

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

	
	public boolean existsPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsPortaApplicativa";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsPortaApplicativa(idPA);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsPortaApplicativa(IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsPortaApplicativa (ricercaPuntuale)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			List<PortaApplicativa> list = driver.getDriverConfigurazioneDB().getPorteApplicative(idServizio, ricercaPuntuale);
			
			return list!=null && list.size()>0;
		} catch(DriverConfigurazioneNotFound nfe) {
			return false;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsPortaApplicativaVirtuale(IDServizio idServizio, IDSoggetto soggettoVirtuale, boolean ricercaPuntuale) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsPortaApplicativa_soggettoVirtuale (ricercaPuntuale)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			List<PortaApplicativa> list = driver.getDriverConfigurazioneDB().getPorteApplicativeVirtuali(soggettoVirtuale, idServizio, ricercaPuntuale);
			
			return list!=null && list.size()>0;

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsPortaApplicativaAzione(String nome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsPortaApplicativaAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsPortaApplicativaAzione(nome);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDPortaApplicativa> getPortaApplicativaAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getPortaApplicativaAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPortaApplicativaAzione(nome);

		}catch(DriverConfigurazioneNotFound dNotF){
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + dNotF.getMessage(), dNotF);
			throw dNotF;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<PortaApplicativa> getPorteApplicativaByIdProprietario(long idProprietario) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPorteApplicativaByIdProprietario";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPorteApplicativaByIdProprietario(idProprietario);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<PortaApplicativa> getPorteApplicativeWithServizio(Long idServizio, String tiposervizio, String nomeservizio, Integer versioneServizio,
			Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPorteApplicativeWithServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPorteApplicativeWithServizio(idServizio, tiposervizio, nomeservizio, versioneServizio,
					idSoggetto, tiposoggetto, nomesoggetto);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public PortaApplicativa getPortaApplicativaWithSoggettoAndServizio(String nome, Long idSoggetto, Long idServizio, String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPortaApplicativaWithSoggettoAndServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPortaApplicativaWithSoggettoAndServizio(nome, idSoggetto, idServizio, tipoServizio, nomeServizio, versioneServizio);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<MtomProcessorFlowParameter> porteApplicativeMTOMRequestList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteApplicativeMTOMRequestList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeMTOMRequestList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<MtomProcessorFlowParameter> porteApplicativeMTOMResponseList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteApplicativeMTOMResponseList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeMTOMResponseList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	
	public IDPortaApplicativa getIDPortaApplicativaAssociataDefault(IDServizio idServizio) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIDPortaApplicativaAssociataDefault";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getIDPortaApplicativaAssociataDefault(idServizio);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public IDPortaApplicativa getIDPortaApplicativaAssociataAzione(IDServizio idServizio) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIDPortaApplicativaAssociataAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getIDPortaApplicativaAssociataAzione(idServizio);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDPortaApplicativa> getIDPorteApplicativeAssociate(IDServizio idServizio) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIDPorteApplicativeAssociate";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getIDPorteApplicativeAssociate(idServizio);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public boolean existsMappingErogazionePortaApplicativa(MappingErogazionePortaApplicativa mapping) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsMappingErogazionePortaApplicativa";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.existsMappingErogazionePortaApplicativa(mapping);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public void aggiornaDescrizioneMappingErogazionePortaApplicativa(MappingErogazionePortaApplicativa mapping) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "aggiornaDescrizioneMappingErogazionePortaApplicativa";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			DBMappingUtils.updateMappingErogazione(mapping.getTableId(), mapping.getDescrizione(), con, this.tipoDB);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public MappingErogazionePortaApplicativa getMappingErogazionePortaApplicativa(PortaApplicativa pa) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getMappingErogazionePortaApplicativa";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(pa.getNome());
			
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
					pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), 
					pa.getServizio().getVersione());
			
			return DBMappingUtils.getMappingErogazione(idServizio, idPA, con, this.tipoDB);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<MappingErogazionePortaApplicativa> countMappingErogazionePortaApplicativa(IDServizio idServizio) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "countMappingErogazionePortaApplicativa";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			return DBMappingUtils.mappingErogazionePortaApplicativaList(con, this.tipoDB, idServizio);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public String getLabelRegolaMappingErogazionePortaApplicativa(String functionDi, String function, PortaApplicativa pa) throws DriverConfigurazioneException {
		return getLabelRegolaMappingErogazionePortaApplicativa(functionDi, function, pa, 50, false);
	}
	public String getLabelRegolaMappingErogazionePortaApplicativa(String functionDi, String function, PortaApplicativa pa, boolean forceGroupName) throws DriverConfigurazioneException {
		return getLabelRegolaMappingErogazionePortaApplicativa(functionDi, function, pa, 50, forceGroupName);
	}
	public String getLabelRegolaMappingErogazionePortaApplicativa(String functionDi, String function, PortaApplicativa pa, int sizeSubstring) throws DriverConfigurazioneException {
		return getLabelRegolaMappingErogazionePortaApplicativa(functionDi, function, pa, sizeSubstring, false);
	}
	public String getLabelRegolaMappingErogazionePortaApplicativa(String functionDi, String function, PortaApplicativa pa, int sizeSubstring, boolean forceGroupName) throws DriverConfigurazioneException {
		
		boolean showGroup = true;
		
		String prefix = "";
		if(functionDi!=null) {
			prefix = functionDi;
		}
		
		MappingErogazionePortaApplicativa mapping = this.getMappingErogazionePortaApplicativa(pa);
				
		if(mapping.isDefault()) {
			if(this.countMappingErogazionePortaApplicativa(mapping.getIdServizio()).size()>1 || forceGroupName) {
				if(showGroup) {
					return prefix+getLabelGroup(mapping.getDescrizione());
				}
				else {
					return prefix+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_NOME_DEFAULT;
				}
				//return "(*)";
			}
			else {
				return function!=null ? function : PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_NOME_DEFAULT;
			}
		}
		else {
			if(showGroup) {
				StringBuffer sb = new StringBuffer(mapping.getDescrizione());
				if(sb.length()>sizeSubstring)
					return prefix+getLabelGroup(sb.toString().substring(0, (sizeSubstring-3))+"...");
				else 
					return prefix+getLabelGroup(sb.toString());
			}
			else {
				//return mapping.getNome();
				List<String> listaAzioni = pa.getAzione()!= null ?  pa.getAzione().getAzioneDelegataList() : new ArrayList<String>();
				if(listaAzioni.size() > 0) {
					StringBuffer sb = new StringBuffer();
					for (String string : listaAzioni) {
						if(sb.length() >0)
							sb.append(", ");
						
						sb.append(string);
					}
					if(sb.length()>sizeSubstring)
						return prefix+sb.toString().substring(0, (sizeSubstring-3))+"...";
					else 
						return prefix+sb.toString();
				}
				else {
					return prefix+"???";
				}
			}
		}
	}
	
	public List<IDPortaApplicativa> getAllIdPorteApplicative(FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getAllIdPorteApplicative";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getAllIdPorteApplicative(filtroRicerca);

		} catch (DriverConfigurazioneNotFound notFound) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + notFound.getMessage(), notFound);
			return new ArrayList<IDPortaApplicativa>();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<String> portaApplicativaRuoliList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "portaApplicativaRuoliList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().portaApplicativaRuoliList(idPA, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<String> portaApplicativaScopeList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "portaApplicativaScopeList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().portaApplicativaScopeList(idPA, ricerca);

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
			Map<String, String> readProperties = DBPropertiesUtils.readProperties(con, this.tipoDB, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME, 
					CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_ID_PORTA, idPortaDelegata);
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
			Map<String, String> readProperties = DBPropertiesUtils.readProperties(con, this.tipoDB, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME, 
					CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_ID_PORTA, idPortaDelegata);
			return DBPropertiesUtils.toMultiMap(readProperties);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}	
	}
}
