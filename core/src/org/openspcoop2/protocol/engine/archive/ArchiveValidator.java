/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.engine.archive;

import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.IRegistryReader;

/**
 *  ArchiveValidator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveValidator {

	
	private IRegistryReader registryReader;
	
	public ArchiveValidator(IRegistryReader registryReader){
		this.registryReader = registryReader;
	}
	
	
	
	public void validateArchive(Archive archive, String protocolloEffettivo, 
			boolean validazioneDocumenti, ImportInformationMissingCollection importInformationMissingCollection, 
			String userLogin, boolean checkCorrelazioneAsincrona) throws Exception,ImportInformationMissingException{
		try{
			
			IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolloEffettivo);
			
			validateAndFillImportInformationMissing(archive, protocolFactory, importInformationMissingCollection, validazioneDocumenti, userLogin, checkCorrelazioneAsincrona);
							
		}catch(ImportInformationMissingException e){
			throw e;
		}catch(Exception e){
			throw e;
		}
	}
	
	private void validateAndFillImportInformationMissing(Archive archive,IProtocolFactory protocolFactory, 
			ImportInformationMissingCollection importInformationMissingCollection, boolean validazioneDocumenti, String userLogin, boolean checkCorrelazioneAsincrona) throws Exception,ImportInformationMissingException{
		
		ImporterInformationMissingUtils importerInformationMissingUtils = 
				new ImporterInformationMissingUtils(importInformationMissingCollection, this.registryReader, validazioneDocumenti, protocolFactory, userLogin, archive);
		
		// ArchiveInformationMissing
		if(archive.getInformationMissing()!=null){
			importerInformationMissingUtils.validateAndFillInformationMissing(archive.getInformationMissing());
		}
			
		// ServiziApplicativi
		for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
			importerInformationMissingUtils.validateAndFillServizioApplicativo(archive.getServiziApplicativi().get(i));
		}
		
		// PorteDelegate
		for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
			importerInformationMissingUtils.validateAndFillPortaDelegata(archive.getPorteDelegate().get(i));
		}
		
		// PorteApplicative
		for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
			importerInformationMissingUtils.validateAndFillPortaApplicativa(archive.getPorteApplicative().get(i));
		}
		
		// Accordi di Cooperazione
		for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
			importerInformationMissingUtils.validateAndFillAccordoCooperazione(archive.getAccordiCooperazione().get(i));
		}
				
		// Accordi di Servizio Parte Comune 
		for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
			importerInformationMissingUtils.validateAndFillAccordoServizioParteComune(archive.getAccordiServizioParteComune().get(i),
					checkCorrelazioneAsincrona);
		}
		
		// Accordi di Servizio Parte Specifica 
		for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
			importerInformationMissingUtils.validateAndFillAccordoServizioParteSpecifica(archive.getAccordiServizioParteSpecifica().get(i));
		}

		// Accordi di Servizio Composti
		for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
			importerInformationMissingUtils.validateAndFillAccordoServizioParteComune(archive.getAccordiServizioComposto().get(i),
					checkCorrelazioneAsincrona);
		}
		
		// Fruizioni
		for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
			importerInformationMissingUtils.validateAndFillFruitore(archive.getAccordiFruitori().get(i));
		}
			
		// Templates Names
		ImporterInformationMissingSetter.replaceTemplatesNames(archive);
	}
	
}
