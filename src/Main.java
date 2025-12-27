import UI.ConsoleApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"services","storage","UI", "com.hotelbooker"})
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        ConsoleApp consoleApp = context.getBean(ConsoleApp.class);
        consoleApp.run();
    }
}
