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

import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoServizioParteSpecifica 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteSpecificaModel extends AbstractModel<AccordoServizioParteSpecifica> {

	public AccordoServizioParteSpecificaModel(){
	
		super();
	
		this.CONFIGURAZIONE_SERVIZIO = new org.openspcoop2.core.registry.model.ConfigurazioneServizioModel(new Field("configurazione-servizio",org.openspcoop2.core.registry.ConfigurazioneServizio.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.FRUITORE = new org.openspcoop2.core.registry.model.FruitoreModel(new Field("fruitore",org.openspcoop2.core.registry.Fruitore.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.ALLEGATO = new org.openspcoop2.core.registry.model.DocumentoModel(new Field("allegato",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.SPECIFICA_SEMIFORMALE = new org.openspcoop2.core.registry.model.DocumentoModel(new Field("specifica-semiformale",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.SPECIFICA_LIVELLO_SERVIZIO = new org.openspcoop2.core.registry.model.DocumentoModel(new Field("specifica-livello-servizio",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.SPECIFICA_SICUREZZA = new org.openspcoop2.core.registry.model.DocumentoModel(new Field("specifica-sicurezza",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.PROTOCOL_PROPERTY = new org.openspcoop2.core.registry.model.ProtocolPropertyModel(new Field("protocol-property",org.openspcoop2.core.registry.ProtocolProperty.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.SUPER_USER = new Field("super-user",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.STATO_PACKAGE = new Field("stato-package",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.PRIVATO = new Field("privato",Boolean.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ID_ACCORDO = new Field("id-accordo",java.lang.Long.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ID_SOGGETTO = new Field("id-soggetto",java.lang.Long.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.BYTE_WSDL_IMPLEMENTATIVO_EROGATORE = new Field("byte-wsdl-implementativo-erogatore",byte[].class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.BYTE_WSDL_IMPLEMENTATIVO_FRUITORE = new Field("byte-wsdl-implementativo-fruitore",byte[].class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.TIPO_SOGGETTO_EROGATORE = new Field("tipo-soggetto-erogatore",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.NOME_SOGGETTO_EROGATORE = new Field("nome-soggetto-erogatore",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.NOME = new Field("nome",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.VERSIONE = new Field("versione",java.lang.Integer.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ACCORDO_SERVIZIO_PARTE_COMUNE = new Field("accordo-servizio-parte-comune",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.TIPOLOGIA_SERVIZIO = new Field("tipologia-servizio",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.PORT_TYPE = new Field("port-type",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.WSDL_IMPLEMENTATIVO_EROGATORE = new Field("wsdl-implementativo-erogatore",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.WSDL_IMPLEMENTATIVO_FRUITORE = new Field("wsdl-implementativo-fruitore",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.FILTRO_DUPLICATI = new Field("filtro-duplicati",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.CONFERMA_RICEZIONE = new Field("conferma-ricezione",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ID_COLLABORAZIONE = new Field("id-collaborazione",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ID_RIFERIMENTO_RICHIESTA = new Field("id-riferimento-richiesta",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.CONSEGNA_IN_ORDINE = new Field("consegna-in-ordine",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.SCADENZA = new Field("scadenza",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.VERSIONE_PROTOCOLLO = new Field("versione-protocollo",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.MESSAGE_TYPE = new Field("message-type",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
	
	}
	
	public AccordoServizioParteSpecificaModel(IField father){
	
		super(father);
	
		this.CONFIGURAZIONE_SERVIZIO = new org.openspcoop2.core.registry.model.ConfigurazioneServizioModel(new ComplexField(father,"configurazione-servizio",org.openspcoop2.core.registry.ConfigurazioneServizio.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.FRUITORE = new org.openspcoop2.core.registry.model.FruitoreModel(new ComplexField(father,"fruitore",org.openspcoop2.core.registry.Fruitore.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.ALLEGATO = new org.openspcoop2.core.registry.model.DocumentoModel(new ComplexField(father,"allegato",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.SPECIFICA_SEMIFORMALE = new org.openspcoop2.core.registry.model.DocumentoModel(new ComplexField(father,"specifica-semiformale",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.SPECIFICA_LIVELLO_SERVIZIO = new org.openspcoop2.core.registry.model.DocumentoModel(new ComplexField(father,"specifica-livello-servizio",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.SPECIFICA_SICUREZZA = new org.openspcoop2.core.registry.model.DocumentoModel(new ComplexField(father,"specifica-sicurezza",org.openspcoop2.core.registry.Documento.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.PROTOCOL_PROPERTY = new org.openspcoop2.core.registry.model.ProtocolPropertyModel(new ComplexField(father,"protocol-property",org.openspcoop2.core.registry.ProtocolProperty.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class));
		this.SUPER_USER = new ComplexField(father,"super-user",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.STATO_PACKAGE = new ComplexField(father,"stato-package",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.PRIVATO = new ComplexField(father,"privato",Boolean.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ID_ACCORDO = new ComplexField(father,"id-accordo",java.lang.Long.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ID_SOGGETTO = new ComplexField(father,"id-soggetto",java.lang.Long.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.BYTE_WSDL_IMPLEMENTATIVO_EROGATORE = new ComplexField(father,"byte-wsdl-implementativo-erogatore",byte[].class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.BYTE_WSDL_IMPLEMENTATIVO_FRUITORE = new ComplexField(father,"byte-wsdl-implementativo-fruitore",byte[].class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.TIPO_SOGGETTO_EROGATORE = new ComplexField(father,"tipo-soggetto-erogatore",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.NOME_SOGGETTO_EROGATORE = new ComplexField(father,"nome-soggetto-erogatore",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.Integer.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ACCORDO_SERVIZIO_PARTE_COMUNE = new ComplexField(father,"accordo-servizio-parte-comune",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.TIPOLOGIA_SERVIZIO = new ComplexField(father,"tipologia-servizio",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.PORT_TYPE = new ComplexField(father,"port-type",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.WSDL_IMPLEMENTATIVO_EROGATORE = new ComplexField(father,"wsdl-implementativo-erogatore",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.WSDL_IMPLEMENTATIVO_FRUITORE = new ComplexField(father,"wsdl-implementativo-fruitore",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.FILTRO_DUPLICATI = new ComplexField(father,"filtro-duplicati",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.CONFERMA_RICEZIONE = new ComplexField(father,"conferma-ricezione",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ID_COLLABORAZIONE = new ComplexField(father,"id-collaborazione",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ID_RIFERIMENTO_RICHIESTA = new ComplexField(father,"id-riferimento-richiesta",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.CONSEGNA_IN_ORDINE = new ComplexField(father,"consegna-in-ordine",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.SCADENZA = new ComplexField(father,"scadenza",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.VERSIONE_PROTOCOLLO = new ComplexField(father,"versione-protocollo",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
		this.MESSAGE_TYPE = new ComplexField(father,"message-type",java.lang.String.class,"accordo-servizio-parte-specifica",AccordoServizioParteSpecifica.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.ConfigurazioneServizioModel CONFIGURAZIONE_SERVIZIO = null;
	 
	public org.openspcoop2.core.registry.model.FruitoreModel FRUITORE = null;
	 
	public org.openspcoop2.core.registry.model.DocumentoModel ALLEGATO = null;
	 
	public org.openspcoop2.core.registry.model.DocumentoModel SPECIFICA_SEMIFORMALE = null;
	 
	public org.openspcoop2.core.registry.model.DocumentoModel SPECIFICA_LIVELLO_SERVIZIO = null;
	 
	public org.openspcoop2.core.registry.model.DocumentoModel SPECIFICA_SICUREZZA = null;
	 
	public org.openspcoop2.core.registry.model.ProtocolPropertyModel PROTOCOL_PROPERTY = null;
	 
	public IField SUPER_USER = null;
	 
	public IField STATO_PACKAGE = null;
	 
	public IField PRIVATO = null;
	 
	public IField ID_ACCORDO = null;
	 
	public IField ID_SOGGETTO = null;
	 
	public IField BYTE_WSDL_IMPLEMENTATIVO_EROGATORE = null;
	 
	public IField BYTE_WSDL_IMPLEMENTATIVO_FRUITORE = null;
	 
	public IField TIPO_SOGGETTO_EROGATORE = null;
	 
	public IField NOME_SOGGETTO_EROGATORE = null;
	 
	public IField TIPO = null;
	 
	public IField NOME = null;
	 
	public IField VERSIONE = null;
	 
	public IField ACCORDO_SERVIZIO_PARTE_COMUNE = null;
	 
	public IField TIPOLOGIA_SERVIZIO = null;
	 
	public IField PORT_TYPE = null;
	 
	public IField WSDL_IMPLEMENTATIVO_EROGATORE = null;
	 
	public IField WSDL_IMPLEMENTATIVO_FRUITORE = null;
	 
	public IField FILTRO_DUPLICATI = null;
	 
	public IField CONFERMA_RICEZIONE = null;
	 
	public IField ID_COLLABORAZIONE = null;
	 
	public IField ID_RIFERIMENTO_RICHIESTA = null;
	 
	public IField CONSEGNA_IN_ORDINE = null;
	 
	public IField SCADENZA = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField VERSIONE_PROTOCOLLO = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField MESSAGE_TYPE = null;
	 

	@Override
	public Class<AccordoServizioParteSpecifica> getModeledClass(){
		return AccordoServizioParteSpecifica.class;
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