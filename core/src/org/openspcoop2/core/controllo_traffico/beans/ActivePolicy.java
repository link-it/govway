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

package org.openspcoop2.core.controllo_traffico.beans;

import java.io.Serializable;

import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;

/**
 * ActivePolicy 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ActivePolicy extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ConfigurazionePolicy configurazionePolicy;
	
	private TipoRisorsa tipoRisorsaPolicy;
	
	private AttivazionePolicy instanceConfiguration;
	
	private ConfigurazioneControlloCongestione configurazioneControlloCongestione;
	
	public ConfigurazioneControlloCongestione getConfigurazioneControlloCongestione() {
		return this.configurazioneControlloCongestione;
	}

	public void setConfigurazioneControlloCongestione(
			ConfigurazioneControlloCongestione configurazioneControlloCongestione) {
		this.configurazioneControlloCongestione = configurazioneControlloCongestione;
	}

	public AttivazionePolicy getInstanceConfiguration() {
		return this.instanceConfiguration;
	}

	public void setInstanceConfiguration(AttivazionePolicy instanceConfiguration) {
		this.instanceConfiguration = instanceConfiguration;
	}

	public TipoRisorsa getTipoRisorsaPolicy() {
		return this.tipoRisorsaPolicy;
	}

	public void setTipoRisorsaPolicy(TipoRisorsa tipoRisorsaPolicy) {
		this.tipoRisorsaPolicy = tipoRisorsaPolicy;
	}
	
	public ConfigurazionePolicy getConfigurazionePolicy() {
		return this.configurazionePolicy;
	}

	public void setConfigurazionePolicy(ConfigurazionePolicy configurazionePolicy) {
		this.configurazionePolicy = configurazionePolicy;
	}


	
}
