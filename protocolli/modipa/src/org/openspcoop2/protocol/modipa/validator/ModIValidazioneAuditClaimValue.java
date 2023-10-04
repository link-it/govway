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
package org.openspcoop2.protocol.modipa.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.modipa.config.ModIAuditClaimConfig;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.validator.ValidazioneUtils;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

import com.fasterxml.jackson.databind.node.TextNode;

/**
 * ModIValidazioneAuditClaimValue
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIValidazioneAuditClaimValue {

	private String claimName;
	private String v;
	private Object o;
	private ModIAuditClaimConfig modIAuditClaimConfig;
	
	// Usato in validazione
	public ModIValidazioneAuditClaimValue(String claimName, String v, Object o, ModIAuditClaimConfig modIAuditClaimConfig){
		this.claimName = claimName;
		this.v = v;
		this.o = o;
		this.modIAuditClaimConfig = modIAuditClaimConfig;
	}
	// Usato in imbustamento
	public ModIValidazioneAuditClaimValue(String claimName, String v, ModIAuditClaimConfig modIAuditClaimConfig, String prefix) throws ProtocolException{
		this.claimName = claimName;
		this.v = v;
		this.modIAuditClaimConfig = modIAuditClaimConfig;
		
		this.parseValue(prefix);
	}
	
	public String getValore() {
		return this.v;
	}
	public Object getRawObject() {
		return this.o;
	}
	
	private String getPrefixInvalidFormatValue() {
		return "Invalid format value '"+this.v+"'; ";
	}
	
	private void parseValue(String prefix) throws ProtocolException {
		if(this.modIAuditClaimConfig.isStringType()) {
			this.o = this.v;
		}
		else {
			if("true".equals(this.v)) {
				this.o = true;
			}
			else if("false".equals(this.v)) {
				this.o = false;
			}
			else {
				String expected = "";
				try {
					if(this.v.contains(".")) {
						expected = "double";
						double d = Double.parseDouble(this.v);
						this.o = d;
					}
					else {
						expected = "long";
						long l = Long.parseLong(this.v);
						this.o = l;
					}
				}catch(Exception e) {
					String msgErrore = prefix+ModIValidazioneSintatticaRest.getErroreTokenClaimNonValido(this.claimName, 
							new Exception(getPrefixInvalidFormatValue()+"expected "+expected+" primitive type"));
					processException(null, null, msgErrore, e);
				}
			}
		}
	}
	
	public void validate(String prefix) throws ProtocolException {
		this.validate(null, null, prefix);
	}
	public void validate(ValidazioneUtils validazioneUtils, List<Eccezione> erroriValidazione, String prefix) throws ProtocolException {
		
		validateValueType(validazioneUtils, erroriValidazione, prefix);
		
		validateValueLength(validazioneUtils, erroriValidazione, prefix);

		validateValueRegexp(validazioneUtils, erroriValidazione, prefix);
		
		validateValueByEnum(validazioneUtils, erroriValidazione, prefix);
		
	}
	private void processException(ValidazioneUtils validazioneUtils, List<Eccezione> erroriValidazione, String msgErrore) throws ProtocolException {
		processException(validazioneUtils, erroriValidazione, msgErrore, null);
	}
	private void processException(ValidazioneUtils validazioneUtils, List<Eccezione> erroriValidazione, String msgErrore, Exception e) throws ProtocolException {
		if(validazioneUtils!=null) {
			erroriValidazione.add(validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, msgErrore));
		}
		else {
			if(e!=null) {
				throw new ProtocolException(msgErrore, e);
			}
			else {
				throw new ProtocolException(msgErrore);
			}
		}
	}
	private void validateValueType(ValidazioneUtils validazioneUtils, List<Eccezione> erroriValidazione, String prefix) throws ProtocolException {
		if(this.modIAuditClaimConfig.isStringType()) {
			if( !(this.o instanceof TextNode) && !(this.o instanceof String) ) {
				String msgErrore = prefix+ModIValidazioneSintatticaRest.getErroreTokenClaimNonValido(this.claimName, 
						new Exception(getPrefixInvalidFormatValue()+"expected string type, found '"+this.o.getClass().getName()+"'"));
				processException(validazioneUtils, erroriValidazione, msgErrore);
			}
		}else {
			if(this.o instanceof TextNode) {
				String msgErrore = prefix+ModIValidazioneSintatticaRest.getErroreTokenClaimNonValido(this.claimName, 
						new Exception(getPrefixInvalidFormatValue()+"expected primitive type, found string"));
				processException(validazioneUtils, erroriValidazione, msgErrore);
			}
		}
	}
	private void validateValueLength(ValidazioneUtils validazioneUtils, List<Eccezione> erroriValidazione, String prefix) throws ProtocolException {
		int lengthV = this.v!=null ? this.v.length() : 0;
		
		if(this.modIAuditClaimConfig.getMinLength()>0 && lengthV<this.modIAuditClaimConfig.getMinLength()) {
			String msgErrore = prefix+ModIValidazioneSintatticaRest.getErroreTokenClaimNonValido(this.claimName, 
					new Exception("Invalid value '"+this.v+"'; min length requirement '"+this.modIAuditClaimConfig.getMinLength()+"' not met"));
			processException(validazioneUtils, erroriValidazione, msgErrore);
		}
		
		if(this.modIAuditClaimConfig.getMaxLength()>0 && lengthV>this.modIAuditClaimConfig.getMaxLength()) {
			String msgErrore = prefix+ModIValidazioneSintatticaRest.getErroreTokenClaimNonValido(this.claimName,
					new Exception("Invalid value '"+this.v+"'; max length requirement '"+this.modIAuditClaimConfig.getMaxLength()+"' not met"));
			processException(validazioneUtils, erroriValidazione, msgErrore);
		}
	}
	private void validateValueRegexp(ValidazioneUtils validazioneUtils, List<Eccezione> erroriValidazione, String prefix) throws ProtocolException {
		if(this.modIAuditClaimConfig.getRegexp()!=null && StringUtils.isNotEmpty(this.modIAuditClaimConfig.getRegexp())) {
			try {
				if(!RegularExpressionEngine.isMatch(this.v, this.modIAuditClaimConfig.getRegexp())) {
					throw new ProtocolException("Validation by regexp '"+this.modIAuditClaimConfig.getRegexp()+"' failed: invalid value '"+this.v+"'");
				}
			}catch(Exception e) {
				String msgErrore = prefix+ModIValidazioneSintatticaRest.getErroreTokenClaimNonValido(this.claimName, e);
				processException(validazioneUtils, erroriValidazione, msgErrore, e);
			}
		}
	}
	private void validateValueByEnum(ValidazioneUtils validazioneUtils, List<Eccezione> erroriValidazione, String prefix) throws ProtocolException {
		if(this.modIAuditClaimConfig.getValues()!=null && !this.modIAuditClaimConfig.getValues().isEmpty() &&
				!this.modIAuditClaimConfig.getValues().contains(this.v)) {
			String msgErrore = prefix+ModIValidazioneSintatticaRest.getErroreTokenClaimNonValido(this.claimName, new Exception("Validation by enum-list failed: value '"+this.v+"' unknown"));
			processException(validazioneUtils, erroriValidazione, msgErrore);
		}
	}
}
