
import com.example.studysmart.data.local.dao.SubjectDao
import com.example.studysmart.data.mapper.toDomain
import com.example.studysmart.data.mapper.toEntity
import com.example.studysmart.data.repo.SubjectRepo
import com.example.studysmart.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepoRoomImpl @Inject constructor(
    private val dao: SubjectDao
) : SubjectRepo {

    override fun observeSubjects(): Flow<List<Subject>> =
        dao.observeSubjects().map { list -> list.map { it.toDomain() } }

    override suspend fun listSubjects(): List<Subject> =
        dao.listSubjects().map { it.toDomain() }

    override suspend fun getSubject(id: Long): Subject? =
        dao.get(id)?.toDomain()

    override suspend fun upsertSubject(s: Subject): Long =
        dao.upsert(s.toEntity())

    override suspend fun deleteSubject(id: Long) {
        dao.delete(id)
    }
}