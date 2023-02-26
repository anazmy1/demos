package com.rms.service;

import com.rms.domain.sales.Customer;
import com.rms.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {
    private CustomerRepository customerRepository;

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getById(Integer id) {
        return customerRepository.findById(id);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }


    public void deleteById(Integer id) {
        customerRepository.deleteById(id);
    }

    public Optional<Customer>findNationalId(String nationalId)
    {
        return customerRepository.findByNationalId(nationalId);
    }
    public Optional<Customer>findCustomerCode(String customerCode)
    {
        return customerRepository.findByCustomerCode(customerCode);
    }

}
