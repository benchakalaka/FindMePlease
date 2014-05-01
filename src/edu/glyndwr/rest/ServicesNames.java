package edu.glyndwr.rest;


/**
 * Provides all addresses of services as variable of string type
 * 
 * @author Karpachev Ihor
 * 
 */
public final class ServicesNames {
    // main server full adress
    public static String SERVER_ADRESS = "http://192.168.1.157:8080/";

    // authorization rest adress
    public static final String GET_SERVER_PUBLIC_KEY  = "Tracker/resources/entities.devices.device/getServerRSAPublicKey";

    // check server status rest address
    public static final String DIRECT_SYNCHRONIZATION = "Tracker/resources/entities.devices.device/synchronization";

    // check server status rest address
    public static final String REVERSE_SYNCHRONIZATION= "Tracker/resources/entities.devices.device/getSettings";

    
    // change password rest address
    public static final String CHANGE_PASSWORD = "entities.voter/changePassword";
}
