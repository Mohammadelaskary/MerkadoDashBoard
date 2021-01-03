package com.example.elmohammadymarket.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyDao {

    @Insert
    void insertDepartmentName(DepartmentNames name);

    @Delete
    void deleteDepartmentName(DepartmentNames name);

    @Update
    void updateDepartmentName(DepartmentNames name);

    @Query("select * from DepartmentNames")
    List<DepartmentNames> getAll();

    @Query("delete  from DepartmentNames")
    void deleteAllDepartmentsNames();

    @Query("delete from Product")
    void deleteAllProducts();

    @Insert
    void insertProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Query("select * from Product")
    List<Product> getAllProducts();


}
