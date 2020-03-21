package hfe;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.camunda", "hfe"})
public class Bpm {

    private final ProcessEngine processEngine;
    private final TaskListHelper helper;
    private final TaskService taskService;

    public static void main(String[] args) {
        SpringApplication.run(Bpm.class);
    }

    Bpm(ProcessEngine processEngine, TaskListHelper helper, TaskService taskService) {
        this.processEngine = processEngine;
        this.helper = helper;
        this.taskService = taskService;

        BpmnModelInstance modelInstance;


        ProcessDefinition instance = getProcessDefinition();
        if(instance == null) {
            modelInstance = Bpmn.createExecutableProcess()
                    .name("hfe")
                    .startEvent()
                    .name("bAre Massenupdate")
                    .userTask()
                    .name("Assign Approver")
                    .endEvent()
                    .name("Invoice processed")
                    .done();
            Deployment deployment = processEngine.getRepositoryService().createDeployment().addModelInstance("hfe.bpmn", modelInstance).deploy();
            instance = getProcessDefinition();
            String deploymentId = deployment.getId();
            String in = instance.getId();
        } else {
            modelInstance = processEngine.getRepositoryService().getBpmnModelInstance(instance.getId());
        }
        Bpmn.validateModel(modelInstance);

        processEngine.getRuntimeService().createProcessInstanceById(instance.getId()).execute();

        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(instance.getId());
        Task task = this.taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();




        //processEngine.getManagementService().registerDeploymentForJobExecutor(modelInstance.getDefinitions().getId());

        //Bpmn.writeModelToStream(System.out, modelInstance);

    }

    private ProcessDefinition getProcessDefinition() {
        return processEngine.getRepositoryService().createProcessDefinitionQuery().list().stream().filter(def -> def.getName().equals("hfe")).findAny().orElse(null);
    }
}
