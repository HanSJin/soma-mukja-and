package com.hansjin.mukja_android.Model;

import java.util.List;

/**
 * Created by kksd0900 on 16. 10. 16..
 */
public class Category {
    public List<String> taste;
    public List<String> country;
    public List<String> cooking;

    public Category(List<String> taste, List<String> country, List<String> cooking){
        this.taste = taste;
        this.country = country;
        this.cooking = cooking;
    }
    public Category() {
    }
}
