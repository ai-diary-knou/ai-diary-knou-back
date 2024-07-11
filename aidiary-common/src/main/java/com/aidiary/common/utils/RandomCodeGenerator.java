package com.aidiary.common.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RandomCodeGenerator {

    private static RandomCodeGenerator randomCodeGenerator;

    public static RandomCodeGenerator getInstance(){
        if (Objects.isNull(randomCodeGenerator)) {
            randomCodeGenerator = new RandomCodeGenerator();
        }
        return randomCodeGenerator;
    }

    public String createAlphanumericCodeWithSpecialKeys(){

        String permittedLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%&*123456789";

        List<Character> charList = permittedLetters.chars().mapToObj(c -> (char) c).toList();

        Collections.shuffle(charList);

        return charList.stream().limit(10).map(String::valueOf).collect(Collectors.joining());
    }

}
