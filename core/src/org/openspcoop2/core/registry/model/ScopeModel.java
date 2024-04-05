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

import org.openspcoop2.core.registry.Scope;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Scope 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ScopeModel extends AbstractModel<Scope> {

	public ScopeModel(){
	
		super();
	
		this.PROPRIETA_OGGETTO = new org.openspcoop2.core.registry.model.ProprietaOggettoModel(new Field("proprieta-oggetto",org.openspcoop2.core.registry.ProprietaOggetto.class,"scope",Scope.class));
		this.NOME = new Field("nome",java.lang.String.class,"scope",Scope.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"scope",Scope.class);
		this.TIPOLOGIA = new Field("tipologia",java.lang.String.class,"scope",Scope.class);
		this.NOME_ESTERNO = new Field("nome-esterno",java.lang.String.class,"scope",Scope.class);
		this.CONTESTO_UTILIZZO = new Field("contesto-utilizzo",java.lang.String.class,"scope",Scope.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"scope",Scope.class);
		this.SUPER_USER = new Field("super-user",java.lang.String.class,"scope",Scope.class);
	
	}
	
	public ScopeModel(IField father){
	
		super(father);
	
		this.PROPRIETA_OGGETTO = new org.openspcoop2.core.registry.model.ProprietaOggettoModel(new ComplexField(father,"proprieta-oggetto",org.openspcoop2.core.registry.ProprietaOggetto.class,"scope",Scope.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"scope",Scope.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"scope",Scope.class);
		this.TIPOLOGIA = new ComplexField(father,"tipologia",java.lang.String.class,"scope",Scope.class);
		this.NOME_ESTERNO = new ComplexField(father,"nome-esterno",java.lang.String.class,"scope",Scope.class);
		this.CONTESTO_UTILIZZO = new ComplexField(father,"contesto-utilizzo",java.lang.String.class,"scope",Scope.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"scope",Scope.class);
		this.SUPER_USER = new ComplexField(father,"super-user",java.lang.String.class,"scope",Scope.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.ProprietaOggettoModel PROPRIETA_OGGETTO = null;
	 
	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField TIPOLOGIA = null;
	 
	public IField NOME_ESTERNO = null;
	 
	public IField CONTESTO_UTILIZZO = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField SUPER_USER = null;
	 

	@Override
	public Class<Scope> getModeledClass(){
		return Scope.class;
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