package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/tasks")
public class TaskController {
    @Autowired
    private TaskService service;

    @GetMapping
    public ResponseEntity<List<TaskModel>> getTasks(@RequestParam(required = false) String title,
                                                    @RequestParam(required = false) String description,
                                                    @RequestParam(required = false) String assignedTo,
                                                    @RequestParam(required = false) TaskModel.TaskStatus status,
                                                    @RequestParam(required = false) TaskModel.TaskSeverity severity) {
        List<TaskModel> tasks = service.getTasks(title, description, assignedTo, status, severity);
        return tasks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tasks);
    }

    @GetMapping(value = "/sorted", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<List<TaskModel>> getTasksSortedByTitle(@RequestHeader("Accept") String format,
                                                                @RequestParam(required = false) String title,
                                                                @RequestParam(required = false) String description,
                                                                @RequestParam(required = false) String assignedTo,
                                                                @RequestParam(required = false) TaskModel.TaskStatus status,
                                                                @RequestParam(required = false) TaskModel.TaskSeverity severity) throws IOException {
        List<TaskModel> tasks = service.getTasksSortedByTitle(title, description, assignedTo, status, severity);

        for(TaskModel task : tasks) {
            service.addSortedTask(task);
        }

        return tasks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskModel> getTaskById(@PathVariable String id) {
        Optional<TaskModel> task = service.getTask(id);
        return task.isPresent() ? ResponseEntity.ok(task.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Void> addTask(@RequestBody TaskModel task) {
        try {
            service.addTask(task);
            URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(task.getId()).toUri();
            return ResponseEntity.created(uri).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable String id, @RequestBody TaskModel task) {
        try {
            if(service.updateTask(id, task)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchTask(@PathVariable String id, @RequestBody TaskModel task) {
        try {
            if(service.patchTask(id, task)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        try {
            if(service.deleteTask(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
