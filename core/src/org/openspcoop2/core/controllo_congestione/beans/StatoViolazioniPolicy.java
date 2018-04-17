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


package org.openspcoop2.core.controllo_congestione.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * StatoViolazioniPolicy 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoViolazioniPolicy {

	private List<String> violazioni = new ArrayList<>();
	private List<String> violazioniWarningOnly = new ArrayList<>();
	
	private static String RAGGRUPPAMENTO_DISABILITATO = "*";
	
	private static String SEPARATOR_VIOLAZIONE = "\n- ";
	private static String SEPARATOR_TRA_VIOLATE_E_WARNING = "\n\n";
	private static String WARN = "in modalità WarningOnly";
	
	public StatoViolazioniPolicy(String statoAttuale) {
		if(statoAttuale==null) {
			return;
		}
		String violate = null;
		String violateWarningOnly = null;
		if(statoAttuale.contains(SEPARATOR_TRA_VIOLATE_E_WARNING)) {
			String [] tmp = statoAttuale.split(SEPARATOR_TRA_VIOLATE_E_WARNING);
			for (int i = 0; i < tmp.length; i++) {
				String entry = tmp[i];
				if(entry.contains(WARN)) {
					violateWarningOnly = entry;
				}
				else {
					violate = entry;
				}
			}
		}
		else {
			if(statoAttuale.contains(WARN)) {
				violateWarningOnly = statoAttuale;
			}
			else {
				violate = statoAttuale;
			}
		}
		if(violate!=null) {
			if(violate.contains(SEPARATOR_VIOLAZIONE)) {
				String [] tmp = statoAttuale.split(SEPARATOR_VIOLAZIONE);
				if(tmp!=null && tmp.length>1) {
					// la prima riga e' l'intestazione
					for (int i = 1; i < tmp.length; i++) {
						String entry = tmp[i];
						this.violazioni.add(entry);
					}
				}
			}
			else {
				this.violazioni.add(violate);
			}
		}
		if(violateWarningOnly!=null) {
			if(violateWarningOnly.contains(SEPARATOR_VIOLAZIONE)) {
				String [] tmp = statoAttuale.split(SEPARATOR_VIOLAZIONE);
				if(tmp!=null && tmp.length>1) {
					// la prima riga e' l'intestazione
					for (int i = 1; i < tmp.length; i++) {
						String entry = tmp[i];
						this.violazioniWarningOnly.add(entry);
					}
				}
			}
			else {
				this.violazioniWarningOnly.add(violateWarningOnly);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer();
		if(this.violazioni.size()>0) {
			if(this.violazioni.size()==1) {
				bf.append("Rilevata violazione della policy: ");
			}
			else {
				bf.append("Rilevate ").append(this.violazioni.size()).append(" violazioni della policy: ");
			}
			for (String violazione : this.violazioni) {
				bf.append(SEPARATOR_VIOLAZIONE);
				bf.append(violazione);
			}
		}
		if(this.violazioniWarningOnly.size()>0) {
			if(this.violazioni.size()>0) {
				bf.append(SEPARATOR_TRA_VIOLATE_E_WARNING);
			}
			if(this.violazioniWarningOnly.size()==1) {
				bf.append("Rilevata violazione (in modalità WarningOnly) della policy: ");
			}
			else {
				bf.append("Rilevate ").append(this.violazioniWarningOnly.size()).append(" violazioni ("+WARN+") della policy: ");
			}
			for (String violazione : this.violazioniWarningOnly) {
				bf.append(SEPARATOR_VIOLAZIONE);
				bf.append(violazione);
			}
		}
		if(bf.length()>0)
			return bf.toString();
		else
			return null;
	}
	
	public void addViolazione(String gruppo,boolean warningOnly) {
		if(warningOnly) {
			this._addViolazione(gruppo, this.violazioniWarningOnly);
		}
		else {
			this._addViolazione(gruppo, this.violazioni);
		}
	}
	public boolean existsViolazione(String gruppo,boolean warningOnly) {
		if(warningOnly) {
			return this._existsViolazione(gruppo, this.violazioniWarningOnly);
		}
		else {
			return this._existsViolazione(gruppo, this.violazioni);
		}
	}
	public boolean removeViolazione(String gruppo,boolean warningOnly) {
		if(warningOnly) {
			return this._removeViolazione(gruppo, this.violazioniWarningOnly);
		}
		else {
			return this._removeViolazione(gruppo, this.violazioni);
		}
	}
	public int sizeViolazioni(boolean warningOnly) {
		if(warningOnly) {
			return this.violazioniWarningOnly.size();
		}
		else {
			return this.violazioni.size();
		}
	}

	
	private void _addViolazione(String gruppo, List<String> violazioni) {
		if(gruppo==null || gruppo.equals("") || gruppo.equalsIgnoreCase("Disabilitato")) {
			gruppo = RAGGRUPPAMENTO_DISABILITATO;
		}
		if(violazioni.contains(gruppo)==false) {
			violazioni.add(gruppo);
		}
	}
	
	private boolean _existsViolazione(String gruppo,  List<String> violazioni) {
		if(gruppo==null || gruppo.equals("") || gruppo.equalsIgnoreCase("Disabilitato")) {
			gruppo = RAGGRUPPAMENTO_DISABILITATO;
		}
		return violazioni.contains(gruppo);
	}
	
	private boolean _removeViolazione(String gruppo, List<String> violazioni) {
		if(gruppo==null || gruppo.equals("") || gruppo.equalsIgnoreCase("Disabilitato")) {
			gruppo = RAGGRUPPAMENTO_DISABILITATO;
		}
		int i = -1;
		boolean found = false;
		for (i = 0; i < violazioni.size(); i++) {
			if(violazioni.get(i).equals(gruppo)) {
				found = true;
				break;
			}
		}
		if(found) {
			violazioni.remove(i);
			return true;
		}
		return false;
	}
}
