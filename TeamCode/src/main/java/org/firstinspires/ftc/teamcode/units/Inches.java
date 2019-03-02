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
        return value()*2.54;
    }

    @Override
    public double toInches() {
        return value();
    }
}
