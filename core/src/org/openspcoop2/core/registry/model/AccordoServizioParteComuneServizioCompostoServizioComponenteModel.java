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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoServizioParteComuneServizioCompostoServizioComponente 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneServizioCompostoServizioComponenteModel extends AbstractModel<AccordoServizioParteComuneServizioCompostoServizioComponente> {

	public AccordoServizioParteComuneServizioCompostoServizioComponenteModel(){
	
		super();
	
		this.ID_SERVIZIO_COMPONENTE = new Field("id-servizio-componente",java.lang.Long.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
		this.TIPO_SOGGETTO = new Field("tipo-soggetto",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
		this.NOME_SOGGETTO = new Field("nome-soggetto",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
		this.NOME = new Field("nome",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
		this.AZIONE = new Field("azione",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	
	}
	
	public AccordoServizioParteComuneServizioCompostoServizioComponenteModel(IField father){
	
		super(father);
	
		this.ID_SERVIZIO_COMPONENTE = new ComplexField(father,"id-servizio-componente",java.lang.Long.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
		this.TIPO_SOGGETTO = new ComplexField(father,"tipo-soggetto",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
		this.NOME_SOGGETTO = new ComplexField(father,"nome-soggetto",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"accordo-servizio-parte-comune-servizio-composto-servizio-componente",AccordoServizioParteComuneServizioCompostoServizioComponente.class);
	
	}
	
	

	public IField ID_SERVIZIO_COMPONENTE = null;
	 
	public IField TIPO_SOGGETTO = null;
	 
	public IField NOME_SOGGETTO = null;
	 
	public IField TIPO = null;
	 
	public IField NOME = null;
	 
	public IField AZIONE = null;
	 

	@Override
	public Class<AccordoServizioParteComuneServizioCompostoServizioComponente> getModeledClass(){
		return AccordoServizioParteComuneServizioCompostoServizioComponente.class;
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