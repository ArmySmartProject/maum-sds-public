package first.common.util;

import java.beans.ConstructorProperties;

public class PropInfo {
    // prod, dev, local ..
    public static String  env;

    public static String  itfIP;
    public static int     itfPort;
    public static String  nerIP;
    public static int     nerPort;

    public static String  blIP;
    public static int     blPort1;
    public static int     blPort2;
    public static String  cmIP;
    public static int     cmPort;

    public static String  m2uUrl;

    public static String SimpleBotTTSIP;
    public static String SimpleBotTTSPort;

    public static String siteCustom;

    public static String nqaYN;

    @ConstructorProperties({"env", "itfIP", "itfPort", "nerIP", "nerPort", "blIP", "blPort1", "blPort2", "cmIP", "cmPort", "m2uUrl", "SimpleBotTTSIP", "SimpleBotTTSPort", "siteCustom","nqaYN"})
    public PropInfo(String env, String itfIP, int itfPort, String nerIP, int nerPort,
        String blIP, int blPort1, int blPort2, String cmIP, int cmPort, String m2uUrl, String SimpleBotTTSIP, String SimpleBotTTSPort, String siteCustom,String nqaYN) {

        this.env = env;

        this.itfIP = itfIP;
        this.itfPort = itfPort;
        this.nerIP = nerIP;
        this.nerPort = nerPort;

        this.blIP = blIP;
        this.blPort1 = blPort1;
        this.blPort2 = blPort2;
        this.cmIP = cmIP;
        this.cmPort = cmPort;

        this.m2uUrl = m2uUrl;

        this.SimpleBotTTSIP = SimpleBotTTSIP;
        this.SimpleBotTTSPort = SimpleBotTTSPort;

        this.siteCustom = siteCustom;
        this.nqaYN = nqaYN;

        System.out.println("SimpleBot Execution Environment : " + this.env);
        System.out.println("SimpleBot BertIntent itfIP : " + this.itfIP);
        System.out.println("SimpleBot BertIntent nerIP : " + this.nerIP);
        System.out.println("SimpleBot BL IP : " + this.blIP);
        System.out.println("SimpleBot CM IP : " + this.cmIP);
        System.out.println("SimpleBot M2U rest address : " + this.m2uUrl);
    }


}
