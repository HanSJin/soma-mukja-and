package com.hansjin.mukja_android.TasteAnalyst;

import com.hansjin.mukja_android.Model.Analyst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by kksd0900 on 2016. 11. 18..
 */

public class AnalystManager {

    /*
        do : food + taste + cooking + country + ingredient
        then => List<keyword>
     */
    public static List<String> makeKeywordList(final Analyst analyst) {
        List<String> keywords = new ArrayList();
        for (String str : analyst.food_names)
            keywords.add(str);
        for (String str : analyst.tastes)
            keywords.add(str);
        for (String str : analyst.cookings)
            keywords.add(str);
        for (String str : analyst.countries)
            keywords.add(str);
        for (String str : analyst.ingredients)
            keywords.add(str);
        return keywords;
    }

    /*
        do : number of Contains Keyword from List<keyword>
        then => Map<String, Integer>
     */
    public static Map<String, Integer> makeContainerDictionary(final List<String> keywords) {
        Map<String, Integer> containerDictionary = new HashMap();
        for (String keyword : keywords) {
            if (containerDictionary.containsKey(keyword)) {
                containerDictionary.put(keyword, containerDictionary.get(keyword)+1);
            } else {
                containerDictionary.put(keyword, 0);
            }
        }
        return containerDictionary;
    }

    /*
        do : sort Map
        then => sorted Map<String, Integer>
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
                return ( o2.getValue() ).compareTo( o1.getValue() );
            }
        });
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
