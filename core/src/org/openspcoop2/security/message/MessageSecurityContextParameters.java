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

package org.openspcoop2.security.message;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * MessageSecurityContextParameters
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecurityContextParameters {

	private boolean useActorDefaultIfNotDefined;
	private String actorDefault;
	private Logger log;
	private boolean functionAsClient;
	private String prefixWsuId;
	private boolean removeAllWsuIdRef;
	
	private IDSoggetto idFruitore;
	private String pddFruitore;
	
	private IDServizio idServizio;
	private String pddErogatore;
		
	public boolean isUseActorDefaultIfNotDefined() {
		return this.useActorDefaultIfNotDefined;
	}
	public void setUseActorDefaultIfNotDefined(boolean useActorDefaultIfNotDefined) {
		this.useActorDefaultIfNotDefined = useActorDefaultIfNotDefined;
	}
	public String getActorDefault() {
		return this.actorDefault;
	}
	public void setActorDefault(String actorDefault) {
		this.actorDefault = actorDefault;
	}
	public Logger getLog() {
		return this.log;
	}
	public void setLog(Logger log) {
		this.log = log;
	}
	public boolean isFunctionAsClient() {
		return this.functionAsClient;
	}
	public void setFunctionAsClient(boolean functionAsClient) {
		this.functionAsClient = functionAsClient;
	}
	public String getPrefixWsuId() {
		return this.prefixWsuId;
	}
	public void setPrefixWsuId(String prefixWsuId) {
		this.prefixWsuId = prefixWsuId;
	}
	public boolean isRemoveAllWsuIdRef() {
		return this.removeAllWsuIdRef;
	}
	public void setRemoveAllWsuIdRef(boolean removeAllWsuIdRef) {
		this.removeAllWsuIdRef = removeAllWsuIdRef;
	}
	public IDSoggetto getIdFruitore() {
		return this.idFruitore;
	}
	public void setIdFruitore(IDSoggetto idFruitore) {
		this.idFruitore = idFruitore;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	public String getPddErogatore() {
		return this.pddErogatore;
	}
	public void setPddErogatore(String pddErogatore) {
		this.pddErogatore = pddErogatore;
	}
	public String getPddFruitore() {
		return this.pddFruitore;
	}
	public void setPddFruitore(String pddFruitore) {
		this.pddFruitore = pddFruitore;
	}
	
}
