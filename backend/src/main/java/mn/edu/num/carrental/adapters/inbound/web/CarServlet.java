package mn.edu.num.carrental.adapters.inbound.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.carrental.adapters.inbound.web.dto.CarRequestDto;
import mn.edu.num.carrental.core.domain.Car;
import mn.edu.num.carrental.core.exception.CarDeletionNotAllowedException;
import mn.edu.num.carrental.core.exception.CarNotFoundException;
import mn.edu.num.carrental.core.exception.DuplicateCarException;
import mn.edu.num.carrental.core.ports.in.IGetAvailableCarsUseCase;
import mn.edu.num.carrental.core.ports.in.IGetCarsUseCase;
import mn.edu.num.carrental.core.ports.in.IManageCarsUseCase;
import mn.edu.num.carrental.shared.constants.ApiConstants;
import mn.edu.num.carrental.shared.util.JsonConverter;

@Component
public class CarServlet extends HttpServlet {

    @Autowired
    private IGetAvailableCarsUseCase getAvailableCarsUseCase;

    @Autowired
    private IGetCarsUseCase getCarsUseCase;

    @Autowired
    private IManageCarsUseCase manageCarsUseCase;

    @Autowired
    private mn.edu.num.carrental.adapters.inbound.web.mapper.WebDtoMapper webDtoMapper;

    @Autowired
    private JsonConverter jsonConverter;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = normalizePath(req.getPathInfo());
            if (path.isEmpty()) {
                writeJson(resp, HttpServletResponse.SC_OK, getCarsUseCase.getCars());
                return;
            }

            if ("available".equals(path)) {
                writeJson(resp, HttpServletResponse.SC_OK, getAvailableCarsUseCase.getAvailableCars());
                return;
            }

            writeJson(resp, HttpServletResponse.SC_OK, getCarsUseCase.getCarById(path));
        } catch (CarNotFoundException ex) {
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ensureCollectionPath(req);
            CarRequestDto requestDto = jsonConverter.fromJson(req.getInputStream(), CarRequestDto.class);
            Car savedCar = manageCarsUseCase.createCar(webDtoMapper.toCar(requestDto));
            writeJson(resp, HttpServletResponse.SC_CREATED, savedCar);
        } catch (DuplicateCarException ex) {
            writeError(resp, HttpServletResponse.SC_CONFLICT, ex.getMessage());
        } catch (IllegalArgumentException | IOException ex) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, resolveRequestErrorMessage(ex));
        } catch (Exception ex) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String carId = requireCarId(req);
            CarRequestDto requestDto = jsonConverter.fromJson(req.getInputStream(), CarRequestDto.class);
            Car updatedCar = manageCarsUseCase.updateCar(carId, webDtoMapper.toCar(requestDto));
            writeJson(resp, HttpServletResponse.SC_OK, updatedCar);
        } catch (CarNotFoundException ex) {
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        } catch (DuplicateCarException ex) {
            writeError(resp, HttpServletResponse.SC_CONFLICT, ex.getMessage());
        } catch (IllegalArgumentException | IOException ex) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, resolveRequestErrorMessage(ex));
        } catch (Exception ex) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String carId = requireCarId(req);
            manageCarsUseCase.deleteCar(carId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (CarNotFoundException ex) {
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        } catch (CarDeletionNotAllowedException ex) {
            writeError(resp, HttpServletResponse.SC_CONFLICT, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private void ensureCollectionPath(HttpServletRequest req) {
        String path = normalizePath(req.getPathInfo());
        if (!path.isEmpty()) {
            throw new IllegalArgumentException("Use /api/cars to create a new car.");
        }
    }

    private String requireCarId(HttpServletRequest req) {
        String path = normalizePath(req.getPathInfo());
        if (path.isEmpty() || "available".equals(path)) {
            throw new IllegalArgumentException("Car id is required in the request path.");
        }
        return path;
    }

    private String normalizePath(String pathInfo) {
        if (pathInfo == null || pathInfo.trim().isEmpty() || "/".equals(pathInfo.trim())) {
            return "";
        }
        String normalizedPath = pathInfo.trim();
        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }
        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        return normalizedPath;
    }

    private String resolveRequestErrorMessage(Exception ex) {
        if (ex instanceof IOException) {
            return "Invalid JSON request body.";
        }
        return ex.getMessage();
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

