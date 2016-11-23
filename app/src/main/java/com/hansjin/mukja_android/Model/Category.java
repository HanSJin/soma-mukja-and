package com.hansjin.mukja_android.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kksd0900 on 16. 10. 16..
 */
public class Category {
    public List<String> taste= new ArrayList<>();
    public List<String> country= new ArrayList<>();
    public List<String> cooking= new ArrayList<>();
    public List<String> group = new ArrayList<>();

    public Category(List<String> taste, List<String> country, List<String> cooking){
        this.taste = taste;
        this.country = country;
        this.cooking = cooking;
    }

    public Category(){}
}
