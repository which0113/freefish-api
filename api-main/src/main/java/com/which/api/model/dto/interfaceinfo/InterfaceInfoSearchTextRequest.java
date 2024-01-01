package com.which.api.model.dto.interfaceinfo;

import com.which.api.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author which
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoSearchTextRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String searchText;
}