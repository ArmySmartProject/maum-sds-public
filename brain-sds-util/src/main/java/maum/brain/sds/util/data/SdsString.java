package maum.brain.sds.util.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SdsString {
    public static String splitIdx(String sentence, String idxString){
        if(idxString=="none") return "";
        try{
            Integer.parseInt(idxString.split(",")[0]);
            Integer.parseInt(idxString.split(",")[1]);
        }catch (Exception e){
            System.out.println("SdsString Error by checkSplit ("  + e + ") : invalid idxString :: "
                    + idxString + " ::: Make it default (0,-1)");
            return sentence;
        }

        boolean finalSpliter        =   true;
        if(Pattern.matches("^[?!.]*$", Character.toString(sentence.charAt(sentence.length()-1)))==false){
            sentence += "њ";
            finalSpliter = false;
        }
        Pattern pattern             =   Pattern.compile("[?!.њ]");
        Matcher matcher             =   pattern.matcher(sentence);
        List<String> stringList     =   new ArrayList<>();
        List<Integer> startIdxList  =   new ArrayList<>();
        List<Integer> endIdxList    =   new ArrayList<>();

        int prevIdx = 0;
        while(matcher.find()) {
            stringList.add(sentence.substring(prevIdx,matcher.end()));
            prevIdx = matcher.end();
        }
        try{
            String[] idxSplit = idxString.split(",");
            for(int idx = 0; idx<idxSplit.length; idx = idx + 2){
                startIdxList.add(Integer.parseInt(idxSplit[idx]));
                endIdxList.add(Integer.parseInt(idxSplit[idx+1]));
                if(startIdxList.get(idx/2)<0) startIdxList.set((idx/2),stringList.size() + startIdxList.get(idx/2));
                if(endIdxList.get((idx/2))<0) endIdxList.set(((idx/2)),stringList.size() + endIdxList.get((idx/2)));
                if(endIdxList.get((idx/2))<startIdxList.get((idx/2))){
                    throw new Exception("SdsString Error");
                }
            }
        }catch (Exception e){
            System.out.println("SdsString Error ("  + e + ") : invalid idxString :: "
                    + idxString + " ::: Make it default (0,-1)");
            startIdxList  =   new ArrayList<>();
            endIdxList    =   new ArrayList<>();
            startIdxList.add(0);
            endIdxList.add(stringList.size()-1);
        }
        String retString = "";
        for(int idx = 0; idx<startIdxList.size(); idx++){
            for(int i = startIdxList.get(idx); i<=endIdxList.get(idx); i++){
                retString += stringList.get(i);
            }
        }if(startIdxList.size()==0) retString = sentence;
        String retStringAfter = retString.trim();
        if(!finalSpliter) retStringAfter = retStringAfter.replace("њ","");
        return retStringAfter;
    }
}