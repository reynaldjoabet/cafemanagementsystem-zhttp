package db
import model._
import zio._
import zio.Accessible


trait  DBManager{
    def addUser(user:User): Task[Unit]
    def getUserByEmail(email:String): Task[User]
    def getUserById(id:Int): Task[User]

}

final  case class DBManagerLive() extends DBManager{
    import QuillContext._
    override def addUser(user: User): Task[Unit] = 
        run(query[User].insertValue(lift(user))).unit
        .provide(dataSourceLayer)// can fail with SQLException
        .mapError(e=> Error.InternalServerError(e.getMessage))


    
    override def getUserByEmail(email: String): Task[User] =
        run(query[User].filter(user=>user.email== lift(email)))
        .map(_.headOption)
        .provide(dataSourceLayer)
        .mapError( e=>Error.InternalServerError(e.getMessage))
        .collect(Error.NotFound("User not found")){case Some(u)=>u}

    override def getUserById(id: Int): Task[User] = 
           run(query[User].filter(user=>user.id== lift(id)))
           .map(_.headOption)
           .provide(dataSourceLayer)
           .mapError( e=>Error.InternalServerError(e.getMessage))
           .collect(Error.NotFound("User not found")){case Some(u)=>u}
}


object DBManager extends Accessible[DBManager] {
    val live= DBManagerLive.toLayer
  
}
