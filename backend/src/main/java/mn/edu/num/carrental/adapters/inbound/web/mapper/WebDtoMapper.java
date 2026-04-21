package mn.edu.num.carrental.adapters.inbound.web.mapper;

import mn.edu.num.annotation.Component;
import mn.edu.num.carrental.adapters.inbound.web.dto.RentResponseDto;
import mn.edu.num.carrental.core.domain.Invoice;
import mn.edu.num.carrental.core.domain.RentalType;

@Component
public class WebDtoMapper {

    public RentalType toRentalType(String rentalType) {
        return RentalType.from(rentalType);
    }

    public RentResponseDto toRentResponse(Invoice invoice) {
        RentResponseDto responseDto = new RentResponseDto();
        responseDto.setInvoiceId(invoice.getInvoiceId());
        responseDto.setRentalId(invoice.getRentalId());
        responseDto.setCarId(invoice.getCarId());
        responseDto.setCarDescription(invoice.getCarDescription());
        responseDto.setCustomerName(invoice.getCustomerName());
        responseDto.setRentalType(invoice.getRentalType().name());
        responseDto.setDuration(invoice.getDuration());
        responseDto.setTotalPrice(invoice.getTotalPrice());
        responseDto.setIssuedAt(invoice.getIssuedAt());
        responseDto.setMessage("Car rented successfully.");
        return responseDto;
    }
}

