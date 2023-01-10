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
package org.openspcoop2.protocol.registry;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.constants.StatoCheck;

/**
 * CertificateStatoCheck
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificateCheck {

	private StatoCheck statoCheck;
	private String configurationId;
	private List<String> errorCertificateIdentity = new ArrayList<String>();
	private List<String> errorDetails = new ArrayList<String>();
	private List<String> errorCertificateDetails = new ArrayList<String>();
	private List<String> warningCertificateIdentity = new ArrayList<String>();
	private List<String> warningDetails = new ArrayList<String>();
	private List<String> warningCertificateDetails = new ArrayList<String>();
	
	public void addError(String identity, String details, String certificateDetails) {
		this.errorCertificateIdentity.add(identity);
		this.errorDetails.add(details);
		this.errorCertificateDetails.add(certificateDetails!=null ? certificateDetails : "");
	}
	public void addWarning(String identity, String details, String certificateDetails) {
		this.warningCertificateIdentity.add(identity);
		this.warningDetails.add(details);
		this.warningCertificateDetails.add(certificateDetails!=null ? certificateDetails : "");
	}
	public void setConfigurationId(String configurationId) {
		this.configurationId = configurationId;
	}
	
	public StatoCheck getStatoCheck() {
		return this.statoCheck;
	}
	public void setStatoCheck(StatoCheck statoCheck) {
		this.statoCheck = statoCheck;
	}

	
	public String toString(String newLine) {
		StringBuilder sbEsito = new StringBuilder();
		sbEsito.append(this.statoCheck.toString());
		switch (this.statoCheck) {
		case ERROR:
			printDetails(sbEsito, newLine,
					this.errorCertificateIdentity, this.errorDetails, this.errorCertificateDetails);			
			break;
		case WARN:
			if(!this.errorCertificateIdentity.isEmpty()) {
				this.warningCertificateIdentity.addAll(this.errorCertificateIdentity);
				this.warningDetails.addAll(this.errorDetails);
				this.warningCertificateDetails.addAll(this.errorCertificateDetails);
			}
			
			printDetails(sbEsito, newLine,
					this.warningCertificateIdentity, this.warningDetails, this.warningCertificateDetails);			
			break;
		default:
			break;
		}
		return sbEsito.toString();
	}
	private void printDetails(StringBuilder sbEsito, String newLine,
			List<String> certificateIdentities, List<String> detailsList, List<String> certificateDetailsList) {
		if(!detailsList.isEmpty()) {
			if(this.configurationId!=null) {
				sbEsito.append(newLine);
				sbEsito.append(this.configurationId);
			}
			for (int i = 0; i < detailsList.size(); i++) {
				String details = detailsList.get(i);
				String certificateDetails = certificateDetailsList.get(i);
				if(certificateIdentities.size()>1) {
					String identity = certificateIdentities.get(i);
					sbEsito.append(newLine);
					sbEsito.append("- ");
					sbEsito.append(identity);
				}
				if(StringUtils.isNotEmpty(certificateDetails)) {
					sbEsito.append(newLine);
					sbEsito.append(certificateDetails);
				}
				sbEsito.append(newLine);
				sbEsito.append(details);
			}
		}	
	}
}
