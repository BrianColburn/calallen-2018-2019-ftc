package org.firstinspires.ftc.teamcode.units;

import org.jetbrains.annotations.NotNull;

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
    public Centimeter toCM() {
        return new Inches(toInches().value()).toCM();
    }

    @Override
    public Inches toInches() {
        return new Inches(value * 12);
    }

    @Override
    public double abs() {
        return Math.abs(toCM().getValue());
    }

    @Override
    public double signum() {
        return Math.signum(value);
    }

    @Override
    public int compareTo(@NotNull Distance other) {
        return Double.compare(toInches().value(), other.toInches().value());
    }
}
