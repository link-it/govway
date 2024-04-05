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

package org.openspcoop2.protocol.utils;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ModIValidazioneSemanticaProfiloSicurezza
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIValidazioneSemanticaProfiloSicurezza {

	private Busta busta;
	
	private String securityMessageProfileNonFiltratoPDND;
	
	private boolean sicurezzaTokenOauth;
	private String securityMessageProfileSorgenteTokenIdAuth;
	
	private boolean sicurezzaMessaggio;
	private boolean sicurezzaMessaggioIDAR04 = false;
	private String securityMessageProfile;
	
	private boolean sicurezzaAudit;
	private String securityAuditPattern;
	
	public ModIValidazioneSemanticaProfiloSicurezza(Busta bustaParam, boolean isRichiesta) throws ProtocolException {
		
		this.busta = bustaParam;
		
		this.securityMessageProfileNonFiltratoPDND = this.busta.getProperty(CostantiDB.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO);
		if(this.securityMessageProfileNonFiltratoPDND!=null) {
			this.securityMessageProfileNonFiltratoPDND = ModIUtils.convertProfiloSicurezzaToConfigurationValue(this.securityMessageProfileNonFiltratoPDND);
		}
		
		this.securityMessageProfileSorgenteTokenIdAuth = this.busta.getProperty(CostantiDB.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN);
		if(this.securityMessageProfileSorgenteTokenIdAuth!=null) {
			this.securityMessageProfileSorgenteTokenIdAuth = ModIUtils.convertProfiloSicurezzaSorgenteTokenToConfigurationValue(this.securityMessageProfileSorgenteTokenIdAuth);
		}
		
		this.securityMessageProfile = this.securityMessageProfileNonFiltratoPDND;
		if(this.securityMessageProfileSorgenteTokenIdAuth!=null &&
			(	
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(this.securityMessageProfileNonFiltratoPDND) 
					|| 
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(this.securityMessageProfileNonFiltratoPDND)
			)
			&&
			(	
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(this.securityMessageProfileSorgenteTokenIdAuth) 
					||
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(this.securityMessageProfileSorgenteTokenIdAuth)
			)
		){
			this.securityMessageProfile = null;
		}
					
		this.sicurezzaTokenOauth = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(this.securityMessageProfileSorgenteTokenIdAuth) 
				||
			CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(this.securityMessageProfileSorgenteTokenIdAuth);
		
		this.sicurezzaMessaggio = this.securityMessageProfile!=null && !CostantiDB.MODIPA_VALUE_UNDEFINED.equals(this.securityMessageProfile);
		this.sicurezzaMessaggioIDAR04 = false;
		if(this.sicurezzaMessaggio) {
			this.sicurezzaMessaggioIDAR04 = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(this.securityMessageProfile) 
											|| 
										CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(this.securityMessageProfile);
		}
		
		this.securityAuditPattern = this.busta.getProperty(CostantiDB.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_PATTERN);
		
		if(this.securityAuditPattern!=null) {
			this.securityAuditPattern = ModIUtils.convertProfiloAuditToConfigurationValue(this.securityAuditPattern);
		}
		
		this.sicurezzaAudit = isRichiesta && 
				this.securityAuditPattern!=null && !CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(this.securityAuditPattern);
		
	}

	public Busta getBusta() {
		return this.busta;
	}
	
	public String getSecurityMessageProfileNonFiltratoPDND() {
		return this.securityMessageProfileNonFiltratoPDND;
	}
	
	public boolean isSicurezzaTokenOauth() {
		return this.sicurezzaTokenOauth;
	}
	public String getSecurityMessageProfileSorgenteTokenIdAuth() {
		return this.securityMessageProfileSorgenteTokenIdAuth;
	}

	public boolean isSicurezzaMessaggio() {
		return this.sicurezzaMessaggio;
	}
	public boolean isSicurezzaMessaggioIDAR04() {
		return this.sicurezzaMessaggioIDAR04;
	}
	public String getSecurityMessageProfile() {
		return this.securityMessageProfile;
	}

	public boolean isSicurezzaAudit() {
		return this.sicurezzaAudit;
	}
	public String getSecurityAuditPattern() {
		return this.securityAuditPattern;
	}
}
