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
	private boolean visualizzaCampiPasswordComeLock;
	private String warningMessage;
	private String servletNameSecretDecoder;
	private String messaggioInformativoInformazioneNonCifrata;
	private String messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy;
	
	
	public LockUtilities(DriverBYOKUtilities driverBYOKUtilities, boolean visualizzaInformazioniCifrate,
			String warningMessage, String servletNameSecretDecoder,
			String messaggioInformativoInformazioneNonCifrata, String messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy, boolean visualizzaCampiPasswordComeLock) {
		this.driverBYOKUtilities = driverBYOKUtilities;
		this.visualizzaInformazioniCifrate = visualizzaInformazioniCifrate;
		this.warningMessage = warningMessage;
		this.servletNameSecretDecoder = servletNameSecretDecoder;
		this.messaggioInformativoInformazioneNonCifrata = messaggioInformativoInformazioneNonCifrata; 
		this.messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy = messaggioInformativoInformazioneCifrataDifferenteSecurityPolicy; 
		this.visualizzaCampiPasswordComeLock = visualizzaCampiPasswordComeLock;
	}
	
	
	public void lockProperty(DataElement de, String value) {
		lockProperty(de, value, true);
	}
	public void lockProperty(DataElement de, String value, boolean escapeHtml) {
		lockPropertyEngine(de, value, escapeHtml, false, false);
	}
	public void lockPropertyReadOnly(DataElement de, String value) {
		lockPropertyReadOnly(de, value, true);
	}
	public void lockPropertyReadOnly(DataElement de, String value, boolean escapeHtml) {
		lockPropertyEngine(de, value, escapeHtml, false, true);
	}
	public void lockPropertyHidden(DataElement de, String value) {
		lockPropertyHidden(de, value, true);
	}
	public void lockPropertyHidden(DataElement de, String value, boolean escapeHtml) {
		lockPropertyEngine(de, value, escapeHtml, true, false);
	}
	private void lockPropertyEngine(DataElement de, String value, boolean escapeHtml, boolean hidden, boolean readOnly) {
		if(BYOKManager.isEnabledBYOK()) {
			lockEngineWithBIOK(de, value, escapeHtml, hidden, readOnly, 
					value); // non viene effettuato qual il wrap del valore, ma dovrà essere effettuato dalla servlet chiamante, in seguito alla chiamata dell'utente.
			de.forceLockVisualizzazioneInputUtente(this.driverBYOKUtilities.isWrapped(value),this.visualizzaInformazioniCifrate);
		}
		else {
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(value);
		}
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
			processByByokDisabled(de, value, escapeHtml, hidden, readOnly);
		}
	}
	private void processByByokDisabled(DataElement de, String value, boolean escapeHtml, boolean hidden, boolean readOnly) {
		if(isForceHidden(de, hidden)){
			de.setType(DataElementType.HIDDEN);
		}
		else if(isForceReadOnly(de, readOnly)){
			if(this.visualizzaCampiPasswordComeLock) {
				this.lockEngineWithoutBIOK(de, value, escapeHtml, readOnly);
			} else {
				de.setType(DataElementType.TEXT);
			}
		}
		else if( de.getType()==null || StringUtils.isEmpty(de.getType()) || 
				( (!DataElementType.TEXT_EDIT.toString().equals(de.getType())) && (!DataElementType.TEXT_AREA.toString().equals(de.getType())) )
				){
			if(this.visualizzaCampiPasswordComeLock) {
				this.lockEngineWithoutBIOK(de, value, escapeHtml, readOnly);
			} else {
				de.setType(DataElementType.TEXT_EDIT);
			}
		} else {
			if(this.visualizzaCampiPasswordComeLock) {
				this.lockEngineWithoutBIOK(de, value, escapeHtml, readOnly);
			} 
		}
		de.setValue(escapeHtml ? StringEscapeUtils.escapeHtml(value) : value);
	}
	private boolean isForceHidden(DataElement de, boolean hidden) {
		return hidden &&
				(de.getType()==null || StringUtils.isEmpty(de.getType()) || !DataElementType.HIDDEN.toString().equals(de.getType())) ;
	}
	private boolean isForceReadOnly(DataElement de, boolean readOnly) {
		return readOnly &&
				(de.getType()==null || StringUtils.isEmpty(de.getType()) || !DataElementType.TEXT.toString().equals(de.getType())) ;
	}
	private void lockEngineWithBIOK(DataElement de, String value, boolean escapeHtml, boolean hidden, boolean readOnly ) throws UtilsException {
		String wrapValue = this.driverBYOKUtilities.wrap(value); // viene lasciato il valore wrapped, non viene effettuato nuovamente il wrap
		lockEngineWithBIOK(de, value, escapeHtml, hidden, readOnly, wrapValue);
	}
	private void lockEngineWithBIOK(DataElement de, String value, boolean escapeHtml, boolean hidden, boolean readOnly, String wrapValue) {
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
		lockEngineWithBIOK(de, wrapValue, originalValue, escapeHtml, readOnly, 
				// il valore viene decifrato tramite le chiamate getLockedParameter, quindi questo controllo non può essere implementato.
				false); 
	}
	private void lockEngineWithBIOK(DataElement de, String wrapValue, String originalValue, boolean escapeHtml, boolean readOnly, boolean checkOriginalValue) {
		StringBuilder sb = new StringBuilder();
		if(originalValue!=null) {
			// nop
		}
		/**String checkValue = originalValue;*/ // il valore viene decifrato tramite le chiamate getLockedParameter, quindi questo controllo non può essere implementato.
		String checkValue = wrapValue; // viene verificato sempre il wrapValue, il quale se già cifrato non viene nuovamente cifrato e quindi permane la vecchia security policy.
		if(checkValue!=null && StringUtils.isNotEmpty(checkValue)) {
			if(BYOKUtilities.isWrappedValue(checkValue)) {
				if(!this.driverBYOKUtilities.isWrapped(checkValue)) {
					appendErrorMessageSecurityPolicyDifferente(sb, checkValue);
				}
			}
			else if(checkOriginalValue && this.messaggioInformativoInformazioneNonCifrata!=null && StringUtils.isNotEmpty(this.messaggioInformativoInformazioneNonCifrata)) {
				sb.append(this.messaggioInformativoInformazioneNonCifrata);
			}
		}
		if(sb.length()>0) {
			de.setNote(sb.toString());
		}
		de.setLock(escapeHtml ? StringEscapeUtils.escapeHtml(wrapValue) : wrapValue, readOnly, this.visualizzaInformazioniCifrate, true, this.warningMessage, this.servletNameSecretDecoder);
	}
	private void lockEngineWithoutBIOK(DataElement de, String wrapValue, boolean escapeHtml, boolean readOnly) {
		de.setLock(escapeHtml ? StringEscapeUtils.escapeHtml(wrapValue) : wrapValue, readOnly, false, false, null, null);
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
