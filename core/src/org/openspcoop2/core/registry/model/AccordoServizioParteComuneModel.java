/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.registry.AccordoServizioParteComune;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoServizioParteComune 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneModel extends AbstractModel<AccordoServizioParteComune> {

	public AccordoServizioParteComuneModel(){
	
		super();
	
		this.SOGGETTO_REFERENTE = new org.openspcoop2.core.registry.model.IdSoggettoModel(new Field("soggetto-referente",org.openspcoop2.core.registry.IdSoggetto.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.SERVIZIO_COMPOSTO = new org.openspcoop2.core.registry.model.AccordoServizioParteComuneServizioCompostoModel(new Field("servizio-composto",org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.AZIONE = new org.openspcoop2.core.registry.model.AzioneModel(new Field("azione",org.openspcoop2.core.registry.Azione.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.PORT_TYPE = new org.openspcoop2.core.registry.model.PortTypeModel(new Field("port-type",org.openspcoop2.core.registry.PortType.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.RESOURCE = new org.openspcoop2.core.registry.model.ResourceModel(new Field("resource",org.openspcoop2.core.registry.Resource.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.ALLEGATO = new org.openspcoop2.core.registry.model.DocumentoModel(new Field("allegato",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.SPECIFICA_SEMIFORMALE = new org.openspcoop2.core.registry.model.DocumentoModel(new Field("specifica-semiformale",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.PROTOCOL_PROPERTY = new org.openspcoop2.core.registry.model.ProtocolPropertyModel(new Field("protocol-property",org.openspcoop2.core.registry.ProtocolProperty.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.GRUPPI = new org.openspcoop2.core.registry.model.GruppiAccordoModel(new Field("gruppi",org.openspcoop2.core.registry.GruppiAccordo.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.SUPER_USER = new Field("super-user",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.STATO_PACKAGE = new Field("stato-package",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.PRIVATO = new Field("privato",Boolean.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_WSDL_DEFINITORIO = new Field("byte-wsdl-definitorio",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_WSDL_CONCETTUALE = new Field("byte-wsdl-concettuale",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_WSDL_LOGICO_EROGATORE = new Field("byte-wsdl-logico-erogatore",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_WSDL_LOGICO_FRUITORE = new Field("byte-wsdl-logico-fruitore",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_SPECIFICA_CONVERSAZIONE_CONCETTUALE = new Field("byte-specifica-conversazione-concettuale",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_SPECIFICA_CONVERSAZIONE_EROGATORE = new Field("byte-specifica-conversazione-erogatore",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_SPECIFICA_CONVERSAZIONE_FRUITORE = new Field("byte-specifica-conversazione-fruitore",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.SERVICE_BINDING = new Field("service-binding",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.MESSAGE_TYPE = new Field("message-type",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.NOME = new Field("nome",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.PROFILO_COLLABORAZIONE = new Field("profilo-collaborazione",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.FORMATO_SPECIFICA = new Field("formato-specifica",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.WSDL_DEFINITORIO = new Field("wsdl-definitorio",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.WSDL_CONCETTUALE = new Field("wsdl-concettuale",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.WSDL_LOGICO_EROGATORE = new Field("wsdl-logico-erogatore",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.WSDL_LOGICO_FRUITORE = new Field("wsdl-logico-fruitore",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.SPECIFICA_CONVERSAZIONE_CONCETTUALE = new Field("specifica-conversazione-concettuale",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.SPECIFICA_CONVERSAZIONE_EROGATORE = new Field("specifica-conversazione-erogatore",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.SPECIFICA_CONVERSAZIONE_FRUITORE = new Field("specifica-conversazione-fruitore",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.UTILIZZO_SENZA_AZIONE = new Field("utilizzo-senza-azione",boolean.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.FILTRO_DUPLICATI = new Field("filtro-duplicati",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.CONFERMA_RICEZIONE = new Field("conferma-ricezione",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.ID_COLLABORAZIONE = new Field("id-collaborazione",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.ID_RIFERIMENTO_RICHIESTA = new Field("id-riferimento-richiesta",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.CONSEGNA_IN_ORDINE = new Field("consegna-in-ordine",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.SCADENZA = new Field("scadenza",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.VERSIONE = new Field("versione",java.lang.Integer.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.CANALE = new Field("canale",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
	
	}
	
	public AccordoServizioParteComuneModel(IField father){
	
		super(father);
	
		this.SOGGETTO_REFERENTE = new org.openspcoop2.core.registry.model.IdSoggettoModel(new ComplexField(father,"soggetto-referente",org.openspcoop2.core.registry.IdSoggetto.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.SERVIZIO_COMPOSTO = new org.openspcoop2.core.registry.model.AccordoServizioParteComuneServizioCompostoModel(new ComplexField(father,"servizio-composto",org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.AZIONE = new org.openspcoop2.core.registry.model.AzioneModel(new ComplexField(father,"azione",org.openspcoop2.core.registry.Azione.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.PORT_TYPE = new org.openspcoop2.core.registry.model.PortTypeModel(new ComplexField(father,"port-type",org.openspcoop2.core.registry.PortType.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.RESOURCE = new org.openspcoop2.core.registry.model.ResourceModel(new ComplexField(father,"resource",org.openspcoop2.core.registry.Resource.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.ALLEGATO = new org.openspcoop2.core.registry.model.DocumentoModel(new ComplexField(father,"allegato",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.SPECIFICA_SEMIFORMALE = new org.openspcoop2.core.registry.model.DocumentoModel(new ComplexField(father,"specifica-semiformale",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.PROTOCOL_PROPERTY = new org.openspcoop2.core.registry.model.ProtocolPropertyModel(new ComplexField(father,"protocol-property",org.openspcoop2.core.registry.ProtocolProperty.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.GRUPPI = new org.openspcoop2.core.registry.model.GruppiAccordoModel(new ComplexField(father,"gruppi",org.openspcoop2.core.registry.GruppiAccordo.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.SUPER_USER = new ComplexField(father,"super-user",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.STATO_PACKAGE = new ComplexField(father,"stato-package",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.PRIVATO = new ComplexField(father,"privato",Boolean.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_WSDL_DEFINITORIO = new ComplexField(father,"byte-wsdl-definitorio",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_WSDL_CONCETTUALE = new ComplexField(father,"byte-wsdl-concettuale",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_WSDL_LOGICO_EROGATORE = new ComplexField(father,"byte-wsdl-logico-erogatore",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_WSDL_LOGICO_FRUITORE = new ComplexField(father,"byte-wsdl-logico-fruitore",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_SPECIFICA_CONVERSAZIONE_CONCETTUALE = new ComplexField(father,"byte-specifica-conversazione-concettuale",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_SPECIFICA_CONVERSAZIONE_EROGATORE = new ComplexField(father,"byte-specifica-conversazione-erogatore",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.BYTE_SPECIFICA_CONVERSAZIONE_FRUITORE = new ComplexField(father,"byte-specifica-conversazione-fruitore",byte[].class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.SERVICE_BINDING = new ComplexField(father,"service-binding",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.MESSAGE_TYPE = new ComplexField(father,"message-type",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.PROFILO_COLLABORAZIONE = new ComplexField(father,"profilo-collaborazione",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.FORMATO_SPECIFICA = new ComplexField(father,"formato-specifica",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.WSDL_DEFINITORIO = new ComplexField(father,"wsdl-definitorio",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.WSDL_CONCETTUALE = new ComplexField(father,"wsdl-concettuale",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.WSDL_LOGICO_EROGATORE = new ComplexField(father,"wsdl-logico-erogatore",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.WSDL_LOGICO_FRUITORE = new ComplexField(father,"wsdl-logico-fruitore",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.SPECIFICA_CONVERSAZIONE_CONCETTUALE = new ComplexField(father,"specifica-conversazione-concettuale",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.SPECIFICA_CONVERSAZIONE_EROGATORE = new ComplexField(father,"specifica-conversazione-erogatore",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.SPECIFICA_CONVERSAZIONE_FRUITORE = new ComplexField(father,"specifica-conversazione-fruitore",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.UTILIZZO_SENZA_AZIONE = new ComplexField(father,"utilizzo-senza-azione",boolean.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.FILTRO_DUPLICATI = new ComplexField(father,"filtro-duplicati",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.CONFERMA_RICEZIONE = new ComplexField(father,"conferma-ricezione",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.ID_COLLABORAZIONE = new ComplexField(father,"id-collaborazione",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.ID_RIFERIMENTO_RICHIESTA = new ComplexField(father,"id-riferimento-richiesta",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.CONSEGNA_IN_ORDINE = new ComplexField(father,"consegna-in-ordine",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.SCADENZA = new ComplexField(father,"scadenza",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.Integer.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.CANALE = new ComplexField(father,"canale",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.IdSoggettoModel SOGGETTO_REFERENTE = null;
	 
	public org.openspcoop2.core.registry.model.AccordoServizioParteComuneServizioCompostoModel SERVIZIO_COMPOSTO = null;
	 
	public org.openspcoop2.core.registry.model.AzioneModel AZIONE = null;
	 
	public org.openspcoop2.core.registry.model.PortTypeModel PORT_TYPE = null;
	 
	public org.openspcoop2.core.registry.model.ResourceModel RESOURCE = null;
	 
	public org.openspcoop2.core.registry.model.DocumentoModel ALLEGATO = null;
	 
	public org.openspcoop2.core.registry.model.DocumentoModel SPECIFICA_SEMIFORMALE = null;
	 
	public org.openspcoop2.core.registry.model.ProtocolPropertyModel PROTOCOL_PROPERTY = null;
	 
	public org.openspcoop2.core.registry.model.GruppiAccordoModel GRUPPI = null;
	 
	public IField SUPER_USER = null;
	 
	public IField STATO_PACKAGE = null;
	 
	public IField PRIVATO = null;
	 
	public IField BYTE_WSDL_DEFINITORIO = null;
	 
	public IField BYTE_WSDL_CONCETTUALE = null;
	 
	public IField BYTE_WSDL_LOGICO_EROGATORE = null;
	 
	public IField BYTE_WSDL_LOGICO_FRUITORE = null;
	 
	public IField BYTE_SPECIFICA_CONVERSAZIONE_CONCETTUALE = null;
	 
	public IField BYTE_SPECIFICA_CONVERSAZIONE_EROGATORE = null;
	 
	public IField BYTE_SPECIFICA_CONVERSAZIONE_FRUITORE = null;
	 
	public IField SERVICE_BINDING = null;
	 
	public IField MESSAGE_TYPE = null;
	 
	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField PROFILO_COLLABORAZIONE = null;
	 
	public IField FORMATO_SPECIFICA = null;
	 
	public IField WSDL_DEFINITORIO = null;
	 
	public IField WSDL_CONCETTUALE = null;
	 
	public IField WSDL_LOGICO_EROGATORE = null;
	 
	public IField WSDL_LOGICO_FRUITORE = null;
	 
	public IField SPECIFICA_CONVERSAZIONE_CONCETTUALE = null;
	 
	public IField SPECIFICA_CONVERSAZIONE_EROGATORE = null;
	 
	public IField SPECIFICA_CONVERSAZIONE_FRUITORE = null;
	 
	public IField UTILIZZO_SENZA_AZIONE = null;
	 
	public IField FILTRO_DUPLICATI = null;
	 
	public IField CONFERMA_RICEZIONE = null;
	 
	public IField ID_COLLABORAZIONE = null;
	 
	public IField ID_RIFERIMENTO_RICHIESTA = null;
	 
	public IField CONSEGNA_IN_ORDINE = null;
	 
	public IField SCADENZA = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField VERSIONE = null;
	 
	public IField CANALE = null;
	 

	@Override
	public Class<AccordoServizioParteComune> getModeledClass(){
		return AccordoServizioParteComune.class;
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