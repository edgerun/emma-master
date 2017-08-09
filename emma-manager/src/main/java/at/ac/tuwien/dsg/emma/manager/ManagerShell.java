package at.ac.tuwien.dsg.emma.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.orvell.Context;
import at.ac.tuwien.dsg.orvell.annotation.Command;
import at.ac.tuwien.dsg.orvell.annotation.CommandGroup;

@CommandGroup("emma")
@Component
public class ManagerShell {

    @Autowired
    private Environment environment;

    @Command
    public void status(Context ctx) {
        ctx.out().println("status unknown");
    }

    @Command
    public void env(Context ctx) {
        Map<String, Object> properties = getAllProperties();

        List<String> keys = new ArrayList<>(properties.keySet());
        keys.removeIf(key -> !key.startsWith("emma."));
        keys.sort(String::compareTo);

        for (String key : keys) {
            ctx.out().printf("%-30s %s%n", key, properties.get(key));
        }
        ctx.out().flush();
    }

    private Map<String, Object> getAllProperties() {
        Map<String, Object> map = new HashMap<>();
        for (PropertySource<?> propertySource : ((AbstractEnvironment) environment).getPropertySources()) {
            if (propertySource instanceof MapPropertySource) {
                map.putAll(((MapPropertySource) propertySource).getSource());
            }
        }
        return map;
    }

}
