package mn.edu.num.infrastructure.server;

import jakarta.servlet.DispatcherType;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;
import mn.edu.num.adapters.inbound.web.CarServlet;
import mn.edu.num.adapters.inbound.web.CorsFilter;
import mn.edu.num.adapters.inbound.web.RentalServlet;
import mn.edu.num.infrastructure.config.ApplicationConfig;
import mn.edu.num.shared.constants.ApiConstants;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

public class EmbeddedTomcatServer {

    private final AtomicBoolean started = new AtomicBoolean(false);

    private ApplicationConfig applicationConfig;

    private CarServlet carServlet;

    private RentalServlet rentalServlet;

    private CorsFilter corsFilter;

    private Tomcat tomcat;

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public void setCarServlet(CarServlet carServlet) {
        this.carServlet = carServlet;
    }

    public void setRentalServlet(RentalServlet rentalServlet) {
        this.rentalServlet = rentalServlet;
    }

    public void setCorsFilter(CorsFilter corsFilter) {
        this.corsFilter = corsFilter;
    }

    public void start() throws Exception {
        if (!started.compareAndSet(false, true)) {
            return;
        }

        tomcat = new Tomcat();
        tomcat.setPort(applicationConfig.getServerPort());
        tomcat.setBaseDir(Files.createTempDirectory("car-rental-tomcat").toFile().getAbsolutePath());
        tomcat.getConnector();

        Context context = tomcat.addContext(normalizeContextPath(applicationConfig.getContextPath()),
                new File(".").getAbsolutePath());

        registerServlet(context, "carServlet", carServlet, ApiConstants.AVAILABLE_CARS_ENDPOINT);
        registerServlet(context, "rentalServlet", rentalServlet, ApiConstants.RENTAL_ENDPOINT);
        registerHealthServlet(context);
        registerCorsFilter(context);

        tomcat.start();

        String baseUrl = "http://localhost:" + applicationConfig.getServerPort()
                + displayContextPath(applicationConfig.getContextPath());
        System.out.println("CarRentalSystem backend started successfully.");
        System.out.println("Available cars endpoint: " + baseUrl + ApiConstants.AVAILABLE_CARS_ENDPOINT);
        System.out.println("Rent car endpoint: " + baseUrl + ApiConstants.RENTAL_ENDPOINT);

        tomcat.getServer().await();
    }

    private void registerServlet(Context context, String name, jakarta.servlet.http.HttpServlet servlet, String path) {
        Tomcat.addServlet(context, name, servlet);
        context.addServletMappingDecoded(path, name);
    }

    private void registerHealthServlet(Context context) {
        Tomcat.addServlet(context, "healthServlet", new jakarta.servlet.http.HttpServlet() {
            @Override
            protected void doGet(jakarta.servlet.http.HttpServletRequest req,
                                 jakarta.servlet.http.HttpServletResponse resp) throws java.io.IOException {
                resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_OK);
                resp.setContentType(ApiConstants.APPLICATION_JSON);
                resp.getWriter().write("{\"status\":\"UP\"}");
            }
        });
        context.addServletMappingDecoded(ApiConstants.HEALTH_ENDPOINT, "healthServlet");
    }

    private void registerCorsFilter(Context context) {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("corsFilter");
        filterDef.setFilter(corsFilter);
        filterDef.setFilterClass(corsFilter.getClass().getName());
        context.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("corsFilter");
        filterMap.addURLPattern("/*");
        filterMap.setDispatcher(DispatcherType.REQUEST.name());
        filterMap.setDispatcher(DispatcherType.FORWARD.name());
        filterMap.setDispatcher(DispatcherType.INCLUDE.name());
        filterMap.setDispatcher(DispatcherType.ASYNC.name());
        filterMap.setDispatcher(DispatcherType.ERROR.name());
        context.addFilterMapBefore(filterMap);
    }

    private String normalizeContextPath(String contextPath) {
        if (contextPath == null || contextPath.trim().isEmpty() || "/".equals(contextPath.trim())) {
            return "";
        }
        return contextPath.startsWith("/") ? contextPath : "/" + contextPath;
    }

    private String displayContextPath(String contextPath) {
        return normalizeContextPath(contextPath);
    }
}

