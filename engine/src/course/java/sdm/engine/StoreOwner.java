package course.java.sdm.engine;

public class StoreOwner {

    private Integer id;
    private String name;

    public StoreOwner(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
