package uav.ifa.module.path_replanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import lib.color.StandardPrints;
import lib.uav.hardware.aircraft.Drone;
import lib.uav.struct.constants.TypeAltitudeDecay;
import lib.uav.util.UtilGeo;
import lib.uav.util.UtilIO;

/**
 * Class that runs the path replanner GA-GH-4s.
 * @author Jesimar S. Arantes
 * @since version 2.0.0
 * @see Replanner
 */
public class GA_GH_4s extends Replanner{
    
    private final String dirGA = "../Modules-IFA/GA4s/";
    private final String dirGH = "../Modules-IFA/GH4s/";
    
    /**
     * Class constructor
     * @param drone instance of the aircraft
     * @since version 2.0.0
     */
    public GA_GH_4s(Drone drone) {
        super(drone);
    }

    /**
     * Execute the replanner
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 2.0.0
     */
    @Override
    public boolean exec() {
        boolean itIsOkUpdate = updateFileConfig();
        boolean itIsOkExec   = execMethod();
        boolean itIsOkParse  = parseRoute3DtoGeo();
        return itIsOkUpdate && itIsOkExec && itIsOkParse;
    }
    
    /**
     * Updates the configuration file used by the method.
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 2.0.0
     */
    @Override
    public boolean updateFileConfig() { 
        try {            
            double px = UtilGeo.convertGeoToX(pointGeo, drone.getSensors().getGPS().lng);
            double py = UtilGeo.convertGeoToY(pointGeo, drone.getSensors().getGPS().lat);
            double vel = 1.5;//drone.getSensorUAV().groundspeed;
            int head = (int)drone.getSensors().getSensorUAV().heading;
            int heading = UtilGeo.convertAngleAviationToAngleMath(head);
            double angle = Math.toRadians(heading);//Math.atan2(vy, vx);          
            //double vx = drone.getVelocity().vx;
            //double vy = drone.getVelocity().vy;            
            //double dt = 2;
            //px = px + vx * dt;//esse tipo de projecao nao fica bom
            //py = py + vy * dt;//esse tipo de projecao nao fica bom
            
            File srcGA = new File(dirGA + "config-base.sgl");
            File dstGA = new File(dirGA + "config.sgl");
            String state = px + " " + py + " " + vel + " " + angle;
            String qtdWpt = config.getNumberWaypointsReplanner();
            String delta = config.getDeltaReplanner();
            UtilIO.copyFileModifiedIFA(srcGA, dstGA, state, 8, qtdWpt, 20, delta, 26);
            
            File src_ga = new File(dirGA + "instance-base");
            File dst_ga = new File(dirGA + "instance");
            String time = config.getTimeExecReplanner();
            UtilIO.copyFileModifiedIFA(src_ga, dst_ga, time, 117);
            
            File srcGH = new File(dirGH + "config-base.sgl");
            File dstGH = new File(dirGH + "config.sgl");
            UtilIO.copyFileModifiedIFA(srcGH, dstGH, state, 8, qtdWpt, 20, delta, 26);
            
            return true;
        } catch (FileNotFoundException ex) {
            StandardPrints.printMsgWarning("Warning [FileNotFoundException]: updateFileConfig()");
            return false;
        }
    }   

    /**
     * Converts the route in Cartesian coordinates to geographic coordinates
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 1.0.0
     */
    @Override
    public boolean parseRoute3DtoGeo() {
        try {
            String nameFileRoute3D =  "route.txt";
            String nameFileRouteGeo = "routeGeo.txt";                   
            File fileRouteGeoGA = new File(dirGA + nameFileRouteGeo);
            PrintStream printGeoGA = new PrintStream(fileRouteGeoGA);        
            Scanner readRoute3DGA = new Scanner(new File(dirGA + nameFileRoute3D));
            double h = drone.getSensors().getBarometer().alt_rel;
            int qtdWpt = UtilIO.getLineNumber(new File(dirGA + nameFileRoute3D));
            double frac = h/qtdWpt;
            int countLines = 0;
            while(readRoute3DGA.hasNext()){                        
                double x = readRoute3DGA.nextDouble();
                double y = readRoute3DGA.nextDouble();           
                if (config.getTypeAltitudeDecayReplanner().equals(TypeAltitudeDecay.LINEAR)){
                    h = h - frac;
                }
                printGeoGA.println(UtilGeo.parseToGeo(pointGeo, x, y, h, ";"));   
                countLines++;
            }
            if (countLines == 0){
                StandardPrints.printMsgWarning("Route-Empty GA");
            }
            readRoute3DGA.close();
            printGeoGA.close();
                    
            File fileRouteGeoGH = new File(dirGH + nameFileRouteGeo);
            PrintStream printGeoGH = new PrintStream(fileRouteGeoGH);        
            Scanner readRoute3DGH = new Scanner(new File(dirGH + nameFileRoute3D));
            qtdWpt = UtilIO.getLineNumber(new File(dirGH + nameFileRoute3D));
            frac = h/qtdWpt;
            countLines = 0;
            while(readRoute3DGH.hasNext()){                        
                double x = readRoute3DGH.nextDouble();
                double y = readRoute3DGH.nextDouble();           
                if (config.getTypeAltitudeDecayReplanner().equals(TypeAltitudeDecay.LINEAR)){
                    h = h - frac;
                }
                printGeoGH.println(UtilGeo.parseToGeo(pointGeo, x, y, h, ";"));   
                countLines++;
            }
            if (countLines == 0){
                StandardPrints.printMsgWarning("Route-Empty GH");
            }
            readRoute3DGH.close();
            printGeoGH.close();
            
            return true;
        } catch (FileNotFoundException ex) {
            StandardPrints.printMsgWarning("Warning [FileNotFoundException] parseRoute3DtoGeo()");
            return false;
        } catch (IOException ex) {
            StandardPrints.printMsgWarning("Warning [IOException] parseRoute3DtoGeo()");
            return false;
        }
    }
    
    /**
     * Clears log files generated by method
     * @since version 1.0.0
     */
    @Override
    public void clearLogs() {
        UtilIO.deleteFile(new File(dirGA), ".log");
        UtilIO.deleteFile(new File(dirGA), ".png");
        new File(dirGA + "route.txt").delete();
        new File(dirGA + "routeGeo.txt").delete();
        
        UtilIO.deleteFile(new File(dirGH), ".log");
        UtilIO.deleteFile(new File(dirGH), ".png");
        new File(dirGH + "route.txt").delete();
        new File(dirGH + "routeGeo.txt").delete();
    }    
    
    public String bestMethod() {
        try {
            String nameFileOutput = "output-simulation.log";
            Scanner readOutput = new Scanner(new File(dir + nameFileOutput));
            while(readOutput.hasNext()){                        
                String line = readOutput.nextLine();
                if (line.contains("Method: ")){
                    String v[] = line.split(" ");
                    return v[1];
                }
            }
            readOutput.close();
            return "null";
        } catch (FileNotFoundException ex) {
            StandardPrints.printMsgWarning("Warning [FileNotFoundException] bestMethod()");
            return "null";
        }
    }
        
}
