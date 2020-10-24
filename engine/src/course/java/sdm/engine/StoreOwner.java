package course.java.sdm.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreOwner {

    private Integer id;
    private String name;
    private Map<String, Zone> zones = new HashMap<>();
    private Account account = new Account();

    public StoreOwner(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public  Map<String, Zone> getZones() {
        return zones;
    }

    public Integer getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }
}
