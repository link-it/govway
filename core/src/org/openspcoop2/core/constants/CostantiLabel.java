/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.constants;

/**
 * CostantiLabel
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class CostantiLabel {
	    

    public static final String TRASPARENTE_PROTOCOL_NAME = "trasparente";
    public static final String TRASPARENTE_PROTOCOL_LABEL = "API Gateway";
    
    public static final String SPCOOP_PROTOCOL_NAME = "spcoop";
    public static final String SPCOOP_PROTOCOL_LABEL = "SPCoop";
    
    public static final String MODIPA_PROTOCOL_NAME = "modipa";
    public static final String MODIPA_PROTOCOL_LABEL = "ModI";
    
    public static final String SDI_PROTOCOL_NAME = "sdi";
    public static final String SDI_PROTOCOL_LABEL = "Fatturazione Elettronica";
    
    public static final String AS4_PROTOCOL_NAME = "as4";
    public static final String AS4_PROTOCOL_LABEL = "eDelivery";
	
   	
    /**
     * PROPRIETA MODI
     */
	
	public static final String MODIPA_API_PROFILO_CANALE_LABEL = "Sicurezza Canale";
	public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01 = "ID_AUTH_CHANNEL_01";
	public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02 = "ID_AUTH_CHANNEL_02";
	
	public static final String MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL = "Sicurezza Messaggio";
	public static final String MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_LABEL = "Sicurezza Messaggio";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01 = "ID_AUTH_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST = "ID_AUTH_REST_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP = "ID_AUTH_SOAP_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02 = "ID_AUTH_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST = "ID_AUTH_REST_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP = "ID_AUTH_SOAP_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301 = "INTEGRITY_01 con ID_AUTH_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST = "INTEGRITY_REST_01 con ID_AUTH_REST_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP = "INTEGRITY_SOAP_01 con ID_AUTH_SOAP_01";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302 = "INTEGRITY_01 con ID_AUTH_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST = "INTEGRITY_REST_01 con ID_AUTH_REST_02";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP = "INTEGRITY_SOAP_01 con ID_AUTH_SOAP_02";
	
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL = "Digest Richiesta";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL = "Informazioni Utente";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL = "Audience"; 
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL = "WSAddressing To";
    public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_LABEL = "Identificativo Client";
    public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO_LABEL = "Reply Audience/WSA-To";
    public static final String MODIPA_APPLICATIVI_AUDIENCE_WSATO_LABEL = "Audience/WSA-To";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_LABEL = "Claims";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL = "KeyStore";
	
}
