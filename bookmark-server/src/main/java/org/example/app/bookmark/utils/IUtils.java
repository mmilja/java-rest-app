package org.example.app.bookmark.utils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.example.app.bookmark.exceptions.ExecutionFailedException;
import org.example.app.bookmark.exceptions.InitFailedException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Defines utility methods to be used in other classes. Used to isolate references
 * to external packages and enable unit testing.
 */
public interface IUtils {

    /**
     * @param name property name
     * @return String with property value, null if doesn't exist
     * @see System#getProperty(String)
     */
    String getProperty(final String name);

    /**
     * @param threadPool  for the server
     * @param port        for the server
     * @param pathMapping path mapping for the servlet
     * @return Server object
     * @see org.eclipse.jetty.server.Server
     * Creates and sets-up Jetty server that will use the provided thread pool
     */
    Server setupJettyServer(final QueuedThreadPool threadPool, final int port, final String pathMapping);

    /**
     * Starts the provided Jetty server.
     *
     * @param server to be started
     * @throws Exception if unsuccessful
     * @see org.eclipse.jetty.server.Server#start()
     */
    // This is just a wrapper for 3pp function, suppress the warning as we can't do anything about it
    @SuppressWarnings("squid:S00112")
    void startJettyServer(final Server server) throws Exception;

    /**
     * Stops the provided Jetty server.
     *
     * @param server to be stopped
     * @throws Exception if unsuccessful
     * @see org.eclipse.jetty.server.Server#stop()
     */
    // This is just a wrapper for 3pp function, suppress the warning as we can't do anything about it
    @SuppressWarnings("squid:S00112")
    void stopJettyServer(final Server server) throws Exception;

    /**
     * @param fileName child
     * @return new File instance
     * @see java.io.File#File(String)
     */
    File newFile(final String fileName);

    /**
     * @param directory to create
     * @throws IOException in case of an error
     * @see java.nio.file.Files#createDirectory(Path, java.nio.file.attribute.FileAttribute[])
     */
    void createDirectory(final Path directory) throws IOException;

    /**
     * Creates the directory if it doesn't exist. Won't throw an exception if the directory exists.
     *
     * @param directory to be created
     * @throws InitFailedException in case of an IOException while
     *                             creating the directory
     */
    void createDirectoryIfItDoesntExist(final Path directory);

    /**
     * @param file to create
     * @throws IOException in case of an error
     * @see java.nio.file.Files#createFile(Path, java.nio.file.attribute.FileAttribute[])
     */
    void createFile(final Path file) throws IOException;

    /**
     * Deletes a file if it exists. Doesn't thrown an exception in case the file doesn't exist.
     *
     * @param file path to the file which will be deleted.
     * @throws ExecutionFailedException in case of an error deleting the file
     */
    void deleteFileIfItExists(final Path file);

    /**
     * @param file to delete
     * @throws IOException in case of an error
     * @see java.nio.file.Files#delete(Path)
     */
    void deleteFile(final Path file) throws IOException;

    /**
     * @param fileName absolute path to the file used to create the Path
     * @return new Path instance
     * @see java.nio.file.Paths#get(String, String...)
     */
    Path getPaths(final String fileName);

    /**
     * Get all files ending with .jar extension.
     *
     * @param path path from which to get all jar files.
     * @return list of found jar files.
     * @throws IOException in case of an error when searching for files.
     */
    List<String> getJarsRecursively(final Path path) throws IOException;

    /**
     * Read file to string auxiliary function.
     *
     * @param pathString to file which is read.
     * @param encoding   of the file.
     * @return file read to string.
     * @throws IOException when file cannot be read.
     */
    String readFile(final String pathString, final Charset encoding) throws IOException;

    /**
     * Read file to string auxiliary function.
     *
     * @param path     to file which is read* @param encoding of the file.
     * @param encoding of the file.
     * @throws IOException when file cannot be read.
     */
    void sendFileToSystemErr(final String path, final Charset encoding) throws IOException;

    /**
     * Generate random UUID.
     *
     * @return generated random uuid.
     */
    UUID getRandomUUID();
}