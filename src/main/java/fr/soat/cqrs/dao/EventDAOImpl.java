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
        String sql = "select 1 from consumed_event where hash = ?";
        try {
            jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{eventHash},
                    Boolean.class);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }


    @Override
    public void insert(String eventHash) {
        String sql = "INSERT INTO consumed_event VALUES (?)";
        jdbcTemplate.update(sql, eventHash);
    }
}
