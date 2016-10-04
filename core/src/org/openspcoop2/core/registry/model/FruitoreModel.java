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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.Fruitore;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Fruitore 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FruitoreModel extends AbstractModel<Fruitore> {

	public FruitoreModel(){
	
		super();
	
		this.SERVIZIO_APPLICATIVO = new Field("servizio-applicativo",java.lang.String.class,"fruitore",Fruitore.class);
		this.CONNETTORE = new org.openspcoop2.core.registry.model.ConnettoreModel(new Field("connettore",org.openspcoop2.core.registry.Connettore.class,"fruitore",Fruitore.class));
		this.STATO_PACKAGE = new Field("stato-package",java.lang.String.class,"fruitore",Fruitore.class);
		this.BYTE_WSDL_IMPLEMENTATIVO_EROGATORE = new Field("byte-wsdl-implementativo-erogatore",byte[].class,"fruitore",Fruitore.class);
		this.BYTE_WSDL_IMPLEMENTATIVO_FRUITORE = new Field("byte-wsdl-implementativo-fruitore",byte[].class,"fruitore",Fruitore.class);
		this.ID_SOGGETTO = new Field("id-soggetto",java.lang.Long.class,"fruitore",Fruitore.class);
		this.ID_SERVIZIO = new Field("id-servizio",java.lang.Long.class,"fruitore",Fruitore.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"fruitore",Fruitore.class);
		this.NOME = new Field("nome",java.lang.String.class,"fruitore",Fruitore.class);
		this.WSDL_IMPLEMENTATIVO_EROGATORE = new Field("wsdl-implementativo-erogatore",java.lang.String.class,"fruitore",Fruitore.class);
		this.WSDL_IMPLEMENTATIVO_FRUITORE = new Field("wsdl-implementativo-fruitore",java.lang.String.class,"fruitore",Fruitore.class);
		this.FILTRO_DUPLICATI = new Field("filtro-duplicati",java.lang.String.class,"fruitore",Fruitore.class);
		this.CONFERMA_RICEZIONE = new Field("conferma-ricezione",java.lang.String.class,"fruitore",Fruitore.class);
		this.ID_COLLABORAZIONE = new Field("id-collaborazione",java.lang.String.class,"fruitore",Fruitore.class);
		this.CONSEGNA_IN_ORDINE = new Field("consegna-in-ordine",java.lang.String.class,"fruitore",Fruitore.class);
		this.SCADENZA = new Field("scadenza",java.lang.String.class,"fruitore",Fruitore.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"fruitore",Fruitore.class);
		this.VERSIONE_PROTOCOLLO = new Field("versione-protocollo",java.lang.String.class,"fruitore",Fruitore.class);
		this.CLIENT_AUTH = new Field("client-auth",java.lang.String.class,"fruitore",Fruitore.class);
	
	}
	
	public FruitoreModel(IField father){
	
		super(father);
	
		this.SERVIZIO_APPLICATIVO = new ComplexField(father,"servizio-applicativo",java.lang.String.class,"fruitore",Fruitore.class);
		this.CONNETTORE = new org.openspcoop2.core.registry.model.ConnettoreModel(new ComplexField(father,"connettore",org.openspcoop2.core.registry.Connettore.class,"fruitore",Fruitore.class));
		this.STATO_PACKAGE = new ComplexField(father,"stato-package",java.lang.String.class,"fruitore",Fruitore.class);
		this.BYTE_WSDL_IMPLEMENTATIVO_EROGATORE = new ComplexField(father,"byte-wsdl-implementativo-erogatore",byte[].class,"fruitore",Fruitore.class);
		this.BYTE_WSDL_IMPLEMENTATIVO_FRUITORE = new ComplexField(father,"byte-wsdl-implementativo-fruitore",byte[].class,"fruitore",Fruitore.class);
		this.ID_SOGGETTO = new ComplexField(father,"id-soggetto",java.lang.Long.class,"fruitore",Fruitore.class);
		this.ID_SERVIZIO = new ComplexField(father,"id-servizio",java.lang.Long.class,"fruitore",Fruitore.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"fruitore",Fruitore.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"fruitore",Fruitore.class);
		this.WSDL_IMPLEMENTATIVO_EROGATORE = new ComplexField(father,"wsdl-implementativo-erogatore",java.lang.String.class,"fruitore",Fruitore.class);
		this.WSDL_IMPLEMENTATIVO_FRUITORE = new ComplexField(father,"wsdl-implementativo-fruitore",java.lang.String.class,"fruitore",Fruitore.class);
		this.FILTRO_DUPLICATI = new ComplexField(father,"filtro-duplicati",java.lang.String.class,"fruitore",Fruitore.class);
		this.CONFERMA_RICEZIONE = new ComplexField(father,"conferma-ricezione",java.lang.String.class,"fruitore",Fruitore.class);
		this.ID_COLLABORAZIONE = new ComplexField(father,"id-collaborazione",java.lang.String.class,"fruitore",Fruitore.class);
		this.CONSEGNA_IN_ORDINE = new ComplexField(father,"consegna-in-ordine",java.lang.String.class,"fruitore",Fruitore.class);
		this.SCADENZA = new ComplexField(father,"scadenza",java.lang.String.class,"fruitore",Fruitore.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"fruitore",Fruitore.class);
		this.VERSIONE_PROTOCOLLO = new ComplexField(father,"versione-protocollo",java.lang.String.class,"fruitore",Fruitore.class);
		this.CLIENT_AUTH = new ComplexField(father,"client-auth",java.lang.String.class,"fruitore",Fruitore.class);
	
	}
	
	

	public IField SERVIZIO_APPLICATIVO = null;
	 
	public org.openspcoop2.core.registry.model.ConnettoreModel CONNETTORE = null;
	 
	public IField STATO_PACKAGE = null;
	 
	public IField BYTE_WSDL_IMPLEMENTATIVO_EROGATORE = null;
	 
	public IField BYTE_WSDL_IMPLEMENTATIVO_FRUITORE = null;
	 
	public IField ID_SOGGETTO = null;
	 
	public IField ID_SERVIZIO = null;
	 
	public IField TIPO = null;
	 
	public IField NOME = null;
	 
	public IField WSDL_IMPLEMENTATIVO_EROGATORE = null;
	 
	public IField WSDL_IMPLEMENTATIVO_FRUITORE = null;
	 
	public IField FILTRO_DUPLICATI = null;
	 
	public IField CONFERMA_RICEZIONE = null;
	 
	public IField ID_COLLABORAZIONE = null;
	 
	public IField CONSEGNA_IN_ORDINE = null;
	 
	public IField SCADENZA = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField VERSIONE_PROTOCOLLO = null;
	 
	public IField CLIENT_AUTH = null;
	 

	@Override
	public Class<Fruitore> getModeledClass(){
		return Fruitore.class;
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