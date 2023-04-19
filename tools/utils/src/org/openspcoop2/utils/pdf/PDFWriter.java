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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDXFAResource;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* PDFWriter
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class PDFWriter extends AbstractPDFCore {
	
	private static final String CONTENT_UNDEFINED = "content undefined";
	private static final String FILENAME_UNDEFINED = "fileName undefined";
	
	public static void writeTo(PDDocument doc, OutputStream os) throws UtilsException {
		PDFWriter writer = new PDFWriter(doc);
		writer.writeTo(os);
	}
	public static void writeTo(PDDocument doc, File file) throws UtilsException {
		PDFWriter writer = new PDFWriter(doc);
		writer.writeTo(file);
	}
	
	public PDFWriter(PDDocument doc) throws UtilsException {
		super(doc);
	}
	public PDFWriter(byte[] content) throws UtilsException {
		super(content, false);
	}
	public PDFWriter(File doc) throws UtilsException {
		super(doc, false);
	}
	public PDFWriter(InputStream is) throws UtilsException {
		super(is, false);
	}

	private void checkFile(File file) throws UtilsException {
		if(file==null) {
			throw new UtilsException("File undefined");
		}
	}
	
	public void writeTo(OutputStream os) throws UtilsException {
		if(os==null) {
			throw new UtilsException("OutputStream undefined");
		}
		try {
			this.document.save(os);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void writeTo(File file) throws UtilsException {
		if(file==null) {
			throw new UtilsException("OutputFile undefined");
		}
		try {
			this.document.save(file);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public void addEmbeddedFile(File file) throws UtilsException {
		addEmbeddedFile(file, null);
	}
	public void addEmbeddedFile(File file, String mediaType) throws UtilsException {
		checkFile(file);
		try(FileInputStream fin = new FileInputStream(file)) {
			this.addEmbeddedFile(file.getName(), fin, (int) file.length(), mediaType);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void addEmbeddedFile(String fileName, byte[] content) throws UtilsException {
		addEmbeddedFile(fileName, content, null);
	}
	public void addEmbeddedFile(String fileName, byte[] content, String mediaType) throws UtilsException {
		if(content==null) {
			throw new UtilsException(CONTENT_UNDEFINED);
		}
		try(ByteArrayInputStream bin = new ByteArrayInputStream(content)) {
			this.addEmbeddedFile(fileName, bin, content.length, mediaType);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void addEmbeddedFile(String fileName, InputStream content) throws UtilsException {
		addEmbeddedFile(fileName, content, -1);
	}
	public void addEmbeddedFile(String fileName, InputStream content, int size) throws UtilsException {
		addEmbeddedFile(fileName, content, size, null);
	}
	public void addEmbeddedFile(String fileName, InputStream content, String mediaType) throws UtilsException {
		addEmbeddedFile(fileName, content, -1, mediaType);
	}
	public void addEmbeddedFile(String fileName, InputStream content, int size, String mediaType) throws UtilsException {
		
		if(fileName==null) {
			throw new UtilsException(FILENAME_UNDEFINED);
		}
		if(content==null) {
			throw new UtilsException(CONTENT_UNDEFINED);
		}
		
		try {
			if(mediaType==null) {
				mediaType = MimeTypes.getInstance().getMimeType(new File(fileName));
			}
			
			checkDocumentCatalog();
			
			PDDocumentNameDictionary names = this.document.getDocumentCatalog().getNames();
			if(names==null) {
				names = new PDDocumentNameDictionary( this.document.getDocumentCatalog() );
				this.document.getDocumentCatalog().setNames( names );
			}
			
			PDEmbeddedFilesNameTreeNode efTree = names.getEmbeddedFiles();
			if(efTree==null) {
				efTree = new PDEmbeddedFilesNameTreeNode();
				names.setEmbeddedFiles( efTree );
			}
	
			PDComplexFileSpecification fs = new PDComplexFileSpecification();
			fs.setFile(fileName);
			PDEmbeddedFile ef = new PDEmbeddedFile(this.document, content );
			ef.setSubtype( mediaType );
			if(size>0) {
				ef.setSize( size );
			}
			ef.setCreationDate( new GregorianCalendar() );
			fs.setEmbeddedFile( ef );

			Map<String,PDComplexFileSpecification> efMapOrig = efTree.getNames();
			Map<String,PDComplexFileSpecification> efMap = new HashMap<>();
			if(efMapOrig!=null && !efMapOrig.isEmpty()) {
				for (Map.Entry<String,PDComplexFileSpecification> entry : efMapOrig.entrySet()) {
					String fName = entry.getKey();
					PDComplexFileSpecification orig = entry.getValue();
					efMap.put(fName, orig);
				}
			}
			efMap.put( fileName, fs );
			efTree.setNames( efMap );
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	public void addEmbeddedFileAsKid(File file) throws UtilsException {
		addEmbeddedFileAsKid(file, null);
	}
	public void addEmbeddedFileAsKid(File file, String mediaType) throws UtilsException {
		checkFile(file);
		try(FileInputStream fin = new FileInputStream(file)) {
			this.addEmbeddedFileAsKid(file.getName(), fin, (int) file.length(), mediaType);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void addEmbeddedFileAsKid(String fileName, byte[] content) throws UtilsException {
		addEmbeddedFileAsKid(fileName, content, null);
	}
	public void addEmbeddedFileAsKid(String fileName, byte[] content, String mediaType) throws UtilsException {
		if(content==null) {
			throw new UtilsException(CONTENT_UNDEFINED);
		}
		try(ByteArrayInputStream bin = new ByteArrayInputStream(content)) {
			this.addEmbeddedFileAsKid(fileName, bin, content.length, mediaType);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void addEmbeddedFileAsKid(String fileName, InputStream content) throws UtilsException {
		addEmbeddedFileAsKid(fileName, content, -1);
	}
	public void addEmbeddedFileAsKid(String fileName, InputStream content, int size) throws UtilsException {
		addEmbeddedFileAsKid(fileName, content, size, null);
	}
	public void addEmbeddedFileAsKid(String fileName, InputStream content, String mediaType) throws UtilsException {
		addEmbeddedFileAsKid(fileName, content, -1, mediaType);
	}
	public void addEmbeddedFileAsKid(String fileName, InputStream content, int size, String mediaType) throws UtilsException {
		
		if(fileName==null) {
			throw new UtilsException(FILENAME_UNDEFINED);
		}
		if(content==null) {
			throw new UtilsException(CONTENT_UNDEFINED);
		}
		
		try {
			if(mediaType==null) {
				mediaType = MimeTypes.getInstance().getMimeType(new File(fileName));
			}
			
			checkDocumentCatalog();
			
			PDDocumentNameDictionary names = this.document.getDocumentCatalog().getNames();
			if(names==null) {
				names = new PDDocumentNameDictionary( this.document.getDocumentCatalog() );
				this.document.getDocumentCatalog().setNames( names );
			}
			
			PDEmbeddedFilesNameTreeNode efTree = names.getEmbeddedFiles();
			if(efTree==null) {
				efTree = new PDEmbeddedFilesNameTreeNode();
				names.setEmbeddedFiles( efTree );
			}
	
			List<PDNameTreeNode<PDComplexFileSpecification>> kids = efTree.getKids();
			boolean kidsEmpty = false;
			if(kids==null) {
				kids = new ArrayList<>();
				kidsEmpty = true;
			}
			
			PDComplexFileSpecification fs = new PDComplexFileSpecification();
			fs.setFile(fileName);
			PDEmbeddedFile ef = new PDEmbeddedFile(this.document, content );
			ef.setSubtype( mediaType );
			if(size>0) {
				ef.setSize( size );
			}
			ef.setCreationDate( new GregorianCalendar() );
			fs.setEmbeddedFile( ef );
	
			PDEmbeddedFilesNameTreeNode pdNameTreeeNodeKids = new PDEmbeddedFilesNameTreeNode();
			Map<String,PDComplexFileSpecification> efMap = new HashMap<>();
			efMap.put( fileName, fs );
			pdNameTreeeNodeKids.setNames(efMap);
			kids.add(pdNameTreeeNodeKids);
			
			if(kidsEmpty) {
				efTree.setKids(kids);
			}
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	public void addAnnotationFile(File file) throws UtilsException {
		addAnnotationFile(file, null);
	}
	public void addAnnotationFile(File file, String mediaType) throws UtilsException {
		checkFile(file);
		try(FileInputStream fin = new FileInputStream(file)) {
			this.addAnnotationFile(file.getName(), fin, (int) file.length(), mediaType);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void addAnnotationFile(String fileName, byte[] content) throws UtilsException {
		addAnnotationFile(fileName, content, null);
	}
	public void addAnnotationFile(String fileName, byte[] content, String mediaType) throws UtilsException {
		if(content==null) {
			throw new UtilsException(CONTENT_UNDEFINED);
		}
		try(ByteArrayInputStream bin = new ByteArrayInputStream(content)) {
			this.addAnnotationFile(fileName, bin, content.length, mediaType);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void addAnnotationFile(String fileName, InputStream content) throws UtilsException {
		addAnnotationFile(fileName, content, -1);
	}
	public void addAnnotationFile(String fileName, InputStream content, int size) throws UtilsException {
		addAnnotationFile(fileName, content, size, null);
	}
	public void addAnnotationFile(String fileName, InputStream content, String mediaType) throws UtilsException {
		addAnnotationFile(fileName, content, -1, mediaType);
	}
	public void addAnnotationFile(String fileName, InputStream content, int size, String mediaType) throws UtilsException {
		
		if(fileName==null) {
			throw new UtilsException(FILENAME_UNDEFINED);
		}
		if(content==null) {
			throw new UtilsException(CONTENT_UNDEFINED);
		}
		
		try {
			if(mediaType==null) {
				mediaType = MimeTypes.getInstance().getMimeType(new File(fileName));
			}
			
			checkDocumentCatalog();
			
			if(this.document.getPages()==null || this.document.getNumberOfPages()<=0) {
				throw new UtilsException("DocumentPage not found");
			}
			PDPage page = this.document.getPage(0);
			if(page==null) {
				throw new UtilsException("FirstDocumentPage undefined");
			}
			List<PDAnnotation> annotations = page.getAnnotations();
			if(annotations==null) {
				annotations = new ArrayList<>();
			}
			
			PDComplexFileSpecification fs = new PDComplexFileSpecification();
			fs.setFile(fileName);
			PDEmbeddedFile ef = new PDEmbeddedFile(this.document, content );
			ef.setSubtype( mediaType );
			if(size>0) {
				ef.setSize( size );
			}
			ef.setCreationDate( new GregorianCalendar() );
			fs.setEmbeddedFile( ef );
	
			PDAnnotationFileAttachment fa = new PDAnnotationFileAttachment();
			fa.setFile(fs);
			
			annotations.add(fa);
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	public void setXFAFile(File file, boolean addXfaDatasets) throws UtilsException {
		byte[]content = null;
		try {
			content = FileSystemUtilities.readBytesFromFile(file);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		this.setXFAFile(content, addXfaDatasets);
	}
	public void setXFAFile(InputStream content, boolean addXfaDatasets) throws UtilsException {
		this.setXFAFile(Utilities.getAsByteArray(content), addXfaDatasets);
	}
	public void setXFAFile(byte[] content, boolean addXfaDatasets) throws UtilsException {
		
		if(content==null) {
			throw new UtilsException(CONTENT_UNDEFINED);
		}
		
		try {
			
			checkDocumentCatalog();
			
			PDAcroForm pdAcroForm = this.document.getDocumentCatalog().getAcroForm();
			if(pdAcroForm==null) {
				pdAcroForm = new PDAcroForm(this.document);
				this.document.getDocumentCatalog().setAcroForm(pdAcroForm);
			}
			
			if(addXfaDatasets) {
				content = XFADocument.addXfaDatasets(content);
			}

			COSStream base = new COSStream();
			try(OutputStream os = base.createOutputStream()){
				os.write(content);
				os.flush();
			}
			PDXFAResource xfa = new PDXFAResource(base);
			pdAcroForm.setXFA(xfa);
										
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
}
