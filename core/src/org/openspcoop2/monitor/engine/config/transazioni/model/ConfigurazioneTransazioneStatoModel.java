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
package org.openspcoop2.monitor.engine.config.transazioni.model;

import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneTransazioneStato 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneTransazioneStatoModel extends AbstractModel<ConfigurazioneTransazioneStato> {

	public ConfigurazioneTransazioneStatoModel(){
	
		super();
	
		this.ENABLED = new Field("enabled",boolean.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
		this.NOME = new Field("nome",java.lang.String.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
		this.TIPO_CONTROLLO = new Field("tipo-controllo",java.lang.String.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
		this.TIPO_MESSAGGIO = new Field("tipo-messaggio",java.lang.String.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
		this.VALORE = new Field("valore",java.lang.String.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
		this.XPATH = new Field("xpath",java.lang.String.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
	
	}
	
	public ConfigurazioneTransazioneStatoModel(IField father){
	
		super(father);
	
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
		this.TIPO_CONTROLLO = new ComplexField(father,"tipo-controllo",java.lang.String.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
		this.TIPO_MESSAGGIO = new ComplexField(father,"tipo-messaggio",java.lang.String.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
		this.VALORE = new ComplexField(father,"valore",java.lang.String.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
		this.XPATH = new ComplexField(father,"xpath",java.lang.String.class,"configurazione-transazione-stato",ConfigurazioneTransazioneStato.class);
	
	}
	
	

	public IField ENABLED = null;
	 
	public IField NOME = null;
	 
	public IField TIPO_CONTROLLO = null;
	 
	public IField TIPO_MESSAGGIO = null;
	 
	public IField VALORE = null;
	 
	public IField XPATH = null;
	 

	@Override
	public Class<ConfigurazioneTransazioneStato> getModeledClass(){
		return ConfigurazioneTransazioneStato.class;
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