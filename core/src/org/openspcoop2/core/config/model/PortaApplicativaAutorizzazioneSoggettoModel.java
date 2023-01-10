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

import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaApplicativaAutorizzazioneSoggetto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaAutorizzazioneSoggettoModel extends AbstractModel<PortaApplicativaAutorizzazioneSoggetto> {

	public PortaApplicativaAutorizzazioneSoggettoModel(){
	
		super();
	
		this.TIPO = new Field("tipo",java.lang.String.class,"porta-applicativa-autorizzazione-soggetto",PortaApplicativaAutorizzazioneSoggetto.class);
		this.NOME = new Field("nome",java.lang.String.class,"porta-applicativa-autorizzazione-soggetto",PortaApplicativaAutorizzazioneSoggetto.class);
	
	}
	
	public PortaApplicativaAutorizzazioneSoggettoModel(IField father){
	
		super(father);
	
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"porta-applicativa-autorizzazione-soggetto",PortaApplicativaAutorizzazioneSoggetto.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-applicativa-autorizzazione-soggetto",PortaApplicativaAutorizzazioneSoggetto.class);
	
	}
	
	

	public IField TIPO = null;
	 
	public IField NOME = null;
	 

	@Override
	public Class<PortaApplicativaAutorizzazioneSoggetto> getModeledClass(){
		return PortaApplicativaAutorizzazioneSoggetto.class;
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