package uav.mosa.module.path_planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import lib.color.StandardPrints;
import lib.uav.hardware.aircraft.Drone;
import lib.uav.hardware.aircraft.DroneFixedWing;
import lib.uav.struct.Waypoint;
import lib.uav.struct.geom.Position3D;
import lib.uav.struct.mission.Mission3D;
import lib.uav.util.UtilGeo;
import lib.uav.util.UtilIO;

/**
 * The class models the path planner HGA4m. 
 * @author Jesimar S. Arantes
 * @since version 1.0.0
 * @see Planner
 */
public class HGA4m extends Planner{
    
    /**
     * Class constructor
     * @param drone instance of the aircraft
     * @param waypointsMission waypoints of the mission
     * @since version 1.0.0
     */
    public HGA4m(Drone drone, Mission3D waypointsMission) {
        super(drone, waypointsMission);
    }   
    
    /**
     * Execute the mission
     * @param i the i-th index of the route
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 1.0.0
     */
    public boolean execMission(int i) {
        boolean itIsOkUpdate = updateFileConfig(i);
        boolean itIsOkpathAB = definePathAB(i);        
        boolean itIsOkExec   = execMethod();
        boolean itIsOkRoute  = createFileFinalRoute(i);
        boolean itIsOkParse  = parseRoute3DtoGeo(i);
        return itIsOkUpdate && itIsOkpathAB && itIsOkExec && itIsOkRoute && itIsOkParse;
    }
    
    /**
     * Updates the configuration file used by the method.
     * @param i the i-th index of the route
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 1.0.0
     */
    public boolean updateFileConfig(int i) {
        try {
            double px1 = waypointsMission.getPosition(i).getX();
            double py1 = waypointsMission.getPosition(i).getY();
            double px2 = waypointsMission.getPosition(i+1).getX();
            double py2 = waypointsMission.getPosition(i+1).getY();
            double dx = px2 - px1;
            double dy = py2 - py1;
            //distancia entre os pontos com uma margem de seguranca
            double dist = Math.sqrt(dx*dx+dy*dy)*2;
            
            File src_ga = new File(dir + "ga-config-base");
            File dst_ga = new File(dir + "ga-config");
            String time = config.getTimeExecPlannerHGA4m(i);
            String timeH = String.format("%d", (int)(dist));
            //usando metade dos waypoints DeltaT=2
            String qtdWpt = String.format("%d", (int)(dist/2));
            String delta = config.getDeltaPlannerHGA4m();
            String maxVel = config.getMaxVelocityPlannerHGA4m();
            String maxCtrl = config.getMaxControlPlannerHGA4m();
            UtilIO.copyFileModifiedMOSA(src_ga, dst_ga, time, 207, delta, 304,
                    qtdWpt, 425, timeH, 426, maxVel, 427, maxCtrl, 428);
            return true;
        } catch (FileNotFoundException ex) {
            StandardPrints.printMsgWarning("Warning [FileNotFoundException]: updateFileConfig()");
            return false;
        }
    }
    
    private double vx1 = 0.0;
    private double vy1 = 0.0;
    
