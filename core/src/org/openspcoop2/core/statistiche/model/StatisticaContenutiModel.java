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
package org.openspcoop2.core.statistiche.model;

import org.openspcoop2.core.statistiche.StatisticaContenuti;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model StatisticaContenuti 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticaContenutiModel extends AbstractModel<StatisticaContenuti> {

	public StatisticaContenutiModel(){
	
		super();
	
		this.DATA = new Field("data",java.util.Date.class,"statistica-contenuti",StatisticaContenuti.class);
		this.RISORSA_NOME = new Field("risorsa-nome",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.RISORSA_VALORE = new Field("risorsa-valore",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_1 = new Field("filtro-nome-1",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_1 = new Field("filtro-valore-1",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_2 = new Field("filtro-nome-2",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_2 = new Field("filtro-valore-2",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_3 = new Field("filtro-nome-3",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_3 = new Field("filtro-valore-3",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_4 = new Field("filtro-nome-4",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_4 = new Field("filtro-valore-4",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_5 = new Field("filtro-nome-5",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_5 = new Field("filtro-valore-5",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_6 = new Field("filtro-nome-6",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_6 = new Field("filtro-valore-6",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_7 = new Field("filtro-nome-7",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_7 = new Field("filtro-valore-7",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_8 = new Field("filtro-nome-8",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_8 = new Field("filtro-valore-8",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_9 = new Field("filtro-nome-9",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_9 = new Field("filtro-valore-9",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_10 = new Field("filtro-nome-10",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_10 = new Field("filtro-valore-10",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.NUMERO_TRANSAZIONI = new Field("numero-transazioni",java.lang.Integer.class,"statistica-contenuti",StatisticaContenuti.class);
		this.DIMENSIONI_BYTES_BANDA_COMPLESSIVA = new Field("dimensioni-bytes-banda-complessiva",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
		this.DIMENSIONI_BYTES_BANDA_INTERNA = new Field("dimensioni-bytes-banda-interna",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
		this.DIMENSIONI_BYTES_BANDA_ESTERNA = new Field("dimensioni-bytes-banda-esterna",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
		this.LATENZA_TOTALE = new Field("latenza-totale",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
		this.LATENZA_PORTA = new Field("latenza-porta",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
		this.LATENZA_SERVIZIO = new Field("latenza-servizio",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
	
	}
	
	public StatisticaContenutiModel(IField father){
	
		super(father);
	
		this.DATA = new ComplexField(father,"data",java.util.Date.class,"statistica-contenuti",StatisticaContenuti.class);
		this.RISORSA_NOME = new ComplexField(father,"risorsa-nome",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.RISORSA_VALORE = new ComplexField(father,"risorsa-valore",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_1 = new ComplexField(father,"filtro-nome-1",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_1 = new ComplexField(father,"filtro-valore-1",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_2 = new ComplexField(father,"filtro-nome-2",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_2 = new ComplexField(father,"filtro-valore-2",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_3 = new ComplexField(father,"filtro-nome-3",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_3 = new ComplexField(father,"filtro-valore-3",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_4 = new ComplexField(father,"filtro-nome-4",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_4 = new ComplexField(father,"filtro-valore-4",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_5 = new ComplexField(father,"filtro-nome-5",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_5 = new ComplexField(father,"filtro-valore-5",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_6 = new ComplexField(father,"filtro-nome-6",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_6 = new ComplexField(father,"filtro-valore-6",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_7 = new ComplexField(father,"filtro-nome-7",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_7 = new ComplexField(father,"filtro-valore-7",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_8 = new ComplexField(father,"filtro-nome-8",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_8 = new ComplexField(father,"filtro-valore-8",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_9 = new ComplexField(father,"filtro-nome-9",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_9 = new ComplexField(father,"filtro-valore-9",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_NOME_10 = new ComplexField(father,"filtro-nome-10",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.FILTRO_VALORE_10 = new ComplexField(father,"filtro-valore-10",java.lang.String.class,"statistica-contenuti",StatisticaContenuti.class);
		this.NUMERO_TRANSAZIONI = new ComplexField(father,"numero-transazioni",java.lang.Integer.class,"statistica-contenuti",StatisticaContenuti.class);
		this.DIMENSIONI_BYTES_BANDA_COMPLESSIVA = new ComplexField(father,"dimensioni-bytes-banda-complessiva",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
		this.DIMENSIONI_BYTES_BANDA_INTERNA = new ComplexField(father,"dimensioni-bytes-banda-interna",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
		this.DIMENSIONI_BYTES_BANDA_ESTERNA = new ComplexField(father,"dimensioni-bytes-banda-esterna",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
		this.LATENZA_TOTALE = new ComplexField(father,"latenza-totale",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
		this.LATENZA_PORTA = new ComplexField(father,"latenza-porta",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
		this.LATENZA_SERVIZIO = new ComplexField(father,"latenza-servizio",java.lang.Long.class,"statistica-contenuti",StatisticaContenuti.class);
	
	}
	
	

	public IField DATA = null;
	 
	public IField RISORSA_NOME = null;
	 
	public IField RISORSA_VALORE = null;
	 
	public IField FILTRO_NOME_1 = null;
	 
	public IField FILTRO_VALORE_1 = null;
	 
	public IField FILTRO_NOME_2 = null;
	 
	public IField FILTRO_VALORE_2 = null;
	 
	public IField FILTRO_NOME_3 = null;
	 
	public IField FILTRO_VALORE_3 = null;
	 
	public IField FILTRO_NOME_4 = null;
	 
	public IField FILTRO_VALORE_4 = null;
	 
	public IField FILTRO_NOME_5 = null;
	 
	public IField FILTRO_VALORE_5 = null;
	 
	public IField FILTRO_NOME_6 = null;
	 
	public IField FILTRO_VALORE_6 = null;
	 
	public IField FILTRO_NOME_7 = null;
	 
	public IField FILTRO_VALORE_7 = null;
	 
	public IField FILTRO_NOME_8 = null;
	 
	public IField FILTRO_VALORE_8 = null;
	 
	public IField FILTRO_NOME_9 = null;
	 
	public IField FILTRO_VALORE_9 = null;
	 
	public IField FILTRO_NOME_10 = null;
	 
	public IField FILTRO_VALORE_10 = null;
	 
	public IField NUMERO_TRANSAZIONI = null;
	 
	public IField DIMENSIONI_BYTES_BANDA_COMPLESSIVA = null;
	 
	public IField DIMENSIONI_BYTES_BANDA_INTERNA = null;
	 
	public IField DIMENSIONI_BYTES_BANDA_ESTERNA = null;
	 
	public IField LATENZA_TOTALE = null;
	 
	public IField LATENZA_PORTA = null;
	 
	public IField LATENZA_SERVIZIO = null;
	 

	@Override
	public Class<StatisticaContenuti> getModeledClass(){
		return StatisticaContenuti.class;
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