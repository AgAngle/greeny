package huarun.com.enity;

import java.util.List;

public class DataModle {
    private List<String> userNames;
    private List<String> hostNames;
    private List<String> systemUsers;
    private List<String> ips;

    public List<String> getSystemUsers() {
        return systemUsers;
    }

    public void setSystemUsers(List<String> systemUsers) {
        this.systemUsers = systemUsers;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public List<String> getHostNames() {
        return hostNames;
    }

    public void setHostNames(List<String> hostNames) {
        this.hostNames = hostNames;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }
}
