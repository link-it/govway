/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import org.openspcoop2.core.config.GestioneTokenAutenticazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model GestioneTokenAutenticazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestioneTokenAutenticazioneModel extends AbstractModel<GestioneTokenAutenticazione> {

	public GestioneTokenAutenticazioneModel(){
	
		super();
	
		this.ISSUER = new Field("issuer",java.lang.String.class,"gestione-token-autenticazione",GestioneTokenAutenticazione.class);
		this.CLIENT_ID = new Field("client-id",java.lang.String.class,"gestione-token-autenticazione",GestioneTokenAutenticazione.class);
		this.SUBJECT = new Field("subject",java.lang.String.class,"gestione-token-autenticazione",GestioneTokenAutenticazione.class);
		this.USERNAME = new Field("username",java.lang.String.class,"gestione-token-autenticazione",GestioneTokenAutenticazione.class);
		this.EMAIL = new Field("email",java.lang.String.class,"gestione-token-autenticazione",GestioneTokenAutenticazione.class);
	
	}
	
	public GestioneTokenAutenticazioneModel(IField father){
	
		super(father);
	
		this.ISSUER = new ComplexField(father,"issuer",java.lang.String.class,"gestione-token-autenticazione",GestioneTokenAutenticazione.class);
		this.CLIENT_ID = new ComplexField(father,"client-id",java.lang.String.class,"gestione-token-autenticazione",GestioneTokenAutenticazione.class);
		this.SUBJECT = new ComplexField(father,"subject",java.lang.String.class,"gestione-token-autenticazione",GestioneTokenAutenticazione.class);
		this.USERNAME = new ComplexField(father,"username",java.lang.String.class,"gestione-token-autenticazione",GestioneTokenAutenticazione.class);
		this.EMAIL = new ComplexField(father,"email",java.lang.String.class,"gestione-token-autenticazione",GestioneTokenAutenticazione.class);
	
	}
	
	

	public IField ISSUER = null;
	 
	public IField CLIENT_ID = null;
	 
	public IField SUBJECT = null;
	 
	public IField USERNAME = null;
	 
	public IField EMAIL = null;
	 

	@Override
	public Class<GestioneTokenAutenticazione> getModeledClass(){
		return GestioneTokenAutenticazione.class;
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