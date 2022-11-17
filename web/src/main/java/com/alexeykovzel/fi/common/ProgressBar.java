package com.alexeykovzel.fi.common;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.function.Consumer;

@Slf4j
public class ProgressBar {
    private static final int TEXT_SIZE = 40;
    private static final int BAR_SIZE = 20;

    private final String text;
    private final int total;

    public ProgressBar(String text, int total) {
        this.text = text + " ".repeat(TEXT_SIZE - text.length());
        this.total = total;
        update(0);
    }

    public static <T> void execute(String text, Collection<T> entities, Consumer<T> consumer) {
        if (entities == null || consumer == null) return;
        ProgressBar bar = new ProgressBar(text, entities.size());
        int i = 0;
        for (T entity : entities) {
            consumer.accept(entity);
            bar.update(i + 1);
            i++;
        }
    }

    public static void execute(String text, Runnable runnable) {
        ProgressBar bar = new ProgressBar(text, 1);
        runnable.run();
        bar.update(1);
    }

    public void update(int current) {
        double share = (total != 0) ? (double) current / total : 1;
        int nums = (int) Math.round(BAR_SIZE * share);
        String signs = "#".repeat(nums) + " ".repeat(BAR_SIZE - nums);
        System.out.printf(text + "[%s] %d/%d\r", signs, current, total);
        if (current == total) System.out.println();
    }
}
