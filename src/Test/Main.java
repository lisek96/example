package Test;

import ProgrammingTask.ProgrammingTask;
import ProgrammingTask.ProgrammingTaskBuilder;
import Exception.RequiredFieldsMissingException;

import java.io.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, RequiredFieldsMissingException {
        /// stworzenie taska bez atrybutu opcjonalnego commonName
        ProgrammingTask programmingTask1 = ProgrammingTask.getBuilder()
                .setTaskContent("Napisz metodę, która jako argument przyjmuje 2 liczby i wypisuje ich sumę, różnicę i iloczyn")
                .setTaskAnswer("Np.: twojaMetoda(2, 5) -> suma: 7, różnica: -3, iloczyn: 10")
                .setTaskState(ProgrammingTask.State.ACTIVE)
                .setTaskName("Pre-Calculator")
                .setTaskDifficultyLevel(ProgrammingTask.TaskDifficultyLevel.VERY_EASY)
                .getProgrammingTask();

        /// stworzenie taska z atrybutem opcjonalym commonName
        ProgrammingTask programmingTask2 = ProgrammingTask.getBuilder()
                .setTaskContent("Napisz metodę, która jako argument przyjmie trzy liczby. " +
                        "Metoda powinna zwrócić true jeśli z odcinków o długości przekazanych " +
                        "w argumentach można zbudować trójkąt prostokątny.")
                .setTaskAnswer("checkTriangle(3,100,2) -> false")
                .setTaskName("Triangle builder")
                .setTaskDifficultyLevel(ProgrammingTask.TaskDifficultyLevel.EASY)
                .setTaskCommonName("Some-sample-common-name")
                .getProgrammingTask();

        //// mozliwosc stwozrenia obiektu poprzez konstruktor/buildera ale zawsze z wypelnionymi required fieldami
        ProgrammingTask programmingTask = new ProgrammingTask(
                "This task", "Wont", "Be used", ProgrammingTask.TaskDifficultyLevel.VERY_HARD);

        String odstep = "\n\n------------------------------------------------------------------------------------------\n";
        //wyświetlenie ekstensji, zostały dodane 3 taski
        ProgrammingTask.StaticService.showExtent();
        //usunieice taska i ponowne wyświetlenie, a potem dodanie i wyświetlenie.
        ProgrammingTask.Repository.delete(programmingTask1);
        System.out.println(odstep);
        ProgrammingTask.StaticService.showExtent();
        ProgrammingTask.Repository.add(programmingTask1);
        System.out.println(odstep);
        ProgrammingTask.StaticService.showExtent();
        //serializacja ekstensji
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("t.tmp"));
        ProgrammingTask.Repository.writeExtent(oos);
        //odczyt ekstensji
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("t.tmp"));
        ProgrammingTask.Repository.readExtent(ois);
        System.out.println(odstep);
        ProgrammingTask.StaticService.showExtent();
        //atrybut złożony
        LocalDate localDate = programmingTask1.getCreatedDate();
        System.out.println(odstep+localDate);
        //atrybut opcjonalny wywołanie przesłoniętej metody toString i gettera na instancji z wypełnionym atrybutem opcjonalnym i nie
        System.out.println(odstep + programmingTask2);
        System.out.println(programmingTask2.getTaskCommonName().get());
        System.out.println(odstep + programmingTask1);
        System.out.println(programmingTask1.getTaskCommonName().orElse("not defined here"));
        //atrybut powtarzalny - historia nazw jakie nosilo zadanie, moze sie zmieniac z roznych wzgledow
        System.out.println(odstep + "atrybut powtarzalny - historia nazw jakie nosilo zadanie, moze sie zmieniac z roznych wzgledow");
        System.out.println(programmingTask.getNameHistory());
        programmingTask.setTaskName(programmingTask.getTaskName()+"#2");
        programmingTask.setTaskName(programmingTask.getTaskName()+"#3");
        programmingTask.setTaskName(programmingTask.getTaskName()+"#4");
        System.out.println(programmingTask.getNameHistory());
        //atrybut klasowy - wspolna liczba nieudanych prób rozwiązania przed 'audytem' |
        ProgrammingTask.InstanceService instanceService1 = programmingTask1. new InstanceService();
        System.out.println(odstep + "Failed attempts before state turns to audit: " + ProgrammingTask.getFailAttemptsBeforeAudit());
        System.out.println("State before: " + programmingTask1.getTaskState());
        //przeciazona metoda logAttempt przyjmujaca argumenty proste lub instancje obiektu
        instanceService1.logAttempt(false, 10);
        instanceService1.logAttempt(programmingTask1 .new AttemptHistoryElement(true, 15, LocalDate.now()));
        instanceService1.logAttempt(false, 16);
        System.out.println("State after 2 fails: " + programmingTask1.getTaskState());
        instanceService1.logAttempt(false, 20);
        System.out.println("State after 3 fails: " + programmingTask1.getTaskState());
        //atrybut pochodny - łączny czas wszystkich prób rozwiązania zadania
        System.out.println(odstep +  "Total attempts length, should be 10+15+16+20=61: " + programmingTask1.getTotalAttemptsLength());
        //metoda klasowa - dostarczenie zadań o podanym poziomie trudności
        System.out.println(odstep + "EASY TASKS: " + ProgrammingTask.StaticService.getSpecificTasks(ProgrammingTask.TaskDifficultyLevel.EASY));
        //lub wybierz random task
        System.out.println("Random task: " + ProgrammingTask.StaticService.getRandomTask());
    }
}
