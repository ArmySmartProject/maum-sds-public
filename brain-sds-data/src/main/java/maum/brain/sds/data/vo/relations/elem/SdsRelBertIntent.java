package maum.brain.sds.data.vo.relations.elem;

import maum.brain.sds.data.vo.SdsData;

public class SdsRelBertIntent implements SdsData {
    private String no;
    private String bertIntent;
    private int lang;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getBertIntent() {
        return bertIntent;
    }

    public void setBertIntent(String bertIntent) {
        this.bertIntent = bertIntent;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public SdsRelBertIntent(String no, String bertIntent, int lang) {
        this.no = no;
        this.bertIntent = bertIntent;
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "SdsRelBertIntent{" +
                "no=" + no +
                ", intent='" + bertIntent + '\'' +
                ", lang=" + lang +
                '}';
    }
}
