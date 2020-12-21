/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.allarmi.AllarmeHistory;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AllarmeHistory 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeHistoryModel extends AbstractModel<AllarmeHistory> {

	public AllarmeHistoryModel(){
	
		super();
	
		this.ID_ALLARME = new org.openspcoop2.core.allarmi.model.IdAllarmeModel(new Field("id-allarme",org.openspcoop2.core.allarmi.IdAllarme.class,"allarme-history",AllarmeHistory.class));
		this.ENABLED = new Field("enabled",java.lang.Integer.class,"allarme-history",AllarmeHistory.class);
		this.STATO = new Field("stato",java.lang.Integer.class,"allarme-history",AllarmeHistory.class);
		this.DETTAGLIO_STATO = new Field("dettaglio-stato",java.lang.String.class,"allarme-history",AllarmeHistory.class);
		this.ACKNOWLEDGED = new Field("acknowledged",java.lang.Integer.class,"allarme-history",AllarmeHistory.class);
		this.TIMESTAMP_UPDATE = new Field("timestamp-update",java.util.Date.class,"allarme-history",AllarmeHistory.class);
		this.UTENTE = new Field("utente",java.lang.String.class,"allarme-history",AllarmeHistory.class);
	
	}
	
	public AllarmeHistoryModel(IField father){
	
		super(father);
	
		this.ID_ALLARME = new org.openspcoop2.core.allarmi.model.IdAllarmeModel(new ComplexField(father,"id-allarme",org.openspcoop2.core.allarmi.IdAllarme.class,"allarme-history",AllarmeHistory.class));
		this.ENABLED = new ComplexField(father,"enabled",java.lang.Integer.class,"allarme-history",AllarmeHistory.class);
		this.STATO = new ComplexField(father,"stato",java.lang.Integer.class,"allarme-history",AllarmeHistory.class);
		this.DETTAGLIO_STATO = new ComplexField(father,"dettaglio-stato",java.lang.String.class,"allarme-history",AllarmeHistory.class);
		this.ACKNOWLEDGED = new ComplexField(father,"acknowledged",java.lang.Integer.class,"allarme-history",AllarmeHistory.class);
		this.TIMESTAMP_UPDATE = new ComplexField(father,"timestamp-update",java.util.Date.class,"allarme-history",AllarmeHistory.class);
		this.UTENTE = new ComplexField(father,"utente",java.lang.String.class,"allarme-history",AllarmeHistory.class);
	
	}
	
	

	public org.openspcoop2.core.allarmi.model.IdAllarmeModel ID_ALLARME = null;
	 
	public IField ENABLED = null;
	 
	public IField STATO = null;
	 
	public IField DETTAGLIO_STATO = null;
	 
	public IField ACKNOWLEDGED = null;
	 
	public IField TIMESTAMP_UPDATE = null;
	 
	public IField UTENTE = null;
	 

	@Override
	public Class<AllarmeHistory> getModeledClass(){
		return AllarmeHistory.class;
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