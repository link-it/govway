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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.DumpConfigurazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DumpConfigurazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpConfigurazioneModel extends AbstractModel<DumpConfigurazione> {

	public DumpConfigurazioneModel(){
	
		super();
	
		this.RICHIESTA_INGRESSO = new org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel(new Field("richiesta-ingresso",org.openspcoop2.core.config.DumpConfigurazioneRegola.class,"dump-configurazione",DumpConfigurazione.class));
		this.RICHIESTA_USCITA = new org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel(new Field("richiesta-uscita",org.openspcoop2.core.config.DumpConfigurazioneRegola.class,"dump-configurazione",DumpConfigurazione.class));
		this.RISPOSTA_INGRESSO = new org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel(new Field("risposta-ingresso",org.openspcoop2.core.config.DumpConfigurazioneRegola.class,"dump-configurazione",DumpConfigurazione.class));
		this.RISPOSTA_USCITA = new org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel(new Field("risposta-uscita",org.openspcoop2.core.config.DumpConfigurazioneRegola.class,"dump-configurazione",DumpConfigurazione.class));
		this.REALTIME = new Field("realtime",java.lang.String.class,"dump-configurazione",DumpConfigurazione.class);
	
	}
	
	public DumpConfigurazioneModel(IField father){
	
		super(father);
	
		this.RICHIESTA_INGRESSO = new org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel(new ComplexField(father,"richiesta-ingresso",org.openspcoop2.core.config.DumpConfigurazioneRegola.class,"dump-configurazione",DumpConfigurazione.class));
		this.RICHIESTA_USCITA = new org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel(new ComplexField(father,"richiesta-uscita",org.openspcoop2.core.config.DumpConfigurazioneRegola.class,"dump-configurazione",DumpConfigurazione.class));
		this.RISPOSTA_INGRESSO = new org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel(new ComplexField(father,"risposta-ingresso",org.openspcoop2.core.config.DumpConfigurazioneRegola.class,"dump-configurazione",DumpConfigurazione.class));
		this.RISPOSTA_USCITA = new org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel(new ComplexField(father,"risposta-uscita",org.openspcoop2.core.config.DumpConfigurazioneRegola.class,"dump-configurazione",DumpConfigurazione.class));
		this.REALTIME = new ComplexField(father,"realtime",java.lang.String.class,"dump-configurazione",DumpConfigurazione.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel RICHIESTA_INGRESSO = null;
	 
	public org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel RICHIESTA_USCITA = null;
	 
	public org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel RISPOSTA_INGRESSO = null;
	 
	public org.openspcoop2.core.config.model.DumpConfigurazioneRegolaModel RISPOSTA_USCITA = null;
	 
	public IField REALTIME = null;
	 

	@Override
	public Class<DumpConfigurazione> getModeledClass(){
		return DumpConfigurazione.class;
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