package ProgrammingTask;

import User.Recruiter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import Exception.RequiredFieldsMissingException;
///// jakies tam zmiany

public class ProgrammingTask implements Serializable {

    public enum State {TEST, ACTIVE, AUDIT_NEEDED}

    private static List<ProgrammingTask> programmingTasks = new ArrayList<>(); // ekstensja
    private static Integer FAIL_ATTEMPTS_BEFORE_AUDIT = 3; // atrybut klasowy
    private int failedAttempts;
    private Recruiter taskCreator;
    private State taskState;
    private String taskId;
    private String taskName;
    //https://stackoverflow.com/questions/24547673/why-java-util-optional-is-not-serializable-how-to-serialize-the-object-with-suc
//    private Optional<String> taskCommonName = Optional.empty(); // atrybut opcjonalny
    private String taskCommonName = null;
    private TaskDifficultyLevel taskDifficultyLevel;
    private String taskContent;
    private String taskAnswer;
    private List<String> nameHistory = new ArrayList<>();
    private LocalDate createdDate = LocalDate.now(); // atrybut zlozony
    private List<AttemptHistoryElement> attemptsHistory = new ArrayList<>(); // atrybut zlozony/powtarzalny
    private final String PROGRAMMING_TASK_HEADER = "PROGRAMMING TASK CREATED " + createdDate;
    private static final String ID_NAMESPACE = "PT"; // atrybut klasowy

    protected ProgrammingTask() {
        init();
    }

    public ProgrammingTask(String taskName, String taskContent, String taskAnswer, TaskDifficultyLevel difficultyLevel)
            throws RequiredFieldsMissingException {
        this.taskName = taskName;
        this.taskContent = taskContent;
        this.taskAnswer = taskAnswer;
        this.taskDifficultyLevel = difficultyLevel;
        ProgrammingTask.StaticService.validateProgrammingTask(this);
        init();
        nameHistory.add(this.taskName);
    }

    private void init() {
        setTaskId();
        setTaskState(State.TEST);
        Repository.add(this);
    }

    public static List<ProgrammingTask> getTasks() {
        return programmingTasks;
    }

    @Override
    public String toString() {
        return PROGRAMMING_TASK_HEADER +
                "taskId='" + taskId + '\n' +
                ", taskName='" + taskName + '\n' +
                ", taskCommonName='" + getTaskCommonName().orElse("undefined!") + '\n' +
                ", taskDifficultyLevel=" + taskDifficultyLevel + '\n' +
                ", taskState=" + taskState + '\n' +
                ", taskContent='" + taskContent + '\n' +
                ", taskAnswer='" + taskAnswer + '\n' + '}';
    }

    //atrybut pochodny
    public Integer getTotalAttemptsLength() {
        return attemptsHistory
                .stream()
                .map(attempt -> attempt.length)
                .reduce(0, (a1, a2) -> a1 + a2);
    }

    public long getNumberOfFailedAttempts() {
        return attemptsHistory
                .stream()
                .filter(attempt -> !attempt.passed)
                .count();
    }

    public static Integer getFailAttemptsBeforeAudit() {
        return FAIL_ATTEMPTS_BEFORE_AUDIT;
    }

    public String getTaskName() {
        return taskName;
    }

    public State getTaskState() {
        return taskState;
    }

    public List<String> getNameHistory() {
        return nameHistory;
    }

    public Optional<String> getTaskCommonName() {
        return Optional.ofNullable(taskCommonName);
    }

