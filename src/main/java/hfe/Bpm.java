package hfe;

import org.apache.ibatis.session.SqlSessionFactory;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"org.camunda", "hfe"})
public class Bpm {

    private final ProcessEngine processEngine;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final SqlSessionFactory session;

    public static void main(String[] args) {
        SpringApplication.run(Bpm.class);
    }

    Bpm(ProcessEngine processEngine, RuntimeService runtimeService, TaskService taskService, SqlSessionFactory session) {
        this.processEngine = processEngine;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.session = session;

        BpmnModelInstance modelInstance;


        for(Deployment instance : getProcessDefinition()) {
            processEngine.getRepositoryService().deleteDeployment(instance.getId(), true);
        }

        modelInstance = Bpmn.createExecutableProcess()
                .name("hfe")
                .startEvent()
                .name("bAre Massenupdate")
                .serviceTask()
                .name("hfe-2")
                .camundaClass(Start.class)
                .endEvent()
                .name("Invoice processed")
                .done();
        Bpmn.validateModel(modelInstance);
        Deployment deployment = processEngine.getRepositoryService().createDeployment().addModelInstance("hfe.bpmn", modelInstance).deploy();

        ProcessDefinition definition = processEngine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deployment.getId()).list().get(0);

        //modelInstance = processEngine.getRepositoryService().getBpmnModelInstance(instance.getId());



        ProcessInstance processInstance = runtimeService.startProcessInstanceById(definition.getId());
        System.out.println(processInstance);

        List<Task> tasks = taskService.createTaskQuery().list();
        for(Task task : tasks) {
            System.out.println("Task: " + task.getName());
        }


        //ProcessInstance processInstance = runtimeService.startProcessInstanceById(instance.getId());
        //Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();


        //processEngine.getManagementService().registerDeploymentForJobExecutor(modelInstance.getDefinitions().getId());

        //Bpmn.writeModelToStream(System.out, modelInstance);

    }

    private List<Deployment> getProcessDefinition() {
        List<Deployment> result = processEngine.getRepositoryService().createDeploymentQuery().list();
        return result;
    }
}
