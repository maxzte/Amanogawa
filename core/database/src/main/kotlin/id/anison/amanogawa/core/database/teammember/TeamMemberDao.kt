package id.anison.amanogawa.core.database.teammember

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface TeamMemberDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTeamMember(member: TeamMemberDbo): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTeamMembers(teamMembers: List<TeamMemberDbo>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDubbers(dubbers: List<DubberDbo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTranslators(translators: List<TranslatorDbo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSoundEngineers(engineers: List<SoundEngineerDbo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDubber(dubber: DubberDbo): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTranslator(translator: TranslatorDbo): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSoundEngineer(engineer: SoundEngineerDbo): Long

    @Query("SELECT * FROM team_member WHERE name = :name LIMIT 1")
    suspend fun getTeamMemberByName(name: String): TeamMemberDbo?

    @Query("SELECT * FROM team_member WHERE name IN (:names)")
    suspend fun getTeamMembersByNames(names: List<String>): List<TeamMemberDbo>


    @Transaction
    @Query("SELECT * FROM team_member WHERE name = :name")
    suspend fun getTeamMemberWithRoles(name: String): TeamMemberWithRoles?

    @Transaction
    @Query("SELECT * FROM team_member")
    suspend fun getAllTeamMembersWithRoles(): List<TeamMemberWithRoles>

    @Transaction
    @Query("SELECT * FROM team_member WHERE teamMemberId IN (:ids)")
    suspend fun getTeamMembersWithRoles(ids: List<Long>): List<TeamMemberWithRoles>

}