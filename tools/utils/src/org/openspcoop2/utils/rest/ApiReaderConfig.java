/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.rest;

import org.openspcoop2.utils.beans.BaseBean;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;

/**
 * ApiOperation
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiReaderConfig extends BaseBean {

	private boolean verbose = false;
	private boolean processInclude = true;
	private boolean processInlineSchema = true;
	private Charset charset = Charset.UTF_8;
	private AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
	
	public AbstractXMLUtils getXmlUtils() {
		return this.xmlUtils;
	}
	public void setXmlUtils(AbstractXMLUtils xmlUtils) {
		this.xmlUtils = xmlUtils;
	}
	public Charset getCharset() {
		return this.charset;
	}
	public void setCharset(Charset charset) {
		this.charset = charset;
	}
	public boolean isVerbose() {
		return this.verbose;
	}
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	public boolean isProcessInclude() {
		return this.processInclude;
	}
	public void setProcessInclude(boolean processInclude) {
		this.processInclude = processInclude;
	}
	public boolean isProcessInlineSchema() {
		return this.processInlineSchema;
	}
	public void setProcessInlineSchema(boolean processInlineSchema) {
		this.processInlineSchema = processInlineSchema;
	}
	
}
