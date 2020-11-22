package org.example.app.bookmark.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.example.app.bookmark.config.JerseyConfig;
import org.example.app.bookmark.exceptions.ExecutionFailedException;
import org.example.app.bookmark.exceptions.InitFailedException;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class with utilities for Bookmark application.
 * Used to isolate references to external packages.
 */
public class Utils implements IUtils {

    /**
     * Logger for this instance.
     */
    private static final Logger LOGGER = LogManager.getLogger(Utils.class.getSimpleName());

    /**
     * Timeout for idle connections (in ms).
     */
    private static final int IDLE_CONNECTION_TIMEOUT = 30000;

    /**
     * Number of acceptor threads to be used by the server.
     */
    private static final int ACCEPTOR_THREADS = 1;

    /**
     * Number of selector threads to be used by the server.
     */
    private static final int SELECTOR_THREADS = 2;

    /**
     * Constructor for the utility class.
     * Methods from external packages are wrapped in methods of this class
     * to enable unit testing. Class cannot be static for the same reason.
     */
    public Utils() {
        // Methods from external packages are wrapped in methods of this class
        // to enable unit testing. Class cannot be static for the same reason.
    }

    @Override
    public final void createDirectory(final Path directory) throws IOException {
        Files.createDirectory(directory);
    }

    @Override
    public final void createDirectoryIfItDoesntExist(final Path directory) {
        try {
            createDirectory(directory);
        } catch (FileAlreadyExistsException exception) {
            LOGGER.info("Directory {} already exists", directory.toString());
        } catch (IOException exception) {
            throw new InitFailedException("Error creating directory " + directory.toString(), exception);
        }
    }

    @Override
    public final String getProperty(final String name) {
        return System.getProperty(name);
    }

    @Override
    public final Server setupJettyServer(final QueuedThreadPool threadPool, final int port, final String pathMapping) {
        Server server = new Server(threadPool);
        ServerConnector httpConnector = new ServerConnector(server, ACCEPTOR_THREADS,
                SELECTOR_THREADS, new HttpConnectionFactory());
        httpConnector.setPort(port);
        httpConnector.setIdleTimeout(Utils.IDLE_CONNECTION_TIMEOUT);
        server.addConnector(httpConnector);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");
        server.setHandler(contextHandler);

        ServletHolder servletHolder = contextHandler.addServlet(ServletContainer.class, pathMapping + "/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getCanonicalName());

        return server;
    }

    @Override
    public final void startJettyServer(final Server server) throws Exception {
        server.start();
    }

    @Override
    public final void stopJettyServer(final Server server) throws Exception {
        server.stop();
    }

    @Override
    public final File newFile(final String fileName) {
        return new File(fileName);
    }

    @Override
    public final void createFile(final Path file) throws IOException {
        Files.createFile(file);
    }

    @Override
    public final void deleteFile(final Path file) throws IOException {
        Files.delete(file);
    }

    @Override
    public final void deleteFileIfItExists(final Path file) {
        try {
            deleteFile(file);
        } catch (NoSuchFileException exception) {
            LOGGER.info("File {} doesn't exist", file.toString(), exception);
        } catch (IOException exception) {
            LOGGER.error("Error deleting: {}", file.toString(), exception);
            throw new ExecutionFailedException("Error deleting file: " + file.toString(), exception);
        }
    }

    @Override
    public final Path getPaths(final String fileName) {
        return Paths.get(fileName);
    }

    @Override
    public final List<String> getJarsRecursively(final Path path) throws IOException {
        return Files.walk(path).map(Path::toString).filter(p -> p.endsWith(".jar")).collect(Collectors.toList());
    }

    @Override
    public final String readFile(final String path, final Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @Override
    public final void sendFileToSystemErr(final String pathString, final Charset encoding) throws IOException {
        try {
            Path path = Paths.get(pathString);
            Files.copy(path, System.err);
            System.err.flush();
        } catch (IOException e) {
            LOGGER.error("Failed to read copy file to standard error!");
            throw e;
        }
    }

    @Override
    public final UUID getRandomUUID() {
        return UUID.randomUUID();
    }
}
