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
package it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model;

import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.RispostaSdINotificaEsitoType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RispostaSdINotificaEsitoType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RispostaSdINotificaEsitoTypeModel extends AbstractModel<RispostaSdINotificaEsitoType> {

	public RispostaSdINotificaEsitoTypeModel(){
	
		super();
	
		this.ESITO = new Field("Esito",java.lang.String.class,"rispostaSdINotificaEsito_Type",RispostaSdINotificaEsitoType.class);
		this.SCARTO_ESITO = new it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model.FileSdIBaseTypeModel(new Field("ScartoEsito",it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType.class,"rispostaSdINotificaEsito_Type",RispostaSdINotificaEsitoType.class));
	
	}
	
	public RispostaSdINotificaEsitoTypeModel(IField father){
	
		super(father);
	
		this.ESITO = new ComplexField(father,"Esito",java.lang.String.class,"rispostaSdINotificaEsito_Type",RispostaSdINotificaEsitoType.class);
		this.SCARTO_ESITO = new it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model.FileSdIBaseTypeModel(new ComplexField(father,"ScartoEsito",it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType.class,"rispostaSdINotificaEsito_Type",RispostaSdINotificaEsitoType.class));
	
	}
	
	

	public IField ESITO = null;
	 
	public it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model.FileSdIBaseTypeModel SCARTO_ESITO = null;
	 

	@Override
	public Class<RispostaSdINotificaEsitoType> getModeledClass(){
		return RispostaSdINotificaEsitoType.class;
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