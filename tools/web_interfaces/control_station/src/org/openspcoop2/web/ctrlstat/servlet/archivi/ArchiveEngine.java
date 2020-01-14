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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;

/**
 * ArchiveEngine
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveEngine extends org.openspcoop2.protocol.engine.archive.AbstractArchiveEngine {

	private ArchiviCore archiviCore;
	private boolean smista;
	private String userLogin;
	
	public ArchiveEngine(DriverRegistroServiziDB driverRegistroServizi,
			DriverConfigurazioneDB driverConfigurazione,
			JDBCServiceManager serviceManagerControlloTraffico,
			ArchiviCore archiviCore,boolean smista,String userLogin) {
		super(driverRegistroServizi, driverConfigurazione, serviceManagerControlloTraffico);
		this.archiviCore = archiviCore;
		this.smista = smista;
		this.userLogin = userLogin;
	}
	
	// --- Users ---
	
	@Override
	public boolean isVisioneOggettiGlobale(String userLogin) {
		return this.archiviCore.isVisioneOggettiGlobale(userLogin);
	}

	
	// --- PDD ---
	
	@Override
	public void createPortaDominio(PortaDominio pdd)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, pdd);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public void updatePortaDominio(PortaDominio pdd)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, pdd);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public void deletePortaDominio(PortaDominio pdd)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, pdd);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	// --- GRUPPI ---
	
	@Override
	public void createGruppo(Gruppo gruppo) throws DriverRegistroServiziException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, gruppo);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	@Override
	public void updateGruppo(Gruppo gruppo) throws DriverRegistroServiziException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, gruppo);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteGruppo(Gruppo gruppo) throws DriverRegistroServiziException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, gruppo);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	
	// --- RUOLI ---
	
	@Override
	public void createRuolo(Ruolo ruolo) throws DriverRegistroServiziException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, ruolo);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	@Override
	public void updateRuolo(Ruolo ruolo) throws DriverRegistroServiziException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, ruolo);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteRuolo(Ruolo ruolo) throws DriverRegistroServiziException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, ruolo);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	// --- SCOPE ---
	
	@Override
	public void createScope(Scope scope) throws DriverRegistroServiziException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, scope);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	@Override
	public void updateScope(Scope scope) throws DriverRegistroServiziException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, scope);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteScope(Scope scope) throws DriverRegistroServiziException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, scope);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	// --- Soggetti Registro ---
	
	@Override
	public void createSoggettoRegistro(Soggetto soggetto)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, soggetto);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public void updateSoggettoRegistro(Soggetto soggetto)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, soggetto);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public void deleteSoggettoRegistro(Soggetto soggetto)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, soggetto);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}

	
	// --- Soggetti Configurazione ---
	
	@Override
	public void createSoggettoConfigurazione(
			org.openspcoop2.core.config.Soggetto soggetto)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, soggetto);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	@Override
	public void updateSoggettoConfigurazione(
			org.openspcoop2.core.config.Soggetto soggetto)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, soggetto);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	@Override
	public void deleteSoggettoConfigurazione(
			org.openspcoop2.core.config.Soggetto soggetto)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, soggetto);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}

	
	// --- Servizi Applicativi ---
	
	@Override
	public void createServizioApplicativo(
			ServizioApplicativo servizioApplicativo)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, servizioApplicativo);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	@Override
	public void updateServizioApplicativo(
			ServizioApplicativo servizioApplicativo)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, servizioApplicativo);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	@Override
	public void deleteServizioApplicativo(
			ServizioApplicativo servizioApplicativo)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, servizioApplicativo);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}

	
	// --- Accordi di Cooperazione ---

	@Override
	public void createAccordoCooperazione(
			AccordoCooperazione accordoCooperazione)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, accordoCooperazione);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public void updateAccordoCooperazione(
			AccordoCooperazione accordoCooperazione)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, accordoCooperazione);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public void deleteAccordoCooperazione(
			AccordoCooperazione accordoCooperazione)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, accordoCooperazione);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}

	
	// --- Accordi di Servizio Parte Comune ---
	
	@Override
	public void createAccordoServizioParteComune(
			AccordoServizioParteComune accordoServizioParteComune)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, accordoServizioParteComune);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public void updateAccordoServizioParteComune(
			AccordoServizioParteComune accordoServizioParteComune)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, accordoServizioParteComune);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public void deleteAccordoServizioParteComune(
			AccordoServizioParteComune accordoServizioParteComune)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, accordoServizioParteComune);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}

	
	// --- Accordi di Servizio Parte Specifica ---
	
	@Override
	public void createAccordoServizioParteSpecifica(
			AccordoServizioParteSpecifica accordoServizioParteSpecifica)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, accordoServizioParteSpecifica);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public void updateAccordoServizioParteSpecifica(
			AccordoServizioParteSpecifica accordoServizioParteSpecifica)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, accordoServizioParteSpecifica);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public void deleteAccordoServizioParteSpecifica(
			AccordoServizioParteSpecifica accordoServizioParteSpecifica)
			throws DriverRegistroServiziException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, accordoServizioParteSpecifica);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	// --- Mapping Erogazione ---
	
	@Override	
	public void createMappingErogazione(String nome, String descrizione, boolean isDefault, IDServizio idServizio, IDPortaApplicativa idPortaApplicativa) throws DriverRegistroServiziException {
		try{
			MappingErogazionePortaApplicativa mapping = new MappingErogazionePortaApplicativa();
			mapping.setIdServizio(idServizio);
			mapping.setIdPortaApplicativa(idPortaApplicativa);
			mapping.setNome(nome);
			mapping.setDescrizione(descrizione);
			mapping.setDefault(isDefault); 
			
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, mapping);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa) throws DriverRegistroServiziException {
		try{
			MappingErogazionePortaApplicativa mapping = new MappingErogazionePortaApplicativa();
			mapping.setIdServizio(idServizio);
			mapping.setIdPortaApplicativa(idPortaApplicativa);
			
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, mapping);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	// --- Mapping Fruizione ---
	
	@Override
	public void createMappingFruizione(String nome, String descrizione, boolean isDefault, IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata) throws DriverRegistroServiziException {
		try{
			MappingFruizionePortaDelegata mapping = new MappingFruizionePortaDelegata();
			mapping.setIdFruitore(idFruitore);
			mapping.setIdServizio(idServizio);
			mapping.setIdPortaDelegata(idPortaDelegata);
			mapping.setNome(nome);
			mapping.setDescrizione(descrizione);
			mapping.setDefault(isDefault); 
			
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, mapping);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata) throws DriverRegistroServiziException {
		try{
			MappingFruizionePortaDelegata mapping = new MappingFruizionePortaDelegata();
			mapping.setIdFruitore(idFruitore);
			mapping.setIdServizio(idServizio);
			mapping.setIdPortaDelegata(idPortaDelegata);
			
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, mapping);
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	


	
	// --- Porte Delegate ---
	
	@Override
	public void createPortaDelegata(PortaDelegata portaDelegata)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, portaDelegata);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	@Override
	public void updatePortaDelegata(PortaDelegata portaDelegata)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, portaDelegata);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	@Override
	public void deletePortaDelegata(PortaDelegata portaDelegata)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, portaDelegata);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}

	
	// --- Porte Applicative ---
	
	@Override
	public void createPortaApplicativa(PortaApplicativa portaApplicativa)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, portaApplicativa);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}

	@Override
	public void updatePortaApplicativa(PortaApplicativa portaApplicativa)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, portaApplicativa);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	@Override
	public void deletePortaApplicativa(PortaApplicativa portaApplicativa)
			throws DriverConfigurazioneException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, portaApplicativa);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}


	// --- Controllo Traffico (Configurazione) ---
	
	@Override
	public void updateControlloTraffico_configurazione(ConfigurazioneGenerale configurazione) throws DriverConfigurazioneException{
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, configurazione);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteControlloTraffico_Configurazione(ConfigurazioneGenerale configurazione) throws DriverConfigurazioneException{
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, configurazione);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	
	// --- Controllo Traffico (ConfigurazionePolicy) ---
	
	@Override
	public void createControlloTraffico_configurationPolicy(ConfigurazionePolicy policy) throws DriverConfigurazioneException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, policy);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	@Override
	public void updateControlloTraffico_configurationPolicy(ConfigurazionePolicy policy) throws DriverConfigurazioneException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, policy);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteControlloTraffico_configurationPolicy(ConfigurazionePolicy policy) throws DriverConfigurazioneException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, policy);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	
	// --- Controllo Traffico (AttivazionePolicy) ---
	
	@Override
	public void createControlloTraffico_activePolicy(AttivazionePolicy policy) throws DriverConfigurazioneException {
		try{
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, policy);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	@Override
	public void updateControlloTraffico_activePolicy(AttivazionePolicy policy) throws DriverConfigurazioneException {
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, policy);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteControlloTraffico_activePolicy(AttivazionePolicy policy) throws DriverConfigurazioneException {
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, policy);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	
	// --- Token Policy ---
	
	@Override
	public void createGenericProperties(GenericProperties gp) throws DriverConfigurazioneException{
		try {
			this.archiviCore.performCreateOperation(this.userLogin, this.smista, gp);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	@Override
	public void updateGenericProperties(GenericProperties gp) throws DriverConfigurazioneException{
		try {
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, gp);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	@Override
	public void deleteGenericProperties(GenericProperties gp) throws DriverConfigurazioneException{
		try {
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, gp);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	
	// --- ConfigurazionePdD ---
	
	@Override
	public void updateConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException{
		try{
			this.archiviCore.performUpdateOperation(this.userLogin, this.smista, configurazione);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	@Override
	public void deleteConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException{
		try{
			this.archiviCore.performDeleteOperation(this.userLogin, this.smista, configurazione);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
}
