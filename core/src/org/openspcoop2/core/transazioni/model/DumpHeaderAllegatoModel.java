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
package org.openspcoop2.core.transazioni.model;

import org.openspcoop2.core.transazioni.DumpHeaderAllegato;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DumpHeaderAllegato 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpHeaderAllegatoModel extends AbstractModel<DumpHeaderAllegato> {

	public DumpHeaderAllegatoModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"dump-header-allegato",DumpHeaderAllegato.class);
		this.VALORE = new Field("valore",java.lang.String.class,"dump-header-allegato",DumpHeaderAllegato.class);
		this.DUMP_TIMESTAMP = new Field("dump-timestamp",java.util.Date.class,"dump-header-allegato",DumpHeaderAllegato.class);
	
	}
	
	public DumpHeaderAllegatoModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"dump-header-allegato",DumpHeaderAllegato.class);
		this.VALORE = new ComplexField(father,"valore",java.lang.String.class,"dump-header-allegato",DumpHeaderAllegato.class);
		this.DUMP_TIMESTAMP = new ComplexField(father,"dump-timestamp",java.util.Date.class,"dump-header-allegato",DumpHeaderAllegato.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField VALORE = null;
	 
	public IField DUMP_TIMESTAMP = null;
	 

	@Override
	public Class<DumpHeaderAllegato> getModeledClass(){
		return DumpHeaderAllegato.class;
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