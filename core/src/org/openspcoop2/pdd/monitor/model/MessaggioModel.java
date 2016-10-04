/*
 * OpenSPCoop - Customizable API Gateway 
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

import org.openspcoop2.pdd.monitor.Messaggio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Messaggio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessaggioModel extends AbstractModel<Messaggio> {

	public MessaggioModel(){
	
		super();
	
		this.DETTAGLIO = new org.openspcoop2.pdd.monitor.model.DettaglioModel(new Field("dettaglio",org.openspcoop2.pdd.monitor.Dettaglio.class,"messaggio",Messaggio.class));
		this.BUSTA_INFO = new org.openspcoop2.pdd.monitor.model.BustaModel(new Field("busta-info",org.openspcoop2.pdd.monitor.Busta.class,"messaggio",Messaggio.class));
		this.ID_MESSAGGIO = new Field("id-messaggio",java.lang.String.class,"messaggio",Messaggio.class);
		this.ORA_ATTUALE = new Field("ora-attuale",java.util.Date.class,"messaggio",Messaggio.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"messaggio",Messaggio.class);
		this.STATO = new Field("stato",java.lang.String.class,"messaggio",Messaggio.class);
		this.FILTRO = new org.openspcoop2.pdd.monitor.model.FiltroModel(new Field("filtro",org.openspcoop2.pdd.monitor.Filtro.class,"messaggio",Messaggio.class));
	
	}
	
	public MessaggioModel(IField father){
	
		super(father);
	
		this.DETTAGLIO = new org.openspcoop2.pdd.monitor.model.DettaglioModel(new ComplexField(father,"dettaglio",org.openspcoop2.pdd.monitor.Dettaglio.class,"messaggio",Messaggio.class));
		this.BUSTA_INFO = new org.openspcoop2.pdd.monitor.model.BustaModel(new ComplexField(father,"busta-info",org.openspcoop2.pdd.monitor.Busta.class,"messaggio",Messaggio.class));
		this.ID_MESSAGGIO = new ComplexField(father,"id-messaggio",java.lang.String.class,"messaggio",Messaggio.class);
		this.ORA_ATTUALE = new ComplexField(father,"ora-attuale",java.util.Date.class,"messaggio",Messaggio.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"messaggio",Messaggio.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"messaggio",Messaggio.class);
		this.FILTRO = new org.openspcoop2.pdd.monitor.model.FiltroModel(new ComplexField(father,"filtro",org.openspcoop2.pdd.monitor.Filtro.class,"messaggio",Messaggio.class));
	
	}
	
	

	public org.openspcoop2.pdd.monitor.model.DettaglioModel DETTAGLIO = null;
	 
	public org.openspcoop2.pdd.monitor.model.BustaModel BUSTA_INFO = null;
	 
	public IField ID_MESSAGGIO = null;
	 
	public IField ORA_ATTUALE = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField STATO = null;
	 
	public org.openspcoop2.pdd.monitor.model.FiltroModel FILTRO = null;
	 

	@Override
	public Class<Messaggio> getModeledClass(){
		return Messaggio.class;
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