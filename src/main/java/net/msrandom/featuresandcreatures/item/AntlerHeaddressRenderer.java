package net.msrandom.featuresandcreatures.item;

import net.msrandom.featuresandcreatures.client.model.AntlerHeaddressModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class AntlerHeaddressRenderer extends GeoArmorRenderer<AntlerHeaddressItem> {
    public AntlerHeaddressRenderer() {
        super(new AntlerHeaddressModel());
        this.headBone = "head";
        this.bodyBone = "body";
        this.rightArmBone = "rarm";
        this.leftArmBone = "larm";
        this.rightLegBone = "rleg";
        this.leftLegBone = "lleg";
        this.rightBootBone = "rboot";
        this.leftBootBone = "lboot";
    }
}
