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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IscrizioneREAType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IscrizioneREAType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IscrizioneREATypeModel extends AbstractModel<IscrizioneREAType> {

	public IscrizioneREATypeModel(){
	
		super();
	
		this.UFFICIO = new Field("Ufficio",java.lang.String.class,"IscrizioneREAType",IscrizioneREAType.class);
		this.NUMERO_REA = new Field("NumeroREA",java.lang.String.class,"IscrizioneREAType",IscrizioneREAType.class);
		this.CAPITALE_SOCIALE = new Field("CapitaleSociale",java.math.BigDecimal.class,"IscrizioneREAType",IscrizioneREAType.class);
		this.SOCIO_UNICO = new Field("SocioUnico",java.lang.String.class,"IscrizioneREAType",IscrizioneREAType.class);
		this.STATO_LIQUIDAZIONE = new Field("StatoLiquidazione",java.lang.String.class,"IscrizioneREAType",IscrizioneREAType.class);
	
	}
	
	public IscrizioneREATypeModel(IField father){
	
		super(father);
	
		this.UFFICIO = new ComplexField(father,"Ufficio",java.lang.String.class,"IscrizioneREAType",IscrizioneREAType.class);
		this.NUMERO_REA = new ComplexField(father,"NumeroREA",java.lang.String.class,"IscrizioneREAType",IscrizioneREAType.class);
		this.CAPITALE_SOCIALE = new ComplexField(father,"CapitaleSociale",java.math.BigDecimal.class,"IscrizioneREAType",IscrizioneREAType.class);
		this.SOCIO_UNICO = new ComplexField(father,"SocioUnico",java.lang.String.class,"IscrizioneREAType",IscrizioneREAType.class);
		this.STATO_LIQUIDAZIONE = new ComplexField(father,"StatoLiquidazione",java.lang.String.class,"IscrizioneREAType",IscrizioneREAType.class);
	
	}
	
	

	public IField UFFICIO = null;
	 
	public IField NUMERO_REA = null;
	 
	public IField CAPITALE_SOCIALE = null;
	 
	public IField SOCIO_UNICO = null;
	 
	public IField STATO_LIQUIDAZIONE = null;
	 

	@Override
	public Class<IscrizioneREAType> getModeledClass(){
		return IscrizioneREAType.class;
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