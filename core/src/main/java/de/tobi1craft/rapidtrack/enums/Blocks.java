package de.tobi1craft.rapidtrack.enums;

public enum Blocks {
    ROAD_STRAIGHT("road/straight"),
    ROAD_CURVE("road/curve"),
    ROAD_T("road/t"),
    ROAD_CROSS("road/cross");


    private final String path;

    Blocks(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
