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
package org.openspcoop2.web.lib.mvc.byok;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.pdd.core.byok.DriverBYOKUtilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;

/**
 * LockUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LockUtilities {

	private DriverBYOKUtilities driverBYOKUtilities;
	private boolean visualizzaInformazioniCifrate;
	private String warningMessage;
	private String servletNameSecretDecoder;
	private String messaggioInformativoInformazioneNonCifrata;
	private String messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy;
	
	
	public LockUtilities(DriverBYOKUtilities driverBYOKUtilities, boolean visualizzaInformazioniCifrate,
			String warningMessage, String servletNameSecretDecoder,
			String messaggioInformativoInformazioneNonCifrata, String messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy) {
		this.driverBYOKUtilities = driverBYOKUtilities;
		this.visualizzaInformazioniCifrate = visualizzaInformazioniCifrate;
		this.warningMessage = warningMessage;
		this.servletNameSecretDecoder = servletNameSecretDecoder;
		this.messaggioInformativoInformazioneNonCifrata = messaggioInformativoInformazioneNonCifrata; 
		this.messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy = messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy; 
	}
	
	public void lock(DataElement de, String value) throws UtilsException {
		lock(de, value, true);
	}
	public void lock(DataElement de, String value, boolean escapeHtml) throws UtilsException {
		lockEngine(de, value, escapeHtml, false, false);
	}
	public void lockReadOnly(DataElement de, String value) throws UtilsException {
		lockReadOnly(de, value, true);
	}
	public void lockReadOnly(DataElement de, String value, boolean escapeHtml) throws UtilsException {
		lockEngine(de, value, escapeHtml, false, true);
	}
	public void lockHidden(DataElement de, String value) throws UtilsException {
		lockHidden(de, value, true);
	}
	public void lockHidden(DataElement de, String value, boolean escapeHtml) throws UtilsException {
		lockEngine(de, value, escapeHtml, true, false);
	}
	private void lockEngine(DataElement de, String value, boolean escapeHtml, boolean hidden, boolean readOnly) throws UtilsException {
		if(BYOKManager.isEnabledBYOK()) {
			lockEngineWithBIOK(de, value, escapeHtml, hidden, readOnly);
		}
		else {
			if(hidden &&
					(de.getType()==null || StringUtils.isEmpty(de.getType()) || !DataElementType.HIDDEN.toString().equals(de.getType())) 
					){
				de.setType(DataElementType.HIDDEN);
			}
			else if(readOnly &&
					(de.getType()==null || StringUtils.isEmpty(de.getType()) || !DataElementType.TEXT.toString().equals(de.getType())) 
					){
				de.setType(DataElementType.TEXT);
			}
			else if( de.getType()==null || StringUtils.isEmpty(de.getType()) || 
					( (!DataElementType.TEXT_EDIT.toString().equals(de.getType())) && (!DataElementType.TEXT_AREA.toString().equals(de.getType())) )
					){
				de.setType(DataElementType.TEXT_EDIT);
			}
			de.setValue(escapeHtml ? StringEscapeUtils.escapeHtml(value) : value);
		}
	}
	private void lockEngineWithBIOK(DataElement de, String value, boolean escapeHtml, boolean hidden, boolean readOnly ) throws UtilsException {
		String wrapValue = this.driverBYOKUtilities.wrap(value); // viene lasciato il valore wrapped, non viene effettuato nuovamente il wrap
		if(hidden) {
			if(de.getType()==null || StringUtils.isEmpty(de.getType()) || !DataElementType.HIDDEN.toString().equals(de.getType())) {
				de.setType(DataElementType.HIDDEN);
			}
			de.setValue(escapeHtml ? StringEscapeUtils.escapeHtml(wrapValue) : wrapValue);
		}
		else {
			lockEngineWithBIOK(de, wrapValue, value, escapeHtml, readOnly );
		}
	}
	private void lockEngineWithBIOK(DataElement de, String wrapValue, String originalValue, boolean escapeHtml, boolean readOnly) {
		StringBuilder sb = new StringBuilder();
		if(originalValue!=null && StringUtils.isNotEmpty(originalValue)) {
			if(BYOKUtilities.isWrappedValue(originalValue)) {
				if(!this.driverBYOKUtilities.isWrapped(originalValue)) {
					appendErrorMessageSecurityPolicyDifferente(sb, originalValue);
				}
			}
			else if(this.messaggioInformativoInformazioneNonCifrata!=null && StringUtils.isNotEmpty(this.messaggioInformativoInformazioneNonCifrata)) {
				sb.append(this.messaggioInformativoInformazioneNonCifrata);
			}
		}
		if(sb.length()>0) {
			de.setNote(sb.toString());
		}
		de.setLock(escapeHtml ? StringEscapeUtils.escapeHtml(wrapValue) : wrapValue, readOnly, this.visualizzaInformazioniCifrate, this.warningMessage, this.servletNameSecretDecoder);
	}
	private void appendErrorMessageSecurityPolicyDifferente(StringBuilder sb, String wrapValue) {
		if(this.messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy!=null && StringUtils.isNotEmpty(this.messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy)) {
			String suffix ="";
			try {
				String old = BYOKUtilities.getPolicy(wrapValue);
				if(old!=null && StringUtils.isNotEmpty(old)) {
					suffix = old;
				}
			}catch(Exception ignore) {
				// ignore
			}
			String s = this.messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy.replace("@SECURITY_POLICY_ID@", suffix);
			sb.append(s);
		}
	}
	

	public DriverBYOKUtilities getDriverBYOKUtilities() {
		return this.driverBYOKUtilities;
	}
}
