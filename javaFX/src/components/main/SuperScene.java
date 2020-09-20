package components.main;

import course.java.sdm.engine.Engine;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;

public class SuperScene  extends Scene {

    private Engine engine;

    public SuperScene(Engine engine){
        super(new FlowPane(), 500, 400);
        this.engine = engine;
    }
}
