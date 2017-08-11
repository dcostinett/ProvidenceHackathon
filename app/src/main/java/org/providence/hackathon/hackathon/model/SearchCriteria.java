package org.providence.hackathon.hackathon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

/**
 *
 */

public class SearchCriteria {
    public int size = 20;
    public List<Sort> sort = new ArrayList<>();

    public SearchCriteria() {
        sort.add(new Sort());
    }
}
