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

package org.openspcoop2.protocol.sdk.archive;

/**
 *  ArchiveCascadeConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveCascadeConfiguration {
	
	public ArchiveCascadeConfiguration(){	
	}
	public ArchiveCascadeConfiguration(boolean cascadeAllEnabled){
		
		this.cascadePdd = cascadeAllEnabled;
		
		this.cascadeGruppi = cascadeAllEnabled;
		
		this.cascadeRuoli = cascadeAllEnabled;
		
		this.cascadeScope = cascadeAllEnabled;
		
		this.cascadeSoggetti = cascadeAllEnabled;
		
		this.cascadeServiziApplicativi = cascadeAllEnabled;
		this.cascadePorteDelegate = cascadeAllEnabled;
		this.cascadePorteApplicative = cascadeAllEnabled;
		
		this.cascadeAccordoServizioParteComune = cascadeAllEnabled;
		this.cascadeAccordoServizioComposto = cascadeAllEnabled;
		this.cascadeAccordoServizioParteSpecifica = cascadeAllEnabled;
		this.cascadeAccordoCooperazione = cascadeAllEnabled;
		this.cascadeFruizioni = cascadeAllEnabled;
	}
	
	@Override
	public Object clone(){
		ArchiveCascadeConfiguration config = new ArchiveCascadeConfiguration();
		
		config.cascadePdd = this.cascadePdd;
		
		config.cascadeGruppi = this.cascadeGruppi;
		
		config.cascadeRuoli = this.cascadeRuoli;
		
		config.cascadeScope = this.cascadeScope;
		
		config.cascadeSoggetti = this.cascadeSoggetti;
		
		config.cascadeServiziApplicativi = this.cascadeServiziApplicativi;
		config.cascadePorteDelegate = this.cascadePorteDelegate;
		config.cascadePorteApplicative = this.cascadePorteApplicative;
		
		config.cascadeAccordoServizioParteComune = this.cascadeAccordoServizioParteComune;
		config.cascadeAccordoServizioComposto = this.cascadeAccordoServizioComposto;
		config.cascadeAccordoServizioParteSpecifica = this.cascadeAccordoServizioParteSpecifica;
		config.cascadeAccordoCooperazione = this.cascadeAccordoCooperazione;
		config.cascadeFruizioni = this.cascadeFruizioni;
	
		return config;
	}
	
	
	private boolean cascadePdd = false;
	
	private boolean cascadeGruppi = false;

	private boolean cascadeRuoli = false;
	
	private boolean cascadeScope = false;
	
	private boolean cascadeSoggetti = false;
	
	private boolean cascadeServiziApplicativi = false;
	private boolean cascadePorteDelegate = false;
	private boolean cascadePorteApplicative = false;
	
	private boolean cascadeAccordoServizioParteComune = false;
	private boolean cascadeAccordoServizioComposto = false;
	private boolean cascadeAccordoServizioParteSpecifica = false;
	private boolean cascadeAccordoCooperazione = false;
	private boolean cascadeFruizioni = false;
	
	private boolean cascadePolicyConfigurazione = false;
	private boolean cascadePluginConfigurazione = false;
	
	
	public boolean isCascadePdd() {
		return this.cascadePdd;
	}
	public void setCascadePdd(boolean cascadePdd) {
		this.cascadePdd = cascadePdd;
	}
	
	public boolean isCascadeGruppi() {
		return this.cascadeGruppi;
	}
	public void setCascadeGruppi(boolean cascadeGruppi) {
		this.cascadeGruppi = cascadeGruppi;
	}
	
	public boolean isCascadeRuoli() {
		return this.cascadeRuoli;
	}
	public void setCascadeRuoli(boolean cascadeRuoli) {
		this.cascadeRuoli = cascadeRuoli;
	}
	
	public boolean isCascadeScope() {
		return this.cascadeScope;
	}
	public void setCascadeScope(boolean cascadeScope) {
		this.cascadeScope = cascadeScope;
	}
	
	public boolean isCascadeFruizioni() {
		return this.cascadeFruizioni;
	}
	public void setCascadeFruizioni(boolean cascadeFruizioni) {
		this.cascadeFruizioni = cascadeFruizioni;
	}
	public boolean isCascadeSoggetti() {
		return this.cascadeSoggetti;
	}
	public void setCascadeSoggetti(boolean cascadeSoggetti) {
		this.cascadeSoggetti = cascadeSoggetti;
	}
	public boolean isCascadeServiziApplicativi() {
		return this.cascadeServiziApplicativi;
	}
	public void setCascadeServiziApplicativi(boolean cascadeServiziApplicativi) {
		this.cascadeServiziApplicativi = cascadeServiziApplicativi;
	}
	public boolean isCascadePorteDelegate() {
		return this.cascadePorteDelegate;
	}
	public void setCascadePorteDelegate(boolean cascadePorteDelegate) {
		this.cascadePorteDelegate = cascadePorteDelegate;
	}
	public boolean isCascadePorteApplicative() {
		return this.cascadePorteApplicative;
	}
	public void setCascadePorteApplicative(boolean cascadePorteApplicative) {
		this.cascadePorteApplicative = cascadePorteApplicative;
	}
	public boolean isCascadeAccordoServizioParteComune() {
		return this.cascadeAccordoServizioParteComune;
	}
	public void setCascadeAccordoServizioParteComune(
			boolean cascadeAccordoServizioParteComune) {
		this.cascadeAccordoServizioParteComune = cascadeAccordoServizioParteComune;
	}
	public boolean isCascadeAccordoServizioComposto() {
		return this.cascadeAccordoServizioComposto;
	}
	public void setCascadeAccordoServizioComposto(
			boolean cascadeAccordoServizioComposto) {
		this.cascadeAccordoServizioComposto = cascadeAccordoServizioComposto;
	}
	public boolean isCascadeAccordoServizioParteSpecifica() {
		return this.cascadeAccordoServizioParteSpecifica;
	}
	public void setCascadeAccordoServizioParteSpecifica(
			boolean cascadeAccordoServizioParteSpecifica) {
		this.cascadeAccordoServizioParteSpecifica = cascadeAccordoServizioParteSpecifica;
	}
	public boolean isCascadeAccordoCooperazione() {
		return this.cascadeAccordoCooperazione;
	}
	public void setCascadeAccordoCooperazione(boolean cascadeAccordoCooperazione) {
		this.cascadeAccordoCooperazione = cascadeAccordoCooperazione;
	}

	public boolean isCascadePolicyConfigurazione() {
		return this.cascadePolicyConfigurazione;
	}
	public void setCascadePolicyConfigurazione(boolean cascadeConfigurazione) {
		this.cascadePolicyConfigurazione = cascadeConfigurazione;
	}
	
	public boolean isCascadePluginConfigurazione() {
		return this.cascadePluginConfigurazione;
	}
	public void setCascadePluginConfigurazione(boolean cascadePluginConfigurazione) {
		this.cascadePluginConfigurazione = cascadePluginConfigurazione;
	}
}
