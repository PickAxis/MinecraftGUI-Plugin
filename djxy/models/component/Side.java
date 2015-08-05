/*
 * Copyright (C) Samuel Marchildon-Lavoie - All Rights Reserved
 *
 * License: https://github.com/djxy/MinecraftGUI-Plugin/blob/master/License.txt
 */

package djxy.models.component;

public class Side {
    
    private boolean top = false;
    private boolean left = false;
    private boolean right = false;
    private boolean bottom = false;

    public Side() {
    }

    public Side(boolean left, boolean top, boolean right, boolean bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public boolean isTop() {
        return top;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isBottom() {
        return bottom;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setBottom(boolean bottom) {
        this.bottom = bottom;
    }
    
}
