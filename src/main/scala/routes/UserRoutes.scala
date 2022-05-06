package routes
import zhttp.http._
import zhttp.http.Http
import db._
import model._
import zhttp.http.{Request, Response}
import zhttp.http.Method
import service._
import pdi.jwt.JwtClaim
import zio._
import zhttp.service.Server
import zio.json.{DecoderOps, EncoderOps}

object UserRoutes {
    private def authenticate[R, E](fail: HttpApp[R, E], success: JwtClaim => HttpApp[R, E]): HttpApp[R, E] =
      Http.fromFunction[Request] {
               _.bearerToken.flatMap{token =>AuthenticationService.jwtDecode(token)}
                 .fold[HttpApp[R, E]](fail)(success)
             }
             .flatten

             // requires authentication
        private  def user(claim: JwtClaim): UHttpApp = Http.collect[Request] {
           case Method.GET -> !! / "user" / "expiration"   => Response.text(s"Expires in: ${claim.expiration.getOrElse(-1L)}")
         }

         
        private  def login= Http.collectZIO[Request] {
          
         case Method.GET -> !! / "login" / username / password =>

           if (password.hashCode == username.hashCode){
             val cookie=Cookie( name="MyCookie", content= AuthenticationService.jwtEncode(username),sameSite = Some(Cookie.SameSite.Lax),isHttpOnly=true)
              ZIO(Response.text(AuthenticationService.jwtEncode(username))
             .withAuthorization("Bearer "+AuthenticationService.jwtEncode(username))
             .addCookie(cookie))
           }
          else  ZIO(Response.fromHttpError(HttpError.Unauthorized("Invalid username or password\n")))

         case  req@Method.POST -> _ /"signup" =>
             httpResponse[DBManager with Console,User]( for{
               body<-req.bodyAsString
               _ <- Console.printLine(body)
               bodyJson<-ZIO.fromEither(body.fromJson[SignupForm].left.map(e => Error.DecodeError(e)))
                user<-DBManager(_.getUserByEmail(bodyJson.email))
               _ <- Console.printLine(user)
              } yield user,u=>Response.json(u.toJson)

             )
         }


  private def httpResponse[R, A](rio: RIO[R, A], onSuccess: A => Response) =
    rio.fold(
      {
        case Error.DecodeError(msg) => Response.fromHttpError(HttpError.BadRequest(msg))
        case Error.NotFound(msg)    => Response.fromHttpError(HttpError.UnprocessableEntity(msg))
        case e                      => Response.fromHttpError(HttpError.InternalServerError("hello"))
      },
      onSuccess
    )

           

  val app = login ++ authenticate(Http.forbidden("Not allowed!"), user)

   // Run it like any simple app
}
