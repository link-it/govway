package org.openspcoop2.web.monitor.core.dynamic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;

import javax.faces.model.SelectItem;

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
