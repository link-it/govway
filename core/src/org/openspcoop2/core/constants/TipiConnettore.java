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


package org.openspcoop2.core.constants;

/**
 * Contiene i tipi di connettori di default
 *
 * @author corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum TipiConnettore {

	DISABILITATO ("disabilitato",null,null),
	HTTP ("http",null,null),
	HTTPS ("https",null,"Consente di ridefinire i certificati server e/o client"),
	JMS ("jms",null,"Consente di consegnare la richiesta su una coda di un broker JMS"),
	NULL ("null",null,"Consuma la richiesta e ritorna una risposta vuota"),
	NULLECHO("nullEcho","echo","Restituisce in risposta la richiesta ricevuta"),
	FILE ("file",null,"Consente di salvare la richiesta su filesystem e restituire una risposta"),
	CUSTOM("custom","plugin",null);
	
	
	private final String nome;
	private final String label;
	private final String note;

	TipiConnettore(String nome, String label, String note)
	{
		this.nome = nome;
		this.label = label;
		this.note = note;
	}

	public String getNome()
	{
		return this.nome;
	}

	public String getLabel() {
		if(this.label!=null) {
			return this.label;
		}
		return this.nome;
	}

	public String getNote() {
		return this.note;
	}
	
	@Override
	public String toString() {
		return this.nome;
	}
	
	public static TipiConnettore toEnumFromName(String name) {
		TipiConnettore [] tipi = TipiConnettore.values();
		for (int i = 0; i < tipi.length; i++) {
			TipiConnettore tipo = tipi[i];
			if(tipo.getNome().equals(name)) {
				return tipo;
			}
		}
		return null;
	}
}

