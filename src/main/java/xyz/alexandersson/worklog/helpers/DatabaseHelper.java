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

    public static void saveLogEntry(@NotNull LogEntry logEntry) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(logEntry);
            session.getTransaction().commit();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

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

    public static void saveProject(@NotNull Project project) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(project);
            session.getTransaction().commit();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @NotNull
    public static List<Project> getProjects() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSession();
            transaction = session.beginTransaction();

            Query<Project> query = session.createQuery("from Project", Project.class);
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

    private static Session getSession() {
        return sessionFactory.isOpen() ? sessionFactory.getCurrentSession() : sessionFactory.openSession();
    }

    public static void closeSessionFactory() {
        if (!sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
