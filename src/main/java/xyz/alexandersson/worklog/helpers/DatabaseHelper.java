package xyz.alexandersson.worklog.helpers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.models.LogEntry;
import xyz.alexandersson.worklog.models.Project;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    private static SessionFactory sessionFactory = new Configuration().configure("/hibernate.cfg.xml").buildSessionFactory();

    /**
     * Save or update a LogEntry to the database
     *
     * @param logEntry The LogEntry to save or update
     * @return true if successful, false if failed
     */
    public static boolean saveUpdateLogEntry(@NotNull LogEntry logEntry) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(logEntry);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Delete a LogEntry from the database
     *
     * @param logEntry The LogEntry to delete
     * @return true if successful, false if failed
     */
    public static boolean deleteLogEntry(@NotNull LogEntry logEntry) {
        return delete(logEntry);
    }

    /**
     * Returns all LogEntries from the database
     *
     * @return list of LogEntries
     */
    @NotNull
    public static List<LogEntry> getLogEntries() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();

            Query<LogEntry> query = session.createQuery("from LogEntry", LogEntry.class);
            return query.list();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<>();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Save or update a project to the database
     *
     * @param project The Project to save
     * @return true if successful, false if failed
     */
    public static boolean saveUpdateProject(@NotNull Project project) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(project);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Delete a Project from the database
     *
     * @param project The Project to delete
     * @return true if successful, false if failed
     */
    public static boolean deleteProject(@NotNull Project project) {
        return delete(project);
    }

    /**
     * Returns all Projects from the database
     *
     * @return list of Projects
     */
    @NotNull
    public static List<Project> getProjects() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();

            Query<Project> query = session.createQuery("from Project order by name", Project.class);
            return query.list();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
            return new ArrayList<>();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private static boolean delete(Object object) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();

            session.delete(object);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private static Session getSession() {
        return sessionFactory.isOpen() ? sessionFactory.getCurrentSession() : sessionFactory.openSession();
    }

    public static void closeSessionFactory() {
        if (!sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
