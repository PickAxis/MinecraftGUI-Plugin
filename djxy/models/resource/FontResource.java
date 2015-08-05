/*
 * Copyright (C) Samuel Marchildon-Lavoie - All Rights Reserved
 *
 * License: https://github.com/djxy/MinecraftGUI-Plugin/blob/master/License.txt
 */

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
