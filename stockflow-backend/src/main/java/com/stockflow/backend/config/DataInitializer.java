package com.stockflow.backend.config;

import com.stockflow.backend.entity.*;
import com.stockflow.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DataInitializer — Seeds default roles, ADMIN user, and initial business master data on application startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private boolean enabled = true;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final StockMovementRepository stockMovementRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!enabled) {
            log.info("=== StockFlow DataInitializer disabled ===");
            return;
        }
        log.info("=== StockFlow DataInitializer starting ===");
        seedRoles();
        User admin = seedAdminUser();
        seedStaffUser();
        seedBusinessData(admin);
        log.info("=== StockFlow DataInitializer completed ===");
    }

    private void seedRoles() {
        for (UserRole roleName : UserRole.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = Role.builder()
                        .name(roleName)
                        .description(roleName.name() + " role for StockFlow system")
                        .build();
                roleRepository.save(role);
                log.info("Seeded role: {}", roleName);
            }
        }
    }

    private User seedAdminUser() {
        Role adminRole = roleRepository.findByName(UserRole.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found after seeding"));

        return userRepository.findByUsername("admin").orElseGet(() -> {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@stockflow.com")
                    .password(passwordEncoder.encode("Admin@1234"))
                    .firstName("Admin")
                    .lastName("User")
                    .role(adminRole)
                    .active(true)
                    .build();

            User saved = userRepository.save(admin);
            log.info("Default ADMIN user created — username: admin, password: Admin@1234");
            return saved;
        });
    }

    private void seedStaffUser() {
        Role staffRole = roleRepository.findByName(UserRole.STAFF)
                .orElseThrow(() -> new IllegalStateException("STAFF role not found after seeding"));

        userRepository.findByUsername("staff").orElseGet(() -> {
            User staff = User.builder()
                    .username("staff")
                    .email("staff@stockflow.com")
                    .password(passwordEncoder.encode("Staff@1234"))
                    .firstName("Staff")
                    .lastName("Operator")
                    .role(staffRole)
                    .active(true)
                    .build();

            User saved = userRepository.save(staff);
            log.info("Default STAFF user created — username: staff, password: Staff@1234");
            return saved;
        });
    }

    private void seedBusinessData(User admin) {
        if (categoryRepository.count() == 0) {
            Category category = categoryRepository.save(Category.builder()
                    .name("Electronics")
                    .description("Electronic gadgets and accessories")
                    .active(true)
                    .build());

            Brand brand = brandRepository.save(Brand.builder()
                    .name("Logitech")
                    .description("Computer peripherals manufacturer")
                    .active(true)
                    .build());

            Supplier supplier = supplierRepository.save(Supplier.builder()
                    .name("ABC Suppliers")
                    .email("supplier@abc.com")
                    .phone("+1-555-0192")
                    .address("123 Industrial Park, New York")
                    .city("New York")
                    .state("NY")
                    .country("USA")
                    .active(true)
                    .build());

            Warehouse warehouse = warehouseRepository.save(Warehouse.builder()
                    .name("Main Warehouse")
                    .code("WH-MAIN")
                    .location("Central Hub, District 1")
                    .address("Central Hub, District 1")
                    .city("New York")
                    .state("NY")
                    .country("USA")
                    .capacity(10000)
                    .active(true)
                    .build());



            Product product = productRepository.save(Product.builder()
                    .name("Mouse")
                    .sku("MOUSE-LOG-001")
                    .barcode("8901234567890")
                    .description("High precision wireless optical mouse")
                    .buyingPrice(new BigDecimal("25.00"))
                    .sellingPrice(new BigDecimal("45.00"))
                    .quantity(50)
                    .minimumStock(5)
                    .category(category)
                    .brand(brand)
                    .supplier(supplier)
                    .warehouse(warehouse)
                    .active(true)
                    .build());

            Purchase purchase = Purchase.builder()
                    .purchaseNumber("PO-INIT-001")
                    .purchaseDate(LocalDateTime.now())
                    .invoiceNumber("INV-INIT-001")
                    .paymentStatus("PAID")
                    .paymentMethod("CASH")
                    .remarks("Initial inventory seed purchase")
                    .totalAmount(new BigDecimal("1250.00"))
                    .supplier(supplier)
                    .warehouse(warehouse)
                    .createdBy(admin)
                    .build();

            PurchaseItem purchaseItem = PurchaseItem.builder()
                    .purchase(purchase)
                    .product(product)
                    .quantity(50)
                    .unitPrice(new BigDecimal("25.00"))
                    .subtotal(new BigDecimal("1250.00"))
                    .build();

            purchase.addPurchaseItem(purchaseItem);
            purchaseRepository.save(purchase);

            stockMovementRepository.save(StockMovement.builder()
                    .product(product)
                    .warehouse(warehouse)
                    .user(admin)
                    .movementType(MovementType.STOCK_IN)
                    .quantity(50)
                    .quantityBefore(0)
                    .quantityAfter(50)
                    .movementDate(LocalDateTime.now())
                    .remarks("Initial inventory stock receipt (PO-INIT-001)")
                    .build());

            log.info("Seeded initial business data (Electronics, Logitech, ABC Suppliers, Main Warehouse, Mouse with 50 units stock)");
        }
    }
}
