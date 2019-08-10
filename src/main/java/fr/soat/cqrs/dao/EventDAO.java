package fr.soat.cqrs.dao;

public interface EventDAO {

    boolean exists(String eventHash);

    void insert(String eventHash);

}
