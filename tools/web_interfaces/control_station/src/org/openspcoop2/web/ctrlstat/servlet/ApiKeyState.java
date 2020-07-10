/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.ModalitaIdentificazione;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ApiKeyState
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ApiKeyState {

	// posizione 0: appId
	String appId = null;
	public boolean appIdSelected = false;

	// posizione
	List<String> posizioneValues = new ArrayList<String>();
	List<String> posizioneLabels = new ArrayList<String>();
	List<String> posizioneSelected = new ArrayList<String>();
	// posizione 1: queryParameter
	String queryParameter = Costanti.CHECK_BOX_ENABLED;
	boolean queryParameterEnabled = true;
	// posizione 2: header
	String header = Costanti.CHECK_BOX_ENABLED;
	boolean headerEnabled = true;
	// posizione 3: cookie
	String cookie = Costanti.CHECK_BOX_ENABLED;
	boolean cookieEnabled = true;
	
	// posizione 4: useOAS3Names
	String useOAS3Names = Costanti.CHECK_BOX_ENABLED;
	boolean useOAS3NamesSelected = true;
	
	// posizione 5: cleanApiKey 
	String forwardApiKey = null;
	boolean forwardApiKeySelected = false;
	
	// posizione 6: cleanAppId 
	String forwardAppId = null;
	boolean forwardAppIdSelected = false;

	// posizione 7: queryParameterApiKey
	String queryParameterApiKey = ParametriAutenticazioneApiKey.DEFAULT_QUERY_PARAMETER_API_KEY;
	
	// posizione 8: headerApiKey
	String headerApiKey = ParametriAutenticazioneApiKey.DEFAULT_HEADER_API_KEY;
	
	// posizione 9: cookieApiKey
	String cookieApiKey = ParametriAutenticazioneApiKey.DEFAULT_COOKIE_API_KEY;
	
	// posizione 10: queryParameterAppId
	String queryParameterAppId = ParametriAutenticazioneApiKey.DEFAULT_QUERY_PARAMETER_APP_ID;
	
	// posizione 11: headerAppId
	String headerAppId = ParametriAutenticazioneApiKey.DEFAULT_HEADER_APP_ID;
	
	// posizione 12: cookieAppId
	String cookieAppId = ParametriAutenticazioneApiKey.DEFAULT_COOKIE_APP_ID;
	
	public ApiKeyState(List<String> autenticazioneParametroList){

		this.posizioneValues.add(ParametriAutenticazioneApiKey.QUERY_PARAMETER);
		this.posizioneValues.add(ParametriAutenticazioneApiKey.HEADER);
		this.posizioneValues.add(ParametriAutenticazioneApiKey.COOKIE);
		
		this.posizioneLabels.add(ModalitaIdentificazione.FORM_BASED.getLabel());
		this.posizioneLabels.add(ModalitaIdentificazione.HEADER_BASED.getLabel());
		this.posizioneLabels.add(ModalitaIdentificazione.COOKIE_BASED.getLabel());
		
		this.posizioneSelected.addAll(this.posizioneValues);// default
		
		if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty()) {
			if(autenticazioneParametroList.size()>0) {
				this.appId = autenticazioneParametroList.get(0);
				this.appIdSelected = ServletUtils.isCheckBoxEnabled(this.appId);
			}
			if(autenticazioneParametroList.size()>1) {
				this.posizioneSelected = new ArrayList<String>();
				this.queryParameter = autenticazioneParametroList.get(1);
				this.queryParameterEnabled = ServletUtils.isCheckBoxEnabled(this.queryParameter);
				if(this.queryParameterEnabled) {
					this.posizioneSelected.add(ParametriAutenticazioneApiKey.QUERY_PARAMETER);
				}
			}
			if(autenticazioneParametroList.size()>2) {
				this.header = autenticazioneParametroList.get(2);
				this.headerEnabled = ServletUtils.isCheckBoxEnabled(this.header);
				if(this.headerEnabled) {
					this.posizioneSelected.add(ParametriAutenticazioneApiKey.HEADER);
				}
			}
			if(autenticazioneParametroList.size()>3) {
				this.cookie = autenticazioneParametroList.get(3);
				this.cookieEnabled = ServletUtils.isCheckBoxEnabled(this.cookie);
				if(this.cookieEnabled) {
					this.posizioneSelected.add(ParametriAutenticazioneApiKey.COOKIE);
				}
			}
			if(autenticazioneParametroList.size()>4) {
				this.useOAS3Names = autenticazioneParametroList.get(4);
				this.useOAS3NamesSelected = ServletUtils.isCheckBoxEnabled(this.useOAS3Names);
			}
			if(autenticazioneParametroList.size()>5) {
				String cleanApiKey = autenticazioneParametroList.get(5);
				if(ServletUtils.isCheckBoxEnabled(cleanApiKey)) {
					this.forwardApiKey = Costanti.CHECK_BOX_DISABLED;
				}
				else {
					this.forwardApiKey = Costanti.CHECK_BOX_ENABLED;
				}
				this.forwardApiKeySelected = ServletUtils.isCheckBoxEnabled(this.forwardApiKey);
			}
			if(autenticazioneParametroList.size()>6) {
				String cleanAppId = autenticazioneParametroList.get(6);
				if(ServletUtils.isCheckBoxEnabled(cleanAppId)) {
					this.forwardAppId = Costanti.CHECK_BOX_DISABLED;
				}
				else {
					this.forwardAppId = Costanti.CHECK_BOX_ENABLED;
				}
				this.forwardAppIdSelected = ServletUtils.isCheckBoxEnabled(this.forwardAppId);
			}
			if(autenticazioneParametroList.size()>7) {
				this.queryParameterApiKey = autenticazioneParametroList.get(7);
			}
			if(autenticazioneParametroList.size()>8) {
				this.headerApiKey = autenticazioneParametroList.get(8);
			}
			if(autenticazioneParametroList.size()>9) {
				this.cookieApiKey = autenticazioneParametroList.get(9);
			}
			if(autenticazioneParametroList.size()>10) {
				this.queryParameterAppId = autenticazioneParametroList.get(10);
			}
			if(autenticazioneParametroList.size()>11) {
				this.headerAppId = autenticazioneParametroList.get(11);
			}
			if(autenticazioneParametroList.size()>12) {
				this.cookieAppId = autenticazioneParametroList.get(12);
			}
		}
	}
	
}
