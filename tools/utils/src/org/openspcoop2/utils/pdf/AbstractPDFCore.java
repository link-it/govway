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
package org.openspcoop2.utils.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* AbstractPDFCore
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AbstractPDFCore {

	protected PDDocument document;
	protected byte[] rawDocument;
	
	public AbstractPDFCore(PDDocument doc) throws UtilsException {
		this.document = doc;
		if(this.document==null) {
			throw new UtilsException("Document undefined");
		}
	}
	public AbstractPDFCore(byte [] content, boolean saveRawDocument) throws UtilsException {
		try {
			if(saveRawDocument) {
				this.rawDocument = content;
			}
			this.document = PDDocument.load(content);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public AbstractPDFCore(InputStream is, boolean saveRawDocument) throws UtilsException {
		try {
			if(saveRawDocument) {
				this.rawDocument = Utilities.getAsByteArray(is);
				try(ByteArrayInputStream bin = new ByteArrayInputStream(this.rawDocument)){
					this.document = PDDocument.load(bin);
				}
			}
			else {
				this.document = PDDocument.load(is);
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public AbstractPDFCore(File doc, boolean saveRawDocument) throws UtilsException {
		try {
			if(saveRawDocument) {
				this.rawDocument = FileSystemUtilities.readBytesFromFile(doc);
			}
			this.document = PDDocument.load(doc);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public PDDocument getDocument() {
		return this.document;
	}
	
	protected void checkDocumentCatalog() throws UtilsException {
		if(this.document.getDocumentCatalog()==null) {
			throw new UtilsException("DocumentCatalog undefined");
		}
	}
	
}
