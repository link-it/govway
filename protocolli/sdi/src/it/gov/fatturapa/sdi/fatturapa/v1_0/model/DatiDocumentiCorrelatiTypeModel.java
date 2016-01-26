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
package it.gov.fatturapa.sdi.fatturapa.v1_0.model;

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDocumentiCorrelatiType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiDocumentiCorrelatiType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiDocumentiCorrelatiTypeModel extends AbstractModel<DatiDocumentiCorrelatiType> {

	public DatiDocumentiCorrelatiTypeModel(){
	
		super();
	
		this.RIFERIMENTO_NUMERO_LINEA = new Field("RiferimentoNumeroLinea",java.lang.Integer.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.ID_DOCUMENTO = new Field("IdDocumento",java.lang.String.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.DATA = new Field("Data",java.util.Date.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.NUM_ITEM = new Field("NumItem",java.lang.String.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.CODICE_COMMESSA_CONVENZIONE = new Field("CodiceCommessaConvenzione",java.lang.String.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.CODICE_CUP = new Field("CodiceCUP",java.lang.String.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.CODICE_CIG = new Field("CodiceCIG",java.lang.String.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
	
	}
	
	public DatiDocumentiCorrelatiTypeModel(IField father){
	
		super(father);
	
		this.RIFERIMENTO_NUMERO_LINEA = new ComplexField(father,"RiferimentoNumeroLinea",java.lang.Integer.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.ID_DOCUMENTO = new ComplexField(father,"IdDocumento",java.lang.String.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.DATA = new ComplexField(father,"Data",java.util.Date.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.NUM_ITEM = new ComplexField(father,"NumItem",java.lang.String.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.CODICE_COMMESSA_CONVENZIONE = new ComplexField(father,"CodiceCommessaConvenzione",java.lang.String.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.CODICE_CUP = new ComplexField(father,"CodiceCUP",java.lang.String.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
		this.CODICE_CIG = new ComplexField(father,"CodiceCIG",java.lang.String.class,"DatiDocumentiCorrelatiType",DatiDocumentiCorrelatiType.class);
	
	}
	
	

	public IField RIFERIMENTO_NUMERO_LINEA = null;
	 
	public IField ID_DOCUMENTO = null;
	 
	public IField DATA = null;
	 
	public IField NUM_ITEM = null;
	 
	public IField CODICE_COMMESSA_CONVENZIONE = null;
	 
	public IField CODICE_CUP = null;
	 
	public IField CODICE_CIG = null;
	 

	@Override
	public Class<DatiDocumentiCorrelatiType> getModeledClass(){
		return DatiDocumentiCorrelatiType.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}