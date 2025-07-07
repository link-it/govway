/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.statistiche.model;

import org.openspcoop2.core.statistiche.StatistichePdndTracing;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model StatistichePdndTracing 
 *
 * @author Poli Andrea (poli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatistichePdndTracingModel extends AbstractModel<StatistichePdndTracing> {

	public StatistichePdndTracingModel(){
	
		super();
	
		this.DATA_TRACCIAMENTO = new Field("data-tracciamento",java.util.Date.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.DATA_REGISTRAZIONE = new Field("data-registrazione",java.util.Date.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.DATA_PUBBLICAZIONE = new Field("data-pubblicazione",java.util.Date.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.PDD_CODICE = new Field("pdd-codice",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.CSV = new Field("csv",byte[].class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.METHOD = new Field("method",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.STATO_PDND = new Field("stato-pdnd",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.TENTATIVI_PUBBLICAZIONE = new Field("tentativi-pubblicazione",java.lang.Integer.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.FORCE_PUBLISH = new Field("force-publish",boolean.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.STATO = new Field("stato",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.TRACING_ID = new Field("tracing-id",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.ERROR_DETAILS = new Field("error-details",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.HISTORY = new Field("history",int.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
	
	}
	
	public StatistichePdndTracingModel(IField father){
	
		super(father);
	
		this.DATA_TRACCIAMENTO = new ComplexField(father,"data-tracciamento",java.util.Date.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.DATA_REGISTRAZIONE = new ComplexField(father,"data-registrazione",java.util.Date.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.DATA_PUBBLICAZIONE = new ComplexField(father,"data-pubblicazione",java.util.Date.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.PDD_CODICE = new ComplexField(father,"pdd-codice",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.CSV = new ComplexField(father,"csv",byte[].class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.METHOD = new ComplexField(father,"method",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.STATO_PDND = new ComplexField(father,"stato-pdnd",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.TENTATIVI_PUBBLICAZIONE = new ComplexField(father,"tentativi-pubblicazione",java.lang.Integer.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.FORCE_PUBLISH = new ComplexField(father,"force-publish",boolean.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.TRACING_ID = new ComplexField(father,"tracing-id",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.ERROR_DETAILS = new ComplexField(father,"error-details",java.lang.String.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
		this.HISTORY = new ComplexField(father,"history",int.class,"statistiche-pdnd-tracing",StatistichePdndTracing.class);
	
	}
	
	

	public IField DATA_TRACCIAMENTO = null;
	 
	public IField DATA_REGISTRAZIONE = null;
	 
	public IField DATA_PUBBLICAZIONE = null;
	 
	public IField PDD_CODICE = null;
	 
	public IField CSV = null;
	 
	public IField METHOD = null;
	 
	public IField STATO_PDND = null;
	 
	public IField TENTATIVI_PUBBLICAZIONE = null;
	 
	public IField FORCE_PUBLISH = null;
	 
	public IField STATO = null;
	 
	public IField TRACING_ID = null;
	 
	public IField ERROR_DETAILS = null;
	 
	public IField HISTORY = null;
	 

	@Override
	public Class<StatistichePdndTracing> getModeledClass(){
		return StatistichePdndTracing.class;
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
