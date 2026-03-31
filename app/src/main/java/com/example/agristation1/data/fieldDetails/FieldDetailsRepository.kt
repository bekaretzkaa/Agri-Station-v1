package com.example.agristation1.data.fieldDetails

import kotlinx.coroutines.flow.Flow

interface FieldDetailsRepository {
    fun getAllFieldsStream(): Flow<List<FieldDetails>>

    fun getArchivedFieldsStream(): Flow<List<FieldDetails>>

    fun getFieldByIdStream(id: Int): Flow<FieldDetails?>

    fun getImmediateAttentionFieldsStream(): Flow<List<FieldDetails>>
}

class FieldDetailsOfflineRepository(
    private val fieldDetailsDao: FieldDetailsDao
) : FieldDetailsRepository {

    override fun getAllFieldsStream(): Flow<List<FieldDetails>> {
        return fieldDetailsDao.getAllFields()
    }

    override fun getArchivedFieldsStream(): Flow<List<FieldDetails>> {
        return fieldDetailsDao.getArchivedFields()
    }

    override fun getFieldByIdStream(id: Int): Flow<FieldDetails?> {
        return fieldDetailsDao.getFieldById(id)
    }

    override fun getImmediateAttentionFieldsStream(): Flow<List<FieldDetails>> {
        return fieldDetailsDao.getImmediateAttentionFields()
    }

}