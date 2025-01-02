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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.Soggetto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Soggetto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettoModel extends AbstractModel<Soggetto> {

	public SoggettoModel(){
	
		super();
	
		this.CONNETTORE = new org.openspcoop2.core.registry.model.ConnettoreModel(new Field("connettore",org.openspcoop2.core.registry.Connettore.class,"soggetto",Soggetto.class));
		this.PROPRIETA = new org.openspcoop2.core.registry.model.ProprietaModel(new Field("proprieta",org.openspcoop2.core.registry.Proprieta.class,"soggetto",Soggetto.class));
		this.PROTOCOL_PROPERTY = new org.openspcoop2.core.registry.model.ProtocolPropertyModel(new Field("protocol-property",org.openspcoop2.core.registry.ProtocolProperty.class,"soggetto",Soggetto.class));
		this.CREDENZIALI = new org.openspcoop2.core.registry.model.CredenzialiSoggettoModel(new Field("credenziali",org.openspcoop2.core.registry.CredenzialiSoggetto.class,"soggetto",Soggetto.class));
		this.RUOLI = new org.openspcoop2.core.registry.model.RuoliSoggettoModel(new Field("ruoli",org.openspcoop2.core.registry.RuoliSoggetto.class,"soggetto",Soggetto.class));
		this.ACCORDO_SERVIZIO_PARTE_SPECIFICA = new org.openspcoop2.core.registry.model.AccordoServizioParteSpecificaModel(new Field("accordo-servizio-parte-specifica",org.openspcoop2.core.registry.AccordoServizioParteSpecifica.class,"soggetto",Soggetto.class));
		this.PROPRIETA_OGGETTO = new org.openspcoop2.core.registry.model.ProprietaOggettoModel(new Field("proprieta-oggetto",org.openspcoop2.core.registry.ProprietaOggetto.class,"soggetto",Soggetto.class));
		this.SUPER_USER = new Field("super-user",java.lang.String.class,"soggetto",Soggetto.class);
		this.PRIVATO = new Field("privato",Boolean.class,"soggetto",Soggetto.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"soggetto",Soggetto.class);
		this.NOME = new Field("nome",java.lang.String.class,"soggetto",Soggetto.class);
		this.IDENTIFICATIVO_PORTA = new Field("identificativo-porta",java.lang.String.class,"soggetto",Soggetto.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"soggetto",Soggetto.class);
		this.PORTA_DOMINIO = new Field("porta-dominio",java.lang.String.class,"soggetto",Soggetto.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"soggetto",Soggetto.class);
		this.VERSIONE_PROTOCOLLO = new Field("versione-protocollo",java.lang.String.class,"soggetto",Soggetto.class);
		this.CODICE_IPA = new Field("codice-ipa",java.lang.String.class,"soggetto",Soggetto.class);
	
	}
	
	public SoggettoModel(IField father){
	
		super(father);
	
		this.CONNETTORE = new org.openspcoop2.core.registry.model.ConnettoreModel(new ComplexField(father,"connettore",org.openspcoop2.core.registry.Connettore.class,"soggetto",Soggetto.class));
		this.PROPRIETA = new org.openspcoop2.core.registry.model.ProprietaModel(new ComplexField(father,"proprieta",org.openspcoop2.core.registry.Proprieta.class,"soggetto",Soggetto.class));
		this.PROTOCOL_PROPERTY = new org.openspcoop2.core.registry.model.ProtocolPropertyModel(new ComplexField(father,"protocol-property",org.openspcoop2.core.registry.ProtocolProperty.class,"soggetto",Soggetto.class));
		this.CREDENZIALI = new org.openspcoop2.core.registry.model.CredenzialiSoggettoModel(new ComplexField(father,"credenziali",org.openspcoop2.core.registry.CredenzialiSoggetto.class,"soggetto",Soggetto.class));
		this.RUOLI = new org.openspcoop2.core.registry.model.RuoliSoggettoModel(new ComplexField(father,"ruoli",org.openspcoop2.core.registry.RuoliSoggetto.class,"soggetto",Soggetto.class));
		this.ACCORDO_SERVIZIO_PARTE_SPECIFICA = new org.openspcoop2.core.registry.model.AccordoServizioParteSpecificaModel(new ComplexField(father,"accordo-servizio-parte-specifica",org.openspcoop2.core.registry.AccordoServizioParteSpecifica.class,"soggetto",Soggetto.class));
		this.PROPRIETA_OGGETTO = new org.openspcoop2.core.registry.model.ProprietaOggettoModel(new ComplexField(father,"proprieta-oggetto",org.openspcoop2.core.registry.ProprietaOggetto.class,"soggetto",Soggetto.class));
		this.SUPER_USER = new ComplexField(father,"super-user",java.lang.String.class,"soggetto",Soggetto.class);
		this.PRIVATO = new ComplexField(father,"privato",Boolean.class,"soggetto",Soggetto.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"soggetto",Soggetto.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"soggetto",Soggetto.class);
		this.IDENTIFICATIVO_PORTA = new ComplexField(father,"identificativo-porta",java.lang.String.class,"soggetto",Soggetto.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"soggetto",Soggetto.class);
		this.PORTA_DOMINIO = new ComplexField(father,"porta-dominio",java.lang.String.class,"soggetto",Soggetto.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"soggetto",Soggetto.class);
		this.VERSIONE_PROTOCOLLO = new ComplexField(father,"versione-protocollo",java.lang.String.class,"soggetto",Soggetto.class);
		this.CODICE_IPA = new ComplexField(father,"codice-ipa",java.lang.String.class,"soggetto",Soggetto.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.ConnettoreModel CONNETTORE = null;
	 
	public org.openspcoop2.core.registry.model.ProprietaModel PROPRIETA = null;
	 
	public org.openspcoop2.core.registry.model.ProtocolPropertyModel PROTOCOL_PROPERTY = null;
	 
	public org.openspcoop2.core.registry.model.CredenzialiSoggettoModel CREDENZIALI = null;
	 
	public org.openspcoop2.core.registry.model.RuoliSoggettoModel RUOLI = null;
	 
	public org.openspcoop2.core.registry.model.AccordoServizioParteSpecificaModel ACCORDO_SERVIZIO_PARTE_SPECIFICA = null;
	 
	public org.openspcoop2.core.registry.model.ProprietaOggettoModel PROPRIETA_OGGETTO = null;
	 
	public IField SUPER_USER = null;
	 
	public IField PRIVATO = null;
	 
	public IField TIPO = null;
	 
	public IField NOME = null;
	 
	public IField IDENTIFICATIVO_PORTA = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField PORTA_DOMINIO = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField VERSIONE_PROTOCOLLO = null;
	 
	public IField CODICE_IPA = null;
	 

	@Override
	public Class<Soggetto> getModeledClass(){
		return Soggetto.class;
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