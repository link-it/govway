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

import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;

/**
* PDFEmbeddedFile
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class EmbeddedFile {

	private PDComplexFileSpecification fileSpec;
	private PDEmbeddedFile embeddedFile;
	
	private String filename;
	private byte[] content;
	private String mediaType;
	
	public PDComplexFileSpecification getFileSpec() {
		return this.fileSpec;
	}
	public void setFileSpec(PDComplexFileSpecification fileSpec) {
		this.fileSpec = fileSpec;
	}
	public PDEmbeddedFile getEmbeddedFile() {
		return this.embeddedFile;
	}
	public void setEmbeddedFile(PDEmbeddedFile embeddedFile) {
		this.embeddedFile = embeddedFile;
	}
	public String getFilename() {
		return this.filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public byte[] getContent() {
		return this.content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public String getMediaType() {
		return this.mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
}
