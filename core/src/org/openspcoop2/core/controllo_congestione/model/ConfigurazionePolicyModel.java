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
package org.openspcoop2.core.controllo_congestione.model;

import org.openspcoop2.core.controllo_congestione.ConfigurazionePolicy;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazionePolicy 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePolicyModel extends AbstractModel<ConfigurazionePolicy> {

	public ConfigurazionePolicyModel(){
	
		super();
	
		this.ID_POLICY = new Field("id-policy",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.RISORSA = new Field("risorsa",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.SIMULTANEE = new Field("simultanee",boolean.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.VALORE = new Field("valore",java.lang.Long.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.VALORE_TIPO_BANDA = new Field("valore-tipo-banda",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.VALORE_TIPO_LATENZA = new Field("valore-tipo-latenza",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.MODALITA_CONTROLLO = new Field("modalita-controllo",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.TIPO_INTERVALLO_OSSERVAZIONE_REALTIME = new Field("tipo-intervallo-osservazione-realtime",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO = new Field("tipo-intervallo-osservazione-statistico",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.INTERVALLO_OSSERVAZIONE = new Field("intervallo-osservazione",java.lang.Integer.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.FINESTRA_OSSERVAZIONE = new Field("finestra-osservazione",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.TIPO_APPLICABILITA = new Field("tipo-applicabilita",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.APPLICABILITA_CON_CONGESTIONE = new Field("applicabilita-con-congestione",boolean.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.APPLICABILITA_DEGRADO_PRESTAZIONALE = new Field("applicabilita-degrado-prestazionale",boolean.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_MODALITA_CONTROLLO = new Field("degrado-avg-time-modalita-controllo",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME = new Field("degrado-avg-time-tipo-intervallo-osservazione-realtime",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO = new Field("degrado-avg-time-tipo-intervallo-osservazione-statistico",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE = new Field("degrado-avg-time-intervallo-osservazione",java.lang.Integer.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE = new Field("degrado-avg-time-finestra-osservazione",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_TIPO_LATENZA = new Field("degrado-avg-time-tipo-latenza",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.APPLICABILITA_STATO_ALLARME = new Field("applicabilita-stato-allarme",boolean.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.ALLARME_NOME = new Field("allarme-nome",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.ALLARME_STATO = new Field("allarme-stato",java.lang.Integer.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.ALLARME_NOT_STATO = new Field("allarme-not-stato",boolean.class,"configurazione-policy",ConfigurazionePolicy.class);
	
	}
	
	public ConfigurazionePolicyModel(IField father){
	
		super(father);
	
		this.ID_POLICY = new ComplexField(father,"id-policy",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.RISORSA = new ComplexField(father,"risorsa",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.SIMULTANEE = new ComplexField(father,"simultanee",boolean.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.VALORE = new ComplexField(father,"valore",java.lang.Long.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.VALORE_TIPO_BANDA = new ComplexField(father,"valore-tipo-banda",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.VALORE_TIPO_LATENZA = new ComplexField(father,"valore-tipo-latenza",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.MODALITA_CONTROLLO = new ComplexField(father,"modalita-controllo",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.TIPO_INTERVALLO_OSSERVAZIONE_REALTIME = new ComplexField(father,"tipo-intervallo-osservazione-realtime",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO = new ComplexField(father,"tipo-intervallo-osservazione-statistico",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.INTERVALLO_OSSERVAZIONE = new ComplexField(father,"intervallo-osservazione",java.lang.Integer.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.FINESTRA_OSSERVAZIONE = new ComplexField(father,"finestra-osservazione",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.TIPO_APPLICABILITA = new ComplexField(father,"tipo-applicabilita",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.APPLICABILITA_CON_CONGESTIONE = new ComplexField(father,"applicabilita-con-congestione",boolean.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.APPLICABILITA_DEGRADO_PRESTAZIONALE = new ComplexField(father,"applicabilita-degrado-prestazionale",boolean.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_MODALITA_CONTROLLO = new ComplexField(father,"degrado-avg-time-modalita-controllo",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME = new ComplexField(father,"degrado-avg-time-tipo-intervallo-osservazione-realtime",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO = new ComplexField(father,"degrado-avg-time-tipo-intervallo-osservazione-statistico",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE = new ComplexField(father,"degrado-avg-time-intervallo-osservazione",java.lang.Integer.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE = new ComplexField(father,"degrado-avg-time-finestra-osservazione",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.DEGRADO_AVG_TIME_TIPO_LATENZA = new ComplexField(father,"degrado-avg-time-tipo-latenza",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.APPLICABILITA_STATO_ALLARME = new ComplexField(father,"applicabilita-stato-allarme",boolean.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.ALLARME_NOME = new ComplexField(father,"allarme-nome",java.lang.String.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.ALLARME_STATO = new ComplexField(father,"allarme-stato",java.lang.Integer.class,"configurazione-policy",ConfigurazionePolicy.class);
		this.ALLARME_NOT_STATO = new ComplexField(father,"allarme-not-stato",boolean.class,"configurazione-policy",ConfigurazionePolicy.class);
	
	}
	
	

	public IField ID_POLICY = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField RISORSA = null;
	 
	public IField SIMULTANEE = null;
	 
	public IField VALORE = null;
	 
	public IField VALORE_TIPO_BANDA = null;
	 
	public IField VALORE_TIPO_LATENZA = null;
	 
	public IField MODALITA_CONTROLLO = null;
	 
	public IField TIPO_INTERVALLO_OSSERVAZIONE_REALTIME = null;
	 
	public IField TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO = null;
	 
	public IField INTERVALLO_OSSERVAZIONE = null;
	 
	public IField FINESTRA_OSSERVAZIONE = null;
	 
	public IField TIPO_APPLICABILITA = null;
	 
	public IField APPLICABILITA_CON_CONGESTIONE = null;
	 
	public IField APPLICABILITA_DEGRADO_PRESTAZIONALE = null;
	 
	public IField DEGRADO_AVG_TIME_MODALITA_CONTROLLO = null;
	 
	public IField DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME = null;
	 
	public IField DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO = null;
	 
	public IField DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE = null;
	 
	public IField DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE = null;
	 
	public IField DEGRADO_AVG_TIME_TIPO_LATENZA = null;
	 
	public IField APPLICABILITA_STATO_ALLARME = null;
	 
	public IField ALLARME_NOME = null;
	 
	public IField ALLARME_STATO = null;
	 
	public IField ALLARME_NOT_STATO = null;
	 

	@Override
	public Class<ConfigurazionePolicy> getModeledClass(){
		return ConfigurazionePolicy.class;
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