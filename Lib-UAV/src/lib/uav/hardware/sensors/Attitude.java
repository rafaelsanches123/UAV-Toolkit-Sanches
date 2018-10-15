package lib.uav.hardware.sensors;

/**
 * The class models the attitude of the drone (pitch, yaw, roll).
 * @author Jesimar S. Arantes
 * @since version 2.0.0
 */
public class Attitude {
    
    public double pitch;//in radians
    public double yaw;  //in radians
    public double roll; //in radians

    /**
     * Class constructor.
     * @since version 2.0.0
     */
    public Attitude() {
        
    }

    /**
     * Class constructor.
     * @param pitch angle of pitch (in radians) (range -&pi; to &pi;)
     * @param yaw angle of yaw (in radians) (range -&pi; to &pi;)
     * @param roll angle of roll (in radians) (range -&pi; to &pi;)
     * @since version 2.0.0
     */
    public Attitude(double pitch, double yaw, double roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }
    
    /**
     * Converts line in JSON format to pitch, yaw and roll values.
     * @param line FORMAT: {"att": [-0.0018487547, 1.9062933921, -0.001687332987]}
     * @since version 2.0.0
     */
    public void parserInfoAttitude(String line) {
        try{
            line = line.substring(9, line.length() - 2);
            String v[] = line.split(",");        
            this.pitch = Double.parseDouble(v[0]);        
            this.yaw = Double.parseDouble(v[1]);
            this.roll = Double.parseDouble(v[2]);
        }catch (NumberFormatException ex){
            
        }
    } 
    
    /**
     * Update the info the pitch, yaw and roll.
     * @param pitch angle of pitch (in radians) (range -&pi; to &pi;)
     * @param yaw angle of yaw (in radians) (range -&pi; to &pi;)
     * @param roll angle of roll (in radians) (range -&pi; to &pi;)
     * @since version 2.0.0 
     */
    public void updateAttitude(String pitch, String yaw, String roll){
        try{                 
            this.pitch = Double.parseDouble(pitch);        
            this.yaw = Double.parseDouble(yaw);
            this.roll = Double.parseDouble(roll);
        }catch (NumberFormatException ex){
            
        }
    }

    /**
     * Set the pitch value
     * @param pitch the pitch value
     * @since version 2.0.0
     */
    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    /**
     * Set the yaw value
     * @param yaw the yaw value
     * @since version 2.0.0
     */
    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    /**
     * Set the roll value
     * @param roll the roll value
     * @since version 2.0.0
     */
    public void setRoll(double roll) {
        this.roll = roll;
    }
       
    @Override
    public String toString() {
        return "Attitude{" + "pitch=" + pitch + ", yaw=" + yaw + ", roll=" + roll + '}';
    }        
    
}
