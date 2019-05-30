package huarun.com.enity;

import java.util.List;

public class AssetsUsers {

    private List<AssetsUser> adminusers;
    private List<AssetsUser> systemuser;

    public List<AssetsUser> getAdminusers() {
        return adminusers;
    }

    public void setAdminusers(List<AssetsUser> adminusers) {
        this.adminusers = adminusers;
    }

    public List<AssetsUser> getSystemuser() {
        return systemuser;
    }

    public void setSystemuser(List<AssetsUser> systemuser) {
        this.systemuser = systemuser;
    }
}
