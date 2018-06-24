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
package it.gov.spcoop.sica.manifest.model;

import it.gov.spcoop.sica.manifest.AccordoCooperazione;

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
	
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"accordoCooperazione",AccordoCooperazione.class);
		this.SPECIFICA_SEMIFORMALE = new it.gov.spcoop.sica.manifest.model.SpecificaSemiformaleModel(new Field("specificaSemiformale",it.gov.spcoop.sica.manifest.SpecificaSemiformale.class,"accordoCooperazione",AccordoCooperazione.class));
		this.ALLEGATI = new it.gov.spcoop.sica.manifest.model.ElencoAllegatiModel(new Field("allegati",it.gov.spcoop.sica.manifest.ElencoAllegati.class,"accordoCooperazione",AccordoCooperazione.class));
		this.ELENCO_PARTECIPANTI = new it.gov.spcoop.sica.manifest.model.ElencoPartecipantiModel(new Field("elencoPartecipanti",it.gov.spcoop.sica.manifest.ElencoPartecipanti.class,"accordoCooperazione",AccordoCooperazione.class));
		this.SERVIZI_COMPOSTI = new it.gov.spcoop.sica.manifest.model.ElencoServiziCompostiModel(new Field("serviziComposti",it.gov.spcoop.sica.manifest.ElencoServiziComposti.class,"accordoCooperazione",AccordoCooperazione.class));
		this.NOME = new Field("nome",java.lang.String.class,"accordoCooperazione",AccordoCooperazione.class);
		this.VERSIONE = new Field("versione",java.lang.String.class,"accordoCooperazione",AccordoCooperazione.class);
		this.DATA_CREAZIONE = new Field("dataCreazione",java.util.Date.class,"accordoCooperazione",AccordoCooperazione.class);
		this.DATA_PUBBLICAZIONE = new Field("dataPubblicazione",java.util.Date.class,"accordoCooperazione",AccordoCooperazione.class);
		this.FIRMATO = new Field("firmato",boolean.class,"accordoCooperazione",AccordoCooperazione.class);
		this.RISERVATO = new Field("riservato",boolean.class,"accordoCooperazione",AccordoCooperazione.class);
		this.COORDINATORE = new Field("coordinatore",java.net.URI.class,"accordoCooperazione",AccordoCooperazione.class);
	
	}
	
	public AccordoCooperazioneModel(IField father){
	
		super(father);
	
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"accordoCooperazione",AccordoCooperazione.class);
		this.SPECIFICA_SEMIFORMALE = new it.gov.spcoop.sica.manifest.model.SpecificaSemiformaleModel(new ComplexField(father,"specificaSemiformale",it.gov.spcoop.sica.manifest.SpecificaSemiformale.class,"accordoCooperazione",AccordoCooperazione.class));
		this.ALLEGATI = new it.gov.spcoop.sica.manifest.model.ElencoAllegatiModel(new ComplexField(father,"allegati",it.gov.spcoop.sica.manifest.ElencoAllegati.class,"accordoCooperazione",AccordoCooperazione.class));
		this.ELENCO_PARTECIPANTI = new it.gov.spcoop.sica.manifest.model.ElencoPartecipantiModel(new ComplexField(father,"elencoPartecipanti",it.gov.spcoop.sica.manifest.ElencoPartecipanti.class,"accordoCooperazione",AccordoCooperazione.class));
		this.SERVIZI_COMPOSTI = new it.gov.spcoop.sica.manifest.model.ElencoServiziCompostiModel(new ComplexField(father,"serviziComposti",it.gov.spcoop.sica.manifest.ElencoServiziComposti.class,"accordoCooperazione",AccordoCooperazione.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"accordoCooperazione",AccordoCooperazione.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"accordoCooperazione",AccordoCooperazione.class);
		this.DATA_CREAZIONE = new ComplexField(father,"dataCreazione",java.util.Date.class,"accordoCooperazione",AccordoCooperazione.class);
		this.DATA_PUBBLICAZIONE = new ComplexField(father,"dataPubblicazione",java.util.Date.class,"accordoCooperazione",AccordoCooperazione.class);
		this.FIRMATO = new ComplexField(father,"firmato",boolean.class,"accordoCooperazione",AccordoCooperazione.class);
		this.RISERVATO = new ComplexField(father,"riservato",boolean.class,"accordoCooperazione",AccordoCooperazione.class);
		this.COORDINATORE = new ComplexField(father,"coordinatore",java.net.URI.class,"accordoCooperazione",AccordoCooperazione.class);
	
	}
	
	

	public IField DESCRIZIONE = null;
	 
	public it.gov.spcoop.sica.manifest.model.SpecificaSemiformaleModel SPECIFICA_SEMIFORMALE = null;
	 
	public it.gov.spcoop.sica.manifest.model.ElencoAllegatiModel ALLEGATI = null;
	 
	public it.gov.spcoop.sica.manifest.model.ElencoPartecipantiModel ELENCO_PARTECIPANTI = null;
	 
	public it.gov.spcoop.sica.manifest.model.ElencoServiziCompostiModel SERVIZI_COMPOSTI = null;
	 
	public IField NOME = null;
	 
	public IField VERSIONE = null;
	 
	public IField DATA_CREAZIONE = null;
	 
	public IField DATA_PUBBLICAZIONE = null;
	 
	public IField FIRMATO = null;
	 
	public IField RISERVATO = null;
	 
	public IField COORDINATORE = null;
	 

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