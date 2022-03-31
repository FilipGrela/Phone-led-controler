package pl.filipgrela.ledcontroller;

public class Variables {
    private static Variables variables;

    public boolean isHSeekBarTouched = false;
    public boolean isSSeekBarTouched = false;
    public boolean isVSeekBarTouched = false;

    public double hValueLast;
    public double sValueLast;
    public double vValueLast;

    public void sethValue(double hValue) {
        hValueLast = this.hValue;
        this.hValue = hValue;
    }

    public void setsValue(double sValue) {
        sValueLast = this.sValue;
        this.sValue = sValue;
    }

    public void setvValue(double vValue) {
        vValueLast = this.vValue;
        this.vValue = vValue;
    }

    public double hValue;
    public double sValue;
    public double vValue;

    private Variables(){
    }



    public static synchronized Variables getInstance() {
        if (variables == null)
            variables = new Variables();
        return variables;
    }
}
