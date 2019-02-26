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
package org.openspcoop2.protocol.sdk.archive;

import org.openspcoop2.core.config.Configurazione;
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
	
	private Configurazione configurazionePdD;
	
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
	
}
