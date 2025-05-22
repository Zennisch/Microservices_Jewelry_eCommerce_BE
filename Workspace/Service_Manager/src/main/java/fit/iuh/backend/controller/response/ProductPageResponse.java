package fit.iuh.backend.controller.response;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPageResponse extends PageResponseAbstract implements Serializable {
    private List<ProductResponse> products;
}

