package maum.brain.sds.data.vo.relations.elem;

import maum.brain.sds.data.vo.SdsData;

public class SdsRelIntent implements SdsData {
    private String no;
    private String intent;
    private int lang;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public SdsRelIntent(String intent, int lang) {
        this.intent = intent;
        this.lang = lang;
    }

    public SdsRelIntent(String no, String intent, int lang) {
        this.no = no;
        this.intent = intent;
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "SdsIntent{" +
                "no=" + no +
                ", intent='" + intent + '\'' +
                ", lang=" + lang +
                '}';
    }
}
