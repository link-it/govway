/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.security.message.utils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * ProcessingPartUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProcessingPartUtils {


	private String processingPartsConstant;
	private String processingPartsElementConstant;
	private String processingPartsContentConstant;
	private String processingPartsNamespaceAttachConstant;
	private String processingPartsCompleteConstant;

	public ProcessingPartUtils(String processingPartsConstant,
			String processingPartsElementConstant,
			String processingPartsContentConstant,
			String processingPartsNamespaceAttachConstant,
			String processingPartsCompleteConstant) {

		this.processingPartsConstant = processingPartsConstant;
		this.processingPartsElementConstant = processingPartsElementConstant;
		this.processingPartsContentConstant = processingPartsContentConstant;
		this.processingPartsNamespaceAttachConstant = processingPartsNamespaceAttachConstant;
		this.processingPartsCompleteConstant = processingPartsCompleteConstant;
	}

	public List<ProcessingPart<?,?>> getProcessingParts(String processingPartString) throws Exception {
		List<ProcessingPart<?,?>> lst = new ArrayList<ProcessingPart<?,?>>();
		String[] split = processingPartString.split(";");
		for (int i = 0; i < split.length; i++) {
			String[]split2 = split[i].trim().split("}");
			String tipo = split2[0].trim().substring(1); // Element o Content
			
			if(!this.processingPartsContentConstant.equals(tipo) && !this.processingPartsElementConstant.equals(tipo)){
				throw new Exception(this.processingPartsConstant+"["+i+"] possiede un tipo non supportato (supportati:"+this.processingPartsContentConstant
						+","+this.processingPartsElementConstant+"): "+tipo);
			}
			
			String namespace = split2[1].trim().substring(1);
			if(this.processingPartsNamespaceAttachConstant.equals(namespace)){
				String indice = split2[2].trim().substring(1);
				lst.add(getAttachmentProcessingPart(tipo, indice, i));
			} else {
				String nome = split2[2].trim();
				lst.add(getProcessingPart(tipo, namespace, nome));
			}
		}
		
		return lst;
	}

	private ElementProcessingPart getProcessingPart(String tipo, String namespace, String nome) throws Exception {
		QName elemento = new QName(namespace,nome);
		return new ElementProcessingPart(elemento, this.processingPartsContentConstant.equals(tipo));
	}

	private AttachmentProcessingPart getAttachmentProcessingPart(String tipo, String indice, int indexPart) throws Exception {

		boolean complete = this.processingPartsCompleteConstant.equals(tipo);
		if("*".equals(indice) || "".equals(indice)){
			return new AttachmentProcessingPart(complete);
		} else{
			int indexAllegato = -1;
			try{
				indexAllegato = Integer.parseInt(indice);
				return new AttachmentProcessingPart(indexAllegato, complete);
			}catch(NumberFormatException e){
				throw new Exception("Property "+this.processingPartsConstant+ "["+indexPart+"] (Attach) con un indice ["+indice+"] che non risulta essere ne un numero intero ne un carattere speciale", e);
			}

		}

	}
	
	public static ProcessingPartUtils getSignatureInstance() {
		return new ProcessingPartUtils(SecurityConstants.SIGNATURE_PARTS, SecurityConstants.SIGNATURE_PART_ELEMENT, SecurityConstants.SIGNATURE_PART_CONTENT, SecurityConstants.SIGNATURE_NAMESPACE_ATTACH, SecurityConstants.SIGNATURE_PART_COMPLETE);
	}
	
	public static ProcessingPartUtils getEncryptionInstance() {
		return new ProcessingPartUtils(SecurityConstants.ENCRYPTION_PARTS, SecurityConstants.ENCRYPTION_PART_ELEMENT, SecurityConstants.ENCRYPTION_PART_CONTENT, SecurityConstants.ENCRYPTION_NAMESPACE_ATTACH, SecurityConstants.ENCRYPTION_PART_COMPLETE);
	}
	
}
