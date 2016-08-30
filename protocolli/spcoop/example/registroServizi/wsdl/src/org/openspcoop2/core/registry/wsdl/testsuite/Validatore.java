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


package org.openspcoop2.core.registry.wsdl.testsuite;

import it.gov.spcoop.sica.dao.Costanti;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;

import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.common.toolspec.ToolSpec;
import org.apache.cxf.tools.validator.WSDLValidator;
import org.apache.cxf.tools.wsdlto.WSDLToJava;
import org.openspcoop2.core.registry.wsdl.SplitWSDL;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;


/**
*
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
*
* @author Lorenzo Nardi <nardi@link.it>
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class Validatore {

	
	
	
	public static void verificaFileAttesi(File baseDir,boolean checkErogatore,boolean checkFruitore,boolean checkImplementativo,
			boolean definitorioAsAllegati,String [] xsd,int numeroFileXSDEstrattiTypes)
		throws Exception{
		
		System.out.println("\t validazione struttura wsdl/xsd ...");
		
		// Verifica WSDL Concettuale
		File wsdlConcettuale = new File(baseDir,Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL);
		Validatore.isFileExists(wsdlConcettuale.getAbsolutePath(),"WSDL2SPCoop");
		
		if(checkErogatore){
			
			// Verifica WSDL LogicoErogatore
			File wsdlLogicoErogatore = new File(baseDir,Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL);
			Validatore.isFileExists(wsdlLogicoErogatore.getAbsolutePath(),"WSDL2SPCoop");	
			
			if(checkImplementativo){				
				// Verifica WSDL ImplementativoErogatore
				File wsdlImplementativoErogatore = new File(baseDir,Costanti.SPECIFICA_PORTI_ACCESSO_DIR+File.separatorChar+Costanti.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL);
				Validatore.isFileExists(wsdlImplementativoErogatore.getAbsolutePath(),"WSDL2SPCoop");	
			}
		}
		
		File wsdlLogicoFruitore = new File(baseDir,Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL);
		File wsdlImplementativoFruitore = new File(baseDir,Costanti.SPECIFICA_PORTI_ACCESSO_DIR+File.separatorChar+Costanti.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL);
		if(checkFruitore){
			
			// Verifica WSDL LogicoFruitore
			Validatore.isFileExists(wsdlLogicoFruitore.getAbsolutePath(),"WSDL2SPCoop");	
			
			if(checkImplementativo){				
				// Verifica WSDL ImplementativoFruitore
				Validatore.isFileExists(wsdlImplementativoFruitore.getAbsolutePath(),"WSDL2SPCoop");	
			}
		}
		else{
			Validatore.isFileNotExists(wsdlLogicoFruitore.getAbsolutePath(),"WSDL2SPCoop");
			Validatore.isFileNotExists(wsdlImplementativoFruitore.getAbsolutePath(),"WSDL2SPCoop");
		}
		
		// Verifico se tra gli allegati di default esiste gia un'interfaccia definitoria
		boolean esisteInterfacciaDefinitoriaSchemiInput = false;
		for(int k=0;k<xsd.length;k++){
			if(xsd[k].equals(Costanti.ALLEGATO_DEFINITORIO_XSD)){
				esisteInterfacciaDefinitoriaSchemiInput = true;
				break;
			}
		}
		
		if(definitorioAsAllegati){
			
			// allegati attesi
			for(int i=0;i<xsd.length;i++){
				File allegato = new File(baseDir,Costanti.ALLEGATI_DIR+File.separatorChar+xsd[i]);
				Validatore.isFileExists(allegato.getAbsolutePath(),"WSDL2SPCoop");	
			}
			
			// check InterfacciaDefinitoria
			int numeroInterfaccieDefinitorieGenerate = numeroFileXSDEstrattiTypes;
			if(esisteInterfacciaDefinitoriaSchemiInput==false){
				numeroInterfaccieDefinitorieGenerate--;
				File allegato = new File(baseDir,Costanti.ALLEGATI_DIR+File.separatorChar+"InterfacciaDefinitoria.xsd");
				Validatore.isFileExists(allegato.getAbsolutePath(),"WSDL2SPCoop");	
			}
			
			// Numero file generati
			int i=0;
			for(;i<numeroInterfaccieDefinitorieGenerate;i++){
				File allegato = new File(baseDir,Costanti.ALLEGATI_DIR+File.separatorChar+"InterfacciaDefinitoria_"+i+".xsd");
				Validatore.isFileExists(allegato.getAbsolutePath(),"WSDL2SPCoop");	
			}
			File allegato = new File(baseDir,Costanti.ALLEGATI_DIR+File.separatorChar+"InterfacciaDefinitoria_"+i+".xsd");
			Validatore.isFileNotExists(allegato.getAbsolutePath(),"WSDL2SPCoop");
			
		}else{
			
			// allegati attesi
			for(int i=0;i<xsd.length;i++){
				File specifica = new File(baseDir,Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+xsd[i]);
				Validatore.isFileExists(specifica.getAbsolutePath(),"WSDL2SPCoop");	
			}
			
			// check InterfacciaDefinitoria
			int numeroInterfaccieDefinitorieGenerate = numeroFileXSDEstrattiTypes;
			if(esisteInterfacciaDefinitoriaSchemiInput==false){
				numeroInterfaccieDefinitorieGenerate--;
				File specifica = new File(baseDir,Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+"InterfacciaDefinitoria.xsd");
				Validatore.isFileExists(specifica.getAbsolutePath(),"WSDL2SPCoop");	
			}
			
			// Numero file generati
			int i=0;
			for(;i<numeroInterfaccieDefinitorieGenerate;i++){
				File specifica = new File(baseDir,Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+"InterfacciaDefinitoria_"+i+".xsd");
				Validatore.isFileExists(specifica.getAbsolutePath(),"WSDL2SPCoop");	
			}
			File specifica = new File(baseDir,Costanti.SPECIFICA_SEMIFORMALE_DIR+File.separatorChar+"InterfacciaDefinitoria_"+i+".xsd");
			Validatore.isFileNotExists(specifica.getAbsolutePath(),"WSDL2SPCoop");	
			
		}
		
		System.out.println("\t validazione struttura wsdl/xsd ok");
	}
	
	
	public static void validaOutput(String base, 
			String[] portTypeErogatore,String[] operationsErogatore, String [] listaFilesAttesiErogatore,String [] listaFilesAttesiBindingErogatore,
			String[] portTypeFruitore,String[] operationsFruitore, String [] listaFilesAttesiFruitore,String [] listaFilesAttesiBindingFruitore) throws Exception { 

		// Inserisco in portType sia i pt dell'erogatore che del fruitore (per il concettuale)
		String[] portType = new String[portTypeErogatore.length + portTypeFruitore.length];
		String[] operation = new String[operationsErogatore.length + operationsFruitore.length];
		int i = 0;
		for(; i<portTypeErogatore.length; i++){
			portType[i] = portTypeErogatore[i];
		}
		for(int j=0;j<portTypeFruitore.length;j++){
			portType[i] = portTypeFruitore[j];
			i++;
		}
		i = 0;
		for(; i<operationsErogatore.length; i++){
			operation[i] = operationsErogatore[i];
		}
		for(int j=0;j<operationsFruitore.length;j++){
			operation[i] = operationsFruitore[j];
			i++;
		}
		
		// Inserisco in listaFileAttesiComplessivi sia i files attesi per l'erogatore che per il fruitore (per il concettuale)
		Vector<String> listaFileAttesiComplessivi = new Vector<String>();
		for(int k=0; k<listaFilesAttesiErogatore.length; k++){
			listaFileAttesiComplessivi.add(listaFilesAttesiErogatore[k]);
		}
		for(int k=0; k<listaFilesAttesiFruitore.length; k++){
			listaFileAttesiComplessivi.add(listaFilesAttesiFruitore[k]);
		}
				
		XMLUtils xmlUtils = XMLUtils.getInstance();
		
		// WSDL Concettuale
		System.out.println("\tWSDL Concettuale ...");
		String concettualePath = base + File.separator + SplitWSDL.FOLDER_INTERFACCE + File.separator + SplitWSDL.CONCETTUALE_FILENAME;
		DefinitionWrapper concettuale = new DefinitionWrapper(concettualePath,xmlUtils);
		Validatore.verificaPresenzaElementiWSDLAttesi(concettuale, portType,operation,false);
		Validatore.validazioneWSDL(concettualePath,true);
		Validatore.generazioneVerificaStubSkeleton("CONCETTUALE", concettualePath, listaFileAttesiComplessivi.toArray(new String[1]),null,true);
		System.out.println("\tWSDL Concettuale ok");

		// WSDL ImplementativoErogatore
		System.out.println("\tWSDL ImplementativoErogatore ...");
		String implementativoErogatorePath = base + File.separator + SplitWSDL.FOLDER_IMPLEMENTATIVI + File.separator + SplitWSDL.IMPLEMENTATIVO_EROGATORE_FILENAME;
		DefinitionWrapper implementativoErogatore = new DefinitionWrapper(implementativoErogatorePath,xmlUtils);
		Validatore.verificaPresenzaElementiWSDLAttesi(implementativoErogatore, portTypeErogatore,operationsErogatore,true);
		Validatore.validazioneWSDL(implementativoErogatorePath,false);
		Validatore.generazioneVerificaStubSkeleton("EROGATORE", implementativoErogatorePath, listaFilesAttesiErogatore,listaFilesAttesiBindingErogatore,true);
		System.out.println("\tWSDL ImplementativoErogatore ok");
		
		// WSDL ImplementativoFruitore
		if(portTypeFruitore.length > 0){
			System.out.println("\tWSDL ImplementativoFruitore ...");
			String implementativoFruitorePath = base + File.separator + SplitWSDL.FOLDER_IMPLEMENTATIVI + File.separator + SplitWSDL.IMPLEMENTATIVO_FRUITORE_FILENAME;
			DefinitionWrapper implementativoFruitore = new DefinitionWrapper(implementativoFruitorePath,xmlUtils);
			Validatore.verificaPresenzaElementiWSDLAttesi(implementativoFruitore, portTypeFruitore,operationsFruitore,true);
			Validatore.validazioneWSDL(implementativoFruitorePath,false);
			Validatore.generazioneVerificaStubSkeleton("FRUITORE", implementativoFruitorePath, listaFilesAttesiFruitore,listaFilesAttesiBindingFruitore,true);
			System.out.println("\tWSDL ImplementativoFruitore ok");
		}
	}
	public static void validaOutputErogatore(String base, String[] portTypes,String[] operations,String [] listaFilesAttesi,String [] listaFilesAttesiBinding) throws Exception { 

		XMLUtils xmlUtils = XMLUtils.getInstance();
		
		// WSDL Concettuale
		System.out.println("\tWSDL Concettuale ...");
		String concettualePath = base + File.separator + SplitWSDL.FOLDER_INTERFACCE + File.separator + SplitWSDL.CONCETTUALE_FILENAME;
		DefinitionWrapper concettuale = new DefinitionWrapper(concettualePath,xmlUtils);
		Validatore.verificaPresenzaElementiWSDLAttesi(concettuale, portTypes,operations,false);
		Validatore.validazioneWSDL(concettualePath,true);
		Validatore.generazioneVerificaStubSkeleton("CONCETTUALE", concettualePath, listaFilesAttesi,null,true);
		System.out.println("\tWSDL Concettuale ok");

		// WSDL ImplementativoErogatore
		System.out.println("\tWSDL ImplementativoErogatore ...");
		String implementativoErogatorePath = base + File.separator + SplitWSDL.FOLDER_IMPLEMENTATIVI + File.separator + SplitWSDL.IMPLEMENTATIVO_EROGATORE_FILENAME;
		DefinitionWrapper implementativoErogatore = new DefinitionWrapper(implementativoErogatorePath,xmlUtils);
		Validatore.verificaPresenzaElementiWSDLAttesi(implementativoErogatore, portTypes,operations,true);
		Validatore.validazioneWSDL(implementativoErogatorePath,false);
		Validatore.generazioneVerificaStubSkeleton("EROGATORE", implementativoErogatorePath, listaFilesAttesi,listaFilesAttesiBinding,true);
		System.out.println("\tWSDL ImplementativoErogatore ok");

	}
	public static void validaOutputFruitore(String base, String[] portTypes,String[] operations,String [] listaFilesAttesi,String [] listaFilesAttesiBinding) throws Exception { 

		XMLUtils xmlUtils = XMLUtils.getInstance();
		
		// WSDL Concettuale
		System.out.println("\tWSDL Concettuale ...");
		String concettualePath = base + File.separator + SplitWSDL.FOLDER_INTERFACCE + File.separator + SplitWSDL.CONCETTUALE_FILENAME;
		DefinitionWrapper concettuale = new DefinitionWrapper(concettualePath,xmlUtils);
		Validatore.verificaPresenzaElementiWSDLAttesi(concettuale, portTypes,operations,false);
		Validatore.validazioneWSDL(concettualePath,true);
		Validatore.generazioneVerificaStubSkeleton("CONCETTUALE", concettualePath, listaFilesAttesi,null,true);
		System.out.println("\tWSDL Concettuale ok");

		// WSDL ImplementativoFruitore
		System.out.println("\tWSDL ImplementativoFruitore ...");
		String implementativoFruitorePath = base + File.separator + SplitWSDL.FOLDER_IMPLEMENTATIVI + File.separator + SplitWSDL.IMPLEMENTATIVO_FRUITORE_FILENAME;
		DefinitionWrapper implementativoFruitore = new DefinitionWrapper(implementativoFruitorePath,xmlUtils);
		Validatore.verificaPresenzaElementiWSDLAttesi(implementativoFruitore, portTypes,operations,true);
		Validatore.validazioneWSDL(implementativoFruitorePath,false);
		Validatore.generazioneVerificaStubSkeleton("FRUITORE", implementativoFruitorePath, listaFilesAttesi,listaFilesAttesiBinding,true);
		System.out.println("\tWSDL ImplementativoFruitore ok");

	}

	public static void verificaPresenzaElementiWSDLAttesi(DefinitionWrapper wsdl, String[] portType, String[]operation,boolean implementativo) throws Exception{
		System.out.println("\t\t verifica presenza elementi WSDL attesi ...");
		Map<?, ?> m = wsdl.getAllPortTypes();
				
		if(m.size() != portType.length) throw new Exception("Il numero di PortType del wsdl ("+m.size()+") e quelli previsti ("+portType.length+") non corrispondono");
		Iterator<?> it = m.values().iterator();
		while (it.hasNext()){
			PortType now = (PortType) it.next();
			String cuBinName = now.getQName().getLocalPart();
			boolean found = false;
			for(int i=0; i<portType.length;i++){
				String check = portType[i];
				if (check.equals(cuBinName))
				{
					
					// Check operation
					List<?> operations = now.getOperations();
					String [] operationsAttese = operation[i].split(",");
					if(operations.size()!=operationsAttese.length){
						throw new Exception("Il PortType [" + cuBinName + "] non possiede tutte e sole le operations attese (PT="+operations.size()+" attese="+operationsAttese.length+").");
					}
					for(int j=0;j<operations.size();j++){
						Operation op = (Operation) operations.get(j);
						boolean opFind = false;
						for(int k=0;k<operationsAttese.length;k++){
							if(op.getName().equals(operationsAttese[k])){
								opFind = true;
								break;
							}
						}
						if(opFind==false){
							throw new Exception("Il PortType [" + cuBinName + "]  possiede l'operation ["+op.getName()+"] non richiesta.");
						}
					}
					
					found = true;
					break;
				}
			}
			if(!found){
				throw new Exception("Il PortType [" + cuBinName + "] non e' nella lista dei PortType previsti.");
			}
		}
		
		if(implementativo){
			
			Hashtable<String, String> mappingPortTypeBinding = new Hashtable<String, String>();
			
			// CHECK BINDING
			Map<?, ?> mBindings = wsdl.getAllBindings();
			if(mBindings.size() != portType.length) throw new Exception("Il numero di Bindings del wsdl ("+mBindings.size()+") e quelli previsti ("+portType.length+") non corrispondono");
			Iterator<?> itBinding = mBindings.values().iterator();
			while (itBinding.hasNext()){
				Binding b = (Binding) itBinding.next();
				String cuBinName = b.getPortType().getQName().getLocalPart();
				
				mappingPortTypeBinding.put(cuBinName, b.getQName().getLocalPart());
				
				boolean found = false;
				for(int i=0; i<portType.length;i++){
					String check = portType[i];
					if (check.equals(cuBinName))
					{
						// Check operation
						List<?> operations = b.getBindingOperations();
						String [] operationsAttese = null;
						for(int j=0; j<portType.length;j++){
							if (portType[j].equals(cuBinName)){
								operationsAttese = operation[j].split(",");
							}
						}
						if(operations.size()!=operationsAttese.length){
							throw new Exception("Il Binding [" + b.getQName().getLocalPart() + "] implementa il port type [" + cuBinName + "] ma non possiede tutte e sole le operations attese.");
						}
						for(int j=0;j<operations.size();j++){
							BindingOperation op = (BindingOperation) operations.get(j);
							boolean opFind = false;
							for(int k=0;k<operationsAttese.length;k++){
								if(op.getName().equals(operationsAttese[k])){
									opFind = true;
									break;
								}
							}
							if(opFind==false){
								throw new Exception("Il Binding [" + b.getQName().getLocalPart() + "] implementa il port type [" + cuBinName + "] ma pero' non possiede l'operation ["+op.getName()+"] non richiesta.");
							}
						}
						
						
						found = true;
						break;
					}
				}
				if(!found){
					throw new Exception("Il Binding [" + b.getQName().getLocalPart() + "] implementa un port type["+cuBinName+"] che non e' nella lista dei PortType previsti.");
				}
			}
			
			
			// CHECK SERVICES
			Map<?, ?> mServices = wsdl.getAllServices();
			if(mServices.size() != portType.length) throw new Exception("Il numero di Services del wsdl ("+mServices.size()+") e quelli previsti ("+portType.length+") non corrispondono");
			Iterator<?> itServices = mServices.values().iterator();
			while (itServices.hasNext()){
				Service s = (Service) itServices.next();
				Port port = (Port) s.getPorts().values().iterator().next();
				String bindingName = port.getBinding().getQName().getLocalPart();
				if(mappingPortTypeBinding.containsValue(bindingName)==false){
					throw new Exception("Il Service [" + s.getQName().getLocalPart() + "] implementa un binding["+bindingName+"] non previsto.");
				}
			}
		}
		
		System.out.println("\t\t verifica presenza elementi WSDL attesi ok");
	}

	public static void validazioneWSDL(String src,boolean concettuale) throws Exception{
		
		System.out.println("\t\t validazione wsdl ...");
		FileOutputStream fosInfo= null,fosError = null;
		PrintStream psInfo= null,psError= null;	
		PrintStream infoOriginal = System.out;
		PrintStream errorOriginal = System.err;
		File fInfoLog = new File("validazione.info.log");
		File fErrorLog = new File("validazione.error.log");
		try{
			fosInfo = new FileOutputStream(fInfoLog);
			psInfo = new PrintStream(fosInfo);
			fosError = new FileOutputStream(fErrorLog);
			psError = new PrintStream(fosError);
			System.setErr(psError);
			System.setOut(psInfo);
			
			ToolSpec toolSpec = new ToolSpec(org.apache.cxf.tools.validator.WSDLValidator.class.getResourceAsStream("wsdlvalidator.xml"));
			org.apache.cxf.tools.validator.WSDLValidator validator = new WSDLValidator(toolSpec);
			String[] args = {"-quiet",src};
			validator.setArguments(args);
			validator.setContext(new ToolContext());
			ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
			validator.setErrOutputStream(errorOutput);
			ByteArrayOutputStream infoOutput = new ByteArrayOutputStream();
			validator.setOutOutputStream(infoOutput);
			validator.execute(false);
			//System.out.println("RESULT: "+infoOutput);
			//System.out.println("ERROR: "+errorOutput);
			if(errorOutput.size()!=0){
				String msg = "WSDLValidator Error : WSI-BP-1.0 R2718 violation: A wsdl:binding in a DESCRIPTION MUST have the same set of wsdl:operations as the wsdl:portType to which it refers. ";
				String msg2 = " not found in wsdl:binding.";
				if(concettuale){
					if(errorOutput.toString().contains(msg)==false || errorOutput.toString().contains(msg2)==false){
						throw new Exception(errorOutput.toString()+infoOutput.toString());
					}
				}
				else{
					throw new Exception(errorOutput.toString()+infoOutput.toString());
				}
			}
			
			try{
				psError.flush();
				psError.close();
			}catch(Exception eClose){}
			try{
				psInfo.flush();
				psInfo.close();
			}catch(Exception eClose){}
			try{
				fosError.flush();
				fosError.close();
			}catch(Exception eClose){}
			try{
				fosInfo.flush();
				fosInfo.close();
			}catch(Exception eClose){}
			try{
				fInfoLog.deleteOnExit();
			}catch(Exception eClose){}
			try{
				fErrorLog.deleteOnExit();
			}catch(Exception eClose){}
				
		}finally{
			System.setErr(errorOriginal);
			System.setOut(infoOriginal);
		}
		System.out.println("\t\t validazione wsdl ok");
	}

	
	public static void generazioneVerificaStubSkeleton(String dirName,String fileWSDL,String [] listaFilesAttesi,String [] listaFilesAttesiBinding,boolean print) throws Exception{
		
		if(print)
			System.out.println("\t\t generazione stub/skeleton ...");
		
		String outputDir = "STUB_SKELETON_"+dirName;

        // Delete directory dove vengono prodotte le classi prima di produrre il test.
        FileSystemUtilities.deleteDir(outputDir);
		
		FileOutputStream fosInfo= null,fosError = null;
		PrintStream psInfo= null,psError= null;	
		PrintStream infoOriginal = System.out;
		PrintStream errorOriginal = System.err;
		File fInfoLog = new File("generazione.info.log");
		File fErrorLog = new File("generazione.error.log");
		try{
			fosInfo = new FileOutputStream(fInfoLog);
			psInfo = new PrintStream(fosInfo);
			fosError = new FileOutputStream(fErrorLog);
			psError = new PrintStream(fosError);
			System.setErr(psError);
			System.setOut(psInfo);

           
			ToolContext toolCtx = new ToolContext();
			String[] args = {"-quiet","-all","-d",outputDir,fileWSDL};
			org.apache.cxf.tools.wsdlto.WSDLToJava wsdl2java = new WSDLToJava(args);
			wsdl2java.run(toolCtx);
			
			try{
				psError.flush();
				psError.close();
			}catch(Exception eClose){}
			try{
				psInfo.flush();
				psInfo.close();
			}catch(Exception eClose){}
			try{
				fosError.flush();
				fosError.close();
			}catch(Exception eClose){}
			try{
				fosInfo.flush();
				fosInfo.close();
			}catch(Exception eClose){}
			try{
				fInfoLog.deleteOnExit();
			}catch(Exception eClose){}
			try{
				fErrorLog.deleteOnExit();
			}catch(Exception eClose){}
				
		}finally{
			System.setErr(errorOriginal);
			System.setOut(infoOriginal);
		}
	
		try{
			if(print)System.out.println("\t\t generazione stub/skeleton ok");
			
			if(print)System.out.println("\t\t validazione stub/skeleton ...");
			
			for(int i=0;i<listaFilesAttesi.length;i++){
				String checkFile = outputDir+File.separatorChar+listaFilesAttesi[i];
				Validatore.isFileExists(checkFile,"axis.WSDL2Java");
			}
			
			if(listaFilesAttesiBinding!=null){
				for(int i=0;i<listaFilesAttesiBinding.length;i++){
					String checkFile = outputDir+File.separatorChar+listaFilesAttesiBinding[i];
					Validatore.isFileExists(checkFile,"axis.WSDL2Java");
				}
			}
			if(print)System.out.println("\t\t validazione stub/skeleton ok");
		}finally{
		
			// 	Delete directory dove vengono prodotte le classi.
			FileSystemUtilities.deleteDir(outputDir);
		
		}
		
	}
	
	private static void isFileExists(String file,String actor) throws Exception{
		if ((new File(file)).exists()==false){
			throw new Exception((new File(file)).getAbsolutePath()+" non esistente? "+actor+" non ha prodotto l'output atteso");
		}
	}
	
	private static void isFileNotExists(String file,String actor) throws Exception{
		if ((new File(file)).exists()){
			throw new Exception((new File(file)).getAbsolutePath()+" esistente? "+actor+" non ha prodotto l'output atteso (il file non doveva essere generato)");
		}
	}
	
}
