package org.firstinspires.ftc.teamcode.units;

import org.jetbrains.annotations.NotNull;

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
    public Centimeter toCM() {
        return new Centimeter(value() * 2.54);
    }

    @Override
    public Inches toInches() {
        return this;
    }

    @Override
    public double abs() {
        return Math.abs(toCM().value());
    }

    @Override
    public double signum() {
        return Math.signum(value);
    }

    @Override
    public int compareTo(@NotNull Distance other) {
        return Double.compare(value, other.toInches().value);
    }
}
