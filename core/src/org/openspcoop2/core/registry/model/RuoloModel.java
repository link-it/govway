/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.Ruolo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Ruolo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RuoloModel extends AbstractModel<Ruolo> {

	public RuoloModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"ruolo",Ruolo.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"ruolo",Ruolo.class);
		this.TIPOLOGIA = new Field("tipologia",java.lang.String.class,"ruolo",Ruolo.class);
		this.NOME_ESTERNO = new Field("nome-esterno",java.lang.String.class,"ruolo",Ruolo.class);
		this.CONTESTO_UTILIZZO = new Field("contesto-utilizzo",java.lang.String.class,"ruolo",Ruolo.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"ruolo",Ruolo.class);
		this.SUPER_USER = new Field("super-user",java.lang.String.class,"ruolo",Ruolo.class);
	
	}
	
	public RuoloModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"ruolo",Ruolo.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"ruolo",Ruolo.class);
		this.TIPOLOGIA = new ComplexField(father,"tipologia",java.lang.String.class,"ruolo",Ruolo.class);
		this.NOME_ESTERNO = new ComplexField(father,"nome-esterno",java.lang.String.class,"ruolo",Ruolo.class);
		this.CONTESTO_UTILIZZO = new ComplexField(father,"contesto-utilizzo",java.lang.String.class,"ruolo",Ruolo.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"ruolo",Ruolo.class);
		this.SUPER_USER = new ComplexField(father,"super-user",java.lang.String.class,"ruolo",Ruolo.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField TIPOLOGIA = null;
	 
	public IField NOME_ESTERNO = null;
	 
	public IField CONTESTO_UTILIZZO = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField SUPER_USER = null;
	 

	@Override
	public Class<Ruolo> getModeledClass(){
		return Ruolo.class;
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