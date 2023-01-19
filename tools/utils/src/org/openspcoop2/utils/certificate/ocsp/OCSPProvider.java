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
	private List<String> ocsp_types = new ArrayList<>();
	private List<String> ocsp_labels = new ArrayList<>();
	private static String NO_OCSP = "--no_ocsp--";
	private static List<String> no_ocsp = new ArrayList<>();
	{
		no_ocsp.add(NO_OCSP);	
	}
	public OCSPProvider() {
		SortedMap<String> _ocsp = OCSPManager.getInstance().getOCSPConfigTypesLabels();
		this.ocsp = _ocsp!=null && !_ocsp.isEmpty();
		if(this.ocsp) {
			List<String> _ocsp_types = new ArrayList<>();
			List<String> _ocsp_labels = new ArrayList<>();
			if(_ocsp!=null && !_ocsp.isEmpty()) {
				for (String type : _ocsp.keys()) {
					_ocsp_types.add(type);
					_ocsp_labels.add(_ocsp.get(type));
				}
			}
			boolean ocspEnabled = _ocsp_types!=null && !_ocsp_types.isEmpty();
			if(ocspEnabled) {
				this.ocsp_types.add("");
				this.ocsp_types.addAll(_ocsp_types);
				this.ocsp_labels.add("-");
				this.ocsp_labels.addAll(_ocsp_labels);
			}
		}
	}
	
	public List<String> getValues() {
		return this.ocsp ? this.ocsp_types : no_ocsp;
	}

	public List<String> getLabels() {
		return this.ocsp ? this.ocsp_labels : no_ocsp;
	}
	
}
