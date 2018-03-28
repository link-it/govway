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

import org.openspcoop2.core.transazioni.DumpMessaggio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DumpMessaggio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpMessaggioModel extends AbstractModel<DumpMessaggio> {

	public DumpMessaggioModel(){
	
		super();
	
		this.ID_TRANSAZIONE = new Field("id-transazione",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.TIPO_MESSAGGIO = new Field("tipo-messaggio",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.CONTENT_TYPE = new Field("content-type",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.MULTIPART_CONTENT_TYPE = new Field("multipart-content-type",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.MULTIPART_CONTENT_ID = new Field("multipart-content-id",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.MULTIPART_CONTENT_LOCATION = new Field("multipart-content-location",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.MULTIPART_HEADER = new org.openspcoop2.core.transazioni.model.DumpMultipartHeaderModel(new Field("multipart-header",org.openspcoop2.core.transazioni.DumpMultipartHeader.class,"dump-messaggio",DumpMessaggio.class));
		this.BODY = new Field("body",byte[].class,"dump-messaggio",DumpMessaggio.class);
		this.HEADER_TRASPORTO = new org.openspcoop2.core.transazioni.model.DumpHeaderTrasportoModel(new Field("header-trasporto",org.openspcoop2.core.transazioni.DumpHeaderTrasporto.class,"dump-messaggio",DumpMessaggio.class));
		this.ALLEGATO = new org.openspcoop2.core.transazioni.model.DumpAllegatoModel(new Field("allegato",org.openspcoop2.core.transazioni.DumpAllegato.class,"dump-messaggio",DumpMessaggio.class));
		this.CONTENUTO = new org.openspcoop2.core.transazioni.model.DumpContenutoModel(new Field("contenuto",org.openspcoop2.core.transazioni.DumpContenuto.class,"dump-messaggio",DumpMessaggio.class));
		this.DUMP_TIMESTAMP = new Field("dump-timestamp",java.util.Date.class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESS_HEADER = new Field("post-process-header",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESS_FILENAME = new Field("post-process-filename",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESS_CONTENT = new Field("post-process-content",byte[].class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESS_CONFIG_ID = new Field("post-process-config-id",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESS_TIMESTAMP = new Field("post-process-timestamp",java.util.Date.class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESSED = new Field("post-processed",int.class,"dump-messaggio",DumpMessaggio.class);
	
	}
	
	public DumpMessaggioModel(IField father){
	
		super(father);
	
		this.ID_TRANSAZIONE = new ComplexField(father,"id-transazione",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.TIPO_MESSAGGIO = new ComplexField(father,"tipo-messaggio",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.CONTENT_TYPE = new ComplexField(father,"content-type",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.MULTIPART_CONTENT_TYPE = new ComplexField(father,"multipart-content-type",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.MULTIPART_CONTENT_ID = new ComplexField(father,"multipart-content-id",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.MULTIPART_CONTENT_LOCATION = new ComplexField(father,"multipart-content-location",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.MULTIPART_HEADER = new org.openspcoop2.core.transazioni.model.DumpMultipartHeaderModel(new ComplexField(father,"multipart-header",org.openspcoop2.core.transazioni.DumpMultipartHeader.class,"dump-messaggio",DumpMessaggio.class));
		this.BODY = new ComplexField(father,"body",byte[].class,"dump-messaggio",DumpMessaggio.class);
		this.HEADER_TRASPORTO = new org.openspcoop2.core.transazioni.model.DumpHeaderTrasportoModel(new ComplexField(father,"header-trasporto",org.openspcoop2.core.transazioni.DumpHeaderTrasporto.class,"dump-messaggio",DumpMessaggio.class));
		this.ALLEGATO = new org.openspcoop2.core.transazioni.model.DumpAllegatoModel(new ComplexField(father,"allegato",org.openspcoop2.core.transazioni.DumpAllegato.class,"dump-messaggio",DumpMessaggio.class));
		this.CONTENUTO = new org.openspcoop2.core.transazioni.model.DumpContenutoModel(new ComplexField(father,"contenuto",org.openspcoop2.core.transazioni.DumpContenuto.class,"dump-messaggio",DumpMessaggio.class));
		this.DUMP_TIMESTAMP = new ComplexField(father,"dump-timestamp",java.util.Date.class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESS_HEADER = new ComplexField(father,"post-process-header",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESS_FILENAME = new ComplexField(father,"post-process-filename",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESS_CONTENT = new ComplexField(father,"post-process-content",byte[].class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESS_CONFIG_ID = new ComplexField(father,"post-process-config-id",java.lang.String.class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESS_TIMESTAMP = new ComplexField(father,"post-process-timestamp",java.util.Date.class,"dump-messaggio",DumpMessaggio.class);
		this.POST_PROCESSED = new ComplexField(father,"post-processed",int.class,"dump-messaggio",DumpMessaggio.class);
	
	}
	
	

	public IField ID_TRANSAZIONE = null;
	 
	public IField TIPO_MESSAGGIO = null;
	 
	public IField CONTENT_TYPE = null;
	 
	public IField MULTIPART_CONTENT_TYPE = null;
	 
	public IField MULTIPART_CONTENT_ID = null;
	 
	public IField MULTIPART_CONTENT_LOCATION = null;
	 
	public org.openspcoop2.core.transazioni.model.DumpMultipartHeaderModel MULTIPART_HEADER = null;
	 
	public IField BODY = null;
	 
	public org.openspcoop2.core.transazioni.model.DumpHeaderTrasportoModel HEADER_TRASPORTO = null;
	 
	public org.openspcoop2.core.transazioni.model.DumpAllegatoModel ALLEGATO = null;
	 
	public org.openspcoop2.core.transazioni.model.DumpContenutoModel CONTENUTO = null;
	 
	public IField DUMP_TIMESTAMP = null;
	 
	public IField POST_PROCESS_HEADER = null;
	 
	public IField POST_PROCESS_FILENAME = null;
	 
	public IField POST_PROCESS_CONTENT = null;
	 
	public IField POST_PROCESS_CONFIG_ID = null;
	 
	public IField POST_PROCESS_TIMESTAMP = null;
	 
	public IField POST_PROCESSED = null;
	 

	@Override
	public Class<DumpMessaggio> getModeledClass(){
		return DumpMessaggio.class;
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