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

import java.util.ArrayList;

import javax.xml.validation.Schema;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.TipologiaServizio;

/**
 * Classe utilizzata per rappresentare i wsdl che formano un accordo di un servizio
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioWrapper implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Schema XSD */
	private transient Schema schema = null;

	public Schema getSchema() {
		return this.schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}




	/** PortTypes */
	protected ArrayList<PortType> portTypeList = new ArrayList<PortType>();
	private boolean portTypesLoadedFromWSDL = false;
	public boolean isPortTypesLoadedFromWSDL() {
		return this.portTypesLoadedFromWSDL;
	}
	public void setPortTypesLoadedFromWSDL(boolean portTypesLoadedFromWSDL) {
		this.portTypesLoadedFromWSDL = portTypesLoadedFromWSDL;
	}
	
	public void addPortType(PortType portTypeList) {
		this.portTypeList.add(portTypeList);
	}

	public PortType getPortType(String nome) {
		for(int i=0; i<this.portTypeList.size();i++){
			PortType pt = this.portTypeList.get(i);
			if(nome.equals(pt.getNome()) )
				return pt;
		}
		return null;
	}
	
	public PortType getPortType(int index) {
		return this.portTypeList.get( index );
	}

	public PortType removePortType(String nome) {
		for(int i=0; i<this.portTypeList.size();i++){
			PortType pt = this.portTypeList.get(i);
			if(nome.equals(pt.getNome()) ){
				this.portTypeList.remove(i);
				return pt;
			}
		}
		return null;
	}
	
	public PortType removePortType(int index) {
		return this.portTypeList.remove( index );
	}

	public PortType[] getPortTypeList() {
		PortType[] array = new PortType[1];
		if(this.portTypeList.size()>0){
			return this.portTypeList.toArray(array);
		}else{
			return null;
		}
	}

	public void setPortTypeList(PortType[] array) {
		if(array!=null){
			for(int i=0; i<array.length; i++){
				this.portTypeList.add(array[i]);
			}
		}
	}

	public int sizePortTypeList() {
		return this.portTypeList.size();
	}
	
	
	
	
	/** Nome accordo di servizio */
	private IDAccordo idAccordoServizio;
	public IDAccordo getIdAccordoServizio() {
		return this.idAccordoServizio;
	}
	public void setIdAccordoServizio(IDAccordo idAccordoServizio) {
		this.idAccordoServizio = idAccordoServizio;
	}
	
	
	/** Nome PortType */
	private String nomePortType;
	public String getNomePortType() {
		return this.nomePortType;
	}
	public void setNomePortType(String nomePortType) {
		this.nomePortType = nomePortType;
	}
	
	
	/** Indicazione se il servizio e' correlato */
	private TipologiaServizio tipologiaServizio;
	public TipologiaServizio getTipologiaServizio() {
		return this.tipologiaServizio;
	}
	public void setTipologiaServizio(TipologiaServizio tipologiaServizio) {
		this.tipologiaServizio = tipologiaServizio;
	}
	
	
	/** Accordo di Servizio OpenSPCoop */
	private org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio;
	public org.openspcoop2.core.registry.AccordoServizioParteComune getAccordoServizio() {
		return this.accordoServizio;
	}
	public void setAccordoServizio(
			org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio) {
		this.accordoServizio = accordoServizio;
	}

	
	/** wsdl implementativi */
	private String locationWsdlImplementativoErogatore;
	private String locationWsdlImplementativoFruitore;
	private byte[] bytesWsdlImplementativoErogatore;
	private byte[] bytesWsdlImplementativoFruitore;
	public String getLocationWsdlImplementativoErogatore() {
		return this.locationWsdlImplementativoErogatore;
	}
	public void setLocationWsdlImplementativoErogatore(
			String locationWsdlImplementativoErogatore) {
		this.locationWsdlImplementativoErogatore = locationWsdlImplementativoErogatore;
	}
	public String getLocationWsdlImplementativoFruitore() {
		return this.locationWsdlImplementativoFruitore;
	}
	public void setLocationWsdlImplementativoFruitore(
			String locationWsdlImplementativoFruitore) {
		this.locationWsdlImplementativoFruitore = locationWsdlImplementativoFruitore;
	}
	public byte[] getBytesWsdlImplementativoErogatore() {
		return this.bytesWsdlImplementativoErogatore;
	}
	public void setBytesWsdlImplementativoErogatore(
			byte[] bytesWsdlImplementativoErogatore) {
		this.bytesWsdlImplementativoErogatore = bytesWsdlImplementativoErogatore;
	}
	public byte[] getBytesWsdlImplementativoFruitore() {
		return this.bytesWsdlImplementativoFruitore;
	}
	public void setBytesWsdlImplementativoFruitore(
			byte[] bytesWsdlImplementativoFruitore) {
		this.bytesWsdlImplementativoFruitore = bytesWsdlImplementativoFruitore;
	}


	
	@Override
	public AccordoServizioWrapper clone(){
		return this.clone(true);
	}
	public AccordoServizioWrapper clone(boolean clonePortTypeList){
		AccordoServizioWrapper as = new AccordoServizioWrapper();
		
		if(this.accordoServizio!=null)
			as.accordoServizio = (AccordoServizioParteComune) this.accordoServizio.clone();
		
		as.bytesWsdlImplementativoErogatore = this.bytesWsdlImplementativoErogatore;
		as.bytesWsdlImplementativoFruitore = this.bytesWsdlImplementativoFruitore;
		
		if(this.idAccordoServizio!=null)
			as.idAccordoServizio = this.idAccordoServizio.clone();
		
		as.locationWsdlImplementativoErogatore = this.locationWsdlImplementativoErogatore;
		as.locationWsdlImplementativoFruitore = this.locationWsdlImplementativoFruitore;
		
		if(this.nomePortType!=null)
			as.nomePortType = new String(this.nomePortType);
		
		if(clonePortTypeList && this.portTypeList!=null){
			as.portTypeList = new ArrayList<PortType>();
			for (PortType pt : this.portTypeList) {
				as.addPortType((PortType) pt.clone());
			}
		}
			
		as.portTypesLoadedFromWSDL = this.portTypesLoadedFromWSDL;
		
		as.schema = this.schema;
		
		as.tipologiaServizio = this.tipologiaServizio;
		
		return as;
	}

}

