/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaApplicativaServizioApplicativoConnettore 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaServizioApplicativoConnettoreModel extends AbstractModel<PortaApplicativaServizioApplicativoConnettore> {

	public PortaApplicativaServizioApplicativoConnettoreModel(){
	
		super();
	
		this.FILTRO = new Field("filtro",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.PROPRIETA = new org.openspcoop2.core.config.model.ProprietaModel(new Field("proprieta",org.openspcoop2.core.config.Proprieta.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class));
		this.NOME = new Field("nome",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.NOTIFICA = new Field("notifica",boolean.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.STATO = new Field("stato",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.CODA = new Field("coda",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.PRIORITA = new Field("priorita",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.PRIORITA_MAX = new Field("priorita-max",boolean.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
	
	}
	
	public PortaApplicativaServizioApplicativoConnettoreModel(IField father){
	
		super(father);
	
		this.FILTRO = new ComplexField(father,"filtro",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.PROPRIETA = new org.openspcoop2.core.config.model.ProprietaModel(new ComplexField(father,"proprieta",org.openspcoop2.core.config.Proprieta.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.NOTIFICA = new ComplexField(father,"notifica",boolean.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.CODA = new ComplexField(father,"coda",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.PRIORITA = new ComplexField(father,"priorita",java.lang.String.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
		this.PRIORITA_MAX = new ComplexField(father,"priorita-max",boolean.class,"porta-applicativa-servizio-applicativo-connettore",PortaApplicativaServizioApplicativoConnettore.class);
	
	}
	
	

	public IField FILTRO = null;
	 
	public org.openspcoop2.core.config.model.ProprietaModel PROPRIETA = null;
	 
	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField NOTIFICA = null;
	 
	public IField STATO = null;
	 
	public IField CODA = null;
	 
	public IField PRIORITA = null;
	 
	public IField PRIORITA_MAX = null;
	 

	@Override
	public Class<PortaApplicativaServizioApplicativoConnettore> getModeledClass(){
		return PortaApplicativaServizioApplicativoConnettore.class;
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