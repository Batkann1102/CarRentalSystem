package mn.edu.num.carrental;

import mn.edu.num.annotation.EnableIoC;
import mn.edu.num.container.ApplicationContext;
import mn.edu.num.carrental.infrastructure.server.EmbeddedTomcatServer;
import mn.edu.num.carrental.infrastructure.database.DatabaseInitializer;

@EnableIoC(scanPackages = {"mn.edu.num.carrental"}, visualize = true)
public class App {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = ApplicationContext.run(App.class);
        DatabaseInitializer databaseInitializer = applicationContext.getBean(DatabaseInitializer.class);
        databaseInitializer.initializeSchemaIfEnabled();
        EmbeddedTomcatServer server = applicationContext.getBean(EmbeddedTomcatServer.class);
        server.start();
    }
}
