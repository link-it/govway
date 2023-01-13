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

import java.io.Serializable;
import java.security.cert.CRLReason;
import java.util.Date;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.transport.http.OCSPResponseException;

/**
 * CertificateStatus
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificateStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	private CertificateStatusCode code;
	private Date revocationTime;
	private CRLReason revocationReason;
	private String details; // disponibili per crl
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.code);
		if(this.revocationTime!=null) {
			sb.append("\nrevocationTime: "+DateUtils.getSimpleDateFormatMs().format(this.revocationTime));
		}
		if(this.revocationReason!=null) {
			sb.append("\nrevocationReason: "+this.revocationReason);
		}
		if(this.details!=null) {
			sb.append("\ndetails: "+this.details);
		}
		return sb.toString();
	}
	
	public CertificateStatusCode getCode() {
		return this.code;
	}
	public void setCode(CertificateStatusCode code) {
		this.code = code;
	}
	public Date getRevocationTime() {
		return this.revocationTime;
	}
	public void setRevocationTime(Date revocationTime) {
		this.revocationTime = revocationTime;
	}
	public CRLReason getRevocationReason() {
		return this.revocationReason;
	}
	public void setRevocationReason(CRLReason revocationReason) {
		this.revocationReason = revocationReason;
	}
	public String getDetails() {
		return this.details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	public boolean isGOOD() {
		return CertificateStatusCode.GOOD.equals(this.code);
	}
	public boolean isSELF_SIGNED() {
		return CertificateStatusCode.SELF_SIGNED.equals(this.code);
	}
	public boolean isISSUER_NOT_FOUND() {
		return CertificateStatusCode.ISSUER_NOT_FOUND.equals(this.code);
	}
	public boolean isOCSP_RESPONDER_NOT_FOUND() {
		return CertificateStatusCode.OCSP_RESPONDER_NOT_FOUND.equals(this.code);
	}
	public boolean isCRL_NOT_FOUND() {
		return CertificateStatusCode.CRL_NOT_FOUND.equals(this.code);
	}
	public boolean isUNKNOWN() {
		return CertificateStatusCode.UNKNOWN.equals(this.code);
	}
	public boolean isREVOKED() {
		return CertificateStatusCode.REVOKED.equals(this.code);
	}
	public boolean isEXPIRED() {
		return CertificateStatusCode.EXPIRED.equals(this.code);
	}
	public boolean isValid() {
		return this.code!=null ? this.code.isValid() : false;
	}
	public void checkValid() throws UtilsException, OCSPResponseException {
		if(this.code==null) {
			throw new UtilsException("CertificateStatusCode unspecified");
		}
		switch (this.code) {
		case REVOKED:
		case EXPIRED:
		case UNKNOWN:
			StringBuilder sb = new StringBuilder("Certificate ");
			sb.append(this.code.name().toLowerCase());
			if(this.revocationTime!=null) {
				sb.append(" in date '"+DateUtils.getSimpleDateFormatMs().format(this.revocationTime)+"'");
			}
			if(this.revocationReason!=null) {
				sb.append(" (Reason: ").append(this.revocationReason).append(")");
			}
			if(this.details!=null) {
				sb.append(": ").append(this.details);
			}
			throw new OCSPResponseStatusException(this, sb.toString());
		default:
			if(this.code.isInvalid()) {
				throw new OCSPResponseStatusException(this, "Certificate status code '"+this.code+"'");
			}
			break;
		}
	}
	
	
	
	public static CertificateStatus GOOD() {
		CertificateStatus s = new CertificateStatus();
		s.code = CertificateStatusCode.GOOD;
		return s;
	}
	public static CertificateStatus SELF_SIGNED() {
		CertificateStatus s = new CertificateStatus();
		s.code = CertificateStatusCode.SELF_SIGNED;
		return s;
	}
	public static CertificateStatus ISSUER_NOT_FOUND() {
		CertificateStatus s = new CertificateStatus();
		s.code = CertificateStatusCode.ISSUER_NOT_FOUND;
		return s;
	}
	public static CertificateStatus OCSP_RESPONDER_NOT_FOUND() {
		CertificateStatus s = new CertificateStatus();
		s.code = CertificateStatusCode.OCSP_RESPONDER_NOT_FOUND;
		return s;
	}
	public static CertificateStatus CRL_NOT_FOUND() {
		CertificateStatus s = new CertificateStatus();
		s.code = CertificateStatusCode.CRL_NOT_FOUND;
		return s;
	}
	public static CertificateStatus UNKNOWN() {
		CertificateStatus s = new CertificateStatus();
		s.code = CertificateStatusCode.UNKNOWN;
		return s;
	}
	public static CertificateStatus REVOKED(CRLReason reason, Date time) {
		CertificateStatus s = new CertificateStatus();
		s.code = CertificateStatusCode.REVOKED;
		s.revocationReason = reason;
		s.revocationTime = time;
		return s;
	}
	public static CertificateStatus EXPIRED(String details, Date time) {
		CertificateStatus s = new CertificateStatus();
		s.code = CertificateStatusCode.EXPIRED;
		s.revocationTime = time;
		s.details = details;
		return s;
	}
}
