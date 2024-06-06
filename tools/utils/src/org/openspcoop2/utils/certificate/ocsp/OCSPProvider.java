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


package org.openspcoop2.utils.certificate.ocsp;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.SortedMap;

/**     
 * OCSPProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPProvider {
	
	private boolean ocsp = false;
	public boolean isOcspEnabled() {
		return this.ocsp;
	}
	private List<String> ocspTypes = new ArrayList<>();
	private List<String> ocspLabels = new ArrayList<>();
	private static final String NO_OCSP = "--no_ocsp--";
	private static List<String> noOCSP = new ArrayList<>();
	static{
		noOCSP.add(NO_OCSP);	
	}
	public OCSPProvider() {
		SortedMap<String> ocspSortedMap = OCSPManager.getInstance().getOCSPConfigTypesLabels();
		this.ocsp = ocspSortedMap!=null && !ocspSortedMap.isEmpty();
		if(this.ocsp) {
			List<String> ocspTypesAdd = new ArrayList<>();
			List<String> ocspLabelsAdd = new ArrayList<>();
			if(!ocspSortedMap.isEmpty()) {
				for (String type : ocspSortedMap.keys()) {
					ocspTypesAdd.add(type);
					ocspLabelsAdd.add(ocspSortedMap.get(type));
				}
			}
			boolean ocspEnabled = !ocspTypesAdd.isEmpty();
			if(ocspEnabled) {
				this.ocspTypes.add("");
				this.ocspTypes.addAll(ocspTypesAdd);
				this.ocspLabels.add("-");
				this.ocspLabels.addAll(ocspLabelsAdd);
			}
		}
	}
	
	public List<String> getValues() {
		return this.ocsp ? this.ocspTypes : noOCSP;
	}

	public List<String> getLabels() {
		return this.ocsp ? this.ocspLabels : noOCSP;
	}
	
}
