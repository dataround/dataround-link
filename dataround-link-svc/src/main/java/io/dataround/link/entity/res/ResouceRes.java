package io.dataround.link.entity.res;

import java.util.List;

import io.dataround.link.entity.Resource;
import io.dataround.link.entity.ResourceApi;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Resource response class for frontend display
 * @author yuehan124@gmail.com
 * @since 2026-05-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ResouceRes extends Resource {

    // Resolved i18n name
    private String name;
    // API list
    private List<ResourceApi> resourceApis;
}
