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

package org.openspcoop2.utils.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;

/**     
 * DaylightSavingUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DaylightSavingUtils {
	
	/**
	 * Ora Solare (CET - Central European Time): L'ora solare è il tempo standard utilizzato durante l'inverno. In Europa, è UTC+1, il che significa che è un'ora avanti rispetto al Coordinated Universal Time (UTC). L'ora solare è usata dal tardo autunno all'inizio della primavera, solitamente da fine ottobre a fine marzo.

	   Ora Legale (CEST - Central European Summer Time): L'ora legale viene utilizzata durante l'estate per risparmiare energia, spostando le lancette avanti di un'ora. In Europa, è UTC+2, ovvero due ore avanti rispetto a UTC. L'ora legale è in vigore da fine marzo a fine ottobre.

	   Passaggio da Ora Legale a Ora Solare: In autunno (solitamente l'ultima domenica di ottobre), l'orologio viene spostato indietro di un'ora alle 03:00 AM CEST, tornando alle 02:00 AM CET (ora solare).
	   Passaggio da Ora Solare a Ora Legale: In primavera (solitamente l'ultima domenica di marzo), l'orologio viene spostato avanti di un'ora alle 02:00 AM CET, diventando le 03:00 AM CEST (ora legale).
	 */
	
	private DaylightSavingUtils() {}

    // Formato per il parsing delle date con offset
	private static final String OFFSET_FORMAT = "yyyy-MM-dd HH:mm:ssXXX";
	public static String getOffsetFormat() {
		return OFFSET_FORMAT;
	}
    private static final DateTimeFormatter OFFSET_FORMATTER = DateTimeFormatter.ofPattern(OFFSET_FORMAT);
    public static DateTimeFormatter getOffsetFormatter() {
		return OFFSET_FORMATTER;
	}

	// Formato per il parsing delle date senza offset
    private static final String BASIC_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String getBasicFormat() {
		return BASIC_FORMAT;
	}
    private static final DateTimeFormatter BASIC_FORMATTER = DateTimeFormatter.ofPattern(BASIC_FORMAT);
    public static DateTimeFormatter getBasicFormatter() {
		return BASIC_FORMATTER;
	}

	// Zona di riferimento, ad esempio Europe/Rome
    /**private static final ZoneId ZONE_ID = ZoneId.of("Europe/Rome");*/
    private static final ZoneId ZONE_ID_DEFAULT = ZoneId.systemDefault();
    public static ZoneId getZoneIdDefault() {
		return ZONE_ID_DEFAULT;
	}
    
	/**
     * Calcola quanti minuti mancano al prossimo cambio di ora legale o solare
     * per una data con offset di fuso orario.
     *
     * @param inputDate La data in formato "yyyy-MM-dd HH:mm:ss+01"
     * @return Minuti mancanti al cambio di ora
     */
    public static long minutesUntilNextTransition(String inputDate) {
    	return minutesUntilNextTransition(inputDate, ZONE_ID_DEFAULT);
    }
    public static long minutesUntilNextTransition(String inputDate, ZoneId zoneId) {
    	return minutesUntilNextTransition(inputDate, zoneId, null, null);
    }
    public static long minutesUntilNextTransition(String inputDate, String format) {
    	return minutesUntilNextTransition(inputDate, ZONE_ID_DEFAULT, format);
    }
    public static long minutesUntilNextTransition(String inputDate, ZoneId zoneId, String format) {
    	return minutesUntilNextTransition(inputDate, zoneId, format, null);
    }
    public static long minutesUntilNextTransition(String inputDate, DateTimeFormatter formatter) {
    	return minutesUntilNextTransition(inputDate, ZONE_ID_DEFAULT, formatter);
    }
    public static long minutesUntilNextTransition(String inputDate, ZoneId zoneId, DateTimeFormatter formatter) {
    	return minutesUntilNextTransition(inputDate, zoneId, null, formatter);
    }
    private static long minutesUntilNextTransition(String inputDate, ZoneId zoneId, String format, DateTimeFormatter formatter) {
        OffsetDateTime offsetDateTime = formatter!=null ? parseOffsetDateTime(inputDate, formatter) : parseOffsetDateTime(inputDate, format);
        ZonedDateTime zonedDateTime = offsetDateTime.atZoneSameInstant(zoneId);

        ZoneRules zoneRules = zoneId.getRules();
        ZoneOffsetTransition nextTransition = zoneRules.nextTransition(zonedDateTime.toInstant());

        if (nextTransition != null) {
        	ZonedDateTime transitionDateTime = ZonedDateTime.ofInstant(nextTransition.getDateTimeBefore(), nextTransition.getOffsetBefore(), zoneId);
            return Duration.between(zonedDateTime, transitionDateTime).toMinutes();
        }
        return -1; // Nessuna transizione trovata
    }

    /**
     * Calcola quanti minuti mancano al prossimo cambio di ora legale o solare
     * per una data senza offset di fuso orario.
     *
     * @param inputDate La data in formato "yyyy-MM-dd HH:mm:ss"
     * @return Minuti mancanti al cambio di ora
     */
    public static long minutesUntilNextTransitionWithoutOffset(String inputDate) {
    	return minutesUntilNextTransitionWithoutOffset(inputDate, ZONE_ID_DEFAULT);
    }
    public static long minutesUntilNextTransitionWithoutOffset(String inputDate, ZoneId zoneId) {
    	return minutesUntilNextTransitionWithoutOffset(inputDate, zoneId, null, null);
    }
    public static long minutesUntilNextTransitionWithoutOffset(String inputDate, String format) {
    	return minutesUntilNextTransitionWithoutOffset(inputDate, ZONE_ID_DEFAULT, format);
    }
    public static long minutesUntilNextTransitionWithoutOffset(String inputDate, ZoneId zoneId, String format) {
    	return minutesUntilNextTransitionWithoutOffset(inputDate, zoneId, format, null);
    }
    public static long minutesUntilNextTransitionWithoutOffset(String inputDate, DateTimeFormatter formatter) {
    	return minutesUntilNextTransitionWithoutOffset(inputDate, ZONE_ID_DEFAULT, formatter);
    }
    public static long minutesUntilNextTransitionWithoutOffset(String inputDate, ZoneId zoneId, DateTimeFormatter formatter) {
    	return minutesUntilNextTransitionWithoutOffset(inputDate, zoneId, null, formatter);
    }
    private static long minutesUntilNextTransitionWithoutOffset(String inputDate, ZoneId zoneId, String format, DateTimeFormatter formatter) {
        LocalDateTime localDateTime = formatter!=null ? parseLocalDateTime(inputDate, formatter) : parseLocalDateTime(inputDate, format);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        ZoneRules zoneRules = zoneId.getRules();
        ZoneOffsetTransition nextTransition = zoneRules.nextTransition(zonedDateTime.toInstant());

        if (nextTransition != null) {
        	ZonedDateTime transitionDateTime = ZonedDateTime.ofInstant(nextTransition.getDateTimeBefore(), nextTransition.getOffsetBefore(), zoneId);
            return Duration.between(zonedDateTime, transitionDateTime).toMinutes();
        }
        return -1; // Nessuna transizione trovata
    }
    
    
    /**
     * Determina se una certa data si trova in ora legale o ora solare.
     *
     * @param inputDate La data in formato "yyyy-MM-dd HH:mm:ss+01"
     * @return "ora legale" o "ora solare" a seconda della data
     */
    public static TimeType getTimeType(String inputDate) {
    	return getTimeType(inputDate, ZONE_ID_DEFAULT);
    }
    public static TimeType getTimeType(String inputDate, ZoneId zoneId) {
    	return getTimeType(inputDate, zoneId, null, null);
    }
    public static TimeType getTimeType(String inputDate, String format) {
    	return getTimeType(inputDate, ZONE_ID_DEFAULT, format);
    }
    public static TimeType getTimeType(String inputDate, ZoneId zoneId, String format) {
    	return getTimeType(inputDate, zoneId, format, null);
    }
    public static TimeType getTimeType(String inputDate, DateTimeFormatter formatter) {
    	return getTimeType(inputDate, ZONE_ID_DEFAULT, formatter);
    }
    public static TimeType getTimeType(String inputDate, ZoneId zoneId, DateTimeFormatter formatter) {
    	return getTimeType(inputDate, zoneId, null, formatter);
    }
    private static TimeType getTimeType(String inputDate, ZoneId zoneId, String format, DateTimeFormatter formatter) {
        OffsetDateTime offsetDateTime = formatter!=null ? parseOffsetDateTime(inputDate, formatter) : parseOffsetDateTime(inputDate, format);
        ZonedDateTime zonedDateTime = offsetDateTime.atZoneSameInstant(zoneId);

        boolean isDaylightSavings = zonedDateTime.getZone().getRules().isDaylightSavings(zonedDateTime.toInstant());
        return isDaylightSavings ? TimeType.DAYLIGHT_SAVING_TIME : TimeType.STANDARD_TIME;
    }

    /**
     * Converte una data in formato "yyyy-MM-dd HH:mm:ss+01" in un ZonedDateTime
     * per il fuso orario specificato (es. Europe/Rome).
     *
     * @param inputDate La data in formato "yyyy-MM-dd HH:mm:ss+01"
     * @return ZonedDateTime corrispondente
     */
    public static ZonedDateTime convertToZonedDateTime(String inputDate) {
    	return convertToZonedDateTime(inputDate, ZONE_ID_DEFAULT);
    }
    public static ZonedDateTime convertToZonedDateTime(String inputDate, ZoneId zoneId) {
    	return convertToZonedDateTime(inputDate, zoneId, null, null);
    }
    public static ZonedDateTime convertToZonedDateTime(String inputDate, String format) {
    	return convertToZonedDateTime(inputDate, ZONE_ID_DEFAULT, format);
    }
    public static ZonedDateTime convertToZonedDateTime(String inputDate, ZoneId zoneId, String format) {
    	return convertToZonedDateTime(inputDate, zoneId, format, null);
    }
    public static ZonedDateTime convertToZonedDateTime(String inputDate, DateTimeFormatter formatter) {
    	return convertToZonedDateTime(inputDate, ZONE_ID_DEFAULT, formatter);
    }
    public static ZonedDateTime convertToZonedDateTime(String inputDate, ZoneId zoneId, DateTimeFormatter formatter) {
    	return convertToZonedDateTime(inputDate, zoneId, null, formatter);
    }
    private static ZonedDateTime convertToZonedDateTime(String inputDate, ZoneId zoneId, String format, DateTimeFormatter formatter) {
        OffsetDateTime offsetDateTime = formatter!=null ? parseOffsetDateTime(inputDate, formatter) : parseOffsetDateTime(inputDate, format);
        return offsetDateTime.atZoneSameInstant(zoneId);
    }

    
    /**
     * Determina se una data è nel giorno in cui avviene il cambio tra ora legale e ora solare (o viceversa).
     *
     * @param inputDate La data in formato "yyyy-MM-dd HH:mm:ss+01:00"
     * @return true se la data è nel giorno del cambio di ora, false altrimenti
     */
    public static TimeTransitionType getTimeChangePendingToday(String inputDate) {
    	return getTimeChangePendingToday(inputDate, ZONE_ID_DEFAULT);
    }
    public static TimeTransitionType getTimeChangePendingToday(String inputDate, ZoneId zoneId) {
    	return getTimeChangePendingToday(inputDate, zoneId, null, null);
    }
    public static TimeTransitionType getTimeChangePendingToday(String inputDate, String format) {
    	return getTimeChangePendingToday(inputDate, ZONE_ID_DEFAULT, format);
    }
    public static TimeTransitionType getTimeChangePendingToday(String inputDate, ZoneId zoneId, String format) {
    	return getTimeChangePendingToday(inputDate, zoneId, format, null);
    }
    public static TimeTransitionType getTimeChangePendingToday(String inputDate, DateTimeFormatter formatter) {
    	return getTimeChangePendingToday(inputDate, ZONE_ID_DEFAULT, formatter);
    }
    public static TimeTransitionType getTimeChangePendingToday(String inputDate, ZoneId zoneId, DateTimeFormatter formatter) {
    	return getTimeChangePendingToday(inputDate, zoneId, null, formatter);
    }
    private static TimeTransitionType getTimeChangePendingToday(String inputDate, ZoneId zoneId, String format, DateTimeFormatter formatter) {
        // Parse della stringa in OffsetDateTime
        OffsetDateTime dateTime = formatter!=null ? parseOffsetDateTime(inputDate, formatter) : parseOffsetDateTime(inputDate, format);

        // Ottieni il fuso orario dell'ambiente (ad esempio Europe/Rome)
         ZoneRules zoneRules = zoneId.getRules();

        // Ottieni la transizione successiva
        ZoneOffsetTransition nextTransition = zoneRules.nextTransition(dateTime.toInstant());

        if (nextTransition != null) {
            // Ottieni la data e l'ora del giorno della transizione
            LocalDate transitionDate = nextTransition.getDateTimeBefore().toLocalDate();

            // Verifica se la data fornita è nello stesso giorno della transizione
            if( dateTime.toLocalDate().equals(transitionDate) ) {
        	     // Ottieni gli offset prima e dopo la transizione
                ZoneOffset offsetBefore = nextTransition.getOffsetBefore();
                ZoneOffset offsetAfter = nextTransition.getOffsetAfter();

                // Determina il tipo di transizione
                if (offsetBefore.getTotalSeconds() > offsetAfter.getTotalSeconds()) {
                	return TimeTransitionType.FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME;  // Ora legale a ora solare
                } else {
                    return TimeTransitionType.FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME;  // Ora solare a ora legale
                }

            }
        }
        
        return null;  // Nessuna transizione trovata
    }
    
    
    /**
     * Determina se una data è nel giorno in cui avviene il cambio tra ora legale e ora solare (o viceversa) per una data senza offset di fuso orario.
     *
     * @param inputDate La data in formato "yyyy-MM-dd HH:mm:ss"
     * @return true se la data è nel giorno del cambio di ora, false altrimenti
     */
    public static TimeTransitionType getTimeChangePendingTodayWithoutOffset(String inputDate) {
    	return getTimeChangePendingTodayWithoutOffset(inputDate, ZONE_ID_DEFAULT);
    }
    public static TimeTransitionType getTimeChangePendingTodayWithoutOffset(String inputDate, ZoneId zoneId) {
    	return getTimeChangePendingTodayWithoutOffset(inputDate, zoneId, null, null);
    }
    public static TimeTransitionType getTimeChangePendingTodayWithoutOffset(String inputDate, String format) {
    	return getTimeChangePendingTodayWithoutOffset(inputDate, ZONE_ID_DEFAULT, format);
    }
    public static TimeTransitionType getTimeChangePendingTodayWithoutOffset(String inputDate, ZoneId zoneId, String format) {
    	return getTimeChangePendingTodayWithoutOffset(inputDate, zoneId, format, null);
    }
    public static TimeTransitionType getTimeChangePendingTodayWithoutOffset(String inputDate, DateTimeFormatter formatter) {
    	return getTimeChangePendingTodayWithoutOffset(inputDate, ZONE_ID_DEFAULT, formatter);
    }
    public static TimeTransitionType getTimeChangePendingTodayWithoutOffset(String inputDate, ZoneId zoneId, DateTimeFormatter formatter) {
    	return getTimeChangePendingTodayWithoutOffset(inputDate, zoneId, null, formatter);
    }
    private static TimeTransitionType getTimeChangePendingTodayWithoutOffset(String inputDate, ZoneId zoneId, String format, DateTimeFormatter formatter) {
    	
    	LocalDateTime localDateTime = formatter!=null ? parseLocalDateTime(inputDate, formatter) : parseLocalDateTime(inputDate, format);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        ZoneRules zoneRules = zoneId.getRules();
        ZoneOffsetTransition nextTransition = zoneRules.nextTransition(zonedDateTime.toInstant());

        if (nextTransition != null) {
        	ZonedDateTime transitionDateTime = ZonedDateTime.ofInstant(nextTransition.getDateTimeBefore(), nextTransition.getOffsetBefore(), zoneId);
        	
        	  // Verifica se la data fornita è nello stesso giorno della transizione
            if( localDateTime.toLocalDate().equals(transitionDateTime.toLocalDate()) ) {
        	     // Ottieni gli offset prima e dopo la transizione
                ZoneOffset offsetBefore = nextTransition.getOffsetBefore();
                ZoneOffset offsetAfter = nextTransition.getOffsetAfter();

                // Determina il tipo di transizione
                if (offsetBefore.getTotalSeconds() > offsetAfter.getTotalSeconds()) {
                	return TimeTransitionType.FROM_DAYLIGHT_SAVING_TO_STANDARD_TIME;  // Ora legale a ora solare
                } else {
                    return TimeTransitionType.FROM_STANDARD_TO_DAYLIGHT_SAVING_TIME;  // Ora solare a ora legale
                }

            }
        }
    	
        return null;  // Nessuna transizione trovata
    }
    
    
    
    /** Parser */
    
    public static LocalDateTime parseLocalDateTime(String inputDate) {
    	return parseLocalDateTime(inputDate, BASIC_FORMATTER);
    }
    public static LocalDateTime parseLocalDateTime(String inputDate, String format) {
    	if(format==null) {
    		return parseLocalDateTime(inputDate);
    	}
    	return parseLocalDateTime(inputDate, DateTimeFormatter.ofPattern(format));
    }
    public static LocalDateTime parseLocalDateTime(String inputDate, DateTimeFormatter formatter) {
    	if(formatter==null) {
    		return parseLocalDateTime(inputDate);
    	}
    	return LocalDateTime.parse(inputDate, formatter);
    }
    
    public static OffsetDateTime parseOffsetDateTime(String inputDate) {
    	return parseOffsetDateTime(inputDate, OFFSET_FORMATTER);
    }
    public static OffsetDateTime parseOffsetDateTime(String inputDate, String format) {
    	if(format==null) {
    		return parseOffsetDateTime(inputDate);
    	}
    	return parseOffsetDateTime(inputDate, DateTimeFormatter.ofPattern(format));
    }
    public static OffsetDateTime parseOffsetDateTime(String inputDate, DateTimeFormatter formatter) {
    	if(formatter==null) {
    		return parseOffsetDateTime(inputDate);
    	}
    	if (inputDate.matches(".*[+-]\\d{2}$")) {
            inputDate += ":00";
        }
    	return OffsetDateTime.parse(inputDate, formatter);
    }
}
