package org.firstinspires.ftc.teamcode.units;

public class Centimeter implements Distance {
    private final double value;

    public Centimeter(double value) {
        this.value = value;
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    public double toCM() {
        return value;
    }

    @Override
    public double toInches() {
        return value/2.54;
    }
}
