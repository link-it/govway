/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package it.cnipa.collprofiles.model;

import it.cnipa.collprofiles.OperationType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model OperationType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperationTypeModel extends AbstractModel<OperationType> {

	public OperationTypeModel(){
	
		super();
	
		this.SERVIZIO = new Field("servizio",java.lang.String.class,"operationType",OperationType.class);
		this.OPERAZIONE = new Field("operazione",java.lang.String.class,"operationType",OperationType.class);
		this.PROFILO_DI_COLLABORAZIONE = new Field("profiloDiCollaborazione",java.lang.String.class,"operationType",OperationType.class);
		this.SERVIZIO_CORRELATO = new Field("servizioCorrelato",java.lang.String.class,"operationType",OperationType.class);
		this.OPERAZIONE_CORRELATA = new Field("operazioneCorrelata",java.lang.String.class,"operationType",OperationType.class);
	
	}
	
	public OperationTypeModel(IField father){
	
		super(father);
	
		this.SERVIZIO = new ComplexField(father,"servizio",java.lang.String.class,"operationType",OperationType.class);
		this.OPERAZIONE = new ComplexField(father,"operazione",java.lang.String.class,"operationType",OperationType.class);
		this.PROFILO_DI_COLLABORAZIONE = new ComplexField(father,"profiloDiCollaborazione",java.lang.String.class,"operationType",OperationType.class);
		this.SERVIZIO_CORRELATO = new ComplexField(father,"servizioCorrelato",java.lang.String.class,"operationType",OperationType.class);
		this.OPERAZIONE_CORRELATA = new ComplexField(father,"operazioneCorrelata",java.lang.String.class,"operationType",OperationType.class);
	
	}
	
	

	public IField SERVIZIO = null;
	 
	public IField OPERAZIONE = null;
	 
	public IField PROFILO_DI_COLLABORAZIONE = null;
	 
	public IField SERVIZIO_CORRELATO = null;
	 
	public IField OPERAZIONE_CORRELATA = null;
	 

	@Override
	public Class<OperationType> getModeledClass(){
		return OperationType.class;
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