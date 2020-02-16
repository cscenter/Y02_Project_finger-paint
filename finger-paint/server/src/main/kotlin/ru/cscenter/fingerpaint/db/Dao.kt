package ru.cscenter.fingerpaint.db

import ru.cscenter.fingerpaint.db.entities.*
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

object Dao {
    private val factory: EntityManagerFactory =
        Persistence.createEntityManagerFactory("FingerPaint")

    private fun <T> run(query: (EntityManager) -> T): T {
        val entityManager = factory.createEntityManager()
        val transaction = entityManager.transaction
        try {
            transaction.begin()
            val result = query(entityManager)
            transaction.commit()
            return result
        } catch (e: Throwable) {
            transaction.rollback()
            throw e
        } finally {
            entityManager.close()
        }
    }

    fun init() {}

    fun insertUser(user: User): User = run { em ->
        em.persist(user)
        user
    }

    fun selectPatients(userId: Long): List<Patient> = run { em ->
        em.createQuery("SELECT m FROM Patient m WHERE m.userId = :userId", Patient::class.java)
            .setParameter("userId", userId)
            .resultList
    }

    fun containsPatient(patientId: Long): Boolean = run { em ->
        em.find(Patient::class.java, patientId) != null
    }

    fun insertPatients(userId: Long, names: List<String>): List<Patient> = run { em ->
        names
            .map { Patient(it, userId) }
            .onEach { em.persist(it) }
    }

    fun deletePatients(ids: List<Long>) = run { em ->
        ids
            .map { em.find(Patient::class.java, it) }
            .forEach { em.remove(it) }
    }

    fun renamePatient(id: Long, newName: String): Patient = run { em ->
        em.find(Patient::class.java, id).also {
            it.name = newName
            em.persist(it)
        }
    }

    fun selectStatistics(patientIds: List<Long>): List<Statistic> = run { em ->
        em.createQuery(
            "SELECT m FROM Statistic m WHERE m.id.patientId IN (:patientIds)",
            Statistic::class.java
        ).setParameter("patientIds", patientIds)
            .resultList
    }

    fun updateStatistic(statisticId: StatisticId, success: Boolean) = run { em ->
        val statistic = em.find(Statistic::class.java, statisticId) ?: Statistic(statisticId, 0, 0)
        statistic.total++
        statistic.success += if (success) 1 else 0
        em.persist(statistic)
    }

    fun selectChooseTasks(): List<ChooseTask> = run { em ->
        em.createQuery("SELECT m FROM ChooseTask m", ChooseTask::class.java).resultList
    }

    fun insertChooseTask(task: ChooseTask): ChooseTask = run { em ->
        em.persist(task)
        task
    }

    fun selectDrawTasks(): List<DrawTask> = run { em ->
        em.createQuery("SELECT m FROM DrawTask m", DrawTask::class.java).resultList
    }

    fun insertDrawTask(task: DrawTask): DrawTask = run { em ->
        em.persist(task)
        task
    }

    fun selectImage(id: Long): Image = run { em ->
        em.find(Image::class.java, id)
    }

    fun insertImage(image: Image): Image = run { em ->
        em.persist(image)
        image
    }

}
