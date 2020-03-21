package hfe;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;

import javax.inject.Named;

@Named
public class TaskListHelper {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public TaskListHelper(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }
}
