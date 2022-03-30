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


package org.openspcoop2.utils.cache.test;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheType;
import org.slf4j.Logger;

/**
 * PerformanceTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PerformanceTest
{
   private static  int loops = 20;

   private static  int tries = 50000;
	
	public static void main(String [] args) throws Exception {
		

        //int maxSize = 1000000;
        //int maxSize = 100000;
		int maxSize = 50000;
    	//int maxSize = 49000;
    	int lifeTime = 500;

    	Cache eh = new Cache(CacheType.EH, "configEH");
    	eh.setCacheSize(maxSize);
    	eh.setItemLifeTime(lifeTime);
    	eh.build();

    	
    	Logger log = LoggerWrapperFactory.getLogger(PerformanceTest.class);
    	Cache.initialize(log, log, 
    			"org/openspcoop2/utils/cache/test/cache.ccf", 
    			null, null, 
    			org.openspcoop2.utils.Costanti.OPENSPCOOP2_LOCAL_HOME, "OPENSPCOOP2_CACHE_PROPERTIES", "OPENSPCOOP2_CONFIG_PROPERTIES");
    	Cache jcs = new Cache(CacheType.JCS, "config");
    	jcs.setCacheSize(maxSize);
    	jcs.setItemLifeTime(lifeTime);
    	jcs.build();
    	
    	

    	Cache limitedHashMap = new Cache(CacheType.LimitedHashMap, "configLHASH");
    	limitedHashMap.setCacheSize(maxSize);
    	limitedHashMap.setItemLifeTime(lifeTime);
    	limitedHashMap.build();
    	
    	
    	
    	
        // run settings
        long start = 0;
        long end = 0;
        long time = 0;
        float tPer = 0;

        long putTotalJCS = 0;
        long getTotalJCS = 0;
        long putTotalEHCache = 0;
        long getTotalEHCache = 0;
        long putTotalLimitedCache = 0;
        long getTotalLimitedCache = 0;

        String jcsDisplayName = "JCS";
        String ehCacheDisplayName = "";
        String limitedCacheDisplayName = "";

        try
        {
            for ( int j = 0; j < loops; j++ )
            {

                jcsDisplayName = "JCS      ";
                start = System.currentTimeMillis();
                for ( int i = 0; i < tries; i++ )
                {
                    jcs.put( "key:" + i, "data" + i );
                }
                end = System.currentTimeMillis();
                time = end - start;
                putTotalJCS += time;
                tPer = Float.intBitsToFloat( (int) time ) / Float.intBitsToFloat( tries );
                System.out
                    .println( jcsDisplayName + " put time for " + tries + " = " + time + "; millis per = " + tPer );

                start = System.currentTimeMillis();
                for ( int i = 0; i < tries; i++ )
                {
                    jcs.get( "key:" + i );
                }
                end = System.currentTimeMillis();
                time = end - start;
                getTotalJCS += time;
                tPer = Float.intBitsToFloat( (int) time ) / Float.intBitsToFloat( tries );
                System.out
                    .println( jcsDisplayName + " get time for " + tries + " = " + time + "; millis per = " + tPer );
                System.out.println("size: "+jcs.getItemCount());

                // /////////////////////////////////////////////////////////////
                ehCacheDisplayName = "EHCache  ";

                start = System.currentTimeMillis();
                for ( int i = 0; i < tries; i++ )
                {
                    eh.put( "key:" + i, "data" + i );
                }
                end = System.currentTimeMillis();
                time = end - start;
                putTotalEHCache += time;
                tPer = Float.intBitsToFloat( (int) time ) / Float.intBitsToFloat( tries );
                System.out.println( ehCacheDisplayName + " put time for " + tries + " = " + time + "; millis per = "
                    + tPer );

                start = System.currentTimeMillis();
                for ( int i = 0; i < tries; i++ )
                {
                    eh.get( "key:" + i );
                }
                end = System.currentTimeMillis();
                time = end - start;
                getTotalEHCache += time;
                tPer = Float.intBitsToFloat( (int) time ) / Float.intBitsToFloat( tries );
                System.out.println( ehCacheDisplayName + " get time for " + tries + " = " + time + "; millis per = "
                    + tPer );
                System.out.println("size: "+eh.getItemCount());
                
                
                // /////////////////////////////////////////////////////////////
                limitedCacheDisplayName = "LimitedHashMap  ";

                start = System.currentTimeMillis();
                for ( int i = 0; i < tries; i++ )
                {
                    limitedHashMap.put( "key:" + i, "data" + i );
                }
                end = System.currentTimeMillis();
                time = end - start;
                putTotalLimitedCache += time;
                tPer = Float.intBitsToFloat( (int) time ) / Float.intBitsToFloat( tries );
                System.out.println( limitedCacheDisplayName + " put time for " + tries + " = " + time + "; millis per = "
                    + tPer );

                start = System.currentTimeMillis();
                for ( int i = 0; i < tries; i++ )
                {
                	limitedHashMap.get( "key:" + i );
                }
                end = System.currentTimeMillis();
                time = end - start;
                getTotalLimitedCache += time;
                tPer = Float.intBitsToFloat( (int) time ) / Float.intBitsToFloat( tries );
                System.out.println( limitedCacheDisplayName + " get time for " + tries + " = " + time + "; millis per = "
                    + tPer );
                System.out.println("size: "+limitedHashMap.getItemCount());

                System.out.println( "\n" );
            }

        }
        catch ( Exception e )
        {
            e.printStackTrace( System.out );
            System.out.println( e );
        }

        System.out.println("\n");
        Cache.printStatistics(System.out, "\n", "\n===========\n");
        
        long putAvJCS = putTotalJCS / loops;
        long getAvJCS = getTotalJCS / loops;
        long putAvEH = putTotalEHCache / loops;
        long getAvEH = getTotalEHCache / loops;
        long putAvLimited = putTotalLimitedCache / loops;
        long getAvLimited = getTotalLimitedCache / loops;

        System.out.println( "Finished " + loops + " loops of " + tries + " gets and puts" );

        System.out.println( "\n" );
        System.out.println( "Put average for " + jcsDisplayName + "  = " + putAvJCS );
        System.out.println( "Put average for " + ehCacheDisplayName + " = " + putAvEH );
        System.out.println( "Put average for " + limitedCacheDisplayName + " = " + putAvLimited );
        float ratioPut = Float.intBitsToFloat( (int) putAvLimited ) / Float.intBitsToFloat( (int) putAvJCS );
        float targetPut = 0.75f;
        String msg = limitedCacheDisplayName + " puts took " + ratioPut + " times the " + jcsDisplayName
            + ", the goal is <" + targetPut + "x";
        System.out.println( msg );
        if(ratioPut>targetPut) {
        	throw new Exception(msg);
        }
        ratioPut = Float.intBitsToFloat( (int) putAvLimited ) / Float.intBitsToFloat( (int) putAvEH );
        msg = limitedCacheDisplayName + " puts took " + ratioPut + " times the " + ehCacheDisplayName
                + ", the goal is <" + targetPut + "x" ;
        System.out.println( msg );
        if(ratioPut>targetPut) {
        	throw new Exception(msg);
        }

        System.out.println( "\n" );
        System.out.println( "Get average for  " + jcsDisplayName + "  = " + getAvJCS );
        System.out.println( "Get average for " + ehCacheDisplayName + " = " + getAvEH );
        System.out.println( "Get average for " + limitedCacheDisplayName + " = " + getAvLimited );
        float ratioGet = Float.intBitsToFloat( (int) getAvLimited ) / Float.intBitsToFloat( (int) getAvJCS );
        float targetGet = 0.75f;
        msg = limitedCacheDisplayName + " gets took " + ratioGet + " times the " + jcsDisplayName
            + ", the goal is <" + targetGet + "x";
        System.out.println( msg );
        if(ratioGet>targetGet) {
        	throw new Exception(msg);
        }
        ratioGet = Float.intBitsToFloat( (int) getAvLimited ) / Float.intBitsToFloat( (int) getAvEH );
        msg = limitedCacheDisplayName + " gets took " + ratioGet + " times the " + ehCacheDisplayName
                + ", the goal is <" + targetGet + "x" ;
        System.out.println( msg );
        if(ratioGet>targetGet) {
        	throw new Exception(msg);
        }


    }

}
