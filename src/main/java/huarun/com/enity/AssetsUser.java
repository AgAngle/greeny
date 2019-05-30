package huarun.com.enity;

public class AssetsUser {

    private String useranme;
    private String name;

    public AssetsUser(String useranme, String name) {
        this.useranme = useranme;
        this.name = name;
    }

    public String getUseranme() {
        return useranme;
    }

    public void setUseranme(String useranme) {
        this.useranme = useranme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
