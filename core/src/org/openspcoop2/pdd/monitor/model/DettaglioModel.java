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

import org.openspcoop2.pdd.monitor.Dettaglio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Dettaglio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DettaglioModel extends AbstractModel<Dettaglio> {

	public DettaglioModel(){
	
		super();
	
		this.ERRORE_PROCESSAMENTO = new Field("errore-processamento",java.lang.String.class,"dettaglio",Dettaglio.class);
		this.ID_CORRELAZIONE_APPLICATIVA = new Field("id-correlazione-applicativa",java.lang.String.class,"dettaglio",Dettaglio.class);
		this.ID_MODULO = new Field("id-modulo",java.lang.String.class,"dettaglio",Dettaglio.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"dettaglio",Dettaglio.class);
		this.SERVIZIO_APPLICATIVO_CONSEGNA = new org.openspcoop2.pdd.monitor.model.ServizioApplicativoConsegnaModel(new Field("servizio-applicativo-consegna",org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna.class,"dettaglio",Dettaglio.class));
		this.PROPRIETA = new org.openspcoop2.pdd.monitor.model.ProprietaModel(new Field("proprieta",org.openspcoop2.pdd.monitor.Proprieta.class,"dettaglio",Dettaglio.class));
	
	}
	
	public DettaglioModel(IField father){
	
		super(father);
	
		this.ERRORE_PROCESSAMENTO = new ComplexField(father,"errore-processamento",java.lang.String.class,"dettaglio",Dettaglio.class);
		this.ID_CORRELAZIONE_APPLICATIVA = new ComplexField(father,"id-correlazione-applicativa",java.lang.String.class,"dettaglio",Dettaglio.class);
		this.ID_MODULO = new ComplexField(father,"id-modulo",java.lang.String.class,"dettaglio",Dettaglio.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"dettaglio",Dettaglio.class);
		this.SERVIZIO_APPLICATIVO_CONSEGNA = new org.openspcoop2.pdd.monitor.model.ServizioApplicativoConsegnaModel(new ComplexField(father,"servizio-applicativo-consegna",org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna.class,"dettaglio",Dettaglio.class));
		this.PROPRIETA = new org.openspcoop2.pdd.monitor.model.ProprietaModel(new ComplexField(father,"proprieta",org.openspcoop2.pdd.monitor.Proprieta.class,"dettaglio",Dettaglio.class));
	
	}
	
	

	public IField ERRORE_PROCESSAMENTO = null;
	 
	public IField ID_CORRELAZIONE_APPLICATIVA = null;
	 
	public IField ID_MODULO = null;
	 
	public IField TIPO = null;
	 
	public org.openspcoop2.pdd.monitor.model.ServizioApplicativoConsegnaModel SERVIZIO_APPLICATIVO_CONSEGNA = null;
	 
	public org.openspcoop2.pdd.monitor.model.ProprietaModel PROPRIETA = null;
	 

	@Override
	public Class<Dettaglio> getModeledClass(){
		return Dettaglio.class;
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