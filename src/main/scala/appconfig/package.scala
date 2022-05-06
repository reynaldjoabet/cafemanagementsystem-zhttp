
import  zio.config.ConfigDescriptor._
import zio.config.syntax._
import zio.config.typesafe.TypesafeConfig
import zio.config.magnolia.DeriveConfigDescriptor
import zio.config.ConfigDescriptor
import zio.config.ReadError
import zio.Layer
import zio.ZLayer
package object appconfig {

    type AllConfig=AppConfig with FlywayConfig with ServerConfig
    private val Root="cafemanagement"

    private val Descriptor=DeriveConfigDescriptor.descriptor[AppConfig]
   private val appConfig: Layer[ReadError[String],AppConfig]=TypesafeConfig.fromResourcePath(nested(Root)(Descriptor))

   val live: ZLayer[Any,ReadError[String],AppConfig with FlywayConfig with ServerConfig]=
       appConfig >+>
        appConfig.narrow(_.flywayConfig)>+>
         appConfig.narrow(_.serverConfig)
  
}
