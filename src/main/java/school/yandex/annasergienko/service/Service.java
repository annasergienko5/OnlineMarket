package school.yandex.annasergienko.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import school.yandex.annasergienko.DTO.DTOEntityConverter;
import school.yandex.annasergienko.DTO.Item;
import school.yandex.annasergienko.DTO.ItemsArray;
import school.yandex.annasergienko.DTO.NodeInfo;
import school.yandex.annasergienko.entity.Category;
import school.yandex.annasergienko.entity.Offer;
import school.yandex.annasergienko.repository.CategoryRepository;
import school.yandex.annasergienko.repository.OfferRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Component
public class Service {
    private final OfferRepository offerRepository;
    private final CategoryRepository categoryRepository;
    private final DTOEntityConverter dtoEntityConverter;
    private final Validator validator;

    @Autowired
    public Service(final OfferRepository offerRepository, final CategoryRepository categoryRepository,
                   final DTOEntityConverter dtoEntityConverter, final Validator validator) {
        this.offerRepository = offerRepository;
        this.categoryRepository = categoryRepository;
        this.dtoEntityConverter = dtoEntityConverter;
        this.validator = validator;
    }

    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public ResponseEntity processImport(ItemsArray itemsArray){
        if (itemsArray != null) {
            if (validator.isWrong(itemsArray)) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

            for (Item i : itemsArray.items) {
                i.updateDate = itemsArray.updateDate;
                if (i.type.equals("CATEGORY")) {
                    Category category = dtoEntityConverter.itemToCategory(i);
                    categoryRepository.save(category);
                } else if (i.type.equals("OFFER")){
                    Category category = categoryRepository.findEntityById(i.parentId);
                    Offer offer = dtoEntityConverter.itemToOffer(i, category);
                    offerRepository.save(offer);
                }
            }
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity processDelete(String id) {
        if (offerRepository.findById(id).isPresent()) {
            offerRepository.deleteById(id);
        } else if (categoryRepository.findById(id).isPresent()) {
            deleteAllSubCategories(id);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public void deleteAllSubCategories(String id){
        ArrayList<String> subIdList = categoryRepository.selectIdByParentId(id);
        if (!subIdList.isEmpty()) {
            for (String subId : subIdList) {
                deleteAllSubCategories(subId);
            }
        }
        categoryRepository.deleteById(id);
    }

    public NodeInfo processInfo(String id){
        NodeInfo nodeInfo = null;
        if (offerRepository.findById(id).isPresent()){
            Offer offer = offerRepository.findEntityById(id);
            nodeInfo = dtoEntityConverter.OfferToNodeInfo(offer);
        } else if (categoryRepository.findById(id).isPresent()) {
            Category category = categoryRepository.findEntityById(id);
            nodeInfo = new NodeInfo();
            nodeInfo.children = getAllChildren(category);
            dtoEntityConverter.CategoryToNodeInfo(category, nodeInfo);
            setDate(nodeInfo);
        } else {
            categoryRepository.findById(id).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Not Found"));
        }
        return nodeInfo;
    }

    int offerCount;
    int offerPriceSum;
    public ArrayList getAllChildren(Category category){
        ArrayList<NodeInfo> nodeInfoList = new ArrayList<>();
        String id = category.getId();

        ArrayList<Offer> offerList = offerRepository.findAllByCategory(category);
        ArrayList<Category> categoryList = categoryRepository.findAllByParentId(id);

        if (!offerList.isEmpty()) {
            int localCount = 0;
            int localPriceSum = 0;
            for (Offer offer : offerList) {
                localPriceSum += offer.getPrice();
                offerPriceSum += offer.getPrice();
                localCount++;
                offerCount++;
                NodeInfo nodeInfo = dtoEntityConverter.OfferToNodeInfo(offer);
                nodeInfoList.add(nodeInfo);
            }
            categoryRepository.updateCategoryPrice(localPriceSum / localCount, category.getId());
            category.setPrice(localPriceSum / localCount);
        } else if (!categoryList.isEmpty()) {
            for (Category childCategory : categoryList) {
                NodeInfo nodeInfo = new NodeInfo();
                nodeInfo.children = getAllChildren(childCategory);
                int categoryPrice = categoryRepository.selectPrice(childCategory.getId());
                childCategory.setPrice(categoryPrice);
                dtoEntityConverter.CategoryToNodeInfo(childCategory, nodeInfo);
                nodeInfoList.add(nodeInfo);
                }
            if (offerCount != 0) {
                categoryRepository.updateCategoryPrice(offerPriceSum / offerCount, category.getId());
                category.setPrice(offerPriceSum / offerCount);
            }
            return nodeInfoList;
        }
        return nodeInfoList;
    }

    public void setDate(NodeInfo nodeInfo){
        try {
            Date dateParent = inputFormat.parse(nodeInfo.date);
            if (nodeInfo.children != null && !nodeInfo.children.isEmpty()){
                for (NodeInfo child : nodeInfo.children) {
                    Date dateChild = inputFormat.parse(child.date);
                    setDate(child);
                    if (dateParent.before(dateChild)) {
                        nodeInfo.date = child.date;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}