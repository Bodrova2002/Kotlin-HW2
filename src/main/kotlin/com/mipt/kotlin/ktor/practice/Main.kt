import com.mipt.kotlin.ktor.practice.api.commentsApi
import com.mipt.kotlin.ktor.practice.api.model.UserRegistrationRequest
import com.mipt.kotlin.ktor.practice.commentsModule
import com.mipt.kotlin.ktor.practice.repository.impl.DefaultCommentsRepository
import com.mipt.kotlin.ktor.practice.repository.impl.DefaultUserRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.gson.gson
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.features.CallLogging
import io.ktor.auth.authentication
import io.ktor.http.*

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        configureServer()

        commentsApi()
        usersApi()
        securityConfig()
    }.start(wait = true)
}

fun Application.configureServer() {
    install(Koin) {
        modules(commentsModule, userModule)
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(CallLogging)
}

// Ручки для работы с пользователем
val userModule = module {
    single { DefaultUserRepository() }
}

val commentsModule = module {
    single { DefaultCommentsRepository() }
}


fun Application.securityConfig() {
    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            realm = "ktor.io"
            validate {
                val name = it.payload.getClaim("name").asString()
                if (name != null) {
                    UserIdPrincipal(name)
                } else {
                    null
                }
            }
        }
    }
    routing {
        // Ручка регистрации
        post("/register") {
            val userRegistrationRequest = call.receive<UserRegistrationRequest>()
            val user = userRepository.createUser(userRegistrationRequest)
            call.respond(HttpStatusCode.OK, "User ${user.login} registered successfully")
        }

        // Ручка входа
        post("/login") {
            val loginData = call.receive<LoginData>()
            val user = userRepository.getUserByLogin(loginData.login)
            if (user != null && user.password == loginData.password) {
                val token = JwtConfig.makeToken(user)
                call.respond(HttpStatusCode.OK, token)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid login or password")
            }
        }

        // Защищенные ручки
        authenticate {
            // Защищенный ресурс, требующий валидного токена
            get("/protected/resource") {
                // Ваша логика для доступа к защищенному ресурсу
                call.respondText("This is a protected resource for authorized users only")
            }

            // Ручка для создания публикации
            post("/publication/create") {
                val user = getCurrentUser(call)
                if (user != null) {
                    val publication = call.receive<PublicationData>()
                    // Добавьте логику для сохранения публикации с идентификацией автора
                    call.respond(HttpStatusCode.OK, "Publication created successfully")
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                }
            }

            // Ручка для обновления публикации
            put("/publication/update/{publicationId}") {
                val user = getCurrentUser(call)
                if (user != null) {
                    val publicationId = call.parameters["publicationId"]
                    // Добавьте логику для проверки авторства и обновления публикации
                    call.respond(HttpStatusCode.OK, "Publication updated successfully")
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                }
            }
        }
    }

