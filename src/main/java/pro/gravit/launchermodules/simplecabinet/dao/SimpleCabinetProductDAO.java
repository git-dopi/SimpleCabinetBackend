package pro.gravit.launchermodules.simplecabinet.dao;

import org.hibernate.*;
import pro.gravit.launchermodules.simplecabinet.model.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class SimpleCabinetProductDAO {
    private final SessionFactory factory;

    public SimpleCabinetProductDAO(SessionFactory factory) {
        this.factory = factory;
    }

    public ProductEntity findById(long id) {
        try (Session s = factory.openSession()) {
            return s.get(ProductEntity.class, id);
        }
    }
    @SuppressWarnings("unchecked")
    public List<ProductEntity> fetchPage(int startId, int limit, ProductEntity.ProductType type, String name, boolean active)
    {
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> personCriteria = cb.createQuery(ProductEntity.class);
        Root<ProductEntity> rootUser = personCriteria.from(ProductEntity.class);
        Expression<Boolean> where = null;
        personCriteria.select(rootUser);
        if(type != null) {
            where = cb.equal(rootUser.get("type"), type);
        }
        if(active) {
            if(where == null) where = cb.or(cb.isNull(rootUser.get("endDate")), cb.greaterThan(rootUser.get("endDate"), LocalDateTime.now()));
            else where = cb.and(where, cb.or(cb.isNull(rootUser.get("endDate")), cb.greaterThan(rootUser.get("endDate"), LocalDateTime.now())));
        }
        if(where != null) personCriteria.where(where);
        Query query  = em.createQuery(personCriteria);

        query.setFirstResult(startId);
        query.setMaxResults(limit);
        List<ProductEntity> result = query.getResultList();
        em.close();

        return result;
    }

    public List<ProductEnchantEntity> fetchEnchantsInProduct(ProductEntity entity)
    {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.lock(entity, LockMode.NONE);
            List<ProductEnchantEntity> list = entity.getEnchants();
            Hibernate.initialize(list);
            transaction.commit();
            return list;
        }
    }

    public void save(ProductEntity productEntity) {
        try (Session session = factory.openSession()) {
            Transaction tx1 = session.beginTransaction();
            session.save(productEntity);
            tx1.commit();
        }
    }

    public void update(ProductEntity productEntity) {
        try (Session session = factory.openSession()) {
            Transaction tx1 = session.beginTransaction();
            session.update(productEntity);
            tx1.commit();
        }
    }

    public void delete(ProductEntity productEntity) {
        try (Session session = factory.openSession()) {
            Transaction tx1 = session.beginTransaction();
            session.delete(productEntity);
            tx1.commit();
        }
    }
}
