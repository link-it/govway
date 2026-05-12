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
package org.openspcoop2.core.commons;

/**
 * Wrapper utilizzato per identificare nell'audit un'operazione di
 * abilitazione/disabilitazione dello stato di un oggetto (es. PortaApplicativa,
 * PortaDelegata) senza alterare la classe dell'oggetto trasportato.
 *
 * @author Pintori Giuliano (pintori@link.it)
 *
 */
public class StatoWrapper {

	private Object obj;
	private boolean enable;
	private String nomeGruppo;
	private String descrizioneGruppo;
	private boolean defaultGruppo;

	public StatoWrapper() {
		// default
	}

	public StatoWrapper(Object obj, boolean enable) {
		this.obj = obj;
		this.enable = enable;
	}

	public Object getObj() {
		return this.obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public boolean isEnable() {
		return this.enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getNomeGruppo() {
		return this.nomeGruppo;
	}

	public void setNomeGruppo(String nomeGruppo) {
		this.nomeGruppo = nomeGruppo;
	}

	public String getDescrizioneGruppo() {
		return this.descrizioneGruppo;
	}

	public void setDescrizioneGruppo(String descrizioneGruppo) {
		this.descrizioneGruppo = descrizioneGruppo;
	}

	public boolean isDefaultGruppo() {
		return this.defaultGruppo;
	}

	public void setDefaultGruppo(boolean defaultGruppo) {
		this.defaultGruppo = defaultGruppo;
	}

	/**
	 * Appende l'indicazione del gruppo (descrizione) all'ID quando il gruppo
	 * e' non-default oppure quando, pur essendo il default, e' stata definita
	 * una descrizione diversa da quella canonica "Predefinito".
	 */
	public static String appendDescrizioneGruppo(String id, StatoWrapper sw){
		String descr = sw.getDescrizioneGruppo();
		if(descr==null || descr.isEmpty()){
			return id;
		}
		if(sw.isDefaultGruppo() && org.openspcoop2.core.constants.Costanti.MAPPING_DESCRIZIONE_DEFAULT.equals(descr)){
			return id;
		}
		return id + " (" + descr + ")";
	}
}
