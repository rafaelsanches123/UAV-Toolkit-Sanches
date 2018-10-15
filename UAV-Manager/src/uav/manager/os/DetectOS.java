package uav.manager.os;

import javax.swing.JOptionPane;

/**
 * @author Jesimar S. Arantes
 * @see version 3.0.0
 */
public class DetectOS {
    
    public static ServicesOS detect(){
        String os = System.getProperties().get("os.name").toString().toLowerCase();
        if (os.contains("linux")){
            return new LinuxOS();
        }else if (os.contains("windows")){
            return new WindowsOS();
        }else{
            int op = JOptionPane.showOptionDialog(null, "Try detected OS but found \"" + os + 
                    "\" please select the OS bellow", "OS Detection", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                    null, new Object[]{"linux", "windows"}, "linux");
            if (op == 0){
                return new LinuxOS();
            }else if (op == 1){
                return new WindowsOS();
            }else{
                System.exit(0);
            }
        }
        return null;
    };
    
    public static void main(String[] args) {
        System.out.println("OS: " + detect());
    }
}
