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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.ProprietaOggetto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ProprietaOggetto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProprietaOggettoModel extends AbstractModel<ProprietaOggetto> {

	public ProprietaOggettoModel(){
	
		super();
	
		this.UTENTE_RICHIEDENTE = new Field("utente-richiedente",java.lang.String.class,"proprieta-oggetto",ProprietaOggetto.class);
		this.DATA_CREAZIONE = new Field("data-creazione",java.util.Date.class,"proprieta-oggetto",ProprietaOggetto.class);
		this.UTENTE_ULTIMA_MODIFICA = new Field("utente-ultima-modifica",java.lang.String.class,"proprieta-oggetto",ProprietaOggetto.class);
		this.DATA_ULTIMA_MODIFICA = new Field("data-ultima-modifica",java.util.Date.class,"proprieta-oggetto",ProprietaOggetto.class);
	
	}
	
	public ProprietaOggettoModel(IField father){
	
		super(father);
	
		this.UTENTE_RICHIEDENTE = new ComplexField(father,"utente-richiedente",java.lang.String.class,"proprieta-oggetto",ProprietaOggetto.class);
		this.DATA_CREAZIONE = new ComplexField(father,"data-creazione",java.util.Date.class,"proprieta-oggetto",ProprietaOggetto.class);
		this.UTENTE_ULTIMA_MODIFICA = new ComplexField(father,"utente-ultima-modifica",java.lang.String.class,"proprieta-oggetto",ProprietaOggetto.class);
		this.DATA_ULTIMA_MODIFICA = new ComplexField(father,"data-ultima-modifica",java.util.Date.class,"proprieta-oggetto",ProprietaOggetto.class);
	
	}
	
	

	public IField UTENTE_RICHIEDENTE = null;
	 
	public IField DATA_CREAZIONE = null;
	 
	public IField UTENTE_ULTIMA_MODIFICA = null;
	 
	public IField DATA_ULTIMA_MODIFICA = null;
	 

	@Override
	public Class<ProprietaOggetto> getModeledClass(){
		return ProprietaOggetto.class;
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