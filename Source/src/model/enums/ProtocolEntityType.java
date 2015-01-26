package model.enums;

public enum ProtocolEntityType {

    BAA("DEFORMATION",true), BAB("CRACK",true), BAC("BURST_OR_COLLAPSE",true), BAF("SURFACE_DAMAGE",true),
    BAH("DAMAGED_JUNCTION",true), BAI("INTRUDING_SEALING_MATERIAL",true), BAJ("DISPLACED_CONNECTION",true),
    BAK("DAMAGES_INTERIOR_LINING",true), BBF("INFILTRATION",true), BBG("EXTFILTRATION",true), BAP("CAVITIY_VISIBLE",false),
    BBA("PLANT_ROOTS",false), BAG("INTRUDING_JUNCTION",false), BBB("ADHESIVE_MATERIALS",false), BCA("LATERAL_CONNECTION",false),
    BCD("SHAFT",false), BBE("OTHER_OBSTACLES",false), BDC("INSPECTION_ABOTED",false);

    
    private final String description;
    private final boolean damage;
    
    private ProtocolEntityType(String description, boolean damage){
        this.description = description;
        this.damage = damage;
    }

    public String getDescription(){
        return description;
    }
    
    public String toString(){
        return name()+"_"+description;
    }

    public boolean isDamage(){
        return damage;
    }


}
