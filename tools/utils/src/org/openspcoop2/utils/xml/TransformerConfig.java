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
package org.openspcoop2.utils.xml;

/**
 * TrasformerConfig
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransformerConfig {

	private boolean omitXMLDeclaration;
	private String charset;
	private boolean indent;
	
	public boolean isOmitXMLDeclaration() {
		return this.omitXMLDeclaration;
	}
	public void setOmitXMLDeclaration(boolean omitXMLDeclaration) {
		this.omitXMLDeclaration = omitXMLDeclaration;
	}
	public String getCharset() {
		return this.charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public boolean isIndent() {
		return this.indent;
	}
	public void setIndent(boolean indent) {
		this.indent = indent;
	}
}
