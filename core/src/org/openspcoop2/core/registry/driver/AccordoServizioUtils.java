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

package org.openspcoop2.core.registry.driver;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;

import javax.xml.validation.Schema;

import org.apache.log4j.Logger;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XSDUtils;

/**
 * AccordoServizioUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class AccordoServizioUtils {

	private Logger logger = null;
	private AbstractXMLUtils xmlUtils = null;
	private XSDUtils xsdUtils = null;
	public AccordoServizioUtils(Logger log){
		if(log!=null)
			this.logger = log;
		else
			this.logger = Logger.getLogger(AccordoServizioUtils.class);
		this.xmlUtils = XMLUtils.getInstance();
		this.xsdUtils = new XSDUtils(this.xmlUtils);
	}
	
	


	
	public Schema buildSchema(AccordoServizioParteComune as,boolean fromBytes) throws DriverRegistroServiziException {

		boolean definitorioPresente = false;
		if(fromBytes){
			definitorioPresente = as.getByteWsdlDefinitorio()!=null;
		}else{
			definitorioPresente = as.getWsdlDefinitorio()!=null;
		}
		if( (!definitorioPresente) && as.sizeAllegatoList()==0 && as.sizeSpecificaSemiformaleList()==0){
			throw new DriverRegistroServiziException("L'acccordo di servizio parte comune non contiene schemi");
		}

		Hashtable<String, byte[]> resources = new Hashtable<String, byte[]>();
		Hashtable<String, String> mappingNamespaceLocations = new Hashtable<String, String>();


		// --------- WSDL DEFINITORIO ---------------------
		if( (fromBytes && as.getByteWsdlDefinitorio()!=null) ||
				(!fromBytes && as.getWsdlDefinitorio()!=null)
				){

			byte[] resource = null;
			String systemId = null;
			if(fromBytes){
				systemId = CostantiRegistroServizi.ALLEGATO_DEFINITORIO_XSD;
				resource = as.getByteWsdlDefinitorio();
			}else{
				String location = as.getWsdlDefinitorio();
				try{
					if(location.startsWith("http://") || location.startsWith("file://")){
						URL url = new URL(location);
						resource = HttpUtilities.requestHTTPFile(url.toString());
						systemId = (new File(url.getFile())).getName();
					}
					else{
						File f = new File(location);
						resource = FileSystemUtilities.readBytesFromFile(f);
						systemId = f.getName();
					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("L'acccordo di servizio parte comune indirizza un wsdl definitorio ["+location+"] non esistente");
				}
			}

			try{
				if(this.xsdUtils.isXSDSchema(resource)){

					// registro risorsa con systemid
					//System.out.println("ADD DEFINITIORIO ["+systemId+"] ");
					resources.put(systemId, resource);

					// registro namespace
					this.xsdUtils.registraMappingNamespaceLocations(resource, systemId, mappingNamespaceLocations);
				}
			}catch(Exception e){
				throw new DriverRegistroServiziException("La lettura del wsdl definitorio ha causato un errore: "+e.getMessage(),e);
			}
		}

		// --------- ALLEGATI --------- 
		if(as.sizeAllegatoList()>0){
			for(int i=0; i<as.sizeAllegatoList();i++){

				Documento allegato = as.getAllegato(i);
				byte[] resource = null;
				String systemId = null;
				//String location = null;
				if(fromBytes){
					//location = allegato.getFile();
					systemId = allegato.getFile();
					resource = allegato.getByteContenuto();
					if(resource==null){
						throw new DriverRegistroServiziException("Allegato ["+systemId+"] non contiene i bytes che ne definiscono il contenuto");
					}
				}
				else{
					String location = allegato.getFile();
					try{
						if(location.startsWith("http://") || location.startsWith("file://")){
							URL url = new URL(location);
							resource = HttpUtilities.requestHTTPFile(url.toString());
							systemId = (new File(url.getFile())).getName();
						}
						else{
							File f = new File(location);
							resource = FileSystemUtilities.readBytesFromFile(f);
							systemId = f.getName();
						}	
					}catch(Exception e){
						throw new DriverRegistroServiziException("Allegato ["+location+"] indirizza un documento non esistente");
					}
				}

				try{
					if(this.xsdUtils.isXSDSchema(resource)){

						if(resources.containsKey(systemId)){
							throw new Exception("Esiste più di un documento xsd, registrato tra allegati e specifiche semiformali, con nome ["+systemId+"] (La validazione xsd di OpenSPCoop richiede l'utilizzo di nomi diversi)");
						}
						resources.put(systemId,resource); 

						// registro namespace
						this.xsdUtils.registraMappingNamespaceLocations(resource, systemId, mappingNamespaceLocations);
					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("La lettura dell'allegato ["+systemId+"] ha causato un errore: "+e.getMessage(),e);
				}

			}
		}


		// --------- SPECIFICHE SEMIFORMALI --------- 
		if(as.sizeSpecificaSemiformaleList()>0){
			for(int i=0; i<as.sizeSpecificaSemiformaleList();i++){

				Documento specificaSemiformale = as.getSpecificaSemiformale(i);
				byte[] resource = null;
				String systemId = null;
				//String location = null;
				if(fromBytes){
					//location = specificaSemiformale.getFile();
					systemId = specificaSemiformale.getFile();
					resource = specificaSemiformale.getByteContenuto();
					if(resource==null){
						throw new DriverRegistroServiziException("Specifica Semiformale ["+systemId+"] non contiene i bytes che ne definiscono il contenuto");
					}
				}
				else{
					String location = specificaSemiformale.getFile();
					try{
						if(location.startsWith("http://") || location.startsWith("file://")){
							URL url = new URL(location);
							resource = HttpUtilities.requestHTTPFile(url.toString());
							systemId = (new File(url.getFile())).getName();
						}
						else{
							File f = new File(location);
							resource = FileSystemUtilities.readBytesFromFile(f);
							systemId = f.getName();
						}	
					}catch(Exception e){
						throw new DriverRegistroServiziException("Specifica Semiformale ["+location+"] indirizza un documento non esistente");
					}
				}

				try{
					if(this.xsdUtils.isXSDSchema(resource)){

						if(resources.containsKey(systemId)){
							throw new Exception("Esiste più di un documento xsd, registrato tra allegati e specifiche semiformali, con nome ["+systemId+"] (La validazione xsd di OpenSPCoop richiede l'utilizzo di nomi diversi)");
						}
						resources.put(systemId,resource); 

						// registro namespace
						this.xsdUtils.registraMappingNamespaceLocations(resource, systemId, mappingNamespaceLocations);
					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("La lettura della specifica semiformale ["+systemId+"] ha causato un errore: "+e.getMessage(),e);
				}

			}
		}

		try{
			return this.xsdUtils.buildSchema(resources, mappingNamespaceLocations, this.logger);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
			
	}

	
}
