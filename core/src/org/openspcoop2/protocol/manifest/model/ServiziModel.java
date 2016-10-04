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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.Servizi;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Servizi 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiziModel extends AbstractModel<Servizi> {

	public ServiziModel(){
	
		super();
	
		this.TIPI = new org.openspcoop2.protocol.manifest.model.TipiModel(new Field("tipi",org.openspcoop2.protocol.manifest.Tipi.class,"servizi",Servizi.class));
		this.PROFILO = new org.openspcoop2.protocol.manifest.model.ProfiloModel(new Field("profilo",org.openspcoop2.protocol.manifest.Profilo.class,"servizi",Servizi.class));
		this.FUNZIONALITA = new org.openspcoop2.protocol.manifest.model.FunzionalitaModel(new Field("funzionalita",org.openspcoop2.protocol.manifest.Funzionalita.class,"servizi",Servizi.class));
		this.WSDL_DEFINITORIO = new Field("wsdlDefinitorio",boolean.class,"servizi",Servizi.class);
		this.SPECIFICA_CONVERSAZIONI = new Field("specificaConversazioni",boolean.class,"servizi",Servizi.class);
	
	}
	
	public ServiziModel(IField father){
	
		super(father);
	
		this.TIPI = new org.openspcoop2.protocol.manifest.model.TipiModel(new ComplexField(father,"tipi",org.openspcoop2.protocol.manifest.Tipi.class,"servizi",Servizi.class));
		this.PROFILO = new org.openspcoop2.protocol.manifest.model.ProfiloModel(new ComplexField(father,"profilo",org.openspcoop2.protocol.manifest.Profilo.class,"servizi",Servizi.class));
		this.FUNZIONALITA = new org.openspcoop2.protocol.manifest.model.FunzionalitaModel(new ComplexField(father,"funzionalita",org.openspcoop2.protocol.manifest.Funzionalita.class,"servizi",Servizi.class));
		this.WSDL_DEFINITORIO = new ComplexField(father,"wsdlDefinitorio",boolean.class,"servizi",Servizi.class);
		this.SPECIFICA_CONVERSAZIONI = new ComplexField(father,"specificaConversazioni",boolean.class,"servizi",Servizi.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.TipiModel TIPI = null;
	 
	public org.openspcoop2.protocol.manifest.model.ProfiloModel PROFILO = null;
	 
	public org.openspcoop2.protocol.manifest.model.FunzionalitaModel FUNZIONALITA = null;
	 
	public IField WSDL_DEFINITORIO = null;
	 
	public IField SPECIFICA_CONVERSAZIONI = null;
	 

	@Override
	public Class<Servizi> getModeledClass(){
		return Servizi.class;
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