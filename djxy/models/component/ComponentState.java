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
