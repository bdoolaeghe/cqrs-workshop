package fr.soat.cqrs.dao;

import fr.soat.cqrs.configuration.AppConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThatThrownBy;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class EventDAOImplTest {

    @Autowired
    EventDAO eventDAO;

    @Transactional
    @Rollback(true)
    @Test
    public void should_succeed_inserting_new_hash() {
        eventDAO.insert("new_hash");
        Assert.assertTrue(eventDAO.exists("new_hash"));
        Assert.assertFalse(eventDAO.exists("not_found"));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void should_fail_to_insert_existing_hash() {
        eventDAO.insert("new_hash");
        assertThatThrownBy(() -> eventDAO.insert("new_hash"))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("consumed_event_pkey");
    }
}
