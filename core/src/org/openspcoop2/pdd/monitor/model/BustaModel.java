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

import org.openspcoop2.pdd.monitor.Busta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Busta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BustaModel extends AbstractModel<Busta> {

	public BustaModel(){
	
		super();
	
		this.ATTESA_RISCONTRO = new Field("attesa-riscontro",boolean.class,"busta",Busta.class);
		this.MITTENTE = new org.openspcoop2.pdd.monitor.model.BustaSoggettoModel(new Field("mittente",org.openspcoop2.pdd.monitor.BustaSoggetto.class,"busta",Busta.class));
		this.DESTINATARIO = new org.openspcoop2.pdd.monitor.model.BustaSoggettoModel(new Field("destinatario",org.openspcoop2.pdd.monitor.BustaSoggetto.class,"busta",Busta.class));
		this.SERVIZIO = new org.openspcoop2.pdd.monitor.model.BustaServizioModel(new Field("servizio",org.openspcoop2.pdd.monitor.BustaServizio.class,"busta",Busta.class));
		this.AZIONE = new Field("azione",java.lang.String.class,"busta",Busta.class);
		this.PROFILO_COLLABORAZIONE = new Field("profilo-collaborazione",java.lang.String.class,"busta",Busta.class);
		this.COLLABORAZIONE = new Field("collaborazione",java.lang.String.class,"busta",Busta.class);
		this.RIFERIMENTO_MESSAGGIO = new Field("riferimento-messaggio",java.lang.String.class,"busta",Busta.class);
	
	}
	
	public BustaModel(IField father){
	
		super(father);
	
		this.ATTESA_RISCONTRO = new ComplexField(father,"attesa-riscontro",boolean.class,"busta",Busta.class);
		this.MITTENTE = new org.openspcoop2.pdd.monitor.model.BustaSoggettoModel(new ComplexField(father,"mittente",org.openspcoop2.pdd.monitor.BustaSoggetto.class,"busta",Busta.class));
		this.DESTINATARIO = new org.openspcoop2.pdd.monitor.model.BustaSoggettoModel(new ComplexField(father,"destinatario",org.openspcoop2.pdd.monitor.BustaSoggetto.class,"busta",Busta.class));
		this.SERVIZIO = new org.openspcoop2.pdd.monitor.model.BustaServizioModel(new ComplexField(father,"servizio",org.openspcoop2.pdd.monitor.BustaServizio.class,"busta",Busta.class));
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"busta",Busta.class);
		this.PROFILO_COLLABORAZIONE = new ComplexField(father,"profilo-collaborazione",java.lang.String.class,"busta",Busta.class);
		this.COLLABORAZIONE = new ComplexField(father,"collaborazione",java.lang.String.class,"busta",Busta.class);
		this.RIFERIMENTO_MESSAGGIO = new ComplexField(father,"riferimento-messaggio",java.lang.String.class,"busta",Busta.class);
	
	}
	
	

	public IField ATTESA_RISCONTRO = null;
	 
	public org.openspcoop2.pdd.monitor.model.BustaSoggettoModel MITTENTE = null;
	 
	public org.openspcoop2.pdd.monitor.model.BustaSoggettoModel DESTINATARIO = null;
	 
	public org.openspcoop2.pdd.monitor.model.BustaServizioModel SERVIZIO = null;
	 
	public IField AZIONE = null;
	 
	public IField PROFILO_COLLABORAZIONE = null;
	 
	public IField COLLABORAZIONE = null;
	 
	public IField RIFERIMENTO_MESSAGGIO = null;
	 

	@Override
	public Class<Busta> getModeledClass(){
		return Busta.class;
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