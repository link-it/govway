/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.core.registry.wsdl.testsuite;

import java.io.File;
import java.util.List;

import javax.wsdl.Definition;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.wsdl.RegistroOpenSPCoopUtilities;
import org.openspcoop2.core.registry.wsdl.SplitWSDL;
import org.openspcoop2.core.registry.wsdl.SchemaXSDAccordoServizio;
import org.openspcoop2.core.registry.wsdl.ConverterStandardWSDL2SplitWSDL;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;


/**
*
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
*
* @author Lorenzo Nardi <nardi@link.it>
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class Utilities {

	public static byte[] generaWsdlBytes(File wsdl,String definitorio,String [] allegati,String [] specificheSemiformali) throws Exception{ 
		
		AccordoServizioParteComune as = new AccordoServizioParteComune();
		
		byte[] testA_wsdlBytesErogatore = FileSystemUtilities.readBytesFromFile(wsdl);
		as.setByteWsdlLogicoErogatore(testA_wsdlBytesErogatore);
		
		// Definitorio
		if(definitorio!=null){
			byte[] definitorioBytes = FileSystemUtilities.readBytesFromFile(new File(Costanti.TEST2_BASE_DIR,"definitorio"+File.separatorChar+definitorio));
			as.setByteWsdlDefinitorio(definitorioBytes);
		}
		
		// Allegati
		if(allegati!=null){
			for(int i=0;i<allegati.length;i++){
				Documento allegato = new Documento();
				allegato.setFile(allegati[i]);
				allegato.setByteContenuto(FileSystemUtilities.readBytesFromFile(new File(Costanti.TEST2_BASE_DIR,"allegati"+File.separatorChar+allegati[i])));
				as.addAllegato(allegato);
			}
		}
		
		// SpecificheSemiformali
		if(specificheSemiformali!=null){
			for(int i=0;i<specificheSemiformali.length;i++){
				Documento specifica = new Documento();
				specifica.setFile(specificheSemiformali[i]);
				specifica.setByteContenuto(FileSystemUtilities.readBytesFromFile(new File(Costanti.TEST2_BASE_DIR,"specificaSemiformale"+File.separatorChar+specificheSemiformali[i])));
				as.addSpecificaSemiformale(specifica);
			}
		}
		
		// Trasformazione in definition
		RegistroOpenSPCoopUtilities registroOpenSPCoopUtilities = new RegistroOpenSPCoopUtilities(null);
		Definition definition = registroOpenSPCoopUtilities.buildWsdlFromObjects(as, null, true);
		DefinitionWrapper defitionWrapper = new DefinitionWrapper(definition,XMLUtils.getInstance());
		//FileSystemUtilities.writeFile(new File("wsdlTestBytes.wsdl"), defitionWrapper.toByteArray());
		return defitionWrapper.toByteArray();
		
	}
	
	
	
	public static void writeSpcoopWsdlTo(File output,ConverterStandardWSDL2SplitWSDL wsdl2spcoop,boolean prettyPrint) throws Exception{
		
		XMLUtils xmlUtils = XMLUtils.getInstance();
		WSDLUtilities wsdlUtilities = WSDLUtilities.getInstance(xmlUtils);
		
		output.mkdir();
		
		File specificaInterfacce = new File(output,SplitWSDL.FOLDER_INTERFACCE);
		specificaInterfacce.mkdir();
		
		File concettuale = new File(specificaInterfacce,SplitWSDL.CONCETTUALE_FILENAME);
		wsdlUtilities.writeWsdlTo(wsdl2spcoop.getWsdlConcettuale(), concettuale, prettyPrint);
		
		File logicoErogatore = new File(specificaInterfacce,SplitWSDL.LOGICO_EROGATORE_FILENAME);
		wsdlUtilities.writeWsdlTo(wsdl2spcoop.getWsdlLogicoErogatore(), logicoErogatore, prettyPrint);
		
		if(wsdl2spcoop.getWsdlLogicoFruitore()!=null){
			File logicoFruitore = new File(specificaInterfacce,SplitWSDL.LOGICO_FRUITORE_FILENAME);
			wsdlUtilities.writeWsdlTo(wsdl2spcoop.getWsdlLogicoFruitore(), logicoFruitore, prettyPrint);
		}
		
		File specificaPortiAccesso = new File(output,SplitWSDL.FOLDER_IMPLEMENTATIVI);
		specificaPortiAccesso.mkdir();
		
		File implementativoErogatore = new File(specificaPortiAccesso,SplitWSDL.IMPLEMENTATIVO_EROGATORE_FILENAME);
		wsdlUtilities.writeWsdlTo(wsdl2spcoop.getWsdlImplementativoErogatore(), implementativoErogatore, prettyPrint);
		
		if(wsdl2spcoop.getWsdlImplementativoFruitore()!=null){
			File implementativoFruitore = new File(specificaPortiAccesso,SplitWSDL.IMPLEMENTATIVO_FRUITORE_FILENAME);
			wsdlUtilities.writeWsdlTo(wsdl2spcoop.getWsdlImplementativoFruitore(), implementativoFruitore, prettyPrint);
		}
				
		List<SchemaXSDAccordoServizio> schemiErogatore = wsdl2spcoop.getSchemiErogatore();
		for(int i=0; i<schemiErogatore.size(); i++){
			SchemaXSDAccordoServizio schema = schemiErogatore.get(i);
			schema.writeTo(output,prettyPrint);
		}
		
		List<SchemaXSDAccordoServizio> schemiFruitore = wsdl2spcoop.getSchemiFruitore();
		if(schemiFruitore!=null){
			for(int i=0; i<schemiFruitore.size(); i++){
				SchemaXSDAccordoServizio schema = schemiFruitore.get(i);
				schema.writeTo(output,prettyPrint);
			}
		}
		
	}
}
