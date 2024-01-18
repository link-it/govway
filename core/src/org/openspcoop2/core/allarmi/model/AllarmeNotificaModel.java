/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.allarmi.model;

import org.openspcoop2.core.allarmi.AllarmeNotifica;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AllarmeNotifica 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeNotificaModel extends AbstractModel<AllarmeNotifica> {

	public AllarmeNotificaModel(){
	
		super();
	
		this.DATA_NOTIFICA = new Field("data-notifica",java.util.Date.class,"allarme-notifica",AllarmeNotifica.class);
		this.ID_ALLARME = new org.openspcoop2.core.allarmi.model.IdAllarmeModel(new Field("id-allarme",org.openspcoop2.core.allarmi.IdAllarme.class,"allarme-notifica",AllarmeNotifica.class));
		this.OLD_STATO = new Field("old-stato",java.lang.Integer.class,"allarme-notifica",AllarmeNotifica.class);
		this.OLD_DETTAGLIO_STATO = new Field("old-dettaglio-stato",java.lang.String.class,"allarme-notifica",AllarmeNotifica.class);
		this.NUOVO_STATO = new Field("nuovo-stato",java.lang.Integer.class,"allarme-notifica",AllarmeNotifica.class);
		this.NUOVO_DETTAGLIO_STATO = new Field("nuovo-dettaglio-stato",java.lang.String.class,"allarme-notifica",AllarmeNotifica.class);
		this.HISTORY_ENTRY = new Field("history-entry",java.lang.String.class,"allarme-notifica",AllarmeNotifica.class);
	
	}
	
	public AllarmeNotificaModel(IField father){
	
		super(father);
	
		this.DATA_NOTIFICA = new ComplexField(father,"data-notifica",java.util.Date.class,"allarme-notifica",AllarmeNotifica.class);
		this.ID_ALLARME = new org.openspcoop2.core.allarmi.model.IdAllarmeModel(new ComplexField(father,"id-allarme",org.openspcoop2.core.allarmi.IdAllarme.class,"allarme-notifica",AllarmeNotifica.class));
		this.OLD_STATO = new ComplexField(father,"old-stato",java.lang.Integer.class,"allarme-notifica",AllarmeNotifica.class);
		this.OLD_DETTAGLIO_STATO = new ComplexField(father,"old-dettaglio-stato",java.lang.String.class,"allarme-notifica",AllarmeNotifica.class);
		this.NUOVO_STATO = new ComplexField(father,"nuovo-stato",java.lang.Integer.class,"allarme-notifica",AllarmeNotifica.class);
		this.NUOVO_DETTAGLIO_STATO = new ComplexField(father,"nuovo-dettaglio-stato",java.lang.String.class,"allarme-notifica",AllarmeNotifica.class);
		this.HISTORY_ENTRY = new ComplexField(father,"history-entry",java.lang.String.class,"allarme-notifica",AllarmeNotifica.class);
	
	}
	
	

	public IField DATA_NOTIFICA = null;
	 
	public org.openspcoop2.core.allarmi.model.IdAllarmeModel ID_ALLARME = null;
	 
	public IField OLD_STATO = null;
	 
	public IField OLD_DETTAGLIO_STATO = null;
	 
	public IField NUOVO_STATO = null;
	 
	public IField NUOVO_DETTAGLIO_STATO = null;
	 
	public IField HISTORY_ENTRY = null;
	 

	@Override
	public Class<AllarmeNotifica> getModeledClass(){
		return AllarmeNotifica.class;
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