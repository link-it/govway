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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.registry.wsdl.SchemaXSDAccordoServizio;
import org.openspcoop2.core.registry.wsdl.TipoSchemaXSDAccordoServizio;
import org.openspcoop2.utils.resources.FileSystemUtilities;



/**
*
*
* @author Lorenzo Nardi <nardi@link.it>
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class Costanti {

	
	
	/* ****************** TEST 1 ************************* */
	
	public static File TEST1_BASE_DIR = new File("test_files"+File.separatorChar+"test1");
	public static File TEST1_WSDL_FILE = new File(Costanti.TEST1_BASE_DIR,"wsdl"+File.separatorChar+"TestSuite.wsdl");
	public static String TEST1_WSDL_FILE_PATH = Costanti.TEST1_WSDL_FILE.getAbsolutePath();
	
	public static String [] TEST1_PORT_TYPES_EROGATORE = {"Oneway","Sincrono","AsincronoSimmetricoRichiesta","AsincronoAsimmetricoRichiesta",
		"AsincronoAsimmetricoRisposta","AsincronoAsimmetrico","SincronoNamespaceWSDL"};
	public static String [] TEST1_OPERATIONS_EROGATORE = {"notifica","aggiornamento","richiestaAggiornamento","richiestaRegistrazione",
		"richiestaStatoRegistrazione","richiestaRegistrazione,richiestaStatoRegistrazione","aggiornamentoNamespaceWSDL"};
	
	public static String [] TEST1_PORT_TYPES_FRUITORE = {"AsincronoSimmetricoRisposta"};
	public static String [] TEST1_OPERATIONS_FRUITORE = {"esitoAggiornamento"};
	
	public static String [] TEST1_PORT_TYPES = {Costanti.TEST1_PORT_TYPES_EROGATORE[0],Costanti.TEST1_PORT_TYPES_EROGATORE[1],Costanti.TEST1_PORT_TYPES_EROGATORE[2],
		Costanti.TEST1_PORT_TYPES_EROGATORE[3],Costanti.TEST1_PORT_TYPES_EROGATORE[4],Costanti.TEST1_PORT_TYPES_EROGATORE[5],Costanti.TEST1_PORT_TYPES_EROGATORE[6],
		Costanti.TEST1_PORT_TYPES_FRUITORE[0]};
	public static String [] TEST1_OPERATIONS = {Costanti.TEST1_OPERATIONS_EROGATORE[0],Costanti.TEST1_OPERATIONS_EROGATORE[1],Costanti.TEST1_OPERATIONS_EROGATORE[2],
		Costanti.TEST1_OPERATIONS_EROGATORE[3],Costanti.TEST1_OPERATIONS_EROGATORE[4],Costanti.TEST1_OPERATIONS_EROGATORE[5],Costanti.TEST1_OPERATIONS_EROGATORE[6],
		Costanti.TEST1_OPERATIONS_FRUITORE[0]};	
	
	private static String TEST1_FILE_PATH_NAMESPACE = "org"+File.separatorChar+"openspcoop2"+File.separatorChar+"example"+File.separatorChar;
	public static String [] TEST1_FILE_PATH_LOGICO_EROGATORE = {
			Costanti.TEST1_FILE_PATH_NAMESPACE+"AggiornamentoNamespaceWSDLRequest.java",Costanti.TEST1_FILE_PATH_NAMESPACE+"AggiornamentoNamespaceWSDLResponse.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"allegato"+File.separatorChar+"_import"+File.separatorChar+"EsempioAllegatoImport1.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"allegato"+File.separatorChar+"_import"+File.separatorChar+"EsempioAllegatoImport2.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"allegato"+File.separatorChar+"_import"+File.separatorChar+"allegato"+File.separatorChar+"interno"+File.separatorChar+"EsempioAllegatoInterno.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"allegato"+File.separatorChar+"import2"+File.separatorChar+"AltroOggettoImportato.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"allegato"+File.separatorChar+"importwsdl"+File.separatorChar+"AllegatoImportatoDirettamenteInWSDL.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"specificasemiformale"+File.separatorChar+"_import"+File.separatorChar+"EsempioSpecificaSemiformaleImport1.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"specificasemiformale"+File.separatorChar+"_import"+File.separatorChar+"EsempioSpecificaSemiformaleImport2.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"specificasemiformale"+File.separatorChar+"_import"+File.separatorChar+"specificasemiformale"+File.separatorChar+"interno"+File.separatorChar+"EsempioSpecificaSemiformaleInterno.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"AggiornamentoRequest.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"AggiornamentoResponse.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"Dati.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsempioAllegatoInclude1.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsempioAllegatoInclude2.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsempioSpecificaSemiformaleInclude1.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsempioSpecificaSemiformaleInclude2.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"Esito.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"IdentificativoRichiestaAsincrona.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"NotificaRequest.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"PresaInCarico.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaAggiornamentoRequest.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaAggiornamentoResponse.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaRegistrazioneRequest.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaRegistrazioneResponse.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaStatoRegistrazioneRequest.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaStatoRegistrazioneResponse.java"
	};                      

	
	public static String [] TEST1_FILE_PATH_LOGICO_FRUITORE = {
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsitoAggiornamentoRequest.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsitoAggiornamentoResponse.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"Esito.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"PresaInCarico.java"};			
	private static Vector<String> TEST1_FILE_PATH_LOGICO_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST1_FILE_PATH_LOGICO_EROGATORE.length; i++){
			Costanti.TEST1_FILE_PATH_LOGICO_VECTOR.add(Costanti.TEST1_FILE_PATH_LOGICO_EROGATORE[i]);
		}
		for(int i=0; i<Costanti.TEST1_FILE_PATH_LOGICO_FRUITORE.length; i++){
			Costanti.TEST1_FILE_PATH_LOGICO_VECTOR.add(Costanti.TEST1_FILE_PATH_LOGICO_FRUITORE[i]);
		}
	}
	public static String [] TEST1_FILE_PATH_LOGICO = Costanti.TEST1_FILE_PATH_LOGICO_VECTOR.toArray(new String[1]);
		
	private static Vector<String> TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST1_PORT_TYPES_EROGATORE.length; i++){
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add("build.xml");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPES_EROGATORE[i]+"Service.java");
			if("Oneway".equals(Costanti.TEST1_PORT_TYPES_EROGATORE[i])){
				Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPES_EROGATORE[i]+"_NotificaPort_Client.java");
				Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPES_EROGATORE[i]+"_NotificaPort_Server.java");
			}
			else{
				Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPES_EROGATORE[i]+"_"+Costanti.TEST1_PORT_TYPES_EROGATORE[i]+"Port_Client.java");
				Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPES_EROGATORE[i]+"_"+Costanti.TEST1_PORT_TYPES_EROGATORE[i]+"Port_Server.java");
			}
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPES_EROGATORE[i]+".java");
		}
	}
	public static String [] TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.toArray(new String[1]);
	private static Vector<String> TEST1_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST1_PORT_TYPES_FRUITORE.length; i++){
			Costanti.TEST1_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add("build.xml");
			Costanti.TEST1_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPES_FRUITORE[i]+"Service.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPES_FRUITORE[i]+"_"+Costanti.TEST1_PORT_TYPES_FRUITORE[i]+"Port_Client.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPES_FRUITORE[i]+"_"+Costanti.TEST1_PORT_TYPES_FRUITORE[i]+"Port_Server.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPES_FRUITORE[i]+".java");
		}
	}
	public static String [] TEST1_FILE_PATH_IMPLEMENTATIVO_FRUITORE = Costanti.TEST1_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.toArray(new String[1]);
	private static Vector<String> TEST1_FILE_PATH_IMPLEMENTATIVO_VECTOR = new Vector<String>();
	static{
		Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_VECTOR.addAll(Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR);
		Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_VECTOR.addAll(Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR);
	}
	public static String [] TEST1_FILE_PATH_IMPLEMENTATIVO = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_VECTOR.toArray(new String[1]);
	
	public static String [] TEST1_SCHEMI_DEFINITORIO = {};
	public static String [] TEST1_SCHEMI_ALLEGATI = {
		"allegatoImportFromAllegato.xsd",
		"allegatoImportFromDefinitorio2.xsd",
		"allegatoImportFromDefinitorio.xsd",
		"allegatoImportFromWSDL.xsd",
		"allegatoIncludeFromDefinitorio.xsd"
	};
	public static String [] TEST1_SCHEMI_SPECIFICHE_SEMIFORMALI = {
		"specificaSemiformaleImportFromDefinitorio.xsd",
		"specificaSemiformaleImportFromSpecificaSemiformale.xsd",
		"specificaSemiformaleImportFromWSDL.xsd",
		"specificaSemiformaleIncludeFromDefinitorio.xsd"
	};
	private static Vector<String> TEST1_SCHEMI_VECTOR = new Vector<String>();
	static{
		if(Costanti.TEST1_SCHEMI_DEFINITORIO!=null && Costanti.TEST1_SCHEMI_DEFINITORIO.length>0){
			for(int i=0;i<Costanti.TEST1_SCHEMI_DEFINITORIO.length;i++){
				Costanti.TEST1_SCHEMI_VECTOR.add(Costanti.TEST1_SCHEMI_DEFINITORIO[i]);
			}
		}
		if(Costanti.TEST1_SCHEMI_ALLEGATI!=null && Costanti.TEST1_SCHEMI_ALLEGATI.length>0){
			for(int i=0;i<Costanti.TEST1_SCHEMI_ALLEGATI.length;i++){
				Costanti.TEST1_SCHEMI_VECTOR.add(Costanti.TEST1_SCHEMI_ALLEGATI[i]);
			}
		}
		if(Costanti.TEST1_SCHEMI_SPECIFICHE_SEMIFORMALI!=null && Costanti.TEST1_SCHEMI_SPECIFICHE_SEMIFORMALI.length>0){
			for(int i=0;i<Costanti.TEST1_SCHEMI_SPECIFICHE_SEMIFORMALI.length;i++){
				Costanti.TEST1_SCHEMI_VECTOR.add(Costanti.TEST1_SCHEMI_SPECIFICHE_SEMIFORMALI[i]);
			}
		}
	}
	public static String [] TEST1_SCHEMI = Costanti.TEST1_SCHEMI_VECTOR.toArray(new String[1]);
	
	
	public static String [] TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO = {"AsincronoAsimmetrico"};
	public static String [] TEST1_OPERATIONS_ASINCRONO_ASIMMETRICO = {"richiestaRegistrazione,richiestaStatoRegistrazione"};
	public static String [] TEST1_FILE_PATH_LOGICO_ASINCRONO_ASIMMETRICO = {
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"Dati.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"Esito.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"IdentificativoRichiestaAsincrona.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"PresaInCarico.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaRegistrazioneRequest.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaRegistrazioneResponse.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaStatoRegistrazioneRequest.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaStatoRegistrazioneResponse.java"
	};
	private static Vector<String> TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO.length; i++){
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO[i]+"BindingImpl.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO[i]+"BindingSkeleton.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO[i]+"BindingStub.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO[i]+"Service.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO[i]+"ServiceLocator.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO[i]+".java");
		}
	}
	public static String [] TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO_VECTOR.toArray(new String[1]);
	
	
	public static String [] TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA = {"AsincronoSimmetricoRichiesta"};
	public static String [] TEST1_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA = {"richiestaAggiornamento"};
	public static String [] TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA = {"AsincronoSimmetricoRisposta"};
	public static String [] TEST1_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA = {"esitoAggiornamento"};
	public static String [] TEST1_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RICHIESTA = {
		Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"Dati.java",
		Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"PresaInCarico.java",
		Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaAggiornamentoRequest.java",
		Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"RichiestaAggiornamentoResponse.java",
	};
	private static Vector<String> TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA.length; i++){
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA[i]+"BindingImpl.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA[i]+"BindingSkeleton.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA[i]+"BindingStub.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA[i]+"Service.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA[i]+"ServiceLocator.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA[i]+".java");
		}
	}
	public static String [] TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA_VECTOR.toArray(new String[1]);
	public static String [] TEST1_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RISPOSTA = {
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"Esito.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"PresaInCarico.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsitoAggiornamentoRequest.java",
			Costanti.TEST1_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsitoAggiornamentoResponse.java",
	};
	private static Vector<String> TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA.length; i++){
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA[i]+"BindingImpl.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA[i]+"BindingSkeleton.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA[i]+"BindingStub.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA[i]+"Service.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA[i]+"ServiceLocator.java");
			Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA_VECTOR.add(Costanti.TEST1_FILE_PATH_NAMESPACE+Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA[i]+".java");
		}
	}
	public static String [] TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA_VECTOR.toArray(new String[1]);
	
	
	
	
	
	/* ****************** TEST 2 ************************* */
	
	public static File TEST2_BASE_DIR = new File("test_files"+File.separatorChar+"test2");
	public static File TEST2_WSDL_FILE = new File(Costanti.TEST2_BASE_DIR,"wsdl"+File.separatorChar+"TestSuite.wsdl");
	public static String TEST2_WSDL_FILE_PATH = Costanti.TEST2_WSDL_FILE.getAbsolutePath();
	
	public static String [] TEST2_SCHEMI_DEFINITORIO = {"InterfacciaDefinitoria.xsd"};
	public static String [] TEST2_SCHEMI_ALLEGATI = {
		"types.xsd",
		"allegatoImportFromAllegato.xsd",
		"allegatoImportFromDefinitorio2.xsd",
		"allegatoImportFromDefinitorio.xsd",
		"allegatoImportFromWSDL.xsd",
		"allegatoIncludeFromDefinitorio.xsd"
	};
	public static String [] TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI = {
		"specificaSemiformaleImportFromDefinitorio.xsd",
		"specificaSemiformaleImportFromSpecificaSemiformale.xsd",
		"specificaSemiformaleImportFromWSDL.xsd",
		"specificaSemiformaleIncludeFromDefinitorio.xsd"
	};
	private static Vector<String> TEST2_SCHEMI_VECTOR = new Vector<String>();
	static{
		if(Costanti.TEST2_SCHEMI_DEFINITORIO!=null && Costanti.TEST2_SCHEMI_DEFINITORIO.length>0){
			for(int i=0;i<Costanti.TEST2_SCHEMI_DEFINITORIO.length;i++){
				Costanti.TEST2_SCHEMI_VECTOR.add(Costanti.TEST2_SCHEMI_DEFINITORIO[i]);
			}
		}
		if(Costanti.TEST2_SCHEMI_ALLEGATI!=null && Costanti.TEST2_SCHEMI_ALLEGATI.length>0){
			for(int i=0;i<Costanti.TEST2_SCHEMI_ALLEGATI.length;i++){
				Costanti.TEST2_SCHEMI_VECTOR.add(Costanti.TEST2_SCHEMI_ALLEGATI[i]);
			}
		}
		if(Costanti.TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI!=null && Costanti.TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI.length>0){
			for(int i=0;i<Costanti.TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI.length;i++){
				Costanti.TEST2_SCHEMI_VECTOR.add(Costanti.TEST2_SCHEMI_SPECIFICHE_SEMIFORMALI[i]);
			}
		}
	}
	public static String [] TEST2_SCHEMI = Costanti.TEST2_SCHEMI_VECTOR.toArray(new String[1]);
	
	public static String [] TEST2_PORT_TYPES_EROGATORE = Costanti.TEST1_PORT_TYPES_EROGATORE;
	public static String [] TEST2_OPERATIONS_EROGATORE = Costanti.TEST1_OPERATIONS_EROGATORE;
	
	public static String [] TEST2_PORT_TYPES_FRUITORE = Costanti.TEST1_PORT_TYPES_FRUITORE;
	public static String [] TEST2_OPERATIONS_FRUITORE = Costanti.TEST1_OPERATIONS_FRUITORE;
	
	public static String [] TEST2_PORT_TYPES = Costanti.TEST1_PORT_TYPES;
	public static String [] TEST2_OPERATIONS = Costanti.TEST1_OPERATIONS;	
	
	public static String [] TEST2_FILE_PATH_LOGICO_EROGATORE = Costanti.TEST1_FILE_PATH_LOGICO_EROGATORE;
	public static String [] TEST2_FILE_PATH_LOGICO_FRUITORE = Costanti.TEST1_FILE_PATH_LOGICO_FRUITORE;
	public static String [] TEST2_FILE_PATH_LOGICO = Costanti.TEST1_FILE_PATH_LOGICO;
	
	public static String [] TEST2_FILE_PATH_IMPLEMENTATIVO_EROGATORE = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE;
	public static String [] TEST2_FILE_PATH_IMPLEMENTATIVO_FRUITORE = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_FRUITORE;
	public static String [] TEST2_FILE_PATH_IMPLEMENTATIVO = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO;
	
	public static String [] TEST2_PORT_TYPE_ASINCRONO_ASIMMETRICO = Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO;
	public static String [] TEST2_OPERATIONS_ASINCRONO_ASIMMETRICO = Costanti.TEST1_OPERATIONS_ASINCRONO_ASIMMETRICO;
	public static String [] TEST2_FILE_PATH_LOGICO_ASINCRONO_ASIMMETRICO = Costanti.TEST1_FILE_PATH_LOGICO_ASINCRONO_ASIMMETRICO;
	public static String [] TEST2_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO;
	
	public static String [] TEST2_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA = Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA;
	public static String [] TEST2_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA = Costanti.TEST1_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA;
	public static String [] TEST2_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA = Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA;
	public static String [] TEST2_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA = Costanti.TEST1_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA;
	public static String [] TEST2_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RICHIESTA = Costanti.TEST1_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RICHIESTA;
	public static String [] TEST2_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA;
	public static String [] TEST2_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RISPOSTA = Costanti.TEST1_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RISPOSTA;
	public static String [] TEST2_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA;
	
	
	
	
	
	/* ****************** TEST 3 ************************* */
	
	public static File TEST3_BASE_DIR = new File("test_files"+File.separatorChar+"test3");
	public static File TEST3_WSDL_FILE_IMPLEMENTATIVO_EROGATORE = new File(Costanti.TEST3_BASE_DIR,"specificaPortiAccesso"+File.separatorChar+"PortiAccessoErogatore.wsdl");
	public static String TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE = Costanti.TEST3_WSDL_FILE_IMPLEMENTATIVO_EROGATORE.getAbsolutePath();
	public static File TEST3_WSDL_FILE_IMPLEMENTATIVO_FRUITORE = new File(Costanti.TEST3_BASE_DIR,"specificaPortiAccesso"+File.separatorChar+"PortiAccessoFruitore.wsdl");
	public static String TEST3_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE = Costanti.TEST3_WSDL_FILE_IMPLEMENTATIVO_FRUITORE.getAbsolutePath();
	public static File TEST3_WSDL_FILE_LOGICO_EROGATORE = new File(Costanti.TEST3_BASE_DIR,"specificaInterfaccia"+File.separatorChar+"InterfacciaLogicaErogatore.wsdl");
	public static String TEST3_WSDL_FILE_PATH_LOGICO_EROGATORE = Costanti.TEST3_WSDL_FILE_LOGICO_EROGATORE.getAbsolutePath();
	public static File TEST3_WSDL_FILE_LOGICO_FRUITORE = new File(Costanti.TEST3_BASE_DIR,"specificaInterfaccia"+File.separatorChar+"InterfacciaLogicaFruitore.wsdl");
	public static String TEST3_WSDL_FILE_PATH_LOGICO_FRUITORE = Costanti.TEST3_WSDL_FILE_LOGICO_FRUITORE.getAbsolutePath();
	public static String TEST3_WSDL_NAME_IMPORT_WSDL_TEST = "import.wsdl";
	public static File TEST3_WSDL_FILE_IMPORT_WSDL_TEST = new File(Costanti.TEST3_BASE_DIR,"specificaInterfaccia"+File.separatorChar+Costanti.TEST3_WSDL_NAME_IMPORT_WSDL_TEST);
	public static String TEST3_WSDL_FILE_PATH_IMPORT_WSDL_TEST = Costanti.TEST3_WSDL_FILE_IMPORT_WSDL_TEST.getAbsolutePath();
	
	public static String [] TEST3_PORT_TYPES_EROGATORE = {"Sincrono","Oneway"};
	public static String [] TEST3_OPERATIONS_EROGATORE = {"echo","notifica,notifica2"};
	public static String [] TEST3_PORT_TYPES_FRUITORE = {"OnewayFruitore"};
	public static String [] TEST3_OPERATIONS_FRUITORE = {"notificaFruitore"};
	public static String [] TEST3_PORT_TYPES = {Costanti.TEST3_PORT_TYPES_EROGATORE[0],Costanti.TEST3_PORT_TYPES_EROGATORE[1],Costanti.TEST3_PORT_TYPES_FRUITORE[0]};
	public static String [] TEST3_OPERATIONS = {Costanti.TEST3_OPERATIONS_EROGATORE[0],Costanti.TEST3_OPERATIONS_EROGATORE[1],Costanti.TEST3_OPERATIONS_FRUITORE[0]};	
	
	private static String TEST3_FILE_PATH_NAMESPACE = "org"+File.separatorChar+"openspcoop"+File.separatorChar+"www"+File.separatorChar+"example"+File.separatorChar;
	private static String TEST3_FILE_PATH_NAMESPACE_EXAMPLE2 = "org"+File.separatorChar+"openspcoop"+File.separatorChar+"www"+File.separatorChar+"example2"+File.separatorChar;
	private static String TEST3_FILE_PATH_NAMESPACE_EXAMPLE3 = "org"+File.separatorChar+"openspcoop"+File.separatorChar+"www"+File.separatorChar+"example3"+File.separatorChar;
	public static String [] TEST3_FILE_PATH_LOGICO_EROGATORE = {
			Costanti.TEST3_FILE_PATH_NAMESPACE+"NotificaRequestIncluso.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"allegato"+File.separatorChar+"_import"+File.separatorChar+"EsempioAllegatoImport1.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"allegato"+File.separatorChar+"_import"+File.separatorChar+"EsempioAllegatoImport2.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"NotificaRequestImportato.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"NotificaRequest.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsempioAllegatoInclude1.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsempioAllegatoInclude2.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE_EXAMPLE3+"NotificaRequest2.java"
	};
	public static String [] TEST3_FILE_PATH_LOGICO_FRUITORE = {
			Costanti.TEST3_FILE_PATH_NAMESPACE+"NotificaRequestIncluso.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"allegato"+File.separatorChar+"_import"+File.separatorChar+"EsempioAllegatoImport1.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"allegato"+File.separatorChar+"_import"+File.separatorChar+"EsempioAllegatoImport2.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"NotificaRequestImportato.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"NotificaRequest.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsempioAllegatoInclude1.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"EsempioAllegatoInclude2.java",
			Costanti.TEST3_FILE_PATH_NAMESPACE_EXAMPLE2+"NotificaRequest2.java"};			
	
	private static Vector<String> TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST3_PORT_TYPES_EROGATORE.length; i++){
			Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_EROGATORE[i]+"BindingImpl.java");
			Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_EROGATORE[i]+"BindingSkeleton.java");
			Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_EROGATORE[i]+"BindingStub.java");
			Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_EROGATORE[i]+"Service.java");
			Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_EROGATORE[i]+"ServiceLocator.java");
			Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_EROGATORE[i]+".java");
		}
	}
	public static String [] TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE = Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR.toArray(new String[1]);
	private static Vector<String> TEST3_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST3_PORT_TYPES_FRUITORE.length; i++){
			Costanti.TEST3_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_FRUITORE[i]+"BindingImpl.java");
			Costanti.TEST3_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_FRUITORE[i]+"BindingSkeleton.java");
			Costanti.TEST3_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_FRUITORE[i]+"BindingStub.java");
			Costanti.TEST3_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_FRUITORE[i]+"Service.java");
			Costanti.TEST3_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_FRUITORE[i]+"ServiceLocator.java");
			Costanti.TEST3_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.add(Costanti.TEST3_FILE_PATH_NAMESPACE+Costanti.TEST3_PORT_TYPES_FRUITORE[i]+".java");
		}
	}
	public static String [] TEST3_FILE_PATH_IMPLEMENTATIVO_FRUITORE = Costanti.TEST3_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR.toArray(new String[1]);
	
	private static Vector<String> TEST3_FILE_PATH_LOGICO_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE.length; i++)
			Costanti.TEST3_FILE_PATH_LOGICO_VECTOR.add(Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE[i]);
		for(int i=0; i<Costanti.TEST3_FILE_PATH_LOGICO_FRUITORE.length; i++)
			Costanti.TEST3_FILE_PATH_LOGICO_VECTOR.add(Costanti.TEST3_FILE_PATH_LOGICO_FRUITORE[i]);
	}
	public static String [] TEST3_FILE_PATH_LOGICO = Costanti.TEST3_FILE_PATH_LOGICO_VECTOR.toArray(new String[1]);
	private static Vector<String> TEST3_FILE_PATH_IMPLEMENTATIVO_VECTOR = new Vector<String>();
	static{
		Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_VECTOR.addAll(Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE_VECTOR);
		Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_VECTOR.addAll(Costanti.TEST3_FILE_PATH_IMPLEMENTIVO_FRUITORE_VECTOR);
	}
	public static String [] TEST3_FILE_PATH_IMPLEMENTATIVO = Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_VECTOR.toArray(new String[1]);
	
	
	
	/* ****************** TEST 4 ************************* */
	public static File TEST4_BASE_DIR = new File("test_files"+File.separatorChar+"test4");
	
	public static File TEST4_WSDL_FILE_IMPLEMENTATIVO_EROGATORE = new File(Costanti.TEST4_BASE_DIR,"specificaPortiAccesso"+File.separatorChar+"PortiAccessoErogatore.wsdl");
	public static String TEST4_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE = Costanti.TEST4_WSDL_FILE_IMPLEMENTATIVO_EROGATORE.getAbsolutePath();
	public static byte[] TEST4_WSDL_BYTE_IMPLEMENTATIVO_EROGATORE = null;
	static{
		try{
			Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_EROGATORE = FileSystemUtilities.readBytesFromFile(Costanti.TEST4_WSDL_FILE_PATH_IMPLEMENTATIVO_EROGATORE);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static File TEST4_WSDL_FILE_IMPLEMENTATIVO_FRUITORE = new File(Costanti.TEST4_BASE_DIR,"specificaPortiAccesso"+File.separatorChar+"PortiAccessoFruitore.wsdl");
	public static String TEST4_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE = Costanti.TEST4_WSDL_FILE_IMPLEMENTATIVO_FRUITORE.getAbsolutePath();
	public static byte[] TEST4_WSDL_BYTE_IMPLEMENTATIVO_FRUITORE = null;
	static{
		try{
			Costanti.TEST4_WSDL_BYTE_IMPLEMENTATIVO_FRUITORE = FileSystemUtilities.readBytesFromFile(Costanti.TEST4_WSDL_FILE_PATH_IMPLEMENTATIVO_FRUITORE);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static File TEST4_WSDL_FILE_LOGICO_EROGATORE = new File(Costanti.TEST4_BASE_DIR,"specificaInterfaccia"+File.separatorChar+"InterfacciaLogicaErogatore.wsdl");
	public static String TEST4_WSDL_FILE_PATH_LOGICO_EROGATORE = Costanti.TEST4_WSDL_FILE_LOGICO_EROGATORE.getAbsolutePath();
	public static byte[] TEST4_WSDL_BYTE_LOGICO_EROGATORE = null;
	static{
		try{
			Costanti.TEST4_WSDL_BYTE_LOGICO_EROGATORE = FileSystemUtilities.readBytesFromFile(Costanti.TEST4_WSDL_FILE_PATH_LOGICO_EROGATORE);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static File TEST4_WSDL_FILE_LOGICO_FRUITORE = new File(Costanti.TEST4_BASE_DIR,"specificaInterfaccia"+File.separatorChar+"InterfacciaLogicaFruitore.wsdl");
	public static String TEST4_WSDL_FILE_PATH_LOGICO_FRUITORE = Costanti.TEST4_WSDL_FILE_LOGICO_FRUITORE.getAbsolutePath();
	public static byte[] TEST4_WSDL_BYTE_LOGICO_FRUITORE = null;
	static{
		try{
			Costanti.TEST4_WSDL_BYTE_LOGICO_FRUITORE = FileSystemUtilities.readBytesFromFile(Costanti.TEST4_WSDL_FILE_PATH_LOGICO_FRUITORE);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static String TEST4_WSDL_NAME_IMPORT_WSDL_TEST = "import.wsdl";
	public static File TEST4_WSDL_FILE_IMPORT_WSDL_TEST = new File(Costanti.TEST4_BASE_DIR,"specificaInterfaccia"+File.separatorChar+Costanti.TEST4_WSDL_NAME_IMPORT_WSDL_TEST);
	public static String TEST4_WSDL_FILE_PATH_IMPORT_WSDL_TEST = Costanti.TEST4_WSDL_FILE_IMPORT_WSDL_TEST.getAbsolutePath();
	
	public static File TEST4_ALLEGATI_DIR = new File(Costanti.TEST4_BASE_DIR,"allegati");
	public static String [] TEST4_SCHEMI_XSD = {
			Costanti.TEST4_ALLEGATI_DIR.getAbsolutePath()+File.separatorChar+"allegatoImportFromDefinitorio.xsd",
			Costanti.TEST4_ALLEGATI_DIR.getAbsolutePath()+File.separatorChar+"allegatoIncludeFromDefinitorio.xsd",
			Costanti.TEST4_ALLEGATI_DIR.getAbsolutePath()+File.separatorChar+"InterfacciaDefinitoria_0.xsd",
			Costanti.TEST4_ALLEGATI_DIR.getAbsolutePath()+File.separatorChar+"InterfacciaDefinitoria_1.xsd",
			Costanti.TEST4_ALLEGATI_DIR.getAbsolutePath()+File.separatorChar+"InterfacciaDefinitoria.xsd",
			Costanti.TEST4_ALLEGATI_DIR.getAbsolutePath()+File.separatorChar+"SchemaDatiImportato.xsd",
			Costanti.TEST4_ALLEGATI_DIR.getAbsolutePath()+File.separatorChar+"SchemaDatiIncluso.xsd"
	};
	public static File [] TEST4_SCHEMI_XSD_FILES = null;
	static{
		Costanti.TEST4_SCHEMI_XSD_FILES = new File[Costanti.TEST4_SCHEMI_XSD.length];
		for(int i=0;i<Costanti.TEST4_SCHEMI_XSD.length;i++){
			Costanti.TEST4_SCHEMI_XSD_FILES[i] = new File(Costanti.TEST4_SCHEMI_XSD[i]);
		}
	}
	public static List<SchemaXSDAccordoServizio> TEST4_SCHEMI_XSD_LIST = new ArrayList<SchemaXSDAccordoServizio>();
	static{
		for(int i=0;i<Costanti.TEST4_SCHEMI_XSD.length;i++){
			try{
				SchemaXSDAccordoServizio schemaXSD = 
					new SchemaXSDAccordoServizio(FileSystemUtilities.readBytesFromFile(Costanti.TEST4_SCHEMI_XSD_FILES[i]),
							Costanti.TEST4_SCHEMI_XSD_FILES[i].getName(), TipoSchemaXSDAccordoServizio.ALLEGATO);
				Costanti.TEST4_SCHEMI_XSD_LIST.add(schemaXSD);
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	public static String [] TEST4_PORT_TYPES_EROGATORE = Costanti.TEST3_PORT_TYPES_EROGATORE;
	public static String [] TEST4_OPERATIONS_EROGATORE = Costanti.TEST3_OPERATIONS_EROGATORE;
	public static String [] TEST4_PORT_TYPES_FRUITORE = Costanti.TEST3_PORT_TYPES_FRUITORE;
	public static String [] TEST4_OPERATIONS_FRUITORE = Costanti.TEST3_OPERATIONS_FRUITORE;
	public static String [] TEST4_PORT_TYPES = Costanti.TEST3_PORT_TYPES;
	public static String [] TEST4_OPERATIONS = Costanti.TEST3_OPERATIONS;	
	
	public static String [] TEST4_FILE_PATH_LOGICO_EROGATORE = Costanti.TEST3_FILE_PATH_LOGICO_EROGATORE;
	public static String [] TEST4_FILE_PATH_LOGICO_FRUITORE = Costanti.TEST3_FILE_PATH_LOGICO_FRUITORE;			

	public static String [] TEST4_FILE_PATH_IMPLEMENTATIVO_EROGATORE = Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_EROGATORE;
	public static String [] TEST4_FILE_PATH_IMPLEMENTATIVO_FRUITORE = Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO_FRUITORE;

	public static String [] TEST4_FILE_PATH_LOGICO = Costanti.TEST3_FILE_PATH_LOGICO;
	public static String [] TEST4_FILE_PATH_IMPLEMENTATIVO = Costanti.TEST3_FILE_PATH_IMPLEMENTATIVO;
	
	
	
	
	
	
	
	/* ****************** TEST 5 ************************* */
	
	public static File TEST5_BASE_DIR = new File("test_files"+File.separatorChar+"test5");
	public static File TEST5_WSDL_FILE = new File(Costanti.TEST5_BASE_DIR,"wsdl"+File.separatorChar+"Testsuite.wsdl");
	public static String TEST5_WSDL_FILE_PATH = Costanti.TEST5_WSDL_FILE.getAbsolutePath();
	
	public static String [] TEST5_SCHEMI_TYPES = {
		"types.xsd",
		"types_0.xsd",
		"types_1.xsd",
		"types_2.xsd",
		"types_3.xsd",
		"types_4.xsd",
		"types_5.xsd",
		"types_6.xsd",
		"types_7.xsd"
	};
	
	public static String [] TEST5_PORT_TYPES_EROGATORE = Costanti.TEST1_PORT_TYPES_EROGATORE;
	public static String [] TEST5_OPERATIONS_EROGATORE = Costanti.TEST1_OPERATIONS_EROGATORE;
	
	public static String [] TEST5_PORT_TYPES_FRUITORE = Costanti.TEST1_PORT_TYPES_FRUITORE;
	public static String [] TEST5_OPERATIONS_FRUITORE = Costanti.TEST1_OPERATIONS_FRUITORE;
	
	public static String [] TEST5_PORT_TYPES = Costanti.TEST1_PORT_TYPES;
	public static String [] TEST5_OPERATIONS = Costanti.TEST1_OPERATIONS;	
	
	public static String [] TEST5_FILE_PATH_LOGICO_EROGATORE = Costanti.TEST1_FILE_PATH_LOGICO_EROGATORE;
	public static String [] TEST5_FILE_PATH_LOGICO_FRUITORE = Costanti.TEST1_FILE_PATH_LOGICO_FRUITORE;
	public static String [] TEST5_FILE_PATH_LOGICO = Costanti.TEST1_FILE_PATH_LOGICO;
	
	public static String [] TEST5_FILE_PATH_IMPLEMENTATIVO_EROGATORE = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_EROGATORE;
	public static String [] TEST5_FILE_PATH_IMPLEMENTATIVO_FRUITORE = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_FRUITORE;
	public static String [] TEST5_FILE_PATH_IMPLEMENTATIVO = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO;
	
	public static String [] TEST5_PORT_TYPE_ASINCRONO_ASIMMETRICO = Costanti.TEST1_PORT_TYPE_ASINCRONO_ASIMMETRICO;
	public static String [] TEST5_OPERATIONS_ASINCRONO_ASIMMETRICO = Costanti.TEST1_OPERATIONS_ASINCRONO_ASIMMETRICO;
	public static String [] TEST5_FILE_PATH_LOGICO_ASINCRONO_ASIMMETRICO = Costanti.TEST1_FILE_PATH_LOGICO_ASINCRONO_ASIMMETRICO;
	public static String [] TEST5_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_ASIMMETRICO;
	
	public static String [] TEST5_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA = Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RICHIESTA;
	public static String [] TEST5_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA = Costanti.TEST1_OPERATION_ASINCRONO_SIMMETRICO_RICHIESTA;
	public static String [] TEST5_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA = Costanti.TEST1_PORT_TYPE_ASINCRONO_SIMMETRICO_RISPOSTA;
	public static String [] TEST5_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA = Costanti.TEST1_OPERATION_ASINCRONO_SIMMETRICO_RISPOSTA;
	public static String [] TEST5_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RICHIESTA = Costanti.TEST1_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RICHIESTA;
	public static String [] TEST5_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RICHIESTA;
	public static String [] TEST5_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RISPOSTA = Costanti.TEST1_FILE_PATH_LOGICO_ASINCRONO_SIMMETRICO_RISPOSTA;
	public static String [] TEST5_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA = Costanti.TEST1_FILE_PATH_IMPLEMENTATIVO_ASINCRONO_SIMMETRICO_RISPOSTA;
	
	
	
	
	
	
	
	
	
	
	
	
	/* ****************** TEST 6 ************************* */
	
	public static File TEST6_BASE_DIR = new File("test_files"+File.separatorChar+"test6");
	public static File TEST6_WSDL_FILE = new File(Costanti.TEST6_BASE_DIR,"Testsuite.wsdl");
	public static String TEST6_WSDL_FILE_PATH = Costanti.TEST6_WSDL_FILE.getAbsolutePath();
	
	public static String [] TEST6_SCHEMI= {};
	
	public static String [] TEST6_PORT_TYPES = {"IntegrationManager"};
	
	public static String [] TEST6_OPERATIONS = {"getMessage"};
	
	private static String TEST6_FILE_PATH_NAMESPACE = "org"+File.separatorChar+"openspcoop"+File.separatorChar+"www"+File.separatorChar+"example"+File.separatorChar;
	
	public static String [] TEST6_FILE_PATH_LOGICO = {
		Costanti.TEST6_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"SPCoopException.java",
		Costanti.TEST6_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"SPCoopHeaderInfo.java",
		Costanti.TEST6_FILE_PATH_NAMESPACE+"types"+File.separatorChar+"SPCoopMessage.java",
		Costanti.TEST6_FILE_PATH_NAMESPACE+"wrapper"+File.separatorChar+"GetMessageException.java",
		//TEST6_FILE_PATH_NAMESPACE+"wrapper"+File.separatorChar+"GetMessage.java",
		//TEST6_FILE_PATH_NAMESPACE+"wrapper"+File.separatorChar+"GetMessageResponse.java"
	};
	
	private static Vector<String> TEST6_FILE_PATH_IMPLEMENTIVO_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST6_PORT_TYPES.length; i++){
			Costanti.TEST6_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST6_FILE_PATH_NAMESPACE+"impl"+File.separatorChar+Costanti.TEST6_PORT_TYPES[i]+"BindingImpl.java");
			Costanti.TEST6_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST6_FILE_PATH_NAMESPACE+"impl"+File.separatorChar+Costanti.TEST6_PORT_TYPES[i]+"BindingSkeleton.java");
			Costanti.TEST6_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST6_FILE_PATH_NAMESPACE+"impl"+File.separatorChar+Costanti.TEST6_PORT_TYPES[i]+"BindingStub.java");
			Costanti.TEST6_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST6_FILE_PATH_NAMESPACE+"impl"+File.separatorChar+Costanti.TEST6_PORT_TYPES[i]+"Service.java");
			Costanti.TEST6_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST6_FILE_PATH_NAMESPACE+"impl"+File.separatorChar+Costanti.TEST6_PORT_TYPES[i]+"ServiceLocator.java");
			Costanti.TEST6_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST6_FILE_PATH_NAMESPACE+"impl"+File.separatorChar+Costanti.TEST6_PORT_TYPES[i]+".java");
		}
	}
	public static String [] TEST6_FILE_PATH_IMPLEMENTATIVO = Costanti.TEST6_FILE_PATH_IMPLEMENTIVO_VECTOR.toArray(new String[1]);
	
	
	
	
	
	
	
	
	
	
	/* ****************** TEST 7 ************************* */
	
	public static String TEST7_WSDL_URL = "http://127.0.0.1:8080/openspcoop/IntegrationManager?wsdl";	
	
	public static String [] TEST7_SCHEMI= {"InterfacciaDefinitoria.xsd"};
	
	public static String [] TEST7_PORT_TYPES = {"IntegrationManager"};
	
	public static String [] TEST7_OPERATIONS = {"getMessage,"+ 
			"deleteMessage,getAllMessagesId,getAllMessagesIdByService," +
			"getNextMessagesId,getNextMessagesIdByService,getMessagesIdArray," +
			"getMessagesIdArrayByService,getMessageByReference,deleteMessageByReference," +
			"deleteAllMessages,invocaPortaDelegata,invocaPortaDelegataPerRiferimento," +
			"sendRispostaAsincronaSimmetrica,sendRichiestaStatoAsincronaAsimmetrica"};
	
	private static String TEST7_FILE_PATH_NAMESPACE = "org"+File.separatorChar+"openspcoop"+File.separatorChar+"pdd"+File.separatorChar+"services"+File.separatorChar;
	
	public static String [] TEST7_FILE_PATH_LOGICO = {
		Costanti.TEST7_FILE_PATH_NAMESPACE+"DeleteAllMessages.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"DeleteAllMessagesResponse.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"DeleteMessageByReference.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"DeleteMessageByReferenceResponse.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"DeleteMessage.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"DeleteMessageResponse.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"GetAllMessagesIdByService.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"GetAllMessagesId.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"GetMessageByReference.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"GetMessageByReferenceResponse.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"GetMessage.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"GetMessageResponse.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"GetMessagesIdArrayByService.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"GetMessagesIdArray.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"GetNextMessagesIdByService.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"GetNextMessagesId.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"InvocaPortaDelegata.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"InvocaPortaDelegataPerRiferimento.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"InvocaPortaDelegataPerRiferimentoResponse.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"InvocaPortaDelegataResponse.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"SendRichiestaStatoAsincronaAsimmetrica.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"SendRichiestaStatoAsincronaAsimmetricaResponse.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"SendRispostaAsincronaSimmetrica.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"SendRispostaAsincronaSimmetricaResponse.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"SPCoopException.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"SPCoopHeaderInfo.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"SPCoopMessage.java"
	};
	
	public static String [] TEST7_FILE_PATH_LOGICO_EROGATORE = {
		Costanti.TEST7_FILE_PATH_NAMESPACE+"SPCoopException.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"SPCoopHeaderInfo.java",
		Costanti.TEST7_FILE_PATH_NAMESPACE+"SPCoopMessage.java"
	};
	
	private static Vector<String> TEST7_FILE_PATH_IMPLEMENTIVO_VECTOR = new Vector<String>();
	static{
		for(int i=0; i<Costanti.TEST7_PORT_TYPES.length; i++){
			Costanti.TEST7_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST7_FILE_PATH_NAMESPACE+Costanti.TEST7_PORT_TYPES[i]+"SoapBindingImpl.java");
			Costanti.TEST7_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST7_FILE_PATH_NAMESPACE+Costanti.TEST7_PORT_TYPES[i]+"SoapBindingSkeleton.java");
			Costanti.TEST7_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST7_FILE_PATH_NAMESPACE+Costanti.TEST7_PORT_TYPES[i]+"SoapBindingStub.java");
			Costanti.TEST7_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST7_FILE_PATH_NAMESPACE+Costanti.TEST7_PORT_TYPES[i]+"Service.java");
			Costanti.TEST7_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST7_FILE_PATH_NAMESPACE+Costanti.TEST7_PORT_TYPES[i]+"ServiceLocator.java");
			Costanti.TEST7_FILE_PATH_IMPLEMENTIVO_VECTOR.add(Costanti.TEST7_FILE_PATH_NAMESPACE+Costanti.TEST7_PORT_TYPES[i]+".java");
		}
	}
	public static String [] TEST7_FILE_PATH_IMPLEMENTATIVO = Costanti.TEST7_FILE_PATH_IMPLEMENTIVO_VECTOR.toArray(new String[1]);
}

