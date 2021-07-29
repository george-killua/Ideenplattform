package com.killua.ideenplattform.data.caching

import androidx.room.*
import com.killua.ideenplattform.data.models.local.CategoryCaching

@Dao
interface CategoryDao {
    @Query("select * from categorycaching order by id desc")
    fun getAllCategories(): List<CategoryCaching>?

    @Query("select * from categorycaching where id =:id order by id desc")
    fun getCategoryWithId(id: String): CategoryCaching?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCategories(vararg categoryCaching: CategoryCaching)

    @Query("select count(*) from categorycaching ")
    fun categoriesCount(): Int

    @Delete
    fun remove(vararg categoryCaching: CategoryCaching)
}