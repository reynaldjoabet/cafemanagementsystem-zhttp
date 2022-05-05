
import zhttp.http._
import zhttp.http.Http
import pdi.jwt.JwtClaim
import zhttp.http.{Request,Response}
import zhttp.http.Method
import pdi.jwt.{Jwt,JwtAlgorithm,JwtClaim}
import java.time.Clock
import zio.json._
import zio._
import zhttp.service.Server
import java.time.LocalDate
object Main extends ZIOAppDefault {
  // Secret Authentication key
    val SECRET_KEY = "secretKey"
    implicit val clock: Clock = Clock.systemUTC
  def authenticate[R, E](fail: HttpApp[R, E], success: JwtClaim => HttpApp[R, E]): HttpApp[R, E] =
         Http
           .fromFunction[Request] { 
             _.bearerToken.flatMap(jwtDecode(_))
               .fold[HttpApp[R, E]](fail)(success)
           }
           .flatten

           // requires authentication
       def user(claim: JwtClaim): UHttpApp = Http.collect[Request] {
         case Method.GET -> !! / "user" / "expiration"   => Response.text(s"Expires in: ${claim.expiration.getOrElse(-1L)}")
       }

       def login: UHttpApp = Http.collect[Request] { 
       case Method.GET -> !! / "login" / username / password =>
         if (password.hashCode == username.hashCode) 
           Response.text(jwtEncode(username))
           .withAuthorization("Bearer "+jwtEncode(username))                                         
         else Response.fromHttpError(HttpError.Unauthorized("Invalid username or password\n"))
       }

       private def jwtDecode(jwt:String): Option[JwtClaim]=
         Jwt.decode(jwt,SECRET_KEY, Seq(JwtAlgorithm.HS512)).toOption

         // Helper to encode the JWT token
       private def jwtEncode(value:String): String={
          
        val json  = {"username:"+value}
        val claim = JwtClaim { json }.issuedNow.expiresIn(60)
        Jwt.encode(claim, SECRET_KEY, JwtAlgorithm.HS512)       
            
         }

       val app: UHttpApp = login ++ authenticate(Http.forbidden("Not allowed!"), user)

 // Run it like any simple app
  

     override def run: ZIO[ZEnv with ZIOAppArgs, Any, Any]=Server.start(8090, app).exitCode
}