package school.yandex.annasergienko.DTO;

import org.springframework.stereotype.Component;
import school.yandex.annasergienko.entity.Category;
import school.yandex.annasergienko.entity.Offer;

@Component
public class DTOEntityConverter {
    public Offer itemToOffer(Item item, Category category) {
            Offer offer = new Offer();
            offer.setId(item.id);
            offer.setType(item.type);
            offer.setName(item.name);
            offer.setCategory(category);
            offer.setPrice(item.price);
            offer.setUpdateDate(item.updateDate);
            return offer;
    }
    public Category itemToCategory(Item item) {
             Category category = new Category();
             category.setId(item.id);
             category.setName(item.name);
             category.setType(item.type);
             category.setParentId(item.parentId);
             category.setPrice(item.price);
             category.setUpdateDate(item.updateDate);
             return category;
     }

     public NodeInfo OfferToNodeInfo (Offer offer) {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.id = offer.getId();
        nodeInfo.type = offer.getType();
        nodeInfo.name = offer.getName();
        nodeInfo.parentId = offer.getCategory().getId();
        nodeInfo.price = offer.getPrice();
        nodeInfo.date = offer.getUpdateDate();
        return nodeInfo;
     }

    public void CategoryToNodeInfo (Category category, NodeInfo nodeInfo) {
        nodeInfo.id = category.getId();
        nodeInfo.type = category.getType();
        nodeInfo.name = category.getName();
        nodeInfo.parentId = category.getParentId();
        nodeInfo.price = category.getPrice();
        nodeInfo.date = category.getUpdateDate();
    }
}
