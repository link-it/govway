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
package org.openspcoop2.core.transazioni.model;

import org.openspcoop2.core.transazioni.TransazioneExtendedInfo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TransazioneExtendedInfo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneExtendedInfoModel extends AbstractModel<TransazioneExtendedInfo> {

	public TransazioneExtendedInfoModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"transazione-extended-info",TransazioneExtendedInfo.class);
		this.VALORE = new Field("valore",java.lang.String.class,"transazione-extended-info",TransazioneExtendedInfo.class);
	
	}
	
	public TransazioneExtendedInfoModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"transazione-extended-info",TransazioneExtendedInfo.class);
		this.VALORE = new ComplexField(father,"valore",java.lang.String.class,"transazione-extended-info",TransazioneExtendedInfo.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField VALORE = null;
	 

	@Override
	public Class<TransazioneExtendedInfo> getModeledClass(){
		return TransazioneExtendedInfo.class;
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