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
package it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.model;

import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.RispostaSdIRiceviFileType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RispostaSdIRiceviFileType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RispostaSdIRiceviFileTypeModel extends AbstractModel<RispostaSdIRiceviFileType> {

	public RispostaSdIRiceviFileTypeModel(){
	
		super();
	
		this.IDENTIFICATIVO_SD_I = new Field("IdentificativoSdI",java.lang.Integer.class,"rispostaSdIRiceviFile_Type",RispostaSdIRiceviFileType.class);
		this.DATA_ORA_RICEZIONE = new Field("DataOraRicezione",java.util.Date.class,"rispostaSdIRiceviFile_Type",RispostaSdIRiceviFileType.class);
		this.ERRORE = new Field("Errore",java.lang.String.class,"rispostaSdIRiceviFile_Type",RispostaSdIRiceviFileType.class);
	
	}
	
	public RispostaSdIRiceviFileTypeModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO_SD_I = new ComplexField(father,"IdentificativoSdI",java.lang.Integer.class,"rispostaSdIRiceviFile_Type",RispostaSdIRiceviFileType.class);
		this.DATA_ORA_RICEZIONE = new ComplexField(father,"DataOraRicezione",java.util.Date.class,"rispostaSdIRiceviFile_Type",RispostaSdIRiceviFileType.class);
		this.ERRORE = new ComplexField(father,"Errore",java.lang.String.class,"rispostaSdIRiceviFile_Type",RispostaSdIRiceviFileType.class);
	
	}
	
	

	public IField IDENTIFICATIVO_SD_I = null;
	 
	public IField DATA_ORA_RICEZIONE = null;
	 
	public IField ERRORE = null;
	 

	@Override
	public Class<RispostaSdIRiceviFileType> getModeledClass(){
		return RispostaSdIRiceviFileType.class;
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