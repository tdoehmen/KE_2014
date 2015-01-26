package model.enums;

public enum SurfaceCondition {
    REQUIRES_RECONSTRUCTION, BELOW_AVERAGE, AVERAGE, GOOD_CONDITION, NEW_OR_PERFECT;
    
    public static String[] valueStrings(){
        return new String[]{"REQUIRES_RECONSTRUCTION","BELOW_AVERAGE","AVERAGE","GOOD_CONDITION","NEW_OR_PERFECT"};
    }
}
