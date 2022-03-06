package pl.filipgrela.ledcontroller;

public class Variables {
    private static Variables variables;

    public boolean isHSeekBarTouched = false;
    public boolean isSSeekBarTouched = false;
    public boolean isVSeekBarTouched = false;

    public double hValue;
    public double sValue;
    public double vValue;

    private Variables(){
    }

    public static synchronized Variables getInstance( ) {
        if (variables == null)
            variables=new Variables();
        return variables;
    }
}
