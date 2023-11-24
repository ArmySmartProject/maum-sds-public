package maum.brain.sds.logdb.components;


public class SessionLogMaxIDChecker {
  public static int nowMaxID = 0;
  public static void resetMaxID(){
    try{
      LoggerDaoBeta loggerDao = new LoggerDaoBeta();
      nowMaxID = loggerDao.sessionMaxIDCheck();
    }catch (Exception e){
      nowMaxID = 1;
    }
    System.out.println("SessionLogMaxIDChecker :: nowMaxID : " + nowMaxID);
  }
  public static int getSessionLogID(){
    nowMaxID += 1;
    return nowMaxID;
  }
}
