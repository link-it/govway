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
	
	private ArchiveSortedMap<ArchiveConfigurationPolicy> controlloCongestione_configurationPolicies = new ArchiveSortedMap<ArchiveConfigurationPolicy>();
	private ArchiveSortedMap<ArchiveActivePolicy> controlloCongestione_activePolicies = new ArchiveSortedMap<ArchiveActivePolicy>();
	private org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale controlloCongestione_configurazione;
	
	private Configurazione configurazionePdD;
	
	public org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale getControlloCongestione_configurazione() {
		return this.controlloCongestione_configurazione;
	}
	public void setControlloCongestione_configurazione(
			org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale controlloCongestione_configurazione) {
		this.controlloCongestione_configurazione = controlloCongestione_configurazione;
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
	public ArchiveSortedMap<ArchiveConfigurationPolicy> getControlloCongestione_configurationPolicies() {
		return this.controlloCongestione_configurationPolicies;
	}
	public ArchiveSortedMap<ArchiveActivePolicy> getControlloCongestione_activePolicies() {
		return this.controlloCongestione_activePolicies;
	}
	
}
