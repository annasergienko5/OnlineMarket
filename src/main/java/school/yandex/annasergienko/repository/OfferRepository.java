package school.yandex.annasergienko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import school.yandex.annasergienko.entity.Category;
import school.yandex.annasergienko.entity.Offer;

import java.util.ArrayList;

public interface OfferRepository extends JpaRepository<Offer, String> {

    Offer findEntityById(String id);

    ArrayList<Offer> findAllByCategory(Category category);
}