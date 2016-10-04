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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model GestioneErroreCodiceTrasporto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestioneErroreCodiceTrasportoModel extends AbstractModel<GestioneErroreCodiceTrasporto> {

	public GestioneErroreCodiceTrasportoModel(){
	
		super();
	
		this.VALORE_MINIMO = new Field("valore-minimo",java.lang.Integer.class,"gestione-errore-codice-trasporto",GestioneErroreCodiceTrasporto.class);
		this.VALORE_MASSIMO = new Field("valore-massimo",java.lang.Integer.class,"gestione-errore-codice-trasporto",GestioneErroreCodiceTrasporto.class);
		this.COMPORTAMENTO = new Field("comportamento",java.lang.String.class,"gestione-errore-codice-trasporto",GestioneErroreCodiceTrasporto.class);
		this.CADENZA_RISPEDIZIONE = new Field("cadenza-rispedizione",java.lang.String.class,"gestione-errore-codice-trasporto",GestioneErroreCodiceTrasporto.class);
	
	}
	
	public GestioneErroreCodiceTrasportoModel(IField father){
	
		super(father);
	
		this.VALORE_MINIMO = new ComplexField(father,"valore-minimo",java.lang.Integer.class,"gestione-errore-codice-trasporto",GestioneErroreCodiceTrasporto.class);
		this.VALORE_MASSIMO = new ComplexField(father,"valore-massimo",java.lang.Integer.class,"gestione-errore-codice-trasporto",GestioneErroreCodiceTrasporto.class);
		this.COMPORTAMENTO = new ComplexField(father,"comportamento",java.lang.String.class,"gestione-errore-codice-trasporto",GestioneErroreCodiceTrasporto.class);
		this.CADENZA_RISPEDIZIONE = new ComplexField(father,"cadenza-rispedizione",java.lang.String.class,"gestione-errore-codice-trasporto",GestioneErroreCodiceTrasporto.class);
	
	}
	
	

	public IField VALORE_MINIMO = null;
	 
	public IField VALORE_MASSIMO = null;
	 
	public IField COMPORTAMENTO = null;
	 
	public IField CADENZA_RISPEDIZIONE = null;
	 

	@Override
	public Class<GestioneErroreCodiceTrasporto> getModeledClass(){
		return GestioneErroreCodiceTrasporto.class;
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