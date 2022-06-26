package school.yandex.annasergienko.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "category")
public class Category {
    @Id
    @Column(name = "id")
    private String id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category",
    fetch = FetchType.EAGER)
    private List<Offer> offerList;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "parentId")
    private String parentId;

    @Column(name = "price")
    private Integer price;

    @Column(name = "updateDate")
    private String updateDate;
}