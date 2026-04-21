package mn.edu.num.adapters.inbound.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.core.domain.Car;
import mn.edu.num.core.ports.in.IGetAvailableCarsUseCase;
import mn.edu.num.shared.constants.ApiConstants;
import mn.edu.num.shared.util.JsonConverter;

@Component
public class CarServlet extends HttpServlet {

    @Autowired
    private IGetAvailableCarsUseCase getAvailableCarsUseCase;

    @Autowired
    private JsonConverter jsonConverter;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Car> cars = getAvailableCarsUseCase.getAvailableCars();
            writeJson(resp, HttpServletResponse.SC_OK, cars);
        } catch (Exception ex) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private void writeJson(HttpServletResponse response, int status, Object payload) throws IOException {
        response.setStatus(status);
        response.setContentType(ApiConstants.APPLICATION_JSON);
        response.getWriter().write(jsonConverter.toJson(payload));
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        writeJson(response, status, new ErrorResponse(message));
    }

    private record ErrorResponse(String error) {
    }
}

