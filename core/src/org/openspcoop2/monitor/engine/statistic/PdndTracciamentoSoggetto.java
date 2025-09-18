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
package org.openspcoop2.monitor.engine.statistic;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * PdndTracciamentoSoggettoAggregatore
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PdndTracciamentoSoggetto {

	private IDSoggetto idSoggetto;
	private String statoTracciamento;
	private List<IDSoggetto> idSoggettiAggregati = new ArrayList<>();
	
	public PdndTracciamentoSoggetto(IDSoggetto idSoggetto, String statoTracciamento) throws ProtocolException {
		if(idSoggetto==null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null || idSoggetto.getCodicePorta()==null) {
			throw new ProtocolException("Dati forniti  per idSoggetto incompleti ("+idSoggetto+")");
		}
		if(statoTracciamento!=null && 
				!statoTracciamento.equals(CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID) && 
				!statoTracciamento.equals(CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_ID) && 
				!statoTracciamento.equals(CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DISABLE_ID)){
			throw new ProtocolException("Stato tracciamento '"+statoTracciamento+"' non supportato");
		}
		this.idSoggetto = idSoggetto;
		this.statoTracciamento = statoTracciamento;
	}
	
	public void addSoggettoAggregato(IDSoggetto idSoggetto) throws ProtocolException {
		if(this.idSoggettiAggregati==null) {
			this.idSoggettiAggregati = new ArrayList<>();
		}
		int index = getIndexSoggettoAggregato(idSoggetto);
		if(index>=0) {
			throw new ProtocolException("Soggetto '"+idSoggetto+"' già esistente");
		}
		this.idSoggettiAggregati.add(idSoggetto);
	}
	public void removeSoggettoAggregato(IDSoggetto idSoggetto) {
		int index = getIndexSoggettoAggregato(idSoggetto);
		if(index>=0) {
			this.idSoggettiAggregati.remove(index);
		}
	}
	public int getIndexSoggettoAggregato(IDSoggetto idSoggetto) {
		int index = -1;
		if(this.idSoggettiAggregati!=null && !this.idSoggettiAggregati.isEmpty()) {
			for (int i = 0; i < this.idSoggettiAggregati.size(); i++) {
				if(this.idSoggettiAggregati.get(i).equals(idSoggetto)) {
					index = i;
					break;
				}
			}
		}
		return index;
	}
	public List<IDSoggetto> getIdSoggettiAggregati() {
		return this.idSoggettiAggregati;
	}
	
	public IDSoggetto getIdSoggetto() {
		return this.idSoggetto;
	}
	public void setIdSoggetto(IDSoggetto idSoggetto) {
		this.idSoggetto = idSoggetto;
	}
	public String getStatoTracciamento() {
		return this.statoTracciamento;
	}
	public void setStatoTracciamento(String statoTracciamento) {
		this.statoTracciamento = statoTracciamento;
	}

	
	public boolean isTracciamentoDefault() {
		return this.statoTracciamento == null || this.statoTracciamento.equals(CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID);
	}
	public boolean isTracciamentoAbilitato() {
		return this.statoTracciamento != null && this.statoTracciamento.equals(CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_ID);
	}
	public boolean isTracciamentoDisabilitato() {
		return this.statoTracciamento != null && this.statoTracciamento.equals(CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DISABLE_ID);
	}
}

