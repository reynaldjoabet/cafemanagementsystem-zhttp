package model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

final case class User(
    id:Int,name:String,contactNumber:String, email:String,
    password:String,status:Boolean,role:String
)


object  User{
  implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
}
