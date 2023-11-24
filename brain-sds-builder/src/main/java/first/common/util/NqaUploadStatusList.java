package first.common.util;

import first.builder.service.BuilderService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@NoArgsConstructor
public class NqaUploadStatusList {
    @Resource(name = "builderService")
    private BuilderService builderService;

    // status -> uploading(진행중), success(업로드 성공), fail(업로드 실패), preparation(준비상태)
    public static List<Map<String, Object>> nqaStatusList;

    @Bean
    public List setNqaStatusList(){
        nqaStatusList = builderService.getNqaStatusChatbotList();
        return nqaStatusList;
    }

    public static void nqaUploadStatusUpdate(Map<String, Object> status){
        for(int i=0; i<nqaStatusList.size(); i++){
            if(nqaStatusList.get(i).get("host").toString().equals(status.get("host").toString())){
                nqaStatusList.get(i).replace("nqaUploadStatus", status.get("nqaUploadStatus"));
                break;
            }
        }
    }

    public static void addNqaUploadStatus(Map<String, Object> status){
        if(status.get("host") != null && status.get("nqaUploadStatus") != null){
            nqaStatusList.add(status);
        }
    }

    public static Map<String, Object> getNqaStaus(Map<String, Object> status){
        Map<String, Object> map = null;
        if(status.get("host") != null){
            for(Map tmpMap : nqaStatusList){
                if(tmpMap.get("host").toString().equals(status.get("host").toString())){
                    map = tmpMap;
                    break;
                }
            }
        }
        return map;
    }

}
