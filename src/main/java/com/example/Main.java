package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Ошибка: Укажите путь к входному файлу в качестве аргумента");
            System.err.println("Пример: java -jar solution.jar lng-4.txt.gz");
            return;
        }

        String inputFilePath = args[0];
        String outputFilePath = "output.txt";
        long startTime = System.currentTimeMillis();

        try {
            Set<String> uniqueLinesSet = readAndFilterUniqueLines(inputFilePath);
            List<String> uniqueLines = new ArrayList<>(uniqueLinesSet);

            if (uniqueLines.isEmpty()) {
                System.out.println("Не найдено валидных строк для обработки");
                return;
            }
            
            Map<Integer, List<String>> groups = groupLines(uniqueLines);

            writeGroupsToFile(groups, outputFilePath);

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            System.out.println("Программа успешно выполнена.");
            // сколько памяти выводить не стал, тем более что запускаемся с флагом
            System.out.printf("Время выполнения: %d мс.%n", executionTime);

        } catch (IOException e) {
            System.err.println("Произошла ошибка при обработке файла: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Set<String> readAndFilterUniqueLines(String filePath) throws IOException {
        Set<String> uniqueLines = new LinkedHashSet<>();
        // добавил поддержку .gz файлов
        if (filePath.endsWith(".gz")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new GZIPInputStream(new FileInputStream(filePath)), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isValidLine(line)) {
                        uniqueLines.add(line);
                    }
                }
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isValidLine(line)) {
                        uniqueLines.add(line);
                    }
                }
            }
        }
        return uniqueLines;
    }

    private static boolean isValidLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return false;
        }

        String[] parts = line.split(";", -1);
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty() && !part.matches("^\\d+$") && !part.matches("^\"\\d+\"$")) {
                return false;
            }
        }
        
        return true;
    }

    private static Map<Integer, List<String>> groupLines(List<String> lines) {
        int n = lines.size();
        DisjointSetUnion dsu = new DisjointSetUnion(n);    

        // тут мапа для быстрого поиска одинаковых значений
        Map<String, Integer> valueToLineIndex = new HashMap<>();

        for (int i = 0; i < n; i++) {
            String line = lines.get(i);
            String[] parts = line.split(";", -1);
            
            for (int j = 0; j < parts.length; j++) {
                String value = parts[j].trim();
                
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1).trim();
                }

                if (!value.isEmpty()) {
                    String key = j + ":" + value;
                    if (valueToLineIndex.containsKey(key)) {
                        dsu.union(i, valueToLineIndex.get(key));
                    } else {
                        valueToLineIndex.put(key, i);
                    }
                }
            }
        }

        Map<Integer, List<String>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = dsu.find(i);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(lines.get(i));
        }
        return groups;
    }

    private static void writeGroupsToFile(Map<Integer, List<String>> groups, String filePath) throws IOException {
        // оставляем только требуемые группы, где больше одного элемента
        List<List<String>> sortedGroups = groups.values().stream()
                .filter(list -> list.size() > 1)
                .sorted(Comparator.comparingInt(List<String>::size).reversed())
                .collect(Collectors.toList());
        
        long groupsWithMoreThanOneElement = sortedGroups.size();
        System.out.println("Количество полученных групп с более чем одним элементом: " + groupsWithMoreThanOneElement);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            writer.write("Всего групп с более чем одним элементом: " + groupsWithMoreThanOneElement);
            writer.newLine();
            writer.newLine();

            int groupCounter = 1;
            for (List<String> group : sortedGroups) {
                writer.write("Группа " + groupCounter++);
                writer.newLine();
                for (String line : group) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.newLine();
            }
        }
    }
}