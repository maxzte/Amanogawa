package id.anison.amanogawa.core.database

import id.anison.amanogawa.core.database.teammember.DubberDbo
import id.anison.amanogawa.core.database.teammember.SoundEngineerDbo
import id.anison.amanogawa.core.database.teammember.TeamMemberDao
import id.anison.amanogawa.core.database.teammember.TeamMemberDbo
import id.anison.amanogawa.core.database.teammember.TranslatorDbo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TeamMemberDaoTest : DatabaseTest() {

    private lateinit var dao: TeamMemberDao

    @Before
    override fun setUp() {
        super.setUp()
        dao = db.teamMemberDao()
    }

    @Test
    fun `insert and get team member by name`() = runTest {
        val member = TeamMemberDbo(name = "Alice")
        val id = dao.insertTeamMember(member)
        val result = dao.getTeamMemberByName("Alice")
        assertNotNull(result)
        assertEquals("Alice", result?.name)
    }

    @Test
    fun `get by name returns null when not found`() = runTest {
        assertNull(dao.getTeamMemberByName("nonexistent"))
    }

    @Test
    fun `insert multiple and get by names`() = runTest {
        dao.insertTeamMember(TeamMemberDbo(name = "A"))
        dao.insertTeamMember(TeamMemberDbo(name = "B"))
        val result = dao.getTeamMembersByNames(listOf("A", "B"))
        assertEquals(2, result.size)
    }

    @Test
    fun `insert dubber and get team member with roles`() = runTest {
        val id = dao.insertTeamMember(TeamMemberDbo(name = "Alice"))
        dao.insertDubber(DubberDbo(dubberId = 100, teamMemberId = id))
        val result = dao.getTeamMemberWithRoles("Alice")
        assertNotNull(result)
        assertEquals(1, result?.dubbers?.size)
        assertEquals(100L, result?.dubbers?.first()?.dubberId)
    }

    @Test
    fun `insert translator and get team member with roles`() = runTest {
        val id = dao.insertTeamMember(TeamMemberDbo(name = "Bob"))
        dao.insertTranslator(TranslatorDbo(translatorId = 200, teamMemberId = id))
        val result = dao.getTeamMemberWithRoles("Bob")
        assertNotNull(result)
        assertEquals(1, result?.translators?.size)
        assertEquals(200L, result?.translators?.first()?.translatorId)
    }

    @Test
    fun `insert sound engineer and get team member with roles`() = runTest {
        val id = dao.insertTeamMember(TeamMemberDbo(name = "Charlie"))
        dao.insertSoundEngineer(SoundEngineerDbo(soundEngineerId = 300, teamMemberId = id))
        val result = dao.getTeamMemberWithRoles("Charlie")
        assertNotNull(result)
        assertEquals(1, result?.soundEngineers?.size)
        assertEquals(300L, result?.soundEngineers?.first()?.soundEngineerId)
    }

    @Test
    fun `getAllTeamMembersWithRoles returns all`() = runTest {
        val id1 = dao.insertTeamMember(TeamMemberDbo(name = "A"))
        val id2 = dao.insertTeamMember(TeamMemberDbo(name = "B"))
        dao.insertDubber(DubberDbo(dubberId = 1, teamMemberId = id1))
        val all = dao.getAllTeamMembersWithRoles()
        assertEquals(2, all.size)
    }

    @Test
    fun `getTeamMembersWithRoles returns matching`() = runTest {
        val id1 = dao.insertTeamMember(TeamMemberDbo(name = "A"))
        dao.insertTeamMember(TeamMemberDbo(name = "B"))
        val result = dao.getTeamMembersWithRoles(listOf(id1))
        assertEquals(1, result.size)
        assertEquals("A", result.first().member.name)
    }

    @Test
    fun `cascade delete removes child roles`() = runTest {
        val id = dao.insertTeamMember(TeamMemberDbo(name = "Alice"))
        dao.insertDubber(DubberDbo(dubberId = 100, teamMemberId = id))
        db.teamMemberDao().insertDubber(DubberDbo(dubberId = 101, teamMemberId = id))
        db.clearAllTables()
        val result = dao.getTeamMemberWithRoles("Alice")
        assertNull(result)
    }

    @Test
    fun `insert ignores duplicate name`() = runTest {
        dao.insertTeamMember(TeamMemberDbo(name = "Alice"))
        dao.insertTeamMember(TeamMemberDbo(name = "Alice"))
        val result = dao.getTeamMembersByNames(listOf("Alice"))
        assertEquals(1, result.size)
    }
}
