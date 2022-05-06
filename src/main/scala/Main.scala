

import zhttp.service.Server
import routes._
import zio._
import db.{DBManager, QuillContext}
import zhttp.http.Http
import zio.Console.{printLine, readLine}
import appconfig._
object Main extends ZIOAppDefault {
  
     override def run: ZIO[ZEnv with ZIOAppArgs, Any, Any]=
       (for{
         _ <- QuillContext.migrate
         f <- Server.start(8090, UserRoutes.app <> Http.notFound).forkDaemon
        - <- printLine("Press Any Key to stop the cafe server\n\n") *> readLine.catchAll(e =>
                    printLine(s"There was an error! ${e.getMessage}")
                  ) *> f.interrupt
       } yield ())
         .provide(appconfig.live,Console.live,DBManager.live)
}