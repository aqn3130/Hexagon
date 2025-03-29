import com.adapters.PostgresRepository
import com.domain.models.UserViewModel
import com.domain.ports.UserController
import org.flywaydb.core.Flyway
import org.http4k.core.*
import org.http4k.core.Method.POST
import org.http4k.filter.DebuggingFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.template.ViewModel

data class IndexViewModel(val message: String) : ViewModel

fun main() {
    // Run Flyway migrations
    val flyway = Flyway.configure()
        .dataSource("jdbc:postgresql://localhost:5432/hexagon", "user", "password")
        .load()
    flyway.migrate()

    val userRepository = PostgresRepository()
//    val userService = UserService(userRepository)
//    val renderer = HandlebarsTemplates().HotReload("src/main/resources/templates")
    val userController = UserController(userRepository)

    val app = DebuggingFilters.PrintRequestAndResponse()
        .then(routes(
            "/" bind Method.GET to { Response(Status.OK).body("Welcome to the User App!") },
            "/user/{id}" bind Method.GET to userController::getUser,
            "/user" bind POST to { request ->
                val user = request.bodyString().let { UserViewModel(it, "New User") }
                userRepository.saveUser(user)
                Response(Status.CREATED)
            }
        ))

    app.asServer(Jetty(8080)).start()
}

