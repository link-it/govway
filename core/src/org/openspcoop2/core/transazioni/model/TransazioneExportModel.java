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
package org.openspcoop2.core.transazioni.model;

import org.openspcoop2.core.transazioni.TransazioneExport;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TransazioneExport 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneExportModel extends AbstractModel<TransazioneExport> {

	public TransazioneExportModel(){
	
		super();
	
		this.INTERVALLO_INIZIO = new Field("intervallo-inizio",java.util.Date.class,"transazione-export",TransazioneExport.class);
		this.INTERVALLO_FINE = new Field("intervallo-fine",java.util.Date.class,"transazione-export",TransazioneExport.class);
		this.NOME = new Field("nome",java.lang.String.class,"transazione-export",TransazioneExport.class);
		this.EXPORT_STATE = new Field("export-state",java.lang.String.class,"transazione-export",TransazioneExport.class);
		this.EXPORT_ERROR = new Field("export-error",java.lang.String.class,"transazione-export",TransazioneExport.class);
		this.EXPORT_TIME_START = new Field("export-time-start",java.util.Date.class,"transazione-export",TransazioneExport.class);
		this.EXPORT_TIME_END = new Field("export-time-end",java.util.Date.class,"transazione-export",TransazioneExport.class);
		this.DELETE_STATE = new Field("delete-state",java.lang.String.class,"transazione-export",TransazioneExport.class);
		this.DELETE_ERROR = new Field("delete-error",java.lang.String.class,"transazione-export",TransazioneExport.class);
		this.DELETE_TIME_START = new Field("delete-time-start",java.util.Date.class,"transazione-export",TransazioneExport.class);
		this.DELETE_TIME_END = new Field("delete-time-end",java.util.Date.class,"transazione-export",TransazioneExport.class);
	
	}
	
	public TransazioneExportModel(IField father){
	
		super(father);
	
		this.INTERVALLO_INIZIO = new ComplexField(father,"intervallo-inizio",java.util.Date.class,"transazione-export",TransazioneExport.class);
		this.INTERVALLO_FINE = new ComplexField(father,"intervallo-fine",java.util.Date.class,"transazione-export",TransazioneExport.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"transazione-export",TransazioneExport.class);
		this.EXPORT_STATE = new ComplexField(father,"export-state",java.lang.String.class,"transazione-export",TransazioneExport.class);
		this.EXPORT_ERROR = new ComplexField(father,"export-error",java.lang.String.class,"transazione-export",TransazioneExport.class);
		this.EXPORT_TIME_START = new ComplexField(father,"export-time-start",java.util.Date.class,"transazione-export",TransazioneExport.class);
		this.EXPORT_TIME_END = new ComplexField(father,"export-time-end",java.util.Date.class,"transazione-export",TransazioneExport.class);
		this.DELETE_STATE = new ComplexField(father,"delete-state",java.lang.String.class,"transazione-export",TransazioneExport.class);
		this.DELETE_ERROR = new ComplexField(father,"delete-error",java.lang.String.class,"transazione-export",TransazioneExport.class);
		this.DELETE_TIME_START = new ComplexField(father,"delete-time-start",java.util.Date.class,"transazione-export",TransazioneExport.class);
		this.DELETE_TIME_END = new ComplexField(father,"delete-time-end",java.util.Date.class,"transazione-export",TransazioneExport.class);
	
	}
	
	

	public IField INTERVALLO_INIZIO = null;
	 
	public IField INTERVALLO_FINE = null;
	 
	public IField NOME = null;
	 
	public IField EXPORT_STATE = null;
	 
	public IField EXPORT_ERROR = null;
	 
	public IField EXPORT_TIME_START = null;
	 
	public IField EXPORT_TIME_END = null;
	 
	public IField DELETE_STATE = null;
	 
	public IField DELETE_ERROR = null;
	 
	public IField DELETE_TIME_START = null;
	 
	public IField DELETE_TIME_END = null;
	 

	@Override
	public Class<TransazioneExport> getModeledClass(){
		return TransazioneExport.class;
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