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
