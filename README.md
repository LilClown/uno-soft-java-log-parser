# Решение задачи группировки строк


Решение основано на DSU

## Как запустить

1.  **Соберите проект** с помощью Maven:
    ```bash
    mvn clean package
    ```
    
2.  **Запустите JAR-файл**, указав полный путь к входному файлу:
    ```bash
    java -Xmx1G -jar your-project-name-1.0-SNAPSHOT-jar-with-dependencies.jar /путь/к/вашему/файлу/lng-4.txt
    ```
   
    Результаты будут записаны в файл `output.txt`
