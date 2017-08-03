package at.ac.tuwien.dsg.emma.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import at.ac.tuwien.dsg.orvell.Shell;
import at.ac.tuwien.dsg.orvell.net.TcpShellServer;

/**
 * ManagerApp.
 */
@SpringBootApplication
public class ManagerApp implements CommandLineRunner {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ManagerApp.class, args);
    }

    @Autowired
    private ManagerShell managerShell;

    @Override
    public void run(String... args) throws Exception {
        new TcpShellServer(this::init).run();
    }

    private void init(Shell shell) {
        shell.register(ManagerShell.class, managerShell);
    }

}
