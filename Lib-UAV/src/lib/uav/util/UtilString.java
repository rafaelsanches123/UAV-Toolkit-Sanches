package lib.uav.util;

import lib.uav.struct.geom.Position2D;
import lib.uav.struct.geom.Position3D;

/**
 * Class with util methods on string.
 * @author Jesimar S. Arantes
 * @since version 1.0.0
 */
public class UtilString {
    
    /**
     * Method that change the type of separator.
     * @param line - Any line
     * @return a line with the separator ";" without "\t", "; ", ", ", ",", " ".
     * @since version 1.0.0
     */
    public static String changeValueSeparator(String line){
        line = line.replace("\t", ";");
        line = line.replace("; ", ";");
        line = line.replace(", ", ";");
        line = line.replace(",",  ";");
        line = line.replace(" ",  ";");
        return line;
    }
    
    /**
     * Method that return the type of separator.
     * @param separator - String {"space", "tab", "semicolon", "comma", "barn"}
     * @return Type of separator.
     * @since version 1.0.0
     */
    public static String defineSeparator(String separator){
        switch (separator) {
            case "space":
                return " ";
            case "tab":
                return "\t";
            case "semicolon":
                return ";";
            case "comma":
                return ",";
            case "barn":
                return "\n";
            default:
                return "-1";
        }
    }
    
    /**
     * Method that split the line using the separator and convert in the Position2D.
     * @param line content the line
     * @param separator - Any separator i.e: ";", ",", "\t", "; ", ", ", ...
     * @return the object Position2D.
     * @since version 1.0.0
     */
    public static Position2D split2D(String line, String separator){
        String v[] = line.split(separator);
        return new Position2D(Double.parseDouble(v[0]), Double.parseDouble(v[1]));
    }
    
    /**
     * Method that split the line using the separator and convert in the Position3D.
     * @param line content the line
     * @param separator - String {"space", "tab", "semicolon", "comma", "barn"}
     * @return the object Position3D.
     * @since version 1.0.0
     */
    public static Position3D split3D(String line, String separator){
        String v[] = line.split(separator);
        return new Position3D(Double.parseDouble(v[0]), Double.parseDouble(v[1]), Double.parseDouble(v[2]));
    }
    
}
