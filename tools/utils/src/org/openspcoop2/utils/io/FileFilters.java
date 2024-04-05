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


package org.openspcoop2.utils.io;

import java.io.File;
import java.io.FileFilter;

/**
 * FileFilters
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileFilters {

	public static final FileFilter DIRECTORIES_ONLY = new FileFilter ()
	{
		@Override
		public boolean accept (File f)
		{
			if (f.exists () && f.isDirectory ()) return true;
			else return false;
		}
	};
	
	public static final FileFilter FILES_ONLY = new FileFilter ()
	{
		@Override
		public boolean accept (File f)
		{
			if (f.exists () && f.isFile ()) return true;
			else return false;
		}
	};
	
	public static final FileFilter HIDDEN_ONLY = new FileFilter ()
	{
		@Override
		public boolean accept (File f)
		{
			if (f.exists () && f.isHidden()) return true;
			else return false;
		}
	};
	
	public static final FileFilter JAR_ONLY = new FileFilter ()
	{
		@Override
		public boolean accept (File f)
		{
			if (f.exists () && f.getName().endsWith(".jar")) return true;
			else return false;
		}
	};
	
	public static final FileFilter WAR_ONLY = new FileFilter ()
	{
		@Override
		public boolean accept (File f)
		{
			if (f.exists () && f.getName().endsWith(".war")) return true;
			else return false;
		}
	};
	
	public static final FileFilter EAR_ONLY = new FileFilter ()
	{
		@Override
		public boolean accept (File f)
		{
			if (f.exists () && f.getName().endsWith(".ear")) return true;
			else return false;
		}
	};
	
}
