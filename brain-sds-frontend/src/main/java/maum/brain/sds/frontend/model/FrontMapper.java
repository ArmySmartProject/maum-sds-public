package maum.brain.sds.frontend.model;


import java.util.Map;

public interface FrontMapper {
    public String selectHostName(String host);
    public String selectHostCode(String host);
    public Map selectHostInfo(String host);
    public Map selectSupplier(String hostNo);
    public int addLinkLog(Map<String, Object> param);
}
