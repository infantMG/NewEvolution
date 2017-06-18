package evolution.dao.impl;


import evolution.common.UserRoleEnum;
import evolution.dao.UserDao;
import evolution.model.user.User;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 09.03.2017.
 */


@Repository
@Transactional
public class UserDaoImpl implements UserDao {

    private SessionFactory sessionFactory;

    @Autowired
    private evolution.dao.Repository repository;

    @Autowired
    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public User find(Long id) {
        return session().find(User.class, id);
    }

    @Override
    public User findByLogin(String login) {
        Query query = session().createQuery(FIND_USER_BY_USERNAME);
        query.setParameter("l", login);
        return (User) query.getSingleResult();
    }

    @Override
    public List<User> findUserByFirstLastName(String p1, String p2) {
        Query query = session().createQuery(FIND_USER_BY_FIRST_LAST_NAME);
        query.setParameter("p1", p1);
        query.setParameter("p2", p2);
        return query.list();
    }

    @Override
    public List<User> findUserByFirstOrLastName(String p1) {
        Query query = session().createQuery(FIND_USER_BY_FIRST_OR_LAST_NAME);
        query.setParameter("p1", p1);
        return query.list();
    }

    @Override
    public List<User> findUserByFirstLastName(String p1, String p2, int limit, int offset) {
        Query query = session().createQuery(FIND_USER_BY_FIRST_LAST_NAME);
        query.setParameter("p1", p1);
        query.setParameter("p2", p2);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.list();
    }

    @Override
    public List<User> findUserByFirstOrLastName(String p1, int limit, int offset) {
        Query query = session().createQuery(FIND_USER_BY_FIRST_OR_LAST_NAME);
        query.setParameter("p1", p1);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.list();
    }

    @Override
    public User selectFirstLastName(long id) {
        Query query = session().createQuery(SELECT_FIRST_LAST_NAME);
        query.setParameter("id", id);
        return (User) query.getSingleResult();
    }

    @Override
    public User selectIdFirstLastName(long id) {
        Query query = session().createQuery(SELECT_ID_FIRST_LAST_NAME);
        query.setParameter("id", id);
        return (User) query.getSingleResult();
    }

    @Override
    public List<User> findAllUser(int limit, int offset) {
        Query query = session().createQuery(FIND_USER_BY_ROLE_USER);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.list();
    }

    @Override
    public List<User> findAllAdmin(int limit, int offset) {
        Query query = session().createQuery(FIND_USER_BY_ROLE_ADMIN);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.list();
    }

    @Override
    public List<User> findAll(int limit, int offset) {
        Query query = session().createQuery(FIND_ALL_USER);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        return query.list();
    }

    @Override
    public evolution.dao.Repository repository() {
        return repository;
    }
}
