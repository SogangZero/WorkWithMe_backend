package com.wwme.wwme.fileupload.domain.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UploadFileWithGroupReturnDTO {
    @JsonProperty(namespace = "meta_id")
    private Long filemetadata_id;
}
