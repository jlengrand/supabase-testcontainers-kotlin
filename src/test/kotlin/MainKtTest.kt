import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File

@Testcontainers
class MainKtTestTestContainers {

    private val jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyAgCiAgICAicm9sZSI6ICJzZXJ2aWNlX3JvbGUiLAogICAgImlzcyI6ICJzdXBhYmFzZS1kZW1vIiwKICAgICJpYXQiOiAxNjQxNzY5MjAwLAogICAgImV4cCI6IDE3OTk1MzU2MDAKfQ.DaYlNEoUrrEn2Ig7tqibS-PHK5vgusbcbo7X36XVt4Q"

    private lateinit var supabaseClient: SupabaseClient

    @Container
    var environment: ComposeContainer =
        ComposeContainer(File("src/test/resources/supabase/docker/docker-compose.yml"))
            .withExposedService("kong", 8000)
            .withExposedService("analytics", 4000)
            .withExposedService("db", 5432)

    @BeforeEach
    fun setUp() {
        val fakeSupabaseUrl = environment.getServiceHost("kong", 8000) +
                ":" + environment.getServicePort("kong", 8000)

        supabaseClient = createSupabaseClient(
            supabaseUrl = "http://$fakeSupabaseUrl",
            supabaseKey = jwtToken
        ) {
            install(Postgrest)
        }
    }

    @Test
    fun testEmptyPersonTable(){
        runBlocking {
            val result = getPerson(supabaseClient)
            assertEquals(0, result.size)
        }
    }

    @Test
    fun testSavePersonAndRetrieve(){
        val randomPersons = listOf(Person("Jan", 30), Person("Jane", 42))

        runBlocking {
            val result = savePerson(randomPersons, supabaseClient)
            assertEquals(2, result.size)
            assertEquals(randomPersons, result.map { it.toPerson() })

            val fetchResult = getPerson(supabaseClient)
            assertEquals(2, fetchResult.size)
            assertEquals(randomPersons, fetchResult.map { it.toPerson() })
        }
    }
}