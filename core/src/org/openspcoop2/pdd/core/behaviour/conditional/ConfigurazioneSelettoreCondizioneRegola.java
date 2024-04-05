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
public class ConfigurazioneSelettoreCondizioneRegola extends ConfigurazioneSelettoreCondizione {

	private String regola;
	private String patternOperazione; // exprRegulare (Per far matchare più azioni '^(?:azione1|get\.azione2)$')
	private String staticInfo; 
	
	public String getStaticInfo() {
		return this.staticInfo;
	}
	public void setStaticInfo(String staticInfo) {
		this.staticInfo = staticInfo;
	}
	public String getRegola() {
		return this.regola;
	}
	public void setRegola(String regola) {
		this.regola = regola;
	}
	public String getPatternOperazione() {
		return this.patternOperazione;
	}
	public void setPatternOperazione(String patternOperazione) {
		this.patternOperazione = patternOperazione;
	}

	
}