package br.pucpr.databases;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataLoader {

    public List<List<String>> loadDataToList(String path, boolean removeHeader) {
        List<List<String>> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(
                Path.of(path),
                StandardCharsets.UTF_8))
        {
            String line;
            while ((line = br.readLine()) != null) {
                String[] value = line.split(";");
                list.add(Arrays.asList(value));
            }
            if (removeHeader) list.remove(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}

