package huarun.com.enity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ServerInfoModle {
    private String hostName;
    private String ip;
    private String systemType;

    private Set<String> nodeIds;


    public ServerInfoModle() {
        nodeIds = new TreeSet<String>();
    }

    public Set<String> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(Set<String> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }
}
