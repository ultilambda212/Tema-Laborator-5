package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TaskRepository {
    private File file;
    private File file2;
    private Map<String, TaskModel> tasks;

    private TaskRepository(@Value("${repository}") String repository, @Value("${specialRepository}") String specialRepository) throws IOException {
        file = Paths.get(repository).toFile();
        file2 = Paths.get(specialRepository).toFile();
        tasks = file.exists() ? Arrays.stream(new ObjectMapper().readValue(file, TaskModel[].class))
                .collect(Collectors.toMap(task -> task.getId(), task -> task))
                : new HashMap<>();
    }

    public Collection<TaskModel> findAll() {
        return tasks.values();
    }

    public Optional<TaskModel> findById(String id) {
        return tasks.containsKey(id) ?
                Optional.of(tasks.get(id)):
                Optional.empty();
    }

    public void save(TaskModel task) throws IOException {
        if(tasks.containsKey(task.getId())) {
            tasks.get(task.getId()).update(task);
        } else {
            tasks.put(task.getId(), task);
        }
        writeToFile();
    }

    public void saveSortedTask(TaskModel task) throws IOException {
        if(tasks.containsKey(task.getId())) {
            tasks.get(task.getId()).update(task);
        } else {
            tasks.put(task.getId(), task);
        }
        writeToFileSorted();
    }

    private void writeToFileSorted() throws IOException {
        ObjectMapper objectMapper = new XmlMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        List<TaskModel> tasks = (List<TaskModel>) findAll();
        Collections.sort(tasks, new SortTaskByTitle());
        objectMapper.writeValue(file2, tasks);

    }

    private void writeToFile() throws IOException {
        new ObjectMapper().writeValue(file, findAll());
    }

    public boolean deleteById(String id) throws IOException {
        if(tasks.containsKey(id)) {
            tasks.remove(id);
            writeToFile();
            return true;
        } else {
            return false;
        }
    }

}
