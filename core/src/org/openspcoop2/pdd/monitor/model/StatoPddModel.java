/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.pdd.monitor.model;

import org.openspcoop2.pdd.monitor.StatoPdd;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model StatoPdd 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoPddModel extends AbstractModel<StatoPdd> {

	public StatoPddModel(){
	
		super();
	
		this.NUM_MSG_IN_CONSEGNA = new Field("num-msg-in-consegna",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MEDIO_ATTESA_IN_CONSEGNA = new Field("tempo-medio-attesa-in-consegna",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MAX_ATTESA_IN_CONSEGNA = new Field("tempo-max-attesa-in-consegna",long.class,"stato-pdd",StatoPdd.class);
		this.NUM_MSG_IN_SPEDIZIONE = new Field("num-msg-in-spedizione",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MEDIO_ATTESA_IN_SPEDIZIONE = new Field("tempo-medio-attesa-in-spedizione",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MAX_ATTESA_IN_SPEDIZIONE = new Field("tempo-max-attesa-in-spedizione",long.class,"stato-pdd",StatoPdd.class);
		this.NUM_MSG_IN_PROCESSAMENTO = new Field("num-msg-in-processamento",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MEDIO_ATTESA_IN_PROCESSAMENTO = new Field("tempo-medio-attesa-in-processamento",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MAX_ATTESA_IN_PROCESSAMENTO = new Field("tempo-max-attesa-in-processamento",long.class,"stato-pdd",StatoPdd.class);
		this.TOT_MESSAGGI = new Field("tot-messaggi",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MEDIO_ATTESA = new Field("tempo-medio-attesa",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MAX_ATTESA = new Field("tempo-max-attesa",long.class,"stato-pdd",StatoPdd.class);
		this.TOT_MESSAGGI_DUPLICATI = new Field("tot-messaggi-duplicati",long.class,"stato-pdd",StatoPdd.class);
		this.FILTRO = new org.openspcoop2.pdd.monitor.model.FiltroModel(new Field("filtro",org.openspcoop2.pdd.monitor.Filtro.class,"stato-pdd",StatoPdd.class));
	
	}
	
	public StatoPddModel(IField father){
	
		super(father);
	
		this.NUM_MSG_IN_CONSEGNA = new ComplexField(father,"num-msg-in-consegna",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MEDIO_ATTESA_IN_CONSEGNA = new ComplexField(father,"tempo-medio-attesa-in-consegna",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MAX_ATTESA_IN_CONSEGNA = new ComplexField(father,"tempo-max-attesa-in-consegna",long.class,"stato-pdd",StatoPdd.class);
		this.NUM_MSG_IN_SPEDIZIONE = new ComplexField(father,"num-msg-in-spedizione",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MEDIO_ATTESA_IN_SPEDIZIONE = new ComplexField(father,"tempo-medio-attesa-in-spedizione",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MAX_ATTESA_IN_SPEDIZIONE = new ComplexField(father,"tempo-max-attesa-in-spedizione",long.class,"stato-pdd",StatoPdd.class);
		this.NUM_MSG_IN_PROCESSAMENTO = new ComplexField(father,"num-msg-in-processamento",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MEDIO_ATTESA_IN_PROCESSAMENTO = new ComplexField(father,"tempo-medio-attesa-in-processamento",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MAX_ATTESA_IN_PROCESSAMENTO = new ComplexField(father,"tempo-max-attesa-in-processamento",long.class,"stato-pdd",StatoPdd.class);
		this.TOT_MESSAGGI = new ComplexField(father,"tot-messaggi",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MEDIO_ATTESA = new ComplexField(father,"tempo-medio-attesa",long.class,"stato-pdd",StatoPdd.class);
		this.TEMPO_MAX_ATTESA = new ComplexField(father,"tempo-max-attesa",long.class,"stato-pdd",StatoPdd.class);
		this.TOT_MESSAGGI_DUPLICATI = new ComplexField(father,"tot-messaggi-duplicati",long.class,"stato-pdd",StatoPdd.class);
		this.FILTRO = new org.openspcoop2.pdd.monitor.model.FiltroModel(new ComplexField(father,"filtro",org.openspcoop2.pdd.monitor.Filtro.class,"stato-pdd",StatoPdd.class));
	
	}
	
	

	public IField NUM_MSG_IN_CONSEGNA = null;
	 
	public IField TEMPO_MEDIO_ATTESA_IN_CONSEGNA = null;
	 
	public IField TEMPO_MAX_ATTESA_IN_CONSEGNA = null;
	 
	public IField NUM_MSG_IN_SPEDIZIONE = null;
	 
	public IField TEMPO_MEDIO_ATTESA_IN_SPEDIZIONE = null;
	 
	public IField TEMPO_MAX_ATTESA_IN_SPEDIZIONE = null;
	 
	public IField NUM_MSG_IN_PROCESSAMENTO = null;
	 
	public IField TEMPO_MEDIO_ATTESA_IN_PROCESSAMENTO = null;
	 
	public IField TEMPO_MAX_ATTESA_IN_PROCESSAMENTO = null;
	 
	public IField TOT_MESSAGGI = null;
	 
	public IField TEMPO_MEDIO_ATTESA = null;
	 
	public IField TEMPO_MAX_ATTESA = null;
	 
	public IField TOT_MESSAGGI_DUPLICATI = null;
	 
	public org.openspcoop2.pdd.monitor.model.FiltroModel FILTRO = null;
	 

	@Override
	public Class<StatoPdd> getModeledClass(){
		return StatoPdd.class;
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