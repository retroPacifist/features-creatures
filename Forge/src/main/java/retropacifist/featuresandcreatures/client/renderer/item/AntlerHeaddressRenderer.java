package retropacifist.featuresandcreatures.client.renderer.item;

import retropacifist.featuresandcreatures.client.model.AntlerHeaddressModel;
import retropacifist.featuresandcreatures.common.item.AntlerHeaddressItem;
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
