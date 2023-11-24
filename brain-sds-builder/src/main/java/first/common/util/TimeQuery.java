package first.common.util;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeQuery {
    public static String queryString = "";
    public static String getQueryString(){
        if(queryString==""){
            try{
                Date nowDate = new Date();
                DateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                queryString = format.format(nowDate);
            }catch (Exception e){
                System.out.println("queryString Error");
                System.out.println(e);
            }
        }
        return queryString;
    }
}
