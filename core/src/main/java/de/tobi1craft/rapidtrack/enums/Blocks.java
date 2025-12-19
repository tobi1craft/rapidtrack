package de.tobi1craft.rapidtrack.enums;

public enum Blocks {
    ROAD_STRAIGHT("road/straight");

    private final String path;

    Blocks(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
