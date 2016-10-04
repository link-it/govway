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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoServizioParteComuneServizioComposto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneServizioCompostoModel extends AbstractModel<AccordoServizioParteComuneServizioComposto> {

	public AccordoServizioParteComuneServizioCompostoModel(){
	
		super();
	
		this.SERVIZIO_COMPONENTE = new org.openspcoop2.core.registry.model.AccordoServizioParteComuneServizioCompostoServizioComponenteModel(new Field("servizio-componente",org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente.class,"accordo-servizio-parte-comune-servizio-composto",AccordoServizioParteComuneServizioComposto.class));
		this.SPECIFICA_COORDINAMENTO = new org.openspcoop2.core.registry.model.DocumentoModel(new Field("specifica-coordinamento",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-comune-servizio-composto",AccordoServizioParteComuneServizioComposto.class));
		this.ID_ACCORDO_COOPERAZIONE = new Field("id-accordo-cooperazione",java.lang.Long.class,"accordo-servizio-parte-comune-servizio-composto",AccordoServizioParteComuneServizioComposto.class);
		this.ACCORDO_COOPERAZIONE = new Field("accordo-cooperazione",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto",AccordoServizioParteComuneServizioComposto.class);
	
	}
	
	public AccordoServizioParteComuneServizioCompostoModel(IField father){
	
		super(father);
	
		this.SERVIZIO_COMPONENTE = new org.openspcoop2.core.registry.model.AccordoServizioParteComuneServizioCompostoServizioComponenteModel(new ComplexField(father,"servizio-componente",org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente.class,"accordo-servizio-parte-comune-servizio-composto",AccordoServizioParteComuneServizioComposto.class));
		this.SPECIFICA_COORDINAMENTO = new org.openspcoop2.core.registry.model.DocumentoModel(new ComplexField(father,"specifica-coordinamento",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-comune-servizio-composto",AccordoServizioParteComuneServizioComposto.class));
		this.ID_ACCORDO_COOPERAZIONE = new ComplexField(father,"id-accordo-cooperazione",java.lang.Long.class,"accordo-servizio-parte-comune-servizio-composto",AccordoServizioParteComuneServizioComposto.class);
		this.ACCORDO_COOPERAZIONE = new ComplexField(father,"accordo-cooperazione",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto",AccordoServizioParteComuneServizioComposto.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.AccordoServizioParteComuneServizioCompostoServizioComponenteModel SERVIZIO_COMPONENTE = null;
	 
	public org.openspcoop2.core.registry.model.DocumentoModel SPECIFICA_COORDINAMENTO = null;
	 
	public IField ID_ACCORDO_COOPERAZIONE = null;
	 
	public IField ACCORDO_COOPERAZIONE = null;
	 

	@Override
	public Class<AccordoServizioParteComuneServizioComposto> getModeledClass(){
		return AccordoServizioParteComuneServizioComposto.class;
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