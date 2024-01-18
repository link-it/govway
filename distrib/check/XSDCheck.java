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

import java.io.*;

/**
 * XSDCheck
 *
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class XSDCheck {

	public static javax.xml.validation.Schema schemaConfigurazione;
	public static javax.xml.validation.Schema schemaRegistro;

	public static java.util.List<String> fileNonValidi = new java.util.ArrayList<>();
	public static java.util.List<String> dichiarazioneAssente = new java.util.ArrayList<>();

	// codice di uscita:
	// -1 invocazione non valida
	// 1 Errore generale
	// 2 file non validi per dichiarazione GPL
	public static void main(String[] args) {
		try {

			if(args.length < 2){
				System.out.println("Error usage: java GPLWriter directory sorgenteSchemi");
				System.exit(-1);
			}

			String dir = args[0];
			String srcSchemi = args[1];

			// Inizializzo schema config
			javax.xml.transform.stream.StreamSource streamSourceConfig 
			= new javax.xml.transform.stream.StreamSource(new FileInputStream(srcSchemi+"/config.xsd"));	
			javax.xml.transform.stream.StreamSource streamSourceRegistro 
			= new javax.xml.transform.stream.StreamSource(new FileInputStream(srcSchemi+"/registroServizi.xsd"));

			// Creo schema
			javax.xml.validation.SchemaFactory factory = 
					javax.xml.validation.SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schemaConfigurazione = factory.newSchema(streamSourceConfig);
			schemaRegistro = factory.newSchema(streamSourceRegistro);

			checkXSD(new File(dir));

			if(fileNonValidi.size()>0){

				for(int i=0; i<fileNonValidi.size(); i++){

					System.out.println("\nIl file "+fileNonValidi.get(i)+" non ha superato la validazione xsd: \n"+dichiarazioneAssente.get(i)+"\n");
				}

				System.exit(2);
			}


		} catch(Exception ex) {
			System.err.println("Errore generale: " + ex);
			System.exit(1);
		}

	}


	public static void checkXSD(File f) {
		try {
			if(f.isFile()){
				if(f.getName().endsWith(".xml") && !f.getName().endsWith("-ds.xml")) {

					try{

						if(f.getName().startsWith("config")){
							//System.out.println("validate CONFIGURAZIONE");
							javax.xml.validation.Validator validator  = schemaConfigurazione.newValidator();
							validator.validate(new javax.xml.transform.stream.StreamSource(f)); 
						}else if(f.getName().startsWith("registro")){
							//System.out.println("validate REGISTRO");
							javax.xml.validation.Validator validator  = schemaRegistro.newValidator();
							validator.validate(new javax.xml.transform.stream.StreamSource(f)); 
						}else if(f.getName().startsWith("autorizzazioneBusteEGov")){
							//System.out.println("validate REGISTRO");
							javax.xml.validation.Validator validator  = schemaRegistro.newValidator();
							validator.validate(new javax.xml.transform.stream.StreamSource(f));
						}
					}catch(Exception ex) {
						boolean gestioneErrore = true;

						// Gestione eccezioni
						if(f.getName().startsWith("config")){
							if(f.getName().endsWith("configurazioneSoggetti.xml") ||
									f.getName().endsWith("configurazioneDefault.xml") ||
									f.getName().endsWith("configurazioneDump.xml") ||
									f.getName().endsWith("configurazioneNewConnectionForResponse.xml") ){
								gestioneErrore = false;
							}
						}

						if(gestioneErrore){
							fileNonValidi.add(f.getAbsolutePath());
							dichiarazioneAssente.add(ex.getMessage());		
						}
					}
				}   
			}else{
				File [] fChilds = f.listFiles();
				if(fChilds!=null){
					for (int i = 0; i < fChilds.length; i++) {
						checkXSD(fChilds[i]);
					}
				}
			}

		}
		catch(Exception ex) {
			System.out.println("Errore Check XSD file["+f.getAbsolutePath()+"]: " + ex);
			return;
		}

	}
}
