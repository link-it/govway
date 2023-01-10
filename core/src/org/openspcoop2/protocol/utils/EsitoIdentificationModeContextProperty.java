/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.utils;

import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.utils.MapKey;

/**
* EsitoIdentificationModeMessageProperty
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class EsitoIdentificationModeContextProperty {

	private EsitoTransazioneName esito;
	private MapKey<String> mapKey;
	private String name;
	private String value;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public MapKey<String> getMapKey() {
		return this.mapKey;
	}
	public void setMapKey(MapKey<String> mapKey) {
		this.mapKey = mapKey;
	}
	
	public EsitoTransazioneName getEsito() {
		return this.esito;
	}
	public void setEsito(EsitoTransazioneName esito) {
		this.esito = esito;
	}
}
