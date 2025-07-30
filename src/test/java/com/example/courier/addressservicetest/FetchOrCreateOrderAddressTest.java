package com.example.courier.addressservicetest;

import com.example.courier.dto.mapper.AddressMapper;
import com.example.courier.repository.AddressRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FetchOrCreateOrderAddressTest {

    @Mock private AddressMapper addressMapper;
    @Mock private AddressRepository addressRepository;
}
