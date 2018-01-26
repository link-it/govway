/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.generic_project.expression.impl.test.model;


import java.util.Date;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.expression.impl.test.beans.Version;

/**
 * VersionModel
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VersionModel extends AbstractModel<Version> {

	public VersionModel(IField father){
		
		super(father);
		
		this.NUMBER = new ComplexField(father,"number", String.class, "version", Version.class);
		this.DATE = new ComplexField(father,"date", Date.class, "version", Version.class);
		
	}
	
	public IField NUMBER = null;
	public IField DATE = null;
	
	@Override
	public Class<Version> getModeledClass(){
		return Version.class;
	}

}