    public TaskDifficultyLevel getTaskDifficultyLevel() {
        return taskDifficultyLevel;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public String getTaskAnswer() {
        return taskAnswer;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public Recruiter getTaskCreator() {
        return taskCreator;
    }

    public void setTaskCreator(Recruiter taskCreator) {
        if(taskCreator == null){
            return;
        }
        this.taskCreator = taskCreator;
    }

    public List<AttemptHistoryElement> getAttemptsHistory() {
        return attemptsHistory;
    }

    public void setTaskName(String taskName) {
        if (taskName == null) {
            return;
        }
        if (this.taskName != null) {
            nameHistory.add(this.taskName);
        }
        this.taskName = taskName;
    }

    private void setTaskId() {
        this.taskId = prepareId();
    }

    public static String prepareId() {
        Integer numberOfTasks = programmingTasks.size();
        return ID_NAMESPACE + numberOfTasks;
    }

    public void setTaskCommonName(String taskCommonName) {
        this.taskCommonName = taskCommonName;
    }

    public void setTaskDifficultyLevel(TaskDifficultyLevel taskDifficultyLevel) {
        if (taskDifficultyLevel == null) {
            return;
        }
        this.taskDifficultyLevel = taskDifficultyLevel;
    }

    public void setTaskContent(String taskContent) {
        if (taskContent == null) {
            return;
        }
        this.taskContent = taskContent;
    }

    public void setTaskAnswer(String taskAnswer) {
        if (taskAnswer == null) {
            return;
        }
        this.taskAnswer = taskAnswer;
    }

    public void setTaskState(State taskState) {
        if (taskState == null) {
            return;
        }
        this.taskState = taskState;
    }

    public static ProgrammingTaskBuilder getBuilder() {
        return new ProgrammingTaskBuilder();
    }

    public static class StaticService {
        public static List<ProgrammingTask> getSpecificTasks(TaskDifficultyLevel taskDifficultyLevel) {
            return programmingTasks
                    .stream()
                    .filter(programmingTask -> programmingTask.taskDifficultyLevel.label.equals(taskDifficultyLevel.label))
                    .collect(Collectors.toList());
        }

        public static List<ProgrammingTask> getSpecificTasks(Predicate<ProgrammingTask> filter) {
            return programmingTasks
                    .stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        public static List<AttemptHistoryElement> getAllAttempts() {
            return programmingTasks
                    .stream()
                    .map(task -> task.attemptsHistory)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        public static ProgrammingTask getRandomTask() {
            return programmingTasks.get(new Random()
                    .ints(0, programmingTasks.size())
                    .findFirst()
                    .getAsInt());
        }

        public static void showExtent(){
            for(ProgrammingTask programmingTask : programmingTasks){
                System.out.println(programmingTask);
            };
        }

        public static void validateProgrammingTask(ProgrammingTask record) throws RequiredFieldsMissingException {
            if(record.getTaskName()==null
                    || record.getTaskAnswer()==null
                    || record.getTaskContent()==null
                    || record.getTaskDifficultyLevel()==null) {
                throw new RequiredFieldsMissingException("Required fields missing. Please set Name, Answer, Content and Difficulty level.");
            }
        }
    }

    public class InstanceService {
        public void logAttempt(Boolean passed, int length) {
            logAttempt(new AttemptHistoryElement(passed, length, LocalDate.now()));
        }

        //przeciazenie metody logAttempt
        public void logAttempt(AttemptHistoryElement attemptHistoryElement) {
            if (attemptHistoryElement.passed == false) {
                failedAttempts++;
            }
            if (failedAttempts == FAIL_ATTEMPTS_BEFORE_AUDIT) {
                taskState = State.AUDIT_NEEDED;
            }
            ProgrammingTask.this.attemptsHistory.add(attemptHistoryElement);
        }

        public void logAttempts(List<AttemptHistoryElement> attempts) {
            attemptsHistory.addAll(attempts);
        }
    }

    public static class Repository {
        public static void add(ProgrammingTask programmingTask) {
            programmingTasks.add(programmingTask);
        }

        public static void delete(ProgrammingTask programmingTask) {
            programmingTasks.remove(programmingTask);
        }

        public static void writeExtent(ObjectOutputStream stream) throws IOException {
            stream.writeObject(programmingTasks);
        }

        public static List<ProgrammingTask> readExtent(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            programmingTasks = (ArrayList<ProgrammingTask>) stream.readObject();
            return programmingTasks;
        }
    }

    public enum TaskDifficultyLevel implements Serializable {
        VERY_EASY(1, 10, "Very easy"), EASY(3, 12, "Easy"), MEDIUM(5, 14, "Medium"),
        HARD(8, 16, "Hard"), VERY_HARD(11, 25, "Very Hard");

        private int value;
        private int timeInMinutes;
        private String label;

        TaskDifficultyLevel(int value, int timeInMinutes, String label) {
            this.value = value;
            this.timeInMinutes = timeInMinutes;
            this.label = label;
        }
    }

    public class AttemptHistoryElement implements Serializable {
        private String taskName = ProgrammingTask.this.taskName;
        private Boolean passed;
        private int length;
        private LocalDate dateOfAttempt;

        public AttemptHistoryElement(Boolean passed, int attemptLength, LocalDate dateOfAttempt) {
            this.passed = passed;
            this.length = attemptLength;
            this.dateOfAttempt = dateOfAttempt;
        }

        @Override
        public String toString() {
            return "ProgrammingTaskAttemptHistoryElement{" +
                    "taskName='" + taskName + '\'' +
                    ", passed=" + passed +
                    ", length=" + length +
                    ", dateOfAttempt=" + dateOfAttempt +
                    '}';
        }
    }
}
