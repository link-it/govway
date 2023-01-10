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

package org.openspcoop2.utils.openapi.validator;

import java.util.List;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * MultipartUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MultipartUtilities {

	public static MimeMultipart buildMimeMultipart(String subtype,
			String contentTxt, String contentTypeTxt, String nameTxt, String fileNameTxt,
			String contentJson, String contentTypeJson, String nameJson, String fileNameJson,
			byte[] contentPdf, String contentTypePdf, String namePdf, String fileNamePdf,
			byte[] contentPdf2, String contentTypePdf2, String namePdf2, String fileNamePdf2,
			byte[] contentOther, String contentTypeOther, String nameOther, String fileNameOther) throws Exception {
		
		MimeMultipart mm = new MimeMultipart(subtype);
		
		if(contentTxt!=null) {
			BodyPart bodyTxt = new MimeBodyPart();
			String ct = contentTypeTxt;
			if(ct==null) {
				ct = HttpConstants.CONTENT_TYPE_PLAIN;
			}
			bodyTxt.setDataHandler(new DataHandler(new ByteArrayDataSource(contentTxt.getBytes(), ct)));
			if(contentTypeTxt!=null) {
				bodyTxt.addHeader(HttpConstants.CONTENT_TYPE, contentTypeTxt);
			}
			if(nameTxt!=null) {
				if(!"".equals(nameTxt)) {
					String hV = HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA+"; "+HttpConstants.CONTENT_DISPOSITION_NAME_PREFIX+""+nameTxt;
					String fileName = fileNameTxt;
					if(fileName!=null) {
						hV = hV + "; "+HttpConstants.CONTENT_DISPOSITION_FILENAME_PREFIX+fileName;
					}
					bodyTxt.addHeader(HttpConstants.CONTENT_DISPOSITION,hV);
				}
				else {
					bodyTxt.addHeader(HttpConstants.CONTENT_DISPOSITION, HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA);
				}
			}
			bodyTxt.addHeader("X-Custom-Header", "222");
			mm.addBodyPart(bodyTxt);
		}
		
		if(contentJson!=null) {
			BodyPart bodyJson = new MimeBodyPart();
			String ct = contentTypeJson;
			if(ct==null) {
				ct = HttpConstants.CONTENT_TYPE_JSON;
			}
			bodyJson.setDataHandler(new DataHandler(new ByteArrayDataSource(contentJson.getBytes(), ct)));
			if(contentTypeJson!=null) {
				bodyJson.addHeader(HttpConstants.CONTENT_TYPE, contentTypeJson);
			}
			if(nameJson!=null) {
				if(!"".equals(nameJson)) {
					String hV = HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA+"; "+HttpConstants.CONTENT_DISPOSITION_NAME_PREFIX+""+nameJson;
					String fileName = fileNameJson;
					if(fileName!=null) {
						hV = hV + "; "+HttpConstants.CONTENT_DISPOSITION_FILENAME_PREFIX+fileName;
					}
					bodyJson.addHeader(HttpConstants.CONTENT_DISPOSITION,hV);
				}
				else {
					bodyJson.addHeader(HttpConstants.CONTENT_DISPOSITION, HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA);
				}
			}
			mm.addBodyPart(bodyJson);
		}

		if(contentPdf!=null) {
			BodyPart bodyPdf = new MimeBodyPart();
			String ct = contentTypePdf;
			if(ct==null) {
				ct = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
			}
			bodyPdf.setDataHandler(new DataHandler(new ByteArrayDataSource(contentPdf, ct)));
			if(contentTypePdf!=null) {
				bodyPdf.addHeader(HttpConstants.CONTENT_TYPE, contentTypePdf);
			}
			if(namePdf!=null) {
				if(!"".equals(namePdf)) {
					String hV = HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA+"; "+HttpConstants.CONTENT_DISPOSITION_NAME_PREFIX+""+namePdf;
					String fileName = fileNamePdf;
					if(fileName!=null) {
						hV = hV + "; "+HttpConstants.CONTENT_DISPOSITION_FILENAME_PREFIX+fileName;
					}
					bodyPdf.addHeader(HttpConstants.CONTENT_DISPOSITION,hV);
				}
				else {
					bodyPdf.addHeader(HttpConstants.CONTENT_DISPOSITION, HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA);
				}
			}
			mm.addBodyPart(bodyPdf);
		}
		
		if(contentPdf2!=null) {
			BodyPart bodyPdf2 = new MimeBodyPart();
			String ct = contentTypePdf2;
			if(ct==null) {
				ct = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
			}
			bodyPdf2.setDataHandler(new DataHandler(new ByteArrayDataSource(contentPdf2, ct)));
			if(contentTypePdf2!=null) {
				bodyPdf2.addHeader(HttpConstants.CONTENT_TYPE, contentTypePdf2);
			}
			if(namePdf2!=null) {
				if(!"".equals(namePdf2)) {
					String hV = HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA+"; "+HttpConstants.CONTENT_DISPOSITION_NAME_PREFIX+""+namePdf2;
					String fileName = fileNamePdf2;
					if(fileName!=null) {
						hV = hV + "; "+HttpConstants.CONTENT_DISPOSITION_FILENAME_PREFIX+fileName;
					}
					bodyPdf2.addHeader(HttpConstants.CONTENT_DISPOSITION,hV);
				}
				else {
					bodyPdf2.addHeader(HttpConstants.CONTENT_DISPOSITION, HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA);
				}
			}
			mm.addBodyPart(bodyPdf2);
		}
		
		if(contentOther!=null) {
			BodyPart bodyOther = new MimeBodyPart();
			String ct = contentTypeOther;
			if(ct==null) {
				ct = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
			}
			bodyOther.setDataHandler(new DataHandler(new ByteArrayDataSource(contentOther, ct)));
			if(contentTypeOther!=null) {
				bodyOther.addHeader(HttpConstants.CONTENT_TYPE, contentTypeOther);
			}
			if(nameOther!=null) {
				if(!"".equals(nameOther)) {
					String hV = HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA+"; "+HttpConstants.CONTENT_DISPOSITION_NAME_PREFIX+""+nameOther;
					String fileName = fileNameOther;
					if(fileName!=null) {
						hV = hV + "; "+HttpConstants.CONTENT_DISPOSITION_FILENAME_PREFIX+fileName;
					}
					bodyOther.addHeader(HttpConstants.CONTENT_DISPOSITION,hV);
				}
				else {
					bodyOther.addHeader(HttpConstants.CONTENT_DISPOSITION, HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA);
				}
			}
			mm.addBodyPart(bodyOther);
		}
		
		return mm;
		
	}
	
	public static final String templateNumero = "NUMERO";
	public static MimeMultipart buildMimeMultipart(String subtype,
			List<byte[]> contents, String contentType, String name, String fileName
			) throws Exception {
		
		MimeMultipart mm = new MimeMultipart(subtype);
		
		int attachNumero = contents.size();
		
		for (int k = 0; k < attachNumero; k++) {
			byte[] content = contents.get(k);
			if(content!=null) {
				BodyPart body = new MimeBodyPart();
				String ct = contentType;
				if(ct==null) {
					ct = HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
				}
				body.setDataHandler(new DataHandler(new ByteArrayDataSource(content, ct)));
				if(contentType!=null) {
					body.addHeader(HttpConstants.CONTENT_TYPE, contentType);
				}
				if(name!=null) {
					if(!"".equals(name)) {
						String hV = HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA+"; "+HttpConstants.CONTENT_DISPOSITION_NAME_PREFIX+""+name;
						if(fileName!=null) {
							hV = hV + "; "+HttpConstants.CONTENT_DISPOSITION_FILENAME_PREFIX+fileName.replace(templateNumero, k+"");
						}
						body.addHeader(HttpConstants.CONTENT_DISPOSITION,hV);
					}
					else {
						body.addHeader(HttpConstants.CONTENT_DISPOSITION, HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_FORM_DATA);
					}
				}
				mm.addBodyPart(body);
			}
		}
		
		return mm;
	}
	
}
