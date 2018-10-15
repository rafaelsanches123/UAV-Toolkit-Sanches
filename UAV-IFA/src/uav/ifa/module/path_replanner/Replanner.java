package uav.ifa.module.path_replanner;

import java.io.File;
import java.io.IOException;
import lib.uav.hardware.aircraft.Drone;
import lib.uav.reader.ReaderFileConfig;
import lib.uav.struct.geom.PointGeo;
import lib.uav.util.UtilRunThread;
import uav.ifa.module.security_manager.SecurityManager;

/**
 * Class models mission replanner of drone avoiding obstacles and landing the aircraft.
 * @author Jesimar S. Arantes
 * @since version 1.0.0
 */
public abstract class Replanner {
    
    final ReaderFileConfig config;
    final String dir;
    final Drone drone; 
    final PointGeo pointGeo;

    /**
     * Class constructor
     * @param drone instance of the aircraft
     * @since version 1.0.0
     */
    public Replanner(Drone drone) {
        this.config = ReaderFileConfig.getInstance();
        this.dir = config.getDirReplanner();
        this.drone = drone;
        this.pointGeo = SecurityManager.pointGeo;
    }
    
    /**
     * Execute the replanner
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 1.0.0
     */
    public abstract boolean exec();
    
    /**
     * Updates the configuration file used by the method.
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 1.0.0
     */
    public abstract boolean updateFileConfig();
    
    /**
     * Converts the route in Cartesian coordinates to geographic coordinates
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 1.0.0
     */
    public abstract boolean parseRoute3DtoGeo();
    
    /**
     * Clears log files generated by method
     * @since version 1.0.0
     */
    public abstract void clearLogs();
    
    /**
     * Method that runs the path replanner.
     * @return {@code true} if the execution was successful
     *         {@code false} otherwise
     * @since version 1.0.0
     */
    boolean execMethod() {
        try{
            boolean isPrint = false;
            boolean isPrintError = false;
            String cmd = config.getCmdExecReplanner();
            UtilRunThread.dualSingleThreadWaitFor(cmd, new File(dir), isPrint, isPrintError);            
            return true;
        } catch (IOException ex) {
            System.err.println("Error [IOException] execMethod()");
            ex.printStackTrace();
            return false;
        } catch (InterruptedException ex) {
            System.err.println("Error [InterruptedException] execMethod()");
            ex.printStackTrace();
            return false;
        }
    }   
}