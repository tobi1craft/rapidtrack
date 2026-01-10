package de.tobi1craft.rapidtrack.enums;

public enum Blocks {
    ROAD_STRAIGHT("road/straight"),
    ROAD_START("road/straight", Props.START),
    ROAD_FINISH("road/straight", Props.FINISH),
    ROAD_TURN("road/turn");

    private final String path;
    private float friction = 0.5f;
    private int specialProperty = 0;

    Blocks(String path) {
        this.path = path;
    }

    Blocks(String path, float friction) {
        this.path = path;
        this.friction = friction;
    }

    Blocks(String path, int specialProperty) {
        this.path = path;
        this.specialProperty = specialProperty;
    }

    Blocks(String path, float friction, int specialProperty) {
        this.path = path;
        this.friction = friction;
        this.specialProperty = specialProperty;
    }

    public String getPath() {
        return path;
    }

    public float getFriction() {
        return friction;
    }

    public int getSpecialProperty() {
        return specialProperty;
    }

    //! Interface, weil direkte Values nicht funktionieren
    public interface Props {
        int NONE = 0;
        int START = 1;
        int FINISH = 2;
        int NO_COLLISION = 3;
    }
}
