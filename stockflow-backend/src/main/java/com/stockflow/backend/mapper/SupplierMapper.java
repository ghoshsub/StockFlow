package com.stockflow.backend.mapper;

import com.stockflow.backend.dto.supplier.SupplierRequest;
import com.stockflow.backend.dto.supplier.SupplierResponse;
import com.stockflow.backend.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SupplierMapper — Manual mapper between Supplier entity and DTOs.
 */
@Component
public class SupplierMapper {

    private String trimOrNull(String str) {
        return (str != null && !str.isBlank()) ? str.trim() : null;
    }

    public Supplier toEntity(SupplierRequest request) {
        return Supplier.builder()
                .name(trimOrNull(request.getName()))
                .contactPerson(trimOrNull(request.getContactPerson()))
                .email(trimOrNull(request.getEmail()))
                .phone(trimOrNull(request.getPhone()))
                .gstNumber(trimOrNull(request.getGstNumber()))
                .address(trimOrNull(request.getAddress()))
                .city(trimOrNull(request.getCity()))
                .state(trimOrNull(request.getState()))
                .country(trimOrNull(request.getCountry()))
                .postalCode(trimOrNull(request.getPostalCode()))
                .website(trimOrNull(request.getWebsite()))
                .active(true)
                .build();
    }

    public void updateEntity(SupplierRequest request, Supplier supplier) {
        supplier.setName(trimOrNull(request.getName()));
        supplier.setContactPerson(trimOrNull(request.getContactPerson()));
        supplier.setEmail(trimOrNull(request.getEmail()));
        supplier.setPhone(trimOrNull(request.getPhone()));
        supplier.setGstNumber(trimOrNull(request.getGstNumber()));
        supplier.setAddress(trimOrNull(request.getAddress()));
        supplier.setCity(trimOrNull(request.getCity()));
        supplier.setState(trimOrNull(request.getState()));
        supplier.setCountry(trimOrNull(request.getCountry()));
        supplier.setPostalCode(trimOrNull(request.getPostalCode()));
        supplier.setWebsite(trimOrNull(request.getWebsite()));
        if (request.getActive() != null) {
            supplier.setActive(request.getActive());
        }
    }

    public SupplierResponse toResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .gstNumber(supplier.getGstNumber())
                .address(supplier.getAddress())
                .city(supplier.getCity())
                .state(supplier.getState())
                .country(supplier.getCountry())
                .postalCode(supplier.getPostalCode())
                .website(supplier.getWebsite())
                .active(supplier.getActive())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }

    public List<SupplierResponse> toResponseList(List<Supplier> suppliers) {
        return suppliers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Page<SupplierResponse> toResponsePage(Page<Supplier> page) {
        return page.map(this::toResponse);
    }
}
