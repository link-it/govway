/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.protocol.information_missing.Openspcoop2;


/**
 * Archivio
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Archive {

	private ArchiveSortedMap<ArchivePdd> pdd = new ArchiveSortedMap<ArchivePdd>();
	
	private ArchiveSortedMap<ArchiveGruppo> gruppi = new ArchiveSortedMap<ArchiveGruppo>();
	
	private ArchiveSortedMap<ArchiveRuolo> ruoli = new ArchiveSortedMap<ArchiveRuolo>();
	
	private ArchiveSortedMap<ArchiveScope> scope = new ArchiveSortedMap<ArchiveScope>();
	
	private Openspcoop2 informationMissing;
		
	private ArchiveSortedMap<ArchiveSoggetto> soggetti = new ArchiveSortedMap<ArchiveSoggetto>();
	
	private ArchiveSortedMap<ArchiveServizioApplicativo> serviziApplicativi = new ArchiveSortedMap<ArchiveServizioApplicativo>();
	private ArchiveSortedMap<ArchivePortaDelegata> porteDelegate = new ArchiveSortedMap<ArchivePortaDelegata>();
	private ArchiveSortedMap<ArchivePortaApplicativa> porteApplicative = new ArchiveSortedMap<ArchivePortaApplicativa>();
	
	private ArchiveSortedMap<ArchiveAccordoCooperazione> accordiCooperazione = new ArchiveSortedMap<ArchiveAccordoCooperazione>();
	private ArchiveSortedMap<ArchiveAccordoServizioParteComune> accordiServizioParteComune = new ArchiveSortedMap<ArchiveAccordoServizioParteComune>();
	private ArchiveSortedMap<ArchiveAccordoServizioComposto> accordiServizioComposto = new ArchiveSortedMap<ArchiveAccordoServizioComposto>();
	private ArchiveSortedMap<ArchiveAccordoServizioParteSpecifica> accordiServizioParteSpecifica = new ArchiveSortedMap<ArchiveAccordoServizioParteSpecifica>();
	private ArchiveSortedMap<ArchiveFruitore> accordiFruitori = new ArchiveSortedMap<ArchiveFruitore>();
	
	private ArchiveSortedMap<ArchiveConfigurationPolicy> controlloTraffico_configurationPolicies = new ArchiveSortedMap<ArchiveConfigurationPolicy>();
	private ArchiveSortedMap<ArchiveActivePolicy> controlloTraffico_activePolicies = new ArchiveSortedMap<ArchiveActivePolicy>();
	private org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale controlloTraffico_configurazione;
	
	private ArchiveSortedMap<ArchiveAllarme> allarmi = new ArchiveSortedMap<ArchiveAllarme>();
	
	private ArchiveSortedMap<ArchiveTokenPolicy> token_validation_policies = new ArchiveSortedMap<ArchiveTokenPolicy>();
	private ArchiveSortedMap<ArchiveTokenPolicy> token_retrieve_policies = new ArchiveSortedMap<ArchiveTokenPolicy>();
	
	private ArchiveSortedMap<ArchiveAttributeAuthority> attributeAuthorities = new ArchiveSortedMap<ArchiveAttributeAuthority>();
	
	private ArchiveSortedMap<ArchivePluginClasse> plugin_classi = new ArchiveSortedMap<ArchivePluginClasse>();
	private ArchiveSortedMap<ArchivePluginArchivio> plugin_archivi = new ArchiveSortedMap<ArchivePluginArchivio>();
	
	private ConfigurazioneUrlInvocazione configurazionePdD_urlInvocazione;
	private ArchiveSortedMap<ArchiveUrlInvocazioneRegola> configurazionePdD_urlInvocazione_regole = new ArchiveSortedMap<ArchiveUrlInvocazioneRegola>();
	
	private Configurazione configurazionePdD;
		
	public ConfigurazioneUrlInvocazione getConfigurazionePdD_urlInvocazione() {
		return this.configurazionePdD_urlInvocazione;
	}
	public void setConfigurazionePdD_urlInvocazione(ConfigurazioneUrlInvocazione configurazionePdD_urlInvocazione) {
		this.configurazionePdD_urlInvocazione = configurazionePdD_urlInvocazione;
	}
	
	public org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale getControlloTraffico_configurazione() {
		return this.controlloTraffico_configurazione;
	}
	public void setControlloTraffico_configurazione(
			org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale controlloTraffico_configurazione) {
		this.controlloTraffico_configurazione = controlloTraffico_configurazione;
	}
		
	public Configurazione getConfigurazionePdD() {
		return this.configurazionePdD;
	}
	public void setConfigurazionePdD(Configurazione configurazionePdD) {
		this.configurazionePdD = configurazionePdD;
	}
	
	public ArchiveSortedMap<ArchivePdd> getPdd() {
		return this.pdd;
	}
	public ArchiveSortedMap<ArchiveGruppo> getGruppi() {
		return this.gruppi;
	}
	public ArchiveSortedMap<ArchiveRuolo> getRuoli() {
		return this.ruoli;
	}
	public ArchiveSortedMap<ArchiveScope> getScope() {
		return this.scope;
	}
	public Openspcoop2 getInformationMissing() {
		return this.informationMissing;
	}
	public void setInformationMissing(Openspcoop2 informationMissing) {
		this.informationMissing = informationMissing;
	}
	public ArchiveSortedMap<ArchiveSoggetto> getSoggetti() {
		return this.soggetti;
	}
	public ArchiveSortedMap<ArchiveServizioApplicativo> getServiziApplicativi() {
		return this.serviziApplicativi;
	}
	public ArchiveSortedMap<ArchivePortaDelegata> getPorteDelegate() {
		return this.porteDelegate;
	}
	public ArchiveSortedMap<ArchivePortaApplicativa> getPorteApplicative() {
		return this.porteApplicative;
	}
	public ArchiveSortedMap<ArchiveAccordoCooperazione> getAccordiCooperazione() {
		return this.accordiCooperazione;
	}
	public ArchiveSortedMap<ArchiveAccordoServizioParteComune> getAccordiServizioParteComune() {
		return this.accordiServizioParteComune;
	}
	public ArchiveSortedMap<ArchiveAccordoServizioComposto> getAccordiServizioComposto() {
		return this.accordiServizioComposto;
	}
	public ArchiveSortedMap<ArchiveAccordoServizioParteSpecifica> getAccordiServizioParteSpecifica() {
		return this.accordiServizioParteSpecifica;
	}
	public ArchiveSortedMap<ArchiveFruitore> getAccordiFruitori() {
		return this.accordiFruitori;
	}
	public ArchiveSortedMap<ArchiveConfigurationPolicy> getControlloTraffico_configurationPolicies() {
		return this.controlloTraffico_configurationPolicies;
	}
	public ArchiveSortedMap<ArchiveActivePolicy> getControlloTraffico_activePolicies() {
		return this.controlloTraffico_activePolicies;
	}
	public ArchiveSortedMap<ArchiveAllarme> getAllarmi() {
		return this.allarmi;
	}
	public ArchiveSortedMap<ArchiveTokenPolicy> getToken_validation_policies() {
		return this.token_validation_policies;
	}
	public ArchiveSortedMap<ArchiveTokenPolicy> getToken_retrieve_policies() {
		return this.token_retrieve_policies;
	}
	public ArchiveSortedMap<ArchiveAttributeAuthority> getAttributeAuthorities() {
		return this.attributeAuthorities;
	}
	public ArchiveSortedMap<ArchivePluginClasse> getPlugin_classi() {
		return this.plugin_classi;
	}
	public ArchiveSortedMap<ArchivePluginArchivio> getPlugin_archivi() {
		return this.plugin_archivi;
	}
	public ArchiveSortedMap<ArchiveUrlInvocazioneRegola> getConfigurazionePdD_urlInvocazione_regole() {
		return this.configurazionePdD_urlInvocazione_regole;
	}
}
