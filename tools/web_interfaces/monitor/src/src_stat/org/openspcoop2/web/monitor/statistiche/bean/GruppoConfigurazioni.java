package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GruppoConfigurazioni implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String label = null;
	List<ConfigurazioneGenerale> listaConfigurazioni = null;
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<ConfigurazioneGenerale> getListaConfigurazioni() {
		return this.listaConfigurazioni;
	}
	public void setListaConfigurazioni(
			List<ConfigurazioneGenerale> listaConfigurazioni) {
		this.listaConfigurazioni = listaConfigurazioni;
	}

	public GruppoConfigurazioni(){

	}
	public GruppoConfigurazioni(String label, List<ConfigurazioneGenerale> lista){
		this.label = label;
		this.listaConfigurazioni = lista;

		int i = 0;			
		for (ConfigurazioneGenerale configurazioneGenerale : lista) {
			this.add("" + i, configurazioneGenerale);
			i++;

		}

	}

	public void add(String key, ConfigurazioneGenerale value) {
		this.objects.put(key, value);
	}

	private Map<String, ConfigurazioneGenerale> objects = new HashMap<String, ConfigurazioneGenerale>();

	public Map<String, ConfigurazioneGenerale> getObjectsMap() {
		return this.objects;
	}
}