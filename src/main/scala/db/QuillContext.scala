package db
import appconfig._
import io.getquill.{PostgresZioJdbcContext,SnakeCase}
import javax.sql.DataSource
import io.getquill.context.ZioJdbc.DataSourceLayer
import zio.ZLayer
import zio._
import org.flywaydb.core.Flyway

object QuillContext  extends PostgresZioJdbcContext(SnakeCase){
   val dataSourceLayer: ZLayer[Any,Nothing,DataSource]=DataSourceLayer.fromPrefix("postgres").orDie

   def migrate: ZIO[FlywayConfig,Throwable,Unit]=ZIO.serviceWithZIO[FlywayConfig]{config=>
      for{
         flyway<-Task(Flyway.configure().dataSource(config.url,config.username,config.password).load())
         _     <- Task(flyway.migrate())
      } yield ()

   }
}
