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

package org.openspcoop2.protocol.engine.archive;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneGestioneErrore;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.config.RegistroPlugins;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.utils.ConfigurazionePdDUtils;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.utils.ControlloTrafficoDriverUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**
 *  AbstractArchiveEngine
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractArchiveEngine {

	private DriverRegistroServiziDB driverRegistroServizi;
	public DriverRegistroServiziDB getDriverRegistroServizi() {
		return this.driverRegistroServizi;
	}

	private DriverConfigurazioneDB driverConfigurazione;
	public DriverConfigurazioneDB getDriverConfigurazione() {
		return this.driverConfigurazione;
	}

	private org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager serviceManagerPlugins;
	public org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager getServiceManagerPlugins() {
		return this.serviceManagerPlugins;
	}
	
	private org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManagerControlloTraffico;
	public org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager getServiceManagerControlloTraffico() {
		return this.serviceManagerControlloTraffico;
	}
	
	private org.openspcoop2.core.allarmi.dao.jdbc.JDBCServiceManager serviceManagerAllarmi;
	public org.openspcoop2.core.allarmi.dao.jdbc.JDBCServiceManager getServiceManagerAllarmi() {
		return this.serviceManagerAllarmi;
	}

	public AbstractArchiveEngine(DriverRegistroServiziDB driverRegistroServizi,DriverConfigurazioneDB driverConfigurazione,
			org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager serviceManagerPlugins,
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManagerControlloTraffico, 
			org.openspcoop2.core.allarmi.dao.jdbc.JDBCServiceManager serviceManagerAllarmi){
		this.driverRegistroServizi = driverRegistroServizi;
		this.driverConfigurazione = driverConfigurazione;
		this.serviceManagerPlugins = serviceManagerPlugins;
		this.serviceManagerControlloTraffico = serviceManagerControlloTraffico;
		this.serviceManagerAllarmi = serviceManagerAllarmi;
	}
	

	public void finalizeImport(Archive archive) throws DriverConfigurazioneException{
		
	}
	public void finalizeDelete(Archive archive) throws DriverConfigurazioneException{
		
	}
	
	
	// --- Users ---
	
	public abstract boolean isVisioneOggettiGlobale(String userLogin);
	
	
	// --- PDD ---
	
	public List<String> getAllIdPorteDominio(FiltroRicerca filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdPorteDominio(filtroRicerca);
	}
	
	public PortaDominio getPortaDominio(String nomePortaDominio) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getPortaDominio(nomePortaDominio);
	}
	
	public String getTipoPortaDominio(String nomePortaDominio) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getTipoPortaDominio(nomePortaDominio);
	}
	
	public boolean existsPortaDominio(String nomePortaDominio) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsPortaDominio(nomePortaDominio);
	}
	
	public void createPortaDominio(PortaDominio pdd) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createPortaDominio(pdd);
	}
	
	public void updatePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updatePortaDominio(pdd);
	}
	
	public void deletePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deletePortaDominio(pdd);
	}
	
	public boolean isPddInUso(String nomePortaDominio, List<String> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isPddInUso");
			return DBOggettiInUsoUtils.isPddInUso(con, this.driverRegistroServizi.getTipoDB(), nomePortaDominio, whereIsInUso, normalizeObjectIds);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	
	
	
	
	
	
	// --- GRUPPI ---
	
	public List<IDGruppo> getAllIdGruppi(FiltroRicercaGruppi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdGruppi(filtroRicerca);
	}
	
	public Gruppo getGruppo(IDGruppo idGruppo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getGruppo(idGruppo);
	}
	
	public boolean existsGruppo(IDGruppo idGruppo) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsGruppo(idGruppo);
	}
	
	public void createGruppo(Gruppo gruppo) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createGruppo(gruppo);
	}
	
	public void updateGruppo(Gruppo gruppo) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateGruppo(gruppo);
	}
	
	public void deleteGruppo(Gruppo gruppo) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteGruppo(gruppo);
	}
	
	public boolean isGruppoInUso(IDGruppo idGruppo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isGruppoInUso");
			return DBOggettiInUsoUtils.isGruppoInUso(con, this.driverRegistroServizi.getTipoDB(), idGruppo, whereIsInUso, normalizeObjectIds);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	
	
	
	
	
	
	
	// --- RUOLI ---
	
	public List<IDRuolo> getAllIdRuoli(FiltroRicercaRuoli filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdRuoli(filtroRicerca);
	}
	
	public Ruolo getRuolo(IDRuolo idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getRuolo(idRuolo);
	}
	
	public boolean existsRuolo(IDRuolo idRuolo) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsRuolo(idRuolo);
	}
	
	public void createRuolo(Ruolo ruolo) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createRuolo(ruolo);
	}
	
	public void updateRuolo(Ruolo ruolo) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateRuolo(ruolo);
	}
	
	public void deleteRuolo(Ruolo ruolo) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteRuolo(ruolo);
	}
	
	public boolean isRuoloInUso(IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isRuoloInUso");
			return DBOggettiInUsoUtils.isRuoloInUso(con, this.driverRegistroServizi.getTipoDB(), idRuolo, whereIsInUso, normalizeObjectIds);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	
	
	
	
	// --- SCOPE ---
	
	public List<IDScope> getAllIdScope(FiltroRicercaScope filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdScope(filtroRicerca);
	}
	
	public Scope getScope(IDScope idScope) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getScope(idScope);
	}
	
	public boolean existsScope(IDScope idScope) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsScope(idScope);
	}
	
	public void createScope(Scope scope) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createScope(scope);
	}
	
	public void updateScope(Scope scope) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateScope(scope);
	}
	
	public void deleteScope(Scope scope) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteScope(scope);
	}
	
	public boolean isScopeInUso(IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isScopeInUso");
			return DBOggettiInUsoUtils.isScopeInUso(con, this.driverRegistroServizi.getTipoDB(), idScope, whereIsInUso, normalizeObjectIds);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}

	
		
	
	
	// --- Soggetti Registro ---
	
	public List<IDSoggetto> getAllIdSoggettiRegistro(org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAllIdSoggetti(filtroRicerca);
	}
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistro(IDSoggetto idSoggetto) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getSoggetto(idSoggetto);
	}
	
	public boolean existsSoggettoRegistro(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsSoggetto(idSoggetto);
	}
	
	public void createSoggettoRegistro(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createSoggetto(soggetto);
	}
	
	public void updateSoggettoRegistro(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateSoggetto(soggetto);
	}
	
	public void deleteSoggettoRegistro(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteSoggetto(soggetto);
	}
	
	public boolean isSoggettoRegistroInUso(IDSoggetto idSoggetto, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds, boolean verificaRuoli) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isSoggettoRegistroInUso");
			return DBOggettiInUsoUtils.isSoggettoRegistryInUso(con, this.driverRegistroServizi.getTipoDB(), idSoggetto, true, whereIsInUso, normalizeObjectIds, verificaRuoli);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public IDSoggetto getSoggettoDefault(String protocollo) throws DriverRegistroServiziException{
		try {
			List<IDSoggetto> soggettiDefault = this.driverRegistroServizi.getSoggettiDefault();
			if(soggettiDefault!=null && !soggettiDefault.isEmpty()) {
				ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
				for (IDSoggetto idSoggetto : soggettiDefault) {
					if(idSoggetto!=null && idSoggetto.getTipo()!=null) {
						String protCheck = protocolFactoryManager.getProtocolByOrganizationType(idSoggetto.getTipo());
						if(protCheck.equals(protocollo)) {
							return idSoggetto;
						}
					}
				}
			}
			return null;
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistroCredenzialiBasic(String utente) throws DriverRegistroServiziException{
		return this.driverRegistroServizi.soggettoWithCredenzialiBasic(utente, null, false);
	}
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistroCredenzialiApiKey(String utente, boolean appId) throws DriverRegistroServiziException{
		return this.driverRegistroServizi.soggettoWithCredenzialiApiKey(utente, appId);
	}
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistroCredenzialiSsl(String subject, String issuer) throws DriverRegistroServiziException{
		try {
			return this.driverRegistroServizi.getSoggettoByCredenzialiSsl(subject, issuer);
		}catch(DriverRegistroServiziNotFound notFound) {
			return null;
		}
	}
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistroCredenzialiSsl(byte[]archivio, boolean strictVerifier) throws DriverRegistroServiziException{
		Certificate cSelezionato = null;
		try {
			cSelezionato = ArchiveLoader.load(archivio);
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		try {
			return this.driverRegistroServizi.getSoggettoByCredenzialiSsl(cSelezionato.getCertificate(), strictVerifier);
		}catch(DriverRegistroServiziNotFound notFound) {
			return null;
		}
	}
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistroCredenzialiPrincipal(String principal) throws DriverRegistroServiziException{
		try {
			return this.driverRegistroServizi.getSoggettoByCredenzialiPrincipal(principal);
		}catch(DriverRegistroServiziNotFound notFound) {
			return null;
		}
	}
	
	
	
	// --- Soggetti Configurazione ---
		
	public List<IDSoggetto> getAllIdSoggettiConfigurazione(FiltroRicercaSoggetti filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getAllIdSoggetti(filtroRicerca);
	}
	
	public org.openspcoop2.core.config.Soggetto getSoggettoConfigurazione(IDSoggetto idSoggetto) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getSoggetto(idSoggetto);
	}
	
	public boolean existsSoggettoConfigurazione(IDSoggetto idSoggetto) throws DriverConfigurazioneException {
		return this.driverConfigurazione.existsSoggetto(idSoggetto);
	}
	
	public void createSoggettoConfigurazione(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		this.driverConfigurazione.createSoggetto(soggetto);
	}
	
	public void updateSoggettoConfigurazione(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		this.driverConfigurazione.updateSoggetto(soggetto);
	}
	
	public void deleteSoggettoConfigurazione(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		this.driverConfigurazione.deleteSoggetto(soggetto);
	}
	
	public boolean isSoggettoConfigurazioneInUso(IDSoggetto idSoggetto, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds, boolean verificaRuoli) throws DriverConfigurazioneException {
		Connection con = null;
		try{
			con = this.driverConfigurazione.getConnection("archive.isSoggettoConfigurazioneInUso");
			return DBOggettiInUsoUtils.isSoggettoConfigInUso(con, this.driverConfigurazione.getTipoDB(), idSoggetto, true, whereIsInUso, normalizeObjectIds, verificaRuoli);
		}
		catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverConfigurazione.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	
	// --- Servizi Applicativi ---
	
	public List<IDServizioApplicativo> getAllIdServiziApplicativi(FiltroRicercaServiziApplicativi filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getAllIdServiziApplicativi(filtroRicerca);
	}
	
	public org.openspcoop2.core.config.ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getServizioApplicativo(idServizioApplicativo);
	}
	
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException {
		return this.driverConfigurazione.existsServizioApplicativo(idServizioApplicativo);
	}
	
	public void createServizioApplicativo(org.openspcoop2.core.config.ServizioApplicativo servizioApplicativo) throws DriverConfigurazioneException {
		this.driverConfigurazione.createServizioApplicativo(servizioApplicativo);
	}
	
	public void updateServizioApplicativo(org.openspcoop2.core.config.ServizioApplicativo servizioApplicativo) throws DriverConfigurazioneException {
		this.driverConfigurazione.updateServizioApplicativo(servizioApplicativo);
	}
	
	public void deleteServizioApplicativo(org.openspcoop2.core.config.ServizioApplicativo servizioApplicativo) throws DriverConfigurazioneException {
		this.driverConfigurazione.deleteServizioApplicativo(servizioApplicativo);
	}
	
	public boolean isServizioApplicativoInUso(IDServizioApplicativo idServizioApplicativo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds, boolean verificaRuoli) throws DriverConfigurazioneException {
		Connection con = null;
		try{
			con = this.driverConfigurazione.getConnection("archive.isServizioApplicativoInUso");
			return DBOggettiInUsoUtils.isServizioApplicativoInUso(con, this.driverConfigurazione.getTipoDB(), idServizioApplicativo, whereIsInUso, true, normalizeObjectIds, verificaRuoli);
		}
		catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverConfigurazione.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public org.openspcoop2.core.config.ServizioApplicativo getServizioApplicativoCredenzialiBasic(String utente) throws DriverConfigurazioneException{
		List<org.openspcoop2.core.config.ServizioApplicativo> l = this.driverConfigurazione.servizioApplicativoWithCredenzialiBasicList(utente, null, false);
		if(l!=null && !l.isEmpty()) {
			return l.get(0);
		}
		return null;
	}
	public org.openspcoop2.core.config.ServizioApplicativo getServizioApplicativoCredenzialiApiKey(String utente, boolean appId) throws DriverConfigurazioneException{
		List<org.openspcoop2.core.config.ServizioApplicativo> l = this.driverConfigurazione.servizioApplicativoWithCredenzialiApiKeyList(utente, appId);
		if(l!=null && !l.isEmpty()) {
			return l.get(0);
		}
		return null;
	}
	public org.openspcoop2.core.config.ServizioApplicativo getServizioApplicativoCredenzialiSsl(String subject, String issuer) throws DriverConfigurazioneException{
		List<org.openspcoop2.core.config.ServizioApplicativo> l = this.driverConfigurazione.servizioApplicativoWithCredenzialiSslList(subject, issuer);
		if(l!=null && !l.isEmpty()) {
			return l.get(0);
		}
		return null;
	}
	public org.openspcoop2.core.config.ServizioApplicativo getServizioApplicativoCredenzialiSsl(byte[]archivio, boolean strictVerifier) throws DriverConfigurazioneException{
		Certificate cSelezionato = null;
		try {
			cSelezionato = ArchiveLoader.load(archivio);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		List<org.openspcoop2.core.config.ServizioApplicativo> l = this.driverConfigurazione.servizioApplicativoWithCredenzialiSslList(cSelezionato.getCertificate(), strictVerifier);
		if(l!=null && !l.isEmpty()) {
			return l.get(0);
		}
		return null;
	}
	public org.openspcoop2.core.config.ServizioApplicativo getServizioApplicativoCredenzialiPrincipal(String principal) throws DriverConfigurazioneException{
		List<org.openspcoop2.core.config.ServizioApplicativo> l = this.driverConfigurazione.servizioApplicativoWithCredenzialiPrincipalList(principal);
		if(l!=null && !l.isEmpty()) {
			return l.get(0);
		}
		return null;
	}
	public org.openspcoop2.core.config.ServizioApplicativo getServizioApplicativoCredenzialiToken(String tokenPolicy, String tokenClientId) throws DriverConfigurazioneException{
		
		boolean tokenWithHttpsEnabled = false;
		try {
			// basta un protocollo che lo supporta per doverli cercare anche con la funzionalita' abilitata
			for(IProtocolFactory<?> protocolFactory: ProtocolFactoryManager.getInstance().getProtocolFactories().values()) {
				if(protocolFactory.createProtocolConfiguration().isSupportatoAutenticazioneApplicativiHttpsConToken()) {
					tokenWithHttpsEnabled = true;
					break;
				}
			}
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		List<org.openspcoop2.core.config.ServizioApplicativo> l = this.driverConfigurazione.servizioApplicativoWithCredenzialiTokenList(tokenPolicy, tokenClientId, tokenWithHttpsEnabled);
		if(l!=null && !l.isEmpty()) {
			return l.get(0);
		}
		return null;
	}
	
	
	
	// --- Accordi di Cooperazione ---
	
	public List<IDAccordoCooperazione> getAllIdAccordiCooperazione(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdAccordiCooperazione(filtroRicerca);
	}
	
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoCooperazione(idAccordoCooperazione);
	}
	
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione, boolean readContenutoAllegati) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoCooperazione(idAccordoCooperazione,readContenutoAllegati);
	}
	
	public boolean existsAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsAccordoCooperazione(idAccordoCooperazione);
	}
	
	public void createAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createAccordoCooperazione(accordoCooperazione);
	}
	
	public void updateAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateAccordoCooperazione(accordoCooperazione);
	}
	
	public void deleteAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteAccordoCooperazione(accordoCooperazione);
	}
	
	public void validaStatoAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws ValidazioneStatoPackageException {
		this.driverRegistroServizi.validaStatoAccordoCooperazione(accordoCooperazione);
	}
	
	public boolean isAccordoCooperazioneInUso(IDAccordoCooperazione idAccordo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isAccordoCooperazioneInUso");
			return DBOggettiInUsoUtils.isAccordoCooperazioneInUso(con, this.driverRegistroServizi.getTipoDB(), idAccordo, whereIsInUso, normalizeObjectIds);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	// --- Accordi di Servizio Parte Comune ---
	
	public List<IDAccordo> getAllIdAccordiServizioParteComune(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdAccordiServizioParteComune(filtroRicerca);
	}
	
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordoServizioParteComune) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoServizioParteComune(idAccordoServizioParteComune);
	}
	
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordoServizioParteComune, boolean readContenutoAllegati,boolean readDatiRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoServizioParteComune(idAccordoServizioParteComune, readContenutoAllegati, readDatiRegistro);
	}
	
	public boolean existsAccordoServizioParteComune(IDAccordo idAccordoServizioParteComune) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsAccordoServizioParteComune(idAccordoServizioParteComune);
	}
	
	public void createAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createAccordoServizioParteComune(accordoServizioParteComune);
	}
	
	public void updateAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateAccordoServizioParteComune(accordoServizioParteComune);
	}
	
	public void deleteAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteAccordoServizioParteComune(accordoServizioParteComune);
	}
	
	public void validaStatoAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune,boolean utilizzoAzioniDiretteInAccordoAbilitato) throws ValidazioneStatoPackageException {
		this.driverRegistroServizi.validaStatoAccordoServizio(accordoServizioParteComune,utilizzoAzioniDiretteInAccordoAbilitato);
	}
	
	public boolean isAccordoServizioParteComuneInUso(IDAccordo idAccordo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isAccordoServizioParteComuneInUso");
			return DBOggettiInUsoUtils.isAccordoServizioParteComuneInUso(con, this.driverRegistroServizi.getTipoDB(), idAccordo, whereIsInUso, normalizeObjectIds);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	// --- Accordi di Servizio Parte Specifica ---
	
	public List<IDServizio> getAllIdAccordiServizioParteSpecifica(FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.driverRegistroServizi.getAllIdServizi(filtroRicerca);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoServizioParteSpecifica(idServizio);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio, boolean readContenutoAllegati) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.driverRegistroServizi.getAccordoServizioParteSpecifica(idServizio,readContenutoAllegati);
	}
	
	public boolean existsAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException {
		return this.driverRegistroServizi.existsAccordoServizioParteSpecifica(idServizio);
	}
	
	public boolean existsFruzioneAccordoServizioParteSpecifica(IDServizio idServizio, IDSoggetto fruitore) throws DriverRegistroServiziException {
		if( this.driverRegistroServizi.existsAccordoServizioParteSpecifica(idServizio) ) {
			try {
				AccordoServizioParteSpecifica asps = this.getAccordoServizioParteSpecifica(idServizio, false);
				if(asps.sizeFruitoreList()>0) {
					for (Fruitore fr : asps.getFruitoreList()) {
						if(fr.getTipo().equals(fruitore.getTipo()) && fr.getNome().equals(fruitore.getNome())) {
							return true;
						}
					}
				}
			}catch(Exception e) {
				return false;
			}			
		}
		return false;
	}
	
	public void createAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException {
		this.driverRegistroServizi.createAccordoServizioParteSpecifica(accordoServizioParteSpecifica);
	}
	
	public void updateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException {
		this.driverRegistroServizi.updateAccordoServizioParteSpecifica(accordoServizioParteSpecifica);
	}
	
	public void deleteAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException {
		this.driverRegistroServizi.deleteAccordoServizioParteSpecifica(accordoServizioParteSpecifica);
	}
	
	public void validaStatoAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica, boolean gestioneWsdlImplementativo, boolean checkConnettore) throws ValidazioneStatoPackageException {
		this.driverRegistroServizi.validaStatoAccordoServizioParteSpecifica(accordoServizioParteSpecifica, gestioneWsdlImplementativo, checkConnettore);
	}
	
	public void validaStatoFruitoreServizio(Fruitore fruitore, AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws ValidazioneStatoPackageException {
		this.driverRegistroServizi.validaStatoFruitoreServizio(fruitore, accordoServizioParteSpecifica);
	}
	
	public void controlloUnicitaImplementazioneAccordoPerSoggetto(String portType,
			IDSoggetto idSoggettoErogatore, long idSoggettoErogatoreLong, 
			IDAccordo idAccordoServizioParteComune, long idAccordoServizioParteComuneLong,
			IDServizio idAccordoServizioParteSpecifica, long idAccordoServizioParteSpecificaLong,
			boolean isUpdate,boolean isServizioCorrelato,
			boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
			boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto) throws DriverRegistroServiziException{
		this.driverRegistroServizi.controlloUnicitaImplementazioneAccordoPerSoggetto(portType, idSoggettoErogatore, idSoggettoErogatoreLong, 
				idAccordoServizioParteComune, idAccordoServizioParteComuneLong, 
				idAccordoServizioParteSpecifica, idAccordoServizioParteSpecificaLong, 
				isUpdate, isServizioCorrelato, 
				isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto, 
				isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto);
	}
	
	public boolean isAccordoServizioParteSpecificaInUso(IDServizio idServizio, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isAccordoServizioParteSpecificaInUso");
			return DBOggettiInUsoUtils.isAccordoServizioParteSpecificaInUso(con, this.driverRegistroServizi.getTipoDB(), idServizio, whereIsInUso, null, null, normalizeObjectIds);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	
	
	// --- Mapping Erogazione ---
	
	public void createMappingErogazione(String nome, String descrizione, boolean isDefault, IDServizio idServizio, IDPortaApplicativa idPortaApplicativaByNome) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("createMappingErogazione");
			DBMappingUtils.createMappingErogazione(nome, descrizione, isDefault, idServizio, idPortaApplicativaByNome, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try {
				if(this.driverRegistroServizi.isAtomica()) {
					con.commit();
				}
			}catch(Throwable t) {}
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public IDPortaApplicativa getIDPortaApplicativaDefaultAssociataErogazione(IDServizio idServizio) throws DriverRegistroServiziException{
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("getIDPortaApplicativaDefaultAssociataErogazione");
			return DBMappingUtils.getIDPortaApplicativaAssociataDefault(idServizio, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public IDPortaApplicativa getIDPortaApplicativaPerAzioneAssociataErogazione(IDServizio idServizio) throws DriverRegistroServiziException{
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("getIDPortaApplicativaPerAzioneAssociataErogazione");
			return DBMappingUtils.getIDPortaApplicativaAssociataAzione(idServizio, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public List<IDPortaApplicativa> getIDPorteApplicativeAssociateErogazione(IDServizio idServizio) throws DriverRegistroServiziException{
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("getIDPorteApplicativeAssociateErogazione");
			return DBMappingUtils.getIDPorteApplicativeAssociate(idServizio, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public void deleteMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativaByNome) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("deleteMappingErogazione");
			DBMappingUtils.deleteMappingErogazione(idServizio, idPortaApplicativaByNome, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try {
				if(this.driverRegistroServizi.isAtomica()) {
					con.commit();
				}
			}catch(Throwable t) {}
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public void deleteMappingErogazione(IDServizio idServizio) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("deleteMappingErogazione");
			DBMappingUtils.deleteMappingErogazione(idServizio, null, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try {
				if(this.driverRegistroServizi.isAtomica()) {
					con.commit();
				}
			}catch(Throwable t) {}
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public boolean existsIDPortaApplicativaDefaultAssociataErogazione(IDServizio idServizio) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("existsIDPortaApplicativaDefaultAssociataErogazione");
			return DBMappingUtils.existsIDPortaApplicativaAssociataDefault(idServizio, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public boolean existsIDPortaApplicativaPerAzioneAssociataErogazione(IDServizio idServizio) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("existsIDPortaApplicativaPerAzioneAssociataErogazione");
			return DBMappingUtils.existsIDPortaApplicativaAssociataAzione(idServizio, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public boolean existsIDPorteApplicativeAssociateErogazione(IDServizio idServizio) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("existsIDPorteApplicativeAssociateErogazione");
			return DBMappingUtils.existsIDPorteApplicativeAssociate(idServizio, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public boolean existsMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("existsMappingErogazione");
			return DBMappingUtils.existsMappingErogazione(idServizio, idPortaApplicativa, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public MappingErogazionePortaApplicativa getMappingErogazionePortaApplicativa(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("getMappingErogazionePortaApplicativa");
			return DBMappingUtils.getMappingErogazione(idServizio, idPortaApplicativa, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	// Inefficente
	@Deprecated 
	public void initMappingErogazione(Logger log) throws DriverRegistroServiziException {
		try{
			UtilitiesMappingFruizioneErogazione utilities = new UtilitiesMappingFruizioneErogazione(this.driverConfigurazione, this.driverRegistroServizi, log);
			utilities.initMappingErogazione();
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	public void initMappingErogazioneById(Logger log, List<IDPortaApplicativa> listPA) throws DriverRegistroServiziException {
		try{
			UtilitiesMappingFruizioneErogazione utilities = new UtilitiesMappingFruizioneErogazione(this.driverConfigurazione, this.driverRegistroServizi, log);
			utilities.gestioneMappingErogazionePAbyId(listPA);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	public void initMappingErogazione(Logger log, List<PortaApplicativa> listPA) throws DriverRegistroServiziException {
		try{
			UtilitiesMappingFruizioneErogazione utilities = new UtilitiesMappingFruizioneErogazione(this.driverConfigurazione, this.driverRegistroServizi, log);
			utilities.gestioneMappingErogazionePA(listPA);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	// --- Mapping Fruizione ---
	
	public void createMappingFruizione(String nome, String descrizione, boolean isDefault, IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("createMappingFruizione");
			DBMappingUtils.createMappingFruizione(nome, descrizione, isDefault, idServizio, idFruitore, idPortaDelegata, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try {
				if(this.driverRegistroServizi.isAtomica()) {
					con.commit();
				}
			}catch(Throwable t) {}
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public IDPortaDelegata getIDPortaDelegataDefaultAssociataFruizione(IDServizio idServizio, IDSoggetto idFruitore) throws DriverRegistroServiziException{
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("getIDPortaDelegataDefaultAssociataFruizione");
			return DBMappingUtils.getIDPortaDelegataAssociataDefault(idServizio, idFruitore, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public IDPortaDelegata getIDPortaDelegataPerAzioneAssociataFruizione(IDServizio idServizio, IDSoggetto idFruitore) throws DriverRegistroServiziException{
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("getIDPortaDelegataPerAzioneAssociataFruizione");
			return DBMappingUtils.getIDPortaDelegataAssociataAzione(idServizio, idFruitore, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public List<IDPortaDelegata> getIDPorteDelegateAssociateFruizione(IDServizio idServizio, IDSoggetto idFruitore) throws DriverRegistroServiziException{
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("getIDPorteDelegateAssociateFruizione");
			return DBMappingUtils.getIDPorteDelegateAssociate(idServizio, idFruitore, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public void deleteMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("deleteMappingFruizione");
			DBMappingUtils.deleteMappingFruizione(idServizio, idFruitore, idPortaDelegata, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try {
				if(this.driverRegistroServizi.isAtomica()) {
					con.commit();
				}
			}catch(Throwable t) {}
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public void deleteMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("deleteMappingFruizione");
			DBMappingUtils.deleteMappingFruizione(idServizio, idFruitore, null, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try {
				if(this.driverRegistroServizi.isAtomica()) {
					con.commit();
				}
			}catch(Throwable t) {}
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public boolean existsIDPortaDelegataDefaultAssociataFruizione(IDServizio idServizio, IDSoggetto idFruitore) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("existsIDPortaDelegataDefaultAssociataFruizione");
			return DBMappingUtils.existsIDPortaDelegataAssociataDefault(idServizio, idFruitore, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public boolean existsIDPortaDelegataPerAzioneAssociataFruizione(IDServizio idServizio, IDSoggetto idFruitore) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("existsIDPortaDelegataPerAzioneAssociataFruizione");
			return DBMappingUtils.existsIDPortaDelegataAssociataAzione(idServizio, idFruitore, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public boolean existsIDPorteDelegateAssociateFruizione(IDServizio idServizio, IDSoggetto idFruitore) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("existsIDPorteDelegateAssociateFruizione");
			return DBMappingUtils.existsIDPorteDelegateAssociate(idServizio, idFruitore, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public boolean existsMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("existsMappingFruizione");
			return DBMappingUtils.existsMappingFruizione(idServizio, idFruitore, idPortaDelegata, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	public MappingFruizionePortaDelegata getMappingFruizionePortaDelegata(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("getMappingFruizionePortaDelegata");
			return DBMappingUtils.getMappingFruizione(idServizio, idFruitore, idPortaDelegata, con, this.driverRegistroServizi.getTipoDB());
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	// Inefficente
	@Deprecated 
	public void initMappingFruizione(Logger log) throws DriverRegistroServiziException {
		try{
			UtilitiesMappingFruizioneErogazione utilities = new UtilitiesMappingFruizioneErogazione(this.driverConfigurazione, this.driverRegistroServizi, log);
			utilities.initMappingFruizione();
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	public void initMappingFruizioneById(Logger log, List<IDPortaDelegata> listPD) throws DriverRegistroServiziException {
		try{
			UtilitiesMappingFruizioneErogazione utilities = new UtilitiesMappingFruizioneErogazione(this.driverConfigurazione, this.driverRegistroServizi, log);
			utilities.gestioneMappingFruizionePDbyId(listPD);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	public void initMappingFruizione(Logger log, List<PortaDelegata> listPD) throws DriverRegistroServiziException {
		try{
			UtilitiesMappingFruizioneErogazione utilities = new UtilitiesMappingFruizioneErogazione(this.driverConfigurazione, this.driverRegistroServizi, log);
			utilities.gestioneMappingFruizionePD(listPD);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
		
	
	
	// --- Porte Delegate ---
	
	public List<IDPortaDelegata> getAllIdPorteDelegate(FiltroRicercaPorteDelegate filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getAllIdPorteDelegate(filtroRicerca);
	}
	
	public org.openspcoop2.core.config.PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getPortaDelegata(idPortaDelegata);
	}
	
	public boolean existsPortaDelegata(IDPortaDelegata idPortaDelegata) throws DriverConfigurazioneException {
		return this.driverConfigurazione.existsPortaDelegata(idPortaDelegata);
	}
	
	public void createPortaDelegata(org.openspcoop2.core.config.PortaDelegata portaDelegata) throws DriverConfigurazioneException {
		this.driverConfigurazione.createPortaDelegata(portaDelegata);
	}
	
	public void deletePortaDelegata(org.openspcoop2.core.config.PortaDelegata portaDelegata) throws DriverConfigurazioneException {
		this.driverConfigurazione.deletePortaDelegata(portaDelegata);
	}
	
	public void updatePortaDelegata(org.openspcoop2.core.config.PortaDelegata portaDelegata) throws DriverConfigurazioneException {
		this.driverConfigurazione.updatePortaDelegata(portaDelegata);
	}
	
	
	
	
	// --- Porte Applicative ---
	
	public List<IDPortaApplicativa> getAllIdPorteApplicative(FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getAllIdPorteApplicative(filtroRicerca);
	}
	
	public org.openspcoop2.core.config.PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.driverConfigurazione.getPortaApplicativa(idPortaApplicativa);
	}
	
	public boolean existsPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws DriverConfigurazioneException {
		return this.driverConfigurazione.existsPortaApplicativa(idPortaApplicativa);
	}
	
	public void createPortaApplicativa(org.openspcoop2.core.config.PortaApplicativa portaApplicativa) throws DriverConfigurazioneException {
		this.driverConfigurazione.createPortaApplicativa(portaApplicativa);
	}
	
	public void deletePortaApplicativa(org.openspcoop2.core.config.PortaApplicativa portaApplicativa) throws DriverConfigurazioneException {
		this.driverConfigurazione.deletePortaApplicativa(portaApplicativa);
	}
	
	public void updatePortaApplicativa(org.openspcoop2.core.config.PortaApplicativa portaApplicativa) throws DriverConfigurazioneException {
		this.driverConfigurazione.updatePortaApplicativa(portaApplicativa);
	}
	
	
	
	
	// --- Controllo Traffico (Configurazione) ---
	
	public void updateControlloTraffico_configurazione(ConfigurazioneGenerale configurazione) throws DriverConfigurazioneException{
		try {
			this.serviceManagerControlloTraffico.getConfigurazioneGeneraleService().update(configurazione);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public void deleteControlloTraffico_Configurazione(ConfigurazioneGenerale configurazione) throws DriverConfigurazioneException{
		try {
			this.serviceManagerControlloTraffico.getConfigurazioneGeneraleService().delete(configurazione);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public ConfigurazioneGenerale getControlloTraffico_Configurazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			return this.serviceManagerControlloTraffico.getConfigurazioneGeneraleService().get();
		}catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	
	// --- Controllo Traffico (ConfigurazionePolicy) ---
	
	public List<IdPolicy> getAllIdControlloTraffico_configurationPolicies() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			IPaginatedExpression pagExpr = this.serviceManagerControlloTraffico.getConfigurazionePolicyServiceSearch().newPaginatedExpression();
			List<IdPolicy> l = this.serviceManagerControlloTraffico.getConfigurazionePolicyServiceSearch().findAllIds(pagExpr);
			if(l==null || l.size()<=0) {
				throw new NotFoundException("Non esistono policy");
			}
			return l;
		}catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public ConfigurazionePolicy getControlloTraffico_configurationPolicy(String idPolicy) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		IdPolicy idPolicyObject = new IdPolicy();
		idPolicyObject.setNome(idPolicy);
		return this.getControlloTraffico_configurationPolicy(idPolicyObject);
	}
	public ConfigurazionePolicy getControlloTraffico_configurationPolicy(IdPolicy idPolicy) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		try {
			return this.serviceManagerControlloTraffico.getConfigurazionePolicyServiceSearch().get(idPolicy);
		}catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean existsControlloTraffico_configurationPolicy(String idPolicy) throws DriverConfigurazioneException {
		IdPolicy idPolicyObject = new IdPolicy();
		idPolicyObject.setNome(idPolicy);
		return this.existsControlloTraffico_configurationPolicy(idPolicyObject);
	}
	public boolean existsControlloTraffico_configurationPolicy(IdPolicy idPolicy) throws DriverConfigurazioneException {
		try {
			return this.serviceManagerControlloTraffico.getConfigurazionePolicyServiceSearch().exists(idPolicy);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public void createControlloTraffico_configurationPolicy(ConfigurazionePolicy policy) throws DriverConfigurazioneException {
		try {
			this.serviceManagerControlloTraffico.getConfigurazionePolicyService().create(policy);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public void updateControlloTraffico_configurationPolicy(ConfigurazionePolicy policy) throws DriverConfigurazioneException {
		try {
			IdPolicy oldId = new IdPolicy();
			oldId.setNome(policy.getIdPolicy());
			if(policy.getOldIdPolicy()!=null && policy.getOldIdPolicy().getNome()!=null) {
				oldId.setNome(policy.getOldIdPolicy().getNome());
			}
			this.serviceManagerControlloTraffico.getConfigurazionePolicyService().update(oldId, policy);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public void deleteControlloTraffico_configurationPolicy(ConfigurazionePolicy policy) throws DriverConfigurazioneException {
		try {
			this.serviceManagerControlloTraffico.getConfigurazionePolicyService().delete(policy);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean isControlloTraffico_configurationPolicyInUso(String idPolicy, List<String> whereIsInUso) throws DriverConfigurazioneException {
		IdPolicy idPolicyObject = new IdPolicy();
		idPolicyObject.setNome(idPolicy);
		return this.isControlloTraffico_configurationPolicyInUso(idPolicyObject,whereIsInUso);
	}
	public boolean isControlloTraffico_configurationPolicyInUso(IdPolicy idPolicy, List<String> whereIsInUso) throws DriverConfigurazioneException {
		try {
			IPaginatedExpression pagExpr = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().newPaginatedExpression();
			pagExpr.equals(AttivazionePolicy.model().ID_POLICY, idPolicy.getNome());
			List<IdActivePolicy> l = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().findAllIds(pagExpr);
			if(l==null || l.size()<=0) {
				return false;
			}
			else {
				for (IdActivePolicy idActivePolicy : l) {
					whereIsInUso.add(idActivePolicy.getNome());
				}
				return true;	
			}
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	
	// --- Controllo Traffico (AttivazionePolicy) ---
	
	public List<IdActivePolicy> getAllIdControlloTraffico_activePolicies_globali(String tag) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdControlloTraffico_activePolicies(true, tag);
	}
	public List<IdActivePolicy> getAllIdControlloTraffico_activePolicies_erogazioniFruizioni(String tag) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdControlloTraffico_activePolicies(false, tag);
	}
	public List<IdActivePolicy> getAllIdControlloTraffico_activePolicies_all(String tag) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdControlloTraffico_activePolicies(null, tag);
	}
	private List<IdActivePolicy> _getAllIdControlloTraffico_activePolicies(Boolean globali, String tag) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			IPaginatedExpression pagExpr = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().newPaginatedExpression();
			pagExpr.and();
			if(globali!=null) {
				if(globali) {
					pagExpr.isNull(org.openspcoop2.core.controllo_traffico.AttivazionePolicy.model().FILTRO.NOME_PORTA);
				}else {
					pagExpr.isNotNull(org.openspcoop2.core.controllo_traffico.AttivazionePolicy.model().FILTRO.NOME_PORTA);
				}
			}
			if(StringUtils.isNotEmpty(tag)) {
				pagExpr.equals(org.openspcoop2.core.controllo_traffico.AttivazionePolicy.model().FILTRO.TAG, tag);
			}
			List<IdActivePolicy> l = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().findAllIds(pagExpr);
			if(l==null || l.size()<=0) {
				throw new NotFoundException("Non esistono policy");
			}
			return l;
		}catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public List<IdActivePolicy> getAllIdControlloTraffico_activePolicies_fruizione(String nomePorta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdControlloTraffico_activePolicies_porta(RuoloPolicy.DELEGATA, nomePorta);
	}
	public List<IdActivePolicy> getAllIdControlloTraffico_activePolicies_erogazione(String nomePorta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{	
		return _getAllIdControlloTraffico_activePolicies_porta(RuoloPolicy.APPLICATIVA, nomePorta);
	}
	private List<IdActivePolicy> _getAllIdControlloTraffico_activePolicies_porta(RuoloPolicy ruoloPorta, String nomePorta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			IPaginatedExpression pagExpr = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().newPaginatedExpression();
			pagExpr.equals(org.openspcoop2.core.controllo_traffico.AttivazionePolicy.model().FILTRO.RUOLO_PORTA,ruoloPorta.getValue());
			pagExpr.and();
			pagExpr.equals(org.openspcoop2.core.controllo_traffico.AttivazionePolicy.model().FILTRO.NOME_PORTA,nomePorta);
			List<IdActivePolicy> l = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().findAllIds(pagExpr);
			if(l==null || l.size()<=0) {
				throw new NotFoundException("Non esistono policy");
			}
			return l;
		}catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	// Pericoloso: l'id è dinamico e dipende dal sistema dove viene usato.
//	public AttivazionePolicy getControlloTraffico_activePolicy(String IdActivePolicy) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
//		IdActivePolicy IdActivePolicyObject = new IdActivePolicy();
//		IdActivePolicyObject.setNome(IdActivePolicy);
//		return this.getControlloTraffico_activePolicy(IdActivePolicyObject);
//	}
//	public AttivazionePolicy getControlloTraffico_activePolicy(IdActivePolicy IdActivePolicy) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
//		try {
//			return this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().get(IdActivePolicy);
//		}catch(NotFoundException e) {
//			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
//		}catch(Exception e) {
//			throw new DriverConfigurazioneException(e.getMessage(),e);
//		}
//	}
	public AttivazionePolicy getControlloTraffico_activePolicy(RuoloPolicy ruoloPorta, String nomePorta, String alias) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		try {
			IExpression expr = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().newExpression();
			
			expr.and();
			
			if(ruoloPorta!=null && nomePorta!=null) {
				expr.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, ruoloPorta);
				expr.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA, nomePorta);
			}
			else {
				expr.isNull(AttivazionePolicy.model().FILTRO.NOME_PORTA);
			}
			
			expr.equals(AttivazionePolicy.model().ALIAS, alias);
			
			AttivazionePolicy att = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().find(expr);
			return att;
		}
		catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}
		catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	// Pericoloso: l'id è dinamico e dipende dal sistema dove viene usato.
//	public boolean existsControlloTraffico_activePolicy(String IdActivePolicy) throws DriverConfigurazioneException {
//		IdActivePolicy IdActivePolicyObject = new IdActivePolicy();
//		IdActivePolicyObject.setNome(IdActivePolicy);
//		return this.existsControlloTraffico_activePolicy(IdActivePolicyObject);
//	}
//	public boolean existsControlloTraffico_activePolicy(IdActivePolicy IdActivePolicy) throws DriverConfigurazioneException {
//		try {
//			return this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().exists(IdActivePolicy);
//		}catch(Exception e) {
//			throw new DriverConfigurazioneException(e.getMessage(),e);
//		}
//	}
	public boolean existsControlloTraffico_activePolicy(RuoloPolicy ruoloPorta, String nomePorta, String alias) throws DriverConfigurazioneException {
		try {
			IExpression expr = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().newExpression();
			
			expr.and();
			
			if(ruoloPorta!=null && nomePorta!=null) {
				expr.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, ruoloPorta);
				expr.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA, nomePorta);
			}
			else {
				expr.isNull(AttivazionePolicy.model().FILTRO.NOME_PORTA);
			}
			
			expr.equals(AttivazionePolicy.model().ALIAS, alias);
			
			AttivazionePolicy att = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().find(expr);
			return att!=null;
		}
		catch(NotFoundException notFound) {
			return false;
		}
		catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public String getNextPolicyInstanceSerialId(String policyId, Logger log) throws RegistryException{
			
		Connection con = null;
		try {
			con = this.driverConfigurazione.getConnection("getNextPolicyInstanceSerialId");
			
			return ControlloTrafficoDriverUtils.getNextPolicyInstanceSerialId(policyId,
					con, log, this.driverConfigurazione.getTipoDB());
			
		}
		catch(Exception e) {
			throw new RegistryException(e.getMessage(),e);
		}
		finally {
			try {
				if(con!=null) {
					this.driverConfigurazione.releaseConnection(con);
				}
			}catch(Exception eClose) {
				// close
			}
		}
		
	}
	
	protected void updatePosizioneBeforeCreate(AttivazionePolicy policy, Logger log) throws DriverConfigurazioneException{
		Connection con = null;
		try {
			con = this.driverConfigurazione.getConnection("updatePosizioneAttivazionePolicy");
			
			RuoloPolicy ruoloPorta=null;
			String nomePorta=null;
			if(policy.getFiltro()!=null) {
				ruoloPorta = policy.getFiltro().getRuoloPorta();
				nomePorta = policy.getFiltro().getNomePorta();
			}
		
			List<InfoPolicy> lst = ControlloTrafficoDriverUtils.getInfoPolicyList(null, policy.getIdPolicy(),
					con, log, this.driverConfigurazione.getTipoDB()); 
			InfoPolicy infoPolicy = (lst != null && lst.size() > 0) ? lst.get(0) : null;
			if(infoPolicy==null) {
				throw new Exception("Policy '"+policy.getIdPolicy()+"' non trovata");
			}
			ControlloTrafficoDriverUtils.updatePosizioneAttivazionePolicy(infoPolicy, policy,
					ruoloPorta, nomePorta, 
					con, log, this.driverConfigurazione.getTipoDB());			
		}
		catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		finally {
			try {
				if(con!=null) {
					this.driverConfigurazione.releaseConnection(con);
				}
			}catch(Exception eClose) {
				// close
			}
		}
	}
	public void createControlloTraffico_activePolicy(AttivazionePolicy policy, Logger log) throws DriverConfigurazioneException {
		updatePosizioneBeforeCreate(policy, log);		
		try {
			this.serviceManagerControlloTraffico.getAttivazionePolicyService().create(policy);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public void updateControlloTraffico_activePolicy(AttivazionePolicy policy) throws DriverConfigurazioneException {
		try {
			IdActivePolicy oldId = new IdActivePolicy();
			oldId.setNome(policy.getIdActivePolicy());
			if(policy.getOldIdActivePolicy()!=null && policy.getOldIdActivePolicy().getNome()!=null) {
				oldId.setNome(policy.getOldIdActivePolicy().getNome());
			}
			this.serviceManagerControlloTraffico.getAttivazionePolicyService().update(oldId, policy);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public void deleteControlloTraffico_activePolicy(AttivazionePolicy policy) throws DriverConfigurazioneException {
		try {
			// Il file importato potrebbe avere un identificativo diverso da quello effettivamente salvato
			if(policy.getAlias()==null) {
				this.serviceManagerControlloTraffico.getAttivazionePolicyService().delete(policy);
			}
			else {
				IExpression expr = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().newExpression();
				
				expr.and();
				
				if(policy.getFiltro()!=null && policy.getFiltro().getRuoloPorta()!=null && policy.getFiltro().getNomePorta()!=null) {
					expr.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, policy.getFiltro().getRuoloPorta());
					expr.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA, policy.getFiltro().getNomePorta());
				}
				else {
					expr.isNull(AttivazionePolicy.model().FILTRO.NOME_PORTA);
				}
				
				expr.equals(AttivazionePolicy.model().ALIAS, policy.getAlias());
				
				try {
					AttivazionePolicy att = this.serviceManagerControlloTraffico.getAttivazionePolicyServiceSearch().find(expr);
					this.serviceManagerControlloTraffico.getAttivazionePolicyService().delete(att);
				}catch(NotFoundException notFound) {}
			}
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean isControlloTraffico_activePolicyInUso(RuoloPolicy ruoloPorta, String nomePorta, String alias, List<String> whereIsInUso) throws DriverRegistroServiziException {
		return false;
	}
	
	
	
	
	
	
	
	// --- Allarmi ---
	
	public List<IdAllarme> getAllIdAllarmi_globali(String tag) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdAllarmi(true, tag);
	}
	public List<IdAllarme> getAllIdAllarmi_erogazioniFruizioni(String tag) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdAllarmi(false, tag);
	}
	public List<IdAllarme> getAllIdAllarmi_all(String tag) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdAllarmi(null, tag);
	}
	private List<IdAllarme> _getAllIdAllarmi(Boolean globali, String tag) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			IPaginatedExpression pagExpr = this.serviceManagerAllarmi.getAllarmeServiceSearch().newPaginatedExpression();
			pagExpr.and();
			if(globali!=null) {
				if(globali) {
					pagExpr.isNull(org.openspcoop2.core.allarmi.Allarme.model().FILTRO.NOME_PORTA);
				}else {
					pagExpr.isNotNull(org.openspcoop2.core.allarmi.Allarme.model().FILTRO.NOME_PORTA);
				}
			}
			if(StringUtils.isNotEmpty(tag)) {
				pagExpr.equals(org.openspcoop2.core.allarmi.Allarme.model().FILTRO.TAG, tag);
			}
			List<IdAllarme> l = this.serviceManagerAllarmi.getAllarmeServiceSearch().findAllIds(pagExpr);
			if(l==null || l.size()<=0) {
				throw new NotFoundException("Non esistono allarmi");
			}
			return l;
		}catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public List<IdAllarme> getAllIdAllarmi_fruizione(String nomePorta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdAllarmi_porta(RuoloPorta.DELEGATA, nomePorta);
	}
	public List<IdAllarme> getAllIdAllarmi_erogazione(String nomePorta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{	
		return _getAllIdAllarmi_porta(RuoloPorta.APPLICATIVA, nomePorta);
	}
	private List<IdAllarme> _getAllIdAllarmi_porta(RuoloPorta ruoloPorta, String nomePorta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			IPaginatedExpression pagExpr = this.serviceManagerAllarmi.getAllarmeServiceSearch().newPaginatedExpression();
			pagExpr.equals(org.openspcoop2.core.allarmi.Allarme.model().FILTRO.RUOLO_PORTA,ruoloPorta.getValue());
			pagExpr.and();
			pagExpr.equals(org.openspcoop2.core.allarmi.Allarme.model().FILTRO.NOME_PORTA,nomePorta);
			List<IdAllarme> l = this.serviceManagerAllarmi.getAllarmeServiceSearch().findAllIds(pagExpr);
			if(l==null || l.size()<=0) {
				throw new NotFoundException("Non esistono allarmi");
			}
			return l;
		}catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	// Pericoloso: l'id è dinamico e dipende dal sistema dove viene usato.
//	public Allarme getAllarme(String IdAllarme) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
//		IdAllarme IdAllarmeObject = new IdAllarme();
//		IdAllarmeObject.setNome(IdAllarme);
//		return this.getAllarme(IdAllarmeObject);
//	}
//	public Allarme getAllarme(IdAllarme IdAllarme) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
//		try {
//			return this.serviceManagerAllarmi.getAllarmeServiceSearch().get(IdAllarme);
//		}catch(NotFoundException e) {
//			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
//		}catch(Exception e) {
//			throw new DriverConfigurazioneException(e.getMessage(),e);
//		}
//	}
	public Allarme getAllarme(RuoloPorta ruoloPorta, String nomePorta, String alias) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		try {
			IExpression expr = this.serviceManagerAllarmi.getAllarmeServiceSearch().newExpression();
			
			expr.and();
			
			if(ruoloPorta!=null && nomePorta!=null) {
				expr.equals(Allarme.model().FILTRO.RUOLO_PORTA, ruoloPorta);
				expr.equals(Allarme.model().FILTRO.NOME_PORTA, nomePorta);
			}
			else {
				expr.isNull(Allarme.model().FILTRO.NOME_PORTA);
			}
			
			expr.equals(Allarme.model().ALIAS, alias);
			
			Allarme att = this.serviceManagerAllarmi.getAllarmeServiceSearch().find(expr);
			return att;
		}
		catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}
		catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	// Pericoloso: l'id è dinamico e dipende dal sistema dove viene usato.
//	public boolean existsAllarme(String IdAllarme) throws DriverConfigurazioneException {
//		IdAllarme IdAllarmeObject = new IdAllarme();
//		IdAllarmeObject.setNome(IdAllarme);
//		return this.existsAllarme(IdAllarmeObject);
//	}
//	public boolean existsAllarme(IdAllarme IdAllarme) throws DriverConfigurazioneException {
//		try {
//			return this.serviceManagerAllarmi.getAllarmeServiceSearch().exists(IdAllarme);
//		}catch(Exception e) {
//			throw new DriverConfigurazioneException(e.getMessage(),e);
//		}
//	}
	public boolean existsAllarme(RuoloPorta ruoloPorta, String nomePorta, String alias) throws DriverConfigurazioneException {
		try {
			IExpression expr = this.serviceManagerAllarmi.getAllarmeServiceSearch().newExpression();
			
			expr.and();
			
			if(ruoloPorta!=null && nomePorta!=null) {
				expr.equals(Allarme.model().FILTRO.RUOLO_PORTA, ruoloPorta);
				expr.equals(Allarme.model().FILTRO.NOME_PORTA, nomePorta);
			}
			else {
				expr.isNull(Allarme.model().FILTRO.NOME_PORTA);
			}
			
			expr.equals(Allarme.model().ALIAS, alias);
			
			Allarme att = this.serviceManagerAllarmi.getAllarmeServiceSearch().find(expr);
			return att!=null;
		}
		catch(NotFoundException notFound) {
			return false;
		}
		catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public String getNextAlarmInstanceSerialId(String policyId, Logger log) throws RegistryException{
			
		Connection con = null;
		try {
			con = this.driverConfigurazione.getConnection("getNextAlarmInstanceSerialId");
			
			return AllarmiDriverUtils.getNextAlarmInstanceSerialId(policyId,
					con, log, this.driverConfigurazione.getTipoDB());
			
		}
		catch(Exception e) {
			throw new RegistryException(e.getMessage(),e);
		}
		finally {
			try {
				if(con!=null) {
					this.driverConfigurazione.releaseConnection(con);
				}
			}catch(Exception eClose) {
				// close
			}
		}
		
	}
	
	
	public void createAllarme(Allarme allarme, Logger log) throws DriverConfigurazioneException {
		try {
			this.serviceManagerAllarmi.getAllarmeService().create(allarme);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public void updateAllarme(Allarme allarme, Logger log) throws DriverConfigurazioneException {
		try {
			IdAllarme oldId = new IdAllarme();
			oldId.setNome(allarme.getNome());
//			if(allarme.get!=null && allarme.getOldIdAllarme().getNome()!=null) {
//				oldId.setNome(allarme.getOldIdAllarme().getNome());
//			}
			this.serviceManagerAllarmi.getAllarmeService().update(oldId, allarme);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public void deleteAllarme(Allarme policy, Logger log) throws DriverConfigurazioneException {
		try {
			// Il file importato potrebbe avere un identificativo diverso da quello effettivamente salvato
			if(policy.getAlias()==null) {
				this.serviceManagerAllarmi.getAllarmeService().delete(policy);
			}
			else {
				IExpression expr = this.serviceManagerAllarmi.getAllarmeService().newExpression();
				
				expr.and();
				
				if(policy.getFiltro()!=null && policy.getFiltro().getRuoloPorta()!=null && policy.getFiltro().getNomePorta()!=null) {
					expr.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, policy.getFiltro().getRuoloPorta());
					expr.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA, policy.getFiltro().getNomePorta());
				}
				else {
					expr.isNull(AttivazionePolicy.model().FILTRO.NOME_PORTA);
				}
				
				expr.equals(AttivazionePolicy.model().ALIAS, policy.getAlias());
				
				try {
					Allarme all = this.serviceManagerAllarmi.getAllarmeService().find(expr);
					this.serviceManagerAllarmi.getAllarmeService().delete(all);
				}catch(NotFoundException notFound) {}
			}
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean isAllarmeInUso(RuoloPorta ruoloPorta, String nomePorta, String alias, List<String> whereIsInUso) throws DriverRegistroServiziException {
		return false;
	}
	
	
	
	
	
	
	
	
	
	// --- Token Policy ---
	
	public List<IDGenericProperties> getAllIdGenericProperties(String tipologia) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdGenericProperties(tipologia);
	}
	public List<IDGenericProperties> getAllIdGenericProperties_validation() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION);
	}
	public List<IDGenericProperties> getAllIdGenericProperties_retrieve() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE);
	}
	public List<IDGenericProperties> getAllIdGenericProperties_attributeAuthorities() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY);
	}
	private List<IDGenericProperties> _getAllIdGenericProperties(String tipologia) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			List<GenericProperties> l = this.driverConfigurazione.getGenericProperties(tipologia);
			if(l==null || l.isEmpty()) {
				throw new DriverConfigurazioneNotFound();
			}
			List<IDGenericProperties> lNew = new ArrayList<IDGenericProperties>();
			for (GenericProperties genericProperties : l) {
				
				if(!tipologia.equals(genericProperties.getTipologia())) {
					continue;
				}
				
				IDGenericProperties id = new IDGenericProperties();
				id.setNome(genericProperties.getNome());
				id.setTipologia(tipologia);
				lNew.add(id);
			}
			return lNew;
		}catch(DriverConfigurazioneNotFound e) {
			throw e;
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public GenericProperties getGenericProperties(String tipologia, String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return _getGenericProperties(tipologia, nome);
	}
	public GenericProperties getGenericProperties(IDGenericProperties idGP) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return _getGenericProperties(idGP.getTipologia(), idGP.getNome());
	}
	public GenericProperties getGenericProperties_validation(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION, nome);
	}
	public GenericProperties getGenericProperties_retrieve(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE, nome);
	}
	public GenericProperties getGenericProperties_attributeAuthority(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY, nome);
	}
	private GenericProperties _getGenericProperties(String tipologia, String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			GenericProperties gp = this.driverConfigurazione.getGenericProperties(tipologia, nome);
			if(gp==null) {
				throw new DriverConfigurazioneNotFound();
			}
			return gp;
		}catch(DriverConfigurazioneNotFound e) {
			throw e;
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean existsGenericProperties(String tipologia, String nome) throws DriverConfigurazioneException {
		return _existsGenericProperties(tipologia, nome);
	}
	public boolean existsGenericProperties(IDGenericProperties idGP) throws DriverConfigurazioneException {
		return _existsGenericProperties(idGP.getTipologia(), idGP.getNome());
	}
	public boolean existsGenericProperties_validation(String nome) throws DriverConfigurazioneException{
		return _existsGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION, nome);
	}
	public boolean existsGenericProperties_retrieve(String nome) throws DriverConfigurazioneException{
		return _existsGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE, nome);
	}
	public boolean existsGenericProperties_attributeAuthority(String nome) throws DriverConfigurazioneException{
		return _existsGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY, nome);
	}
	private boolean _existsGenericProperties(String tipologia, String nome) throws DriverConfigurazioneException{
		try {
			GenericProperties gp = this.driverConfigurazione.getGenericProperties(tipologia, nome);
			return gp!=null;
		}catch(DriverConfigurazioneNotFound e) {
			return false;
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}

	public void createGenericProperties(GenericProperties gp) throws DriverConfigurazioneException{
		try {
			this.driverConfigurazione.createGenericProperties(gp);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public void updateGenericProperties(GenericProperties gp) throws DriverConfigurazioneException{
		try {
			this.driverConfigurazione.updateGenericProperties(gp);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public void deleteGenericProperties(GenericProperties gp) throws DriverConfigurazioneException{
		try {
			this.driverConfigurazione.deleteGenericProperties(gp);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean isGenericPropertiesInUso(String tipologia, String nome, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		IDGenericProperties idGP = new IDGenericProperties();
		idGP.setTipologia(tipologia);
		idGP.setNome(nome);
		return this.isGenericPropertiesInUso(idGP,whereIsInUso, normalizeObjectIds);
	}
	public boolean isGenericPropertiesInUso(IDGenericProperties idGP, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		Connection con = null;
		try{
			con = this.driverRegistroServizi.getConnection("archive.isGenericPropertiesInUso");
			return DBOggettiInUsoUtils.isGenericPropertiesInUso(con, this.driverRegistroServizi.getTipoDB(), idGP, whereIsInUso, normalizeObjectIds);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverRegistroServizi.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	
	
	
	
	// Plugin Classe
	
	public List<IdPlugin> getAllIdPluginClasse() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return _getAllIdPluginClasse(null);
	}
	private List<IdPlugin> _getAllIdPluginClasse(String tipoPlugin) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			IPaginatedExpression pagExpr = this.serviceManagerPlugins.getPluginServiceSearch().newPaginatedExpression();
			if(tipoPlugin!=null) {
				pagExpr.equals(org.openspcoop2.core.plugins.Plugin.model().TIPO_PLUGIN, tipoPlugin);
			}
			List<IdPlugin> l = this.serviceManagerPlugins.getPluginServiceSearch().findAllIds(pagExpr);
			if(l==null || l.size()<=0) {
				throw new NotFoundException("Non esistono plugins");
			}
			return l;
		}catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public Plugin getPluginClasse(String tipoPlugin, String tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		IdPlugin idPluginObject = new IdPlugin();
		idPluginObject.setTipoPlugin(tipoPlugin);
		idPluginObject.setTipo(tipo);
		return this.getPluginClasse(idPluginObject);
	}
	public Plugin getPluginClasse(IdPlugin idPlugin) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		try {
			return this.serviceManagerPlugins.getPluginServiceSearch().get(idPlugin);
		}catch(NotFoundException e) {
			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
//		try {
//			IExpression expr = this.serviceManagerPlugins.getPluginServiceSearch().newExpression();
//			expr.equals(org.openspcoop2.core.plugins.Plugin.model().TIPO_PLUGIN, idPlugin.getTipoPlugin());
//			expr.equals(org.openspcoop2.core.plugins.Plugin.model().TIPO, idPlugin.getTipo());
//			return this.serviceManagerPlugins.getPluginServiceSearch().find(expr);
//		}catch(NotFoundException e) {
//			throw new DriverConfigurazioneNotFound(e.getMessage(),e);
//		}catch(Exception e) {
//			throw new DriverConfigurazioneException(e.getMessage(),e);
//		}
	}
	
	public boolean existsPluginClasse(String tipoPlugin, String tipo) throws DriverConfigurazioneException {
		IdPlugin idPluginObject = new IdPlugin();
		idPluginObject.setTipoPlugin(tipoPlugin);
		idPluginObject.setTipo(tipo);
		return this.existsPluginClasse(idPluginObject);
	}
	public boolean existsPluginClasse(IdPlugin idPlugin) throws DriverConfigurazioneException {
		try {
			return this.serviceManagerPlugins.getPluginServiceSearch().exists(idPlugin);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
//		try {
//			IExpression expr = this.serviceManagerPlugins.getPluginServiceSearch().newExpression();
//			expr.equals(org.openspcoop2.core.plugins.Plugin.model().TIPO_PLUGIN, idPlugin.getTipoPlugin());
//			expr.equals(org.openspcoop2.core.plugins.Plugin.model().TIPO, idPlugin.getTipo());
//			NonNegativeNumber nn = this.serviceManagerPlugins.getPluginServiceSearch().count(expr);
//			if(nn!=null) {
//				if(nn.longValue()>1) {
//					throw new MultipleResultException("Trovate "+nn.longValue()+" occorrenze");
//				}
//				else if(nn.longValue()==1) {
//					return true;
//				}
//			}
//			return false;
//		}catch(Exception e) {
//			throw new DriverConfigurazioneException(e.getMessage(),e);
//		}
	}
	
	
	public void createPluginClasse(Plugin plugin) throws DriverConfigurazioneException {
		try {
			this.serviceManagerPlugins.getPluginService().create(plugin);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public void updatePluginClasse(Plugin plugin) throws DriverConfigurazioneException {
		try {
			IdPlugin oldId = new IdPlugin();
			oldId.setTipoPlugin(plugin.getTipoPlugin());
			oldId.setTipo(plugin.getTipo());
			if(plugin.getOldIdPlugin()!=null && plugin.getOldIdPlugin().getTipoPlugin()!=null) {
				oldId.setTipoPlugin(plugin.getOldIdPlugin().getTipoPlugin());
			}
			if(plugin.getOldIdPlugin()!=null && plugin.getOldIdPlugin().getTipo()!=null) {
				oldId.setTipo(plugin.getOldIdPlugin().getTipo());
			}
			this.serviceManagerPlugins.getPluginService().update(oldId, plugin);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public void deletePluginClasse(Plugin plugin) throws DriverConfigurazioneException {
		try {
			this.serviceManagerPlugins.getPluginService().delete(plugin);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean isPluginClasseInUso(IdPlugin idPlugin, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverConfigurazioneException {
		Connection con = null;
		try{
			con = this.driverConfigurazione.getConnection("archive.isPluginClasseInUso");
			return DBOggettiInUsoUtils.isPluginInUso(con, this.driverRegistroServizi.getTipoDB(), 
					idPlugin.getClassName(), idPlugin.getLabel(), idPlugin.getTipoPlugin(), idPlugin.getTipo(), 
					whereIsInUso, normalizeObjectIds);
		}
		catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		finally{
			try{
				this.driverConfigurazione.releaseConnection(con);
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	
	
	
	
	
	
	
	// --- Plugin Archivio ---
	
	public List<String> getAllIdPluginArchivio() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			RegistroPlugins registroPlugins = this.driverConfigurazione.getRegistroPlugins();
			List<String> l = new ArrayList<String>();
			if(registroPlugins!=null && registroPlugins.sizePluginList()>0) {
				for (RegistroPlugin registroPlugin : registroPlugins.getPluginList()) {
					l.add(registroPlugin.getNome());
				}
			}
			if(l==null || l.isEmpty()) {
				throw new DriverConfigurazioneNotFound();
			}
			return l;
		}catch(DriverConfigurazioneNotFound e) {
			throw e;
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public RegistroPlugin getPluginArchivio(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		try {
			RegistroPlugins registroPlugins = this.driverConfigurazione.getRegistroPlugins();
			RegistroPlugin rp = null;
			if(registroPlugins!=null && registroPlugins.sizePluginList()>0) {
				for (RegistroPlugin registroPlugin : registroPlugins.getPluginList()) {
					if(nome.equals(registroPlugin.getNome())) {
						rp = registroPlugin;
						break;
					}
				}
			}
			if(rp==null) {
				throw new DriverConfigurazioneNotFound();
			}
			return rp;
		}catch(DriverConfigurazioneNotFound e) {
			throw e;
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean existsPluginArchivio(String nome) throws DriverConfigurazioneException {
		try {
			RegistroPlugin rp = getPluginArchivio(nome);
			return rp!=null;
		}catch(DriverConfigurazioneNotFound e) {
			return false;
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}

	protected void updatePosizioneBeforeCreate(RegistroPlugin rp) throws DriverConfigurazioneException{
		try {
			// calcolo prossima posizione
			int posizione = this.driverConfigurazione.getMaxPosizioneRegistroPlugin() + 1;
			rp.setPosizione(posizione);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	protected void updateDate(RegistroPlugin rp) throws DriverConfigurazioneException{
		try {
			// aggiorno tutte le date
			rp.setData(DateManager.getDate());
			if(rp.sizeArchivioList()>0) {
				for (RegistroPluginArchivio rpa : rp.getArchivioList()) {
					rpa.setData(DateManager.getDate());
				}
			}
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public void createPluginArchivio(RegistroPlugin rp) throws DriverConfigurazioneException{
		updatePosizioneBeforeCreate(rp);
		updateDate(rp);
		try {
			this.driverConfigurazione.createRegistroPlugin(rp);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public void updatePluginArchivio(RegistroPlugin rp) throws DriverConfigurazioneException{
		updateDate(rp);
		try {
			this.driverConfigurazione.updateRegistroPlugin(rp);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public void deletePluginArchivio(RegistroPlugin rp) throws DriverConfigurazioneException{
		try {
			this.driverConfigurazione.deleteRegistroPlugin(rp);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean isPluginArchivioInUso(String nome, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		return false;
	}
	
	
	
	
	
	
	// --- Url Invocazione Regole ---
	
	public List<String> getAllIdUrlInvocazioneRegola() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try {
			ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = this.driverConfigurazione.getConfigurazioneGenerale().getUrlInvocazione();
			List<String> l = new ArrayList<String>();
			if(configurazioneUrlInvocazione!=null && configurazioneUrlInvocazione.sizeRegolaList()>0) {
				for (ConfigurazioneUrlInvocazioneRegola regola : configurazioneUrlInvocazione.getRegolaList()) {
					l.add(regola.getNome());
				}
			}
			if(l==null || l.isEmpty()) {
				throw new DriverConfigurazioneNotFound();
			}
			return l;
		}catch(DriverConfigurazioneNotFound e) {
			throw e;
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public ConfigurazioneUrlInvocazioneRegola getUrlInvocazioneRegola(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		try {
			return this.driverConfigurazione.getUrlInvocazioneRegola(nome);
		}catch(DriverConfigurazioneNotFound e) {
			throw e;
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean existsUrlInvocazioneRegola(String nome) throws DriverConfigurazioneException {
		try {
			return this.driverConfigurazione.existsUrlInvocazioneRegola(nome);
		}catch(DriverConfigurazioneNotFound e) {
			return false;
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}

	protected void updatePosizioneBeforeCreate(ConfigurazioneUrlInvocazioneRegola regola) throws DriverConfigurazioneException{
		try {
			// calcolo prossima posizione
			int posizione = ConfigurazionePdDUtils.getProssimaPosizioneUrlInvocazioneRegola(this.driverConfigurazione.getConfigurazioneGenerale());
			regola.setPosizione(posizione);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public void createUrlInvocazioneRegola(ConfigurazioneUrlInvocazioneRegola regola) throws DriverConfigurazioneException{
		updatePosizioneBeforeCreate(regola);
		try {
			this.driverConfigurazione.createUrlInvocazioneRegola(regola);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public void updateUrlInvocazioneRegola(ConfigurazioneUrlInvocazioneRegola regola) throws DriverConfigurazioneException{
		try {
			this.driverConfigurazione.updateUrlInvocazioneRegola(regola);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public void deleteUrlInvocazioneRegola(ConfigurazioneUrlInvocazioneRegola regola) throws DriverConfigurazioneException{
		try {
			this.driverConfigurazione.deleteUrlInvocazioneRegola(regola);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	public boolean isUrlInvocazioneRegolaInUso(String nome, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverRegistroServiziException {
		return false;
	}
	
	
	
	
	
	// --- Configurazione (Url Invocazione) ---
	
	public void updateConfigurazione_UrlInvocazione(ConfigurazioneUrlInvocazione configurazione) throws DriverConfigurazioneException{
		try {
			Configurazione config = this.driverConfigurazione.getConfigurazioneGenerale();
			if(config.getUrlInvocazione()==null) {
				config.setUrlInvocazione(configurazione);
			}
			else {
				config.getUrlInvocazione().setBaseUrl(configurazione.getBaseUrl());
				config.getUrlInvocazione().setBaseUrlFruizione(configurazione.getBaseUrlFruizione());
			}
			this.driverConfigurazione.updateConfigurazione(config);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	// --- ConfigurazionePdD ---
	
	public void updateConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException{
		if(configurazione.getRoutingTable()!=null){
			this.driverConfigurazione.updateRoutingTable(configurazione.getRoutingTable());
		}
		if(configurazione.getAccessoRegistro()!=null){
			this.driverConfigurazione.updateAccessoRegistro(configurazione.getAccessoRegistro());
		}
		if(configurazione.getGestioneErrore()!=null){
			if(configurazione.getGestioneErrore().getComponenteCooperazione()!=null){
				this.driverConfigurazione.updateGestioneErroreComponenteCooperazione(configurazione.getGestioneErrore().getComponenteCooperazione());
			}
			if(configurazione.getGestioneErrore().getComponenteIntegrazione()!=null){
				this.driverConfigurazione.updateGestioneErroreComponenteIntegrazione(configurazione.getGestioneErrore().getComponenteIntegrazione());
			}
		}
		if(configurazione.getStatoServiziPdd()!=null){
			this.driverConfigurazione.updateStatoServiziPdD(configurazione.getStatoServiziPdd());
		}
		if(configurazione.getSystemProperties()!=null){
			this.driverConfigurazione.updateSystemPropertiesPdD(configurazione.getSystemProperties());
		}
		this.driverConfigurazione.updateConfigurazione(configurazione);
	}
	public void deleteConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException{
		this.driverConfigurazione.deleteConfigurazione(configurazione);
	}

	public Configurazione getConfigurazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		Configurazione configurazione = this.driverConfigurazione.getConfigurazioneGenerale();
		configurazione.setRoutingTable(this.driverConfigurazione.getRoutingTable());
		configurazione.setAccessoRegistro(this.driverConfigurazione.getAccessoRegistro());
		try{
			configurazione.setAccessoConfigurazione(this.driverConfigurazione.getAccessoConfigurazione());
		}catch(DriverConfigurazioneNotFound notFound){}
		try{
			configurazione.setAccessoDatiAutorizzazione(this.driverConfigurazione.getAccessoDatiAutorizzazione());
		}catch(DriverConfigurazioneNotFound notFound){}
		GestioneErrore gestioneErroreCooperazione = null;
		GestioneErrore gestioneErroreIntegrazione = null;
		try{
			gestioneErroreCooperazione = this.driverConfigurazione.getGestioneErroreComponenteCooperazione();
		}catch(DriverConfigurazioneNotFound notFound){}
		try{
			gestioneErroreIntegrazione = this.driverConfigurazione.getGestioneErroreComponenteIntegrazione();
		}catch(DriverConfigurazioneNotFound notFound){}
		if(gestioneErroreCooperazione!=null || gestioneErroreIntegrazione!=null){
			if(configurazione.getGestioneErrore()==null){
				configurazione.setGestioneErrore(new ConfigurazioneGestioneErrore());
			}
			if(gestioneErroreCooperazione!=null){
				configurazione.getGestioneErrore().setComponenteCooperazione(gestioneErroreCooperazione);
			}
			if(gestioneErroreIntegrazione!=null){
				configurazione.getGestioneErrore().setComponenteIntegrazione(gestioneErroreIntegrazione);
			}
		}
		try{
			configurazione.setStatoServiziPdd(this.driverConfigurazione.getStatoServiziPdD());
		}catch(DriverConfigurazioneNotFound notFound){}
		try{
			configurazione.setSystemProperties(this.driverConfigurazione.getSystemPropertiesPdD());
		}catch(DriverConfigurazioneNotFound notFound){}
		try{
			configurazione.setGenericPropertiesList(this.driverConfigurazione.getGenericProperties());
		}catch(DriverConfigurazioneNotFound notFound){}
		
		return configurazione;
	}
}
