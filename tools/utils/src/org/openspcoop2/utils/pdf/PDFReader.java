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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.pdmodel.interactive.form.PDXFAResource;
import org.openspcoop2.utils.UtilsException;

/**
* PDFReader
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class PDFReader extends AbstractPDFCore {
	
	public PDFReader(PDDocument doc) throws UtilsException {
		super(doc);
	}
	
	public PDFReader(byte[] content) throws UtilsException {
		super(content, false);
	}
	public PDFReader(File doc) throws UtilsException {
		super(doc, false);
	}
	public PDFReader(InputStream is) throws UtilsException {
		super(is, false);
	}
	
	public PDFReader(byte[] content, boolean analyzeSignature) throws UtilsException {
		super(content, analyzeSignature);
		if(analyzeSignature) {
			this.initSignature();
		}
	}
	public PDFReader(File doc, boolean analyzeSignature) throws UtilsException {
		super(doc, analyzeSignature);
		if(analyzeSignature) {
			this.initSignature();
		}
	}
	public PDFReader(InputStream is, boolean analyzeSignature) throws UtilsException {
		super(is, analyzeSignature);
		if(analyzeSignature) {
			this.initSignature();
		}
	}
		
	public Map<String, EmbeddedFile> getEmbeddedFiles(boolean analyzeKids) throws UtilsException{
		Map<String, EmbeddedFile> map = new HashMap<>();
		checkDocumentCatalog();
		try {
			PDDocumentNameDictionary namesDictionary = new PDDocumentNameDictionary( this.document.getDocumentCatalog());
			PDEmbeddedFilesNameTreeNode efTree = namesDictionary.getEmbeddedFiles();
			if (efTree != null) {
				Map<String, PDComplexFileSpecification> names = efTree.getNames();
				if (names != null && !names.isEmpty()) {
					readEmbeddedFiles(names, map);
				} 
				
				if(analyzeKids) {
					readEmbeddedFilesFromKids(efTree, map);
				}
	         }
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		return map;
	}
	private void readEmbeddedFilesFromKids(PDEmbeddedFilesNameTreeNode efTree, Map<String, EmbeddedFile> map) throws UtilsException{
		try {
			List<PDNameTreeNode<PDComplexFileSpecification>> kids = efTree.getKids();
			if(kids!=null && !kids.isEmpty()) {
				for (PDNameTreeNode<PDComplexFileSpecification> node : kids) {
					Map<String, PDComplexFileSpecification> names = node.getNames();
					readEmbeddedFiles(names, map);
				}
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private void readEmbeddedFiles(Map<String, PDComplexFileSpecification> names, Map<String, EmbeddedFile> map) throws UtilsException {
        for (Map.Entry<String, PDComplexFileSpecification> entry : names.entrySet()) {
        	PDComplexFileSpecification fileSpec = entry.getValue();
        	EmbeddedFile internalDoc = createPDFInternalDocument(fileSpec);
            if(internalDoc!=null) {
            	String fileName = internalDoc.getFilename()!=null ? internalDoc.getFilename() : "file-"+map.size()+1;
            	map.put(fileName, internalDoc);
            }
        }
    }
	private EmbeddedFile createPDFInternalDocument(PDComplexFileSpecification fileSpec) throws UtilsException {
		try {
			EmbeddedFile internalDoc = null;
			String filename = getFileName(fileSpec);
	        PDEmbeddedFile embeddedFile = getEmbeddedFile(fileSpec);
	        if(embeddedFile!=null) {
	        	internalDoc = new EmbeddedFile();
	        	internalDoc.setFileSpec(fileSpec);
	        	internalDoc.setEmbeddedFile(embeddedFile);
	        	internalDoc.setFilename(filename);
	        	internalDoc.setContent(embeddedFile.toByteArray());
	        	internalDoc.setMediaType(embeddedFile.getSubtype());
	        }
	        return internalDoc;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
    private String getFileName(PDComplexFileSpecification fileSpec) {
    	String filename = null;
        if (fileSpec != null) {
        	filename = fileSpec.getFileUnicode();
            if (filename == null) {
            	filename = fileSpec.getFileDos();
            }
            if (filename == null) {
            	filename = fileSpec.getFileMac();
            }
            if (filename == null) {
            	filename = fileSpec.getFileUnix();
            }
            if (filename == null) {
            	filename = fileSpec.getFile();
            }
        }
        return filename;
    }
    private PDEmbeddedFile getEmbeddedFile(PDComplexFileSpecification fileSpec) {
        PDEmbeddedFile embeddedFile = null;
        if (fileSpec != null) {
            embeddedFile = fileSpec.getEmbeddedFileUnicode();
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileDos();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileMac();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileUnix();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFile();
            }
        }
        return embeddedFile;
    }
    
    
    
    public Map<String, EmbeddedFile> getAnnotationFiles() throws UtilsException{
		Map<String, EmbeddedFile> map = new HashMap<>();
		try {
			if(this.document.getPages()!=null) {
				for (PDPage page : this.document.getPages()) {
					readAnnotationFiles(page, map);
	            }
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		return map;
	}
    private void readAnnotationFiles(PDPage page, Map<String, EmbeddedFile> map) throws UtilsException {
    	try {
    		if(page!=null && page.getAnnotations()!=null) {
                for (PDAnnotation annotation : page.getAnnotations()) {
                    if (annotation instanceof PDAnnotationFileAttachment) {
                        PDAnnotationFileAttachment annotationFileAttachment = (PDAnnotationFileAttachment) annotation;
                        readAnnotationFile(annotationFileAttachment, map);
                    }
                }
			}
    	}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
    }
    private void readAnnotationFile(PDAnnotationFileAttachment annotationFileAttachment, Map<String, EmbeddedFile> map) throws UtilsException {
    	try {
	    	PDComplexFileSpecification fileSpec = (PDComplexFileSpecification) annotationFileAttachment.getFile();
	        EmbeddedFile internalDoc = createPDFInternalDocument(fileSpec);
	        if(internalDoc!=null) {
	        	String fileName = internalDoc.getFilename()!=null ? internalDoc.getFilename() : "file-"+map.size()+1;
	        	map.put(fileName, internalDoc);
	        }
    	}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
    }
    
    
    
    public XFADocument getXFAFile() throws UtilsException{
    	checkDocumentCatalog();
    	try {
    		XFADocument doc = null;
    		PDAcroForm pdAcroForm = this.document.getDocumentCatalog().getAcroForm();
			if(pdAcroForm!=null && pdAcroForm.getXFA()!=null) {
				PDXFAResource xfa = pdAcroForm.getXFA();
				doc = new XFADocument();
				doc.setXfa(xfa);
				doc.setDocument(xfa.getDocument());
				doc.setContent(xfa.getBytes());
			}
			return doc;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
    }
    
    
    public boolean isSignaturesExist() {
    	return this.document.getDocumentCatalog()!=null && this.document.getDocumentCatalog().getAcroForm()!=null && this.document.getDocumentCatalog().getAcroForm().isSignaturesExist();
    }

    private List<PDFSignature> pdfSignatures = null;
    private synchronized void initSignature() throws UtilsException {
    	if(this.pdfSignatures==null) {
    		
    		checkDocumentCatalog();
    		
    		if(this.rawDocument==null) {
    			throw new UtilsException("Initialize PDFReader with boolean parameter 'analyzeSignature' enabled");
    		}
    		
    		try {
        		PDAcroForm pdAcroForm = this.document.getDocumentCatalog().getAcroForm();
    			if(pdAcroForm!=null) {
		    		Map<String, PDSignatureField> mapPDSignatureField = readMapSignatureField(pdAcroForm);
		            
		    		List<PDSignature> signatures = this.document.getSignatureDictionaries();
		            this.pdfSignatures = readSignatures(signatures, mapPDSignatureField);		    		
    			}
    			else {
    				this.pdfSignatures=new ArrayList<>(); // per evitare nuovamente una inizializzazione
    			}
    		}catch(Exception e) {
    			throw new UtilsException(e.getMessage(),e);
    		}
    	}
    }
    private Map<String, PDSignatureField> readMapSignatureField(PDAcroForm pdAcroForm) throws UtilsException{
    	try {
    		Map<String, PDSignatureField> mapPDSignatureField = new HashMap<>();
            for (PDField f : pdAcroForm.getFields()) {
            	if(f instanceof PDSignatureField) {
            		PDSignatureField signatureField = (PDSignatureField) f;
            		if(signatureField.getSignature()!=null) {
            			mapPDSignatureField.put(signatureField.getSignature().getName(), signatureField);
 	                }
            	}
			}
            return mapPDSignatureField;
    	}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
    }
    private List<PDFSignature> readSignatures(List<PDSignature> signatures, Map<String, PDSignatureField> mapPDSignatureField) throws UtilsException{
    	try {
    		List<PDFSignature> pdfSignaturesList = new ArrayList<>();
    		if(signatures!=null) {
    			for (PDSignature signature : signatures) {
	        		PDSignatureField signatureField = mapPDSignatureField.get(signature.getName());
	        		if(signatureField==null) {
	        			throw new UtilsException("SignatureField '"+signature.getName()+"' not found");
	        		}
	        		PDFSignature pdfSignature = new PDFSignature(this.rawDocument, signatureField, signature);
	        		pdfSignaturesList.add(pdfSignature);
	        	}
    		}
    		return pdfSignaturesList;
    	}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
    }
    public List<PDFSignature> getSignature() throws UtilsException {
    	if(this.pdfSignatures==null) {
    		initSignature();
    	}
    	return this.pdfSignatures;
    }
}
