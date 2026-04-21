package mn.edu.num;

import mn.edu.num.annotation.EnableIoC;
import mn.edu.num.container.ApplicationContext;
import mn.edu.num.infrastructure.server.EmbeddedTomcatServer;

@EnableIoC(scanPackages = {"mn.edu.num"}, visualize = true)
public class App {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = ApplicationContext.run(App.class);
        EmbeddedTomcatServer server = applicationContext.getBean(EmbeddedTomcatServer.class);
        server.start();
    }
}
