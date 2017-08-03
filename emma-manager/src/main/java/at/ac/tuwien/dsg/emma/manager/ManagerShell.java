package at.ac.tuwien.dsg.emma.manager;

import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.orvell.Context;
import at.ac.tuwien.dsg.orvell.annotation.Command;
import at.ac.tuwien.dsg.orvell.annotation.CommandGroup;

/**
 * ManagerShell.
 */
@CommandGroup("eman")
@Component
public class ManagerShell {

    @Command
    public void status(Context context) {
        context.out().println("status unknown");
    }
}
