package com.qst.dms.utils;

import java.math.BigDecimal;

public class NumberUtils {
    public static double round(double number, int round) {
        BigDecimal decimal = new BigDecimal(number);
        return decimal.setScale(round,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
