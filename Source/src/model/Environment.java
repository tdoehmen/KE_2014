package model;

import model.enums.LandUse;


public class Environment {

    protected LandUse land_use;
    protected boolean water_protection_area;
    
    public LandUse getLand_use() {
        return land_use;
    }
    public void setLand_use(LandUse land_use) {
        this.land_use = land_use;
    }
    public boolean isWater_protection_area() {
        return water_protection_area;
    }
    public void setWater_protection_area(boolean water_protection_area) {
        this.water_protection_area = water_protection_area;
    }
 
}
