package uav.mosa.module.communication;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import lib.color.StandardPrints;
import lib.uav.module.comm.Communication;
import lib.uav.module.comm.Server;
import lib.uav.reader.ReaderFileConfig;
import lib.uav.struct.constants.Constants;
import lib.uav.struct.constants.TypeInputCommand;
import lib.uav.struct.states.StateCommunication;

/**
 * The class controls communication with GCS.
 * @author Jesimar S. Arantes
 * @since version 3.0.0
 * @see Communication
 * @see Server
 */
public class CommunicationGCS extends Communication implements Server{
    
    private ServerSocket server;
    private OutputStream out;

    private boolean hasReceiveRouteGCS;
    private String routePlannerGCS;
    
    private boolean behaviorChanged;
    private boolean behaviorChangedCircle;
    private boolean behaviorChangedTriangle;
    private boolean behaviorChangedRectangle;
    private final ReaderFileConfig config;

    /**
     * Class contructor.
     * @since version 3.0.0
     */
    public CommunicationGCS() {
        this.stateCommunication = StateCommunication.WAITING;
        this.config = ReaderFileConfig.getInstance();
        this.hasReceiveRouteGCS = false;
        this.behaviorChanged = false;
        this.behaviorChangedCircle = false;
        this.behaviorChangedTriangle = false;
        this.behaviorChangedRectangle = false;
    }

    /**
     * Start the server
     * @since version 4.0.0
     */
    @Override
    public void startServer() {
        StandardPrints.printMsgEmph("MOSA waiting the connection to UAV-GCS ...");
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    server = new ServerSocket(config.getPortNetworkMOSAandGCS());
                    socket = server.accept();//wait the connection
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new PrintWriter(socket.getOutputStream(), true);
                    out = socket.getOutputStream();
                    StandardPrints.printMsgEmph("MOSA connected in UAV-GCS");
                } catch (IOException ex) {
                    StandardPrints.printMsgWarning("Warning [IOException] startServer()");
                    ex.printStackTrace();
                    stateCommunication = StateCommunication.DISABLED;
                }
            }
        });
    }

    /**
     * Treats the data to be received
     * @since version 3.0.0
     */
    @Override
    public void receiveData() {
        stateCommunication = StateCommunication.LISTENING;
        StandardPrints.printMsgEmph("MOSA listening to the connection with UAV-GCS ...");
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (input != null){
                            String answer = input.readLine();
                            if (answer != null) {
                                StandardPrints.printMsgYellow("Data: " + answer);
                                answer = answer.toLowerCase();
                                if (answer.contains(TypeInputCommand.CMD_MISSION)){
                                    hasReceiveRouteGCS = true;
                                    routePlannerGCS = answer;
                                } else if (answer.contains(TypeInputCommand.CMD_CHANGE_BEHAVIOR)){
                                    behaviorChanged = true;
                                } else if (answer.contains(TypeInputCommand.CMD_CHANGE_BEHAVIOR_CIRCLE)){
                                    behaviorChangedCircle = true;
                                } else if (answer.contains(TypeInputCommand.CMD_CHANGE_BEHAVIOR_TRIANGLE)){
                                    behaviorChangedTriangle = true;
                                } else if (answer.contains(TypeInputCommand.CMD_CHANGE_BEHAVIOR_RECTANGLE)){
                                    behaviorChangedRectangle = true;
                                } else if (answer.contains(TypeInputCommand.CMD_GET_PICTURE)){
                                    sendPicture();
                                }
                            }
                        }
                        Thread.sleep(Constants.TIME_TO_SLEEP_BETWEEN_MSG);
                    }
                } catch (InterruptedException ex) {
                    stateCommunication = StateCommunication.DISABLED;
                } catch (IOException ex) {
                    stateCommunication = StateCommunication.DISABLED;
                }
            }
        });
    }
    
    /**
     * Close the communication
     * @since version 3.0.0
     */
    @Override
    public void close() {
        super.close();
        try {
            server.close();
            stateCommunication = StateCommunication.DISABLED;
        } catch (IOException ex) {
            System.err.println("Warning [IOException] close()");
            ex.printStackTrace();
            stateCommunication = StateCommunication.DISABLED;
        } catch (Exception ex) {
            System.err.println("Warning [Exception] close()");
            ex.printStackTrace();
            stateCommunication = StateCommunication.DISABLED;
        }
    }

    public boolean hasReceiveRouteGCS() {
        return hasReceiveRouteGCS;
    }

    public boolean isBehaviorChanged() {
        return behaviorChanged;
    }
    
    public void setBehaviorChanged(boolean behavior) {
        this.behaviorChanged = behavior;
    }
    
    public boolean isBehaviorChangedCircle() {
        return behaviorChangedCircle;
    }
    
    public void setBehaviorChangedCircle(boolean behavior) {
        this.behaviorChangedCircle = behavior;
    }
    
    public boolean isBehaviorChangedTriangle() {
        return behaviorChangedTriangle;
    }
    
    public void setBehaviorChangedTriangle(boolean behavior) {
        this.behaviorChangedTriangle = behavior;
    }
    
    public boolean isBehaviorChangedRectangle() {
        return behaviorChangedRectangle;
    }
    
    public void setBehaviorChangedRectangle(boolean behavior) {
        this.behaviorChangedRectangle = behavior;
    }
    
    public String getRoutePlannerGCS(){
        return routePlannerGCS;
    }
    
    private void sendPicture(){
        try {
            StandardPrints.printMsgEmph("send picture");
            String path = config.getDirCamera();
            BufferedImage image = ImageIO.read(new File(path + "picture/foto.jpg"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageInByte = baos.toByteArray();
            byte[] size = ByteBuffer.allocate(4).putInt(baos.size()).array();            
            out.write(size);
            out.write(imageInByte);
            out.flush();
        } catch (IOException ex) {
            StandardPrints.printMsgWarning("Warning [IOException] sendPicture()");
        }
    }
    
}
