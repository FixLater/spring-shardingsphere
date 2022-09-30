package com.hyk.sharding.hibernate;

import com.google.common.collect.Lists;
import com.hyk.sharding.utils.ReflectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 功能:Hibernate基础CRUD数据操作实现类
 * <p>
 * 修改历史:对程序的修改历史进行记录
 * </p>
 *
 * @param <T>  泛型实体
 * @param <P> 泛型主键类型
 * @author none
 */
public class HibernateDao<T, P extends Serializable> {
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int FIREST_RESULT_START = 0;
    public static final int MAX_RESULT_SIZE = 10000;
    public static final int MMAX_RESULT_SIZE = MAX_RESULT_SIZE * 10;
    /**
     * 要跟配置中的hibernate.jdbc.batch_size一致
     */
    private static final int BATCH_SIZE = 50;
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateDao.class);

    /**
     * hibernate sessionFactory.
     */
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * 实体类.
     */
    protected Class<T> entityClass;

    /**
     * 默认的无参构造函数,默认情况下xml配置的hibernate sessionFactory的id=sessionFactory.
     */
    public HibernateDao() {
        this.entityClass = ReflectionUtil.getSuperClassGenricType(getClass());
    }

    /**
     * 返回一个Hibernate的Session.
     *
     * @return Session Hibernate的Session
     */
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 保存一个对象.
     *
     * @param entity 被保存的对象
     * @return T 保存后生成ID的对象
     */
    public T save(T entity) {
        Assert.notNull(entity, "entity is not null!!!");
        Session session = getSession();
        session.saveOrUpdate(entity);
        return entity;
    }

    /**
     * 保存一个集合的对象.
     *
     * @param entities 被保存的集合
     * @return Collection<T> 返回保存成功后的对象
     */
    public List<T> save(List<T> entities) {
        List<T> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(entities)) {
            return Lists.newArrayList();
        }
        for (T t : entities) {
            list.add(save(t));
        }
        return list;
    }

    /**
     * 根据传入的条件进行查询,匹配方式为相等.
     *
     * @param propertyName 查询参数
     * @param value        值
     * @return T 对象
     */
    @SuppressWarnings("unchecked")
    public T getBy(String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName 不能为空");
        Criterion criterion = Restrictions.eq(propertyName, value);
        Criteria criteria = createCriteria(criterion);
        criteria.setFirstResult(0);
        criteria.setMaxResults(1);
        List<T> list = criteria.list();
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    /**
     * 根据传入的条件进行查询,匹配方式为相等.
     *
     * @param propertyName 查询参数
     * @param value        值
     * @return List<T> 对象
     * <p>
     * Modify by HanHongmin 2017-11-01 最多返回10000 条记录
     */
    @SuppressWarnings("unchecked")
    public List<T> findBy(String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName 不能为空");
        Criterion criterion = Restrictions.eq(propertyName, value);
        Criteria criteria = createCriteria(criterion);
        criteria.setFirstResult(0);
        criteria.setMaxResults(MMAX_RESULT_SIZE);
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<T> findPaperOrder(String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName 不能为空");
        Criterion criterion = Restrictions.eq(propertyName, value);
        Criteria criteria = createCriteria(criterion);
        criteria.setFirstResult(0);
        criteria.setMaxResults(MMAX_RESULT_SIZE);
        return criteria.list();
    }

    /**
     * 根据传入的条件进行查询,匹配方式为相等并排序
     *
     * @param propertyName      属性名
     * @param value             属性值
     * @param orderPropertyName 排序属性
     * @param isAsc             true正序
     * @return 结果集合
     * <p>
     * Modify by HanHongmin 2017-11-01 最多返回10000 条
     */
    @SuppressWarnings("unchecked")
    public List<T> findOrderBy(String propertyName, Object value, String orderPropertyName, boolean isAsc) {
        Assert.hasText(propertyName, "propertyName is not null!!!");
        Criterion criterion = Restrictions.eq(propertyName, value);
        Order order = isAsc ? Order.asc(orderPropertyName) : Order.desc(orderPropertyName);
        Criteria criteria = createCriteria(criterion).addOrder(order);
        criteria.setFirstResult(0);
        criteria.setMaxResults(MMAX_RESULT_SIZE);
        return criteria.list();
    }

    /**
     * 返回所有对象.
     *
     * @return Collection<T> 查询出的所有对象
     */
    public List<T> findAll() {
        return find();
    }

    /**
     * 根据hql语句进行查询.
     *
     * @param hql    查询语句
     * @param values 条件
     * @return Collection<T> 对象集合
     * Modify by HanHongmin 2017-11-01 最多返回10000 条记录
     */
    @SuppressWarnings("unchecked")
    protected List<T> find(String hql, Object... values) {
        Query query = createQuery(hql, values);
        query.setFirstResult(0);
        query.setMaxResults(MAX_RESULT_SIZE);
        return query.list();
    }

    /**
     * 根据ql语句和参数进行查询.
     *
     * @param hql    查询语句
     * @param values 条件
     * @return Collection<T> 对象集合
     * modify by HanHongmin 2017-11-01 最多返回 10000 条记录
     */
    @SuppressWarnings("unchecked")
    protected List<T> find(String hql, Map<String, Object> values) {
        Query query = createQuery(hql, values);
        query.setFirstResult(0);
        query.setMaxResults(MMAX_RESULT_SIZE);
        return query.list();
    }

    /**
     * 根据主键ID的集合返回一个对象集合.
     *
     * @param ids 主键ID的集合
     * @return Collection<T> 对象的集合
     */
    public List<T> findAll(Collection<P> ids) {
        if(ids.isEmpty()){
            return new ArrayList<>();
        }
        return find(Restrictions.in(getIdName(), ids));
    }

    /**
     * 判断数据库中Dao的泛型类所对应的表中propertyName字段的value值是否存在.true是存在,false是不存在.
     *
     * @param propertyName 字段名
     * @param value        值
     * @return Boolean true/false
     * <p>
     * Modify by HanHongmin 2017-11-01 限制查询条数
     */
    public Boolean isExists(String propertyName, Object value) {
        Criterion criterion = Restrictions.eq(propertyName, value);
        Criteria criteria = createCriteria(criterion);
        criteria.setFirstResult(0);
        criteria.setMaxResults(1);
        return !criteria.list().isEmpty();
    }

    /**
     * 返回存储的对象总数.
     *
     * @return Long 总数
     */
    public Long count() {
        String countHql = "select count(*) from " + entityClass.getSimpleName();
        return findUnique(countHql);
    }

    /**
     * 按Criteria查询对象列表.
     *
     * @param criterion 数量可变的Criterion.
     *                  Modify by HanHongmin 2017-11-01 最多返回10000 条记录
     */
    @SuppressWarnings("unchecked")
    public List<T> find(final Criterion... criterion) {
        Criteria criteria = createCriteria(criterion).setCacheable(true);
        criteria.setFirstResult(0);
        criteria.setMaxResults(MAX_RESULT_SIZE);
        return criteria.list();
    }

    /**
     * 根据Criterion条件创建Criteria.
     * <p/>
     * 本类封装的find()函数全部默认返回对象类型为T,当不为T时使用本函数.
     *
     * @param criterion 数量可变的Criterion.
     */
    private Criteria createCriteria(final Criterion... criterion) {
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.setCacheable(true);
        for (Criterion c : criterion) {
            criteria.add(c);
        }
        return criteria;
    }

    /**
     * 按HQL查询唯一对象.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    @SuppressWarnings("unchecked")
    private <X> X findUnique(final String hql, final Object... values) {
        return (X) createQuery(hql, values).setCacheable(true).uniqueResult();
    }

    /**
     * 按HQL查询唯一对象.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    @SuppressWarnings("unchecked")
    protected <X> X findUnique(final String hql,
                               final Map<String, Object> values) {
        return (X) createQuery(hql, values).setCacheable(true).uniqueResult();
    }

    /**
     * 执行count查询获得本次Hql查询所能获得的对象总数.
     * <p/>
     * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
     */
    protected long countHqlResult(final String hql, final Object... values) {
        String fromHql = hql;
        // select子句与order by子句会影响count查询,进行简单的排除.
        fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
        fromHql = StringUtils.substringBefore(fromHql, "order by");

        String countHql = "select count(*) " + fromHql;
        return findUnique(countHql, values);
    }

    /**
     * 根据查询HQL与参数列表创建Query对象.
     * <p/>
     * 本类封装的find()函数全部默认返回对象类型为T,当不为T时使用本函数.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    protected Query createQuery(final String queryString,
                                final Object... values) {
        Assert.hasText(queryString, "queryString 不能为空");
        Query query = getSession().createQuery(queryString);
        query.setCacheable(true);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }
        return query;
    }

    /**
     * 根据查询HQL与参数列表创建Query对象.
     *
     * @param values 命名参数,按名称绑定.
     */
    protected Query createQuery(final String queryString,
                                final Map<String, Object> values) {
        Assert.hasText(queryString, "queryString is not null!!!");
        Query query = getSession().createQuery(queryString);
        query.setCacheable(true);
        if (values != null) {
            query.setProperties(values);
        }
        return query;
    }

    /**
     * 取得对象的主键名.
     */
    private String getIdName() {
        ClassMetadata meta = sessionFactory.getClassMetadata(entityClass);
        return meta.getIdentifierPropertyName();
    }

    /**
     * 取得对象属性类型
     *
     * @return 属性类型
     */
    @SuppressWarnings("unused")
    protected Type getPropertyTypes(String name) {
        ClassMetadata meta = sessionFactory.getClassMetadata(entityClass);

        return meta.getPropertyType(name);
    }

    /**
     * 取得对象属性名称
     *
     * @return 属性名称
     */
    @SuppressWarnings("unused")
    protected String[] getPropertyNames() {
        ClassMetadata meta = sessionFactory.getClassMetadata(entityClass);
        return meta.getPropertyNames();
    }
}