package tw.hyin.demo.dao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.procedure.ProcedureCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import tw.hyin.demo.utils.GenericsUtil;

/**
 * 
 * @author YingHan 2021-11-24
 *
 * @param <T> 目標 entity
 * hibernate 常用功能
 */
@Repository
@Transactional
@SuppressWarnings("unchecked")
public class BaseDAO<T> {

    @Autowired
    private SessionFactory sessionFactory;

    private Class<T> entityClass;//接收繼承類別

    @Getter
    private static final Integer maxLimit = 1000;

    public BaseDAO() {
        this.entityClass = GenericsUtil.getSuperClassGenricType(this.getClass());
    }

    /**
     * 獲得當前可用的 session
     *
     * @return session
     */
    public Session getSession() {
        Session session = sessionFactory.getCurrentSession();
        if (session == null) {
            session = sessionFactory.openSession();
        }
        return session;
    }

    /**
     * 新增一筆資料
     *
     * @param bean entity object
     */
    @Transactional
    public void saveBean(T bean) throws Exception {
        getSession().save(bean);
        getSession().flush();
    }

    /**
     * 修改一筆資料
     *
     * @param bean entity object
     */
    @Transactional
    public void updateBean(T bean) throws Exception {
        getSession().update(bean);
        getSession().flush();
    }

    /**
     * 新增或修改一筆資料
     *
     * @param bean entity object
     */
    @Transactional
    public void saveOrUpdateBean(T bean) throws Exception {
        getSession().saveOrUpdate(bean);
        getSession().flush();
    }

    /**
     * 刪除一筆資料
     *
     * @param bean data
     */
    @Transactional
    public void deleteBean(T bean) throws Exception {
        getSession().delete(bean);
        getSession().flush();
    }

    /**
     * 刪除一筆資料
     */
    @Transactional
    public void deleteBeanById(Object id) throws Exception {
        getSession()
                .createSQLQuery(String.format("DELETE FROM %s WHERE id=:id", entityClass.getSimpleName()))
                .setParameter("id", id).executeUpdate();
        getSession().flush();
    }

    /**
     * 刪除多筆資料
     */
    @Transactional
    public void deleteBeansById(Object[] ids) throws Exception {
        getSession()
                .createSQLQuery(String.format("DELETE FROM %s WHERE id IN (:ids)", entityClass.getSimpleName()))
                .setParameterList("ids", ids).executeUpdate();
        getSession().flush();
    }

    /**
     * 刪除所有資料
     */
    @Transactional
    public void deleteBeanAll() throws Exception {
        String sql = String.format("TRUNCATE TABLE %s", entityClass.getSimpleName());
        doBySQL(sql);
    }

    /**
     * @param id PK
     */
    public T selectBean(Object id) {
        return getSession().get(entityClass, (Serializable) id);
    }

    /**
     * @param ids PKs
     */
    public List<T> selectBeans(Object[] ids) {
        return getSession()
                .createSQLQuery("FROM " + entityClass.getSimpleName() + " WHERE id IN (:ids)")
                .setParameterList("ids", ids).list();
    }

    /**
     * 查詢所有資料
     */
    public List<T> selectAllBean() throws Exception {
        return getSession().createSQLQuery("FROM " + entityClass.getSimpleName()).list();
    }

    /**
     * 產生流水號編號 (以日期為前綴)
     *
     * @param datePattern 前綴日期格式
     * @param SerialNoCol 流水號編號屬性名稱
     * @return 流水號編號
     */
    public String createSerialNoByDate(String datePattern, String suffix, String SerialNoCol) throws Exception {

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        // Create a query object by creating an instance of the CriteriaQuery interface
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        // Set the query Root by calling the from() method on the CriteriaQuery object
        // to define a range variable in FROM clause
        Root<?> root = query.from(entityClass);

        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        String date = sdf.format(new Date());
        //所有以 yyyyMMdd 起頭的字串 (convert to BigInteger desc)
        Long maxNo = getSession().createQuery(query.select(cb.max(root.get(SerialNoCol).as(Long.class)))
                .where(cb.like(root.get(SerialNoCol), date + "[0-9]%"))).getSingleResult();

        maxNo = (maxNo != null ? ++maxNo : Long.valueOf(date + suffix));
        return String.valueOf(maxNo);
    }

    /**
     * 產生流水號編號
     *
     * @param SerialNoCol 流水號編號屬性名稱
     * @return 流水號編號
     */
    public Long createSerialNo(Long firstNo, String SerialNoCol) throws Exception {

        CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
        // Create a query object by creating an instance of the CriteriaQuery interface
        CriteriaQuery<Integer> query = criteriaBuilder.createQuery(Integer.class);

        // Set the query Root by calling the from() method on the CriteriaQuery object
        // to define a range variable in FROM clause
        Root<?> root = query.from(entityClass);

        //ex 6000000000L
        Integer msgNo = firstNo.intValue();
        Integer maxNO = getSession().createQuery(query.select(criteriaBuilder.max(root.get(SerialNoCol)))
                .where(criteriaBuilder.ge(root.get(SerialNoCol), msgNo))).getSingleResult();

        msgNo = (maxNO != null ? maxNO : msgNo);
        return (long) (msgNo + 1);
    }

    /**
     * @param sql SQL
     */
    @Transactional
    public void doBySQL(String sql) {
        getSession().createSQLQuery(sql).executeUpdate();
        getSession().flush();
    }

    public void callSP(String SPname) throws Exception {
        ProcedureCall pc = getSession().createStoredProcedureCall(SPname);
        pc.getOutputs();
    }

}