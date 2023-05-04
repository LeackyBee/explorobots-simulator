package io.github.leackybee.main;

public final class Constants {

    public enum VISION{
        SHADOW,
        RAY,
        INVERSE_RAY
    }

    // Defaults, these get replaced when we initialise the map
    public static int MAP_WIDTH = 800;
    public static int MAP_HEIGHT = 600;
    public static String MAP_DIRECTORY = "simulator/src/main/maps/";
    public static String MAP = "leaves.png";
    public static boolean THICK_LINES = true;
    public static boolean VISION_DEBUG = false;
    public static VISION AGENT_VISION_TYPE = VISION.RAY;
    public static boolean PATH_DEBUG = false;

}
