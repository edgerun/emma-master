package at.ac.tuwien.dsg.emma.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import at.ac.tuwien.dsg.orvell.Shell;
import at.ac.tuwien.dsg.orvell.net.TcpShellServer;

/**
 * ManagerApp.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ManagerApp implements CommandLineRunner {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ManagerApp.class, args);
    }

    @Autowired
    private ManagerShell managerShell;

    @Value("${emma.manager.shell.port}")
    private Integer shellPort;

    @Override
    public void run(String... args) throws Exception {
        new TcpShellServer(shellPort, this::init).run();
    }

    private void init(Shell shell) {
        shell.register(ManagerShell.class, managerShell);
    }

}
