package it.unibas.mythesis.backend;

import it.unibas.mythesis.Constant;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DesktopBackend implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DesktopBackend.class);
    /* */
    // CREDENZIALI DI ACCESSO AL DATABASE prova1
    private String uri = "bolt://localhost:7687";
    private String username = "neo4j";
    private String password = "provapassword";
    //Collegamento proprietà da application.properties -> non riesce a recuperare le credenziali -> Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException: Username can't be null


    /* 
    //CREDENZIALI DI ACCESSO AL DATABASE ESTRATTE DAL FILE application.properties
    @Value("${spring.neo4j.uri}")
    private String uri;
    @Value("{spring.neo4j.authentication.username}")
    private String username;
    @Value("${spring.neo4j.authentication.password}")
    private String password;
    //@Value("{spring.neo4j.authentication.username}") String username, @Value("${spring.neo4j.authentication.password}") String password, @Value("${spring.neo4j.uri}") String uri
     */
 /* 
    private String uri;
    private String username;
    private String password;
     */
    private Driver driver;
    private Session session;
    private Transaction tx;

    public DesktopBackend() {
        AuthToken token = AuthTokens.basic(username, password);
        this.driver = GraphDatabase.driver(uri, token);
        this.session = driver.session();
    }

    public void createDB(){
        //NAME OF THE DATABASE TO CREATE
        String databaseName = Constant.DATABASE_NAME;
        String createDatabase = "CREATE DATABASE $name IF NOT EXISTS";

        LOGGER.info("Session creation in progress..");
        try {
            driver.verifyConnectivity();
            LOGGER.info("Is the session open? " + session.isOpen());
            session.run(createDatabase, Values.parameters("name", databaseName));
            LOGGER.info("Session successfully created.");
        } catch (Neo4jException ex) {
            driver.verifyConnectivity();
            System.out.println("Session state: " + driver.session().isOpen());
            LOGGER.error("createDB raised an exception: " + ex.getLocalizedMessage(), ex, Level.SEVERE);
            throw ex;
        }
        newDBSession();
    }

    public void dropDB() {
        String databaseName = Constant.DATABASE_NAME;
        String dropDatabase = "DROP DATABASE $name IF EXISTS";
        LOGGER.info("Session creation in progress..");
        try {
            tx = session.beginTransaction();
            Result result = tx.run(dropDatabase, Values.parameters("name", databaseName));
            LOGGER.info("Drop successfully done.");
        } catch (Neo4jException ex) {
            LOGGER.error("dropDB raised an exception", ex, Level.SEVERE);
            throw ex;
        }
    }

    public void detachDB() {
        String detachDB
                = "MATCH (n) " + "\n"
                + "DETACH DELETE n";
        LOGGER.info("Session creation in progress..");
        try {
            LOGGER.info("Executing newDBSession..");
            newDBSession();
            session.run(detachDB);
            LOGGER.info("Detach successfully done.");
        } catch (Neo4jException ex) {
            LOGGER.error("detachDB raised an exception", ex, Level.SEVERE);
            throw ex;
        }
    }
    
    //*****NODES*****
    public void createAuthorNode(String author) {
        //CREATION AUTHOR NODE
        String createAuthorNode
                = "CREATE (n:Author {name: $name})";
        LOGGER.info("Creating AUTHOR node...");
        try {
            session.run(createAuthorNode, Values.parameters("name", author));
            LOGGER.info("***AUTHOR node successfully created.");
        } catch (Neo4jException ex) {
            LOGGER.error("***createAuthorNode raised an exception: " + ex.getLocalizedMessage(), ex, Level.SEVERE);
            throw ex;
        }
    }

    @Transactional
    public void createPDFNode(String pdfTitle, String author, Integer creationYear) {
        //CREATION NODE
        String createNode
                = "CREATE (n:PDF {title: $title, author: $author, creationYear: $creationYear})";
        LOGGER.info("Creating the PDF node..");
        Map<String, Object> params = new HashMap<>();
        params.put("title", pdfTitle);
        params.put("author", author);
        params.put("creationYear", creationYear);
        try {
            session.run(createNode, params);
            LOGGER.info("***PDF node creation successful.");
        } catch (Neo4jException ex) {
            LOGGER.error("***createPDFNode raised an exception: " + ex.getLocalizedMessage(), ex, Level.SEVERE);
            throw ex;
        }
    }

    @Transactional
    public void createYearNode(Integer year) {
        //CREATION NODE
        String createYearNode
                = "CREATE (n:Year {year: $year})";
        LOGGER.info("Creating the YEAR node..");
        try {
            //Per WRITE TRANSACTION vedere sotto
            session.run(createYearNode, Values.parameters("year", year));
            LOGGER.info("***YEAR node creation successful.");
        } catch (Neo4jException ex) {
            LOGGER.error("***createYearNodo raised an exception: " + ex.getLocalizedMessage(), ex, Level.SEVERE);
            throw ex;
        }
    }

    //*****RELAZIONI*****
    @Transactional
    public void createRelationshipAP() {
        //CREATE THE RELATIONSHIP BETWEEN AUTHOR NODE AND PDF NODE
        String createRelationship
                = "MATCH (a:Author), (p:PDF)" + "\n"
                + "WHERE a.name = p.author" + "\n"
                + "CREATE (a)-[r:AUTHOR_OF]->(p)";
        LOGGER.info("Creating the RELATIONSHIP...");
        try {
            //Per WRITE TRANSACTION vedere sotto
            session.run(createRelationship);
            LOGGER.info("***RELATIONSHIP A - P successfully created.");
        } catch (Neo4jException ex) {
            LOGGER.error("***createRelationshipAP raised an exception: " + ex.getLocalizedMessage(), ex, Level.SEVERE);
            throw ex;
        }
    }

    @Transactional
    public void createRelationshipCYP() {
        //TO-DO
        String createRelationship
                = "MATCH (p:PDF), (y:Year)" + "\n"
                + "WHERE p.creationYear = y.year" + "\n"
                + "CREATE (p)-[r:CREATED_IN]->(y)";
        LOGGER.info("Creating the RELATIONSHIP...");
        try {
            //Per WRITE TRANSACTION vedere sotto
            session.run(createRelationship);
            LOGGER.info("***RELATIONSHIP Y - P successfully created.");
        } catch (Neo4jException ex) {
            LOGGER.error("***createRelationshipYP raised an exception: " + ex.getLocalizedMessage(), ex, Level.SEVERE);
            throw ex;
        }
    }

    //*****OTHERS*****
    private void newDBSession() {
        String databaseName = Constant.DATABASE_NAME;
        this.session.close();
        //CONNECTION WITH DATABASE unibas
        try {
            LOGGER.debug("Connecting with the new session in progress...");
            this.session = driver.session(SessionConfig.forDatabase(databaseName));
            LOGGER.debug("New session connected!");
        } catch (Exception ex) {
            LOGGER.error("sessionNewDB raised an exception: " + ex.getLocalizedMessage(), ex, Level.SEVERE);
        }
    }

    public void controllaDB() {
        //STAMPA IL TOTALE DI NODI PDF NELLA BASE DI DATI
        String attualeDB
                = "MATCH (p:PDF) RETURN p.title AS title";
        try {
            newDBSession();
            Result result = session.run(attualeDB);
            LOGGER.info("controllaDB correttamente eseguito!");
            System.out.println("RISULTATO: \n");
            /*
                String rows = null;
                while (result.hasNext()) {
                    Map<String, Object> row = (Map<String, Object>) result.next();
                    for (Entry<String, Object> column : row.entrySet()) {
                        rows += column.getKey() + ": " + column.getValue() + "; ";
                        System.out.println(rows);
                    }
                    rows += "\n";
                }
             */
            System.out.println("TOTALE PDF NELLA BASE DI DATI: " + result.list().size());
            for(Record pdf : result.list()){
                System.out.println("RISULTATO: " + pdf.toString());
            }
        } catch (Neo4jException ex) {
            LOGGER.error("Impossibile eseguire controllaDB.");
            LOGGER.error(ex.getLocalizedMessage());
        }
    }

    /**
     * Closes this resource, relinquishing any underlying resources. This method
     * is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     *
     * <p>
     * While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to declare
     * concrete implementations of the {@code close} method to throw more
     * specific exceptions, or to throw no exception at all if the close
     * operation cannot fail.
     *
     * <p>
     * Cases where the close operation may fail require careful attention by
     * implementers. It is strongly advised to relinquish the underlying
     * resources and to internally <em>mark</em> the resource as closed, prior
     * to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so this
     * ensures that the resources are released in a timely manner. Furthermore
     * it reduces problems that could arise when the resource wraps, or is
     * wrapped, by another resource.
     *
     * <p>
     * <em>Implementers of this interface are also strongly advised to not have
     * the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status, and runtime
     * misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an exception to be
     * suppressed, the {@code AutoCloseable.close} method should not throw it.
     *
     * <p>
     * Note that unlike the {@link Closeable#close close} method of
     * {@link Closeable}, this {@code close} method is <em>not</em> required to
     * be idempotent. In other words, calling this {@code close} method more
     * than once may have some visible side effect, unlike
     * {@code Closeable.close} which is required to have no effect if called
     * more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged to make
     * their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        driver.close();
    }

    /*
    WRITE TRANSACTION
        sessione.writeTransaction( tx -> {
        tx.run(creaNodo, parameters( "autore", nodo )).consume();
        return 1;
    });
 */
}