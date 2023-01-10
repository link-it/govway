/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.listener;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Comparator
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Comparator {

	public static void main(String[] args) {

		List<String> l = new ArrayList<>();
		l.add("RegioneToscana/api-config/v1");
		l.add("RegioneToscana/BUTTARE/v1");
		l.add("RegioneToscana/api-monitor/v1");
		l.add("RegioneToscana/aPi-monitor/v1");
		l.add("RegioneToscana/Api-monitor/v1");
		l.add("RegioneToscana/ArpaRolesPublisher/v1");
		Collections.sort(l, new IgnoreCaseComp());
		System.out.println("l: "+l);
		/*
 l: [	RegioneToscana/api-config/v1, 
 		RegioneToscana/api-monitor/v1, 
 		RegioneToscana/aPi-monitor/v1,
 		RegioneToscana/Api-monitor/v1, 
 		RegioneToscana/ArpaRolesPublisher/v1, 
 		RegioneToscana/BUTTARE/v1]
		 * */	
	}

	public static class IgnoreCaseComp implements java.util.Comparator<String> {
		private Collator col;

		IgnoreCaseComp() {
			this.col = Collator.getInstance();
			this.col.setStrength(Collator.PRIMARY);
		}

		@Override
		public int compare(String strA, String strB) {
			return this.col.compare(strA, strB);
		}
	}
}
