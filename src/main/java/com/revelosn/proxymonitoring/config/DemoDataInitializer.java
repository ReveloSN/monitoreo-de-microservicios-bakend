package com.revelosn.proxymonitoring.config;

import com.revelosn.proxymonitoring.model.InventoryItem;
import com.revelosn.proxymonitoring.model.OrderRecord;
import com.revelosn.proxymonitoring.model.OrderStatus;
import com.revelosn.proxymonitoring.model.PaymentRecord;
import com.revelosn.proxymonitoring.model.PaymentStatus;
import com.revelosn.proxymonitoring.repository.InventoryRepository;
import com.revelosn.proxymonitoring.repository.OrderRepository;
import com.revelosn.proxymonitoring.repository.PaymentRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;

@Configuration
public class DemoDataInitializer {

    @Bean
    public ApplicationRunner applicationRunner(InventoryRepository inventoryRepository,
                                               OrderRepository orderRepository,
                                               PaymentRepository paymentRepository) {
        return args -> {
            if (!inventoryRepository.findAll().isEmpty()) {
                return;
            }

            inventoryRepository.save(new InventoryItem(1L, "Laptop Pro", "Equipo portátil de alto rendimiento",
                    BigDecimal.valueOf(2400), 10));
            inventoryRepository.save(new InventoryItem(2L, "Monitor UltraWide", "Monitor panorámico para estaciones de trabajo",
                    BigDecimal.valueOf(650), 14));
            inventoryRepository.save(new InventoryItem(3L, "Teclado Mecánico", "Teclado mecánico retroiluminado",
                    BigDecimal.valueOf(120), 25));
            inventoryRepository.save(new InventoryItem(4L, "Mouse Inalámbrico", "Mouse ergonómico de precisión",
                    BigDecimal.valueOf(80), 30));

            inventoryRepository.reserveStock(1L, 1);
            inventoryRepository.reserveStock(2L, 2);

            orderRepository.save(new OrderRecord(1L, "Ana Pérez", 1L, 1, BigDecimal.valueOf(2400),
                    OrderStatus.CREATED, Instant.parse("2026-04-17T17:45:00Z")));
            orderRepository.save(new OrderRecord(2L, "Carlos Ruiz", 2L, 2, BigDecimal.valueOf(1300),
                    OrderStatus.CREATED, Instant.parse("2026-04-17T18:00:00Z")));

            paymentRepository.save(new PaymentRecord(1L, 1L, BigDecimal.valueOf(2400), "CARD",
                    PaymentStatus.SUCCESS, Instant.parse("2026-04-17T17:50:00Z")));
            paymentRepository.save(new PaymentRecord(2L, 2L, BigDecimal.valueOf(1300), "TRANSFER",
                    PaymentStatus.SUCCESS, Instant.parse("2026-04-17T18:05:00Z")));
        };
    }
}

