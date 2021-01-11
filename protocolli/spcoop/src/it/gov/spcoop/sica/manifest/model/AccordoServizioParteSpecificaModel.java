/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica;

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
	
		this.RIFERIMENTO_PARTE_COMUNE = new Field("riferimentoParteComune",java.net.URI.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
		this.SPECIFICA_PORTI_ACCESSO = new it.gov.spcoop.sica.manifest.model.SpecificaPortiAccessoModel(new Field("specificaPortiAccesso",it.gov.spcoop.sica.manifest.SpecificaPortiAccesso.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.SPECIFICA_SICUREZZA = new it.gov.spcoop.sica.manifest.model.SpecificaSicurezzaModel(new Field("specificaSicurezza",it.gov.spcoop.sica.manifest.SpecificaSicurezza.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.SPECIFICA_LIVELLI_SERVIZIO = new it.gov.spcoop.sica.manifest.model.SpecificaLivelliServizioModel(new Field("specificaLivelliServizio",it.gov.spcoop.sica.manifest.SpecificaLivelliServizio.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.ADESIONE = new Field("adesione",java.lang.String.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
		this.EROGATORE = new Field("erogatore",java.net.URI.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
	
	}
	
	public AccordoServizioParteSpecificaModel(IField father){
	
		super(father);
	
		this.RIFERIMENTO_PARTE_COMUNE = new ComplexField(father,"riferimentoParteComune",java.net.URI.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
		this.SPECIFICA_PORTI_ACCESSO = new it.gov.spcoop.sica.manifest.model.SpecificaPortiAccessoModel(new ComplexField(father,"specificaPortiAccesso",it.gov.spcoop.sica.manifest.SpecificaPortiAccesso.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.SPECIFICA_SICUREZZA = new it.gov.spcoop.sica.manifest.model.SpecificaSicurezzaModel(new ComplexField(father,"specificaSicurezza",it.gov.spcoop.sica.manifest.SpecificaSicurezza.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.SPECIFICA_LIVELLI_SERVIZIO = new it.gov.spcoop.sica.manifest.model.SpecificaLivelliServizioModel(new ComplexField(father,"specificaLivelliServizio",it.gov.spcoop.sica.manifest.SpecificaLivelliServizio.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.ADESIONE = new ComplexField(father,"adesione",java.lang.String.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
		this.EROGATORE = new ComplexField(father,"erogatore",java.net.URI.class,"accordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
	
	}
	
	

	public IField RIFERIMENTO_PARTE_COMUNE = null;
	 
	public it.gov.spcoop.sica.manifest.model.SpecificaPortiAccessoModel SPECIFICA_PORTI_ACCESSO = null;
	 
	public it.gov.spcoop.sica.manifest.model.SpecificaSicurezzaModel SPECIFICA_SICUREZZA = null;
	 
	public it.gov.spcoop.sica.manifest.model.SpecificaLivelliServizioModel SPECIFICA_LIVELLI_SERVIZIO = null;
	 
	public IField ADESIONE = null;
	 
	public IField EROGATORE = null;
	 

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