package maum.brain.sds.data.vo.engines.spec;

import maum.brain.sds.data.vo.SdsData;

public class SdsSpecRegex implements SdsSpec{
    private boolean match;
    private String regex;
    private String intent;

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    @Override
    public String toString() {
        return "SdsSpecRegex{" +
                "match=" + match +
                ", regex='" + regex + '\'' +
                ", intent='" + intent + '\'' +
                '}';
    }

    public SdsSpecRegex(boolean match, String regex, String intent) {
        this.match = match;
        this.regex = regex;
        this.intent = intent;
    }

    public SdsSpecRegex() {
    }
}
