package com.example.agristation1.data.fieldDetails

import kotlinx.coroutines.flow.Flow

interface FieldDetailsRepository {
    fun getAllFieldsStream(): Flow<List<FieldDetails>>

    suspend fun getAllFieldsList(): List<FieldDetails>

    fun getArchivedFieldsStream(): Flow<List<FieldDetails>>

    fun getFieldByIdStream(id: Long): Flow<FieldDetails?>

    fun getFieldById(id: Long): FieldDetails?

    fun getImmediateAttentionFieldsStream(): Flow<List<FieldDetails>>

    suspend fun copyCurrentValuesToLastValid()

    suspend fun upsert(fieldDetails: FieldDetails)

    suspend fun getFieldWithPointsById(id: Long): FieldWithPoints

    suspend fun upsertFieldPoints(fieldPoints: List<FieldPoints>)
}

class FieldDetailsOfflineRepository(
    private val fieldDetailsDao: FieldDetailsDao
) : FieldDetailsRepository {

    override fun getAllFieldsStream(): Flow<List<FieldDetails>> {
        return fieldDetailsDao.getAllFields()
    }

    override suspend fun getAllFieldsList(): List<FieldDetails> {
        return fieldDetailsDao.getAllFieldsList()
    }

    override fun getArchivedFieldsStream(): Flow<List<FieldDetails>> {
        return fieldDetailsDao.getArchivedFields()
    }

    override fun getFieldByIdStream(id: Long): Flow<FieldDetails?> {
        return fieldDetailsDao.getFieldByIdStream(id)
    }

    override fun getFieldById(id: Long): FieldDetails? {
        return fieldDetailsDao.getFieldById(id)
    }

    override fun getImmediateAttentionFieldsStream(): Flow<List<FieldDetails>> {
        return fieldDetailsDao.getImmediateAttentionFields()
    }

    override suspend fun copyCurrentValuesToLastValid() {
        fieldDetailsDao.copyCurrentValuesToLastValid()
    }

    override suspend fun upsert(fieldDetails: FieldDetails) {
        fieldDetailsDao.upsert(fieldDetails)
    }

    override suspend fun getFieldWithPointsById(id: Long): FieldWithPoints {
        return fieldDetailsDao.getFieldWithPointsById(id)
    }

    override suspend fun upsertFieldPoints(fieldPoints: List<FieldPoints>) {
        fieldDetailsDao.upsertFieldPoints(fieldPoints)
    }
}