package hfe;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.Map;

public class Start implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String id = execution.getId();
        Map<String, Object> variables = execution.getVariables();
    }
}
