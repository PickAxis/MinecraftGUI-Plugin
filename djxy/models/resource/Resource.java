package djxy.models.resource;

public abstract class Resource {

    private String url;

    public abstract Resource clone();

    public Resource(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
