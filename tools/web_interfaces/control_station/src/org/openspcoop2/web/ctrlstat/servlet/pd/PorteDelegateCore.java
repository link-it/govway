/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
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
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
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
			String fruizioneAutenticazione, String fruizioneAutenticazioneOpzionale, TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal, List<String> fruizioneAutenticazioneParametroList,
			String fruizioneAutorizzazione, String fruizioneAutorizzazioneAutenticati, String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			String fruizioneServizioApplicativo, String fruizioneRuolo, 
			String fruizioneAutorizzazione_tokenOptions,
			String fruizioneAutorizzazioneScope, String fruizioneScope, String fruizioneAutorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy) {
		
		portaDelegata.setAutenticazione(fruizioneAutenticazione);
		if(fruizioneAutenticazioneOpzionale != null){
			if(ServletUtils.isCheckBoxEnabled(fruizioneAutenticazioneOpzionale))
				portaDelegata.setAutenticazioneOpzionale(org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO);
			else 
				portaDelegata.setAutenticazioneOpzionale(org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO);
		} else 
			portaDelegata.setAutenticazioneOpzionale(null);
		portaDelegata.getProprietaAutenticazioneList().clear();
		List<Proprieta> proprietaAutenticazione = this.convertToAutenticazioneProprieta(fruizioneAutenticazione, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList);
		if(proprietaAutenticazione!=null && !proprietaAutenticazione.isEmpty()) {
			portaDelegata.getProprietaAutenticazioneList().addAll(proprietaAutenticazione);
		}
		
		portaDelegata.setAutorizzazione(AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(fruizioneAutorizzazione, 
				ServletUtils.isCheckBoxEnabled(fruizioneAutorizzazioneAutenticati), 
				ServletUtils.isCheckBoxEnabled(fruizioneAutorizzazioneRuoli),
				ServletUtils.isCheckBoxEnabled(fruizioneAutorizzazioneScope),
				fruizioneAutorizzazione_tokenOptions,
				RuoloTipologia.toEnumConstant(fruizioneAutorizzazioneRuoliTipologia)));
		
		if(fruizioneAutorizzazione != null && fruizioneAutorizzazione.equals(AutorizzazioneUtilities.STATO_XACML_POLICY) && allegatoXacmlPolicy.getValue() != null) {
			portaDelegata.setXacmlPolicy(new String(allegatoXacmlPolicy.getValue()));
		} else {
			portaDelegata.setXacmlPolicy(null);
		}
		
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
			if(ServletUtils.isCheckBoxEnabled(gestioneTokenOpzionale)) {
				portaDelegata.getGestioneToken().setTokenOpzionale(StatoFunzionalita.ABILITATO);
			}
			else {
				portaDelegata.getGestioneToken().setTokenOpzionale(StatoFunzionalita.DISABILITATO);
			}
			portaDelegata.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenValidazioneInput));
			portaDelegata.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenIntrospection));
			portaDelegata.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenUserInfo));
			portaDelegata.getGestioneToken().setForward(StatoFunzionalita.toEnumConstant(gestioneTokenForward)); 
			portaDelegata.getGestioneToken().setOptions(autorizzazione_tokenOptions);
			if(portaDelegata.getGestioneToken().getAutenticazione()==null) {
				portaDelegata.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
			}
			portaDelegata.getGestioneToken().getAutenticazione().setIssuer(ServletUtils.isCheckBoxEnabled(autenticazioneTokenIssuer) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenIssuer)); 
			portaDelegata.getGestioneToken().getAutenticazione().setClientId(ServletUtils.isCheckBoxEnabled(autenticazioneTokenClientId) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenClientId)); 
			portaDelegata.getGestioneToken().getAutenticazione().setSubject(ServletUtils.isCheckBoxEnabled(autenticazioneTokenSubject) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenSubject)); 
			portaDelegata.getGestioneToken().getAutenticazione().setUsername(ServletUtils.isCheckBoxEnabled(autenticazioneTokenUsername) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenUsername)); 
			portaDelegata.getGestioneToken().getAutenticazione().setEmail(ServletUtils.isCheckBoxEnabled(autenticazioneTokenEMail) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenEMail)); 
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
	
	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityRequestList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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

	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityResponseList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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
	
	public List<MappingFruizionePortaDelegata> getMappingConGruppiPerAzione(String nomeAzione, List<IDServizio> list) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getMappingConGruppiPerAzione";
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			List<MappingFruizionePortaDelegata> listInUtilizzo = new ArrayList<>();
			
			if(list!=null && !list.isEmpty()) {
				for (IDServizio idServizio : list) {
					
					AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(this);
					Long idS = aspsCore.getIdAccordoServizioParteSpecifica(idServizio);
					Search s = new Search(true);
					List<Fruitore> listFruitori = aspsCore.serviziFruitoriList(idS, s);
					if(listFruitori!=null && !listFruitori.isEmpty()) {
					
						for (Fruitore fruitore : listFruitori) {
							IDSoggetto idSoggettoFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
							List<MappingFruizionePortaDelegata> lPD = DBMappingUtils.mappingFruizionePortaDelegataList(con, this.tipoDB, idSoggettoFruitore, idServizio);
							if(lPD!=null && lPD.size()>0) {
								for (MappingFruizionePortaDelegata mapping : lPD) {
									try {
										PortaDelegata pd = this.getPortaDelegata(mapping.getIdPortaDelegata());
										if(pd!=null && pd.getAzione()!=null && pd.getAzione().getAzioneDelegataList()!=null &&
												pd.getAzione().getAzioneDelegataList().contains(nomeAzione)) {
											listInUtilizzo.add(mapping);
										}
									}catch(DriverConfigurazioneNotFound notFound) {}
								}
							}
						}
						
					}
				}
			}
				
			return listInUtilizzo;

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<MappingFruizionePortaDelegata> getMapping(List<IDServizio> list, boolean addDefault, boolean addNotDefault) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getMapping";
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			List<MappingFruizionePortaDelegata> listMappingDefault = new ArrayList<>();
			
			if(list!=null && !list.isEmpty()) {
				for (IDServizio idServizio : list) {
					
					AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(this);
					Long idS = aspsCore.getIdAccordoServizioParteSpecifica(idServizio);
					Search s = new Search(true);
					List<Fruitore> listFruitori = aspsCore.serviziFruitoriList(idS, s);
					if(listFruitori!=null && !listFruitori.isEmpty()) {
					
						for (Fruitore fruitore : listFruitori) {
							IDSoggetto idSoggettoFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
							List<MappingFruizionePortaDelegata> lPD = DBMappingUtils.mappingFruizionePortaDelegataList(con, this.tipoDB, idSoggettoFruitore, idServizio);
							if(lPD!=null && lPD.size()>0) {
								for (MappingFruizionePortaDelegata mapping : lPD) {
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
				}
			}
			
			return listMappingDefault;

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

	public List<PortaDelegata> porteDelegateList(long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
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

	public List<ServizioApplicativo> porteDelegateServizioApplicativoList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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
	
	public List<CorrelazioneApplicativaElemento> porteDelegateCorrelazioneApplicativaList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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
	
	public List<CorrelazioneApplicativaRispostaElemento> porteDelegateCorrelazioneApplicativaRispostaList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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
	
	public List<MtomProcessorFlowParameter> porteDelegateMTOMRequestList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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

	public List<MtomProcessorFlowParameter> porteDelegateMTOMResponseList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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
	
	public void aggiornaDescrizioneMappingFruizionePortaDelegata(MappingFruizionePortaDelegata mapping) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "aggiornaDescrizioneMappingFruizionePortaDelegata";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			DBMappingUtils.updateMappingFruizione(mapping.getTableId(), mapping.getDescrizione(), con, this.tipoDB);

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
	public List<MappingFruizionePortaDelegata> countMappingFruizionePortaDelegata(IDSoggetto idFruitore, IDServizio idServizio) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "countMappingFruizionePortaDelegata";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			return DBMappingUtils.mappingFruizionePortaDelegataList(con, this.tipoDB, idFruitore, idServizio);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public String getLabelRegolaMappingFruizionePortaDelegata(String functionDi, String function, PortaDelegata pd) throws DriverConfigurazioneException {
		return getLabelRegolaMappingFruizionePortaDelegata(functionDi, function, pd, 50);
	}
	public String getLabelRegolaMappingFruizionePortaDelegata(String functionDi, String function, PortaDelegata pd, boolean forceGroupName) throws DriverConfigurazioneException {
		return getLabelRegolaMappingFruizionePortaDelegata(functionDi, function, pd, 50, forceGroupName);
	}
	public String getLabelRegolaMappingFruizionePortaDelegata(String functionDi, String function, PortaDelegata pd, int sizeSubstring) throws DriverConfigurazioneException {
		return getLabelRegolaMappingFruizionePortaDelegata(functionDi, function, pd, sizeSubstring, false);
	}
	public String getLabelRegolaMappingFruizionePortaDelegata(String functionDi, String function, PortaDelegata pd, int sizeSubstring, boolean forceGroupName) throws DriverConfigurazioneException {
		
		boolean showGroup = true;
		
		String prefix = "";
		if(functionDi!=null) {
			prefix = functionDi;
			if(showGroup) {
				prefix = convertPrefixConfigDelGruppo(prefix);
			}
		}
		
		MappingFruizionePortaDelegata mapping = this.getMappingFruizionePortaDelegata(pd);
		if(mapping.isDefault()) {
			if(this.countMappingFruizionePortaDelegata(mapping.getIdFruitore(),mapping.getIdServizio()).size()>1 || forceGroupName) {
				if(showGroup) {
					return prefix+getLabelGroup(mapping.getDescrizione());
				}
				else {
					return prefix+PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_NOME_DEFAULT;
				}
				//return "(*)";
			}
			else {
				return function!=null ? function : PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_NOME_DEFAULT;
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
				List<String> listaAzioni = pd.getAzione()!= null ?  pd.getAzione().getAzioneDelegataList() : new ArrayList<String>();
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
	public List<Proprieta> porteDelPropList(long idPortaDelegata, Search ricerca) throws DriverConfigurazioneException {
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
	
	public List<ResponseCachingConfigurazioneRegola> getResponseCachingConfigurazioneRegolaList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getResponseCachingConfigurazioneRegolaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().portaDelegataResponseCachingConfigurazioneRegolaList(idPA, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public boolean existsResponseCachingConfigurazioneRegola(Long idPorta, Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsResponseCachingConfigurazioneRegola";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsPortaDelegataResponseCachingConfigurazioneRegola(idPorta, statusMin,statusMax,fault);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TrasformazioneRegola> porteDelegateTrasformazioniList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateTrasformazioniList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateTrasformazioniList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public boolean existsTrasformazione(long idPorta, String azioni, String pattern, String contentType) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsTrasformazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsPortaDelegataTrasformazione(idPorta, azioni, pattern, contentType);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().existsPortaDelegataTrasformazione(idPorta, nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public TrasformazioneRegola getTrasformazione(long idPorta, String azioni, String pattern, String contentType) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "getTrasformazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPortaDelegataTrasformazione(idPorta, azioni, pattern, contentType);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().getPortaDelegataTrasformazione(idPorta, nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TrasformazioneRegolaRisposta> porteDelegateTrasformazioniRispostaList(long idPortaDelegata, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateTrasformazioniRispostaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateTrasformazioniRispostaList(idPortaDelegata, idTrasformazione, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().existsPortaDelegataTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().existsPortaDelegataTrasformazioneRisposta(idPorta, idTrasformazione, nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().getPortaDelegataTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().getPortaDelegataTrasformazioneRisposta(idPorta, idTrasformazione, nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRispostaHeaderList(long idPortaDelegata, long idTrasformazione, long idTrasformazioneRisposta,  ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateTrasformazioniRispostaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateTrasformazioniRispostaHeaderList(idPortaDelegata, idTrasformazione, idTrasformazioneRisposta, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().existsPortaDelegataTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().getPortaDelegataTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRichiestaHeaderList(long idPortaDelegata, long idTrasformazione,  ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateTrasformazioniRichiestaHeaderList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateTrasformazioniRichiestaHeaderList(idPortaDelegata, idTrasformazione, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRichiestaUrlParameterList(long idPortaDelegata, long idTrasformazione,  ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateTrasformazioniRichiestaUrlParameterList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateTrasformazioniRichiestaUrlParameterList(idPortaDelegata, idTrasformazione, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().existsPortaDelegataTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().getPortaDelegataTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().existsPortaDelegataTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			return driver.getDriverConfigurazioneDB().getPortaDelegataTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
}
