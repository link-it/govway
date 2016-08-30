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



import java.io.File;
import java.util.List;

import org.openspcoop2.core.registry.wsdl.ConverterStandardWSDL2SplitWSDL;
import org.openspcoop2.core.registry.wsdl.SchemaXSDAccordoServizio;
import org.openspcoop2.core.registry.wsdl.SplitWSDL;
import org.openspcoop2.core.registry.wsdl.StandardWSDLOutputMode;
import org.openspcoop2.core.registry.wsdl.TipoSchemaXSDAccordoServizio;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.SchemaXSD;




/**
*
import org.openspcoop2.utils.xml.SchemaXSD;
*
* @author Lorenzo Nardi <nardi@link.it>
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class Testsuite {

	static boolean debug = true;
	static String output = "OUTPUT";
	static File outputDir = null;
	static String schemaLocation = "allegati"+File.separatorChar;
	


	public static void main(String[] argv) throws Exception{

		// creo directory di OUTPUT
		Testsuite.outputDir = new File(Testsuite.output);
		if(Testsuite.outputDir.exists()==false){
			if(Testsuite.outputDir.mkdir()==false){
				System.out.println("Creazione directory fallita: "+Testsuite.outputDir.getAbsolutePath());
				return;
			}
		}
		else{
			System.out.println("Directory ["+Testsuite.outputDir.getAbsolutePath()+"] gia esistente.");
			return;
		}
		

		

		// STANDARD 2 SPCOOP

		System.out.println("----------------------------------------");
		System.out.println("Test.1 : ");
		System.out.println("  input:  WSDL completo (String)");
		System.out.println("  output: WSDL diviso in componenti SPCoop ");
		System.out.println("");
		System.out.println("	- wsdl/TestSuite.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:Types");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example/types\"");
		System.out.println("				- xsd:include(allegati/allegatoIncludeFromDefinitorio.xsd)");
		System.out.println("				- xsd:include(specificaSemiformale/specificaSemiformaleIncludeFromDefinitorio.xsd)");
		System.out.println("				- xsd:import(allegati/allegatoImportFromDefinitorio.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/import\"");
		System.out.println("					- xsd:import(allegati/allegatoImportFromAllegato.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/import/allegato/interno\"");
		System.out.println("				- xsd:import(allegati/allegatoImportFromDefinitorio2.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/import2\"");
		System.out.println("				- xsd:import(specificaSemiformale/specificaSemiformaleImportFromDefinitorio.xsd) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/import\"");
		System.out.println("					- xsd:import(specificaSemiformale/specificaSemiformaleImportFromSpecificaSemiformale.xsd) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/import/specificasemiformale/interno\"");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- contenuto elementi che prendono lo stesso namespace del wsdl");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- xsd:import(allegati/allegatoImportFromWSDL.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/importwsdl\"");
		System.out.println("		- wsdl:import(specificaSemiformale/specificaSemiformaleImportFromWSDL.xsd) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/importwsdl\"");
		System.out.println("		- wsdl:messages");
		System.out.println("		- wsdl:port-types");
		System.out.println("		- wsdl:bindings");
		System.out.println("		- wsdl:services");
		System.out.println("----------------------------------------");
		System.out.println(" WriteTo automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test1(true,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo manuale");
		System.out.println("----------------------------------------");
		Testsuite.test1(false,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test1(true,true);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) manuale");
		System.out.println("----------------------------------------");
		Testsuite.test1(false,true);
		System.out.println("----------------------------------------\n\n\n");
		
		
		System.out.println("----------------------------------------");
		System.out.println("Test.2 : ");
		System.out.println("  input:  WSDL completo (byte[])");
		System.out.println("  output: WSDL diviso in componenti SPCoop ");
		System.out.println("");
		System.out.println("  wsdl (messages,portTypes,bindings e services) contenente schemi xsd interni, xsd:include, xsd:import e wsdl:import");
		System.out.println("  Struttura: ");
		System.out.println("	- wsdl/TestSuite.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:Types");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- xsd:import(allegati/types.xsd)  namespace=\"http://www.openspcoop2.org/example/types\"");
		System.out.println("					- xsd:include(allegati/allegatoIncludeFromDefinitorio.xsd)");
		System.out.println("					- xsd:include(specificaSemiformale/specificaSemiformaleIncludeFromDefinitorio.xsd)");
		System.out.println("					- xsd:import(allegati/allegatoImportFromDefinitorio.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/import\"");
		System.out.println("						- xsd:import(allegati/allegatoImportFromAllegato.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/import/allegato/interno\"");
		System.out.println("					- xsd:import(allegati/allegatoImportFromDefinitorio2.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/import2\"");
		System.out.println("					- xsd:import(specificaSemiformale/specificaSemiformaleImportFromDefinitorio.xsd) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/import\"");
		System.out.println("						- xsd:import(specificaSemiformale/specificaSemiformaleImportFromSpecificaSemiformale.xsd) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/import/specificasemiformale/interno\"");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- xsd:include(definitorio/InterfacciaDefinitoria.xsd)");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- xsd:import(allegati/allegatoImportFromWSDL.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/importwsdl\"");
		System.out.println("		- wsdl:import(specificaSemiformale/specificaSemiformaleImportFromWSDL.xsd) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/importwsdl\"");
		System.out.println("		- wsdl:messages");
		System.out.println("		- wsdl:port-types");
		System.out.println("		- wsdl:bindings");
		System.out.println("		- wsdl:services");
		System.out.println("----------------------------------------");
		System.out.println(" WriteTo automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test2(true,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo manuale");
		System.out.println("----------------------------------------");
		Testsuite.test2(false,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test2(true,true);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) manuale");
		System.out.println("----------------------------------------");
		Testsuite.test2(false,true);
		System.out.println("----------------------------------------\n\n\n");
		
		
		
		// SPCOOP 2 STANDARD
		
		System.out.println("----------------------------------------");
		System.out.println("Test.3 : ");
		System.out.println("  input:  WSDL diviso in componenti SPCoop (String)");
		System.out.println("  output: WSDL completo");
		System.out.println("");
		System.out.println("  wsdl (messages,portTypes,bindings e services) contenente schemi xsd interni, xsd:include, xsd:import e wsdl:import");
		System.out.println("  Struttura: ");
		System.out.println("	- specificaInterfaccia/InterfacciaLogicaErogatore.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:Types");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example/types\"");
		System.out.println("				- xsd:include(allegati/InterfacciaDefinitoria.xsd)");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- xsd:import(allegati/InterfacciaDefinitoria_1.xsd) namespace=\"http://www.openspcoop2.org/example3\"");
		System.out.println("		- wsdl:import(specificaInterfaccia/import.wsdl) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/importwsdl\"");
		System.out.println("		- wsdl:messages");
		System.out.println("		- wsdl:port-types");
		System.out.println("	- specificaInterfaccia/InterfacciaLogicaFruitore.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:Types");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example/types\"");
		System.out.println("				- xsd:include(allegati/InterfacciaDefinitoria.xsd)");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- xsd:import(allegati/InterfacciaDefinitoria0.xsd) namespace=\"http://www.openspcoop2.org/example2\"");
		System.out.println("		- wsdl:messages");
		System.out.println("		- wsdl:port-types");
		System.out.println("	- specificaPortiAccesso/PortiAccessoErogatore.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:import(specificaInterfaccia/InterfacciaLogicaErogatore.wsdl) namespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("		- wsdl:bindings");
		System.out.println("		- wsdl:services");
		System.out.println("	- specificaPortiAccesso/PortiAccessoFruitore.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:import(specificaInterfaccia/InterfacciaLogicaFruitore.wsdl) namespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("		- wsdl:bindings");
		System.out.println("		- wsdl:services");
		System.out.println("----------------------------------------");
		System.out.println(" WriteTo automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test3(true,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo manuale");
		System.out.println("----------------------------------------");
		Testsuite.test3(false,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test3(true,true);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) manuale");
		System.out.println("----------------------------------------");
		Testsuite.test3(false,true);
		System.out.println("----------------------------------------\n\n\n");
		
		
		System.out.println("----------------------------------------");
		System.out.println("Test.4 : ");
		System.out.println("  input:  WSDL diviso in componenti SPCoop (byte[])");
		System.out.println("  output: WSDL completo");
		System.out.println("");
		System.out.println("  wsdl (messages,portTypes,bindings e services) contenente schemi xsd interni, xsd:include, xsd:import e wsdl:import");
		System.out.println("  Struttura: ");
		System.out.println("	- specificaInterfaccia/InterfacciaLogicaErogatore.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:Types");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example/types\"");
		System.out.println("				- xsd:include(allegati/InterfacciaDefinitoria.xsd)");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- xsd:import(allegati/InterfacciaDefinitoria_1.xsd) namespace=\"http://www.openspcoop2.org/example3\"");
		System.out.println("		- wsdl:import(specificaInterfaccia/import.wsdl) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/importwsdl\"");
		System.out.println("		- wsdl:messages");
		System.out.println("		- wsdl:port-types");
		System.out.println("	- specificaInterfaccia/InterfacciaLogicaFruitore.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:Types");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example/types\"");
		System.out.println("				- xsd:include(allegati/InterfacciaDefinitoria.xsd)");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- xsd:import(allegati/InterfacciaDefinitoria0.xsd) namespace=\"http://www.openspcoop2.org/example2\"");
		System.out.println("		- wsdl:messages");
		System.out.println("		- wsdl:port-types");
		System.out.println("	- specificaPortiAccesso/PortiAccessoErogatore.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:import(specificaInterfaccia/InterfacciaLogicaErogatore.wsdl) namespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("		- wsdl:bindings");
		System.out.println("		- wsdl:services");
		System.out.println("	- specificaPortiAccesso/PortiAccessoFruitore.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:import(specificaInterfaccia/InterfacciaLogicaFruitore.wsdl) namespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("		- wsdl:bindings");
		System.out.println("		- wsdl:services");
		System.out.println("----------------------------------------");
		System.out.println(" WriteTo automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test4(true,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo manuale");
		System.out.println("----------------------------------------");
		Testsuite.test4(false,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test4(true,true);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) manuale");
		System.out.println("----------------------------------------");
		Testsuite.test4(false,true);
		System.out.println("----------------------------------------\n\n\n");
		
		

		// STANDARD 2 SPCOOP: casi limite
		System.out.println("----------------------------------------");
		System.out.println("Test.5 : ");
		System.out.println("  input:  WSDL completo che include schemi con stesso nome types.xsd (String)");
		System.out.println("  output: WSDL diviso in componenti SPCoop ");
		System.out.println("");
		System.out.println("	- wsdl/TestSuite.wsdl (targetNamespace=\"http://www.openspcoop2.org/example\")");
		System.out.println("		- wsdl:Types");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- xsd:include(allegati/allegatoIncludeFromDefinitorio/types.xsd)");
		System.out.println("				- xsd:include(specificaSemiformale/specificaSemiformaleIncludeFromDefinitorio/types.xsd)");
		System.out.println("				- xsd:import(allegati/allegatoImportFromDefinitorio/types.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/import\"");
		System.out.println("					- xsd:import(allegati/allegatoImportFromAllegato/types.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/import/allegato/interno\"");
		System.out.println("				- xsd:import(allegati/allegatoImportFromDefinitorio2/types.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/import2\"");
		System.out.println("				- xsd:import(specificaSemiformale/specificaSemiformaleImportFromDefinitorio/types.xsd) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/import\"");
		System.out.println("					- xsd:import(specificaSemiformale/specificaSemiformaleImportFromSpecificaSemiformale/types.xsd) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/import/specificasemiformale/interno\"");
		System.out.println("				- contenuto elementi che prendono lo stesso namespace del wsdl");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- contenuto elementi che prendono lo stesso namespace del wsdl");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example\"");
		System.out.println("				- xsd:import(allegati/allegatoImportFromWSDL/types.xsd) namespace=\"http://www.openspcoop2.org/example/allegato/importwsdl\"");
		System.out.println("		- wsdl:import(specificaSemiformale/specificaSemiformaleImportFromWSDL/types.xsd) namespace=\"http://www.openspcoop2.org/example/specificasemiformale/importwsdl\"");
		System.out.println("		- wsdl:messages");
		System.out.println("		- wsdl:port-types");
		System.out.println("		- wsdl:bindings");
		System.out.println("		- wsdl:services");
		System.out.println("----------------------------------------");
		System.out.println(" WriteTo automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test5(true,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo manuale");
		System.out.println("----------------------------------------");
		Testsuite.test5(false,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test5(true,true);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) manuale");
		System.out.println("----------------------------------------");
		Testsuite.test5(false,true);
		System.out.println("----------------------------------------\n\n\n");
		
		
			
		System.out.println("----------------------------------------");
		System.out.println("Test.6 : ");
		System.out.println("  input:  WSDL completo che include schemi con diversi namespace + wrapped (String)");
		System.out.println("  output: WSDL diviso in componenti SPCoop ");
		System.out.println("");
		System.out.println("	- wsdl/TestSuite.wsdl (targetNamespace=\"http://www.openspcoop2.org/example/impl\")");
		System.out.println("		- wsdl:Types");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example/types\"");
		System.out.println("				- xsd:include(InterfacciaDefinitoria.xsd)");
		System.out.println("				- contenuto elementi");
		System.out.println("			- xsd:schema targetNamespace=\"http://www.openspcoop2.org/example/wrapper\"");
		System.out.println("				- contenuto elementi che rendono il wsdl wrapped document literall");
		System.out.println("		- wsdl:messages");
		System.out.println("		- wsdl:port-types");
		System.out.println("		- wsdl:bindings");
		System.out.println("		- wsdl:services");
		System.out.println("----------------------------------------");
		System.out.println(" WriteTo automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test6(true,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo manuale");
		System.out.println("----------------------------------------");
		Testsuite.test6(false,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test6(true,true);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) manuale");
		System.out.println("----------------------------------------");
		Testsuite.test6(false,true);
		System.out.println("----------------------------------------\n\n\n");
		
		
		
		
		
		
		
		System.out.println("----------------------------------------");
		System.out.println("Test.7 : ");
		System.out.println("  input:  WSDL prelevato da URL: http://test.spcoop.it/openspcoop/IntegrationManager?wsdl");
		System.out.println("  output: WSDL diviso in componenti SPCoop ");
		System.out.println("");
		System.out.println("----------------------------------------");
		System.out.println(" WriteTo automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test7(true,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo manuale");
		System.out.println("----------------------------------------");
		Testsuite.test7(false,false);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) automatico effettuato dalla libreria");
		System.out.println("----------------------------------------");
		Testsuite.test7(true,true);
		System.out.println("\n\n----------------------------------------");
		System.out.println(" WriteTo (prettyPrint) manuale");
		System.out.println("----------------------------------------");
		Testsuite.test7(false,true);
		System.out.println("----------------------------------------\n\n\n");
		
	}







	/* -------------------- Test 1. WSDL --------------------------------

	/**
	 * Test da Standard a SPCoop partendo da un WSDL unico. Tutti i documenti importati sono univoci
	 * @throws Exception 
	 */	

	private static void test1(boolean libOutputDir,boolean prettyPrint) throws Exception{

		File outputDirTest = null;
		if(libOutputDir){
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test1_split_inputString"+File.separatorChar+"writeToAutomaticoPrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test1_split_inputString"+File.separatorChar+"writeToAutomatico");
			}
		}else{
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test1_split_inputString"+File.separatorChar+"writeToManualePrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test1_split_inputString"+File.separatorChar+"writeToManuale");
			}
		}
		FileSystemUtilities.mkdirParentDirectory(outputDirTest.getAbsolutePath());
		if(outputDirTest.mkdir()==false){
			throw new Exception("Creazione directory fallita: "+outputDirTest.getAbsolutePath());
		}

		
		// TEST A.
		
		System.out.println("Test a. \n\tWSDL2SPCoopUtility.split(String wsdl)");
		File testA_outputDir = new File(outputDirTest,"A");
		ConverterStandardWSDL2SplitWSDL testA_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA_util = new ConverterStandardWSDL2SplitWSDL(testA_outputDir,prettyPrint);
			else
				testA_util = new ConverterStandardWSDL2SplitWSDL(testA_outputDir);
		}else{
			testA_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testA_util.split(Costanti.TEST1_WSDL_FILE_PATH);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testA_outputDir, testA_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testA_outputDir, true, false, true, true, Costanti.TEST1_SCHEMI, 3);
		Validatore.validaOutputErogatore(testA_outputDir.getAbsolutePath(), Costanti.TEST1_PORT_TYPES, Costanti.TEST1_OPERATIONS, 
				Costanti.TEST1_FILE_PATH_LOGICO,Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test a. OK\n");
		
		
		
		// TEST B.
		// Siccome utilizzo lo stesso wsdl sia per l'erogatore che per il fruitore avro' le stesse classi di output.
		// NOTA: gli allegati (a meno di quelli inseriti direttamente in wsdl:types) non saranno ripetuti 2 volte!!

		System.out.println("Test b. \n\tWSDL2SPCoopUtility.split(String wsdlErogatore,String wsdlFruitore)");
		File testB_outputDir = new File(outputDirTest,"B");
		ConverterStandardWSDL2SplitWSDL testB_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testB_util = new ConverterStandardWSDL2SplitWSDL(testB_outputDir,prettyPrint);
			else
				testB_util = new ConverterStandardWSDL2SplitWSDL(testB_outputDir);
		}else{
			testB_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testB_util.split(Costanti.TEST1_WSDL_FILE_PATH,Costanti.TEST1_WSDL_FILE_PATH);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testB_outputDir, testB_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testB_outputDir, true, true, true, true, Costanti.TEST1_SCHEMI, 6);
		Validatore.validaOutputErogatore(testB_outputDir.getAbsolutePath(), Costanti.TEST1_PORT_TYPES, Costanti.TEST1_OPERATIONS, 
				Costanti.TEST1_FILE_PATH_LOGICO,Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO);
		Validatore.validaOutputFruitore(testB_outputDir.getAbsolutePath(), Costanti.TEST1_PORT_TYPES, Costanti.TEST1_OPERATIONS, 
				Costanti.TEST1_FILE_PATH_LOGICO,Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test b. OK\n");
		
		
		
		// TEST C.
		// Gli allegati pero' vengono salvati come specifiche semiformali
		System.out.println("Test c. \n\tWSDL2SPCoopUtility.split(String wsdlErogatore,SPECIFICA_SEMIFORMALE)");
		File testC_outputDir = new File(outputDirTest,"C");
		ConverterStandardWSDL2SplitWSDL testC_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testC_util = new ConverterStandardWSDL2SplitWSDL(testC_outputDir,prettyPrint);
			else
				testC_util = new ConverterStandardWSDL2SplitWSDL(testC_outputDir);
		}else{
			testC_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testC_util.split(Costanti.TEST1_WSDL_FILE_PATH,TipoSchemaXSDAccordoServizio.SPECIFICA_SEMIFORMALE);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testC_outputDir, testC_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testC_outputDir, true, false, true, false, Costanti.TEST1_SCHEMI, 3);
		Validatore.validaOutputErogatore(testC_outputDir.getAbsolutePath(), Costanti.TEST1_PORT_TYPES, Costanti.TEST1_OPERATIONS, 
				Costanti.TEST1_FILE_PATH_LOGICO,Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test c. OK\n");
		
		
		
		// TEST D.
		// Siccome utilizzo lo stesso wsdl sia per l'erogatore che per il fruitore avro' le stesse classi di output.
		// NOTA: gli allegati (a meno di quelli inseriti direttamente in wsdl:types) non saranno ripetuti 2 volte!!
		// Gli allegati pero' vengono salvati come specifiche semiformali
		
		System.out.println("Test d. \n\tWSDL2SPCoopUtility.split(String wsdlErogatore,String wsdlFruitore,SPECIFICA_SEMIFORMALE)");
		File testD_outputDir = new File(outputDirTest,"D");
		ConverterStandardWSDL2SplitWSDL testD_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testD_util = new ConverterStandardWSDL2SplitWSDL(testD_outputDir,prettyPrint);
			else
				testD_util = new ConverterStandardWSDL2SplitWSDL(testD_outputDir);
		}else{
			testD_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testD_util.split(Costanti.TEST1_WSDL_FILE_PATH,Costanti.TEST1_WSDL_FILE_PATH, TipoSchemaXSDAccordoServizio.SPECIFICA_SEMIFORMALE);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testD_outputDir, testD_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testD_outputDir, true, true, true, false, Costanti.TEST1_SCHEMI, 6);
		Validatore.validaOutputErogatore(testD_outputDir.getAbsolutePath(), Costanti.TEST1_PORT_TYPES, Costanti.TEST1_OPERATIONS, 
				Costanti.TEST1_FILE_PATH_LOGICO,Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO);
		Validatore.validaOutputFruitore(testD_outputDir.getAbsolutePath(), Costanti.TEST1_PORT_TYPES, Costanti.TEST1_OPERATIONS, 
				Costanti.TEST1_FILE_PATH_LOGICO,Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test d. OK\n");
		
		
		
		// TEST E.
		System.out.println("Test e. \n\tWSDL2SPCoopUtility.split(String wsdl, porttypesErogatore, operationPorttypesErogatore)");
		File testE_outputDir = new File(outputDirTest,"E");
		ConverterStandardWSDL2SplitWSDL testE_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testE_util = new ConverterStandardWSDL2SplitWSDL(testE_outputDir,prettyPrint);
			else
				testE_util = new ConverterStandardWSDL2SplitWSDL(testE_outputDir);
		}else{
			testE_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testE_util.split(Costanti.TEST1_WSDL_FILE_PATH, Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO,Costanti.TEST1_OPERATIONS_ASINCRONO_ASIMMETRICO);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testE_outputDir, testE_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testE_outputDir, true, false, true, true, Costanti.TEST1_SCHEMI, 3);
		Validatore.validaOutputErogatore(testE_outputDir.getAbsolutePath(), Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO,Costanti.TEST1_OPERATIONS_ASINCRONO_ASIMMETRICO, 
				Costanti.TEST1_FILE_PATH_LOGICO_ASINCRONO_ASIMMETRICO,
				Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO);
		System.out.println("Test e. OK\n");
		
		
		
		
		
		// TEST F.
		System.out.println("Test f. \n\tWSDL2SPCoopUtility.split(String wsdl, porttypesErogatore,operationPorttypesErogatore,porttypesFruitore, operationPorttypesFruitore)");
		File testF_outputDir = new File(outputDirTest,"F");
		ConverterStandardWSDL2SplitWSDL testF_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testF_util = new ConverterStandardWSDL2SplitWSDL(testF_outputDir,prettyPrint);
			else
				testF_util = new ConverterStandardWSDL2SplitWSDL(testF_outputDir);
		}else{
			testF_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit...");
		testF_util.split(Costanti.TEST1_WSDL_FILE_PATH, Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST1_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA, 
				Costanti.TEST1_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testF_outputDir, testF_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testF_outputDir, true, true, true, true, Costanti.TEST1_SCHEMI, 3);
		Validatore.validaOutput(testF_outputDir.getAbsolutePath(), Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA, Costanti.TEST1_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST1_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA, Costanti.TEST1_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA, 
				Costanti.TEST1_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RISPOSTA, 
				Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA);
		System.out.println("Test f. OK\n");
		
		
	}
	
	
	private static void test2(boolean libOutputDir,boolean prettyPrint)throws Exception{
		
		
		File outputDirTest = null;
		if(libOutputDir){
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test2_split_inputByte"+File.separatorChar+"writeToAutomaticoPrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test2_split_inputByte"+File.separatorChar+"writeToAutomatico");
			}
		}else{
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test2_split_inputByte"+File.separatorChar+"writeToManualePrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test2_split_inputByte"+File.separatorChar+"writeToManuale");
			}
		}
		FileSystemUtilities.mkdirParentDirectory(outputDirTest.getAbsolutePath());
		if(outputDirTest.mkdir()==false){
			throw new Exception("Creazione directory fallita: "+outputDirTest.getAbsolutePath());
		}

		

		
		// TEST A.
		
		System.out.println("Test a. \n\tWSDL2SPCoopUtility.split(byte[] wsdl)");
		File testA_outputDir = new File(outputDirTest,"A");
		ConverterStandardWSDL2SplitWSDL testA_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA_util = new ConverterStandardWSDL2SplitWSDL(testA_outputDir,prettyPrint);
			else
				testA_util = new ConverterStandardWSDL2SplitWSDL(testA_outputDir);
		}else{
			testA_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		byte[] testA_wsdlBytesErogatore = Utilities.generaWsdlBytes(Costanti.TEST2_WSDL_FILE, Costanti.TEST2_SCHEMI_DEFINITORIO[0],
				Costanti.TEST2_SCHEMI_ALLEGATI, Costanti.TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI);
		testA_util.split(testA_wsdlBytesErogatore);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testA_outputDir, testA_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testA_outputDir, true, false, true, true, Costanti.TEST2_SCHEMI_DEFINITORIO, 10);
		Validatore.validaOutputErogatore(testA_outputDir.getAbsolutePath(), Costanti.TEST2_PORT_TYPES, Costanti.TEST2_OPERATIONS, 
				Costanti.TEST2_FILE_PATH_LOGICO,Costanti.TEST2_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test a. OK\n");



		
		// TEST B.
		// Siccome utilizzo lo stesso wsdl sia per l'erogatore che per il fruitore avro' le stesse classi di output.
		// NOTA: gli allegati (a meno di quelli inseriti direttamente in wsdl:types) non saranno ripetuti 2 volte!!

		System.out.println("Test b. \n\tWSDL2SPCoopUtility.split(byte[] wsdlErogatore,byte[] wsdlFruitore)");
		File testB_outputDir = new File(outputDirTest,"B");
		ConverterStandardWSDL2SplitWSDL testB_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testB_util = new ConverterStandardWSDL2SplitWSDL(testB_outputDir,prettyPrint);
			else
				testB_util = new ConverterStandardWSDL2SplitWSDL(testB_outputDir);
		}else{
			testB_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		byte[] testB_wsdlBytes = Utilities.generaWsdlBytes(Costanti.TEST2_WSDL_FILE, Costanti.TEST2_SCHEMI_DEFINITORIO[0],
				Costanti.TEST2_SCHEMI_ALLEGATI, Costanti.TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI);
		testB_util.split(testB_wsdlBytes,testB_wsdlBytes);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testB_outputDir, testB_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testB_outputDir, true, true, true, true, Costanti.TEST2_SCHEMI_DEFINITORIO, 21);
		Validatore.validaOutputErogatore(testB_outputDir.getAbsolutePath(), Costanti.TEST2_PORT_TYPES, Costanti.TEST2_OPERATIONS, 
				Costanti.TEST2_FILE_PATH_LOGICO,Costanti.TEST2_FILE_PATH_IMPLEMENTATIVO);
		Validatore.validaOutputFruitore(testB_outputDir.getAbsolutePath(), Costanti.TEST2_PORT_TYPES, Costanti.TEST2_OPERATIONS, 
				Costanti.TEST2_FILE_PATH_LOGICO,Costanti.TEST2_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test b. OK\n");

		
		
		
		// TEST C.
		
		System.out.println("Test c. \n\tWSDL2SPCoopUtility.split(byte[] wsdlErogatore,SPECIFICA_SEMIFORMALE)");
		File testC_outputDir = new File(outputDirTest,"C");
		ConverterStandardWSDL2SplitWSDL testC_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testC_util = new ConverterStandardWSDL2SplitWSDL(testC_outputDir,prettyPrint);
			else
				testC_util = new ConverterStandardWSDL2SplitWSDL(testC_outputDir);
		}else{
			testC_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		byte[] testC_wsdlBytesErogatore = Utilities.generaWsdlBytes(Costanti.TEST2_WSDL_FILE, Costanti.TEST2_SCHEMI_DEFINITORIO[0],
				Costanti.TEST2_SCHEMI_ALLEGATI, Costanti.TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI);
		testC_util.split(testC_wsdlBytesErogatore,TipoSchemaXSDAccordoServizio.SPECIFICA_SEMIFORMALE);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testC_outputDir, testC_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testC_outputDir, true, false, true, false, Costanti.TEST2_SCHEMI_DEFINITORIO, 10);
		Validatore.validaOutputErogatore(testC_outputDir.getAbsolutePath(), Costanti.TEST2_PORT_TYPES, Costanti.TEST2_OPERATIONS, 
				Costanti.TEST2_FILE_PATH_LOGICO,Costanti.TEST2_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test c. OK\n");
		
		
		
		
		// TEST D.
		// Siccome utilizzo lo stesso wsdl sia per l'erogatore che per il fruitore avro' le stesse classi di output.
		// NOTA: gli allegati (a meno di quelli inseriti direttamente in wsdl:types) non saranno ripetuti 2 volte!!

		System.out.println("Test d. \n\tWSDL2SPCoopUtility.split(byte[] wsdlErogatore,byte[] wsdlFruitore,SPECIFICA_SEMIFORMALE)");
		File testD_outputDir = new File(outputDirTest,"D");
		ConverterStandardWSDL2SplitWSDL testD_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testD_util = new ConverterStandardWSDL2SplitWSDL(testD_outputDir,prettyPrint);
			else
				testD_util = new ConverterStandardWSDL2SplitWSDL(testD_outputDir);
		}else{
			testD_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		byte[] testD_wsdlBytes = Utilities.generaWsdlBytes(Costanti.TEST2_WSDL_FILE, Costanti.TEST2_SCHEMI_DEFINITORIO[0],
				Costanti.TEST2_SCHEMI_ALLEGATI, Costanti.TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI);
		testD_util.split(testD_wsdlBytes,testD_wsdlBytes,TipoSchemaXSDAccordoServizio.SPECIFICA_SEMIFORMALE);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testD_outputDir, testD_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testD_outputDir, true, true, true, false, Costanti.TEST2_SCHEMI_DEFINITORIO, 21);
		Validatore.validaOutputErogatore(testD_outputDir.getAbsolutePath(), Costanti.TEST2_PORT_TYPES, Costanti.TEST2_OPERATIONS, 
				Costanti.TEST2_FILE_PATH_LOGICO,Costanti.TEST2_FILE_PATH_IMPLEMENTATIVO);
		Validatore.validaOutputFruitore(testD_outputDir.getAbsolutePath(), Costanti.TEST2_PORT_TYPES, Costanti.TEST2_OPERATIONS, 
				Costanti.TEST2_FILE_PATH_LOGICO,Costanti.TEST2_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test d. OK\n");
		
		
		
		
		// TEST E.
		System.out.println("Test e. \n\tWSDL2SPCoopUtility.split(byte[] wsdl, porttypesErogatore, operationPorttypesErogatore)");
		File testE_outputDir = new File(outputDirTest,"E");
		ConverterStandardWSDL2SplitWSDL testE_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testE_util = new ConverterStandardWSDL2SplitWSDL(testE_outputDir,prettyPrint);
			else
				testE_util = new ConverterStandardWSDL2SplitWSDL(testE_outputDir);
		}else{
			testE_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		byte[] testE_wsdlBytes = Utilities.generaWsdlBytes(Costanti.TEST2_WSDL_FILE, Costanti.TEST2_SCHEMI_DEFINITORIO[0],
				Costanti.TEST2_SCHEMI_ALLEGATI, Costanti.TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI);
		testE_util.split(testE_wsdlBytes, Costanti.TEST2_PORT_TYPE_ASINCRONO_ASIMMETRICO,Costanti.TEST2_OPERATIONS_ASINCRONO_ASIMMETRICO);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testE_outputDir, testE_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testE_outputDir, true, false, true, true, Costanti.TEST2_SCHEMI_DEFINITORIO, 10);
		Validatore.validaOutputErogatore(testE_outputDir.getAbsolutePath(), Costanti.TEST2_PORT_TYPE_ASINCRONO_ASIMMETRICO,Costanti.TEST2_OPERATIONS_ASINCRONO_ASIMMETRICO, 
				Costanti.TEST2_FILE_PATH_LOGICO_ASINCRONO_ASIMMETRICO,
				Costanti.TEST2_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO);
		System.out.println("Test e. OK\n");
		
		
		
		
		// TEST F.
		System.out.println("Test f. \n\tWSDL2SPCoopUtility.split(byte[] wsdl, porttypesErogatore,operationPorttypesErogatore,porttypesFruitore, operationPorttypesFruitore)");
		File testF_outputDir = new File(outputDirTest,"F");
		ConverterStandardWSDL2SplitWSDL testF_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testF_util = new ConverterStandardWSDL2SplitWSDL(testF_outputDir,prettyPrint);
			else
				testF_util = new ConverterStandardWSDL2SplitWSDL(testF_outputDir);
		}else{
			testF_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		byte[] testF_wsdlBytes = Utilities.generaWsdlBytes(Costanti.TEST2_WSDL_FILE, Costanti.TEST2_SCHEMI_DEFINITORIO[0],
				Costanti.TEST2_SCHEMI_ALLEGATI, Costanti.TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI);
		testF_util.split(testF_wsdlBytes, Costanti.TEST2_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST2_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST2_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA, 
				Costanti.TEST2_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testF_outputDir, testF_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testF_outputDir, true, true, true, true, Costanti.TEST2_SCHEMI_DEFINITORIO, 10);
		Validatore.validaOutput(testF_outputDir.getAbsolutePath(), Costanti.TEST2_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA, Costanti.TEST2_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST2_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST2_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST2_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA, Costanti.TEST2_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA, 
				Costanti.TEST2_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RISPOSTA, 
				Costanti.TEST2_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA);
		System.out.println("Test f. OK\n");
		
	}
	
	
	
	
	
	
	
	
	
	
	private static void test3(boolean libOutputDir,boolean prettyPrint)throws Exception{
		
		File outputDirTest = null;
		if(libOutputDir){
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test3_WSDLStandard_string"+File.separatorChar+"writeToAutomaticoPrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test3_WSDLStandard_string"+File.separatorChar+"writeToAutomatico");
			}
		}else{
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test3_WSDLStandard_string"+File.separatorChar+"writeToManualePrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test3_WSDLStandard_string"+File.separatorChar+"writeToManuale");
			}
		}
		FileSystemUtilities.mkdirParentDirectory(outputDirTest.getAbsolutePath());
		if(outputDirTest.mkdir()==false){
			throw new Exception("Creazione directory fallita: "+outputDirTest.getAbsolutePath());
		}
		
		XMLUtils xmlUtils = XMLUtils.getInstance();
		WSDLUtilities wsdlUtilities = WSDLUtilities.getInstance(xmlUtils);
		
		
		
		
		
		
		
		
		
		// TEST A-0.
		System.out.println("Test a-0. \n\tWSDL2SPCoopUtility.split(String wsdl,MANTIENI_IMPORT_INCLUDE_ORIGINALI)");
		File testA0_outputDir = new File(outputDirTest,"A0");
		ConverterStandardWSDL2SplitWSDL testA0_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA0_util = new ConverterStandardWSDL2SplitWSDL(testA0_outputDir,prettyPrint);
			else
				testA0_util = new ConverterStandardWSDL2SplitWSDL(testA0_outputDir);
		}else{
			testA0_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testA0_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI);
		System.out.println("\tsplit ok");
		File outputWsdltestA0 = new File(testA0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File importWsdltestA0 = new File(testA0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestA0);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestA0);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testA0_util.getWsdlErogatore(), outputWsdltestA0, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestA0 = testA0_util.getSchemiErogatore();
			if(schemaErogatoritestA0.size()>0){
				for(int i=0;i<schemaErogatoritestA0.size();i++){
					SchemaXSD schema = schemaErogatoritestA0.get(i);
					schema.writeTo(testA0_outputDir,prettyPrint);
				}
			}
		}
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestA0,xmlUtils), Costanti.TEST3_PORT_TYPES_EROGATORE, Costanti.TEST3_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestA0.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestA0.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		System.out.println("Test a-0. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		// TEST A-1.
		System.out.println("Test a-1. \n\tWSDL2SPCoopUtility.split(String wsdl,INGLOBA_SCHEMI_IN_WSDL)");
		File testA1_outputDir = new File(outputDirTest,"A1");
		ConverterStandardWSDL2SplitWSDL testA1_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA1_util = new ConverterStandardWSDL2SplitWSDL(testA1_outputDir,prettyPrint);
			else
				testA1_util = new ConverterStandardWSDL2SplitWSDL(testA1_outputDir);
		}else{
			testA1_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testA1_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL);
		System.out.println("\tsplit ok");
		File outputWsdlTestA1 = new File(testA1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File importWsdltestA1 = new File(testA1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestA1);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestA1);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testA1_util.getWsdlErogatore(), outputWsdlTestA1, prettyPrint);
		}
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdlTestA1,xmlUtils), Costanti.TEST3_PORT_TYPES_EROGATORE, Costanti.TEST3_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdlTestA1.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdlTestA1.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		System.out.println("Test a-1. OK\n");
		
		
		
		
		
		
		
		
		
		// TEST A-2.
		System.out.println("Test a-2. \n\tWSDL2SPCoopUtility.split(String wsdl,INGLOBA_SOLO_SCHEMI_IMPORTATI_IN_WSDL)");
		File testA2_outputDir = new File(outputDirTest,"A2");
		ConverterStandardWSDL2SplitWSDL testA2_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA2_util = new ConverterStandardWSDL2SplitWSDL(testA2_outputDir,prettyPrint);
			else
				testA2_util = new ConverterStandardWSDL2SplitWSDL(testA2_outputDir);
		}else{
			testA2_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testA2_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_IMPORTATI_IN_WSDL);
		System.out.println("\tsplit ok");
		File outputWsdltestA2 = new File(testA2_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File importWsdltestA2 = new File(testA2_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestA2);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestA2);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testA2_util.getWsdlErogatore(), outputWsdltestA2, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoriTestA2 = testA2_util.getSchemiErogatore();
			if(schemaErogatoriTestA2.size()>0){
				for(int i=0;i<schemaErogatoriTestA2.size();i++){
					SchemaXSD schema = schemaErogatoriTestA2.get(i);
					schema.writeTo(testA2_outputDir, prettyPrint);
				}
			}
		}
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestA2,xmlUtils), Costanti.TEST3_PORT_TYPES_EROGATORE, Costanti.TEST3_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestA2.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestA2.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		System.out.println("Test a-2. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST A-3.
		System.out.println("Test a-3. \n\tWSDL2SPCoopUtility.split(String wsdl,INGLOBA_SOLO_SCHEMI_INCLUSI_IN_WSDL)");
		File testA3_outputDir = new File(outputDirTest,"A3");
		ConverterStandardWSDL2SplitWSDL testA3_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA3_util = new ConverterStandardWSDL2SplitWSDL(testA3_outputDir,prettyPrint);
			else
				testA3_util = new ConverterStandardWSDL2SplitWSDL(testA3_outputDir);
		}else{
			testA3_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testA3_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_IN_WSDL);
		System.out.println("\tsplit ok");
		File outputWsdltestA3 = new File(testA3_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File importWsdltestA3 = new File(testA3_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestA3);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestA3);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testA3_util.getWsdlErogatore(), outputWsdltestA3, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestA3 = testA3_util.getSchemiErogatore();
			if(schemaErogatoritestA3.size()>0){
				for(int i=0;i<schemaErogatoritestA3.size();i++){
					SchemaXSD schema = schemaErogatoritestA3.get(i);
					schema.writeTo(testA3_outputDir,prettyPrint);
				}
			}
		}
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestA3,xmlUtils), Costanti.TEST3_PORT_TYPES_EROGATORE, Costanti.TEST3_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestA3.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestA3.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		System.out.println("Test a-3. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST A-4.
		System.out.println("Test a-4. \n\tWSDL2SPCoopUtility.split(String wsdl,INGLOBA_SOLO_SCHEMI_INCLUSI_NOME_AUTOGENERATO_IN_WSDL)");
		File testA4_outputDir = new File(outputDirTest,"A4");
		ConverterStandardWSDL2SplitWSDL testA4_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA4_util = new ConverterStandardWSDL2SplitWSDL(testA4_outputDir,prettyPrint);
			else
				testA4_util = new ConverterStandardWSDL2SplitWSDL(testA4_outputDir);
		}else{
			testA4_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testA4_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_NOME_AUTOGENERATO_IN_WSDL);
		System.out.println("\tsplit ok");
		File outputWsdltestA4 = new File(testA4_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File importWsdltestA4 = new File(testA4_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestA4);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestA4);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testA4_util.getWsdlErogatore(), outputWsdltestA4,prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestA4 = testA4_util.getSchemiErogatore();
			if(schemaErogatoritestA4.size()>0){
				for(int i=0;i<schemaErogatoritestA4.size();i++){
					SchemaXSD schema = schemaErogatoritestA4.get(i);
					schema.writeTo(testA4_outputDir,prettyPrint);
				}
			}
		}
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestA4,xmlUtils), Costanti.TEST3_PORT_TYPES_EROGATORE, Costanti.TEST3_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestA4.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestA4.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		System.out.println("Test a-4. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST B-0.
		System.out.println("Test b-0. \n\tWSDL2SPCoopUtility.split(String wsdlImplementativoErogatore, String wsdlImplementativoFruitore,MANTIENI_IMPORT_INCLUDE_ORIGINALI,false)");
		File testB0_outputDir = new File(outputDirTest,"B0");
		ConverterStandardWSDL2SplitWSDL testB0_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testB0_util = new ConverterStandardWSDL2SplitWSDL(testB0_outputDir,prettyPrint);
			else
				testB0_util = new ConverterStandardWSDL2SplitWSDL(testB0_outputDir);
		}else{
			testB0_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testB0_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE,
				StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI,false);
		System.out.println("\tsplit ok");
		File outputWsdltestB0erogatore = new File(testB0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File outputWsdltestB0fruitore = new File(testB0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Fruitore.wsdl");
		File importWsdltestB0 = new File(testB0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestB0);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestB0);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testB0_util.getWsdlErogatore(), outputWsdltestB0erogatore, prettyPrint);
			wsdlUtilities.writeWsdlTo(testB0_util.getWsdlFruitore(), outputWsdltestB0fruitore, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestB0 = testB0_util.getSchemiErogatore();
			if(schemaErogatoritestB0.size()>0){
				for(int i=0;i<schemaErogatoritestB0.size();i++){
					SchemaXSD schema = schemaErogatoritestB0.get(i);
					schema.writeTo(testB0_outputDir,prettyPrint);
				}
			}
			List<SchemaXSDAccordoServizio> schemaFruitoritestB0 = testB0_util.getSchemiFruitore();
			if(schemaFruitoritestB0.size()>0){
				for(int i=0;i<schemaFruitoritestB0.size();i++){
					SchemaXSD schema = schemaFruitoritestB0.get(i);
					schema.writeTo(testB0_outputDir,prettyPrint);
				}
			}
		}
		// Verifica erogatore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB0erogatore,xmlUtils), Costanti.TEST3_PORT_TYPES_EROGATORE, Costanti.TEST3_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestB0erogatore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestB0erogatore.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);
		// Verifica fruitore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB0fruitore,xmlUtils), Costanti.TEST3_PORT_TYPES_FRUITORE, Costanti.TEST3_OPERATIONS_FRUITORE,true);
		Validatore.validazioneWSDL(outputWsdltestB0fruitore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLFRUITORE", outputWsdltestB0fruitore.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_FRUITORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_FRUITORE,true);	
		System.out.println("Test b-0. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST B-1.
		System.out.println("Test b-1. \n\tWSDL2SPCoopUtility.split(String wsdlImplementativoErogatore,String wsdlImplementativoFruitore,INGLOBA_SCHEMI_IN_WSDL,false)");
		File testB1_outputDir = new File(outputDirTest,"B1");
		ConverterStandardWSDL2SplitWSDL testB1_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testB1_util = new ConverterStandardWSDL2SplitWSDL(testB1_outputDir,prettyPrint);
			else
				testB1_util = new ConverterStandardWSDL2SplitWSDL(testB1_outputDir);
		}else{
			testB1_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testB1_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE,
				StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL,false);
		System.out.println("\tsplit ok");
		File outputWsdltestB1erogatore = new File(testB1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File outputWsdltestB1fruitore = new File(testB1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Fruitore.wsdl");
		File importWsdltestB1 = new File(testB1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestB1);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestB1);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testB1_util.getWsdlErogatore(), outputWsdltestB1erogatore,prettyPrint);
			wsdlUtilities.writeWsdlTo(testB1_util.getWsdlFruitore(), outputWsdltestB1fruitore,prettyPrint);
		}
		// Verifica erogatore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB1erogatore,xmlUtils), Costanti.TEST3_PORT_TYPES_EROGATORE, Costanti.TEST3_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestB1erogatore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestB1erogatore.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		// Verifica fruitore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB1fruitore,xmlUtils), Costanti.TEST3_PORT_TYPES_FRUITORE, Costanti.TEST3_OPERATIONS_FRUITORE,true);
		Validatore.validazioneWSDL(outputWsdltestB1fruitore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLFRUITORE", outputWsdltestB1fruitore.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_FRUITORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_FRUITORE,true);	
		System.out.println("Test b-1. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST B-2.
		System.out.println("Test b-2. \n\tWSDL2SPCoopUtility.split(String wsdlImplementativoErogatore,String wsdlImplementativoFruitore,INGLOBA_SOLO_SCHEMI_IMPORTATI_IN_WSDL,false)");
		File testB2_outputDir = new File(outputDirTest,"B2");
		ConverterStandardWSDL2SplitWSDL testB2_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testB2_util = new ConverterStandardWSDL2SplitWSDL(testB2_outputDir,prettyPrint);
			else
				testB2_util = new ConverterStandardWSDL2SplitWSDL(testB2_outputDir);
		}else{
			testB2_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testB2_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE,
				StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_IMPORTATI_IN_WSDL,false);
		System.out.println("\tsplit ok");
		File outputWsdltestB2erogatore = new File(testB2_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File outputWsdltestB2fruitore = new File(testB2_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Fruitore.wsdl");
		File importWsdltestB2 = new File(testB2_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestB2);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestB2);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testB2_util.getWsdlErogatore(), outputWsdltestB2erogatore, prettyPrint);
			wsdlUtilities.writeWsdlTo(testB2_util.getWsdlFruitore(), outputWsdltestB2fruitore, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestB2 = testB2_util.getSchemiErogatore();
			if(schemaErogatoritestB2.size()>0){
				for(int i=0;i<schemaErogatoritestB2.size();i++){
					SchemaXSD schema = schemaErogatoritestB2.get(i);
					schema.writeTo(testB2_outputDir,prettyPrint);
				}
			}
			List<SchemaXSDAccordoServizio> schemaFruitoritestB2 = testB2_util.getSchemiFruitore();
			if(schemaFruitoritestB2.size()>0){
				for(int i=0;i<schemaFruitoritestB2.size();i++){
					SchemaXSD schema = schemaFruitoritestB2.get(i);
					schema.writeTo(testB2_outputDir,prettyPrint);
				}
			}
		}
		// Verifica erogatore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB2erogatore,xmlUtils), Costanti.TEST3_PORT_TYPES_EROGATORE, Costanti.TEST3_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestB2erogatore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestB2erogatore.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		// Verifica fruitore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB2fruitore,xmlUtils), Costanti.TEST3_PORT_TYPES_FRUITORE, Costanti.TEST3_OPERATIONS_FRUITORE,true);
		Validatore.validazioneWSDL(outputWsdltestB2fruitore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLFRUITORE", outputWsdltestB2fruitore.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_FRUITORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_FRUITORE,true);	
		System.out.println("Test b-2. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST B-3.
		System.out.println("Test b-3. \n\tWSDL2SPCoopUtility.split(String wsdlImplementativoErogatore,String wsdlImplementativoFruitore,INGLOBA_SOLO_SCHEMI_INCLUSI_IN_WSDL,false)");
		File testB3_outputDir = new File(outputDirTest,"B3");
		ConverterStandardWSDL2SplitWSDL testB3_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testB3_util = new ConverterStandardWSDL2SplitWSDL(testB3_outputDir,prettyPrint);
			else
				testB3_util = new ConverterStandardWSDL2SplitWSDL(testB3_outputDir);
		}else{
			testB3_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testB3_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE,
				StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_IN_WSDL,false);
		System.out.println("\tsplit ok");
		File outputWsdltestB3erogatore = new File(testB3_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File outputWsdltestB3fruitore = new File(testB3_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Fruitore.wsdl");
		File importWsdltestB3 = new File(testB3_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestB3);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestB3);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testB3_util.getWsdlErogatore(), outputWsdltestB3erogatore,prettyPrint);
			wsdlUtilities.writeWsdlTo(testB3_util.getWsdlFruitore(), outputWsdltestB3fruitore,prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestB3 = testB3_util.getSchemiErogatore();
			if(schemaErogatoritestB3.size()>0){
				for(int i=0;i<schemaErogatoritestB3.size();i++){
					SchemaXSD schema = schemaErogatoritestB3.get(i);
					schema.writeTo(testB3_outputDir,prettyPrint);
				}
			}
			List<SchemaXSDAccordoServizio> schemaFruitoritestB3 = testB3_util.getSchemiFruitore();
			if(schemaFruitoritestB3.size()>0){
				for(int i=0;i<schemaFruitoritestB3.size();i++){
					SchemaXSD schema = schemaFruitoritestB3.get(i);
					schema.writeTo(testB3_outputDir,prettyPrint);
				}
			}
		}
		// Verifica erogatore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB3erogatore,xmlUtils), Costanti.TEST3_PORT_TYPES_EROGATORE, Costanti.TEST3_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestB3erogatore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestB3erogatore.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		// Verifica fruitore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB3fruitore,xmlUtils), Costanti.TEST3_PORT_TYPES_FRUITORE, Costanti.TEST3_OPERATIONS_FRUITORE,true);
		Validatore.validazioneWSDL(outputWsdltestB3fruitore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLFRUITORE", outputWsdltestB3fruitore.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_FRUITORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_FRUITORE,true);	
		System.out.println("Test b-3. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST B-4.
		System.out.println("Test b-4. \n\tWSDL2SPCoopUtility.split(String wsdlImplementativoErogatore,String wsdlImplementativoFruitore,INGLOBA_SOLO_SCHEMI_INCLUSI_NOME_AUTOGENERATO_IN_WSDL,false)");
		File testB4_outputDir = new File(outputDirTest,"B4");
		ConverterStandardWSDL2SplitWSDL testB4_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testB4_util = new ConverterStandardWSDL2SplitWSDL(testB4_outputDir,prettyPrint);
			else
				testB4_util = new ConverterStandardWSDL2SplitWSDL(testB4_outputDir);
		}else{
			testB4_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testB4_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE,
				StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_NOME_AUTOGENERATO_IN_WSDL,false);
		System.out.println("\tsplit ok");
		File outputWsdltestB4erogatore = new File(testB4_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File outputWsdltestB4fruitore = new File(testB4_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Fruitore.wsdl");
		File importWsdltestB4 = new File(testB4_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestB4);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestB4);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testB4_util.getWsdlErogatore(), outputWsdltestB4erogatore,prettyPrint);
			wsdlUtilities.writeWsdlTo(testB4_util.getWsdlFruitore(), outputWsdltestB4fruitore,prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestB4 = testB4_util.getSchemiErogatore();
			if(schemaErogatoritestB4.size()>0){
				for(int i=0;i<schemaErogatoritestB4.size();i++){
					SchemaXSD schema = schemaErogatoritestB4.get(i);
					schema.writeTo(testB4_outputDir,prettyPrint);
				}
			}
			List<SchemaXSDAccordoServizio> schemaFruitoritestB4 = testB4_util.getSchemiFruitore();
			if(schemaFruitoritestB4.size()>0){
				for(int i=0;i<schemaFruitoritestB4.size();i++){
					SchemaXSD schema = schemaFruitoritestB4.get(i);
					schema.writeTo(testB4_outputDir,prettyPrint);
				}
			}
		}
		// Verifica erogatore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB4erogatore,xmlUtils), Costanti.TEST3_PORT_TYPES_EROGATORE, Costanti.TEST3_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestB4erogatore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestB4erogatore.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		// Verifica fruitore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB4fruitore,xmlUtils), Costanti.TEST3_PORT_TYPES_FRUITORE, Costanti.TEST3_OPERATIONS_FRUITORE,true);
		Validatore.validazioneWSDL(outputWsdltestB4fruitore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLFRUITORE", outputWsdltestB4fruitore.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO_FRUITORE,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_FRUITORE,true);	
		System.out.println("Test b-4. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST C-0.
		System.out.println("Test c-0. \n\tWSDL2SPCoopUtility.split(String wsdlImplementativoErogatore, String wsdlImplementativoFruitore,MANTIENI_IMPORT_INCLUDE_ORIGINALI,true)");
		File testC0_outputDir = new File(outputDirTest,"C0");
		ConverterStandardWSDL2SplitWSDL testC0_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testC0_util = new ConverterStandardWSDL2SplitWSDL(testC0_outputDir,prettyPrint);
			else
				testC0_util = new ConverterStandardWSDL2SplitWSDL(testC0_outputDir);
		}else{
			testC0_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testC0_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE,
				StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI,true);
		System.out.println("\tsplit ok");
		File outputWsdltestC0unico = new File(testC0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Definition.wsdl");
		File importWsdltestC0 = new File(testC0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestC0);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestC0);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testC0_util.getWsdlErogatore(), outputWsdltestC0unico, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestC0 = testC0_util.getSchemiErogatore();
			if(schemaErogatoritestC0.size()>0){
				for(int i=0;i<schemaErogatoritestC0.size();i++){
					SchemaXSD schema = schemaErogatoritestC0.get(i);
					schema.writeTo(testC0_outputDir, prettyPrint);
				}
			}
		}
		// Verifica unico wsdl
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestC0unico,xmlUtils), Costanti.TEST3_PORT_TYPES, Costanti.TEST3_OPERATIONS,true);
		Validatore.validazioneWSDL(outputWsdltestC0unico.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLUNICO", outputWsdltestC0unico.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO,true);
		System.out.println("Test c-0. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST C-1.
		System.out.println("Test c-1. \n\tWSDL2SPCoopUtility.split(String wsdlImplementativoErogatore,String wsdlImplementativoFruitore,INGLOBA_SCHEMI_IN_WSDL,true");
		File testC1_outputDir = new File(outputDirTest,"C1");
		ConverterStandardWSDL2SplitWSDL testC1_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testC1_util = new ConverterStandardWSDL2SplitWSDL(testC1_outputDir,prettyPrint);
			else
				testC1_util = new ConverterStandardWSDL2SplitWSDL(testC1_outputDir);
		}else{
			testC1_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testC1_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE,
				StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL,true);
		System.out.println("\tsplit ok");
		File outputWsdltestC1unico = new File(testC1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Definition.wsdl");
		File importWsdltestC1 = new File(testC1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestC1);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestC1);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testC1_util.getWsdlErogatore(), outputWsdltestC1unico,prettyPrint);
		}
		// Verifica unico wsdl
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestC1unico,xmlUtils), Costanti.TEST3_PORT_TYPES, Costanti.TEST3_OPERATIONS,true);
		Validatore.validazioneWSDL(outputWsdltestC1unico.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLUNICO", outputWsdltestC1unico.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO,true);	
		System.out.println("Test c-1. OK\n");
		
		
		
		
		
		
		
		
		
		
		// TEST C-2.
		System.out.println("Test c-2. \n\tWSDL2SPCoopUtility.split(String wsdlImplementativoErogatore,String wsdlImplementativoFruitore,INGLOBA_SOLO_SCHEMI_IMPORTATI_IN_WSDL,true)");
		File testC2_outputDir = new File(outputDirTest,"C2");
		ConverterStandardWSDL2SplitWSDL testC2_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testC2_util = new ConverterStandardWSDL2SplitWSDL(testC2_outputDir,prettyPrint);
			else
				testC2_util = new ConverterStandardWSDL2SplitWSDL(testC2_outputDir);
		}else{
			testC2_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testC2_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE,
				StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_IMPORTATI_IN_WSDL,true);
		System.out.println("\tsplit ok");
		File outputWsdltestC2unico = new File(testC2_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Definition.wsdl");
		File importWsdltestC2 = new File(testC2_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestC2);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestC2);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testC2_util.getWsdlErogatore(), outputWsdltestC2unico, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestC2 = testC2_util.getSchemiErogatore();
			if(schemaErogatoritestC2.size()>0){
				for(int i=0;i<schemaErogatoritestC2.size();i++){
					SchemaXSD schema = schemaErogatoritestC2.get(i);
					schema.writeTo(testC2_outputDir, prettyPrint);
				}
			}
		}
		// Verifica wsdl unico
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestC2unico,xmlUtils), Costanti.TEST3_PORT_TYPES, Costanti.TEST3_OPERATIONS,true);
		Validatore.validazioneWSDL(outputWsdltestC2unico.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLUNICO", outputWsdltestC2unico.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO,true);	
		System.out.println("Test c-2. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST C-3.
		System.out.println("Test c-3. \n\tWSDL2SPCoopUtility.split(String wsdlImplementativoErogatore,String wsdlImplementativoFruitore,INGLOBA_SOLO_SCHEMI_INCLUSI_IN_WSDL,true)");
		File testC3_outputDir = new File(outputDirTest,"C3");
		ConverterStandardWSDL2SplitWSDL testC3_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testC3_util = new ConverterStandardWSDL2SplitWSDL(testC3_outputDir,prettyPrint);
			else
				testC3_util = new ConverterStandardWSDL2SplitWSDL(testC3_outputDir);
		}else{
			testC3_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testC3_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE,
				StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_IN_WSDL,true);
		System.out.println("\tsplit ok");
		File outputWsdltestC3unico = new File(testC3_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Definition.wsdl");
		File importWsdltestC3 = new File(testC3_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestC3);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestC3);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testC3_util.getWsdlErogatore(), outputWsdltestC3unico, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestC3 = testC3_util.getSchemiErogatore();
			if(schemaErogatoritestC3.size()>0){
				for(int i=0;i<schemaErogatoritestC3.size();i++){
					SchemaXSD schema = schemaErogatoritestC3.get(i);
					schema.writeTo(testC3_outputDir, prettyPrint);
				}
			}
		}
		// Verifica wsdl unico
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestC3unico,xmlUtils), Costanti.TEST3_PORT_TYPES, Costanti.TEST3_OPERATIONS,true);
		Validatore.validazioneWSDL(outputWsdltestC3unico.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLUNICO", outputWsdltestC3unico.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO,true);	
		System.out.println("Test c-3. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST C-4.
		System.out.println("Test c-4. \n\tWSDL2SPCoopUtility.split(String wsdlImplementativoErogatore,String wsdlImplementativoFruitore,INGLOBA_SOLO_SCHEMI_INCLUSI_NOME_AUTOGENERATO_IN_WSDL,true)");
		File testC4_outputDir = new File(outputDirTest,"C4");
		ConverterStandardWSDL2SplitWSDL testC4_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testC4_util = new ConverterStandardWSDL2SplitWSDL(testC4_outputDir,prettyPrint);
			else
				testC4_util = new ConverterStandardWSDL2SplitWSDL(testC4_outputDir);
		}else{
			testC4_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit ...");
		testC4_util.buildWSDLStandard(Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE,Costanti.TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE,
				StandardWSDLOutputMode.INGLOBA_SOLO_SCHEMI_INCLUSI_NOME_AUTOGENERATO_IN_WSDL,true);
		System.out.println("\tsplit ok");
		File outputWsdltestC4unico = new File(testC4_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Definition.wsdl");
		File importWsdltestC4 = new File(testC4_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestC4);
		FileSystemUtilities.copy(Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestC4);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testC4_util.getWsdlErogatore(), outputWsdltestC4unico, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestC4 = testC4_util.getSchemiErogatore();
			if(schemaErogatoritestC4.size()>0){
				for(int i=0;i<schemaErogatoritestC4.size();i++){
					SchemaXSD schema = schemaErogatoritestC4.get(i);
					schema.writeTo(testC4_outputDir, prettyPrint);
				}
			}
		}
		// Verifica wsdl unico
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestC4unico,xmlUtils), Costanti.TEST3_PORT_TYPES, Costanti.TEST3_OPERATIONS,true);
		Validatore.validazioneWSDL(outputWsdltestC4unico.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLUNICO", outputWsdltestC4unico.getAbsolutePath(), Costanti.TEST3_FILE_PATH_LOGICO,
				Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO,true);	
		System.out.println("Test c-4. OK\n");
		
		
	}







	
	
	
	private static void test4(boolean libOutputDir, boolean prettyPrint)throws Exception{
		
		File outputDirTest = null;
		if(libOutputDir){
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test4_WSDLStandard_bytes"+File.separatorChar+"writeToAutomaticoPrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test4_WSDLStandard_bytes"+File.separatorChar+"writeToAutomatico");
			}
		}else{
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test4_WSDLStandard_bytes"+File.separatorChar+"writeToManualePrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test4_WSDLStandard_bytes"+File.separatorChar+"writeToManuale");
			}
		}
		FileSystemUtilities.mkdirParentDirectory(outputDirTest.getAbsolutePath());
		if(outputDirTest.mkdir()==false){
			throw new Exception("Creazione directory fallita: "+outputDirTest.getAbsolutePath());
		}
		
		
		
		
		XMLUtils xmlUtils = XMLUtils.getInstance();
		WSDLUtilities wsdlUtilities = WSDLUtilities.getInstance(xmlUtils);
		
		
		
		
		
		
		// TEST A-0.
		System.out.println("Test a-0. \n\tWSDL2SPCoopUtility.split(list<byte[]>schemi,byte[]implErogatore,byte[]logicoErogatore,MANTIENI_IMPORT_INCLUDE_ORIGINALI)");
		File testA0_outputDir = new File(outputDirTest,"A0");
		ConverterStandardWSDL2SplitWSDL testA0_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA0_util = new ConverterStandardWSDL2SplitWSDL(testA0_outputDir,prettyPrint);
			else
				testA0_util = new ConverterStandardWSDL2SplitWSDL(testA0_outputDir);
		}else{
			testA0_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		
		System.out.println("\tsplit ...");
		testA0_util.buildWSDLStandard(Costanti.TEST4_SCHEMI_XSD_LIST,
				Costanti.TEST4_WSDL_BYTE_LOGICO_EROGATORE,
				Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_EROGATORE,StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI);
		System.out.println("\tsplit ok");
		File outputWsdltestA0 = new File(testA0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File importWsdltestA0 = new File(testA0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST4_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestA0);
		FileSystemUtilities.copy(Costanti.TEST4_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestA0);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testA0_util.getWsdlErogatore(), outputWsdltestA0, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestA0 = testA0_util.getSchemiErogatore();
			if(schemaErogatoritestA0.size()>0){
				for(int i=0;i<schemaErogatoritestA0.size();i++){
					SchemaXSD schema = schemaErogatoritestA0.get(i);
					schema.writeTo(testA0_outputDir, prettyPrint);
				}
			}
		}
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestA0,xmlUtils), Costanti.TEST4_PORT_TYPES_EROGATORE, Costanti.TEST4_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestA0.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestA0.getAbsolutePath(), Costanti.TEST4_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST4_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		System.out.println("Test a-0. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST A-1.
		System.out.println("Test a-1. \n\tWSDL2SPCoopUtility.split(list<byte[]>schemi,byte[]implErogatore,byte[]logicoErogatore,INGLOBA_SCHEMI_IN_WSDL)");
		File testA1_outputDir = new File(outputDirTest,"A1");
		ConverterStandardWSDL2SplitWSDL testA1_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA1_util = new ConverterStandardWSDL2SplitWSDL(testA1_outputDir,prettyPrint);
			else
				testA1_util = new ConverterStandardWSDL2SplitWSDL(testA1_outputDir);
		}else{
			testA1_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		
		System.out.println("\tsplit ...");
		testA1_util.buildWSDLStandard(Costanti.TEST4_SCHEMI_XSD_LIST,
				Costanti.TEST4_WSDL_BYTE_LOGICO_EROGATORE,
				Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_EROGATORE,StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL);
		System.out.println("\tsplit ok");
		File outputWsdltestA1 = new File(testA1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File importWsdltestA1 = new File(testA1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST4_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestA1);
		FileSystemUtilities.copy(Costanti.TEST4_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestA1);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testA1_util.getWsdlErogatore(), outputWsdltestA1, prettyPrint);
		}
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestA1,xmlUtils), Costanti.TEST4_PORT_TYPES_EROGATORE, Costanti.TEST4_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestA1.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestA1.getAbsolutePath(), Costanti.TEST4_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST4_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		System.out.println("Test a-1. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST B-0.
		System.out.println("Test b-0. \n\tWSDL2SPCoopUtility.split(list<byte[]>schemi,byte[]implErogatore,byte[]logicoErogatore,byte[]implFruitore,byte[]logicoFruitore,false,MANTIENI_IMPORT_INCLUDE_ORIGINALI)");
		File testB0_outputDir = new File(outputDirTest,"B0");
		ConverterStandardWSDL2SplitWSDL testB0_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testB0_util = new ConverterStandardWSDL2SplitWSDL(testB0_outputDir,prettyPrint);
			else
				testB0_util = new ConverterStandardWSDL2SplitWSDL(testB0_outputDir);
		}else{
			testB0_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		
		System.out.println("\tsplit ...");
		testB0_util.buildWSDLStandard(Costanti.TEST4_SCHEMI_XSD_LIST,
				Costanti.TEST4_WSDL_BYTE_LOGICO_EROGATORE,
				Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_EROGATORE,
				Costanti.TEST4_WSDL_BYTE_LOGICO_FRUITORE,
				Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_FRUITORE,
				false,StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI);
		System.out.println("\tsplit ok");
		File outputWsdltestB0erogatore = new File(testB0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File outputWsdltestB0fruitore = new File(testB0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Fruitore.wsdl");
		File importWsdltestB0 = new File(testB0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST4_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestB0);
		FileSystemUtilities.copy(Costanti.TEST4_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestB0);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testB0_util.getWsdlErogatore(), outputWsdltestB0erogatore, prettyPrint);
			wsdlUtilities.writeWsdlTo(testB0_util.getWsdlFruitore(), outputWsdltestB0fruitore, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestB0 = testB0_util.getSchemiErogatore();
			if(schemaErogatoritestB0.size()>0){
				for(int i=0;i<schemaErogatoritestB0.size();i++){
					SchemaXSD schema = schemaErogatoritestB0.get(i);
					schema.writeTo(testB0_outputDir, prettyPrint);
				}
			}
			List<SchemaXSDAccordoServizio> schemaFruitoritestB0 = testB0_util.getSchemiFruitore();
			if(schemaFruitoritestB0.size()>0){
				for(int i=0;i<schemaFruitoritestB0.size();i++){
					SchemaXSD schema = schemaFruitoritestB0.get(i);
					schema.writeTo(testB0_outputDir, prettyPrint);
				}
			}
		}
		// verifica erogatore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB0erogatore,xmlUtils), Costanti.TEST4_PORT_TYPES_EROGATORE, Costanti.TEST4_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestB0erogatore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestB0erogatore.getAbsolutePath(), Costanti.TEST4_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST4_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		// Verifica fruitore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB0fruitore,xmlUtils), Costanti.TEST4_PORT_TYPES_FRUITORE, Costanti.TEST4_OPERATIONS_FRUITORE,true);
		Validatore.validazioneWSDL(outputWsdltestB0fruitore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLFRUITORE", outputWsdltestB0fruitore.getAbsolutePath(), Costanti.TEST4_FILE_PATH_LOGICO_FRUITORE,
				Costanti.TEST4_FILE_PATH_IMPLEMENTATIVO_FRUITORE,true);	
		System.out.println("Test b-0. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST B-1.
		System.out.println("Test b-1. \n\tWSDL2SPCoopUtility.split(list<byte[]>schemi,byte[]implErogatore,byte[]logicoErogatore,byte[]implFruitore,byte[]logicoFruitore,false,INGLOBA_SCHEMI_IN_WSDL)");
		File testB1_outputDir = new File(outputDirTest,"B1");
		ConverterStandardWSDL2SplitWSDL testB1_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testB1_util = new ConverterStandardWSDL2SplitWSDL(testB1_outputDir,prettyPrint);
			else
				testB1_util = new ConverterStandardWSDL2SplitWSDL(testB1_outputDir);
		}else{
			testB1_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		
		System.out.println("\tsplit ...");
		testB1_util.buildWSDLStandard(Costanti.TEST4_SCHEMI_XSD_LIST,
				Costanti.TEST4_WSDL_BYTE_LOGICO_EROGATORE,
				Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_EROGATORE,
				Costanti.TEST4_WSDL_BYTE_LOGICO_FRUITORE,
				Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_FRUITORE,
				false,StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL);
		System.out.println("\tsplit ok");
		File outputWsdltestB1erogatore = new File(testB1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Erogatore.wsdl");
		File outputWsdltestB1fruitore = new File(testB1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Fruitore.wsdl");
		File importWsdltestB1 = new File(testB1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST4_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestB1);
		FileSystemUtilities.copy(Costanti.TEST4_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestB1);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testB1_util.getWsdlErogatore(), outputWsdltestB1erogatore, prettyPrint);
			wsdlUtilities.writeWsdlTo(testB1_util.getWsdlFruitore(), outputWsdltestB1fruitore, prettyPrint);
		}
		// verifica erogatore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB1erogatore,xmlUtils), Costanti.TEST4_PORT_TYPES_EROGATORE, Costanti.TEST4_OPERATIONS_EROGATORE,true);
		Validatore.validazioneWSDL(outputWsdltestB1erogatore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLEROGATORE", outputWsdltestB1erogatore.getAbsolutePath(), Costanti.TEST4_FILE_PATH_LOGICO_EROGATORE,
				Costanti.TEST4_FILE_PATH_IMPLEMENTATIVO_EROGATORE,true);	
		// Verifica fruitore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestB1fruitore,xmlUtils), Costanti.TEST4_PORT_TYPES_FRUITORE, Costanti.TEST4_OPERATIONS_FRUITORE,true);
		Validatore.validazioneWSDL(outputWsdltestB1fruitore.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLFRUITORE", outputWsdltestB1fruitore.getAbsolutePath(), Costanti.TEST4_FILE_PATH_LOGICO_FRUITORE,
				Costanti.TEST4_FILE_PATH_IMPLEMENTATIVO_FRUITORE,true);	
		System.out.println("Test b-1. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TEST C-0.
		System.out.println("Test c-0. \n\tWSDL2SPCoopUtility.split(list<byte[]>schemi,byte[]implErogatore,byte[]logicoErogatore,byte[]implFruitore,byte[]logicoFruitore,true,MANTIENI_IMPORT_INCLUDE_ORIGINALI)");
		File testC0_outputDir = new File(outputDirTest,"C0");
		ConverterStandardWSDL2SplitWSDL testC0_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testC0_util = new ConverterStandardWSDL2SplitWSDL(testC0_outputDir,prettyPrint);
			else
				testC0_util = new ConverterStandardWSDL2SplitWSDL(testC0_outputDir);
		}else{
			testC0_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		
		System.out.println("\tsplit ...");
		testC0_util.buildWSDLStandard(Costanti.TEST4_SCHEMI_XSD_LIST,
				Costanti.TEST4_WSDL_BYTE_LOGICO_EROGATORE,
				Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_EROGATORE,
				Costanti.TEST4_WSDL_BYTE_LOGICO_FRUITORE,
				Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_FRUITORE,
				true,StandardWSDLOutputMode.MANTIENI_IMPORT_INCLUDE_ORIGINALI);
		System.out.println("\tsplit ok");
		File outputWsdltestC0unificato = new File(testC0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Definition.wsdl");
		File importWsdltestC0 = new File(testC0_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST4_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestC0);
		FileSystemUtilities.copy(Costanti.TEST4_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestC0);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testC0_util.getWsdlErogatore(), outputWsdltestC0unificato, prettyPrint);
			// riporto allegati
			List<SchemaXSDAccordoServizio> schemaErogatoritestC0 = testC0_util.getSchemiErogatore();
			if(schemaErogatoritestC0.size()>0){
				for(int i=0;i<schemaErogatoritestC0.size();i++){
					SchemaXSD schema = schemaErogatoritestC0.get(i);
					schema.writeTo(testC0_outputDir, prettyPrint);
				}
			}
		}
		// verifica erogatore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestC0unificato,xmlUtils), Costanti.TEST4_PORT_TYPES, Costanti.TEST4_OPERATIONS,true);
		Validatore.validazioneWSDL(outputWsdltestC0unificato.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLUNIFICATO", outputWsdltestC0unificato.getAbsolutePath(), Costanti.TEST4_FILE_PATH_LOGICO,
				Costanti.TEST4_FILE_PATH_IMPLEMENTATIVO,true);	
		System.out.println("Test c-0. OK\n");
		
		
		
		
		
		
		
		
		
		
		
		// TEST C-1.
		System.out.println("Test c-1. \n\tWSDL2SPCoopUtility.split(list<byte[]>schemi,byte[]implErogatore,byte[]logicoErogatore,byte[]implFruitore,byte[]logicoFruitore,true,INGLOBA_SCHEMI_IN_WSDL)");
		File testC1_outputDir = new File(outputDirTest,"C1");
		ConverterStandardWSDL2SplitWSDL testC1_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testC1_util = new ConverterStandardWSDL2SplitWSDL(testC1_outputDir,prettyPrint);
			else
				testC1_util = new ConverterStandardWSDL2SplitWSDL(testC1_outputDir);
		}else{
			testC1_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		
		System.out.println("\tsplit ...");
		testC1_util.buildWSDLStandard(Costanti.TEST4_SCHEMI_XSD_LIST,
				Costanti.TEST4_WSDL_BYTE_LOGICO_EROGATORE,
				Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_EROGATORE,
				Costanti.TEST4_WSDL_BYTE_LOGICO_FRUITORE,
				Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_FRUITORE,
				true,StandardWSDLOutputMode.INGLOBA_SCHEMI_IN_WSDL);
		System.out.println("\tsplit ok");
		File outputWsdltestC1unificato = new File(testC1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+"Definition.wsdl");
		File importWsdltestC1 = new File(testC1_outputDir,it.gov.spcoop.sica.dao.Costanti.SPECIFICA_INTERFACCIA_DIR+File.separatorChar+Costanti.TEST4_WSDL_NAME_IMPORT_WSDL_TEST);
		FileSystemUtilities.mkdirParentDirectory(importWsdltestC1);
		FileSystemUtilities.copy(Costanti.TEST4_WSDL_FILE_IMPORT_WSDL_TEST, importWsdltestC1);
		if(!libOutputDir){
			wsdlUtilities.writeWsdlTo(testC1_util.getWsdlErogatore(), outputWsdltestC1unificato, prettyPrint);
		}
		// verifica erogatore
		Validatore.verificaPresenzaElementiWSDLAttesi(new DefinitionWrapper(outputWsdltestC1unificato,xmlUtils), Costanti.TEST4_PORT_TYPES, Costanti.TEST4_OPERATIONS,true);
		Validatore.validazioneWSDL(outputWsdltestC1unificato.getAbsolutePath(),false);
		Validatore.generazioneVerificaStubSkeleton("WSDLUNIFICATO", outputWsdltestC1unificato.getAbsolutePath(), Costanti.TEST4_FILE_PATH_LOGICO,
				Costanti.TEST4_FILE_PATH_IMPLEMENTATIVO,true);	
		System.out.println("Test c-1. OK\n");
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static void test5(boolean libOutputDir,boolean prettyPrint)throws Exception{
		
		File outputDirTest = null;
		if(libOutputDir){
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test5_WSDLFilenameDuplicati"+File.separatorChar+"writeToAutomaticoPrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test5_WSDLFilenameDuplicati"+File.separatorChar+"writeToAutomatico");
			}
		}else{
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test5_WSDLFilenameDuplicati"+File.separatorChar+"writeToManualePrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test5_WSDLFilenameDuplicati"+File.separatorChar+"writeToManuale");
			}
		}
		FileSystemUtilities.mkdirParentDirectory(outputDirTest.getAbsolutePath());
		if(outputDirTest.mkdir()==false){
			throw new Exception("Creazione directory fallita: "+outputDirTest.getAbsolutePath());
		}
		
			
		
		// TEST A.
		
		System.out.println("Test a. \n\tWSDL2SPCoopUtility.split(String wsdl)");
		File testA_outputDir = new File(outputDirTest,"A");
		ConverterStandardWSDL2SplitWSDL testA_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA_util = new ConverterStandardWSDL2SplitWSDL(testA_outputDir,prettyPrint);
			else
				testA_util = new ConverterStandardWSDL2SplitWSDL(testA_outputDir);
		}else{
			testA_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testA_util.split(Costanti.TEST5_WSDL_FILE_PATH);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testA_outputDir, testA_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testA_outputDir, true, false, true, true, Costanti.TEST5_SCHEMI_TYPES, 3);
		Validatore.validaOutputErogatore(testA_outputDir.getAbsolutePath(), Costanti.TEST5_PORT_TYPES, Costanti.TEST5_OPERATIONS, 
				Costanti.TEST5_FILE_PATH_LOGICO,Costanti.TEST5_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test a. OK\n");
		
		
		
		// TEST B.
		// Siccome utilizzo lo stesso wsdl sia per l'erogatore che per il fruitore avro' le stesse classi di output.
		// NOTA: gli allegati (a meno di quelli inseriti direttamente in wsdl:types) non saranno ripetuti 2 volte!!

		System.out.println("Test b. \n\tWSDL2SPCoopUtility.split(String wsdlErogatore,String wsdlFruitore)");
		File testB_outputDir = new File(outputDirTest,"B");
		ConverterStandardWSDL2SplitWSDL testB_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testB_util = new ConverterStandardWSDL2SplitWSDL(testB_outputDir,prettyPrint);
			else
				testB_util = new ConverterStandardWSDL2SplitWSDL(testB_outputDir);
		}else{
			testB_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testB_util.split(Costanti.TEST5_WSDL_FILE_PATH,Costanti.TEST5_WSDL_FILE_PATH);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testB_outputDir, testB_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testB_outputDir, true, true, true, true, Costanti.TEST5_SCHEMI_TYPES, 6);
		Validatore.validaOutputErogatore(testB_outputDir.getAbsolutePath(), Costanti.TEST5_PORT_TYPES, Costanti.TEST5_OPERATIONS, 
				Costanti.TEST5_FILE_PATH_LOGICO,Costanti.TEST5_FILE_PATH_IMPLEMENTATIVO);
		Validatore.validaOutputFruitore(testB_outputDir.getAbsolutePath(), Costanti.TEST5_PORT_TYPES, Costanti.TEST5_OPERATIONS, 
				Costanti.TEST5_FILE_PATH_LOGICO,Costanti.TEST5_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test b. OK\n");
		
		
		
		// TEST C.
		// Gli allegati pero' vengono salvati come specifiche semiformali
		System.out.println("Test c. \n\tWSDL2SPCoopUtility.split(String wsdlErogatore,SPECIFICA_SEMIFORMALE)");
		File testC_outputDir = new File(outputDirTest,"C");
		ConverterStandardWSDL2SplitWSDL testC_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testC_util = new ConverterStandardWSDL2SplitWSDL(testC_outputDir,prettyPrint);
			else
				testC_util = new ConverterStandardWSDL2SplitWSDL(testC_outputDir);
		}else{
			testC_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testC_util.split(Costanti.TEST5_WSDL_FILE_PATH,TipoSchemaXSDAccordoServizio.SPECIFICA_SEMIFORMALE);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testC_outputDir, testC_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testC_outputDir, true, false, true, false, Costanti.TEST5_SCHEMI_TYPES, 3);
		Validatore.validaOutputErogatore(testC_outputDir.getAbsolutePath(), Costanti.TEST5_PORT_TYPES, Costanti.TEST5_OPERATIONS, 
				Costanti.TEST5_FILE_PATH_LOGICO,Costanti.TEST5_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test c. OK\n");
		
		
		
		// TEST D.
		// Siccome utilizzo lo stesso wsdl sia per l'erogatore che per il fruitore avro' le stesse classi di output.
		// NOTA: gli allegati (a meno di quelli inseriti direttamente in wsdl:types) non saranno ripetuti 2 volte!!
		// Gli allegati pero' vengono salvati come specifiche semiformali
		
		System.out.println("Test d. \n\tWSDL2SPCoopUtility.split(String wsdlErogatore,String wsdlFruitore,SPECIFICA_SEMIFORMALE)");
		File testD_outputDir = new File(outputDirTest,"D");
		ConverterStandardWSDL2SplitWSDL testD_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testD_util = new ConverterStandardWSDL2SplitWSDL(testD_outputDir,prettyPrint);
			else
				testD_util = new ConverterStandardWSDL2SplitWSDL(testD_outputDir);
		}else{
			testD_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testD_util.split(Costanti.TEST5_WSDL_FILE_PATH,Costanti.TEST5_WSDL_FILE_PATH, TipoSchemaXSDAccordoServizio.SPECIFICA_SEMIFORMALE);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testD_outputDir, testD_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testD_outputDir, true, true, true, false, Costanti.TEST5_SCHEMI_TYPES, 6);
		Validatore.validaOutputErogatore(testD_outputDir.getAbsolutePath(), Costanti.TEST5_PORT_TYPES, Costanti.TEST5_OPERATIONS, 
				Costanti.TEST5_FILE_PATH_LOGICO,Costanti.TEST5_FILE_PATH_IMPLEMENTATIVO);
		Validatore.validaOutputFruitore(testD_outputDir.getAbsolutePath(), Costanti.TEST5_PORT_TYPES, Costanti.TEST5_OPERATIONS, 
				Costanti.TEST5_FILE_PATH_LOGICO,Costanti.TEST5_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test d. OK\n");
		
		
		
		// TEST E.
		System.out.println("Test e. \n\tWSDL2SPCoopUtility.split(String wsdl, porttypesErogatore, operationPorttypesErogatore)");
		File testE_outputDir = new File(outputDirTest,"E");
		ConverterStandardWSDL2SplitWSDL testE_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testE_util = new ConverterStandardWSDL2SplitWSDL(testE_outputDir,prettyPrint);
			else
				testE_util = new ConverterStandardWSDL2SplitWSDL(testE_outputDir);
		}else{
			testE_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testE_util.split(Costanti.TEST5_WSDL_FILE_PATH, Costanti.TEST5_PORT_TYPE_ASINCRONO_ASIMMETRICO,Costanti.TEST5_OPERATIONS_ASINCRONO_ASIMMETRICO);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testE_outputDir, testE_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testE_outputDir, true, false, true, true, Costanti.TEST5_SCHEMI_TYPES, 3);
		Validatore.validaOutputErogatore(testE_outputDir.getAbsolutePath(), Costanti.TEST5_PORT_TYPE_ASINCRONO_ASIMMETRICO,Costanti.TEST5_OPERATIONS_ASINCRONO_ASIMMETRICO, 
				Costanti.TEST5_FILE_PATH_LOGICO_ASINCRONO_ASIMMETRICO,
				Costanti.TEST5_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO);
		System.out.println("Test e. OK\n");
		
		
		
		
		
		// TEST F.
		System.out.println("Test f. \n\tWSDL2SPCoopUtility.split(String wsdl, porttypesErogatore,operationPorttypesErogatore,porttypesFruitore, operationPorttypesFruitore)");
		File testF_outputDir = new File(outputDirTest,"F");
		ConverterStandardWSDL2SplitWSDL testF_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testF_util = new ConverterStandardWSDL2SplitWSDL(testF_outputDir,prettyPrint);
			else
				testF_util = new ConverterStandardWSDL2SplitWSDL(testF_outputDir);
		}else{
			testF_util = new ConverterStandardWSDL2SplitWSDL();
		}	
		System.out.println("\tsplit...");
		testF_util.split(Costanti.TEST5_WSDL_FILE_PATH, Costanti.TEST5_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST5_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST5_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA, 
				Costanti.TEST5_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testF_outputDir, testF_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testF_outputDir, true, true, true, true, Costanti.TEST5_SCHEMI_TYPES, 3);
		Validatore.validaOutput(testF_outputDir.getAbsolutePath(), Costanti.TEST5_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA, Costanti.TEST5_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST5_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST5_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA, 
				Costanti.TEST5_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA, Costanti.TEST5_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA, 
				Costanti.TEST5_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RISPOSTA, 
				Costanti.TEST5_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA);
		System.out.println("Test f. OK\n");
	}
	
	
	
	


	
	
	
	
	
	
	
	
	private static void test6(boolean libOutputDir,boolean prettyPrint)throws Exception{
		
		File outputDirTest = null;
		if(libOutputDir){
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test6_WSDLNamespace"+File.separatorChar+"writeToAutomaticoPrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test6_WSDLNamespace"+File.separatorChar+"writeToAutomatico");
			}
		}else{
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test6_WSDLNamespace"+File.separatorChar+"writeToManualePrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test6_WSDLNamespace"+File.separatorChar+"writeToManuale");
			}
		}
		FileSystemUtilities.mkdirParentDirectory(outputDirTest.getAbsolutePath());
		if(outputDirTest.mkdir()==false){
			throw new Exception("Creazione directory fallita: "+outputDirTest.getAbsolutePath());
		}
		
			
		
		// TEST A.
		
		System.out.println("Test a. \n\tWSDL2SPCoopUtility.split(String wsdl)");
		File testA_outputDir = new File(outputDirTest,"A");
		ConverterStandardWSDL2SplitWSDL testA_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA_util = new ConverterStandardWSDL2SplitWSDL(testA_outputDir,prettyPrint);
			else
				testA_util = new ConverterStandardWSDL2SplitWSDL(testA_outputDir);
		}else{
			testA_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testA_util.split(Costanti.TEST6_WSDL_FILE_PATH);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testA_outputDir, testA_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testA_outputDir, true, false, true, true, Costanti.TEST6_SCHEMI, 3);
		Validatore.validaOutputErogatore(testA_outputDir.getAbsolutePath(), Costanti.TEST6_PORT_TYPES, Costanti.TEST6_OPERATIONS, 
				Costanti.TEST6_FILE_PATH_LOGICO,Costanti.TEST6_FILE_PATH_IMPLEMENTATIVO);
		System.out.println("Test a. OK\n");
	
	
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static void test7(boolean libOutputDir,boolean prettyPrint)throws Exception{
		
		File outputDirTest = null;
		if(libOutputDir){
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test7_WSDLPrelevatoDaUrl"+File.separatorChar+"writeToAutomaticoPrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test7_WSDLPrelevatoDaUrl"+File.separatorChar+"writeToAutomatico");
			}
		}else{
			if(prettyPrint){
				outputDirTest = new File(Testsuite.outputDir,"Test7_WSDLPrelevatoDaUrl"+File.separatorChar+"writeToManualePrettyPrint");
			}else{
				outputDirTest = new File(Testsuite.outputDir,"Test7_WSDLPrelevatoDaUrl"+File.separatorChar+"writeToManuale");
			}
		}
		FileSystemUtilities.mkdirParentDirectory(outputDirTest.getAbsolutePath());
		if(outputDirTest.mkdir()==false){
			throw new Exception("Creazione directory fallita: "+outputDirTest.getAbsolutePath());
		}
		
			
		XMLUtils xmlUtils = XMLUtils.getInstance();
		
		
		// TEST A.
		
		System.out.println("Test a. \n\tWSDL2SPCoopUtility.split("+Costanti.TEST7_WSDL_URL+")");
		File testA_outputDir = new File(outputDirTest,"A");
		ConverterStandardWSDL2SplitWSDL testA_util = null;
		if(libOutputDir){
			if(prettyPrint)
				testA_util = new ConverterStandardWSDL2SplitWSDL(testA_outputDir,prettyPrint);
			else
				testA_util = new ConverterStandardWSDL2SplitWSDL(testA_outputDir);
		}else{
			testA_util = new ConverterStandardWSDL2SplitWSDL();
		}
		System.out.println("\tsplit...");
		testA_util.split(Costanti.TEST7_WSDL_URL);
		System.out.println("\tsplit ok");
		if(!libOutputDir){
			Utilities.writeSpcoopWsdlTo(testA_outputDir, testA_util, prettyPrint);
		}
		Validatore.verificaFileAttesi(testA_outputDir, true, false, true, true, Costanti.TEST7_SCHEMI, 0);

		// WSDL Concettuale
		System.out.println("\tWSDL Concettuale ...");
		String concettualePath = testA_outputDir.getAbsolutePath() + File.separator + SplitWSDL.FOLDER_INTERFACCE + File.separator + SplitWSDL.CONCETTUALE_FILENAME;
		DefinitionWrapper concettuale = new DefinitionWrapper(concettualePath,xmlUtils);
		Validatore.verificaPresenzaElementiWSDLAttesi(concettuale, Costanti.TEST7_PORT_TYPES,Costanti.TEST7_OPERATIONS,false);
		Validatore.validazioneWSDL(concettualePath,true);
		Validatore.generazioneVerificaStubSkeleton("CONCETTUALE", concettualePath, Costanti.TEST7_FILE_PATH_LOGICO,null,true);
		System.out.println("\tWSDL Concettuale ok");

		// WSDL ImplementativoErogatore
		System.out.println("\tWSDL ImplementativoErogatore ...");
		String implementativoErogatorePath = testA_outputDir.getAbsolutePath() + File.separator + SplitWSDL.FOLDER_IMPLEMENTATIVI + File.separator + SplitWSDL.IMPLEMENTATIVO_EROGATORE_FILENAME;
		DefinitionWrapper implementativoErogatore = new DefinitionWrapper(implementativoErogatorePath,xmlUtils);
		Validatore.verificaPresenzaElementiWSDLAttesi(implementativoErogatore, Costanti.TEST7_PORT_TYPES,Costanti.TEST7_OPERATIONS,true);
		Validatore.validazioneWSDL(implementativoErogatorePath,false);
		Validatore.generazioneVerificaStubSkeleton("EROGATORE", implementativoErogatorePath, Costanti.TEST7_FILE_PATH_LOGICO_EROGATORE,Costanti.TEST7_FILE_PATH_IMPLEMENTATIVO,true);
		System.out.println("\tWSDL ImplementativoErogatore ok");
		System.out.println("Test a. OK\n");
	
	
	}
	
	
	
	


	

}


