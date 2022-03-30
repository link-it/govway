/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;

/**	
 * AccordiSevizioParteSpecificaPorteApplicativeMappingInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteSpecificaPorteApplicativeMappingInfo {

	private List<MappingErogazionePortaApplicativa> listaMappingErogazione = null;
	private MappingErogazionePortaApplicativa mappingSelezionato = null;
	private MappingErogazionePortaApplicativa mappingDefault = null;
	private String mappingLabel = null;
	private String[] listaMappingLabels = null;
	private String[] listaMappingValues = null;
	private List<String> azioniOccupate = new ArrayList<>();
	private String nomeNuovaConfigurazione = null;
	private boolean paMappingSelezionatoMulti = false;
	
	public List<MappingErogazionePortaApplicativa> getListaMappingErogazione() {
		return this.listaMappingErogazione;
	}
	public void setListaMappingErogazione(List<MappingErogazionePortaApplicativa> listaMappingErogazione) {
		this.listaMappingErogazione = listaMappingErogazione;
	}
	public MappingErogazionePortaApplicativa getMappingSelezionato() {
		return this.mappingSelezionato;
	}
	public void setMappingSelezionato(MappingErogazionePortaApplicativa mappingSelezionato) {
		this.mappingSelezionato = mappingSelezionato;
	}
	public MappingErogazionePortaApplicativa getMappingDefault() {
		return this.mappingDefault;
	}
	public void setMappingDefault(MappingErogazionePortaApplicativa mappingDefault) {
		this.mappingDefault = mappingDefault;
	}
	public String getMappingLabel() {
		return this.mappingLabel;
	}
	public void setMappingLabel(String mappingLabel) {
		this.mappingLabel = mappingLabel;
	}
	public String[] getListaMappingLabels() {
		return this.listaMappingLabels;
	}
	public void setListaMappingLabels(String[] listaMappingLabels) {
		this.listaMappingLabels = listaMappingLabels;
	}
	public String[] getListaMappingValues() {
		return this.listaMappingValues;
	}
	public void setListaMappingValues(String[] listaMappingValues) {
		this.listaMappingValues = listaMappingValues;
	}
	public List<String> getAzioniOccupate() {
		return this.azioniOccupate;
	}
	public void setAzioniOccupate(List<String> azioniOccupate) {
		this.azioniOccupate = azioniOccupate;
	}
	public String getNomeNuovaConfigurazione() {
		return this.nomeNuovaConfigurazione;
	}
	public void setNomeNuovaConfigurazione(String nomeNuovaConfigurazione) {
		this.nomeNuovaConfigurazione = nomeNuovaConfigurazione;
	}
	public boolean isPaMappingSelezionatoMulti() {
		return this.paMappingSelezionatoMulti;
	}
	public void setPaMappingSelezionatoMulti(boolean paMappingSelezionatoMulti) {
		this.paMappingSelezionatoMulti = paMappingSelezionatoMulti;
	}
}
