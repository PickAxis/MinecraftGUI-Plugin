/*
 *     Minecraft GUI Server
 *     Copyright (C) 2015  Samuel Marchildon-Lavoie
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package djxy.models.component;

public enum ComponentType{
    PANEL,
    IMAGE,
    PARAGRAPH,
    BUTTON,
    BUTTON_URL,
    LIST,
    INPUT_INVISIBLE,
    INPUT_TEXT,
    INPUT_PASSWORD,
    INPUT_INTEGER,
    INPUT_DECIMAL;

    public static ComponentType getComponentType(String type){
        try{
            return valueOf(type.toUpperCase());
        }catch (Exception e){
        }

        return PANEL;
    }
}
