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


package org.openspcoop2.core.registry.wsdl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.wsdl.WSDLException;

import org.openspcoop2.utils.wsdl.DefinitionWrapper;

/**
 * Classe utilizzata per leggere/modificare i wsdl tra il formato standard e quelo suddiviso in schemi, accordi parte comune e accordi parte specifica
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lezza Aldo (lezza@openspcoop.org)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConverterStandardWSDL2SplitWSDL {

	/** Directory dove vengono salvati i wsdl */
	File outputDir = null;
	boolean prettyPrint = false;
	public ConverterStandardWSDL2SplitWSDL(File outputDir)throws WSDLException{
		this(outputDir,false);
	}
	public ConverterStandardWSDL2SplitWSDL(File outputDir,boolean prettyPrint)throws WSDLException{
		this(outputDir.getAbsolutePath(),prettyPrint);
	}
	public ConverterStandardWSDL2SplitWSDL(String outputDir)throws WSDLException{
		this(outputDir,false);
	}
	public ConverterStandardWSDL2SplitWSDL(String outputDir,boolean prettyPrint)throws WSDLException{
		try{
			this.prettyPrint = prettyPrint;
			this.outputDir = new File(outputDir);
			if(this.outputDir.exists()){
				if(this.outputDir.isDirectory()==false){
					throw new Exception("Path indicato ["+outputDir+"] esiste e non è una directory");
				}
			}else{
				if(this.outputDir.mkdir()==false){
					throw new Exception("Path indicato ["+outputDir+"] non esiste e non è possibile crearlo come directory");
				}
			}
		}catch(Exception e){
			throw new WSDLException("ConverterStandardWSDL2SplitWSDL(String tmpDir)","Errore durante l'inizializzazione della directory: "+e.getMessage(),e);
		}
	}
	public ConverterStandardWSDL2SplitWSDL()throws WSDLException{
	}
	
	
	/** Variabili per entrambe per conversioni */
	/** Schemi */
	public List<SchemaXSDAccordoServizio> schemiErogatore;
	/** Schemi */
	public List<SchemaXSDAccordoServizio> schemiFruitore;
	public List<SchemaXSDAccordoServizio> getSchemiErogatore() {
		return this.schemiErogatore;
	}
	public List<SchemaXSDAccordoServizio> getSchemiFruitore() {
		return this.schemiFruitore;
	}
	
	
	
	



	/** ------------ Utility per costruire i wsdl standard dal wsdl implementativo erogatore e fruitore ----------------- */

	/** WSDL erogatore */
	public DefinitionWrapper wsdlErogatore;
	/** WSDL fruitore */
	public DefinitionWrapper wsdlFruitore;
	
	public DefinitionWrapper getWsdlErogatore() {
		return this.wsdlErogatore;
	}
	public DefinitionWrapper getWsdlFruitore() {
		return this.wsdlFruitore;
	}	

	/* ------------- build a partire da file ------------- */
	
	public void buildWSDLStandard(String pathWsdlImplementativoErogatore,StandardWSDLOutputMode outputMode)throws WSDLException{
		this.buildWSDLStandard(pathWsdlImplementativoErogatore,null,outputMode,false);
	}
	public void buildWSDLStandard(String pathWsdlImplementativoErogatore,String pathWsdlImplementativoFruitore,StandardWSDLOutputMode outputMode,boolean unicoWSDLDestinazione)throws WSDLException{
		
		try{
			StandardWSDL standardWSDL = new StandardWSDL(pathWsdlImplementativoErogatore, pathWsdlImplementativoFruitore,outputMode);
			if(unicoWSDLDestinazione){
				this.wsdlErogatore = standardWSDL.getWsdlUnificato();
				this.schemiErogatore = standardWSDL.getSchemiWsdlUnificato();
				if(this.outputDir!=null){
					standardWSDL.writeWsdlUnificatoTo(this.outputDir,this.prettyPrint);
				}
			}else{
				this.wsdlErogatore = standardWSDL.getWsdlErogatore();
				this.schemiErogatore = standardWSDL.getSchemiWsdlErogatore();
				if(this.outputDir!=null){
					standardWSDL.writeWsdlErogatoreTo(this.outputDir,this.prettyPrint);
				}
				if(pathWsdlImplementativoFruitore!=null){
					this.wsdlFruitore = standardWSDL.getWsdlFruitore();
					this.schemiFruitore = standardWSDL.getSchemiWsdlFruitore();
					if(this.outputDir!=null){
						standardWSDL.writeWsdlFruitoreTo(this.outputDir,this.prettyPrint);
					}
				}
			}
			
		}catch(Exception e){
			if(this.debug)
				e.printStackTrace();
			throw new WSDLException("ConverterStandardWSDL2SplitWSDL(String pathWsdlImplementativoErogatore,WSDL pathWsdlImplementativoFruitore)",
					"Costruzione non riuscita: "+e.getMessage(),e);
		}

	}
	
	/* ------------- build a partire da byte[] ------------- */
	
	public void buildWSDLStandard(SchemaXSDAccordoServizio definitorio,
			byte[] byteWsdlLogicoErogatore, byte[] byteWsdlImplementativoErogatore,
			StandardWSDLOutputMode outputMode)throws WSDLException{
		this.buildWSDLStandard(definitorio,byteWsdlLogicoErogatore, byteWsdlImplementativoErogatore, null, null,false,outputMode);
	}
	public void buildWSDLStandard(List<SchemaXSDAccordoServizio> schemi,
			byte[] byteWsdlLogicoErogatore, byte[] byteWsdlImplementativoErogatore,
			StandardWSDLOutputMode outputMode)throws WSDLException{
		this.buildWSDLStandard(schemi,byteWsdlLogicoErogatore, byteWsdlImplementativoErogatore, null, null,false,outputMode);
	}
	public void buildWSDLStandard(SchemaXSDAccordoServizio definitorio,
			byte[] byteWsdlLogicoErogatore,byte[] byteWsdlImplementativoErogatore,
			byte[] byteWsdlLogicoFruitore, byte[] byteWsdlImplementativoFruitore,
			boolean unicoWSDLDestinazione,
			StandardWSDLOutputMode outputMode)throws WSDLException{
		List<SchemaXSDAccordoServizio> schemi = new ArrayList<SchemaXSDAccordoServizio>();
		schemi.add(definitorio);
		buildWSDLStandard(schemi, byteWsdlImplementativoErogatore, byteWsdlLogicoErogatore, 
				byteWsdlImplementativoFruitore, byteWsdlLogicoFruitore, unicoWSDLDestinazione,outputMode);
	}
	public void buildWSDLStandard(List<SchemaXSDAccordoServizio> schemi,
			byte[] byteWsdlLogicoErogatore, byte[] byteWsdlImplementativoErogatore,
			byte[] byteWsdlLogicoFruitore, byte[] byteWsdlImplementativoFruitore,
			boolean unicoWSDLDestinazione,
			StandardWSDLOutputMode outputMode)throws WSDLException{
		try{
			
			StandardWSDL standardWSDL = new StandardWSDL(schemi, byteWsdlLogicoErogatore, byteWsdlImplementativoErogatore,
					byteWsdlLogicoFruitore, byteWsdlImplementativoFruitore,outputMode);
			if(unicoWSDLDestinazione){
				this.wsdlErogatore = standardWSDL.getWsdlUnificato();
				this.schemiErogatore = standardWSDL.getSchemiWsdlUnificato();
				if(this.outputDir!=null){
					standardWSDL.writeWsdlUnificatoTo(this.outputDir,this.prettyPrint);
				}
			}else{
				this.wsdlErogatore = standardWSDL.getWsdlErogatore();
				this.schemiErogatore = standardWSDL.getSchemiWsdlErogatore();
				if(this.outputDir!=null){
					standardWSDL.writeWsdlErogatoreTo(this.outputDir,this.prettyPrint);
				}
				if(byteWsdlImplementativoFruitore!=null){
					this.wsdlFruitore = standardWSDL.getWsdlFruitore();
					this.schemiFruitore = standardWSDL.getSchemiWsdlFruitore();
					if(this.outputDir!=null){
						standardWSDL.writeWsdlFruitoreTo(this.outputDir,this.prettyPrint);
					}
				}
			}
						
		}catch(Exception e){
			if(this.debug)
				e.printStackTrace();
			throw new WSDLException("ConverterStandardWSDL2SplitWSDL(Byte)",
					"Costruzione non riuscita: "+e.getMessage(),e);
		}
	}
	



	
	/** ----------------- Utility per costruire i wsdl Suddivisi in schemi, parte comune e parte specifica dai wsdl standard ------------------------- */

	/** WSDL Concettuale */
	public DefinitionWrapper wsdlConcettuale;
	/** WSDL logico erogatore */
	public DefinitionWrapper wsdlLogicoErogatore;
	/** WSDL logico fruitore */
	public DefinitionWrapper wsdlLogicoFruitore;
	/** WSDL implementativo erogatore */
	public DefinitionWrapper wsdlImplementativoErogatore;
	/** WSDL Implementativo fruitore */
	public DefinitionWrapper wsdlImplementativoFruitore;
	
	public DefinitionWrapper getWsdlConcettuale() {
		return this.wsdlConcettuale;
	}
	public DefinitionWrapper getWsdlLogicoErogatore() {
		return this.wsdlLogicoErogatore;
	}
	public DefinitionWrapper getWsdlLogicoFruitore() {
		return this.wsdlLogicoFruitore;
	}
	public DefinitionWrapper getWsdlImplementativoErogatore() {
		return this.wsdlImplementativoErogatore;
	}
	public DefinitionWrapper getWsdlImplementativoFruitore() {
		return this.wsdlImplementativoFruitore;
	}

	
	/* ------------- SPLIT a partire da file ------------- */
	
	public void split(String wsdlErogatore)throws WSDLException{
		String fruitore = null;
		this.split(wsdlErogatore,fruitore);
	}
	public void split(String wsdlErogatore, String wsdlFruitore)throws WSDLException{
		split(wsdlErogatore, wsdlFruitore, TipoSchemaXSDAccordoServizio.ALLEGATO);
	}	
	public void split(String wsdlErogatore,TipoSchemaXSDAccordoServizio tipoSchema)throws WSDLException{
		String fruitore = null;
		this.split(wsdlErogatore,fruitore,tipoSchema);
	}	
	public void split(String wsdlErogatore, String wsdlFruitore, TipoSchemaXSDAccordoServizio tipoSchema) throws WSDLException{
		this.split(wsdlErogatore,wsdlFruitore,tipoSchema,false);
	}
	public void split(String wsdlErogatore, String wsdlFruitore, TipoSchemaXSDAccordoServizio tipoSchema,boolean permettiSchemaLocationNonDefiniti) throws WSDLException{
		try{
			SplitWSDL splitWSDL = new SplitWSDL(wsdlErogatore,wsdlFruitore, tipoSchema, permettiSchemaLocationNonDefiniti);
			
			this.wsdlConcettuale = splitWSDL.getWsdlConcettuale();
			this.wsdlLogicoErogatore = splitWSDL.getWsdlLogicoErogatore();
			this.wsdlImplementativoErogatore = splitWSDL.getWsdlImplementativoErogatore();
			this.schemiErogatore = splitWSDL.getSchemiErogatore();
			if (wsdlFruitore!=null){
				this.wsdlLogicoFruitore = splitWSDL.getWsdlLogicoFruitore();
				this.wsdlImplementativoFruitore = splitWSDL.getWsdlImplementativoFruitore();
				this.schemiFruitore = splitWSDL.getSchemiFruitore();
			}
					
			if(this.outputDir!=null){
				splitWSDL.writeTo(this.outputDir,this.prettyPrint);	
			}
			
		}catch(Exception e){
			e.printStackTrace();
			if(this.debug)
				e.printStackTrace();
			throw new WSDLException("split(String)",
					"Split non riuscito: "+e.getMessage(),e);
		}
	}
	
	
	public void split(String wsdl,
			String[] porttypesErogatore,String[] operationPorttypesErogatore)throws WSDLException{
		this.split(wsdl,porttypesErogatore,operationPorttypesErogatore,null,null,TipoSchemaXSDAccordoServizio.ALLEGATO);
	}	
	public void split(String wsdl,
			String[] porttypesErogatore,String[] operationPorttypesErogatore,
			TipoSchemaXSDAccordoServizio tipoSchema)throws WSDLException{
		this.split(wsdl,porttypesErogatore,operationPorttypesErogatore,null,null,tipoSchema);
	}	
	public void split(String wsdl,
			String[] porttypesErogatore, String[] operationPorttypesErogatore,
			String[] porttypesFruitore, String[] operationPorttypesFruitore) throws WSDLException{
		this.split(wsdl,porttypesErogatore,operationPorttypesErogatore,porttypesFruitore,operationPorttypesFruitore,TipoSchemaXSDAccordoServizio.ALLEGATO);
	}	
	public void split(String wsdl,
			String[] porttypesErogatore, String[] operationPorttypesErogatore,
			String[] porttypesFruitore, String[] operationPorttypesFruitore,
			TipoSchemaXSDAccordoServizio tipoSchema) throws WSDLException{
		
		try{
				
			SplitWSDL splitWsdl = null;
			if(porttypesFruitore==null)
				splitWsdl = new SplitWSDL(wsdl, porttypesErogatore, operationPorttypesErogatore, tipoSchema, true);
			else
				splitWsdl = new SplitWSDL(wsdl, porttypesErogatore, operationPorttypesErogatore, porttypesFruitore, operationPorttypesFruitore, tipoSchema, true);
				
			this.wsdlConcettuale = splitWsdl.getWsdlConcettuale();
			this.wsdlLogicoErogatore = splitWsdl.getWsdlLogicoErogatore();
			this.wsdlImplementativoErogatore = splitWsdl.getWsdlImplementativoErogatore();
			this.schemiErogatore = splitWsdl.getSchemiErogatore();
			if (porttypesFruitore!=null){
				this.wsdlLogicoFruitore = splitWsdl.getWsdlLogicoFruitore();
				this.wsdlImplementativoFruitore = splitWsdl.getWsdlImplementativoFruitore();
				this.schemiFruitore = splitWsdl.getSchemiFruitore();
			}
					
			if(this.outputDir!=null){
				splitWsdl.writeTo(this.outputDir,this.prettyPrint);	
			}
			
		}catch(Exception e){
			if(this.debug)
				e.printStackTrace();
			throw new WSDLException("split(String,portTypes...)",
					"Split non riuscito: "+e.getMessage(),e);
		}
		
	}

	
	/* ------------- SPLIT a partire da byte[] ------------- */
	public void split(byte[] wsdlErogatore)throws WSDLException{
		byte[] fruitore = null;
		this.split(wsdlErogatore,fruitore,TipoSchemaXSDAccordoServizio.ALLEGATO);
	}
	public void split(byte[] wsdlErogatore, TipoSchemaXSDAccordoServizio tipoSchema)throws WSDLException{
		byte[] fruitore = null;
		this.split(wsdlErogatore,fruitore,tipoSchema);
	}
	public void split(byte[] wsdlErogatore,byte[] wsdlFruitore)throws WSDLException{
		this.split(wsdlErogatore,wsdlFruitore,TipoSchemaXSDAccordoServizio.ALLEGATO);
	}
	public void split(byte[] wsdlErogatore,byte[] wsdlFruitore, TipoSchemaXSDAccordoServizio tipoSchema)throws WSDLException{
		this.split(wsdlErogatore,wsdlFruitore,tipoSchema,false);
	}
	public void split(byte[] wsdlErogatore,byte[] wsdlFruitore, TipoSchemaXSDAccordoServizio tipoSchema, boolean permettiSchemaLocationNonDefiniti)throws WSDLException{
		try{
			File wsdlErogatoreTmpFile = File.createTempFile("WSDLErogatoreFromByte-", "-split", this.outputDir);
			if (!wsdlErogatoreTmpFile.exists())
				wsdlErogatoreTmpFile.createNewFile();
			FileOutputStream fout = new FileOutputStream(wsdlErogatoreTmpFile);
			fout.write(wsdlErogatore);
			fout.flush();
			fout.close();
			String pathFileErogatore = wsdlErogatoreTmpFile.getAbsolutePath();
			
			String pathFileFruitore = null;
			File wsdlFruitoreTmpFile = null;
			if(wsdlFruitore!=null){
				wsdlFruitoreTmpFile = File.createTempFile("WSDLFruitoreFromByte-", "-split", this.outputDir);
				if (!wsdlFruitoreTmpFile.exists())
					wsdlFruitoreTmpFile.createNewFile();
				fout = new FileOutputStream(wsdlFruitoreTmpFile);
				fout.write(wsdlFruitore);
				fout.flush();
				fout.close();
				pathFileFruitore = wsdlFruitoreTmpFile.getAbsolutePath();
			}
			
			this.split(pathFileErogatore,pathFileFruitore,tipoSchema,permettiSchemaLocationNonDefiniti);
			
			// delete file tmp
			wsdlErogatoreTmpFile.delete();
			if(wsdlFruitore!=null){
				wsdlFruitoreTmpFile.delete();
			}
			
		}catch(Exception e){
			if(this.debug)
				e.printStackTrace();
			throw new WSDLException("split(Byte)",
					"Split non riuscito: "+e.getMessage(),e);
		}
	}
	
	
	public void split(byte[] wsdl,
			String[] porttypesErogatore,String[] operationPorttypesErogatore)throws WSDLException{
		this.split(wsdl,porttypesErogatore,operationPorttypesErogatore,null,null,TipoSchemaXSDAccordoServizio.ALLEGATO);
	}
	public void split(byte[] wsdl,
			String[] porttypesErogatore,String[] operationPorttypesErogatore,
			TipoSchemaXSDAccordoServizio tipoSchema)throws WSDLException{
		this.split(wsdl,porttypesErogatore,operationPorttypesErogatore,null,null,tipoSchema);
	}
	public void split(byte[] wsdl,
			String[] porttypesErogatore,String[] operationPorttypesErogatore,
			String[] porttypesFruitore,String[] operationPorttypesFruitore)throws WSDLException{
		split(wsdl,
				porttypesErogatore,operationPorttypesErogatore,
				porttypesFruitore,operationPorttypesFruitore,
				TipoSchemaXSDAccordoServizio.ALLEGATO);
	}
	public void split(byte[] wsdl,
			String[] porttypesErogatore,String[] operationPorttypesErogatore,
			String[] porttypesFruitore,String[] operationPorttypesFruitore,
			TipoSchemaXSDAccordoServizio tipoSchema)throws WSDLException{
		try{
			File wsdlErogatoreTmpFile = File.createTempFile("WSDLErogatoreFromByte-", "-split", this.outputDir);
			if (!wsdlErogatoreTmpFile.exists())
				wsdlErogatoreTmpFile.createNewFile();
			FileOutputStream fout = new FileOutputStream(wsdlErogatoreTmpFile);
			fout.write(wsdl);
			fout.flush();
			fout.close();
			String pathFileErogatore = wsdlErogatoreTmpFile.getAbsolutePath();
			
			this.split(pathFileErogatore,porttypesErogatore,operationPorttypesErogatore,porttypesFruitore,operationPorttypesFruitore,tipoSchema);
			
			// delete file tmp
			wsdlErogatoreTmpFile.delete();
			
		}catch(Exception e){
			if(this.debug)
				e.printStackTrace();
			throw new WSDLException("split(Byte,PortType...)",
					"Split non riuscito: "+e.getMessage(),e);
		}
	}
	
	




	private boolean debug = false;

	public boolean isDebug() {
		return this.debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}


}
