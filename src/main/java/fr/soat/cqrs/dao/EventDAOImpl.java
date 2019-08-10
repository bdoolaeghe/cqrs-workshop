package fr.soat.cqrs.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class EventDAOImpl implements EventDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean exists(String eventHash) {
        //FIXME
        throw new RuntimeException("implement me !");
    }


    @Override
    public void insert(String eventHash) {
        //FIXME
        throw new RuntimeException("implement me !");
    }
}
