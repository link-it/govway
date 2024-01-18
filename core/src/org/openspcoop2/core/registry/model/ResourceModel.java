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

import org.openspcoop2.core.registry.Resource;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Resource 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResourceModel extends AbstractModel<Resource> {

	public ResourceModel(){
	
		super();
	
		this.REQUEST = new org.openspcoop2.core.registry.model.ResourceRequestModel(new Field("request",org.openspcoop2.core.registry.ResourceRequest.class,"resource",Resource.class));
		this.RESPONSE = new org.openspcoop2.core.registry.model.ResourceResponseModel(new Field("response",org.openspcoop2.core.registry.ResourceResponse.class,"resource",Resource.class));
		this.PROTOCOL_PROPERTY = new org.openspcoop2.core.registry.model.ProtocolPropertyModel(new Field("protocol-property",org.openspcoop2.core.registry.ProtocolProperty.class,"resource",Resource.class));
		this.PROF_AZIONE = new Field("prof-azione",java.lang.String.class,"resource",Resource.class);
		this.ID_ACCORDO = new Field("id-accordo",java.lang.Long.class,"resource",Resource.class);
		this.NOME = new Field("nome",java.lang.String.class,"resource",Resource.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"resource",Resource.class);
		this.PATH = new Field("path",java.lang.String.class,"resource",Resource.class);
		this.METHOD = new Field("method",java.lang.String.class,"resource",Resource.class);
		this.MESSAGE_TYPE = new Field("message-type",java.lang.String.class,"resource",Resource.class);
		this.REQUEST_MESSAGE_TYPE = new Field("request-message-type",java.lang.String.class,"resource",Resource.class);
		this.RESPONSE_MESSAGE_TYPE = new Field("response-message-type",java.lang.String.class,"resource",Resource.class);
		this.FILTRO_DUPLICATI = new Field("filtro-duplicati",java.lang.String.class,"resource",Resource.class);
		this.CONFERMA_RICEZIONE = new Field("conferma-ricezione",java.lang.String.class,"resource",Resource.class);
		this.ID_COLLABORAZIONE = new Field("id-collaborazione",java.lang.String.class,"resource",Resource.class);
		this.ID_RIFERIMENTO_RICHIESTA = new Field("id-riferimento-richiesta",java.lang.String.class,"resource",Resource.class);
		this.CONSEGNA_IN_ORDINE = new Field("consegna-in-ordine",java.lang.String.class,"resource",Resource.class);
		this.SCADENZA = new Field("scadenza",java.lang.String.class,"resource",Resource.class);
	
	}
	
	public ResourceModel(IField father){
	
		super(father);
	
		this.REQUEST = new org.openspcoop2.core.registry.model.ResourceRequestModel(new ComplexField(father,"request",org.openspcoop2.core.registry.ResourceRequest.class,"resource",Resource.class));
		this.RESPONSE = new org.openspcoop2.core.registry.model.ResourceResponseModel(new ComplexField(father,"response",org.openspcoop2.core.registry.ResourceResponse.class,"resource",Resource.class));
		this.PROTOCOL_PROPERTY = new org.openspcoop2.core.registry.model.ProtocolPropertyModel(new ComplexField(father,"protocol-property",org.openspcoop2.core.registry.ProtocolProperty.class,"resource",Resource.class));
		this.PROF_AZIONE = new ComplexField(father,"prof-azione",java.lang.String.class,"resource",Resource.class);
		this.ID_ACCORDO = new ComplexField(father,"id-accordo",java.lang.Long.class,"resource",Resource.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"resource",Resource.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"resource",Resource.class);
		this.PATH = new ComplexField(father,"path",java.lang.String.class,"resource",Resource.class);
		this.METHOD = new ComplexField(father,"method",java.lang.String.class,"resource",Resource.class);
		this.MESSAGE_TYPE = new ComplexField(father,"message-type",java.lang.String.class,"resource",Resource.class);
		this.REQUEST_MESSAGE_TYPE = new ComplexField(father,"request-message-type",java.lang.String.class,"resource",Resource.class);
		this.RESPONSE_MESSAGE_TYPE = new ComplexField(father,"response-message-type",java.lang.String.class,"resource",Resource.class);
		this.FILTRO_DUPLICATI = new ComplexField(father,"filtro-duplicati",java.lang.String.class,"resource",Resource.class);
		this.CONFERMA_RICEZIONE = new ComplexField(father,"conferma-ricezione",java.lang.String.class,"resource",Resource.class);
		this.ID_COLLABORAZIONE = new ComplexField(father,"id-collaborazione",java.lang.String.class,"resource",Resource.class);
		this.ID_RIFERIMENTO_RICHIESTA = new ComplexField(father,"id-riferimento-richiesta",java.lang.String.class,"resource",Resource.class);
		this.CONSEGNA_IN_ORDINE = new ComplexField(father,"consegna-in-ordine",java.lang.String.class,"resource",Resource.class);
		this.SCADENZA = new ComplexField(father,"scadenza",java.lang.String.class,"resource",Resource.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.ResourceRequestModel REQUEST = null;
	 
	public org.openspcoop2.core.registry.model.ResourceResponseModel RESPONSE = null;
	 
	public org.openspcoop2.core.registry.model.ProtocolPropertyModel PROTOCOL_PROPERTY = null;
	 
	public IField PROF_AZIONE = null;
	 
	public IField ID_ACCORDO = null;
	 
	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField PATH = null;
	 
	public IField METHOD = null;
	 
	public IField MESSAGE_TYPE = null;
	 
	public IField REQUEST_MESSAGE_TYPE = null;
	 
	public IField RESPONSE_MESSAGE_TYPE = null;
	 
	public IField FILTRO_DUPLICATI = null;
	 
	public IField CONFERMA_RICEZIONE = null;
	 
	public IField ID_COLLABORAZIONE = null;
	 
	public IField ID_RIFERIMENTO_RICHIESTA = null;
	 
	public IField CONSEGNA_IN_ORDINE = null;
	 
	public IField SCADENZA = null;
	 

	@Override
	public Class<Resource> getModeledClass(){
		return Resource.class;
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