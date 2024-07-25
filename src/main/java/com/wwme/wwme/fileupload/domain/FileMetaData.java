package com.wwme.wwme.fileupload.domain;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.task.domain.Task;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileMetaData {

    @Builder(builderMethodName = "builderWithoutTask")
    public FileMetaData(Group group, String originalFileName, String savedFileName, String url) {
        this.group = group;
        this.originalFileName = originalFileName;
        this.savedFileName = savedFileName;
        this.url = url;
        this.task = null;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Group group;
    private String originalFileName;
    private String savedFileName;
    private String url;




}
