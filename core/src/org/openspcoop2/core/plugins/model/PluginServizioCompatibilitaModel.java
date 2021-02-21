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
package org.openspcoop2.core.plugins.model;

import org.openspcoop2.core.plugins.PluginServizioCompatibilita;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PluginServizioCompatibilita 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PluginServizioCompatibilitaModel extends AbstractModel<PluginServizioCompatibilita> {

	public PluginServizioCompatibilitaModel(){
	
		super();
	
		this.URI_ACCORDO = new Field("uri-accordo",java.lang.String.class,"plugin-servizio-compatibilita",PluginServizioCompatibilita.class);
		this.SERVIZIO = new Field("servizio",java.lang.String.class,"plugin-servizio-compatibilita",PluginServizioCompatibilita.class);
		this.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA = new org.openspcoop2.core.plugins.model.PluginServizioAzioneCompatibilitaModel(new Field("plugin-servizio-azione-compatibilita",org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita.class,"plugin-servizio-compatibilita",PluginServizioCompatibilita.class));
	
	}
	
	public PluginServizioCompatibilitaModel(IField father){
	
		super(father);
	
		this.URI_ACCORDO = new ComplexField(father,"uri-accordo",java.lang.String.class,"plugin-servizio-compatibilita",PluginServizioCompatibilita.class);
		this.SERVIZIO = new ComplexField(father,"servizio",java.lang.String.class,"plugin-servizio-compatibilita",PluginServizioCompatibilita.class);
		this.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA = new org.openspcoop2.core.plugins.model.PluginServizioAzioneCompatibilitaModel(new ComplexField(father,"plugin-servizio-azione-compatibilita",org.openspcoop2.core.plugins.PluginServizioAzioneCompatibilita.class,"plugin-servizio-compatibilita",PluginServizioCompatibilita.class));
	
	}
	
	

	public IField URI_ACCORDO = null;
	 
	public IField SERVIZIO = null;
	 
	public org.openspcoop2.core.plugins.model.PluginServizioAzioneCompatibilitaModel PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA = null;
	 

	@Override
	public Class<PluginServizioCompatibilita> getModeledClass(){
		return PluginServizioCompatibilita.class;
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