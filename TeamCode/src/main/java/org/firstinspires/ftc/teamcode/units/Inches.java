package org.firstinspires.ftc.teamcode.units;

public class Inches implements Distance {
    private final double value;

    public Inches(double value) {
        this.value = value;
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    public double toCM() {
        return value()*0.39370079;
    }

    @Override
    public double toInches() {
        return value();
    }
}
