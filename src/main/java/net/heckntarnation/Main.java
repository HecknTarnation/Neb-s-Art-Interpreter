package net.heckntarnation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]);
        BufferedReader fr = new BufferedReader(new FileReader(file));
        List<String> lines = fr.lines().toList();
        lines.forEach((s) ->{System.out.println(s);});
    }
}