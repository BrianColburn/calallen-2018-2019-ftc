package org.firstinspires.ftc.teamcode.units;

public class Foot implements Distance {
    private final double value;

    public Foot(double value) {
        this.value = value;
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    public double toCM() {
        return new Inches(toInches()).toCM();
    }

    @Override
    public double toInches() {
        return value*12;
    }
}
