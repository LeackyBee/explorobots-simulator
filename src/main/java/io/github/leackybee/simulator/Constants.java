package io.github.leackybee.simulator;

public final class Constants {

    public enum VISION{
        SHADOW,
        RAY,
        INVERSE_RAY
    }

    public enum FRONTIER_DETECTION{
        NAIVE,
        WFD,
        WFD_IP,
        FFD,
    }

    // Defaults, these get replaced when we initialise the map
    public static int MAP_WIDTH;
    public static int MAP_HEIGHT;

    // Directories
    public static String MAP_DIRECTORY = "src/main/resources/maps/";
    public static String CANVAS_DIRECTORY = "src/main/resources/META-INF/resources/images/canvas/";

    // Debug constants
    public static boolean VISION_DEBUG = false;
    public static boolean PATH_DEBUG = false;
    public static boolean DRAW_FRONTIERS = false;

    // Simulation Configuration
    public static int NUM_AGENTS = 1;
    public static String MAP = "leaves.png";

    // Algorithm Choices
    public static FRONTIER_DETECTION FRONTIER_DETECTION_ALGORITHM = FRONTIER_DETECTION.NAIVE;
    public static VISION AGENT_VISION_TYPE = VISION.SHADOW;
    public static boolean THICK_LINES = true;



}
