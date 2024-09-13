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

package org.openspcoop2.core.monitor.rs.testsuite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ExcelUtils
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExcelUtils {

	private ExcelUtils() {}
	
    public static boolean existsStringByPath(String filePath, String searchString) throws IOException {
        try(FileInputStream file = new FileInputStream(new File(filePath));){
	        return existsString(file, searchString);
        }
    }
    public static boolean existsString(String content, String searchString) throws IOException {
    	return existsString(content.getBytes(), searchString);
    }
    public static boolean existsString(byte[] content, String searchString) throws IOException {
        try(ByteArrayInputStream file = new ByteArrayInputStream(content);){
	        return existsString(file, searchString);
        }
    }
    public static boolean existsString(InputStream is, String searchString) throws IOException {
    	Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0); // Legge il primo foglio

        for (Row row : sheet) {
            for (Cell cell : row) {
                if ( (cell.getCellType() == CellType.STRING) &&
                    (cell.getStringCellValue().contains(searchString)) 
                		) {
                	workbook.close();
                	return true; // Stringa trovata
                }
            }
        }
        workbook.close();
        return false; // Stringa non trovata
    }
}
