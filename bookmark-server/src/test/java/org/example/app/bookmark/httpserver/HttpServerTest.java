package org.example.app.bookmark.httpserver;

import org.eclipse.jetty.server.Server;
import org.example.app.bookmark.config.InternalConfig;
import org.example.app.bookmark.testutils.TestUtils;
import org.example.app.bookmark.utils.IUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static org.example.app.bookmark.testutils.TestUtils.printTestFooter;
import static org.example.app.bookmark.testutils.TestUtils.printTestHeader;
import static org.example.app.bookmark.testutils.TestUtils.printTestInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpServerTest {
  private static final int PORT = 8080;
  @Rule
  public TestName testName = new TestName();
  private IUtils utils;
  private Server server;
  private InternalConfig internalConfig;

  @Before
  public void setUp() {
    utils = mock(IUtils.class);
    server = mock(Server.class);
    internalConfig = new InternalConfig();
    internalConfig.setHttpPort(PORT);
    internalConfig.setRootApiPath("bookmark/");

    printTestHeader(testName.getMethodName());
  }

  @After
  public void tearDown() {
    printTestFooter();
  }

  @Test
  public void testConstructor() {
    when(utils.setupJettyServer(any(), anyInt(), anyString())).thenReturn(server);
    HttpServer httpServer = new HttpServer(internalConfig, utils);

    assertFalse("Server should not be running yet", httpServer.isServerRunning());
  }

  @Test
  public void testStart() throws Exception {
    when(utils.setupJettyServer(any(), anyInt(), anyString())).thenReturn(server);
    HttpServer httpServer = new HttpServer(internalConfig, utils);

    doNothing().when(utils).startJettyServer(server);
    assertTrue("Starting the server should have been successful!", httpServer.start());
    assertTrue("Server should be running", httpServer.isServerRunning());
  }

  @Test
  public void testStartInvalid() throws Exception {
    when(utils.setupJettyServer(any(), anyInt(), anyString())).thenReturn(server);
    HttpServer httpServer = new HttpServer(internalConfig, utils);

    doThrow(new Exception()).when(utils).startJettyServer(server);
    assertFalse("Starting the server should not have been successful!", httpServer.start());
    assertFalse("Server should not be running", httpServer.isServerRunning());
  }

  @Test
  public void testJoin() throws InterruptedException {
    when(utils.setupJettyServer(any(), anyInt(), anyString())).thenReturn(server);
    HttpServer httpServer = new HttpServer(internalConfig, utils);

    doNothing().when(server).join();
    httpServer.join();
    verify(server).join();
  }

  @Test(expected = InterruptedException.class)
  public void testJoinInvalid() throws InterruptedException {
    when(utils.setupJettyServer(any(), anyInt(), anyString())).thenReturn(server);
    HttpServer httpServer = new HttpServer(internalConfig, utils);

    doThrow(new InterruptedException()).when(server).join();
    httpServer.join();
    verify(server).join();
  }

  @Test
  public void testClose() throws Exception {
    when(utils.setupJettyServer(any(), anyInt(), anyString())).thenReturn(server);
    HttpServer httpServer = new HttpServer(internalConfig, utils);

    doNothing().when(utils).stopJettyServer(server);
    httpServer.close();
    assertFalse("Server should not be running", httpServer.isServerRunning());
  }

  @Test
  public void testCloseInvalid() throws Exception {
    when(utils.setupJettyServer(any(), anyInt(), anyString())).thenReturn(server);
    HttpServer httpServer = new HttpServer(internalConfig, utils);
    boolean serverRunning = httpServer.isServerRunning();

    doThrow(new Exception()).when(utils).stopJettyServer(server);
    httpServer.close();
    assertEquals("Server should not have changed running state", httpServer.isServerRunning(), serverRunning);
  }
}
