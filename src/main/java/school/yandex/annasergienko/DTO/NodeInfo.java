package school.yandex.annasergienko.DTO;

import java.util.ArrayList;

public class NodeInfo {
    public String id;

    public String type;

    public String name;

    public String parentId;

    public int price;

    public String date;

    public ArrayList<NodeInfo> children;

    public NodeInfo(){}
}