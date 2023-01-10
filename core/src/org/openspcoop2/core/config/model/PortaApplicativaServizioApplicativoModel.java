/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaApplicativaServizioApplicativo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaServizioApplicativoModel extends AbstractModel<PortaApplicativaServizioApplicativo> {

	public PortaApplicativaServizioApplicativoModel(){
	
		super();
	
		this.DATI_CONNETTORE = new org.openspcoop2.core.config.model.PortaApplicativaServizioApplicativoConnettoreModel(new Field("dati-connettore",org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore.class,"porta-applicativa-servizio-applicativo",PortaApplicativaServizioApplicativo.class));
		this.NOME = new Field("nome",java.lang.String.class,"porta-applicativa-servizio-applicativo",PortaApplicativaServizioApplicativo.class);
		this.ID_SERVIZIO_APPLICATIVO = new Field("id-servizio-applicativo",java.lang.Long.class,"porta-applicativa-servizio-applicativo",PortaApplicativaServizioApplicativo.class);
	
	}
	
	public PortaApplicativaServizioApplicativoModel(IField father){
	
		super(father);
	
		this.DATI_CONNETTORE = new org.openspcoop2.core.config.model.PortaApplicativaServizioApplicativoConnettoreModel(new ComplexField(father,"dati-connettore",org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore.class,"porta-applicativa-servizio-applicativo",PortaApplicativaServizioApplicativo.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-applicativa-servizio-applicativo",PortaApplicativaServizioApplicativo.class);
		this.ID_SERVIZIO_APPLICATIVO = new ComplexField(father,"id-servizio-applicativo",java.lang.Long.class,"porta-applicativa-servizio-applicativo",PortaApplicativaServizioApplicativo.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.PortaApplicativaServizioApplicativoConnettoreModel DATI_CONNETTORE = null;
	 
	public IField NOME = null;
	 
	public IField ID_SERVIZIO_APPLICATIVO = null;
	 

	@Override
	public Class<PortaApplicativaServizioApplicativo> getModeledClass(){
		return PortaApplicativaServizioApplicativo.class;
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