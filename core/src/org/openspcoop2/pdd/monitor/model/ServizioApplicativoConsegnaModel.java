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
package org.openspcoop2.pdd.monitor.model;

import org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ServizioApplicativoConsegna 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoConsegnaModel extends AbstractModel<ServizioApplicativoConsegna> {

	public ServizioApplicativoConsegnaModel(){
	
		super();
	
		this.AUTORIZZAZIONE_INTEGRATION_MANAGER = new Field("autorizzazione-integration-manager",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.ERRORE_PROCESSAMENTO = new Field("errore-processamento",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.NOME = new Field("nome",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.SBUSTAMENTO_SOAP = new Field("sbustamento-soap",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.TIPO_CONSEGNA = new Field("tipo-consegna",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
	
	}
	
	public ServizioApplicativoConsegnaModel(IField father){
	
		super(father);
	
		this.AUTORIZZAZIONE_INTEGRATION_MANAGER = new ComplexField(father,"autorizzazione-integration-manager",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.ERRORE_PROCESSAMENTO = new ComplexField(father,"errore-processamento",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.SBUSTAMENTO_SOAP = new ComplexField(father,"sbustamento-soap",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.TIPO_CONSEGNA = new ComplexField(father,"tipo-consegna",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
	
	}
	
	

	public IField AUTORIZZAZIONE_INTEGRATION_MANAGER = null;
	 
	public IField ERRORE_PROCESSAMENTO = null;
	 
	public IField NOME = null;
	 
	public IField SBUSTAMENTO_SOAP = null;
	 
	public IField TIPO_CONSEGNA = null;
	 

	@Override
	public Class<ServizioApplicativoConsegna> getModeledClass(){
		return ServizioApplicativoConsegna.class;
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