package fr.soat.cqrs.dao;

public interface ProductInventoryDAO {

    void increaseProductInventory(Long productReference, int provisionedQuantity);

    void decreaseProductInventory(Long productReference, int removedQuantity);

    Integer getStock(Long productReference);
}
