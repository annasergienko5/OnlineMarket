package school.yandex.annasergienko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import school.yandex.annasergienko.entity.Category;

import java.util.ArrayList;

public interface CategoryRepository extends JpaRepository<Category, String> {
    @Modifying
    @Transactional
    @Query(value = "update category set price = :price where id = :id", nativeQuery = true)
    void updateCategoryPrice(@Param("price") int price, @Param("id") String parentId);

    @Transactional
    @Query(value = "select id from category where parent_id = :parent_id", nativeQuery = true)
    ArrayList<String> selectIdByParentId(@Param("parent_id") String parent_id);

    @Transactional
    @Query(value = "select price from category c where id = :id", nativeQuery = true)
    int selectPrice(@Param("id") String id);

    Category findEntityById(String id);

    ArrayList<Category> findAllByParentId(String parentId);
}