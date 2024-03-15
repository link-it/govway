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

package org.openspcoop2.pdd.logger.filetrace;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.slf4j.Logger;

/**     
 * Topic
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Topic {

	private boolean erogazioni;
	private String nome;
	private boolean onlyRequestSent = false;
	private boolean onlyInRequestContentDefined = false;
	private boolean onlyOutRequestContentDefined = false;
	private boolean onlyInResponseContentDefined = false;
	private boolean onlyOutResponseContentDefined = false;
	private List<FaseTracciamento> onlyInTrackingPhase = new ArrayList<>();
	
	private String categoryName;
	private Logger log;
	
	private String format;
	
	public boolean isErogazioni() {
		return this.erogazioni;
	}
	public void setErogazioni(boolean erogazioni) {
		this.erogazioni = erogazioni;
	}
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCategoryName() {
		return this.categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Logger getLog() {
		return this.log;
	}
	public void setLog(Logger log) {
		this.log = log;
	}
	@Deprecated
	public boolean isOnlyRequestSended() {
		return this.onlyRequestSent;
	}
	@Deprecated
	public void setOnlyRequestSended(boolean onlyRequestSent) {
		this.onlyRequestSent = onlyRequestSent;
	}
	public boolean isOnlyRequestSent() {
		return this.onlyRequestSent;
	}
	public void setOnlyRequestSent(boolean onlyRequestSent) {
		this.onlyRequestSent = onlyRequestSent;
	}
	public boolean isOnlyInRequestContentDefined() {
		return this.onlyInRequestContentDefined;
	}
	public void setOnlyInRequestContentDefined(boolean onlyInRequestContentDefined) {
		this.onlyInRequestContentDefined = onlyInRequestContentDefined;
	}
	public boolean isOnlyOutRequestContentDefined() {
		return this.onlyOutRequestContentDefined;
	}
	public void setOnlyOutRequestContentDefined(boolean onlyOutRequestContentDefined) {
		this.onlyOutRequestContentDefined = onlyOutRequestContentDefined;
	}
	public boolean isOnlyInResponseContentDefined() {
		return this.onlyInResponseContentDefined;
	}
	public void setOnlyInResponseContentDefined(boolean onlyInResponseContentDefined) {
		this.onlyInResponseContentDefined = onlyInResponseContentDefined;
	}
	public boolean isOnlyOutResponseContentDefined() {
		return this.onlyOutResponseContentDefined;
	}
	public void setOnlyOutResponseContentDefined(boolean onlyOutResponseContentDefined) {
		this.onlyOutResponseContentDefined = onlyOutResponseContentDefined;
	}
	public void addFaseTracciamento(FaseTracciamento fase) {
		this.onlyInTrackingPhase.add(fase);
	}
	public boolean isEnabled(FaseTracciamento fase) {
		if(this.onlyInTrackingPhase == null || this.onlyInTrackingPhase.isEmpty()) {
			// default
			/**System.out.println("ENABLE DEFAULT '"+this.nome+"' ["+fase+"]: "+(FaseTracciamento.POST_OUT_RESPONSE.equals(fase)));*/
			//return FaseTracciamento.POST_OUT_RESPONSE.equals(fase);
			/**System.out.println("ENABLE DEFAULT '"+this.nome+"' ["+fase+"]: "+true);*/
			return true; // devo usare le fasi nella config per decidere se applicare solo in una fase
		}
		/**System.out.println("ENABLE '"+this.nome+"' ["+fase+"]: "+(this.onlyInTrackingPhase.contains(fase)));*/
		return this.onlyInTrackingPhase.contains(fase);
	}
	public String getFormat() {
		return this.format;
	}
	public void setFormat(String format) {
		this.format = format;
	}	
}
