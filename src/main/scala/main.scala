
// akka

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
// akka http
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
// spray
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import spray.json.DefaultJsonProtocol.*

final case class User(id: Long, name: String, email: String)

// Simple example of Scala API
@main def httpserver(): Unit = {
  implicit val actorSystem: ActorSystem[Any] = ActorSystem(Behaviors.empty, "akka-http")
  implicit val userMarshaller: spray.json.RootJsonFormat[User] = jsonFormat3(User.apply)

  val getUser = get {
    path("user" / LongNumber) {
      println("Getting user")
      userId =>
        complete(User.apply(userId, "Test-user", "test@test.com"))
    }
  }

  val createUser = post {
    path("user") {
      entity(as[User]) {
        println("Creating user")
        user => complete(user)
      }
    }
  }

  val updateUser = put {
    path("user" / LongNumber) { userId =>
      entity(as[User]) {
        println("Updating user")
        user => complete(User(userId, user.name, user.email))
      }
    }
  }

  val deleteUser = delete {
    path("user" / LongNumber) {
      println("Deleting user")
      userId => complete(User(userId, "DeleteTestUser", "test@teste.com"))
    }
  }

  val routes = cors() {
    concat(getUser, createUser, updateUser, deleteUser)
  }

  Http().newServerAt("localhost", 8080).bind(routes)
}

