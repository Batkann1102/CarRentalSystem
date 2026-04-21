package mn.edu.num.adapters.bridge.server;

import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.adapters.bridge.config.ApplicationConfigComponent;
import mn.edu.num.adapters.inbound.web.CarServlet;
import mn.edu.num.adapters.inbound.web.CorsFilter;
import mn.edu.num.adapters.inbound.web.RentalServlet;
import mn.edu.num.infrastructure.server.EmbeddedTomcatServer;

@Component
public class EmbeddedTomcatServerComponent extends EmbeddedTomcatServer {

    @Autowired
    private ApplicationConfigComponent applicationConfig;

    @Autowired
    private CarServlet carServlet;

    @Autowired
    private RentalServlet rentalServlet;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    public void start() throws Exception {
        setApplicationConfig(applicationConfig);
        setCarServlet(carServlet);
        setRentalServlet(rentalServlet);
        setCorsFilter(corsFilter);
        super.start();
    }
}

