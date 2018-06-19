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

import org.openspcoop2.core.config.GestioneToken;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model GestioneToken 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestioneTokenModel extends AbstractModel<GestioneToken> {

	public GestioneTokenModel(){
	
		super();
	
		this.AUTENTICAZIONE = new org.openspcoop2.core.config.model.GestioneTokenAutenticazioneModel(new Field("autenticazione",org.openspcoop2.core.config.GestioneTokenAutenticazione.class,"gestione-token",GestioneToken.class));
		this.POLICY = new Field("policy",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.TOKEN_OPZIONALE = new Field("token-opzionale",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.VALIDAZIONE = new Field("validazione",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.INTROSPECTION = new Field("introspection",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.USER_INFO = new Field("userInfo",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.FORWARD = new Field("forward",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.OPTIONS = new Field("options",java.lang.String.class,"gestione-token",GestioneToken.class);
	
	}
	
	public GestioneTokenModel(IField father){
	
		super(father);
	
		this.AUTENTICAZIONE = new org.openspcoop2.core.config.model.GestioneTokenAutenticazioneModel(new ComplexField(father,"autenticazione",org.openspcoop2.core.config.GestioneTokenAutenticazione.class,"gestione-token",GestioneToken.class));
		this.POLICY = new ComplexField(father,"policy",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.TOKEN_OPZIONALE = new ComplexField(father,"token-opzionale",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.VALIDAZIONE = new ComplexField(father,"validazione",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.INTROSPECTION = new ComplexField(father,"introspection",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.USER_INFO = new ComplexField(father,"userInfo",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.FORWARD = new ComplexField(father,"forward",java.lang.String.class,"gestione-token",GestioneToken.class);
		this.OPTIONS = new ComplexField(father,"options",java.lang.String.class,"gestione-token",GestioneToken.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.GestioneTokenAutenticazioneModel AUTENTICAZIONE = null;
	 
	public IField POLICY = null;
	 
	public IField TOKEN_OPZIONALE = null;
	 
	public IField VALIDAZIONE = null;
	 
	public IField INTROSPECTION = null;
	 
	public IField USER_INFO = null;
	 
	public IField FORWARD = null;
	 
	public IField OPTIONS = null;
	 

	@Override
	public Class<GestioneToken> getModeledClass(){
		return GestioneToken.class;
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