    /**
     * Define the path between two points (i.e. A and B).
     * @param i the i-th index of the route
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 1.0.0
     */
    private boolean definePathAB(int i) {
        try {
            double px1 = waypointsMission.getPosition(i).getX();
            double py1 = waypointsMission.getPosition(i).getY();
            double px2 = waypointsMission.getPosition(i+1).getX();
            double py2 = waypointsMission.getPosition(i+1).getY();
            double vx2 = 0;
            double vy2 = 0;
            if (i < waypointsMission.size() - 2){
                double px3 = waypointsMission.getPosition(i+2).getX();
                double py3 = waypointsMission.getPosition(i+2).getY();
                double dx = px3 - px1;
                double dy = py3 - py1;
                double norm = Math.sqrt(dx*dx+dy*dy);
                double vc = drone.getAttributes().getSpeedCruize();
                vx2 = dx * vc/norm;
                vy2 = dy * vc/norm;
            }   
            
            PrintStream print = new PrintStream(new File(dir + "mission-config.sgl"));
            print.println("----------- start state (px py vx vy) -----------");
            if (drone instanceof DroneFixedWing){
                print.println(px1 + "," + py1 + "," + vx1 + "," + vy1);
            } else {
                print.println(px1 + "," + py1 + ",0.0,0.0");
            }
            print.println("--------------- end point (px py)---------------");
            if (drone instanceof DroneFixedWing){
                print.println(px2 + "," + py2 + "," + vx2 + "," + vy2);
            } else {
                print.println(px2 + "," + py2 + ",0.0,0.0");
            }
            print.println();
            print.println("<TrueName>");
            print.println("Config2D-2.sgl");
            print.close();
            vx1 = vx2;
            vy1 = vy2;
            return true;
        } catch (FileNotFoundException ex) {
            StandardPrints.printMsgWarning("Warning [FileNotFoundException]: definePathAB()");
            return false;
        } 
    }  
    
    /**
     * Creates a final route file
     * @param i the i-th index of the route
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 1.0.0
     */
    private boolean createFileFinalRoute(int i) {
        try {
            File src = new File(dir + "output-simulation.log");
            File dst = new File(dir + "out-sim"+i+".log");
            UtilIO.copyFile(src, dst);
            File route = new File(dir + "route3D"+i+".txt");
            
            Scanner sc = new Scanner(dst);
            PrintStream print = new PrintStream(route);
            boolean test1 = false;
            boolean test2 = false;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!test1 && !test2){
                    if (line.equals("------------------------ [ states ] ------------------------")){
                        test1 = true;
                    }
                }else if (test1 && !test2){
                    if (!line.equals("------------------------ [ controls ] ------------------------")){
                        print.println(line);
                    }else{
                        test2 = true;
                    }
                }
            }
            sc.close();
            print.close();
            return true;
        } catch (IOException ex) {
            StandardPrints.printMsgWarning("Warning [IOException]: createFileFinalRoute()");
            return false;
        }
    }
    
    /**
     * Converts the route in Cartesian coordinates to geographic coordinates 
     * @param i the i-th index of the route
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 1.0.0
     */
    public boolean parseRoute3DtoGeo(int i){
        try {
            String nameFileRoute3D =  "route3D"  + i + ".txt";
            String nameFileRouteGeo = "routeGeo" + i + ".txt";
            File fileRouteGeo = new File(dir + nameFileRouteGeo);
            PrintStream printGeo = new PrintStream(fileRouteGeo);
            Scanner readRoute3D = new Scanner(new File(dir + nameFileRoute3D));
            int countLines = 0;
            double h = config.getAltRelMission(); 
            while(readRoute3D.hasNext()){
                double x = readRoute3D.nextDouble();
                double y = readRoute3D.nextDouble();
                readRoute3D.nextDouble();
                readRoute3D.nextDouble();
                printGeo.println(UtilGeo.parseToGeo(pointGeo, x, y, h, ";"));
                mission3D.addPosition(new Position3D(x, y, h));
                missionGeo.addWaypoint(new Waypoint(UtilGeo.parseToGeo1(pointGeo, x, y, h)));
                countLines++;
            }
            if (countLines == 0){
                StandardPrints.printMsgWarning("Route-Empty");
                if (!drone.getSensors().getStatusUAV().armed){
                    System.exit(1);
                }
            }
            readRoute3D.close();
            printGeo.close();
            return true;
        } catch (FileNotFoundException ex) {
            StandardPrints.printMsgWarning("Warning [FileNotFoundException] parseRoute3DtoGeo()");
            return false;
        } 
    }
    
    /**
     * Clears log files generated by method
     * @since version 1.0.0
     */
    @Override
    public void clearLogs() {
        UtilIO.deleteFile(new File(dir), ".log");
        UtilIO.deleteFile(new File(dir), ".png");
        new File(dir + "log_error.txt").delete(); 
    }

}
