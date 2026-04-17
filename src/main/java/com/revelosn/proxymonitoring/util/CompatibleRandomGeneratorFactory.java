package com.revelosn.proxymonitoring.util;

import java.util.Random;
import java.util.random.RandomGenerator;

public final class CompatibleRandomGeneratorFactory {

    private CompatibleRandomGeneratorFactory() {
    }

    public static RandomGenerator create() {
        try {
            return RandomGenerator.getDefault();
        } catch (IllegalArgumentException exception) {
            return new Random();
        }
    }
}

