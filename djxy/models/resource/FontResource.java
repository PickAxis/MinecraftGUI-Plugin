package djxy.models.resource;

public class FontResource extends Resource {

    @Override
    public Resource clone() {
        return new FontResource(this.getUrl());
    }

    public FontResource(String url) {
        super(url);
    }

}
