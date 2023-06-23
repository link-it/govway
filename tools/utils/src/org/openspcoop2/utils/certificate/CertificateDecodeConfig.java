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

package org.openspcoop2.utils.certificate;

/**
 * CertificateHttpUtilsConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificateDecodeConfig {

	private boolean urlDecode = false;
	
	private boolean base64Decode = false;
	
	private boolean hexDecode = false;
	
	private boolean urlDecodeOrBase64Decode = false;
	
	private boolean urlDecodeOrBase64DecodeOrHexDecode = false;
	
	private boolean enrichPEMBeginEnd = false;
	
	private boolean replace = false;
	private String replaceSource= "\t";
	private String replaceDest = "\n";
	
	public boolean isUrlDecode() {
		return this.urlDecode;
	}
	public void setUrlDecode(boolean urlDecode) {
		this.urlDecode = urlDecode;
	}
	public boolean isBase64Decode() {
		return this.base64Decode;
	}
	public void setBase64Decode(boolean base64Decode) {
		this.base64Decode = base64Decode;
	}
	public boolean isHexDecode() {
		return this.hexDecode;
	}
	public void setHexDecode(boolean hexDecode) {
		this.hexDecode = hexDecode;
	}
	public boolean isUrlDecodeOrBase64Decode() {
		return this.urlDecodeOrBase64Decode;
	}
	public void setUrlDecodeOrBase64Decode(boolean urlDecodeOrBase64Decode) {
		this.urlDecodeOrBase64Decode = urlDecodeOrBase64Decode;
	}
	public boolean isUrlDecodeOrBase64DecodeOrHexDecode() {
		return this.urlDecodeOrBase64DecodeOrHexDecode;
	}
	public void setUrlDecodeOrBase64DecodeOrHexDecode(boolean urlDecodeOrBase64DecodeOrHexDecode) {
		this.urlDecodeOrBase64DecodeOrHexDecode = urlDecodeOrBase64DecodeOrHexDecode;
	}
	public boolean isEnrichPEMBeginEnd() {
		return this.enrichPEMBeginEnd;
	}
	public void setEnrichPEMBeginEnd(boolean enrich) {
		this.enrichPEMBeginEnd = enrich;
	}
	public boolean isReplace() {
		return this.replace;
	}
	public void setReplace(boolean replace) {
		this.replace = replace;
	}
	public String getReplaceSource() {
		return this.replaceSource;
	}
	public void setReplaceSource(String replaceSource) {
		this.replaceSource = replaceSource;
	}
	public String getReplaceDest() {
		return this.replaceDest;
	}
	public void setReplaceDest(String replaceDest) {
		this.replaceDest = replaceDest;
	}
}
