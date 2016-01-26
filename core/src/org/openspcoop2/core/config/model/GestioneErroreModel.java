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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.GestioneErrore;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model GestioneErrore 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestioneErroreModel extends AbstractModel<GestioneErrore> {

	public GestioneErroreModel(){
	
		super();
	
		this.CODICE_TRASPORTO = new org.openspcoop2.core.config.model.GestioneErroreCodiceTrasportoModel(new Field("codice-trasporto",org.openspcoop2.core.config.GestioneErroreCodiceTrasporto.class,"gestione-errore",GestioneErrore.class));
		this.SOAP_FAULT = new org.openspcoop2.core.config.model.GestioneErroreSoapFaultModel(new Field("soap-fault",org.openspcoop2.core.config.GestioneErroreSoapFault.class,"gestione-errore",GestioneErrore.class));
		this.NOME = new Field("nome",java.lang.String.class,"gestione-errore",GestioneErrore.class);
		this.COMPORTAMENTO = new Field("comportamento",java.lang.String.class,"gestione-errore",GestioneErrore.class);
		this.CADENZA_RISPEDIZIONE = new Field("cadenza-rispedizione",java.lang.String.class,"gestione-errore",GestioneErrore.class);
	
	}
	
	public GestioneErroreModel(IField father){
	
		super(father);
	
		this.CODICE_TRASPORTO = new org.openspcoop2.core.config.model.GestioneErroreCodiceTrasportoModel(new ComplexField(father,"codice-trasporto",org.openspcoop2.core.config.GestioneErroreCodiceTrasporto.class,"gestione-errore",GestioneErrore.class));
		this.SOAP_FAULT = new org.openspcoop2.core.config.model.GestioneErroreSoapFaultModel(new ComplexField(father,"soap-fault",org.openspcoop2.core.config.GestioneErroreSoapFault.class,"gestione-errore",GestioneErrore.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"gestione-errore",GestioneErrore.class);
		this.COMPORTAMENTO = new ComplexField(father,"comportamento",java.lang.String.class,"gestione-errore",GestioneErrore.class);
		this.CADENZA_RISPEDIZIONE = new ComplexField(father,"cadenza-rispedizione",java.lang.String.class,"gestione-errore",GestioneErrore.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.GestioneErroreCodiceTrasportoModel CODICE_TRASPORTO = null;
	 
	public org.openspcoop2.core.config.model.GestioneErroreSoapFaultModel SOAP_FAULT = null;
	 
	public IField NOME = null;
	 
	public IField COMPORTAMENTO = null;
	 
	public IField CADENZA_RISPEDIZIONE = null;
	 

	@Override
	public Class<GestioneErrore> getModeledClass(){
		return GestioneErrore.class;
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