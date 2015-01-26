package model;

import model.enums.Material;
import model.enums.ProfileType;
import model.enums.WastewaterType;


public class Sewer {

   
    protected String sewer_identifier;
    protected ProfileType profile_type;
    protected Material material;
    protected WastewaterType wastewater_type;
    protected int construction_year;
    protected int dimension_mm;
    protected double length_m;
    protected double depth;
    protected int nr_lateral_connections;
    protected boolean in_groundwater;

    public Sewer(){
    }
 
    public String getSewer_identifier() {
        return sewer_identifier;
    }

    public void setSewer_identifier(String sewer_identifier) {
        this.sewer_identifier = sewer_identifier;
    }

    public ProfileType getProfile_type() {
        return profile_type;
    }

    public void setProfile_type(ProfileType profile_type) {
        this.profile_type = profile_type;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public WastewaterType getWastewater_type() {
        return wastewater_type;
    }

    public void setWastewater_type(WastewaterType wastewater_type) {
        this.wastewater_type = wastewater_type;
    }

    public int getConstruction_year() {
        return construction_year;
    }

    public void setConstruction_year(int construction_year) {
        this.construction_year = construction_year;
    }

    public int getDimension_mm() {
        return dimension_mm;
    }

    public void setDimension_mm(int dimension_mm) {
        this.dimension_mm = dimension_mm;
    }

    public double getLength_m() {
        return length_m;
    }

    public void setLength_m(double length_m) {
        this.length_m = length_m;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public int getNr_lateral_connections() {
        return nr_lateral_connections;
    }

    public void setNr_lateral_connections(int nr_lateral_connections) {
        this.nr_lateral_connections = nr_lateral_connections;
    }

    public boolean isIn_groundwater() {
        return in_groundwater;
    }

    public void setIn_groundwater(boolean in_groundwater) {
        this.in_groundwater = in_groundwater;
    }

}
