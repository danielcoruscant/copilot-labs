package com.example.helloworld.customers.application.idempotency;

import com.example.helloworld.customers.application.dtos.CustomerDTO;
import com.example.helloworld.customers.domain.exceptions.IdempotencyKeyConflictException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class IdempotencyKeyStore {

    private final Map<String, Entry> entries = new HashMap<>();

    public synchronized Optional<CustomerDTO> find(String key, String requestFingerprint) {
        Entry entry = entries.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (!entry.requestFingerprint().equals(requestFingerprint)) {
            throw new IdempotencyKeyConflictException(key);
        }
        return Optional.of(entry.response());
    }

    public synchronized void save(String key, String requestFingerprint, CustomerDTO response) {
        entries.put(key, new Entry(requestFingerprint, response));
    }

    private record Entry(String requestFingerprint, CustomerDTO response) {
    }
}