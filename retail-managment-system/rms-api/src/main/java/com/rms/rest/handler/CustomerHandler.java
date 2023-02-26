package com.rms.rest.handler;

import com.rms.domain.core.Employee;
import com.rms.domain.investor.Transaction;
import com.rms.domain.sales.Customer;
import com.rms.domain.sales.Installment;
import com.rms.rest.dto.CustomerDto;
import com.rms.rest.dto.InstallmentDto;
import com.rms.rest.exception.*;
import com.rms.rest.modelmapper.CustomerMapper;
import com.rms.rest.modelmapper.InstallmentMapper;
import com.rms.service.CustomerService;
import com.rms.service.InstallmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CustomerHandler {
    private CustomerService customerService;
    private InstallmentService installmentService;
    private CustomerMapper mapper;
    private InstallmentMapper installmentMapper;

    public ResponseEntity<List<CustomerDto>> getAll() {
        List<Customer> customers = customerService.getAll();
        List<CustomerDto> dtos = mapper.toDto(customers);
        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<CustomerDto> getById(Integer id) {
        Customer customer = customerService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Customer.class.getSimpleName(),id));
        CustomerDto dto = mapper.toDto(customer);
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> getCustomerInstallments(Integer id) {
        List<Installment> installments = installmentService.getCustomerInstallments(id);
        List<InstallmentDto> installmentDtos = installmentMapper.toDto(installments);

        return ResponseEntity.ok(installmentDtos);
    }

    public ResponseEntity<CustomerDto> addCustomer(CustomerDto customerDto) {
        Optional<Customer> exsitedTrustReceiptNo = customerService.getTrustReceiptNo(customerDto.getTrustReceiptNo());
        if (exsitedTrustReceiptNo.isPresent()) {throw new ResourceAlreadyExistsException(Employee.class.getSimpleName(),
                "trustReceiptNo", Integer.toString(customerDto.getTrustReceiptNo()), ErrorCodes.DUPLICATE_RESOURCE.getCode());
    }
            Customer customer = mapper.toEntity(customerDto);
            customerService.save(customer);
            CustomerDto dto = mapper.toDto(customer);
            return ResponseEntity.ok(dto);


    }

    public ResponseEntity<CustomerDto> updateCustomer(CustomerDto customerDto,Integer id) {
        Customer customer = customerService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Customer.class.getSimpleName(),id));
        Optional<Customer> exsitedTrustReceiptNo = customerService.getTrustReceiptNo (customerDto.getTrustReceiptNo());
        if (exsitedTrustReceiptNo.isPresent() &&  !exsitedTrustReceiptNo.get().getId().equals(id)) {throw new ResourceAlreadyExistsException(Employee.class.getSimpleName(),
                "trustReceiptNo", Integer.toString(customerDto.getTrustReceiptNo()), ErrorCodes.DUPLICATE_RESOURCE.getCode());
        }
        mapper.updateEntityFromDto(customerDto, customer);
        customerService.save(customer);
        CustomerDto dto = mapper.toDto(customer);
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> deleteById(Integer id) {
        customerService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Customer.class.getSimpleName(),id));
        try {
            customerService.deleteById(id);
        } catch (Exception exception) {
            throw new ResourceRelatedException(Customer.class.getSimpleName(), "Id", id.toString(), ErrorCodes.RELATED_RESOURCE.getCode());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Response("deleted"));
    }
}
