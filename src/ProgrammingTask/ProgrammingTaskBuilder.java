package ProgrammingTask;

import User.Recruiter;
import Exception.RequiredFieldsMissingException;

public class ProgrammingTaskBuilder {
    private ProgrammingTask record;

    ProgrammingTaskBuilder(){
        record = new ProgrammingTask();
    }

    public ProgrammingTaskBuilder setTaskName(String taskName){
        record.setTaskName(taskName);
        return this;
    }

    public ProgrammingTaskBuilder setTaskCommonName(String commonName){
        record.setTaskCommonName(commonName);
        return this;
    }

    public ProgrammingTaskBuilder setTaskDifficultyLevel(ProgrammingTask.TaskDifficultyLevel taskDifficultyLevel){
        record.setTaskDifficultyLevel(taskDifficultyLevel);
        return this;
    }

    public ProgrammingTaskBuilder setTaskContent(String taskContent){
        record.setTaskContent(taskContent);
        return this;
    }

    public ProgrammingTaskBuilder setTaskAnswer(String taskAnswer){
        record.setTaskAnswer(taskAnswer);
        return this;
    }

    public ProgrammingTaskBuilder setTaskCreator(Recruiter taskCreator){
        record.setTaskCreator(taskCreator);
        return this;
    }

    public ProgrammingTaskBuilder setTaskState(ProgrammingTask.State state){
        record.setTaskState(state);
        return this;
    }
    public ProgrammingTask getProgrammingTask() throws RequiredFieldsMissingException {
        ProgrammingTask.StaticService.validateProgrammingTask(record);
        return record;
    }
}
