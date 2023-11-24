package maum.brain.sds.data.vo.engines.spec;

import java.util.ArrayList;
import java.util.List;

public class SdsSpecCustomServer implements SdsSpec{
    private String engine;
    private List<SdsSpecCustomServerMap> answer;


    public void addMap(SdsSpecCustomServerMap sdsSpecCustomServerMap){
        answer.add(sdsSpecCustomServerMap);
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public List<SdsSpecCustomServerMap> getAnswer() {
        return answer;
    }

    public void setAnswer(List<SdsSpecCustomServerMap> answer) {
        this.answer = answer;
    }

    public SdsSpecCustomServer() {
        answer = new ArrayList<>();
    }

    public SdsSpecCustomServer(String name,
        List<SdsSpecCustomServerMap> answer) {
        this.engine = name;
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "SdsSpecCustomServer{" +
            "engine='" + engine + '\'' +
            ", answer=" + answer +
            '}';
    }
}
