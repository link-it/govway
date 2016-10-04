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

import org.openspcoop2.core.registry.AccordoCooperazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoCooperazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoCooperazioneModel extends AbstractModel<AccordoCooperazione> {

	public AccordoCooperazioneModel(){
	
		super();
	
		this.URI_SERVIZI_COMPOSTI = new Field("uri-servizi-composti",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.SOGGETTO_REFERENTE = new org.openspcoop2.core.registry.model.IdSoggettoModel(new Field("soggetto-referente",org.openspcoop2.core.registry.IdSoggetto.class,"accordo-cooperazione",AccordoCooperazione.class));
		this.ELENCO_PARTECIPANTI = new org.openspcoop2.core.registry.model.AccordoCooperazionePartecipantiModel(new Field("elenco-partecipanti",org.openspcoop2.core.registry.AccordoCooperazionePartecipanti.class,"accordo-cooperazione",AccordoCooperazione.class));
		this.ALLEGATO = new org.openspcoop2.core.registry.model.DocumentoModel(new Field("allegato",org.openspcoop2.core.registry.Documento.class,"accordo-cooperazione",AccordoCooperazione.class));
		this.SPECIFICA_SEMIFORMALE = new org.openspcoop2.core.registry.model.DocumentoModel(new Field("specifica-semiformale",org.openspcoop2.core.registry.Documento.class,"accordo-cooperazione",AccordoCooperazione.class));
		this.SUPER_USER = new Field("super-user",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.STATO_PACKAGE = new Field("stato-package",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.PRIVATO = new Field("privato",Boolean.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.NOME = new Field("nome",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.VERSIONE = new Field("versione",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"accordo-cooperazione",AccordoCooperazione.class);
	
	}
	
	public AccordoCooperazioneModel(IField father){
	
		super(father);
	
		this.URI_SERVIZI_COMPOSTI = new ComplexField(father,"uri-servizi-composti",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.SOGGETTO_REFERENTE = new org.openspcoop2.core.registry.model.IdSoggettoModel(new ComplexField(father,"soggetto-referente",org.openspcoop2.core.registry.IdSoggetto.class,"accordo-cooperazione",AccordoCooperazione.class));
		this.ELENCO_PARTECIPANTI = new org.openspcoop2.core.registry.model.AccordoCooperazionePartecipantiModel(new ComplexField(father,"elenco-partecipanti",org.openspcoop2.core.registry.AccordoCooperazionePartecipanti.class,"accordo-cooperazione",AccordoCooperazione.class));
		this.ALLEGATO = new org.openspcoop2.core.registry.model.DocumentoModel(new ComplexField(father,"allegato",org.openspcoop2.core.registry.Documento.class,"accordo-cooperazione",AccordoCooperazione.class));
		this.SPECIFICA_SEMIFORMALE = new org.openspcoop2.core.registry.model.DocumentoModel(new ComplexField(father,"specifica-semiformale",org.openspcoop2.core.registry.Documento.class,"accordo-cooperazione",AccordoCooperazione.class));
		this.SUPER_USER = new ComplexField(father,"super-user",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.STATO_PACKAGE = new ComplexField(father,"stato-package",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.PRIVATO = new ComplexField(father,"privato",Boolean.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"accordo-cooperazione",AccordoCooperazione.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"accordo-cooperazione",AccordoCooperazione.class);
	
	}
	
	

	public IField URI_SERVIZI_COMPOSTI = null;
	 
	public org.openspcoop2.core.registry.model.IdSoggettoModel SOGGETTO_REFERENTE = null;
	 
	public org.openspcoop2.core.registry.model.AccordoCooperazionePartecipantiModel ELENCO_PARTECIPANTI = null;
	 
	public org.openspcoop2.core.registry.model.DocumentoModel ALLEGATO = null;
	 
	public org.openspcoop2.core.registry.model.DocumentoModel SPECIFICA_SEMIFORMALE = null;
	 
	public IField SUPER_USER = null;
	 
	public IField STATO_PACKAGE = null;
	 
	public IField PRIVATO = null;
	 
	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField VERSIONE = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 

	@Override
	public Class<AccordoCooperazione> getModeledClass(){
		return AccordoCooperazione.class;
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