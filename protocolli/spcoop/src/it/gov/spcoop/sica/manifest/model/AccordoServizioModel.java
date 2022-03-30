/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.manifest.model;

import it.gov.spcoop.sica.manifest.AccordoServizio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoServizio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioModel extends AbstractModel<AccordoServizio> {

	public AccordoServizioModel(){
	
		super();
	
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"accordoServizio",AccordoServizio.class);
		this.SPECIFICA_SEMIFORMALE = new it.gov.spcoop.sica.manifest.model.SpecificaSemiformaleModel(new Field("specificaSemiformale",it.gov.spcoop.sica.manifest.SpecificaSemiformale.class,"accordoServizio",AccordoServizio.class));
		this.ALLEGATI = new it.gov.spcoop.sica.manifest.model.ElencoAllegatiModel(new Field("allegati",it.gov.spcoop.sica.manifest.ElencoAllegati.class,"accordoServizio",AccordoServizio.class));
		this.PARTE_COMUNE = new it.gov.spcoop.sica.manifest.model.AccordoServizioParteComuneModel(new Field("parteComune",it.gov.spcoop.sica.manifest.AccordoServizioParteComune.class,"accordoServizio",AccordoServizio.class));
		this.PARTE_SPECIFICA = new it.gov.spcoop.sica.manifest.model.AccordoServizioParteSpecificaModel(new Field("parteSpecifica",it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica.class,"accordoServizio",AccordoServizio.class));
		this.NOME = new Field("nome",java.lang.String.class,"accordoServizio",AccordoServizio.class);
		this.VERSIONE = new Field("versione",java.lang.String.class,"accordoServizio",AccordoServizio.class);
		this.DATA_CREAZIONE = new Field("dataCreazione",java.util.Date.class,"accordoServizio",AccordoServizio.class);
		this.DATA_PUBBLICAZIONE = new Field("dataPubblicazione",java.util.Date.class,"accordoServizio",AccordoServizio.class);
		this.FIRMATO = new Field("firmato",boolean.class,"accordoServizio",AccordoServizio.class);
		this.RISERVATO = new Field("riservato",boolean.class,"accordoServizio",AccordoServizio.class);
	
	}
	
	public AccordoServizioModel(IField father){
	
		super(father);
	
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"accordoServizio",AccordoServizio.class);
		this.SPECIFICA_SEMIFORMALE = new it.gov.spcoop.sica.manifest.model.SpecificaSemiformaleModel(new ComplexField(father,"specificaSemiformale",it.gov.spcoop.sica.manifest.SpecificaSemiformale.class,"accordoServizio",AccordoServizio.class));
		this.ALLEGATI = new it.gov.spcoop.sica.manifest.model.ElencoAllegatiModel(new ComplexField(father,"allegati",it.gov.spcoop.sica.manifest.ElencoAllegati.class,"accordoServizio",AccordoServizio.class));
		this.PARTE_COMUNE = new it.gov.spcoop.sica.manifest.model.AccordoServizioParteComuneModel(new ComplexField(father,"parteComune",it.gov.spcoop.sica.manifest.AccordoServizioParteComune.class,"accordoServizio",AccordoServizio.class));
		this.PARTE_SPECIFICA = new it.gov.spcoop.sica.manifest.model.AccordoServizioParteSpecificaModel(new ComplexField(father,"parteSpecifica",it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica.class,"accordoServizio",AccordoServizio.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"accordoServizio",AccordoServizio.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"accordoServizio",AccordoServizio.class);
		this.DATA_CREAZIONE = new ComplexField(father,"dataCreazione",java.util.Date.class,"accordoServizio",AccordoServizio.class);
		this.DATA_PUBBLICAZIONE = new ComplexField(father,"dataPubblicazione",java.util.Date.class,"accordoServizio",AccordoServizio.class);
		this.FIRMATO = new ComplexField(father,"firmato",boolean.class,"accordoServizio",AccordoServizio.class);
		this.RISERVATO = new ComplexField(father,"riservato",boolean.class,"accordoServizio",AccordoServizio.class);
	
	}
	
	

	public IField DESCRIZIONE = null;
	 
	public it.gov.spcoop.sica.manifest.model.SpecificaSemiformaleModel SPECIFICA_SEMIFORMALE = null;
	 
	public it.gov.spcoop.sica.manifest.model.ElencoAllegatiModel ALLEGATI = null;
	 
	public it.gov.spcoop.sica.manifest.model.AccordoServizioParteComuneModel PARTE_COMUNE = null;
	 
	public it.gov.spcoop.sica.manifest.model.AccordoServizioParteSpecificaModel PARTE_SPECIFICA = null;
	 
	public IField NOME = null;
	 
	public IField VERSIONE = null;
	 
	public IField DATA_CREAZIONE = null;
	 
	public IField DATA_PUBBLICAZIONE = null;
	 
	public IField FIRMATO = null;
	 
	public IField RISERVATO = null;
	 

	@Override
	public Class<AccordoServizio> getModeledClass(){
		return AccordoServizio.class;
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