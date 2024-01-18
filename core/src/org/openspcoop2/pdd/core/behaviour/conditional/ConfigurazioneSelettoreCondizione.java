/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.behaviour.conditional;

/**
 * ConfigurazioneSelettoreCondizione
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneSelettoreCondizione {

	private TipoSelettore tipoSelettore; // required se la proprietà selectionByFilter=true 
	private String pattern; // exprRegular o xpath o jsonPath o template
	private String prefix;
	private String suffix;
		
	public TipoSelettore getTipoSelettore() {
		return this.tipoSelettore;
	}
	public void setTipoSelettore(TipoSelettore tipoSelettore) {
		this.tipoSelettore = tipoSelettore;
	}
	public String getPattern() {
		return this.pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getPrefix() {
		return this.prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return this.suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}
