/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

/**
 *  ArchiveEsitoImport
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveEsitoImport {

	private ArchiveSortedMap<ArchiveEsitoImportDetail> pdd = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	
	private ArchiveSortedMap<ArchiveEsitoImportDetail> gruppi = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	
	private ArchiveSortedMap<ArchiveEsitoImportDetail> ruoli = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	
	private ArchiveSortedMap<ArchiveEsitoImportDetail> scope = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
		
	private ArchiveSortedMap<ArchiveEsitoImportDetail> soggetti = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	
	private ArchiveSortedMap<ArchiveEsitoImportDetail> serviziApplicativi = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> porteDelegate = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> porteApplicative = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	
	private ArchiveSortedMap<ArchiveEsitoImportDetail> porteDelegate_initMapping = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> porteApplicative_initMapping = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> mappingFruizioni = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> mappingErogazioni = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	
	private ArchiveSortedMap<ArchiveEsitoImportDetail> accordiCooperazione = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> accordiServizioParteComune = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> accordiServizioComposto = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> accordiServizioParteSpecifica = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> accordiServizioParteSpecificaServiziComposti = new ArchiveSortedMap<ArchiveEsitoImportDetail>();

	private ArchiveSortedMap<ArchiveEsitoImportDetail> accordiFruitori = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	
	private ArchiveEsitoImportDetailConfigurazione<org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale> controlloTraffico_configurazione;
	private ArchiveSortedMap<ArchiveEsitoImportDetail> controlloTraffico_configurationPolicies = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> controlloTraffico_activePolicies = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	
	private ArchiveSortedMap<ArchiveEsitoImportDetail> token_validation_policies = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	private ArchiveSortedMap<ArchiveEsitoImportDetail> token_retrieve_policies = new ArchiveSortedMap<ArchiveEsitoImportDetail>();
	
	private ArchiveEsitoImportDetailConfigurazione<Configurazione> configurazionePdD;

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getPdd() {
		return this.pdd;
	}
	
	public ArchiveSortedMap<ArchiveEsitoImportDetail> getGruppi() {
		return this.gruppi;
	}
	
	public ArchiveSortedMap<ArchiveEsitoImportDetail> getRuoli() {
		return this.ruoli;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getScope() {
		return this.scope;
	}
	
	public ArchiveSortedMap<ArchiveEsitoImportDetail> getSoggetti() {
		return this.soggetti;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getServiziApplicativi() {
		return this.serviziApplicativi;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getPorteDelegate() {
		return this.porteDelegate;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getPorteApplicative() {
		return this.porteApplicative;
	}
	
	public ArchiveSortedMap<ArchiveEsitoImportDetail> getPorteDelegate_initMapping() {
		return this.porteDelegate_initMapping;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getPorteApplicative_initMapping() {
		return this.porteApplicative_initMapping;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getMappingFruizioni() {
		return this.mappingFruizioni;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getMappingErogazioni() {
		return this.mappingErogazioni;
	}
	
	public ArchiveSortedMap<ArchiveEsitoImportDetail> getAccordiCooperazione() {
		return this.accordiCooperazione;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getAccordiServizioParteComune() {
		return this.accordiServizioParteComune;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getAccordiServizioComposto() {
		return this.accordiServizioComposto;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getAccordiServizioParteSpecifica() {
		return this.accordiServizioParteSpecifica;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getAccordiServizioParteSpecificaServiziComposti() {
		return this.accordiServizioParteSpecificaServiziComposti;
	}
	
	public ArchiveSortedMap<ArchiveEsitoImportDetail> getAccordiFruitori() {
		return this.accordiFruitori;
	}

	public ArchiveEsitoImportDetailConfigurazione<org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale> getControlloTraffico_configurazione() {
		return this.controlloTraffico_configurazione;
	}

	public void setControlloTraffico_configurazione(
			ArchiveEsitoImportDetailConfigurazione<org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale> controlloTraffico_configurazione) {
		this.controlloTraffico_configurazione = controlloTraffico_configurazione;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getControlloTraffico_configurationPolicies() {
		return this.controlloTraffico_configurationPolicies;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getControlloTraffico_activePolicies() {
		return this.controlloTraffico_activePolicies;
	}
	
	public ArchiveSortedMap<ArchiveEsitoImportDetail> getToken_validation_policies() {
		return this.token_validation_policies;
	}

	public ArchiveSortedMap<ArchiveEsitoImportDetail> getToken_retrieve_policies() {
		return this.token_retrieve_policies;
	}
	
	public ArchiveEsitoImportDetailConfigurazione<Configurazione> getConfigurazionePdD() {
		return this.configurazionePdD;
	}
	
	public void setConfigurazionePdD(ArchiveEsitoImportDetailConfigurazione<Configurazione> configurazionePdD) {
		this.configurazionePdD = configurazionePdD;
	}
}
