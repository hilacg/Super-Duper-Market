package course.java.sdm.engine;

import java.util.ArrayList;
import java.util.List;

public class StoreOwner {

    private Integer id;
    private String name;
    private List<String> zones = new ArrayList<>();

    public StoreOwner(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getZones() {
        return zones;
    }

    public Integer getId() {
        return id;
    }
}
