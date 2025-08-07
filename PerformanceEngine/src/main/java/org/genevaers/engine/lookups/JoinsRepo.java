package org.genevaers.engine.lookups;

import java.util.HashMap;
import java.util.Map;

public class JoinsRepo {
    private static Map<Integer, Join> joins = new HashMap<>();

    public static void addJoin(Join jn) {
        joins.computeIfAbsent(jn.getJoinId(), j -> addNewJoin(jn));
    }

    private static Join addNewJoin(Join j) {
        return j;
    }

    public static Join getJoin(int id) {
        return joins.get(id);
    }
}
