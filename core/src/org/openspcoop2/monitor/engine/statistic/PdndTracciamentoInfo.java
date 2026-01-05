/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.statistic;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDSoggetto;

/**
 * PdndTracciamentoInfoAggregazione
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PdndTracciamentoInfo {

	private List<PdndTracciamentoSoggetto> soggetti = new ArrayList<>();

	public void addSoggetto(PdndTracciamentoSoggetto soggetto) {
		if(this.soggetti==null) {
			this.soggetti = new ArrayList<>();
		}
		this.soggetti.add(soggetto);
	}
	
	public List<PdndTracciamentoSoggetto> getSoggetti() {
		return this.soggetti;
	}

	public void setSoggetti(List<PdndTracciamentoSoggetto> soggetti) {
		this.soggetti = soggetti;
	}
	
	
	public PdndTracciamentoSoggetto getInfoByNomeSoggetto(String nome, boolean searchId, boolean searchInAggregati) {
		if(this.soggetti!=null && !this.soggetti.isEmpty()) {
			for (PdndTracciamentoSoggetto PdndTracciamentoSoggetto : this.soggetti) {
				PdndTracciamentoSoggetto found = checkByNome(nome, searchId, searchInAggregati, PdndTracciamentoSoggetto);
				if(found!=null) {
					return found;
				}
			}
		}
		return null;
	}
	private PdndTracciamentoSoggetto checkByNome(String nome, boolean searchId, boolean searchInAggregati, PdndTracciamentoSoggetto soggetto) {
		if(searchId &&
				soggetto.getIdSoggetto()!=null && soggetto.getIdSoggetto().getNome()!=null && 
				soggetto.getIdSoggetto().getNome().equals(nome)) {
			return soggetto;
		}
		if(searchInAggregati && 
				soggetto.getIdSoggettiAggregati()!=null && !soggetto.getIdSoggettiAggregati().isEmpty()) {
			for (IDSoggetto idoggettoAggregato : soggetto.getIdSoggettiAggregati()) {
				if(idoggettoAggregato!=null && idoggettoAggregato.getNome()!=null && idoggettoAggregato.getNome().equals(nome)) {
					return soggetto;
				}
			}
		}
		return null;
	}
	
	public PdndTracciamentoSoggetto getInfoByIdentificativoPorta(String idPorta, boolean searchId, boolean searchInAggregati) {
		if(this.soggetti!=null && !this.soggetti.isEmpty()) {
			for (PdndTracciamentoSoggetto PdndTracciamentoSoggetto : this.soggetti) {
				PdndTracciamentoSoggetto found = checkByIdentificativoPorta(idPorta, searchId, searchInAggregati, PdndTracciamentoSoggetto);
				if(found!=null) {
					return found;
				}
			}
		}
		return null;
	}
	private PdndTracciamentoSoggetto checkByIdentificativoPorta(String idPorta, boolean searchId, boolean searchInAggregati, PdndTracciamentoSoggetto soggetto ) {
		if(searchId &&
				soggetto.getIdSoggetto()!=null && soggetto.getIdSoggetto().getCodicePorta()!=null && 
				soggetto.getIdSoggetto().getCodicePorta().equals(idPorta)) {
			return soggetto;
		}
		if(searchInAggregati && 
				soggetto.getIdSoggettiAggregati()!=null && !soggetto.getIdSoggettiAggregati().isEmpty()) {
			for (IDSoggetto idoggettoAggregato : soggetto.getIdSoggettiAggregati()) {
				if(idoggettoAggregato!=null && idoggettoAggregato.getCodicePorta()!=null && idoggettoAggregato.getCodicePorta().equals(idPorta)) {
					return soggetto;
				}
			}
		}
		return null;
	}
	
}

