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
package org.openspcoop2.web.monitor.core.dynamic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;

import javax.faces.model.SelectItem;

/**
 * Ricerche
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Ricerche implements Serializable {

	private static final long serialVersionUID = -630512519085994905L;
	private List<ConfigurazioneRicerca> ricerche;
	private List<SelectItem> ricercheSelectItems;

	public void setRicerche(List<ConfigurazioneRicerca> ricerche) {
		this.ricerche = ricerche;
	}

	public List<ConfigurazioneRicerca> getRicerche() {
		return this.ricerche;
	}

	public void addRicerca(ConfigurazioneRicerca ricerca) {

		if (this.ricerche == null)
			this.ricerche = new ArrayList<ConfigurazioneRicerca>();

		this.ricerche.add(ricerca);

	}

	public List<SelectItem> getRicercheSelectItems() {

		if (this.ricercheSelectItems != null)
			return this.ricercheSelectItems;

		this.ricercheSelectItems = new ArrayList<SelectItem>();

		for (ConfigurazioneRicerca ricerca : this.ricerche)
			this.ricercheSelectItems.add(new SelectItem(ricerca.getLabel()));

		return this.ricercheSelectItems;

	}

	public ConfigurazioneRicerca getRicercaByLabel(String nomeRicercaPersonalizzata) {

		for (ConfigurazioneRicerca ricerca : this.ricerche)
			if (ricerca.getLabel().equals(nomeRicercaPersonalizzata))
				return ricerca;

		return null;
	}

}
