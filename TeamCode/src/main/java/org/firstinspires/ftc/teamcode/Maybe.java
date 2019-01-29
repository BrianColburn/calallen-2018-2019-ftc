package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Function;

public class Maybe<T> {
    private T item;

    public Maybe() {}

    public Maybe(T item) {
        this.item = item;
    }

    public <R> Maybe<R> map(Function<T, R> f) {
        return !isNull() ? new Maybe<>(f.apply(item)) : new Maybe<R>();
    }

    public void map(Consumer<T> f) {
        if (!isNull()) {
            f.accept(item);
        }
    }

    public boolean isNull() {
        return item == null;
    }
}
