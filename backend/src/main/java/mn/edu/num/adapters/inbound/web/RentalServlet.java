package mn.edu.num.adapters.inbound.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.adapters.inbound.web.dto.RentRequestDto;
import mn.edu.num.adapters.inbound.web.mapper.WebDtoMapper;
import mn.edu.num.core.domain.Invoice;
import mn.edu.num.core.exception.CarNotAvailableException;
import mn.edu.num.core.exception.CarNotFoundException;
import mn.edu.num.core.exception.InvalidRentalTypeException;
import mn.edu.num.core.ports.in.IGetRentalHistoryUseCase;
import mn.edu.num.core.ports.in.IRentCarUseCase;
import mn.edu.num.shared.constants.ApiConstants;
import mn.edu.num.shared.util.JsonConverter;

@Component
public class RentalServlet extends HttpServlet {

    @Autowired
    private IRentCarUseCase rentCarUseCase;

    @Autowired
    private IGetRentalHistoryUseCase getRentalHistoryUseCase;

    @Autowired
    private WebDtoMapper webDtoMapper;

    @Autowired
    private JsonConverter jsonConverter;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, HttpServletResponse.SC_OK, getRentalHistoryUseCase.getRentalHistory());
        } catch (Exception ex) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            RentRequestDto requestDto = jsonConverter.fromJson(req.getInputStream(), RentRequestDto.class);
            Invoice invoice = rentCarUseCase.rentCar(
                    requestDto.getCarId(),
                    requestDto.getCustomerName(),
                    webDtoMapper.toRentalType(requestDto.getRentalType()),
                    requestDto.getDuration()
            );
            writeJson(resp, HttpServletResponse.SC_CREATED, webDtoMapper.toRentResponse(invoice));
        } catch (InvalidRentalTypeException | IllegalArgumentException ex) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (CarNotFoundException ex) {
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        } catch (CarNotAvailableException ex) {
            writeError(resp, HttpServletResponse.SC_CONFLICT, ex.getMessage());
        } catch (IOException ex) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON request body.");
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

