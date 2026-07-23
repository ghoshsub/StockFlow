package com.stockflow.backend.mapper;

import com.stockflow.backend.dto.warehouse.WarehouseRequest;
import com.stockflow.backend.dto.warehouse.WarehouseResponse;
import com.stockflow.backend.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * WarehouseMapper — Manual mapper for Warehouse entity and DTOs.
 */
@Component
public class WarehouseMapper {

    private String trimOrNull(String str) {
        return (str != null && !str.isBlank()) ? str.trim() : null;
    }

    public Warehouse toEntity(WarehouseRequest request) {
        return Warehouse.builder()
                .name(trimOrNull(request.getName()))
                .code(request.getCode() != null ? request.getCode().trim().toUpperCase() : null)
                .address(trimOrNull(request.getAddress()))
                .city(trimOrNull(request.getCity()))
                .state(trimOrNull(request.getState()))
                .country(trimOrNull(request.getCountry()))
                .postalCode(trimOrNull(request.getPostalCode()))
                .capacity(request.getCapacity())
                .managerName(trimOrNull(request.getManagerName()))
                .managerEmail(trimOrNull(request.getManagerEmail()))
                .managerPhone(trimOrNull(request.getManagerPhone()))
                .active(true)
                .build();
    }

    public void updateEntity(WarehouseRequest request, Warehouse warehouse) {
        warehouse.setName(trimOrNull(request.getName()));
        warehouse.setCode(request.getCode() != null ? request.getCode().trim().toUpperCase() : warehouse.getCode());
        warehouse.setAddress(trimOrNull(request.getAddress()));
        warehouse.setCity(trimOrNull(request.getCity()));
        warehouse.setState(trimOrNull(request.getState()));
        warehouse.setCountry(trimOrNull(request.getCountry()));
        warehouse.setPostalCode(trimOrNull(request.getPostalCode()));
        warehouse.setCapacity(request.getCapacity());
        warehouse.setManagerName(trimOrNull(request.getManagerName()));
        warehouse.setManagerEmail(trimOrNull(request.getManagerEmail()));
        warehouse.setManagerPhone(trimOrNull(request.getManagerPhone()));
        if (request.getActive() != null) {
            warehouse.setActive(request.getActive());
        }
    }

    public WarehouseResponse toResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .code(warehouse.getCode())
                .address(warehouse.getAddress())
                .city(warehouse.getCity())
                .state(warehouse.getState())
                .country(warehouse.getCountry())
                .postalCode(warehouse.getPostalCode())
                .capacity(warehouse.getCapacity())
                .managerName(warehouse.getManagerName())
                .managerEmail(warehouse.getManagerEmail())
                .managerPhone(warehouse.getManagerPhone())
                .active(warehouse.getActive())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }

    public List<WarehouseResponse> toResponseList(List<Warehouse> warehouses) {
        return warehouses.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Page<WarehouseResponse> toResponsePage(Page<Warehouse> page) {
        return page.map(this::toResponse);
    }
}
