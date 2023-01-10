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
	
	private boolean urlDecode_or_base64Decode = false;
	
	private boolean enrich_BEGIN_END = false;
	
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
	public boolean isUrlDecode_or_base64Decode() {
		return this.urlDecode_or_base64Decode;
	}
	public void setUrlDecode_or_base64Decode(boolean urlDecode_or_base64Decode) {
		this.urlDecode_or_base64Decode = urlDecode_or_base64Decode;
	}
	public boolean isEnrich_BEGIN_END() {
		return this.enrich_BEGIN_END;
	}
	public void setEnrich_BEGIN_END(boolean enrich_BEGIN_END) {
		this.enrich_BEGIN_END = enrich_BEGIN_END;
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
