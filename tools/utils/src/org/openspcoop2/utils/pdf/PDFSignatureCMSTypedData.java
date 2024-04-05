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
package org.openspcoop2.utils.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.openspcoop2.utils.CopyStream;

/**
* PDFSignatureCMSTypedData
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class PDFSignatureCMSTypedData implements CMSTypedData {

	private InputStream is;
	private ASN1ObjectIdentifier contentType;
	
	public PDFSignatureCMSTypedData(InputStream is, ASN1ObjectIdentifier contentType) {
		this.is = is;
		if(contentType==null) {
			this.contentType = new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId());	
		}
		else {
			this.contentType = contentType;
		}
	}
	
	@Override
	public Object getContent() {
		return this.is;
	}

	@Override
	public void write(OutputStream out) throws IOException, CMSException {
		try {
			CopyStream.copy(this.is, out);
		}catch(Exception e) {
			throw new IOException(e.getMessage(),e);
		}
	}

	@Override
	public ASN1ObjectIdentifier getContentType() {
		return this.contentType;
	}

}
