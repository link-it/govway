/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky;

/**
 * ConfigurazioneSelettoreCondizione
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StickyConfigurazione {

	private boolean stickyEnabled = false;
	private StickyTipoSelettore tipoSelettore;
	private String pattern; // exprRegular o xpath o jsonPath o template
	private Integer maxAgeSeconds;
	
	public boolean isStickyEnabled() {
		return this.stickyEnabled;
	}
	public void setStickyEnabled(boolean sticky) {
		this.stickyEnabled = sticky;
	}
	public StickyTipoSelettore getTipoSelettore() {
		return this.tipoSelettore;
	}
	public void setTipoSelettore(StickyTipoSelettore tipoSelettore) {
		this.tipoSelettore = tipoSelettore;
	}
	public String getPattern() {
		return this.pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public Integer getMaxAgeSeconds() {
		return this.maxAgeSeconds;
	}
	public void setMaxAgeSeconds(Integer expire) {
		this.maxAgeSeconds = expire;
	}
}
