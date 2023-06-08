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
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneToken;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.AzioneSintetica;
import org.openspcoop2.core.registry.beans.OperationSintetica;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
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

	public PorteApplicativeCore() throws DriverControlStationException {
		super();
	}
	public PorteApplicativeCore(ControlStationCore core) throws DriverControlStationException {
		super(core);
	}
	
	public void configureControlloAccessiPortaApplicativa(PortaApplicativa pa,
			String erogazioneAutenticazione, String erogazioneAutenticazioneOpzionale, TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal, List<String> erogazioneAutenticazioneParametroList,
			String erogazioneAutorizzazione, String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,
			String nomeSA, String erogazioneRuolo, IDSoggetto idErogazioneSoggettoAutenticato, 
			String autorizzazioneAutenticatiToken, 
			String autorizzazioneRuoliToken, String autorizzazioneRuoliTipologiaToken, String autorizzazioneRuoliMatchToken,
			String erogazioneAutorizzazioneTokenOptions,
			String erogazioneAutorizzazioneScope, String erogazioneScope, String erogazioneAutorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			String identificazioneAttributiStato, String [] attributeAuthoritySelezionate, String attributeAuthorityAttributi) {
		pa.setAutenticazione(erogazioneAutenticazione);
		if(erogazioneAutenticazioneOpzionale != null){
			if(ServletUtils.isCheckBoxEnabled(erogazioneAutenticazioneOpzionale))
				pa.setAutenticazioneOpzionale(StatoFunzionalita.ABILITATO);
			else 
				pa.setAutenticazioneOpzionale(StatoFunzionalita.DISABILITATO);
		} else 
			pa.setAutenticazioneOpzionale(null);
		pa.getProprietaAutenticazioneList().clear();
		List<Proprieta> proprietaAutenticazione = this.convertToAutenticazioneProprieta(erogazioneAutenticazione, erogazioneAutenticazionePrincipal, erogazioneAutenticazioneParametroList);
		if(proprietaAutenticazione!=null && !proprietaAutenticazione.isEmpty()) {
			pa.getProprietaAutenticazioneList().addAll(proprietaAutenticazione);
		}
		
		pa.setAutorizzazione(AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(erogazioneAutorizzazione, 
				ServletUtils.isCheckBoxEnabled(erogazioneAutorizzazioneAutenticati), 
				ServletUtils.isCheckBoxEnabled(erogazioneAutorizzazioneRuoli),
				ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticatiToken), 
				ServletUtils.isCheckBoxEnabled(autorizzazioneRuoliToken),
				ServletUtils.isCheckBoxEnabled(erogazioneAutorizzazioneScope),
				erogazioneAutorizzazioneTokenOptions,
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
		
		// token applicativi
		if(ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticatiToken)) {
			if(pa.getAutorizzazioneToken()==null) {
				pa.setAutorizzazioneToken(new PortaApplicativaAutorizzazioneToken() );
			}
			pa.getAutorizzazioneToken().setAutorizzazioneApplicativi(StatoFunzionalita.ABILITATO); 
		}
		else {
			if(pa.getAutorizzazioneToken()!=null) {
				pa.getAutorizzazioneToken().setAutorizzazioneApplicativi(null);
				pa.getAutorizzazioneToken().setServiziApplicativi(null);
			}
		}
		
		// token ruoli
		if(ServletUtils.isCheckBoxEnabled(autorizzazioneRuoliToken)) {
			if(pa.getAutorizzazioneToken()==null) {
				pa.setAutorizzazioneToken(new PortaApplicativaAutorizzazioneToken() );
			}
			pa.getAutorizzazioneToken().setAutorizzazioneRuoli(StatoFunzionalita.ABILITATO); 
			
			if(autorizzazioneRuoliTipologiaToken!=null && !"".equals(autorizzazioneRuoliTipologiaToken)){
				org.openspcoop2.core.config.constants.RuoloTipologia ruoloTipologia = org.openspcoop2.core.config.constants.RuoloTipologia.toEnumConstant(autorizzazioneRuoliTipologiaToken);
				pa.getAutorizzazioneToken().setTipologiaRuoli(ruoloTipologia);
			}
			else {
				pa.getAutorizzazioneToken().setTipologiaRuoli(null);
			}
			
			if(autorizzazioneRuoliMatchToken!=null && !"".equals(autorizzazioneRuoliMatchToken)){
				RuoloTipoMatch ruoloTipoMatch = RuoloTipoMatch.toEnumConstant(autorizzazioneRuoliMatchToken);
				if(ruoloTipoMatch!=null){
					if(pa.getAutorizzazioneToken().getRuoli()==null){
						pa.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
					}
					pa.getAutorizzazioneToken().getRuoli().setMatch(ruoloTipoMatch);
				}
				else {
					if(pa.getAutorizzazioneToken().getRuoli()!=null){
						pa.getAutorizzazioneToken().getRuoli().setMatch(null);
					}
				}
			}
			else {
				if(pa.getAutorizzazioneToken().getRuoli()!=null){
					pa.getAutorizzazioneToken().getRuoli().setMatch(null);
				}
			}
		}
		else {
			if(pa.getAutorizzazioneToken()!=null) {
				pa.getAutorizzazioneToken().setAutorizzazioneRuoli(null);
				pa.getAutorizzazioneToken().setTipologiaRuoli(null);
				pa.getAutorizzazioneToken().setRuoli(null);
			}
		}
		
		// token options
		// impostato in configureControlloAccessiGestioneToken 
		
		// scope
		if(ServletUtils.isCheckBoxEnabled(erogazioneAutorizzazioneScope)) {
			if(pa.getScope() == null)
				pa.setScope(new AutorizzazioneScope());
			
			pa.getScope().setStato(StatoFunzionalita.ABILITATO); 
		}
		else {
			pa.setScope(null);
		}
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
		
		// attribute authority
		while (pa.sizeAttributeAuthorityList()>0) {
			pa.removeAttributeAuthority(0);
		}
		if(StatoFunzionalita.ABILITATO.getValue().equals(identificazioneAttributiStato) && attributeAuthoritySelezionate!=null && attributeAuthoritySelezionate.length>0) {
			for (String aaName : attributeAuthoritySelezionate) {
				pa.addAttributeAuthority(this.buildAttributeAuthority(attributeAuthoritySelezionate.length, aaName, attributeAuthorityAttributi));
			}
		}
	}
	
	public void configureControlloAccessiGestioneToken (PortaApplicativa portaApplicativa, String gestioneToken, 
			String gestioneTokenPolicy,  String gestioneTokenOpzionale,  
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			String autorizzazioneTokenOptions) {
		if(portaApplicativa.getGestioneToken() == null)
			portaApplicativa.setGestioneToken(new GestioneToken());
		
		if(gestioneToken!=null && gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) {
			portaApplicativa.getGestioneToken().setPolicy(gestioneTokenPolicy);
			if(ServletUtils.isCheckBoxEnabled(gestioneTokenOpzionale)) {
				portaApplicativa.getGestioneToken().setTokenOpzionale(StatoFunzionalita.ABILITATO);
			}
			else {
				portaApplicativa.getGestioneToken().setTokenOpzionale(StatoFunzionalita.DISABILITATO);
			}
			portaApplicativa.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenValidazioneInput));
			portaApplicativa.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenIntrospection));
			portaApplicativa.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenUserInfo));
			portaApplicativa.getGestioneToken().setForward(StatoFunzionalita.toEnumConstant(gestioneTokenForward)); 	
			portaApplicativa.getGestioneToken().setOptions(autorizzazioneTokenOptions);
			if(portaApplicativa.getGestioneToken().getAutenticazione()==null) {
				portaApplicativa.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
			}
			portaApplicativa.getGestioneToken().getAutenticazione().setIssuer(ServletUtils.isCheckBoxEnabled(autenticazioneTokenIssuer) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenIssuer)); 
			portaApplicativa.getGestioneToken().getAutenticazione().setClientId(ServletUtils.isCheckBoxEnabled(autenticazioneTokenClientId) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenClientId)); 
			portaApplicativa.getGestioneToken().getAutenticazione().setSubject(ServletUtils.isCheckBoxEnabled(autenticazioneTokenSubject) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenSubject)); 
			portaApplicativa.getGestioneToken().getAutenticazione().setUsername(ServletUtils.isCheckBoxEnabled(autenticazioneTokenUsername) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenUsername)); 
			portaApplicativa.getGestioneToken().getAutenticazione().setEmail(ServletUtils.isCheckBoxEnabled(autenticazioneTokenEMail) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenEMail)); 
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
	
	
	public List<PortaApplicativa> porteAppWithServizio(long idSoggettoErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	

	public List<PortaApplicativa> porteAppList(long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e));
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	

	public List<Proprieta> porteAppPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<String> nomiProprietaPA(String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoServiziProtocollo) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "nomiProprietaPA";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().nomiProprietaPA(filterSoggettoTipo, filterSoggettoNome, tipoServiziProtocollo);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<ServizioApplicativo> porteAppServizioApplicativoList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<PortaApplicativaAutorizzazioneSoggetto> porteAppSoggettoList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<PortaApplicativaAutorizzazioneServizioApplicativo> porteAppServiziApplicativiAutorizzatiList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppServiziApplicativiAutorizzatiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppServiziApplicativiAutorizzatiList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<PortaApplicativaAutorizzazioneServizioApplicativo> porteAppServiziApplicativiAutorizzatiTokenList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppServiziApplicativiAutorizzatiTokenList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteAppServiziApplicativiAutorizzatiTokenList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<MessageSecurityFlowParameter> porteAppMessageSecurityRequestList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<MessageSecurityFlowParameter> porteAppMessageSecurityResponseList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<CorrelazioneApplicativaElemento> porteApplicativeCorrelazioneApplicativaList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<CorrelazioneApplicativaRispostaElemento> porteApplicativeCorrelazioneApplicativaRispostaList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e));
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<PortaApplicativaAzione> porteAppAzioneList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			
			return list!=null && !list.isEmpty();
		} catch(DriverConfigurazioneNotFound nfe) {
			return false;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			
			return list!=null && !list.isEmpty();

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean azioneUsataInTrasformazioniPortaApplicativa(String nome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "azioneUsataInTrasformazioniPortaApplicativa";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().azioneUsataInTrasformazioniPortaApplicativa(nome);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<MappingErogazionePortaApplicativa> getMappingConGruppiPerAzione(String nomeAzione, List<IDServizio> list) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getMappingConGruppiPerAzione";
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			List<MappingErogazionePortaApplicativa> listInUtilizzo = new ArrayList<>();
			
			if(list!=null && !list.isEmpty()) {
				for (IDServizio idServizio : list) {
					List<MappingErogazionePortaApplicativa> lPA = DBMappingUtils.mappingErogazionePortaApplicativaList(con, this.tipoDB, idServizio, false);
					if(lPA!=null && !lPA.isEmpty()) {
						for (MappingErogazionePortaApplicativa mapping : lPA) {
							try {
								PortaApplicativa pa = this.getPortaApplicativa(mapping.getIdPortaApplicativa());
								if(pa!=null && pa.getAzione()!=null && pa.getAzione().getAzioneDelegataList()!=null &&
										pa.getAzione().getAzioneDelegataList().contains(nomeAzione)) {
									listInUtilizzo.add(mapping);
								}
							}catch(DriverConfigurazioneNotFound notFound) {
								// ignore
							}
						}
					}
				}
			}
			
			return listInUtilizzo;

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<MappingErogazionePortaApplicativa> getMapping(List<IDServizio> list, boolean addDefault, boolean addNotDefault) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getMapping";
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			List<MappingErogazionePortaApplicativa> listMappingDefault = new ArrayList<>();
			
			if(list!=null && !list.isEmpty()) {
				for (IDServizio idServizio : list) {
					List<MappingErogazionePortaApplicativa> lPA = DBMappingUtils.mappingErogazionePortaApplicativaList(con, this.tipoDB, idServizio, false);
					if(lPA!=null && !lPA.isEmpty()) {
						for (MappingErogazionePortaApplicativa mapping : lPA) {
							if(mapping.isDefault()) {
								if(addDefault) {
									listMappingDefault.add(mapping);
								}
							}
							else {
								if(addNotDefault) {
									listMappingDefault.add(mapping);
								}
							}
						}
					}
				}
			}
			
			return listMappingDefault;

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logDebug("[ControlStationCore::" + nomeMetodo + "] Exception :" + dNotF.getMessage(), dNotF);
			throw dNotF;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<MtomProcessorFlowParameter> porteApplicativeMTOMRequestList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<MtomProcessorFlowParameter> porteApplicativeMTOMResponseList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			
			return DBMappingUtils.mappingErogazionePortaApplicativaList(con, this.tipoDB, idServizio, false);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	private static boolean showGroup = true;
	public static boolean isShowGroup() {
		return showGroup;
	}
	public static void setShowGroup(boolean showGroup) {
		PorteApplicativeCore.showGroup = showGroup;
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
		
		String prefix = "";
		if(functionDi!=null) {
			prefix = functionDi;
			if(showGroup) {
				prefix = convertPrefixConfigDelGruppo(prefix);
			}
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
			}
			else {
				return function!=null ? function : PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_NOME_DEFAULT;
			}
		}
		else {
			if(showGroup) {
				StringBuilder sb = new StringBuilder(mapping.getDescrizione());
				if(sb.length()>sizeSubstring)
					return prefix+getLabelGroup(sb.toString().substring(0, (sizeSubstring-3))+"...");
				else 
					return prefix+getLabelGroup(sb.toString());
			}
			else {
				List<String> listaAzioni = pa.getAzione()!= null ?  pa.getAzione().getAzioneDelegataList() : new ArrayList<>();
				if(!listaAzioni.isEmpty()) {
					StringBuilder sb = new StringBuilder();
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
			ControlStationCore.logDebug("[ControlStationCore::" + nomeMetodo + "] Exception :" + notFound.getMessage(), notFound);
			return new ArrayList<>();
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<String> portaApplicativaRuoliTokenList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "portaApplicativaRuoliTokenList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().portaApplicativaRuoliTokenList(idPA, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
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
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}	
	}
	
	
	public List<ResponseCachingConfigurazioneRegola> getResponseCachingConfigurazioneRegolaList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getResponseCachingConfigurazioneRegolaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().portaApplicativaResponseCachingConfigurazioneRegolaList(idPA, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public boolean existsResponseCachingConfigurazioneRegola(long idPorta, Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsResponseCachingConfigurazioneRegola";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsPortaApplicativaResponseCachingConfigurazioneRegola(idPorta, statusMin,statusMax,fault);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public List<TrasformazioneRegola> porteAppTrasformazioniList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppTrasformazioniList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeTrasformazioniList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public boolean existsTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsTrasformazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsPortaApplicativaTrasformazione(idPorta, azioni, pattern, contentType, connettori);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsTrasformazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsPortaApplicativaTrasformazione(idPorta, nome);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public TrasformazioneRegola getTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori,
			List<TrasformazioneRegolaApplicabilitaSoggetto> soggetti,
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicativi,
			boolean interpretaNullList) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "getTrasformazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPortaApplicativaTrasformazione(idPorta, azioni, pattern, contentType, connettori, soggetti, applicativi, interpretaNullList);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public TrasformazioneRegola getTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "getTrasformazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPortaApplicativaTrasformazione(idPorta, nome);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TrasformazioneRegolaRisposta> porteAppTrasformazioniRispostaList(long idPortaApplicativa, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppTrasformazioniRispostaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeTrasformazioniRispostaList(idPortaApplicativa, idTrasformazione, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsTrasformazioneRisposta";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsPortaApplicativaTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsTrasformazioneRisposta";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsPortaApplicativaTrasformazioneRisposta(idPorta, idTrasformazione, nome);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public TrasformazioneRegolaRisposta getTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "getTrasformazioneRisposta";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPortaApplicativaTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public TrasformazioneRegolaRisposta getTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "getTrasformazioneRisposta";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPortaApplicativaTrasformazioneRisposta(idPorta, idTrasformazione, nome);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TrasformazioneRegolaParametro> porteAppTrasformazioniRispostaHeaderList(long idPortaApplicativa, long idTrasformazione, long idTrasformazioneRisposta,  ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppTrasformazioniRispostaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeTrasformazioniRispostaHeaderList(idPortaApplicativa, idTrasformazione, idTrasformazioneRisposta, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsTrasformazioneRispostaHeader(long idPorta, long idTrasformazione,  long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsTrasformazioneRispostaHeader";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsPortaApplicativaTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public TrasformazioneRegolaParametro getTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "getTrasformazioneRispostaHeader";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPortaApplicativaTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TrasformazioneRegolaParametro> porteAppTrasformazioniRichiestaHeaderList(long idPortaApplicativa, long idTrasformazione,  ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppTrasformazioniRichiestaHeaderList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeTrasformazioniRichiestaHeaderList(idPortaApplicativa, idTrasformazione, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TrasformazioneRegolaParametro> porteAppTrasformazioniRichiestaUrlParameterList(long idPortaApplicativa, long idTrasformazione,  ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppTrasformazioniRichiestaUrlParameterList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeTrasformazioniRichiestaUrlParameterList(idPortaApplicativa, idTrasformazione, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsTrasformazioneRichiestaHeader";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsPortaApplicativaTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public TrasformazioneRegolaParametro getTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "getTrasformazioneRichiestaHeader";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPortaApplicativaTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsTrasformazioneRichiestaUrlParameter";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsPortaApplicativaTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public TrasformazioneRegolaParametro getTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "getTrasformazioneRichiestaUrlParameter";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPortaApplicativaTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TrasformazioneRegolaApplicabilitaSoggetto> porteAppTrasformazioniSoggettoList(long idPortaApplicativa, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppTrasformazioniSoggettoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeTrasformazioniSoggettiList(idPortaApplicativa, idTrasformazione, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<TrasformazioneRegolaApplicabilitaServizioApplicativo> porteAppTrasformazioniServiziApplicativiAutorizzatiList(long idPortaApplicativa, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteAppTrasformazioniServiziApplicativiAutorizzatiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeTrasformazioniServiziApplicativiList(idPortaApplicativa, idTrasformazione, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<Proprieta> porteApplicativeAutenticazioneCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteApplicativeAutenticazioneCustomPropList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeAutenticazioneCustomPropList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<Proprieta> porteApplicativeAutorizzazioneCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteApplicativeAutorizzazioneCustomPropList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeAutorizzazioneCustomPropList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<Proprieta> porteApplicativeAutorizzazioneContenutoCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteApplicativeAutorizzazioneContenutoCustomPropList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeAutorizzazioneContenutoCustomPropList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<Proprieta> porteApplicativeConnettoriMultipliConfigPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteApplicativeConnettoriMultipliConfigPropList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeConnettoriMultipliConfigPropList(idPortaApplicativa, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<Proprieta> porteApplicativeConnettoriMultipliPropList(long idPaSa, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteApplicativeConnettoriMultipliPropList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeConnettoriMultipliPropList(idPaSa, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDPortaApplicativa> porteApplicativeWithApplicativoErogatore(IDServizioApplicativo idSA) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteApplicativeWithApplicativoErogatore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteApplicativeWithApplicativoErogatore(idSA);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean azioniTutteOneway(AccordoServizioParteSpecifica asps, AccordoServizioParteComuneSintetico as, List<String> azioniDaControllare) {
		String profiloCollaborazioneAccordo = as.getProfiloCollaborazione().toString();
		if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
			profiloCollaborazioneAccordo = "oneway";
		} else if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.SINCRONO.getValue())) {
			profiloCollaborazioneAccordo = "sincrono";
		} else if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO.getValue())) {
			profiloCollaborazioneAccordo = "asincronoSimmetrico";
		} else if (profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.getValue())) {
			profiloCollaborazioneAccordo = "asincronoAsimmetrico";
		}
		
		for (String nomeAzione : azioniDaControllare) {
			boolean isProfiloOneWay = true;
			// recupero profilo collaborazione azione se l'azione e'
			// specificata e il profilo azione e' ridefinito
			String profiloCollaborazioneAzione = "";
			if (nomeAzione != null && !nomeAzione.equals("")) {
				if(asps.getPortType()!=null){
					for (PortTypeSintetico pt : as.getPortType()) {
						if(pt.getNome().equals(asps.getPortType())){
							for (OperationSintetica op : pt.getAzione()) {
								if(op.getNome().equals(nomeAzione)){
									if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(op.getProfAzione())){
										if(op.getProfiloCollaborazione()!=null)
											profiloCollaborazioneAzione = op.getProfiloCollaborazione().toString();
									}
									else{
										if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT()) &&
											pt.getProfiloCollaborazione()!=null) {
											profiloCollaborazioneAzione = pt.getProfiloCollaborazione().toString();
										}
									}
								}
							}
						}
					}
				}
				else{
					for (int i = 0; i < as.getAzione().size(); i++) {
						AzioneSintetica tmpAz = as.getAzione().get(i);
						if (tmpAz.getProfAzione().equals(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO)) {
							if(tmpAz.getProfiloCollaborazione()!=null)
								profiloCollaborazioneAzione = tmpAz.getProfiloCollaborazione().toString();
							break;
						}
					}
				}
			}

			// Controllo se nomeAzione e' specificato allora e' possibile
			// che il profilo azione sia ridefinito
			// quindi devo controllare se e' diverso da oneway
			if (nomeAzione != null && !nomeAzione.equals("")) {
				// se e' diverso da oneway posso avere solo un azione
				if ( profiloCollaborazioneAzione != null && !profiloCollaborazioneAzione.equals("") ){
					if (!profiloCollaborazioneAzione.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
						isProfiloOneWay = false;
					}
				}
				else{
					if (!profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
						isProfiloOneWay = false;
					}	
				}
			} else {
				// non ho azione (o azione con profilo non ridefinito)
				// allora considero il profilo dell'accordo
				if (!profiloCollaborazioneAccordo.equals(CostantiRegistroServizi.ONEWAY.getValue())) {
					isProfiloOneWay = false;
				}
			}
			
			if(!isProfiloOneWay)
				return false;
		}
		
		
		return true;
	}
}
