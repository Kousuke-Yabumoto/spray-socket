package com.example

import akka.actor.{ ActorSystem, Actor, Props, ActorLogging, ActorRef, ActorRefFactory }
import akka.io.{Tcp, IO}
import spray.can.Http
import spray.can.server.UHttp
import spray.can.websocket
import spray.can.websocket.frame.{ BinaryFrame, TextFrame }
import spray.http.HttpRequest
import spray.can.websocket.{UpgradedToWebSocket, FrameCommandFailed}
import spray.routing.HttpServiceActor
import scala.concurrent.ExecutionContext.Implicits.global

object WebSocketExample extends App {

  final case class Push(msg: String)

  object WebSocketExampleServer {
    def props() = Props(classOf[WebSocketExampleServer])
  }

  class WebSocketExampleServer extends Actor with ActorLogging {

    def receive = {
      case Http.Connected(remoteAddress, localAddress) =>
        val serverConnection = sender()
        val conn = context.actorOf(WebSocketWorker.props(serverConnection))
        serverConnection ! Http.Register(conn)
    }
  }

  object WebSocketWorker {
    def props(serverConnection: ActorRef) = Props(classOf[WebSocketWorker], serverConnection)
  }

  class WebSocketWorker(val serverConnection: ActorRef) extends HttpServiceActor with websocket.WebSocketServerWorker {
    import scala.concurrent.duration._
    import scala.language.postfixOps

    val system = context.system

    override def receive = handshaking orElse businessLogicNoUpgrade orElse businessLogic orElse closeLogic

    def businessLogic: Receive = {
      // just bounce frames back for Autobahn testsuite
      case TextFrame(x) =>
        sender() ! TextFrame(s"test OK! receaved :${x.utf8String}")

      case Push(msg) =>
        send(TextFrame(msg))

      case x: FrameCommandFailed =>
        log.error("frame command failed", x)

      case r: HttpRequest =>
        println(s"connection start: ${r.uri.toString()}")

      case x: Tcp.ConnectionClosed =>
        log.info("Server Close")

      case UpgradedToWebSocket =>
        log.info("Server Upgraded to WebSocket")
        system.scheduler.schedule(5 seconds, 5 seconds, self, Push("５秒経ったよ！"))

      case x => println(s"error: $x")
    }

    def businessLogicNoUpgrade: Receive = {
      implicit val refFactory: ActorRefFactory = context
      runRoute {
        getFromResourceDirectory("webapp")
      }
    }
  }

  def doMain() {
    implicit val system = ActorSystem()
    import system.dispatcher

    val server = system.actorOf(WebSocketExampleServer.props(), "websocket")

    IO(UHttp) ! Http.Bind(server, "localhost", 8080)

    readLine("Hit ENTER to exit ...\n")
    system.shutdown()
    system.awaitTermination()
  }

  // because otherwise we get an ambiguous implicit if doMain is inlined
  doMain()
}
