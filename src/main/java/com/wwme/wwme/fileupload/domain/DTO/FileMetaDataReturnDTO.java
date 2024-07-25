package com.wwme.wwme.fileupload.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class FileMetaDataReturnDTO {
    private Long Id;
    private String FileName;
}
