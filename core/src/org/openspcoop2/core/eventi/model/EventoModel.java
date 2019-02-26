/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.eventi.model;

import org.openspcoop2.core.eventi.Evento;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Evento 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EventoModel extends AbstractModel<Evento> {

	public EventoModel(){
	
		super();
	
		this.TIPO = new Field("tipo",java.lang.String.class,"evento",Evento.class);
		this.CODICE = new Field("codice",java.lang.String.class,"evento",Evento.class);
		this.SEVERITA = new Field("severita",int.class,"evento",Evento.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"evento",Evento.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"evento",Evento.class);
		this.ID_TRANSAZIONE = new Field("id-transazione",java.lang.String.class,"evento",Evento.class);
		this.ID_CONFIGURAZIONE = new Field("id-configurazione",java.lang.String.class,"evento",Evento.class);
		this.CONFIGURAZIONE = new Field("configurazione",java.lang.String.class,"evento",Evento.class);
		this.CLUSTER_ID = new Field("cluster-id",java.lang.String.class,"evento",Evento.class);
	
	}
	
	public EventoModel(IField father){
	
		super(father);
	
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"evento",Evento.class);
		this.CODICE = new ComplexField(father,"codice",java.lang.String.class,"evento",Evento.class);
		this.SEVERITA = new ComplexField(father,"severita",int.class,"evento",Evento.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"evento",Evento.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"evento",Evento.class);
		this.ID_TRANSAZIONE = new ComplexField(father,"id-transazione",java.lang.String.class,"evento",Evento.class);
		this.ID_CONFIGURAZIONE = new ComplexField(father,"id-configurazione",java.lang.String.class,"evento",Evento.class);
		this.CONFIGURAZIONE = new ComplexField(father,"configurazione",java.lang.String.class,"evento",Evento.class);
		this.CLUSTER_ID = new ComplexField(father,"cluster-id",java.lang.String.class,"evento",Evento.class);
	
	}
	
	

	public IField TIPO = null;
	 
	public IField CODICE = null;
	 
	public IField SEVERITA = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField ID_TRANSAZIONE = null;
	 
	public IField ID_CONFIGURAZIONE = null;
	 
	public IField CONFIGURAZIONE = null;
	 
	public IField CLUSTER_ID = null;
	 

	@Override
	public Class<Evento> getModeledClass(){
		return Evento.class;
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