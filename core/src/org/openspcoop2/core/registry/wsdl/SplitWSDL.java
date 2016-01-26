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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Import;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLElement;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.wsdl.extensions.schema.SchemaImpl;

/**
 * Classe per la creazione dei WSDL suddivisi in schemi, parte comune e parte specifica a partire da WSDL standard. 
 * 
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SplitWSDL {
	
	/** Costanti */
	public static final String FOLDER_IMPLEMENTATIVI = CostantiRegistroServizi.SPECIFICA_PORTI_ACCESSO_DIR;
	public static final String IMPLEMENTATIVO_EROGATORE_FILENAME = CostantiRegistroServizi.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL;
	public static final String IMPLEMENTATIVO_FRUITORE_FILENAME = CostantiRegistroServizi.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL;
	
	public static final String FOLDER_INTERFACCE = CostantiRegistroServizi.SPECIFICA_INTERFACCIA_DIR;
	public static final String LOGICO_EROGATORE_FILENAME = CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL;
	public static final String LOGICO_FRUITORE_FILENAME = CostantiRegistroServizi.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL;
	public static final String CONCETTUALE_FILENAME = CostantiRegistroServizi.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL;
	public static final String DEFINITORIO_FILENAME = CostantiRegistroServizi.ALLEGATO_DEFINITORIO_XSD;
	
	
	/* ------- INPUT --------- */
	/** WSDL erogatore. */
	private DefinitionWrapper wsdlErogatore;
	private String wsdlErogatorePath;
	/** WSDL Fruitore */
	private DefinitionWrapper wsdlFruitore;
	private String wsdlFruitorePath;
	/** Array dei nomi PortType e Operations per la divisione del wsdl */
	private String[] porttypesErogatore;
	private String[] operationPorttypesErogatore;
	private String[] porttypesFruitore;
	private String[] operationPorttypesFruitore;
	
	
	/* ------- OUTPUT --------- */
	/** WSDL implementativo erogatore */
	private DefinitionWrapper wsdlImplementativoErogatore;
	/** WSDL Implementativo fruitore */
	private DefinitionWrapper wsdlImplementativoFruitore;
	/** WSDL logico erogatore */
	private DefinitionWrapper wsdlLogicoErogatore;
	/** WSDL logico fruitore */
	private DefinitionWrapper wsdlLogicoFruitore;
	/** WSDL Concettuale */
	private DefinitionWrapper wsdlConcettuale;
	/** Schemi */
	private List<SchemaXSDAccordoServizio> schemiErogatore = new ArrayList<SchemaXSDAccordoServizio>();
	private List<SchemaXSDAccordoServizio> schemiFruitore = new ArrayList<SchemaXSDAccordoServizio>();
	private List<String> schemiNames = new ArrayList<String>();
	/** Import degli schemi (xsd) */
	private List<Schema> importsSchemiErogatore = new ArrayList<Schema>();
	private List<Schema> importsSchemiFruitore = new ArrayList<Schema>();
	/** Import  (wsdl) */
	private List<Import> importsWsdlErogatore = new ArrayList<Import>();
	private List<Import> importsWsdlFruitore = new ArrayList<Import>();
	/** Directory dove vengono salvati gli schemi */
	private TipoSchemaXSDAccordoServizio tipoSchema = TipoSchemaXSDAccordoServizio.ALLEGATO;
	/** Il booleano gestisciNomi indica se, in caso di conflitti di nomi, questi vengono automaticamente risolti
	 * o venga lanciata un'eccezione dai metodi. */
	private boolean gestisciNomi;
	

	
	/* ------- ALTRO ---------- */
	private XMLUtils xmlUtils = null;
	private WSDLUtilities wsdlUtilities = null;
	
	
	
	
	
	/* ------- Costruttori --------- */
	
	/**
	 * Suddivide un wsdl erogatore.
	 * 
	 * Tutti i documenti inclusi dai wsdl (via import o include) sono recuperati per essere
	 * ricopiati nella cartella schemiDir (di default "allegati") e gli attributi di location 
	 * aggiornati di conseguenza. In caso di conflitti di nomi, questi vengono automaticamente risolti.
	 * 
	 * Gli schema vengono estratti e copiati come xsd  nella cartella schemiDir ed inclusi nel documento 
	 * originale via xsd:include/xsd:import.
	 * 
	 * @param wsdlErogatore
	 * @throws SplitWSDLException
	 */
	public SplitWSDL(String wsdlErogatore) throws SplitWSDLException{
		this(wsdlErogatore, null, null, null, null, null, TipoSchemaXSDAccordoServizio.ALLEGATO, true, false);
	}
	public SplitWSDL(String wsdlErogatore,boolean permettiSchemaLocationNonDefiniti) throws SplitWSDLException{
		this(wsdlErogatore, null, null, null, null, null, TipoSchemaXSDAccordoServizio.ALLEGATO, true,permettiSchemaLocationNonDefiniti);
	}
	
	
	/**
	 * Suddivide i wsdl erogatore e fruitore.
	 * 
	 * Tutti i documenti inclusi dai wsdl (via import o include) sono recuperati per essere
	 * ricopiati nella cartella schemiDir (di default "allegati") e gli attributi di location 
	 * aggiornati di conseguenza. In caso di conflitti di nomi, questi vengono automaticamente risolti.
	 * 
	 * Gli schema vengono estratti e copiati come xsd  nella cartella schemiDir ed inclusi nel documento 
	 * originale via xsd:include/xsd:import.
	 * 
	 * I documenti inclusi da entrambi i wsdl vengono riconosciuti e copiati una sola volta tra gli schemi
	 * 
	 * @param wsdlErogatore
	 * @param wsdlFruitore
	 * @throws SplitWSDLException
	 */
	
	public SplitWSDL(String wsdlErogatore, String wsdlFruitore) throws SplitWSDLException{
		this(wsdlErogatore, wsdlFruitore, null, null, null, null, TipoSchemaXSDAccordoServizio.ALLEGATO, true, false);
	}
	
	
	/**
	 * Suddivide i wsdl erogatore e fruitore.
	 * 
	 * Tutti i documenti inclusi dai wsdl (via import o include) sono recuperati per essere
	 * ricopiati nella cartella schemiDir (di default "allegati") e gli attributi di location 
	 * aggiornati di conseguenza. 
	 * 
	 * Il booleano gestisciNomi indica se, in caso di conflitti di nomi, questi vengono automaticamente risolti
	 * o venga lanciata un'eccezione.
	 * 
	 * Gli schema vengono estratti e copiati come xsd  nella cartella schemiDir ed inclusi nel documento 
	 * originale via xsd:include.
	 * 
	 * I documenti inclusi da entrambi i wsdl vengono riconosciuti e copiati una sola volta tra gli schemi
	 * 
	 * @param wsdlErogatore
	 * @param wsdlFruitore
	 * @param gestisciNomi
	 * @throws SplitWSDLException
	 */

	public SplitWSDL(String wsdlErogatore, String wsdlFruitore, boolean gestisciNomi) throws SplitWSDLException{
		this(wsdlErogatore, wsdlFruitore, null, null, null, null, TipoSchemaXSDAccordoServizio.ALLEGATO, true, false);
	}
	public SplitWSDL(String wsdlErogatore, String wsdlFruitore, boolean gestisciNomi,boolean permettiSchemaLocationNonDefiniti) throws SplitWSDLException{
		this(wsdlErogatore, wsdlFruitore, null, null, null, null, TipoSchemaXSDAccordoServizio.ALLEGATO, true, permettiSchemaLocationNonDefiniti);
	}
	
	/**
	 * Suddivide i wsdl erogatore e fruitore.
	 * 
	 * Tutti i documenti inclusi dai wsdl (via import o include) sono recuperati per essere
	 * ricopiati nella cartella schemiDir e gli attributi di location 
	 * aggiornati di conseguenza. In caso di conflitti di nomi, questi vengono automaticamente risolti.
	 * 
	 * Gli schema vengono estratti e copiati come xsd  nella cartella schemiDir ed inclusi nel documento 
	 * originale via xsd:include.
	 * 
	 * I documenti inclusi da entrambi i wsdl vengono riconosciuti e copiati una sola volta tra gli schemi
	 * 
	 * @param wsdlErogatore
	 * @param wsdlFruitore
	 * @param tipoSchema
	 * @throws SplitWSDLException
	 */
	public SplitWSDL(String wsdlErogatore, String wsdlFruitore, TipoSchemaXSDAccordoServizio tipoSchema) throws SplitWSDLException{
		this(wsdlErogatore, wsdlFruitore, null, null, null, null, tipoSchema, true, false);
	}
	public SplitWSDL(String wsdlErogatore, String wsdlFruitore, TipoSchemaXSDAccordoServizio tipoSchema,boolean permettiSchemaLocationNonDefiniti) throws SplitWSDLException{
		this(wsdlErogatore, wsdlFruitore, null, null, null, null, tipoSchema, true, permettiSchemaLocationNonDefiniti);
	}
	
	/**
	 * Suddivide un wsdl.
	 * 
	 * Tutti i documenti inclusi dai wsdl (via import o include) sono recuperati per essere
	 * ricopiati nella cartella schemiDir e gli attributi di location 
	 * aggiornati di conseguenza. In caso di conflitti di nomi, questi vengono automaticamente risolti.
	 * 
	 * Gli schema vengono estratti e copiati come xsd  nella cartella schemiDir ed inclusi nel documento 
	 * originale via xsd:include.
	 * 
	 * Il booleano gestisciNomi indica se, in caso di conflitti di nomi, questi vengono automaticamente risolti
	 * o venga lanciata un'eccezione.
	 * 
	 * I documenti inclusi da entrambi i wsdl vengono riconosciuti e copiati una sola volta tra gli schemi
	 * 
	 */
	public SplitWSDL(String wsdl, String[] porttypesErogatore,String[] operationPorttypesErogatore, TipoSchemaXSDAccordoServizio tipoSchema, boolean gestisciNomi) throws SplitWSDLException{
		this(wsdl, null, porttypesErogatore, operationPorttypesErogatore, null, null, tipoSchema, gestisciNomi,false);
	}
	public SplitWSDL(String wsdl, String[] porttypesErogatore,String[] operationPorttypesErogatore,
			String[] porttypesFruitore,String[] operationPorttypesFruitore, TipoSchemaXSDAccordoServizio tipoSchema, boolean gestisciNomi) throws SplitWSDLException{
		this(wsdl, wsdl, porttypesErogatore, operationPorttypesErogatore, porttypesFruitore, operationPorttypesFruitore, tipoSchema, gestisciNomi,false);
	}
	public SplitWSDL(String wsdl, String[] porttypesErogatore,String[] operationPorttypesErogatore,
			String[] porttypesFruitore,String[] operationPorttypesFruitore, TipoSchemaXSDAccordoServizio tipoSchema, boolean gestisciNomi,boolean permettiSchemaLocationNonDefiniti) throws SplitWSDLException{
		this(wsdl, wsdl, porttypesErogatore, operationPorttypesErogatore, porttypesFruitore, operationPorttypesFruitore, tipoSchema, gestisciNomi,permettiSchemaLocationNonDefiniti);
	}
	
	private SplitWSDL(String wsdlErogatore, String wsdlFruitore, String[] porttypesErogatore,String[] operationPorttypesErogatore,
			String[] porttypesFruitore,String[] operationPorttypesFruitore, TipoSchemaXSDAccordoServizio tipoSchema, 
			boolean gestisciNomi,boolean permettiSchemaLocationNonDefiniti) throws SplitWSDLException{
		
		this.gestisciNomi = gestisciNomi;
		this.tipoSchema = tipoSchema;
		
		
		/** Controllo che le informazioni passate siano consistenti */
		
		if(wsdlErogatore==null){
			throw new SplitWSDLException("WSDL Erogatore non fornito");
		}
		if(porttypesErogatore!=null && operationPorttypesErogatore==null){
			throw new SplitWSDLException("Operations dei PortyTypes per il wsdl erogatore non forniti");
		}
		if(porttypesErogatore!=null && porttypesErogatore.length!=operationPorttypesErogatore.length){
			throw new SplitWSDLException("Operations ["+operationPorttypesErogatore.length+"] dei PortyTypes ["+porttypesErogatore.length+"] per il wsdl erogatore non correlate correttamente ai port types");
		}
		if(porttypesFruitore!=null){
			if(operationPorttypesFruitore==null){
				throw new SplitWSDLException("Operations dei PortyTypes per il wsdl fruitore non forniti");
			}
			if(porttypesFruitore.length!=operationPorttypesFruitore.length){
				throw new SplitWSDLException("Operations ["+operationPorttypesErogatore.length+"] dei PortyTypes ["+porttypesErogatore.length+"] per il wsdl fruitore non correlate correttamente ai port types");
			}
		}
		
		this.porttypesErogatore = porttypesErogatore;
		this.porttypesFruitore = porttypesFruitore;
		this.operationPorttypesErogatore = operationPorttypesErogatore;
		this.operationPorttypesFruitore = operationPorttypesFruitore;
		
		try{
		
			this.xmlUtils = XMLUtils.getInstance();
			this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
			
			/** Carico i wsdl necessari */
			if (wsdlErogatore!= null) {
				this.wsdlErogatorePath = wsdlErogatore;
				this.wsdlErogatore = new DefinitionWrapper(this.wsdlUtilities.readWSDLFromLocation(wsdlErogatore),this.xmlUtils);
				this.wsdlConcettuale = new DefinitionWrapper(this.wsdlUtilities.readWSDLFromLocation(wsdlErogatore),this.xmlUtils);
				this.wsdlLogicoErogatore = new DefinitionWrapper(this.wsdlUtilities.readWSDLFromLocation(wsdlErogatore),this.xmlUtils);
				this.wsdlImplementativoErogatore = new DefinitionWrapper(this.wsdlUtilities.readWSDLFromLocation(wsdlErogatore),this.xmlUtils);
			} 
			
			if (wsdlFruitore!= null) {
				this.wsdlFruitorePath = wsdlFruitore;
				this.wsdlFruitore = new DefinitionWrapper(this.wsdlUtilities.readWSDLFromLocation(wsdlFruitore),this.xmlUtils);
				this.wsdlLogicoFruitore = new DefinitionWrapper(this.wsdlUtilities.readWSDLFromLocation(wsdlFruitore),this.xmlUtils);
				this.wsdlImplementativoFruitore = new DefinitionWrapper(this.wsdlUtilities.readWSDLFromLocation(wsdlFruitore),this.xmlUtils);
			}
			
			/** Costruisco tutti i partizionamenti **/
			creaSchemi(permettiSchemaLocationNonDefiniti);
			creaConcettuale();
			creaLogicoErogatore();
			creaImplementativoErogatore();
			if(wsdlFruitore==null || (porttypesErogatore!= null && porttypesFruitore==null)) return;
			creaLogicoFruitore();
			creaImplementativoFruitore();
			
		}catch(Exception e){
			throw new SplitWSDLException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ------------------ Metodi pubblici ----------------------- */
	
	/** 
	 * METODI PER LA SCRITTURA SU FS DEI DOCUMENTI
	 * 
	 * @param dir Directory dove produrre i documenti
	 * @throws IOException 
	 * @throws WSDLException 
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 * @throws ParserConfigurationException 
	 * @throws XMLException 
	 */
	public void writeTo(File dir,boolean prettyPrint) throws WSDLException, IOException, TransformerException,
		ParserConfigurationException, TransformerFactoryConfigurationError, org.openspcoop2.utils.wsdl.WSDLException, XMLException{
		writeSchemiTo(dir,prettyPrint);
		// Creo il folder per le interfacce
		File interfacce = new File(dir.getAbsolutePath() + File.separator + SplitWSDL.FOLDER_INTERFACCE);
		interfacce.mkdir();
		writeConcettualeTo(dir,prettyPrint);
		writeLogicoErogatoreTo(dir,prettyPrint);
		writeLogicoFruitoreTo(dir,prettyPrint);
		File implementativi = new File(dir.getAbsolutePath() + File.separator + SplitWSDL.FOLDER_IMPLEMENTATIVI);
		implementativi.mkdir();
		writeImplementativoErogatoreTo(dir,prettyPrint);
		writeImplementativoFruitoreTo(dir,prettyPrint);
	}
	
	public DefinitionWrapper getWsdlImplementativoErogatore() {
		return this.wsdlImplementativoErogatore;
	}
	public DefinitionWrapper getWsdlImplementativoFruitore() {
		return this.wsdlImplementativoFruitore;
	}
	public DefinitionWrapper getWsdlLogicoErogatore() {
		return this.wsdlLogicoErogatore;
	}
	public DefinitionWrapper getWsdlLogicoFruitore() {
		return this.wsdlLogicoFruitore;
	}
	public DefinitionWrapper getWsdlConcettuale() {
		return this.wsdlConcettuale;
	}
	public List<SchemaXSDAccordoServizio> getSchemiErogatore() {
		return this.schemiErogatore;
	}
	public List<SchemaXSDAccordoServizio> getSchemiFruitore() {
		return this.schemiFruitore;
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ------------------- SCHEMI ----------------------------- */
	
	/**
	 * Trasferisce tutti i documenti inclusi e importati in un'unica cartella (schemaLocation)
	 * Prepara i wsdl:import da sostuituire agli schema del wsdl:types nel wsdlErogatore e wsdlFruitore
	 * 
	 * @throws TransformerException
	 * @throws SchemaXSDNamingException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XMLException 
	 */
	@SuppressWarnings("unchecked")
	private void creaSchemi(boolean permettiSchemaLocationNonDefiniti) throws TransformerException, SchemaXSDNamingException, ParserConfigurationException, SAXException, IOException, XMLException{
		// Mi serve per non ricominciare la numerazione degli schemi da 0 quando li produco per il fruitore
		int flag = 0; 
		
		flag = gestisciImportInclude(this.wsdlErogatore, this.wsdlErogatorePath, 
				this.schemiErogatore, this.importsSchemiErogatore, this.importsWsdlErogatore, true, flag,permettiSchemaLocationNonDefiniti);
		
		/** Se la sorgente e' unica, non importa gestire nuovi import/include per il wsdlFruitore. E' lo stesso, verrebbero solo duplicati */
		if(this.porttypesErogatore!=null){
			this.schemiFruitore = (ArrayList<SchemaXSDAccordoServizio>) ((ArrayList<SchemaXSDAccordoServizio>) this.schemiErogatore).clone();
			this.importsSchemiFruitore = (ArrayList<Schema>) ((ArrayList<Schema>)this.importsSchemiErogatore).clone();
			this.importsWsdlFruitore = (ArrayList<Import>) ((ArrayList<Import>)this.importsWsdlErogatore).clone();
		} else {
			flag = gestisciImportInclude(this.wsdlFruitore, this.wsdlFruitorePath, this.schemiFruitore, this.importsSchemiFruitore, this.importsWsdlFruitore, false, flag, permettiSchemaLocationNonDefiniti);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private int gestisciImportInclude(DefinitionWrapper input_wsdl, String input_wsdlPath, 
			List<SchemaXSDAccordoServizio> output_schemi, List<Schema> output_importsSchemi,List<Import> output_importsWsdl, 
			boolean isErogatore, int flag, boolean permettiSchemaLocationNonDefiniti) throws ParserConfigurationException, SAXException, IOException, SchemaXSDNamingException, XMLException {
		int ritorno = 0;
		if(input_wsdl==null) return ritorno;
		
		/** Schemi XSD */
		if(input_wsdl.getTypes()!=null){
			List<?> schemas = input_wsdl.getTypes().getExtensibilityElements();
			//Controllo se ci sono xsd:Schema dentro i types
			if(schemas!=null){
				for(int i=0; i<schemas.size(); i++){
					Schema schema = (Schema) schemas.get(i);
					String targetNamespace = schema.getElement().getAttribute("targetNamespace");
					
					// Prendo la lista dei namespace dichiarati nel wsdl
					Map<String,String> definitionNamespace = input_wsdl.getWsdlDefinition().getNamespaces();
					Set<String> prefixes = definitionNamespace.keySet();
					Iterator<String> it = prefixes.iterator();
					while(it.hasNext()){
						String prefix = it.next();
						if(prefix.equals("")){
							if(schema.getElement().getAttribute("xmlns").equals(""))
								schema.getElement().setAttribute("xmlns", definitionNamespace.get(prefix));
						}
						else {
							if(schema.getElement().getAttribute("xmlns:"+prefix).equals(""))
								schema.getElement().setAttribute("xmlns:"+prefix, definitionNamespace.get(prefix));
						}
					}
					
					// Cerco un nome per l'xsd che vado a creare
					String filename = getLocalNameSchema(SplitWSDL.DEFINITORIO_FILENAME, output_schemi, false);
					
					// Gli XSDSchema devono essere integrati con il costrutto xsd:include 
					Element newSchemaElement = schema.getElement().getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "xsd:schema");
					newSchemaElement.setAttribute("targetNamespace", targetNamespace);
					
					Element include = schema.getElement().getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "xsd:include");
					include.setAttribute("schemaLocation", ".." + File.separator + this.tipoSchema.getDirectory() + File.separator + filename);
					
					newSchemaElement.appendChild(include);
					Schema newSchema = new SchemaImpl();
					newSchema.setElement(newSchemaElement);
					newSchema.setElementType(schema.getElementType());
					newSchema.setRequired(schema.getRequired());
					
					output_importsSchemi.add(newSchema);
					
					/// Aggiungo lo schema tra gli schemi
					if(isErogatore) 
						addSchemaErogatore(schema.getElement(),filename, new File(input_wsdlPath), false, permettiSchemaLocationNonDefiniti);
					else
						addSchemaFruitore(schema.getElement(), filename, new File(input_wsdlPath), false, permettiSchemaLocationNonDefiniti);
					ritorno ++;
				}
			}
		}
		
		/** WSDL imports */
		// Controllo se ci sono wsdl:import
		Map<?,?> importsMap = input_wsdl.getImports();
		Iterator<?> namespaces = importsMap.values().iterator();
		while(namespaces.hasNext()){
			List<?> imports = (List<?>) namespaces.next();
			for(int i = 0; i<imports.size(); i++){
				
				Import myimport = (Import) imports.get(i);
				String location = myimport.getLocationURI();
				
				File importedFile = new File(new File(input_wsdlPath).getParentFile(), location);
				
				String schemaFilename = getLocalNameSchema(importedFile, output_schemi, false);
				
				/** Creo il wsdl:import **/
				Import newImport = input_wsdl.createImport();
				newImport.setLocationURI(".." + File.separator + this.tipoSchema.getDirectory() + File.separator + schemaFilename);
				newImport.setNamespaceURI(myimport.getNamespaceURI());
				output_importsWsdl.add(newImport);
				
				/** Aggiungo il documento importato alla lista degli schemi */
				org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
				Document doc = xmlUtils.newDocument(importedFile);
				if(isErogatore) 
					addSchemaErogatore(doc.getDocumentElement(), schemaFilename, importedFile, true, permettiSchemaLocationNonDefiniti);
				else 
					addSchemaFruitore(doc.getDocumentElement(), schemaFilename, importedFile, true, permettiSchemaLocationNonDefiniti);
			}
		}
		return ritorno;
	}
	
	/**
	 * Ottiene un nome da usare per lo schema.
	 * Se il file e' nuovo in lista ed il suo nome univoco, viene mantenuto.
	 * Se il file e' gia' incluso in lista, gli viene dato lo stesso nome.
	 * Se il file e' nuovo in lista, ma il nome gia' usato, viene aggiunto e rinominato.
	 * 
	 * @param file Documento da aggiungere
	 * @param lista Lista degli schemi
	 * @param force indica se forzare l'uso del nome originale o meno.
	 * 
	 * @return nome da usare per lo schema.
	 * @throws SchemaXSDNamingException
	 */
	private String getLocalNameSchema (File file, List<SchemaXSDAccordoServizio> lista, boolean force) throws SchemaXSDNamingException{
		String name =  getLocalNameSchema(file, null, lista, force, true);
		this.schemiNames.add(name);
		return name;
	}
	
	/**
	 * Ottiene un nome da usare per lo schema.
	 * Se il file e' nuovo in lista ed il suo nome univoco, viene mantenuto.
	 * Se il file e' gia' incluso in lista, gli viene dato lo stesso nome.
	 * Se il file e' nuovo in lista, ma il nome gia' usato, viene aggiunto e rinominato.
	 * 
	 * @param filename nome del file da aggiungere
	 * @param lista Lista degli schemi
	 * @param force indica se forzare l'uso del nome originale o meno.
	 * 
	 * @return nome da usare per lo schema.
	 * @throws SchemaXSDNamingException
	 */
	private String getLocalNameSchema (String filename, List<SchemaXSDAccordoServizio> lista, boolean force) throws SchemaXSDNamingException{
		String name = getLocalNameSchema(null, filename, lista, force, true);
		this.schemiNames.add(name);
		return name;
	}
	
	/**
	 * Ottiene un nome da usare per lo schema.
	 * Se il file e' nuovo in lista ed il suo nome univoco, viene mantenuto.
	 * Se il file e' gia' incluso in lista, gli viene dato lo stesso nome.
	 * Se il file e' nuovo in lista, ma il nome gia' usato, viene aggiunto e rinominato.
	 * 
	 * Per gli schemi creati a partire dagli schemi definiti nel wsdl originale, file sara' a null mentre filename viene valorizzato.
	 * 
	 * @param file
	 * @param filename
	 * @param lista
	 * @param force
	 * @param firstTime
	 * @return nome da usare per lo schema.
	 * @throws SchemaXSDNamingException
	 */
	private String getLocalNameSchema (File file, String filename, List<SchemaXSDAccordoServizio> lista, boolean force, boolean firstTime) throws SchemaXSDNamingException{

		if(force){
			if(file!=null)
				return file.getName();
			else
				return filename;
		}
				
		// Scorro la lista
		// Controllo (solo la prima volta) Se il file e' gia in lista
		if(firstTime && file!=null){
			for(int i=0;i<lista.size(); i++){
				if(file.getAbsolutePath().compareTo(lista.get(i).getSourceAbsolutePath()) == 0) return lista.get(i).getFilename();
			}
		}
		if(lista.equals(this.schemiFruitore) && file!=null){
			//Il documento potrebbe gia' esser stato importato dal wsdlErogatore. 
			//In questo caso devo importarlo con lo stesso nome assegnato in precedenza. 
			for(int i=0;i<this.schemiErogatore.size(); i++){
				if(file.getAbsolutePath().compareTo(this.schemiErogatore.get(i).getSourceAbsolutePath()) == 0){ 
					return this.schemiErogatore.get(i).getFilename();
				}
			}
		}
		
		if(filename==null) 
			filename = file.getName();
		
		for(int i=0;i<this.schemiNames.size(); i++){
			// Se c'e' gia un file con quel nome in lista
			if(filename.compareTo(this.schemiNames.get(i)) == 0) {
				// Se c'e' la gestione dei nomi doppi
				if(this.gestisciNomi){
					// Prendo il nome del file lo nomino filename_X.ext , dove X e' un intero
					int punto = filename.lastIndexOf(".");
					String filenameSenzaExt = filename.substring(0, punto);
					String filenameExt = filename.substring(punto);
					int underscore = filename.lastIndexOf("_");
					if(underscore<0){
						filename = filenameSenzaExt + "_0" + filenameExt;
					}
					else {
						try{
							int number = Integer.parseInt(filenameSenzaExt.substring(underscore+1)); 
							filename = filenameSenzaExt.substring(0, underscore+1) + (number + 1) + filenameExt;
						} catch (Exception e) {
							filename = filenameSenzaExt + "_0" + filenameExt;
						}
					}
					// Controllo l'univocita' del nuovo nome di file
					if(file!=null)
						return getLocalNameSchema(new File(file.getParentFile(),filename), lista, false);
					else
						return getLocalNameSchema(filename, lista, false);
				}
				else {
					throw new SchemaXSDNamingException("Ci sono due documenti inclusi/importati che hanno lo stesso nome di file: " + filename);
				}
			}
		}
		return filename;
	}
	
	/**
	 * Aggiunge lo schema all'array dell'Erogatore.
	 * 
	 * 
	 * @param Element del documento importato o incluso
	 * @param Nome del file con cui verra' salvato il documento passato
	 * @param Riferimento al documento che ha incluso/importato
	 * @param Controlla se il file e' gia' stato incluso
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws SchemaXSDNamingException
	 * @throws XMLException 
	 */
	private void addSchemaErogatore(Element xml, String filename, File localpath, 
			boolean check, boolean permettiSchemaLocationNonDefiniti) throws ParserConfigurationException, SAXException, IOException, SchemaXSDNamingException, XMLException{
		addSchema(xml, filename, localpath, this.schemiErogatore, check, permettiSchemaLocationNonDefiniti);
	}
	
	/**
	 * Aggiunge lo schema all'array del Fruitore
	 * 
	 * 
	 * @param Element del documento importato o incluso
	 * @param Nome del file con cui verra' salvato il documento passato
	 * @param Riferimento al documento che ha incluso/importato
	 * @param Controlla se il file e' gia' stato incluso
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws SchemaXSDNamingException
	 * @throws XMLException 
	 */
	private void addSchemaFruitore(Element xml, String filename, File localpath, 
			boolean check, boolean permettiSchemaLocationNonDefiniti) throws ParserConfigurationException, SAXException, IOException, SchemaXSDNamingException, XMLException{
		addSchema(xml, filename, localpath, this.schemiFruitore, check, permettiSchemaLocationNonDefiniti);
	}
	
	/**
	 * Prende il documento xml passato, cerca eventuali include o import per iterare la procedura, modifica opportunamente 
	 * i valori dei location e schemaLocation, aggiunge uno schema alla lista passata.
	 * 
	 * @param Element del documento importato o incluso
	 * @param Nome del file con cui verra' salvato il documento passato
	 * @param Riferimento al documento
	 * @param Lista in cui aggiungere lo schema
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws SchemaXSDNamingException
	 * @throws XMLException 
	 */
	private void addSchema(Element xml, String filename, File xmlSource, List<SchemaXSDAccordoServizio> schemi, 
			boolean check, boolean permettiSchemaLocationNonDefiniti) throws ParserConfigurationException, SAXException, IOException, SchemaXSDNamingException, XMLException{
		
		org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
				
		// Indica se il filename deve essere forzato ad essere quello indicato
		boolean force = false;
		// Devo controllare se questo schema e' gia' nella lista. In tal caso esco.
		if(check){
			for(int i=0;i<schemi.size(); i++){
				if(xmlSource.getAbsolutePath().compareTo(schemi.get(i).getSourceAbsolutePath()) == 0) return;
			}
		}
		
		// Cerco gli xs:import
		NodeList nl = xml.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "import");
		for(int i = 0; i<nl.getLength(); i++){
			// Trovato un xsd:import. prendo lo schemaLocation
			String schemaLocation = ((Element) nl.item(i)).getAttribute("schemaLocation");
			if(schemaLocation==null || "".equals(schemaLocation)){
				if(!permettiSchemaLocationNonDefiniti){
					// Lo schema location non è presente ad esempio quando si costruisce un unico file wsdl+xsd, 
					// e tutti gli xsd sono all'interno del wsdl. E' presente l'import ma non lo schema location
					throw new ParserConfigurationException("attributo 'schemaLocation' non definito in import (file "+filename+")");
				}
			}
			else{
				// Prendo il file in questione
				File schema = new File(xmlSource.getParentFile() ,schemaLocation); 
				String schemaFilename = getLocalNameSchema(schema,schemi, force);
				// Cambio lo schemaLocation con il path locale.
				((Element) nl.item(i)).setAttribute("schemaLocation",schemaFilename);
				
				// Aggiungo il documento nella lista degli schemi
				Document doc = xmlUtils.newDocument(schema);
				addSchema(doc.getDocumentElement(), schemaFilename, schema, schemi, true, permettiSchemaLocationNonDefiniti);
			}
		}
		// Cerco gli xs:include
		nl = xml.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "include");
		for(int i = 0; i<nl.getLength(); i++){
			// Trovato un xsd:import. prendo lo schemaLocation
			String schemaLocation = ((Element) nl.item(i)).getAttribute("schemaLocation");
			if(schemaLocation==null || "".equals(schemaLocation)){
				if(!permettiSchemaLocationNonDefiniti){
					// Lo schema location non è presente ad esempio quando si costruisce un unico file wsdl+xsd, 
					// e tutti gli xsd sono all'interno del wsdl. E' presente l'import ma non lo schema location
					throw new ParserConfigurationException("attributo 'schemaLocation' non definito in include (file "+filename+")");
				}
			}
			else{
				// Prendo il file in questione
				File schema = new File(xmlSource.getParentFile() ,schemaLocation); 
				String schemaFilename = getLocalNameSchema(schema, schemi, force);
				// Cambio lo schemaLocation con il path locale.
				((Element) nl.item(i)).setAttribute("schemaLocation",schemaFilename);
				
				// Aggiungo il documento nella lista degli schemi
				Document doc = xmlUtils.newDocument(schema);
				addSchema(doc.getDocumentElement(), schemaFilename, schema, schemi, true, permettiSchemaLocationNonDefiniti);
			}
		}
		
		// Cerco i wsdl:import
		nl = xml.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "import");
		for(int i = 0; i<nl.getLength(); i++){
			// Trovato un wsdl:import. prendo il location
			String schemaLocation = ((Element) nl.item(i)).getAttribute("location");
			if(schemaLocation==null || "".equals(schemaLocation)){
				if(!permettiSchemaLocationNonDefiniti){
					// Lo schema location non è presente ad esempio quando si costruisce un unico file wsdl+xsd, 
					// e tutti gli xsd sono all'interno del wsdl. E' presente l'import ma non lo schema location
					throw new ParserConfigurationException("attributo 'location' non definito in include (file "+filename+")");
				}
			}
			else{
				// Prendo il file in questione
				File schema = new File(xmlSource.getParentFile() ,schemaLocation); 
				String schemaFilename = getLocalNameSchema(schema, schemi, force);
				// Cambio lo schemaLocation con il path locale.
				((Element) nl.item(i)).setAttribute("location", schemaFilename);
				
				// Aggiungo il documento nella lista degli schemi
				Document doc = xmlUtils.newDocument(schema);
				addSchema(doc.getDocumentElement() , schemaFilename, schema, schemi, true, permettiSchemaLocationNonDefiniti);
			}
		}
		
		SchemaXSDAccordoServizio schema = new SchemaXSDAccordoServizio(xml, filename, xmlSource, this.tipoSchema);
		schemi.add(schema);
	}
	
	private void writeSchemiTo(File basedir,boolean prettyPrint) throws TransformerException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, XMLException{
		
		if((this.schemiErogatore.size() + this.schemiFruitore.size()) == 0) return; 
		
		if(!basedir.isDirectory()) {
			throw new IOException("Il path indicato non e' una directory valida.");
		}
		
		for(int i=0; i< this.schemiErogatore.size(); i++){
			this.schemiErogatore.get(i).writeTo(basedir,prettyPrint);
		}
		
		for(int i=0; i< this.schemiFruitore.size(); i++){
			this.schemiFruitore.get(i).writeTo(basedir,prettyPrint);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------------- CONCETTUALI ----------------------------- */
	
	/**
	 * Crea un documento concettuale, contenente cioe' solo la definizione dei messaggi scambiati
	 * e la definizione astratta delle operazioni ammesse.
	 */
	private void creaConcettuale () 
	throws IOException, WSDLException,ParserConfigurationException,SAXException,IOException,TransformerException,TransformerConfigurationException{
		
		
		this.wsdlUtilities.removeAllBindings(this.wsdlConcettuale);
	    this.wsdlUtilities.removeAllServices(this.wsdlConcettuale);
		
		Types types = this.wsdlConcettuale.getTypes();
		List<?> xsdTypes = types.getExtensibilityElements();
		while (xsdTypes.size()>0){
			types.removeExtensibilityElement((Schema) xsdTypes.get(0));
			xsdTypes = types.getExtensibilityElements();
		}
		
		//Sistemo gli imports:
		this.wsdlConcettuale.removeAllImports();
		
		for(int i = 0; i<this.importsWsdlErogatore.size(); i++) { 
			this.wsdlConcettuale.addImport(this.importsWsdlErogatore.get(i));
		}
		
		for(int i = 0; i<this.importsSchemiErogatore.size(); i++) { 
			this.wsdlConcettuale.getTypes().addExtensibilityElement(this.importsSchemiErogatore.get(i));
		}
		
		// controllo se il documento importato era gia' tra quelli dell'erogatore 
		for(int i = 0; i<this.importsWsdlFruitore.size(); i++) { 
			boolean found = false;
			for(int j = 0; j<this.importsWsdlErogatore.size(); j++) { 
				if(this.importsWsdlErogatore.get(j).getLocationURI().compareTo(this.importsWsdlFruitore.get(i).getLocationURI()) == 0 && 
						this.importsWsdlErogatore.get(j).getNamespaceURI().compareTo(this.importsWsdlFruitore.get(i).getNamespaceURI()) == 0){
					found = true;
				}
			}
			if(!found) this.wsdlConcettuale.addImport(this.importsWsdlFruitore.get(i));
		}
		
		// Controllo anche gli schemi importati.
		for(int i = 0; i<this.importsSchemiFruitore.size(); i++) { 
			boolean found = false;
			Element includeFruitore = (Element) this.importsSchemiFruitore.get(i).getElement().getFirstChild();
			
			for(int j = 0; j<this.importsSchemiErogatore.size(); j++) { 
				Element includeErogatore = (Element) this.importsSchemiErogatore.get(j).getElement().getFirstChild();
				if(includeFruitore.getAttribute("schemaLocation").compareTo(includeErogatore.getAttribute("schemaLocation")) == 0){
					found = true;
				}
			}
			if(!found) this.wsdlConcettuale.getTypes().addExtensibilityElement(this.importsSchemiFruitore.get(i));
		}


		if (this.wsdlFruitore !=null){
			// e' un concettuale, si aggiungono i portTypes, i messaggi ed eventuali namespace particolari
			//namespace
			Map<?,?> m = this.wsdlFruitore.getNamespaces();
			Iterator<?> it = null;

			m = this.wsdlFruitore.getMessages();
			it = m.values().iterator();
			while (it.hasNext()){
				Message im = (Message) it.next();
				this.wsdlConcettuale.addMessage(im);
			}

			m = this.wsdlFruitore.getAllPortTypes();
			it = m.values().iterator();
			while (it.hasNext()){
				PortType im = (PortType) it.next();
				this.wsdlConcettuale.addPortType(im);
			}
		}
		
		if(this.porttypesErogatore!=null){
			String[] portTypes = merge(this.porttypesErogatore, this.porttypesFruitore);
			/** PULIZIA PORT TYPE */
			Map<?, ?> m = this.wsdlConcettuale.getAllPortTypes();
			Iterator<?> it = m.values().iterator();
			while (it.hasNext()){
				PortType now = (PortType) it.next();
				String cuBinName = now.getQName().getLocalPart();
				boolean delete = true; 
				for(int i=0; i<portTypes.length;i++){
					String check = portTypes[i];
					if (check.equals(cuBinName))
					{
						delete = false;
						break;
					}
				}
				if (delete){
					this.wsdlConcettuale.removePortType(now.getQName());
				}
			}

			/** Pulizia message */
			Vector<Message> mess = new Vector<Message>();
			m = this.wsdlConcettuale.getAllPortTypes();
			it = m.values().iterator();
			while (it.hasNext()){
				PortType p = (PortType) it.next();
				List<?> myl = p.getOperations();
				//prelevo i messaggi che interessano le operazioni di questo porttype
				for (int h=0, k=myl.size(); h<k; h++){
					Operation operationWSDL = (Operation) myl.get(h);
					Message input = operationWSDL.getInput()== null? null : operationWSDL.getInput().getMessage();
					if (input !=null)
						mess.add(input);
					Message output = operationWSDL.getOutput() == null? null: operationWSDL.getOutput().getMessage();
					if (output !=null)
						mess.add(output);
				}
			}

			//rimuovo i messaggi non presenti nei porttype
			m = this.wsdlConcettuale.getMessages();
			it = m.values().iterator();
			Vector<Message> removeM = new Vector<Message>();
			while (it.hasNext()){
				Message temp = ((Message) it.next());
				if (!mess.contains(temp)){
					removeM.add(temp);
				}
			}
			for (int i=0, j=removeM.size(); i<j; i++){
				this.wsdlConcettuale.removeMessage(removeM.get(i).getQName());
			}
		}

		
		
	}
	
	private void writeConcettualeTo(File dir,boolean prettyPrint) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		this.wsdlUtilities.writeWsdlTo(this.wsdlConcettuale, 
				dir.getAbsolutePath() + File.separator + SplitWSDL.FOLDER_INTERFACCE + File.separator + SplitWSDL.CONCETTUALE_FILENAME,
				prettyPrint);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------------- LOGICI ----------------------------- */
	
	private void creaLogicoErogatore() 
	throws IOException, WSDLException,ParserConfigurationException,SAXException,IOException,TransformerException,TransformerConfigurationException{
		creaLogico(this.wsdlLogicoErogatore, this.importsSchemiErogatore, this.importsWsdlErogatore, this.porttypesErogatore, this.operationPorttypesErogatore);
	}
	
	private void creaLogicoFruitore() 
	throws IOException, WSDLException,ParserConfigurationException,SAXException,IOException,TransformerException,TransformerConfigurationException{
		if(this.wsdlLogicoFruitore==null) return;
		creaLogico(this.wsdlLogicoFruitore, this.importsSchemiFruitore, this.importsWsdlFruitore, this.porttypesFruitore, this.operationPorttypesFruitore);
	}
	
	private void creaLogico (DefinitionWrapper wsdlLogico, List<Schema> importsSchemi, List<Import> importsWsdl, String[] portTypes, String[] operations) 
	throws IOException, WSDLException,ParserConfigurationException,SAXException,IOException,TransformerException,TransformerConfigurationException{
		
		//rimozione binding
		Map<?,?> m = null;
		Iterator<?> it = null;
		this.wsdlUtilities.removeAllBindings(wsdlLogico);
		this.wsdlUtilities.removeAllServices(wsdlLogico);
		removeExtensibilityElements(wsdlLogico.getTypes());
		for (int i=0; i<importsSchemi.size(); i++){
			wsdlLogico.getTypes().addExtensibilityElement(importsSchemi.get(i));
		}
		
		wsdlLogico.removeAllImports();
		for (int i=0; i<importsWsdl.size(); i++){
			wsdlLogico.addImport(importsWsdl.get(i));
		}

		//prelevo da un portType esistente prefix e namespace (necessari al QName)
		m = wsdlLogico.getPortTypes();
		it = m.values().iterator();
		String prefix = null;
		String namespace = null;
		while (it.hasNext()){
			PortType p = (PortType) it.next();
			prefix = p.getQName().getPrefix();
			namespace = p.getQName().getNamespaceURI();
			break;
		}

		if (prefix == null && namespace == null)
		{
			throw new WSDLException(namespace, prefix);
		}

		//portType nulli, wsdl separati
		if (portTypes == null){
			return;
		}

		//riimposto i portType
		Vector<PortType> pts = new Vector<PortType>();

		for (int i=0, j=portTypes.length; i<j; i++){

			QName current = new QName(namespace, portTypes[i], prefix);
			PortType now = wsdlLogico.getPortType(current);
			String[] myops = operations[i] == null ? null: operations[i].split(",");
			if (myops == null)
			{
				return;
			}
			//da tutte le operazioni di questo pt rimuovo quelle non interessanti
			List<?> opts = now.getOperations();
			Operation[] opp = new Operation[opts.size()];
			opts.toArray(opp);

			for (int x=0, y = opts.size(); x < y; x++)
			{
				String cur = opp[x].getName(); 
				boolean delete = true;
				for (int h=0, k= myops.length; h < k; h++){
					if (cur.equals(myops[h])){
						delete = false;
						break;
					}
				}
				if (delete)
					now.removeOperation(opp[x].getName(), null,null);

			}

			pts.add(now);
		}

		//in pts ho i portType filtrati come richiesto

		m = wsdlLogico.getAllPortTypes();
		it = m.values().iterator();

		//rimuovo tutti i portType
		while (it.hasNext()){
			it.next();
			it.remove();
			//def.removePortType(((PortType)it.next()).getQName());
		}

		Vector<Message> mess = new Vector<Message>();
		//inserisco quelli richiesti
		for (int i=0, j=pts.size(); i<j; i++){
			PortType p = pts.get(i);
			List<?> myl = p.getOperations();
			//prelevo i messaggi che interessano le operazioni di questo porttype
			for (int h=0, k=myl.size(); h<k; h++){
				Operation operationWSDL = (Operation) myl.get(h);
				Message input = operationWSDL.getInput()== null? null : operationWSDL.getInput().getMessage();
				if (input !=null)
					mess.add(input);
				Message output = operationWSDL.getOutput() == null? null: operationWSDL.getOutput().getMessage();
				if (output !=null)
					mess.add(output);
			}
			wsdlLogico.addPortType(p);
		}

		//rimuovo i messaggi non presenti nei porttype

		m = wsdlLogico.getMessages();
		it = m.values().iterator();
		Vector<Message> removeM = new Vector<Message>();
		while (it.hasNext()){
			Message temp = ((Message) it.next());
			if (!mess.contains(temp))
				removeM.add(temp);
		}

		for (int i=0, j=removeM.size(); i<j; i++){
			wsdlLogico.removeMessage(removeM.get(i).getQName());
		}

		/** PULIZIA PORT TYPE */
		m = wsdlLogico.getAllPortTypes();
		it = m.values().iterator();
		while (it.hasNext()){
			PortType now = (PortType) it.next();
			String cuBinName = now.getQName().getLocalPart();
			boolean delete = true; 
			for(int i=0; i<portTypes.length;i++){
				String check = portTypes[i];
				//System.out.println("CONFRONTO ["+check+"] con ["+cuBinName+"]");
				if (check.equals(cuBinName))
				{
					delete = false;
					break;
				}
			}
			if (delete){
				wsdlLogico.removePortType(now.getQName());
			}
		}

	}
	
	private void writeLogicoErogatoreTo(File dir,boolean prettyPrint) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		this.wsdlUtilities.writeWsdlTo(this.wsdlLogicoErogatore, 
				dir.getAbsolutePath() + File.separator + SplitWSDL.FOLDER_INTERFACCE + File.separator + SplitWSDL.LOGICO_EROGATORE_FILENAME,
				prettyPrint);
	}
	
	private void writeLogicoFruitoreTo(File dir,boolean prettyPrint) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		this.wsdlUtilities.writeWsdlTo(this.wsdlLogicoFruitore, 
				dir.getAbsolutePath() + File.separator + SplitWSDL.FOLDER_INTERFACCE + File.separator + SplitWSDL.LOGICO_FRUITORE_FILENAME,
				prettyPrint);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------------- IMPLEMENTATIVI ----------------------------- */
	
	private void creaImplementativoErogatore() 
	throws IOException, WSDLException,ParserConfigurationException,SAXException,IOException,TransformerException,TransformerConfigurationException{
		Import logicoErogatore = this.wsdlImplementativoErogatore.createImport();
		logicoErogatore.setLocationURI(".." + File.separator + SplitWSDL.FOLDER_INTERFACCE + File.separator + SplitWSDL.LOGICO_EROGATORE_FILENAME);
		logicoErogatore.setNamespaceURI(this.wsdlLogicoErogatore.getTargetNamespace());
		creaImplementativo(this.wsdlImplementativoErogatore, logicoErogatore, this.porttypesErogatore, this.operationPorttypesErogatore);
	}
	
	private void creaImplementativoFruitore() 
	throws IOException, WSDLException,ParserConfigurationException,SAXException,IOException,TransformerException,TransformerConfigurationException{
		if(this.wsdlImplementativoFruitore==null) return;
		Import logicoFruitore = this.wsdlImplementativoFruitore.createImport();
		logicoFruitore.setLocationURI(".." + File.separator + SplitWSDL.FOLDER_INTERFACCE + File.separator + SplitWSDL.LOGICO_FRUITORE_FILENAME);
		logicoFruitore.setNamespaceURI(this.wsdlLogicoFruitore.getTargetNamespace());
		creaImplementativo(this.wsdlImplementativoFruitore, logicoFruitore, this.porttypesFruitore, this.operationPorttypesFruitore);
	}
	
	private void creaImplementativo (DefinitionWrapper wsdlImplementativo, Import logico, String[] portTypes, String[] operations) 
	throws IOException, WSDLException,ParserConfigurationException,SAXException,IOException,TransformerException,TransformerConfigurationException{
		
		//rimozione messaggi

		this.wsdlUtilities.removeAllMessages(wsdlImplementativo);
		this.wsdlUtilities.removeAllPortTypes(wsdlImplementativo);
		
		if (portTypes == null) {
			//Importo il logico
			wsdlImplementativo.removeAllImports();
			wsdlImplementativo.addImport(logico);
			removeExtensibilityElements(wsdlImplementativo.getTypes());
			return;
		}
		Map<?,?> m = wsdlImplementativo.getBindings();
		Iterator<?> it = m.values().iterator();
		//creo il vettore dei binding corretti.
		Vector<Binding> bnd = new Vector<Binding>();
		while (it.hasNext()){
			Binding now = wsdlImplementativo.getBinding(((Binding)it.next()).getQName());
			String refPTName = now.getPortType().getQName().getLocalPart();
			boolean found = false;
			int i=0;
			for (int j=portTypes.length; i<j; i++){
				if (refPTName.equals(portTypes[i]))
				{
					found = true;
					break;
				}
			}
			if (found){
				List<?> ops = now.getBindingOperations();
				String[] myops = operations[i].split(",");
				Vector<String> toDel = new Vector<String>();
				for (int h=0, k=ops.size(); h<k; h++){
					String curName = ((BindingOperation)ops.get(h)).getName();
					boolean rem = true;
					for (int mm=0, n=myops.length; mm<n; mm++){
						if (myops[mm].equals(curName))
						{
							rem = false;
							break;
						}
					}
					if (rem)
						toDel.add(curName);
					//now.removeBindingOperation(myops[h], null, null);
				}
				for (int x=0, y=toDel.size(); x<y; x++)
					now.removeBindingOperation(toDel.get(x), null, null);
			}

			bnd.add(now);

		}

		/** PULIZIA BINDINGS */
		m = wsdlImplementativo.getAllBindings();
		it = m.values().iterator();
		while (it.hasNext()){
			Binding now = (Binding) it.next();
			String cuBinName = now.getPortType().getQName().getLocalPart();
			boolean delete = true; 
			for(int i=0; i<portTypes.length;i++){
				String check = portTypes[i];
				//System.out.println("CONFRONTO ["+check+"] con ["+cuBinName+"]");
				if (check.equals(cuBinName))
				{
					delete = false;
					break;
				}
			}
			if (delete){
				wsdlImplementativo.removeBinding(now.getQName());
			}
		}

		/** PULIZIA SERVICES */
		m = wsdlImplementativo.getAllServices();
		it = m.values().iterator();
		while (it.hasNext()){
			Service now = (Service) it.next();
			Map<?,?> pts = now.getPorts();
			Iterator<?> ports = pts.values().iterator();
			boolean delete = true; 
			while (ports.hasNext()){
				//if (debug) System.out.println("Iterazione "+iter);
				Port cuPor = (Port) ports.next();
				String cuBinName = cuPor.getBinding().getPortType().getQName().getLocalPart();
				for(int i=0; i<portTypes.length;i++){
					String check = portTypes[i];
					//System.out.println("CONFRONTO ["+check+"] con ["+cuBinName+"]");
					if (check.equals(cuBinName))
					{
						delete = false;
						break;
					}
				}
			}
			if (delete){
				wsdlImplementativo.removeService(now.getQName());
			}
		}

		//Importo il logico
		wsdlImplementativo.removeAllImports();
		wsdlImplementativo.addImport(logico);
		removeExtensibilityElements(wsdlImplementativo.getTypes());
		
	}
	
	private void writeImplementativoErogatoreTo(File dir,boolean prettyPrint) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		this.wsdlUtilities.writeWsdlTo(this.wsdlImplementativoErogatore, 
				dir.getAbsolutePath() + File.separator + SplitWSDL.FOLDER_IMPLEMENTATIVI + File.separator + SplitWSDL.IMPLEMENTATIVO_EROGATORE_FILENAME,
				prettyPrint);
	}
	
	private void writeImplementativoFruitoreTo(File dir,boolean prettyPrint) throws WSDLException, IOException, org.openspcoop2.utils.wsdl.WSDLException{
		this.wsdlUtilities.writeWsdlTo(this.wsdlImplementativoFruitore, 
				dir.getAbsolutePath() + File.separator + SplitWSDL.FOLDER_IMPLEMENTATIVI + File.separator + SplitWSDL.IMPLEMENTATIVO_FRUITORE_FILENAME,
				prettyPrint);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------------- UTILITIES GENERALI ----------------------------- */
	
	private void removeExtensibilityElements(WSDLElement ele){
		List<?> ext = ele.getExtensibilityElements();
		while (ext.size()>0){
			ele.removeExtensibilityElement((ExtensibilityElement) ext.get(0));
			ext = ele.getExtensibilityElements();
		}
	}
	
	private String[] merge(String[] a, String[] b){
		if(a==null) return b;
		if(b==null) return a;
		String[] c = new String[a.length + b.length];
		int i = 0;
		for(int j=0; j<a.length;j++){
			c[i] = a[j];
			i++;
		}
		for(int j=0; j<b.length;j++){
			c[i] = b[j];
			i++;
		}
		return c;
	}
	

}
