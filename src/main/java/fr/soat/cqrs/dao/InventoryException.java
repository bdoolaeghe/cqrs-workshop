package fr.soat.cqrs.dao;

public class InventoryException extends RuntimeException {

    public InventoryException(String msg) {
        super(msg);
    }

}
