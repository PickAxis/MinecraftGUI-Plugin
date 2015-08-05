/*
 * Copyright (C) Samuel Marchildon-Lavoie - All Rights Reserved
 *
 * License: https://github.com/djxy/MinecraftGUI-Plugin/blob/master/License.txt
 */

package djxy.models.component;

public enum ComponentState {
    NORMAL,
    HOVER,
    CLICK;

    public static ComponentState getComponentState(String value){
        try{
            return valueOf(value.toUpperCase());
        }catch (Exception e){}

        return null;
    }
}
