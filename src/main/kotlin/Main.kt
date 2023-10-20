import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data class Person (
    val name: String,
    val age: Int,
)

@Serializable
data class ResultPerson (
    val id: Int,
    val name: String,
    val age: Int,
    val timestamp: String
){
    fun toPerson() = Person(
        name = this.name,
        age = this.age
    )
}

fun main() {
    // Your application logic
}

suspend fun getPerson(client: SupabaseClient): List<ResultPerson> {
    return client
        .postgrest["person"]
        .select().decodeList<ResultPerson>()
        .filter { it.age > 18 }
}

suspend fun savePerson(persons: List<Person>, client: SupabaseClient): List<ResultPerson> {
    val adults = persons.filter { it.age > 18 }

    return client
        .postgrest["person"]
        .insert(adults)
        .decodeList<ResultPerson>()
}

// Used in MainKtTestSubclass
class DatabaseClient(private val client: SupabaseClient){
    suspend fun savePerson(persons: List<Person>): List<ResultPerson> {
        val adults = persons.filter { it.age > 18 }

        return client
            .postgrest["person"]
            .insert(adults)
            .decodeList<ResultPerson>()
    }
}