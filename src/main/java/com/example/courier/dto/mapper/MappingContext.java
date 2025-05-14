package com.example.courier.dto.mapper;

public class MappingContext {
    private final boolean isAdmin;

    public MappingContext(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
