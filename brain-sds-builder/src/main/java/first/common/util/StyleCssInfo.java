package first.common.util;

import org.springframework.core.io.ClassPathResource;

import java.beans.ConstructorProperties;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StyleCssInfo {
    public static String  resourcesPath;
    public static String[] defaultContents;
    public static String[] defaultContentsCss;
    public static String logoiconMapping;
    public static String supplier;


    @ConstructorProperties({"resourcesPath", "defaultStyle", "supplier", "logoiconMappingval"})
    public StyleCssInfo(String resourcesPath, String defaultStyle, String supplier, String logoiconMappingval){
        this.resourcesPath = resourcesPath;
        System.out.println("SimpleBot Style Resources Path : " + this.resourcesPath);
        defaultContents = (defaultStyle+"/IconImg/logoImg").split("/");
        defaultContentsCss = defaultStyle.split("/");
        logoiconMapping = logoiconMappingval;

        String path = this.resourcesPath + "/Default"; //폴더 경로
        StyleCssInfo.deleteFolder(path);
        File Folder = new File(path);
        if (!Folder.exists()) {
            try{
                Folder.mkdir();
                for(int i = 0; i<defaultContents.length; i++){
                    System.out.println("Default Resources Make : " + defaultContents[i]);
                    ClassPathResource resource = new ClassPathResource("defaultStyle/" + defaultContents[i]);
                    Path filePath = Paths.get(resource.getURI());
                    byte[] content = Files.readAllBytes(filePath);
                    File lOutFile = new File(resourcesPath + "/Default/" + defaultContents[i]);
                    FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
                    lFileOutputStream.write(content);
                    lFileOutputStream.close();
                }
                String[] suppliedList = supplier.split("/");
                for(int i = 0; i<suppliedList.length; i++){
                    System.out.println("Default Resources Make : " + suppliedList[i]);
                    ClassPathResource resource = new ClassPathResource("defaultStyle/supplier_" + suppliedList[i] + ".css");
                    Path filePath = Paths.get(resource.getURI());
                    byte[] content = Files.readAllBytes(filePath);
                    File lOutFile = new File(resourcesPath + "/Default/supplier_" + suppliedList[i] + ".css");
                    FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
                    lFileOutputStream.write(content);
                    lFileOutputStream.close();
                }
                System.out.println("Default Resource constructed.");
            }
            catch(Exception e){
                e.getStackTrace();
                System.out.println("StyleCssInfo Error :: " + e);
            }
        }
    }


    public static void deleteFolder(String path) {
        File folder = new File(path);
        try {
            if(folder.exists()){
                File[] folder_list = folder.listFiles();

                for (int i = 0; i < folder_list.length; i++) {
                    if(folder_list[i].isFile()) {
                        folder_list[i].delete();
                    }else {
                        deleteFolder(folder_list[i].getPath());
                    }
                    folder_list[i].delete();
                }
                folder.delete();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }


    public static void remakeHostResource(int host, String mainTextRGB, String mainColorRGB, String logoSize, String posX, String posY, String supplier) throws IOException {
        String path = resourcesPath + "/" + host; //폴더 경로
        File Folder = new File(path);
        if (!Folder.exists()) {
            Folder.mkdir();
        }
        for(int i = 0; i<defaultContentsCss.length; i++){
            File file = new File(path+"/"+defaultContentsCss[i]);
            if( file.exists()){ if(file.delete()){ System.out.println("파일삭제 성공");}}
            List<String> nowCSSText = new ArrayList<>();
            FileReader rw = null;
            BufferedReader br = null;
            try{
                rw = new FileReader(resourcesPath + "/Default/" + defaultContentsCss[i]);
                br = new BufferedReader( rw );
                String readLine = null ;
                while( ( readLine =  br.readLine()) != null ){
                    nowCSSText.add(readLine);
                }
            }catch ( IOException e ) {
                System.out.println(e);
                continue;
            }finally {
                if (rw != null) {
                    try {
                        rw.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            File newAddFile = new File(path+"/"+defaultContentsCss[i]+".add.css");
            if(newAddFile.exists()){
                try{
                    rw = new FileReader(path+"/"+defaultContentsCss[i]+".add.css");
                    br = new BufferedReader( rw );
                    String readLine = null ;
                    while( ( readLine =  br.readLine()) != null ){
                        nowCSSText.add(readLine);
                    }
                }catch ( IOException e ) {
                    System.out.println(e);
                    continue;
                }finally {
                    if (rw != null) {
                        try {
                            rw.close();
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (br != null) {
                        try {
                            br.close();
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if(defaultContentsCss[i].equals("chatbot_floating.css")){
                String nowSupplier = "";
                try{
                    if(supplier.equals("") || supplier==null){
                        nowSupplier = "default";
                    }else{
                        nowSupplier = supplier;
                    }
                }catch (Exception e){
                    nowSupplier = "default";
                }

                File newSupplierCss = new File(resourcesPath + "/Default/supplier_" + nowSupplier+".css");
                if(newSupplierCss.exists()){
                    try{
                        rw = new FileReader(resourcesPath + "/Default/supplier_" + nowSupplier+".css");
                        br = new BufferedReader( rw );
                        String readLine = null ;
                        while( ( readLine =  br.readLine()) != null ){
                            nowCSSText.add(readLine);
                        }
                    }catch ( IOException e ) {
                        System.out.println(e);
                        continue;
                    }finally {
                        if (rw != null) {
                            try {
                                rw.close();
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (br != null) {
                            try {
                                br.close();
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            for(int j= 0; j<nowCSSText.size(); j++){
                nowCSSText.set(j,nowCSSText.get(j).replace("--main-text-rgb",mainTextRGB));
                nowCSSText.set(j,nowCSSText.get(j).replace("--main-color-rgb",mainColorRGB));
                nowCSSText.set(j,nowCSSText.get(j).replace("--main-icon",logoiconMapping + "IconImg?host=" + host));
                nowCSSText.set(j,nowCSSText.get(j).replace("--main-logo",logoiconMapping + "logoImg?host=" + host));
                nowCSSText.set(j,nowCSSText.get(j).replace("--main-couselor-icon",logoiconMapping + "counselorImg?host=" + host));
                nowCSSText.set(j,nowCSSText.get(j).replace("--logo-size-",logoSize));
                if(posX.equals("-1")){nowCSSText.set(j,nowCSSText.get(j).replace("--logo-left", "50%"));}
                else{nowCSSText.set(j,nowCSSText.get(j).replace("--logo-left",posX + "px"));}
                if(posY.equals("-1")){nowCSSText.set(j,nowCSSText.get(j).replace("--logo-top","50%"));}
                else{nowCSSText.set(j,nowCSSText.get(j).replace("--logo-top",posY + "px"));}
            }
            FileWriter fw = null;
            BufferedWriter bw = null;
            try
            {
                fw = new FileWriter( path+"/"+defaultContentsCss[i] ,true);
                bw = new BufferedWriter( fw );
                for(int j=0; j<nowCSSText.size(); j++){
                    bw.write(nowCSSText.get(j));
                    bw.newLine();
                }
                bw.flush(); //버퍼의 내용을 파일에 쓰기
            }catch ( IOException e ) {
                System.out.println(e);
            }finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bw != null) {
                    try {
                        bw.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }


        //chatbot_custom과 chatbot_floating을 합친 custom_merged.css 만들기
        BufferedReader br1 = null;
        BufferedReader br2 = null;
        BufferedWriter bw = null;
        try{
            br1 = new BufferedReader(new FileReader(path+"/chatbot_custom.css"));
            br2 = new BufferedReader(new FileReader(path+"/chatbot_floating.css"));
            bw = new BufferedWriter(new FileWriter(path+"/custom_merged.css"));
            String c;
            while((c=br1.readLine())!=null) bw.write(c + "\n");
            while((c=br2.readLine())!=null) bw.write(c + "\n");
            bw.flush();
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            if (br1 != null) {
                try {
                    br1.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            if (br2 != null) {
                try {
                    br2.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
