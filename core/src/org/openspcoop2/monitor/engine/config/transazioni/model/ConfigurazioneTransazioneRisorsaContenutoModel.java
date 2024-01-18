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
package org.openspcoop2.monitor.engine.config.transazioni.model;

import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneTransazioneRisorsaContenuto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneTransazioneRisorsaContenutoModel extends AbstractModel<ConfigurazioneTransazioneRisorsaContenuto> {

	public ConfigurazioneTransazioneRisorsaContenutoModel(){
	
		super();
	
		this.ABILITA_ANONIMIZZAZIONE = new Field("abilita-anonimizzazione",java.lang.Integer.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.ABILITA_COMPRESSIONE = new Field("abilita-compressione",java.lang.Integer.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.TIPO_COMPRESSIONE = new Field("tipo-compressione",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.CARATTERE_MASCHERA = new Field("carattere-maschera",char.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.NUMERO_CARATTERI_MASCHERA = new Field("numero-caratteri-maschera",java.lang.Integer.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.POSIZIONAMENTO_MASCHERA = new Field("posizionamento-maschera",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.TIPO_MASCHERAMENTO = new Field("tipo-mascheramento",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.ENABLED = new Field("enabled",boolean.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.NOME = new Field("nome",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.TIPO_MESSAGGIO = new Field("tipo-messaggio",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.XPATH = new Field("xpath",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.STAT_ENABLED = new Field("stat-enabled",boolean.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.ID_CONFIGURAZIONE_TRANSAZIONE_STATO = new org.openspcoop2.monitor.engine.config.transazioni.model.IdConfigurazioneTransazioneStatoModel(new Field("id-configurazione-transazione-stato",org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class));
	
	}
	
	public ConfigurazioneTransazioneRisorsaContenutoModel(IField father){
	
		super(father);
	
		this.ABILITA_ANONIMIZZAZIONE = new ComplexField(father,"abilita-anonimizzazione",java.lang.Integer.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.ABILITA_COMPRESSIONE = new ComplexField(father,"abilita-compressione",java.lang.Integer.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.TIPO_COMPRESSIONE = new ComplexField(father,"tipo-compressione",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.CARATTERE_MASCHERA = new ComplexField(father,"carattere-maschera",char.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.NUMERO_CARATTERI_MASCHERA = new ComplexField(father,"numero-caratteri-maschera",java.lang.Integer.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.POSIZIONAMENTO_MASCHERA = new ComplexField(father,"posizionamento-maschera",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.TIPO_MASCHERAMENTO = new ComplexField(father,"tipo-mascheramento",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.TIPO_MESSAGGIO = new ComplexField(father,"tipo-messaggio",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.XPATH = new ComplexField(father,"xpath",java.lang.String.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.STAT_ENABLED = new ComplexField(father,"stat-enabled",boolean.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class);
		this.ID_CONFIGURAZIONE_TRANSAZIONE_STATO = new org.openspcoop2.monitor.engine.config.transazioni.model.IdConfigurazioneTransazioneStatoModel(new ComplexField(father,"id-configurazione-transazione-stato",org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazioneStato.class,"configurazione-transazione-risorsa-contenuto",ConfigurazioneTransazioneRisorsaContenuto.class));
	
	}
	
	

	public IField ABILITA_ANONIMIZZAZIONE = null;
	 
	public IField ABILITA_COMPRESSIONE = null;
	 
	public IField TIPO_COMPRESSIONE = null;
	 
	public IField CARATTERE_MASCHERA = null;
	 
	public IField NUMERO_CARATTERI_MASCHERA = null;
	 
	public IField POSIZIONAMENTO_MASCHERA = null;
	 
	public IField TIPO_MASCHERAMENTO = null;
	 
	public IField ENABLED = null;
	 
	public IField NOME = null;
	 
	public IField TIPO_MESSAGGIO = null;
	 
	public IField XPATH = null;
	 
	public IField STAT_ENABLED = null;
	 
	public org.openspcoop2.monitor.engine.config.transazioni.model.IdConfigurazioneTransazioneStatoModel ID_CONFIGURAZIONE_TRANSAZIONE_STATO = null;
	 

	@Override
	public Class<ConfigurazioneTransazioneRisorsaContenuto> getModeledClass(){
		return ConfigurazioneTransazioneRisorsaContenuto.class;
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