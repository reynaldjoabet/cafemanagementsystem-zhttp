package model

import zio.json.{DeriveJsonDecoder, JsonDecoder}

case class SignupForm(name:String,email:String)

object  SignupForm{
  implicit val encoder: JsonDecoder[SignupForm] = DeriveJsonDecoder.gen[SignupForm]
}
