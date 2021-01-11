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
package org.openspcoop2.monitor.engine.config.base.model;

import org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PluginServizioAzioneCompatibilita 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PluginServizioAzioneCompatibilitaModel extends AbstractModel<PluginServizioAzioneCompatibilita> {

	public PluginServizioAzioneCompatibilitaModel(){
	
		super();
	
		this.AZIONE = new Field("azione",java.lang.String.class,"plugin-servizio-azione-compatibilita",PluginServizioAzioneCompatibilita.class);
	
	}
	
	public PluginServizioAzioneCompatibilitaModel(IField father){
	
		super(father);
	
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"plugin-servizio-azione-compatibilita",PluginServizioAzioneCompatibilita.class);
	
	}
	
	

	public IField AZIONE = null;
	 

	@Override
	public Class<PluginServizioAzioneCompatibilita> getModeledClass(){
		return PluginServizioAzioneCompatibilita.class;
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